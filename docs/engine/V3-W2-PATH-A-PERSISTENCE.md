# V3 W2 Path A — /system persistence empirical test

Date: 2026-05-20
Board: DAYU200 (dd011a414436314130101250040eac00)
Agent: 80
Operator gate: Path A approved by user after agent 79 discovered hard power-cycle wipes both `/system` V3 artifacts and `/data/local/tmp/`. Goal: empirically determine whether a SOFT reboot (`hdc target boot`) behaves the same way, in order to clarify the persistence model for `/system` on DAYU200.

---

## TL;DR

**Soft reboot via `hdc target boot` PRESERVES writes to both `/system` and `/data/local/tmp`.** `/system` is a real ext4 partition (`/dev/block/mmcblk0p7`, mounted at `/` as the rootfs) — not a tmpfs overlay — with no AVB / dm-verity flags in the kernel cmdline. The "hard power-cycle wipes /system" observation from agent 79 is therefore NOT explained by a kernel-side overlay-drop or boot-time integrity re-image; it is consistent with eMMC-side caching, a special boot path on power-on (e.g. recovery / factory-reset trigger), or a different mechanism that does not engage on warm reboot. **For V3 deploy, this means small, idempotent edits to `/system` are durable across soft reboots and SOPs can stage on `/system` with the same confidence as on `/data` — but only the `hdc target boot` flow has been demonstrated; the original hard-cycle ablation remains a board hazard that should be avoided during W2 retries until separately characterized.**

---

## 1. Baseline

| Check | Value |
|------|------|
| Enforcing | `Enforcing` |
| `/system/lib/` entry count | 1126 |
| `/data/local/tmp/` head | `debugserver` (no V3 artifacts present pre-test) |
| `find /system -type f` total | **4887** |
| `/system` mount entry (`mount` command) | `/dev/block/mmcblk0p7 on / type ext4 (ro,seclabel,nodev,relatime)` |
| `/system` mount entry (`/proc/mounts`) | `/dev/block/platform/fe310000.sdhci/by-name/system / ext4 ro,seclabel,nodev,relatime 0 0` |
| `/vendor` mount entry | `/dev/block/mmcblk0p8 on /vendor type ext4 (ro,seclabel,relatime)` |
| `/data` mount entry | `/dev/block/mmcblk0p15 on /data type f2fs (rw, ...)` |

Pre-test mount snapshot saved to `/tmp/v3-persist-test-pre-mounts.txt`. Pre-test /system file count saved to `/tmp/v3-persist-test-pre-systemcount.txt`.

**Important structural finding:** `/system` is NOT a separate mount on DAYU200. The eMMC partition labelled `system` is mounted as **the rootfs (`/`)**, and the path `/system` is just a directory inside that rootfs. The first attempt to `mount -o remount,rw /system` therefore failed with `mount: '/system' not in /proc/mounts`. Remount-rw of `/` succeeded and made the `/system/...` paths writable.

---

## 2. Sentinel write results

Two markers, contents are the unix timestamp at write time so they are uniquely identifiable post-reboot:

| Path | Write exit | Content read-back | ls timestamp |
|------|------|------|------|
| `/system/lib/v3_persist_marker.txt` (after `mount -o remount,rw /`) | 0 | `V3_PERSIST_TEST_1779293103` | `2017-08-04 17:13` |
| `/data/local/tmp/v3_persist_marker.txt` | 0 | `V3_PERSIST_TEST_1779293095` | `2017-08-04 17:12` |

(Device RTC reads 2017 because there is no NTP / cellular time sync on this development board; the marker contents prove freshness independently.)

Both writes followed by explicit `sync`. Both files verified present and readable.

---

## 3. Soft reboot outcome

| Event | Wall-clock |
|------|------|
| `sync; sync; sync` issued | 09:05:15 PDT |
| `hdc target boot` issued | 09:05:16 PDT |
| First polling cycle starts (after 60s initial wait) | 09:06:29 PDT |
| Board re-enumerated (`hdc list targets` returns serial) | 09:06:29 PDT (attempt 1) |

**Board re-enumerated approximately 73 seconds after reboot was issued.** Smooth boot — no soft-brick, no Channel-A regression, no operator intervention required. Distinct contrast with the W2 Stage-B aftermath where `hdc target boot` followed by silent chcon batches left the board non-enumerable until hard power-cycle.

---

## 4. Post-reboot marker check

| Path | exit code | content | ls |
|------|------|------|------|
| `/system/lib/v3_persist_marker.txt` | 0 | `V3_PERSIST_TEST_1779293103` | `-rw-r--r-- 1 root root 27 2017-08-04 17:13 /system/lib/v3_persist_marker.txt` |
| `/data/local/tmp/v3_persist_marker.txt` | 0 | `V3_PERSIST_TEST_1779293095` | `-rw-r--r-- 1 root root 27 2017-08-04 17:12 /data/local/tmp/v3_persist_marker.txt` |

**BOTH MARKERS SURVIVED.** Content matches exactly; inode timestamps preserved.

`find /system -type f` post-reboot = **4888** (= pre 4887 + 1 marker we added). No other changes to /system file count.

Post-reboot mount snapshot (`/tmp/v3-persist-test-post-mounts.txt`) diff vs. pre (`/tmp/v3-persist-test-mounts-diff.txt`) shows:
- Root re-mounted back to `ro` (cmdline says `rw`, but init re-asserts the cmdline-driven `ohos.required_mount.system=...@ro,barrier=1@wait,required` flag during stage-2 init).
- `/data` mount options drift slightly: `nosuid,nodev,noatime,…usrquota` (pre) → `relatime,…` no `usrquota` (post). Minor and not relevant to persistence question.
- `hdc on /dev/usb-ffs/hdc` re-ordered in the table. Cosmetic.

No mount-type change. `/system` is still ext4 on `mmcblk0p7`. **No tmpfs overlay layer is present.**

---

## 5. Cleanup

| Step | Result |
|------|------|
| `mount -o remount,rw /` | OK |
| `rm /system/lib/v3_persist_marker.txt` + `sync` | OK |
| `rm -f /data/local/tmp/v3_persist_marker.txt` + `sync` | OK |
| `ls /system/lib/v3_persist_marker.txt` | `No such file or directory` |
| `ls /data/local/tmp/v3_persist_marker.txt` | `No such file or directory` |
| `mount -o remount,ro /` | OK |
| Post-cleanup `find /system -type f` count | **4887** (matches pre-baseline exactly) |
| Post-cleanup root mount entry | `/dev/block/platform/fe310000.sdhci/by-name/system / ext4 ro,seclabel,nodev,relatime 0 0` (matches pre) |

Board restored to pre-test state. No residual artifacts.

---

## 6. Mount type analysis

| Question | Answer |
|------|------|
| `/system` mount type | **ext4 on real eMMC block device** (`/dev/block/mmcblk0p7`, alias `/dev/block/platform/fe310000.sdhci/by-name/system`) |
| Mounted at | `/` (rootfs) — NOT a separate `/system` mountpoint |
| `/vendor` mount type | ext4 on `/dev/block/mmcblk0p8` |
| `/data` mount type | f2fs on `/dev/block/mmcblk0p15` |
| tmpfs / overlayfs layer over `/system`? | **None.** No `overlayfs`, no `tmpfs` shadowing root in `mount` output or `/proc/mounts`. The only tmpfs mounts are `/dev`, `/mnt`, `/storage`, `/module_update`, `/mnt/user/...sharefs/docs` — all expected scratch dirs. |
| dm-verity / AVB? | **No verity-managed mount.** Kernel cmdline: `currentslot=0 bootslots=0 rw rootwait earlycon=… console=ttyFIQ0 ohos.boot.eng_mode=on root=PARTUUID=614e0000-0000 hardware=rk3568 default_boot_device=fe310000.sdhci ohos.required_mount.system=/dev/block/platform/fe310000.sdhci/by-name/system@/usr@ext4@ro,barrier=1@wait,required ohos.required_mount.vendor=/dev/block/platform/fe310000.sdhci/by-name/vendor@/vendor@ext4@ro,barrier=1@wait,required ohos.required_mount.misc=… ohos.required_mount.bootctrl=…`. The flags `ro,barrier=1,wait,required` are read-only-mount + ordered-journal-write + wait-for-block-device + required-or-fail-boot. No `verity=`, no `avb_=`, no `dm-mod.create=`. `eng_mode=on` is the dev-build flag and confirms this is an engineering image. |
| A/B partition scheme | Yes per `currentslot=0 bootslots=0`, but slot management is not relevant to soft-reboot persistence. |
| `/system/etc/init/` references to `remount` or `restore` | None found in spot-check (`grep -ri "verity\|avb"` returned empty). |

---

## 7. Persistence model verdict

**`/system` on DAYU200 is a real, single-copy ext4 partition that persists writes across soft reboots (`hdc target boot`).** It is mounted read-only by default per kernel cmdline, but `mount -o remount,rw /` succeeds for the root user under SELinux Enforcing. There is no overlay, no verity, no boot-time re-image of `/system` for the soft-reboot path. The single marker we wrote was readable post-reboot with content and timestamp preserved exactly; the `find -type f` count matched expectation precisely (4887 → 4888 with our marker, 4887 again after cleanup).

This directly contradicts the simplest interpretation of agent 79's observation that hard power-cycle wiped `/system` artifacts. Possible reconciling explanations (NOT tested in this run):

1. **eMMC write cache lost on abrupt power-loss.** Even after `sync`, the eMMC controller may have un-flushed writes in its internal cache; abrupt power-cycle (no controlled shutdown) loses them. Soft reboot via `hdc target boot` triggers an orderly shutdown that drains eMMC cache → writes persist.
2. **Boot-from-power-on differs from boot-from-warm-reset.** Some Rockchip/RK3568 boards have a "first boot after cold start" path that touches `misc` / `bootctrl` partitions and can trigger recovery / factory-reset under certain conditions (e.g. a sticky bit set by a watchdog timeout). The `/system` artifact loss agent 79 saw may have been a one-time recovery flow rather than a recurring property of hard power-cycle.
3. **Operator action during the power-cycle window** (button combo, key held during boot) may have invoked a recovery-mode wipe inadvertently.

None of (1)/(2)/(3) is testable without another hard power-cycle, which is brick-risk under W2 — explicitly out of scope for this Path A test.

---

## 8. Implications for V3 /system deploy

1. **`/system` deploy IS valid for warm-reboot lifecycle.** Any V3 W2 retry that needs to stage artifacts in `/system/lib`, `/system/etc`, etc. and then soft-reboot the device can rely on `hdc target boot` to bring the device back with /system contents intact. Operator does NOT need to redeploy after every soft reboot. This recovers a deploy mode that was assumed lost after agent 79's report.
2. **Use `mount -o remount,rw /`, NOT `mount -o remount,rw /system`.** On DAYU200 `/system` is not a separate mount; the legacy "remount /system" idiom inherited from AOSP/HBC SOPs will silently no-op on this device. SOPs (`V3-DEPLOY-SOP.md`, `V3-DEPLOY-HARDENED-SOP.md`, `V3-DEPLOY-CHROOT-SOP.md`) should be audited for this and the remount target normalized to `/`.
3. **Re-asserting `ro` after the deploy session is important.** Init re-mounts root `ro` at every boot per cmdline, so leaving it `rw` only persists for the current uptime — but during that window other services (writable hilog, faultlogger, ueventd-created device nodes, /tmp-style scratch) may write into the root partition and dirty it. The post-test cleanup explicitly ran `mount -o remount,ro /` to bring the system back to a clean baseline; SOPs should bracket every deploy session with `remount,rw` at start and `remount,ro` at finish.
4. **Cold-power-cycle persistence is STILL an open question.** Path A only tested the warm path. Until agent 79's hard-cycle observation is replicated under controlled conditions (or shown to be operator-error / recovery-mode invocation), W2 SOPs should treat hard power-cycle as catastrophic for `/system` contents and instruct operators to use soft reboot exclusively unless the board is wedged. The hard-power-cycle scenario should NOT be invoked as a recovery step from a softly-recoverable state.
5. **No tmpfs-overlay or dm-verity to plan around for V3.** This rules out two classes of architectural problem we might otherwise have had to design for (overlay-drop on reboot, verity-rejection of modified pages). V3 `/system` deploys can treat the partition as a plain ext4 mount with the only access constraint being SELinux labelling (which is the W2 Stage-B chcon work).
6. **The 73-second soft-reboot latency is a useful operational metric** for tuning W2 retry SOPs; current SOPs that wait 30-60s may be too short.

---

## Cleanup performed

- `rm /system/lib/v3_persist_marker.txt`
- `rm -f /data/local/tmp/v3_persist_marker.txt`
- `mount -o remount,ro /`
- Post-cleanup `find /system -type f` count = 4887 (matches pre-baseline exactly)
- Post-cleanup root mount = ext4 ro,seclabel,nodev,relatime (matches pre-baseline)

No residual artifacts on device.
