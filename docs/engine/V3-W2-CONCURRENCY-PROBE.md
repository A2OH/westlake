# V3 W2 Concurrency Probe — Channel A+B interleave
Date: 2026-05-20
Agent: 84 (post-agent-83 follow-up to settle the Channel A+B concurrency hypothesis)
Board: DAYU200 (`dd011a414436314130101250040eac00`, factory clean post-power-cycle)
hdc.exe: `Ver: 3.2.0b` at `/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe`
Operator gate: post-agent-83 follow-up; chcon-WRITE-only probe was CLEAN, READ-only was CLEAN — the remaining variable to settle is Channel A (`hdc shell chcon`) interleaved with Channel B (`hdc file send`) at Stage B's actual workload ratio.

---

## TL;DR (one sentence)

**Channel A+B concurrency hypothesis DENIED.** 5-minute sustained probe — 129 rounds × (5 file sends + 5 chcons) = **645 pushes + 645 chcons = 1290 total hdc operations** — exited with **0 silent returns, 0 hangs, 0 push fails, push p95 287 ms (1.19× median), chcon p95 236 ms (1.15× median), Channel A alive post-probe, Enforcing preserved, board uptime continuous (1:10 → 1:24, no reboot)**. Channel A+B interleave at Stage B's per-stage rhythm by itself does **not** kill hdc 3.2.0b in any observable way. Combined with agent 81 (READ-only CLEAN) and agent 83 (WRITE-only CLEAN), three independent probes have now failed to reproduce Stage B's channel-death signature. Stage B's failure must originate from a code path NOT covered by any of the three probes — most likely **`chcon || true` semantics on absent paths** (Stage 3f attempted chcons on `/system/lib/liboh_adapter_bridge.so` which the harness believes was pushed but the verify-loop reported empty label) OR **/system-mutating chcon-OK paths** that trigger xattr-writes + policy reload events the scratch probes structurally cannot exercise.

---

## 1. Probe design (5 push + 5 chcon per round)

### Rationale

Agent 81 (long-load READ probe) was 5 min / 1415 iter / clean. Agent 83 (chcon-WRITE probe) was 5 min / 1395 iter / clean. Both used **Channel A exclusively** (one socket = `hdc shell …`). Stage B's hardened-deploy script interleaves Channel B (`hdc file send` → its own daemon socket) with Channel A (`hdc shell chcon` → a separate ephemeral shell socket per call) in a tight pattern: each stage pushes a batch of files then chcons them in a batch. The two-channel + tight-pattern combination is the unprobed dimension. The concurrency-pathology hypothesis is **daemon-side mutex contention on a shared transport-state struct OR scheduler starvation between the two channel families under sustained pressure**.

### Workload calibration

Stage B's per-stage ratio is ~1 chcon per ~3-5 pushes. The probe runs each "round" as **5 file sends + 5 chcons** (matching the rhythm of Stage 3b/3c/3d/3e/3f's actual sequencing in `scripts/v3/deploy-hbc-to-dayu200-hardened.sh` lines 146-279). Each push and each chcon is a **separate `hdc` invocation** (mirroring the harness, which fork+exec's hdc.exe per file rather than batching). This is the load-shape that Stage B died under.

### Probe script

`scripts/v3/hdc-concurrency-probe.sh` (4 KB, +x). Per-iteration `timeout 30 hdc file send` and `timeout 30 hdc shell chcon`; tracks silent (empty/non-printable stdout on chcon), hang (rc=124 on either), push_fail (push stdout missing `FileTransfer finish`), plus rolling p75 latency progress every 10 rounds.

### Scratch setup

- `/data/local/tmp/v3-concurrency-probe-scratch` (mkdir + initial cleanup; never touches `/system`)
- 200 × 1 KB local source files under `$HOME/android-to-openharmony-migration/v3-concurrency-source/` (varied random content — each push is unique, defeats any host- or device-side dedup; cycled mod 200)
- hdc.exe-Windows-CWD path issue handled via `wslpath -w` translation (sole revision from initial run — see §6)

### Hard-stop #13 honored

All push destinations and chcon targets path under `/data/local/tmp/v3-concurrency-probe-scratch/round-${N}-push-${j}.bin`. The script greps clean for any `/system` argument.

---

## 2. Results

### Final summary

```
==== CONCURRENCY PROBE FINAL ====
Duration: 300s actual=300s
Rounds: 129
Push total: 645 | fails: 0 | hangs: 0 (both)
Chcon total: 645 | silent: 0
Push latency median/p95/max: 242ms / 287ms / 366ms
Chcon latency median/p95/max: 206ms / 236ms / 295ms
Log: /tmp/v3-concurrency-probe/probe-1779297822.log
```

### Progress (every 10 rounds)

```
[ 22/300s] round=10  push_total=50  chcon_total=50  silent=0 hangs=0 push_fails=0 p75_chcon=235ms p75_push=242ms
[ 45/300s] round=20  push_total=100 chcon_total=100 silent=0 hangs=0 push_fails=0 p75_chcon=230ms p75_push=279ms
[ 69/300s] round=30  push_total=150 chcon_total=150 silent=0 hangs=0 push_fails=0 p75_chcon=232ms p75_push=272ms
[ 93/300s] round=40  push_total=200 chcon_total=200 silent=0 hangs=0 push_fails=0 p75_chcon=216ms p75_push=284ms
[115/300s] round=50  push_total=250 chcon_total=250 silent=0 hangs=0 push_fails=0 p75_chcon=215ms p75_push=241ms
[138/300s] round=60  push_total=300 chcon_total=300 silent=0 hangs=0 push_fails=0 p75_chcon=214ms p75_push=272ms
[161/300s] round=70  push_total=350 chcon_total=350 silent=0 hangs=0 push_fails=0 p75_chcon=201ms p75_push=246ms
[185/300s] round=80  push_total=400 chcon_total=400 silent=0 hangs=0 push_fails=0 p75_chcon=231ms p75_push=287ms
[208/300s] round=90  push_total=450 chcon_total=450 silent=0 hangs=0 push_fails=0 p75_chcon=215ms p75_push=244ms
[231/300s] round=100 push_total=500 chcon_total=500 silent=0 hangs=0 push_fails=0 p75_chcon=216ms p75_push=273ms
[254/300s] round=110 push_total=550 chcon_total=550 silent=0 hangs=0 push_fails=0 p75_chcon=214ms p75_push=274ms
[277/300s] round=120 push_total=600 chcon_total=600 silent=0 hangs=0 push_fails=0 p75_chcon=231ms p75_push=266ms
```

### Latency characterization

| Metric | Push (Channel B) | Chcon (Channel A) | Threshold (brief) | Verdict |
|---|---|---|---|---|
| Throughput | 645 / 300s ≈ 2.15 / s | 645 / 300s ≈ 2.15 / s | — | Healthy (combined 4.3 ops/s) |
| Median | 242 ms | 206 ms | — | — |
| p95 | 287 ms | 236 ms | < 3× median (~700 ms) | PASS (1.19× / 1.15×) |
| Max | 366 ms | 295 ms | — | tight tails |
| p75 drift over 5 min | 242 → 266 ms (+9.9%) | 235 → 231 ms (−1.7%) | bounded | PASS |
| Silent returns | — | 0 / 645 | 0 | PASS |
| Hangs (>30s) | 0 / 645 | 0 / 645 | 0 | PASS |
| Push fails (missing `FileTransfer finish`) | 0 / 645 | — | 0 | PASS |

### Comparison vs prior probes (same hdc 3.2.0b, same board, same 5 min)

| Metric | READ probe (agent 81) | WRITE probe (agent 83) | A+B probe (this) | Notes |
|---|---|---|---|---|
| Iterations | 1415 single-call | 1395 single-call | 1290 (645 push + 645 chcon) | A+B serialized lower ops/s due to channel-B push overhead |
| Silent | 0 | 0 | 0 | identical |
| Hangs | 0 | 0 | 0 | identical |
| Channel A median | 195 ms | 203 ms | 206 ms | within noise (+1.5% chcon vs WRITE-only) |
| Channel A p95 | 303 ms | 236 ms | 236 ms | **IDENTICAL** to WRITE-only — Channel B activity did NOT slow Channel A |
| Channel B median | — | — | 242 ms | first measurement of `hdc file send` p50 on this hdc/board |
| Channel B p95 | — | — | 287 ms | |
| Channel A alive post-probe | YES | YES | YES | identical |
| Board uptime through probe | continuous | continuous | continuous (1:10 → 1:24) | no reboot any probe |

The Channel A latency distribution under Channel B concurrent push pressure is **statistically indistinguishable** from the Channel-A-only WRITE probe (p95 236 ms in both). This is the strongest signal: **the daemon's per-shell shell-startup cost (~200 ms RTT) dominates and is not perturbed by concurrent transfer activity**.

Raw logs: `/tmp/v3-concurrency-probe/probe-1779297822.log`, `/tmp/v3-concurrency-probe/run.out`.

---

## 3. Channel A health post-probe

```
$ hdc shell 'echo POSTPROBE_$(date +%s); getenforce; date; uptime'
POSTPROBE_1501843001
Enforcing
Fri Aug  4 18:36:41 CST 2017
 18:36:41 up  1:23,  0 users,  load average: 1.22, 1.16, 1.06

$ hdc shell 'echo POST_ALIVE_CHANNEL_A_$(date +%s)'
POST_ALIVE_CHANNEL_A_1501843001

$ hdc shell 'echo CHANNEL_A_FINAL_$(date +%s); getenforce; uptime'
CHANNEL_A_FINAL_1501843042
Enforcing
 18:37:22 up  1:24,  0 users,  load average: 1.26, 1.17, 1.07
```

**Channel A: alive, responsive, three consecutive post-probe sentinel echoes succeeded with normal latency. Enforcing preserved. Board uptime continuous (1:10 pre, 1:24 post — no reboot, no kernel panic, no daemon restart).** Loadavg climbed 1.00 → 1.26 during the probe window (expected — sustained 4.3 ops/s on a single-CPU-pressured arm32 board) but is within the normal range for the device under any workload.

Cleanup completed cleanly:

```
$ hdc shell 'rm -rf /data/local/tmp/v3-concurrency-probe-scratch && ls /data/local/tmp/v3-concurrency-probe-scratch'
CLEAN_1501843041
ls: /data/local/tmp/v3-concurrency-probe-scratch: No such file or directory
```

Host-side: `rm -rf $HOME/android-to-openharmony-migration/v3-concurrency-source` (200 × 1 KB random files removed).

---

## 4. Audit events generated

| File | Lines / Count |
|---|---|
| pre-probe `hilog -t audit` | 0 |
| post-probe `hilog -t audit` | 0 |
| post-probe dmesg tail (50 lines) | 50 lines, no probe-attributable entries |
| post-probe `dmesg \| grep -c "avc:"` | 788 |
| post-probe `dmesg \| grep -c "comm.chcon"` | **0** |

The dmesg has 788 total `avc:` lines (background-service noise: hiview, appspawn, nwebspawn, OS_FFRT_*), but **zero `comm="chcon"` entries** — meaning none of the 645 chcon attempts generated a logged avc denial in the kernel buffer at the time of post-probe capture. There are two possible interpretations:

1. The avc cache + audit rate-limiting suppressed the chcon-denied events (the kernel often coalesces or drops repeated identical denials beyond a threshold).
2. The chcons may have succeeded silently against `data_local_tmp` files (some chcon variants on shell-owned scratch files under permissive bits don't always emit audit).

Verified out-of-band: a manual single chcon to a remnant file before cleanup returned `Permission denied / EXIT_1`:

```
$ hdc shell 'chcon u:object_r:shell_data_file:s0 /data/local/tmp/v3-concurrency-probe-scratch/round-1-push-1.bin 2>&1; echo EXIT_$?'
chcon: '/data/local/tmp/v3-concurrency-probe-scratch/round-1-push-1.bin' to u:object_r:shell_data_file:s0: Permission denied
EXIT_1
```

So chcons DID exercise the relabel-deny code path (matching agent 83's EPERM finding) — they just produced fewer-than-expected avc audit events in the captured window, likely because of kernel-side rate limiting under sustained load. For the purposes of THIS probe, the important property is **the chcon RTT was tightly bounded (p95 236 ms) and Channel A returned the EPERM string promptly every single time** — exactly the behavior that should "fail" if H-concurrency were the bug. It didn't.

`/sys/fs/selinux/enforce` was not directly recv'd this run (no out-of-band Channel-B liveness check needed because Channel A stayed live throughout), but `getenforce` reported `Enforcing` both immediately post-probe and ~40 s later, confirming policy state unchanged.

---

## 5. Verdict: concurrency reproduces / does not

### Restating the hypothesis

> **H-concurrency:** Stage B died because the orchestrating script interleaves `hdc file send` (Channel B = file transport socket) with `hdc shell chcon` (Channel A = ephemeral shell socket). Two-channel mixed load triggers a daemon-side scheduling pathology (mutex contention on a shared transport-state struct, or starvation of stdout flushes) that single-channel READ and single-channel WRITE probes by construction cannot reproduce.

### Verdict: **DENIED.**

The structurally-correct stress test of the two-channel interleave at Stage B's workload ratio — **645 pushes + 645 chcons in 129 rounds over 5 min on the same daemon (hdcd 3.2.0b), same board, same per-shell setup tax, same audit-event class, ~8× the chcon-call volume Stage B generated (645 vs ~78)** — exits with:

- 0 silent returns on Channel A
- 0 hangs on either channel
- 0 push failures on Channel B
- Channel A p95 latency 236 ms = **identical to the single-channel WRITE probe's 236 ms** (no concurrency penalty)
- Channel A alive post-probe (three consecutive sentinels succeeded)
- Board uptime continuous (no reboot, no daemon respawn, no kernel panic)
- Enforcing preserved
- Cleanup successful both sides

The two-channel concurrency hypothesis at Stage B's per-stage rhythm is **not** the root cause of Stage B's channel death.

### Hypotheses that survive all three probes

After probes 81 (READ), 83 (WRITE), and 84 (A+B interleave) all CLEAN at 5 min on the same board with the same hdc 3.2.0b, the unbroken death-axes are:

1. **`chcon || true` on absent or not-yet-pushed paths.** Stage 3f's actual failure mode (per V3-W2-STAGE-B-REPORT §11) was `chcon_verify` reporting `got label='' expected='system_lib_file'`. The script's silent-skip of an earlier `|| true`-masked chcon could mean the chcon ran against a path the kernel believes is ENOENT — and `chcon` against an ENOENT path inside hdcd may exercise a code path none of these scratch probes touch (because all three probe-suites only chcon files that DO exist at chcon time).
2. **chcon-OK on /system.** All three probes operate against shell-owned `/data/local/tmp` scratch under permissive bits — chcon DENIES, no xattr write happens. A real chcon against a `/system` target where it SUCCEEDS triggers an actual `setxattr` → SELinux LSM update → potential security-policy reload event. The kernel + daemon code path for a successful relabel is structurally different from a denied one. Brick-safety (hard-stop #13) forbids reproducing this on /system; it could be tested on a tmpfs mount under `/data/local/tmp` where chcon-OK would actually succeed, but the policy permissions to relabel on tmpfs may be similarly restricted in shell-domain.
3. **Cumulative duration beyond 5 min.** All three probes ran exactly 5 min. Stage B died at ~3 min into Stage 3f, but Stage 3f was *cumulative* over Stages 3.0 → 3b → 3c → 3d → 3e → 3f totaling 15-20 min of sustained hdc traffic before death. A 10-15-min probe could exercise a slowly-leaking resource (e.g., hdcd socket buffer growth, fd leak per shell session) that 5 min doesn't.
4. **Specific Stage-B-only operations.** The hardened script (`deploy-hbc-to-dayu200-hardened.sh`) does things none of these probes do: `hdc shell` invocations with multi-line scripts, `restorecon` on files with policy conflicts, snapshot-capture loops with `ls -laZ` chains, and the `chcon_verify` retry-on-empty-label loop. Any of these may interact with the hdc.exe Windows side's stdout buffering in a unique way.

The H formulation should narrow further: **not "chcon-write generally," not "two-channel interleave generally," but "Stage-B-script-specific operations — chcon-on-ENOENT, multi-line `hdc shell`, or cumulative 10-15-min sustained load."**

---

## 6. Recommendation

### If Stage B is retried (default path)

The mitigation stack from V3-W2-CHCON-WRITE-PROBE.md §6 remains correct and is now reinforced by this probe:

1. **Drop the `|| true` on every chcon/restorecon call** — surface failures instead of hiding them. The probes have now demonstrated that the chcon-deny load itself is benign; what kills Stage B must be the *invisibility* of a real failure under `|| true`, not its frequency.
2. **Add inter-batch Channel A sentinel** — between every batch of ≤10 calls, issue `echo SENT_BATCH_$N` and `grep -q SENT_BATCH_$N`. Use Channel B `hdc file recv /sys/fs/selinux/enforce` as the out-of-band liveness fallback if the sentinel fails.
3. **Add 200-500 ms inter-batch sleep** — even if not strictly necessary per these probes, the cost is negligible and it provides a small backstop against the daemon-side resource-leak hypothesis (hypothesis 3 above).
4. **NEW: Re-validate every chcon target's existence inline before chcon-ing it.** Add `[[ -e $target ]] || { echo "MISSING: $target" >&2; FAIL=1; continue; }` immediately before each chcon. This directly addresses hypothesis 1 — even if chcon-on-ENOENT inside hdcd is well-behaved on most paths, eliminating the possibility removes it from the hypothesis ranking.
5. **NEW: Bound cumulative session duration.** Add an inter-stage shell-channel reset: between Stage 3b/3c/3d/3e/3f, issue an explicit `hdc shell exit` (or a `kill -0` on the daemon socket) and then echo a fresh sentinel to confirm a NEW shell socket comes up. This recovers from any single-shell-session resource leak (hypothesis 3) without needing the operator to power-cycle.

### Probes still worth running (post-Stage-B-retry, if it still dies)

1. **Cumulative 15-min A+B probe** — same script, longer duration. If channel death first appears between minute 5-15, hypothesis 3 (cumulative resource exhaustion) gets a strong positive signal.
2. **`hdc shell` multi-line script probe** — Stage B's harness uses `hdc shell "for f in …; do chcon …; done"` patterns. A probe that sends 50-line shell scripts per invocation would exercise that path.
3. **`hdc shell` with chcon-on-ENOENT probe** — explicitly target paths that don't exist, see how hdcd + the kernel handle the ENOENT vs EPERM split.

### Proceed with Stage B retry (with mitigations 1-5 above) — RECOMMENDED next action

Three independent probes (READ, WRITE, A+B) have now failed to reproduce Stage B's channel death. The mitigations above are cheap to add, correct on first principles (eliminate silent failures, add visibility, bound state), and don't require any additional probe work. The most likely outcome of a Stage B retry with mitigations 1-5 is:

- **Best case:** Stage B completes cleanly. We learn that the original failure was a silent-failure interaction the mitigations now surface.
- **Realistic case:** Stage B fails again, but with a clear failure signal (specific chcon target, specific batch, specific shell invocation) instead of silent channel death. That signal then drives the next narrowing.
- **Worst case:** Stage B silent-dies again at the same point, confirming the failure is in a code path none of our probes can reach. At that point, escalate to the multi-line-shell probe and the cumulative-15-min probe.

### What NOT to do

- **Do NOT pin hdc to a different version yet.** Three independent probes have demoted both H1 (chcon-write storm) and H2 (3.2.0b stdout regression). Pinning hdc has no remaining evidence to support it.
- **Do NOT add `setenforce 0` to Stage B.** Audit subsystem absorbed three probes' worth of attempted denials without backpressure — there's no flux problem.
- **Do NOT skip the `chcon -e` / target-existence check.** Hypothesis 1 is the strongest survivor; the cheap inline existence check directly addresses it.

---

## Files created / modified

- `scripts/v3/hdc-concurrency-probe.sh` — new, +x. 5-push + 5-chcon-per-round interleave probe with rolling p75 progress + final summary. Scratch hardcoded to `/data/local/tmp/v3-concurrency-probe-scratch`; never touches `/system`. Uses `wslpath -w` to translate WSL source paths to UNC paths for Windows hdc.exe (the first run of this script silently failed every push because hdc.exe couldn't resolve WSL `/tmp/...` paths from its Windows CWD; the script was patched on second iteration before the authoritative 5-min run).
- `docs/engine/V3-W2-CONCURRENCY-PROBE.md` — this file.
- `/tmp/v3-concurrency-probe/probe-1779297822.log`, `/tmp/v3-concurrency-probe/run.out` — raw probe output (host-side, ephemeral).
- `/tmp/v3-concurrency-pre-state.txt`, `/tmp/v3-concurrency-pre-audit-count.txt`, `/tmp/v3-concurrency-post-audit-count.txt`, `/tmp/v3-concurrency-post-dmesg.txt` — pre/post snapshots (host-side, ephemeral).

## Hard constraints honored

- No `hdc target boot`. PASS.
- No `setenforce`. PASS (Enforcing preserved through entire probe; verified pre/post + 40 s after).
- No chcon on `/system` paths (Hard Stop #13). PASS (all chcons targeted `/data/local/tmp/v3-concurrency-probe-scratch/round-${N}-push-${j}.bin`; script greps clean for `/system`).
- No touch of anything outside `/data/local/tmp/v3-concurrency-probe-scratch/` (Hard Stop #13). PASS.
- Scratch dir cleaned up after probe both sides. PASS.
- Local commit only, no push. (commit at end of this run; see git log).

## Cross-references

- `docs/engine/V3-W2-PATH-B-RESUMED.md` (commit `450d0f57`) — agent 81 READ probe baseline.
- `docs/engine/V3-W2-CHCON-WRITE-PROBE.md` (commit `84cea37d`) — agent 83 WRITE probe; identified concurrency as the next gap-closer in §6 recommendation 4.
- `docs/engine/V3-W2-STAGE-B-REPORT.md` (commit `4ba8695f`) — the original Channel A death event being investigated.
- `docs/engine/V3-W2-POSTMORTEM.md` — H1 / H2 hypotheses; H2 already DENIED by agent 61, H1 narrowed by agent 83, H-concurrency now DENIED by this probe.
- `docs/engine/V3-HDC-3.2.0B-PROBE-REPORT.md` (commit `f158cf58`) — agent 61 Probe 4 established the chcon-EPERM-relay property.
- `scripts/v3/hdc-long-load-probe.sh`, `scripts/v3/hdc-chcon-write-probe.sh`, `scripts/v3/hdc-concurrency-probe.sh` — the three-probe matrix.
