# MANIFEST — noice-on-OHOS reproducibility artifacts

## Release assets (the binary baseline — too large for git)

https://github.com/A2OH/westlake/releases/tag/baseline-300581d1-20260608

| asset | size | md5 | what |
|---|---|---|---|
| `westlake-complete-bundle-20260608.tar.gz` | 247 MB | `14eaa14f` | **COMPLETE from-zero set**: v3-hbc consistent overlay (appspawn-x + 56 libs + jars + boot + etc + scripts) + `current-fixes/` (runtime 16e08711, libart 7b856a2d + paired boot, hwui 8b8f84ec, bridge 60126181, jars, shims) + `device-tmp/` (start_asx.sh, launch_noice.sh, bpfgrant, apk_install, noice APK) + README-COMPLETE.md + MANIFEST.md5 |
| `ohos-base-system.img.gz` | 606 MB | `fca2f09e` | OHOS DAYU200/RK3568 base system image (gunzip → flash) |
| `ohos-base-updater.img` | 20 MB | `f3d15a6b` | OHOS updater image |
| `westlake-baseline-300581d1-20260608.tar.gz` | 74 MB | `74f67dbb` | curated layer-3 fixes only (subset of the complete bundle) |

**From-zero reproduction:** flash `ohos-base-*` → deploy the complete bundle's
`overlay/` (per `overlay/scripts/DEPLOY_SOP.md`) → apply `current-fixes/` →
push `device-tmp/` to `/data/local/tmp` → `start_asx.sh` then `launch_noice.sh`.
All assembled on host; no live device was needed to produce them.


> **Pre-built baseline tarball (the collect-only / un-rebuildable half):** https://github.com/A2OH/westlake/releases/tag/baseline-300581d1-20260608 — `westlake-baseline-300581d1-20260608.tar.gz` (74MB, md5 `74f67dbb`, generation `300581d1`). Contains runtime `16e08711` (un-rebuildable), libart `7b856a2d`, libhwui `8b8f84ec`, bridge `60126181`, the paired 27-segment boot image, BCP jars, shims, tools, and the noice APK, plus its own `MANIFEST.md5` + `README-BASELINE.md`. Restore to the device paths in its README, then follow `REPRODUCE.md`. Assembled on host — no live device required.

Every artifact required to reproduce the **current deployed** "noice running on
OpenHarmony (Westlake adapter)" state. Device: OpenHarmony DAYU200 / RK3568,
32-bit ARM, app uid **13731**, appspawn-x AOSP adapter.

md5s captured from the live device on **2026-06-07** (read-only). Where a smaller
artifact is committed in this repo its in-repo path is given; large/un-rebuildable
blobs are **referenced** (pull with `collect-artifacts.sh`) and their build path is
given so they can be regenerated.

Legend for **Repro** column: **committed** = the fixed binary is in this repo ·
**collect** = pull from device with `collect-artifacts.sh` (too large / un-rebuildable)
· **build** = produced by the build command shown.

## A. Deployed device binaries (the fixes)

| Artifact | Device path | Deployed md5 | Source location (this repo) | Build / regen command | Repro |
|----------|-------------|--------------|------------------------------|------------------------|-------|
| `libart.so` (proxy iftable fix) | `/system/android/lib/libart.so` | `7b856a2d450184f500d2b4044335afec` | `runtime-proxy-fix/class_linker.cc.FIX-IFTABLE-PROXY.snippet` (+ full src in `$HOME/libart-pathA-work/src`) | `bash $HOME/libart-pathA-work/build_libart_pathA.sh` → `out/libart.so` | committed `runtime-proxy-fix/libart.so.7b856a2d` (11 MB) + build |
| `adapter-mainline-stubs.jar` (SSLSockets stub) | `/system/android/framework/adapter-mainline-stubs.jar` | `41834c1febb589cd272f26ae8e3a5ef1` | `sslsockets-fix/SSLSockets.java` (+ `.smali`) | §2.4 of REPRODUCE-CLEAN-WSL.md (javac→d8→merge into existing classes.dex `--min-api 28`→rezip) | committed `prebuilt-jars/adapter-mainline-stubs.jar.41834c1f` |
| `oh-adapter-framework.jar` (share/createChooser fix) | `/system/android/framework/oh-adapter-framework.jar` | `300581d16eadde15ebff377eb05acf32` | `share-fix/IntentWantConverter.java` | §2.5 (baksmali `IntentWantConverter.smali`→inject ACTION_CHOOSER unwrap→SmaliAssemble→rezip) | committed `prebuilt-jars/oh-adapter-framework.jar.300581d1` |
| `adapter-runtime-bcp.jar` (native-TLS `$Sf` ctor) | `/system/android/framework/adapter-runtime-bcp.jar` | `d5d39a0526fdf1a4030807f969c499d8` | `native-tls/TlsShimProvider_Sf_*.smali` | §2.6 / §5F (DexClassLoader `$Sf` splice) | committed `prebuilt-jars/adapter-runtime-bcp.jar.d5d39a05` |
| `framework.jar` (universal service-fetcher fixes) | `/system/android/framework/framework.jar` | `8524dc564c5bdb99ebb6c4577a4c7fce` | `framework-smali-patches/*.smali` | §2.7 (baksmali→patch→SmaliAssemble→rezip) | collect (15 MB) + build |
| `liboh_adapter_bridge.so` (drag/input bridge) | `/system/lib/liboh_adapter_bridge.so` **and** `/system/android/lib/liboh_adapter_bridge.so` | `82b0d82a61e9d6456338cb29bd9f2778` | `bridge-drag/oh_input_bridge.cpp` (+ `bridge-src/oh_window_manager_client.cpp`, `oh_input_bridge.h`) | §2.3 (`build_adapter.sh --target=liboh_adapter_bridge.so`) | committed `bridge-drag/liboh_adapter_bridge.so.82b0d82a` (1.3 MB) + build |
| `libhwui.so` (G3.8 + EGL fixes) | `/system/android/lib/libhwui.so` | `8b8f84ecef0d28a40d4d72c003b9a9c8` | `bridge-src/hwui_oh_abi_patch.cpp` | §2.8 (`build_aosp_lib.sh --target=libhwui.so`) | collect (un-rebuildable env, see GAP) |
| `liboh_android_runtime.so` (base, **un-rebuildable**) | `/system/android/lib/liboh_android_runtime.so` | `f82d8cdcbf8276577413197e8fec5349` | — none — | — fixed blob; do NOT rebuild — | collect (GAP: no buildable source) |

## B. Native LD_PRELOAD shims + native TLS

| Artifact | Device path | Deployed md5 | Source (this repo) | Build | Repro |
|----------|-------------|--------------|--------------------|-------|-------|
| `libsetgidhook.so` (inet gid 3003/3004) | `/system/android/lib/libsetgidhook.so` | `0c4987daddc876e39917a126573fdf8c` | **none** (GAP) | — | committed `prebuilt-native/libsetgidhook.so` (binary only) |
| `libdnshook.so` (direct-UDP DNS) | `/system/android/lib/libdnshook.so` | `30c68f91361ff555af0090bac75da31c` | **none** (GAP) | — | committed `prebuilt-native/libdnshook.so` (binary only) |
| `libjdnshook.so` (libcore DNS JNI, v2) | `/system/android/lib/libjdnshook.so` | `f35af7f62fa037e41e20c30b90548102` | `native-libs/libjdnshook_v2.c` | §2.9 (OHOS clang arm32) | committed binary + source |
| `libnetlog.so` (net trace) | `/system/android/lib/libnetlog.so` | `989405e3270aef725bc9913c456459bf` | **none** (GAP) | — | committed `prebuilt-native/libnetlog.so` (binary only) |
| `libw14supp.so` (W14 substrate) | `/system/android/lib/libw14supp.so` | `439497e1118c559be69f1bf9b0c15a6a` | **none** (GAP) | — | committed `prebuilt-native/libw14supp.so` (binary only) |
| `libv4force.so` (AF_INET6→AF_INET) | `/system/android/lib/libv4force.so` | deployed `afe84b5f…` / committed `7c3e5ece…` | `native-libs/libv4force.c` | §2.9 | committed `native-libs/libv4force.so` (md5 differs from deployed — see GAP) + source |
| `libtlsjni.so` (native OpenSSL JNI) | `/system/android/lib/libtlsjni.so` | deployed `409b24a2…` / committed `e248cc47…` | `native-tls/libtlsjni.c` | §2.9 | committed `native-tls/libtlsjni.so` (md5 differs from deployed — see GAP) + source |
| `tlsjni-extra.dex` (TlsJni/TlsJniSocket, out-of-BCP) | `/system/android/framework/tlsjni-extra.dex` | `01ade5c45da4a9c56e7cbc677123126d` | `native-tls/{TlsJni,TlsJniSocket}.java` | §5F | committed `native-tls/tlsjni-extra.dex` + source |

## C. Boot image (regenerated; BCP-jar changes need it)

| Artifact | Device path | Repro | Notes |
|----------|-------------|-------|-------|
| `boot*.{art,oat,vdex}` (30 segments) | `/system/android/framework/arm/` | build (`/tmp/tagsoup-boot/regen.sh`) or collect | Regenerated from the 10 BCP jars. **Inputs already staged** at `/tmp/tagsoup-boot/jars/` (md5s match deployed exactly). ~143 MB — not committed; regenerate (≈16 s) or collect. |

## D. Tooling, data, and the APK (device-side, /data/local/tmp)

| Artifact | Device path | Deployed md5 | Source / build | Repro |
|----------|-------------|--------------|----------------|-------|
| noice `base.apk` (coro-patched, debug-signed) | `/data/app/el1/bundle/public/com.github.ashutoshgngwr.noice/android/base.apk` | `5c58abca185f25b88c0993f06c9ee9c4` | `noice-smali-patches/kotlinx-coroutines/a.smali` + a stock noice base APK | build (§2.10) / collect | GAP: stock noice APK not committed (license). |
| `start_asx.sh` (appspawn-x bring-up + LD_PRELOAD) | `/data/local/tmp/start_asx.sh` | (text, see §3) | reproduced verbatim in REPRODUCE-CLEAN-WSL.md §3 | inline |
| `apk_install` | `/data/local/tmp/apk_install` | — | device-resident installer (accepts debug-signed APK) | collect |
| `bpfgrant` | `/data/local/tmp/bpfgrant` | — | grants per-uid internet in netsys eBPF map; ref src `bpf-analysis/netsys-ebpf.c` | collect (GAP: tool binary, no committed build) |
| `noice-room.db.bak` | `/data/local/tmp/noice-room.db.bak` | — | cached populated sound library (Room DB) | collect |
| `noice-cdn-cache.bak/` | `/data/local/tmp/noice-cdn-cache.bak/` | — | cached cdn library.json + assets | collect |
| `cacerts.tgz` (CA roots) | extracted to `/system/etc/security/cacerts/` | `888d018ddebbd183d65745faa0972c1c` | `ca-store/cacerts.tgz` | committed |

## E. Build scripts / helpers (host)

| Script | Location | Purpose |
|--------|----------|---------|
| `build_libart_pathA.sh` | `$HOME/libart-pathA-work/build_libart_pathA.sh` | rebuild patched `libart.so` |
| `build_adapter.sh` | `$HOME/bridge-build/build/build_adapter.sh` | build `liboh_adapter_bridge.so` |
| `build_aosp_lib.sh` | `$HOME/bridge-build/build/build_aosp_lib.sh` | build `libhwui.so` |
| `regen.sh` | `/tmp/tagsoup-boot/regen.sh` (canonical: `docs/engine/V3-5APP-V2-EVIDENCE/regen_boot.sh`) | dex2oat 10-jar boot-image regen |
| `SmaliAssemble.java` | `scripts/SmaliAssemble.java` | smali tree → classes.dex (apktool has no assembler `main()`) |

## BCP order (verified on device, appspawn-x)

```
core-oj  core-libart  core-icu4j  okhttp  bouncycastle  apache-xml
adapter-mainline-stubs  framework  adapter-runtime-bcp  oh-adapter-framework
```
This is the exact `--dex-file` order the boot-image regen must use.
