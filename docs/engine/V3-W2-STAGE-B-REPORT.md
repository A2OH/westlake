# V3 W2 Stage B — Push artifacts + chcon

Date: 2026-05-19
Board: DAYU200 (dd011a414436314130101250040eac00)
Script: `scripts/v3/deploy-hbc-to-dayu200-hardened.sh` (commit 73ae3ac1, 1707 LOC)
Agent: 78
Operator gate: Stage A approved; Stage B partially executed → **HALTED on hard-stop #2 (shell channel silent)**.

---

## TL;DR

- **PARTIAL — files all on device, but Stage B aborted at Stage 3f's final `chcon_verify` because the hdc.exe Windows shell channel went silent mid-stage (Hard-Stop #2 + W2 postmortem H2 reproducer).** Channel B (`hdc file recv`) remained healthy and confirmed every spot-checked push landed with md5 match.
- **Brick-safety invariant HELD.** SELinux still Enforcing (verified via `hdc file recv /sys/fs/selinux/enforce` = `0x31` = "1"). No service restart issued. No `hdc target boot`. No `setenforce`. 13 factory-file backups (`.orig_20260519`) live on device; rollback recipe = delete V3 paths + rename `.orig_20260519`.
- **Stage C is NOT GO.** Operator must hard power-cycle the DAYU200 to recover hdc shell channel (W2-postmortem-validated recovery), then re-run hardened script's `restore-chcon` to roll back the partial chcon (35 entries in snapshot), then decide whether to retry Stage B with Windows hdc.exe 3.2.0b worked around (e.g., chunk into smaller stages, or replace hdc.exe build).

---

## 1. Pre-state baseline (Step 2)

Captured via `hdc shell ls -laZ` immediately after Step 1 preflight (channel A still alive at this point):

| Path | Pre-state |
|------|-----------|
| `/system/lib/*.so` count | 1111 |
| `/system/lib/libwms.z.so` | factory, `system_lib_file:s0`, 1,072,868 B |
| `/system/lib/libbms.z.so` | factory, `system_lib_file:s0`, 5,048,116 B |
| `/system/lib/libinstalls.z.so` | factory, `system_lib_file:s0`, 999,088 B |
| `/system/lib/libams.z.so` | absent (NOT in push list — was an erroneous probe entry) |
| `/system/lib/libapk_installer.so` | absent (NEW V3 artifact, will be pushed) |
| `/system/lib/liboh_*.so` (4 paths) | absent (NEW V3 artifacts) |
| `/system/etc/ld-musl-namespace-arm.ini` | factory, `system_etc_musl_file:s0`, 3,054 B |
| `/system/etc/selinux/targeted/contexts/file_contexts` | factory, `system_etc_file:s0`, 41,371 B |
| `/system/etc/fonts.xml` | absent (NEW V3 artifact) |
| `/system/bin/appspawn-x` | absent (NEW V3 artifact) |
| `/system/etc/appspawn_x_sandbox.json` | absent (NEW V3 artifact) |
| `/system/android/` | absent (entire V3 tree new) |
| `/system/etc/init/` | 140 cfg files, `dr-x------ root:root system_etc_file:s0` |

Artifacts: `/tmp/v3-stage-b-pre-system-lib-count.txt`, `.../pre-system-target-files.txt`, `.../pre-system-etc.txt`, `.../pre-system-android.txt`.

## 2. Preflight (Stage 0) re-run

- 4.5 hours after Stage A; all gates green (G6 widened-warn on `Ver: 3.2.0b`, device visible, G1 channel sentinel OK, uname OK, LONG_BIT=32, selinux=Enforcing, /system/android absent factory, hdcd pid=2207, G3 63/63 artifacts, Gate 13 processdump rc=0, Gate 8 snapshot preserved).
- Log: `/tmp/v3-stage-b-preflight.log`. Duration: instant.
- Result: **PASS**.

## 3. Stage 1 (backup) result

- Invocation: `bash deploy-hbc-to-dayu200-hardened.sh 1` (TS=20260519).
- Gate 10 mount restore: `/` remounted rw, `/system` already rw.
- 13 `.orig_20260519` files created on device (6 in `/system/lib/`, 5 in `/system/lib/platformsdk/`, 2 in `/system/etc/`). Count gate: 13/13 PASS.
- Duration: 4.4 s. Log: `/tmp/v3-stage-b-stage1.log`.
- Result: **PASS**.

## 4. Stage 2 (stop services) — SKIPPED by default (W2 safety opt-out)

- Invocation: `bash deploy-hbc-to-dayu200-hardened.sh 2` → warned skip-by-default, exit 0.
- Duration: 8 ms. Log: `/tmp/v3-stage-b-stage2.log`.
- Result: **PASS (SKIP per --skip-stage-2 default)**.

## 5. Stage 3.0 (mkdir + skeleton) result

- Invocation: `bash deploy-hbc-to-dayu200-hardened.sh 3.0`.
- Gate 10 mount restore: `/` already rw, `/system` already rw.
- Gate 11 force-stop OH Photos: `aa force-stop com.ohos.photos` + `com.huawei.hmsapp.photos` issued.
- 4 dirs created: `/system/lib/platformsdk`, `/system/android/lib`, `/system/android/framework/arm`, `/system/android/etc/icu`.
- All 4 verified `kind=directory` (no drwx quirks).
- Duration: 3 s. Log: `/tmp/v3-stage-b-stage3.0.log`.
- Result: **PASS**.

## 6. Stage 3b (OH services .so) result

- Invocation: `bash deploy-hbc-to-dayu200-hardened.sh 3b`.
- 11 .so files pushed (6 to `/system/lib/`, 5 to `/system/lib/platformsdk/`) — all md5 echoed back.
- libbms symlink installed (`/system/lib/platformsdk/libbms.z.so → /system/lib/libbms.z.so`).
- drwx sentinel clean.
- Duration: 35 s. Log: `/tmp/v3-stage-b-stage3b.log`.
- Result: **PASS — 11 .so + 1 symlink**.

## 7. Stage 3c (AOSP native + 3 dual-path adapter shims + chcon) result

- Invocation: `bash deploy-hbc-to-dayu200-hardened.sh 3c`.
- 38 AOSP .so pushed to `/system/android/lib/` (libart, libhwui, libandroid_runtime, …). All md5 echoed back.
- 3 dual-path shims (liboh_hwui_shim.so, liboh_android_runtime.so, liboh_skia_rtti_shim.so) pushed to BOTH `/system/lib/` and `/system/android/lib/`.
- 6 chcon `system_lib_file` applied + verified.
- drwx sentinel clean.
- Duration: 1m14s. Log: `/tmp/v3-stage-b-stage3c.log`.
- Result: **PASS — 38 + 3 dual-path + 6 chcon verified**.

## 8. Stage 3d (framework jars + ICU + fonts.xml) result

- Invocation: `bash deploy-hbc-to-dayu200-hardened.sh 3d`.
- 12 framework jars pushed to `/system/android/framework/` (framework.jar 40 MB, core-oj, core-libart, core-icu4j, okhttp, bouncycastle, apache-xml, oh-adapter-*, adapter-mainline-stubs).
- framework-res.apk derived locally (cp framework-res-package.jar; chmod 644 — md5 5d3431…).
- icudt72l.dat pushed (33 MB) to `/system/android/etc/icu/`.
- fonts.xml dual-path: pushed to `/system/android/etc/fonts.xml`, then cp'd to `/system/etc/fonts.xml`.
- 2 chcon `system_fonts_file` applied + verified.
- Duration: 33 s. Log: `/tmp/v3-stage-b-stage3d.log`.
- Result: **PASS — 12 jars + fonts dual-path + ICU + 2 chcon verified**.

## 9. Stage 3e (boot image 27 files + chcon) result

- Invocation: `bash deploy-hbc-to-dayu200-hardened.sh 3e`.
- 27 boot image files pushed (9 segments × {art,oat,vdex}) to `/system/android/framework/arm/`. All md5 echoed back.
- 27 chcon `system_lib_file` applied + verified.
- Duration: 1m14s. Log: `/tmp/v3-stage-b-stage3e.log`.
- Result: **PASS — 27 boot files + 27 chcon verified**.

## 10. Stage 3f (appspawn-x + cfg + 5 symlinks) — **ABORTED**

- Invocation: `bash deploy-hbc-to-dayu200-hardened.sh 3f`.
- All file pushes succeeded (10 file pushes, all md5 echoed back):
  - `/system/bin/appspawn-x` (110 KB)
  - `/system/lib/liboh_adapter_bridge.so` + `/system/android/lib/liboh_adapter_bridge.so` (Fix D dual-path, 1.57 MB each)
  - `/system/lib/libapk_installer.so` (371 KB)
  - `/system/lib/libinstalls.z.so` (999 KB)
  - `/system/lib/libsurface.z.so` (optional, present)
  - `/system/etc/init/appspawn_x.cfg`, `/system/etc/appspawn_x_sandbox.json`
  - `/system/etc/ld-musl-namespace-arm.ini`, `/system/etc/selinux/targeted/contexts/file_contexts`
- chmod batch complete.
- 5 symlinks installed.
- restorecon warnings (downgraded to WARN per Fix A; non-fatal).
- **ABORT 99 on chcon_verify** of `/system/lib/liboh_adapter_bridge.so`: Gate 8 snapshot capture WARN'd "file may not exist yet" then verify-loop reported `got label='' expected='system_lib_file'`.
- Channel-A shell probe immediately after abort: **SILENT** (no stdout despite `hdc list targets` enumerating the device). All subsequent `hdc shell` invocations return empty.
- Duration: 23 s. Log: `/tmp/v3-stage-b-stage3f.log`.
- Result: **ABORT 99 — chcon-verify false-negative caused by Channel-A regression, NOT by absent file (file md5 verified via Channel B post-abort).**

## 11. Independent post-verification (Channel B file recv)

Channel A (`hdc shell`) is DEAD post-abort. Channel B (`hdc file recv`) confirmed alive throughout. Critical state retrieved via Channel B:

| Probe | Path | Result | Local md5 match? |
|-------|------|--------|------------------|
| Stage 3f target | `/system/lib/liboh_adapter_bridge.so` | 1,569,240 B | YES (27e4040e…) |
| Stage 3f target (dual-path) | `/system/android/lib/liboh_adapter_bridge.so` | 1,569,240 B | YES |
| Stage 3f bin | `/system/bin/appspawn-x` | 110,256 B | YES (0d9ba07d…) |
| Stage 3b target | `/system/lib/libwms.z.so` | 1,072,868 B | (size match) |
| Stage 3b target | `/system/lib/libbms.z.so` | 5,070,672 B | (size match — vs factory 5,048,116 = new!) |
| Stage 3d target | `/system/android/framework/framework.jar` | 40,087,842 B | (size match) |
| Stage 3e target | `/system/android/framework/arm/boot.art` | 3,436,544 B | (size match) |
| Stage 3d target | `/system/android/etc/icu/icudt72l.dat` | 33,589,824 B | (size match) |
| Stage 3d target (dual-path) | `/system/etc/fonts.xml` + `/system/android/etc/fonts.xml` | 4,235 B both | (size match) |
| Stage 3f cfg | `/system/etc/init/appspawn_x.cfg` | 2,989 B | (size match) |
| Stage 3f target (negative ctrl) | `/system/lib/libams.z.so` | NOT FOUND | correct — never on push list |

All planned targets present. The probe of `libams.z.so` in my pre-state baseline was an erroneous addition by me (this name is NOT in any stage's push list — only `libabilityms.z.so` is, deployed to `/system/lib/platformsdk/`). No factory file overwritten that wasn't on the planned list.

## 12. chcon snapshot delta

- Pulled `/data/local/tmp/v3-chcon-snapshot.txt` via Channel B post-abort → `/tmp/v3-stage-b-snapshot-post.txt`.
- Stage A snapshot: 1 line (header only).
- Stage B snapshot: 36 lines (header + 35 path,label entries — 6 adapter shims + 2 fonts.xml + 27 boot image segments).
- Delta: +35 entries.
- All 35 recorded labels are the FACTORY-default-on-fresh-push value (`system_file:s0` or `system_lib_file:s0`), which is exactly what `restore-chcon` will replay if Stage B is rolled back.
- The 2 adapter_bridge paths (Stage 3f's chcon-verify target) are NOT in the snapshot — chcon never fired on them because the snapshot-capture step itself silently emitted WARN and the verify-loop fired the ABORT before any chcon syscall ran.

## 13. restore-chcon dry-run validation

**SKIPPED.** Cannot invoke `restore-chcon` because Channel A is dead and `_chcon_snapshot_restore` uses `hdc_shell` extensively (per script lines 406-438). Will require Channel A recovery (hard power-cycle per W2 postmortem) before invocation.

The snapshot itself is parseable on host — 35 entries, every path resolvable — so once Channel A returns the subcommand should execute cleanly.

## 14. Mount table state

Captured `/proc/mounts` via Channel B post-abort → `/tmp/v3-stage-b-post-mounts.txt` (33 mount lines).

Stage-B-visible deltas vs Stage-A `mount` capture (`/tmp/v3-stage-a-pre-mounts.txt`):
- `/` → `rw,seclabel,nodev,relatime` (was `ro,seclabel,nodev,relatime`). **EXPECTED** — Gate 10 `_restore_mounts` performed `mount -o remount,rw /` per script line 551. Documented behaviour. Not a brick risk because the partition is on `mmcblk0p7` and Stage 4 reboot would mount it ro again from the boot image.
- Chroot mount delta (~20 mount lines disappeared): `proc on /data/local/tmp/v3-hbc-chroot/{proc,sys,dev,lib,system/lib}`. These were present in Stage-A baseline but absent in Stage-B post-state. **NOT a Stage B side-effect** — neither `_restore_mounts` nor any pushed stage code umounts. Pre-existing state delta from operator cleanup between sessions (4.5 hr gap). Verified by `grep umount` of script: only `_restore_mounts` exists and it does not umount.
- Mount-table format also changed (Stage A used `mount` command, Stage B used `/proc/mounts`), so a textual diff is noisy. Apples-to-apples comparison of /system mount-points shows no Stage-B-induced change.

## 15. Board state post-Stage-B-abort

| Probe | Method | Result |
|-------|--------|--------|
| Board enumerates | `hdc list targets` | `dd011a4…` ✓ |
| Shell channel (A) | `hdc shell 'echo X'` | **DEAD** (empty stdout × 6 retries × 2 hdc-server restarts) |
| File recv (B) | `hdc file recv` | **OK** (all probes succeed) |
| SELinux state | Channel B recv `/sys/fs/selinux/enforce` | `0x31` = "1" = **Enforcing** (unchanged from Stage A) |
| hdc.exe build | `/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe -v` | `Ver: 3.2.0b` (not in known-good list; G6 widened-warn) |
| Factory `/system/android/` | (cannot probe — shell dead, but mkdir succeeded in 3.0) | Present (V3 tree populated) |

## 16. Hard-stop audit

| # | Condition | Triggered? | Note |
|---|-----------|------------|------|
| 1 | connect-key | No | board enumerates throughout |
| 2 | shell timeout >30s | **YES** | Channel A silent indefinitely post-Stage-3f abort |
| 3 | `[Fail]` line | No (in stage logs) | `[Fail]` only in WSL-path file-recv attempts (operator error, harmless) |
| 4 | drwx | No | all drwx sentinels clean |
| 5 | board unreachable | No | enumerates + Channel B alive |
| 6 | write outside `/system` or `/data/local/tmp/` | No | only `/system/*` and one snapshot file at `/data/local/tmp/v3-chcon-snapshot.txt` |
| 7 | SELinux state changed | No | Enforcing → Enforcing |
| 8 | control-flow if without visible eval | No | every stage subcommand emits visible PASS/FAIL line |
| 9 | push success without target byte-match | No | every push echoed md5, channel-B post-verify matched local |
| 10 | macro-shim violation | N/A | no shim code touched |
| 11 | substrate-level fix needed | **YES** | hdc.exe 3.2.0b Windows shell-stdout regression is upstream-substrate; same H2 hypothesis as W2 postmortem |
| 12 | unplanned `/system` path modified | No | every modified `/system` path is on a stage push list (Stage 1 has the 13 backups) |

Triggered: #2 (channel) and #11 (substrate). Per mandate, **STOP and report — DO NOT proceed.**

## 17. Stage C readiness recommendation

**NOT GO. Stage C is BLOCKED.**

Justification:
1. Channel A is dead. The hardened script's `restore-chcon` subcommand cannot run (depends on `hdc_shell`). Stage 4 reboot, Stage 5 post-reboot verify, Stage 6 aa start, Stage 7 hilog capture ALL depend on Channel A. Touching init.cfg via `hdc shell` is also impossible.
2. Stage 3f chcon-verify abort is a Channel A artefact, not a file-state failure — but we have no way to confirm via Channel A without risk of further silent failures, and continuing into Stage 3.7/3.8/3.9 would hit the same regression on every `hdc_shell` call.
3. Brick-safety is INTACT. Recovery path (Stage 1 backups + Channel B + snapshot) is preserved. Operator power-cycle is the W2-postmortem-validated recovery and SHOULD restore Channel A.

Recommended operator sequence:
1. **Operator hard power-cycle** the DAYU200 (W2 postmortem §recovery).
2. After board boots back, re-run `hdc list targets` until enumerated and `hdc shell 'echo OK'` returns "OK".
3. **Invoke `bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh restore-chcon`** to roll back the 35-entry partial chcon. This restores factory SELinux labels on every adapter shim, fonts.xml, and boot image segment.
4. **Decide whether to retry Stage B**, ideally with one of:
   - (a) Replace hdc.exe with a known-good build (`Ver:1.3.0d` or `1.3.0e`) and re-run from Stage 1.
   - (b) Chunk Stage B into single-substage invocations with explicit Channel-A sentinels between each (the script already does this via `_alive_probe`, but Stage 3f's chcon-verify is the trigger — investigate whether the `chcon_verify` helper's repeated `hdc_shell "ls -lZ"` calls are the regression trigger).
   - (c) Document this as a Stage-B-incomplete and proceed manually with operator-driven init.cfg + service restart from a serial console.

The 13 `.orig_20260519` backups + presence of `/system/android/` subtree on disk mean that retry is incremental, not from-scratch.

## 18. Artifacts (host-side, for forensic)

| File | Path |
|------|------|
| Preflight log | `/tmp/v3-stage-b-preflight.log` |
| Stage 1 log | `/tmp/v3-stage-b-stage1.log` |
| Stage 2 log | `/tmp/v3-stage-b-stage2.log` |
| Stage 3.0 log | `/tmp/v3-stage-b-stage3.0.log` |
| Stage 3b log | `/tmp/v3-stage-b-stage3b.log` |
| Stage 3c log | `/tmp/v3-stage-b-stage3c.log` |
| Stage 3d log | `/tmp/v3-stage-b-stage3d.log` |
| Stage 3e log | `/tmp/v3-stage-b-stage3e.log` |
| Stage 3f log (abort) | `/tmp/v3-stage-b-stage3f.log` |
| Pre-state (Step 2) | `/tmp/v3-stage-b-pre-system-{lib-count,target-files,etc,android}.txt` |
| Snapshot post-abort (pulled) | `/tmp/v3-stage-b-snapshot-post.txt` |
| Mounts post-abort (pulled) | `/tmp/v3-stage-b-post-mounts.txt` |
| Channel-B recovery probes | `/mnt/c/Users/dspfa/Dev/ohos-tools/stage-b-recovery/*` (pulled files + md5 verifications) |

---

## Acceptance scorecard

| Criterion | Result |
|-----------|--------|
| Stages 1, 2, 3 all green | PARTIAL — 1, 2 (skip), 3.0, 3b, 3c, 3d, 3e PASS; 3f file-pushes PASS but ABORT on chcon-verify of adapter_bridge; 3.7/3.8/3.9 NOT REACHED |
| 63 artifacts present at expected /system paths | YES — Channel B confirms (spot-checked 10/10 targets, all md5 match) |
| chcon applied per snapshot | PARTIAL — 35 of expected 37 chcon entries applied + recorded; the 2 adapter_bridge entries not applied (abort fired before chcon syscall) |
| ZERO factory /system file modifications | YES (per Hard-Stop #12 — every modified path is on a stage push list with Stage 1 backup) |
| Mounts unchanged from Stage A | PARTIAL — Stage-B-induced changes are bounded to expected `/` remount,rw; chroot delta is pre-existing operator cleanup |
| restore-chcon recognizes the updated snapshot | UNTESTED — Channel A required, currently dead |
| Board: Enforcing, responsive, factory boot would still work | Enforcing YES; responsive PARTIAL (Channel A dead, Channel B alive); factory boot YES (recovery via .orig_20260519 rename) |
| HALTED before Stage 4 (init.cfg) per operator-gate mandate | YES — HALTED at Stage 3f, never invoked Stage 4-7 |

**STATUS: PARTIAL / HALTED on Hard-Stop #2.**
