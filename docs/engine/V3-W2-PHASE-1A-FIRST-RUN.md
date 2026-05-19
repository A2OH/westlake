# V3 W2 Phase 1a — First Board Run

Date: 2026-05-19
Operator: agent 66
Board: DAYU200 (`dd011a414436314130101250040eac00`)
Script: `scripts/v3/deploy-hbc-to-dayu200-chroot.sh` (commit `81c9de3a`)
Mandate: Phase 1a chroot substrate validation, brick-impossible by construction.

## TL;DR

- **HALTED at Stage 2 (push)** on a script bug (not a board issue, not a soft-brick). HBC全局三条 rule #3 (`[Fail]` in output) tripped cleanly; script aborted with exit 99 as designed; channel remained alive; SELinux still Enforcing; no mounts established; no recovery attempted (per mandate).
- Root cause: `OPTIONAL_MANIFEST[2]` pushes `app/HelloWorld.apk` → device-rel `/data/local/tmp/HelloWorld.apk` (resolves under chroot to `/data/local/tmp/v3-hbc-chroot/data/local/tmp/HelloWorld.apk`), but Stage 1 only creates `$V3_CHROOT_ROOT/data`, not `$V3_CHROOT_ROOT/data/local/tmp`. `hdc file send` does not auto-mkdir parents → "no such file or directory".
- Substrate validation NOT achieved this run. Fix is mechanical (one mkdir entry in Stage 1's `dirs=` array OR move HelloWorld.apk to a chroot-rel path whose parent is already created). Brick-safety invariants HELD: 0 writes outside `$V3_CHROOT_ROOT`, board state restorable by `teardown` or `rm -rf /data/local/tmp/v3-hbc-chroot/`.

## 1. Pre-flight result

`bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh preflight` — **PASS** in ~2s.

- hdc binary: `/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe`, Ver: 3.2.0b (logged; not gated)
- Device visible: `dd011a414436314130101250040eac00`
- G1: hdc shell sentinel OK
- G3: all 63 required artifacts present locally
- WARN: could not parse `df /data` free space; toybox `df` output did not match script's awk pattern (and `awk` itself is `inaccessible or not found` on board). Manual verification shows 19 GB free on `/dev/block/mmcblk0p15` — well above the 600 MB threshold. Non-blocking; flagged in §7.
- Board pre-state captured at `/tmp/v3-chroot-deploy-current/board-state-pre.txt`
- SELinux mode: Enforcing
- WARN: `--setenforce-0` not specified; deferred decision to launch stage.

## 2. Per-stage results (gate + duration)

| Stage | Status | Duration | Notes |
|-------|--------|----------|-------|
| 0 preflight | PASS | ~2 s | non-blocking df-parse WARN |
| 1 setup | PASS | ~5 s | 22 chroot dirs verified; `/system/bin/sh` discovered + copied + verified `-x` (Tier-1 #1 fix exercised cleanly) |
| 2 push | **ABORT 99** | ~36 s (after ~50 files pushed) | `[Fail]Error opening file: no such file or directory` on HelloWorld.apk; HBC全局三条 caught it |
| 3 mount | not attempted | — | gated on Stage 2 |
| 4 env | not attempted | — | gated on Stage 2 |
| 5 launch | not attempted | — | gated on Stage 2 |
| 6 verify | not attempted | — | gated on Stage 2 |

Pushed ~50 files cleanly with channel-alive sentinels firing at the 25-file boundary; the abort happened on the 53rd push attempt (HelloWorld.apk, the 3rd OPTIONAL entry).

## 3. Launch result + marker emission

Not reached. No `launch.log`. No `[v3-chroot-launch]` marker. SELinux mode at launch decision point: N/A (script halted before Stage 5).

## 4. Post-run board state

Captured immediately post-abort:

```
getenforce                           = Enforcing  (unchanged from pre)
mount | grep v3-hbc-chroot           = 0 lines    (Stage 3 not run)
/data/local/tmp/v3-hbc-chroot/       = 22 directories + ~50 files (~375 MB)
  /system/bin/                       = appspawn-x, sh
  /system/android/framework/         = adapter-mainline-stubs.jar, apache-xml.jar, arm/, bouncycastle.jar, core-icu4j.jar, ...
  /system/lib/                       = 14 entries (subset pushed before abort)
  /data/                             = empty (the missing tmp/ subdir)
hdc shell sentinel                   = SENTINEL_POSTFAIL_1501900244 OK (channel alive)
```

Brick-safety invariants HELD:

- No writes anywhere outside `/data/local/tmp/v3-hbc-chroot/`.
- `/system` untouched.
- SELinux untouched (no `setenforce 0` in this run because launch wasn't reached and `--setenforce-0` wasn't passed).
- No bind-mounts established (Stage 3 not reached).
- Board fully responsive on both `hdc shell` and `hdc file send/recv`.
- No `hdc target boot` issued.

Per mandate, chroot tree left in place (not torn down) so a next-run retry doesn't need re-push of the ~50 already-pushed files.

## 5. Acceptance criteria (substrate validation)

| Criterion | Result |
|-----------|--------|
| Chroot exec works | N/A (Stage 5 not reached) |
| Bind-mounts visible inside chroot | N/A (Stage 3 not reached) |
| Env wrap installed correctly | N/A (Stage 4 not reached) |
| boot.art visible inside chroot | UNKNOWN (push aborted mid-stream; boot.art is in REQUIRED list which completed, so likely yes — but unverified by Stage 6) |
| Channel-alive sentinel survives launch | N/A (no launch); however, channel alive throughout the run including post-abort |
| `[v3-chroot-launch]` marker emitted | NO (Stage 5 not reached) |
| Board state pre/post identical (modulo chroot dir) | YES |

**Verdict: substrate validation NOT achieved.** No Phase 1a acceptance criterion was reached. The halt is in pre-launch plumbing, so this is not evidence for or against the substrate's viability — it's a bug in Stage 1's dir-creation manifest.

## 6. SELinux mode used

Enforcing throughout. `--setenforce-0` never passed. Script's EXIT trap `_restore_enforcing` is registered but a no-op for this run (the `SETENFORCE_0_APPLIED` flag was never set because Stage 5 was not reached). Verified post-run: `getenforce` returns `Enforcing`.

## 7. Anomalies + lessons

### Anomaly A — Stage 1 dir manifest missing `data/local/tmp` subdir (HALT cause)

`OPTIONAL_MANIFEST` at line 367 maps `app/HelloWorld.apk` → chroot-rel `/data/local/tmp/HelloWorld.apk`. `_push_one` (line 623) prepends `$V3_CHROOT_ROOT` to form `/data/local/tmp/v3-hbc-chroot/data/local/tmp/HelloWorld.apk`. The 22-entry `dirs=` array in `stage_setup` (lines 523-546) creates `$V3_CHROOT_ROOT/data` but not `$V3_CHROOT_ROOT/data/local/tmp`. `hdc file send` requires the parent dir; it does not auto-create. Result: `[Fail]Error opening file: no such file or directory` → HBC全局三条 rule #3 fires → abort exit 99.

Comment block at lines 357-363 explicitly contemplates this lands inside the chroot and reassures it's "brick-safe" — but neglected to also add the corresponding parent-dir creation. A future similar mismatch (between manifest target paths and the explicit `dirs=` skeleton) is easy to introduce.

**Lesson:** Either (a) derive the `dirs=` set from `${MANIFEST_ENTRIES##*=}` parent paths instead of maintaining it as a separate hand-curated list, or (b) add a pre-push check at end of `stage_setup` that walks both manifests and `mkdir -p` the parent of every target. Option (b) is minimally invasive.

### Anomaly B — toybox `df` output not parsed by Stage 0 (non-blocking)

Stage 0 line ~470 (`df /data | awk ...`) returned `/bin/sh:awk:inaccessibleornotfound` because OHOS DAYU200 doesn't ship `awk` and the script's pipeline runs on the board, not on host. Resulted in a WARN that swallowed the actual error message via `tr -d '\r'` collapsing. Free-space check effectively skipped. In this run it was harmless (19 GB free), but the gate is silently bypassed.

**Lesson:** Use toybox-compatible primitives only inside `hdc_shell "..."` payloads; do the parsing host-side after pulling the raw `df` output. E.g. `df_out=$(hdc_shell "df /data"); avail_kb=$(echo "$df_out" | sed -n '2p' | tr -s ' ' | cut -d' ' -f4)`.

### Anomaly C — soft-brick lessons HELD

All five soft-brick rules from `feedback_soft_brick_w2_2026-05-16.md` were honored by the chroot harness:

- Channel-alive sentinels fired at the 25-file boundary AND on every Stage entry/exit AND were verified by operator post-abort.
- No `|| true` swallowing in this script (chcon path isn't reached in Phase 1a anyway, but the discipline is in place: `_assert_no_fail_or_drwx` is called on every output).
- No silent-SKIP of required artifacts (`_push_one` calls `abort` on missing local file; preflight verifies all 63 required artifacts).
- Incremental stage-by-stage execution per mandate (not full-shot).
- No borrowed-SOP issue (chroot deploy is Westlake-authored, not adopted from HBC verbatim).

### Anomaly D — `/system/bin/sh` is `mksh`-symlink (informational)

Stage 1 Tier-1 #1 fix exercised cleanly: `/system/bin/sh` was discovered and copied to `$V3_CHROOT_ROOT/system/bin/sh`. No issue, but worth recording the path was the first candidate (not toybox/toolbox), as expected for a stock DAYU200 ROM-flash.

## 8. Recommendation

**Needs human gate on a one-line fix, then re-run.**

The fix is mechanical and low-risk. Two equivalent options:

**Option 1 (minimal):** Add `"$V3_CHROOT_ROOT/data/local/tmp"` to the `dirs=` array in `stage_setup` at line ~543 (after `"$V3_CHROOT_ROOT/data"`).

**Option 2 (defensive, recommended):** After the existing `dirs=` mkdir loop, walk both manifests and `mkdir -p` the parent dir of every target inside the chroot. This auto-prevents this class of bug for any future manifest entry. Pseudocode:

```bash
# After existing dirs=[] loop in stage_setup:
local parents=""
for entry in "${REQUIRED_MANIFEST[@]}" "${OPTIONAL_MANIFEST[@]}"; do
    rel="${entry##*=}"
    parent=$(dirname "$V3_CHROOT_ROOT$rel")
    parents="$parents $parent"
done
out=$(hdc_shell "mkdir -p $parents 2>&1")
_assert_no_fail_or_drwx "$out" "stage_setup manifest-parents mkdir"
```

After fix:

- Re-run `setup` (idempotent — should pass on the existing tree).
- Re-run `push` (most files already present; `hdc file send` overwrites — should complete in seconds for the un-pushed tail).
- Continue with `mount` → `env` → `launch` → `verify` per the original mandate.

Do NOT pivot to /system. Do NOT pivot to Phase 1b. The chroot substrate has not yet had its first launch attempt; the run was halted in plumbing, not at a substrate viability boundary. Phase 1a remains the right next step.

**Secondary recommendation (defer):** Fix Anomaly B in same patch — toybox-compatible `df` parse — since both are in the same `stage_setup`/`stage_preflight` neighborhood. Low priority; harmless if deferred.

Brick-impossibility invariant remains intact: even on the bug path, no /system write occurred, no bind-mount was left orphaned, no SELinux change happened, channel never went silent. The hard-stop discipline (HBC全局三条) caught the bug at the earliest possible boundary.
