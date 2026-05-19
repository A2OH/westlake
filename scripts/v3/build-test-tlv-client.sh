#!/usr/bin/env bash
# ============================================================================
# build-test-tlv-client.sh — cross-compile HBC's test_tlv_client.c for ARM32 OHOS
#
# Phase 1b.1 sentinel binary: speaks raw OH AppSpawn binary TLV protocol to the
# AppSpawnX Unix-domain socket. Used to verify chroot spawn round-trip:
# (test_tlv_client → appspawn-x parent → fork() → child specialization →
#  AppSpawnXInit.initChild → ActivityThread.main).
#
# This script lives at scripts/v3/ for parity with deploy-hbc-to-dayu200-chroot.sh.
# It's intentionally one-shot (cross-compile once, push and reuse forever).
# The output binary `test_tlv_client` is 43 KB ARM32 PIE dynamically linked
# against /lib/ld-musl-arm.so.1 (musl-only). It works inside the chroot as
# long as /lib/ld-musl-arm.so.1 is bind-mounted (which it is per Stage 3).
#
# Authored 2026-05-19 (agent 73) implementing Phase 1b.1 of
# `docs/engine/V3-W2-PHASE-1B-PROGRESS.md`.
# Verified PASS on agent 73's session: spawn round-trip complete end-to-end.
#
# Usage:
#   bash scripts/v3/build-test-tlv-client.sh
#   # outputs: $REPO_ROOT/engine/v3/phase-one-b/bin/test_tlv_client
# ============================================================================

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SRC="$REPO_ROOT/westlake-deploy-ohos/v3-hbc/adapter-src/framework/appspawn-x/test/test_tlv_client.c"
OUT_DIR="$REPO_ROOT/engine/v3/phase-one-b/bin"
OUT="$OUT_DIR/test_tlv_client"

# Toolchain — verified present on agent 73's system (2026-05-19).
# OpenHarmony NDK with armv7-unknown-linux-ohos-clang.
CC="${CC:-/home/dspfac/openharmony/prebuilts/clang/ohos/linux-x86_64/llvm/bin/armv7-unknown-linux-ohos-clang}"
SYSROOT="${SYSROOT:-/home/dspfac/openharmony/out/sdk/sdk-native/os-irrelevant/sysroot}"

if [ ! -f "$SRC" ]; then
    echo "ERROR: source not found at $SRC" >&2
    exit 1
fi
if [ ! -x "$CC" ]; then
    echo "ERROR: OHOS clang not found / not executable at $CC" >&2
    echo "  Set CC env to override (must be ARM32 OHOS clang)." >&2
    exit 1
fi
if [ ! -d "$SYSROOT" ]; then
    echo "ERROR: OHOS sysroot not found at $SYSROOT" >&2
    echo "  Set SYSROOT env to override." >&2
    exit 1
fi

mkdir -p "$OUT_DIR"
"$CC" --sysroot="$SYSROOT" -O2 -o "$OUT" "$SRC"

# Verify ELF format (must be ARM32 PIE)
arch=$(file "$OUT" | grep -oE '32-bit.*ARM' | head -1)
if [ -z "$arch" ]; then
    echo "ERROR: build produced non-ARM32 binary. file output:" >&2
    file "$OUT" >&2
    exit 1
fi

echo "BUILD OK: $OUT ($(stat -c %s "$OUT") bytes, $arch)"
echo ""
echo "Next: push into chroot:"
echo "  cd $REPO_ROOT  # CRITICAL: hdc.exe resolves relative paths against cwd"
echo "  HDC=/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe"
echo "  WINPATH=\$(wslpath -w \"$OUT\")"
echo "  \"\$HDC\" file send \"\$WINPATH\" /data/local/tmp/v3-hbc-chroot/system/bin/test_tlv_client"
echo "  \"\$HDC\" shell 'chmod 755 /data/local/tmp/v3-hbc-chroot/system/bin/test_tlv_client'"
echo ""
echo "Then: invoke via combo.sh inside chroot:"
echo "  See $REPO_ROOT/engine/v3/phase-one-b/combo.sh"
