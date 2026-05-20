# V3 W2 Path B — restore-chcon + pinned hdc + long-load probe

Date: 2026-05-20
Board: DAYU200 (dd011a414436314130101250040eac00, post-power-cycle)
Agent: 79
Operator gate: Path B approved by user after Stage B HALTED (commit `4ba8695f`). This run executed Step 1 only and **HALTED at the Step-1 STOP gate** because post-power-cycle board state did not match the brief's preconditions.

---

## TL;DR

- **BLOCKED — Step 1 hard-stop tripped.** Hard power-cycle did NOT preserve Stage B's intermediate state. The chcon snapshot (`/data/local/tmp/v3-chcon-snapshot.txt`), the chroot (`/data/local/tmp/v3-hbc-chroot/`), and the 13 `.orig_20260519` factory backups are all GONE. Only `debugserver` remains in `/data/local/tmp/`. The board is reachable, Channel A is alive, SELinux is Enforcing — these three preflight checks PASS — but the snapshot/backup preconditions for `restore-chcon` FAIL.
- **Silver lining: /system is factory-equivalent.** Spot-check + full target-file diff vs. pre-Stage-B baseline shows /system has reverted to factory state. The 7 V3-NEW artifacts are absent (same as pre-Stage-B), and the 3 factory files (`libwms.z.so`, `libbms.z.so`, `libinstalls.z.so`) are present at expected sizes/timestamps/labels. `restore-chcon` is therefore **moot** — there are no Stage-B-applied chcon labels left to roll back.
- **Steps 2 (HBC server hdc.exe fetch) and 3 (long-load probe) NOT executed.** Per brief: "If any of these [Step 1 checks] fail → STOP + report." Halting per explicit instruction. Steps 2-3 are independent of snapshot state and remain valid for a follow-up brief; flagging for operator decision.

---

## 1. Board post-power-cycle state

| Check | Brief expectation | Actual | Pass/Fail |
|------|------|------|------|
| Board enumerated | Yes | `dd011a414436314130101250040eac00` returned by `hdc list targets` | PASS |
| Channel A alive | Yes (echo returns) | `PROBE_1501837574` returned; subsequent `hdc shell` calls succeed | PASS |
| Enforcing | Yes | `Enforcing` | PASS |
| Snapshot present | `~36 lines` | **Absent** (`/data/local/tmp/v3-chcon-snapshot.txt` does not exist) | **FAIL** |
| Chroot intact | Yes | **Absent** (`/data/local/tmp/v3-hbc-chroot/` does not exist) | **FAIL** |
| 13 `.orig_20260519` backups | Present in /system | **0 found** (full /system/lib + /system/etc scan) | **FAIL** |
| 63 V3 artifacts in /system | Present | **0 of the 7 V3-NEW probes found** (`libams.z.so`, `libapk_installer.so`, `liboh_*` all absent) | **FAIL** |

**Full /data/local/tmp listing:**
```
total 12288
drwxrwx--x 3 shell shell 3452 2017-08-04 17:02 .
drwxr-x--x 6 root  root  3452 2017-08-04 17:02 ..
drwxr-xr-x 2 shell shell 3452 2017-08-04 17:02 debugserver
```

**Hypothesis** (not validated): /system was an overlay on tmpfs that was discarded by the hard power-cycle; /data/local/tmp persistent storage was wiped or the files lived on a transient mount. Investigation deferred to operator — outside scope of this brief.

**Forensic capture:** `/tmp/v3-path-b-forensic/post-restore-state.txt` (ls -laZ of probe targets), `/tmp/v3-stage-b-snapshot-post.txt` (host-side copy of the 36-line snapshot Stage B took, still valid as ground truth for what *would have been* restored).

## 2. /system factory-equivalence verification

Comparing post-cycle `/system/lib` state to pre-Stage-B baseline (`/tmp/v3-stage-b-pre-system-target-files.txt`):

| Path | Pre-Stage-B baseline | Post-power-cycle | Match? |
|------|------|------|------|
| `libams.z.so` | absent | absent | ✓ |
| `libapk_installer.so` | absent | absent | ✓ |
| `liboh_hwui_shim.so` | absent | absent | ✓ |
| `liboh_android_runtime.so` | absent | absent | ✓ |
| `liboh_skia_rtti_shim.so` | absent | absent | ✓ |
| `liboh_adapter_bridge.so` | absent | absent | ✓ |
| `librender_service_base.z.so` | absent | absent | ✓ |
| `libappexecfwk_common.z.so` | absent | absent | ✓ |
| `libbms.z.so` | factory 5048116 / 2026-04-04 / `system_lib_file:s0` | factory 5048116 / 2026-04-04 / `system_lib_file:s0` | ✓ |
| `libinstalls.z.so` | factory 999088 / 2026-04-04 / `system_lib_file:s0` | factory 999088 / 2026-04-04 / `system_lib_file:s0` | ✓ |
| `libwms.z.so` | factory 1072868 / 2026-04-04 / `system_lib_file:s0` | factory 1072868 / 2026-04-04 / `system_lib_file:s0` | ✓ |

`/system/lib` file count: 1126 post-cycle (vs. 1111 pre-Stage-B — a 15-file increase that warrants follow-up; could be factory state drift from earlier non-Stage-B work or different counting context — flagged for operator).

`/system/etc/init/` Westlake/HBC/V3 init artifacts: 0 found. `/system/bin/appspawn-x`: absent. `/system/android/`: not probed but inferable from `appspawn-x` and the V3 init artifacts being absent.

**Verdict:** /system on this board is factory-equivalent (modulo the 15-file lib count diff). `restore-chcon` would be operating on files that no longer exist — semantically a no-op. Decision to skip `restore-chcon` invocation: even if invoked, the script's `_chcon_snapshot_restore` would short-circuit at line 408 (`test -s $CHCON_SNAPSHOT_DEVICE` → false → `return 0`).

## 3. HBC server scan + hdc.exe candidates found

**NOT EXECUTED.** Step 2 halted by Step 1 STOP per brief.

## 4. Pinned hdc.exe pulled (version, size, sha256)

**NOT EXECUTED.** Step 2 halted by Step 1 STOP per brief.

## 5. Long-load probe — 3.2.0b results

**NOT EXECUTED.** Step 3 halted by Step 1 STOP per brief.

## 6. Long-load probe — pinned 1.3.0c results

**NOT EXECUTED.** Step 3 halted by Step 1 STOP per brief.

## 7. Verdict on hdc-version-pinning fix

**Inconclusive — diagnostic not run.** The hdc-version-pinning hypothesis from the W2 postmortem (H2) remains the leading theory but is **not validated by this run**. The long-load probe is the right next experiment but requires either (a) operator approval to proceed with Steps 2-3 despite Step-1 STOP, or (b) a fresh brief that decouples the long-load probe from the snapshot/chroot precondition.

## 8. Recommendation for retry approach

**Recommended next step (in priority order):**

1. **Fresh brief for hdc-pin diagnostic decoupled from snapshot state.** The long-load probe (Step 3 of this brief) is independent of snapshot/chroot/.orig-backup presence. It compares two hdc.exe binaries side-by-side under sustained load — the board only needs to be reachable + Channel A alive (both of which are currently true). This is the highest-value remaining diagnostic and can run today. Recommend an agent 80 brief that skips Step 1's snapshot/chroot/.orig-backup checks and goes straight to "verify board reachable + Enforcing" → Step 2 (HBC server scan + pull pinned hdc.exe) → Step 3 (long-load probe). No deploy retry, same HALT gate at end.

2. **Investigate why /system reverted post-power-cycle.** If /system writes don't persist a power-cycle, that fundamentally changes the deploy model (every deploy needs to either persist via a different mount or be re-applied each boot). Worth a separate forensic — check whether DAYU200 boots from a read-only ROM and overlays /system in tmpfs, or whether the user-applied tweaks during power-cycle (e.g., recovery-mode flash) explicitly wiped /system.

3. **Do NOT retry Stage B yet.** The pre-power-cycle state and the post-power-cycle state are sufficiently different that the original Stage B "63 artifacts + 13 backups" assumption needs to be re-verified before any retry. After (1) validates the pin, the rerun should treat the board as a clean slate and not assume the partial Stage B state is recoverable in-place.

4. **Consider script-side improvements regardless of hdc-pin outcome:** persist the chcon snapshot off-device (push a copy to host at the end of `--snapshot-only`), so a power-cycle event doesn't lose the rollback recipe. Currently the snapshot is the rollback recipe and it lives only on a partition the power-cycle wiped.

---

## Files created / referenced

- `docs/engine/V3-W2-PATH-B-REPORT.md` (this file)
- `/tmp/v3-path-b-forensic/post-restore-state.txt` (ls -laZ of /system/lib probe targets post-cycle)
- `/tmp/v3-stage-b-snapshot-post.txt` (pre-existing host-side copy of Stage B's 36-line snapshot — still authoritative for what *would have been* restored)
- `/tmp/v3-stage-b-pre-system-target-files.txt` (pre-existing pre-Stage-B baseline used for diff)
- No probe script created (`scripts/v3/hdc-long-load-probe.sh` deferred to follow-up brief)
- No pinned hdc.exe pulled (`tools/hdc-pinned-1.3.0/` deferred to follow-up brief)

## Hard constraints honored

- ✓ No deploy stage past Stage 0 invoked (Stage 1+ never touched)
- ✓ No `hdc target boot`
- ✓ No `setenforce`
- ✓ No touch to `/data/local/tmp/v3-hbc-chroot/` (which is gone anyway)
- ✓ No overwrite of `/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe` (3.2.0b preserved for forensic)
- ✓ Commit doc locally; no push
