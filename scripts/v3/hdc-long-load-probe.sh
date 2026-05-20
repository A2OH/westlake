#!/usr/bin/env bash
# Long-load probe for hdc.exe Channel A regression hunting.
# Sustains ~5 minutes of varied shell call patterns. Detects:
#   - Silent returns (empty stdout + exit 0)
#   - Hangs (single call > 30s)
#   - Latency growth (rolling p95)
#   - Cumulative channel death (eventual permanent silence)
#
# Usage: hdc-long-load-probe.sh [hdc-path] [serial] [duration-secs]
#
# READ-ONLY. Probe never writes to /system or /data/local/tmp.
# Never `hdc target boot`, never `setenforce`.

set -uo pipefail

HDC="${1:-/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe}"
SERIAL="${2:-dd011a414436314130101250040eac00}"
DURATION_SECS="${3:-300}"  # default 5 min

if [ ! -x "$HDC" ] && [ ! -f "$HDC" ]; then
    echo "ERR: hdc not found at $HDC" >&2
    exit 2
fi

mkdir -p /tmp/v3-hdc-long-probe
TAG=$(basename "$HDC" .exe)
LOG=/tmp/v3-hdc-long-probe/probe-${TAG}-$(date +%s).log

echo "==== START: hdc=$HDC serial=$SERIAL duration=${DURATION_SECS}s ====" | tee "$LOG"
echo "==== hdc version: $("$HDC" -v 2>&1 | head -1) ====" | tee -a "$LOG"
echo "" | tee -a "$LOG"

# Test patterns (cycling). Read-only, no setenforce, no writes.
patterns=(
    'echo SENTINEL_$$_$(date +%N)'
    'echo LARGE: $(printf %1024s | tr " " "X")'
    'ls -laZ /system/lib/libc.so 2>&1'
    'getenforce'
    'ls -laR /system/lib 2>/dev/null | wc -l'
    'mountpoint -q /system; echo EXIT_$?'
    'cat /proc/loadavg'
    'date +%Y-%m-%dT%H:%M:%S'
)

silent=0
hangs=0
total=0
start=$(date +%s)
declare -a latencies

while true; do
    now=$(date +%s)
    elapsed=$((now - start))
    if [ "$elapsed" -ge "$DURATION_SECS" ]; then break; fi

    pattern="${patterns[$((total % ${#patterns[@]}))]}"

    t0=$(date +%s%N)
    out=$(timeout 30 "$HDC" -t "$SERIAL" shell "$pattern" 2>&1)
    rc=$?
    t1=$(date +%s%N)

    latency_ms=$(( (t1 - t0) / 1000000 ))
    total=$((total + 1))
    latencies+=("$latency_ms")

    if [ "$rc" -eq 124 ]; then
        hangs=$((hangs + 1))
        echo "ITER $total HANG: pattern=[$pattern] duration=${latency_ms}ms" >> "$LOG"
    elif [ -z "$out" ] || ! echo "$out" | grep -q '[A-Za-z0-9]'; then
        silent=$((silent + 1))
        echo "ITER $total SILENT: pattern=[$pattern] rc=$rc latency=${latency_ms}ms out=[$out]" >> "$LOG"
    fi

    # Progress every 100 iterations
    if [ $((total % 100)) -eq 0 ]; then
        # rolling p95 of last 100
        recent=$(printf '%s\n' "${latencies[@]: -100}" | sort -n)
        p95_idx=$(( ${#latencies[@]} < 100 ? ${#latencies[@]} : 100 ))
        p95_line=$(( p95_idx * 95 / 100 ))
        [ "$p95_line" -lt 1 ] && p95_line=1
        p95=$(echo "$recent" | sed -n "${p95_line}p")
        echo "[${elapsed}/${DURATION_SECS}s] iter=$total silent=$silent hangs=$hangs p95_recent=${p95}ms" | tee -a "$LOG"
    fi
done

# Final summary
n=${#latencies[@]}
if [ "$n" -eq 0 ]; then
    echo "ERROR: zero iterations completed" | tee -a "$LOG"
    exit 3
fi
all_sorted=$(printf '%s\n' "${latencies[@]}" | sort -n)
median_idx=$(( n / 2 ))
[ "$median_idx" -lt 1 ] && median_idx=1
p95_idx=$(( n * 95 / 100 ))
[ "$p95_idx" -lt 1 ] && p95_idx=1
median=$(echo "$all_sorted" | sed -n "${median_idx}p")
p95=$(echo "$all_sorted" | sed -n "${p95_idx}p")
max=$(echo "$all_sorted" | tail -1)
min=$(echo "$all_sorted" | head -1)

echo "" | tee -a "$LOG"
echo "==== FINAL ====" | tee -a "$LOG"
echo "Total iterations: $total" | tee -a "$LOG"
echo "Silent returns: $silent" | tee -a "$LOG"
echo "Hangs (>30s): $hangs" | tee -a "$LOG"
echo "Latency min: ${min}ms" | tee -a "$LOG"
echo "Latency median: ${median}ms" | tee -a "$LOG"
echo "Latency p95: ${p95}ms" | tee -a "$LOG"
echo "Latency max: ${max}ms" | tee -a "$LOG"
echo "Log: $LOG" | tee -a "$LOG"
