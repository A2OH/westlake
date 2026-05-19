# V3-DEPLOY-CHROOT-SOP — Phase 1 brick-impossible deploy procedure

**Status:** AUTHORITATIVE for the V3-W2 chroot-containment retry path.
**Date:** 2026-05-19.
**Authoring agent:** 62 (W14 deliverable; companion to
`scripts/v3/deploy-hbc-to-dayu200-chroot.sh`).
**Companion docs:**
[`V3-CHROOT-CONTAINMENT-PROPOSAL.md`](V3-CHROOT-CONTAINMENT-PROPOSAL.md)
(the spec this implements),
[`V3-W2-POSTMORTEM.md`](V3-W2-POSTMORTEM.md) (brick mechanism),
[`V3-HDC-3.2.0B-PROBE-REPORT.md`](V3-HDC-3.2.0B-PROBE-REPORT.md)
(hdc transport + chcon EPERM evidence),
[`V3-HBC-ARTIFACT-MANIFEST.md`](V3-HBC-ARTIFACT-MANIFEST.md) (the
563-file inventory),
[`V3-DEPLOY-HARDENED-SOP.md`](V3-DEPLOY-HARDENED-SOP.md) (the /system
sibling path; Phase 2 substrate).

---

## 1. Scope and motivation

The W2 retry needs a deploy path with the property that **no failure can
brick the board**. The hardened `/system` deploy
(`scripts/v3/deploy-hbc-to-dayu200-hardened.sh`) reduces brick *rate*
via G1-G7 but does not change brick *class*: any write to host
`/system/` carries the risk that a misordered or mislabeled state leaves
init unable to come back up, and recovery depends on a shell that may be
dead by the time the failure becomes visible.

This SOP describes the chroot-containment alternative
(`scripts/v3/deploy-hbc-to-dayu200-chroot.sh`):

* Every artifact lands under `/data/local/tmp/v3-hbc-chroot/`.
* No writes to host `/system/` anywhere.
* No `hdc target boot`.
* Recovery is `rm -rf /data/local/tmp/v3-hbc-chroot/` (single shell
  command, brick-impossible).

The Island team (`artifacts/v3-w3-westlake-stack/16.14-Island/`) runs an
analogous chroot stack on the same hardware class with 77/77 smoke
pass, validating the pattern empirically (proposal §2.5).

---

## 2. Acceptance criteria (Phase 1)

Phase-1 success ≡ all of the following hold after running the
seven-stage flow:

1. Every required artifact is present under `$V3_CHROOT_ROOT` with
   matching byte counts on the three spot-checked samples (Stage 2).
2. `/proc`, `/sys`, `/dev` are bind-mounted into the chroot (Stage 3).
3. `chroot $V3_CHROOT_ROOT /system/bin/sh /launch.sh` runs to clean exit
   within `$LAUNCH_BUDGET_SECS` (default 60s), emitting the
   `[v3-chroot-launch]` marker in stdout (Stage 5/6).
4. Channel-alive sentinel (`echo` round-trip) succeeds at end-of-Stage
   6 (Stage 6).
5. No `avc: denied` lines in the last 100 hilog entries (Stage 6
   warn-not-fail, but absence is the goal).
6. Teardown (`teardown` command) cleans the chroot to a state where
   `ls -ld $V3_CHROOT_ROOT` returns `No such file or directory`.

**Phase-1 explicitly does NOT require:**

* HBC HelloWorld.apk's `MainActivity.onCreate L83` (that's the **HBC**
  acceptance; reachable in Phase 1 only with `$V3_CHROOT_HELLO_CMD` set
  to an ART/aa-launch invocation — gated behind W4/W5/W6).
* on-screen render via factory `render_service` (Phase 1 is headless).
* `appspawn-x` running as OH init service in the `appspawn:s0` domain
  (Phase 1 invokes appspawn-x manually from shell, in `sh:s0` —
  per proposal §3.3).

Once Phase-1 PASS lands, Westlake declares "first-light reached in
containment." Phase 2 (selective `/system` writes per proposal §5)
becomes a separate workstream gated by human decision.

---

## 3. Per-stage operator instructions

The chroot script supports nine commands matching the stages plus `all`,
`teardown`, `status`, `help`. The flow:

```
preflight → setup → push → mount → env → launch → verify
                                                       ↓
                                                  teardown (recovery)
```

### 3.1 One-command full deploy

```bash
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh all
```

Runs stages 0 → 6 sequentially. If any stage aborts (exit 99 = HBC
全局三条), the script stops and reports the trip cause; do NOT manually
retry without operator review.

For SELinux Enforcing boards, add `--setenforce-0` to opt into the
Phase-1 caveat (see §5).

### 3.2 Stage-by-stage manual flow (RECOMMENDED for first retry)

```bash
# Stage 0 — preflight (READ-ONLY)
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh preflight

# Stage 1 — create chroot dir skeleton
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh setup

# Stage 2 — push 100+ artifacts (largest stage; ~5-10 min)
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh push

# Stage 3 — bind-mount /proc /sys /dev
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh mount

# Stage 4 — install launch.sh env wrapper
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh env

# Stage 5 — chroot launch (default = env-probe-only)
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh launch

# Stage 6 — verify markers + AVC scan + restore SELinux
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh verify
```

Each stage is invocable alone and idempotent for the safe ones
(`setup`, `mount`, `status`).

### 3.3 Stage details

| Stage | Script command | Writes? | Channel sentinel | Notes |
|-------|----------------|---------|------------------|-------|
| 0     | `preflight`    | NO (READ-ONLY) | 2× | Verifies hdc reachable, 63 artifacts present, ≥600 MB free in `/data`, captures pre-state to `/tmp/v3-chroot-deploy-<TS>/board-state-pre.txt`. hdc version is LOGGED not GATED (probe report widens 3.2.0b). |
| 1     | `setup`        | YES (mkdir 22 dirs under chroot + board-internal `cp` of shell binary into `$V3_CHROOT_ROOT/system/bin/sh`) | 2× | `mkdir -p` is idempotent. Verifies each dir is a `directory` (catches the hdc 造目录 quirk if it ever fires here). Then auto-discovers the board's shell binary in this order: `/system/bin/sh` → `/system/bin/toybox` → `/system/bin/toolbox`. First-match is `cp`'d to `$V3_CHROOT_ROOT/system/bin/sh` (`chmod 755` + `test -x` verified). Aborts if none exist. Required for Stage 4/5 chroot exec to find an interpreter for `launch.sh`. |
| 2     | `push`         | YES (~108 `hdc file send`) | every 25 files + 2× | Pushes 63 required + ~41 AOSP natives + 3 dual-path shims + 0-3 optional. Spot-size-checks 3 sentinel files. |
| 3     | `mount`        | YES (mount --bind) | 2× | Idempotent (mountpoint check first). Mounts `/proc`, `/sys`, `/dev`. |
| 4     | `env`          | YES (one file: `launch.sh`) | 2× | Generates launch.sh wrapper inside chroot with BOOTCLASSPATH, LD_LIBRARY_PATH, ANDROID_ROOT etc. Runs a chroot env-probe to confirm framework.jar visible. |
| 5     | `launch`       | YES (chroot exec; no FS writes) | 2× | Force-stops OH Photos defensively. Runs `chroot $V3_CHROOT_ROOT /system/bin/sh /launch.sh [$V3_CHROOT_HELLO_CMD]` under `timeout $LAUNCH_BUDGET_SECS`. If `--setenforce-0` is set, switches to Permissive for this stage only. |
| 6     | `verify`       | YES (setenforce 1 if needed) | 2× | Restores SELinux to Enforcing. Greps launch log for marker. Scans hilog for `avc: denied`. Captures post-state. |

### 3.4 Teardown (recovery)

```bash
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh teardown
```

Idempotent: umounts `/dev`, `/sys`, `/proc` (each guarded by
`mountpoint -q`) then `rm -rf` the chroot. Asserts `ls -ld
$V3_CHROOT_ROOT` returns `No such file or directory` post-rm. Safe to
run on any partial state.

### 3.5 Status (READ-ONLY inspector)

```bash
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh status
```

Prints chroot directory listing, `du -sh`, active mounts, presence of
key files (framework.jar, appspawn-x, boot.art), and SELinux mode. Zero
writes.

### 3.6 Dry-run

```bash
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh all --dry-run
```

Prints every hdc invocation without executing. Useful for inspecting
what the script *would* do before a real run. The hdc version probe and
G1 sentinel are skipped (logged as `[DRY]`).

### 3.7 Custom inner command

By default Stage 5 runs `launch.sh` with no extra arg = env-probe-only
mode (prints the env, exits 0). To run a real inner command (e.g., an
ART/aa-launch invocation):

```bash
export V3_CHROOT_HELLO_CMD="/system/bin/appspawn-x --socket-name AppSpawnX"
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh launch --setenforce-0
```

The marker the verify stage greps for is overridable via `MARKER` env
(default: `[v3-chroot-launch]`; switch to `MainActivity.onCreate` for
HBC HelloWorld validation).

### 3.8 Log directory across invocations

In the stage-by-stage flow (§3.2), each invocation needs to find logs
written by previous invocations (specifically, `verify` reads
`launch.log` written by `launch`). The script handles this via:

* Default: writes to `/tmp/v3-chroot-deploy-<TS>/` and refreshes the
  symlink `/tmp/v3-chroot-deploy-current → /tmp/v3-chroot-deploy-<TS>/`
  on every invocation. The script's `$LOG_DIR` resolves through this
  symlink, so subsequent invocations read the most-recent run.
* Operator override: `export V3_CHROOT_LOG_DIR=/tmp/my-tagged-run` to
  pin all invocations to a specific tagged directory (useful for
  parallel experiments or named regressions). When set, the symlink is
  NOT touched.

Either way, forensic per-TS subdirs persist for diff across runs; only
the symlink target rotates.

```bash
# Tagged run (all stages write to /tmp/my-run-1):
export V3_CHROOT_LOG_DIR=/tmp/my-run-1
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh launch --setenforce-0
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh verify --setenforce-0
unset V3_CHROOT_LOG_DIR
```

### 3.9 SELinux restore safety net

When `--setenforce-0` is passed, the script registers an EXIT trap that
restores SELinux to Enforcing if Stage 5 actually issued `setenforce 0`
in this process. This covers:

* Normal exit (Stage 6 verify happy path — explicit restore + redundant
  trap fire as no-op).
* Abort (e.g., `_alive_probe` exit-99 between launch and verify) — trap
  restores before exit.
* `teardown --setenforce-0` invocation — defense-in-depth: if board is
  currently Permissive, restores Enforcing.

Not covered: `kill -9` of the script process (the trap doesn't fire on
SIGKILL). Operator manual recovery: `hdc shell setenforce 1`.

---

## 4. Failure modes + recovery

| Failure | Diagnosis | Recovery |
|---------|-----------|----------|
| `Stage 0: DAYU200 ... not connected` | Board off, USB unplugged, hdcd dead | Plug in / power on / restart hdc.exe |
| `Stage 0: G3 required missing` | W1 pull incomplete | Re-run W1 pull; verify file with `ls -la $V3_LOCAL/<rel>` |
| `Stage 1: no shell binary found on board` | OHOS variant lacks `/system/bin/{sh,toybox,toolbox}` | STOP. Investigate which shell-like binary the board ships. Add it to the discovery list in `stage_setup` (line ~530). Without an in-chroot shell, chroot exec cannot find an interpreter for `launch.sh`. |
| `Stage 1: shell copy did not emit CP_OK` | Board-internal `cp` failed (permission, disk full, source missing) | Inspect via `hdc shell ls -la /system/bin/sh`. If source exists, suspect FS quirk; try `teardown` + retry. |
| `Stage 4: chroot env probe FAILED — saw 'No such'` | (NEW: now ABORTs not WARN) Either chroot `/system/bin/sh` is missing (Stage 1 regression) or framework.jar is not at expected path inside chroot | Inspect chroot contents: `hdc shell ls -la $V3_CHROOT_ROOT/system/bin/sh $V3_CHROOT_ROOT/system/android/framework/framework.jar`. Run `teardown` + restart from `setup`. |
| `Stage 0: /data free space ...` | Disk full on board | Free space (`rm /data/local/tmp/<old-stuff>`) or run `teardown` first |
| `[ABORT 99] G1: hdc shell silent` | hdc transport regressed (H2 candidate) | STOP. Capture state. Do NOT retry without investigation. |
| `[ABORT 99] HBC全局三条 hit ([Fail])` | hdc reported a fatal | STOP. Examine the failing command. Run `teardown` to clear partial state, then start over. |
| `[ABORT 99] drwx in ls output` | hdc 造目录 quirk fired (file landed as dir) | STOP. Run `teardown`. Investigate the specific path that hit it. |
| `Stage 2: size mismatch` | hdc file send truncated | Run `teardown`, retry from `setup`. If repeats, suspect hdc.exe corruption or disk full. |
| `Stage 3: mount --bind did NOT take effect` | SELinux denied mount in `sh:s0` (proposal §9 Q4) | Re-run with `--setenforce-0`. If still fails, mount must be done outside script (operator from a privileged shell). |
| `chroot launch hangs (timeout 124)` | Inner command hung; budget hit | Inspect `/tmp/v3-chroot-deploy-<TS>/launch.log`. Inner command needs debugging. Run `teardown` to clean state. |
| `Stage 6: avc: denied` warnings | SELinux denied something inside chroot | Inspect denied lines. Decide whether to add `--setenforce-0` or to specifically grant the denied permission. |
| Any stage exits with code 99 | HBC 全局三条 abort | **Do NOT retry without operator review.** Run `teardown` to clear state. Examine `/tmp/v3-chroot-deploy-<TS>/deploy.log`. |
| Anything that would have been a `/system` brick | Cannot happen by construction | The chroot has zero `/system` writes. Worst case is `rm -rf $V3_CHROOT_ROOT` and retry. |

---

## 5. Phase 1 limitations (the fidelity caveats)

Phase 1 explicitly trades fidelity for brick-immunity. The following are
**known not-validated** in this SOP; Phase 2 (proposal §5) addresses them.

### 5.1 Headless only

No on-screen render in Phase 1. The chroot processes can dump PNG via
HWUI's headless path; reaching factory `render_service` requires either
the HBC `librender_service.z.so` replacement on host `/system/lib/`
(Phase 2) or a PNG-blit-to-`/dev/fb0` shim path.

### 5.2 appspawn-x not in OH init

The HBC `appspawn-x` daemon is meant to run as an OH init service in
the `appspawn:s0` SELinux domain (per its `etc/appspawn_x.cfg`).
Phase 1 invokes it manually from shell, putting it in `sh:s0`. Some
inter-process operations may EPERM:

* PID namespace setup requiring CAP_SYS_ADMIN
* AppSpawnX socket label transitions (`appspawn_socket`)
* `setuid()` from sh domain to other UIDs

If any of these matter for the inner command, Phase 2's
`/system/etc/init/appspawn_x.cfg` write becomes necessary.

### 5.3 setenforce 0 caveat (the explicit one)

Per `V3-HDC-3.2.0B-PROBE-REPORT.md` Probe 4, `chcon` on
`/data/local/tmp/probe.txt` is **denied** under SELinux Enforcing for
the shell uid. The chroot domain inherits from `sh:s0`. Several
operations (mount, chcon, exec under different secon) may EPERM in
`sh:s0`.

Phase 1 mitigation: opt into `--setenforce-0`. This temporarily
disables SELinux for Stage 5 (launch), then restores Enforcing in
Stage 6. **This is a fidelity caveat**: running in Permissive isolates
the SELinux question from the chroot question, but the result is not
representative of a real OH deployment. The full Phase 2 path
re-enables Enforcing with proper domain labels.

### 5.4 Factory OH services unchanged

Factory `foundation` / `render_service` / `launcher` / `hdcd` continue
running on factory `/system/lib/` artifacts. HBC's 13 OH service
replacements (`libabilityms.z.so`, `libwms.z.so`, etc.) are pushed
into the chroot but only consumed by chroot-internal processes; they do
NOT replace what factory `foundation` loads.

For Westlake's substrate this means cross-Activity launches via factory
AMS may not see HBC's adapter hooks (proposal §9 Q5). Single-Activity
first-light is fine; multi-Activity flows need Phase 2.

### 5.5 No PNG-blit display in Phase 1

The Island `force-stop com.ohos.photos + restart` pattern is defensive
in Phase 1 (we kill stale viewers before our launch) but doesn't drive
a display path. When Phase 2 adds display, the same pattern in §6
becomes load-bearing.

---

## 6. Phase 2 path (layered /system writes, later)

If Phase 1 hits a hard fidelity gap that blocks W4/W5/W6 acceptance,
Phase 2 selectively layers back `/system` writes one at a time:

| Order | What to add | Why | Risk |
|------:|-------------|-----|------|
| 1     | `/system/lib/libabilityms.z.so` (HBC variant via hardened script Stage 3b) | adapter hooks for AMS → ART callbacks | Single .so; rollback via `.orig_<TS>` |
| 2     | `/system/lib/libwms.z.so`, `libappms.z.so` | window/app integration | Same pattern |
| 3     | `/system/etc/init/appspawn_x.cfg` + `/system/etc/selinux/.../file_contexts` | proper appspawn:s0 domain transition | High risk — uses full hardened script G1-G7 discipline |
| 4     | `/system/lib/librender_service.z.so` | on-screen render via HBC adapter | High risk — service load order matters |

Each addition is a separate W-stage with its own postmortem-able
rollback. The hardened script's G1-G7 still applies. The full
`deploy-hbc-to-dayu200-hardened.sh` becomes the LAST resort only if all
layered adds fail.

---

## 7. Diff vs hardened (`/system`) SOP

| Concern | Hardened (`/system`) SOP | Chroot SOP |
|---------|--------------------------|------------|
| Target paths | `/system/lib`, `/system/android`, `/system/bin`, `/system/etc/init`, `/system/etc/selinux` | `/data/local/tmp/v3-hbc-chroot/system/...` only |
| chcon | `chcon_verify` to `system_lib_file`, `system_fonts_file`, `appspawn_exec` | NONE — chroot files inherit `shell_data_file:s0`; chcon EPERMs here per probe §4 |
| Reboot | Stage 4 (`hdc target boot`) | NEVER |
| Recovery | `--uninstall` restores from `.orig_<TS>` (assumes shell alive); else ROM flash | `teardown` = `rm -rf` (always works on hdc-alive board) |
| Brick risk | LOW (post-G1-G7) but not zero | IMPOSSIBLE by construction |
| Fidelity | FULL — runs HBC stack as HBC designed | PARTIAL — see §5 caveats |
| LOC | 1190 (hardened script) | 979 (chroot script) |
| When to choose | Phase 2 (selective layering after Phase 1 PASS) | Phase 1 (first W2 retry, today) |
| Per-deploy time | ~20 min + reboot + 2-5 min post-reboot wait | ~5-10 min (no reboot) |
| hdc version pin | KNOWN_GOOD list (G6, warn-not-abort) | LOGGED not gated (per probe) |

The two SOPs are **siblings**, not replacements. The chroot SOP is the
Phase-1 brick-immune entry point; the hardened SOP is the Phase-2
layered-fidelity path. Both reference the same HBC `DEPLOY_SOP.md` for
artifact discipline.

---

## 8. 全局三条 abort triggers (verbatim from V3-DEPLOY-HARDENED-SOP §4)

The chroot script aborts (exit 99) on the same five conditions as the
hardened script:

1. `connect-key` in any `hdc` output → `_assert_no_fail_or_drwx`
2. `timeout` in any `hdc` output → `_assert_no_fail_or_drwx`
3. `[Fail]` in any output → `_assert_no_fail_or_drwx`
4. `drwx` in `ls` of a pushed file → `_assert_no_fail_or_drwx` (the
   hdc 造目录 quirk; would be set checked in Stage 1 `setup`)
5. **empty stdout** from a sentinel echo (Westlake addition per
   `V3-W2-POSTMORTEM.md` H2) → `_alive_probe`

Once any abort fires, the script exits 99 **without proceeding**. Even
in the chroot path (which has no reboot to skip), exit 99 means "the
operator MUST review state before re-running." Run `teardown` to clear
partial state, then start over from `preflight`.

---

## 9. Open risks (for operator awareness)

Tracked in `V3-CHROOT-CONTAINMENT-PROPOSAL.md` §9. Surface assumption +
mitigation per risk:

### Q1 — render_service needs /system writes?

* **Assumption:** Phase 1 is headless (no on-screen render). HWUI dumps
  to PNG via the headless path; factory `render_service` is not used.
* **Mitigation:** scope Phase 1 to MainActivity.onCreate hilog marker
  (or env-probe-only). On-screen is Phase 2.
* **Validate at runtime:** if anything in launch.log expects a Display
  object and crashes, Phase 1 is bounded — proceed to Phase 2 §6 row 4.

### Q2 — appspawn-x needs /system/bin location for SELinux domain transition?

* **Assumption:** appspawn-x run manually from shell (in `sh:s0`)
  succeeds for the first-light env probe.
* **Mitigation:** Phase 1 does not require appspawn-x to spawn children
  via the `AppSpawnX` socket. Direct ART invocation is the fallback.
* **Validate at runtime:** if appspawn-x in chroot fails with EPERM on
  cgroup/setuid, this risk fires. Phase 2 §6 row 3 fixes via OH init
  service registration.

### Q3 — boot-image baked /system/framework/boot.art path breaks chroot?

* **Assumption:** Inside chroot, baked `/system/android/framework/...`
  strings resolve to `$V3_CHROOT_ROOT/system/android/framework/...` via
  the chroot's filesystem view; ART's `OatFile::ValidateBootClassPath`
  sees identical strings on both sides (proposal §3.2 path C).
* **Mitigation:** chroot mkdir layout exactly mirrors AOSP/HBC paths.
  No symlinks needed.
* **Validate at runtime:** if ART logs "Cannot find oat file at expected
  location" or "boot image not relocated," this assumption fails.
  Last-resort fix: re-run dex2oat for chroot paths (proposal §3.2 path
  D; ~1-2 extra days).

### Q4 — SELinux denials inside chroot (the BIG risk)

* **Assumption:** chroot processes inherit `sh:s0` domain. Some ops
  (mount, chcon, setuid) may EPERM in this domain even with root uid.
  Probe Report §4 empirically confirmed `chcon` is denied.
* **Mitigation:** `--setenforce-0` flag toggles to Permissive for the
  duration of Stage 5, then restores Enforcing in Stage 6. This is a
  Phase-1 caveat; Phase 2 introduces proper domain labels via the
  hardened script's `file_contexts` write.
* **Validate at runtime:** Stage 6 grep `hilog | grep avc:.*denied` —
  if any denials appear, operator must decide between
  `--setenforce-0` (fast) or per-permission tuning (slow but fidelity-
  preserving).

### Q5 — Cross-Activity launch through factory AMS?

* **Assumption:** Phase 1 acceptance is single-Activity (env probe or
  MainActivity.onCreate via direct ART invocation). Cross-Activity
  launches (HelloWorld.startActivity(Intent)) hit factory
  `libabilityms.z.so` which lacks HBC adapter hooks.
* **Mitigation:** scope Phase 1 to single-Activity flow. HBC HelloWorld
  itself only calls MainActivity.onCreate without further
  startActivity, so this is a soft constraint, not a hard one for the
  acceptance criterion.
* **Validate at runtime:** if HelloWorld.onCreate calls startActivity
  and the launched activity never reaches onCreate, this risk fires.
  Phase 2 §6 row 1 (libabilityms.z.so replacement on `/system/lib/`)
  resolves.

---

## 10. Cross-references

* Script: `scripts/v3/deploy-hbc-to-dayu200-chroot.sh`
* Hardened sibling: `scripts/v3/deploy-hbc-to-dayu200-hardened.sh`
* Containment spec: `docs/engine/V3-CHROOT-CONTAINMENT-PROPOSAL.md`
* Brick postmortem: `docs/engine/V3-W2-POSTMORTEM.md`
* hdc transport probe: `docs/engine/V3-HDC-3.2.0B-PROBE-REPORT.md`
* HBC artifact manifest: `docs/engine/V3-HBC-ARTIFACT-MANIFEST.md`
* Hardened SOP (Phase 2): `docs/engine/V3-DEPLOY-HARDENED-SOP.md`
* Westlake-adapted SOP: `docs/engine/V3-DEPLOY-SOP.md`
* HBC source-of-truth: `westlake-deploy-ohos/v3-hbc/scripts/DEPLOY_SOP.md`
* Island chroot reference: `artifacts/v3-w3-westlake-stack/16.14-Island/
  tests/R2-jni-smoke/setup-7.0.sh` (structural template),
  `artifacts/v3-w3-westlake-stack/16.14-Island/demo/run-live.sh` (Never-Die
  patterns)
* Memory:
  `/home/dspfac/.claude/projects/-home-dspfac-openharmony/memory/
  feedback_soft_brick_w2_2026-05-16.md` (operational lessons)
