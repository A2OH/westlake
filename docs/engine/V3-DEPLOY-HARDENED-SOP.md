# V3-DEPLOY-HARDENED-SOP — 12-stage incremental deploy procedure

**Status:** AUTHORITATIVE for the post-ROM-recovery W2 re-attempt.
**Date:** 2026-05-18.
**Authoring agent:** 55 (W2-prep deliverable; companion to the hardened
script at `scripts/v3/deploy-hbc-to-dayu200-hardened.sh`).
**Companion docs:**
[`V3-W2-POSTMORTEM.md`](V3-W2-POSTMORTEM.md) (agent 52, G1-G7 enumeration),
[`V3-W2-RECOVERY-PROCEDURE.md`](V3-W2-RECOVERY-PROCEDURE.md) (agent 53,
HBC re-query),
[`V3-DEPLOY-SOP.md`](V3-DEPLOY-SOP.md) (W9 Westlake adaptation),
[`westlake-deploy-ohos/v3-hbc/scripts/DEPLOY_SOP.md`](../../westlake-deploy-ohos/v3-hbc/scripts/DEPLOY_SOP.md) (HBC SOP v4).

---

## 1. Scope and motivation

The first W2 deploy (agent 49, 2026-05-16) soft-bricked DAYU200. The
postmortem (`V3-W2-POSTMORTEM.md` §2) ranked two top causes: (H1) silent
chcon failure → SELinux respawn storm at next boot, and (H2) Windows
`hdc.exe` shell stdout silently dropped mid-Stage-3. The legacy driver
`scripts/v3/deploy-hbc-to-dayu200.sh` had no defense against either: it
ran `chcon ... || true` (swallowing failures), batched many operations
without re-probing channel health, and issued `hdc target boot` after
the shell channel had already gone silent.

This document specifies the hardened driver
`scripts/v3/deploy-hbc-to-dayu200-hardened.sh` that supersedes the
legacy one for the re-attempt. The legacy script is **preserved
unchanged** under its original filename for forensic diff.

## 2. 12-stage incremental flow

Every stage is separately invocable. Each stage prints clear PASS / FAIL
and asserts that the channel is alive at entry and exit
(`_alive_probe` — G1 sentinel).

```
Stage 0    preflight             hdc version pin (G6), uname, getconf LONG_BIT,
                                 getenforce, /system/android absent,
                                 hdcd alive, required-artifact allowlist (G3)
Stage 1    backup                13 device-side .orig_<TS> copies (HBC §1)
Stage 2    stop services         foundation + render_service (SKIPPED by default;
                                 G4 opt-out via --no-skip-stage-2)
Stage 3.0  mkdir                 4 dirs HBC requires (HBC P-B prerequisite)
Stage 3b   OH services .so       10 .so + libbms symlink (HBC §3b)
Stage 3c   AOSP native .so       38 + 3 dual-path adapter shims; chcon+verify (G2)
Stage 3d   framework jars        12 jars + ICU + fonts.xml dual-path; chcon+verify (G2)
Stage 3e   boot image            27 files (9 segs × 3 ext); chcon+verify (G2)
Stage 3f   appspawn-x + cfg      bin + 3 cfg + namespace ini + file_contexts + 5 symlinks
Stage 3.7  final chcon sweep     re-verify high-risk labels stuck (G2)
Stage 3.8  channel-alive probe   3× sentinel BEFORE reboot (G1; ANTI-W2)
Stage 3.9  integrity             md5 + size of full manifest + drwx sentinel (G5)
Stage 4    reboot                ONLY after Stage 3.8 + 3.9 PASS; poll for return
Stage 5    post-reboot verify    pidof foundation/render_service/launcher/hdcd
Stage 6    aa start              install HelloWorld.apk + aa-launch.sh
Stage 7    capture marker        scan hilog for MainActivity.onCreate (W2 accept)
```

Each is invocable as: `bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh <stage>`
where `<stage>` ∈ {0, 1, 2, 3.0, 3b, 3c, 3d, 3e, 3f, 3.7, 3.8, 3.9,
4, 5, 6, 7, all}. The `all` form runs 0 → 3.9 + 3.8 (no reboot); add
`--reboot` to also run 4 → 7.

## 3. G1-G7 implementation map

Each gap enumerated in `V3-W2-POSTMORTEM.md` §4 maps to a concrete script
construct:

| Gap | Postmortem §4 description                              | Hardened script implementation |
|-----|--------------------------------------------------------|--------------------------------|
| G1  | channel-alive sentinel between every stage             | `_alive_probe()` (L150-162); called at entry + exit of every `stage_N()` function. Sends a random per-process marker and asserts exact-match. Empty stdout → `abort` (exit 99). |
| G2  | chcon-verify after every chcon batch                   | `chcon_verify()` (L368-381); runs chcon then `ls -lZ` and asserts the label suffix grep-matches the expected `<label>_file`. Replaces all `chcon ... \|\| true` from legacy. |
| G3  | required-artifact allowlist                            | `REQUIRED_ARTIFACTS` array (L175-237; 76 entries) + `_verify_required_artifacts()` (L255-272). `libinstalls.z.so` + `libappexecfwk_common.z.so` both included (the 2 W2 misses). Fail-fast: missing required → exit 1. |
| G4  | Stage 2 opt-out                                        | `SKIP_STAGE_2=1` default; `--no-skip-stage-2` flag inverts. `stage_2()` documents both paths. Never touches `appspawn` (HBC prerequisite 1). |
| G5  | Stage 3.9 integrity + drwx sentinel as script step     | `stage_3_9()` (L878-980); 76-entry manifest md5+size compare + drwx sentinel via `find -type d` on 5 critical dirs. drwx-found → `abort`. |
| G6  | pin hdc.exe version                                    | `_check_hdc_version()` (L164-181); prints `hdc version`, matches against `KNOWN_GOOD_HDC_VERSIONS`; warns on drift (does not abort — operator may have validated a newer build). |
| G7  | 12 incremental stages, each separately invocable       | Dispatcher (L1141-1185); each `<stage>` token routes to one `stage_*()` function. `all` runs in dependency order. |

## 4. HBC "全局三条" abort conditions enforced

Per `westlake-deploy-ohos/v3-hbc/scripts/DEPLOY_SOP.md` lines 6-10 and
`V3-W2-RECOVERY-PROCEDURE.md` §2, the script aborts (exit 99) on any
of these 5 conditions:

1. `connect-key` in any `hdc` output → `_assert_no_fail_or_drwx`
2. `timeout` in any `hdc` output → `_assert_no_fail_or_drwx`
3. `[Fail]` in any output → `_assert_no_fail_or_drwx`
4. `drwx` in `ls` of a pushed file → `_assert_no_fail_or_drwx`
   AND `stat -c %F` ≠ "regular file" in `stage_push()`
5. **empty stdout** from a sentinel echo (Westlake addition per
   postmortem H2) → `_alive_probe`

Once any abort fires, the script exits 99 **without issuing reboot**.
This is the discipline W2 violated when it ran `hdc target boot` after
the shell had gone silent.

## 5. Staging-dir-only rule

Per HBC SOP "全局三条" rule #2: **never** call
`hdc file send <local> /system/...` directly. Use `stage_push()` (L311-369):

1. `hdc file send <win> /data/local/tmp/stage/<basename>`
2. Assert `stat -c %F` returns `"regular file"` (NOT directory — the
   hdc 造目录 quirk)
3. `md5sum` local == `md5sum` staging
4. `cp /data/local/tmp/stage/<basename> /system/<dst>`
5. `md5sum` local == `md5sum` final (catches mid-cp truncation)

Every push in the hardened script goes through `stage_push()`. There
are no direct `/system/*` writes. The legacy script's 17 direct-push
sites (per the regression `check_deploy_sop_compliance` probe) are not
replicated here.

## 6. Operator instructions

### 6.1 Prerequisites

- DAYU200 powered on and `hdc list targets` shows the serial
- `getconf LONG_BIT == 32` (CR60 expectation)
- `/system/android` does NOT exist (factory baseline; if it does, run
  `--uninstall` then reboot first)
- `westlake-deploy-ohos/v3-hbc/lib/libinstalls.z.so` and
  `libappexecfwk_common.z.so` present (added 2026-05-16 by W2 recovery)

### 6.2 One-command full deploy (after ROM recovery)

```bash
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh all --reboot
```

This runs Stage 0 → Stage 7 sequentially. Each stage's PASS is a hard
gate for the next. If ANY stage aborts (exit 99 = HBC 全局三条 hit),
the script stops and reports the trip cause; do NOT manually retry
without operator review.

### 6.3 Stage-by-stage manual flow (RECOMMENDED for re-attempt)

```bash
# Phase A — preflight + backup
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh 0
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh 1

# Phase B — push (skip Stage 2 by default)
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh 3.0
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh 3b
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh 3c
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh 3d
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh 3e
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh 3f

# Phase C — gates BEFORE reboot
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh 3.7
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh 3.9
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh 3.8     # ← FINAL gate

# Phase D — reboot + acceptance (ONLY if Phase C all PASS)
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh 4
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh 5
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh 6
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh 7
```

### 6.4 Pre-flight smoke probes from regression suite

```bash
bash scripts/v3/run-hbc-regression.sh --probe-stage=0     # preflight probe
bash scripts/v3/run-hbc-regression.sh --probe-stage=3.8   # G1 sentinel ×3
bash scripts/v3/run-hbc-regression.sh --probe-stage=5     # post-reboot pids
```

These are READ-ONLY (no chcon, no push, no reboot) and can be run any
time the board is up to validate that the channel is healthy before
queuing a real deploy.

### 6.5 What to do if a stage aborts

| Exit | Meaning | Operator action |
|-----:|---------|-----------------|
| 0  | PASS                         | Proceed to next stage. |
| 1  | Assertion FAIL (resumable)   | Inspect output, fix root cause, re-run SAME stage. |
| 2  | Usage / setup error          | Fix CLI; re-run. |
| 99 | HBC 全局三条 ABORT           | **STOP.** Do NOT reboot. Capture hilog if possible. Investigate. |

For exit 99, follow `V3-W2-RECOVERY-PROCEDURE.md` §"Phase A": operator
hard power-cycle the board, then re-run from Stage 0.

## 7. Pre-brick rollback

If the deploy succeeded but caused boot-time instability (post-reboot
service crashes, etc.), and the board is still hdc-responsive:

```bash
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh --uninstall
# Reboot the device
```

`--uninstall` restores from the `.orig_<TS>` backups created in Stage 1.
Per `V3-W2-RECOVERY-PROCEDURE.md` §2: "HBC has no shell-side recovery
for an already-bricked board." If the device is silent, the only path
is operator hard power-cycle + (if needed) DriverAssitant USB reflash.

## 8. Regression coverage

`scripts/v3/run-hbc-regression.sh` covers this script via:

| Slot | Coverage |
|------|----------|
| W2-prep: hardened deploy present  | File exists, executable, `bash -n` parses |
| W2-prep: hardened deploy lints clean | G1-G7 markers present, 2 new .so in allowlist, abort helpers wired |
| hardened stage-0 probe            | Read-only board preflight, mirrors Stage 0 sentinel |
| hardened stage-3.8 probe          | 3× G1 sentinel against live board |
| hardened stage-5 probe            | Post-reboot pids of foundation/render_service/launcher/hdcd |
| W2 slot                           | Board-state inspection; references hardened script |

Run with `--no-board` for static checks only, or `--quick` for
artifact + smoke + lints (skips W-slots).

## 9. Differences from the legacy `deploy-hbc-to-dayu200.sh`

| Concern                  | Legacy                                  | Hardened |
|--------------------------|-----------------------------------------|----------|
| Push template            | `hdc file send <local> /system/...` direct (17 sites; bypass staging) | `stage_push()` — staging dir + md5 round-trip + drwx sentinel |
| chcon                    | `chcon ... \|\| true` (silent failure) | `chcon_verify()` — chcon + `ls -lZ` + grep, abort on mismatch |
| Channel-alive check      | One-shot at preflight                  | `_alive_probe()` at entry+exit of every stage; triple at Stage 3.8 |
| Missing artifacts        | `SKIP (not found)` silent return 0      | `_verify_required_artifacts()` fail-fast at Stage 0 |
| 2 new .so files          | Absent                                  | In `REQUIRED_ARTIFACTS` (G3) |
| hdc version              | No check                                | `_check_hdc_version()` warn on drift (G6) |
| Integrity verify         | Boot image only                         | Full 76-entry manifest md5+size (G5) |
| drwx sentinel            | Absent                                  | After every stage that writes; final sweep at Stage 3.9 |
| Reboot guard             | Always issues `hdc target boot`         | Reboot is Stage 4 only; requires Stage 3.8 PASS |
| Stage 2 default          | Skipped without flag                    | Skipped by default with explicit `--no-skip-stage-2` opt-in (G4) |
| LOC                      | 284                                     | 1190 (mostly comment + per-stage isolation) |

## 10. Cross-references

- Script: `scripts/v3/deploy-hbc-to-dayu200-hardened.sh`
- Legacy (forensic): `scripts/v3/deploy-hbc-to-dayu200.sh`
- Regression: `scripts/v3/run-hbc-regression.sh` (Section 1 + 2 slots)
- Postmortem: `docs/engine/V3-W2-POSTMORTEM.md`
- Recovery procedure: `docs/engine/V3-W2-RECOVERY-PROCEDURE.md`
- Westlake-adapted SOP: `docs/engine/V3-DEPLOY-SOP.md`
- HBC source-of-truth: `westlake-deploy-ohos/v3-hbc/scripts/DEPLOY_SOP.md`
- HBC stage_push template: `westlake-deploy-ohos/v3-hbc/scripts/deploy_stage.sh` L125-156
- Launch model: `docs/engine/V3-LAUNCH-MODEL.md`
