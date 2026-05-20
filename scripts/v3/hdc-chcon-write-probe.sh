#!/usr/bin/env bash
# chcon-WRITE-shaped probe — settle H1 hypothesis that chcon-write triggers
# Channel A death distinct from READ workloads (agent 81 READ probe was clean).
# Target /data/local/tmp/v3-chcon-probe-scratch/ — NEVER /system.

set -uo pipefail

HDC="${1:-/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe}"
SERIAL="${2:-dd011a414436314130101250040eac00}"
DURATION="${3:-300}"

SCRATCH=/data/local/tmp/v3-chcon-probe-scratch
mkdir -p /tmp/v3-chcon-probe
LOG=/tmp/v3-chcon-probe/probe-$(date +%s).log

# chcon WRITE patterns — all target SCRATCH; some will EPERM (relayed clean per
# V3-HDC-3.2.0B-PROBE-REPORT Probe 4); all exercise LSM relabel + audit path.
# Rotate target file across 50 scratch files to avoid xattr-cache effects.
patterns=(
    'chcon u:object_r:shell_data_file:s0 ${SCRATCH}/probe-${i}.txt 2>&1; echo EXIT:$?'
    'chcon u:object_r:data_local_tmp:s0 ${SCRATCH}/probe-${i}.txt 2>&1; echo EXIT:$?'
    'restorecon ${SCRATCH}/probe-${i}.txt 2>&1; echo EXIT:$?'
    'chcon -h u:object_r:shell_data_file:s0 ${SCRATCH}/probe-${i}.txt 2>&1; echo EXIT:$?'
    'ls -Z ${SCRATCH}/probe-${i}.txt 2>&1'
)

silent=0
hangs=0
total=0
eperm=0
ok=0
declare -a latencies
start=$(date +%s)

while true; do
    now=$(date +%s)
    elapsed=$((now - start))
    if [ "$elapsed" -ge "$DURATION" ]; then break; fi

    i=$(( (total % 50) + 1 ))
    raw_pattern="${patterns[$((total % ${#patterns[@]}))]}"
    pattern="${raw_pattern//\$\{SCRATCH\}/$SCRATCH}"
    pattern="${pattern//\$\{i\}/$i}"

    t0=$(date +%s%N)
    out=$(timeout 30 "$HDC" -t "$SERIAL" shell "$pattern" 2>&1)
    rc=$?
    t1=$(date +%s%N)
    latency_ms=$(( (t1 - t0) / 1000000 ))
    total=$((total + 1))
    latencies+=("$latency_ms")

    if [ "$rc" -eq 124 ]; then
        hangs=$((hangs + 1))
        echo "ITER $total HANG: [$pattern] ${latency_ms}ms" >> "$LOG"
    elif [ -z "$out" ] || ! echo "$out" | grep -q '[A-Za-z0-9]'; then
        silent=$((silent + 1))
        echo "ITER $total SILENT: [$pattern] rc=$rc out=[$out]" >> "$LOG"
    fi

    # Track EPERM vs OK distinction (helps separate "kernel denial" from "channel death")
    if echo "$out" | grep -qi 'permission denied\|operation not permitted'; then
        eperm=$((eperm + 1))
    elif echo "$out" | grep -q '^EXIT:0$'; then
        ok=$((ok + 1))
    fi

    # Progress every 100
    if [ $((total % 100)) -eq 0 ]; then
        recent=$(printf '%s\n' "${latencies[@]: -100}" | sort -n)
        p95=$(echo "$recent" | awk -v n=100 'NR==95{print; exit}' || echo n/a)
        echo "[${elapsed}/${DURATION}s] iter=$total silent=$silent hangs=$hangs eperm=$eperm ok=$ok p95_recent=${p95}ms" | tee -a "$LOG"
    fi
done

# Summary
all=$(printf '%s\n' "${latencies[@]}" | sort -n)
n=${#latencies[@]}
median=$(echo "$all" | awk -v n=$n 'NR==int(n/2){print; exit}')
p95=$(echo "$all" | awk -v n=$n 'NR==int(n*0.95){print; exit}')
p99=$(echo "$all" | awk -v n=$n 'NR==int(n*0.99){print; exit}')
max=$(echo "$all" | tail -1)

echo "==== chcon-WRITE PROBE FINAL ====" | tee -a "$LOG"
echo "Duration: ${DURATION}s actual=${elapsed}s" | tee -a "$LOG"
echo "Total iterations: $total" | tee -a "$LOG"
echo "Silent returns: $silent" | tee -a "$LOG"
echo "Hangs (>30s): $hangs" | tee -a "$LOG"
echo "EPERM returns: $eperm" | tee -a "$LOG"
echo "OK returns: $ok" | tee -a "$LOG"
echo "Latency median: ${median}ms" | tee -a "$LOG"
echo "Latency p95: ${p95}ms" | tee -a "$LOG"
echo "Latency p99: ${p99}ms" | tee -a "$LOG"
echo "Latency max: ${max}ms" | tee -a "$LOG"
echo "Log: $LOG"
