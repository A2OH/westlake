# V3 W2 Path B Resumed — HBC hdc fetch + long-load probe

Date: 2026-05-20
Agent: 81 (resuming Path B Steps 2-3 after agent 79 BLOCKED on Step 1)
Board: DAYU200 (dd011a414436314130101250040eac00, post-power-cycle, /system factory-equivalent per agent 79's verification)
Operator gate: User-approved Path B (Steps 2-3 only) after agent 79's Step-1 STOP. Steps 2-3 are independent of snapshot state.

---

## TL;DR

- **Step 2 (HBC hdc.exe fetch): STOP triggered per Hard-Stop #13.** Exhaustive search of HBC server (`[REDACTED]@[REDACTED-IP]:[REDACTED-PORT]`) yielded **zero hdc.exe Windows binaries**. The HBC tree builds only Linux hdc (ELF x86-64); the mingw_x86_64 SDK output does not include hdc; the D200 "burn tools" Windows dir contains only Rockchip RKDevTool.exe. No "known-good 1.3.0c/d/e" pinned source is available there. Brief's premise wrong on this point — recorded as fact, no fabrication.
- **Step 3 (long-load probe) EXECUTED on hdc 3.2.0b alone.** 5-minute sustained probe (~1415 iterations, 8 rotating read-only call patterns) on the current `/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe`. **Result: 0 silent returns, 0 hangs, latency p95 303 ms (1.55× median, well under brief's 3× threshold), Channel A alive post-probe, Enforcing preserved.** No regression reproduced.
- **Verdict:** hdc 3.2.0b is **NOT** intrinsically broken under sustained 5-min Channel-A load (negative reproduction). This shifts the postmortem hypothesis ranking: H1 ("silent-chcon SELinux respawn storm") is now stronger than H2 ("hdc 3.2.0b stdout-channel regression"). The probe used `ls -laZ` (label-READ) only — not `chcon` (label-WRITE) — so it does NOT rule out a chcon-write-specific failure path. Recommended next: a chcon-shaped probe against `/data/local/tmp/` (write path exercised without mutating /system).
- **Net empirical contribution:** objective single-binary baseline for hdc 3.2.0b under sustained Channel-A read load. Sufficient to demote the "pinning" hypothesis and re-aim the next diagnostic at the chcon-write path. Comparison-vs-pinned remains unanswerable from HBC; would require an OHOS release SDK download from a non-HBC source.

---

## 1. HBC search results (paths + versions found)

### Search outcome: EMPTY for hdc.exe

| Path searched | Outcome |
|---|---|
| `find $HOME -name "hdc.exe"` | 0 hits |
| `find / -name "hdc.exe"` (full filesystem) | 0 hits |
| `$HOME/D200/HiHope_DAYU200/烧写工具及指南/windows/` | RKDevTool.exe (Rockchip flasher) + DriverAssistant_v5.1.1.zip — **no hdc.exe** |
| `$HOME/oh/out/sdk/mingw_x86_64/` | exists, contains no hdc |
| `$HOME/oh/out/sdk/sdk-native/os-specific/windows/` | only `build-tools` + `llvm` — no hdc |
| `$HOME/oh/prebuilts/ohos-sdk/` | only `linux/`, no `windows/` |
| `$HOME/oh/prebuilts/tool/command-line-tools/bin/` | `hvigorw`, `ohpm` only — no hdc |
| `$HOME/oh/prebuilts/build-tools/windows-x86/` | only `bin/` with non-hdc tools |

### Linux hdc binaries found (informational only — not the .exe we need)

| Path | Size | sha256 | Version (probe pending) |
|---|---|---|---|
| `$HOME/oh/out/sdk/clang_x64/developtools/hdc/hdc` | 6161824 | `5ddf68c67cc6d45dd4d0b426062dc950b754203c459e02f8e1e85dfcd615bbcc` | unknown (ELF Linux, irrelevant to Windows-hdc regression) |
| `$HOME/oh/prebuilts/ohos-sdk/linux/24/toolchains/hdc` | 6161824 | same `5ddf68c…` | same |

(Both Linux hdcs are identical — same hash. SDK API level 24.)

### Implication

Per brief's **Hard Stop #13**: "HBC server unreachable or hdc.exe not findable — STOP + report; don't fabricate 'known-good' without proof." Triggered.

If a pinned Windows hdc.exe is required, it must be sourced from:
- Huawei Cloud OHOS SDK release bundles, OR
- OpenHarmony GitHub Releases mirror (`OpenHarmony/developtools_hdc` or release tags of the full SDK), OR
- A different developer machine where the Windows OHOS SDK was previously installed.

None of these are accessible from this agent's context. Documented at `tools/hdc-candidates/README.md` for the next agent to act on.

## 2. Pulled binaries (paths + sha256 + version strings)

**Not applicable** — Step 2 stopped per Hard Stop #13. No binaries pulled.

## 3. Long-load probe — 3.2.0b

Script: `scripts/v3/hdc-long-load-probe.sh` (created this run, +x, READ-ONLY, no setenforce, no writes to /system or /data/local/tmp).
hdc under test: `/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe` (Ver: 3.2.0b)
Board: DAYU200, serial `dd011a414436314130101250040eac00`
Duration: 300s (5 min)
Pre-flight: 30s sanity check first — 136 iter, 0 silent, 0 hangs, p95 302ms, max 331ms. Channel A confirmed alive.

### Patterns cycled (read-only)

```
echo SENTINEL_$$_$(date +%N)            # small echo
echo LARGE: $(printf %1024s | tr ' ' X) # 1KB echo
ls -laZ /system/lib/libc.so 2>&1        # SELinux label query (chcon-shaped read)
getenforce                              # mode query
ls -laR /system/lib | wc -l             # large recursive listing (~1MB output)
mountpoint -q /system; echo EXIT_$?     # quick syscall
cat /proc/loadavg                       # tiny kernel read
date +%Y-%m-%dT%H:%M:%S                 # tiny syscall
```

### Results

```
==== START: hdc=/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe serial=dd011a414436314130101250040eac00 duration=300s ====
==== hdc version: Ver: 3.2.0b ====

[21/300s]  iter=100  silent=0 hangs=0 p95_recent=293ms
[42/300s]  iter=200  silent=0 hangs=0 p95_recent=300ms
[62/300s]  iter=300  silent=0 hangs=0 p95_recent=303ms
[83/300s]  iter=400  silent=0 hangs=0 p95_recent=294ms
[104/300s] iter=500  silent=0 hangs=0 p95_recent=294ms
[124/300s] iter=600  silent=0 hangs=0 p95_recent=297ms
[145/300s] iter=700  silent=0 hangs=0 p95_recent=299ms
[165/300s] iter=800  silent=0 hangs=0 p95_recent=295ms
[187/300s] iter=900  silent=0 hangs=0 p95_recent=312ms
[209/300s] iter=1000 silent=0 hangs=0 p95_recent=308ms
[231/300s] iter=1100 silent=0 hangs=0 p95_recent=302ms
[253/300s] iter=1200 silent=0 hangs=0 p95_recent=303ms
[275/300s] iter=1300 silent=0 hangs=0 p95_recent=307ms
[296/300s] iter=1400 silent=0 hangs=0 p95_recent=306ms

==== FINAL ====
Total iterations: 1415
Silent returns: 0
Hangs (>30s): 0
Latency min:    132ms
Latency median: 195ms
Latency p95:    303ms
Latency max:    354ms
```

Per-iteration anomaly log: **zero `SILENT` or `HANG` lines** (file `/tmp/v3-hdc-long-probe/probe-hdc-1779294038.log` contains only the periodic progress lines and the final summary — no anomaly entries were written).

Post-probe channel-health check:
```
$ hdc -t dd011a414436314130101250040eac00 shell 'echo POST_PROBE_$(date +%s)'
POST_PROBE_1779294375
$ hdc -t dd011a414436314130101250040eac00 shell 'getenforce'
Enforcing
```

Channel A remained alive end-to-end. Enforcing preserved. No soft-brick triggered by 5 minutes of sustained varied load. Board did not require any recovery.

### Stability characterization

| Metric | Value | Interpretation |
|---|---|---|
| Throughput | 1415 calls / 297s ≈ 4.76 calls/s | Healthy sustained Channel-A throughput |
| Latency span | 132 – 354 ms (2.68× ratio) | Tight; no tail blow-up |
| p95 / median | 303 / 195 = 1.55× | Well below brief's "<3×" PASS threshold for "no regression" |
| p95 drift over 5 min | 293 → 306 ms (+4.4%) | Stable; no monotonic creep |
| Silent returns | 0 / 1415 | No "empty stdout + rc 0" bug observed |
| Hangs (>30s) | 0 / 1415 | No call exceeded its 30s budget |
| Channel death | None (post-probe check passed) | Channel A is not cumulatively degraded by this load |

Log: `/tmp/v3-hdc-long-probe/3.2.0b.tee` + per-iteration anomaly log at `/tmp/v3-hdc-long-probe/probe-hdc-1779294038.log`.

## 4. Long-load probe — pinned 1.3.0c (or whichever found)

**Not applicable** — no pinned hdc.exe available (see §1, Hard Stop #13).

Future agent: once a pinned hdc.exe is sourced from an OHOS release SDK bundle, re-run `scripts/v3/hdc-long-load-probe.sh <pinned-hdc-path> <serial> 300` and paste results here. Comparison criteria from brief:
- 1.3.0 silent returns: 0 across 300s
- 1.3.0 hangs: 0 across 300s
- 1.3.0 latency p95: < 3× median
- AND 3.2.0b reproduces regression in §3 (silent/hangs > 0 OR p95 >> median)

## 5. Verdict

**hdc 3.2.0b does NOT exhibit the Channel-A regression under this 5-minute sustained-load probe.** 1415 iterations of mixed small/large/listing/syscall calls completed with 0 silent returns, 0 hangs, and a tight 132-354 ms latency band. Channel A was alive immediately after the probe completed. Enforcing was preserved end-to-end.

This is a **negative reproduction** of Stage B's "channel-death-after-~3-min-of-chcon-work" symptom under a structurally similar continuous load pattern. Interpreted strictly: this probe does NOT confirm the brief's premise that "3.2.0b regression under load is real" — it provides evidence in the opposite direction.

But before declaring "3.2.0b is fine" we must note **what this probe does NOT cover** vs. what Stage B did:
1. **`hdc shell chcon …` (write to SELinux-attribute APIs)** — Stage B's actual workload. This probe used `ls -laZ` (label-READ) only, since Hard Constraint #2 forbids `setenforce` and Hard Constraint #3 forbids writes to /system. chcon is a *write* via the security_xattr API and may trigger a different code path on the daemon side than `ls -laZ`. **This is the leading remaining hypothesis** for why Stage B died and this probe didn't: load shape mismatch, not load duration mismatch.
2. **Parallel `hdc file recv` running on Channel B** — Stage B observed Channel B (file recv) continuing to work *while* Channel A (shell) went silent. This probe used only Channel A. A two-channel concurrency stress is a different probe.
3. **chcon batch size + `|| true` semantics** — postmortem H1 fingered "silent-chcon || true" potentially generating SELinux audit storms that respawn the daemon. None of those code paths exercised here.

So: **this probe rules out "any 5-min sustained Channel-A load kills 3.2.0b"** but does NOT rule out **"sustained chcon-write load on Channel A specifically kills 3.2.0b"**. The right next probe — once a safe `chcon`-using load can be designed that doesn't actually mutate /system (e.g., chcon to the file's current label on a tmpfs scratch dir, or chcon round-trip on /data/local/tmp/probe-scratch/) — would close that gap.

## 6. Recommendation

**Ordered recommendation for the next agent:**

1. **Re-scope the diagnostic.** Don't treat hdc-version-pinning as the most-likely cause yet. The 1415-iter clean run shifts probability mass toward "chcon-write workload specifically" rather than "any sustained 3.2.0b Channel A". Postmortem hypothesis ranking should be updated: H1 (silent-chcon SELinux respawn storm) is now stronger than H2 (hdc-stdout-channel regression).

2. **Design a chcon-shaped probe that doesn't mutate /system.** Probe target: `/data/local/tmp/v3-chcon-scratch/` populated with throwaway files; for each file, `chcon $(ls -Z file | awk '{print $1}') file` (label round-trip — same value, but exercises the write path). Run the same 5-min sustained load. If THIS dies, we've reproduced Stage B without touching /system; the answer is "chcon write workload" and the fix is chunking + `chcon` health-check between batches, not pinning hdc. If this stays clean too, look elsewhere (Channel B concurrency, audit-log overflow, etc.).

3. **For hdc-pinning: source from OHOS release SDK, NOT HBC.** If a future agent still wants the pinning comparison, the only viable path is downloading a Windows OHOS SDK bundle from Huawei Cloud / OpenHarmony Releases (specific 5.x release matching the DAYU200 ROM). HBC does not produce Windows hdc.exe — confirmed exhaustively in §1.

4. **Persist Stage-B-style snapshots off-device.** Agent 79 discovered the chcon snapshot/backups were wiped by power-cycle. Independent of any pinning question, the V3 deploy script should `hdc file recv` the snapshot file to host immediately after `--snapshot-only` returns, so a power-cycle event doesn't lose the rollback recipe.

5. **Do NOT retry Stage B yet.** Pre-conditions from agent 79's report still hold — partial-Stage-B state is gone, /system is factory-equivalent. Any Stage B retry should be planned as a from-scratch deploy run after (2) settles the chcon-workload question, not a "resume from snapshot" attempt.

---

## Files created / modified

- `scripts/v3/hdc-long-load-probe.sh` — new, +x, 8-pattern rotating probe with rolling-p95 progress and final-summary stats.
- `tools/hdc-candidates/README.md` — documents the HBC empty-search outcome so a future agent doesn't repeat the dead-end.
- `docs/engine/V3-W2-PATH-B-RESUMED.md` — this file.
- `/tmp/v3-hdc-long-probe/3.2.0b.tee`, `/tmp/v3-hdc-long-probe/probe-hdc-*.log` — raw probe output (host-side, ephemeral).

## Hard constraints honored

- ✓ No `hdc target boot`.
- ✓ No `setenforce`.
- ✓ No writes to /system or /data/local/tmp on board (probe is strictly READ-ONLY — `ls`, `cat`, `echo`, `getenforce`, `mountpoint`, `date`).
- ✓ No overwrite of `/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe` (3.2.0b preserved unchanged).
- ✓ Commit local only, no push.
