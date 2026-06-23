#!/bin/bash
# regen_boot.sh — regenerate boot image segments for FIX-5APP-V2 (2026-05-26).
# Mirrors /tmp/5app-fix-build/regen_boot.sh exactly. Output in $WORK/out/boot-image/.
set -e
WORK="${WORK:-/tmp/5app-v2-build}"
FWK=$WORK/out/aosp_fwk
ADAPTER_OUT=$WORK/out/adapter
OUTPUT=$WORK/out/boot-image
DEX2OAT=$HOME/tools/dex2oat64
SIGCHAIN=$HOME/tools/lib64/libsigchain.so

# BCP order matching appspawn-x main.cpp kBootClasspath (Scope C/Tier 3 order):
# core-oj, core-libart, core-icu4j, okhttp, bouncycastle, apache-xml,
# adapter-mainline-stubs, framework, adapter-runtime-bcp, oh-adapter-framework
JARS=(core-oj.jar core-libart.jar core-icu4j.jar okhttp.jar bouncycastle.jar apache-xml.jar adapter-mainline-stubs.jar framework.jar adapter-runtime-bcp.jar oh-adapter-framework.jar)
ADAPTER_JARS="oh-adapter-framework.jar adapter-mainline-stubs.jar adapter-runtime-bcp.jar"

resolve_jar_path() {
    case " $ADAPTER_JARS " in
        *" $1 "*) echo "$ADAPTER_OUT/$1" ;;
        *) echo "$FWK/$1" ;;
    esac
}

mkdir -p $OUTPUT
DEX_ARGS=""; LOC_ARGS=""
for jar in "${JARS[@]}"; do
    src=$(resolve_jar_path $jar)
    [ -f "$src" ] || { echo "MISSING: $src"; exit 1; }
    DEX_ARGS+=" --dex-file=$src"
    LOC_ARGS+=" --dex-location=/system/android/framework/$jar"
done

export ANDROID_ROOT=$HOME/tools
export ANDROID_DATA=/tmp/dex2oat_data_$$
mkdir -p $ANDROID_DATA/dalvik-cache/arm

echo "[REGEN] Running dex2oat64 for 10-jar BCP boot image..."
LD_LIBRARY_PATH=$HOME/tools/lib64 \
LD_PRELOAD=$SIGCHAIN \
$DEX2OAT \
    --android-root=$HOME/tools \
    --instruction-set=arm \
    $DEX_ARGS \
    $LOC_ARGS \
    --oat-file=$OUTPUT/boot.oat \
    --image=$OUTPUT/boot.art \
    --base=0x70000000 \
    --runtime-arg -Xms64m \
    --runtime-arg -Xmx512m \
    --compiler-filter=speed

rm -rf $ANDROID_DATA
echo "[REGEN] Done. Outputs:"
ls -la $OUTPUT/ | head -40
