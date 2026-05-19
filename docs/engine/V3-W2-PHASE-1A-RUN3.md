# V3 W2 Phase 1a — Board Run 3 (agent 68)

Date: 2026-05-19
Operator: agent 68
Board: DAYU200 (`dd011a414436314130101250040eac00`)
Script: `scripts/v3/deploy-hbc-to-dayu200-chroot.sh` (commit `f10ee81b`)
Mandate: retry Phase 1a after `hdc_shell_check` helper fix (agent 67 halted at Stage 3 on hdc exit-code laundering).

## TL;DR

- **HALTED at Stage 4 (env) on a NEW finding**: the chroot env probe fails with `chroot: exec /system/bin/sh: No such file or directory` because the board's `/system/bin/sh` is a **dynamic** ELF requiring the musl loader `/lib/ld-musl-arm.so.1`, which is not present inside the chroot (and the chroot directory skeleton has no `/lib/`). Stage 4's abort 99 path fires correctly and the script halts.
- **The `hdc_shell_check` fix from `f10ee81b` WORKS**. Stage 3 now actually establishes 3 bind-mounts and `mount | grep v3-hbc-chroot` independently returns 3 lines (proc, sysfs, tmpfs/dev). The control-flow `if hdc_shell_check "..."` evaluates against the device-side exit code via the `__EXIT__=$?` sentinel — verified by both Stage-3's mount table output and the post-run probe.
- Stages 0 (preflight), 1 (setup), 2 (push), **3 (mount)** all PASSED — agent 67's helper fix is validated end-to-end. The mount silent-NOOP bug is closed.
- **Brick-safety invariants HELD throughout**: 0 writes outside `$V3_CHROOT_ROOT`, SELinux still Enforcing (never touched), channel responsive throughout, no `hdc target boot` issued, no orphan mounts (3 expected bind-mounts in chroot — to be teardown'd in a later run per mandate).

## 1. Pre-flight result

`bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh preflight` — **PASS** in ~2s.

- hdc binary: `/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe`, Ver: 3.2.0b
- Device visible: `dd011a414436314130101250040eac00`
- G1: hdc shell sentinel OK
- G3: all 63 required artifacts present locally
- `/data` free space: **18478 MB** (>= 600 MB required)
- Board pre-state captured at `/tmp/v3-chroot-deploy-current/board-state-pre.txt`
- SELinux mode: Enforcing
- WARN: `--setenforce-0` not specified; deferred decision to launch stage.

## 2. Per-stage results (gate + duration)

| Stage | Status | Duration | Notes |
|-------|--------|----------|-------|
| 0 preflight | PASS | ~2 s | unchanged from run 2 |
| 1 setup | PASS | ~6 s | 24 chroot dirs verified, `/system/bin/sh` re-copied + `-x` verified |
| 2 push | PASS | ~32 s | 70 files pushed, 3 size-verified (libwms.z.so 1072868, framework.jar 40087842, appspawn-x 110256); channel-alive sentinels at 25/50 boundaries; no [Fail] |
| 3 mount | **PASS (REAL)** | ~2 s | 3 bind-mounts established AND independently verified: proc on …/proc, sysfs on …/sys, tmpfs on …/dev. `hdc_shell_check` fix validated. |
| 4 env | **ABORT 99** | <1 s | `chroot: exec /system/bin/sh: No such file or directory` — chroot sh is dynamic ELF, ld-musl-arm.so.1 absent from chroot. See §7. |
| 5 launch | not attempted | — | gated |
| 6 verify | not attempted | — | gated |

## 3. Launch result + marker emission

Not reached. No `launch.log` (launch.sh was generated + installed by Stage 4 before the probe failed; no execution attempt). No `[v3-chroot-launch]` marker. SELinux mode at halt: Enforcing.

## 4. Post-run board state

Captured immediately post-halt:

```
POST_STAGE4_1501901235                = OK (channel alive)
getenforce                            = Enforcing (unchanged from pre)
mount | grep v3-hbc-chroot | wc -l    = 3 (bind-mounts ESTABLISHED — fix works)
  proc on .../proc type proc          (rw,relatime,gid=3009,hidepid=invisible)
  sysfs on .../sys type sysfs         (rw,seclabel,relatime)
  tmpfs on .../dev type tmpfs         (rw,seclabel,nosuid,relatime,size=995596k,...)
/data/local/tmp/v3-hbc-chroot/        = 24 directories + 71 files
  /system/bin/                        = appspawn-x (110256), sh (235204, dynamic ELF)
  /system/lib/                        = full set (libapk_installer, libappms, libbms,
                                         liboh_*, libwms, ...)
  /system/android/framework/          = framework.jar (40087842) + AOSP-14 jars + arm/boot.art
  /lib/                               = ABSENT (root cause of Stage 4 halt)
  /data/local/tmp/HelloWorld.apk      = present
launch.sh                             = installed at chroot /launch.sh (chmod 755)
```

Brick-safety invariants HELD:

- No writes anywhere outside `/data/local/tmp/v3-hbc-chroot/`.
- `/system` untouched.
- SELinux untouched (`getenforce` = Enforcing pre/post). `--setenforce-0` not passed; Stage 5 not reached.
- **3 bind-mounts now active inside chroot** — expected, not orphan; left in place per mandate ("DO NOT teardown — leave for follow-up").
- Board responsive on both `hdc shell` and `hdc file send/recv` throughout.
- No `hdc target boot` issued.

Per mandate, chroot tree + bind-mounts left in place (no teardown).

## 5. Acceptance criteria (substrate validation)

| Criterion | Result |
|-----------|--------|
| Chroot exec works | **NO** — chroot/sh dynamic-loader gap |
| Bind-mounts visible inside chroot | **YES** — 3 of 3 (Stage 3 fix validated) |
| Env wrap installed correctly | Partially — launch.sh installed, but exec probe fails |
| boot.art visible inside chroot | UNVERIFIED at runtime (push manifest landed; ls probe not reached because chroot exec failed) |
| Channel-alive sentinel survives launch | N/A (no launch); channel alive throughout |
| `[v3-chroot-launch]` marker emitted | NO (Stage 5 not reached) |
| Board state pre/post identical (modulo chroot dir + 3 expected mounts) | YES |

**Verdict: substrate validation NOT achieved.** Halt is again in Stage-4 plumbing (chroot missing dynamic linker for the shell binary), not at a substrate viability boundary. Mount layer is now real and verified.

## 6. SELinux mode used

Enforcing throughout. `--setenforce-0` never passed. Script's EXIT trap `_restore_enforcing` is a no-op for this run. Verified post-run: `getenforce` returns `Enforcing`.

## 7. Anomalies + lessons

### Anomaly I — Stage 4 chroot exec fails: dynamic linker not in chroot (HALT cause)

**Symptom.** Stage 4 env probe output:

```
chroot: exec /system/bin/sh: No such file or directory
```

with `chroot $V3_CHROOT_ROOT /system/bin/sh /launch.sh /system/bin/sh -c 'pwd; ls …'` (Stage 4 line ~856). Script aborts with code 99 per the `_assert_no_fail_or_drwx`-adjacent "saw 'No such'" check (the abort message lists `sh` and `framework.jar` as candidate causes; here it's the former).

**Root cause.** Two-layer:

1. The board's `/system/bin/sh` is a dynamic ELF: `file /system/bin/sh` reports `ELF 32-bit LSB arm, EABI5, dynamic (/lib/ld-musl-arm.so.1)`. The dynamic loader path `/lib/ld-musl-arm.so.1` is hard-coded as the program interpreter (`PT_INTERP`) at link time.

2. The chroot skeleton (Stage 1 `dirs=` array, lines 542-567) does NOT include `/lib/` and Stage 2 does not push the linker. After `chroot`, the kernel's exec path looks for `/lib/ld-musl-arm.so.1` *under the new root* and finds nothing → ENOENT propagated as "No such file or directory" against the program path (this is the Linux misleading-but-canonical ENOENT-on-missing-interp message).

Verified directly:

```
$ hdc shell 'file /data/local/tmp/v3-hbc-chroot/system/bin/sh'
… ELF shared object, 32-bit LSB arm, EABI5, soft float, dynamic
(/lib/ld-musl-arm.so.1), BuildID=…, stripped

$ hdc shell 'ls /data/local/tmp/v3-hbc-chroot/lib 2>&1'
ls: /data/local/tmp/v3-hbc-chroot/lib: No such file or directory

$ hdc shell 'ls -la /lib/ld-musl-arm.so.1'
-rwxr-xr-x 1 root shell 1235180 2026-04-04 16:39 /lib/ld-musl-arm.so.1
```

Loader on host filesystem, gap inside chroot.

**Why the script's Stage 1 doesn't cover this.** Stage 1's `cp /system/bin/sh → chroot/system/bin/sh` was added as a Tier-1 fix in review 400642fa specifically because Stage 2 only pushes appspawn-x into `/system/bin/`. That fix correctly establishes the executable but doesn't bring its runtime dependency — the dynamic loader. Stage 1 implicitly assumed the board shell is static.

The deeper issue: the chroot skeleton itself was designed around the AOSP-rooted layout (`/system/lib/…`) and not around the OHOS host musl layout (`/lib/ld-musl-arm.so.1`, `/lib/libc.so`). For the chroot's sh to load, it needs at least `/lib/` populated with the loader and whatever transitive deps `ldd /system/bin/sh` lists.

**Minimum-disturbance fix proposals (NOT YET APPLIED — per mandate hard-stop).**

Three viable directions, in increasing order of intrusiveness:

**Option A (small — add /lib + linker to Stage 1).** Extend `dirs=` to include `$V3_CHROOT_ROOT/lib`. Then in Stage 1, after the existing `cp /system/bin/sh ...`, also:

```bash
cp /lib/ld-musl-arm.so.1   $V3_CHROOT_ROOT/lib/
# Discover transitive deps and copy them too
for so in $(readelf -d /system/bin/sh 2>/dev/null | awk -F'[][]' '/NEEDED/{print $2}'); do
    src=$(find /lib /system/lib -name "$so" 2>/dev/null | head -1)
    [ -n "$src" ] && cp "$src" $V3_CHROOT_ROOT/lib/
done
```

(Run remotely via `hdc_shell` so the entire population stays inside the chroot directory. Brick-safe: writes only to `$V3_CHROOT_ROOT`.)

Note: `readelf` is unlikely to be present in the OHOS base — fall back to `ldd` or a hard-coded list (`libc.so`, etc.) discovered from a one-off probe.

**Option B (small — use a static shell binary).** If a static `toybox` or `busybox-static` is bundled in HBC artifacts (none currently in the manifest — verify), copy that as `/system/bin/sh` instead and skip the loader bundling. Cleanest if such a binary exists.

**Option C (architectural — populate /lib from board /lib).** Bind-mount `/lib` read-only into the chroot at Stage 3 (alongside proc/sys/dev). Brick-safe (bind-mount, no copy), but expands chroot's view of the host filesystem beyond what the proposal currently sanctions and may interfere with later attempts to use the chroot's own `/system/lib`.

Per mandate hard-stop discipline (rule 8: any control-flow not visibly demonstrating its remote-side evaluation → STOP + investigate; same family as silent-skip), I am NOT applying any of these. The fix choice itself has architectural consequences (e.g. does `appspawn-x` later need a different loader? Does framework.jar's native side?) that warrant the human gate.

### Anomaly J — agent 67's `hdc_shell_check` helper WORKS CLEANLY (positive finding)

Confirming the fix end-to-end:

- Script line 760: `if hdc_shell_check "$cmd"; then ok "$mp already mounted"; else ... mount --bind ...`. On a fresh chroot whose mount targets are NOT mounts, the helper returns 1 (via the `__EXIT__=$?` sentinel pinned by the `mountpoint -q` device-side exit), the script takes the else branch, executes `mount --bind`, and the followup `if ! hdc_shell_check "mountpoint -q ..."` correctly takes the false branch (verify succeeded).
- Direct verification: `mount | grep v3-hbc-chroot` returns 3 lines, all 3 expected bind-mounts present.
- Stage 3 reported the actual mount-table output as part of its PASS line, not just "already mounted (idempotent)" — proves the do-mount branch was taken.

The silent-NOOP anti-pattern is closed for this caller. The wider audit (every `hdc_shell` used in control-flow context) per agent 67's Tier-1 recommendation is still outstanding — but the two Stage-3 callers were the only ones in the script that actually drove brick-relevant control flow, so the immediate brick-risk is closed.

### Anomaly K — chroot tree from agent 67's partial run was preserved + reused

Pre-state showed 24 dirs + 71 files already present from agent 67. Stage 1 stat-loop verified each dir; Stage 2 push overwrote with identical bytes (size-verified samples match). No artifact mismatch.

### Anomaly L — `_alive_probe` continues to fire cleanly

Channel-alive sentinels fired at Stage 0/1/2/3/4 entry+exit AND at Stage 2's 25/50-file boundaries. Operator-side sentinel post-halt also passed. Channel never went silent, matching the soft-brick lesson rule "probe `hdc shell` echo-sentinel BETWEEN every Stage".

## 8. Recommendation

**Needs human gate on the Stage 1/4 linker-population strategy, then re-run.**

This is the third consecutive Phase 1a run halted in plumbing, but the pattern is healthy: each iteration fixes the prior failure mode and reveals the next previously-masked one. The substrate viability question is still unanswered — chroot exec has never succeeded, so we have no signal on whether `chroot $V3_CHROOT_ROOT /system/bin/sh /launch.sh` would actually run the launch.sh body inside an Enforcing chroot.

Recommended next step: pick Option A (smallest, most contained) — add `/lib/` to the chroot skeleton and copy ld-musl-arm.so.1 (plus the small set of libc transitives) inside Stage 1. Re-run setup→push→mount→env. If Stage 4 env probe then emits `[v3-chroot-launch]`, substrate viability is proven at the sh level and we can attempt the launch path. If env still ENOENTs, the gap is something other than the loader (e.g. the etc/ld-musl resolver config) and we iterate.

Do NOT pivot to /system. Do NOT pivot to Phase 1b. The chroot substrate has STILL not had its first launch attempt; the run was halted again in plumbing, not at a substrate viability boundary.

Brick-impossibility invariant remains intact: no /system write, 3 expected (not orphan) bind-mounts, no SELinux change, channel never went silent, no `hdc target boot`. The hard-stop discipline (HBC全局三条 + soft-brick lessons §17 + new rule 8 on control-flow visibility) caught the next bug at the earliest possible boundary, again.

**Pattern note for the V3 narrative.** Three consecutive Phase 1a runs halted in plumbing (agent 66: mkdir manifest miss; agent 67: hdc exit-code laundering; agent 68: chroot dynamic-linker gap). All three are pre-launch script bugs in different layers of the deploy harness — manifest, control-flow integrity, runtime-dep population — each <30 lines to fix in isolation. Each fix successfully unblocked one more stage. This pattern does NOT yet meet the `feedback_two_pivots_in_two_days.md` pivot bar: three retries are in the *same* deploy-harness W-level (W2), but each halt has been at a *different* stage with a *different* symptom and a *different* root-cause class. That's the "iterating on a single architecture" signature, not the "symptom-rotation" signature that signals the architecture itself is wrong. Continue iterating on the deploy harness; do not re-think the chroot architecture itself.

The brick-safety discipline is working as designed: every halt is at a state the operator can safely inspect, every halt preserves enough state for the next run to resume from, and no halt has required board recovery.
