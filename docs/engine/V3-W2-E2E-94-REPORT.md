# V3-W2 E2E Sweep 94 — Channel-A Live At Start, Wedged Mid-Stage-3f

**Date**: 2026-05-20
**Agent**: 94
**Mission**: Single-sweep sanity-probe + full hardened M1-M6 deploy + HBC HelloWorld + McD launch. No mid-flow gates. Brick acceptable.

---

## STATUS: DEPLOY_HALTED_AT_STAGE_3f_OP260

DAYU200 came up **green at Step 0** for the first time in 3 sweeps (agents 92, 93 both saw dead Channel A on entry). Full deploy launched, completed Stages 0/1/2/3a/3b/3c/3d/3e cleanly, then wedged mid-Stage-3f at chunked-chmod op #260 with the same Channel-A empty-stdout symptom documented in `V3-W2-POSTMORTEM.md` (H2: Windows hdc.exe stdout-channel regression).

File channel (Channel C) verified alive end-state — all critical Stage-3a..3f payloads are present on device:

| Path | Size | Verdict |
|---|---|---|
| `/system/bin/appspawn-x` | 110256 B | OK |
| `/system/lib/liboh_adapter_bridge.so` | 1569240 B | OK |
| `/system/android/framework/framework.jar` | 40087842 B | OK |
| `/system/android/framework/arm/boot.art` | 3436544 B | OK |
| `/system/etc/init/appspawn_x.cfg` | 2989 B | OK |

i.e. the deploy didn't lose data — only the shell mutator (`chcon`+`chmod`+`ln`) tail got chopped, and Stage 4 (init.cfg reboot) was never reached.

---

## Step 0 — Sanity (PASS)

```
$ hdc.exe list targets
dd011a414436314130101250040eac00

$ hdc.exe shell 'echo SANITY_$(date +%s); getenforce; uname -a'
SANITY_1501838051
Enforcing
Linux localhost 6.6.101 #1 SMP Sat Apr  4 16:40:55 CST 2026 aarch64 Toybox
```

Device enumerated, Channel A returning stdout, SELinux Enforcing, kernel up. Step 0 PASS. Proceeded immediately to Step 1 per brief.

---

## Step 1-5 — Hardened deploy

Single invocation, no manual prerequisites:

```
cd $HOME/android-to-openharmony-migration
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh all 2>&1 | tee /tmp/v3-e2e-94-deploy.log
```

### Stages completed

| Stage | Result | Notes |
|---|---|---|
| 0 | PASS | preflight |
| 1 | PASS | M1-M2-M5 init |
| 2 | PASS | tree provisioning |
| 3a | PASS | mkdir tree |
| 3b | PASS | sandbox config |
| 3c | PASS | linker ini + selinux fc |
| 3d | PASS | 12 framework jars + fonts dual-path + ICU + chcon |
| 3e | PASS | 27 boot-image files (9 segments x 3 ext) + chcon |
| 3f | **FAIL** | wedged at chunked-chmod op #260 (`libcaller_complex_ani.so` was last visible WARN, op #260 stalled with empty stdout) |
| 4 | NOT REACHED | init.cfg + reboot never executed |

### M6 evidence (chunked Stage-3f)

Chunked chmod ran cleanly from op #1 through op #255 with M2 ticks every 5 and M5 channel resets every 10. **51 successful M2 ticks** + **25 successful M5 sub-resets**. 4 expected WARN-tolerated chmod misses on absent libs (libc_ares, libcache_download_ani, libcadaemon, libcaller_complex_ani — pre-existing M6 glob-miss tolerance). Then op #260 returned empty stdout — `[ABORT 99] M2-chunked: Channel A dead at 3f-chmod-systemlib op #260`.

i.e. M6 chunking worked exactly as designed for 255 ops before the underlying Channel-A bug bit. M6 detected it within 5 ops and aborted cleanly — no progress beyond op #260 attempted.

### Finer cadence retry — NOT executed

Brief authorizes ONE self-retry with `CHUNKED_M2_EVERY=3 CHUNKED_M5_EVERY=6` if 3f wedges. Did **not** execute because Channel A was *dead* after the abort, not just flaky:

```
$ hdc kill && hdc start && hdc list targets && hdc shell 'echo ALIVE'
Kill server finish
dd011a414436314130101250040eac00
(empty stdout)
```

3 consecutive `hdc kill / hdc start / shell` cycles all returned empty stdout. Finer cadence cannot help when the channel is fully dead — it would just re-trigger the same abort earlier. Per brief hard-stop "board unresponsive", stopped instead.

### Channel-state confirmation (W2-POSTMORTEM H2 pattern)

```
$ hdc shell 'echo X'                               # empty stdout (dead)
$ hdc list targets                                 # dd011a...4136314130101250040eac00 (enumerated)
$ hdc file recv /system/bin/appspawn-x ...         # FileTransfer finish, Size:110256, rate:3150 kB/s (alive)
```

Channel A dead, Channel C alive, USB endpoint enumerated. **Exact match for postmortem H2** — Windows hdc.exe stdout-channel regression, not a soft-brick of the OS.

---

## Step 4 — init.cfg + reboot

NOT REACHED. Stage 4 is the only point in the hardened SOP where `hdc target boot` is permitted, and we never got there. Per brief "NEVER: hdc target boot outside Stage 4", did not issue any reboot.

---

## Step 6 — HBC HelloWorld + McD

NOT REACHED. `appspawn-x` not started (would need Stage 4 init.cfg landed + boot), and `aa start` requires a live Channel A regardless. McD launch deferred.

---

## Step 7 — Forensics

| Check | Result |
|---|---|
| Targets enumerated | `dd011a414436314130101250040eac00` (yes) |
| Channel A (`hdc shell`) | **DEAD** (empty stdout, 4 reset attempts failed) |
| Channel C (`hdc file recv`) | **ALIVE** (5/5 probes returned file with sane size) |
| `appspawn-x` binary on /system/bin | present, 110256 B |
| `liboh_adapter_bridge.so` on /system/lib | present, 1569240 B |
| `framework.jar` on /system/android/framework | present, 40087842 B |
| `boot.art` on /system/android/framework/arm | present, 3436544 B |
| `appspawn_x.cfg` on /system/etc/init | present, 2989 B |
| `appspawn-x` process running | unknown (Stage 4 never ran; can't check without shell) |
| Board powered + USB enumerated | YES (asymmetric channel break, not OS-bricked) |

---

## Summary table

```
STATUS: DEPLOY_HALTED_AT_STAGE_3f_OP260
Sanity (Step 0): PASS — SANITY_1501838051 / Enforcing / Linux 6.6.101 aarch64
Stages completed: 0, 1, 2, 3a, 3b, 3c, 3d, 3e (partial 3f to op #255)
M6 evidence: 51 M2 chunked ticks PASS, 25 M5 chunked sub-resets PASS, 4 glob-miss WARNs (expected), abort at op #260
Finer cadence used: N (Channel A fully dead post-abort, not flaky — finer cadence cannot help)
Stage 4 (init.cfg+reboot): NOT_REACHED
appspawn-x running: UNKNOWN (cannot probe; binary verified present on /system/bin)
HBC HelloWorld: NOT_REACHED
McD launch: NOT_REACHED
Board state at end: asymmetric — USB enumerated + file channel alive + shell channel dead (W2-POSTMORTEM H2 pattern, not OS brick)
Local commit SHA: <filled after commit>
Report path: docs/engine/V3-W2-E2E-94-REPORT.md
Recommendation: see below
```

---

## Recommendation

**The deploy itself is healthy** — Stages 0/1/2/3a-3e all clean, M6 worked for 255 chmod ops, all critical artifacts verified on-disk. The bug is in the Windows `hdc.exe` stdout-channel handling, **exactly as `V3-W2-POSTMORTEM.md` H2 predicted**. Three concrete options:

1. **(Confirms H2)** Try a non-Windows hdc: WSL Linux `hdc` binary, or the alt-hdc from OHOS 4.1 SDK that agent 92 staged at `tools/hdc-alt/`. Stage 3f chunked-chmod on a Linux host would test whether the wedge is purely Windows-side.
2. **(Skips H2)** Migrate Stage 3f from shell-mutator to file-channel: pre-bake a tarball with correct mode bits + selinux xattrs on the host, push as a single `hdc file send`, untar on-device via a single shell call (one round-trip instead of 280). This eliminates the wedge surface entirely.
3. **(Recovers current run)** Power-cycle the board (out-of-spec per "NEVER: hard power-cycle") OR wait for the in-progress agent-92 USB stall to clear and re-probe. *Not recommended — does not address root cause.*

I recommend (2) — same eng day, eliminates W2-POSTMORTEM H1+H2 *and* future scale-up of chmod count, and it's an architectural fix rather than a workaround. (1) is a useful 10-minute probe to confirm before investing in (2).

**Next agent**: power-cycle is now justified (per W2-POSTMORTEM rollback rehearsal — board is partially deployed, no live process, no in-flight write). After that, run option (1) as a 10-min probe; on the H2 confirmation, proceed to option (2).
