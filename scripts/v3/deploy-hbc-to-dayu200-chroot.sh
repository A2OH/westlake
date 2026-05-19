#!/usr/bin/env bash
# ============================================================================
# deploy-hbc-to-dayu200-chroot.sh — V3 HBC chroot-containment deploy
#
# BRICK-IMPOSSIBLE-BY-CONSTRUCTION variant of
# `deploy-hbc-to-dayu200-hardened.sh`. Never writes to host /system.
# Phase-1 required subset = ~109 files (63 required + ~41 AOSP natives
# via _iter_aosp_natives + 3 dual-path shims + 0-3 optional) land under
#   /data/local/tmp/v3-hbc-chroot/
# and are exec'd via chroot(2). Recovery is `rm -rf` the chroot — no
# operator reflash needed. The full 563-file bundle inventory lives in
# `docs/engine/V3-HBC-ARTIFACT-MANIFEST.md`; Phase 1 deploys only the
# 109 subset needed for headless first-light.
#
# Authored 2026-05-19 (agent 62) implementing
# `docs/engine/V3-CHROOT-CONTAINMENT-PROPOSAL.md` §10 decisions 1+2
# (operator-approved Phase 1 chroot containment for the W2 retry).
#
# DESIGN PRINCIPLES
#   1. Chroot-only writes; host /system is NEVER touched   (proposal §4)
#   2. Channel-alive sentinel between every Stage          (G1, kept from hardened)
#   3. Required-artifact allowlist                         (G3 from hardened)
#   4. `setenforce 0` requires explicit --setenforce-0     (caveat per probe §1.3)
#   5. Loop-budget on chroot exec                          (Island pattern §6.4)
#   6. Idempotent teardown via `rm -rf` recovery           (proposal §2.2)
#   7. Per-stage logs to /tmp for forensic diff
#   8. NEVER `|| true` (the W2 brick mechanism)
#   9. HBC 全局三条 abort discipline                       (connect-key/timeout/[Fail]/drwx)
#
# CONTRAST WITH HARDENED (/system) DEPLOY
#   * NO chcon to system_lib_file (chroot files are shell_data_file:s0)
#   * NO `hdc target boot` (chroot needs no reboot to take effect)
#   * NO writes outside $V3_CHROOT_ROOT
#   * setenforce 0 acceptance via explicit flag (Phase 1 caveat; chroot
#     domain inherits sh:s0 which EPERMs on chcon under Enforcing per
#     V3-HDC-3.2.0B-PROBE-REPORT.md Probe 4)
#
# REFERENCED HBC / Island LINE NUMBERS (for traceability)
#   Hardened deploy-hbc-to-dayu200-hardened.sh L150-162: _alive_probe (G1)
#   Hardened deploy-hbc-to-dayu200-hardened.sh L295-309: _assert_no_fail_or_drwx
#   Hardened deploy-hbc-to-dayu200-hardened.sh L256-273: _verify_required_artifacts (G3)
#   Island demo/run-live.sh L35-42:                      mount restore pattern
#   Island demo/run-live.sh L60-62:                      force-stop OH Photos
#   Island demo/run-live.sh L71:                         -loop-budget=60000 pattern
#   Island tests/R2-jni-smoke/setup-7.0.sh L55:          full mkdir layout
#   Island tests/R2-jni-smoke/setup-7.0.sh L219-221:     ENV + chroot $ROOT linker exec
#
# Usage:
#   bash deploy-hbc-to-dayu200-chroot.sh COMMAND [FLAGS]
#
# Commands:
#   preflight          Stage 0 — verify hdc + artifacts + free space (READ-ONLY)
#   setup              Stage 1 — create $V3_CHROOT_ROOT directory skeleton
#   push               Stage 2 — hdc file send 563 artifacts into chroot
#   mount              Stage 3 — bind-mount /proc /sys /dev into chroot
#   env                Stage 4 — generate launch.sh wrapper inside chroot
#   launch             Stage 5 — chroot + run aa-launch helloworld headless
#   verify             Stage 6 — channel sentinel + marker scan post-launch
#   teardown           Recovery — umount + rm -rf $V3_CHROOT_ROOT
#   status             READ-ONLY — inspect current chroot state
#   all                preflight → setup → push → mount → env → launch → verify
#   help               Print this usage block
#
# Flags:
#   --dry-run          Print what would happen, don't execute
#   --setenforce-0     Explicitly accept Phase-1 SELinux caveat (required
#                      for chcon-needing ops; not strictly required for
#                      chroot-only without chcon on labels we own)
#   --hdc PATH         Override hdc binary (default /mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe)
#   --serial SERIAL    Override HDC_SERIAL
#   --root PATH        Override chroot root path (default /data/local/tmp/v3-hbc-chroot)
#
# Exit codes:
#   0    PASS
#   1    Assertion FAIL (resumable from same stage)
#   2    Usage / setup error
#   99   ABORT (HBC 全局三条 hit; do NOT re-run without operator review)
# ============================================================================

set -euo pipefail

# ============================================================================
# Globals
# ============================================================================

HDC="${HDC:-/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe}"
HDC_SERIAL="${HDC_SERIAL:-dd011a414436314130101250040eac00}"
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
V3_LOCAL="${V3_LOCAL:-$REPO_ROOT/westlake-deploy-ohos/v3-hbc}"
V3_CHROOT_ROOT="${V3_CHROOT_ROOT:-/data/local/tmp/v3-hbc-chroot}"
TS="${TS:-$(date +%Y%m%d-%H%M%S)}"
# LOG_DIR resolution (fix Tier-1 #2 from review 400642fa, §4 Stage 6):
#   Cross-invocation log-dir bug — TS regenerates per invocation, so the
#   stage-by-stage flow (SOP §3.2) writes launch.log to TS1 but `verify`
#   in a later invocation looks in TS2 → marker check always misses.
# Resolution:
#   1. If $V3_CHROOT_LOG_DIR is set, use it verbatim (operator-tagged run).
#   2. Otherwise default to /tmp/v3-chroot-deploy-current (stable symlink
#      pointing at the per-invocation timestamped dir, updated each run).
# The symlink target is the per-TS dir so forensic diffs across runs are
# preserved; reading via the symlink always finds the most-recent run.
V3_CHROOT_LOG_DIR_DEFAULT="/tmp/v3-chroot-deploy-current"
if [ -n "${V3_CHROOT_LOG_DIR:-}" ]; then
    LOG_DIR="$V3_CHROOT_LOG_DIR"
    LOG_DIR_PHYSICAL="$LOG_DIR"
elif [ -n "${LOG_DIR:-}" ]; then
    # Back-compat: respect a caller-set LOG_DIR (e.g., old scripts).
    LOG_DIR_PHYSICAL="$LOG_DIR"
else
    LOG_DIR_PHYSICAL="/tmp/v3-chroot-deploy-$TS"
    LOG_DIR="$V3_CHROOT_LOG_DIR_DEFAULT"
fi

# Free-space floor (in MB) — 412 MB bundle + 200 MB headroom
MIN_FREE_DATA_MB="${MIN_FREE_DATA_MB:-600}"

# Launch budget (Island pattern); 60s default
LAUNCH_BUDGET_SECS="${LAUNCH_BUDGET_SECS:-60}"

# Flags
DRY_RUN=0
SETENFORCE_0=0
# Tracks whether `setenforce 0` actually executed in this process (set by
# Stage 5). Used by the EXIT-trap restore to decide whether to issue
# `setenforce 1`. Distinct from $SETENFORCE_0 (the flag), which only
# indicates operator INTENT — if the script aborts before Stage 5 fires,
# nothing was applied and nothing needs restoring.
SETENFORCE_0_APPLIED=0
COMMAND=""

# Parse args (long-form flags before / after the positional command)
POSITIONAL=()
while [ $# -gt 0 ]; do
    case "$1" in
        --dry-run)        DRY_RUN=1; shift ;;
        --setenforce-0)   SETENFORCE_0=1; shift ;;
        --hdc)            HDC="$2"; shift 2 ;;
        --serial)         HDC_SERIAL="$2"; shift 2 ;;
        --root)           V3_CHROOT_ROOT="$2"; shift 2 ;;
        -h|--help)        COMMAND="help"; shift ;;
        --*)              echo "ERROR: unknown flag: $1" >&2; exit 2 ;;
        *)                POSITIONAL+=("$1"); shift ;;
    esac
done

COMMAND="${COMMAND:-${POSITIONAL[0]:-help}}"

# Create the physical timestamped LOG_DIR. If LOG_DIR is the default
# symlink path, point the symlink at the timestamped dir so subsequent
# invocations (e.g. stage-by-stage flow) read/write the same location.
mkdir -p "$LOG_DIR_PHYSICAL"
if [ "$LOG_DIR" = "$V3_CHROOT_LOG_DIR_DEFAULT" ] && [ "$LOG_DIR_PHYSICAL" != "$LOG_DIR" ]; then
    # Refresh symlink to point at this invocation's physical dir.
    rm -f "$V3_CHROOT_LOG_DIR_DEFAULT" 2>/dev/null
    ln -sfn "$LOG_DIR_PHYSICAL" "$V3_CHROOT_LOG_DIR_DEFAULT"
fi
mkdir -p "$LOG_DIR"

# ============================================================================
# Color + log helpers
# ============================================================================

if [ -t 1 ]; then
    RED=$'\033[31m'; GREEN=$'\033[32m'; YELLOW=$'\033[33m'
    CYAN=$'\033[36m'; BOLD=$'\033[1m'; RESET=$'\033[0m'
else
    RED=""; GREEN=""; YELLOW=""; CYAN=""; BOLD=""; RESET=""
fi

log()      { echo "${CYAN}[$(date +%H:%M:%S)]${RESET} $*" | tee -a "$LOG_DIR/deploy.log"; }
ok()       { echo "  ${GREEN}OK${RESET} $*"   | tee -a "$LOG_DIR/deploy.log"; }
warn()     { echo "  ${YELLOW}WARN${RESET} $*" | tee -a "$LOG_DIR/deploy.log" >&2; }
fail()     { echo "  ${RED}FAIL${RESET} $*" | tee -a "$LOG_DIR/deploy.log" >&2; }
pass_msg() { echo "${GREEN}${BOLD}[PASS]${RESET} $*" | tee -a "$LOG_DIR/deploy.log"; }
fail_msg() { echo "${RED}${BOLD}[FAIL]${RESET} $*" | tee -a "$LOG_DIR/deploy.log"; exit 1; }
abort()    { echo "${RED}${BOLD}[ABORT 99]${RESET} $*" | tee -a "$LOG_DIR/deploy.log" >&2; exit 99; }

# ============================================================================
# hdc wrappers
# ============================================================================

hdc_raw() {
    if [ "$DRY_RUN" = "1" ]; then
        echo "[DRY] hdc -t $HDC_SERIAL $*"
        return 0
    fi
    # </dev/null on the hdc.exe invocation: prevents stdin-slurp when this
    # helper runs inside a `while IFS= read` loop. hdc.exe (Windows build,
    # 3.2.0b) consumes stdin even when no input is requested, which terminates
    # the parent loop after iteration 1. Agent 71 caught this on _push_one;
    # the same applies to every helper that wraps "$HDC" ... regardless of
    # subcommand. See V3-W2-POSTMORTEM §H2.
    "$HDC" -t "$HDC_SERIAL" "$@" </dev/null
}

hdc_shell() {
    if [ "$DRY_RUN" = "1" ]; then
        echo "[DRY] hdc shell $*"
        return 0
    fi
    # See hdc_raw note re: </dev/null. Mandatory for stdin-isolation inside
    # while-read loops (and harmless outside them — `hdc shell <cmd>` does
    # not need stdin for forwarded execution).
    "$HDC" -t "$HDC_SERIAL" shell "$@" </dev/null 2>&1 | tr -d '\r'
}

# hdc_shell_check — use ONLY when the boolean result of the remote command matters
# (if/while/&&/||). hdc.exe always returns host exit 0, so `if hdc_shell "..."` is
# permanently true — that's the 2026-05-19 retry halt mechanism. This helper wraps
# the remote command and propagates the DEVICE-side exit code via a sentinel.
# Anti-pattern this exists to prevent: silent-NOOP (control flow always taken).
hdc_shell_check() {
    if [ "$DRY_RUN" = "1" ]; then
        return 0
    fi
    local out rc
    # See hdc_raw note re: </dev/null. Without this, agent-72 found that
    # `_push_one` calls hdc_shell_check from inside the AOSP-natives
    # `while IFS= read` loop, and hdc.exe consumed the loop's process-sub
    # stdin → loop terminated after iteration 1 (only 1 of 38 AOSP libs
    # landed). This is the real Phase-1a-plus push silent-truncation bug
    # (commit 0f4dc8be's _push_one </dev/null was necessary but insufficient).
    out=$("$HDC" -t "$HDC_SERIAL" shell "( $* ); echo __EXIT__=\$?" </dev/null 2>&1 | tr -d '\r')
    rc=$(echo "$out" | grep '^__EXIT__=' | tail -1 | sed 's/__EXIT__=//')
    if [ -z "$rc" ] || ! [[ "$rc" =~ ^[0-9]+$ ]]; then
        return 127  # channel issue — no exit marker observed
    fi
    return "$rc"
}

# G1 — Channel-alive sentinel. Mandatory between every stage.
_alive_probe() {
    if [ "$DRY_RUN" = "1" ]; then
        ok "G1 [DRY] sentinel skipped"
        return 0
    fi
    local mark="V3CHROOT_$$_$(date +%s)_${RANDOM:-0}"
    local out
    out=$("$HDC" -t "$HDC_SERIAL" shell "echo $mark" </dev/null 2>&1 | tr -d '\r\n')
    if [ -z "$out" ]; then
        abort "G1: hdc shell silent (empty stdout). HBC全局三条 abort condition. STOP."
    fi
    if [ "$out" != "$mark" ]; then
        abort "G1: hdc shell sentinel mismatch (sent='$mark' got='$out'). HBC全局三条 abort."
    fi
    return 0
}

# HBC 全局三条 abort assertion on any hdc output
_assert_no_fail_or_drwx() {
    local out="$1" ctx="$2"
    if echo "$out" | grep -qiE 'connect-key|timeout|\[Fail\]'; then
        abort "$ctx: HBC全局三条 hit (connect-key|timeout|[Fail]) in output: $out"
    fi
    if echo "$out" | grep -qE '^drwx|^[[:space:]]*drwx'; then
        abort "$ctx: HBC全局三条 hit (drwx in ls output): $out"
    fi
}

_to_win_path() {
    local p="$1"
    if command -v wslpath >/dev/null 2>&1; then
        wslpath -w "$p"
    else
        echo "$p"
    fi
}

# Fix Tier-2 #1 from review 400642fa, §1 row "setenforce 1 restored":
# Restore SELinux to Enforcing if Stage 5 actually issued `setenforce 0`.
# Called from EXIT trap (covers abort paths between Stage 5 and Stage 6)
# AND from stage_teardown (covers operator who runs launch then teardown
# without verify). Idempotent — safe to call any number of times.
_restore_enforcing() {
    # Guard against trap firing during DRY_RUN or before setenforce ever ran.
    if [ "$SETENFORCE_0_APPLIED" != "1" ]; then
        return 0
    fi
    if [ "$DRY_RUN" = "1" ]; then
        return 0
    fi
    # Best-effort restore — never abort here (we may already be exiting due
    # to another error). Use the raw hdc binary to bypass our DRY/wrapper
    # path and avoid recursion into log helpers if the channel is silent.
    # Wrap in `if ! ... ; then :; fi` instead of `|| true` to satisfy the
    # script's no-`|| true` discipline (W2 brick mechanism).
    if ! "$HDC" -t "$HDC_SERIAL" shell "setenforce 1" >/dev/null 2>&1; then
        : # hdc unreachable; we'll surface the mode check below regardless.
    fi
    local mode=""
    if mode=$("$HDC" -t "$HDC_SERIAL" shell "getenforce" 2>/dev/null | tr -d ' \r\n'); then
        :
    fi
    if [ "$mode" = "Enforcing" ]; then
        # Mark as restored so subsequent invocations are no-ops.
        SETENFORCE_0_APPLIED=0
        # Emit a log line if the log dir still exists (it should).
        [ -d "$LOG_DIR" ] && echo "[$(date +%H:%M:%S)] _restore_enforcing: SELinux restored to Enforcing" >> "$LOG_DIR/deploy.log" 2>/dev/null
    else
        # Surface the failure to stderr; do not abort.
        echo "WARNING: _restore_enforcing: SELinux mode is '$mode' (expected Enforcing). Board may be in Permissive until reboot or manual fix." >&2
        [ -d "$LOG_DIR" ] && echo "[$(date +%H:%M:%S)] _restore_enforcing: FAILED — mode='$mode'" >> "$LOG_DIR/deploy.log" 2>/dev/null
    fi
}

# ============================================================================
# G3 — Required-artifact allowlist (chroot layout)
# ============================================================================
# Mirrors deploy-hbc-to-dayu200-hardened.sh REQUIRED_ARTIFACTS but routes
# each file to its chroot destination per V3-CHROOT-CONTAINMENT-PROPOSAL §3.
# Format: <relpath-under-V3_LOCAL>=<chroot-relative-device-path>

REQUIRED_MANIFEST=(
    # ----- OH service .so (Phase 1: pushed into chroot; factory OH foundation
    # is NOT replaced — these load only into chroot-internal ART child) -----
    "lib/libwms.z.so=/system/lib/libwms.z.so"
    "lib/libappms.z.so=/system/lib/libappms.z.so"
    "lib/libbms.z.so=/system/lib/libbms.z.so"
    "lib/libskia_canvaskit.z.so=/system/lib/libskia_canvaskit.z.so"
    "lib/libappspawn_client.z.so=/system/lib/libappspawn_client.z.so"
    "lib/librender_service.z.so=/system/lib/librender_service.z.so"
    "lib/libabilityms.z.so=/system/lib/platformsdk/libabilityms.z.so"
    "lib/libscene_session.z.so=/system/lib/platformsdk/libscene_session.z.so"
    "lib/libscene_session_manager.z.so=/system/lib/platformsdk/libscene_session_manager.z.so"
    "lib/librender_service_base.z.so=/system/lib/platformsdk/librender_service_base.z.so"
    "lib/libappexecfwk_common.z.so=/system/lib/platformsdk/libappexecfwk_common.z.so"
    # ----- adapter shims (dual-path inside chroot to match host SOP) -----
    "lib/liboh_hwui_shim.so=/system/lib/liboh_hwui_shim.so"
    "lib/liboh_android_runtime.so=/system/lib/liboh_android_runtime.so"
    "lib/liboh_skia_rtti_shim.so=/system/lib/liboh_skia_rtti_shim.so"
    # ----- adapter bridges (dual-path 2026-05-19: agent 73 Phase 1b.2 finding —
    # liboh_android_runtime.so chain-fails on liboh_adapter_bridge.so resolution
    # when loaded from /system/android/lib/; dual-path matches lines 328-330) -----
    "lib/liboh_adapter_bridge.so=/system/lib/liboh_adapter_bridge.so"
    "lib/liboh_adapter_bridge.so=/system/android/lib/liboh_adapter_bridge.so"
    "lib/libapk_installer.so=/system/lib/libapk_installer.so"
    "lib/libinstalls.z.so=/system/lib/libinstalls.z.so"
    # ----- framework jars (chroot-relative; ART boot image baked strings
    # resolve via chroot filesystem view — proposal §3.2 path C) -----
    "jars/framework.jar=/system/android/framework/framework.jar"
    "jars/framework-classes.dex.jar=/system/android/framework/framework-classes.dex.jar"
    "jars/framework-res-package.jar=/system/android/framework/framework-res-package.jar"
    "jars/core-oj.jar=/system/android/framework/core-oj.jar"
    "jars/core-libart.jar=/system/android/framework/core-libart.jar"
    "jars/core-icu4j.jar=/system/android/framework/core-icu4j.jar"
    "jars/okhttp.jar=/system/android/framework/okhttp.jar"
    "jars/bouncycastle.jar=/system/android/framework/bouncycastle.jar"
    "jars/apache-xml.jar=/system/android/framework/apache-xml.jar"
    "jars/oh-adapter-framework.jar=/system/android/framework/oh-adapter-framework.jar"
    "jars/oh-adapter-runtime.jar=/system/android/framework/oh-adapter-runtime.jar"
    "jars/adapter-mainline-stubs.jar=/system/android/framework/adapter-mainline-stubs.jar"
    # ----- ICU + fonts -----
    "etc/icudt72l.dat=/system/android/etc/icu/icudt72l.dat"
    "etc/fonts.xml=/system/android/etc/fonts.xml"
    # Dual-path 2026-05-19: SystemFonts.getSystemPreinstalledFontConfig
    # hardcodes /system/etc/fonts.xml (agent 73 Phase 1b.2 finding).
    "etc/fonts.xml=/system/etc/fonts.xml"
    # ----- boot image (9 segments × 3 ext = 27 files) -----
    "bcp/boot.art=/system/android/framework/arm/boot.art"
    "bcp/boot.oat=/system/android/framework/arm/boot.oat"
    "bcp/boot.vdex=/system/android/framework/arm/boot.vdex"
    "bcp/boot-core-libart.art=/system/android/framework/arm/boot-core-libart.art"
    "bcp/boot-core-libart.oat=/system/android/framework/arm/boot-core-libart.oat"
    "bcp/boot-core-libart.vdex=/system/android/framework/arm/boot-core-libart.vdex"
    "bcp/boot-core-icu4j.art=/system/android/framework/arm/boot-core-icu4j.art"
    "bcp/boot-core-icu4j.oat=/system/android/framework/arm/boot-core-icu4j.oat"
    "bcp/boot-core-icu4j.vdex=/system/android/framework/arm/boot-core-icu4j.vdex"
    "bcp/boot-okhttp.art=/system/android/framework/arm/boot-okhttp.art"
    "bcp/boot-okhttp.oat=/system/android/framework/arm/boot-okhttp.oat"
    "bcp/boot-okhttp.vdex=/system/android/framework/arm/boot-okhttp.vdex"
    "bcp/boot-bouncycastle.art=/system/android/framework/arm/boot-bouncycastle.art"
    "bcp/boot-bouncycastle.oat=/system/android/framework/arm/boot-bouncycastle.oat"
    "bcp/boot-bouncycastle.vdex=/system/android/framework/arm/boot-bouncycastle.vdex"
    "bcp/boot-apache-xml.art=/system/android/framework/arm/boot-apache-xml.art"
    "bcp/boot-apache-xml.oat=/system/android/framework/arm/boot-apache-xml.oat"
    "bcp/boot-apache-xml.vdex=/system/android/framework/arm/boot-apache-xml.vdex"
    "bcp/boot-adapter-mainline-stubs.art=/system/android/framework/arm/boot-adapter-mainline-stubs.art"
    "bcp/boot-adapter-mainline-stubs.oat=/system/android/framework/arm/boot-adapter-mainline-stubs.oat"
    "bcp/boot-adapter-mainline-stubs.vdex=/system/android/framework/arm/boot-adapter-mainline-stubs.vdex"
    "bcp/boot-framework.art=/system/android/framework/arm/boot-framework.art"
    "bcp/boot-framework.oat=/system/android/framework/arm/boot-framework.oat"
    "bcp/boot-framework.vdex=/system/android/framework/arm/boot-framework.vdex"
    "bcp/boot-oh-adapter-framework.art=/system/android/framework/arm/boot-oh-adapter-framework.art"
    "bcp/boot-oh-adapter-framework.oat=/system/android/framework/arm/boot-oh-adapter-framework.oat"
    "bcp/boot-oh-adapter-framework.vdex=/system/android/framework/arm/boot-oh-adapter-framework.vdex"
    # ----- bin + cfg (cfg NOT consumed by OH init in chroot; carried for
    # parity with HBC SOP — appspawn-x is run manually per proposal §3.3) -----
    "bin/appspawn-x=/system/bin/appspawn-x"
    "etc/appspawn_x.cfg=/system/etc/init/appspawn_x.cfg"
    "etc/appspawn_x_sandbox.json=/system/etc/appspawn_x_sandbox.json"
    "etc/ld-musl-namespace-arm.ini=/system/etc/ld-musl-namespace-arm.ini"
    "etc/file_contexts=/system/etc/selinux/targeted/contexts/file_contexts"
)

# Optional artifacts (warn-not-fail)
# NOTE: All device-rel paths are prefixed with $V3_CHROOT_ROOT by _push_one
# (line ~497). HelloWorld.apk's "/data/local/tmp/HelloWorld.apk" therefore
# lands at $V3_CHROOT_ROOT/data/local/tmp/HelloWorld.apk — INSIDE the chroot,
# NOT at host /data/local/tmp/. Brick-safe (no exception to the all-inside-
# chroot invariant); agent 62's self-audit description of an "outside-chroot
# exception" was incorrect. Verified by agent 63 review (2026-05-19, commit
# 400642fa, §1 row 2).
OPTIONAL_MANIFEST=(
    "jars/framework-res.apk=/system/android/framework/framework-res.apk"
    "lib/libsurface.z.so=/system/lib/libsurface.z.so"
    "app/HelloWorld.apk=/data/local/tmp/HelloWorld.apk"
)

# Verify every required artifact exists locally (G3)
_verify_required_artifacts() {
    local missing=0 entry rel mc
    for entry in "${REQUIRED_MANIFEST[@]}"; do
        rel="${entry%%=*}"
        if [ ! -f "$V3_LOCAL/$rel" ]; then
            fail "G3 required missing: $V3_LOCAL/$rel"
            missing=$((missing+1))
        fi
    done
    for entry in "${OPTIONAL_MANIFEST[@]}"; do
        rel="${entry%%=*}"
        if [ ! -f "$V3_LOCAL/$rel" ]; then
            warn "G3 optional absent: $V3_LOCAL/$rel"
        fi
    done
    mc=${#REQUIRED_MANIFEST[@]}
    if [ $missing -gt 0 ]; then
        fail_msg "G3: $missing/$mc required artifacts missing — fix W1 pull before deploy"
    fi
    ok "G3: all $mc required artifacts present"
}

# ============================================================================
# AOSP native .so collector (38 files matching *.so excluding *.z.so / liboh_*)
# These go into $V3_CHROOT_ROOT/system/android/lib/ — separate from the
# .z.so OH service replacements above.
# ============================================================================

_iter_aosp_natives() {
    # Outputs absolute local paths, one per line
    local so bn
    for so in "$V3_LOCAL/lib"/*.so; do
        [ -f "$so" ] || continue
        bn=$(basename "$so")
        case "$bn" in
            *.z.so)              continue ;;
            liboh_*)             continue ;;
            libapk_installer.so) continue ;;
        esac
        echo "$so"
    done
}

# ============================================================================
# Stage 0 — preflight (READ-ONLY against board)
# ============================================================================

stage_preflight() {
    log "Stage 0 · preflight (READ-ONLY; no writes to board)"

    # hdc binary present
    if [ "$DRY_RUN" = "0" ] && [ ! -x "$HDC" ]; then
        abort "Stage 0: hdc binary not found / not executable at: $HDC"
    fi
    ok "hdc binary: $HDC"

    # hdc version — LOG, do not gate (per V3-HDC-3.2.0B-PROBE-REPORT)
    local ver
    if [ "$DRY_RUN" = "1" ]; then
        ver="[DRY] (skipped)"
    else
        ver=$("$HDC" version 2>&1 | head -1 | tr -d '\r\n')
        [ -n "$ver" ] || ver=$("$HDC" -v 2>&1 | head -1 | tr -d '\r\n')
    fi
    log "hdc version: $ver  (logged; NOT gated — probe report widens 3.2.0b)"

    # Board enumerated
    local listing
    if [ "$DRY_RUN" = "1" ]; then
        listing="[DRY] (skipped)"
        ok "device check skipped (--dry-run)"
    else
        listing=$("$HDC" list targets 2>&1 | tr -d '\r')
        if ! echo "$listing" | grep -q "$HDC_SERIAL"; then
            abort "Stage 0: DAYU200 $HDC_SERIAL not connected (hdc list: '$listing')"
        fi
        ok "device visible: $HDC_SERIAL"
    fi

    # G1 first sentinel
    _alive_probe
    ok "G1: hdc shell sentinel OK"

    # G3 — local artifact inventory
    _verify_required_artifacts

    # Free space — chroot bundle is ~412 MB; require MIN_FREE_DATA_MB
    if [ "$DRY_RUN" = "0" ]; then
        local free_kb free_mb
        # awk on host side — toybox on DAYU200 may lack awk; parse the raw df output here
        free_kb=$(hdc_shell "df /data 2>/dev/null" | tr -d '\r' | awk 'NR==2 {print $4}' | tr -d ' \n')
        if [ -z "$free_kb" ] || ! [[ "$free_kb" =~ ^[0-9]+$ ]]; then
            warn "could not parse 'df /data' free space; got: '$free_kb'"
        else
            free_mb=$((free_kb / 1024))
            if [ "$free_mb" -lt "$MIN_FREE_DATA_MB" ]; then
                abort "Stage 0: /data free space ${free_mb}MB < required ${MIN_FREE_DATA_MB}MB"
            fi
            ok "/data free space: ${free_mb}MB (>= ${MIN_FREE_DATA_MB}MB required)"
        fi
    fi

    # Capture pre-deploy board state for postmortem
    if [ "$DRY_RUN" = "0" ]; then
        local pre="$LOG_DIR/board-state-pre.txt"
        {
            echo "# Pre-deploy board state $(date)"
            echo "## uname"
            "$HDC" -t "$HDC_SERIAL" shell "uname -a" 2>&1 | tr -d '\r'
            echo "## getenforce"
            "$HDC" -t "$HDC_SERIAL" shell "getenforce" 2>&1 | tr -d '\r'
            echo "## df /data"
            "$HDC" -t "$HDC_SERIAL" shell "df /data" 2>&1 | tr -d '\r'
            echo "## ls /data/local/tmp"
            "$HDC" -t "$HDC_SERIAL" shell "ls -la /data/local/tmp" 2>&1 | tr -d '\r'
            echo "## chroot root exists?"
            "$HDC" -t "$HDC_SERIAL" shell "ls -ld $V3_CHROOT_ROOT 2>&1" 2>&1 | tr -d '\r'
        } > "$pre"
        ok "pre-deploy state captured: $pre"
    fi

    # SELinux mode advisory (NOT a gate; chroot itself doesn't need setenforce 0
    # unless we hit denials)
    if [ "$DRY_RUN" = "0" ]; then
        local mode
        mode=$(hdc_shell "getenforce" | tr -d ' \r\n')
        log "SELinux mode: $mode"
        if [ "$mode" = "Enforcing" ] && [ "$SETENFORCE_0" = "0" ]; then
            warn "SELinux is Enforcing and --setenforce-0 NOT specified."
            warn "  chroot mount/exec may EPERM in sh:s0 domain — see proposal §9 Q4."
            warn "  If launch fails with avc denials, re-run launch with --setenforce-0."
        fi
        if [ "$SETENFORCE_0" = "1" ]; then
            warn "--setenforce-0 set: launcher will temporarily disable SELinux (Phase 1 caveat)."
        fi
    fi

    _alive_probe
    pass_msg "Stage 0 PASS — preflight clean, ready to setup chroot"
}

# ============================================================================
# Stage 1 — setup chroot directory skeleton
# ============================================================================

stage_setup() {
    log "Stage 1 · setup chroot dir skeleton at $V3_CHROOT_ROOT"
    _alive_probe

    # The mkdir set mirrors Island setup-7.0.sh L55 + HBC layout (system/lib,
    # system/lib/platformsdk, system/android/lib, system/android/framework/arm,
    # system/android/etc/icu) plus chroot mount points (proc, sys, dev) and
    # init.cfg parity dir (system/etc/init).
    local dirs=(
        "$V3_CHROOT_ROOT"
        "$V3_CHROOT_ROOT/system"
        "$V3_CHROOT_ROOT/system/bin"
        "$V3_CHROOT_ROOT/system/lib"
        "$V3_CHROOT_ROOT/system/lib/platformsdk"
        "$V3_CHROOT_ROOT/system/android"
        "$V3_CHROOT_ROOT/system/android/lib"
        "$V3_CHROOT_ROOT/system/android/framework"
        "$V3_CHROOT_ROOT/system/android/framework/arm"
        "$V3_CHROOT_ROOT/system/android/etc"
        "$V3_CHROOT_ROOT/system/android/etc/icu"
        "$V3_CHROOT_ROOT/system/etc"
        "$V3_CHROOT_ROOT/system/etc/init"
        "$V3_CHROOT_ROOT/system/etc/selinux"
        "$V3_CHROOT_ROOT/system/etc/selinux/targeted"
        "$V3_CHROOT_ROOT/system/etc/selinux/targeted/contexts"
        "$V3_CHROOT_ROOT/proc"
        "$V3_CHROOT_ROOT/sys"
        "$V3_CHROOT_ROOT/dev"
        "$V3_CHROOT_ROOT/lib"
        "$V3_CHROOT_ROOT/data"
        "$V3_CHROOT_ROOT/data/local"
        "$V3_CHROOT_ROOT/data/local/tmp"
        "$V3_CHROOT_ROOT/host_tmp"
        "$V3_CHROOT_ROOT/tmp"
    )

    local d_list
    d_list=$(IFS=' '; echo "${dirs[*]}")
    local out
    out=$(hdc_shell "mkdir -p $d_list 2>&1")
    _assert_no_fail_or_drwx "$out" "stage_setup mkdir"

    if [ "$DRY_RUN" = "0" ]; then
        # Verify each dir created
        local d kind
        for d in "${dirs[@]}"; do
            kind=$(hdc_shell "stat -c '%F' $d 2>&1" | tr -d '\r\n')
            if [ "$kind" != "directory" ]; then
                abort "Stage 1: $d not a directory ($kind)"
            fi
        done
        ok "verified ${#dirs[@]} chroot directories"
    else
        ok "[DRY] ${#dirs[@]} chroot dirs would be created"
    fi

    # Fix Tier-1 #1 from review 400642fa, §4 Stage 4/5:
    # The chroot exec sites (`chroot $V3_CHROOT_ROOT /system/bin/sh ...` at
    # Stage 4 line ~703 and Stage 5 line ~772) and launch.sh's shebang
    # (`#!/system/bin/sh`) require a shell binary INSIDE the chroot. Stage 2
    # pushes only appspawn-x into /system/bin/, so without this copy the
    # chroot exec fails with "No such file or directory" and the
    # `[v3-chroot-launch]` marker is never emitted (yet the script would
    # silently report PASS).
    #
    # Discover which shell binary exists on the board (toybox-only OHOS
    # variants don't have /system/bin/sh) and copy the first that exists
    # into $V3_CHROOT_ROOT/system/bin/sh via a board-internal cp (brick-safe
    # — only writes inside the chroot).
    if [ "$DRY_RUN" = "0" ]; then
        local probe_out shell_src=""
        probe_out=$(hdc_shell "ls /system/bin/sh /system/bin/toybox /system/bin/toolbox 2>/dev/null" | tr -d '\r')
        local cand
        for cand in /system/bin/sh /system/bin/toybox /system/bin/toolbox; do
            if echo "$probe_out" | grep -qx "$cand"; then
                shell_src="$cand"
                break
            fi
        done
        if [ -z "$shell_src" ]; then
            abort "Stage 1: no shell binary found on board (tried /system/bin/{sh,toybox,toolbox}). Cannot populate chroot shell. STOP."
        fi
        log "discovered board shell: $shell_src → will copy to $V3_CHROOT_ROOT/system/bin/sh"
        local cpout
        cpout=$(hdc_shell "cp $shell_src $V3_CHROOT_ROOT/system/bin/sh && chmod 755 $V3_CHROOT_ROOT/system/bin/sh && echo CP_OK")
        _assert_no_fail_or_drwx "$cpout" "stage_setup shell copy"
        if ! echo "$cpout" | grep -q CP_OK; then
            abort "Stage 1: shell copy did not emit CP_OK: $cpout"
        fi
        # Verify the chroot shell is now executable
        local verify_out
        verify_out=$(hdc_shell "test -x $V3_CHROOT_ROOT/system/bin/sh && echo SH_OK" | tr -d '\r\n')
        if [ "$verify_out" != "SH_OK" ]; then
            abort "Stage 1: $V3_CHROOT_ROOT/system/bin/sh not executable after copy (got: '$verify_out')"
        fi
        ok "chroot shell installed: $shell_src → $V3_CHROOT_ROOT/system/bin/sh (verified -x)"
    else
        ok "[DRY] would discover board shell and copy to $V3_CHROOT_ROOT/system/bin/sh"
    fi

    _alive_probe
    pass_msg "Stage 1 PASS — chroot skeleton ready at $V3_CHROOT_ROOT"
}

# ============================================================================
# Stage 2 — push 563 artifacts into chroot via hdc file send
# ============================================================================

_push_one() {
    # Args: <local_path> <chroot-relative-device-path>
    local local_path="$1" device_rel="$2"
    local device_path="$V3_CHROOT_ROOT$device_rel"

    if [ ! -f "$local_path" ]; then
        abort "_push_one: local missing: $local_path"
    fi
    local win
    win=$(_to_win_path "$local_path")
    # Two structural fixes (agent 71 finding 2026-05-19):
    # (1) </dev/null — hdc.exe reads stdin when inside `while read`, gobbling
    #     the iterator's remaining lines after the first push, terminating loop.
    # (2) post-push verify via hdc_shell_check — `hdc file send` may return host
    #     exit 0 with silent device-side failure (same class as hdc_shell bug).
    local out
    out=$(hdc_raw file send "$win" "$device_path" </dev/null 2>&1 | tr -d '\r')
    _assert_no_fail_or_drwx "$out" "_push_one $device_rel"
    # Verify the target actually landed (size > 0); aborts on silent failure
    local local_sz
    local_sz=$(stat -c '%s' "$local_path" 2>/dev/null)
    if ! hdc_shell_check "test -s $device_path"; then
        abort "_push_one: device target NOT present after push: $device_path (local=$local_path size=$local_sz)"
    fi
}

stage_push() {
    log "Stage 2 · push HBC artifacts into chroot"
    _alive_probe

    local pushed=0 sampled=0
    local entry rel dev

    # Required manifest first (chroot-relative paths)
    for entry in "${REQUIRED_MANIFEST[@]}"; do
        rel="${entry%%=*}"
        dev="${entry##*=}"
        _push_one "$V3_LOCAL/$rel" "$dev"
        pushed=$((pushed+1))
        # Channel sentinel every 25 files
        if [ $((pushed % 25)) -eq 0 ]; then
            _alive_probe
            log "  ...pushed $pushed files; channel alive"
        fi
    done

    # AOSP native .so (~38 files) → /system/android/lib/ within chroot
    local so bn
    while IFS= read -r so; do
        bn=$(basename "$so")
        _push_one "$so" "/system/android/lib/$bn"
        pushed=$((pushed+1))
        if [ $((pushed % 25)) -eq 0 ]; then
            _alive_probe
            log "  ...pushed $pushed files; channel alive"
        fi
    done < <(_iter_aosp_natives)

    # 3 adapter shims also dual-path into /system/android/lib (HBC SOP §3c)
    local shim
    for shim in liboh_hwui_shim.so liboh_android_runtime.so liboh_skia_rtti_shim.so; do
        _push_one "$V3_LOCAL/lib/$shim" "/system/android/lib/$shim"
        pushed=$((pushed+1))
    done

    # Optional manifest (warn-not-fail)
    for entry in "${OPTIONAL_MANIFEST[@]}"; do
        rel="${entry%%=*}"
        dev="${entry##*=}"
        if [ -f "$V3_LOCAL/$rel" ]; then
            _push_one "$V3_LOCAL/$rel" "$dev"
            pushed=$((pushed+1))
        fi
    done

    _alive_probe

    # Spot-check: byte count of 3 sentinel files (head of REQUIRED_MANIFEST)
    if [ "$DRY_RUN" = "0" ]; then
        local sample_entries=(
            "${REQUIRED_MANIFEST[0]}"      # libwms.z.so (first in list)
            "${REQUIRED_MANIFEST[17]}"     # framework.jar (mid-list)
            "${REQUIRED_MANIFEST[58]}"     # bin/appspawn-x (near end)
        )
        local lsz dsz local_path device_path
        for entry in "${sample_entries[@]}"; do
            rel="${entry%%=*}"
            dev="${entry##*=}"
            local_path="$V3_LOCAL/$rel"
            device_path="$V3_CHROOT_ROOT$dev"
            [ -f "$local_path" ] || continue
            lsz=$(stat -c %s "$local_path" 2>/dev/null || echo 0)
            dsz=$(hdc_shell "stat -c %s $device_path 2>/dev/null" | tr -d '\r\n')
            if [ -z "$dsz" ] || [ "$lsz" != "$dsz" ]; then
                abort "Stage 2: size mismatch on $dev (local=$lsz device=$dsz) — corrupt push"
            fi
            sampled=$((sampled+1))
            ok "size-verified: $dev ($lsz bytes)"
        done
    fi

    # chmod batch (binaries executable)
    if [ "$DRY_RUN" = "0" ]; then
        local cout
        cout=$(hdc_shell "chmod 755 $V3_CHROOT_ROOT/system/bin/appspawn-x 2>&1; chmod 755 $V3_CHROOT_ROOT/system/bin/* 2>&1; echo CHMOD_OK")
        _assert_no_fail_or_drwx "$cout" "Stage 2 chmod"
        echo "$cout" | grep -q CHMOD_OK || warn "chmod batch did not emit CHMOD_OK"
        ok "chmod batch complete"
    fi

    _alive_probe
    pass_msg "Stage 2 PASS — $pushed files pushed, $sampled size-verified"
}

# ============================================================================
# Stage 3 — bind-mount /proc /sys /dev into chroot
# ============================================================================

stage_mount() {
    log "Stage 3 · bind-mount /proc /sys /dev into chroot"
    _alive_probe

    # Idempotent: check mountpoint first (Island pattern from demo/run-live.sh L36-37)
    local mp out
    for mp in proc sys dev; do
        local cmd="mountpoint -q $V3_CHROOT_ROOT/$mp"
        if [ "$DRY_RUN" = "1" ]; then
            log "[DRY] would: $cmd || mount --bind /$mp $V3_CHROOT_ROOT/$mp"
            continue
        fi
        if hdc_shell_check "$cmd"; then
            ok "$mp already mounted into chroot (idempotent)"
        else
            out=$(hdc_shell "mount --bind /$mp $V3_CHROOT_ROOT/$mp 2>&1")
            _assert_no_fail_or_drwx "$out" "stage_mount $mp"
            # Verify mount took effect — use hdc_shell_check (device-side exit code)
            if ! hdc_shell_check "mountpoint -q $V3_CHROOT_ROOT/$mp"; then
                abort "Stage 3: mount --bind /$mp did NOT take effect (mountpoint check failed)"
            fi
            ok "mount --bind /$mp → $V3_CHROOT_ROOT/$mp"
        fi
    done

    # Read-only bind-mount host libs so the chroot's dynamic ELFs (/system/bin/sh
    # needs /lib/ld-musl-arm.so.1, plus appspawn-x needs /system/lib/* later)
    # can resolve their interpreter + transitive deps. Agent 68 caught this:
    # without /lib, kernel returns misleading ENOENT-on-missing-interp for
    # `chroot ... /system/bin/sh`. Two-step bind+remount-ro is the Linux idiom.
    local ro_mp
    for ro_mp in lib system/lib; do
        local src="/$ro_mp"
        local dst="$V3_CHROOT_ROOT/$ro_mp"
        if [ "$DRY_RUN" = "1" ]; then
            log "[DRY] would: mount --bind $src $dst && mount -o remount,ro,bind $dst"
            continue
        fi
        if hdc_shell_check "mountpoint -q $dst"; then
            ok "$ro_mp already RO-mounted into chroot (idempotent)"
        else
            out=$(hdc_shell "mount --bind $src $dst 2>&1 && mount -o remount,ro,bind $dst 2>&1 && echo BIND_RO_OK")
            _assert_no_fail_or_drwx "$out" "stage_mount RO $ro_mp"
            if ! echo "$out" | grep -q BIND_RO_OK; then
                abort "Stage 3: RO bind-mount $src failed: $out"
            fi
            if ! hdc_shell_check "mountpoint -q $dst"; then
                abort "Stage 3: RO bind $src did NOT take effect"
            fi
            ok "mount --bind RO $src → $dst"
        fi
    done

    if [ "$DRY_RUN" = "0" ]; then
        # Summary
        local mounts
        mounts=$(hdc_shell "mount | grep v3-hbc-chroot" | tr -d '\r')
        log "active chroot mounts:"
        echo "$mounts" | sed 's/^/    /' | tee -a "$LOG_DIR/deploy.log"
    fi

    _alive_probe
    pass_msg "Stage 3 PASS — /proc /sys /dev RW + /lib /system/lib RO bind-mounted into chroot"
}

# ============================================================================
# Stage 4 — env wrap (generate launch.sh inside chroot)
# ============================================================================

stage_env() {
    log "Stage 4 · generate /launch.sh wrapper inside chroot"
    _alive_probe

    # The launch.sh runs INSIDE the chroot. Env vars mirror Island
    # demo/run-live.sh L21 but resolve to chroot-relative paths.
    # Per HBC SOP DEPLOY_SOP.md the appspawn-x boot string set:
    #   ANDROID_ROOT=/system/android
    #   ANDROID_DATA=/data
    #   BOOTCLASSPATH = -Xbootclasspath chain on framework jars
    # libfakelog.so/libproperty_stub.so are Island-specific; HBC stack
    # uses its own liblog.so chain already in /system/lib/.
    local launch_local="$LOG_DIR/launch.sh"
    cat > "$launch_local" <<'LAUNCHSH'
#!/system/bin/sh
# Auto-generated by deploy-hbc-to-dayu200-chroot.sh Stage 4
# Runs INSIDE the V3-HBC chroot. argv[1..] is forwarded to the inner command.
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

echo "[v3-chroot-launch] PWD=$(pwd) UID=$(id -u) ARGV=$*"
echo "[v3-chroot-launch] LD_LIBRARY_PATH=$LD_LIBRARY_PATH"
echo "[v3-chroot-launch] BOOTCLASSPATH=$BOOTCLASSPATH"
echo "[v3-chroot-launch] boot.art present: $(ls /system/android/framework/arm/boot.art 2>/dev/null || echo MISSING)"

if [ $# -eq 0 ]; then
    echo "[v3-chroot-launch] no command given; exiting 0 (env probe only)"
    exit 0
fi

# Forward to inner command (typically: appspawn-x --socket-name AppSpawnX,
# or for Phase-1 first-light: a direct binary invocation that emits the
# MainActivity.onCreate marker)
exec "$@"
LAUNCHSH

    if [ "$DRY_RUN" = "1" ]; then
        log "[DRY] would push $launch_local → $V3_CHROOT_ROOT/launch.sh"
        ok "[DRY] /launch.sh contents:"
        sed 's/^/    /' "$launch_local"
    else
        local win
        win=$(_to_win_path "$launch_local")
        local out
        out=$(hdc_raw file send "$win" "$V3_CHROOT_ROOT/launch.sh" 2>&1 | tr -d '\r')
        _assert_no_fail_or_drwx "$out" "stage_env push launch.sh"
        out=$(hdc_shell "chmod 755 $V3_CHROOT_ROOT/launch.sh && echo CHMOD_OK")
        _assert_no_fail_or_drwx "$out" "stage_env chmod launch.sh"
        echo "$out" | grep -q CHMOD_OK || abort "Stage 4: chmod 755 launch.sh failed: $out"
        ok "/launch.sh installed and executable"

        # Sanity probe — chroot in and `pwd`+ls. Promoted from warn-not-fail
        # to ABORT per review 400642fa, Tier 2 #4. Now that Stage 1 guarantees
        # an executable /system/bin/sh inside the chroot, a "No such" here is
        # a real signal (most likely a baked-string mismatch in framework.jar
        # path resolution or a chroot lib-loader issue) — not a missing-shell
        # artifact. Failing fast prevents Stage 5 from emitting a misleading
        # PASS when launch.sh would never execute.
        out=$(hdc_shell "chroot $V3_CHROOT_ROOT /system/bin/sh /launch.sh /system/bin/sh -c 'pwd; ls /system/android/framework/framework.jar 2>&1' 2>&1")
        log "chroot env probe output:"
        echo "$out" | sed 's/^/    /' | tee -a "$LOG_DIR/deploy.log"
        if echo "$out" | grep -q "No such"; then
            abort "Stage 4: chroot env probe FAILED — saw 'No such' in output. Either /system/bin/sh inside chroot is broken (Stage 1 should have installed it) or framework.jar is not visible at /system/android/framework/framework.jar inside chroot. STOP."
        fi
        ok "chroot env probe: framework.jar visible inside chroot"
    fi

    _alive_probe
    pass_msg "Stage 4 PASS — launch.sh installed inside chroot"
}

# ============================================================================
# Stage 5 — launch (chroot + run hello-world headless, loop-budgeted)
# ============================================================================

stage_launch() {
    log "Stage 5 · launch HBC HelloWorld inside chroot (budget ${LAUNCH_BUDGET_SECS}s)"
    _alive_probe

    # Island pattern §6.3 — force-stop OH Photos before any display reload.
    # Even though Phase 1 is headless, we kill stale viewers defensively.
    if [ "$DRY_RUN" = "0" ]; then
        hdc_shell "aa force-stop com.ohos.photos 2>&1 | tail -1" >/dev/null
        ok "force-stop com.ohos.photos (Island defensive pattern)"
    fi

    # Phase 1 caveat: optionally setenforce 0 to bypass chroot-domain denials
    # per V3-CHROOT-CONTAINMENT-PROPOSAL §9 Q4.
    if [ "$SETENFORCE_0" = "1" ] && [ "$DRY_RUN" = "0" ]; then
        local mode_before
        mode_before=$(hdc_shell "getenforce" | tr -d ' \r\n')
        log "SELinux before launch: $mode_before"
        hdc_shell "setenforce 0 2>&1" >/dev/null
        # Mark applied so the EXIT trap (and any later teardown) will restore
        # Enforcing if the script aborts before Stage 6 — fix Tier-2 #1 from
        # review 400642fa, §1 row "setenforce 1 restored".
        SETENFORCE_0_APPLIED=1
        local mode_after
        mode_after=$(hdc_shell "getenforce" | tr -d ' \r\n')
        log "SELinux after setenforce 0: $mode_after"
        if [ "$mode_after" != "Permissive" ]; then
            warn "setenforce 0 did NOT switch to Permissive (got: $mode_after)"
        else
            ok "SELinux temporarily Permissive (will restore in Stage 6 or EXIT trap)"
        fi
    fi

    # Loop-budgeted exec. Island uses `apk_runner -loop=N -loop-budget=Tms`
    # but for V3 first-light we just invoke env-probe-only mode of launch.sh
    # (i.e., no extra command → just prints env and exits 0). This validates
    # the chroot CAN exec + the env is right. The actual ART/aa-launch step
    # is gated behind future work (W4/W5/W6) per proposal §5 Phase 1.
    #
    # If $V3_CHROOT_HELLO_CMD is set, we run that inside the chroot instead.
    local inner="${V3_CHROOT_HELLO_CMD:-}"
    local cmd_inside="/system/bin/sh /launch.sh"
    if [ -n "$inner" ]; then
        cmd_inside="/system/bin/sh /launch.sh $inner"
        log "launching custom inner: $inner"
    else
        log "launching env-probe-only (no inner command set; \$V3_CHROOT_HELLO_CMD=)"
    fi

    local launch_log="$LOG_DIR/launch.log"
    if [ "$DRY_RUN" = "1" ]; then
        log "[DRY] would: timeout ${LAUNCH_BUDGET_SECS}s hdc shell 'chroot $V3_CHROOT_ROOT $cmd_inside'"
    else
        # SHELL-side timeout protects against the chroot process hanging
        # forever without exiting.
        set +e
        timeout "${LAUNCH_BUDGET_SECS}s" "$HDC" -t "$HDC_SERIAL" shell \
            "chroot $V3_CHROOT_ROOT $cmd_inside" 2>&1 | tee "$launch_log"
        local rc=${PIPESTATUS[0]}
        set -e
        log "launch exit code: $rc (timeout=124 means budget hit)"
    fi

    _alive_probe
    pass_msg "Stage 5 PASS — chroot launch issued; log at $launch_log"
}

# ============================================================================
# Stage 6 — verify (channel sentinel + marker scan + restore SELinux)
# ============================================================================

stage_verify() {
    log "Stage 6 · verify post-launch state"
    _alive_probe

    if [ "$DRY_RUN" = "0" ]; then
        # Restore SELinux to Enforcing if Stage 5 actually issued setenforce 0.
        # Uses the centralized _restore_enforcing helper (idempotent; also
        # registered as EXIT trap so this stage is the happy-path call).
        if [ "$SETENFORCE_0_APPLIED" = "1" ]; then
            _restore_enforcing
            local mode
            mode=$(hdc_shell "getenforce" | tr -d ' \r\n')
            if [ "$mode" = "Enforcing" ]; then
                ok "SELinux restored to Enforcing"
            else
                warn "SELinux mode is now: $mode (expected Enforcing)"
            fi
        fi

        # Marker scan — env-probe-only mode emits [v3-chroot-launch] PWD=...
        # When V3_CHROOT_HELLO_CMD is set to an ART/HBC invocation, the
        # marker becomes MainActivity.onCreate (override via $MARKER).
        local marker="${MARKER:-\\[v3-chroot-launch\\]}"
        local launch_log="$LOG_DIR/launch.log"
        if [ -f "$launch_log" ] && grep -qE "$marker" "$launch_log"; then
            ok "marker '$marker' observed in launch log"
            grep -E "$marker" "$launch_log" | head -3 | sed 's/^/    /'
        else
            warn "marker '$marker' NOT observed in $launch_log"
            warn "first 20 lines of launch log:"
            head -20 "$launch_log" 2>/dev/null | sed 's/^/    /' | tee -a "$LOG_DIR/deploy.log"
        fi

        # hilog tail for AVC denials + ART output
        local hilog
        hilog=$(hdc_shell "hilog -x 2>/dev/null | tail -100" | tr -d '\r')
        if echo "$hilog" | grep -qiE 'avc:.*denied'; then
            warn "hilog shows avc denials in last 100 lines:"
            echo "$hilog" | grep -E 'avc:.*denied' | head -5 | sed 's/^/    /' | tee -a "$LOG_DIR/deploy.log"
        else
            ok "no avc denials in hilog tail (good sign)"
        fi

        # Capture post-deploy board state
        local post="$LOG_DIR/board-state-post.txt"
        {
            echo "# Post-deploy board state $(date)"
            echo "## getenforce"
            "$HDC" -t "$HDC_SERIAL" shell "getenforce" 2>&1 | tr -d '\r'
            echo "## chroot du"
            "$HDC" -t "$HDC_SERIAL" shell "du -sh $V3_CHROOT_ROOT 2>&1" 2>&1 | tr -d '\r'
            echo "## stale procs under chroot"
            "$HDC" -t "$HDC_SERIAL" shell "ps -ef | grep -F $V3_CHROOT_ROOT | grep -v grep" 2>&1 | tr -d '\r'
        } > "$post"
        ok "post-deploy state captured: $post"
    fi

    _alive_probe
    pass_msg "Stage 6 PASS — verify complete; logs at $LOG_DIR/"
}

# ============================================================================
# Teardown (recovery) — umount + rm -rf chroot. Idempotent.
# ============================================================================

stage_teardown() {
    log "Teardown · umount + rm -rf $V3_CHROOT_ROOT"

    if [ "$DRY_RUN" = "0" ]; then
        # Channel must be alive for teardown to work, but if it isn't we
        # warn-not-abort because the goal is recovery.
        if ! "$HDC" -t "$HDC_SERIAL" shell "echo TEARDOWN_PROBE" 2>&1 | tr -d '\r\n' | grep -q TEARDOWN_PROBE; then
            warn "hdc channel silent at teardown entry; rerun after operator power-cycle"
            return 1
        fi
    fi

    # Kill any procs that may be using the chroot
    if [ "$DRY_RUN" = "0" ]; then
        hdc_shell "pkill -9 -f $V3_CHROOT_ROOT 2>/dev/null; echo PKILL_DONE" >/dev/null
        ok "killed any chroot-rooted processes"
    fi

    # umount in reverse order. Each is best-effort but logged.
    local mp out
    # Reverse order; teardown both RW (proc/sys/dev) and RO (lib/system/lib) bind-mounts
    for mp in system/lib lib dev sys proc; do
        if [ "$DRY_RUN" = "1" ]; then
            log "[DRY] would: umount $V3_CHROOT_ROOT/$mp"
            continue
        fi
        # Check whether mountpoint first; only umount if mounted (device-side exit code)
        if hdc_shell_check "mountpoint -q $V3_CHROOT_ROOT/$mp"; then
            out=$(hdc_shell "umount $V3_CHROOT_ROOT/$mp 2>&1")
            # We don't abort on umount messages (the recovery path must complete)
            # but we DO surface unexpected output.
            if echo "$out" | grep -qE 'busy|denied|not mounted'; then
                warn "umount $mp: $out"
            else
                ok "umount $V3_CHROOT_ROOT/$mp"
            fi
        else
            ok "$mp already unmounted (idempotent)"
        fi
    done

    # rm -rf the chroot (brick-safe; lives entirely under /data/local/tmp)
    if [ "$DRY_RUN" = "1" ]; then
        log "[DRY] would: rm -rf $V3_CHROOT_ROOT"
    else
        local rmout
        rmout=$(hdc_shell "rm -rf $V3_CHROOT_ROOT && echo RM_OK")
        _assert_no_fail_or_drwx "$rmout" "stage_teardown rm"
        if ! echo "$rmout" | grep -q RM_OK; then
            warn "rm did not emit RM_OK: $rmout"
        fi
        # Verify clean state
        local lout
        lout=$(hdc_shell "ls -ld $V3_CHROOT_ROOT 2>&1")
        if ! echo "$lout" | grep -qE 'No such'; then
            abort "Teardown: $V3_CHROOT_ROOT still exists after rm: $lout"
        fi
        ok "$V3_CHROOT_ROOT removed cleanly"
    fi

    # Defense-in-depth (fix Tier-2 #1 from review 400642fa): if operator
    # passed --setenforce-0 to this teardown invocation AND the board is
    # currently Permissive, restore Enforcing. This handles the kill-9
    # case where the launch process's EXIT trap never fired.
    if [ "$DRY_RUN" = "0" ] && [ "$SETENFORCE_0" = "1" ]; then
        local cur_mode
        cur_mode=$(hdc_shell "getenforce" | tr -d ' \r\n')
        if [ "$cur_mode" = "Permissive" ]; then
            warn "teardown: board is Permissive; restoring to Enforcing (defense-in-depth)"
            SETENFORCE_0_APPLIED=1
            _restore_enforcing
        fi
    fi

    pass_msg "Teardown PASS — chroot removed; board unchanged outside /data/local/tmp"
}

# ============================================================================
# Status (READ-ONLY inspector)
# ============================================================================

stage_status() {
    log "Status · inspect current chroot state (READ-ONLY)"
    if [ "$DRY_RUN" = "1" ]; then
        log "[DRY] would inspect chroot at $V3_CHROOT_ROOT"
        return 0
    fi

    _alive_probe
    log "chroot root:"
    hdc_shell "ls -ld $V3_CHROOT_ROOT 2>&1" | sed 's/^/    /'
    log "chroot disk usage:"
    hdc_shell "du -sh $V3_CHROOT_ROOT 2>/dev/null" | sed 's/^/    /'
    log "active mounts under chroot:"
    hdc_shell "mount | grep -F $V3_CHROOT_ROOT 2>/dev/null" | sed 's/^/    /'
    log "framework.jar present?"
    hdc_shell "ls -la $V3_CHROOT_ROOT/system/android/framework/framework.jar 2>&1" | sed 's/^/    /'
    log "appspawn-x present?"
    hdc_shell "ls -la $V3_CHROOT_ROOT/system/bin/appspawn-x 2>&1" | sed 's/^/    /'
    log "boot.art present?"
    hdc_shell "ls -la $V3_CHROOT_ROOT/system/android/framework/arm/boot.art 2>&1" | sed 's/^/    /'
    log "SELinux mode:"
    hdc_shell "getenforce" | sed 's/^/    /'

    pass_msg "Status complete (no writes)"
}

# ============================================================================
# Dispatcher
# ============================================================================

print_help() {
    sed -n '/^# ====/,/^# ====$/p' "$0" | sed 's/^# \{0,1\}//'
}

main() {
    # Fix Tier-2 #1 from review 400642fa: register EXIT trap BEFORE any
    # stage can issue `setenforce 0`. Restores SELinux to Enforcing if
    # Stage 5 actually applied setenforce 0 in this process. Idempotent
    # (checks SETENFORCE_0_APPLIED flag) and safe to fire on any exit
    # path (success, abort, error). Help/status paths exit early without
    # touching SELinux, so the trap is a no-op for those.
    trap '_restore_enforcing' EXIT

    case "$COMMAND" in
        preflight|stage-0)  stage_preflight ;;
        setup|stage-1)      stage_setup ;;
        push|stage-2)       stage_push ;;
        mount|stage-3)      stage_mount ;;
        env|stage-4)        stage_env ;;
        launch|stage-5)     stage_launch ;;
        verify|stage-6)     stage_verify ;;
        teardown)           stage_teardown ;;
        status)             stage_status ;;
        all)
            stage_preflight
            stage_setup
            stage_push
            stage_mount
            stage_env
            stage_launch
            stage_verify
            ;;
        help|-h|--help)     print_help; exit 0 ;;
        *)
            echo "ERROR: unknown command '$COMMAND'" >&2
            echo "Valid: preflight setup push mount env launch verify teardown status all help" >&2
            exit 2
            ;;
    esac
}

main
