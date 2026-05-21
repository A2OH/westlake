# /misc wipe fix + HBC deploy retry

Date: 2026-05-21
Agent: 111
Board: DAYU200 V7 (dd011a414436314130101250040eac00)

## TL;DR

- /misc wipe applied successfully (4 KB of zeros via `dd if=/dev/zero of=/dev/block/by-name/misc bs=1024 count=4; sync`) BEFORE Stage 3.5 reboot, and /misc remained zeroed AFTER reboot.
- Despite a clean /misc on both sides of the reboot, the entire /system tree REVERTED to factory baseline: appspawn-x missing, /system/android dir absent, all .orig_20260521 backups gone, all 12 file mtimes back to 2026-04-04 20:47.
- /misc-as-OTA-trigger hypothesis is FALSIFIED. The reflash mechanism is something other than `bootloader_message` in /misc; need to look elsewhere (initramfs `mount -o ro,override` of /system, `boot.img` ramdisk-baked /system, A/B slot switch, `/data/update/ota_package/firmware/versions/` recreation suggests OTA hooks were touched but no payload present, or a hardware-level "first boot detect" mechanism).

## 1. Pre-state verification

| Check | Result |
|-------|--------|
| `hdc list targets` | `dd011a414436314130101250040eac00` |
| `getenforce` | Enforcing |
| `uptime` | 25 min |
| /misc first 48 bytes | All zeros |
| /data/update/ota_package/firmware/versions/ | EMPTY (matches mandate) |
| /system/android (HBC marker) | ABSENT (clean factory baseline) |
| Channel A (`hdc shell echo`) | Working |

Matches mandate pre-state precisely.

## 2. Fix implementation (wrapper vs patch)

**Wrapper approach chosen.** No edit to `hbc-deploy-as-is/deploy/deploy_stage.sh`.

The wrapper consists of a single shell command run between Stage 3.9 (last data stage) and Stage 3.5 (sync+reboot stage):

```bash
"$HDC" shell 'dd if=/dev/zero of=/dev/block/by-name/misc bs=1024 count=4 2>&1; sync'
```

Rationale:
- HBC's `stage_3_5()` (deploy_stage.sh:670-714) only does `sync` + `reboot` + poll + service check. No /misc touch.
- A `grep -rnE "write_updater"` over `hbc-deploy-as-is/` returned ZERO hits. So HBC itself does not invoke the OHOS `write_updater` codepath.
- `bs=1024 count=4` (4 KB) covers the full `bootloader_message` struct (~2 KB) without touching unrelated partition regions. Per mandate hint, preferred over `bs=1M count=1`.

Wrapper script lives inline in the bash loop run below — no new file checked in.

## 3. Deploy outcome (stage-by-stage)

| Stage | Result | Notes |
|-------|--------|-------|
| 0 | PASS | hdc alive, factory baseline detected, boot image consistent |
| 1 | PASS | 14 .orig_20260521 backups created on device |
| 2 | PASS | foundation + render_service stopped, hdcd alive |
| 3.0 | PASS | 4 target dirs created |
| 3b | PASS | 10 OH service .so + libbms symlink |
| 3c | PASS | 38 aosp_lib + 3 adapter shims dual-path + chcon |
| 3d | PASS | 14 framework jars + ICU + fonts.xml + SE label fix |
| 3e | PASS | 27 boot image files md5-verified + chcon |
| 3f | PASS | 4 bin + 3 cfg + 1 selinux file_contexts + 3 symlinks + chmod + restorecon |
| 3.9 (try 1) | FAIL | Transient: `size mismatch ... local=564550 device=` (empty device size in output, file actually OK) |
| 3.9 (try 2) | PASS | 101 files verified (md5 + size), no drwx anomalies |

Full log: `/tmp/v3-111-pre-reboot.log` (51 KB).

## 4. /misc state pre/post wipe

```
=== /misc state pre-wipe @ 15:12:11 ===
00000000: 0000 0000 0000 0000 0000 0000 0000 0000  ................
00000010: 0000 0000 0000 0000 0000 0000 0000 0000  ................
00000020: 0000 0000 0000 0000 0000 0000 0000 0000  ................

=== /misc wipe (dd zero 4KB) @ 15:12:11 ===
4+0 records in
4+0 records out
4096 bytes (4.0 K) copied, 0.001 s, 3.9 M/s
WIPE_DONE_1501839275

=== /misc state post-wipe (should still be zeros) @ 15:12:11 ===
00000000: 0000 0000 0000 0000 0000 0000 0000 0000  ................
00000010: 0000 0000 0000 0000 0000 0000 0000 0000  ................
00000020: 0000 0000 0000 0000 0000 0000 0000 0000  ................
```

Wipe succeeded in <2 ms. /misc is still zero immediately after wipe (no concurrent writer raced us).

Full log: `/tmp/v3-111-misc-wipe.log`.

## 5. Stage 3.5 reboot result

```
[15:12:22] Stage 3.5 · reboot + 系统健康验证
[15:12:22] sync complete, rebooting...
ABORT: device did not come back online in 300s    # HBC's internal timeout
Stage 3.5 rc=1 done @ 15:17:34
```

HBC's `stage_3_5()` polls `hdc shell echo alive` once every 5 s for 60 iterations = 300 s. The board needed longer.

Extended polling outside HBC:

```
=== Continued poll @ 15:17:42 ===
Board enumerated at attempt 38 (15:21:31)
```

Total reboot duration: ~9 min from `hdc shell reboot` issuance to first successful `hdc shell echo`. Matches agent 105 observation (7+ min not unusual).

Post-reboot state:
```
POST_REBOOT_1609502421
Enforcing
 20:00:22 up 0 min,  0 users,  load average: 6.50, 1.44, 0.47
```

Uptime 0 min — fresh boot, NOT a stuck/recovery state. Board is fully responsive on Channel A.

## 6. CRITICAL: /system persistence check

```
--- appspawn-x ---
ls: /system/bin/appspawn-x: No such file or directory

--- libwms.z.so ---
-rw-r--r-- 1 root root u:object_r:system_lib_file:s0  1072868 2026-04-04 20:36 /system/lib/libwms.z.so

--- Stage-1 backups ---
ls: /system/lib/*.orig_20260521: No such file or directory

--- Count of recent files in /system ---
0

--- /system/android dir (HBC deploy marker) ---
ls: /system/android/: No such file or directory

--- /misc post-reboot state ---
00000000: 0000 0000 0000 0000 0000 0000 0000 0000  ................
```

| Marker | Status |
|--------|--------|
| /system/bin/appspawn-x | **MISSING** (deploy reverted) |
| /system/lib/libwms.z.so | PRESENT but **factory mtime 2026-04-04 20:36** (deploy reverted) |
| .orig_20260521 backups (12 of them) | **MISSING** (Stage 1 outputs gone) |
| /system/android/ tree (HBC deploy root) | **MISSING** (entire HBC tree gone) |
| Files newer than /system/build.prop | **0** (nothing post-factory) |
| /misc post-reboot | **STILL ZEROS** (wipe held across reboot) |
| /system/ top-level mtime | 2026-04-04 20:47 (factory baseline) |

**Verdict: DEPLOY REVERTED — wipe DID NOT prevent revert.**

The /misc wipe demonstrably HELD (still zero after reboot). The reflash happened anyway. So /misc/bootloader-message is NOT the trigger.

Subsidiary observation: `/data/update/ota_package/firmware/versions/` exists but is EMPTY, and the parent dirs have mtime `2021-01-01 20:00` (epoch start, recreated this boot). Some process recreates this tree on every boot regardless of whether an OTA actually ran. So presence of these dirs is NOT proof of OTA execution.

Full log: `/tmp/v3-111-persistence.log`.

## 7. HBC HelloWorld + McD (if reached)

Not attempted. With appspawn-x, /system/android/, and all HBC binaries gone, no HBC-runtime test can run.

## 8. Verdict on /misc-wipe hypothesis

**FALSIFIED.**

The hypothesis stated:
1. `write_updater` writes OTA command to /misc.
2. Bootloader reads /misc on next boot, enters updater mode, reflashes /system.
3. Zeroing /misc before reboot prevents step 2.

Agent 111 satisfied (3) cleanly:
- /misc was confirmed zero immediately before reboot.
- /misc was confirmed STILL zero immediately after reboot.

Yet /system reverted in full. Therefore (2) is not driven by /misc contents. The reflash mechanism is something else.

Candidate alternative mechanisms (NOT investigated by this run):

| # | Hypothesis | Quick test |
|---|-----------|-----------|
| H1 | /system is mounted from a ramdisk baked into boot.img (or read-only A/B slot), and writes appeared persistent only because we never rebooted in the prior successful sessions — `/system` is overlaid each boot from `/sbin/init`. | Check `mount` output during normal boot: is /system on mmcblk0pN or on a tmpfs/squashfs? Compare to /vendor. |
| H2 | A first-boot detection script (e.g., `/etc/init/factory_reset.cfg`) reflashes /system from `/system.img` stored in another partition. | Grep `/system/etc/init/*.cfg` and `/vendor/etc/init/*.cfg` for `dd`, `mkfs`, `cp -r /` on first boot. |
| H3 | Bootloader treats /system itself as immutable (e.g., dm-verity hash mismatch triggers rollback on next boot). | Check whether /system is dm-verity-protected — `dmsetup ls`, `cat /proc/cmdline` for `verity` or `dm=`. |
| H4 | The OHOS `update_subsystem` recreates `/data/update/ota_package/firmware/versions/` on every boot AND looks for staged updates in a different location we haven't observed. | Trace updater binary: `find / -name 'updater*' -o -name 'update_subsystem*' 2>/dev/null`. |
| H5 | `prepare_images.sh`-style mechanism baked into device: a /vendor or /chip_prod init script does `mkfs.ext4 + mount + cp` on every boot if a sentinel file is absent. | Inspect `/vendor/etc/init/*.cfg`. |

## 9. Recommendation

1. **DO NOT pursue further /misc-based mitigations** — the hypothesis is falsified by direct evidence.
2. **Test H1 first** (mount output + boot.img inspection): if /system is overlaid from ramdisk each boot, every persistent-write attempt in this hardware/boot.img combo is futile — pivot to either modifying boot.img directly or to a runtime-injection deploy path (no reboot).
3. **Test H2 second** (init.cfg grep): cheap, high-information; reveals factory-reset / first-boot hooks.
4. **Test H3 third** (dm-verity): if active, /system can never be persistently modified without rebuilding the boot/verity chain.
5. **Side-effect for current agent's deploy attempt**: Board is back to clean factory baseline (alive, /system factory, /misc zeroed, hdcd alive). No brick — this is actually a clean handoff for the next investigation pass.
6. **Pre-flight for next agent**: capture `mount` output BEFORE any deploy. The previous deploy reports do not have it as a captured baseline. If /system is overlay-mounted that fact will be self-evident.

## Appendix A — Exact commands

```bash
HDC=/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe
export ADAPTER_ROOT=$HOME/android-to-openharmony-migration/hbc-deploy-as-is/adapter
export TS=20260521
export MSYS_NO_PATHCONV=1
cd $HOME/android-to-openharmony-migration/hbc-deploy-as-is/deploy

# Pre-state
"$HDC" shell 'dd if=/dev/block/by-name/misc bs=128 count=1 2>/dev/null | xxd | head -3'

# Stages 0..3.9
for stage in 0 1 2 3.0 3b 3c 3d 3e 3f 3.9; do
    bash deploy_stage.sh $stage 2>&1 | tee -a /tmp/v3-111-pre-reboot.log
done
# Re-run 3.9 once due to transient channel-A hiccup
bash deploy_stage.sh 3.9

# /misc wipe (the fix being tested)
"$HDC" shell 'dd if=/dev/zero of=/dev/block/by-name/misc bs=1024 count=4 2>&1; sync'
"$HDC" shell 'dd if=/dev/block/by-name/misc bs=128 count=1 2>/dev/null | xxd | head -3'

# Stage 3.5 (HBC's sync + reboot + health verify)
bash deploy_stage.sh 3.5

# Extended re-enumeration poll (HBC's 300 s timed out; board needed ~9 min)
for i in $(seq 1 80); do
    "$HDC" list targets 2>&1 | grep -q dd011a414436314130101250040eac00 && break
    sleep 6
done

# Persistence check
"$HDC" shell 'ls -laZ /system/bin/appspawn-x'
"$HDC" shell 'ls -laZ /system/lib/libwms.z.so'
"$HDC" shell 'ls /system/lib/*.orig_20260521 | head'
"$HDC" shell 'find /system -newer /system/build.prop -type f | wc -l'
"$HDC" shell 'ls -la /system/android/'
"$HDC" shell 'dd if=/dev/block/by-name/misc bs=128 count=1 | xxd | head -3'
```

## Appendix B — Artifact list

| Path | Purpose |
|------|---------|
| `/tmp/v3-111-pre-reboot.log` | Full stages 0..3.9 + 3.9 retry log (51 KB) |
| `/tmp/v3-111-misc-wipe.log` | /misc pre + wipe + post output |
| `/tmp/v3-111-reboot.log` | Stage 3.5 attempt + extended poll output |
| `/tmp/v3-111-persistence.log` | /system + /misc post-reboot verification |
| `/tmp/v3-111-final-hilog.txt` | 200-line hilog tail post-reboot |
| `/tmp/v3-111-final-dmesg.txt` | 150-line dmesg tail post-reboot |
| `docs/engine/V3-MISC-WIPE-FIX-RUN.md` | This report |
