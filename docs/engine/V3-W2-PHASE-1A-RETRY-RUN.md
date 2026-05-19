# V3 W2 Phase 1a — Board Retry Run (agent 67)

Date: 2026-05-19
Operator: agent 67
Board: DAYU200 (`dd011a414436314130101250040eac00`)
Script: `scripts/v3/deploy-hbc-to-dayu200-chroot.sh` (commit `8771e59c`)
Mandate: retry Phase 1a after Stage 1 dirs + df awk fixes (agent 66 halted at Stage 2 on missing parents).

## TL;DR

- **HALTED at Stage 3 (mount) on a NEW finding**: Stage 3 reported `PASS` but no bind-mounts were actually established. The idempotency probe `if hdc_shell "mountpoint -q ..." >/dev/null 2>&1` is permanently TRUE because `hdc_shell` (script line 190-196) returns host exit 0 unconditionally — the pipe through `tr -d '\r'` swallows the remote exit code. On a fresh chroot whose `proc/`, `sys/`, `dev/` bind-target dirs exist as empty directories (set up at Stage 1), the script takes the "already mounted" branch for all three mounts and never calls `mount --bind`. Verified by `mount | grep v3-hbc-chroot` returning zero lines after Stage 3 PASS.
- This is the same anti-pattern that caused the 2026-05-16 W2 soft-brick (silent-SKIP via `|| true`-style exit-laundering). Per `feedback_soft_brick_w2_2026-05-16.md` and the mandate's hard-stop discipline, HALT here pending the human gate on a fix.
- Stages 0 (preflight), 1 (setup), 2 (push) all PASSED — agent 66's fixes worked. The 70-file manifest pushed cleanly; HelloWorld.apk landed at `/data/local/tmp/v3-hbc-chroot/data/local/tmp/HelloWorld.apk`; `df` free-space gate now reports 18478 MB (host-side awk fix works).
- **Brick-safety invariants HELD throughout**: 0 writes outside `$V3_CHROOT_ROOT`, 0 mounts established (so 0 orphan mounts to clean up), SELinux still Enforcing, channel responsive throughout, no `hdc target boot` issued.

## 1. Pre-flight result

`bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh preflight` — **PASS** in ~2s.

- hdc binary: `/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe`, Ver: 3.2.0b
- Device visible: `dd011a414436314130101250040eac00`
- G1: hdc shell sentinel OK
- G3: all 63 required artifacts present locally
- `/data` free space: **18478 MB** (>= 600 MB required) — host-side awk fix works
- Board pre-state captured at `/tmp/v3-chroot-deploy-20260519-120118/board-state-pre.txt`
- SELinux mode: Enforcing
- WARN: `--setenforce-0` not specified; deferred decision to launch stage.

## 2. Per-stage results (gate + duration)

| Stage | Status | Duration | Notes |
|-------|--------|----------|-------|
| 0 preflight | PASS | ~2 s | df parse now works (host-side awk) |
| 1 setup | PASS | ~5 s | 24 chroot dirs verified (was 22; +2 from fix: `data/local`, `data/local/tmp`); `/system/bin/sh` verified `-x` (already present from agent 66) |
| 2 push | **PASS** | ~21 s | 70 files pushed, 3 size-verified, channel-alive sentinels at 25/50 boundaries; HelloWorld.apk landed cleanly (was the halt cause last run) |
| 3 mount | **PASS-but-NOOP** | ~1 s | reports PASS, but `mount \| grep v3-hbc-chroot` = 0 lines post-Stage 3. NEW FINDING — see §7. |
| 4 env | not attempted | — | halted pending human gate on Stage 3 noop |
| 5 launch | not attempted | — | gated |
| 6 verify | not attempted | — | gated |

## 3. Launch result + marker emission

Not reached. No `launch.log`. No `[v3-chroot-launch]` marker. SELinux mode at halt: Enforcing.

## 4. Post-run board state

Captured immediately post-halt:

```
SENTINEL_1501900785                   = OK (channel alive)
getenforce                            = Enforcing (unchanged from pre)
mount | wc -l                         = 48 lines (system mounts only)
mount | grep -c v3-hbc-chroot         = 0  (Stage 3 NEVER ESTABLISHED any binds)
mountpoint -q .../proc                = exit 1 (NOT a mountpoint)
mountpoint -q .../sys                 = exit 1 (NOT a mountpoint)
mountpoint -q .../dev                 = exit 1 (NOT a mountpoint)
ls .../proc                           = empty (consistent with no bind-mount)
ls .../dev                            = empty (consistent with no bind-mount)
/data/local/tmp/v3-hbc-chroot/        = 24 directories + 70+ files (~from prior + this run)
  /system/bin/                        = appspawn-x, sh (chmod 755)
  /system/android/framework/          = adapter-mainline-stubs.jar, apache-xml.jar, arm/, bouncycastle.jar, core-icu4j.jar, framework.jar (40087842 bytes), …
  /system/lib/                        = full set
  /data/local/tmp/HelloWorld.apk      = present (Stage 2 fix verified)
```

Brick-safety invariants HELD:

- No writes anywhere outside `/data/local/tmp/v3-hbc-chroot/`.
- `/system` untouched.
- SELinux untouched (no `setenforce 0` — Stage 5 not reached and `--setenforce-0` not passed).
- **No bind-mounts established** — paradoxically the silent-skip is brick-SAFE because the failure mode is "do nothing" rather than "mount and forget to unmount". No orphan mounts to clean.
- Board responsive on both `hdc shell` and `hdc file send/recv` throughout.
- No `hdc target boot` issued.

Per mandate, chroot tree left in place (no teardown).

## 5. Acceptance criteria (substrate validation)

| Criterion | Result |
|-----------|--------|
| Chroot exec works | N/A (Stage 4 env probe not reached) |
| Bind-mounts visible inside chroot | **NO** — Stage 3 silently noop'd |
| Env wrap installed correctly | N/A (Stage 4 not reached) |
| boot.art visible inside chroot | UNVERIFIED (push completed but Stage 4 ls probe not reached); push manifest includes `arm/boot.art` |
| Channel-alive sentinel survives launch | N/A (no launch); channel alive throughout |
| `[v3-chroot-launch]` marker emitted | NO (Stage 5 not reached) |
| Board state pre/post identical (modulo chroot dir) | YES |

**Verdict: substrate validation NOT achieved.** Halt is in Stage-3 plumbing (silent-skip via hdc exit-code laundering), not at a substrate viability boundary. Continuing past it would knowingly violate the soft-brick lesson.

## 6. SELinux mode used

Enforcing throughout. `--setenforce-0` never passed. Script's EXIT trap `_restore_enforcing` is a no-op for this run. Verified post-run: `getenforce` returns `Enforcing`.

## 7. Anomalies + lessons

### Anomaly E — Stage 3 silent-NOOP via hdc exit-code laundering (HALT cause)

**Symptom.** Stage 3 reports `[PASS] Stage 3 PASS — /proc /sys /dev bind-mounted into chroot` with three `OK ... already mounted into chroot (idempotent)` lines. Direct verification immediately after shows `mount | grep v3-hbc-chroot` returns 0 lines and the bind-target dirs are empty.

**Root cause.** Script line 742:

```bash
if hdc_shell "$cmd" >/dev/null 2>&1; then
    ok "$mp already mounted into chroot (idempotent)"
else
    out=$(hdc_shell "mount --bind /$mp $V3_CHROOT_ROOT/$mp 2>&1")
    ...
fi
```

`hdc_shell` (line 190-196):

```bash
hdc_shell() {
    if [ "$DRY_RUN" = "1" ]; then
        echo "[DRY] hdc shell $*"
        return 0
    fi
    "$HDC" -t "$HDC_SERIAL" shell "$@" 2>&1 | tr -d '\r'
}
```

Two compounding issues:

1. `hdc shell` (Windows hdc.exe 3.2.0b) returns host exit 0 regardless of remote command exit code — verified directly: `hdc shell 'false; echo remote_exit=$?'` prints `remote_exit=1 host_exit=0`.
2. The pipe through `tr -d '\r'` further launders any signal; bash sees the last command in the pipeline (`tr`), which always succeeds on non-empty input.

Result: `if hdc_shell "mountpoint -q .../proc"` is permanently TRUE → script takes the "already mounted" branch on every fresh chroot whose Stage-1 bind-target dirs exist, and never invokes `mount --bind`. The followup `_alive_probe` and `_assert_no_fail_or_drwx` don't catch this because there's no fail token to find in the (empty) output.

**Why this matches the soft-brick anti-pattern.** `feedback_soft_brick_w2_2026-05-16.md` rule: "never silent-SKIP a required artifact". Stage 3 here silently SKIPs all three required mounts and reports PASS. This is the same family of bug — only the mechanism differs (here: exit-code laundering through `hdc shell` + `tr`; there: `chcon ... || true` swallowing errors). On a different board state where mounts ARE in fact present (e.g. agent 66's run had no mounts so the same noop would apply, just less visibly), this still misrepresents reality.

**Minimum fix.** Two equivalent options:

**Option A (state-check, recommended).** Replace the idempotency probe with a direct mount-table inspection that doesn't depend on remote exit code:

```bash
local mtab
mtab=$(hdc_shell "mount" | grep "$V3_CHROOT_ROOT/$mp ")
if [ -n "$mtab" ]; then
    ok "$mp already mounted into chroot (idempotent)"
else
    ...
fi
```

This eats the hdc exit code entirely and inspects state directly. Same pattern as the existing summary print at line 758.

**Option B (sentinel-token, defensive).** Force `hdc_shell` callers that care about exit codes to use an explicit sentinel:

```bash
if hdc_shell "mountpoint -q $V3_CHROOT_ROOT/$mp && echo YES_MOUNT || echo NO_MOUNT" | grep -q YES_MOUNT; then
    ...
```

Less invasive (one-line change at the call site) but easy to forget elsewhere. Audit needed: every `hdc_shell` use whose return value or exit code drives control flow should be reviewed under the same lens.

**Wider audit needed (Tier 1).** Grep for `hdc_shell` calls used in `if`/`while`/`&&`/`||` contexts. From `scripts/v3/deploy-hbc-to-dayu200-chroot.sh`:

- Line 742: `if hdc_shell "mountpoint -q ..."` — BROKEN (this finding)
- Line 748: `if ! hdc_shell "mountpoint -q ..."` — would-be the "verify mount took effect" check; would also be permanently TRUE, so the verify-after-mount path would never abort. Double-broken.

The verify-after-mount also being broken means even if Stage 3 had taken the "do mount" branch, a silent mount failure (e.g. EPERM under Enforcing) would still be reported as PASS. The redundancy here makes the silent-skip especially dangerous on a different board state.

### Anomaly F — agent 66's two fixes WORKED CLEANLY (positive finding)

Confirming what the dry-run already showed:

- Stage 1 `dirs=` array now includes `$V3_CHROOT_ROOT/data/local` + `$V3_CHROOT_ROOT/data/local/tmp` → 24 dirs verified (was 22).
- Stage 0 `df /data` parse now reports `18478MB (>= 600MB required)` instead of failing on missing device-side `awk`.
- Stage 2 push completes cleanly; HelloWorld.apk lands at the expected path.

Both fixes are validated end-to-end.

### Anomaly G — chroot tree from agent 66's partial run was preserved + reused

Pre-state showed 22 dirs already present from agent 66. Stage 1 stat-loop verified each as a directory (the new `data/local` + `data/local/tmp` were created via `mkdir -p`, no error). Stage 2 push overwrote ~50 already-present files and added the remaining ~20. No artifact mismatch.

### Anomaly H — `_alive_probe` continues to fire cleanly

Channel-alive sentinels fired at Stage 0/1/2/3 entry+exit AND at Stage 2's 25-file and 50-file boundaries. Operator-side sentinel post-halt also passed. The channel never went silent, matching the soft-brick lesson rule "probe `hdc shell` echo-sentinel BETWEEN every Stage".

## 8. Recommendation

**Needs human gate on the Stage 3 fix, then re-run.**

The fix is mechanical and low-risk (Option A in §7). The wider audit (every `hdc_shell` used in control-flow position) is the Tier-1 follow-up.

After fix:

- Re-run `setup` (idempotent — passes on existing tree).
- Re-run `push` (idempotent — hdc file send overwrites).
- Re-run `mount` — should now actually establish 3 bind-mounts AND verify they took effect.
- Continue with `env` → `launch` → `verify` per the original mandate.

Do NOT pivot to /system. Do NOT pivot to Phase 1b. The chroot substrate has STILL not had its first launch attempt; the run was halted again in plumbing, not at a substrate viability boundary. Phase 1a remains the right next step.

Brick-impossibility invariant remains intact: even on this bug path, no /system write, no orphan mount, no SELinux change, channel never went silent, no `hdc target boot`. The hard-stop discipline (HBC全局三条 + soft-brick lessons §17) caught the silent-skip at the earliest possible boundary via direct state verification.

**Pattern note for the V3 narrative.** This is the second consecutive Phase 1a run halted in plumbing (agent 66: mkdir manifest miss; agent 67: hdc exit-code laundering). Both are pre-launch script bugs, both caught cleanly by the hard-stop / soft-brick disciplines, both fixable in <10 lines. This does NOT yet meet the `feedback_two_pivots_in_two_days.md` pivot bar (no ≥3 W-level retries with symptom-rotation, no substrate-viability evidence either way). Continue iterating on the deploy harness; do not re-think the chroot architecture itself.
