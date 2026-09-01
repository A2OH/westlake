#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
SRC_DIR=$SCRIPT_DIR/src
OUT_ROOT=${1:-$SCRIPT_DIR/out}
STAGE_DIR=$OUT_ROOT/asx
UNSTRIPPED_DIR=$OUT_ROOT/unstripped
LINK_ONLY_DIR=$OUT_ROOT/link-only

OH_ROOT=${OH_ROOT:-/home/dspfac/openharmony}
OHOS_NATIVE_SDK=${OHOS_NATIVE_SDK:-/home/dspfac/ohos-sdk-6.1/linux/native}
BRIDGE_ROOT=${BRIDGE_ROOT:-/home/dspfac/bridge-build}
LLVM=${LLVM:-$OH_ROOT/prebuilts/clang/ohos/linux-x86_64/llvm}
SYSROOT=${SYSROOT:-$OHOS_NATIVE_SDK/sysroot}
JNI_INCLUDE=${JNI_INCLUDE:-$BRIDGE_ROOT/aosp/libnativehelper/include_jni}

CLANG=${CLANG:-$LLVM/bin/clang}
CLANGXX=${CLANGXX:-$LLVM/bin/clang++}
STRIP=${STRIP:-$LLVM/bin/llvm-strip}
READELF=${READELF:-$LLVM/bin/llvm-readelf}

for required in "$CLANG" "$CLANGXX" "$STRIP" "$READELF" \
        "$SYSROOT" "$JNI_INCLUDE/jni.h"; do
    if [[ ! -e $required ]]; then
        echo "missing build input: $required" >&2
        exit 1
    fi
done

mkdir -p "$STAGE_DIR/nsprobe" "$STAGE_DIR/webview-t-lib" \
         "$UNSTRIPPED_DIR" "$LINK_ONLY_DIR"

target_flags=(--target=aarch64-linux-ohos --sysroot="$SYSROOT")
compile_flags=(-fPIC -O2 -g -Wall -Wextra)
shared_flags=(-shared -fuse-ld=lld -Wl,-z,relro,-z,now)

build_shared() {
    local compiler=$1
    local stage_output=$2
    local soname=$3
    shift 3
    local unstripped=$UNSTRIPPED_DIR/$(basename -- "$stage_output")
    echo "BUILD $stage_output"
    "$compiler" "${target_flags[@]}" "${compile_flags[@]}" \
        "${shared_flags[@]}" -Wl,-soname,"$soname" "$@" -o "$unstripped"
    cp "$unstripped" "$stage_output"
    "$STRIP" --strip-unneeded "$stage_output"
}

build_link_only() {
    local soname=$1
    "$CLANG" "${target_flags[@]}" -fPIC -O2 "${shared_flags[@]}" \
        "$SRC_DIR/link_only_stub.c" -Wl,-soname,"$soname" \
        -o "$LINK_ONLY_DIR/$soname"
}

# These three inputs only give lld the target SONAME. They are not deployed.
build_link_only libkeva.so
build_link_only libttmplayer.so
build_link_only libOpenSLES.so

build_shared "$CLANG" "$STAGE_DIR/libbionic_abi_shim.so" \
    libbionic_abi_shim.so "$SRC_DIR/bionic_abi_shim.c" -ldl

build_shared "$CLANG" "$STAGE_DIR/nsprobe/libbionic_stdio_shim.so" \
    libbionic_stdio_shim.so "$SRC_DIR/bionic_stdio_shim.c" -ldl

build_shared "$CLANG" "$STAGE_DIR/liblog_shim.so" \
    liblog_shim.so "$SRC_DIR/liblog_shim.c"

build_shared "$CLANG" "$STAGE_DIR/libandroid_native_network_compat.so" \
    libandroid_native_network_compat.so \
    "$SRC_DIR/android_native_network_compat.c" \
    -Wl,--version-script="$SRC_DIR/android_native_network_compat.map" \
    -ldl -lpthread

build_shared "$CLANG" "$STAGE_DIR/webview-t-lib/libwebview_bionic_shim.so" \
    libwebview_bionic_shim.so "$SRC_DIR/webview_bionic_shim.c" \
    "$SRC_DIR/webview_setjmp_arm64.S" \
    -Wl,--version-script="$SRC_DIR/webview_bionic_shim.map" -ldl -lpthread

build_shared "$CLANGXX" "$STAGE_DIR/libwl636.so" libwl636.so \
    -I"$JNI_INCLUDE" "$SRC_DIR/wl_natives636.cpp" "$SRC_DIR/wl_dump636.cpp" \
    -ldl -lpthread

build_shared "$CLANG" "$STAGE_DIR/libwestlake_exit_trace.so" \
    libwestlake_exit_trace.so "$SRC_DIR/westlake_exit_trace.c" -ldl

# The compat DSO is an input of libwestlake_player_anchor's ordered load group.
build_shared "$CLANG" "$STAGE_DIR/nsprobe/libopensles_android_compat.so" \
    libopensles_android_compat.so "$SRC_DIR/opensles_android_compat.c" \
    -ldl -L"$LINK_ONLY_DIR" -Wl,--no-as-needed -lOpenSLES -Wl,--as-needed

build_shared "$CLANG" \
    "$STAGE_DIR/nsprobe/libwestlake_native_bootstrap_anchor.so" \
    libwestlake_native_bootstrap_anchor.so \
    "$SRC_DIR/westlake_native_bootstrap_anchor.c" \
    -L"$STAGE_DIR" -L"$LINK_ONLY_DIR" -Wl,--no-as-needed \
    -landroid_native_network_compat -lkeva -Wl,--as-needed

# Include the network wrappers in this anchor. This preserves the deployed
# anchor's LIBC-versioned socket ABI while its ordered DT_NEEDED group brings
# in stdio, OpenSL ES compatibility, and the APK player DSO.
build_shared "$CLANG" "$STAGE_DIR/nsprobe/libwestlake_player_anchor.so" \
    libwestlake_player_anchor.so "$SRC_DIR/westlake_player_anchor.c" \
    "$SRC_DIR/android_native_network_compat.c" \
    -Wl,--version-script="$SRC_DIR/android_native_network_compat.map" \
    -L"$STAGE_DIR/nsprobe" -L"$LINK_ONLY_DIR" -Wl,--no-as-needed \
    -lbionic_stdio_shim -lopensles_android_compat -lttmplayer \
    -Wl,--as-needed -ldl -lpthread

echo "BUILD $STAGE_DIR/fatal_signal_tracer"
"$CLANG" "${target_flags[@]}" -O2 -g -Wall -Wextra -fuse-ld=lld \
    -Wl,-z,relro,-z,now "$SCRIPT_DIR/tools/fatal_signal_tracer.c" \
    -o "$UNSTRIPPED_DIR/fatal_signal_tracer"
cp "$UNSTRIPPED_DIR/fatal_signal_tracer" "$STAGE_DIR/fatal_signal_tracer"
"$STRIP" --strip-unneeded "$STAGE_DIR/fatal_signal_tracer"
chmod 0755 "$STAGE_DIR/fatal_signal_tracer"

require_symbol() {
    local file=$1
    local symbol=$2
    if ! "$READELF" --dyn-syms --wide "$file" | \
            awk '{print $8}' | sed 's/@.*//' | grep -Fxq "$symbol"; then
        echo "missing required export $symbol in $file" >&2
        exit 1
    fi
}

require_needed() {
    local file=$1
    local soname=$2
    if ! "$READELF" --dynamic "$file" | grep -Fq "Shared library: [$soname]"; then
        echo "missing required DT_NEEDED $soname in $file" >&2
        exit 1
    fi
}

require_symbol "$STAGE_DIR/libbionic_abi_shim.so" __assert2
require_symbol "$STAGE_DIR/nsprobe/libbionic_stdio_shim.so" __sF
require_symbol "$STAGE_DIR/liblog_shim.so" __android_log_print
require_symbol "$STAGE_DIR/libandroid_native_network_compat.so" getaddrinfo
require_symbol "$STAGE_DIR/webview-t-lib/libwebview_bionic_shim.so" wl_sjp
require_symbol "$STAGE_DIR/webview-t-lib/libwebview_bionic_shim.so" wl_ljmp
require_symbol "$STAGE_DIR/libwl636.so" \
    Java_android_os_FileObserver_00024ObserverThread_init
require_symbol "$STAGE_DIR/libwestlake_exit_trace.so" tgkill
require_symbol "$STAGE_DIR/nsprobe/libwestlake_native_bootstrap_anchor.so" \
    westlake_android_namespace_bootstrap_anchor
require_symbol "$STAGE_DIR/nsprobe/libwestlake_player_anchor.so" \
    westlake_namespace_anchor

require_needed "$STAGE_DIR/nsprobe/libwestlake_native_bootstrap_anchor.so" \
    libandroid_native_network_compat.so
require_needed "$STAGE_DIR/nsprobe/libwestlake_native_bootstrap_anchor.so" libkeva.so
require_needed "$STAGE_DIR/nsprobe/libwestlake_player_anchor.so" \
    libbionic_stdio_shim.so
require_needed "$STAGE_DIR/nsprobe/libwestlake_player_anchor.so" \
    libopensles_android_compat.so
require_needed "$STAGE_DIR/nsprobe/libwestlake_player_anchor.so" libttmplayer.so

(
    cd "$STAGE_DIR"
    find . -type f ! -name MANIFEST.sha256 -print0 | sort -z | \
        xargs -0 sha256sum > MANIFEST.sha256
)

echo "Built and verified Toutiao runtime stage: $STAGE_DIR"
cat "$STAGE_DIR/MANIFEST.sha256"
