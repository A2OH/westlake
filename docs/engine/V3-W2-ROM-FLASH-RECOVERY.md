# V3-W2 — DAYU200 ROM-Flash Recovery Procedure (physical reflash)

**Status:** AUTHORITATIVE for the physical-reflash path that the software-only
recovery in [`V3-W2-RECOVERY-PROCEDURE.md`](V3-W2-RECOVERY-PROCEDURE.md)
explicitly declares "outside this agent's reach" (line 188-189).
**Date:** 2026-05-18.
**Authoring agent:** 54 (board-independent; operator executes on Windows host).
**Scope:** soft-bricked DAYU200 / Yue-D200 (rk3568) after W2 deploy →
factory-baseline OH V7 ROM via Rockchip MASKROM + RKDevTool flow.

**Companion docs:**
- [`V3-W2-POSTMORTEM.md`](V3-W2-POSTMORTEM.md) — hypothesis ranking; this doc
  is the §5 R5 "physical recovery" branch (V3-W2-POSTMORTEM.md line 519-522)
  promoted to a full procedure.
- [`V3-W2-RECOVERY-PROCEDURE.md`](V3-W2-RECOVERY-PROCEDURE.md) — Phase A
  software-only recovery (line 188-189 hands off to this doc when hdc does
  not return within 60 s of a hard power-cycle).
- [`V3-W2-BOOT-HBC-RUNTIME-REPORT.md`](V3-W2-BOOT-HBC-RUNTIME-REPORT.md)
  line 56 — same handoff ("physical recovery via DAYU200 USB flash").

**Source of truth for flash steps:** `Yue-D200-RomFlash-Guide.html`
(operator-side, at `C:\Users\dspfa\Downloads\Yue-D200-RomFlash-Guide.html`,
delivered 2026-05-09 by GZ02 ROM provisioning). All cited line numbers in
this doc refer to that HTML file unless noted otherwise. Section §3 of the
HTML (lines 98-218) is the canonical flow this doc tailors to W2.

---

## 0. When to use this procedure

Trigger condition (any of):

1. After a hard power-cycle (barrel + USB out 10 s, replug), `hdc list targets`
   returns `[Empty]` for ≥ 60 s. (V3-W2-RECOVERY-PROCEDURE.md Phase A step 2.)
2. `hdc list targets` shows the serial, but `hdc shell "echo alive"` returns
   empty stdout and a second hard power-cycle does not fix it.
3. Board boots to OpenHarmony logo but never advances; persistent through
   2 power-cycles.
4. USB never enumerates at the host (Windows Device Manager shows nothing
   new on insert) — the W2 brick signature.

If NONE of the above match, prefer the software-only path in
[`V3-W2-RECOVERY-PROCEDURE.md`](V3-W2-RECOVERY-PROCEDURE.md) §R0-R4. A ROM
flash wipes the device-side `.orig_20260516` backups (acceptable; see §6
below) and all `/data` user state, so it is strictly heavier.

---

## 1. What this flash gives us (and what it costs)

| Aspect | After ROM flash |
|---|---|
| OH ROM | OpenHarmony V7.0.0.18 (DAYU200 rk3568 factory build, dated 2026-05-09) |
| `/system/android` | **absent** — confirms "factory baseline" per HBC SOP line 21 (W2-RECOVERY §1 line 29-30) |
| `/system/lib/*.orig_20260516` | **WIPED** — were W2-era precautionary backups; loss is acceptable because local snapshot lives at `westlake-deploy-ohos/v3-hbc/` and the post-flash `/system/lib/` IS by definition the factory state (which is what those backups captured) |
| `/data` (userdata partition) | **WIPED** — fresh `userdata.img` flashed (1400 MB blank, see HTML line 200) |
| All V2-era state on device | **WIPED** — acceptable per V3 strategic commitment (memory: `project_v2_ohos_direction.md`); V2 superseded by V3 |
| `appspawn-x`, `appspawn_x.cfg`, `appspawn_x_sandbox.json` | **WIPED** — exactly the artifacts §R3 of V3-W2-POSTMORTEM.md sought to remove |
| Wall-clock cost | ~3-5 min flash + ~1 min reboot + ~5 min verify = ~10 min total (HTML line 212) |
| Risk if flash fails | Re-runnable; bootloader/recovery partitions are intact per V3-W2-BOOT-HBC-RUNTIME-REPORT.md line 58 |

After flash: clean factory baseline → ready for hardened V3 re-deploy via
`scripts/v3/deploy-hbc-to-dayu200.sh` (once §4 G1+G2 of V3-W2-POSTMORTEM.md
land into the script). Do **not** re-deploy until those gates are in.

---

## 2. Pre-flash checklist (operator-side, on the Windows host)

Run through ALL items before powering down DAYU200. Each item has an
expected visible signal; if any item fails, stop and resolve before
touching the board.

### 2.1 Host machine

- [ ] Windows host available (the same one used for `hdc.exe` typically;
      under WSL2 the flash tool must run on the Windows side, NOT in WSL,
      because RKDevTool is a Windows GUI app — HTML line 100-102).
- [ ] At least **5 GB free disk** on `D:\` (or wherever the staging dir
      will live). The flash images alone are 3.9 GB (HTML line 73).
- [ ] Administrator rights on the host (the driver installer requires it —
      HTML line 119 "管理员身份" / "run as admin").

### 2.2 ROM image + tools present

Either confirm the operator already has them from a previous flash, or
pull fresh from GZ02. The HTML §2 (lines 63-91) defines the four
authoritative directories:

| Local path (after pull) | Source on GZ02 | Size | Purpose |
|---|---|---|---|
| `D:\rk3568\images\` | `/data/share/rk3568/images/` | 3.9 GB | 15 .img files + `parameter.txt` + `config.cfg` + `MiniLoaderAll.bin` |
| `D:\rk3568\RKDevTool_v3.30_for_window\` | `/data/share/rk3568/RKDevTool_v3.30_for_window/` | 7.1 MB | `RKDevTool.exe` (Rockchip flash tool, **version v3.30**) + 开发工具使用文档_v1.0.pdf |
| `D:\rk3568\DriverAssitant_v5.1.1\` | `/data/share/rk3568/DriverAssitant_v5.1.1/` | 12 MB | Windows USB driver installer (**version v5.1.1**) |

Pull commands (HTML lines 109-112, run on the Windows host in PowerShell
or Git Bash):

```powershell
scp -P 58222 -r yue.chen@1.95.90.207:/data/share/rk3568/images           D:\rk3568\
scp -P 58222 -r yue.chen@1.95.90.207:/data/share/rk3568/RKDevTool_v3.30_for_window  D:\rk3568\
scp -P 58222 -r yue.chen@1.95.90.207:/data/share/rk3568/DriverAssitant_v5.1.1       D:\rk3568\
```

Credentials: user `yue.chen`, password `WeskLakeV3.`, port `58222`
(HTML lines 36-45). After pull, verify:

- [ ] `D:\rk3568\images\MiniLoaderAll.bin` exists, **~444 KB** (HTML line 186
      and line 230 — the exact size check the HTML uses as a corruption
      tell).
- [ ] `D:\rk3568\images\config.cfg` exists (this is the partition map
      RKDevTool will import — HTML line 160).
- [ ] `D:\rk3568\images\parameter.txt` exists (the rk3568 partition table —
      HTML line 187).
- [ ] `D:\rk3568\images\system.img` exists, **~2048 MB** (HTML line 194).
- [ ] `D:\rk3568\RKDevTool_v3.30_for_window\RKDevTool.exe` exists.
- [ ] `D:\rk3568\DriverAssitant_v5.1.1\DriverInstall.exe` exists.

### 2.3 Driver installed

If a previous flash session already installed v5.1.1, skip; otherwise
(HTML lines 116-122):

- [ ] Right-click `DriverInstall.exe` → **Run as administrator**.
- [ ] If a prior Rockchip driver version is installed, click
      **驱动卸载** ("uninstall driver") FIRST, then close and re-open the
      tool. (HTML line 122 — version-skew is a known cause of "device not
      found" later.)
- [ ] Click **驱动安装** ("install driver").
- [ ] Expect popup **"安装驱动成功"** ("driver install succeeded").
- [ ] Verify in Windows Device Manager: with no DAYU200 connected, no
      Rockusb entries; this is the baseline.

### 2.4 Cable + power

- [ ] USB-C data cable on hand (NOT a charge-only cable — HTML line 134
      "纯充电线不行" / "pure-charging cable does not work"). If unsure,
      test the same cable first on a known-good Rockchip device or use a
      cable already known to carry data to DAYU200 in the W2 deploy phase
      (the one `hdc file send` previously used).
- [ ] DAYU200 power supply (barrel jack adapter) plugged in but board
      currently powered off.
- [ ] Direct USB port on the host motherboard (rear I/O preferred over
      a hub — HTML line 226 "直插主板后置 USB,避开 hub").

### 2.5 RKDevTool launch

- [ ] Double-click `D:\rk3568\RKDevTool_v3.30_for_window\RKDevTool.exe`
      (HTML line 127). It opens to the **下载镜像** ("Download image")
      tab by default.
- [ ] Confirm version v3.30 in the title bar.
- [ ] Top status line reads **"没有发现设备"** ("no device found") — this
      is the baseline before the board enters Loader/MASKROM.

---

## 3. Flash procedure

This follows HTML §3.2-3.3 (lines 130-218) tailored to the W2 brick
context. The HTML's Method A (断电法 / "power-down method", lines 138-145)
is the path to use because the board is bricked and not adb-reachable for
Method B's `reboot loader` command.

### Step 1 — Enter MASKROM / Loader mode (HTML method A, line 138)

> **Yue-D200-specific key combo per HTML line 142:** hold **VOL+** (volume-up
> button on the side of the DAYU200 enclosure).
>
> **HTML gap (NOT documented):** the exact physical location of the VOL+
> button on the Yue-D200 housing is not pictured in the HTML guide. On a
> standard HiHope DAYU200 development board the volume buttons are on the
> long edge of the metal-cased SBC; VOL+ is the one closer to the power
> jack (operator-confirm against the unit). **OPERATOR-INVESTIGATE** if
> in doubt; refer to HiHope DAYU200 board photos at the Gitee link in
> HTML line 266 (https://gitee.com/hihope_iot/docs/tree/master/HiHope_DAYU200/).

Operator steps (exact HTML §5 step 5 method A, lines 139-145):

1. Board fully powered down: unplug **both** the barrel power and the
   USB-C cable. Wait 10 sec for any residual capacitance to drain.
2. Press and hold **VOL+** on the DAYU200. Keep holding.
3. With VOL+ still held, plug the USB-C cable into the host. (Do NOT
   plug in the barrel power yet — the USB-C provides enough current to
   enter MASKROM/Loader.)
4. Watch the RKDevTool top status line. Within ~3 sec it should change
   from "没有发现设备" to one of:
   - **"发现一个 LOADER 设备"** ("found one LOADER device") — green-bordered
     in the HTML class `.ok` (line 144). This is the ideal state; go to
     Step 2.
   - **"发现一个 MASKROM 设备"** ("found one MASKROM device") — HTML line 152
     covers this case. Proceed to Step 1b before Step 2.
5. Release VOL+.

#### Step 1b — If MASKROM (not LOADER) was detected (HTML line 152, line 234)

MASKROM means the SoC's on-die boot ROM is talking directly (no eMMC
loader); this happens on a fully-wiped device or one where uboot was
clobbered. The fix is to push `MiniLoaderAll.bin` first so the board
transitions into Loader mode (which then accepts the full image set):

1. In RKDevTool, click the bottom-left **"执行"** ("Execute") row.
2. Click **"下载 Loader"** ("Download Loader").
3. The tool reads the file path from `config.cfg`'s `Loader=` entry
   (which points at `MiniLoaderAll.bin` — HTML line 186); ensure that
   path is correct in the import done in Step 3 below, OR pre-import
   `config.cfg` first via Step 3 then return here.
4. Wait for "下载 Loader 完成" ("Download Loader complete"). The board
   re-enumerates and the status line flips to **"发现一个 LOADER 设备"**.

#### Step 1c — If neither LOADER nor MASKROM appears (HTML §4 row 1, line 224-227)

Try in order:

1. **Wrong USB port:** unplug, plug into a different rear-I/O USB port
   on the host motherboard. Hubs and front-panel ports often miss the
   transient enumeration (HTML line 226).
2. **Cable:** swap to a different USB-C data cable. Charge-only cables
   silently fail enumeration (HTML line 134, 227).
3. **Driver:** re-run §2.3 driver install (uninstall first if a prior
   install exists). Verify in Device Manager → "通用串行总线设备" /
   "Universal Serial Bus devices" — a **"Rockusb Driver"** entry should
   appear briefly when MASKROM is held (HTML line 225).
4. **Key combo (alternative — HTML GAP):** the HTML only documents VOL+.
   For HiHope DAYU200 boards, common alternative combos seen in the
   Rockchip ecosystem are: hold **RECOVERY** button (the small unlabeled
   tactile switch near the CPU heatsink), or **RECOVERY+RESET** chord.
   **ASSUMED** — not in the HTML; operator may consult the HiHope DAYU200
   Gitee README (HTML line 266) if VOL+ alone does not produce
   MASKROM/Loader. Do not flash via an unverified combo — the symptom you
   want is RKDevTool's status change, not Windows USB tone alone.

### Step 2 — Import partition config (HTML §6, line 158-162)

1. In RKDevTool, **right-click** anywhere in the main partition list
   area.
2. Select **"导入配置"** ("Import config").
3. Navigate to and select `D:\rk3568\images\config.cfg`.
4. The partition list populates with **17 rows**. Each row's path column
   should auto-fill to the corresponding `D:\rk3568\images\*.img` file.

### Step 3 — Disable the 2 engineering-build rows (HTML §7, lines 164-181 — DANGER)

This is the W2-context-critical step. The factory `config.cfg` ships
with two engineering-debug partitions that **must be unchecked** before
flashing:

| Row to uncheck | Reason (HTML line 169-176) |
|---|---|
| `eng_system` | Engineering-debug system, not for production flash; `config.cfg` mis-maps its path to `updater.img`, so flashing it would overwrite system with the wrong content. |
| `eng_chipset` | Engineering-debug chipset, not for production flash; `config.cfg` mis-maps to `updater.img` AND `D:\rk3568\images\` does not contain the corresponding .img anyway. |

Operator steps:

1. Scroll the 17-row list, find the `eng_system` row, click the leading
   checkbox to **uncheck** it.
2. Same for `eng_chipset`.
3. Visually re-verify: only **15 of 17 rows** are checked.

### Step 4 — Verify the 15 remaining rows (HTML §8, lines 183-202)

The 15 checked rows must match the table at HTML lines 186-200. Spot-check:

- `Loader` → `MiniLoaderAll.bin` (444 KB)
- `Parameter` → `parameter.txt` (788 B)
- `Uboot` → `uboot.img` (4.0 MB)
- `Boot_linux` → `boot_linux.img` (64 MB)
- `System` → `system.img` (2048 MB) — **the big one**
- `Vendor` → `vendor.img` (256 MB)
- `Userdata` → `userdata.img` (1400 MB)
- `misc` and `bootctrl` show `uboot.img` as the path — this is correct
  per HTML line 201 note; do NOT uncheck them. ("misc/bootctrl are boot
  control structures; the flash tool handles them by address.")

### Step 5 — Execute flash (HTML §9, lines 204-213)

1. Re-confirm `eng_system` and `eng_chipset` are **unchecked**.
2. On the right side of RKDevTool, **check** the **"强制按地址写"**
   ("Force write by address") option (HTML line 208).
3. Click the **"执行"** ("Execute") button.
4. The right-side log pane prints, in order: `下载 boot` (download boot)
   → `下载 system` (download system) → ... → `下载完成` (download
   complete). Total runtime: **3-5 min** (HTML line 212).

### Step 6 — Auto-reboot + first-boot verify (HTML §10, lines 215-218)

1. After "下载完成", the board auto-reboots.
2. Wait for the **OpenHarmony logo** on the DAYU200 display. If the
   board has no attached display, skip to §4 software-side gates.
3. If the screen stays black for > 60 sec, hard-power-cycle (unplug
   barrel + USB, wait 30 sec, replug). If still black after a second
   power-cycle, re-flash from Step 1 (HTML line 217, 238).

---

## 4. Post-flash verification gates

Operator runs these from the same Windows host once the board has
booted. All gates must pass; any FAIL means the flash did not produce
a usable factory baseline and Step 1 should be re-tried.

```powershell
# G1 — hdc enumeration (cures the W2 brick signature by definition).
hdc list targets
# EXPECT: at least one line with a non-empty serial.  NOT "[Empty]".
```

```powershell
# G2 — hdc shell stdout channel alive (the W2 silent-shell sentinel).
hdc shell "echo HDC_SHELL_PROBE_v3"
# EXPECT literal string: HDC_SHELL_PROBE_v3
# (matches V3-W2-POSTMORTEM.md §4 G1 probe, line 303).
```

```powershell
# G3 — factory baseline marker (HBC SOP "factory" definition).
hdc shell "ls -d /system/android 2>&1"
# EXPECT: ls: ... No such file or directory
# (Defining signal per V3-W2-RECOVERY-PROCEDURE.md line 28-32.)
```

```powershell
# G4 — bitness (CR60 confirmed DAYU200 userspace is 32-bit).
hdc shell "getconf LONG_BIT"
# EXPECT: 32
# (Per project memory feedback_bitness_as_parameter.md;
#  64-bit here would indicate a wrong-ROM mis-flash.)
```

```powershell
# G5 — kernel still 64-bit (the board's userspace-32 / kernel-64
#       split is normal; CR60 + project_ohos_mvp_pipeline.md).
hdc shell "uname -a"
# EXPECT: Linux ... aarch64
```

```powershell
# G6 — SELinux enforcing (not permissive — would indicate a
#       development ROM mis-flash).
hdc shell "getenforce"
# EXPECT: Enforcing
```

```powershell
# G7 — V3-era residue absent (must be true — flash wiped /system).
hdc shell "ls /system/lib/*.orig_* 2>/dev/null"
# EXPECT: empty output.
# (Mirrors V3-W2-RECOVERY-PROCEDURE.md line 201.)
hdc shell "ls /system/bin/appspawn-x 2>&1"
# EXPECT: ... No such file or directory
hdc shell "ls /system/etc/init/appspawn_x.cfg 2>&1"
# EXPECT: ... No such file or directory
```

```powershell
# G8 — factory daemons running (proves /system is not subtly broken).
hdc shell "pidof hdcd foundation render_service appspawn"
# EXPECT: 4 non-empty PIDs (one per process, space-separated).
```

```powershell
# G9 — uptime is fresh (proves we are in fact post-flash, not
#       pre-flash leftover).
hdc shell "cat /proc/uptime"
# EXPECT: first number < 600 (less than 10 minutes since boot).
```

```powershell
# G10 — boot image untruncated (HBC SOP §0 truncation guard).
hdc shell "stat -c '%s' /system/framework/arm/boot-framework.art"
# EXPECT: a value > 20000000 (> ~20 MB; NOT 9 MB — the truncation
# symptom per V3-W2-BOOT-HBC-RUNTIME-REPORT.md line 13).
```

If G1-G10 all pass: **factory baseline confirmed**, board is ready for
V3 re-deploy after §4 G1+G2 of V3-W2-POSTMORTEM.md land into
`scripts/v3/deploy-hbc-to-dayu200.sh`.

---

## 5. Post-recovery expected state (factory ROM)

| Path | State |
|---|---|
| `/system/android` | **absent** (factory marker) |
| `/system/lib/libwms.z.so` | factory copy (was W2-overwritten with HBC adapter; now restored to factory) |
| `/system/lib/libappms.z.so` | factory copy |
| `/system/lib/libbms.z.so` | factory copy |
| `/system/lib/libskia_canvaskit.z.so` | factory copy |
| `/system/lib/libinstalls.z.so` | factory copy |
| `/system/lib/libappspawn_client.z.so` | factory copy |
| `/system/lib/platformsdk/libabilityms.z.so` | factory copy |
| `/system/lib/platformsdk/libscene_session.z.so` | factory copy |
| `/system/lib/platformsdk/libscene_session_manager.z.so` | factory copy |
| `/system/lib/platformsdk/librender_service_base.z.so` | factory copy |
| `/system/lib/platformsdk/libappexecfwk_common.z.so` | factory copy |
| `/system/etc/ld-musl-namespace-arm.ini` | factory copy |
| `/system/etc/selinux/targeted/contexts/file_contexts` | factory copy |
| `/system/etc/init/appspawn.cfg` | factory copy (the W2 attempt added `appspawn_x.cfg` alongside; ROM flash removes the .x.cfg) |
| `/system/bin/appspawn-x` | **absent** |
| `/system/etc/appspawn_x_sandbox.json` | **absent** |
| `/data/*` | empty (1400 MB blank userdata.img) |
| OpenHarmony version | V7.0.0.18 |

All 13 device-side `.orig_20260516` backup files from W2 Stage 1 are
WIPED. This is fine — they were factory copies, and what we now have
IS factory.

The local W2 snapshot at `westlake-deploy-ohos/v3-hbc/` is the
canonical "what we plan to push" reference; it is NOT a recovery
artifact and is unaffected by the ROM flash.

---

## 6. W2 brick context — what was lost and why that's OK

| Item lost | Was it valuable? | Recovery story |
|---|---|---|
| 13 `.orig_20260516` device-side backups | No — they captured the factory state, which the flash restores by definition | Factory IS the backup |
| W2-deployed `/system/android/` tree (94 files + 4 symlinks) | No — was the attempt that bricked the board | Local snapshot lives at `westlake-deploy-ohos/v3-hbc/`; can be redeployed once SOP gates land |
| `/data/` user state from prior V2-OHOS demos (M5 audio, M6 surface) | No — V2-OHOS superseded by V3 per `project_v2_ohos_direction.md` memory | Demos can be re-run on V3 substrate |
| Any V2-era binaries (dalvikvm-arm32, libohbridge.so, etc.) pushed to /data/local/tmp | No — V2 path archived (`archive/v2-ohos-substrate/`) | Archive in repo |
| `appspawn-x` + `appspawn_x.cfg` | No — these are exactly what V3-W2-POSTMORTEM.md §R3 (line 491-498) wanted removed | ROM flash IS the §R3 superset |

**Net W2 status after this flash:** board returns to the *exact*
pre-W2-deploy condition. The W2 attempt itself remains documented in
`V3-W2-BOOT-HBC-RUNTIME-REPORT.md` and `V3-W2-POSTMORTEM.md` for
posterity; the re-attempt plan is in `V3-W2-RECOVERY-PROCEDURE.md`
Phase B-D (lines 192-238), with the script hardenings tracked in
`V3-W2-POSTMORTEM.md` §4 (lines 293-416).

**Do not re-deploy until:**
1. `scripts/v3/deploy-hbc-to-dayu200.sh` has the §4 G1 channel-health
   probe between every Stage (V3-W2-POSTMORTEM.md lines 299-315).
2. `scripts/v3/deploy-hbc-to-dayu200.sh` has the §4 G2 chcon
   `set -e` semantics (V3-W2-POSTMORTEM.md lines 317-329).
3. The two missing `.so` files (`libinstalls.z.so`,
   `libappexecfwk_common.z.so`) are confirmed present in
   `westlake-deploy-ohos/v3-hbc/lib/` — already done per
   `V3-W2-RECOVERY-PROCEDURE.md` line 212-213.

---

## 7. Operator checklist (linear, top-to-bottom)

Print or keep open in a second window. Each line has a checkbox; do not
proceed until the previous box is ticked.

### Pre-recovery (before powering down the board)

- [ ] **PRE-1** Windows host available, admin rights confirmed
- [ ] **PRE-2** `D:\rk3568\images\` exists with `MiniLoaderAll.bin`
      (~444 KB), `config.cfg`, `parameter.txt`, `system.img` (~2048 MB)
- [ ] **PRE-3** `D:\rk3568\RKDevTool_v3.30_for_window\RKDevTool.exe`
      exists
- [ ] **PRE-4** `D:\rk3568\DriverAssitant_v5.1.1\DriverInstall.exe`
      exists
- [ ] **PRE-5** Rockchip USB driver v5.1.1 installed (or freshly
      re-installed if any prior version was present)
- [ ] **PRE-6** USB-C **data** cable on hand (NOT charge-only)
- [ ] **PRE-7** DAYU200 barrel power supply on hand
- [ ] **PRE-8** Direct rear-I/O USB port on host identified (no hubs)
- [ ] **PRE-9** RKDevTool.exe launched, version v3.30 confirmed,
      status line reads "没有发现设备"
- [ ] **PRE-10** This document open in a second window for reference

### During recovery (the flash itself)

- [ ] **D-1** Board fully powered down (barrel + USB unplugged 10 sec)
- [ ] **D-2** VOL+ button held down
- [ ] **D-3** USB-C cable plugged in WITH VOL+ still held
- [ ] **D-4** RKDevTool top status shows "发现一个 LOADER 设备"
      (green text). If it says "MASKROM" instead, do Step 1b first.
- [ ] **D-5** VOL+ released
- [ ] **D-6** Right-click in partition area → "导入配置" →
      selected `D:\rk3568\images\config.cfg`
- [ ] **D-7** 17 rows populated in the partition list
- [ ] **D-8** `eng_system` row → checkbox UNCHECKED
- [ ] **D-9** `eng_chipset` row → checkbox UNCHECKED
- [ ] **D-10** Visual count: exactly 15 of 17 rows checked
- [ ] **D-11** Spot-checked `Loader` row = `MiniLoaderAll.bin`,
      `System` row = `system.img`, `Userdata` row = `userdata.img`
- [ ] **D-12** Right-side "强制按地址写" option CHECKED
- [ ] **D-13** Clicked "执行"; log pane shows "下载 boot",
      "下载 system", etc., progressing
- [ ] **D-14** Log pane shows final "下载完成" (within ~5 min)
- [ ] **D-15** Board auto-reboots; OpenHarmony logo visible
      (if display attached) within 60 sec

### Post-recovery (verification gates, see §4)

- [ ] **POST-G1** `hdc list targets` shows a non-empty serial
- [ ] **POST-G2** `hdc shell "echo HDC_SHELL_PROBE_v3"` returns the
      literal string
- [ ] **POST-G3** `hdc shell "ls -d /system/android 2>&1"` says
      "No such file or directory"
- [ ] **POST-G4** `hdc shell "getconf LONG_BIT"` returns `32`
- [ ] **POST-G5** `hdc shell "uname -a"` shows `aarch64`
- [ ] **POST-G6** `hdc shell "getenforce"` returns `Enforcing`
- [ ] **POST-G7** No `*.orig_*` files in `/system/lib/`, no
      `/system/bin/appspawn-x`, no `/system/etc/init/appspawn_x.cfg`
- [ ] **POST-G8** `hdc shell "pidof hdcd foundation render_service
      appspawn"` returns 4 non-empty PIDs
- [ ] **POST-G9** `hdc shell "cat /proc/uptime"` first number < 600
- [ ] **POST-G10** `hdc shell "stat -c '%s'
      /system/framework/arm/boot-framework.art"` > 20000000
- [ ] **POST-DONE** All POST-G1..G10 PASS → factory baseline confirmed,
      board ready for V3 re-deploy after SOP hardenings land

---

## 8. Source citation index

| Step / Claim | HTML source (line range) |
|---|---|
| `RKDevTool.exe` is the flash tool, version v3.30 | §2.1 row 3 (line 82-85) |
| `DriverAssitant` is the Windows USB driver, version v5.1.1 | §2.1 row 4 (line 86-90) |
| Driver-install flow (run-as-admin, click 驱动安装) | §3.1 step 2 (line 116-122) |
| Pull images from GZ02 via `scp -P 58222` | §3.1 step 1 (line 107-112) |
| MASKROM key combo = VOL+ held while plugging USB-C | §3.2 step 5 method A (line 138-145) |
| MASKROM-vs-LOADER recovery via "下载 Loader" | §3.2 note (line 152) + §4 row 3 (line 233-234) |
| Import `config.cfg` via right-click → "导入配置" | §3.3 step 6 (line 158-162) |
| MUST uncheck `eng_system` + `eng_chipset` | §3.3 step 7 DANGER box (line 164-181) |
| 15-row partition manifest | §3.3 step 8 table (line 184-200) |
| Force-write-by-address option + "执行" | §3.3 step 9 (line 204-213) |
| Auto-reboot expectation | §3.3 step 10 (line 215-218) |
| `MiniLoaderAll.bin` corruption check at 444 KB | §4 row 2 (line 229-231) |
| USB / cable troubleshooting (direct port, no hub, data cable) | §4 row 1 (line 224-227) |
| HiHope DAYU200 official docs link | §6 (line 266) |
| Position of VOL+ button on Yue-D200 housing | **NOT in HTML — operator-investigate** |
| Alternative MASKROM key combos | **NOT in HTML — assumed RECOVERY button per Rockchip ecosystem norms** |
| Whether Huawei publishes a public factory ROM URL | **NOT in HTML — only the GZ02 internal path is documented (`/data/share/rk3568/images/`); no public Huawei URL is given** |

---

## 9. Open items / HTML gaps surfaced

1. **VOL+ button physical location.** Not pictured in the HTML.
   Operator-confirm against the unit; fallback reference is the
   HiHope DAYU200 Gitee README (HTML line 266).
2. **Alternative key combos** if VOL+ alone fails to produce
   MASKROM/Loader. The HTML only documents VOL+. Assumed: RECOVERY
   button or RECOVERY+RESET chord per Rockchip ecosystem norms.
3. **Public factory ROM URL.** The HTML routes only via GZ02
   (`/data/share/rk3568/images/`). No Huawei or HiHope public URL is
   given. If GZ02 becomes unavailable, the OpenHarmony CI daily build
   (HTML line 265: http://ci.openharmony.cn/workbench/cicd/dailybuild/dailylist)
   is the documented fallback for fetching a rk3568 nightly, but the
   GZ02 image is the specific build the W2 attempt was sized against
   and is the only one with a known-good `parameter.txt` partition map.
4. **Driver compatibility on Windows 11.** HTML does not call out
   Windows version. v5.1.1 is known to work on Windows 10; Windows 11
   compatibility is **OPERATOR-VERIFY**. If install fails on Win11, the
   Rockchip community typically distributes a v5.x.x successor.
5. **Display dependency for the OpenHarmony logo check.** HTML step 10
   assumes a display is attached. For headless DAYU200 (W2 setup is
   headless per project memory), skip the logo check and rely on
   §4 G1-G10 hdc-side gates.

---

## 10. After this procedure succeeds

Hand back to `V3-W2-RECOVERY-PROCEDURE.md` **Phase B** (lines 192-202)
to confirm the same factory baseline from the V3 deploy script's
perspective, then continue through **Phase C** (lines 208-218) and
**Phase D** (lines 220-238) — but **only after** the
`V3-W2-POSTMORTEM.md` §4 G1/G2 deploy-script hardenings land, per the
hard-block at §6 above.

Do NOT skip the re-attempt gates. The W2 brick was caused by
attempting a deploy without channel-health verification; flashing the
board does not change the deploy script's gap.

---

## 11. Document metadata

- Authored: 2026-05-18 by agent 54.
- Time-boxed: ~2-3 hours.
- Source HTML read in full (lines 1-276).
- Local commit only; no push.
- Companion / superseded relationship: this doc COMPLEMENTS (does not
  replace) `V3-W2-RECOVERY-PROCEDURE.md`. The software path there is
  the preferred recovery; this doc is the fallback for when that path
  cannot run.
