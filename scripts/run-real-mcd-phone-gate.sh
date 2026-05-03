#!/bin/bash
# Run a real-McD Westlake phone gate and write a reproducible artifact bundle.
#
# Usage:
#   scripts/run-real-mcd-phone-gate.sh [artifact-suffix]
#
# Environment:
#   ADB_BIN=/path/to/adb
#   ADB_HOST=localhost
#   ADB_PORT=5037
#   ADB_SERIAL=cfb7c9e3
#   WESTLAKE_HTTP_PROXY_BASE=http://127.0.0.1:18080
#   WESTLAKE_LAUNCH=WESTLAKE_ART_MCD
#   WESTLAKE_GATE_SLEEP=120
#   WESTLAKE_GATE_INTERACT=1
#   WESTLAKE_GATE_PDP_PRE_ADD_TAPS=quantity_plus
#   WESTLAKE_GATE_PDP_POST_ADD_TAPS=quantity_minus,customize

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
WESTLAKE_LAUNCH="${WESTLAKE_LAUNCH:-WESTLAKE_ART_MCD}"
WESTLAKE_GATE_SLEEP="${WESTLAKE_GATE_SLEEP:-120}"
WESTLAKE_GATE_INTERACT="${WESTLAKE_GATE_INTERACT:-0}"
WESTLAKE_GATE_PDP_PRE_ADD_TAPS="${WESTLAKE_GATE_PDP_PRE_ADD_TAPS:-}"
WESTLAKE_GATE_PDP_POST_ADD_TAPS="${WESTLAKE_GATE_PDP_POST_ADD_TAPS:-}"
WESTLAKE_DASH_WAIT="${WESTLAKE_DASH_WAIT:-260}"
WESTLAKE_PDP_WAIT="${WESTLAKE_PDP_WAIT:-180}"
WESTLAKE_GATE_UNSAFE_PROBE="${WESTLAKE_GATE_UNSAFE_PROBE:-0}"
PHONE_DIR="${PHONE_DIR:-/data/local/tmp/westlake}"
HOST_TOUCH_PATH_A="/sdcard/Android/data/com.westlake.host/files/westlake_touch.dat"
HOST_TOUCH_PATH_B="/storage/emulated/0/Android/data/com.westlake.host/files/westlake_touch.dat"
UNSAFE_FLAG_DIR="/sdcard/Android/data/com.westlake.host/files"
UNSAFE_FLAG_FILES="westlake_mcd_unsafe_model_commit.txt westlake_mcd_unsafe_storage_commit.txt westlake_mcd_unsafe_observer_dispatch.txt"
SUFFIX="${1:-mcd_shadow_requestmanager_bridge_gate}"

ART="$REPO_ROOT/artifacts/real-mcd/$(date +%Y%m%d_%H%M%S)_$SUFFIX"
mkdir -p "$ART"
printf '%s\n' "$ART" > "$REPO_ROOT/artifacts/real-mcd/latest_candidate.txt"

ADB=("$ADB_BIN")
if [ -n "$ADB_HOST" ]; then
    ADB+=(-H "$ADB_HOST" -P "$ADB_PORT")
fi
ADB+=(-s "$ADB_SERIAL")

echo "artifact=$ART"
echo "adb=${ADB[*]}"
echo "launch=$WESTLAKE_LAUNCH"
echo "http_proxy_base=$WESTLAKE_HTTP_PROXY_BASE"
echo "interact=$WESTLAKE_GATE_INTERACT"
echo "pdp_pre_add_taps=$WESTLAKE_GATE_PDP_PRE_ADD_TAPS"
echo "pdp_post_add_taps=$WESTLAKE_GATE_PDP_POST_ADD_TAPS"
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
"${ADB[@]}" shell am start -S -W -n com.westlake.host/.WestlakeActivity --es launch "$WESTLAKE_LAUNCH" | tee "$ART/launch.txt"

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

tap_pdp_control() {
    local control="$1"
    local x=""
    local y=""
    case "$control" in
        quantity_minus)
            x=44
            y=428
            ;;
        quantity_plus)
            x=128
            y=428
            ;;
        customize)
            x=350
            y=428
            ;;
        customize_card)
            x=70
            y=512
            ;;
        nutrition)
            x=70
            y=688
            ;;
        add_to_order|add|bag)
            x=350
            y=960
            ;;
        "")
            return 0
            ;;
        *)
            echo "interaction=pdp_control_unknown control=$control"
            return 1
            ;;
    esac
    echo "interaction=pdp_control control=$control x=$x y=$y"
    tap_guest "$x" "$y"
}

tap_pdp_control_sequence() {
    local sequence="$1"
    local old_ifs="$IFS"
    local control=""
    IFS=","
    for control in $sequence; do
        IFS="$old_ifs"
        tap_pdp_control "$control" || return 1
        sleep 1
        IFS=","
    done
    IFS="$old_ifs"
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
if [ "$WESTLAKE_GATE_INTERACT" = "1" ]; then
    if wait_for_stream_marker "MCD_DASH_SECTIONS_READY" "$WESTLAKE_DASH_WAIT"; then
        echo "interaction=dashboard_ready"
        tap_guest 100 500
        sleep 1
        tap_guest 100 500
    else
        echo "interaction=dashboard_timeout"
    fi
    if wait_for_stream_marker "MCD_ORDER_PDP_READY" "$WESTLAKE_PDP_WAIT"; then
        echo "interaction=pdp_ready"
        if [ -n "$WESTLAKE_GATE_PDP_PRE_ADD_TAPS" ]; then
            tap_pdp_control_sequence "$WESTLAKE_GATE_PDP_PRE_ADD_TAPS"
        fi
        tap_guest 350 960
        sleep 1
        tap_guest 350 960
        if [ -n "$WESTLAKE_GATE_PDP_POST_ADD_TAPS" ]; then
            sleep 2
            tap_pdp_control_sequence "$WESTLAKE_GATE_PDP_POST_ADD_TAPS"
        fi
    else
        echo "interaction=pdp_timeout"
    fi
fi
elapsed=$(( $(date +%s) - gate_start ))
remaining=$(( WESTLAKE_GATE_SLEEP - elapsed ))
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
grep -aE "Fatal signal|SIGBUS|SIGSEGV|BUS_ADRALN|Failed requirement|HomeDashboard|Strict dashboard frame|MCD_DASH_U6_SEEDED|MCD_DASH_STOCK_VIEW_ATTACHED|MCD_DASH_REAL_VIEW_ATTACHED|MCD_DASH_RECYCLER_PROBE|MCD_DASH_ADAPTER_BOOTSTRAP|MCD_DASH_REAL_PROMO|MCD_DASH_REAL_VIEW_ATTACH_FAIL|MCD_DASH_SECTION_VIEW_ATTACHED|MCD_DASH_SECTIONS_READY|MCD_DASH_FALLBACK|MCD_DASH_XML_SHELL_RESULT|MCD_REAL_XML|layout_home_menu_guest_user|layout_home_promotion_item|layout_home_popular_item_adapter|MCD_ORDER|MCD_PDP|MCD_CART|MCD_BAG|MCD_JUSTFLIP|MCD_TELEMETRY|Telemetry_not_initialized|BasketAPIHandler|CartProduct|getChoices|ProductUseCase|basecart-active-query|BaseCart|unsafe|UNSAFE|STOCK_REJECT|stock rejection|GENERIC_HIT|WESTLAKE_HOST_TOUCH_DISPATCH|JustFlip|PFCUT-REALM|Realm|PF-MCD-ROOT|Frame:|PFCUT-MCD-NET|WestlakeHttp|NoSuchMethodError|IncompatibleClassChangeError|ClassCastException|VerifyError" "$ART/logcat.txt" > "$ART/proof_grep.txt" || true

"$REPO_ROOT/scripts/check-real-mcd-proof.sh" "$ART" | tee "$ART/check-real-mcd-proof.txt"
