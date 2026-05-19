# V3 Chroot Deploy Script — Pre-flight Code Review

**Date:** 2026-05-19
**Reviewer:** agent 63 (independent of agent 62 build)
**Subject:** `scripts/v3/deploy-hbc-to-dayu200-chroot.sh` (979 LOC, commits `3b8740a0` + `dfdcc541`)
**Companion SOP:** `docs/engine/V3-DEPLOY-CHROOT-SOP.md` (418 LOC)
**Spec:** `docs/engine/V3-CHROOT-CONTAINMENT-PROPOSAL.md` §10 decisions 1+2

## TL;DR

- **Verdict:** SAFE-WITH-CAVEATS — brick-immune by construction (the core invariant holds), but two real bugs will produce a misleading PASS/FAIL signal on the operator's first retry.
- **Top finding:** The chroot exec on Stage 4 (line 703) and Stage 5 (line 772) invokes `/system/bin/sh` *inside* the chroot, but the script never pushes a shell binary into `$V3_CHROOT_ROOT/system/bin/`. Only `appspawn-x` lands there. Result: `chroot: failed to run command '/system/bin/sh': No such file or directory`, the `[v3-chroot-launch]` marker is never emitted, Stage 5 still reports PASS, and Stage 6 warn-not-fails on missing marker. The acceptance criterion (SOP §2 item 3 — clean launch.sh exit + marker emission) will NEVER be reached on first retry.
- **Recommended next step:** Apply Tier 1 fixes before running on board. Approx 30-60 min of additional engineering — primarily (a) push toybox/sh into the chroot or restructure the launch invocation, and (b) fix the cross-invocation log-dir bug. Both are diff-sized fixes, not redesigns.

---

## 1. Brick-safety invariants (PASS/FAIL per item)

| Invariant | Status | Evidence |
|-----------|--------|----------|
| All hdc file send targets prefixed with `$V3_CHROOT_ROOT` | **PASS** | Only 2 send sites: line 505 (`_push_one` → `$V3_CHROOT_ROOT$device_rel`); line 695 (`launch.sh` → `$V3_CHROOT_ROOT/launch.sh`). Both always inside chroot. |
| OPTIONAL `app/HelloWorld.apk=/data/local/tmp/HelloWorld.apk` exception | **PASS (but flagged)** | Agent 62 self-audit claimed this as the "one outside-chroot exception." It is NOT actually outside chroot — `_push_one` *always* prefixes `$V3_CHROOT_ROOT`, so it lands at `$V3_CHROOT_ROOT/data/local/tmp/HelloWorld.apk` (chroot-internal). The "exception" description in the agent-62 self-audit was wrong; the actual behavior is brick-safe. Trivially clarify in code comment. |
| Zero writes to host `/system/...` | **PASS** | Every literal `/system/...` in non-comment lines (533, 544, 587, 703, 757, 759, 925-935) is either inside `chroot $V3_CHROOT_ROOT ...` exec (chroot-relative) or prefixed with `$V3_CHROOT_ROOT`. |
| Zero `\|\| true` in code paths | **PASS** | grep found only line 24 (header comment). |
| Zero `hdc target boot` | **PASS** | grep found only line 29 (header comment). |
| `setenforce 0` gated by `--setenforce-0` | **PASS** | Line 734 guards the call. |
| `setenforce 1` restored after launch | **PARTIAL FAIL** | Restored in Stage 6 verify (line 794) on the happy path. But: (a) NO `trap` handler — if Stage 5 aborts (exit 99 from `_alive_probe`) after `setenforce 0` was already issued, SELinux stays Permissive until next reboot; (b) `teardown` does NOT restore SELinux — operator who runs `launch --setenforce-0` then `teardown` (without `verify`) leaves the board Permissive. Not a brick, but a fidelity / security regression. See Tier 2 fix #1. |
| `_alive_probe` between every stage | **PASS** | Each `stage_*` calls `_alive_probe` at entry and exit. Verified by grep. |
| Per-25-file sentinel during push | **PASS** | Line 523, 535. |
| HBC 全局三条 abort on `connect-key\|timeout\|[Fail]\|drwx\|empty stdout` | **PASS** | `_assert_no_fail_or_drwx` (lines 179-187) + `_alive_probe` (160-176). Both abort with exit 99. |

**Verdict:** Core brick-safety holds. Two SELinux-restore gaps (Tier 2) are bounded (max impact = SELinux stays Permissive until reboot).

---

## 2. Silent-failure trap audit (the H1 class)

| Concern | Status | Notes |
|---------|--------|-------|
| Every `hdc shell` exit-checked or output-verified | **MOSTLY PASS** | Most calls capture stdout in `$(...)` and run `_assert_no_fail_or_drwx`. The bare-call exceptions (728, 738, 794, 864) all use `>/dev/null` and rely on `_alive_probe` to catch silent channel; with `set -o pipefail` + `set -e`, hdc transport failure on these calls would error the script. |
| Batch ops report per-item | **MOSTLY PASS** | Line 587 chmod batch: `chmod 755 $V3_CHROOT_ROOT/system/bin/*` does NOT report per-file — but the dir only contains appspawn-x (since the script doesn't push anything else there). If a later patch adds more binaries, this masks errors. Acceptable for now. Line 469 batch-mkdir: followed by per-dir `stat` verification (475-479) — good. |
| `set -euo pipefail` | **PASS** | Line 77. `set +e/-e` flip at 771/775 around the deliberately-permissive launch invocation — correct usage. |
| Variable expansion quoting | **MOSTLY PASS** | All `hdc_shell "..."` invocations quote the command string. `$V3_CHROOT_ROOT` is referenced inside double quotes everywhere. No IFS surprises detected. The one slightly-fragile spot is line 467 `d_list=$(IFS=' '; echo "${dirs[*]}")` — subshell idiom is correct, but only safe because no chroot dir contains a space. |
| Channel-alive between every stage boundary | **PASS** | Verified for all 8 stages. |

**One specific worry tested and dismissed:** the SOP §4 row "Stage 3: mount --bind did NOT take effect" maps to the script's line 619 check — that check uses `mountpoint -q` post-mount and aborts if not mounted. Correctly catches silent SELinux denial of mount.

---

## 3. Idempotency analysis

| Command | Idempotent? | Evidence |
|---------|-------------|----------|
| `preflight` | YES | Read-only against board (modulo capturing `board-state-pre.txt` to a new `$LOG_DIR` each run). |
| `setup` | YES | `mkdir -p` (line 469); per-dir verify (475-479) accepts pre-existing directories. |
| `push` | YES (mostly) | `hdc file send` overwrites; size-verify spot-checks the 3 sentinels. Re-pushing all files works but wastes ~5-10 min. No per-file pre-check. |
| `mount` | YES | Each `mp` guarded by `mountpoint -q` first (line 613). |
| `env` | YES | `launch.sh` overwritten cleanly. Env probe is read-only. |
| `launch` | YES | No FS writes inside chroot for the env-probe mode. setenforce 0 already idempotent. |
| `verify` | YES | Read-only modulo `setenforce 1` (idempotent). |
| `teardown` on full state | YES | Tested mentally against script. |
| `teardown` on post-stage-3 partial (mounts only) | YES | mountpoint check + umount each, then `rm -rf` succeeds. |
| `teardown` on post-stage-5 partial (mounts + setenforce 0) | **PARTIAL FAIL** | `teardown` skips SELinux restore. See Tier 2 fix #1. |
| `teardown` on empty state (chroot already gone) | YES (sort of) | `mountpoint -q` returns "already unmounted"; `rm -rf` succeeds on absent path; `ls -ld` returns "No such" — passes the assertion. Works. |
| Timestamps distinct across runs | YES | `TS=$(date +%Y%m%d-%H%M%S)` (line 88) per invocation. **BUT** — this creates a cross-invocation idempotency bug for the launch-log marker check. See §4 below. |

---

## 4. Stage-by-stage findings (with line numbers)

### Stage 0 — preflight (lines 336-427)

- **PASS:** Truly read-only against board. The 6 `hdc shell` calls (350, 361, 378, 396-404, 413) all read state. No writes.
- **Minor:** Line 350 captures hdc version via `head -1`; if hdc emits a long version banner, only first line is logged. Acceptable per probe report.
- **Minor:** Line 378 awk pattern `df /data ... awk 'NR==2 {print $4}'` may break on busybox-style df with different column layout. Bounded to a `warn` (not abort) at line 380 — safe.

### Stage 1 — setup (lines 433-488)

- **PASS:** mkdir batched, per-dir verified.
- Line 462 creates an unused `host_tmp` dir. Vestigial from Island pattern; harmless.

### Stage 2 — push (lines 494-595)

- **PASS:** Per-file `_assert_no_fail_or_drwx`; sentinel every 25 files; size-verify on 3 spots.
- **Verified:** Spot-check indices `[0]`, `[17]`, `[58]` correctly map to `libwms.z.so`, `framework.jar`, `bin/appspawn-x`.
- **Minor:** Line 587 chmod batch glob `$V3_CHROOT_ROOT/system/bin/*` is fine TODAY (only `appspawn-x` lives there). Add a comment so a future maintainer doesn't add a non-executable to `/system/bin/` and have it silently get +x.
- **Minor:** OPTIONAL_MANIFEST `app/HelloWorld.apk=/data/local/tmp/HelloWorld.apk` lands at `$V3_CHROOT_ROOT/data/local/tmp/HelloWorld.apk` not host `/data/local/tmp/HelloWorld.apk`. Functionally fine (chroot is brick-safe), but the path string is misleading. Suggest `/data/HelloWorld.apk` to match the rest of chroot layout.

### Stage 3 — mount (lines 601-636)

- **PASS:** Idempotent; mount verified.
- Mounts `/proc /sys /dev`. Skips `/host_tmp` (consistent with Phase 1 headless scope).

### Stage 4 — env (lines 642-715)

- **TIER 1 BUG** (line 703): `chroot $V3_CHROOT_ROOT /system/bin/sh /launch.sh /system/bin/sh -c 'pwd; ls ...'` — `/system/bin/sh` does not exist inside chroot. Stage 1 mkdir creates the directory, Stage 2 pushes only `appspawn-x` into it. chroot fails with "No such file". The probe at line 706 catches the "No such" string and warn-not-fails — operator sees a yellow WARN about framework.jar visibility but Stage 4 PASSes anyway.
- The launch.sh `#!/system/bin/sh` shebang (line 656) compounds the issue — even if the operator manually invoked `chroot ... /launch.sh` later, the shebang would also fail.
- **Recommendation:** Either (a) push a sh binary (host `/system/bin/sh` toybox is small; `hdc file send` it from host to `$V3_CHROOT_ROOT/system/bin/sh`), or (b) invoke launch.sh via an existing interpreter that the chroot DOES have, or (c) drop the chroot-internal sh requirement and run launch.sh's contents directly via `hdc shell "chroot $ROOT /system/bin/appspawn-x ..."` — but appspawn-x isn't a sh either.

  Cleanest fix: add a Stage 1.5 / Stage 2 step that does `hdc shell "cp /system/bin/sh $V3_CHROOT_ROOT/system/bin/sh"` (board-internal copy, brick-safe). The host toybox sh is the same ABI as the appspawn-x binary's interp (`/lib/ld-musl-arm.so.1`), so it should run inside chroot once `/lib/ld-musl-arm.so.1` is also copied (which is a separate concern not addressed by Phase 1 anyway — the chroot's lib search is `/system/lib`, not `/lib`).

### Stage 5 — launch (lines 721-781)

- **TIER 1 BUG inherited from Stage 4:** The actual launch (line 772-773) `chroot $V3_CHROOT_ROOT /system/bin/sh /launch.sh` fails identically. launch.log will contain a chroot error, no `[v3-chroot-launch]` marker. Stage 5 reports PASS regardless because line 776 just logs the exit code.
- **TIER 2:** No `trap` handler. If `_alive_probe` at line 779 aborts (exit 99) after `setenforce 0` was issued at line 738, SELinux stays Permissive on the board.
- Line 728 `aa force-stop com.ohos.photos` is defensive (Island pattern). Output piped through `>/dev/null` after `tail -1`. If hdc silently fails here, the immediately-following SELinux block won't notice; `_alive_probe` at the end of the stage catches it. Acceptable.

### Stage 6 — verify (lines 787-844)

- **TIER 1 BUG** (line 808): `launch_log="$LOG_DIR/launch.log"` re-declares the variable referencing the CURRENT invocation's `$LOG_DIR`. If `verify` is run in a separate script invocation from `launch` (which SOP §3.2 explicitly recommends as "RECOMMENDED for first retry"), `$TS` and therefore `$LOG_DIR` will be different. The launch.log lives at `/tmp/v3-chroot-deploy-T1/launch.log` while `verify` looks in `/tmp/v3-chroot-deploy-T2/launch.log`. The `[ -f "$launch_log" ]` test fails, the warn-not-fail branch fires, marker check always fails on stage-by-stage flow.
- **Recommendation:** Either (a) accept `LOG_DIR` from env (already done — `LOG_DIR="${LOG_DIR:-...}"` at line 89; SOP needs to instruct operator to export it), or (b) write/read a sentinel file at a deterministic path (e.g. `/tmp/v3-chroot-deploy-current → /tmp/v3-chroot-deploy-<TS>`), or (c) make `verify` find the most-recent `/tmp/v3-chroot-deploy-*` and use that.
- Line 820 hilog tail uses `2>/dev/null` — silences hilog command not-found etc. Fine for warn-not-fail use.

### Teardown (lines 850-910)

- **PASS** on core teardown logic.
- **TIER 2:** Does NOT restore SELinux to Enforcing. If operator runs `launch --setenforce-0` then `teardown` (without `verify`), board stays Permissive.
- **MINOR:** Line 864 `pkill -9 -f $V3_CHROOT_ROOT` matches against full argv. Low risk on phone (no other process has that path in argv), but worth a comment.
- **MINOR:** Line 858 — if hdc channel is silent at teardown entry, `return 1` from a `stage_*` function under `set -e` — main()'s case dispatcher will exit non-zero. Operator sees exit 1 with `warn "rerun after operator power-cycle"`. Documented behavior, acceptable.

### Status (lines 916-940)

- **PASS:** Truly read-only.

---

## 5. Script-vs-SOP consistency

| SOP claim | Script reality | Match? |
|-----------|----------------|--------|
| §3.3 "9 commands matching the stages plus `all`, `teardown`, `status`, `help`" | Script has `preflight setup push mount env launch verify teardown status all help` = 11 commands + stage aliases | YES (10 + help; SOP miscounts as 9, off by one) |
| §3.3 stage 0 "63 artifacts present" | REQUIRED_MANIFEST has exactly 63 entries | YES |
| §3.3 stage 2 "63 required + ~41 AOSP natives + 3 dual-path shims + 0-3 optional" | 63 required (loop 517-527) + N AOSP natives where N depends on `$V3_LOCAL/lib/*.so` glob minus filters + 3 dual-path (lines 543-546) + 3 optional (549-556) | YES |
| §2 item 3 "chroot ... runs to clean exit ... emitting [v3-chroot-launch] marker" | **BLOCKED by Tier 1 bug** — the marker is never reachable because chroot exec of `/system/bin/sh` fails | FAIL (script cannot satisfy SOP acceptance gate as written) |
| §4 row "chroot launch hangs (timeout 124)" | Stage 5 wraps in `timeout ${LAUNCH_BUDGET_SECS}s` (line 772) | YES |
| §4 row "Stage 3: mount --bind did NOT take effect" | Line 619 explicitly checks | YES |
| §10 cross-reference list | All paths exist in repo | YES |
| Header line 8 "All 563 HBC artifacts (~412 MB)" | Script actually pushes ~109 files (63 required + ~40 AOSP + 3 dual + 0-3 optional) | NO — script header is misleading. SOP §3.3 stage 2 is honest. |

---

## 6. Recommended fixes

### Tier 1 — must fix before running on board

1. **Push `sh` into chroot OR change the launch invocation** (script lines 703, 757, 759, 772; launch.sh shebang line 656). Without this, Stage 4 env-probe always WARN-not-fails on `No such`, Stage 5 launch never emits the marker, Stage 6 verify always WARN-not-fails on missing marker. The script reports overall PASS but acceptance criterion (SOP §2 item 3) is never met. Operator's mental model "all stages PASS = ready for Phase 2" would be wrong.

   Smallest-diff fix: in `stage_setup` (line 472, after dir verification), add `hdc_shell "cp /system/bin/sh $V3_CHROOT_ROOT/system/bin/sh && cp /lib/ld-musl-arm.so.1 $V3_CHROOT_ROOT/lib/ld-musl-arm.so.1 2>&1"` with assert. Then add `$V3_CHROOT_ROOT/lib` to the dirs list. Verify the copies are size-matched. This is brick-safe (it copies from host READ side into chroot WRITE side; no host /system writes).

2. **Fix cross-invocation log-dir bug** (lines 89, 808). When SOP §3.2 stage-by-stage flow is followed (which it explicitly recommends for first retry), Stage 5's launch.log is unreachable from Stage 6 in a separate invocation.

   Smallest-diff fix: write a symlink `/tmp/v3-chroot-deploy-current → /tmp/v3-chroot-deploy-<TS>` in the top-level setup (after line 119 `mkdir -p $LOG_DIR`), and have `stage_verify` resolve `launch_log` from the symlink target if `$LOG_DIR/launch.log` doesn't exist. Alternative: SOP can instruct operator to `export LOG_DIR=/tmp/v3-chroot-deploy-<TS>` before stage-by-stage flow.

### Tier 2 — should fix

1. **SELinux Permissive can leak past abort/teardown** (lines 738, 794, 850-910).

   Add a `trap '_restore_selinux EXIT'` handler at script init when `$SETENFORCE_0=1`. `_restore_selinux()` issues `setenforce 1` idempotently. Also call it as last step of `stage_teardown`.

2. **Update script header comment** (line 8) to match SOP §3.3 — say "~109 files (63 required + ~40 AOSP natives + 3 dual-path + 0-3 optional)" instead of "All 563 HBC artifacts."

3. **Document OPTIONAL `app/HelloWorld.apk` path behavior** — clarify in code comment that the `/data/local/tmp/HelloWorld.apk` device-rel path lands at `$V3_CHROOT_ROOT/data/local/tmp/HelloWorld.apk`, NOT host `/data/local/tmp/HelloWorld.apk`. Agent 62's self-audit statement that this is an "outside chroot exception" was incorrect; the actual behavior is brick-safe but the inconsistency is confusing.

4. **Stage 4 chroot env-probe** (line 703) — once Tier 1 fix 1 lands, the warn-not-fail branch at 706-710 becomes a real signal. Consider promoting to abort if probe fails.

### Tier 3 — nice to have

1. Remove vestigial `host_tmp` dir creation (line 462) since Phase 1 doesn't use it.
2. Add a per-AOSP-native count log in `stage_push` so operator can confirm "I expected 38, got 38."
3. Line 587 — guard the glob `chmod 755 $V3_CHROOT_ROOT/system/bin/*` with an explicit filename list, so future binaries don't accidentally get +x.
4. Add `set -E` to ensure traps (if added per Tier 2 fix 1) fire on subshell errors.
5. Line 117 `${POSITIONAL[0]:-help}` works on bash 4.4+; minor portability note for older shells (not a real concern on WSL).

---

## 7. Items I verified that PASS without comment (acknowledgment)

- No writes to host `/system/` anywhere in the script (audited every `/system/...` literal).
- No `hdc target boot` (audited).
- No `|| true` in non-comment code (audited).
- `_alive_probe` called at entry + exit of all 8 stages (audited).
- `_assert_no_fail_or_drwx` called on every `hdc shell` output that captures it (audited).
- `set -euo pipefail` correctly handled in launch's deliberately-permissive flip.
- All env-var fallbacks (`${V3_CHROOT_HELLO_CMD:-}`, `${MARKER:-...}`, `${POSITIONAL[0]:-help}`) safe under `set -u`.
- REQUIRED_MANIFEST entries match the HBC SOP §3b-3f authoritative list (cross-checked against `V3-HBC-ARTIFACT-MANIFEST.md` §4).
- Spot-check indices `[0]`, `[17]`, `[58]` correctly map to first / middle / near-end entries.
- `_iter_aosp_natives` glob is safe when directory is empty (failglob not set, `[ -f ]` rejects literal `*.so`).
- Path traversal in artifact manifest is bounded — `_push_one` only ever produces `$V3_CHROOT_ROOT$device_rel`; no shell expansion of artifact paths could escape chroot.
- Per-stage logs go to fresh `$LOG_DIR` per invocation — no clobbering across runs (modulo the cross-invocation log-dir bug noted above, which is the OTHER side of the same coin).
- `teardown` on full state, post-stage-3 state, and empty state all work.
- `_to_win_path` correctly uses `wslpath -w` when available.

---

## 8. Items I could not verify without board contact

1. Whether DAYU200's `/system/bin/sh` is statically linked (relevant for Tier 1 fix 1 — if it's static, no `/lib/ld-musl-arm.so.1` copy needed; if dynamic, the chroot needs that interp present).
2. Whether `pkill -9 -f /data/local/tmp/v3-hbc-chroot` accidentally matches anything benign (e.g. hdcd, init child).
3. Whether the SELinux denial of `mount --bind` in `sh:s0` domain actually fires on this specific OH build (Probe Report §4 confirmed `chcon` is denied; mount is untested).
4. Whether `hdc file send` preserves mtime (relevant to ART's `OatFile::ValidateBootClassPath` per proposal §9 Q3).
5. Whether the chroot env-probe at line 703 — if Tier 1 fix 1 is applied — actually returns `framework.jar` visible (depends on chroot file resolution and the actual baked strings in HBC's boot image).
6. Whether `aa force-stop com.ohos.photos` returns non-zero on a freshly-flashed board where Photos was never started (would error under `set -e` + `set -o pipefail`).

These can only be empirically confirmed by running the script (Tier 1 fixed) on a board, which is out of scope for this desk review.

---

## Cross-references

- Script under review: `scripts/v3/deploy-hbc-to-dayu200-chroot.sh`
- Companion SOP: `docs/engine/V3-DEPLOY-CHROOT-SOP.md`
- Spec implemented: `docs/engine/V3-CHROOT-CONTAINMENT-PROPOSAL.md`
- Sibling /system script (reference only): `scripts/v3/deploy-hbc-to-dayu200-hardened.sh`
- HBC artifact manifest source-of-truth: `docs/engine/V3-HBC-ARTIFACT-MANIFEST.md`
- hdc transport empirics: `docs/engine/V3-HDC-3.2.0B-PROBE-REPORT.md`
- W2 brick context: `docs/engine/V3-W2-POSTMORTEM.md`
- Agent 62 build commits: `3b8740a0` (script) + `dfdcc541` (SOP)
