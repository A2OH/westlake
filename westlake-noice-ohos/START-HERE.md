# START HERE — reproduce noice on OpenHarmony

Everything is in this repo (**A2OH/westlake**). Two parts: the **recipe** (this
git tree) and the **binary baseline** (release assets, too large for git).

## 1. Get the recipe
```
git clone https://github.com/A2OH/westlake.git
cd westlake/westlake-noice-ohos
# read in order: README.md → STATUS.md → MANIFEST.md → REPRODUCE-CLEAN-WSL.md
```

## 2. Get the binaries
Release **`baseline-300581d1-20260608`**:
https://github.com/A2OH/westlake/releases/tag/baseline-300581d1-20260608

- `ohos-dayu200-flash-extras.tar` (496 MB) + `ohos-base-system.img.gz` (606 MB) — the device flash set
- `westlake-complete-bundle-20260608.tar.gz` (247 MB) — the full adapter + current fixes + device tools + APK (ships its own `README-COMPLETE.md` + `MANIFEST.md5`)
- `westlake-baseline-…tar.gz` is only a layer-3 subset of the complete bundle — skip it.

Verify any download with the `MANIFEST.md5` inside each bundle (`md5sum -c`).

## 3. Flash the DAYU200 / RK3568
RKDevTool (Windows) or `upgrade_tool` (Linux):
```
tar xf ohos-dayu200-flash-extras.tar     # MiniLoaderAll.bin, parameter.txt, config.cfg, uboot/boot_linux/ramdisk/vendor/...
gunzip ohos-base-system.img.gz           # → system.img (put it in the same folder)
# flash that folder using parameter.txt + config.cfg.
# userdata is wiped/blank on flash — intentionally not provided.
```

## 4. Deploy the adapter + current fixes
From `westlake-complete-bundle`:
- Follow **`overlay/scripts/DEPLOY_SOP.md`** (authoritative). In short: push
  `overlay/` to the device system paths, then apply `current-fixes/` on top.
- **CRITICAL:** apply `current-fixes/lib/libart.so` **together with**
  `current-fixes/framework-arm/boot-*`. libart and the boot image are a dex2oat
  **pair** — mixing generations → `LinkageError`.

## 5. Bring up
```
# push device-tmp/* to /data/local/tmp, then on device:
setenforce 0
setsid /system/bin/sh /data/local/tmp/start_asx.sh >/dev/null 2>&1 &
chcon u:object_r:appspawn_socket:s0 /dev/unix/socket/AppSpawnX   # after the socket appears
# wait for "Phase 4" in /data/local/tmp/asx_run.err, then:
sh /data/local/tmp/launch_noice.sh        # expect: LAUNCH_OK pid=... drew=1
apk_install /data/local/tmp/noice-base.apk
```

## Heads-up (these are expected, not bugs)
- **~50% of boots are "bad"** (empty sound library, or a busy main-thread spin
  where input doesn't register). **Reboot + relaunch** until good — a good boot
  shows a populated library (~68 KB screenshot) and a near-idle main thread.
- `liboh_android_runtime.so` is an **un-rebuildable blob** — use the provided one,
  don't try to rebuild it.
- Rebuilding *source* components (bridge / shims / jars) needs a full ~100 GB
  OHOS source tree — see `REPRODUCE-CLEAN-WSL.md`. To just *run* it, the provided
  binaries are enough.
- Share/scroll fixes: the live device is one rebuildable delta ahead of the
  committed jars (clamp `efd3f740`, scroll/pasteboard bridge) — see
  `SESSION-2026-06-08.md` + `share-fix/`. Not needed for a basic bring-up.

## Also runs: Material Components Catalog (`io.material.catalog`)
The same baseline also runs the **Material Components Catalog**, including its
**2nd-level demo Activities** (which previously crashed the whole process). It needs
two deltas on top of the noice baseline — see **`catalog-fix/REPRODUCE-CATALOG.md`**:
1. metaData NPE fix — `adapter-runtime-bcp.jar.6e32a253` (+ boot regen).
2. 2nd-level `createHardwareBitmap` SIGBUS fix — `libhwui.so.0c82b1db` +
   `liboh_adapter_bridge.so.20ab65a6` (no boot regen).

Both are committed in `catalog-fix/` (prebuilt + source). Validated 2026-06-23:
`AdaptiveListViewDemoActivity` renders, deeper nav works, SIGBUS=0.
