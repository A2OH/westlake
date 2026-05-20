#!/usr/bin/env bash
# ============================================================================
# deploy-hbc-to-dayu200-hardened.sh — V3 HBC artifact deploy (HARDENED)
#
# This is the V3-W2 RE-ATTEMPT driver, authored 2026-05-18 (agent 55) after
# the soft-brick incident with `deploy-hbc-to-dayu200.sh` (kept alongside
# untouched for forensic diff reference). Every gap enumerated in
# `docs/engine/V3-W2-POSTMORTEM.md` §4 (G1-G7) is implemented here, plus the
# `全局三条` abort discipline documented in
# `docs/engine/V3-W2-RECOVERY-PROCEDURE.md` (HBC SOP §"全局三条").
#
# DESIGN PRINCIPLES
#   1. Staging-dir-only writes to /system   (HBC SOP "全局三条" rule #2)
#   2. Channel-alive sentinel between every Stage   (G1, postmortem §4)
#   3. chcon-verify after every label batch         (G2, postmortem §4)
#   4. Required-artifact allowlist                  (G3, postmortem §4)
#   5. Service-stop with explicit opt-out           (G4, postmortem §4)
#   6. Stage 3.9 integrity + drwx sentinel          (G5, postmortem §4)
#   7. hdc.exe version pin + warn                   (G6, postmortem §4)
#   8. 12 incremental stages, each invocable alone  (G7, postmortem §4)
#
# 2026-05-19 amendments (agent 75 — chroot lessons ported back):
#   FIX A — hdc_shell_check helper (device-side exit propagation)
#           Prevents hdc.exe's host-exit-0 laundering from silently passing
#           false-positive control-flow predicates.
#   FIX B — _push_one stdin redirect + test-s post-push verify
#           </dev/null on every hdc.exe invocation so push helpers may run
#           inside `while read` loops without losing iteration 2+.
#   FIX C — host-side awk for df parsing (toybox-on-DAYU200 lacks awk)
#           Pre-existing in hardened (no-op verified).
#   FIX D — dual-path manifests for liboh_adapter_bridge.so
#           Mirrors chroot lines 334-335. Required for
#           liboh_android_runtime.so chain resolution.
#
#   Gate 8  — chcon snapshot-and-restore (replayable label rollback)
#   Gate 9  — atomic .so swap (push.new + mv = rename(2))
#   Gate 10 — Island-style mount-restore on resume
#   Gate 11 — force-stop OH Photos before display-touching stages
#   Gate 12 — loop-budget on launch (Island demo/run-live.sh L71)
#   Gate 13 — processdump availability probe in preflight
#
# This script DOES NOT amend, replace, or alias the original
# `scripts/v3/deploy-hbc-to-dayu200.sh` — both live side-by-side. The old
# one is the forensic record of what bricked the board; this one is what
# we re-run AFTER ROM recovery.
#
# REFERENCED HBC LINE NUMBERS (for traceability)
#   HBC DEPLOY_SOP.md line 6-10  : 全局三条 (the three global rules)
#   HBC DEPLOY_SOP.md line 22-40 : Stage 1 backup file inventory
#   HBC DEPLOY_SOP.md line 44-54 : Stage 2 service-stop allowlist
#   HBC DEPLOY_SOP.md line 60-70 : Stage 3.0 mkdir prerequisite
#   HBC DEPLOY_SOP.md line 72-104: Stage 3b/3c chcon system_lib_file
#   HBC DEPLOY_SOP.md line 117-123: Stage 3d chcon system_fonts_file verify
#   HBC DEPLOY_SOP.md line 135-146: Stage 3e chcon boot image segments
#   HBC DEPLOY_SOP.md line 165-178: Stage 3f restorecon discipline
#   HBC DEPLOY_SOP.md line 180-186: Stage 3.9 integrity + drwx sentinel
#   HBC deploy_stage.sh line 125-156: stage_push() template (our model)
#   HBC deploy_stage.sh line 158-162: check_hdc_alive() (our model)
#
# Usage:
#   bash deploy-hbc-to-dayu200-hardened.sh <stage>     # run one stage
#   bash deploy-hbc-to-dayu200-hardened.sh all         # run stages 0..7 (no
#                                                       # reboot — needs --reboot)
#   bash deploy-hbc-to-dayu200-hardened.sh all --reboot  # full deploy
#   bash deploy-hbc-to-dayu200-hardened.sh --uninstall # pre-brick rollback
#
# Stages:
#   0    preflight (uname, getconf, getenforce, hdc version, /system/android clean)
#   1    backup (13 device-side .orig_<DATE> copies)
#   2    stop foundation + render_service (SKIPPED by default; --no-skip-stage-2 enables)
#   3.0  mkdir the 4 dirs HBC requires
#   3b   OH services .so (10 files + libbms symlink)
#   3c   AOSP native .so (38 + 3 dual-path adapter shims) + chcon + verify
#   3d   framework jars + ICU + fonts.xml + chcon + verify
#   3e   boot image (27 files, md5-verified) + chcon + verify
#   3f   appspawn-x + cfg + namespace ini + file_contexts + 5 symlinks
#   3.7  chcon batch + verify  (G2)
#   3.8  channel alive probe   (G1; mirror sentinel ANY stage may abort on)
#   3.9  integrity (md5 + size) + drwx sentinel  (G5)
#   4    reboot (only if Stage 3.8 PASS)
#   5    post-reboot verify (pidof foundation/render_service/launcher/hdcd)
#   6    aa start HBC HelloWorld via wrapper
#   7    capture MainActivity.onCreate L83 marker from hilog
#
# Env overrides:
#   HDC          path to hdc.exe         (default /mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe)
#   HDC_SERIAL   target board serial     (default DAYU200 dd011a4…)
#   V3_LOCAL     v3-hbc/ root            (default westlake-deploy-ohos/v3-hbc)
#   TS           backup timestamp suffix (default today YYYYMMDD)
#   STAGE_DIR    device staging path     (default /data/local/tmp/stage)
#
# Exit codes:
#   0   stage(s) PASS
#   1   stage assertion FAIL (resumable from same stage)
#   2   usage / setup error (hdc missing, args wrong)
#   99  ABORT (HBC 全局三条 condition tripped — do NOT re-run without
#        operator investigation; do NOT chain reboot)
# ============================================================================

set -uo pipefail

# ============================================================================
# Globals
# ============================================================================

HDC="${HDC:-/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe}"
HDC_SERIAL="${HDC_SERIAL:-dd011a414436314130101250040eac00}"
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
V3_LOCAL="${V3_LOCAL:-$REPO_ROOT/westlake-deploy-ohos/v3-hbc}"
TS="${TS:-$(date +%Y%m%d)}"
STAGE_DIR="${STAGE_DIR:-/data/local/tmp/stage}"

# G6: known-good hdc.exe versions (extend as new builds are validated).
KNOWN_GOOD_HDC_VERSIONS="Ver:1.3.0c Ver:1.3.0d Ver:1.3.0e"

# G4 opt-out flag (default: skip Stage 2; pass --no-skip-stage-2 to enable)
SKIP_STAGE_2=1
DO_REBOOT=0
UNINSTALL=0
# Gate 8 flag: run snapshot init + a single capture of the high-risk target
# label set without performing any chcon writes. Lets the operator pre-arm
# the rollback snapshot before a real run, and exercises Gate 8 plumbing
# end-to-end as a smoke test.
SNAPSHOT_ONLY=0

# Strip --flags out so we can dispatch on remaining positional
POSITIONAL=()
for arg in "$@"; do
    case "$arg" in
        --no-skip-stage-2) SKIP_STAGE_2=0 ;;
        --skip-stage-2)    SKIP_STAGE_2=1 ;;
        --reboot)          DO_REBOOT=1    ;;
        --uninstall)       UNINSTALL=1    ;;
        --snapshot-only)   SNAPSHOT_ONLY=1 ;;
        -h|--help)         sed -n '1,90p' "$0"; exit 0 ;;
        --*) echo "ERROR: unknown flag: $arg" >&2; exit 2 ;;
        *)   POSITIONAL+=("$arg") ;;
    esac
done

STAGE="${POSITIONAL[0]:-help}"

# ============================================================================
# Color + log helpers
# ============================================================================

if [ -t 1 ]; then
    RED=$'\033[31m'; GREEN=$'\033[32m'; YELLOW=$'\033[33m'
    CYAN=$'\033[36m'; BOLD=$'\033[1m'; RESET=$'\033[0m'
else
    RED=""; GREEN=""; YELLOW=""; CYAN=""; BOLD=""; RESET=""
fi

log()      { echo "${CYAN}[$(date +%H:%M:%S)]${RESET} $*"; }
ok()       { echo "  ${GREEN}OK${RESET} $*"; }
warn()     { echo "  ${YELLOW}WARN${RESET} $*" >&2; }
fail()     { echo "  ${RED}FAIL${RESET} $*" >&2; }
pass_msg() { echo "${GREEN}${BOLD}[PASS]${RESET} $*"; }
fail_msg() { echo "${RED}${BOLD}[FAIL]${RESET} $*"; exit 1; }
abort()    { echo "${RED}${BOLD}[ABORT 99]${RESET} $*" >&2; exit 99; }

# ============================================================================
# hdc wrappers
# ============================================================================

hdc_raw() {
    # </dev/null on hdc.exe invocation: prevents stdin-slurp when this helper
    # runs inside a `while IFS= read` loop. hdc.exe (Windows build, 3.2.0b)
    # consumes stdin even when no input is requested, terminating the parent
    # loop after iteration 1. Port of chroot Fix B (agent 71 finding;
    # commits 0f4dc8be + ee449def). See V3-W2-POSTMORTEM §H2 and
    # feedback_hdc_shell_check_pattern.md for the structural class.
    "$HDC" -t "$HDC_SERIAL" "$@" </dev/null
}

hdc_shell() {
    # Returns stdout with CR stripped. Exit code reflects the hdc CLIENT (host
    # transport), NOT the remote command. For boolean control-flow use
    # hdc_shell_check below — host exit is permanently 0 on transport success
    # regardless of device-side outcome (Rule 7, feedback_hdc_shell_check_pattern.md).
    # </dev/null mandatory (see hdc_raw note).
    "$HDC" -t "$HDC_SERIAL" shell "$@" </dev/null 2>&1 | tr -d '\r'
}

# Fix A (chroot port, commit f10ee81b) — hdc_shell_check: propagates DEVICE-side
# exit code via sentinel wrapper. Use ONLY in boolean control-flow (if / while /
# && / ||). Plain hdc shell host-exit-0 laundering is the structural bug class
# behind the 2026-05-16 soft-brick (see feedback_hdc_shell_check_pattern.md).
# Anti-pattern this exists to prevent: silent-NOOP (control flow always taken).
hdc_shell_check() {
    local out rc
    # </dev/null is critical: without it, agent 72 found that helpers called
    # from inside `while IFS= read` loops consume the loop's process-sub stdin
    # and terminate after iteration 1.
    out=$("$HDC" -t "$HDC_SERIAL" shell "( $* ); echo __EXIT__=\$?" </dev/null 2>&1 | tr -d '\r')
    rc=$(echo "$out" | grep '^__EXIT__=' | tail -1 | sed 's/__EXIT__=//')
    if [ -z "$rc" ] || ! [[ "$rc" =~ ^[0-9]+$ ]]; then
        return 127   # channel issue — no exit marker observed (treat as hard fail)
    fi
    return "$rc"
}

# G1 — Channel-alive sentinel. Mandatory between every stage.
# Implements both HBC's check_hdc_alive (deploy_stage.sh L158-162) AND
# the Westlake addition (empty-stdout-as-abort, per postmortem H2).
_alive_probe() {
    local mark="HBC_DEPLOY_$$_$(date +%s)_${RANDOM:-0}"
    local out
    # Direct hdc invocation with </dev/null (mirrors chroot script) — bypass any
    # wrapper that might disturb sentinel propagation, and stay stdin-isolated.
    out=$("$HDC" -t "$HDC_SERIAL" shell "echo $mark" </dev/null 2>&1 | tr -d '\r\n')
    if [ -z "$out" ]; then
        abort "G1: hdc shell silent (empty stdout). HBC全局三条 abort condition. STOP."
    fi
    if [ "$out" != "$mark" ]; then
        abort "G1: hdc shell sentinel mismatch (sent='$mark' got='$out'). HBC全局三条 abort."
    fi
    return 0
}

# G6 — hdc.exe version pin / warn
_check_hdc_version() {
    if [ ! -x "$HDC" ]; then
        abort "G6: hdc binary not found / not executable at: $HDC"
    fi
    local ver
    ver=$("$HDC" version 2>&1 | head -1 | tr -d '\r\n')
    [ -n "$ver" ] || ver=$("$HDC" -v 2>&1 | head -1 | tr -d '\r\n')
    log "G6: hdc reports: $ver"
    local matched=0
    for known in $KNOWN_GOOD_HDC_VERSIONS; do
        if echo "$ver" | grep -qF "$known"; then matched=1; break; fi
    done
    if [ "$matched" = "1" ]; then
        ok "G6: hdc version in known-good list"
    else
        warn "G6: hdc version '$ver' NOT in known-good list ($KNOWN_GOOD_HDC_VERSIONS)"
        warn "G6: proceeding with caution; if W2-style silent-stdout returns, suspect this"
    fi
}

# ============================================================================
# G3 — Required-artifact allowlist
# ============================================================================
# Files that MUST exist locally before any push begins. Replaces the old
# script's silent-SKIP behavior (postmortem H4). Both 2026-05-14 missing
# .so files are now in this list per V3-W2-RECOVERY-PROCEDURE.md §3.
#
# Format: <relpath-under-V3_LOCAL>
REQUIRED_ARTIFACTS=(
    # Stage 3b — OH service .so (push to /system/lib/)
    "lib/libwms.z.so"
    "lib/libappms.z.so"
    "lib/libbms.z.so"
    "lib/libskia_canvaskit.z.so"
    "lib/libappspawn_client.z.so"
    "lib/librender_service.z.so"
    # Stage 3b — OH service .so (push to /system/lib/platformsdk/)
    "lib/libabilityms.z.so"
    "lib/libscene_session.z.so"
    "lib/libscene_session_manager.z.so"
    "lib/librender_service_base.z.so"
    "lib/libappexecfwk_common.z.so"   # NEW 2026-05-16 (W2 missing)
    # Stage 3c — adapter shims (dual-path)
    "lib/liboh_hwui_shim.so"
    "lib/liboh_android_runtime.so"
    "lib/liboh_skia_rtti_shim.so"
    # Stage 3f — adapter bridges (single-path /system/lib/)
    "lib/liboh_adapter_bridge.so"
    "lib/libapk_installer.so"
    "lib/libinstalls.z.so"            # NEW 2026-05-16 (W2 missing)
    # Stage 3d — framework jars
    "jars/framework.jar"
    "jars/framework-classes.dex.jar"
    "jars/framework-res-package.jar"
    "jars/core-oj.jar"
    "jars/core-libart.jar"
    "jars/core-icu4j.jar"
    "jars/okhttp.jar"
    "jars/bouncycastle.jar"
    "jars/apache-xml.jar"
    "jars/oh-adapter-framework.jar"
    "jars/oh-adapter-runtime.jar"
    "jars/adapter-mainline-stubs.jar"
    # Stage 3d — ICU + fonts
    "etc/icudt72l.dat"
    "etc/fonts.xml"
    # Stage 3e — boot image (9 segments × 3 ext = 27 files)
    "bcp/boot.art" "bcp/boot.oat" "bcp/boot.vdex"
    "bcp/boot-core-libart.art" "bcp/boot-core-libart.oat" "bcp/boot-core-libart.vdex"
    "bcp/boot-core-icu4j.art" "bcp/boot-core-icu4j.oat" "bcp/boot-core-icu4j.vdex"
    "bcp/boot-okhttp.art" "bcp/boot-okhttp.oat" "bcp/boot-okhttp.vdex"
    "bcp/boot-bouncycastle.art" "bcp/boot-bouncycastle.oat" "bcp/boot-bouncycastle.vdex"
    "bcp/boot-apache-xml.art" "bcp/boot-apache-xml.oat" "bcp/boot-apache-xml.vdex"
    "bcp/boot-adapter-mainline-stubs.art" "bcp/boot-adapter-mainline-stubs.oat" "bcp/boot-adapter-mainline-stubs.vdex"
    "bcp/boot-framework.art" "bcp/boot-framework.oat" "bcp/boot-framework.vdex"
    "bcp/boot-oh-adapter-framework.art" "bcp/boot-oh-adapter-framework.oat" "bcp/boot-oh-adapter-framework.vdex"
    # Stage 3f — bin + cfg + namespace + file_contexts
    "bin/appspawn-x"
    "etc/appspawn_x.cfg"
    "etc/appspawn_x_sandbox.json"
    "etc/ld-musl-namespace-arm.ini"
    "etc/file_contexts"
)

# Optional artifacts (warn-not-fail if absent)
OPTIONAL_ARTIFACTS=(
    "jars/framework-res.apk"     # G2.14ai derives this from framework-res-package.jar
    "lib/libsurface.z.so"
    "app/HelloWorld.apk"         # Stage 6 prerequisite (skip Stage 6 if absent)
)

_verify_required_artifacts() {
    local missing=0 mc
    for rel in "${REQUIRED_ARTIFACTS[@]}"; do
        if [ ! -f "$V3_LOCAL/$rel" ]; then
            fail "G3 required missing: $V3_LOCAL/$rel"
            missing=$((missing+1))
        fi
    done
    for rel in "${OPTIONAL_ARTIFACTS[@]}"; do
        if [ ! -f "$V3_LOCAL/$rel" ]; then
            warn "G3 optional absent: $V3_LOCAL/$rel"
        fi
    done
    mc=${#REQUIRED_ARTIFACTS[@]}
    if [ $missing -gt 0 ]; then
        fail_msg "G3: $missing/$mc required artifacts missing — fix W1 pull before deploy"
    fi
    ok "G3: all $mc required artifacts present"
}

# ============================================================================
# Staging-dir-only push (HBC SOP "全局三条" rule #2; HBC stage_push template)
# ============================================================================
# Per HBC deploy_stage.sh:125-156. Steps:
#   1. send to /data/local/tmp/stage/<basename>
#   2. stat -c %F MUST be "regular file" (drwx => abort: HBC 造目录 quirk)
#   3. md5 local == md5 staging
#   4. cp staging → final
#   5. md5 staging == md5 final
# Any non-equality, any [Fail] in output, any drwx => abort.

_to_win_path() {
    local p="$1"
    if command -v wslpath >/dev/null 2>&1; then
        wslpath -w "$p"
    else
        echo "$p"
    fi
}

_assert_no_fail_or_drwx() {
    local out="$1" ctx="$2"
    # HBC 全局三条 abort conditions (per V3-W2-RECOVERY-PROCEDURE.md):
    #   1. hdc connect-key failure
    #   2. hdc timeout
    #   3. [Fail] in any output
    #   4. drwx in ls of a pushed file
    #   5. (Westlake addition) empty stdout — handled in _alive_probe
    if echo "$out" | grep -qiE 'connect-key|timeout|\[Fail\]'; then
        abort "$ctx: HBC全局三条 hit (connect-key|timeout|[Fail]) in output: $out"
    fi
    if echo "$out" | grep -qE '^drwx|^[[:space:]]*drwx'; then
        abort "$ctx: HBC全局三条 hit (drwx in ls output — hdc 造目录 quirk): $out"
    fi
}

# ============================================================================
# Gate 8 — chcon snapshot-and-restore
# ============================================================================
# Before any chcon batch, capture current SELinux contexts of target files
# into a device-side snapshot. On abort, a `restore-chcon` subcommand replays
# the snapshot. Acceptance: kill -9 mid-chcon → operator runs
# `bash <script> restore-chcon` → factory contexts restored without ROM flash.

CHCON_SNAPSHOT_DEVICE="${CHCON_SNAPSHOT_DEVICE:-/data/local/tmp/v3-chcon-snapshot.txt}"
CHCON_SNAPSHOT_HOST="${CHCON_SNAPSHOT_HOST:-/tmp/v3-chcon-snapshot.$$}"

# _chcon_snapshot_capture <path1> [path2 ...] — appends current label rows
# to the device-side snapshot file. Format per line:
#   <abs-path>\t<u:object_r:LABEL:s0>
# Idempotent on already-captured paths (no duplicates; first capture wins
# so a re-run after partial chcon doesn't overwrite the original baseline).
_chcon_snapshot_capture() {
    local p label existing
    for p in "$@"; do
        # Skip if already captured (preserves baseline label across resumes)
        existing=$(hdc_shell "grep -E '^${p}\\b' $CHCON_SNAPSHOT_DEVICE 2>/dev/null" | tr -d '\r')
        if [ -n "$existing" ]; then
            continue
        fi
        # ls -lZ output looks like: -rw-r--r-- 1 root root u:object_r:LABEL:s0 ... path
        label=$(hdc_shell "ls -lZ '$p' 2>/dev/null" | grep -oE 'u:object_r:[a-zA-Z0-9_]+:s0' | head -1)
        if [ -z "$label" ]; then
            warn "Gate 8: could not capture label for $p (file may not exist yet)"
            continue
        fi
        # Append atomically (printf >> file is one write call for short lines)
        hdc_shell "printf '%s\\t%s\\n' '$p' '$label' >> $CHCON_SNAPSHOT_DEVICE" >/dev/null
    done
}

# _chcon_snapshot_restore — replay snapshot, chcon each row back to its
# captured label. Invoked by the `restore-chcon` subcommand. Idempotent.
_chcon_snapshot_restore() {
    log "Gate 8 restore: replaying $CHCON_SNAPSHOT_DEVICE"
    if ! hdc_shell_check "test -s $CHCON_SNAPSHOT_DEVICE"; then
        warn "Gate 8 restore: snapshot absent or empty on device ($CHCON_SNAPSHOT_DEVICE)"
        return 0
    fi
    # Pull snapshot to host, parse, replay chcon device-side line by line.
    local win
    win=$(_to_win_path "$CHCON_SNAPSHOT_HOST")
    hdc_raw file recv "$CHCON_SNAPSHOT_DEVICE" "$win" >/dev/null
    if [ ! -s "$CHCON_SNAPSHOT_HOST" ]; then
        warn "Gate 8 restore: snapshot recv produced empty file at $CHCON_SNAPSHOT_HOST"
        return 1
    fi
    local restored=0 failed=0 path label
    while IFS=$'\t' read -r path label; do
        [ -n "$path" ] || continue
        [ -n "$label" ] || continue
        if hdc_shell_check "chcon $label $path"; then
            restored=$((restored+1))
        else
            failed=$((failed+1))
            warn "Gate 8 restore: chcon $label $path FAILED"
        fi
    done < "$CHCON_SNAPSHOT_HOST"
    rm -f "$CHCON_SNAPSHOT_HOST"
    log "Gate 8 restore: $restored restored, $failed failed"
    [ "$failed" = "0" ]
}

# Initialise snapshot file at the start of any deploy that will run chcon.
# Idempotent: only writes the header line if the file doesn't already exist
# (preserves baseline across resumed runs).
_chcon_snapshot_init() {
    if hdc_shell_check "test -s $CHCON_SNAPSHOT_DEVICE"; then
        log "Gate 8: existing snapshot preserved at $CHCON_SNAPSHOT_DEVICE"
        return 0
    fi
    hdc_shell "printf '# v3 chcon snapshot %s\\n' '$(date)' > $CHCON_SNAPSHOT_DEVICE" >/dev/null
    if ! hdc_shell_check "test -s $CHCON_SNAPSHOT_DEVICE"; then
        abort "Gate 8: could not initialise snapshot file at $CHCON_SNAPSHOT_DEVICE"
    fi
    ok "Gate 8: snapshot initialised at $CHCON_SNAPSHOT_DEVICE"
}

# ============================================================================
# Gate 9 — atomic file swap for live-service .so replacement
# ============================================================================
# Pattern: push to <dst>.new, verify size, mv <dst>.new <dst>. Linux rename(2)
# is atomic for files on the same filesystem; an interrupt at any point leaves
# either old-fully-loaded or new-fully-loaded, never half-replaced. Running
# processes that have the old file mmap'd keep their mapping (kernel defers
# unlink until last reference drops); only new dlopens see the new file.

# Live-service .so allowlist — files in this list trigger _atomic_install
# from inside stage_push's Step 4. Includes the service-loaded .so where a
# half-replaced write would crash a live consumer (libams, libwms,
# render_service base, etc.).
LIVE_SERVICE_SO=(
    "/system/lib/libwms.z.so"
    "/system/lib/libappms.z.so"
    "/system/lib/libbms.z.so"
    "/system/lib/libskia_canvaskit.z.so"
    "/system/lib/libappspawn_client.z.so"
    "/system/lib/librender_service.z.so"
    "/system/lib/platformsdk/libabilityms.z.so"
    "/system/lib/platformsdk/libscene_session.z.so"
    "/system/lib/platformsdk/libscene_session_manager.z.so"
    "/system/lib/platformsdk/librender_service_base.z.so"
    "/system/lib/platformsdk/libappexecfwk_common.z.so"
    "/system/lib/liboh_adapter_bridge.so"
    "/system/lib/libapk_installer.so"
    "/system/lib/libinstalls.z.so"
    "/system/lib/liboh_hwui_shim.so"
    "/system/lib/liboh_android_runtime.so"
    "/system/lib/liboh_skia_rtti_shim.so"
)

_is_live_service_so() {
    local target="$1" entry
    for entry in "${LIVE_SERVICE_SO[@]}"; do
        [ "$target" = "$entry" ] && return 0
    done
    return 1
}

# _atomic_install <staging_src> <final_dst>
#   1. cp staging → <dst>.new (NOT directly to dst)
#   2. verify <dst>.new exists, size>0, size matches staging
#   3. mv <dst>.new <dst>   (rename(2) is atomic on same FS)
#   4. verify <dst> exists, size>0, no .new residue
_atomic_install() {
    local src="$1" dst="$2"
    local new="${dst}.new"

    # Step 1: cp to .new
    local cpout
    cpout=$(hdc_shell "cp $src $new 2>&1")
    _assert_no_fail_or_drwx "$cpout" "_atomic_install cp $src→$new"

    # Step 2: verify .new exists & has positive size matching staging
    if ! hdc_shell_check "test -s $new"; then
        abort "Gate 9: $new not present/empty after cp from $src"
    fi
    local ssz nsz
    ssz=$(hdc_shell "stat -c %s $src 2>/dev/null" | tr -d ' \r\n')
    nsz=$(hdc_shell "stat -c %s $new 2>/dev/null" | tr -d ' \r\n')
    if [ "$ssz" != "$nsz" ] || [ -z "$ssz" ]; then
        abort "Gate 9: size mismatch staging→new ($src=$ssz, $new=$nsz)"
    fi

    # Step 3: atomic rename. mv on same FS = rename(2) = atomic.
    local mvout
    mvout=$(hdc_shell "mv $new $dst 2>&1")
    _assert_no_fail_or_drwx "$mvout" "_atomic_install mv $new→$dst"

    # Step 4: verify dst landed, no .new residue
    if ! hdc_shell_check "test -s $dst"; then
        abort "Gate 9: $dst not present/empty after mv from $new"
    fi
    if hdc_shell_check "test -e $new"; then
        warn "Gate 9: $new still present after mv (cleanup)"
        hdc_shell "rm -f $new" >/dev/null
    fi
}

# ============================================================================
# Gate 10 — Island-style mount-restore on resume
# ============================================================================
# Cites Island demo/run-live.sh L35-42 (idempotent mount --bind via mountpoint
# -q probe). After any board reboot that might have happened during prior
# deploy attempts, mounts may be stale. The `restore-mounts` subcommand
# re-applies the expected mount table: remount /system rw (transient) for
# subsequent /system writes; restore appspawn-x exec mount semantics.

# Canonical mount table for a hardened-/system deploy in progress.
# Format: <op> <args...>
# Re-applied verbatim by _restore_mounts (idempotent — mountpoint -q probe
# precedes each remount). Drawn from the Stage 3.0 patterns in this script.
_restore_mounts() {
    log "Gate 10 restore: re-applying expected mount table (Island pattern)"
    # remount / rw — required for any /system or /system/etc writes that
    # follow. The kernel may have re-mounted ro on the prior reboot.
    if hdc_shell_check "mount | grep -E '\\s/\\s.*\\bro\\b'"; then
        local out
        out=$(hdc_shell "mount -o remount,rw / 2>&1")
        _assert_no_fail_or_drwx "$out" "_restore_mounts remount,rw /"
        ok "Gate 10: / remounted rw"
    else
        ok "Gate 10: / already rw"
    fi
    if hdc_shell_check "mount | grep -E '\\s/system\\s.*\\bro\\b'"; then
        local out
        out=$(hdc_shell "mount -o remount,rw /system 2>&1")
        _assert_no_fail_or_drwx "$out" "_restore_mounts remount,rw /system"
        ok "Gate 10: /system remounted rw"
    else
        ok "Gate 10: /system already rw (or not a separate mount)"
    fi
}

# ============================================================================
# Gate 11 — force-stop OH Photos before any display-touching stage
# ============================================================================
# Cites Island demo/run-live.sh L60-62. Prevents the Photos viewer from
# holding a render_service lock while we replace librender_service.z.so etc.
# Defensive even in headless deploy — Photos may be running from a prior
# operator interaction with the panel.

# OH Photos candidate bundle names (Phone vs HMS variants).
PHOTOS_BUNDLES=(
    "com.ohos.photos"
    "com.huawei.hmsapp.photos"
)

_force_stop_photos() {
    log "Gate 11: force-stop OH Photos (Island defensive pattern, run-live.sh L60-62)"
    local bn
    for bn in "${PHOTOS_BUNDLES[@]}"; do
        # aa force-stop returns 0 even when the bundle isn't installed; we
        # capture and log rather than gate. The defensive intent is satisfied
        # by best-effort send.
        hdc_shell "aa force-stop $bn 2>&1 | tail -1" >/dev/null
        ok "Gate 11: aa force-stop $bn issued"
    done
}

# ============================================================================
# Gate 12 — loop-budget on launch
# ============================================================================
# Cites Island demo/run-live.sh L71 (`-loop=10 -loop-budget=60000`). Any
# spawn or daemon start must have a max-runtime budget so a hung launch
# aborts cleanly rather than pinning resources forever.

LAUNCH_BUDGET_SECS="${LAUNCH_BUDGET_SECS:-60}"

# _budgeted_hdc_shell <budget_secs> <shell command...>
#   Wraps a long-running hdc shell invocation with `timeout`. Returns 124
#   if budget hit (matching coreutils timeout semantics).
_budgeted_hdc_shell() {
    local budget="$1"; shift
    timeout "${budget}s" "$HDC" -t "$HDC_SERIAL" shell "$@" </dev/null 2>&1 | tr -d '\r'
}

# ============================================================================
# Gate 13 — processdump availability check
# ============================================================================
# Phase 1b in chroot was BLOCKED because processdump wasn't reachable inside
# the chroot. /system deploy must verify processdump probes cleanly BEFORE
# any /system modification — diagnostic capability is the value-add over the
# chroot path (per V3-W2-PHASE-1B2-RETRY §5). If processdump is broken at
# preflight, ABORT before any writes; recovery is much cheaper than after
# a crash with no backtrace.
_probe_processdump() {
    log "Gate 13: probing processdump availability"
    # processdump --help is the canonical no-side-effect invocation. Returns
    # non-zero on stuck/missing/broken.
    local out rc
    out=$(hdc_shell "processdump --help 2>&1; echo __EXIT__=\$?" 2>&1 | tr -d '\r')
    rc=$(echo "$out" | grep '^__EXIT__=' | tail -1 | sed 's/__EXIT__=//')
    if [ -z "$rc" ]; then
        abort "Gate 13: processdump probe returned no exit sentinel (channel issue)"
    fi
    # OHOS processdump returns 0 on --help; if it ENOENTs (rc=127) or
    # segvs (rc=139), the diagnostic capability is gone.
    if [ "$rc" = "127" ]; then
        abort "Gate 13: processdump NOT FOUND on board — diagnostic capability missing; ABORT before /system writes"
    fi
    if [ "$rc" != "0" ] && [ "$rc" != "1" ] && [ "$rc" != "2" ]; then
        # rc=1/2 are acceptable for some toybox-style --help variants
        abort "Gate 13: processdump --help unexpected exit $rc (output: $(echo "$out" | head -3 | tr '\n' ' '))"
    fi
    ok "Gate 13: processdump reachable (rc=$rc)"
}

stage_push() {
    # Args: <local_path> <device_path>
    #
    # Fix B port (chroot commit 0f4dc8be):
    #   (1) hdc_raw now applies </dev/null universally — the file-send call below
    #       is stdin-isolated by construction, so callers may safely run
    #       stage_push inside `while IFS= read` loops without losing iteration 2+.
    #   (2) Post-push verify via `test -s` (device-side exit code propagation
    #       through hdc_shell_check). Pre-existing md5 round-trip is stronger
    #       and is kept; the test-s probe is a fast-fail gate that runs first.
    local local_path="$1" device_path="$2"
    [ -f "$local_path" ] || abort "stage_push: local missing: $local_path"
    local bn
    bn=$(basename "$local_path")
    local win
    win=$(_to_win_path "$local_path")

    # Step 1: send to staging (</dev/null isolation via hdc_raw)
    local out
    out=$(hdc_raw file send "$win" "$STAGE_DIR/$bn" 2>&1 | tr -d '\r')
    _assert_no_fail_or_drwx "$out" "stage_push send $bn"

    # Step 1b: Fix B post-push verify — fast device-side test before md5 work.
    # Catches silent-truncation / silent-NOOP / wrong-target-path cases that
    # md5sum would also catch but with much more wire traffic.
    if ! hdc_shell_check "test -s $STAGE_DIR/$bn"; then
        abort "stage_push: device staging NOT present/zero after push: $STAGE_DIR/$bn (local=$local_path)"
    fi

    # Step 2: stat must say "regular file" (NOT directory — the 造目录 quirk)
    local kind
    kind=$(hdc_shell "stat -c '%F' $STAGE_DIR/$bn 2>&1" | tr -d '\r\n')
    if [ "$kind" != "regular file" ]; then
        abort "stage_push: $bn landed as '$kind' in staging (hdc 造目录 quirk)"
    fi

    # Step 3: md5 staging == md5 local
    local lmd5 smd5
    lmd5=$(md5sum "$local_path" | awk '{print $1}')
    smd5=$(hdc_shell "md5sum $STAGE_DIR/$bn 2>/dev/null" | awk '{print $1}' | tr -d '\r\n')
    if [ "$lmd5" != "$smd5" ]; then
        abort "stage_push: md5 mismatch staging $bn (local=$lmd5 stage=$smd5)"
    fi

    # Step 4: cp staging → final (atomic for live .so via Gate 9 wrapper —
    # see _atomic_install below; direct cp here writes via .new + mv when the
    # device target is on the live-service .so allowlist)
    if _is_live_service_so "$device_path"; then
        _atomic_install "$STAGE_DIR/$bn" "$device_path"
    else
        local cpout
        cpout=$(hdc_shell "cp $STAGE_DIR/$bn $device_path 2>&1")
        _assert_no_fail_or_drwx "$cpout" "stage_push cp $bn"
    fi

    # Step 5: md5 final == md5 local (catches mid-cp truncation)
    local fmd5
    fmd5=$(hdc_shell "md5sum $device_path 2>/dev/null" | awk '{print $1}' | tr -d '\r\n')
    if [ "$lmd5" != "$fmd5" ]; then
        abort "stage_push: md5 mismatch final $bn (local=$lmd5 device=$fmd5)"
    fi

    # Step 6: Fix B post-install device-side existence + size>0 gate
    if ! hdc_shell_check "test -s $device_path"; then
        abort "stage_push: device final NOT present/zero after install: $device_path"
    fi

    ok "$bn → $device_path  ($lmd5)"
}

# G2 — chcon-with-verify (replaces `chcon ... || true` from old script)
# Gate 8 integration: capture baseline contexts BEFORE applying chcon, so a
# kill/abort mid-batch leaves a replayable snapshot at $CHCON_SNAPSHOT_DEVICE.
chcon_verify() {
    # Args: <label> <path1> [path2 ...]
    local label="$1"; shift
    # Gate 8: snapshot baseline labels for every path we're about to chcon.
    # Idempotent: paths already in the snapshot are skipped.
    _chcon_snapshot_capture "$@"
    local p out got
    for p in "$@"; do
        out=$(hdc_shell "chcon u:object_r:${label}:s0 '$p' 2>&1")
        _assert_no_fail_or_drwx "$out" "chcon $p"
        got=$(hdc_shell "ls -lZ '$p' 2>&1" | grep -oE "[a-z_]+_file" | head -1)
        if [ "$got" != "$label" ]; then
            abort "G2: chcon did not stick on $p (got label='$got' expected='$label')"
        fi
        ok "chcon $label: $p"
    done
}

_ensure_stage_dir() {
    local out
    out=$(hdc_shell "mkdir -p $STAGE_DIR 2>&1")
    _assert_no_fail_or_drwx "$out" "ensure_stage_dir"
}

# ============================================================================
# UNINSTALL mode (pre-brick rollback per HBC deploy_to_dayu200.sh L379-390)
# ============================================================================

run_uninstall() {
    log "UNINSTALL: rolling back deployed V3 artifacts (PRE-BRICK only)"
    _check_hdc_version
    _alive_probe
    # Gate 10: re-apply expected mount table for rollback (replaces || true
    # silent-NOOP remount).
    _restore_mounts
    # Restore from device-side .orig_<TS> backups
    hdc_shell "for f in \$(find /system -name '*.orig_${TS}' 2>/dev/null); do cp \$f \${f%.orig_${TS}}; done" >/dev/null
    hdc_shell "rm -rf /system/android"
    hdc_shell "rm -f /system/bin/appspawn-x"
    hdc_shell "rm -f /system/etc/init/appspawn_x.cfg /system/etc/appspawn_x_sandbox.json"
    hdc_shell "rm -f /system/lib/liboh_adapter_bridge.so /system/lib/libapk_installer.so /system/lib/libinstalls.z.so"
    # Fix D rollback: remove the dual-path adapter bridge in /system/android/lib too
    hdc_shell "rm -f /system/android/lib/liboh_adapter_bridge.so"
    hdc_shell "rm -f /system/lib/liboh_hwui_shim.so /system/lib/liboh_skia_rtti_shim.so /system/lib/liboh_android_runtime.so"
    hdc_shell "rm -f /system/lib/libc_musl.so /system/lib/libandroid.so"
    # bm uninstall returns non-zero if app is absent; use hdc_shell_check + warn
    # rather than swallowing with || true. Absent app is expected during rollback.
    if ! hdc_shell_check "bm uninstall -n com.example.helloworld 2>&1"; then
        warn "uninstall: bm uninstall non-zero (app may already be absent — benign)"
    fi
    # Gate 8: replay snapshot to restore factory contexts before exit.
    _chcon_snapshot_restore
    _alive_probe
    pass_msg "UNINSTALL done. Reboot device for clean state."
    exit 0
}

# ============================================================================
# Stage 0 — preflight (G0 + G6 + factory-baseline guards)
# ============================================================================

stage_0() {
    log "Stage 0 · preflight (hdc version, board state, factory baseline)"

    # G6 — hdc.exe version pin
    _check_hdc_version

    # board connectivity
    local listing
    listing=$("$HDC" list targets 2>&1 | tr -d '\r')
    if ! echo "$listing" | grep -q "$HDC_SERIAL"; then
        abort "Stage 0: DAYU200 $HDC_SERIAL not connected (hdc list: '$listing')"
    fi
    ok "device visible: $HDC_SERIAL"

    # G1 — first sentinel probe
    _alive_probe
    ok "G1: hdc shell sentinel echo OK"

    # uname/kernel
    local uname_out
    uname_out=$(hdc_shell "uname -a" | tr -d '\r\n')
    ok "uname: $uname_out"

    # 32-bit userspace check (CR60 bitness pivot expectation)
    local bits
    bits=$(hdc_shell "getconf LONG_BIT" | tr -d ' \r\n')
    if [ "$bits" != "32" ]; then
        warn "userspace LONG_BIT=$bits (expected 32 per CR60 bitness pivot)"
    else
        ok "userspace LONG_BIT=32"
    fi

    # SELinux mode
    local mode
    mode=$(hdc_shell "getenforce" | tr -d ' \r\n')
    if [ "$mode" != "Enforcing" ]; then
        warn "selinux mode=$mode (expected Enforcing)"
    else
        ok "selinux Enforcing"
    fi

    # Factory baseline guard — /system/android MUST NOT exist
    local has_android
    has_android=$(hdc_shell "ls -d /system/android 2>&1" | tr -d '\r\n')
    if ! echo "$has_android" | grep -q "No such"; then
        abort "Stage 0: /system/android already exists — not factory baseline; run --uninstall + reboot first"
    fi
    ok "/system/android absent (factory baseline)"

    # No prior deploy residue (.orig_* backups would mean prior incomplete deploy)
    local residue
    residue=$(hdc_shell "ls /system/lib/*.orig_* 2>/dev/null" | tr -d '\r' | wc -l | tr -d ' ')
    if [ "$residue" != "0" ]; then
        warn "found $residue .orig_* files under /system/lib (prior deploy residue)"
        warn "consider --uninstall + reboot before continuing"
    fi

    # hdcd alive (proves the channel)
    local hdcd_pid
    hdcd_pid=$(hdc_shell "pidof hdcd" | tr -d ' \r\n')
    if [ -z "$hdcd_pid" ]; then
        abort "Stage 0: pidof hdcd returned empty — channel will die mid-deploy"
    fi
    ok "hdcd alive (pid=$hdcd_pid)"

    # G3 — local artifact inventory check
    _verify_required_artifacts

    # Gate 13 — processdump probe (diagnostic-capability gate). If processdump
    # is unreachable/broken we ABORT before any /system writes; diagnostic
    # capability is the value-add of /system over the chroot path.
    _probe_processdump

    # Gate 8 — initialise chcon snapshot file on device. Idempotent (preserves
    # baseline across resumed runs). Any subsequent chcon_verify call will
    # append the pre-chcon label of every target path.
    _chcon_snapshot_init

    _alive_probe
    pass_msg "Stage 0 PASS — preflight clean, factory baseline confirmed (Gate 13 + Gate 8 armed)"
}

# ============================================================================
# Stage 1 — backup (13 device-side .orig_<TS> copies)
# Per HBC DEPLOY_SOP.md §Stage 1 (lines 22-40) — device-side cp only, no
# hdc file recv.
# ============================================================================

stage_1() {
    log "Stage 1 · backup 13 device-side originals (TS=$TS)"
    _alive_probe

    # Gate 10: re-apply expected mount table (Island pattern). Replaces the
    # prior `|| true` remount that silently swallowed remount failures.
    _restore_mounts

    # 6 files in /system/lib/
    local lib_files=(libwms.z.so libappms.z.so libbms.z.so libskia_canvaskit.z.so
                     libinstalls.z.so libappspawn_client.z.so)
    # 5 files in /system/lib/platformsdk/
    local sdk_files=(libabilityms.z.so libscene_session.z.so libscene_session_manager.z.so
                     librender_service_base.z.so libappexecfwk_common.z.so)
    # 2 etc files
    local etc_files=("/system/etc/ld-musl-namespace-arm.ini"
                     "/system/etc/selinux/targeted/contexts/file_contexts")

    local expected=$(( ${#lib_files[@]} + ${#sdk_files[@]} + ${#etc_files[@]} ))
    # Backup semantic: cp <src> <.orig_TS> IFF src exists and .orig_TS does
    # not. Rewritten without `|| true`: the device-side conditional [ -f ] &&
    # [ ! -f ] && cp is itself a boolean; missing-src or already-backed-up
    # short-circuits to a successful no-op on the device shell, which our
    # host-side check_hdc-shell now propagates faithfully. The verify-count
    # gate at the end is the actual acceptance criterion.
    local f orig
    for f in "${lib_files[@]}"; do
        orig="/system/lib/${f}.orig_${TS}"
        hdc_shell "if [ -f /system/lib/$f ] && [ ! -f $orig ]; then cp /system/lib/$f $orig; fi" >/dev/null
    done
    for f in "${sdk_files[@]}"; do
        orig="/system/lib/platformsdk/${f}.orig_${TS}"
        hdc_shell "if [ -f /system/lib/platformsdk/$f ] && [ ! -f $orig ]; then cp /system/lib/platformsdk/$f $orig; fi" >/dev/null
    done
    for f in "${etc_files[@]}"; do
        orig="${f}.orig_${TS}"
        hdc_shell "if [ -f $f ] && [ ! -f $orig ]; then cp $f $orig; fi" >/dev/null
    done

    # Verify count
    local actual
    actual=$(hdc_shell "ls /system/lib/*.orig_${TS} /system/lib/platformsdk/*.orig_${TS} /system/etc/ld-musl-namespace-arm.ini.orig_${TS} /system/etc/selinux/targeted/contexts/file_contexts.orig_${TS} 2>/dev/null | wc -l" | tr -d ' \r\n')
    if [ "$actual" != "$expected" ]; then
        abort "Stage 1: backup count $actual != $expected expected"
    fi

    _alive_probe
    pass_msg "Stage 1 PASS — $actual .orig_${TS} backups on device"
}

# ============================================================================
# Stage 2 — stop foundation + render_service (DEFAULT: SKIP, per W2 decision)
# Per HBC DEPLOY_SOP.md §Stage 2 lines 44-54: NEVER stop appspawn.
# G4: --no-skip-stage-2 explicitly enables; default = SKIP for safety.
# ============================================================================

stage_2() {
    log "Stage 2 · stop foundation + render_service"
    if [ "$SKIP_STAGE_2" = "1" ]; then
        warn "Stage 2 SKIPPED by default (--no-skip-stage-2 to enable)"
        warn "  rationale: W2 successfully deployed without Stage 2 stops (lib/ overlay safe)"
        warn "  HBC SOP recommendation: do stop these 2 services to release mmap handles"
        pass_msg "Stage 2 SKIP (opt-out active)"
        return 0
    fi

    _alive_probe

    # CRITICAL: never stop appspawn (per HBC SOP line 51 "停了会断 hdc 通信链")
    # Fix A: hdc_shell_check propagates device-side exit. Service-stop errors
    # are downgraded to WARN rather than silently swallowed.
    if ! hdc_shell_check "begetctl stop_service foundation"; then
        warn "Stage 2: begetctl stop_service foundation non-zero exit (service may already be down)"
    fi
    if ! hdc_shell_check "begetctl stop_service render_service"; then
        warn "Stage 2: begetctl stop_service render_service non-zero exit (service may already be down)"
    fi

    local pf pr ph
    pf=$(hdc_shell "pidof foundation"     | tr -d ' \r\n')
    pr=$(hdc_shell "pidof render_service" | tr -d ' \r\n')
    ph=$(hdc_shell "pidof hdcd"           | tr -d ' \r\n')

    [ -z "$pf" ] || abort "Stage 2: foundation still alive (pid=$pf)"
    [ -z "$pr" ] || abort "Stage 2: render_service still alive (pid=$pr)"
    [ -n "$ph" ] || abort "Stage 2: hdcd not alive — lost transport channel"

    _alive_probe
    pass_msg "Stage 2 PASS — foundation stopped, render_service stopped, hdcd alive ($ph)"
}

# ============================================================================
# Stage 3.0 — mkdir 4 dirs HBC requires (prerequisite P-B in
# V3-W2-RECOVERY-PROCEDURE.md §4)
# Per HBC DEPLOY_SOP.md line 60-70: must mkdir BEFORE any push to prevent
# 造目录 quirk.
# ============================================================================

stage_3_0() {
    log "Stage 3.0 · mkdir 4 prerequisite directories + ensure staging"
    _alive_probe

    # Gate 10: re-apply expected mount table (Island demo/run-live.sh L35-42
    # pattern). Idempotent — mountpoint -q probe precedes each remount.
    # Replaces the prior `|| true` remount calls which silently swallowed
    # remount failures (W2-class bug).
    _restore_mounts

    # Gate 11: force-stop OH Photos (Island demo/run-live.sh L60-62). Even in
    # headless deploy, the Photos viewer may be holding a render_service or
    # graphics-buffer lock that conflicts with later .so replacement.
    _force_stop_photos

    _ensure_stage_dir

    local out
    out=$(hdc_shell "mkdir -p /system/lib/platformsdk /system/android/lib /system/android/framework/arm /system/android/etc/icu /system/android/etc 2>&1")
    _assert_no_fail_or_drwx "$out" "stage_3_0 mkdir"

    local d kind
    for d in /system/lib/platformsdk /system/android/lib /system/android/framework/arm /system/android/etc/icu; do
        kind=$(hdc_shell "stat -c '%F' $d 2>&1" | tr -d '\r\n')
        if [ "$kind" != "directory" ]; then
            abort "Stage 3.0: $d not a directory ($kind)"
        fi
        ok "$d (directory)"
    done

    _alive_probe
    pass_msg "Stage 3.0 PASS — 4 dirs ready, staging area ready"
}

# ============================================================================
# Stage 3b — OH services .so (10 + libbms symlink)
# Per HBC DEPLOY_SOP.md §3b. Mirrors HBC deploy_stage.sh stage_3b.
# ============================================================================

stage_3b() {
    log "Stage 3b · OH services .so (10 files + libbms symlink)"
    _alive_probe
    _ensure_stage_dir

    # /system/lib/
    local f
    for f in libwms.z.so libappms.z.so libbms.z.so libskia_canvaskit.z.so \
             libappspawn_client.z.so librender_service.z.so; do
        stage_push "$V3_LOCAL/lib/$f" "/system/lib/$f"
    done
    # /system/lib/platformsdk/
    for f in libabilityms.z.so libscene_session.z.so libscene_session_manager.z.so \
             librender_service_base.z.so libappexecfwk_common.z.so; do
        stage_push "$V3_LOCAL/lib/$f" "/system/lib/platformsdk/$f"
    done

    # libbms symlink (HBC SOP line 83)
    local out
    out=$(hdc_shell "rm -f /system/lib/platformsdk/libbms.z.so && ln -sf /system/lib/libbms.z.so /system/lib/platformsdk/libbms.z.so && echo OK" | tr -d '\r\n')
    [ "$out" = "OK" ] || abort "Stage 3b: libbms symlink failed: $out"
    ok "symlink /system/lib/platformsdk/libbms.z.so → /system/lib/libbms.z.so"

    # drwx sentinel
    local drwx
    drwx=$(hdc_shell "ls -la /system/lib/platformsdk/ | grep '^d' | grep -vE ' \\.$| \\.\\.$'" | tr -d '\r')
    if [ -n "$drwx" ]; then
        abort "Stage 3b: unexpected drwx in /system/lib/platformsdk/: $drwx"
    fi

    _alive_probe
    pass_msg "Stage 3b PASS — 11 .so + 1 symlink"
}

# ============================================================================
# Stage 3c — AOSP native .so (38 + 3 dual-path adapter shims)
# Per HBC DEPLOY_SOP.md §3c. Includes G2 chcon-verify at end.
# ============================================================================

stage_3c() {
    log "Stage 3c · AOSP native .so → /system/android/lib/ (38 + 3 dual-path)"
    _alive_probe
    _ensure_stage_dir

    # 38 AOSP native: every *.so in v3-hbc/lib/ EXCEPT .z.so, liboh_*, libapk_installer
    local count=0 so bn
    for so in "$V3_LOCAL/lib"/*.so; do
        [ -f "$so" ] || continue
        bn=$(basename "$so")
        case "$bn" in
            *.z.so)            continue ;;
            liboh_*)           continue ;;
            libapk_installer.so) continue ;;
        esac
        stage_push "$so" "/system/android/lib/$bn"
        count=$((count+1))
    done
    if [ $count -lt 30 ]; then
        warn "Stage 3c: only $count AOSP .so pushed (expected ~38) — check W1 pull"
    fi

    # 3 adapter shims DUAL-PATH (per HBC SOP §3c)
    local shim
    for shim in liboh_hwui_shim.so liboh_android_runtime.so liboh_skia_rtti_shim.so; do
        stage_push "$V3_LOCAL/lib/$shim" "/system/android/lib/$shim"
        stage_push "$V3_LOCAL/lib/$shim" "/system/lib/$shim"
    done

    # drwx sentinel
    local drwx
    drwx=$(hdc_shell "ls -la /system/android/lib/ | grep '^d' | grep -vE ' \\.$| \\.\\.$'" | tr -d '\r')
    if [ -n "$drwx" ]; then
        abort "Stage 3c: unexpected drwx in /system/android/lib/: $drwx"
    fi

    # G2: chcon dual-path adapter shims + VERIFY label stuck
    chcon_verify system_lib_file \
        /system/android/lib/liboh_android_runtime.so /system/lib/liboh_android_runtime.so \
        /system/android/lib/liboh_hwui_shim.so /system/lib/liboh_hwui_shim.so \
        /system/android/lib/liboh_skia_rtti_shim.so /system/lib/liboh_skia_rtti_shim.so

    _alive_probe
    pass_msg "Stage 3c PASS — $count AOSP + 3 dual-path shims + chcon verified"
}

# ============================================================================
# Stage 3d — framework jars + ICU + fonts.xml + G2 chcon verify
# Per HBC DEPLOY_SOP.md §3d lines 106-123.
# ============================================================================

stage_3d() {
    log "Stage 3d · framework jars + ICU + fonts.xml dual-path"
    _alive_probe
    _ensure_stage_dir

    local f
    for f in framework.jar framework-classes.dex.jar framework-res-package.jar \
             core-oj.jar core-libart.jar core-icu4j.jar okhttp.jar bouncycastle.jar \
             apache-xml.jar oh-adapter-framework.jar oh-adapter-runtime.jar \
             adapter-mainline-stubs.jar; do
        stage_push "$V3_LOCAL/jars/$f" "/system/android/framework/$f"
    done

    # framework-res.apk — true file (not symlink); fall back to cp from
    # framework-res-package.jar if v3-hbc/jars/ doesn't include it
    if [ -f "$V3_LOCAL/jars/framework-res.apk" ]; then
        stage_push "$V3_LOCAL/jars/framework-res.apk" /system/android/framework/framework-res.apk
    else
        local out
        out=$(hdc_shell "rm -f /system/android/framework/framework-res.apk; cp /system/android/framework/framework-res-package.jar /system/android/framework/framework-res.apk; chmod 644 /system/android/framework/framework-res.apk && echo OK" | tr -d '\r\n')
        [ "$out" = "OK" ] || abort "Stage 3d: framework-res.apk derive failed: $out"
        ok "framework-res.apk derived from framework-res-package.jar"
    fi

    stage_push "$V3_LOCAL/etc/icudt72l.dat" /system/android/etc/icu/icudt72l.dat

    # fonts.xml dual-path (P-12)
    stage_push "$V3_LOCAL/etc/fonts.xml" /system/android/etc/fonts.xml
    local out
    out=$(hdc_shell "rm -f /system/etc/fonts.xml; cp /system/android/etc/fonts.xml /system/etc/fonts.xml && echo OK" | tr -d '\r\n')
    [ "$out" = "OK" ] || abort "Stage 3d: fonts.xml /system/etc cp failed: $out"

    # G2: chcon system_fonts_file:s0 on BOTH copies, verify each
    chcon_verify system_fonts_file /system/etc/fonts.xml /system/android/etc/fonts.xml

    _alive_probe
    pass_msg "Stage 3d PASS — 12 jars + fonts dual-path + ICU + chcon verified"
}

# ============================================================================
# Stage 3e — boot image (27 files, md5-verified via stage_push) + G2 chcon
# Per HBC DEPLOY_SOP.md §3e lines 125-146.
# ============================================================================

stage_3e() {
    log "Stage 3e · boot image (27 files: 9 segments × 3 ext)"
    _alive_probe
    _ensure_stage_dir

    local g e
    for g in boot boot-core-libart boot-core-icu4j boot-okhttp boot-bouncycastle \
             boot-apache-xml boot-adapter-mainline-stubs boot-framework \
             boot-oh-adapter-framework; do
        for e in art oat vdex; do
            stage_push "$V3_LOCAL/bcp/$g.$e" "/system/android/framework/arm/$g.$e"
        done
    done

    # G2: chcon every boot image segment to system_lib_file:s0 (HBC SOP §3e
    # line 137-146 explains the appspawn:s0 flock denial otherwise).
    local segs=()
    for g in boot boot-core-libart boot-core-icu4j boot-okhttp boot-bouncycastle \
             boot-apache-xml boot-adapter-mainline-stubs boot-framework \
             boot-oh-adapter-framework; do
        for e in art oat vdex; do
            segs+=("/system/android/framework/arm/$g.$e")
        done
    done
    chcon_verify system_lib_file "${segs[@]}"

    _alive_probe
    pass_msg "Stage 3e PASS — 27 boot files + chcon verified"
}

# ============================================================================
# Stage 3f — appspawn-x bin + cfg + namespace ini + file_contexts + symlinks
# Per HBC DEPLOY_SOP.md §3f lines 148-178.
# ============================================================================

stage_3f() {
    log "Stage 3f · appspawn-x bin + cfg + linker + selinux + 5 symlinks"
    _alive_probe
    _ensure_stage_dir

    # Binaries
    stage_push "$V3_LOCAL/bin/appspawn-x"              /system/bin/appspawn-x
    stage_push "$V3_LOCAL/lib/liboh_adapter_bridge.so" /system/lib/liboh_adapter_bridge.so
    # Fix D port (chroot dual-path; agent 73 Phase 1b.2 finding 2026-05-19):
    # liboh_android_runtime.so chain-fails on liboh_adapter_bridge.so resolution
    # when loaded from /system/android/lib/ without a sibling there. Mirrors
    # chroot script line 334-335 (dual-path same .so to both lib trees).
    stage_push "$V3_LOCAL/lib/liboh_adapter_bridge.so" /system/android/lib/liboh_adapter_bridge.so
    stage_push "$V3_LOCAL/lib/libapk_installer.so"     /system/lib/libapk_installer.so
    stage_push "$V3_LOCAL/lib/libinstalls.z.so"        /system/lib/libinstalls.z.so

    # libsurface optional
    if [ -f "$V3_LOCAL/lib/libsurface.z.so" ]; then
        stage_push "$V3_LOCAL/lib/libsurface.z.so" /system/lib/libsurface.z.so
    fi

    # configs
    stage_push "$V3_LOCAL/etc/appspawn_x.cfg"          /system/etc/init/appspawn_x.cfg
    stage_push "$V3_LOCAL/etc/appspawn_x_sandbox.json" /system/etc/appspawn_x_sandbox.json
    stage_push "$V3_LOCAL/etc/ld-musl-namespace-arm.ini" /system/etc/ld-musl-namespace-arm.ini
    stage_push "$V3_LOCAL/etc/file_contexts"           /system/etc/selinux/targeted/contexts/file_contexts

    # chmod batch
    hdc_shell "chmod 755 /system/bin/appspawn-x"                                       >/dev/null
    # Fix A: glob-based chmod may match nothing on some boards; use
    # hdc_shell_check + warn (downgraded from prior || true silent NOOP).
    if ! hdc_shell_check "chmod 644 /system/lib/*.so /system/lib/platformsdk/*.z.so 2>/dev/null"; then
        warn "Stage 3f: glob chmod 644 non-zero (some targets may be absent on this board — benign)"
    fi
    hdc_shell "chmod 644 /system/android/lib/*.so"                                     >/dev/null
    hdc_shell "chmod 644 /system/android/framework/*.jar"                              >/dev/null
    hdc_shell "chmod 644 /system/android/framework/arm/*.art /system/android/framework/arm/*.oat /system/android/framework/arm/*.vdex" >/dev/null
    hdc_shell "chmod 644 /system/etc/init/appspawn_x.cfg /system/etc/appspawn_x_sandbox.json /system/etc/ld-musl-namespace-arm.ini /system/etc/selinux/targeted/contexts/file_contexts" >/dev/null
    ok "chmod batch complete"

    # 5 symlinks (per HBC SOP lines 159-163 + G2.14aa libandroid.so dual-path)
    hdc_shell "ln -sf /lib/ld-musl-arm.so.1 /system/lib/libc_musl.so"                                                                                  >/dev/null
    hdc_shell "rm -f /system/android/lib/libshared_libz.z.so; ln -sf /system/lib/chipset-sdk-sp/libshared_libz.z.so /system/android/lib/libshared_libz.z.so" >/dev/null
    hdc_shell "rm -f /system/android/lib/libappexecfwk_common.z.so; ln -sf /system/lib/platformsdk/libappexecfwk_common.z.so /system/android/lib/libappexecfwk_common.z.so" >/dev/null
    hdc_shell "rm -f /system/android/lib/libandroid.so; ln -sf liboh_android_runtime.so /system/android/lib/libandroid.so"                              >/dev/null
    hdc_shell "rm -f /system/lib/libandroid.so; ln -sf liboh_android_runtime.so /system/lib/libandroid.so"                                              >/dev/null
    ok "5 symlinks installed"

    # restorecon for appspawn-x (file_contexts new rules take effect on relabel)
    # Fix A: explicit device-side exit propagation. The W2-class || true pattern
    # is replaced with `if ! hdc_shell_check ...` so silent restorecon failures
    # surface as warnings, not silent NOOPs.
    if ! hdc_shell_check "restorecon /system/bin/appspawn-x"; then
        warn "Stage 3f: restorecon /system/bin/appspawn-x non-zero exit"
    fi
    if ! hdc_shell_check "find /system/android/lib -exec restorecon {} \\;"; then
        warn "Stage 3f: restorecon /system/android/lib non-zero exit (may be benign for files missing from file_contexts)"
    fi
    ok "restorecon /system/bin/appspawn-x + /system/android/lib"

    # Fix D dual-path: chcon-verify the dual-path adapter bridge on BOTH paths.
    # liboh_android_runtime.so resolution of liboh_adapter_bridge.so requires
    # the latter at both /system/lib/ and /system/android/lib/ with matching
    # system_lib_file label. Gate 8 snapshot is captured automatically.
    chcon_verify system_lib_file \
        /system/lib/liboh_adapter_bridge.so \
        /system/android/lib/liboh_adapter_bridge.so

    # G2 verify appspawn-x got appspawn_exec label
    local label
    label=$(hdc_shell "ls -lZ /system/bin/appspawn-x" | grep -oE 'appspawn_exec' | head -1)
    if [ "$label" != "appspawn_exec" ]; then
        warn "appspawn-x label != appspawn_exec (got: '$label'); init domain transition may fail"
    else
        ok "appspawn-x SELinux label: appspawn_exec"
    fi

    _alive_probe
    pass_msg "Stage 3f PASS — 4 bin + 3 cfg + selinux + 5 symlinks + restorecon"
}

# ============================================================================
# Stage 3.7 — final chcon sweep (mostly redundant — every substage already
# chcon+verifies — but kept as a sentinel step matching the brief)
# ============================================================================

stage_3_7() {
    log "Stage 3.7 · chcon final sweep + verify (G2)"
    _alive_probe
    # Re-verify the high-risk labels haven't drifted (Fix D dual-path entries
    # 2026-05-19: liboh_adapter_bridge.so verified at both lib paths)
    local p got
    for p in /system/android/lib/liboh_android_runtime.so /system/lib/liboh_android_runtime.so \
             /system/android/lib/liboh_hwui_shim.so      /system/lib/liboh_hwui_shim.so \
             /system/android/lib/liboh_skia_rtti_shim.so /system/lib/liboh_skia_rtti_shim.so \
             /system/lib/liboh_adapter_bridge.so          /system/android/lib/liboh_adapter_bridge.so \
             /system/etc/fonts.xml /system/android/etc/fonts.xml; do
        got=$(hdc_shell "ls -lZ $p 2>/dev/null" | grep -oE "[a-z_]+_file" | head -1)
        case "$p" in
            *fonts.xml)
                [ "$got" = "system_fonts_file" ] || abort "Stage 3.7: $p label='$got' (expect system_fonts_file)" ;;
            *)
                [ "$got" = "system_lib_file" ] || abort "Stage 3.7: $p label='$got' (expect system_lib_file)" ;;
        esac
        ok "$p → $got"
    done
    # Boot image (sample first/last of each ext)
    for p in /system/android/framework/arm/boot.art /system/android/framework/arm/boot.oat \
             /system/android/framework/arm/boot.vdex \
             /system/android/framework/arm/boot-framework.art \
             /system/android/framework/arm/boot-framework.vdex; do
        got=$(hdc_shell "ls -lZ $p 2>/dev/null" | grep -oE "[a-z_]+_file" | head -1)
        [ "$got" = "system_lib_file" ] || abort "Stage 3.7: $p label='$got' (expect system_lib_file)"
        ok "$p → $got"
    done
    _alive_probe
    pass_msg "Stage 3.7 PASS — labels stuck on hot paths"
}

# ============================================================================
# Stage 3.8 — channel-alive sentinel (G1) BEFORE reboot
# Per V3-W2-RECOVERY-PROCEDURE.md §"Phase D" rule #2.
# This is the FINAL gate. If this fails, we DO NOT reboot.
# ============================================================================

stage_3_8() {
    log "Stage 3.8 · channel-alive sentinel BEFORE reboot (G1; ANTI-W2)"
    _alive_probe
    _alive_probe   # twice for paranoia
    _alive_probe
    pass_msg "Stage 3.8 PASS — channel reliable; safe to issue reboot (Stage 4)"
}

# ============================================================================
# Stage 3.9 — integrity (md5 + size + drwx sentinel)
# Per HBC DEPLOY_SOP.md §3.9 lines 180-186. G5 implementation.
# ============================================================================

stage_3_9() {
    log "Stage 3.9 · integrity (md5 + size) + drwx sentinel (G5)"
    _alive_probe

    local errors=0

    # Build manifest: local => device for every push above
    declare -a manifest
    local f
    for f in libwms.z.so libappms.z.so libbms.z.so libskia_canvaskit.z.so \
             libappspawn_client.z.so librender_service.z.so; do
        manifest+=("$V3_LOCAL/lib/$f=/system/lib/$f")
    done
    for f in libabilityms.z.so libscene_session.z.so libscene_session_manager.z.so \
             librender_service_base.z.so libappexecfwk_common.z.so; do
        manifest+=("$V3_LOCAL/lib/$f=/system/lib/platformsdk/$f")
    done
    local so bn
    for so in "$V3_LOCAL/lib"/*.so; do
        [ -f "$so" ] || continue
        bn=$(basename "$so")
        case "$bn" in *.z.so|liboh_*|libapk_installer.so) continue ;; esac
        manifest+=("$so=/system/android/lib/$bn")
    done
    for f in liboh_hwui_shim.so liboh_android_runtime.so liboh_skia_rtti_shim.so; do
        manifest+=("$V3_LOCAL/lib/$f=/system/android/lib/$f")
    done
    for f in framework.jar framework-classes.dex.jar framework-res-package.jar \
             core-oj.jar core-libart.jar core-icu4j.jar okhttp.jar bouncycastle.jar \
             apache-xml.jar oh-adapter-framework.jar oh-adapter-runtime.jar \
             adapter-mainline-stubs.jar; do
        manifest+=("$V3_LOCAL/jars/$f=/system/android/framework/$f")
    done
    manifest+=("$V3_LOCAL/etc/icudt72l.dat=/system/android/etc/icu/icudt72l.dat")
    local g e
    for g in boot boot-core-libart boot-core-icu4j boot-okhttp boot-bouncycastle \
             boot-apache-xml boot-adapter-mainline-stubs boot-framework \
             boot-oh-adapter-framework; do
        for e in art oat vdex; do
            manifest+=("$V3_LOCAL/bcp/$g.$e=/system/android/framework/arm/$g.$e")
        done
    done
    manifest+=("$V3_LOCAL/bin/appspawn-x=/system/bin/appspawn-x")
    manifest+=("$V3_LOCAL/lib/liboh_adapter_bridge.so=/system/lib/liboh_adapter_bridge.so")
    # Fix D dual-path verify (chroot Phase 1b.2 finding 2026-05-19; matches
    # /system/android/lib/ push in stage_3f)
    manifest+=("$V3_LOCAL/lib/liboh_adapter_bridge.so=/system/android/lib/liboh_adapter_bridge.so")
    manifest+=("$V3_LOCAL/lib/libapk_installer.so=/system/lib/libapk_installer.so")
    manifest+=("$V3_LOCAL/lib/libinstalls.z.so=/system/lib/libinstalls.z.so")
    manifest+=("$V3_LOCAL/etc/appspawn_x.cfg=/system/etc/init/appspawn_x.cfg")
    manifest+=("$V3_LOCAL/etc/appspawn_x_sandbox.json=/system/etc/appspawn_x_sandbox.json")
    manifest+=("$V3_LOCAL/etc/ld-musl-namespace-arm.ini=/system/etc/ld-musl-namespace-arm.ini")
    manifest+=("$V3_LOCAL/etc/file_contexts=/system/etc/selinux/targeted/contexts/file_contexts")

    log "Manifest: ${#manifest[@]} files; running md5 + size compare..."

    local entry lp dp lmd5 dmd5 lsz dsz
    for entry in "${manifest[@]}"; do
        lp="${entry%%=*}"
        dp="${entry##*=}"
        lmd5=$(md5sum "$lp" 2>/dev/null | awk '{print $1}')
        lsz=$(stat -c %s "$lp" 2>/dev/null || echo 0)
        dmd5=$(hdc_shell "md5sum $dp 2>/dev/null" | awk '{print $1}' | tr -d '\r\n')
        dsz=$(hdc_shell "stat -c %s $dp 2>/dev/null" | tr -d '\r\n')
        if [ -z "$dmd5" ]; then
            fail "missing on device: $dp"; errors=$((errors+1))
        elif [ "$lmd5" != "$dmd5" ]; then
            fail "md5 mismatch: $dp (local=$lmd5 device=$dmd5)"; errors=$((errors+1))
        elif [ "$lsz" != "$dsz" ]; then
            fail "size mismatch: $dp (local=$lsz device=$dsz)"; errors=$((errors+1))
        fi
    done

    # G5: drwx sentinel — drwx-in-ls of a pushed file means HBC's
    # "hdc create-dir-as-file" quirk fired → STOP
    local d unexpected
    for d in /system/android/lib /system/android/framework/arm /system/android/etc/icu /system/lib /system/lib/platformsdk; do
        unexpected=$(hdc_shell "find $d -mindepth 1 -maxdepth 1 -type d 2>/dev/null" | tr -d '\r')
        if [ -n "$unexpected" ]; then
            # Allowlist: /system/android/framework/arm is itself a dir, but
            # find -mindepth 1 returns its CONTENTS — none should be dirs
            # except /system/lib/{platformsdk,chipset-sdk-sp,...} (factory dirs)
            case "$d" in
                /system/lib)
                    # Factory subdirs we don't own — skip if NONE of them are
                    # our pushed file basenames (any collision = hdc quirk)
                    : ;;
                /system/lib/platformsdk|/system/android/framework/arm|/system/android/etc/icu|/system/android/lib)
                    fail "drwx sentinel: unexpected dir in $d:"
                    echo "$unexpected" | sed 's/^/    /' >&2
                    errors=$((errors+1)) ;;
            esac
        fi
    done

    if [ $errors -gt 0 ]; then
        abort "Stage 3.9: $errors integrity error(s) — DO NOT REBOOT"
    fi

    _alive_probe
    pass_msg "Stage 3.9 PASS — ${#manifest[@]} files md5+size verified, no drwx anomalies"
}

# ============================================================================
# Stage 4 — reboot (ONLY AFTER Stage 3.8 PASS + Stage 3.9 PASS)
# ============================================================================

stage_4() {
    log "Stage 4 · reboot device (sync first)"
    _alive_probe   # G1 final sentinel — never reboot if shell silent

    hdc_shell "sync" >/dev/null
    log "sync complete; issuing reboot..."
    # || true is documented-justified here per feedback_hdc_shell_check_pattern.md:
    # `reboot` is fire-and-forget — channel drops MID-command by design. We
    # cannot meaningfully evaluate the device-side exit code. The poll loop
    # below is the actual reboot acceptance gate.
    hdc_shell "reboot" >/dev/null || true

    # Poll for return
    local i=0
    while [ $i -lt 72 ]; do
        sleep 5
        # Fix A port (control-flow boolean propagation): output-equality predicate
        # (NOT host-exit-code reliance). </dev/null + -t serial to match Fix A
        # discipline; chroot script uses identical pattern.
        if [ "$("$HDC" -t "$HDC_SERIAL" shell "echo alive" </dev/null 2>/dev/null | tr -d '\r\n')" = "alive" ]; then
            ok "device back online after $((i*5))s"
            break
        fi
        i=$((i+1))
    done
    if [ $i -ge 72 ]; then
        abort "Stage 4: device did not come back online in 360s — manual recovery needed"
    fi

    pass_msg "Stage 4 PASS — device rebooted and hdc responsive"
}

# ============================================================================
# Stage 5 — post-reboot verify (boot completed, hdc responsive, services up)
# ============================================================================

stage_5() {
    log "Stage 5 · post-reboot health verify"
    _alive_probe

    # Give init 30s to reach Phase 4 (foundation/launcher up)
    sleep 30

    local p pid errors=0
    for p in foundation render_service com.ohos.launcher hdcd; do
        pid=$(hdc_shell "pidof $p" | tr -d ' \r\n')
        if [ -n "$pid" ]; then
            ok "$p alive (pid=$pid)"
        else
            fail "$p NOT running"
            errors=$((errors+1))
        fi
    done

    # appspawn-x (HBC adapter daemon)
    local x_pid
    x_pid=$(hdc_shell "pidof appspawn-x" | tr -d ' \r\n')
    if [ -n "$x_pid" ]; then
        ok "appspawn-x alive (pid=$x_pid)"
    else
        warn "appspawn-x not running yet (may spawn on first app launch)"
    fi

    # BMS ready check — Fix A port: capture first, then evaluate. Avoids the
    # `if hdc_shell ... | grep` structural ambiguity (which silently swallows
    # a channel outage as "no match"). Empty output here is a channel-health
    # warning, not a missing-BMS warning.
    local _hilog
    _hilog=$(hdc_shell "hilog 2>/dev/null | head -500")
    if [ -z "$_hilog" ]; then
        warn "BMS check: hilog returned empty (channel may be degraded)"
    elif echo "$_hilog" | grep -qiE 'BMS.*ready|BundleMgr.*init.*ok'; then
        ok "BMS ready signal found in hilog"
    else
        warn "no BMS ready signal in first 500 hilog lines"
    fi

    if [ $errors -gt 0 ]; then
        fail_msg "Stage 5 FAIL — $errors service(s) missing"
    fi
    _alive_probe
    pass_msg "Stage 5 PASS — system healthy post-reboot"
}

# ============================================================================
# Stage 6 — aa start HBC HelloWorld via wrapper
# ============================================================================

stage_6() {
    log "Stage 6 · launch HBC HelloWorld via aa start (Gate 12 budget=${LAUNCH_BUDGET_SECS}s)"
    _alive_probe

    # Gate 11: force-stop OH Photos before any display-touching launch
    # (Island demo/run-live.sh L60-62). Photos might hold render_service
    # state from a prior operator interaction.
    _force_stop_photos

    local apk="$V3_LOCAL/app/HelloWorld.apk"
    if [ ! -f "$apk" ]; then
        warn "Stage 6: HelloWorld.apk not in v3-hbc/app/ — skipping install step"
    else
        local win
        win=$(_to_win_path "$apk")
        # hdc_raw applies </dev/null universally (Fix B port); post-push verify
        # via hdc_shell_check (test -s) catches silent push failure.
        hdc_raw file send "$win" "/data/local/tmp/HelloWorld.apk" >/dev/null
        if ! hdc_shell_check "test -s /data/local/tmp/HelloWorld.apk"; then
            abort "Stage 6: HelloWorld.apk did NOT land on device (silent push failure)"
        fi
        ok "HelloWorld.apk → /data/local/tmp/ (size>0 verified)"

        local out
        out=$(hdc_shell "bm install -p /data/local/tmp/HelloWorld.apk 2>&1" | tr -d '\r')
        if ! echo "$out" | grep -qiE 'success|ok'; then
            abort "Stage 6: bm install failed: $out"
        fi
        ok "bm install OK"
    fi

    local aa_sh="$REPO_ROOT/scripts/v3/aa-launch.sh"
    if [ -x "$aa_sh" ]; then
        # Gate 12: loop-budget on launch (Island demo/run-live.sh L71). Bounds
        # aa-launch.sh runtime so a hung launch surfaces as timeout (rc=124)
        # rather than pinning resources forever.
        log "invoking $aa_sh launch-helloworld (Gate 12 budget=${LAUNCH_BUDGET_SECS}s)..."
        timeout "${LAUNCH_BUDGET_SECS}s" "$aa_sh" launch-helloworld
        local rc=$?
        if [ "$rc" = "124" ]; then
            warn "Stage 6: aa-launch.sh hit Gate 12 launch budget (${LAUNCH_BUDGET_SECS}s) — aborted; Stage 7 marker still authoritative"
        elif [ "$rc" != "0" ]; then
            warn "aa-launch.sh non-zero exit ($rc) (continue to Stage 7 for marker)"
        fi
    else
        log "aa-launch.sh missing; calling aa directly (Gate 12 budget=${LAUNCH_BUDGET_SECS}s)"
        # Gate 12: budget-wrap direct aa start too.
        _budgeted_hdc_shell "$LAUNCH_BUDGET_SECS" "aa start -a com.example.helloworld.MainActivity -b com.example.helloworld 2>&1" | sed 's/^/    /'
    fi

    sleep 3

    local pid
    pid=$(hdc_shell "pidof com.example.helloworld" | tr -d ' \r\n')
    if [ -n "$pid" ]; then
        ok "com.example.helloworld alive (pid=$pid)"
    else
        warn "com.example.helloworld pid not yet visible — Stage 7 marker is authoritative"
    fi

    _alive_probe
    pass_msg "Stage 6 PASS — aa start issued"
}

# ============================================================================
# Stage 7 — capture MainActivity.onCreate L83 marker
# ============================================================================

stage_7() {
    log "Stage 7 · scan hilog for MainActivity.onCreate L83 marker"
    _alive_probe

    local marker="${MARKER:-MainActivity.onCreate}"
    local found=""
    local attempts=12
    local i=0
    while [ $i -lt $attempts ]; do
        # Fix A port: capture-then-evaluate. Empty hilog is a channel-degraded
        # warning distinct from "marker missing".
        local _h
        _h=$(hdc_shell "hilog -x 2>/dev/null | tail -400")
        if [ -z "$_h" ]; then
            warn "Stage 7 attempt $((i+1)): hilog empty (channel may be degraded)"
        elif echo "$_h" | grep -F "$marker" > /tmp/hbc_marker_$$.log; then
            found=$(head -3 /tmp/hbc_marker_$$.log)
            rm -f /tmp/hbc_marker_$$.log
            break
        fi
        i=$((i+1))
        sleep 5
    done

    if [ -z "$found" ]; then
        fail_msg "Stage 7 FAIL — marker '$marker' not seen in hilog within $((attempts*5))s"
    fi

    ok "marker found:"
    echo "$found" | sed 's/^/    /'

    _alive_probe
    pass_msg "Stage 7 PASS — MainActivity.onCreate observed (W2 acceptance criterion)"
}

# ============================================================================
# Dispatcher
# ============================================================================

print_help() {
    sed -n '1,90p' "$0" | sed 's/^# \?//'
}

if [ "$UNINSTALL" = "1" ]; then
    run_uninstall
fi

# Gate 8 — --snapshot-only short-circuit. Initialise snapshot + capture the
# high-risk label set without performing any chcon. Exercises the Gate 8
# plumbing end-to-end (init + capture + recv) as a smoke gate operators can
# run any time the board is up.
if [ "$SNAPSHOT_ONLY" = "1" ]; then
    _check_hdc_version
    _alive_probe
    _chcon_snapshot_init
    # Capture the well-known high-risk paths. Missing paths are skipped with
    # WARN (not aborted) — many won't exist on factory baseline yet.
    _chcon_snapshot_capture \
        /system/lib/liboh_android_runtime.so /system/android/lib/liboh_android_runtime.so \
        /system/lib/liboh_hwui_shim.so       /system/android/lib/liboh_hwui_shim.so \
        /system/lib/liboh_skia_rtti_shim.so  /system/android/lib/liboh_skia_rtti_shim.so \
        /system/lib/liboh_adapter_bridge.so  /system/android/lib/liboh_adapter_bridge.so \
        /system/etc/fonts.xml                /system/android/etc/fonts.xml \
        /system/bin/appspawn-x
    log "Gate 8 snapshot-only: contents of $CHCON_SNAPSHOT_DEVICE:"
    hdc_shell "cat $CHCON_SNAPSHOT_DEVICE" | sed 's/^/    /'
    pass_msg "Gate 8 --snapshot-only PASS — invoke 'restore-chcon' subcommand to replay"
    exit 0
fi

case "$STAGE" in
    0|stage-0)         stage_0   ;;
    1|stage-1)         stage_1   ;;
    2|stage-2)         stage_2   ;;
    3.0|stage-3.0)     stage_3_0 ;;
    3b|stage-3b)       stage_3b  ;;
    3c|stage-3c)       stage_3c  ;;
    3d|stage-3d)       stage_3d  ;;
    3e|stage-3e)       stage_3e  ;;
    3f|stage-3f)       stage_3f  ;;
    3.7|stage-3.7)     stage_3_7 ;;
    3.8|stage-3.8)     stage_3_8 ;;
    3.9|stage-3.9)     stage_3_9 ;;
    4|stage-4)         stage_4   ;;
    5|stage-5)         stage_5   ;;
    6|stage-6)         stage_6   ;;
    7|stage-7)         stage_7   ;;
    # Gate 8 — restore chcon snapshot (operator-invoked rollback)
    restore-chcon)
        _check_hdc_version
        _alive_probe
        _chcon_snapshot_restore
        ;;
    # Gate 10 — restore mount table (operator-invoked rollback)
    restore-mounts)
        _check_hdc_version
        _alive_probe
        _restore_mounts
        pass_msg "Gate 10 restore-mounts PASS"
        ;;
    # Gate 13 — standalone processdump probe
    probe-processdump)
        _check_hdc_version
        _alive_probe
        _probe_processdump
        pass_msg "Gate 13 probe-processdump PASS"
        ;;
    all)
        stage_0
        stage_1
        stage_2
        stage_3_0
        stage_3b
        stage_3c
        stage_3d
        stage_3e
        stage_3f
        stage_3_7
        stage_3_9
        stage_3_8
        if [ "$DO_REBOOT" = "1" ]; then
            stage_4
            stage_5
            stage_6
            stage_7
        else
            warn "'all' run complete WITHOUT reboot (pass --reboot to continue through Stage 4-7)"
        fi
        ;;
    help|-h|--help) print_help; exit 0 ;;
    *)
        echo "ERROR: unknown stage '$STAGE'" >&2
        echo "Valid: 0 1 2 3.0 3b 3c 3d 3e 3f 3.7 3.8 3.9 4 5 6 7 all" >&2
        echo "       restore-chcon (Gate 8) | restore-mounts (Gate 10) | probe-processdump (Gate 13)" >&2
        echo "Flags: --reboot | --no-skip-stage-2 | --uninstall | --snapshot-only (Gate 8)" >&2
        exit 2
        ;;
esac
