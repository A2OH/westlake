#!/usr/bin/env bash
# Concurrency probe: Channel A (chcon via hdc shell) interleaved with
# Channel B (hdc file send) at Stage B's workload ratio.
# Each round = 5 pushes then 5 chcons.
# Targets /data/local/tmp/v3-concurrency-probe-scratch — NEVER /system.

set -uo pipefail

HDC="${1:-/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe}"
SERIAL="${2:-dd011a414436314130101250040eac00}"
DURATION="${3:-300}"

SCRATCH=/data/local/tmp/v3-concurrency-probe-scratch
SRC=$HOME/android-to-openharmony-migration/v3-concurrency-source
mkdir -p /tmp/v3-concurrency-probe
LOG=/tmp/v3-concurrency-probe/probe-$(date +%s).log

# hdc.exe is a Windows binary — translate WSL paths to UNC paths
to_win_path() {
    wslpath -w "$1"
}

PUSH_PER_ROUND=5
CHCON_PER_ROUND=5

silent=0
hangs=0
push_fails=0
chcon_total=0
push_total=0
declare -a chcon_latencies
declare -a push_latencies
start=$(date +%s)
round=0

while true; do
    now=$(date +%s)
    elapsed=$((now - start))
    if [ "$elapsed" -ge "$DURATION" ]; then break; fi
    round=$((round + 1))

    # Channel B phase: 5 pushes
    for j in 1 2 3 4 5; do
        idx=$(( (push_total % 200) + 1 ))
        push_total=$((push_total + 1))
        local_src="$SRC/src-$idx.bin"
        local_src_win=$(to_win_path "$local_src")
        dev_dst="$SCRATCH/round-$round-push-$j.bin"
        t0=$(date +%s%N)
        out=$(timeout 30 "$HDC" -t "$SERIAL" file send </dev/null "$local_src_win" "$dev_dst" 2>&1)
        rc=$?
        t1=$(date +%s%N)
        latency=$(( (t1 - t0) / 1000000 ))
        push_latencies+=("$latency")
        if [ "$rc" -eq 124 ]; then
            hangs=$((hangs + 1))
            echo "PUSH HANG round=$round j=$j duration=${latency}ms" >> "$LOG"
        elif ! echo "$out" | grep -q 'FileTransfer finish'; then
            push_fails=$((push_fails + 1))
            echo "PUSH FAIL round=$round j=$j out=[$out]" >> "$LOG"
        fi
    done

    # Channel A phase: 5 chcons (most will EPERM per agent 83; still exercises path)
    for j in 1 2 3 4 5; do
        chcon_total=$((chcon_total + 1))
        target="$SCRATCH/round-$round-push-$j.bin"
        t0=$(date +%s%N)
        out=$(timeout 30 "$HDC" -t "$SERIAL" shell "chcon u:object_r:shell_data_file:s0 $target 2>&1; echo EXIT_\$?" 2>&1)
        rc=$?
        t1=$(date +%s%N)
        latency=$(( (t1 - t0) / 1000000 ))
        chcon_latencies+=("$latency")
        if [ "$rc" -eq 124 ]; then
            hangs=$((hangs + 1))
            echo "CHCON HANG round=$round j=$j duration=${latency}ms" >> "$LOG"
        elif [ -z "$out" ] || ! echo "$out" | grep -q '[A-Za-z0-9]'; then
            silent=$((silent + 1))
            echo "CHCON SILENT round=$round j=$j rc=$rc out=[$out]" >> "$LOG"
        fi
    done

    # Progress every 10 rounds
    if [ $((round % 10)) -eq 0 ]; then
        c_recent=$(printf '%s\n' "${chcon_latencies[@]: -10}" | sort -n | tail -3 | head -1)
        p_recent=$(printf '%s\n' "${push_latencies[@]: -10}" | sort -n | tail -3 | head -1)
        echo "[${elapsed}/${DURATION}s] round=$round push_total=$push_total chcon_total=$chcon_total silent=$silent hangs=$hangs push_fails=$push_fails p75_chcon=${c_recent}ms p75_push=${p_recent}ms" | tee -a "$LOG"
    fi
done

# Summary
csort=$(printf '%s\n' "${chcon_latencies[@]}" | sort -n)
cn=${#chcon_latencies[@]}
cmedian=$(echo "$csort" | awk -v n=$cn 'NR==int(n/2){print; exit}')
cp95=$(echo "$csort" | awk -v n=$cn 'NR==int(n*0.95){print; exit}')
cmax=$(echo "$csort" | tail -1)

psort=$(printf '%s\n' "${push_latencies[@]}" | sort -n)
pn=${#push_latencies[@]}
pmedian=$(echo "$psort" | awk -v n=$pn 'NR==int(n/2){print; exit}')
pp95=$(echo "$psort" | awk -v n=$pn 'NR==int(n*0.95){print; exit}')
pmax=$(echo "$psort" | tail -1)

echo "==== CONCURRENCY PROBE FINAL ====" | tee -a "$LOG"
echo "Duration: ${DURATION}s actual=${elapsed}s" | tee -a "$LOG"
echo "Rounds: $round" | tee -a "$LOG"
echo "Push total: $push_total | fails: $push_fails | hangs: $hangs (both)" | tee -a "$LOG"
echo "Chcon total: $chcon_total | silent: $silent" | tee -a "$LOG"
echo "Push latency median/p95/max: ${pmedian}ms / ${pp95}ms / ${pmax}ms" | tee -a "$LOG"
echo "Chcon latency median/p95/max: ${cmedian}ms / ${cp95}ms / ${cmax}ms" | tee -a "$LOG"
echo "Log: $LOG"
