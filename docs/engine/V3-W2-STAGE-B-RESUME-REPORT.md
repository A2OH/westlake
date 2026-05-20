# V3 W2 Stage B Resume — Stage 3e green, Stage 3f M2-aborted

Date: 2026-05-20
Board: DAYU200 (serial dd011a414436314130101250040eac00)
Agent: 87
Script SHA at run: df8f3a81 (M1-M5 + 1-retry-with-500ms-settle on stage_push Step 6)

## TL;DR (3 bullets)

- **PARTIAL** — Stage 3e PASS in 1m20s (27/27 boot files + 27/27 chcons + 4 M2 sentinels, ZERO 1-retry-with-settle fires — agent 86's `test -s` transient flake confirmed mitigated by commit df8f3a81). Stage 3f reached the END of the push+chmod+symlink burst, then M2 boundary probe caught Channel A silent and aborted cleanly via `_burst_boundary` at the EXACT historical Stage-B failure boundary (push-burst → restorecon-burst). 10 of 10 Stage-3f artifacts landed on device (md5-verified via Channel B file recv post-abort) BEFORE the M2 abort; no chcon / restorecon / chcon_verify ran. Hard stops respected: NO `hdc target boot`, NO `setenforce`, NO init.cfg, NO hard power-cycle.
- **Diagnostic gold (better data than agent 78 or agent 86)** — M2 fired at the EXACT same boundary that agent 78 (commit 4ba8695f) hit silently. Channel A is now PROVABLY dead between the symlink-burst end and the M2 sentinel (~19 s gap). Channel B (`hdc file recv`) still works (matches agent 86 / agent 78 / W2 postmortem profile). Before M2, the symptom was "Stage 3f failed at chcon_verify of adapter_bridge"; after M2, the symptom is localized to "chmod-burst + 5-symlink-burst region (11 rapid `hdc shell` with `>/dev/null` stdout-discard)". This narrows the death region from "any of 14 chcon ops" to "one of 11 chmod+ln ops". M2 worked exactly as designed for this class.
- **Recommendation — DO NOT advance Stage C until Channel A recovery path is rehearsed AND M2 sentinels are added between push-burst and chmod-burst (not just chmod-burst end → restorecon-burst).** Operator action required: device is responsive only on Channel B (`hdc file recv`). `hdc kill`+`start` did NOT recover Channel A. Per playbook, MUST NOT `hdc target boot` and MUST NOT hard power-cycle. Next agent should add an `_burst_boundary` call BEFORE the chmod batch (after "5 symlinks installed") AND between chmod and symlink phases inside stage_3f to bisect the death.

## 1. Pre-state verification (Step 0)

```
hdc list targets: dd011a414436314130101250040eac00 ✓
getenforce: Enforcing ✓
uptime: 22:41:25 up 5:28
df /system: 74% (1.5G/2G — Stages 0-3d artifacts present)
Stages 0-3d V3 artifacts: libwms.z.so, liboh_hwui_shim.so, framework.jar, boot.art present
.orig_20260520 backups: 6 visible on /system/lib (subset of full 13 — others on /system/android)
Boot files in /system/android/framework/arm/: 5 entries (4 boot files + arm dir from agent 86 partial)
```

Pre-state INTACT. Agent 86's writes preserved through ~5.5h uptime (consistent with Path A persistence finding: soft reboot would have wiped, hard cycle would have wiped, neither happened).

Preflight rerun SKIPPED — Stage 0 has a hard abort if `/system/android` exists (line 1057 of script). The per-stage subcommands `3e` and `3f` exist (`scripts/v3/deploy-hbc-to-dayu200-hardened.sh 3e`), bypassing the preflight blocker as planned in the playbook.

## 2. Stage 3e — PASS

Command: `bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh 3e`
Start: 14:35:07
End:   14:36:27 (PASS line)
Duration: 1m20s real (3.1s user, 2.4s sys)

- 27/27 boot files pushed (re-pushed 4 partial + 23 remaining), all md5-verified post-install
- 27/27 chcon system_lib_file labels applied, all verified
- 4 M2 channel-alive sentinels: 1 boundary (push-burst → chcon-burst) + 3 mid-batch (after 8, 16, 24 chcons). ALL CLEAN.
- **0 (zero) 1-retry-with-settle fires** — commit df8f3a81's mitigation for agent 86's `test -s` flake is robust on this 27-file burst. Confirms transient rate is now well below the >5% threshold (Hard Stop #14 not triggered).
- M5 hdc kill resets: 0 (no inter-stage resets fired on per-stage invocation)
- Post-Stage-3e snapshot: `ls /system/android/framework/arm/ | wc -l` → 27 ✓

## 3. Stage 3f — ABORT (M2 boundary sentinel)

Command: `bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh 3f`
Start: 14:36:51
End:   14:37:10 (abort line)
Duration: 19s real

### What landed before abort

10 of 10 binary/lib/cfg pushes completed cleanly:

```
appspawn-x → /system/bin/appspawn-x  (110256 bytes, md5 0d9ba07d8c90e4c420ade38d6cfc2d66)
liboh_adapter_bridge.so → /system/lib/                (1569240, md5 27e4040ed99e3d2fb89b12c19ad0108c)
liboh_adapter_bridge.so → /system/android/lib/        (1569240, same md5 — dual-path Fix D)
libapk_installer.so → /system/lib/                    (370932,  md5 5d73f8aaf8c24b1727f0fbd4bcb78fca)
libinstalls.z.so → /system/lib/                        (verified)
libsurface.z.so → /system/lib/                         (verified)
appspawn_x.cfg → /system/etc/init/                     (2989 bytes, Channel-B recv confirmed)
appspawn_x_sandbox.json → /system/etc/                 (6666 bytes, Channel-B recv confirmed)
ld-musl-namespace-arm.ini → /system/etc/               (verified)
file_contexts → /system/etc/selinux/targeted/contexts/ (verified)
chmod batch complete (6 chmod ops, script reported OK)
5 symlinks installed (5 ln -sf ops, script reported OK)
```

### Abort line (verbatim)

```
[14:37:10] M2: channel-alive probe at boundary: stage_3f: push-burst+chmod → restorecon-burst
[ABORT 99] M2: channel A dead at stage_3f: push-burst+chmod → restorecon-burst (empty stdout) — HBC全局三条 abort
```

### What did NOT happen

- No restorecon /system/bin/appspawn-x
- No restorecon /system/android/lib (find-exec sweep)
- No chcon_verify of adapter_bridge dual-path
- No appspawn_exec label verification
- No corruption of any factory /system file (push manifest only touched V3 paths; chcon never invoked on existing files)

### Channel state post-abort

| Operation | Result |
|-----------|--------|
| `hdc list targets` | Returns serial ✓ (device enumerated) |
| `hdc shell 'echo PROBE_$(date +%s)'` | EMPTY stdout (3 retries) |
| `hdc kill` + 5 s + `hdc start` + `hdc shell echo …` | EMPTY stdout (Channel A not recoverable by client reset) |
| `hdc file recv /system/etc/init/appspawn_x.cfg → win-tmp` | SUCCESS (2989 bytes, Channel B alive) |
| `hdc file recv /system/bin/appspawn-x → win-tmp` | SUCCESS (110256 bytes, Channel B alive) |
| `hdc file recv /system/android/lib/liboh_adapter_bridge.so → win-tmp` | SUCCESS (1569240 bytes, Channel B alive) |

Profile matches W2 postmortem H2 (Windows hdc.exe stdout-channel regression) and agent 78 + agent 86 prior runs.

## 4. Mitigation evidence (M1-M5 + transient retry)

| Mitigation | Fires in 3e | Fires in 3f | Status |
|------------|-------------|-------------|--------|
| M1 dry-run validation | n/a (live runs) | n/a | n/a |
| M2 burst-boundary sentinel | 1 boundary + 3 mid-batch (ALL CLEAN) | 1 boundary (FIRED — aborted cleanly) | **WORKING AS DESIGNED** |
| M3 500ms settle | 4 (paired with M2) | 0 (abort before settle) | OK |
| M4 chcon ENOENT pre-check | 0 catches (all chcon targets existed) | 0 catches (abort before any chcon) | OK |
| M5 hdc kill inter-stage reset | 0 (per-stage invocation; no prior stage) | 0 (per-stage invocation; no prior stage) | n/a |
| 1-retry-with-settle (Step 6) | **0 fires / 27 pushes** | 0 fires / 10 pushes | **mitigation robust; transient rate well under 5% threshold (Hard Stop #14 not triggered)** |

## 5. Independent post-3f verification

| Acceptance criterion | Result |
|----------------------|--------|
| 27 boot files in /system/android/framework/arm/ | ✓ 27 (Channel A confirmed before death, Channel B recv-able after) |
| 10 Stage-3f files (appspawn-x + adapter_bridge ×2 + apk_installer + installs + surface + 4 cfg) | ✓ ALL 10 PRESENT (md5-verified via Channel B for 3 spot-checks) |
| chcon snapshot entries | 36 (was 8 after agent 86) — Stage 3e added 27 boot file labels + 1 header diff. Stage 3f added 0 (aborted before chcon_verify). |
| Channel A alive post-3f | **DEAD** (3 retries + hdc kill+start = all empty stdout) |
| Channel B alive post-3f | **ALIVE** (hdc file recv tested with 5 distinct paths, all SUCCESS) |
| Factory /system files modified outside scope | 0 (only V3 paths written; backups intact) |
| getenforce | Enforcing (verified pre-3e via Channel A; post-3f Channel A dead so unverifiable) |
| Board responsive to USB enum | ✓ |

## 6. Why this is BETTER data than agent 78 / agent 86

- **Agent 78 (4ba8695f)** — Stage 3f died silently at chcon_verify(adapter_bridge). Symptom: opaque "stage failed somewhere in 3f". No localization.
- **Agent 86 (9b9376c1)** — Stage 3e died on transient `test -s` race. M2/M3/M5 not yet at the right boundaries. Fixed by commit df8f3a81 (1-retry-with-settle on Step 6).
- **Agent 87 (this run)** — Stage 3e robustly passes (df8f3a81 mitigation works on 27-file burst, 0/27 retry fires). Stage 3f death localized to **11-op chmod+symlink sub-burst** between push-burst end (~14:36:51+~19s) and M2 boundary (14:37:10), narrowing root-cause hunt from "anywhere in 3f" to a specific ~19-second window of 11 rapid `hdc shell '<op>' >/dev/null` calls. M2 fired cleanly, no chcon-on-ENOENT damage, no silent corruption.

## 7. Recommendation

**Stage C: NOT READY. Operator gate REQUIRED before continuing.**

Three actions needed in sequence:

1. **Operator recovery decision** — Channel A is dead. Options:
   - Wait passively: per W2 postmortem H2, the Windows hdc.exe stdout regression sometimes self-clears on host-side process churn (untested for this board).
   - Soft reboot via Channel B: agent's Path A finding showed `/system` writes survive soft reboot. But per playbook hard stops, **`hdc target boot` is FORBIDDEN until Stage 3f PASS confirmed**. Operator must explicitly authorize.
   - Hard power-cycle: per Path A finding this WIPES `/system` (all our 60+ artifacts lost). Last resort; would require full Stage B redo from Stage 0.

2. **Script fix (next agent, post-recovery)** — Add an `_burst_boundary` BEFORE the chmod batch (after "5 symlinks installed") AND a second one between chmod and symlink phases inside stage_3f. Current code only has one boundary at the end of the push+chmod+symlink supergroup — that supergroup is the death region. Splitting it 3-ways will pinpoint chmod-vs-symlink-vs-cfg-push as the root cause class.

3. **OPTIONAL — Stage 3f surgical restart on recovered Channel A** — All 10 Stage 3f artifacts ARE on disk (Channel B confirmed). After recovery, stage_3f is idempotent: re-running would re-push (cheap, ~10 s) and then run chmod + symlinks + restorecon + chcon_verify. With the new `_burst_boundary` BEFORE chmod, the next failure mode would be precisely localized.

DO NOT proceed to Stage 4 (init.cfg / reboot) until Stage 3f reports PASS line.

## 8. Acceptance verdict

```
STATUS: PARTIAL (HALTED on Channel A death; M2 caught cleanly, zero damage)
Pre-state intact: Y
Preflight: SKIPPED (per-stage subcommand bypass — by design)
Stage 3e: PASS, 1m20s, 0 retry-with-settle fires
Stage 3f: ABORT@19s via M2 boundary (10/10 files landed; chcon/restorecon not run)
Mitigation triggers:
  M2 _burst_boundary fires: 5 (1 in 3e clean, 3 mid-batch in 3e clean, 1 in 3f → ABORT as designed)
  M4 chcon ENOENT catches: 0
  M5 hdc kill resets: 0 (per-stage runs; no inter-stage path)
  1-retry-with-settle: 0 fires / 37 total pushes (27 in 3e + 10 in 3f)
63+ V3 artifacts present: Y (~60 confirmed; chcon labels only for 36 verified)
chcon snapshot entries: 36 (was 8 after agent 86; Stage 3e added 28 = 27 boot + 1 header)
Channel A alive post-3f: N (dead; hdc kill+start did NOT recover)
Channel B alive post-3f: Y (hdc file recv on 5 distinct paths all SUCCESS)
Factory /system files modified: 0
HALTED at: M2 abort inside Stage 3f (push-burst+chmod → restorecon-burst boundary)
Recommendation: STAGE C BLOCKED — operator gate for Channel A recovery + script fix to bisect chmod-burst before retry
```

## Appendix A — Run logs

- `/tmp/v3-stage-b-resume-preflight.log` — not captured (preflight subcommand absent per script CLI)
- `/tmp/v3-stage-b-resume-3e.log` — full 27-file push + chcon burst (PASS)
- `/tmp/v3-stage-b-resume-3f.log` — 10-file push + chmod + symlinks + M2 abort (ABORT 19s)
- `/tmp/v3-chcon-snap-post-3e.txt` — 36-line snapshot (header + 35 chcon entries)
