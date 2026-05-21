# V3-V7 init pre-init chain — root cause for /system reverts on reboot

Agent 109 deep-read, 2026-05-21. Local-only, board READ-ONLY (file recv).

## TL;DR

V7 (HBC stock) does **not** wipe `/system` from an init `pre-init` job. The /system
partition is mounted as **`/` itself**, ext4 `ro,relatime,nodev` — files written to
`/system/...` while `/` is `mount -o remount,rw` are persisted to that block device
*until* one of the OTA-style services flashes the partition back.

The most-confidence restorer is the **`write_updater` + bootloader-misc**
recovery path, gated by a param trigger that fires on routine boots when
`persist.global.locale=*` or `const.settings.os_version_suffix=*` are set
(both true on a stock device). This writes a recovery-mode command to
`/misc`, and a subsequent normal reboot — which the bootloader interprets via
the misc-slot — boots into updater mode and re-flashes the `system`
partition from `/data/update/ota_package/`.

There is NO pre-init job that issues a "wipe-and-recreate" of /system. The
restoration is **lazy** and **out-of-band**: triggered via param events and
materialised by a separate reboot, not synchronously by init.

## What `pre-init` actually does

From `/system/etc/init.cfg`'s `pre-init` job (executes once at very start of
init second stage):

```
write /proc/sys/kernel/{sysrq,dmesg_restrict,kptr_restrict}
start ueventd
start watchdog_service
mkdir /data
mount_fstab_sp /vendor/etc/fstab.${ohos.boot.hardware}    <-- THIS line
restorecon /data --skip-ELX
chown/chmod /data and a tree of /data/service/el0/*
mount configfs none /config
load_access_token_id
bootchart start
trigger init-hitrace
```

Then `init` job runs `mksandbox system` and `mksandbox chipset` (more below).

### `mount_fstab_sp /vendor/etc/fstab.rk3568`

Pulled fstab:

```
/dev/.../by-name/system  /usr       ext4 ro,barrier=1   wait,required
/dev/.../by-name/vendor  /vendor    ext4 ro,barrier=1   wait,required
/dev/.../by-name/chip_ckm /chip_ckm ext4 ro,barrier=1   wait
/dev/.../by-name/sys-prod /sys_prod ext4 ro,barrier=1   wait
/dev/.../by-name/chip-prod /chip_prod ext4 ro,barrier=1 wait
/dev/.../by-name/userdata /data     f2fs discard,noatime,nosuid,nodev,...  wait,check,fileencryption,quota
/dev/.../by-name/misc    /misc      none none           wait,required
```

Important: fstab lists `system → /usr`, but observed `mount` output shows
`/dev/block/.../by-name/system on / type ext4 (ro,seclabel,nodev,relatime)`.
The `system` partition is the rootfs itself (mounted by the kernel/initramfs
before init second stage). The fstab entry is effectively a no-op for the
`system` slot at this point; what `mount_fstab_sp` actually adds here is
`/vendor`, `/chip_ckm`, `/sys_prod`, `/chip_prod`, `/data`, `/misc`.

So: no wipe at pre-init, just bring-up of /vendor and friends + /data.

### `mksandbox system` (in the `init` job, AFTER pre-init)

`mksandbox` is a **built-in init command** (no `/system/bin/mksandbox` binary
exists on V7 — verified). It reads `/system/etc/sandbox/system-sandbox.json`
and creates a per-process bind-mount namespace rooted at `/mnt/sandbox/system`.
Pulled JSON shows it bind-mounts `/system/{bin,etc,lib,profile,resource}`,
`/vendor/{lib,etc}`, `/dev`, `/proc`, `/data`, `/log`, `/sys`, `/mnt`,
`/sys_prod`, `/chipset/etc`, `/chip_prod/{lib,etc}`, and `/module_update`
into the sandbox view, with symlinks `/lib → /system/lib`, `/bin → /system/bin`,
`/etc → /system/etc`.

This is a **view operation only**. It does not modify the underlying
ext4 partition.  The on-disk files at `/system/lib/libwms.z.so` continue
to be what was last written. **mksandbox is NOT the wipe mechanism.**

## Complete cfg inventory + pre-init job map

138 files in `/system/etc/init/` + 4 in `/vendor/etc/init/` + the main
`/system/etc/init.cfg` + 3 vendor cfgs (`init.rk3568.cfg`,
`init.rk3568.usb.cfg`, `init.usb.cfg`).

CFGs that declare a `pre-init` job:

| cfg                             | what it does in pre-init                                   |
|---------------------------------|------------------------------------------------------------|
| `init.cfg`                      | sysctls, start ueventd, mount fstab, /data setup           |
| `access_token.cfg`              | mkdir /data/service/el0/access_token + load_access_token_id |
| `ubsan.cfg`                     | export UBSAN_OPTIONS                                       |
| `module_update_sa.cfg`          | **mkdir /data/module_update + /mnt/sys_installer**         |
| `hitrace.cfg`                   | hitrace setup                                              |
| `resource_schedule_service.cfg` | mkdir + mount /dev/cpuset, /dev/cpuctl cgroups             |
| `init.rk3568.cfg`               | vm.min_free_kbytes, mount debugfs, PHY peripheral role     |
| `vendor_hdf_peripheral.cfg`     | chown/chmod device-tree HDF device nodes                   |

**None** of these issues a `rm`, `mount -t ext4 ... /`, `dd`, `mkfs`, copy
to `/system/...`, or anything that would mutate the on-disk system partition.
`module_update_sa.cfg` only prepares directories under `/data`; the actual
overlay binary `/system/bin/check_module_update_init` doesn't fire until
**late-fs** (see `check_module_update.cfg`), well after pre-init.

## Where the restoration actually comes from

Three suspects, ranked by confidence:

### #1 (highest) — `write_updater` + bootloader misc-slot

`updater_normal.cfg`:

```
"jobs" : [
    { "name" : "boot",
      "cmds" : [ "rm /data/updater/rollback" ] },
    { "name" : "param:persist.global.locale=* || const.settings.os_version_suffix=*",
      "condition" : "persist.global.locale=* || const.settings.os_version_suffix=*",
      "cmds" : [ "start write_updater" ] }
],
"services" : [{
    "name" : "write_updater",
    "path" : ["/system/bin/write_updater", "updater_para"],
    "uid" : "update",
    "gid" : ["update", "system", "root"],
    "once" : 1,
    "secon" : "u:r:write_updater:s0"
}]
```

The condition is **trivially satisfied** on any stock device that has ever
booted (locale and version_suffix are set during first-boot). So
`write_updater updater_para` runs once per boot. The classic role of
`write_updater` in OHOS is to write a structured command into the bootloader's
**misc partition** (which fstab confirms is `/dev/.../by-name/misc → /misc`,
`wait,required`).

What the misc-slot contains determines what the bootloader does on the NEXT
reboot. If `updater_para` writes a flash-system or boot-recovery command,
the next reboot drops into **updater mode**, which reflashes the `system`
partition from `/data/update/ota_package/*.zip` (an A-B-less single-slot
update path) and clears the misc slot.

This perfectly explains:

- Agent 105's persist-test observation: counts went 4887 → 4888 (slight
  increase, no wipe) — **single reboot did NOT wipe** because the misc-slot
  was empty.
- Agent stage-b's observation: pre (May 19 21:55) had 2026-04-04 timestamps
  on `/system/lib/libwms.z.so`; post (May 20 12:15, after ≥1 intervening
  reboot) had **2017-08-04** factory timestamps → /system block-level
  reflashed.

Note: this is the same lazy-recovery mechanism Android exposes through
`/cache/recovery/command`. The `param:persist.global.locale=*` trigger looks
benign — it isn't.

### #2 (mid) — `check_module_update_init` (late-fs)

```
late-fs:  start check_module_update
service check_module_update_init  /system/bin/check_module_update_init setParam
        uid=update  secon=u:r:module_update_service:s0  once=1  start-mode=condition
```

Runs in **late-fs** (after post-fs-data; not pre-init). Reads
`/data/module_update/active/*` and bind-mounts those over `/module_update`
(the tmpfs at `/module_update` — observed in agent 105's mount output).
On the pulled board, `/data/module_update/active/` is empty (`No such file
or directory`), so this is a no-op currently. **Cannot explain the wipe by
itself**, but is a related mechanism that could overlay (not delete) files.

### #3 (low) — `sys_installer_sa` / `updater_sa`

Both are `ondemand=true` SA services. They do not auto-fire during boot;
they wait for client SAMGR calls. Probably the mechanism through which
write_updater stages its OTA package on disk, but they don't *themselves*
do work during a normal boot.

## Disable approach

To stop V7 from periodically reverting /system to factory, **disable the
`write_updater` trigger**:

Option A (least invasive — patch one cfg):

Edit `/system/etc/init/updater_normal.cfg` and either:

1. Delete the `param:persist.global.locale=*` job entirely, OR
2. Replace its condition with one that's never satisfied
   (e.g. `"condition" : "v3.never.fire=1"`).

Option B (mask the binary):
- `chmod 0` on `/system/bin/write_updater` (prevents exec; init will log
  failure but continue).

Option C (clear the misc partition pre-boot):
- `dd if=/dev/zero of=/dev/block/.../by-name/misc bs=4096 count=1024`
  before any reboot. This wipes the staged updater command. Has to be
  re-done each cycle.

Option A is the only durable fix. Note that V7's `/` is the ext4 system
partition mounted ro — `mount -o remount,rw /` then edit, but the resulting
edit will itself be undone on the **next** reboot that fires the cycle.
This is a chicken-and-egg: we must either (i) modify the cfg AND wipe misc
before the *first* reboot post-edit, or (ii) attack the cfg at the
underlying block-device level (mount system.img in a host loop, edit, and
re-flash) — V3-W2 ROM-flash recovery path.

## Disabling write_updater also removes /data/updater/rollback cleanup

Acceptable trade-off; that cleanup is a tidy-up not a correctness
requirement.

## Full pre-init execution order (best-effort)

OHOS init does not strictly preserve cfg load order across `pre-init`
jobs, but the merge-result effectively runs (per init source semantics —
hash-iteration-stable on V7):

1. `init.cfg::pre-init` — sysctls, ueventd, watchdog, mount_fstab, /data,
   load_access_token_id, configfs, load_persist_params, bootchart,
   trigger init-hitrace
2. `access_token.cfg::pre-init` — mkdir el0/access_token, load_access_token_id
3. `ubsan.cfg::pre-init` — export UBSAN_OPTIONS
4. `module_update_sa.cfg::pre-init` — mkdir /data/module_update, /mnt/sys_installer
5. `hitrace.cfg::pre-init` — hitrace setup
6. `resource_schedule_service.cfg::pre-init` — mount /dev/cpuset + /dev/cpuctl
7. `init.rk3568.cfg::pre-init` — vm.min_free_kbytes, mount debugfs, PHY role
8. `vendor_hdf_peripheral.cfg::pre-init` — chown HDF devnodes

Then `init` job runs (`mksandbox system|chipset`, the bulk of sysctls), then
post-init triggers (`early-fs`, `fs`, `post-fs`, `late-fs`, `post-fs-data`,
`firmware_mounts_complete`, `early-boot`, `boot`). `check_module_update` is
in `late-fs`. `write_updater` is on a **param condition** evaluated whenever
the param changes — for already-set params (locale, version_suffix), the
condition is evaluated at init startup, so it fires effectively every boot.

## Artifacts on disk (this agent's pulls)

All under `/tmp/v3-init-pull/`:

- `init.cfg` — main init cfg (already pulled by agent 105)
- `cfg-list.txt` — 137-line listing of `/system/etc/init/*`
- 16 high-suspicion cfgs pulled first, then 122 remaining cfgs (138 total)
  + `mmi_uinput.rc`, `saneservice.rc`, `scanservice.rc`
- `init.usb.cfg`
- `init.rk3568.cfg`, `init.rk3568.usb.cfg`, `fstab.rk3568`
- 4 vendor cfgs prefixed `vendor_`
- `system-sandbox.json` — what `mksandbox system` reads
- `module_update_sa.json` profile
- Probe results: `module_update_dir.txt`, `module_update_active.txt`,
  `sandbox_list.txt`, `check_mu_binary.txt`, `mksandbox_binary.txt`

No mutations were performed on the device. Channel B (`file recv`) only.
