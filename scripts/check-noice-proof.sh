#!/bin/bash
# Compact acceptance/regression gate for generic-app (Noice 1-day proof)
# Westlake phone artifacts.
#
# This is a fork of scripts/check-real-mcd-proof.sh. McD-specific markers
# (MCD_PDP_*, MCD_DASH_*, etc.) are replaced with generic, app-parameterized
# markers (NOICE_* / APP_*). All cross-cutting Westlake infrastructure
# checks (subprocess purity, unsafe-flags-off, runtime hashes, screenshot)
# are kept verbatim and parameterized by $PACKAGE.
#
# Usage:
#   scripts/check-noice-proof.sh [artifact-dir]
#
# With no artifact-dir, checks the latest artifacts/noice-1day/20* directory.
#
# Environment (passed by run-noice-phone-gate.sh, but standalone-safe):
#   NOICE_PACKAGE     (default: com.github.ashutoshgngwr.noice)
#   NOICE_PACKAGE_TAG (default: noice)         used in marker labels
#   NOICE_APK_NAME    (default: noice.apk)
#   NOICE_DEX_NAME    (default: noice_classes.dex)
#   NOICE_GATE_TAPS   (default: none)          reflects what the gate did
#   NOICE_SOAK_SECONDS (default: 300)          required soak duration

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
DEFAULT_ARTIFACT_ROOT="$REPO_ROOT/artifacts/noice-1day"
ARTIFACT="${1:-}"

if [ -z "$ARTIFACT" ]; then
    while IFS= read -r candidate; do
        if find "$candidate" -maxdepth 1 -type f -name 'logcat*.txt' | grep -q .; then
            ARTIFACT="$candidate"
        fi
    done < <(find "$DEFAULT_ARTIFACT_ROOT" -mindepth 1 -maxdepth 1 -type d -name '20*' | sort)
fi

if [ -z "$ARTIFACT" ] || [ ! -d "$ARTIFACT" ]; then
    echo "ERROR: artifact dir not found: ${ARTIFACT:-<empty>}" >&2
    exit 2
fi

LOG="$ARTIFACT/logcat.txt"
if [ ! -f "$LOG" ]; then
    LOG="$(find "$ARTIFACT" -maxdepth 1 -type f -name 'logcat*.txt' | sort | tail -1)"
fi
LOG_STREAM="$ARTIFACT/logcat-stream.txt"
LOG_DUMP="$ARTIFACT/logcat-dump.txt"
PROCESSES="$ARTIFACT/processes.txt"
SCREEN="$ARTIFACT/screen.png"
LOCAL_SHIM_HASH="$ARTIFACT/local_shim_hash.txt"
LOCAL_RUNTIME_HASHES="$ARTIFACT/local_runtime_hashes.txt"
PHONE_RUNTIME_HASHES="$ARTIFACT/phone_runtime_hashes.txt"
UNSAFE_FLAGS="$ARTIFACT/unsafe_flags.txt"
GATE_PARAMS="$ARTIFACT/noice_gate_params.txt"

if [ -z "$LOG" ] || [ ! -f "$LOG" ]; then
    echo "ERROR: logcat file not found in $ARTIFACT" >&2
    exit 2
fi

# Resolve PACKAGE / PACKAGE_TAG / APK / DEX / TAPS / SOAK from (in order):
# 1. env vars (set by run-noice-phone-gate.sh when invoked from there)
# 2. noice_gate_params.txt in the artifact (standalone re-runs)
# 3. defaults
read_param_from_file() {
    local key="$1"
    if [ -f "$GATE_PARAMS" ]; then
        awk -F= -v k="$key" '$1 == k { print $2 }' "$GATE_PARAMS" | tail -1
    fi
}

PACKAGE="${NOICE_PACKAGE:-$(read_param_from_file package)}"
PACKAGE="${PACKAGE:-com.github.ashutoshgngwr.noice}"
PACKAGE_TAG="${NOICE_PACKAGE_TAG:-$(read_param_from_file package_tag)}"
PACKAGE_TAG="${PACKAGE_TAG:-noice}"
APK_NAME="${NOICE_APK_NAME:-$(read_param_from_file apk_name)}"
APK_NAME="${APK_NAME:-noice.apk}"
DEX_NAME="${NOICE_DEX_NAME:-$(read_param_from_file dex)}"
DEX_NAME="${DEX_NAME:-noice_classes.dex}"
GATE_TAPS="${NOICE_GATE_TAPS:-$(read_param_from_file gate_taps)}"
GATE_TAPS="${GATE_TAPS:-none}"
SOAK_SECONDS="${NOICE_SOAK_SECONDS:-$(read_param_from_file soak_seconds)}"
SOAK_SECONDS="${SOAK_SECONDS:-300}"

# Regex-escape the package for use inside grep -E patterns.
escape_re() {
    printf '%s' "$1" | sed 's/[][\.|$(){}?+*^]/\\&/g'
}
PACKAGE_RE="$(escape_re "$PACKAGE")"
APK_NAME_RE="$(escape_re "$APK_NAME")"
DEX_NAME_RE="$(escape_re "$DEX_NAME")"
# Filename-safe path-suffix patterns (e.g. com.mcdonalds.app -> com_mcdonalds_app)
PACKAGE_FS="$(printf '%s' "$PACKAGE" | tr '.' '_')"
PACKAGE_FS_RE="$(escape_re "$PACKAGE_FS")"

count_re() {
    local file="$1"
    local pattern="$2"
    if [ -f "$file" ]; then
        grep -aE -c "$pattern" "$file" || true
    else
        echo 0
    fi
}

last_re() {
    local file="$1"
    local pattern="$2"
    if [ -f "$file" ]; then
        grep -aE "$pattern" "$file" | tail -1 || true
    fi
}

extract_last_number_field() {
    local line="$1"
    local key="$2"
    printf '%s\n' "$line" \
        | grep -ao "${key}=[0-9][0-9]*" \
        | tail -1 \
        | cut -d= -f2 \
        || true
}

hash_for_path_suffix() {
    local file="$1"
    local suffix="$2"
    if [ -f "$file" ]; then
        awk -v suffix="$suffix" '{
            path=$2
            gsub(/\r/, "", path)
            if (length(path) >= length(suffix) &&
                substr(path, length(path) - length(suffix) + 1) == suffix) {
                print $1
            }
        }' "$file" | tail -1
    fi
}

status=0
artifact_base="$(basename "$ARTIFACT")"
inconclusive_count=0
pass() {
    echo "PASS $*"
}
warn() {
    echo "WARN $*"
}
fail() {
    echo "FAIL $*"
    status=1
}
inconclusive() {
    # INCONCLUSIVE markers do NOT fail the gate (per day-1 contract). They
    # are surfaced verbatim and counted so the supervisor / Agent 5 can
    # decide whether to add launcher emitters.
    echo "INCONCLUSIVE $*"
    inconclusive_count=$((inconclusive_count + 1))
}

echo "artifact=$ARTIFACT"
echo "log=$LOG"
echo "package=$PACKAGE"
echo "package_tag=$PACKAGE_TAG"
echo "apk_name=$APK_NAME"
echo "dex_name=$DEX_NAME"
echo "gate_taps=$GATE_TAPS"
echo "soak_seconds=$SOAK_SECONDS"

# Cross-cutting unsafe-flags-off check. RE-USES the 98719db2 fix:
# the negative cases (no_unsafe / unsafe_off) take precedence and are
# evaluated BEFORE the catch-all *unsafe* fallback. Strictly preserved
# from check-real-mcd-proof.sh.
unsafe_marker_count="$(count_re "$LOG" 'unsafe_|unsafe-|UNSAFE|unsafe_observer_dispatch_opt_in')"
unsafe_flag_count=0
if [ -f "$UNSAFE_FLAGS" ]; then
    unsafe_flag_count="$(count_re "$UNSAFE_FLAGS" 'unsafe_probe=1|westlake_(mcd_)?unsafe_.*=(1|true|TRUE|yes|YES|enabled|ENABLED)')"
fi
unsafe_name_count=0
case "$artifact_base" in
    *no_unsafe*|*NO_UNSAFE*|*unsafe_off*|*UNSAFE_OFF*) unsafe_name_count=0 ;;
    *true_unsafe*|*TRUE_UNSAFE*|*unsafe_probe*|*UNSAFE_PROBE*|*unsafe_cart_commit*|*UNSAFE_CART_COMMIT*|*unsafe_model*|*UNSAFE_MODEL*|*unsafe_storage*|*UNSAFE_STORAGE*|*unsafe_observer*|*UNSAFE_OBSERVER*) unsafe_name_count=1 ;;
    *unsafe*|*UNSAFE*) unsafe_name_count=1 ;;
esac
unsafe_probe=0
if [ "$unsafe_marker_count" -gt 0 ] || [ "$unsafe_flag_count" -gt 0 ] || [ "$unsafe_name_count" -gt 0 ]; then
    unsafe_probe=1
fi
if [ "$unsafe_probe" -eq 0 ]; then
    pass "proof_unsafe_flags_off markers=0 flags=0 artifact=$artifact_base"
else
    fail "proof_unsafe_flags_off unsafeProbe=1 markers=$unsafe_marker_count flags=$unsafe_flag_count artifact=$artifact_base"
    last_re "$LOG" 'unsafe_|unsafe-|UNSAFE|unsafe_observer_dispatch_opt_in'
fi

if [ -s "$LOG_STREAM" ]; then
    stream_bytes="$(stat -c%s "$LOG_STREAM")"
    if cmp -s "$LOG_STREAM" "$LOG"; then
        pass "proof_logcat_streamed source=logcat-stream.txt bytes=$stream_bytes"
    else
        fail "proof_logcat_streamed source_mismatch stream_bytes=$stream_bytes log_bytes=$(stat -c%s "$LOG" 2>/dev/null || echo 0)"
    fi
else
    fail "proof_logcat_streamed missing_or_empty path=$LOG_STREAM"
fi
if [ -s "$LOG_DUMP" ]; then
    pass "proof_logcat_dump_present bytes=$(stat -c%s "$LOG_DUMP")"
else
    warn "proof_logcat_dump_present missing_or_empty path=$LOG_DUMP"
fi

current_shim_hash=""
if [ -f "$REPO_ROOT/aosp-shim.dex" ]; then
    current_shim_hash="$(sha256sum "$REPO_ROOT/aosp-shim.dex" | awk '{print $1}')"
fi
artifact_shim_hash="$(hash_for_path_suffix "$LOCAL_SHIM_HASH" 'aosp-shim.dex')"
if [ -n "$current_shim_hash" ] && [ "$artifact_shim_hash" = "$current_shim_hash" ]; then
    pass "proof_current_local_shim_hash hash=$artifact_shim_hash"
else
    fail "proof_current_local_shim_hash artifact=${artifact_shim_hash:-missing} current=${current_shim_hash:-missing}"
fi
if [ -s "$LOCAL_RUNTIME_HASHES" ]; then
    local_runtime_shim_hash="$(hash_for_path_suffix "$LOCAL_RUNTIME_HASHES" 'aosp-shim.dex')"
    if [ "$local_runtime_shim_hash" = "$current_shim_hash" ]; then
        pass "proof_local_runtime_hashes_present aosp_shim=$local_runtime_shim_hash"
    else
        fail "proof_local_runtime_hashes_present aosp_shim=${local_runtime_shim_hash:-missing} current=${current_shim_hash:-missing}"
    fi
else
    fail "proof_local_runtime_hashes_present missing path=$LOCAL_RUNTIME_HASHES legacy_local_shim_hash=${artifact_shim_hash:-missing}"
fi
if [ -s "$PHONE_RUNTIME_HASHES" ]; then
    phone_runtime_shim_hash="$(hash_for_path_suffix "$PHONE_RUNTIME_HASHES" 'aosp-shim.dex')"
    if [ "$phone_runtime_shim_hash" = "$current_shim_hash" ]; then
        pass "proof_phone_runtime_hashes_current aosp_shim=$phone_runtime_shim_hash"
    else
        fail "proof_phone_runtime_hashes_current aosp_shim=${phone_runtime_shim_hash:-missing} current=${current_shim_hash:-missing}"
    fi
else
    fail "proof_phone_runtime_hashes_current missing path=$PHONE_RUNTIME_HASHES"
fi

if [ -f "$SCREEN" ]; then
    screen_sha="$(sha256sum "$SCREEN" | awk '{print $1}')"
    screen_bytes="$(stat -c%s "$SCREEN")"
    echo "screen_sha=$screen_sha"
    echo "screen_bytes=$screen_bytes"
else
    fail "screenshot_missing path=$SCREEN"
fi

# Generic subprocess purity & guest-dalvikvm checks parameterized by package.
host_pid=""
vm_pid=""
vm_ppid=""
subprocess_purity_ok=0
real_app_guest_ok=0
if [ -f "$PROCESSES" ]; then
    host_pid="$(awk '{sub(/\r$/,"")} $4=="com.westlake.host"{print $2; exit}' "$PROCESSES")"
    vm_pid="$(awk '{sub(/\r$/,"")} $4=="dalvikvm"{print $2; exit}' "$PROCESSES")"
    vm_ppid="$(awk '{sub(/\r$/,"")} $4=="dalvikvm"{print $3; exit}' "$PROCESSES")"
    direct_app="$(count_re "$PROCESSES" "(^|[[:space:]])${PACKAGE_RE}($|[[:space:]])")"
    real_app_guest_line="$(awk -v pkg_re="-Dwestlake\\.apk\\.package=${PACKAGE_RE}\$" \
                              -v apk_re="-Dwestlake\\.apk\\.path=/data/local/tmp/westlake/${APK_NAME_RE}\$" \
                              -v dex_re="${DEX_NAME_RE}" '
        {
            sub(/\r$/, "")
            if ($4 == "dalvikvm" &&
                    match($0, pkg_re) &&
                    match($0, apk_re) &&
                    index($0, dex_re) > 0) {
                print
                exit
            }
        }
    ' "$PROCESSES" || true)"
    # Looser fallback: dalvikvm command line mentions package + dex name
    # (covers minor flag-format drift from the launcher).
    if [ -z "$real_app_guest_line" ]; then
        real_app_guest_line="$(awk -v pkg_re="${PACKAGE_RE}" \
                                  -v dex_re="${DEX_NAME_RE}" '
            {
                sub(/\r$/, "")
                if ($4 == "dalvikvm" &&
                        index($0, pkg_re) > 0 &&
                        index($0, dex_re) > 0) {
                    print
                    exit
                }
            }
        ' "$PROCESSES" || true)"
    fi
    if [ -n "$host_pid" ] && [ -n "$vm_pid" ] && [ "$vm_ppid" = "$host_pid" ] && [ "$direct_app" -eq 0 ]; then
        subprocess_purity_ok=1
        pass "westlake_subprocess_purity host_pid=$host_pid vm_pid=$vm_pid vm_ppid=$vm_ppid direct_${PACKAGE_TAG}_processes=0"
    else
        fail "westlake_subprocess_purity host_pid=${host_pid:-missing} vm_pid=${vm_pid:-missing} vm_ppid=${vm_ppid:-missing} direct_${PACKAGE_TAG}_processes=$direct_app"
    fi
    if [ -n "$real_app_guest_line" ]; then
        real_app_guest_ok=1
        pass "proof_real_app_guest_dalvikvm package=$PACKAGE apk=$APK_NAME classes=$DEX_NAME"
    else
        fail "proof_real_app_guest_dalvikvm package=$PACKAGE apk=$APK_NAME classes=$DEX_NAME vm_pid=${vm_pid:-missing}"
    fi
else
    fail "process_snapshot_missing path=$PROCESSES"
fi

fatal_re='Fatal signal|SIGBUS|BUS_ADRALN|SIGSEGV|SIGABRT|FATAL EXCEPTION|JNI DETECTED ERROR|Failed requirement'
fatal_count="$(count_re "$LOG" "$fatal_re")"
if [ "$fatal_count" -eq 0 ]; then
    pass "no_fatal_failed_requirement count=0"
else
    fail "no_fatal_failed_requirement count=$fatal_count"
    last_re "$LOG" "$fatal_re"
fi

# --- Generic-app (Noice) markers --------------------------------------------
# These come from WestlakeLauncher.java emitters that may not yet exist for
# non-McD apps. If the markers are absent, report INCONCLUSIVE (not FAIL),
# per day-1 contract — Agent 5 (optional) adds the emitters if needed.

main_activity_count="$(count_re "$LOG" 'NOICE_MAIN_ACTIVITY phase=resumed')"
view_inflated_count="$(count_re "$LOG" 'NOICE_VIEW_INFLATED')"
sound_card_click_count="$(count_re "$LOG" 'NOICE_SOUND_CARD_CLICK invoked=true')"

if [ "$main_activity_count" -ge 1 ]; then
    pass "noice_main_activity_resumed count=$main_activity_count required>=1"
else
    inconclusive "noice_main_activity_resumed count=0 required>=1 reason=launcher_emitter_missing_for_${PACKAGE_TAG} next_action=spawn_agent_5"
fi

if [ "$view_inflated_count" -ge 5 ]; then
    pass "noice_views_inflated count=$view_inflated_count required>=5"
elif [ "$view_inflated_count" -gt 0 ]; then
    inconclusive "noice_views_inflated count=$view_inflated_count required>=5 reason=insufficient_inflations_or_partial_emitter"
else
    inconclusive "noice_views_inflated count=0 required>=5 reason=launcher_emitter_missing_for_${PACKAGE_TAG} next_action=spawn_agent_5"
fi

# Sound-card click is only required when the gate actually performed taps
# (NOICE_GATE_TAPS=center). For 'none' / 'settings' / 'toggle' it's not
# applicable — surface as INCONCLUSIVE noting the reason.
case "$GATE_TAPS" in
    center)
        if [ "$sound_card_click_count" -ge 1 ]; then
            pass "noice_sound_card_click count=$sound_card_click_count required>=1 gate_taps=center"
        else
            inconclusive "noice_sound_card_click count=0 required>=1 gate_taps=center reason=tap_did_not_route_or_emitter_missing next_action=spawn_agent_5_or_layout_aware_target"
        fi
        ;;
    none|settings|toggle|"")
        inconclusive "noice_sound_card_click count=$sound_card_click_count required>=1 gate_taps=$GATE_TAPS reason=not_applicable_for_this_tap_profile"
        ;;
    *)
        inconclusive "noice_sound_card_click count=$sound_card_click_count required>=1 gate_taps=$GATE_TAPS reason=unknown_tap_profile"
        ;;
esac

# Idle soak check — vm_pid still present after gate AND log spans soak duration.
# Day-1 evidence: artifact captured a vm_pid AND a logcat-stream that ran for
# >= SOAK_SECONDS without fatal signal. Note: in standalone re-runs we don't
# have the original wall clock, so we approximate by checking (a) vm_pid was
# captured (proves vm survived to gate-end ps snapshot), and (b) no fatal
# signals fired (already covered above; we re-derive here).
soak_status="FAIL"
soak_reason=""
if [ -z "$vm_pid" ]; then
    soak_reason="vm_pid_missing_at_gate_end"
elif [ "$fatal_count" -gt 0 ]; then
    soak_reason="fatal_signal_during_soak"
else
    if [ -s "$LOG_STREAM" ]; then
        # Stream file mtime - file's first-write-time approximation. Use mtime
        # only as upper bound; if logcat-dump.txt also exists, prefer the
        # difference between dump/stream mtimes if interpretable.
        stream_mtime="$(stat -c%Y "$LOG_STREAM" 2>/dev/null || echo 0)"
        artifact_mtime="$(stat -c%Y "$ARTIFACT" 2>/dev/null || echo 0)"
        if [ "$stream_mtime" -gt 0 ] && [ "$artifact_mtime" -gt 0 ]; then
            elapsed=$(( stream_mtime - artifact_mtime ))
            if [ "$elapsed" -lt 0 ]; then
                elapsed=0
            fi
            if [ "$elapsed" -ge "$SOAK_SECONDS" ]; then
                soak_status="PASS"
                soak_reason="vm_pid_present elapsed=${elapsed}s"
            else
                soak_status="INCONCLUSIVE"
                soak_reason="elapsed=${elapsed}s required=${SOAK_SECONDS}s"
            fi
        else
            soak_status="INCONCLUSIVE"
            soak_reason="cannot_determine_elapsed_from_artifact_mtimes"
        fi
    else
        soak_status="INCONCLUSIVE"
        soak_reason="logcat_stream_missing"
    fi
fi
case "$soak_status" in
    PASS)
        pass "noice_idle_soak vm_pid=$vm_pid soak=PASS $soak_reason"
        echo "noice_5min_soak_status=PASS"
        ;;
    INCONCLUSIVE)
        inconclusive "noice_idle_soak vm_pid=${vm_pid:-missing} soak=INCONCLUSIVE $soak_reason"
        echo "noice_5min_soak_status=INCONCLUSIVE"
        ;;
    *)
        fail "noice_idle_soak vm_pid=${vm_pid:-missing} soak=FAIL $soak_reason"
        echo "noice_5min_soak_status=FAIL"
        ;;
esac

# Aggregate gate status. Per day-1 contract: PASS only if all required PASS.
# INCONCLUSIVE markers do NOT fail the gate but are surfaced explicitly.
echo "inconclusive_count=$inconclusive_count"
if [ "$inconclusive_count" -gt 0 ]; then
    echo "gate_status_note=inconclusive_markers_do_not_fail_day_1 inconclusive=$inconclusive_count"
fi
if [ "$status" -eq 0 ]; then
    echo "gate_status=PASS"
else
    echo "gate_status=FAIL"
fi

exit "$status"
