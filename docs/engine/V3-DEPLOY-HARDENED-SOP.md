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

## 3. G1-G7 + Gate 8-13 implementation map

Each gap enumerated in `V3-W2-POSTMORTEM.md` §4 maps to a concrete script
construct. Gates 8-13 (added 2026-05-19 by agent 75 after the chroot Phase 1b.2
substrate halt) port chroot-discovered safety lessons back to the /system
path:

| Gap | Description                                            | Hardened script implementation |
|-----|--------------------------------------------------------|--------------------------------|
| G1  | channel-alive sentinel between every stage             | `_alive_probe()`; called at entry + exit of every `stage_N()` function. Sends a random per-process marker and asserts exact-match. Empty stdout → `abort` (exit 99). |
| G2  | chcon-verify after every chcon batch                   | `chcon_verify()`; runs chcon then `ls -lZ` and asserts the label suffix grep-matches the expected `<label>_file`. Replaces all `chcon ... \|\| true` from legacy. |
| G3  | required-artifact allowlist                            | `REQUIRED_ARTIFACTS` array + `_verify_required_artifacts()`. `libinstalls.z.so` + `libappexecfwk_common.z.so` both included (the 2 W2 misses). Fail-fast: missing required → exit 1. |
| G4  | Stage 2 opt-out                                        | `SKIP_STAGE_2=1` default; `--no-skip-stage-2` flag inverts. `stage_2()` documents both paths. Never touches `appspawn` (HBC prerequisite 1). |
| G5  | Stage 3.9 integrity + drwx sentinel as script step     | `stage_3_9()`; manifest md5+size compare + drwx sentinel via `find -type d` on 5 critical dirs. drwx-found → `abort`. |
| G6  | pin hdc.exe version                                    | `_check_hdc_version()`; prints `hdc version`, matches against `KNOWN_GOOD_HDC_VERSIONS`; warns on drift (does not abort — operator may have validated a newer build). |
| G7  | 12 incremental stages, each separately invocable       | Dispatcher; each `<stage>` token routes to one `stage_*()` function. `all` runs in dependency order. |
| Gate 8  | chcon snapshot-and-restore                          | `_chcon_snapshot_init()` (Stage 0) + `_chcon_snapshot_capture()` (auto-invoked from `chcon_verify`) + `_chcon_snapshot_restore()` (`restore-chcon` subcommand). On-device snapshot at `/data/local/tmp/v3-chcon-snapshot.txt`. Idempotent (preserves baseline across resumed runs). `--snapshot-only` flag pre-arms without chcon writes. **Acceptance: kill -9 mid-chcon batch → `bash <script> restore-chcon` restores factory contexts without ROM flash.** |
| Gate 9  | atomic file swap for live-service .so replacement   | `_atomic_install()` + `_is_live_service_so()` allowlist (17 entries: libwms/libams/libbms/render_service/etc.). Inside `stage_push` Step 4: push to `<dst>.new`, verify size match, `mv <dst>.new <dst>` (rename(2) is atomic on same FS). Old mapping in already-running processes preserved by kernel deferred-unlink. Interrupt at any point leaves either old-fully-loaded or new-fully-loaded, never half-replaced. |
| Gate 10 | Island-style mount-restore on resume                | `_restore_mounts()` re-applies expected mount table. Wired into `stage_1`, `stage_3_0`, `run_uninstall` (replaces 3 prior `\|\| true` remount calls). `restore-mounts` subcommand for standalone use. Cites Island `demo/run-live.sh L35-42` (idempotent `mountpoint -q` probe pattern). |
| Gate 11 | force-stop OH Photos before display-touching stages | `_force_stop_photos()` issues `aa force-stop` for `com.ohos.photos` + `com.huawei.hmsapp.photos`. Called from `stage_3_0` (before any .so replacement) and `stage_6` (before app launch). Cites Island `demo/run-live.sh L60-62`. Defensive — Photos may hold render_service / graphics-buffer lock. |
| Gate 12 | loop-budget on launch                               | `LAUNCH_BUDGET_SECS` env (default 60s) + `_budgeted_hdc_shell()` wrapper. `stage_6` wraps both the `aa-launch.sh` invocation and the direct `aa start` fallback with `timeout`. Hung launch surfaces as rc=124 instead of pinning resources forever. Cites Island `demo/run-live.sh L71` (`-loop=10 -loop-budget=60000`). |
| Gate 13 | processdump availability check in preflight         | `_probe_processdump()` invoked from `stage_0` AFTER G3 artifact verify but BEFORE any /system write. Runs `processdump --help` and propagates device-side exit via sentinel; aborts on rc=127 (binary missing) or unexpected non-success. **Diagnostic capability is the value-add of /system over the chroot path** (chroot Phase 1b.2 was BLOCKED on missing processdump; see `V3-W2-PHASE-1B2-RETRY.md` §5). |

### Chroot-discovered safety fixes (2026-05-19 port)

| Fix | Discovered in chroot                                                | Hardened script port |
|-----|---------------------------------------------------------------------|----------------------|
| Fix A | `hdc_shell_check` helper — propagate device-side exit code via sentinel wrapper. Plain `hdc.exe shell` returns host exit 0 regardless of remote command outcome; `if hdc_shell ...` is permanently true. See `feedback_hdc_shell_check_pattern.md`. | `hdc_shell_check()` helper added. Sites converted: `restorecon`, `begetctl stop_service`, `bm uninstall`, glob `chmod`, BMS-ready check, Stage 7 marker scan, Stage 4 reboot-poll. |
| Fix B | `</dev/null` on every `hdc.exe` invocation + post-push verify via `test -s`. Without stdin redirect, hdc.exe consumes parent loop's stdin and terminates `while read` after iteration 1 (agent 71/72 findings). | `hdc_raw()` applies `</dev/null` universally. `stage_push` Step 1b adds `test -s` gate before md5 work. `stage_6` apk push adds `test -s` post-push verify. |
| Fix C | Host-side awk for `df` output parsing (DAYU200 toybox lacks awk). | Verified: hardened script already pipes all `awk` host-side. No port needed. |
| Fix D | Dual-path `liboh_adapter_bridge.so` to `/system/lib/` AND `/system/android/lib/`. `liboh_android_runtime.so` chain-resolution requires the bridge at both paths. | `stage_3f` pushes both paths; chcon_verify covers both; Stage 3.9 integrity manifest includes both; `run_uninstall` removes both. |

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
| 99 | HBC 全局三条 ABORT           | **STOP.** Do NOT reboot. Run `restore-chcon` if chcon batches had started (Gate 8). Capture hilog if possible. Investigate. |

For exit 99, follow `V3-W2-RECOVERY-PROCEDURE.md` §"Phase A": operator
hard power-cycle the board, then re-run from Stage 0.

### 6.6 Gate 8/10/13 operator subcommands (new 2026-05-19)

```bash
# Gate 8 — pre-arm chcon snapshot without writes (smoke test)
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh --snapshot-only

# Gate 8 — replay snapshot to restore factory SELinux contexts
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh restore-chcon

# Gate 10 — re-apply expected mount table after a stale-mount reboot
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh restore-mounts

# Gate 13 — standalone processdump availability probe
bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh probe-processdump
```

These are READ-ONLY-OR-RECOVERY operations (no `/system` modification beyond
chcon label restoration); safe to invoke at any time the channel is healthy.

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
| LOC                      | 284                                     | ~1700 (G1-G7 + Gate 8-13; mostly comment + per-stage isolation) |
| chcon rollback           | None                                    | Gate 8: snapshot at `/data/local/tmp/v3-chcon-snapshot.txt`; `restore-chcon` subcommand |
| live .so replacement     | Direct cp (half-replaced risk)          | Gate 9: atomic `.new` + `mv` (rename(2)) for 17 service .so |
| Stale-mount resume       | Single `mount -o remount,rw / \|\| true` | Gate 10: `_restore_mounts` + `restore-mounts` subcommand (Island pattern) |
| Photos viewer race       | No mitigation                           | Gate 11: `aa force-stop com.ohos.photos` / hmsapp before display stages |
| Hung launch              | No bound                                | Gate 12: `timeout ${LAUNCH_BUDGET_SECS}s` wrap on Stage 6 launch |
| processdump availability | No probe                                | Gate 13: preflight probe; abort BEFORE /system writes if missing |
| hdc_shell control-flow   | Trusted host exit 0 (false-positive risk) | Fix A: `hdc_shell_check` propagates device-side exit |
| Push stdin in while-loop | Iteration 2+ lost to hdc.exe stdin slurp | Fix B: `</dev/null` on `hdc_raw`; post-push `test -s` gate |
| Adapter bridge resolution | Single path `/system/lib/`             | Fix D: dual-path `/system/lib/` + `/system/android/lib/` |

## 10. Cross-references

- Script: `scripts/v3/deploy-hbc-to-dayu200-hardened.sh`
- Legacy (forensic): `scripts/v3/deploy-hbc-to-dayu200.sh`
- Chroot sibling (Gate 8/10/13 + Fix A/B/D source): `scripts/v3/deploy-hbc-to-dayu200-chroot.sh`
- Regression: `scripts/v3/run-hbc-regression.sh` (Section 1 + 2 slots)
- Postmortem: `docs/engine/V3-W2-POSTMORTEM.md`
- Recovery procedure: `docs/engine/V3-W2-RECOVERY-PROCEDURE.md`
- Chroot Phase 1b.2 substrate halt (Gate 13 motivation): `docs/engine/V3-W2-PHASE-1B2-RETRY.md`
- hdc 3.2.0b probe (H2 death certificate): `docs/engine/V3-HDC-3.2.0B-PROBE-REPORT.md`
- Chroot containment proposal (Gate 8-13 architectural context): `docs/engine/V3-CHROOT-CONTAINMENT-PROPOSAL.md`
- Westlake-adapted SOP: `docs/engine/V3-DEPLOY-SOP.md`
- Island pattern source (Gate 10/11/12): `artifacts/v3-w3-westlake-stack/16.14-Island/demo/run-live.sh` L35-42, L60-62, L71
- HBC source-of-truth: `westlake-deploy-ohos/v3-hbc/scripts/DEPLOY_SOP.md`
- HBC stage_push template: `westlake-deploy-ohos/v3-hbc/scripts/deploy_stage.sh` L125-156
- Launch model: `docs/engine/V3-LAUNCH-MODEL.md`
- Memory: `feedback_hdc_shell_check_pattern.md` (Fix A rationale), `feedback_chroot_dynamic_elf_ro_bind.md` (Stage 3 chroot mount pattern), `feedback_soft_brick_w2_2026-05-16.md` (origin postmortem)
