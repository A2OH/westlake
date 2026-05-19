#!/system/bin/sh
# combo.sh — runs INSIDE the V3 chroot.
#
# Phase 1b.1 round-trip orchestration:
#   1. seed env (LD_LIBRARY_PATH, ANDROID_*, BOOTCLASSPATH, ICU_DATA)
#   2. start appspawn-x daemon in background
#   3. wait for daemon's listen socket
#   4. run test_tlv_client (sends raw OH binary TLV spawn request)
#   5. give child Java init time (~15s)
#   6. kill daemon cleanly
#
# Run via:
#   hdc shell 'chroot /data/local/tmp/v3-hbc-chroot /system/bin/sh /combo.sh'
#
# Acceptance markers (in hilog tag AppSpawnX):
#   Phase 4: Ready to accept spawn requests          (parent ART ready)
#   OH binary msg received                            (TLV parsed)
#   Spawn request: proc=com.example.helloworld ...   (proc launched)
#   Child process started, pid=...                   (fork)
#   [CHILD_CK] CK_BEFORE_initChild_call              (about to enter Java)
#   J_initChild_entry proc=com.example.helloworld    (Java reached)
#
# If `J_initChild_entry` is missing, check child stderr at
#   /data/local/tmp/v3-hbc-chroot/data/local/tmp/adapter_child_<pid>.stderr
# for the actual failure point (DAC / SELinux / OHEnvironment / Log.i).
#
# Authored 2026-05-19 (agent 73). See docs/engine/V3-W2-PHASE-1B-PROGRESS.md.

set -u

export LD_LIBRARY_PATH=/system/lib:/system/android/lib:/system/lib/platformsdk:/system/lib/chipset-sdk:/system/lib/chipset-sdk-sp:/system/lib/ndk
export ANDROID_ROOT=/system/android
export ANDROID_DATA=/data
export ANDROID_RUNTIME_ROOT=/system
export ANDROID_ART_ROOT=/system
export ANDROID_I18N_ROOT=/system
export ANDROID_TZDATA_ROOT=/system
export BOOTCLASSPATH=/system/android/framework/core-oj.jar:/system/android/framework/core-libart.jar:/system/android/framework/core-icu4j.jar:/system/android/framework/okhttp.jar:/system/android/framework/bouncycastle.jar:/system/android/framework/apache-xml.jar:/system/android/framework/adapter-mainline-stubs.jar:/system/android/framework/framework.jar:/system/android/framework/oh-adapter-framework.jar
export ICU_DATA=/system/android/etc/icu

echo "[combo] starting appspawn-x daemon"
/system/bin/appspawn-x --socket-name AppSpawnX > /tmp-appspawn-stderr.log 2>&1 &
DAEMON_PID=$!
echo "[combo] daemon pid=$DAEMON_PID"

# Wait up to 30s (60 * 500ms) for daemon to reach Phase 4 (socket listening).
# We can't easily test for socket via [ -S ] in toybox sh; sleep is the simplest
# robust signal. 8s is the typical ART init time on DAYU200; bump if needed.
i=0
while [ $i -lt 60 ]; do
    if ! kill -0 $DAEMON_PID 2>/dev/null; then
        echo "[combo] daemon died before socket ready (i=$i)"
        cat /tmp-appspawn-stderr.log 2>&1 | tail -30
        echo "[combo] check hilog: hdc shell 'hilog -x | grep AppSpawnX | tail -40'"
        exit 1
    fi
    sleep 0.5
    i=$((i+1))
    if [ $i -ge 16 ]; then break; fi   # ~8s elapsed
done
echo "[combo] daemon alive after $(expr $i \* 500)ms"

echo "[combo] running test_tlv_client"
/system/bin/test_tlv_client /dev/unix/socket/AppSpawnX
TLV_RC=$?
echo "[combo] test_tlv_client rc=$TLV_RC"

if [ $TLV_RC -ne 0 ]; then
    echo "[combo] TLV client failed; investigate daemon log:"
    cat /tmp-appspawn-stderr.log 2>&1 | tail -30
fi

echo "[combo] sleeping 15s for child Java init to complete"
sleep 15

echo "[combo] daemon still alive?"
kill -0 $DAEMON_PID 2>&1 && echo "[combo] yes" || echo "[combo] no"

echo "[combo] killing daemon"
kill -TERM $DAEMON_PID 2>&1
sleep 2
kill -KILL $DAEMON_PID 2>/dev/null

echo "[combo] DONE — check hilog and chroot /data/local/tmp/adapter_child_*.stderr for forensics"
