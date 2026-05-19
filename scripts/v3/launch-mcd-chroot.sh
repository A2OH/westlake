#!/usr/bin/env bash
# ============================================================================
# launch-mcd-chroot.sh — V3 Phase 1b operator wrapper: McD launch in chroot
#
# Drives the full Phase 1b flow against an already-set-up chroot:
#   sub        Subcommand:
#     push-driver       push phase-one-b-driver(-dex).jar into chroot framework
#     push-mcd-apk      push real McD APK into chroot's /data/app/.../base.apk
#     push-tlv          push test_tlv_client (one-shot for Phase 1b.1)
#     spawn-helloworld  use HBC's stock OH binary TLV → spawn HelloWorld bundle
#                       (Phase 1b.1 / 1b.2 — proven WORKING on this commit)
#     spawn-mcd-driver  send text-protocol spawn with targetClass=
#                       com.westlake.engine.PhaseOneBDriver + dexPaths set
#                       (Phase 1b.3 / 1b.4 — BLOCKED on substrate gap;
#                        see docs/engine/V3-W2-PHASE-1B-PROGRESS.md §4)
#     verify            scan hilog for acceptance markers
#     clean             kill daemons, leave chroot artifacts in place
#
# Pre-conditions:
#   - bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh all   (chroot up)
#   - Phase 1a-plus PASS marker present (see V3-W2-PHASE-1A-PLUS-RUN2.md)
#   - For driver subcommands: bash scripts/v3/build-phase-one-b-driver.sh has
#     produced engine/v3/phase-one-b/out/phase-one-b-driver(-dex).jar
#   - For tlv subcommands: bash scripts/v3/build-test-tlv-client.sh has
#     produced engine/v3/phase-one-b/bin/test_tlv_client
#
# Authored 2026-05-19 (agent 73) for docs/engine/V3-W2-PHASE-1B-PROGRESS.md.
# NOT YET FULLY OPERATIONAL — substrate gap blocks spawn-mcd-driver path.
# spawn-helloworld + push-tlv + verify ARE OPERATIONAL on agent 73's commit.
# ============================================================================

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
HDC="${HDC:-/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe}"
HDC_SERIAL="${HDC_SERIAL:-dd011a414436314130101250040eac00}"
V3_CHROOT_ROOT="${V3_CHROOT_ROOT:-/data/local/tmp/v3-hbc-chroot}"

MCD_APK_SRC="${MCD_APK_SRC:-$REPO_ROOT/artifacts/real-mcd/apk/com_mcdonalds_app.apk}"
DRIVER_JAR_DEX="$REPO_ROOT/engine/v3/phase-one-b/out/phase-one-b-driver-dex.jar"
DRIVER_JAR_PLAIN="$REPO_ROOT/engine/v3/phase-one-b/out/phase-one-b-driver.jar"
TLV_BIN="$REPO_ROOT/engine/v3/phase-one-b/bin/test_tlv_client"
COMBO_SH="$REPO_ROOT/engine/v3/phase-one-b/combo.sh"

cd "$REPO_ROOT" || exit 2

# hdc.exe (WSL) resolves relative paths against host CWD; always wslpath -w.
to_win() { wslpath -w "$1" 2>/dev/null || echo "$1"; }
hdc_shell() {
    "$HDC" -t "$HDC_SERIAL" shell "$@" </dev/null 2>&1 | tr -d '\r'
}
hdc_shell_check() {
    local out
    out=$("$HDC" -t "$HDC_SERIAL" shell "( $* ); echo __EXIT__=\$?" </dev/null 2>&1 | tr -d '\r')
    echo "$out" | grep -q "__EXIT__=0"
}

# Push helper: stages to host /data/local/tmp/ first, then cp inside chroot.
# Avoids the hdc-creates-dir confusion when target ends with file name.
stage_then_cp() {
    local local_path="$1" chroot_rel="$2"
    local stage_name; stage_name=$(basename "$chroot_rel")
    local stage_path="/data/local/tmp/.stage_$stage_name"
    local win; win=$(to_win "$local_path")
    "$HDC" -t "$HDC_SERIAL" file send "$win" "$stage_path" </dev/null 2>&1 | tail -1
    hdc_shell "cp $stage_path ${V3_CHROOT_ROOT}${chroot_rel} && rm $stage_path && ls -la ${V3_CHROOT_ROOT}${chroot_rel}; echo __EXIT__=\$?"
}

cmd_push_driver() {
    local jar
    if [ -f "$DRIVER_JAR_DEX" ]; then
        jar="$DRIVER_JAR_DEX"
    elif [ -f "$DRIVER_JAR_PLAIN" ]; then
        jar="$DRIVER_JAR_PLAIN"
    else
        echo "ERROR: build the driver first: bash scripts/v3/build-phase-one-b-driver.sh" >&2
        exit 1
    fi
    echo "Pushing $jar -> chroot:/system/android/framework/phase-one-b-driver.jar"
    stage_then_cp "$jar" "/system/android/framework/phase-one-b-driver.jar"
}

cmd_push_mcd_apk() {
    if [ ! -f "$MCD_APK_SRC" ]; then
        echo "ERROR: McD APK not found at $MCD_APK_SRC. Set MCD_APK_SRC env." >&2
        exit 1
    fi
    hdc_shell "mkdir -p ${V3_CHROOT_ROOT}/data/app/com.mcdonalds.app"
    echo "Pushing $MCD_APK_SRC -> chroot:/data/app/com.mcdonalds.app/base.apk"
    stage_then_cp "$MCD_APK_SRC" "/data/app/com.mcdonalds.app/base.apk"
}

cmd_push_tlv() {
    if [ ! -x "$TLV_BIN" ]; then
        echo "ERROR: build TLV client first: bash scripts/v3/build-test-tlv-client.sh" >&2
        exit 1
    fi
    echo "Pushing $TLV_BIN -> chroot:/system/bin/test_tlv_client"
    stage_then_cp "$TLV_BIN" "/system/bin/test_tlv_client"
    hdc_shell "chmod 755 ${V3_CHROOT_ROOT}/system/bin/test_tlv_client"
    echo "Pushing $COMBO_SH -> chroot:/combo.sh"
    stage_then_cp "$COMBO_SH" "/combo.sh"
    hdc_shell "chmod 755 ${V3_CHROOT_ROOT}/combo.sh"
}

cmd_spawn_helloworld() {
    # Phase 1b.1 — uses combo.sh (start daemon, send canned TLV, read child stderr).
    # Acceptance: J_initChild_entry marker for proc=com.example.helloworld in hilog.
    cmd_clean
    echo "Running combo.sh in chroot (will take ~30s)"
    timeout 60 "$HDC" -t "$HDC_SERIAL" shell \
        "chroot ${V3_CHROOT_ROOT} /system/bin/sh /combo.sh" </dev/null 2>&1 | tr -d '\r'
    echo ""
    echo "Now verify markers:"
    echo "  bash $0 verify"
}

cmd_spawn_mcd_driver() {
    echo "ERROR: spawn-mcd-driver requires substrate fix A1 first." >&2
    echo "  See docs/engine/V3-W2-PHASE-1B-PROGRESS.md §4.3 for substrate gap details." >&2
    echo "  Required: liboh_adapter_bridge.so visible at both /system/lib/" >&2
    echo "  AND /system/android/lib/ in chroot, so child's liboh_android_runtime.so" >&2
    echo "  loads (without parent-side SEGV in preInitTypefaceDefault)." >&2
    exit 2
}

cmd_verify() {
    echo "=== chroot bind mounts ==="
    hdc_shell "mount | grep v3-hbc-chroot | wc -l"
    echo "=== getenforce ==="
    hdc_shell "getenforce"
    echo "=== running daemons ==="
    hdc_shell "pgrep -af appspawn-x"
    echo "=== hilog AppSpawnX last 30 lines ==="
    hdc_shell "hilog -x | grep AppSpawnX | tail -30"
    echo ""
    echo "=== child stderr files (most recent first) ==="
    hdc_shell "ls -t ${V3_CHROOT_ROOT}/data/local/tmp/adapter_child_*.stderr 2>/dev/null | head -3"
    echo ""
    echo "To inspect a child stderr file:"
    echo "  hdc shell 'strings ${V3_CHROOT_ROOT}/data/local/tmp/adapter_child_<pid>.stderr' | tail -30"
}

cmd_clean() {
    hdc_shell "pkill -9 -f '/system/bin/appspawn-x' 2>&1; sleep 1; pgrep -af appspawn-x" || true
    echo "appspawn-x daemons killed (if any). chroot artifacts left intact."
}

case "${1:-}" in
    push-driver)         cmd_push_driver ;;
    push-mcd-apk)        cmd_push_mcd_apk ;;
    push-tlv)            cmd_push_tlv ;;
    spawn-helloworld)    cmd_spawn_helloworld ;;
    spawn-mcd-driver)    cmd_spawn_mcd_driver ;;
    verify)              cmd_verify ;;
    clean)               cmd_clean ;;
    "")
        echo "Usage: $0 {push-driver|push-mcd-apk|push-tlv|spawn-helloworld|spawn-mcd-driver|verify|clean}" >&2
        exit 2 ;;
    *)
        echo "Unknown subcommand: $1" >&2
        exit 2 ;;
esac
