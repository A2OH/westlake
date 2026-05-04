#!/bin/bash
# Run a generic-app Westlake phone gate (Noice 1-day proof, parameterized)
# and write a reproducible artifact bundle.
#
# This is a fork of scripts/run-real-mcd-phone-gate.sh, parameterized by
# $PACKAGE / $APK_NAME / $DEX so it can drive any APK through the Westlake
# guest dalvikvm. McD-specific env vars and tap coordinates are dropped.
#
# Usage:
#   scripts/run-noice-phone-gate.sh [artifact-suffix]
#
# Environment:
#   ADB_BIN=/path/to/adb
#   ADB_HOST=localhost
#   ADB_PORT=5037
#   ADB_SERIAL=cfb7c9e3
#   WESTLAKE_HTTP_PROXY_BASE=http://127.0.0.1:18080
#   WESTLAKE_LAUNCH=WESTLAKE_ART_NOICE
#   NOICE_PACKAGE=com.github.ashutoshgngwr.noice  (override target package)
#   NOICE_APK_NAME=noice.apk                       (APK filename on phone)
#   NOICE_DEX_NAME=noice_classes.dex               (primary dex)
#   NOICE_GATE_SLEEP=120                           (total gate duration)
#   NOICE_BOOT_WAIT=120                            (wait for boot/main activity marker)
#   NOICE_UI_WAIT=60                               (wait for UI inflation marker)
#   NOICE_GATE_INTERACT=1                          (run interactions)
#   NOICE_GATE_TAPS=none|center|settings|toggle    (interaction profile)
#   NOICE_SOAK_SECONDS=300                         (post-interaction soak — checker reads this)

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
DEFAULT_WINDOWS_ADB="/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe"
if [ -z "${ADB_BIN:-}" ] && [ -x "$DEFAULT_WINDOWS_ADB" ]; then
    ADB_BIN="$DEFAULT_WINDOWS_ADB"
else
    ADB_BIN="${ADB_BIN:-adb}"
fi
ADB_HOST="${ADB_HOST:-localhost}"
ADB_PORT="${ADB_PORT:-5037}"
ADB_SERIAL="${ADB_SERIAL:-cfb7c9e3}"
ADB_TIMEOUT="${ADB_TIMEOUT:-30}"
WESTLAKE_HTTP_PROXY_BASE="${WESTLAKE_HTTP_PROXY_BASE:-http://127.0.0.1:18080}"
WESTLAKE_START_HTTP_PROXY="${WESTLAKE_START_HTTP_PROXY:-1}"
WESTLAKE_LAUNCH="${WESTLAKE_LAUNCH:-WESTLAKE_ART_NOICE}"

# Generic-app parameters (Noice defaults)
PACKAGE="${NOICE_PACKAGE:-com.github.ashutoshgngwr.noice}"
APK_NAME="${NOICE_APK_NAME:-noice.apk}"
DEX="${NOICE_DEX_NAME:-noice_classes.dex}"

# Soak / wait shape parallels McD's WESTLAKE_GATE_SLEEP / WESTLAKE_DASH_WAIT / WESTLAKE_PDP_WAIT
NOICE_GATE_SLEEP="${NOICE_GATE_SLEEP:-120}"
NOICE_BOOT_WAIT="${NOICE_BOOT_WAIT:-120}"
NOICE_UI_WAIT="${NOICE_UI_WAIT:-60}"
NOICE_GATE_INTERACT="${NOICE_GATE_INTERACT:-0}"
NOICE_GATE_TAPS="${NOICE_GATE_TAPS:-none}"
NOICE_SOAK_SECONDS="${NOICE_SOAK_SECONDS:-300}"
WESTLAKE_GATE_UNSAFE_PROBE="${WESTLAKE_GATE_UNSAFE_PROBE:-0}"

PHONE_DIR="${PHONE_DIR:-/data/local/tmp/westlake}"
HOST_TOUCH_PATH_A="/sdcard/Android/data/com.westlake.host/files/westlake_touch.dat"
HOST_TOUCH_PATH_B="/storage/emulated/0/Android/data/com.westlake.host/files/westlake_touch.dat"
UNSAFE_FLAG_DIR="/sdcard/Android/data/com.westlake.host/files"
# Generic unsafe-flag list: keep mcd_unsafe_* flags so a stray McD flag from a
# previous run also gets cleaned. App-agnostic; safe for non-McD targets.
UNSAFE_FLAG_FILES="westlake_mcd_unsafe_model_commit.txt westlake_mcd_unsafe_storage_commit.txt westlake_mcd_unsafe_observer_dispatch.txt"

# Convenient short tag used in artifact names ("noice" by default)
PACKAGE_TAG="${NOICE_PACKAGE_TAG:-noice}"
SUFFIX="${1:-${PACKAGE_TAG}_gate}"

ART_ROOT="$REPO_ROOT/artifacts/noice-1day"
mkdir -p "$ART_ROOT"
ART="$ART_ROOT/$(date +%Y%m%d_%H%M%S)_${PACKAGE_TAG}_$SUFFIX"
mkdir -p "$ART"
printf '%s\n' "$ART" > "$ART_ROOT/latest_candidate.txt"

ADB=("$ADB_BIN")
if [ -n "$ADB_HOST" ]; then
    ADB+=(-H "$ADB_HOST" -P "$ADB_PORT")
fi
ADB+=(-s "$ADB_SERIAL")

echo "artifact=$ART"
echo "adb=${ADB[*]}"
echo "launch=$WESTLAKE_LAUNCH"
echo "package=$PACKAGE"
echo "apk_name=$APK_NAME"
echo "dex=$DEX"
echo "http_proxy_base=$WESTLAKE_HTTP_PROXY_BASE"
echo "interact=$NOICE_GATE_INTERACT"
echo "gate_taps=$NOICE_GATE_TAPS"
echo "boot_wait=$NOICE_BOOT_WAIT"
echo "ui_wait=$NOICE_UI_WAIT"
echo "soak_seconds=$NOICE_SOAK_SECONDS"
echo "unsafe_probe=$WESTLAKE_GATE_UNSAFE_PROBE"

timeout "$ADB_TIMEOUT" "${ADB[@]}" get-state >/dev/null
"${ADB[@]}" logcat -c
LOGCAT_PID=""
HTTP_PROXY_PID=""
HTTP_PROXY_PORT=""
cleanup_logcat_stream() {
    if [ -n "$LOGCAT_PID" ] && kill -0 "$LOGCAT_PID" 2>/dev/null; then
        kill "$LOGCAT_PID" 2>/dev/null || true
        wait "$LOGCAT_PID" 2>/dev/null || true
    fi
}
cleanup_http_proxy() {
    if [ -n "$HTTP_PROXY_PID" ] && kill -0 "$HTTP_PROXY_PID" 2>/dev/null; then
        kill "$HTTP_PROXY_PID" 2>/dev/null || true
        wait "$HTTP_PROXY_PID" 2>/dev/null || true
    fi
}
start_http_proxy_if_needed() {
    proxy_port="$(printf '%s\n' "$WESTLAKE_HTTP_PROXY_BASE" \
        | sed -n 's#^http://\(127\.0\.0\.1\|localhost\):\([0-9][0-9]*\).*#\2#p')"
    if [ -z "$proxy_port" ]; then
        return 0
    fi
    HTTP_PROXY_PORT="$proxy_port"
    if [ "$WESTLAKE_START_HTTP_PROXY" != "1" ]; then
        return 0
    fi
    if ss -ltn 2>/dev/null | grep -q "[.:]$proxy_port[[:space:]]"; then
        echo "http_proxy=existing port=$proxy_port"
        return 0
    fi
    python3 "$REPO_ROOT/scripts/westlake-dev-http-proxy.py" \
        --host 127.0.0.1 --port "$proxy_port" \
        > "$ART/http-proxy.out" 2> "$ART/http-proxy.err" &
    HTTP_PROXY_PID="$!"
    sleep 0.5
    if kill -0 "$HTTP_PROXY_PID" 2>/dev/null; then
        echo "http_proxy=started port=$proxy_port pid=$HTTP_PROXY_PID"
    else
        echo "http_proxy=start_failed port=$proxy_port"
        HTTP_PROXY_PID=""
    fi
}
trap 'cleanup_logcat_stream; cleanup_http_proxy' EXIT
"${ADB[@]}" logcat -v threadtime > "$ART/logcat-stream.txt" 2> "$ART/logcat-stream.err" &
LOGCAT_PID="$!"
start_http_proxy_if_needed
if [ -n "$HTTP_PROXY_PORT" ]; then
    "${ADB[@]}" reverse "tcp:$HTTP_PROXY_PORT" "tcp:$HTTP_PROXY_PORT" || true
fi
if [ "$WESTLAKE_GATE_UNSAFE_PROBE" = "1" ]; then
    unsafe_cleanup_cmd=""
else
    unsafe_cleanup_cmd="rm -f"
    for flag in $UNSAFE_FLAG_FILES; do
        unsafe_cleanup_cmd="$unsafe_cleanup_cmd $UNSAFE_FLAG_DIR/$flag"
    done
    unsafe_cleanup_cmd="$unsafe_cleanup_cmd;"
fi
"${ADB[@]}" shell "am force-stop com.westlake.host; mkdir -p /sdcard/Android/data/com.westlake.host/files; rm -f /sdcard/Android/data/com.westlake.host/files/westlake_touch.dat /storage/emulated/0/Android/data/com.westlake.host/files/westlake_touch.dat; $unsafe_cleanup_cmd printf '$WESTLAKE_HTTP_PROXY_BASE' > /sdcard/Android/data/com.westlake.host/files/westlake_http_proxy_base.txt"
{
    echo "unsafe_probe=$WESTLAKE_GATE_UNSAFE_PROBE"
    for flag in $UNSAFE_FLAG_FILES; do
        "${ADB[@]}" shell "if [ -f '$UNSAFE_FLAG_DIR/$flag' ]; then printf '$flag='; cat '$UNSAFE_FLAG_DIR/$flag'; echo; else echo '$flag=missing'; fi" || true
    done
} > "$ART/unsafe_flags.txt"
"${ADB[@]}" shell am start -S -W -n com.westlake.host/.WestlakeActivity \
    --es launch "$WESTLAKE_LAUNCH" \
    --es westlake_pkg "$PACKAGE" \
    --es westlake_apk "$APK_NAME" \
    --es westlake_dex "$DEX" \
    | tee "$ART/launch.txt"

touch_seq="$(date +%s)"
touch_packet="$(mktemp)"
cleanup_touch_packet() {
    rm -f "$touch_packet" 2>/dev/null || true
}
trap 'cleanup_logcat_stream; cleanup_touch_packet; cleanup_http_proxy' EXIT

push_touch() {
    local action="$1"
    local x="$2"
    local y="$3"
    touch_seq=$((touch_seq + 1))
    python3 - "$touch_packet" "$action" "$x" "$y" "$touch_seq" <<'PY'
import struct
import sys
path, action, x, y, seq = sys.argv[1:]
with open(path, "wb") as f:
    f.write(struct.pack("<iiii", int(action), int(x), int(y), int(seq)))
PY
    "${ADB[@]}" push "$touch_packet" "$HOST_TOUCH_PATH_A" >/dev/null 2>&1 || true
    "${ADB[@]}" push "$touch_packet" "$HOST_TOUCH_PATH_B" >/dev/null 2>&1 || true
}

tap_guest() {
    local x="$1"
    local y="$2"
    push_touch 0 "$x" "$y"
    sleep 0.2
    push_touch 1 "$x" "$y"
}

# Generic interaction profile. Day-1 only `none` and `center` are functional.
# `settings` and `toggle` require layout-aware coordinate discovery and are
# stubbed for follow-up Agent 5 work.
run_noice_taps() {
    local profile="$1"
    case "$profile" in
        none)
            echo "interaction=noice_taps profile=none"
            return 0
            ;;
        center)
            echo "interaction=noice_taps profile=center action=single_center_tap"
            tap_guest 540 1100
            sleep 1
            tap_guest 540 1100
            return 0
            ;;
        settings)
            echo "interaction=noice_taps profile=settings status=not_implemented_in_day_1 reason=needs_layout_aware_target"
            return 0
            ;;
        toggle)
            echo "interaction=noice_taps profile=toggle status=not_implemented_in_day_1 reason=needs_layout_aware_target"
            return 0
            ;;
        "")
            return 0
            ;;
        *)
            echo "interaction=noice_taps profile=$profile status=unknown_profile"
            return 1
            ;;
    esac
}

wait_for_stream_marker() {
    local marker="$1"
    local timeout_secs="$2"
    local start
    start="$(date +%s)"
    while true; do
        if grep -aq "$marker" "$ART/logcat-stream.txt" 2>/dev/null; then
            return 0
        fi
        if [ $(( $(date +%s) - start )) -ge "$timeout_secs" ]; then
            return 1
        fi
        sleep 2
    done
}

gate_start="$(date +%s)"
if [ "$NOICE_GATE_INTERACT" = "1" ]; then
    if wait_for_stream_marker "NOICE_MAIN_ACTIVITY phase=resumed" "$NOICE_BOOT_WAIT"; then
        echo "interaction=noice_main_activity_resumed"
    else
        echo "interaction=noice_main_activity_timeout"
    fi
    if wait_for_stream_marker "NOICE_VIEW_INFLATED" "$NOICE_UI_WAIT"; then
        echo "interaction=noice_views_inflated"
        run_noice_taps "$NOICE_GATE_TAPS"
    else
        echo "interaction=noice_views_timeout"
    fi
fi
elapsed=$(( $(date +%s) - gate_start ))
remaining=$(( NOICE_GATE_SLEEP - elapsed ))
if [ "$remaining" -gt 0 ]; then
    sleep "$remaining"
fi
cleanup_logcat_stream
"${ADB[@]}" exec-out screencap -p > "$ART/screen.png" 2> "$ART/screen_screencap.err" || true
"${ADB[@]}" logcat -d > "$ART/logcat-dump.txt"
if [ -s "$ART/logcat-stream.txt" ]; then
    cp "$ART/logcat-stream.txt" "$ART/logcat.txt"
else
    cp "$ART/logcat-dump.txt" "$ART/logcat.txt"
fi
"${ADB[@]}" shell ps -A -o USER,PID,PPID,NAME,ARGS > "$ART/processes.txt" || true
sha256sum "$ART/screen.png" > "$ART/screen_hash.txt" 2>/dev/null || true
sha256sum "$REPO_ROOT/aosp-shim.dex" > "$ART/local_shim_hash.txt"
sha256sum \
    "$REPO_ROOT/ohos-deploy/arm64-a15/dalvikvm" \
    "$REPO_ROOT/ohos-deploy/arm64-a15/core-oj.jar" \
    "$REPO_ROOT/ohos-deploy/arm64-a15/core-libart.jar" \
    "$REPO_ROOT/ohos-deploy/arm64-a15/core-icu4j.jar" \
    "$REPO_ROOT/westlake-host-gradle/app/src/main/assets/bouncycastle.jar" \
    "$REPO_ROOT/aosp-shim.dex" \
    > "$ART/local_runtime_hashes.txt"
"${ADB[@]}" shell sha256sum \
    "$PHONE_DIR/dalvikvm" \
    "$PHONE_DIR/core-oj.jar" \
    "$PHONE_DIR/core-libart.jar" \
    "$PHONE_DIR/core-icu4j.jar" \
    "$PHONE_DIR/bouncycastle.jar" \
    "$PHONE_DIR/aosp-shim.dex" \
    > "$ART/phone_runtime_hashes.txt" 2> "$ART/phone_runtime_hashes.err" || true

# Record gate parameters so the checker can read soak duration / package etc.
{
    echo "package=$PACKAGE"
    echo "package_tag=$PACKAGE_TAG"
    echo "apk_name=$APK_NAME"
    echo "dex=$DEX"
    echo "gate_taps=$NOICE_GATE_TAPS"
    echo "soak_seconds=$NOICE_SOAK_SECONDS"
    echo "boot_wait=$NOICE_BOOT_WAIT"
    echo "ui_wait=$NOICE_UI_WAIT"
} > "$ART/noice_gate_params.txt"

# Generic, app-agnostic proof-grep. We keep the cross-cutting Westlake
# infrastructure markers (Fatal/SIGBUS/Realm/network/touch dispatch) but drop
# the McD-specific ones; we add the NOICE_* markers the launcher should emit.
grep -aE "Fatal signal|SIGBUS|SIGSEGV|BUS_ADRALN|Failed requirement|NOICE_MAIN_ACTIVITY|NOICE_VIEW_INFLATED|NOICE_SOUND_CARD_CLICK|APP_MAIN_ACTIVITY|APP_VIEW_INFLATED|APP_CLICK|unsafe|UNSAFE|WESTLAKE_HOST_TOUCH_DISPATCH|WestlakeHttp|PFCUT-REALM|Realm|NoSuchMethodError|ClassNotFoundException|NoSuchFieldError|UnsatisfiedLinkError|AbstractMethodError|IncompatibleClassChangeError|VerifyError|ClassCastException" "$ART/logcat.txt" > "$ART/proof_grep.txt" || true

NOICE_PACKAGE="$PACKAGE" NOICE_PACKAGE_TAG="$PACKAGE_TAG" \
    NOICE_APK_NAME="$APK_NAME" NOICE_DEX_NAME="$DEX" \
    NOICE_GATE_TAPS="$NOICE_GATE_TAPS" NOICE_SOAK_SECONDS="$NOICE_SOAK_SECONDS" \
    "$REPO_ROOT/scripts/check-noice-proof.sh" "$ART" | tee "$ART/check-noice-proof.txt"
