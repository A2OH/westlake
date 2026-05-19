**[English](V3-ARCHITECTURE.md)** | **[中文](V3-ARCHITECTURE_CN.md)**

# Westlake V3 Architecture — HBC-runtime + Westlake app-hosting engine

**Status:** AUTHORITATIVE (2026-05-15). Supersedes the V2 Phase-2-OHOS direction (`project_v2_ohos_direction.md` 2026-05-15 morning) for the OHOS path. Does NOT supersede V2 for the Android-phone Phase-1 path — that stack is untouched.

**Date:** 2026-05-15
**Author:** agent 42, after the CR-EE / CR-FF HBC architecture analyses landed
**Companion docs (forward-looking, current):**
- `V3-WORKSTREAMS.md` — W1-W13 canonical work breakdown for the V3 OHOS path
- `V3-SUPERVISION-PLAN.md` — priority ordering, parallelism, first-3-day dispatch
- `CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md` — explicit retraction of CR61's libipc/samgr ban for V3
- `CR-EE-HANBINGCHEN-ARCHITECTURE-ANALYSIS.md` — structural overview of HBC's stack (the runtime we adopt)
- `CR-FF-HBC-BORROWABLE-PATTERNS.md` — tactical patterns adopted into V3
- `CR-DD-CANDIDATE-C-VS-V2-OHOS-RECONSIDERED.md` — the analysis that triggered today's pivot
- `CR60_BITNESS_PIVOT_DECISION.md` — 32-bit-ARM-on-DAYU200 prerequisite (preserved by V3)

**Companion docs (historical, preserved verbatim):**
- `BINDER_PIVOT_ARCHITECTURE.md` — V2 consolidated architecture (Phase 1 STAYS authoritative)
- `BINDER_PIVOT_DESIGN.md` (V1, 2026-05-12)
- `BINDER_PIVOT_DESIGN_V2.md` (V2, 2026-05-13)
- `CR41_PHASE2_OHOS_ROADMAP.md` — V2 Phase-2-OHOS milestone plan (M9-M13) — SUPERSEDED BY V3 for the OHOS path
- `project_v2_ohos_direction.md` — V2-OHOS commitment — SUPERSEDED BY V3

---

## 0. Executive summary

After validating the V2 Phase-1 substrate end-to-end on Android (in-process Option 3 hosting both noice and McD inside `com.westlake.host`), Westlake spent ~24 commits (CR-W → CR-X → CR-Y → CR-Z chain) trying to extend the dalvik-kitkat + Westlake-substrate stack onto OHOS-DAYU200. The end-to-end visible result did not advance (panel pixel stayed launcher-white per CR67 / CR-AA diagnostics). The `feedback_additive_shim_vs_architectural_pivot.md` rule fired: the layer itself was wrong, not the next shim.

Today, CR-EE and CR-FF (agents 40 + 41) audited an adjacent teammate's (HBC's) work tree. HBC has independently solved the OHOS+Android hosting problem at multi-engineer-month cost using **real AOSP-14 ART + real `framework.jar` cross-built to OHOS musl**, with `appspawn-x` as a Zygote analog, 5 forward `IXxx.Stub` adapters + 6 reverse C++/Java bridges, and `liboh_skia_rtti_shim.so` (12.6 KB) instead of rebuilding Skia. They are at `MainActivity.onCreate` line 83 today — significantly farther than Westlake's V2-on-OHOS substrate. There is no IP concern with reusing HBC's artifacts: they are the same company, the same product effort.

**V3 commits Westlake to:** stop owning a Java substrate / dalvikvm / libhwui / boot image for the OHOS path; instead **reuse HBC's runtime substrate** and refocus Westlake's investment on the **app-hosting engine** that lives on top of it — intent rewriting, lifecycle orchestration, multi-app coordination, host APK on Android phone, mock-APK and per-app validation. The Phase-1 Android-phone stack (V2 in-process host) is preserved unchanged.

V3 is **architecturally simpler** than V2-OHOS (~85% of the platform substrate work no longer Westlake's problem). V3 is **strategically narrower** for Westlake — we own the layer where Westlake adds product value, not the layer where HBC has already paid the cost.

---

## 1. Strategic context — why V3, why now

### 1.1 What V2 Phase-1 (Android-phone) actually proved

The V2 substrate works for in-process Android hosting on a stock Android phone (cfb7c9e3, Android 15 LineageOS 22). Per `MEMORY.md` "BOTH apps in-process (OPTION 3)" entry (2026-05-14):

- noice renders real UI: Welcome → Library/Favorites/Profile tabs, full fragment nav
- McD renders real UI: SplashActivity → Wi-Fry McD-branded offline screen
- Generic 5-pillar pattern: hidden-API bypass + LoadedApk dir redirect + safe-context bind stub + LocaleManager binder hook + lifecycle drive to Resumed
- Per-app diff = 4 constants + manifest aliases (no per-app code branches; macro-shim contract honored)

This is the Westlake product Phase-1 deliverable. **V3 preserves it 100% unchanged.** It runs on real Android, against real `framework.jar` and real ART. The host APK (`westlake-host-gradle/`) stays.

### 1.2 What V2 Phase-2 (OHOS) attempted and what blocked

V2-OHOS attempted to port the same substrate to OpenHarmony by:
- Building our own libbinder + servicemanager (M9-M10, CR61)
- Building our own audio daemon (M5/M11) + surface daemon (M6/M12)
- Writing a `SoftwareCanvas` extending `android.graphics.Canvas` that recorded `drawColor` + last `drawRect`
- Writing `drm_inproc_bridge.c` to blit directly to `/dev/fb0` (later DRM/KMS)
- Maintaining ~12,403 LOC of Westlake-shadowed framework classes (Application, Activity, Resources, Window, PhoneWindow, DecorView, WindowManagerImpl)

The CR-W → CR-X → CR-Y → CR-Z chain (~½ day's strategy time, ~24 commits) added arsc parsing, theme resolution, lifecycle drive, BCP-level Locale/Date/ByteOrder patches. End result: panel pixel = launcher-fallback white. `feedback_additive_shim_vs_architectural_pivot.md` formalized the pattern: when ≥3 consecutive CRs in the same layer keep generating new symptoms while the visible result doesn't move, the layer is the bug.

### 1.3 What HBC independently demonstrated (CR-EE / CR-FF)

HBC's `~/adapter/` work tree shows:
- Real AOSP-14 ART cross-built to OHOS 32-bit musl: `libart.so` (15 MB), `libhwui.so` (1.85 MB), `libandroid_runtime.so`, 38 cross-built native .so total
- Real `framework.jar` byte-perfect with **4 surgical L5 patches** (228 lines total) delegating `IActivityManager` / `IActivityTaskManager` / `IActivityManagerSingleton` / `IWindowManager` `Singleton.create()` to `OHEnvironment.getXxxAdapter()` (CR-EE §3)
- `appspawn-x` daemon as Zygote analog (`/system/bin/appspawn-x`, hybrid OH AppSpawn + AOSP Zygote, 311 LOC main.cpp) — see CR-EE §1.5 + §5 process model
- Real boot image: 9-segment `dex2oat`-baked (`boot.art/oat/vdex` + 8 module segments)
- `liboh_skia_rtti_shim.so` (12.6 KB, 18 RTTI symbols) instead of rebuilding 23 MB of Skia (CR-EE §6, CR-FF Pattern 4)
- `libbionic_compat.so` shim between AOSP bionic ABI and OHOS musl
- 5 forward bridges (`extends IXxx.Stub`) + 6 reverse callback C++/Java sandwiches, **530+ AIDL methods** total (CR-EE §1.3, §7)
- HelloWorld.apk reaches `MainActivity.onCreate` line 83 (TextView ctor) with real `handleBindApplication`, real Application instance, real `PhoneWindow.<init>`, real `Activity.attach`, `addToDisplayAsUser ADD_OKAY=0` and `activityResumed state=9 rc=0` (CR-EE §8)
- Project invariant: **APK transparency** (zero `import adapter.*` in any APK source)

This is dramatically farther than Westlake's V2-OHOS got, with a 10× larger investment behind it. Reusing HBC's artifacts is the rational move.

### 1.4 The decision

**Westlake commits to V3** as defined below:
- Drop dalvik-kitkat OHOS port (relegate to demo / archived)
- Drop `aosp-shim-ohos.dex` framework substitute (replaced by HBC's real `framework.jar`)
- Drop SoftwareCanvas + `drm_inproc_bridge.c` (replaced by real HWUI + HBC's `ANativeWindow → IBufferProducer` path)
- Drop M5 audio daemon + M6 surface daemon for the OHOS path (replaced by HBC's `appspawn-x` + render integration)
- Drop Westlake's own libbinder + servicemanager for the OHOS path (replaced by HBC's adapter framework which uses real OH IPC innerAPI)
- **Keep V2 Phase-1 Android stack unchanged**
- **Refocus Westlake-owned engineering on the app-hosting engine layer** that sits on top of HBC's runtime

The Phase-1 Android stack (V2 in-process Option 3) is unaffected. V3 is purely a Phase-2-OHOS-direction replacement.

---

## 2. V3 layer stack

```
LAYER STACK (V3 OHOS path)
─────────────────────────────────────────────────────────────
APP (unmodified APK: noice / McD / arbitrary Android app)
  - APK transparency invariant: zero import of any host class
  - Verified only by outside-observer (logcat / hilog / dumpsys)
─────────────────────────────────────────────────────────────
WESTLAKE APP-HOSTING ENGINE                ← Westlake-owned
  - westlake-host-gradle (Android-side; stays unchanged for Phase 1)
  - app intent rewriting (Instrumentation subclass for cross-pkg)
  - lifecycle drive-down patterns (5-pillar pattern from V2)
  - multi-app coordination (one HBC child process per Westlake app)
  - per-app constants table (4 constants per app, no code branches)
  - macro-shim contract enforcement (narrower scope: integration seams only)
─────────────────────────────────────────────────────────────
HBC RUNTIME SUBSTRATE                       ← reused from HBC teammate
  - AOSP-14 ART cross-built to OHOS 32-bit musl (38 native .so + 11 jars)
  - real framework.jar + 4 surgical L5 patches (228 LOC total)
  - libhwui + Skia RTTI shim (liboh_skia_rtti_shim.so 12.6 KB)
  - libbionic_compat.so (musl ↔ bionic ABI compat)
  - 9-segment dex2oat-baked boot image (boot.art/oat/vdex ×9)
  - appspawn-x daemon (Zygote analog; OH AppSpawn-compatible socket)
  - 5 forward bridges (IXxx.Stub subclasses) + 6 reverse C++/Java
    sandwiches; 530+ AIDL methods total
  - OHEnvironment + dual-classloader (PathClassLoader for non-BCP)
  - ScopedJniAttach RAII
─────────────────────────────────────────────────────────────
OHOS PLATFORM                                ← OHOS-native (legacy WMS)
  - libipc_core + samgr_proxy (innerAPI variants only — see CR61.1)
  - render_service / composer_host
  - legacy WindowManagerService (window_manager_use_sceneboard=false on DAYU200)
  - SceneBoard NOT used today; tracked as W8 (independent of V3)
─────────────────────────────────────────────────────────────
HARDWARE (DAYU200 rk3568, 32-bit ARM userspace)
```

**Cross-platform / cross-phase split:**

| Phase / target | Stack |
|---|---|
| Phase 1, Android-phone (cfb7c9e3) | V2 in-process Option 3 — `westlake-host-gradle` on host phone's native ART + real framework.jar — UNCHANGED by V3 |
| Phase 1, McD / noice / any APK | V2 5-pillar pattern + per-app constants table — UNCHANGED by V3 |
| Phase 2, OHOS DAYU200 | V3 stack as defined above |
| Future, OHOS PC / 2-in-1 / SceneBoard | Same V3 stack + SceneBoard board-config workstream (W8) |
| Future, 64-bit OHOS ROM | Same V3 stack, rebuild HBC's aarch64 variants (CR60 §"Cost of reverse-pivot": 2-4 days revalidation) |

---

## 3. Component ownership

| Component | Owner | Source |
|---|---|---|
| `westlake-host-gradle/` Android host APK | Westlake | this repo |
| App-hosting engine (Phase 1 in-process Option 3) | Westlake | `NoiceInProcessActivity.kt`, `McdInProcessActivity.kt` |
| App-hosting engine (Phase 2 OHOS, on HBC) | Westlake | NEW — to be built on top of HBC's appspawn-x |
| App intent rewriting (Instrumentation subclass) | Westlake | open item per `project_noice_inprocess_breakthrough.md` |
| Per-app constants table | Westlake | per V2 contract, narrower scope under V3 |
| Macro-shim contract enforcement | Westlake | `feedback_macro_shim_contract.md` (still applies at integration seams) |
| AOSP-14 ART (`libart.so`, dex2oat) | HBC | `~/adapter/out/aosp_lib/libart.so`, `~/adapter/out/host-tools/dex2oat` |
| `framework.jar` + 4 L5 patches | HBC | `~/adapter/aosp_patches/frameworks/core/java/android/{app,view}/*.patch` |
| `libhwui.so` (real AOSP-14) | HBC | `~/adapter/out/aosp_lib/libhwui.so` |
| `liboh_skia_rtti_shim.so` | HBC | `~/adapter/framework/surface/jni/skia_rtti_shim/` |
| `libbionic_compat.so` | HBC | `~/adapter/framework/appspawn-x/bionic_compat/` |
| Boot image (9 segments) | HBC | `~/adapter/out/boot-image/boot.{art,oat,vdex}` + 8 module segments |
| `appspawn-x` daemon | HBC | `~/adapter/framework/appspawn-x/` |
| 5 forward bridges (IXxx.Stub) | HBC | `~/adapter/framework/{activity,window,broadcast,contentprovider,package-manager}/java/` |
| 6 reverse C++/Java bridges | HBC | `~/adapter/framework/{activity,window}/jni/*_adapter.cpp` |
| `OHEnvironment` + dual-classloader | HBC | `~/adapter/framework/core/java/OHEnvironment.java` |
| `libipc_core` / `samgr_proxy` (innerAPI) | OHOS | linked transitively via HBC's `liboh_adapter_bridge.so` |
| `render_service` / `composer_host` | OHOS | unchanged |
| legacy WMS (DAYU200) | OHOS | unchanged |
| SceneBoard | OHOS | NOT enabled on DAYU200 — board-config item (W8) |
| Linux kernel binder (`/dev/binder`) | OHOS | unchanged; CR60 verified live |

**Scope discipline:** Westlake owns the green rows. HBC owns the blue rows (we pull artifacts, do not fork the source). OHOS owns the platform rows (we consume innerAPI per CR61.1).

---

## 4. What V3 deletes from Westlake's OHOS path

Each deletion lists the replacement (HBC component or OHOS-native service) so the contract is concrete.

| V2-OHOS component deleted under V3 | LOC | Replaced by | Citation |
|---|---|---|---|
| `dalvik-port/` OHOS targets (32-bit static + dynamic dalvikvm) | ~50K (mostly upstream) | HBC's `libart.so` + `appspawn-x` | CR-EE §1, §5 |
| `aosp-shim-ohos.dex` framework substitute | ~1.3 MB dex (~70K LOC) | HBC's real `framework.jar` | CR-EE §3 |
| CR-W setContentView NPE fixes (3 NPEs) | ~200 | Real `framework.jar` doesn't NPE here | CR-EE §8 (handleBindApplication+ |
| CR-X arsc parser + theme resolution | ~400 | Real `Resources` / `AssetManager` from AOSP | CR-EE §3 (libandroidfw AssetManager.cpp.patch + register_android_content_AssetManager AOSP-real) |
| CR-X+1 lifecycle drive (Activity.onStart/onResume) | ~150 | Real ClientTransaction + `handleLaunchActivity` via `AbilitySchedulerBridge` | CR-EE §5, §7 |
| CR-Y+1 Locale.forLanguageTag BCP patch | ~100 | Real AOSP libcore | CR-EE §3 (libcore ships unmodified) |
| CR-Z Date/ByteOrder `<clinit>` fixes | ~80 | Real AOSP libcore | same |
| `SoftwareCanvas extends android.graphics.Canvas` | ~200 | Real libhwui + real Skia | CR-EE §6, CR-FF Pattern 4 |
| `drm_inproc_bridge.c` (direct `/dev/fb0` blit) | ~300 | HBC's `ANativeWindow → OHNativeWindow shim → IBufferProducer → RS BufferQueue` | CR-EE §6 |
| `aosp-libbinder-port/` for OHOS arm32 (M10) | already-built | HBC's `liboh_adapter_bridge.so` linking real OH `libipc_core` + `samgr_proxy` (innerAPI) | CR-FF §"CR61-equivalent finding" |
| Westlake's own `servicemanager` on `/dev/vndbinder` for OHOS | ~2K | HBC's 5 forward bridges + 6 reverse bridges to real OH services | CR-EE §1.3 |
| `aosp-audio-daemon-port/` for OHOS arm32 (M5/M11) | ~5K | HBC's `liboh_adapter_bridge.so` audio path via OH `audio_renderer` | CR-FF Pattern 1 |
| `aosp-surface-daemon-port/` for OHOS arm32 (M6/M12) | ~8K | HBC's `OHGraphicBufferProducer` + `RSSurfaceNode` path | CR-EE §6, §7 |
| `OhosMvpLauncher` driver | ~600 | HBC's `aa start <bundle>` → AMS → `AbilitySchedulerAdapter` → ActivityThread | CR-EE §5 |
| `InProcessAppLauncher` buffer materialization | ~100 | Real hwui RenderThread + EGL eglSwapBuffers | CR-EE §6 |

**Aggregate deletion: ~70K+ LOC of substrate work + ~16K LOC of native daemon work** removed from Westlake's V3 ownership. That work isn't "thrown away" in the sense of being wasted — it was the learning that led to the diagnosis that the layer was wrong. But it doesn't ship under V3.

**What does NOT get deleted under V3:**

| Component | Why kept |
|---|---|
| All V2 Phase-1 Android-phone code (`westlake-host-gradle`, in-process Option 3, McD/noice integrations) | The phone path stays. V3 only affects the OHOS-target path. |
| V2 Java substrate code where it runs on Android phone (cfb7c9e3) | Same — Android side unchanged. |
| M5 audio daemon + M6 surface daemon for ANDROID phone path | Useful for phone V2 path. Only the OHOS targets of these daemons are deleted. |
| CR60 bitness discipline (32-bit ARM userspace, intptr_t/size_t, dual-arch CI) | Inherited prerequisite — HBC's stack is also 32-bit ARM on DAYU200 (CR60 §"Empirical evidence"). |
| Macro-shim contract | Still applies. Narrower scope under V3 — at integration seams between Westlake engine and HBC runtime, not at framework-class level (which is now real AOSP). |
| Memory + handoff system | Updated to point to V3 (W10). |
| Test infrastructure (`scripts/run-ohos-test.sh` and equivalents) | Adapted to HBC artifact paths (W2). |
| `aosp-libbinder-port/` (musl + bionic builds for **Android-phone V2 path**) | UNCHANGED for Phase 1. Only the OHOS arm32 target is unused (HBC handles OHOS IPC). |

---

## 5. What V3 borrows from HBC (with citations)

This section lists every concrete artifact / pattern V3 reuses. Each row cites either CR-EE or CR-FF.

### 5.1 Build artifacts (binary reuse, no fork)

| Artifact | Approximate size | Source | Citation |
|---|---|---|---|
| `libart.so` | ~15 MB | HBC `~/adapter/out/aosp_lib/libart.so` | CR-EE §3, App. B |
| `libhwui.so` | ~1.85 MB | HBC `~/adapter/out/aosp_lib/libhwui.so` | CR-EE §3 |
| `libandroid_runtime.so` (replaced by `liboh_android_runtime.so`) | ~variable | HBC `~/adapter/out/aosp_lib/` | CR-EE §3, with caveat at §12-item-3 about stub register_* fakes |
| 38 cross-built AOSP native .so total | ~80 MB total | HBC `~/adapter/out/aosp_lib/` | CR-EE App. B |
| 11 framework jars (`framework.jar`, `core-oj.jar`, `core-libart.jar`, `core-icu4j.jar`, `okhttp.jar`, `bouncycastle.jar`, `apache-xml.jar`, `framework-res.apk`, `oh-adapter-{framework,runtime}.jar`, `adapter-mainline-stubs.jar`) | ~80 MB | HBC `~/adapter/out/aosp_fwk/` | CR-EE §3 |
| 9-segment dex2oat-baked boot image | ~variable, 27 files | HBC `~/adapter/out/boot-image/` | CR-EE §3 |
| `libbionic_compat.so` | small | HBC `~/adapter/framework/appspawn-x/bionic_compat/` | CR-EE §3 |
| `appspawn-x` binary | small | HBC `~/adapter/out/adapter/appspawn-x` | CR-EE §2 |
| `liboh_skia_rtti_shim.so` | 12.6 KB | HBC `~/adapter/framework/surface/jni/skia_rtti_shim/` | CR-EE §6, CR-FF Pattern 4 |
| 7 patched OH service .so (`libwms.z.so`, `libabilityms.z.so`, `libappms.z.so`, `libbms.z.so`, `librender_service{,_base}.z.so`, `libscene_session_manager.z.so`) | varies | HBC `~/adapter/out/oh-service/` | CR-EE §4 |
| Adapter Java + JNI source (12K+ LOC) | source | HBC `~/adapter/framework/` | CR-EE §2 |

### 5.2 Architectural patterns (concept reuse)

| Pattern | What Westlake adopts | Citation |
|---|---|---|
| 4 L5 framework patches via `OHEnvironment.getXxxAdapter()` | Adopted as-is — Westlake doesn't author them | CR-FF Pattern 1 |
| `OHEnvironment` + dual-classloader (BCP holds only `OHEnvironment`; real adapter classes in `oh-adapter-runtime.jar` loaded via system PathClassLoader) | Adopted as-is; adapter iteration costs **0 boot-image rebuild** | CR-FF Pattern 2 |
| Reverse-bridge sandwich (C++ IRemoteStub → JNI → Java Bridge) | Adopted for any new Westlake-side service callback | CR-FF Pattern 3 |
| `ScopedJniAttach` RAII | Mandatory in all V3 JNI helper code | CR-FF Pattern 3 final paragraph; CR-EE §7 |
| `liboh_skia_rtti_shim.so` audit methodology (`build/discover_skia_rtti_syms.sh`) | Generic "ABI gap, no semantic gap" technique — reusable for any future symbol gap | CR-FF Pattern 4 |
| `DEPLOY_SOP.md` staging discipline (never `hdc send <src> /system/...`; always `send → /data/local/tmp/stage/`, verify, then `cp`) | Adopted; W9 ports the SOP into our deploy scripts | CR-EE §9, CR-FF "Pattern 5" |
| Single-command recovery rule (`bash restore_after_sync.sh && <build>` reproduces last-green from clean `repo sync`) | Adopted as `bash scripts/restore-v3-state.sh` | CR-EE §9 |
| "Blame adapter first" RCA discipline | Adopted at integration seams | CR-EE §11.7 |
| `Handler(Looper.getMainLooper()).post(...)` thread-switch at JNI seam | Adopted (already a Westlake lesson per CR59) | CR-EE §7 |
| APK transparency invariant + anti-grep CI check (`grep -r "import adapter" <APK source>` returns empty) | Adopted at W6/W7 acceptance | CR-EE §11.1 |
| innerAPI > NDK rule | Adopted as CR61.1 amendment | CR-FF §"CR61-equivalent finding" |

### 5.3 Anti-patterns explicitly NOT borrowed

Per CR-EE §12 / CR-FF §"TL;DR bullet 5":

- The "rebuild AOSP-14 native + Java + boot image against musl" work itself — Westlake reuses the **outputs**, does not maintain the source patches or build pipeline
- The 40+ orphan in-place python `sed`-scripts for libhwui (HBC's own admitted anti-pattern, classified as remediation-needed; Westlake never replicates)
- The "mostly-stub `liboh_android_runtime.so`" pattern (13/15 register_* modules are stubs) — V3 acceptance gates require Westlake-side validation that the apps we host actually need only the AOSP-real subset HBC has wired up
- Patching OH `init` `MAX_ENV_VALUE` — workaround via `setenv()` in app-hosting engine entrypoint instead
- Dependence on legacy WMS as the only window path (DAYU200 ships SCB off, but V3 plans for SCB enablement via W8)
- `IActivityClientController` Java-Proxy stub-everything pattern — hides semantic gaps; Westlake's "drive lifecycle to Resumed using real method invocations" remains the contract

---

## 6. Migration path from V2 to V3

### 6.1 What changes on day 0 (when V3 lands)

- This document + `V3-WORKSTREAMS.md` + `V3-SUPERVISION-PLAN.md` + `CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md` committed
- `project_v2_ohos_direction.md` memory annotated SUPERSEDED-BY V3
- `CR41_PHASE2_OHOS_ROADMAP.md` annotated SUPERSEDED-BY V3 for the OHOS path; V1/V2 historical content preserved verbatim
- `CR61_BINDER_STRATEGY_POST_CR60.md` annotated AMENDED-BY `CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md` for V3 path
- GitHub issues opened for W1-W13

### 6.2 What changes during W1 (HBC artifact inventory + pull)

- HBC's binary artifacts pulled into Westlake repo under `third_party/hbc-runtime/` (or similar)
- Westlake's own `dalvik-port/` OHOS targets archived (NOT deleted — moved under `archive/v2-ohos-substrate/`)
- `aosp-shim-ohos.dex` archived

### 6.3 What changes during W2-W5

- V3 boots HBC runtime standalone on DAYU200 (W2)
- Westlake's app-hosting engine integration replaces `OhosMvpLauncher` (W3)
- Adapter customization for Westlake-specific scope (W4)
- Mock APK validation (W5)

### 6.4 What changes during W6-W7

- noice on OHOS via V3 (W6) — directly comparable to V2 in-process Option 3 result
- McD on OHOS via V3 (W7)

### 6.5 What does NOT change

- All Phase-1 Android-phone work remains in-place and is the production path on Android
- `westlake-host-gradle` remains the host APK
- macro-shim contract still applies (narrower scope: integration seams only — see §7)
- CR60 bitness discipline still applies (V3 stack is 32-bit ARM on DAYU200)
- Memory + handoff doc cadence unchanged

---

## 7. Macro-shim contract — V3 scope

The contract from `feedback_macro_shim_contract.md` **still applies**, with the scope adjusted:

**V2 scope:** Westlake-owned classes covering nearly the entire AOSP framework surface (Application/Activity/Resources/Window/PhoneWindow/DecorView/WindowManagerImpl + 7 service classes).

**V3 scope:** Westlake-owned classes covering ONLY the app-hosting engine surface. Real `framework.jar` from HBC means we don't own (and must not shim) any framework class. The contract applies at the **integration seam** between our engine and HBC's runtime.

**Permitted under V3 (macro shim):**
- Implementing public/protected API methods on Westlake-owned classes:
  - App-hosting engine entrypoints
  - Intent rewriting (e.g., Instrumentation subclass)
  - Lifecycle orchestration callbacks
- Method bodies must be one of:
  - (a) AOSP-default verbatim (delegated through HBC's adapter to real OH service)
  - (b) Safe primitive (return null / false / 0 / empty list / no-op)
  - (c) Delegation to another method on our own class

**Forbidden under V3 (micro shim / drift):**
- `sun.misc.Unsafe.allocateInstance(...)` on any framework class (HBC or AOSP)
- `Field.setAccessible(true)` + reflective set on framework internals (HBC adapter internals OR real `framework.jar` internals)
- Planting state on `LoadedApk` / `ContextImpl` / `ActivityThread` / `Theme` / `Configuration` / `AssetManager`
- Per-app branches (`if (pkg.equals("com.mcdonalds.app"))`) — **same rule as before, no exceptions**
- Catching `NoSuchMethodError` / `LinkageError` from framework class reflection
- Modifying HBC's adapter Java sources to add Westlake-specific logic (open a HBC-side bug or copy the pattern to Westlake-owned code instead)

**Self-audit gate (run before V3 CR completes):**

```bash
# Zero new Unsafe usage on touched files
grep -rn "sun.misc.Unsafe\|jdk.internal.misc.Unsafe\|Unsafe.allocateInstance" <touched files> | grep -v "^.*://"
# Zero new setAccessible
grep -rn "setAccessible(true)" <touched files> | grep -v "^.*://"
# Zero new per-app branches
grep -rniE "noice|mcdonalds|com\.mcd|noice\.fragment" <touched files> | grep -v "^.*://\|^.*// "
# Zero edits to HBC adapter sources
git diff --name-only HEAD~1 | grep -E "^third_party/hbc-runtime/" && echo "FAIL: edited HBC source"
```

If any of the above produce new results, STOP and revert.

---

## 8. Anti-patterns to recognize in V3 code review

Drawn from `feedback_additive_shim_vs_architectural_pivot.md`, with V3-specific examples:

1. **"Just one more shim and we're there"** — if a V3 CR is the 3rd in a row in the same integration seam (e.g., 3 CRs all in intent rewriting), audit the layer instead of writing CR #4.

2. **"Technical PASS but semantic FAIL"** — a V3 CR that lands cleanly but the visible result (noice's panel pixel; McD's Wi-Fry screen) hasn't moved is suspect. Check whether the test criteria are checking the right things.

3. **"Hand-roll X when AOSP/HBC ships a real X"** — if a V3 task tempts you to write a custom `Window` or `Canvas` or `View`, stop. Real AOSP `framework.jar` is in HBC's BCP; use it.

4. **"Forking HBC to add a method"** — if a V3 task tempts you to edit `~/adapter/framework/.../*.java`, stop. Either (a) propose the change to HBC, (b) shadow the class in Westlake's `oh-adapter-runtime.jar` (loadable via PathClassLoader per CR-FF Pattern 2), or (c) document the blocker and STOP. Direct edits to HBC source are a one-way door to drift.

5. **"Per-app code branch"** — same rule as V2; same self-audit grep.

6. **"V2-shaped fix in V3 layer"** — e.g., porting a SoftwareCanvas-style work-around into V3 is a sign that the V3 path is being misunderstood. Stop and re-read this doc § 4.

---

## 9. Self-audit checklist for V3 code

Every V3 CR's report must include:

- [ ] **Layer ownership:** "This change lives in the Westlake app-hosting engine layer, not the HBC runtime, not OHOS platform." (cite file paths)
- [ ] **No HBC fork:** "Zero edits to `third_party/hbc-runtime/`."
- [ ] **No framework shim:** "Zero new classes shadowing `android.*` framework classes."
- [ ] **No Unsafe / setAccessible / per-app branches.** Three greps clean.
- [ ] **Macro contract honored at integration seam:** "All my methods live on Westlake-owned classes; bodies are (a)/(b)/(c) per §7."
- [ ] **APK transparency preserved:** "Zero `import adapter.*` (HBC) or `import com.westlake.*` (us) in any APK source under test."
- [ ] **Lifecycle gate:** "If lifecycle drive was involved, real `Handler(Looper.getMainLooper()).post(...)` was used at the JNI seam (CR59 lesson)."

---

## 10. Cross-references

- `CR-EE-HANBINGCHEN-ARCHITECTURE-ANALYSIS.md` — structural overview of HBC (the runtime V3 adopts)
- `CR-FF-HBC-BORROWABLE-PATTERNS.md` — tactical patterns inherited by V3
- `CR-DD-CANDIDATE-C-VS-V2-OHOS-RECONSIDERED.md` — the analysis that triggered today's pivot
- `CR60_BITNESS_PIVOT_DECISION.md` — 32-bit-ARM-on-DAYU200 prerequisite (preserved)
- `CR61_BINDER_STRATEGY_POST_CR60.md` — original CR61 (CR61.1 amends for V3 path)
- `CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md` — amendment legitimizing libipc/samgr via HBC adapter
- `BINDER_PIVOT_ARCHITECTURE.md` — V2 consolidated architecture (Phase-1 Android, STAYS)
- `V3-WORKSTREAMS.md` — W1-W13 work breakdown
- `V3-SUPERVISION-PLAN.md` — dispatch order
- `feedback_additive_shim_vs_architectural_pivot.md` — the lesson V3 acts on
- `feedback_macro_shim_contract.md` — contract still applies, narrower scope under V3
- `artifacts/ohos-mvp/multi-hap-peer-window-spike/20260515_181930/CHECKPOINT.md` — SceneBoard-off finding on DAYU200 (drives W8 scope)
- Memory: `project_v2_ohos_direction.md` — SUPERSEDED BY V3
- Memory: `project_noice_inprocess_breakthrough.md` — Phase-1 Android baseline (unchanged)

---

## Findings 2026-05-19

Three findings landed today that refine V3 without changing the architecture itself.
Recorded here so future readers don't have to re-derive the rationale. Companion docs:
`WESTLAKE-ISLAND-BORROW-MAP.md`, `03-REQUIREMENT-INDEX.md`, updated
`V3-WORKSTREAMS.md` (W4 split + W6-prep + W6-perf), updated `V3-SUPERVISION-PLAN.md`
(DAG + G3.5 / G4.5a / G4.5b gates + risk #11 + #12).

### Engine layer rationale (why V3 needs Westlake-owned code above HBC)

A reasonable challenge: *if we inherit HBC's real `framework.jar` + appspawn-x + 530
AIDL adapter methods + real PMS, why does Westlake need an engine layer at all?*

- **Inheriting substrate ≠ launching arbitrary APKs.** HBC's stack proves a single
  test APK (HelloWorld) reaches `MainActivity.onCreate` line 83. Generalizing to
  arbitrary third-party APKs is a separate problem: per-APK identity (bundle id /
  uid / data dir), Hilt bootstrap, lifecycle drive past `performResume`,
  SystemService routing for services HBC doesn't bridge, cross-package intent
  rewriting, regression matrix.
- **V2-Phone empirically proved real apps need this work even with full real
  `framework.jar` on the host's native BCP.** The 5-pillar pattern
  (`project_noice_inprocess_breakthrough.md`) — hidden-API bypass + LoadedApk dir
  redirect + safe-context bind stub + LocaleManager binder hook + lifecycle drive
  to Resumed — was not optional for noice + McD; it was load-bearing. CR58 / CR59
  / M4-PRE11 / M4-PRE12 / PF-inproc-002 each landed because something past
  `Application.onCreate` failed without it. HBC's substrate is more complete than
  V2-Phone's (real appspawn-x, real PMS) but is not magically *complete enough*
  to skip every one of those pillars; that has to be measured.
- **Per-app boundary work has nowhere else to live.** HBC's APK transparency
  invariant (zero `import adapter.*` in any APK source) forbids putting per-app
  cooperation in the APK. HBC's "blame adapter first" rule + no-source-edits
  redline forbid us editing HBC's adapter for Westlake-specific cases. The only
  place left is a Westlake-owned engine layer above HBC, below the unmodified APK.
- **Product goals require engine-level coordination.** Composable peer windows
  (product goal #1) need a Westlake-side multi-app coordinator deciding how 2
  hosted apps map onto OHOS WindowManager. FPS instrumentation (#2) needs a
  Westlake-side IDisplayEventConnection tap. Neither is HBC's job.
- **Honest caveat.** The *size* of the engine work is currently assumed, not
  measured. `W4-empty` (new 2026-05-19) is the spike that turns assumption into
  evidence — pure HBC, engine = empty, see how far noice / McD actually reach.
  If both reach first frame, W4-engine collapses to ~2 PD (just port the 5
  Island borrows + 03-Req top-10 routing). If both crash early, W4-engine grows
  to 5-8 PD with explicit V2-Phone substrate-port candidates. Either way, the
  engine layer itself remains necessary — only its bulk is to be sized.

### Island borrow scope (top-5; what we adopt, what we explicitly don't)

Per `WESTLAKE-ISLAND-BORROW-MAP.md` (commit `9705487c`) — verdict: stay V3
HBC-reuse, borrow these 5 operational + IBinder-facade-shaped patterns. NOT
borrowing Island's substrate.

1. **`NeverDieAdapterDecorator`** — `InvocationHandler` over HBC's adapter
   proxies, catching `InvocationTargetException` → AOSP-default for the method's
   return type. Lands in W4-engine. ~½ day.
2. **`LifecycleDriver`** — generic helper extracting `flushViewRunQueue` +
   `drainPendingMessages` from V2-Phone's per-app `*InProcessActivity.kt` into a
   Westlake-owned class. Lands in W4-engine. 1 day. Caveat: reflection on
   framework internals — prefer requesting an HBC-side API exposure first.
3. **`SystemServiceRouter`** — Westlake-owned class wrapping
   `HBC.getXxxAdapter()` with the real-ctor + safe-stub matrix from Island's
   `StubContext.getSystemService`, drives 03-Req top-10 routing. NO `Unsafe`, NO
   `setAccessible` (V3 redlines). Lands in W4-engine. 1-2 days.
4. **`scripts/v3-smoke.sh`** — 77-demo regression methodology adapted for V3 (mock
   APK suite + noice + McD via `aa start`). Lands in W9. 1-2 days.
5. **`docs/engine/V3-REDLINES.md`** + 5 grep CI scripts under
   `scripts/v3-redline-ci/` — mechanical audit cadence for §7 macro-shim contract.
   Lands in W9. ½ day.

**Explicitly NOT borrowing:**

- **Island's process-isolation architecture.** Island runs a fully process-isolated
  Bionic+chroot ART against a Musl daemon over Neutral Protocol on AF_UNIX. V3 is
  single-process HBC-reuse on real AOSP-14 ART cross-built to OHOS musl via
  `libbionic_compat.so`. The architectures are not substitutable — Island's
  redline ("Bionic and Musl must never share a process") is the architectural
  opposite of V3's premise.
- **Island's PNG-files-then-blit-to-fb0 display path.** Island demos render
  480×800 PNGs and blit directly to `/dev/graphics/fb0` after killing
  render_service. V3 expects real `render_service` / `IBufferProducer` integration
  via HBC's `ANativeWindow → OHGraphicBufferProducer` bridge — same path real
  OHOS apps use. The product goal of composable peer windows alongside ArkTS apps
  via real WindowManager is incompatible with the fb0-takeover model.

The 5 borrows are tactical, not architectural. The non-borrow list is the architectural
redline that distinguishes V3 from a "should we go Island instead?" pivot.

### Service coverage gap (W7 prereq, surfaced by 3-way comparison)

Per `WESTLAKE-ISLAND-BORROW-MAP.md` §4 "3-way service coverage table" and
`03-REQUIREMENT-INDEX.md`, three McD-critical service packages have **zero runtime
evidence** across HBC / Island / 03-Req today:

- `android.location` (`LocationManager`) — McD store-finder
- `android.hardware.camera2` — McD QR scanner
- `android.security.keystore` — McD biometric login

This becomes a **W7 prerequisite** (see `V3-WORKSTREAMS.md` W7 §"W7-prereq"). Disposition for
each row is one of: safe-stub via NeverDieAdapterDecorator (cheap, may not be
enough), JAVA_SYNTH implementation (engine-layer work), or HBC-upstream-request
(forward-bridge in HBC's adapter). If none of those work for camera2, the
QR-scanner-dependent McD flows fall out of scope for W7 acceptance and become
a follow-on CR.

This gap also surfaces a longer-tail concern: the 03-Req corpus's 50 packages
are 48/50 `DISCOVERY_REQUIRED` today; the V7 schema is filled only for `wifi`
and `app.admin`. Westlake's V3 cannot wait for 03-Req to catch up. The
`SystemServiceRouter` (Island borrow #3) is what fills the gap incrementally,
one row at a time, with each row's evidence captured in
`docs/engine/V3-SYSTEMSERVICE-ROUTING.md`.
