# Reproduce: Material Components Catalog (`io.material.catalog`) on OpenHarmony

This package extends the **noice** baseline so the **Material Components Catalog**
also runs — including **2nd-level demo Activities** (e.g. List View Demo →
`AdaptiveListViewDemoActivity`), which previously crashed the whole process.

> Read `../START-HERE.md` and `../REPRODUCE-CLEAN-WSL.md` first. Catalog reuses the
> *entire* noice baseline (runtime, libart, boot image, all BCP jars, shims,
> appspawn-x bring-up). Only the deltas below are catalog-specific.

There are **two independent fixes**, applied on top of the noice baseline:

| # | Fix | What it unblocks | Artifact(s) | Boot regen? |
|---|-----|------------------|-------------|-------------|
| 1 | metaData NPE | catalog launches + top-level nav | `adapter-runtime-bcp.jar.6e32a253` | **YES** (BCP jar) |
| 2 | 2nd-level Activity crash | demo Activities render (deep nav) | `libhwui.so.0c82b1db`, `liboh_adapter_bridge.so.20ab65a6` | no |

---

## Fix 1 — metaData NPE (catalog won't even launch without it)

**Root cause.** `CatalogApplication.onCreate → overrideApplicationComponent` calls
`getApplicationInfo().metaData.getString("io.material.catalog.application.componentOverride")`.
On the baseline, `ApplicationInfo.metaData` is **null** → NPE → process killed before
any UI. (Decompiled catalog: a null result there falls back to the default Dagger
component, so a **non-null empty Bundle is sufficient** — real values not needed.)

**Fix.** Make `ApplicationInfo.metaData` a non-null `Bundle` in
`PackageInfoBuilder.buildApplicationInfo`. The **active** `PackageInfoBuilder` lives
in **`adapter-runtime-bcp.jar`** (loaded before `oh-adapter-framework.jar` in the BCP
and **shadows** ohaf's copy — first-jar-wins). Patching ohaf's PIB does nothing; you
must patch arb's.

Provided here:
- `adapter-runtime-bcp.jar.6e32a253` — the patched jar (deploy this; supersedes the
  baseline's `prebuilt-jars/adapter-runtime-bcp.jar.d5d39a05`).
- `PackageInfoBuilder.smali` — the patched smali (the delta: right after the
  `new-instance ApplicationInfo` / `<init>`, insert
  `new-instance Bundle; invoke-direct <init>; iput-object` into the `metaData` field;
  `.locals 8`, v1 free). The round-trip preserves the TLS `$Sf` shim
  (DexClassLoader/loadClass/getDeclaredConstructor intact — verified).

To rebuild the jar from baseline instead of using the prebuilt:
```bash
# baksmali baseline arb → replace PackageInfoBuilder.smali → reassemble (SmaliAssemble) → rezip
# (keep META-INF). See ../scripts/SmaliAssemble.java and ../framework-smali-patches/.
```

**Boot regen is mandatory** (arb is a boot-classpath jar). Use `regen_boot.sh` (10-jar
BCP, dex2oat64, 30 segments). Replace **only** the arb jar in the jar set with
`6e32a253`; keep the other 9 baseline jars unchanged:
```bash
# stage the 10 deployed BCP jars into $WORK/out/<...> with arb = 6e32a253, then:
WORK=/tmp/catalog-boot bash regen_boot.sh
```
**Brick-safety check:** byte-compare the regenerated `boot-framework.oat` to the
deployed one — they must MATCH (proves dex2oat pairs with deployed libart `7b856a2d`,
so only the intended `boot-adapter-runtime-bcp.*` segments differ). ohaf stays at the
baseline (`300581d1`); its PIB copy is shadowed by arb, so no ohaf change is needed.

Deploy:
```bash
# adapter-runtime-bcp.jar.6e32a253 → /system/android/framework/adapter-runtime-bcp.jar
# regenerated boot-* segments → /system/android/framework/arm/
```
Backups on a live device from the original work: `/data/local/tmp/{boot-pre-metadata,
arb.pre-metadata}`.

---

## Fix 2 — 2nd-level demo Activity crash (`createHardwareBitmap` SIGBUS)

**Root cause.** Opening a demo Activity makes the Material transition call
`ThreadedRenderer.createHardwareBitmap()`. In
`android_view_ThreadedRenderer_createHardwareBitmapFromRenderNode`, an
**uninitialized `ANativeWindow* window`** was passed to `AImageReader_getWindow` —
but OH has no functional AImageReader, so `window` stayed stack garbage (a leftover
`&setSwapBehavior::$_2 destroy` thunk). `proxy.setSurface(garbage)` →
`CanvasContext::setupPipelineSurface` derefs it → **SIGBUS BUS_ADRALN on RenderThread**
→ whole process dies. Hits **any** app that calls `createHardwareBitmap` (activity
transitions, `RenderEffect`), not just catalog.

**Fix (real off-screen readback, with null fallback).**
- `libhwui.so.0c82b1db` (deploy → `/system/android/lib/libhwui.so`): `createHardwareBitmap`
  now renders the RenderNode into an off-screen OHOS surface (via the bridge's
  AImageReader/AImage shim, dlsym'd across the OH linker-namespace boundary),
  CPU-maps the dmabuf, and copies pixels into a heap `Bitmap`. If the shim is
  unavailable it returns `null` (software-bitmap fallback — `createHardwareBitmap` is
  contractually nullable). Source: `android_graphics_HardwareRenderer.cpp`.
- `liboh_adapter_bridge.so.20ab65a6` (deploy → `/system/lib/liboh_adapter_bridge.so`):
  adds the `AImageReader_*`/`AImage_*` wrappers + `oh_imagereader_*` primitives the
  libhwui readback needs (on top of OHOS surface APIs). Strict superset of the noice
  baseline bridge — all input/scroll/key/drag/control-channel symbols preserved.
  Source: `surface_oh_helper.cpp`.

**Neither is a BCP jar → NO boot regen.** Deploy and **reboot** (so the new `.so`s
load under a fresh appspawn-x prefork). Backups from the original work:
`/data/local/tmp/{libhwui.pre-hwbmp (21daf5e9), bridge.pre-hwbmp (42ab7575)}`.

### Rebuilding from source
Both build from the OHOS+AOSP source trees described in `../REPRODUCE-CLEAN-WSL.md`
(the bridge build host, `bridge-build/`):
```bash
bash build/build_aosp_lib.sh --target=libhwui.so          # → libhwui.so
bash build/build_adapter.sh  --target=liboh_adapter_bridge.so   # → liboh_adapter_bridge.so
```
- **bridge** rebuilds to exactly `20ab65a6` (md5-verified == the deployed binary).
- **libhwui** rebuilds to `01f1900f` — a clean, functionally-equivalent build (same
  readback path; differs only in build non-determinism / a log-string tweak vs the
  validated `0c82b1db`). **For an exact, on-device-validated match, deploy the committed
  `libhwui.so.0c82b1db`.**
- Build-fix note: `surface_oh_helper.cpp` must not re-declare
  `OH_NativeWindow_NativeWindowHandleOpt` (it's in the included `external_window.h`);
  cast `nw → reinterpret_cast<OHNativeWindow*>(nw)` at the call sites.
- libhwui's Phase-4 UND gate can false-fail on a stale/unsorted whitelist
  (`comm: not in sorted order`). The real brick-safety check is `nm -D` UND diff vs a
  known-good libhwui = **0 new UND** (same 1006-symbol set).

---

## Install the catalog APK + bring up + validate

1. Install the Material Components Catalog APK (`io.material.catalog`, e.g. the
   F-Droid build) via the baseline's `apk_install`.
2. Bring up exactly as in `../START-HERE.md` (setenforce 0 → `start_asx.sh` → chcon
   socket → wait for Phase 4). Reboot once after deploying Fix 1 + Fix 2.
3. Launch + drive (the catalog child runs as uid **16371**, *not* noice's 13731):
   ```bash
   aa start -a io.material.catalog.main.MainActivity -b io.material.catalog
   # nav via the control channel (X Y screen coords):
   echo "180 400" > /data/local/tmp/noice_tap   # grid → Adaptive demos
   echo "300 520" > /data/local/tmp/noice_tap   # → List View Demo
   ```

**Expected (validated 2026-06-23):** grid → Adaptive demos page →
`AdaptiveListViewDemoActivity` **renders** (Inbox email list); deeper nav to the email
detail also works. Child stderr shows
`[OH-HWBMP] OK ... req=720x144 buf=720x144 stride=2880` and **SIGBUS=0 SIGSEGV=0
FATAL=0 setupPipelineSurface=0**; the process stays alive. See `evidence/`
(`01-grid-landing`, `03-AdaptiveListViewDemoActivity-RENDERED`,
`04-email-detail-deeper-nav`) and `evidence/EVIDENCE-README.md`.

**Scope of the 2nd-level fix (honest).** The off-screen readback is proven to capture
correct pixels (real-content dump + a magenta-watermark test on the returned bitmap),
but the OHOS adapter does **not** composite the Android shared-element /
`MaterialContainerTransform` overlay — so the snapshot is captured but not displayed;
the transition is a plain cut. The fix's concrete value is **crash-prevention /
reachability** (demo Activities open at all), not a visible morph animation.

---

## md5 manifest (verify your copies)
```
0c82b1db  libhwui.so.0c82b1db
20ab65a6  liboh_adapter_bridge.so.20ab65a6
6e32a253  adapter-runtime-bcp.jar.6e32a253
```
