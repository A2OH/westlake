# CR-EE — user Adapter Architecture Analysis

> Read-only architectural analysis of HBC's `~/adapter/` work tree on the
> shared GZ05 server ([REDACTED-IP]:[REDACTED-PORT] user). Conducted 2026-05-15 by
> agent 40. Source citations are absolute paths on that server unless prefixed
> `~/` for HBC's home. Local cached copies live at `/tmp/hbc-analysis/`.

---

## TL;DR (5 bullets)

1. **HBC is running real, unmodified AOSP-14 ART + framework.jar on top of OHOS
   7.0.0.18 (rk3568, 32-bit)**, not a Java substrate or HAP container. The
   centerpiece is `appspawn-x` — a hybrid Zygote/AppSpawn daemon at
   `/system/bin/appspawn-x` that boots libart, runs `dex2oat`-baked boot image
   segments (boot.art + 8 module segments), then forks per-APK child processes
   exactly like Android's Zygote. Verified: `~/adapter/framework/appspawn-x/src/main.cpp`,
   `~/adapter/framework/appspawn-x/config/appspawn_x.cfg`,
   `~/adapter/out/boot-image/boot.art` etc.

2. **APK transparency is the project's hard invariant** — no `import adapter.*`,
   no rebuild — enforced via 4 surgical L5 patches to AOSP framework Java
   (`ActivityManager.java`, `ActivityTaskManager.java`, `ActivityThread.java`,
   `WindowManagerGlobal.java`) that delegate `IXxx.getService()` to
   `OHEnvironment.getXxxAdapter()` reflectively (`~/adapter/aosp_patches/frameworks/core/java/android/...`,
   `~/adapter/framework/core/java/OHEnvironment.java`). Every other piece of
   compatibility lives outside the APK in `framework.jar` patches + native
   shims.

3. **5 forward bridges (`extends IXxx.Stub`) + 6 reverse callback bridges
   (C++ `IRemoteStub` → JNI → Java)** route **530+ Android AIDL methods** to OH
   `inner_api` services (`AbilityManagerService`, `AppMgrService`,
   `SceneSessionManager`, `BundleManagerService`, `RenderService`,
   `CommonEventService`, `DataShareService`). Forward bridges are
   compile-time-checked Stub subclasses, not dynamic proxies; each holds its
   own `long mOhXxxHandle` and declares its own `native` methods. Code lives
   under `~/adapter/framework/{activity,window,surface,broadcast,contentprovider,package-manager}/{java,jni}/`.

4. **Graphics path is "keep all of AOSP libhwui + EGL + Skia, replace
   ANativeWindow underneath"** — they cross-compile real `libhwui.so`
   (~1.85 MB), real `libart.so` (~15 MB), real `libandroid_runtime.so` and 38
   other AOSP native libs to musl/OH. Skia is *shared with OH* via the system's
   own `libskia_canvaskit.z.so` + a tiny `liboh_skia_rtti_shim.so` (12.6 KB,
   18 RTTI symbols) instead of rebuilding Skia. ANativeWindow is bridged to
   OH's `IBufferProducer` via `~/adapter/framework/window/jni/oh_anativewindow_shim.cpp`
   (708 LOC) + `~/adapter/framework/surface/jni/oh_graphic_buffer_producer.cpp`
   (396 LOC). Direction confirmed: each App process holds its own EGL/Skia
   context and pushes buffers to RenderService through `OHNativeWindow` →
   `IBufferProducer`. **DAYU200 build is `window_manager_use_sceneboard=false`**
   (legacy WMS, not SCB).

5. **Status (per `~/adapter/doc/overall_design.html` §0.4 r26 / `compile_report.html`
   appendix P14)**: HelloWorld APK gets all the way to **`MainActivity.onCreate`
   line 83 (TextView ctor)**, with real `handleBindApplication`, real
   `Application` instance, real `PhoneWindow.<init>`, real `Activity.attach`.
   Surface/window-creation chain reaches `addToDisplayAsUser ADD_OKAY=0` and
   `activityResumed (state=9) rc=0`. Current blocker is an `IllegalStateException`
   in `findMode(1)` because `DisplayInfo.supportedModes` is empty —
   architectural ground is solid; the gap is field-mapping completeness.

---

## 1. Approach overview

HBC's architecture is **fundamentally different** from anything Westlake has
attempted. It is closest in spirit to Anbox / Waydroid (run real Android
runtime side-by-side with the host OS) but **without** a kernel container —
pure userspace, single OH device, ART runs in a peer process to OH ability
processes. There is **no in-process java substrate**, **no HAP container**,
**no V2-style "own libbinder"** — instead, both Android and OHOS Binder stacks
exist independently, and 5 Java Stub subclasses transcode at the IPC seam.

**Three independent runtimes coexist on one OH device:**

| Runtime | Process | Purpose |
|---|---|---|
| OHOS native | `foundation`, `render_service`, `composer_host`, `samgr`, `sceneboard`/launcher, `window_manager_service` | Standard OH services & apps |
| ART (Android) | `appspawn-x` (parent) + per-APK forked children | Hosts Android apps via real ART + real framework.jar |
| Adapter glue | `liboh_adapter_bridge.so` loaded into ART child + `liboh_android_runtime.so` (replaces `libandroid_runtime.so`) | JNI thunks routing Android Binder calls → OH `inner_api` C++ |

**Process model (verified `~/adapter/framework/appspawn-x/src/main.cpp:135-311`):**

```
init.cfg → /system/bin/appspawn-x  (uid=root, secon=u:r:appspawn:s0, ondemand)
   ├─ Phase 1: SpawnServer.initSecurity (OH sandbox/SELinux/AccessToken)
   ├─ Phase 2: AppSpawnXRuntime.startVm  → JNI_CreateJavaVM(libart.so)
   │           → BCP load (8 segments incl. framework.jar + adapter-mainline-stubs + oh-adapter-framework)
   ├─ Phase 3: AppSpawnXInit.preload + libjavacore JNI_OnLoad
   │           (preloads ~30 framework classes; injects ServiceManager.sServiceManager,
   │            IActivityClientController, IContentProvider Java Proxies)
   ├─ Phase 4: listen on /dev/unix/socket/AppSpawnX
   └─ on spawn-msg from `aa start`:
       fork() → child:
          - OH child specialization (DAC/setresuid/setSchedPolicy/SELinux setcon/AccessToken)
          - ChildMain::run → ActivityThread.main() → Looper.loop()
          - ATM/AM/PM/WM Adapters reflectively replace AOSP IXxx singletons
```

**Key invariant** (cited verbatim from `~/adapter/CLAUDE.md:107`): *"ART VM
must boot from boot image (`-Ximage:/system/android/framework/arm/boot.art`) —
`-Xbootclasspath` alone triggers ClassLinker deep recursion stack overflow."*

---

## 2. `adapter/` — core working directory

```
~/adapter/
├── framework/         # OH-side adapter Java + JNI (12K+ LOC)
│   ├── core/{java,jni}/        OHEnvironment + adapter_bridge.cpp
│   ├── activity/{java,jni}/    ActivityManagerAdapter (2309 LOC), ATM, lifecycle
│   ├── window/{java,jni}/      WindowManagerAdapter, WindowSession, Display, Input
│   ├── surface/jni/            oh_graphic_buffer_producer.cpp, skia_rtti_shim/
│   ├── broadcast/{java,jni}/   CommonEvent ↔ Android BroadcastReceiver
│   ├── contentprovider/        DataShare ↔ ContentProvider
│   ├── package-manager/        BMS ↔ PMS, APK install path (libapk_installer.so)
│   ├── appspawn-x/             Hybrid spawner (own executable, OH ohos_executable)
│   │   ├── src/                main.cpp, AndroidRuntime.cpp (427 LOC), spawn_server, child_main
│   │   ├── bionic_compat/      musl ↔ bionic compat shim (libbionic_compat.so)
│   │   ├── config/             appspawn_x.cfg (init service def) + sandbox.json
│   │   └── BUILD.gn
│   ├── android-runtime/        liboh_android_runtime.so  ← replaces AOSP libandroid_runtime.so
│   │   └── src/                30+ register_android_X.cpp (mix of AOSP-real + stub)
│   ├── hwui-shim/jni/          NDK ANativeWindow + minikin + AHB + typeface shims
│   └── jni/                    BUILD.gn for liboh_adapter_bridge.so
│
├── aosp_patches/      # AOSP-14 source patches (mirrors AOSP tree)
│   ├── frameworks/core/java/android/{app,view}/   <- 4 L5 patches (§3 below)
│   ├── frameworks/base/{core/jni,libs/{androidfw,hwui}}/  <- native patches
│   ├── art/runtime/gc/collector/mark_compact.cc.patch
│   ├── data/fonts/fonts.xml   minimal AOSP fontconfig referencing OH HarmonyOS_Sans
│   └── replacement_headers/   sys/pidfd.h, meminfo/* for cross-build
│
├── ohos_patches/      # OH source patches (mirrors OH tree)
│   ├── appspawn/                apply_appspawnx_routing.py + sepolicy + namespace
│   ├── bundle_framework/        APK install path patches (~10 .py)
│   ├── graphic_2d/              G2.14au_r5 RS probes
│   ├── graphic_surface/surface/ buffer_queue.cpp.patch
│   ├── selinux_adapter/         file_contexts.patch (adds appspawn:s0, system_lib_file)
│   ├── third_party/musl/config/ ld-musl-namespace-arm.ini  (adds /system/android/lib)
│   ├── third_party/skia/m133/   diagnostic probe scripts (no behavioral changes)
│   ├── init/                    apply_init_max_env_value.py (workaround for bug Z.1)
│   └── vendor/hihope/rk3568/config.json (product config)
│
├── app/               # HelloWorld test APK source (PURE Android, builds on
│                      # local Android Studio — not on ECS)
│   ├── AndroidManifest.xml      Theme.Material.Light + ic_dialog_info icon
│   └── java/com/example/helloworld/{MainActivity,SecondActivity,HelloService,HelloWorldApplication}.java
│
├── build/             # Cross-compile + GN-args + skia_compat_headers + restore_after_sync.sh
├── deploy/            # deploy_to_dayu200.sh (47KB) + DEPLOY_SOP.md (12KB) — 94 files + 4 symlinks per push
├── doc/               # 30+ HTML design docs (Chinese), see §10
├── out/               # ECS-only build artifacts
│   ├── adapter/       liboh_adapter_bridge.so, liboh_android_runtime.so, liboh_hwui_shim.so,
│   │                  liboh_skia_rtti_shim.so, libapk_installer.so, appspawn-x, *.jar
│   ├── aosp_lib/      38 cross-built AOSP .so (libart, libhwui, libandroidfw, libminikin,
│   │                  libicuuc, libnativehelper, libbase, libcutils, libutils, libft2, etc.)
│   ├── aosp_fwk/      11 framework .jar (framework, core-{oj,libart,icu4j}, okhttp,
│   │                  bouncycastle, apache-xml, framework-res, oh-adapter-{framework,runtime})
│   ├── boot-image/    9-segment dex2oat-AOT boot image (boot.art/oat/vdex × 9)
│   └── oh-service/    7 patched OH service .so (libwms, libabilityms, libappms, libbms,
│                      librender_service{,_base}, libscene_session_manager)
└── CLAUDE.md (30KB project agent contract), readme.txt
```

**Build system** is *not* unified GN/Soong:

- AOSP-side: AOSP Soong (`cd ~/aosp && ./auto_build.sh`) builds Java/jar/APK + host-tools (dex2oat) + cross-compiled AOSP native .so
- OH-side: OH `build.sh --product-name rk3568 --gn-args allow_sanitize_debug=true` builds adapter native + patched OH services
- **Glue scripts** (`~/adapter/build/cross_compile_arm32.sh`, `compile_libhwui.sh`, `compile_skia_rtti_shim.sh`, etc.) bypass GN cross-component isolation by invoking OH clang directly
- See `~/adapter/CLAUDE.md` §"Build Division Principle" line 254 — explicit rule: AOSP Soong owns Java + cross-compiled AOSP native; OH GN owns adapter native + patched OH services

**Dynamic library deps** (verified): `liboh_adapter_bridge.so` links OH
`ipc_core` + `samgr_proxy` + ability_runtime inner_api + `libwms` + `librender_service_client` —
the adapter native side is a **first-class OH platformsdk consumer**, not a
HAP-style NDK consumer. Discipline doc: `~/adapter/doc/technical_decision_overview.html`
§2.10 *"OH 系统接口选型：innerAPI 优先于 NDK"*.

---

## 3. AOSP source — what's modified

`~/aosp/` is a **near-stock AOSP-14 (`android-14.0.0_r1`)** repo with custom
product `oh_adapter`. Confirmed: `~/aosp/.repo/` exists, `~/aosp/build/`,
`~/aosp/art/`, `~/aosp/frameworks/`, `~/aosp/external/`, `~/aosp/libcore/` all
present (~37 dirs).

**Patches applied (10 files, all in `~/adapter/aosp_patches/`):**

| Layer | File | Purpose |
|---|---|---|
| L5 Java | `frameworks/core/java/android/app/ActivityManager.java.patch` | `IActivityManagerSingleton.create()` → `OHEnvironment.getActivityManagerAdapter()` (reflective) |
| L5 Java | `frameworks/core/java/android/app/ActivityTaskManager.java.patch` | Same pattern |
| L5 Java | `frameworks/core/java/android/app/ActivityThread.java.patch` | `getPackageManager()` → adapter |
| L5 Java | `frameworks/core/java/android/view/WindowManagerGlobal.java.patch` | `getWindowManagerService()` + `getWindowSession()` → adapters |
| Native | `frameworks/base/core/jni/AndroidRuntime.cpp.patch` | Adapter init hook in `register_jni_procs` |
| Native | `frameworks/base/core/jni/android_util_Process.cpp.patch` | Bionic→musl process API shim |
| Native | `frameworks/base/libs/androidfw/{Asset,AssetManager2}.cpp.patch` + `.h.patch` | musl path + cgroup compat |
| Native | `frameworks/base/libs/hwui/jni/graphics_jni_helpers.h.patch` | Skia M116→M133 ABI shim |
| ART | `art/runtime/gc/collector/mark_compact.cc.patch` | musl mremap behavior fix |

**Key insight on L5 pattern** (`~/adapter/framework/core/java/OHEnvironment.java:137-173`):

```java
private static Object newAdapterReflective(String className) {
    // Path A — system classloader (oh-adapter-runtime.jar, non-BCP)
    // Path B — BCP classloader fallback (oh-adapter-framework.jar)
    try {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        if (cl != null && cl != OHEnvironment.class.getClassLoader()) {
            Class<?> cls = Class.forName(className, true, cl);
            return cls.getDeclaredConstructor().newInstance();
        }
    } catch (Throwable t) { firstErr = t; }
    try {
        Class<?> cls = Class.forName(className);
        return cls.getDeclaredConstructor().newInstance();
    } catch (Throwable t2) { ... return null; }
}
```

This dual-classloader scheme means **adapter code lives in non-BCP
`oh-adapter-runtime.jar` by default** (so iterative changes don't trigger a
multi-day boot-image rebuild), with BCP `oh-adapter-framework.jar` as fallback
— a strong engineering pattern (`~/adapter/doc/appspawn_x_design.html` v1.1
"调整 2").

**AOSP outputs that ship to device** (`~/adapter/out/aosp_lib/`):
38 .so files including `libart.so` (~15 MB), `libhwui.so` (~1.85 MB),
`libandroid_runtime.so`, `libandroidfw.so` (591 KB after expansion to 25 sources),
`libminikin.so`, `libnativehelper.so`, `libicuuc.so`, `libft2.so`,
`libopenjdk{,jvm}.so`, `libbase.so`, `libcutils.so`, `libutils.so`,
`libdexfile.so`, `libsigchain.so`, `libnativeloader.so`, `libnativebridge.so`,
`libprofile.so`, `libelffile.so`, `libartbase.so`, `libart-compiler.so`, plus
**`libbionic_compat.so`** (custom musl→bionic shim).

**Java jars** (`~/adapter/out/aosp_fwk/`): real `framework.jar`, `core-oj.jar`,
`core-libart.jar`, `core-icu4j.jar`, `okhttp.jar`, `bouncycastle.jar`,
`apache-xml.jar`, `framework-res.apk`. Total ~80 MB.

**Boot image** (`~/adapter/out/boot-image/`): full 9-segment dex2oat output —
`boot.{art,oat,vdex}` + `boot-{core-libart,core-icu4j,okhttp,bouncycastle,apache-xml,adapter-mainline-stubs,framework,oh-adapter-framework}.{art,oat,vdex}`.
Cross-built dex2oat lives in `~/adapter/out/host-tools/`.

---

## 4. OH source — what's patched

`~/oh/` is **OpenHarmony weekly_20260302 = OH 7.0.0.18**, product `rk3568`
(32-bit ARM). 25 top-level OH dirs present.

**`window_manager_use_sceneboard = false`** — DAYU200 runs **legacy WMS, not
SceneBoard**. From `~/adapter/doc/graphics_rendering_design.html` §1.1.2:

> 本工程 DAYU200 当前 build（OH 7.0.0.18，window_manager_use_sceneboard=false）
> 跑的是 legacy 形态

That dramatically simplifies the surface bridge — `WindowManagerAdapter` talks
to `IWindowManager(OH)` (legacy) via `CreateWindow / AddWindow / SetWindowId`,
not to `ISceneSessionManager`. They explicitly call out "future direction"
where they'd switch to SceneBoard SCB chain.

**OH patched modules (small, surgical):**

| Subsystem | Patch | Purpose |
|---|---|---|
| `appspawn` | `apply_appspawnx_routing.py` + `apply_appspawnx_sepolicy_and_namespace.py` | Make OH appspawn forward Android-bundled apps to appspawn-x socket |
| `bundle_framework` | 10 .py + `BUILD.gn.patch` + `bundle_installer.cpp.patch` + `adapter_apk_install_minimal.cpp` | BMS recognizes APK + dispatches to `libapk_installer.so` for AXML/dex2oat path |
| `graphic_surface/surface` | `buffer_queue.cpp.patch` | producer-side compat for adapter's OHGraphicBufferProducer |
| `graphic_2d` | `apply_G214au_r5_oh_rs_probes.py` | RS dirtylist + composer_host probes (diagnostic) |
| `selinux_adapter` | `file_contexts.patch` | adds `/system/bin/appspawn-x → appspawn_exec`, `/system/android/lib/* → system_lib_file` |
| `third_party/musl/config` | `ld-musl-namespace-arm.ini` | extends default ns to include `/system/android/lib` |
| `init` | `apply_init_max_env_value.py` | workaround for OH bug Z.1 (init env[] strcpy_s overflow when value ≥128 B) |
| `vendor/hihope/rk3568/config.json` | product config | adapter binaries in build target |
| `third_party/skia/m133/` | 9 `apply_*.py` probes | DIAGNOSTIC ONLY — no behavioral skia changes (per file names "probe", "revert") |

**OH services built with adapter changes** (`~/adapter/out/oh-service/`):
`libwms.z.so`, `libabilityms.z.so`, `libappms.z.so`, `libbms.z.so`,
`librender_service{,_base}.z.so`, `libscene_session_manager.z.so` — **mainly
to enable interface visibility for adapter, not to change OH semantics.**
Per `~/adapter/CLAUDE.md:299`: *"Boundary: prefer adapter-internal solutions;
modify OH source only when unavoidable. Current OH modifications confined to
ability_rt (5 files) + bundle_framework (4 files); Launcher/SystemUI/foundation
NOT modified."*

**Critical OH-discovered bugs** (`~/adapter/doc/compile_report.html` Appendix Z):

- **Z.1 init cfg env[] buffer overflow**: `OH init_service_manager.c:1023`
  passes `srcLen+1` as `destMax` to `strcpy_s` against a 128-byte
  `service->env[i].value` buffer → corruption of next env entry. Workaround:
  set env from `appspawn-x main()` itself (see appspawn_x.cfg env section
  versus `~/adapter/framework/appspawn-x/src/main.cpp:62-99`).

---

## 5. Process model

```
init (pid 1, OH)
 ├─ samgr, foundation, render_service, composer_host, com.ohos.launcher (OH normal)
 └─ appspawn-x (pid X, secon=u:r:appspawn:s0, ondemand listening on /dev/unix/socket/AppSpawnX)
      └─ on `aa start <bundle>` → AMS routes to appspawn-x socket
         └─ fork() → Android child process (uid 20010xxx)
              ├─ OH child specialization (setresuid, SELinux setcon, sandbox, AccessToken)
              ├─ ART daemons restart (HeapTaskDaemon, Finalizer, Signal Catcher)
              ├─ AppSpawnXInit.initChild → reflective injects:
              │    sCurrentActivityThread, sServiceManager,
              │    IActivityClientController Proxy, IContentProvider Proxy
              ├─ ActivityThread.main() → Looper.loop()
              ├─ AMS callback (via AbilitySchedulerAdapter C++ Stub on OH binder thread)
              │    → JNI → AbilitySchedulerBridge.java
              │    → LifecycleAdapter.mapOHToAndroid + Handler(MainLooper).post(...)  ← critical
              │    → ClientTransaction → handleLaunchActivity → MainActivity.onCreate
              └─ ViewRootImpl.draw → libhwui RenderThread → EGL → OHNativeWindow
                  → IBufferProducer → RS BufferQueue → HDI Composer → display
```

**Native API entry points** (`~/adapter/framework/appspawn-x/src/main.cpp:202-234`):

```cpp
LOGI("Phase 2: Initializing Android Runtime (ART VM)...");
AppSpawnXRuntime runtime;
ret = runtime.startVm();          // dlopen libart.so + JNI_CreateJavaVM
if (ret == 0) ret = runtime.preload();   // load 30 framework classes,
                                          // call libjavacore JNI_OnLoad,
                                          // register adapter bridges
```

`appspawnx_runtime.cpp` (960 LOC) and `AndroidRuntime.cpp` (427 LOC) are the
heart of the ART hosting layer.

---

## 6. Render pipeline (the big surprise)

**HBC kept the entire AOSP HWUI/Skia GPU pipeline.** Per `~/adapter/doc/graphics_rendering_design.html`
§5 (v3.0, 2026-05-06, "全文重写：聚焦硬件渲染，删除软件渲染兜底设计"):

> 主体方案锁定为硬件加速渲染（hwui + EGL + Skia + GPU + HWC）。
> 核心阻塞修复方向：让 App 进程内 hwui 提交的 buffer 不再走 SurfaceFlinger
> client-composition 假设，而是以 OH ProducerSurface 直接产帧到 RS BufferQueue,
> 由 RS 仲裁 DEVICE/CLIENT 合成。

**Concrete pipeline:**

```
Android App.View.draw
 → libhwui RenderThread (real AOSP)
   → DisplayList replay (real)
     → SkCanvas commands (real Skia M116→M133 calls into OH libskia_canvaskit.z.so)
       → EGL eglSwapBuffers
         → ANativeWindow.queueBuffer  (== OHNativeWindow shim)
           → IBufferProducer::FlushBuffer  (OH IPC → RS process)
             → RS BufferQueue consumer
               → RS prepare phase: DEVICE (HWC overlay) or CLIENT (RS GPU compositor)
                 → HDI Composer SetLayerBuffer / Commit
                   → vendor.so → drmModeAtomicCommit → DPU
```

**Skia compatibility** is the most clever piece: AOSP libhwui was M116, OH's
libskia_canvaskit.z.so is M133. Instead of rebuilding Skia, they:
- Cross-built libhwui against OH Skia headers via `~/adapter/build/skia_compat_headers/` (24 sub-dirs of header shims for `apex/`, `vndk/`, `vulkan/`, `media/`, `androidfw/`, `utils/`, etc.)
- Created `liboh_skia_rtti_shim.so` (12,632 bytes, **18 RTTI symbols** in `framework/surface/jni/skia_rtti_shim/`) to satisfy 8 essential `_ZTI*` typeinfo symbols (`SkCanvas`, `SkDrawable`, `SkPixelRef`, etc.) without rebuilding 23 MB of Skia. Audit found **0** `dynamic_cast<Sk*>` and **0** `typeid(Sk*)` in libhwui's 440 source files, so RTTI is link-time only — see `~/adapter/doc/technical_decision_overview.html` §2.9.

**SoftwareCanvas was explicitly deleted** — they reached the same conclusion
Westlake just did (CR-DD): "本项目目标 App 是普通 Android 应用，软件渲染兜底既不必要也不可达."

**Coexistence with OH apps:** Yes. OH `com.ohos.launcher` runs concurrently;
deploy script `DEPLOY_SOP.md` Stage 2 says *"不动 launcher（foundation 停了
它自然退出）"*. Multi-app coexistence is via legacy WMS adding the Android
window as a peer `APP_WINDOW_NODE` in the RS scene tree, side-by-side with
OH ArkUI windows. (SCB mode would put both under `SCB_SCREEN_NODE` — not yet
enabled.)

---

## 7. Window management

`WindowManagerAdapter` (Java, extends `IWindowManager.Stub`, 144 methods) +
`WindowSessionAdapter` (extends `IWindowSession.Stub`, 42 methods) bridge to
OH **legacy** `IWindowManager`/`IWindow` (NOT `ISceneSessionManager`/`ISession`,
because SCB is off).

The C++ side `~/adapter/framework/window/jni/oh_window_manager_client.cpp`
(716 LOC) actually creates an `RSSurfaceNode` client-side and passes it to OH
WMS via `CreateWindow + AddWindow + SetWindowId` — i.e., the App process
*creates the surface node* and the OH WMS just registers it (the OH "App
creates surface → passes to WMS" model). This is opposite to Android's "WMS
creates SurfaceControl, passes back to App" model.

**Reverse callbacks** use a **C++ Stub + JNI + Java Bridge** sandwich:

| C++ side (extends OH `IRemoteStub`) | Java bridge | Android target |
|---|---|---|
| `AppSchedulerAdapter` | `AppSchedulerBridge.java` | `IApplicationThread` (process lifecycle) |
| `AbilitySchedulerAdapter` | `AbilitySchedulerBridge.java` | `IApplicationThread` (activity/service) |
| `AbilityConnectionAdapter` | `AbilityConnectionBridge.java` | `IServiceConnection` (1:1) |
| `WindowCallbackAdapter` | `WindowCallbackBridge.java` | `IWindow` (Android side) |
| `SessionStageAdapter` | `SessionStageBridge.java` | `IWindow` + `IApplicationThread` |
| `WindowManagerAgentAdapter` | `WindowManagerAgentBridge.java` | (OH-only, no Android equivalent) |

Files: `~/adapter/framework/{activity,window}/jni/*_adapter.cpp` and matching
`~/adapter/framework/{activity,window}/java/*Bridge.java`.

**Critical thread-safety trick** (`~/adapter/doc/overall_design.html` §2.3):
*OH binder thread receives IAbilityScheduler callback → must use
`Handler(Looper.getMainLooper()).post(...)` to switch to main thread before
running ClientTransaction*, ensuring hwui RenderThread starts on the right
thread context. This is Westlake's lesson too (CR59 lifecycle drive).

---

## 8. Working apps

**Tested**: `~/adapter/app/` is a tiny `com.example.helloworld` APK with
`MainActivity`, `SecondActivity`, `HelloService`, `HelloWorldApplication`.
**Verified pure Android source** (no `import adapter.*` anywhere). Built on
local Android Studio (Windows), not on ECS.

**Status (per `~/adapter/doc/overall_design.html` §0.4 r26 + `compile_report.html`
appendix P14, 2026-04-29 EOD)**:

| Step | Status |
|---|---|
| `aa start com.example.helloworld` → AMS → AttachApplication → ScheduleLaunchAbility → handleLaunchActivity → MainActivity.onCreate | ✅ All real OH IPC, no mock |
| BCP class load (149 mainline stubs + framework + adapter) | ✅ |
| `handleBindApplication` with REAL AssetManager (resources.arsc parsed by AOSP `register_android_content_AssetManager`) | ✅ |
| `mInitialApplication` = real Application instance | ✅ |
| `ContextImpl.createAppContext` | ✅ |
| `PhoneWindow.<init>` (`Settings.Global.getInt` via Java Proxy IContentProvider) | ✅ |
| `Activity.attach` | ✅ |
| **`MainActivity.onCreate` reaches line 83** (TextView ctor at `TextView.java:1170`) | ✅ |
| `addToDisplayAsUser ADD_OKAY=0` + `activityResumed (state=9) rc=0` | ✅ |
| **CURRENT BLOCKER** — `findMode(1)` throws `IllegalStateException("Unable to locate mode 1")` because `DisplayInfo.supportedModes` is left empty (only 8 of 50+ fields populated by adapter) → ART main-thread abort → SIGABRT ~1.7 s after onCreate | ⚠️ |

That's a significantly farther-along result than Westlake's V2 substrate
currently shows on rk3568.

**Test client / scripts:**

- `~/adapter/out/adapter/test_tlv_client` (binary) — manually sends a SpawnMsg to appspawn-x socket to exercise fork path
- `~/adapter/deploy/deploy_to_dayu200.sh` (47 KB) and `deploy_stage.sh` (38 KB) — deploy automation
- `~/adapter/deploy/DEPLOY_SOP.md` — manual SOP (94 files + 4 symlinks per push, 13 file backup, restorecon dance for SELinux)

No multi-app validation evidence found beyond HelloWorld; no McD / noice /
DoorDash. The project is APK-app-agnostic by design.

---

## 9. Build & deploy workflow

**Build sequence** (per `~/adapter/CLAUDE.md` "Build Division Principle" + `~/adapter/build/restore_after_sync.sh`):

1. Sync source: `cd ~/aosp && repo sync` ; `cd ~/oh && repo sync`
2. Apply adapter patches: `bash ~/adapter/build/restore_after_sync.sh`
   (idempotent, applies `~/adapter/aosp_patches/` to `~/aosp/`,
   `~/adapter/ohos_patches/` to `~/oh/`, sets up dex2oat stubs, GN args)
3. AOSP Soong build: `cd ~/aosp && bash auto_build.sh` (Java jars + cross-built native .so + dex2oat host tool + boot image)
4. OH build: `cd ~/oh && ./build.sh --product-name rk3568 --ccache --gn-args allow_sanitize_debug=true --build-target abilityms libwms libbms libscene_session_manager librender_service oh_adapter_bridge apk_installer appspawn-x`
5. Cross-compile glue: `bash ~/adapter/build/cross_compile_arm32.sh` etc. (40+ phase scripts for libhwui)
6. Pull artifacts: `bash ~/adapter/build/pull_ecs_artifacts.sh` (ECS → local)
7. Deploy: `bash ~/adapter/deploy/deploy_to_dayu200.sh` (push 94 files + 4 symlinks, with chcon SELinux relabeling, restorecon, md5 verify)
8. Reboot, verify with `pidof foundation render_service hdcd`
9. APK test: `hdc file send HelloWorld.apk /data/local/tmp/ ; bm install ; aa start ...`

**Iteration discipline** (`~/adapter/CLAUDE.md` §"Build Isolation Rule"):
*No editor edits to AOSP/OH source allowed. All changes must be authored as
`.patch` files under `aosp_patches/` or `ohos_patches/`, applied by
`restore_after_sync.sh`. Boot-image rebuild costs 10–30 minutes/round so
never put adapter Java in BCP — keep in `oh-adapter-runtime.jar` (non-BCP)
and load via PathClassLoader.*

**Documentation discipline**: Every change must update `doc/build_patch_log.html`
and `doc/compile_report.html` and the relevant `doc/*_design.html`.

---

## 10. Westlake comparison

### HBC vs Westlake CR-BB Candidate C (XComponent HAP)

| Dimension | HBC | Westlake CR-BB |
|---|---|---|
| Where Android runs | Standalone `appspawn-x` daemon | Inside an OH HAP (`com.westlake.host.app`) using XComponent surface |
| Process boundary | Separate `appspawn-x`-fork child (uid 20010xxx) | One JVM inside HAP main process |
| Surface origin | Real `RSSurfaceNode` created client-side, registered with OH WMS | XComponent native window provided by HAP/ArkUI |
| ART runtime | Real cross-built `libart.so` from AOSP-14 | Bionic `dalvikvm-arm32` from KitKat 4.4 era |
| Lifecycle entry | `aa start` → AMS → ScheduleLaunchAbility → adapter bridge → ActivityThread | HAP `onCreate` → JNI invokes ART → MainActivity.onCreate via Java substrate |
| Visible chrome | Treated as peer Android window in legacy WMS scene tree (no chrome) | Inside an OH HAP window with XComponent |
| OH integration depth | Very deep (boot image + appspawn slot + SELinux types + namespace linker) | Shallow (HAP-level) |

### HBC vs Westlake V2 substrate (BINDER_PIVOT_DESIGN_V2)

| Dimension | HBC | Westlake V2 |
|---|---|---|
| Java substrate | **None** — uses real AOSP `framework.jar` byte-perfect (only 4 reflection-injected method bodies) | Custom `WestlakeActivity`, `WestlakeApplication`, thin `Resources/Window/PhoneWindow/DecorView/WindowManagerImpl` (~12,403 LOC) |
| Binder | Real OH IPC (`ipc_core`/`samgr_proxy`) on the OH side, real Android Binder on Android side, **transcoded by Java Stub subclasses** | Custom in-process `libbinder` (own implementation) + own daemons for audio/surface |
| Daemons | Reuses real OH `render_service`, `composer_host`, `audio_service`, `samgr` | Built own `audio_daemon`, `surface_daemon` (M5/M6) |
| Per-app hacks | None permitted by project invariant | Strict no-per-app-hacks rule (CR cleanups removed) |
| Render | Real AOSP libhwui + EGL + OH Skia | SoftwareCanvas (currently being deprecated per CR-DD) + planned in-proc DRM bridge for OHOS |
| Path to OHOS | Already on OHOS (rk3568) | Phase 2 OHOS porting in flight (CR41) |
| LOC of new Java code | ~10K (adapter framework Java) — but supplements real AOSP Java | ~12K (substitutes real AOSP Java) |

### HBC vs Westlake CR-DD recommended hybrid

CR-DD picks Candidate C (XComponent HAP) for OHOS Phase 2 because (a) SoftwareCanvas
is the right path for a HAP and (b) we don't want to rebuild AOSP. **HBC has
demonstrated rebuilding AOSP is tractable** — they cross-built 38 AOSP native
.so + 11 framework jars + 9-segment boot image and got an APK to
`MainActivity.onCreate` line 83. That said, the cost is enormous (40+ python
patch scripts for libhwui alone, multi-day boot-image rebuilds, 94-file
deploy procedure with SELinux dance).

**Verdict: HBC's architecture most resembles a Waydroid/Anbox in-OS variant**,
not any Westlake candidate. Both V2 (own libbinder, own daemons) and Candidate
C (HAP container) are *substantially smaller engineering bets* than HBC's.

---

## 11. What we should borrow

1. **Mandatory APK-transparency invariant**, enforced by anti-grep CI: zero
   `import adapter.*`, zero `OHEnvironment.*` usages in any APK source.
   Verification only via outside-observer (logcat / dumpsys / hilog).
   We've discussed this; HBC actually enforces it in their core CLAUDE.md.

2. **Dual-classloader pattern for adapter**: BCP holds only `OHEnvironment`
   (3 native methods, 3 API methods); all real adapter classes live in
   non-BCP `oh-adapter-runtime.jar`, loaded via `PathClassLoader`. Adapter
   iteration costs 0 boot-image cycles. **Massive iteration-velocity win.**

3. **Reverse-callback C++ Stub + JNI + Java Bridge sandwich**: Westlake currently
   collapses these into one Java callback object using reflection; HBC's two-layer
   pattern is cleaner because it preserves OH binder thread → Android main
   thread switching at the JNI seam (`Handler(Looper.getMainLooper()).post`).
   Solves the very problem CR59 wrestled with.

4. **Real `dex2oat`-baked boot image with adapter-mainline-stubs.jar BEFORE
   framework.jar in BCP order**: HBC found dex2oat verifier soft-fails when
   stub jar follows framework, even though the warning is benign. We'll hit
   this if we ever switch to a real boot image.

5. **`liboh_skia_rtti_shim.so` audit-and-shim trick**: 12.6 KB of typeinfo
   symbols beats 23 MB of rebuilt Skia. The audit methodology
   (`build/discover_skia_rtti_syms.sh` reads `readelf --syms` UND tables
   from `.o` files) is reusable for any "ABI symbol gap, no semantic gap"
   problem.

6. **DEPLOY_SOP.md staging discipline**: never `hdc file send <src>
   /system/...` directly; always `send → /data/local/tmp/stage/<basename>`,
   `ls -la` to verify it's a file (not a hdc-quirk-created directory!), then
   `cp` into `/system`. We've been bitten by this on the OnePlus 6 — formalizing
   the SOP would have saved hours.

7. **The "blame adapter first" RCA discipline** (`~/adapter/CLAUDE.md` §"Root
   Cause Analysis Discipline"): when something breaks, first suspect the
   adapter's transcoding, then cross-compile flags; only after both eliminated
   may you suspect upstream Android or OH. They cite two hard lessons of
   wasted CRs from blaming OH or Android wrongly.

8. **Anti-drift "single-command recovery rule"** (`~/adapter/CLAUDE.md` §"一键恢复规则"):
   `bash restore_after_sync.sh && <build cmd>` must reproduce last green
   from a clean `repo sync`. Forces every workaround to be a `.patch` or
   a script — no orphan changes survive a sync.

## 12. What we should NOT borrow

1. **The whole "rebuild AOSP-14 native + Java + boot image against musl" stack.**
   This is multi-engineer-month effort. HBC has 38 cross-built .so, a
   `libbionic_compat.so`, 40+ libhwui adaptation python scripts, custom
   `cross_compile_arm32.sh` + `compile_libhwui.sh` + `compile_skia_rtti_shim.sh`,
   `out/host-tools/dex2oat`, and they're still chasing field-mapping gaps
   in `DisplayInfo`. Westlake's KitKat dalvikvm route, while older, is a
   **vastly smaller bet**.

2. **The 40+ orphan in-place python patch scripts on libhwui.** They are
   open about it (`~/adapter/CLAUDE.md` §150-174 "典型反例"): these scripts
   sed-edit AOSP source, were never sunk into `.patch` files, and *will be
   wiped by the next `repo sync` of `~/aosp/`*. They classify it as a
   high-priority remediation item. We must not replicate that anti-pattern.

3. **The "mostly-stub `liboh_android_runtime.so` masquerading as `libandroid_runtime.so`"**
   pattern. Currently 13 of 15 `register_*` modules are stubs (Log, Trace,
   SystemProperties, Process, SystemClock, Binder, SurfaceControl,
   MessageQueue, ClassLoaderFactory, Typeface, Canvas, HardwareRenderer,
   GraphicsEnvironment, RenderNode), with only `register_android_content_AssetManager`
   and `_ApkAssets` being AOSP-real. This is a slippery slope of fake-passes
   that bite at runtime. Their `feedback_no_stub_compile.md` rule supposedly
   forbids this but they violate it on register_* modules anyway.

4. **Patching OH `init` `MAX_ENV_VALUE`** — they don't, and use a setenv()
   workaround in `appspawn-x main()` instead. But the underlying lesson is
   that OH `init` cfg `env[]` is unreliable for long values; **don't put
   anything > 128 B in init cfg env on OH**.

5. **Dependency on legacy WMS (`window_manager_use_sceneboard=false`).**
   OH's strategic direction is SceneBoard. HBC's whole window adapter chose
   the legacy path because SCB chain failed on their build. Building Westlake
   onto legacy is shipping on a dead-end OH variant.

6. **The `IActivityClientController` Java Proxy stub-everything pattern** —
   Java-Proxy returning safe defaults for 42 AIDL methods to bypass an NPE
   in `Activity.attach`. Works as a tactical patch but hides exactly the kind
   of semantic gaps that bite real apps. Westlake's "drive lifecycle to
   Resumed using real method invocations" is more honest.

---

## Appendix A — Key file paths (absolute, on GZ05)

- Project agent contract: `$HOME/adapter/CLAUDE.md` (30,473 bytes, 360 lines)
- Top-level inventory: `$HOME/adapter/readme.txt`
- Overall design: `$HOME/adapter/doc/overall_design.html` (1,271 lines)
- AppSpawn-x design: `$HOME/adapter/doc/appspawn_x_design.html`
- Graphics rendering design (v3.3, latest): `$HOME/adapter/doc/graphics_rendering_design.html`
- Surface bridging design: `$HOME/adapter/doc/surface_bridging_design.html`
- WindowManager IPC adapter design: `$HOME/adapter/doc/window_manager_ipc_adapter_design.html`
- Compile report (with OH bug index Z): `$HOME/adapter/doc/compile_report.html`
- Tech decision overview: `$HOME/adapter/doc/technical_decision_overview.html`
- HelloWorld gap analysis: `$HOME/adapter/doc/helloworld_gap_analysis.html`
- Deploy SOP: `$HOME/adapter/deploy/DEPLOY_SOP.md`
- AppSpawn-x init cfg: `$HOME/adapter/framework/appspawn-x/config/appspawn_x.cfg`
- AppSpawn-x main: `$HOME/adapter/framework/appspawn-x/src/main.cpp` (311 LOC)
- AppSpawn-x runtime: `$HOME/adapter/framework/appspawn-x/src/appspawnx_runtime.cpp` (960 LOC)
- AndroidRuntime port: `$HOME/adapter/framework/appspawn-x/src/AndroidRuntime.cpp` (427 LOC)
- OHEnvironment: `$HOME/adapter/framework/core/java/OHEnvironment.java` (199 LOC)
- ActivityManagerAdapter: `$HOME/adapter/framework/activity/java/ActivityManagerAdapter.java` (2,309 LOC)
- WindowManager client (C++): `$HOME/adapter/framework/window/jni/oh_window_manager_client.cpp` (716 LOC)
- ANativeWindow shim: `$HOME/adapter/framework/window/jni/oh_anativewindow_shim.cpp` (708 LOC)
- OHGraphicBufferProducer: `$HOME/adapter/framework/surface/jni/oh_graphic_buffer_producer.cpp` (396 LOC)
- AOSP L5 Java patches: `$HOME/adapter/aosp_patches/frameworks/core/java/android/{app,view}/*.patch`
- OH ohos_patches index: `$HOME/adapter/ohos_patches/README_OHOS_PATCHES.txt`
- AOSP source tree (read-only): `$HOME/aosp/` (37 dirs, ~stock AOSP-14 + repo)
- OH source tree (read-only): `$HOME/oh/` (25 dirs, weekly_20260302 = OH 7.0.0.18)
- Built artifacts (ECS-only): `$HOME/adapter/out/{adapter,aosp_lib,aosp_fwk,boot-image,oh-service,host-tools}/`
- Local cached copies of pulled files: `/tmp/hbc-analysis/` (on this dev box)

## Appendix B — Statistics

- Adapter framework total Java + C++: **~12,000 LOC** (estimated from `wc -l` samples)
- Forward bridges: **5** classes, **530+ AIDL methods**
- Reverse bridges: **6** C++ Stub + Java Bridge pairs, **210 callback methods**
- AOSP files patched: **10** (`ls aosp_patches/frameworks/...`)
- OH files patched: **~25** (`ls ohos_patches/...`)
- AOSP cross-built native .so: **38** in `out/aosp_lib/`
- Adapter native .so: **6** in `out/adapter/`
- AOSP Java jars: **11** in `out/aosp_fwk/`
- Boot image segments: **9** (×3 .art/.oat/.vdex = 27 files)
- Deploy artifact count per push: **94 files + 4 symlinks**
- OH service .so patched: **7** in `out/oh-service/`
- Total AOSP modifications recorded in `build_patch_log.html`: ongoing, hundreds of entries
- HelloWorld test APK Java files: **4** (`MainActivity`, `SecondActivity`, `HelloService`, `HelloWorldApplication`)

## Appendix C — Audit notes

- All claims double-checked against source files actually on disk via `cat`/`ls` over SSH.
- All status assertions cite the exact HTML design doc + section.
- The HBC `~/aosp/` and `~/oh/` trees are NOT inspected exhaustively (~280 GB
  combined); only the patch surfaces in `~/adapter/aosp_patches/` and
  `~/adapter/ohos_patches/` were reviewed plus a top-level structural ls.
- No remote modifications were made; no remote sudo; no background processes;
  no packages installed. ~12 SSH commands total, ~3 MB pulled to local.
- 2-hour analysis budget consumed.
