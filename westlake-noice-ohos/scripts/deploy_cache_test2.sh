#!/bin/bash
HDC=/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe
WINDIR='C:\Users\dspfa\Dev\ohos-tools'; WSLWIN=/mnt/c/Users/dspfa/Dev/ohos-tools
BIMG=/tmp/tagsoup-boot/out/boot-image; BASE=/data/app/el1/0/base/com.github.ashutoshgngwr.noice
EV=$HOME/openharmony/docs/engine/V3-NOICE-DPAD-FINDINGS
sh(){ $HDC shell "$1" 2>&1 | tr -d '\r'; }
npid(){ for i in 1 2 3;do p=$(sh 'for d in /proc/[0-9]*;do u=$(grep -m1 "^Uid:" $d/status 2>/dev/null|cut -f2);c=$(cat $d/comm 2>/dev/null);if [ "$u" = "13731" ]&&[ "$c" != sh ];then echo ${d##*/};fi;done|head -1');[ -n "$p" ]&&{ echo "$p";return;};sleep 1;done; }
$HDC kill >/dev/null 2>&1; $HDC start >/dev/null 2>&1; sleep 2; sh "mount -o remount,rw / 2>/dev/null"
echo "[deploy cached adapter-runtime-bcp.jar + boot]"
cp /tmp/tlsshim/arb5.jar "$WSLWIN/arb5.jar"; sh "rm -f /data/local/tmp/arb5.jar"; $HDC file send "$WINDIR\\arb5.jar" /data/local/tmp/arb5.jar >/dev/null 2>&1
sh "cp /data/local/tmp/arb5.jar /system/android/framework/adapter-runtime-bcp.jar; chmod 644 /system/android/framework/adapter-runtime-bcp.jar; chcon u:object_r:system_file:s0 /system/android/framework/adapter-runtime-bcp.jar 2>/dev/null"
cd "$BIMG"; for f in *; do cp "$f" "$WSLWIN/bk_$f"; sh "rm -f /data/local/tmp/bk_$f"; $HDC file send "$WINDIR\\bk_$f" /data/local/tmp/bk_$f >/dev/null 2>&1; sh "cp /data/local/tmp/bk_$f /system/android/framework/arm/$f"; done
sh "chmod 644 /system/android/framework/arm/boot*; chcon u:object_r:system_file:s0 /system/android/framework/arm/boot* 2>/dev/null; sync"
echo "[reboot]"; sh "param set persist.sys.usb.config hdc_debug; param set persist.usb.setting.gadget_conn_prompt false; sync"; sh "reboot"; sleep 68
$HDC kill >/dev/null 2>&1; sleep 2; $HDC start >/dev/null 2>&1; sleep 3
for i in $(seq 1 18); do T=$($HDC list targets 2>&1|tr -d '\r'|grep -vE '^$|Empty|Fail|connect-key'|head -1); [ -n "$T" ] && break; /mnt/c/Windows/System32/taskkill.exe /F /IM hdc.exe>/dev/null 2>&1; sleep 2; $HDC start>/dev/null 2>&1; sleep 6; done
[ -z "$($HDC list targets 2>&1|tr -d '\r'|grep -vE '^$|Empty|Fail|connect-key')" ] && { echo HDC-DROPPED; exit 0; }
sh "mkdir -p /dev/memcg/perf_sensitive 2>/dev/null; setenforce 0; setsid sh /data/local/tmp/start_asx.sh </dev/null >/dev/null 2>&1 &"; sleep 16
sh "chmod 0666 /dev/unix/socket/AppSpawnX; chcon u:object_r:appspawn_socket:s0 /dev/unix/socket/AppSpawnX; setenforce 0; power-shell wakeup; power-shell timeout -o 86400000" >/dev/null 2>&1
echo "[continuous bpf regrant + launch + NetTest (both api+cdn should be HTTP now)]"
sh "nohup sh -c 'while true; do /data/local/tmp/bpfgrant 13731 oh_sock_permission_map >/dev/null 2>&1; sleep 0.3; done' >/dev/null 2>&1 &"; sleep 2
sh "rm -f /data/local/tmp/tls.log /data/local/tmp/httptest.log"
for t in 1 2 3 4; do
  sh "aa force-stop com.github.ashutoshgngwr.noice >/dev/null 2>&1"; sleep 2
  sh "cp /data/local/tmp/noice-room.db.bak $BASE/databases/com.github.ashutoshgngwr.noice.db 2>/dev/null;cp /data/local/tmp/noice-cdn-cache.bak/* $BASE/cache/cdn-cache/ 2>/dev/null;chown -R 13731:13731 $BASE/databases $BASE/cache 2>/dev/null"
  sh "power-shell wakeup;aa start -a com.github.ashutoshgngwr.noice.activity.MainActivity -b com.github.ashutoshgngwr.noice">/dev/null 2>&1
  for w in $(seq 1 10); do sleep 4; d=$(sh "grep -c 'NetTest done' /data/local/tmp/httptest.log 2>/dev/null"); [ "${d:-0}" -gt 0 ] && break; done
  [ -n "$(npid)" ] && [ "${d:-0}" -gt 0 ] && break
done
echo "noice=$(npid)"
echo "=== NetTest (BOTH requests should be HTTP now) ==="
sh "grep trynoice /data/local/tmp/httptest.log 2>/dev/null"
echo "=== navigate noice subscription (data should load) + capture ==="
sh "echo '5' > /data/local/tmp/noice_tap";sleep 3;sh "echo '360 513' > /data/local/tmp/noice_tap";sleep 10
cd $EV; for k in 1 2 3 4; do sh "aa start -a com.github.ashutoshgngwr.noice.activity.MainActivity -b com.github.ashutoshgngwr.noice">/dev/null 2>&1;sleep 1;sh "snapshot_display -f /data/local/tmp/cc$k.jpeg>/dev/null 2>&1";$HDC file recv /data/local/tmp/cc$k.jpeg cached-sub_$k.jpeg >/dev/null 2>&1;echo "cap$k=$(sh 'stat -c%s /data/local/tmp/cc'$k'.jpeg')";sleep 2;done
sh "pkill -f 'while true' 2>/dev/null"
echo CACHETESTDONE
