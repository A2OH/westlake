# V3 W2 Phase 1a — Board Run 4 (agent 69)

Date: 2026-05-19
Operator: agent 69
Board: DAYU200 (`dd011a414436314130101250040eac00`)
Script: `scripts/v3/deploy-hbc-to-dayu200-chroot.sh` (commit `ad52b63d`)
Mandate: retry Phase 1a after agent 68's RO bind-mount fix for `/lib` + `/system/lib`.

## TL;DR

- **FULL PASS — Phase 1a substrate validation complete.** All six stages green end-to-end under Enforcing SELinux. The chroot env probe now executes successfully; the `[v3-chroot-launch]` marker was emitted and observed by the verify stage; zero AVC denials in hilog tail.
- **The RO bind-mount fix from `ad52b63d` WORKS.** Stage 3 establishes 5 bind-mounts (3 RW: `/proc`, `/sys`, `/dev`; 2 RO: `/lib`, `/system/lib`); `ld-musl-arm.so.1` and 1126 OH system libraries are independently visible inside `/data/local/tmp/v3-hbc-chroot/`; Stage 4 chroot env probe succeeds where it failed ENOENT on run 3.
- **Brick-safety invariants HELD throughout**: 0 writes outside `$V3_CHROOT_ROOT`; SELinux remained Enforcing throughout (never touched, `--setenforce-0` not used); channel responsive throughout; no `hdc target boot` issued; no orphan mounts left dangling (5 expected bind-mounts in chroot — to be teardown'd in a later run per mandate).
- **One harness gotcha surfaced**: per-stage invocations write `launch.log` to a fresh timestamped `$LOG_DIR`, so `verify` invoked separately from `launch` cannot find the marker file. Documented + worked around by re-running `launch && verify` in one invocation with `V3_CHROOT_LOG_DIR=/tmp/v3-chroot-deploy-agent69`. Script comment at line 94 already calls this out; not a regression.

## 1. Pre-flight result

`bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh preflight` — **PASS** in ~2s.

- hdc binary: `/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe`, Ver: 3.2.0b
- Device visible: `dd011a414436314130101250040eac00`
- G1: hdc shell sentinel OK
- G3: all 63 required artifacts present locally
- `/data` free space: **18478 MB** (>= 600 MB required)
- Board pre-state captured at `/tmp/v3-chroot-deploy-current/board-state-pre.txt`
- SELinux mode: Enforcing
- WARN: `--setenforce-0` not specified; deferred decision to launch stage (and ultimately not needed).

## 2. Per-stage results (gate + duration)

| Stage | Status | Duration | Notes |
|-------|--------|----------|-------|
| 0 preflight | PASS | ~2 s | unchanged from run 3 |
| 1 setup | PASS | ~6 s | **25** chroot dirs verified (was 24; +1 = `/lib` mountpoint), `/system/bin/sh` re-copied + `-x` verified |
| 2 push | PASS | ~30 s | 70 files pushed, 3 size-verified (libwms.z.so 1072868, framework.jar 40087842, appspawn-x 110256); channel-alive sentinels at 25/50 boundaries; no [Fail] |
| 3 mount | PASS | ~2 s | **5 bind-mounts** (3 RW + **2 RO new**): proc, sysfs, tmpfs/dev, ext4(/lib RO), ext4(/system/lib RO). Linker + libs independently verified visible inside chroot. |
| 4 env | **PASS (REAL)** | ~1 s | `chroot $CHROOT /system/bin/sh /launch.sh` ran successfully; env probe wrote `[v3-chroot-launch]` lines to stdout. Previous ENOENT (run 3) RESOLVED. |
| 5 launch | PASS | ~1 s | env-probe-only mode (`$V3_CHROOT_HELLO_CMD` unset); chroot launch issued; `[v3-chroot-launch]` marker lines emitted; channel sentinel responded post-launch. |
| 6 verify | PASS | ~5 s | marker observed in `launch.log`; no AVC denials in hilog tail; post-deploy state captured. |

## 3. Launch result + marker emission

Marker **OBSERVED** (env-probe-only mode):

```
[v3-chroot-launch] PWD=/ UID= ARGV=
[v3-chroot-launch] LD_LIBRARY_PATH=/system/lib:/system/android/lib:/system/lib/platformsdk
[v3-chroot-launch] BOOTCLASSPATH=/system/android/framework/core-oj.jar:/system/android/framework/core-libart.jar:/system/android/framework/core-icu4j.jar:/system/android/framework/okhttp.jar:/system/android/framework/bouncycastle.jar:/system/android/framework/apache-xml.jar:/system/android/framework/adapter-mainline-stubs.jar:/system/android/framework/framework.jar:/system/android/framework/oh-adapter-framework.jar
[v3-chroot-launch] boot.art present: MISSING
[v3-chroot-launch] no command given; exiting 0 (env probe only)
```

One pre-marker line `/launch.sh[16]: id: inaccessible or not found` is expected (we don't push `id` into the chroot; only the HBC subset). Does not affect Phase 1a acceptance, which is "chroot sh executes the env probe and exits cleanly under Enforcing." The `boot.art present: MISSING` is also expected at Phase 1a; that's a Phase 1b artifact.

SELinux mode at every checkpoint: **Enforcing** (never touched).

## 4. Post-run board state

Independent probe (not from the script's own logging):

```
$ hdc shell 'getenforce; mount | grep v3-hbc-chroot | wc -l'
Enforcing
5

$ hdc shell 'ls -la /data/local/tmp/v3-hbc-chroot/lib/ld-musl-arm.so.1'
-rwxr-xr-x 1 root shell 1235180 2026-04-04 16:39 /data/local/tmp/v3-hbc-chroot/lib/ld-musl-arm.so.1

$ hdc shell 'ls /data/local/tmp/v3-hbc-chroot/system/lib/ | wc -l'
1126
```

Full mount table inside chroot:

```
proc on /data/local/tmp/v3-hbc-chroot/proc type proc (rw,relatime,gid=3009,hidepid=invisible)
sysfs on /data/local/tmp/v3-hbc-chroot/sys type sysfs (rw,seclabel,relatime)
tmpfs on /data/local/tmp/v3-hbc-chroot/dev type tmpfs (rw,seclabel,nosuid,relatime,size=995596k,nr_inodes=248899,mode=755)
/dev/block/mmcblk0p7 on /data/local/tmp/v3-hbc-chroot/lib type ext4 (ro,seclabel,nodev,relatime)
/dev/block/mmcblk0p7 on /data/local/tmp/v3-hbc-chroot/system/lib type ext4 (ro,seclabel,nodev,relatime)
```

Logs at `/tmp/v3-chroot-deploy-agent69/`:
- `deploy.log` 683 B
- `launch.log` 707 B (marker present)
- `board-state-post.txt` 25,511 B

Board pre/post diff: chroot mounts went 3 → 5 (added 2 RO bind-mounts); no other deltas. Board is responsive, Enforcing, no orphan files outside `$V3_CHROOT_ROOT`.

## 5. Hard-stops check — all clear

| # | Hard stop | Triggered? |
|---|-----------|-----------|
| 1 | connect-key prompt | NO |
| 2 | shell timeout > 30 s | NO (all stages well under) |
| 3 | `[Fail]` in any hdc output | NO |
| 4 | `drwx` (silent dir-listing leak) | NO |
| 5 | board unreachable | NO (responsive at every checkpoint) |
| 6 | write outside chroot | NO (only `/data/local/tmp/v3-hbc-chroot/`) |
| 7 | Enforcing not restored | N/A (Enforcing never disabled) |
| 8 | control-flow `if` without device-side eval | NO (`hdc_shell_check` helper in use since `f10ee81b`) |

## 6. Anomalies / observations

1. **Per-stage `LOG_DIR` rotation**: documented above. When operator runs `launch` and `verify` as separate invocations (without `$V3_CHROOT_LOG_DIR` pinning), each invocation gets a fresh timestamped physical dir, so `verify` cannot find `launch.log` from the prior invocation. Workaround: either run `all`, or run a chained launch+verify under a single invocation, or pin `V3_CHROOT_LOG_DIR`. The script's own comment at line 94 already flags this as a known TS1/TS2 mismatch; no code change needed.
2. **`/launch.sh[16]: id: inaccessible or not found`** — expected; `id` binary is not in the HBC artifact subset we push. Does not block; logged for transparency.
3. **`boot.art present: MISSING`** — expected at Phase 1a; boot image is a Phase 1b artifact. The env probe correctly reports this.
4. **No AVC denials in hilog**: clean Enforcing-mode chroot sh launch is a strong signal that the SELinux domain transitions are well-behaved for this path. Encouraging for Phase 1b.

## 7. Acceptance

**Phase 1a substrate validation: YES.**

The chroot at `/data/local/tmp/v3-hbc-chroot/` is now provably:
- self-contained (sh + minimal coreutils + HBC artifacts pushed; loader + OH libs bind-mounted RO)
- executable under Enforcing SELinux (sh forks, runs `/launch.sh`, emits marker, exits 0)
- safe to teardown (no orphan files outside chroot, no SELinux state change to revert)
- ready for Phase 1b: drop boot.art + invoke ART (`appspawn-x` or `dalvikvm`-analog) inside chroot, then watch for `MainActivity.onCreate` marker.

## 8. Next step

**Phase 1b — invoke ART/HBC inside the chroot.** No pivot needed. Sequence:
1. Locate or build `boot.art` for the OH ART build under chroot's `BOOTCLASSPATH`.
2. Stage a real `$V3_CHROOT_HELLO_CMD` (e.g., `appspawn-x` or direct ART invocation) and re-run `launch` with that command.
3. Expect a new marker (e.g., `MainActivity.onCreate` or `appspawn-x: started`).
4. Brick-safety invariants remain the same: Enforcing first; only fall back to `--setenforce-0` on documented AVC denial.

If Phase 1b reveals a new AVC class or `boot.art` cross-build gap, that's still progress, not regression — the substrate is now proven sound up to and including dynamic ELF execution inside the chroot under Enforcing.

## 9. Run sequence (for replay)

```bash
HDC=/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe
"$HDC" list targets
"$HDC" shell 'getenforce; mount | grep v3-hbc-chroot | wc -l'    # expect Enforcing, 3 (from agent 68)

cd /home/dspfac/android-to-openharmony-migration
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh preflight
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh setup
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh push
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh mount

# Independent mount verification:
"$HDC" shell 'mount | grep v3-hbc-chroot | wc -l'                 # expect 5
"$HDC" shell 'ls /data/local/tmp/v3-hbc-chroot/lib/ld-musl-arm.so.1'  # expect file
"$HDC" shell 'ls /data/local/tmp/v3-hbc-chroot/system/lib/ | wc -l'   # expect > 100

bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh env

# Run launch+verify under one LOG_DIR so marker check succeeds:
V3_CHROOT_LOG_DIR=/tmp/v3-chroot-deploy-agent69 bash -c '
  bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh launch &&
  bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh verify
'
```
