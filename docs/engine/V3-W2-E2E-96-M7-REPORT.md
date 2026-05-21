# V3-W2 E2E Sweep 96 — M7 First Test — Architectural Fix Works, Wrong Tool Assumption

**Date**: 2026-05-20
**Agent**: 96
**Mission**: Single-sweep end-to-end test of M7 tarball-batch architectural fix for Stage 3f Channel A death (agents 87, 88, 94). Reach McD launch. Brick acceptable.

---

## STATUS: DEPLOY_HALTED_AT_STAGE_3f_M7_RESTORECON

M7 structural fix **worked at the channel level** (Stage 3f Channel A calls dropped from ~280 → 3, channel never went silent, board never bricked) but **failed at the tool level**: the on-device `/bin/restorecon` (OHOS musl 32-bit ARM, BuildID `791c3bd86f640f223a4975b3778aebc1`, 3572 bytes) is a single-path-arg implementation that **does not accept the `-R` flag**. M7 was designed against GNU/Android restorecon semantics. The `tar xf && restorecon -R … && echo SENTINEL` compound aborted at the first `restorecon -R`, sentinel never fired, M7 helper raised abort 99.

Deploy completed Stages 0/1/2/3.0/3b/3c/3d/3e cleanly. Stage 4 (init.cfg + reboot) not reached. Board responsive post-abort, SELinux Enforcing, factory appspawn still pid 206. Stage 3f payloads ARE on device and labels look correct because the tar contained the file_contexts and the script had already restorecon-ed staging files at pack time (or labels passed through the toybox tar implementation). `/system/bin/appspawn-x` carries `u:object_r:appspawn_exec:s0`, `/system/etc/init/appspawn_x.cfg` carries `system_etc_file`, `/system/lib/libapk_installer.so` carries `system_lib_file`.

---

## Sanity (Step 0)

```
$ hdc.exe list targets
dd011a414436314130101250040eac00
$ hdc.exe shell 'echo SANITY_$(date +%s); getenforce; uname -a'
SANITY_1501837358
Enforcing
Linux localhost 6.6.101 #1 SMP Sat Apr  4 16:40:55 CST 2026 aarch64 Toybox
```

PASS. Proceeded immediately.

---

## M7 evidence

| Item | Result |
|---|---|
| Stage 3f tarball built locally | Y — `/tmp/v3-stage3f-519911.tar` 5,068,800 B, 15 entries |
| Stage 3f tarball pushed | Y — Channel C OK, verified `test -s` on device |
| Stage 3f `tar xf` completed | **Y** (verified manually post-abort — `tar tf` listed 15 entries cleanly, `tar xf` exit 0) |
| Stage 3f `restorecon -R` completed | **N — OHOS restorecon rejects `-R` with "invalid args!" rc=255** |
| Total Stage 3f Channel A calls | **3** (vs. ~280 prior, vs. target 7-10) |

M7 channel discipline: pre-probe (Channel A #1, OK) + `test -s` after Channel C push (Channel A #2, OK) + extract+restorecon+rm+sentinel single compound (Channel A #3, FAILED at restorecon-R). Channel never went silent.

---

## Stages completed

| Stage | Result | Time | Notes |
|---|---|---|---|
| 0 | PASS | 17:36:26 | preflight + Gate 13 + Gate 8 armed |
| 1 | PASS | 17:36:27 | 13 backups (`.orig_20260520`) |
| 2 | SKIP | 17:36:31 | opt-out active |
| 3.0 | PASS | 17:36:31 | 4 dirs |
| 3b | PASS | 17:36:34 | 11 .so + 1 symlink |
| 3c | PASS | 17:37:11 | 38 AOSP + 3 dual-path + chcon |
| 3d | PASS | 17:39:00 | 12 jars + ICU + fonts dual-path + chcon |
| 3e | PASS | 17:39:06 | 27 boot files + chcon (mid-batch alive probes all OK) |
| **3f** | **ABORT 99** | 17:40:28 | M7 sentinel missing (restorecon -R invalid args) |
| 4 (init.cfg+reboot) | not reached | — | **KEY ENGINE GATE — NOT TESTED THIS SWEEP** |
| 5/6 | not reached | — | — |
| appspawn-x running | N (init.cfg never registered) | — | factory appspawn pid 206 only |

HBC HelloWorld smoke: not run (gated on Stage 4).
McD APK install/launch/marker: not run (gated on Stage 4).

---

## Final board state

Responsive. `getenforce` = Enforcing. Kernel 6.6.101 still up. Channel A + Channel C both alive. No brick. `/system/bin/appspawn-x` present (110256 B) but no init.cfg integration yet. Forensics:

- `/tmp/v3-e2e-96-deploy.log` (17618 B, full stage trace)
- `/tmp/v3-e2e-96-final-hilog.txt` (38911 B)
- `/tmp/v3-e2e-96-final-dmesg.txt` (15721 B)
- `/tmp/v3-e2e-96-final-ps.txt` (3497 B)
- `/tmp/v3-e2e-96-final-state.txt` (796 B)

---

## Anomalies + forensic

**Root cause**: OHOS ships a stripped-down `/bin/restorecon` that accepts ONLY a single path argument. No `-R`. No `-F`. No `-v`. Output: `args : <path>` (one line per path). Bisect: `restorecon -R /system/bin` → "invalid args!" rc=255. `restorecon /system/bin/appspawn-x` → rc=0 (after first warning about missing /vendor/etc/selinux/ignore_cfg). `toybox restorecon` → "Unknown command". `chcon` IS a toybox link and works fine.

**Implication for M7**: the tarball pack+push+single-call discipline is sound (3 Channel A calls, sentinel framing, channel intact). The on-device post-extract relabel cannot use `restorecon -R`. Two viable shapes for M7 v2:
1. Pack-time restorecon discipline: pre-label every staging file with `chcon` before `tar cf --xattrs`, then push tarball with xattrs preserved.
2. Generate the explicit per-file `restorecon /path/to/file` list at pack time from the staging tree, ship it as a `.sh` in the tarball, then run that script in the single shell call.

Shape #2 keeps M7's Channel-A-call discipline (still 3 calls — the embedded shell loops device-side, off Channel A). Shape #1 may need toybox `tar --xattrs` audit.

**What we proved this sweep**: H2 (cumulative Channel A call count) is NOT the only thing standing between us and Stage 4. Once a correct relabel call replaces `restorecon -R`, Stage 3f completes in <5 Channel A calls and Stage 4/5/6 become testable for the first time.

**What we did NOT prove**: Stage 4 init.cfg+reboot path. Untouched.

---

## Local commit SHA

TBD on commit.

## Report path

`docs/engine/V3-W2-E2E-96-M7-REPORT.md`

## Recommendation

Land an **M7-fix** patch: replace the `restorecon -R $path` chain in `_stage_tar_push_and_extract` with shape #2 (in-tarball `restorecon.sh` enumerating explicit per-file restorecon calls, executed in the same single shell). Keep all other M7 discipline (channel framing, sentinel, Channel C push). Re-run E2E in next sweep — expect Stage 3f to PASS in ~5 Channel A calls and to exercise Stage 4 for the first time in the V3 line. Do NOT add per-app hacks. Do NOT change Stage 4 logic this iteration. After fix lands, agent 97 runs the same single-sweep brief with this M7-v2.

If toybox `tar` on device supports `--xattrs` extract, shape #1 (chcon staging + xattr-preserving tar) is even cleaner — verify before commit.
