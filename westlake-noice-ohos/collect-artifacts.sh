#!/bin/bash
# collect-artifacts.sh — pull the DEPLOYED prebuilt binary set off the device for
# anyone who wants the ready-made fixes instead of building from source.
#
# READ-ONLY on the device (only `file recv` + md5 queries). Safe to run while the
# device is in use.
#
# Usage:
#   HDC=/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe \
#   WINDIR='C:\Users\dspfa\Dev\ohos-tools' \
#   WSLWIN=/mnt/c/Users/dspfa/Dev/ohos-tools \
#   bash collect-artifacts.sh [OUTDIR]
#
# The hdc.exe `file recv` quirk: recv to a path UNDER the Windows tools dir, then
# cp the WSL view into OUTDIR. (recv to an arbitrary absolute WSL path mangles.)
set -u
HDC="${HDC:-/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe}"
WINDIR="${WINDIR:-C:\\Users\\dspfa\\Dev\\ohos-tools}"
WSLWIN="${WSLWIN:-/mnt/c/Users/dspfa/Dev/ohos-tools}"
OUT="${1:-./collected}"
mkdir -p "$OUT"

# device_path  ->  output_filename
PULL=(
  "/system/android/lib/libart.so|libart.so"
  "/system/android/lib/liboh_android_runtime.so|liboh_android_runtime.so"
  "/system/android/lib/libhwui.so|libhwui.so"
  "/system/lib/liboh_adapter_bridge.so|liboh_adapter_bridge.so"
  "/system/android/framework/adapter-mainline-stubs.jar|adapter-mainline-stubs.jar"
  "/system/android/framework/oh-adapter-framework.jar|oh-adapter-framework.jar"
  "/system/android/framework/adapter-runtime-bcp.jar|adapter-runtime-bcp.jar"
  "/system/android/framework/framework.jar|framework.jar"
  "/system/android/framework/tlsjni-extra.dex|tlsjni-extra.dex"
  "/system/android/lib/libsetgidhook.so|libsetgidhook.so"
  "/system/android/lib/libdnshook.so|libdnshook.so"
  "/system/android/lib/libjdnshook.so|libjdnshook.so"
  "/system/android/lib/libnetlog.so|libnetlog.so"
  "/system/android/lib/libw14supp.so|libw14supp.so"
  "/system/android/lib/libv4force.so|libv4force.so"
  "/system/android/lib/libtlsjni.so|libtlsjni.so"
  "/data/local/tmp/start_asx.sh|start_asx.sh"
  "/data/local/tmp/apk_install|apk_install"
  "/data/local/tmp/bpfgrant|bpfgrant"
  "/data/local/tmp/noice-room.db.bak|noice-room.db.bak"
  "/data/app/el1/bundle/public/com.github.ashutoshgngwr.noice/android/base.apk|noice-base.apk"
)

pull_one() {
  local dev="$1" name="$2" tmp="rp_collect_$2"
  "$HDC" file recv "$dev" "$WINDIR\\$tmp" 2>&1 | tr -d '\r' | grep -iqE 'finish' \
    && cp "$WSLWIN/$tmp" "$OUT/$name" && rm -f "$WSLWIN/$tmp" \
    && echo "  OK   $name ($(md5sum "$OUT/$name" | cut -d' ' -f1))" \
    || echo "  FAIL $name  ($dev)"
}

echo "[collect] pulling deployed binaries into $OUT ..."
for e in "${PULL[@]}"; do pull_one "${e%%|*}" "${e##*|}"; done

echo "[collect] pulling boot image (30 segments) ..."
mkdir -p "$OUT/boot-arm"
"$HDC" shell "ls /system/android/framework/arm/" 2>&1 | tr -d '\r' | grep -E '^boot' | while read f; do
  "$HDC" file recv "/system/android/framework/arm/$f" "$WINDIR\\rp_boot_$f" 2>&1 | tr -d '\r' | grep -iqE 'finish' \
    && cp "$WSLWIN/rp_boot_$f" "$OUT/boot-arm/$f" && rm -f "$WSLWIN/rp_boot_$f" \
    && echo "  OK   boot-arm/$f" || echo "  FAIL boot-arm/$f"
done

echo "[collect] pulling noice cdn cache ..."
mkdir -p "$OUT/noice-cdn-cache.bak"
"$HDC" shell "ls /data/local/tmp/noice-cdn-cache.bak/" 2>&1 | tr -d '\r' | grep -v WSL | while read f; do
  [ -n "$f" ] || continue
  "$HDC" file recv "/data/local/tmp/noice-cdn-cache.bak/$f" "$WINDIR\\rp_cdn_$f" 2>&1 | tr -d '\r' | grep -iqE 'finish' \
    && cp "$WSLWIN/rp_cdn_$f" "$OUT/noice-cdn-cache.bak/$f" && rm -f "$WSLWIN/rp_cdn_$f"
done

echo "[collect] done. Verify md5s against MANIFEST.md."
