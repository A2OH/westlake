# V3 HBC V7-images pull

**Date**: 2026-05-21
**Agents**: 106 (initial pull, context exhausted mid-pull) → 110 (resumed, completed + verified)
**Mandate**: 8 bricks today on /system deploy path. Hypothesis: V7-images version drift between operator's flashed ROM and what HBC actually runs on their dev board. Pull HBC's current V7-images so operator can reflash with the version-matched ROM via RKDevTool.

---

## TL;DR

Pulled HBC's full V7-images directory (3.9 GB, 15 files, all dated Apr 4 2026) from `[REDACTED]@[REDACTED-IP]:$HOME/D200/V7-images/` to local `hbc-v7-images/`. Also pulled a **newer Apr 7 system.img** (3 days fresher, different md5) into `hbc-v7-images/newer-system-apr07/` as a separate file so operator can either flash the canonical V7-images or swap in the newer system.img. Operator copies `hbc-v7-images/` to Windows, opens RKDevTool, loads `config.cfg`, and flashes.

**All 16 files verified against MD5SUMS.txt (`md5sum -c` PASS).**

### Resume note (agent 110, 2026-05-21 15:19 PDT)

Agent 106's session ended mid-pull. Inherited state was:

| File | Status |
|---|---|
| 11 small / mid files (boot_linux, chip_ckm, chip_prod, config.cfg, eng_system, MiniLoaderAll, parameter, ramdisk, resource, sys_prod, updater) | md5 verified OK |
| `system.img` | truncated (998 MB / 2147 MB), wrong md5 — re-pulled |
| `newer-system-apr07/system.img` | truncated (1072 MB / 2147 MB), wrong md5 — re-pulled |
| `uboot.img`, `userdata.img`, `vendor.img` | missing — pulled |

Truncated files were deleted before re-pull. All four large files pulled via fresh `scp -P [REDACTED-PORT]` (no rsync resume — partials were too short to trust). Total agent-110 transfer: ~5.9 GB in 22 min on ~120-180 MB/min link.

There is **no separate "newer-system-apr07" directory on HBC's server** — that subdir is the local home for HBC's Apr 7 system.img build output at `$HOME/oh/out/rk3568/packages/phone/images/system.img`. Re-verified by `find $HOME/D200 -maxdepth 3 -name "*system.img*"` returning only the two known paths (V7-images/{system,eng_system}.img). HBC themselves do not call this a separate ROM — it is simply their latest /system build that has not yet been re-snapshotted into V7-images/.

---

## 1. HBC V7-images inventory

Remote path: `$HOME/D200/V7-images/`
Remote dir mtime: `Apr 4 20:47`
Total size: `3.9 GB` (15 files)

| File | Size (bytes) | mtime | MD5 | Verified |
|---|---:|---|---|:---:|
| `MiniLoaderAll.bin` | 455,104 | Apr 4 21:14 | `fde58a881bbef4f0b888497dbf975492` | OK |
| `boot_linux.img` | 67,108,864 | Apr 4 21:14 | `fa419e0dbdfe0e4a314770ff050ce38d` | OK |
| `chip_ckm.img` | 33,554,432 | Apr 4 21:14 | `0c4d857d938d0003f368078e6c555132` | OK |
| `chip_prod.img` | 52,428,800 | Apr 4 20:47 | `3aae9d8a9895c2b90cc0d3c9c6f1437c` | OK |
| `config.cfg` | 10,399 | Apr 4 21:14 | `bef0688d4e5945782bffb4e8bf1d51f5` | OK |
| `eng_system.img` | 12,582,912 | Apr 4 20:47 | `de037bcb1d72495e87a5875a7cceeb6f` | OK |
| `parameter.txt` | 788 | Apr 4 21:14 | `7fd3e708f8df6924743836a5f30469b6` | OK |
| `ramdisk.img` | 2,350,052 | Apr 4 20:47 | `73cc4dc94b9114985a187de10e9f4ef3` | OK |
| `resource.img` | 5,652,480 | Apr 4 21:14 | `28e1e54ab1d88cf72951eb70e4a5b508` | OK |
| `sys_prod.img` | 52,428,800 | Apr 4 20:47 | `0d2f2202708bf28e9b7174c2d6b1b274` | OK |
| `system.img` | 2,147,483,648 | Apr 4 20:48 | `5a96f7bcacd7d690e5c2fc782b0fff98` | OK |
| `uboot.img` | 4,194,304 | Apr 4 21:14 | `2a5858fea7a88a092a810b45d500f1a7` | OK |
| `updater.img` | 20,200,013 | Apr 4 21:14 | `f3d15a6b747824e7c3bbe937ee825ea3` | OK |
| `userdata.img` | 1,468,006,400 | Apr 4 20:47 | `9f120bbddc84277c6b2d84b4d1175df6` | OK |
| `vendor.img` | 268,431,360 | Apr 4 20:47 | `f08936b79319f97e81993c9ddced4709` | OK |

Plus the Apr 7 system.img variant (not part of V7-images, see Section 2):

| File | Size (bytes) | mtime | MD5 | Verified |
|---|---:|---|---|:---:|
| `newer-system-apr07/system.img` | 2,147,445,323 | Apr 7 09:33 | `fd746d6c883af139ac344060a78007ca` | OK (md5 matches HBC remote) |

`parameter.txt` content (partition layout):

```
FIRMWARE_VER:11.0
MACHINE_MODEL:rk3568_r
MACHINE_ID:007
MANUFACTURER: rockchip
MAGIC: 0x5041524B
ATAG: 0x00200800
MACHINE: rk3568_r
CHECK_MASK: 0x80
PWR_HLD: 0,0,A,0,1
TYPE: GPT
CMDLINE:mtdparts=rk29xxnand:0x00002000@0x00002000(uboot),0x00002000@0x00004000(misc),0x00001000@0x00006000(bootctrl),0x00003000@0x00007000(resource),0x00030000@0x0000A000(boot_linux:bootable),0x00002000@0x0003A000(ramdisk),0x00400000@0x0003C000(system),0x00200000@0x0043C000(vendor),0x00019000@0x0063C000(sys-prod),0x00019000@0x00655000(chip-prod),0x00010000@0x0066E000(updater),0x00008000@0x0067E000(eng_system),0x00008000@0x00686000(eng_chipset),0x00020000@0x0069E000(chip_ckm),-@0x01308000(userdata:grow)
uuid:system=614e0000-0000-4b53-8000-1d28000054a9
uuid:boot_linux=a2d37d82-51e0-420d-83f5-470db993dd35
```

---

## 2. Variant comparison

Only **one** `V7*` dir exists under HBC's tree:

```
$HOME/D200/V7-images           (3.9 GB, Apr 4)
```

However, HBC's OHOS build output at `$HOME/oh/out/rk3568/packages/phone/images/` is **mostly identical** to V7-images, except `system.img`:

| File | V7-images mtime | oh/out mtime | identical? |
|---|---|---|:---:|
| `system.img` | Apr 4 20:48 (2,147,483,648 B, md5 `5a96f7bc...`) | **Apr 7 09:33 (2,147,445,323 B, md5 `fd746d6c...`)** | **NO — DIFFERS** |
| `config.cfg` | Apr 4 | Apr 4 | yes |
| `parameter.txt` | Apr 4 | Apr 4 | yes |
| all other .img | Apr 4 | Apr 4 (same byte counts) | (not md5'd but timestamps identical) |

**Interpretation**: HBC rebuilt /system between Apr 4 and Apr 7 (3-day gap), but did NOT re-snapshot V7-images. Their bash history (`grep "V7-images"`) shows `cd V7-images` is their flash workflow — they likely flash V7-images then push delta'd OHOS bits over hdc/scp after boot (not a re-flash). For us this means **the V7-images we just pulled is the ROM that boots, but the post-boot /system content on HBC's board may carry the Apr 7 delta or other bits pushed over hdc since**.

Newer system.img also pulled into `hbc-v7-images/newer-system-apr07/system.img` so operator has the option to swap it in.

No `V7-images-v2`, `V7-images-new`, or `V7-images-YYYY-MM-DD` directories found anywhere under `$HOME/` (re-verified by agent 110 — `find $HOME/D200 -maxdepth 3 -name "*system.img*"` returns only the two paths above).

Last RKDevTool flash log on HBC's box: `Log2026-04-15.txt` (Apr 15). So HBC themselves last flashed ~5 weeks ago and have been working incrementally since.

---

## 3. Pulled location

- **WSL path**: `$HOME/android-to-openharmony-migration/hbc-v7-images/`
- **Windows path (via WSL UNC)**: `\\wsl$\Ubuntu\home\user\android-to-openharmony-migration\hbc-v7-images\`
- Contents: full 15-file V7-images snapshot + `newer-system-apr07/system.img` (Apr 7 build, optional swap)
- Total size on disk: 5.9 GB
- Integrity: see `MD5SUMS.txt` — `md5sum -c MD5SUMS.txt` PASS for all 15 V7-images files (verified by agent 110 on 2026-05-21 15:19 PDT); apr07 variant verified separately against HBC remote md5.
- **Not committed to git** (excluded via `.gitignore` rule `hbc-v7-images/`). Re-pull with the commands in the appendix if the local copy is lost.

---

## 4. Operator instructions: how to flash with RKDevTool

### Step A — copy to Windows

From WSL:

```bash
cp -r $HOME/android-to-openharmony-migration/hbc-v7-images/ /mnt/c/Users/dspfa/Dev/v7-images-2026-05-21/
```

Or from Windows Explorer, navigate to `\\wsl$\Ubuntu\home\user\android-to-openharmony-migration\hbc-v7-images\` and copy the whole folder to e.g. `C:\Users\dspfa\Dev\v7-images-2026-05-21\`.

The `newer-system-apr07\` subfolder comes along automatically; you do not need it for the first flash attempt — see Step C.

### Step B — RKDevTool flash (canonical V7-images)

You should already have `RKDevTool.exe` and the Rockchip USB driver installed from a prior session. If not, see the README in `\\wsl$\Ubuntu\home\user\D200\HiHope_DAYU200\烧写工具及指南\windows\` on HBC's box (driver: `DriverAssitant_v5.1.1.zip`).

1. Put DAYU200 into **Loader/MaskRom mode** (hold RECOVERY + tap RESET, or short MaskRom pins if the board is unresponsive after a brick).
2. Open `RKDevTool.exe`.
3. Right-click the file table → **Import Config** → select `C:\Users\dspfa\Dev\v7-images-2026-05-21\config.cfg`.
4. The file table should auto-fill with 11 partition entries. For each row whose `Path` is empty or wrong, click the `...` column and pick the matching file from `v7-images-2026-05-21\`:
   - `Loader` → `MiniLoaderAll.bin`
   - `parameter` → `parameter.txt`
   - `uboot` → `uboot.img`
   - `misc` → (leave empty if not in config)
   - `resource` → `resource.img`
   - `boot_linux` → `boot_linux.img`
   - `ramdisk` → `ramdisk.img`
   - `system` → `system.img`  **(see note about newer Apr 7 variant in Step C)**
   - `vendor` → `vendor.img`
   - `sys-prod` → `sys_prod.img`
   - `chip-prod` → `chip_prod.img`
   - `updater` → `updater.img`
   - `eng_system` → `eng_system.img`
   - `chip_ckm` → `chip_ckm.img`
   - `userdata` → `userdata.img`
5. Check **all** rows.
6. Click **Run**. Wait 5-10 min.
7. After "Download image OK" the board should reboot. First boot may take ~60 s.

### Step C — if first flash still bricks: try the newer system.img

If the V7-images flash still produces the same brick symptom, repeat Step B but in the `system` row swap `system.img` for `newer-system-apr07\system.img`. This is the Apr 7 rebuild from HBC's `oh/out/rk3568/packages/phone/images/`. All other 14 files remain the V7-images Apr 4 versions.

### Step D — verify before deploying anything

After successful flash + boot:

```
hdc shell uname -a               # should boot, prompt should return
hdc shell cat /proc/version
hdc shell mount | grep system    # confirm /system mount intent (RO or RW)
hdc shell ls /system/lib/        # sanity-check fs
```

If `hdc shell` is silent or hangs, **STOP** and report — do not push any deltas; this is the brick signature we've been hitting.

---

## 5. Comparison to what operator likely flashed before

Per project memory + agent 82 (`f3e76173`), prior flash material was also pulled from `$HOME/D200/V7-images/`. **HBC's V7-images has not been re-snapshotted since Apr 4** (verified by mtime + `find $HOME -name "V7*"`, re-confirmed by agent 110 2026-05-21). So byte-for-byte this is the same ROM the operator flashed before — assuming the prior copy was complete and not corrupted in transit.

If the brick reproduces with this re-pulled snapshot, version drift between flashed ROM and HBC's running ROM is **not** the root cause; suspect instead:

- post-flash deltas HBC pushes over hdc (their Apr 7+ system rebuild, adapter framework, etc.) — operator's board has the bare V7-images, HBC's board has V7-images + many days of incremental pushes;
- chcon/SELinux state on /system that HBC's board accumulated over weeks;
- bootloader/uboot init that depends on hardware tweaks HBC applied at the factory.

Recommend: after this re-flash, follow up with agent to **pull HBC's running /system delta** (the difference between V7-images/system.img mount and HBC's actual /system at flash-time), then push that delta after reboot before doing anything else.

---

## Appendix — Pull commands used

Agent 106's original pull (rsync, partial-resume capable):

```bash
SSHPASS=/tmp/sshpass-install/bin/sshpass
"$SSHPASS" -p '***REMOVED***' rsync -av --partial --inplace --no-compress \
  -e "ssh -p [REDACTED-PORT]" \
  [REDACTED]@[REDACTED-IP]:$HOME/D200/V7-images/ \
  $HOME/android-to-openharmony-migration/hbc-v7-images/

"$SSHPASS" -p '***REMOVED***' rsync -av --partial --inplace --no-compress \
  -e "ssh -p [REDACTED-PORT]" \
  [REDACTED]@[REDACTED-IP]:$HOME/oh/out/rk3568/packages/phone/images/system.img \
  $HOME/android-to-openharmony-migration/hbc-v7-images/newer-system-apr07/system.img
```

Agent 110's resume pull (scp, no resume — partials were too short to trust):

```bash
SSHPASS=/tmp/sshpass-install/bin/sshpass

# 1. Delete truncated files first
rm -f $HOME/android-to-openharmony-migration/hbc-v7-images/system.img
rm -rf $HOME/android-to-openharmony-migration/hbc-v7-images/newer-system-apr07/
mkdir $HOME/android-to-openharmony-migration/hbc-v7-images/newer-system-apr07

# 2. Pull missing / truncated files
for f in uboot.img system.img userdata.img vendor.img; do
  "$SSHPASS" -p '***REMOVED***' scp -P [REDACTED-PORT] \
    [REDACTED]@[REDACTED-IP]:$HOME/D200/V7-images/$f \
    $HOME/android-to-openharmony-migration/hbc-v7-images/$f
done

"$SSHPASS" -p '***REMOVED***' scp -P [REDACTED-PORT] \
  [REDACTED]@[REDACTED-IP]:$HOME/oh/out/rk3568/packages/phone/images/system.img \
  $HOME/android-to-openharmony-migration/hbc-v7-images/newer-system-apr07/system.img

# 3. Verify
cd $HOME/android-to-openharmony-migration/hbc-v7-images && md5sum -c MD5SUMS.txt
```

Integrity check (md5 ground truth from HBC's box stored in `hbc-v7-images/MD5SUMS.txt`).
