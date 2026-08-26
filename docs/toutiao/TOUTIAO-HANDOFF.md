# Toutiao on OpenHarmony — Handoff

**Written 2026-08-18; resolved and updated 2026-08-19.** Board
`5cdbf6af00000000000000000923012c`, aarch64 OHOS 6.1.0.31.
Audience: the next agent continuing this bring-up. Read §0 and §1 before touching anything.

---

## 0. TL;DR — where this actually stands

Toutiao (`com.ss.android.article.news`, ByteDance news app) on the Android-compat layer:

| capability | state |
|---|---|
| Launches | ✅ **3/3 runs**, was 1/3 (§673) |
| Survives the whole harness | ✅ no-flag final run alive past 5:56, 0 SEGV (§681) |
| Creates windows | ✅ 3 `ViewRootImpl`, RS surface nodes on tree, `Visible=1` |
| Builds its real UI | ✅ full view tree: search bar, 19 category tabs, feed recycler |
| Gets past first-run consent | ✅ tap 同意 at (600,1207) |
| **Loads real content** | ✅ **6 live news headlines from ByteDance servers** (§676) |
| Responds to input | ⚠️ consent dismisses; feed-item taps deliver but do not navigate (§9) |
| **Renderable/capturable** | ✅ `PROTECTED` removed; both Toutiao RS nodes are `SpecialLayer=0` (§5) |

**The former black-capture problem is resolved.** A build-time/deployed `Surface` vtable epoch
mismatch made typed producer-default virtual calls unsafe. During Toutiao's multi-window
relayout, one nominal default-property call wrote a live stack address into consumer
`defaultUsage_`; the stray `0x800` bit then made RS classify the feed layer as `PROTECTED`.
The bridge now performs producer format/usage configuration only through the deployed
`OHNativeWindow` C API. This is a boundary-only, app-agnostic fix; Android framework behavior is
unchanged. See §5 for the proof and control runs.

---

## 1. Hard rules — violate these and you will waste a day

These are earned, each from a specific failure in this session or earlier ones.

1. **ALWAYS RUN THE CONTROL.** noice is the reference app: same stack, renders perfectly.
   Any claim of the form "X is broken" must be checked against noice before you build on it.
   Three separate "root causes" died this way in one session (see §6).
2. **Check the instrumentation's sampling condition before counting log lines.** All
   `[G214au_HWR]` markers log only at `n == 1 || n % 30 == 0`. Counting them produced a
   confident, fully-argued, completely wrong conclusion.
3. **`tid == pid` is NOT the Android UI thread in this port.** Get the UI thread from the
   thread dump: `touch /data/service/el1/public/appspawnx/DUMPNOW.<pid>` →
   `/data/service/el1/public/appspawnx/threaddump_<pid>.txt`. Reading `wchan` from
   `/proc/<pid>/task/<pid>` gave a false "deadlocked on futex" diagnosis.
4. **Never judge a PROTECTED window by a screenshot** (§5). Use the view tree.
5. **Prove the app is alive, and that your oracle names the RIGHT pid.** Every launcher writes
   `/data/local/tmp/asx/walkpid`. Use it.
6. **`kill -9 $(pidof appspawn-x)` also kills the CHILD** — a fork keeps the parent's cmdline.
   Harmless when children always crashed; now that they survive, each run kills the previous
   run's live child. Watch for this when comparing runs.
7. **Verify a patch is in the BYTES on the device**, not just that the build succeeded.
   `hdc file send` with an absolute host path silently no-ops (mangles the path). Always
   `md5sum` both sides.
8. ⛔ Never `kill -3` (no SignalCatcher — SIGQUIT fakes a crash). Never
   `param set persist.sys.usb.config none`. Never add `critical` to a fail-prone init service.
9. ⛔ No `tr` / `awk` on the device shell. `zipalign -f -p 4` after any python zip repack.
10. **Consult codex before a major fix** (§8). It has now caught four wrong assumptions,
    including two of mine in this session.

---

## 2. The stack — exact deployed state (verified 2026-08-19)

Device paths under `/data/local/tmp/asx/`:

| artifact | md5 | notes |
|---|---|---|
| `libart.so` | `1f560dd3e598c19e5b4ad9bbfcce4aab` | **source rebuild** with §673 fix |
| `libhwui.so` | `7962c1a77b8f674bf01dfa612e053481` | known-good; **do not rebuild** (§7) |
| `fw/adapter-mainline-stubs.jar` | `66155a7da2967942a92cced520e9f635` | §675 + §676 patches |
| `liboh_adapter_bridge.so` | `06bbfcb0b13a994220dcd0b5e94d9d80` | §5 Surface ABI + §681 asset lifetime fixes |

Backups on device (restore points):
`fw/adapter-mainline-stubs.jar.bak-pre675`, `.bak-pre676`, `libhwui.so.pre674`,
`libart.so.pre673`, `run_tt.sh.pre679`,
`liboh_adapter_bridge.so.pre-westlake-surface-abi-final` (successful A/B,
`d6595447f674a1aa0a69798da50b98b4`), and
`liboh_adapter_bridge.so.pre-asset-lifetime-final` (surface-only final,
`ad2dacc70020b2e59616b740a4c8d1e7`), and
`liboh_adapter_bridge.so.pre-usage-preflight` (original bridge,
`2fd623acf965dd78d592c103e6776253`).

`LD_PRELOAD` in `run_tt.sh` must be exactly:
`liblog_shim.so:libwl636.so` — an inert `libwlusage.so` probe was removed; leaving a
non-functional wrapper around a critical EGL entry point is a latent hazard.

★ **The old "libart is not reproducible from source" blocker is DEAD.** The board runs a source
build. Rebuild with `make -f Makefile.ohos-arm64` in `/home/dspfac/art-latest` +
`bridge-build-arm64/build_libart_so_arm64.sh`. Still symbol-diff before deploying.

---

## 3. How to run and measure it

### Harness (built this session, use it)

- **`bridge-build-arm64/ttwalk.sh`** — launch/drive/measure. Subcommands:
  - `launch` — gates on `side-channels started` (**never** a timer), writes `walkpid`
  - `vt` — dump the view tree
  - `tap <x> <y>` / `taps <id-or-text>` — inject a tap
  - `shot <name>` — capture + quantify
- **`bridge-build-arm64/shotlit.py`** — quantifies a capture: mean luminance, lit%, distinct
  colours, verdict `BLACK|FLAT|PARTIAL|LIT`. Calibrated: noice UI = LIT (243 lum, 443 colours);
  desktop = LIT (1165 colours); Toutiao = BLACK (lum 0.0, **one** colour).

### The two oracles — keep them straight

| oracle | how | what it proves |
|---|---|---|
| **view tree** (structural) | `echo v > /data/local/tmp/noice_tap` | UI exists, is laid out, has text |
| **pixels** | `snapshot_display` + `shotlit.py` | ⛔ **worthless for Toutiao** (§5) |

⚠️ The view-tree log tag is `[N/OH_InputBridge] VT` where **N varies per run**. Match
`'OH_InputBridge. VT'`, *not* `'OH_InputBridge:'`. That one mistake made a working side-channel
report "0 widgets" while the consent dialog was plainly in the log.

### Screen state (before believing any capture)

```sh
power-shell timeout -o 3600000   # stop it sleeping
power-shell wakeup
uinput -T -m 600 1500 600 400 300   # swipe up to unlock
```
Focus window **15** = lockscreen (black), **11** = desktop. Check it — two "black screen"
captures early in this session were just the lock screen and a powered-off panel.

### Driving Toutiao specifically

- First run shows a privacy dialog. **Tap 同意 at (600,1207)** to clear it.
- After that `root[1]` holds the real UI: `SSTabHost` → `StreamViewPager` →
  `FeedCommonRefreshView`/`FeedCommonRecyclerView`, plus `HomeCategoryTabStrip` (19 tabs).

---

## 4. What was fixed (all verified on-device)

### §673 — the launch lottery was one out-of-bounds read ★★★★★

**There was never a lottery.** All three control runs died at the *same instruction*; only the
clock varied (43 s / 66 s / 174 s). The run that "won" simply crashed *after* its window came up.

`mirror::Class::FindVirtualMethodForInterface` (`aosp-art-15/runtime/mirror/class-inl.h:589`)
indexes the iftable method array with **no bounds check**. Upstream is entitled to that —
matching the interface by identity is supposed to imply the array is sized for it — but this port
breaks the invariant by injecting classes across patched BCP jars. An out-of-range read returns
adjacent heap bytes as an `ArtMethod*` (`0x1000000014`), which **passes every test** in the §206
guard (it checks bits 48-63; the garbage is in bits 32-47) and is then dereferenced at
`interpreter_common.cc:12292` → SIGSEGV at `libart.so+0x93f250`.

**Fix:** `WlFindVirtualMethodForInterfaceChecked()` in
`art-latest/patches/runtime/interpreter/interpreter_common.cc`, bounds-checking with
`IfTable::GetMethodArrayCount(i)` and returning `nullptr` (which the existing §209 repair path
already handles). Applied at **both** call sites. Fixed at the call site, not in the header —
the Makefile has **no header deps**, so editing an inline header rebuilds nothing.

| | control | §673 |
|---|---|---|
| alive at end | 0/3 | **3/3** |
| CHILDSEGV | 3/3 | **0/3** |
| reached a window | 1/3 | **3/3** |

Offender: `com.android.modules.utils.TypedXmlPullParser`, method idx 57-73 vs array count 29.
⚠️ Unconfirmed but likely: the compiled-out `DCHECK(!method->IsCopied())` — a copied
default-method carries a *vtable* index, and 57-73 are plausible vtable indices.

### §675 — `android.graphics.ColorMatrix` was an empty stub ★★★

The class in `adapter-mainline-stubs.jar` was literally `<init>()V` and nothing else. Toutiao's
`AsyncImageView` and `UiUtils` both call `set(float[])` from `<clinit>` → `NoSuchMethodError` →
both left half-initialised.

**Fix:** faithful AOSP reimplementation (pure Java, no native) —
`recipes/patch-colormatrix.sh` + `java/android/graphics/ColorMatrix.java`.
✅ Both clinit failures gone, tolerated-clinit count 12 → 9, no regression.
⛔ **Did NOT fix the feed.** My "AsyncImageView blocks feed rows" hypothesis was wrong. It is a
genuine, independent bug fix — do not re-credit it with the feed.

### §676 — okhttp had no Conscrypt to find ★★★★★ (this is what loaded the feed)

okhttp's platform detection is a pure `Class.forName` probe:

```
findPlatform() → isAndroid() ("java.vm.name"=="Dalvik", true) → findAndroidPlatform()
  Android10Platform.buildIfSupported() → sdkInt>=29 AND findClass("com.android.org.conscrypt.SSLParametersImpl")
  AndroidPlatform.buildIfSupported()   → findClass(conscrypt…) or findClass(org.apache.harmony…SSLParametersImpl)
  both null → throw NullPointerException("No platform found on Android")
```

**Neither class exists in ANY of the ten BCP jars** — this port has no Conscrypt; TLS is real JSSE
over `libssl_openssl.z.so` (§441). Both builders swallowed `ClassNotFoundException`,
`Platform.<clinit>` died, and the HTTP layer was dead. Requests went out, nothing came back.

**Fix:** a **presence-only stub** — okhttp needs only the *type* to resolve; it uses it solely as a
`readFieldOrNull` reflection target, which returns null gracefully.
`recipes/patch-conscrypt-stub.sh` + `java/com/android/org/conscrypt/SSLParametersImpl.java`.

✅ **Result: view tree 113 → 186 lines, `FeedCommonRecyclerView` gains 6 children with live
headlines** ("朱镕基同志遗体在京火化…", "习近平：中方始终从战略高度…", …).

⚠️ `onResponse`/`onFailure` greps read 0 both **before and after** the fix — a useless oracle on
an obfuscated app whose callbacks aren't named that. The feed contents are the real oracle.

### §681 — pin modern `ApkAssets` for resource string-array reads ★★★★★

The first production run without `WL_USAGE_PREFLIGHT` exposed an independent late SIGSEGV at
`liboh_adapter_bridge.so+0x14d7d8`, inside
`NativeGetResourceStringArray()` while dereferencing `ApkAssets::loaded_arsc_`.

Root cause: this JNI file still used the old raw
`assetmanager->GetApkAssets()[cookie]` API, but the deployed aosp-15 `AssetManager2` stores
`vector<pair<wp<ApkAssets>, sp<ApkAssets>>>`. The inline old-header view had the wrong element
layout and did not pin weak assets across the operation. This is exactly why current AOSP uses
`StartOperation()` plus cookie-safe `GetApkAssets(cookie)`.

Fix: backport those deployed AOSP declarations to
`aosp/frameworks/base/libs/androidfw/include/androidfw/AssetManager2.h` and use the upstream
lifetime protocol in
`src/framework/android-runtime/src/android_util_AssetManager_aosp.cpp`. This restores current
Android framework semantics; it is not an app-specific exception or swallowed crash.

Validation: Toutiao no-flag run stayed alive past 5:56 with 157 threads, 65 window events,
live headlines, zero `WESTLAKE-CHILDSEGV`, and zero expired-asset warnings. Noice and Material
Catalog both passed again on the combined build.

---

## 5. RESOLVED — live pointer in buffer usage / false PROTECTED layer

### 5.0 Resolution (2026-08-19)

The actual defect was an ABI boundary violation. The bridge was compiled against a `surface.h`
whose virtual method order does not match the board's mapped
`/system/lib64/chipset-sdk-sp/libsurface.z.so`. Typed calls such as
`GetDefaultWidth/Height/Usage/Format` and `SetDefaultUsage/Format` therefore cannot be trusted.
The setters were already known to return the surface height, proving wrong-slot dispatch. The
decisive new result is that the typed **getter path** contaminated consumer `defaultUsage_` with
a stale argument register containing the Android UI thread's stack address.

Evidence chain:

1. C-ABI preflight on a fresh surface allocated clean `handleUsage=0x409`.
2. On the old bridge, a later relayout of the same surface allocated
   `handleUsage=0x7ee6a86ac9`; `0x7ee6a86ac0` was inside that run's live UI-thread stack.
3. RS's real queue usage was `0x0040007ee6a86ac0`: the same pointer bits plus producer/vendor
   flags. That exact match proves real allocation contamination, not a BufferHandle layout error.
4. Skipping only typed setters did **not** help; it merely stopped their accidental reset.
5. Skipping all typed producer-default getters and setters made every initial and relayout
   preflight clean, including popup sessions 1/2/3, and changed both nodes to `SpecialLayer=0`.

Permanent fix (Westlake principle):

- `src/framework/surface/jni/oh_surface_bridge.cpp`: no typed producer-default virtual calls;
  retains an opt-in `WL_USAGE_PREFLIGHT=1` C-ABI diagnostic.
- `src/framework/window/jni/oh_window_manager_client.cpp`: removes the dead G3.3 diagnostic
  virtual reads/writes; obtains and sets format/usage only with
  `OH_NativeWindow_NativeWindowHandleOpt`.
- No Android framework, ART, hwui, APK, jar, or RenderService semantics were changed for this fix.

Final on-board evidence with bridge md5 `06bbfcb0b13a994220dcd0b5e94d9d80`:

| app/surface | actual result |
|---|---|
| Toutiao C-ABI preflight | initial/relayout usage `0x409` for 1790 and 1920 surfaces |
| Toutiao actual HWUI queues | stable usage `0x0040000000000608`, `SpecialLayer=0`; no ASLR pointer |
| Toutiao pixels | live-feed capture LIT (lum 248.6, 187 colors) |
| Noice 1200x1920 control | populated UI, `SpecialLayer=0`, capture LIT (lum 242.8, 437 colors) |
| Material Catalog 1200x1920 control | Slider UI, `SpecialLayer=0`, capture LIT (lum 242.7, 129 colors) |

The investigation below is retained as pre-fix evidence so future work does not repeat it.

### 5.1 Historical capture evidence (pre-fix)

**[CERTAIN, from OHOS source]** RS capture *deliberately* blacks out protected layers.
`RSSurfaceRenderNodeDrawable::DrawSpecialLayer()` draws an opaque black rectangle whenever
`SpecialLayerType::PROTECTED` is present, and `CaptureSurface()` calls it **before** drawing the
buffer. ⇒ **Toutiao's black screenshot is fully explained by the flag alone and says nothing
about the pixels.**

**[CERTAIN]** Ordinary *physical* composition does **not** use that branch (capture and
"security display" only). A correct secure path shows protected content on the panel while
captures stay black. So `PROTECTED` is *not* inherently "black on the panel".

This was the correct pre-fix interpretation. The final bridge removes the accidental protected
classification, so ordinary captures are a valid pixel oracle again.

### 5.2 Historical per-surface evidence

Toutiao's main window is flagged `SpecialLayer=4100` = `PROTECTED | HAS_PROTECTED`
(`rs_special_layer_manager.h`, `SPECIAL_TYPE_NUM=10`; note `PROTECTED=0x4`, `HAS_PROTECTED=0x1000`).
It is flagged because its `BufferRequestConfig.usage` contains a stray `0x800`
(`HBM_USE_PROTECTED`, `display_type.h:99`) inside a garbage value.

**The within-app control (the strongest evidence in this investigation).** Same process, same run:

| surface | SpecialLayer | usage |
|---|---|---|
| 1200×**1790** | 0 | `0x004000000000020c` — **byte-identical to noice** |
| 1200×**1920** (the feed window) | **4100** | `0x0040007eee8c6ac0` |
| same 1920 surface, previous run | 4100 | `0x0040007f0ea86ac0` |

★★ **The garbage changes between runs**, stays in the `0x7e`–`0x7f` userspace band, and keeps
identical low bits (`…6ac0`) ⇒ **ASLR ⇒ it is a LIVE POINTER**, not a bad constant.
★★ **It is per-surface, not per-process** — the same code writes a correct usage for one surface
and a pointer for the other, in the same run.

### 5.3 Historical exclusions

- **Producer window config `W` is clean.** The hwui EGL hijack's own readback logs
  `prevUsage=0x300` then `prevUsage=0x40000000000200` on the same window — a `GET_USAGE` after a
  `SET_USAGE` never returns a pointer. ⇒ the variadic `NativeWindowHandleOpt(SET_USAGE)` is **not**
  the writer, despite `oh_window_manager_client.cpp:2998`'s own comment blaming it (historical).
- **`androidToOHUsage()`** (`pixel_format_mapper.h`) — builds its result from scratch by OR-ing
  `OH_USAGE_*` constants; structurally cannot emit a pointer.
- **All our `SET_USAGE` sites** were audited: `oh_egl_surface_adapter.cpp:231`, the EGL hijack
  (`0x300`), `surface_oh_helper.cpp:152` (`kReadbackUsage`). All sane `uint64_t` constants.

Since `R = W | D`, the pointer must enter via **`D` (consumer-side `defaultUsage_`)** or in the merge.

### 5.4 ABI bug (now fixed)

`Surface::SetDefaultUsage()` **lands on the wrong vtable slot**: it returns the surface *height*
(1790/1920), leaves `GetDefaultUsage()==0`, and the paired `SetDefaultFormat` leaves a garbage
format. Codex: *"setter returns height" strongly identifies the deployed target as
`GetDefaultHeight()`* — i.e. a **slot oracle** proving compiled-caller vs deployed-object skew.
Confirmed in this checkout: `producer_surface.h:274` declares 64-bit default usage while
`producer_surface.cpp:287` implements the older 32-bit signature — **mixed interface epochs**.

The original setter observation was only a slot oracle. The later C-ABI preflight and getter-skip
A/B resolved the tension: setter removal alone did not help; the nominal getter sequence during
Toutiao's extra relayout was the contaminating path. Noice never exercises that same lifecycle.

### 5.5 Historical ranked plan (completed)

The black-, gray-, and white-box experiments below were executed in order. Pointer provenance
landed in the app UI-thread stack; both unrelated 1920 controls stayed clean; and the C-ABI
preflight isolated contamination to the typed `Surface` path. Keep the plan for audit value, not
as outstanding work.

**Q1 ranked mechanisms:**
1. *(high)* ABI/vtable epoch mismatch **plus a 1920-specific code path** — a different factory,
   proxy class, first-buffer path, or batch-request path on that surface makes a *different*
   mismatched virtual call land on a mutator.
2. *(med-high)* Persistent corruption of consumer `defaultUsage_` (fits per-surface persistence).
3. *(med)* Uninitialized/stale `BufferRequestConfig::usage` on an alternate request path.
4. *(low alone)* Pure struct-layout mismatch. Expected aarch64 layout:
   `BufferRequestConfig` sizeof 40, usage @ **+0x10**; `BufferHandle` sizeof 56, usage @ **+0x18**,
   `virAddr` @ **+0x20**. (usage and virAddr are adjacent — a layout skew could make a reader pick
   up virAddr *as* usage, with nobody ever writing a pointer.)
5. *(certain)* The OR itself cannot manufacture an address. **Do not compute `D` as `R & ~W`** —
   OR is non-invertible.

**Q2 tiered strategy — do these in order:**

| tier | experiment | falsifies |
|---|---|---|
| **Black box (do FIRST, costs minutes)** | Locate `0x7eee8c6ac0` in `/proc/<pid>/maps` of **both** the app child and the render service during a bad run | child-only ⇒ not RS-origin; RS-only ⇒ not app/EGL-origin; neither ⇒ stale/mis-decoded |
| **Gray box** | Toggle **one** app between inset 1790 and edge-to-edge 1920, recording node/session/queue IDs | bad across unrelated 1920 apps ⇒ not Toutiao-specific; clean on a matched 1920 control ⇒ not geometry alone |
| **White box** | From the **bridge**, on a fresh 1920 node *before* hwui touches it, request exactly one buffer via the deployed native-window C API; log handle usage + `virAddr`; cancel and destroy | pointer already present ⇒ EGL/hwui excluded; clean preflight then bad first EGL buffer ⇒ pre-existing bad `D` excluded |

For an ELF-backed pointer: `load_bias = mapping_start − mapping_file_offset`;
`ELF vaddr = pointer − load_bias`. If heap/anonymous, compare against logged `Surface*`,
producer/proxy objects, native-window/config addresses, and `virAddr`.

**Q3 — detecting ABI skew without rebuilding libhwui:** dump the runtime vtable from the bridge
**without calling it** —
```cpp
void **vt = *reinterpret_cast<void ***>(surface.GetRefPtr());
for (size_t i = 0; i != N; ++i) { Dl_info info{}; dladdr(vt[i], &info); /* log i, vt[i], dli_fname, dli_sname */ }
```
Do it for both the 1790 and 1920 objects and diff. **Never invoke guessed slots.** Also compare
Build-IDs and `llvm-nm -D` symbol sets of the *mapped* `libsurface.z.so` (not the filesystem
default — the process namespace may select a different one). Epoch markers: `GetProducerInitInfo`,
`RequestBuffers`, `SetLppShareFd`, 32- vs 64-bit `SetDefaultUsage`.

**Q4 — clearing PROTECTED.** ⚠️ Codex **corrected its own earlier claim**: internal
`RSSurfaceRenderNode::SetProtectedLayer(false)` **does** exist and can clear the bit, but the
client `RSSurfaceNode` API does not expose it (`SetSecurityLayer/SetSkipLayer` are different
classifications). Recreating the full server-side session/node **and** buffer queue should reset
it (new RS node ID + new queue unique ID); recreating only the EGLSurface/native-window/producer
over the same node will not. Bridge-only prevention: remove the known-broken virtual calls to
`Surface::SetDefaultUsage`/`SetDefaultFormat` (undefined behaviour), keep `W` free of
`HBM_USE_PROTECTED`, and **do not** patch `BufferHandle::usage` post-allocation (that desynchronises
metadata from the real allocation's security properties). Note `W=0` cannot help if `D` is bad —
`W|D` cannot clear bits.

---

## 6. ⛔ REFUTED — do not re-propose these

Nine hypotheses were argued confidently and then killed by measurement. Re-running them is pure
waste. Each is recorded with what killed it.

1. **"Content never populates."** False — 487 `View.draw` incl. Toutiao's own `TabTextView`,
   `RecyclerView`, `ViewPager`, Fresco views.
2. **"Only one frame was presented / `CanvasContext::draw` early-returns 27×."** False — artifact
   of sampled logging (`n==1 || n%30==0`).
3. **"OH session churn — 6 sessions for 3 windows, one per relayout."** False on every count.
   Relayout creates **no** sessions; the 6 sessions are **6 genuinely distinct windows** (two
   Activity tokens + a `type=1000` panel). `BBQ.isSameSurfaceControl` returning false across them
   is *correct*, and 14 `setSurface` calls for 6 windows is ~2 each, not a storm.
4. **"UI thread deadlocked on a futex (§283j)."** False — `tid==pid` is not the UI thread; the
   real one (`sysTid` from the thread dump) idles in `MessageQueue.nativePollOnce`. An idle looper
   after ~28 draws is **normal** for a static screen; noice's 3484 draws reflect an animation.
5. **"A stale/dismissed dialog window covers the content."** False — z-order shows the main
   1920 window at Z=9002, **above** the 1790 dialog shell at Z=9001.
6. **"The EGL hijack sets a bad usage."** False — logs `SET_FORMAT(12) rc=0, SET_USAGE rc=0,
   prevUsage=0x300`.
7. **"`SetDefaultUsage` is broken ⇒ garbage usage."** It *is* broken (§5.4) but is **not the
   cause** — noice shows the identical defect and renders fine.
8. **"`AsyncImageView`'s clinit failure blocks feed rows."** False — §675 fixed it; feed stayed
   empty until §676.
9. **"`timestamp = 0` on the buffers is a defect."** False — noice's buffers show `timestamp = 0`
   too, *while visibly rendering*.

**The pattern in all nine:** inferring a mechanism from a correlation, or from an absent log line,
without running the control. The eliminations that stuck all came from running noice, checking the
sampling condition, or reading the source.

---

## 7. Build hazards — read before rebuilding anything

### ⛔ libhwui is NOT reproducible from source
A rebuild changing **only three `fprintf` argument lists** (154 objects, exit 0) produced a binary
that breaks child init: `INITCHILD-FAIL=89`, `ViewRootImpl=0`, `sidechan=0` vs the shipped
`0 / 115 / 6`. Confirmed by one-variable rollback.

★ **A clean symbol diff is NOT sufficient to deploy libhwui.** Identical 3319 dynsyms, zero
symbols added/lost, sane symbol sizes (`CanvasContext::draw` 0x7ec→0x7fc as expected) — *all
passed*, and the binary was still broken. **Only a full launch run catches it.**
Broken build kept at device `libhwui.so.674instr` / host `hwui-build/libhwui.so`; the
`aosp-14-base` source was reverted to pristine (md5-verified), instrumented copy at
`bridge-build-arm64/CanvasContext_674instr.cpp`.

### ⛔ LD_PRELOAD cannot interpose the render path
Three attempts, **all silent, with the library provably mapped in the child** (4 map entries vs 8
for the working `libwl636`/`liblog_shim`):
- `OH_NativeWindow_NativeWindowRequestBuffer` — `libnative_window.so` isn't even loaded;
- `ProducerSurface::RequestBuffer` (exported `T`) — `Surface::RequestBuffer` is **virtual**, so
  EGL dispatches through the vtable and never touches the PLT;
- `eglSwapBuffers` + `WithDamageKHR/EXT` — never routed through our symbol.

⇒ LD_PRELOAD works for **adding** natives ART resolves via `dlsym(RTLD_DEFAULT)` (the `libwl636.so`
trick) but **not for interposing** calls libhwui/libEGL already bind elsewhere. To instrument the
render path you must patch a rebuildable TU — **the bridge**, not libhwui.

### Other trees
- **ART**: rebuildable and currently deployed from source. Symbol-diff first; check the sigchain
  tell-tales (`AddSpecialSignalHandlerFn` 0x274, `SigchainStartReassert` 0x180).
- **Bridge**: rebuildable, but `build_bridge_arm64.sh` **fails 11/97 TUs and links anyway**
  (2.4 MB vs a working 3.9 MB). Always back up the on-device bridge first.
- ⛔ Never `git checkout --` in `art-latest`/`aosp-art-15`: the real `stubs/sigchain_musl.cc`
  exists only uncommitted.

---

## 8. Consulting codex

```bash
cd <workdir> && codex exec --sandbox read-only --skip-git-repo-check \
  -c model_reasoning_effort="xhigh" < prompt.txt > out.txt 2>&1 &
```
Runs 10-40 min on hard questions — **background it**, don't foreground (10 min cap).
Give it: SETUP, SYMPTOM, **numbered evidence**, hypotheses already refuted (tell it not to
re-propose them), your hypothesis, hard constraints, and **ask it to flag CERTAIN vs SPECULATION**.
That last instruction is what makes the answer auditable.

It has now caught four wrong assumptions, including the "the panel is black" error that reframed
this entire investigation. ⚠️ Still verify on the board — it reasons from upstream sources, which
may differ from this port's patched tree.

Saved prompts/outputs: `scratchpad/codex_prompt.txt`, `codex_out.txt`, `codex_prompt2.txt`,
`codex_out2.txt` (this session's scratchpad — copy them somewhere durable if you need them).

---

## 9. Smaller open items

- **19 category tabs carry no text.** The strip is built with correct geometry (46×81 each) but no
  labels, while sibling TextViews print theirs. ⚠️ `TabTextView` is a custom subclass — the dumper
  may simply not read its text. Unconfirmed either way.
- **Tapping 4 different tabs left the view tree byte-identical** (113 lines). Either the taps miss,
  or tab switching does nothing yet.
- **Resolved in the 2026-08-24 follow-up (§11): feed-item taps navigate.** The locked article opens,
  fetches or consumes cached content, reaches KTX ready, and renders. The remaining defect is
  variable cold/expired-cache latency, not missing click delivery or navigation correctness.
- **9 tolerated `<clinit>` failures remain** (down from 12), incl. `MinFreeHeapOptNative`,
  `FlippedV2Impl`, `HostAbiUtils`, `LibrarianImpl`, `JniHookController`, `PrivateApiLancetImpl`,
  `ZoneInfoDb`, `SoundPool`, `X/Ad4`. Mostly missing native libs and `MediaStore` fields.
- **Assets that fail to open:** `ttnet_config.json`, `libbytedanceweb.so`,
  `plugins/com.ss.android.share_token_rule.jar`, `fonts/ByteNumber-*.otf`,
  `transcode_check_type_new.js`.
- **`sscronet`** (Toutiao's real Chromium net stack) loaded in one run, not in another. The feed
  works via okhttp regardless.
- **`android.webkit` is entirely absent** (126 null-receiver hits/launch). Matters for WebView
  content, not for the feed.
- **Material Catalog retry passed** through direct APK/activity launch. The earlier `aa start`
  failure was missing bundle registration, not a jar/bridge regression. Slider UI was populated,
  `SpecialLayer=0`, capture LIT (lum 242.7, 129 colors).

---

## 10. Suggested order of work

1. Confirm the deployed ASLR-guard Toutiao launch remains alive and the live-feed view tree stays
   populated after extended use.
2. Exercise tab changes, back navigation, scrolling, and any popup/video surfaces; record view tree
   plus RS queue/SpecialLayer state for each. Article opening itself is proven in §11.
3. Work the smaller compatibility items in §9 only when a concrete user flow hits them.
4. Keep Noice and Material Catalog as mandatory controls for every future bridge change.

Do not rebuild libhwui or patch RS/BufferHandle security metadata for this issue. The defect was
already fixed at the OH boundary, and post-allocation flag clearing would be semantically unsafe.

---

## 11. 2026-08-24 follow-up: article timing and ART ASLR guard

The controlled experiment is preserved at
`toutiao-quiet-ab-20260824/RESULTS.md` with artifacts, hashes, complete child logs, fresh control
captures, and reusable runners.

- Suppressing the hot interpreter diagnostic writes was **not** an article-speed fix. Three quiet
  runs all loaded the locked article with live JIT and no fatal exception, but ready times were
  125.501 s, 295.154 s, and 375.057 s. The quiet build is retained only as an opt-in experiment;
  normal builds do not enable it.
- The locked `article_detail` entry has a 600-second TTL. Its `timestamp` is device monotonic
  milliseconds. A fresh-timestamp control proved the app consumed cached content and reached
  ready in 109.011 s; expired-cache runs fetched and replaced the row successfully but were much
  more variable. SQLite operations were 0-11 ms, so database throughput is excluded.
- HTTP-head tracing saw HTTP 200 for the exact
  `/article/content/.../7676979467604083210/...` request. Do not label the remaining delay a
  networking failure: the live request completes and the article renders. The open issue is
  latency before/around request callback and KTX binding.
- `WESTLAKE-BADCALL/BADMETHOD` had an ASLR false-positive bug. Valid aligned canonical pointers
  such as `0x7e5b350050` were called corrupt merely because their address bytes looked printable.
  The original real `on/icon` corruption remains covered by alignment/canonicality checks. The
  narrowed generic ART guard reduced 10,768/10,768 false records to 0/0 in the article run.
- Deployed `libart.so`: `6fb9c8487673ca08559e1842bc60c54e`. It preserves all 32,342 exported
  symbol names and passed the unchanged Toutiao expired-cache article, Noice (95 widgets, LIT),
  and Material Catalog (52 widgets, LIT), all with live JIT and no fatal/segv. Exact rollback:
  `/data/local/tmp/asx/libart.so.noisy-ab-20260824`, MD5
  `ba9e200508ea53d25cbfcdc042e1e057`.

Next action: add monotonic timestamps only at article request enqueue, response callback, DB write,
and KTX data-ready. Do not use `SIGQUIT` thread dumps in timing trials; two dumps materially
perturbed the large Toutiao process. Preserve the locked article and mandatory controls.
