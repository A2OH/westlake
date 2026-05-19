# Westlake-Island Borrow Map — vs V3 HBC-reuse

Generated: 2026-05-19 (agent 57, option 1)
Status: READ-ONLY analysis; Island tree is owned by GZ02 AlexYang's team.
Scope: ~½–1 day. No code changed; this is a per-component borrow classification + verdict.

## TL;DR

1. **Island ≠ V3-replacement; Island is a methodology + ~14 reusable patterns.**
   Island runs a **fully process-isolated** Bionic+chroot ART against a Musl daemon
   over Neutral Protocol on AF_UNIX. V3 is a **single-process HBC-reuse** stack on
   real AOSP-14 ART cross-built to OHOS musl. The architectures are not substitutable
   — the redline that defines Island ("Bionic and Musl must never share a process,
   period") is the architectural opposite of V3's premise (HBC's `libbionic_compat.so`
   *is* the in-process bridge).

2. **Island has reached further on a different axis (rendered Calculator, 77/77
   smoke, V15 baseline, real touch, cross-APK relay) while V3 has reached
   farther on V3's axis (real `framework.jar` + dex2oat boot image + real HWUI/Skia,
   `MainActivity.onCreate` line 83 with real Activity machinery)**. Island's
   demos run in 480×800 PNG-files-then-blit-to-fb0; V3's runtime expects real
   render_service / IBufferProducer integration via HBC's bridges. Different
   substrates, different proof points.

3. **5 highest-value borrows are operational + IBinder-facade-shaped**, not
   substrate-shaped: the `NpBinder` never-die fallback (every transact catches
   Throwable → `writeNoException + 0`); `StubContext.getSystemService` as a
   real-ctor + safe-stub matrix (B.1/B.4 cases); the `flushViewRunQueue` +
   `drainPendingMessages` pair (forces `PerformClick` to fire after onCreate
   returns without `Looper.loop`); `integration-smoke.sh`'s 77-demo regression
   methodology; and the `BIONIC-MUSL-REDLINES.md` red-line CI gating (5 rules
   + 12 P0 + 6-script CI). All have V3 analogs and clear file paths.

4. **Service coverage gap surfaced:** Island exposes 14 Android services
   (7 NP-backed: activity/window/power/battery/wifi/thermal/sysinfo + 7
   safe-stubs: activity_task/display/input_method/audio/content/notification/input
   + 11 more no-op binders for accessibility/vibrator/etc). HBC bridges 5+6
   = 11 services through real `IXxx.Stub` subclasses. 03-Req scopes 50
   packages but **only 2/50 are past WORKSPACE_ONLY** (wifi `ORACLE_PILOT_READY`,
   `app.admin` `READY_WITH_GAPS`). On the top-10 V3-W4 critical packages,
   Island has runtime evidence for 4 (activity/window/power/wifi); HBC has
   runtime evidence for 5 (the 4 + content/window via `WindowSessionAdapter`);
   03-Req has spec evidence for 0 (all top-10 are V5.0 `DISCOVERY_REQUIRED`).

5. **Architectural verdict: V3 HBC-reuse + Island operational-borrow is the
   right balance; a third pivot to process-isolation is NOT justified today.**
   `feedback_two_pivots_in_two_days.md` says a third pivot needs evidence
   harder than V2→V3. The bar isn't cleared: Island's farthest reach
   (cross-APK Calculator render) is the same *class* of result V3 expects
   from HBC's real `framework.jar` (which already reaches Activity.onCreate
   line 83 with real Application/PhoneWindow/Activity.attach), and Island's
   redline #36 is *incompatible* with V3's `libbionic_compat.so` premise.
   The right move is to borrow Island's 5 tactical patterns into V3's
   engine layer, not adopt Island's substrate.

---

## 1. Source-tree inventory (per-file table)

Extracted from `westlake-16.14-Island-source-snapshot.tar.gz` to
`artifacts/v3-w3-westlake-stack/16.14-Island/`. Total: ~1218 file entries,
~5338 LOC across the key code files (5 leaf island/ stub dirs hold only
`property-log-shim` and `bionic-island/property-log-shim/src/property_stub.c`;
the real implementation lives under `tests/R4-apk-runner/src/` +
`neutral-protocol/`).

### 1.1 Bionic-side (chroot ART, Java + JNI)

| File | LOC | Purpose (one-liner) | API surface | Deps | Boundary |
|---|---:|---|---|---|---|
| `tests/R4-apk-runner/src/com/westlake/island/IslandBootstrap.java` | 1287 | Bootstrap: prepare main Looper, security/font providers, ServiceManager inject, WindowManagerGlobal/Choreographer/ActivityThread stub via Unsafe.allocateInstance + reflection, `StubContext`/`StubContentResolver`/`InMemorySharedPreferences` | `init()`, `context()`, `attachBaseToActivity()`, `flushViewRunQueue()`, `installChoreographerStub()`, `addExternalApkAsset()`, `setExternalApkCl/Theme()`, `injectApplication/CurrentActivityThread/PhoneWindow()`, `stubServiceProxy()` (`Proxy.newProxyInstance` returning primitive defaults) | AOSP framework reflection only (no NP socket) | BIONIC-side |
| `tests/R4-apk-runner/src/com/westlake/island/APKRunner.java` | 416 | Loads APK via `DexClassLoader`, instantiates Activity subclass, calls `onCreate` reflectively, measure/layout/draw → PNG; supports `-loop=fps` mode + cross-APK relay (`pendingNextApkPath`) + `-force-layout=0xRESID` fallback when onCreate throws | `main(args)`, `runActivity()`, `runActivityLoop()`, `drainPendingMessages(budgetMs)` (reflective MessageQueue.next + dispatchMessage drain) | IslandBootstrap, TouchBridge, FbWriter | BIONIC-side |
| `tests/R4-apk-runner/src/com/westlake/island/NpBinder.java` | 115 | Fake `IBinder` implementing `transact()` → delegates to lambda; **R5 NeverDie**: any Throwable → `reply.writeNoException(); reply.writeInt(0)`; native method declarations for npCall* (Power/Echo/Brightness/StartOHAbility/HilogTail/InputInjectTap) | `NpBinder(name, svc_id, descriptor, NpHandler)`, native npCallXxx | none | BIONIC-side |
| `tests/R4-apk-runner/src/com/westlake/island/NpBinderRegistry.java` | 207 | (Codegen) builds NpBinder for 7 real services + 7 safe-stub no-op services + 11 more cache-warming stubs (accessibility/vibrator/etc); `TransactCodeResolver`-resolved transact codes | `installAll()`, `buildActivity/Window/Power/Battery/Wifi/Thermalservice/Sysinfo()`, `buildNoOp(name, descriptor)`, `buildActivityTaskStub()` | NpBinder, ServiceManagerInjector, TransactCodeResolver | BIONIC-side |
| `tests/R4-apk-runner/src/com/westlake/island/ServiceManagerInjector.java` | 29 | Reflectively puts `NpBinder` into `ServiceManager.sCache` map | `install(name, binder)`, `installDefaults()` | NpBinderRegistry | BIONIC-side |
| `tests/R4-apk-runner/src/com/westlake/island/TouchBridge.java` | 147 | Daemon thread reading `/dev/input/event5` raw `input_event` (16-byte struct) parsing ABS_MT touch protocol → `MotionEvent.obtain` → `rootView.dispatchTouchEvent` (via `v.post`) | `setTarget(view)`, `start()`, `stop()` | View, MotionEvent | BIONIC-side (chroot /dev mount --bind) |
| `tests/R4-apk-runner/src/com/westlake/island/StubPackageManager.java` | 136 | Extends `PackageManager` with 94 default-null/0/false stubs + 3 real overrides (`getActivityInfo`/`getApplicationInfo`/`resolveActivity`) returning ApplicationInfo with `targetSdkVersion=25` to dodge AOSP-26+ autofill paths | All `PackageManager` abstract methods | none | BIONIC-side |
| `tests/R4-apk-runner/src/com/westlake/island/FbWriter.java` | 76 | Scale + center-pad 480×800 Bitmap → 720×1280 BGRA → write `/dev/graphics/fb0` directly (after `kill -STOP render_service`) | `write(Bitmap)` | Bitmap, Canvas, Matrix | BIONIC-side (chroot /dev access) |
| `tests/R4-apk-runner/src/com/westlake/island/SSRRefsProbe.java` | 173 | Probe: enumerate `SystemServiceRegistry` static refs to find which 154 ref a missing class breaks `<clinit>` | `probe()` | reflection | BIONIC-side |
| `tests/R4-apk-runner/src/com/westlake/island/TransactCodeResolver.java` | 63 | Looks up `IXxx.Stub.TRANSACTION_method` int constant via reflection | `resolve(iface, method)` | reflection | BIONIC-side |
| `tests/R4-apk-runner/src/apk_runner.c` | 171 | C-side JNI runner: `dlopen libart.so`, `JNI_CreateJavaVM` with full V15 `-Xbootclasspath` + `-Ximage:/data/local/oat/boot.art`, `dlopen libandroid/hwui/android_runtime` + `AndroidRuntime::startReg`, `RegisterNatives` for `NpBinderRegistry` + `NpBinder`, `FindClass + main([])` | `main(argc, argv)` parsing `-class`/`-dex` flags | dlopen-only against `/system/lib/` (V14 bionic, chroot-internal) | BIONIC-side |
| `tests/R4-apk-runner/build_apkrunner.sh` | (not opened) | Build script for `apk_runner` + `apkrunner.dex` + `hello-app.apk` | shell entrypoint | NDK, dx/d8 | BIONIC-side build |

### 1.2 Musl-side (host OH 7.0 / OH 6.1 userspace)

| File | LOC | Purpose | API surface | Deps | Boundary |
|---|---:|---|---|---|---|
| `neutral-protocol/runtime-host/np_proto.h` | 152 | NP wire format v1: 28-byte header (magic NPv1, msg_type, service_id, method_id, seq_no, session_id, fd_count, payload_len) + TLV payload (`u8/u16/u32/u64`, `string`, `bytes`, `fd` via SCM_RIGHTS, `session_id`/`callback_id`). Strict whitelist, NO C++ objects / `JNIEnv*` / malloc pointers | `np_send/recv()`, `np_tlv_put_*` / `np_tlv_next()` | none | both sides include |
| `neutral-protocol/runtime-host/np_proto.c` | 256 | Wire serialization implementation | as above | none | both sides |
| `neutral-protocol/runtime-host/np_server.c` | 78 | Server-side `accept(AF_UNIX/SOCK_STREAM)` framework | server entrypoint | np_proto | MUSL-side |
| `neutral-protocol/runtime-host/np_client.c` | 109 | Client-side `connect` + frame send/recv | client API | np_proto | BIONIC-side (built into libhello2_jni.so) |
| `neutral-protocol/runtime-musl/np_daemon.c` | 301 | Main musl daemon: listens on `/data/local/tmp/oh-bridge.sock`, dispatches by `service_id` to 14 service handlers; **R4 NeverDie** fork-per-client mode via `NEVERDIE_FORK=1`; `SIGHUP` re-reads `persist.westlake.experimental.amx` OH param; `SIGSEGV/SIGABRT` → OH hilog FATAL before crash | `main(sockpath)`, `neverdie_segv` handler | All handler_* + OH hilog NDK | MUSL-side |
| `neutral-protocol/runtime-musl/handler_ams.c` | 212 | AMS+WMS handlers: `getCurrentUserId` reads `/proc/self/status`, `startActivity` returns START_SUCCESS, `startOHAbility` shells `aa start -b <bundle>`, `getRunningTasks` shells `ps`, `getDefaultDisplayRotation` reads `/sys/class/graphics/fb0/rotate` | `ams_dispatch`, `wms_dispatch` | popen, /proc, /sys, `aa` CLI | MUSL-side |
| `neutral-protocol/runtime-musl/handler_power_manager.c` | 61 | `isInteractive`: reads backlight | `powermanager_dispatch` | /sys | MUSL-side |
| `neutral-protocol/runtime-musl/handler_battery_manager.c` | 40 | `getCapacity`: battery sysfs | `batterymanager_dispatch` | /sys | MUSL-side |
| `neutral-protocol/runtime-musl/handler_wifi_manager.c` | 30 | `isEnabled`: reads `/sys/class/net/wlan0/operstate` | `wifimanager_dispatch` | /sys | MUSL-side |
| `neutral-protocol/runtime-musl/handler_thermal_manager.c` | 24 | `getCpuTemp`: thermal sysfs | `thermalmanager_dispatch` | /sys | MUSL-side |
| `neutral-protocol/runtime-musl/handler_system_info.c` | 31 | `getAvailMemMb`: `/proc/meminfo` | `systeminfo_dispatch` | /proc | MUSL-side |
| `neutral-protocol/runtime-musl/handler_display_manager.c` | 67 | DisplayManager facade | `displaymanager_dispatch` | file/fb | MUSL-side |
| `neutral-protocol/runtime-musl/handler_sensor_manager.c` | 130 | SensorManager facade | `sensormanager_dispatch` | sensor_client.c | MUSL-side |
| `neutral-protocol/runtime-musl/handler_brightness.c` | 83 | Real-write to `/sys/class/backlight/.../brightness` | `brightness_dispatch` | /sys | MUSL-side |
| `neutral-protocol/runtime-musl/handler_input.c` | 79 | InputManager: `injectTap(x,y)` shells `uitest uiInput click x y` | `input_dispatch` | popen/uitest | MUSL-side |
| `neutral-protocol/runtime-musl/handler_hilog.c` | 67 | hilog `tail N` lines via popen | `hilog_dispatch` | popen/hilog | MUSL-side |
| `neutral-protocol/runtime-musl/handler_param.c` | 129 | OH `param get` shell-out | `param_dispatch` | popen/param | MUSL-side |
| `neutral-protocol/runtime-musl/handler_echo.c` | 104 | Echo for round-trip tests | `echo_dispatch` | none | MUSL-side |
| `neutral-protocol/codegen/np_codegen.py` | (not opened) | Python codegen from YAML IDL → C client+server stubs | shell tool | none | docs/build |
| `neutral-protocol/idl/*.yaml` | (7 files) | Per-service IDL: activity_manager.yaml, battery_manager.yaml, wifi_manager.yaml, system_info.yaml, thermal_manager.yaml, power_manager.yaml, sensor_manager.yaml, display_manager.yaml | YAML → codegen | none | docs |

### 1.3 Ops + demo

| File | LOC | Purpose |
|---|---:|---|
| `integration-smoke.sh` | 209 | 77-demo regression: build NP daemon + JNI + apk_runner, push to D200 via `hdc`, restart np_daemon, run each `Hello*Activity` and verify `SAVED` log line + recv PNG; auto-trigger golden-backup + `show_on_screen.sh` when ALL GREEN |
| `demo/run-live.sh` | 83 | "Never-die" live-demo driver: bind-mount /proc /dev into chroot, kill stale apk_runner, start np_daemon if missing, force-stop Photos viewer, run APKRunner with `-loop=10 -loop-budget=60000`, auto-reload daemon mediatool-sends fresh PNG every 5s for Photos viewer |
| `demo/run-flagship.sh` | 109 | Flagship demo runner |

### 1.4 Documentation (read for context)

| Doc | Notes |
|---|---|
| `docs/BIONIC-MUSL-REDLINES.md` | The 5 redlines + 12 P0 + 6 CI scripts that gate the project; explicitly framed as "Bionic & Musl must never share a process". This is the architectural opposite of V3's `libbionic_compat.so` premise. |
| `docs/REDLINE-REVIEW-2026-05-17.md` | 9-row audit of today's changes against the 5 redlines; concludes ✅ 100% in-redline. Useful methodology template for V3 "no per-app branches" enforcement. |
| `docs/V15-SWITCH-2026-05-18-COMPLETE.md` | V14→V15 chroot upgrade complete; key fix was keeping `V14 libproperty_stub.so` because V15's stub doesn't return abilist ABI strings. ENV adds `libfakelog.so` because V15 liblog interface changed. |
| `docs/V15-SWITCH-2026-05-17-v2.md` | Predecessor; 80%-done state before final fix. |
| `docs/L15-Hello111-ULTIMATE.md` | Cross-APK Calculator: hello-app button → AMS NP startActivity → APKRunner cross-APK relay → SimpleCalc.MainActivity → catch + forced `setContentView(R.layout.activity_main)` → real Calculator UI renders. 6-step diagram. |
| `docs/EXTERNAL-APK-SimpleCalc-FIRST-SUCCESS.md` | First third-party GitHub APK reaching SplashActivity render; 9-layer framework fix history. Blocker for MainActivity: `WindowManagerImpl null` (since fixed via `StubContext.getSystemService(WINDOW_SERVICE)` real ctor). |
| `docs/P3-SURFACEFLINGER-BRIDGE.md` | Phase-A spike confirming OH `RenderService` SA + HDI Display Buffer is SurfaceFlinger-equivalent; rejected wrapper-exe (NDK doesn't expose Surface creation), pivoted to PNG→Photos viewer (P0 upgrade) path. |
| `docs/Agent-A-B4-vsync.md` | Performance brief: 3 frames/30s → 900 frames/30s (vsync 30fps). Candidate A.1-A.5 (measure cache, dirty rect, ART JIT, multi-thread, fb mmap). |
| `docs/Agent-B-B5-zerocopy.md` | dma-buf zero-copy brief: B.1 mmap fb0 → B.2 FBIOPAN_DISPLAY double-buffer → B.3 ION dma-buf via SCM_RIGHTS fd-pass to RenderService → B.4 double Bitmap ping-pong. Stays bionic-internal until B.3 which crosses to musl via SCM_RIGHTS (the redline's one permitted cross-domain mechanism). |
| `docs/Agent-C-real-apks.md` | 5 third-party GitHub APK roadmap: Termux:API → F-Droid → KikaKeyboard → AntennaPod → Anki-Android. Reuses SimpleCalc "9-layer fix" pattern. |
| `docs/2026-05-16-DAY-SUMMARY.html` | Most-recent day summary (rendered HTML, not text-grepped here). |

---

## 2. Per-component borrow classification (4 categories)

### 2.1 AS-IS-BORROW (drop in with minimal adaptation)

These run as standalone discipline / docs / scripts. Zero Island runtime dependency.

| Item | Source | Why borrow | V3 landing point |
|---|---|---|---|
| `BIONIC-MUSL-REDLINES.md` 5-rule + 6-CI-script methodology | `16.14-Island/docs/BIONIC-MUSL-REDLINES.md` + `tools/agent-factory/ci-gates/*.sh` (referenced) | V3 needs analogous "what V3 must never do" — the macro-shim contract is currently 6 grep rules; could be hardened into a redline-style CI gate per their model | `docs/engine/V3-REDLINES.md` + `scripts/v3-redline-ci/{readelf_scan.sh, no_unsafe_grep.sh, no_per_app_branch.sh, no_hbc_edit.sh}` (new) |
| `REDLINE-REVIEW-<date>.md` 9-row weekly audit template | `16.14-Island/docs/REDLINE-REVIEW-2026-05-17.md` | Forces every change-batch to be re-classified against the redlines; 5/5 redlines clean is the report checkbox | `docs/engine/V3-REDLINE-REVIEW-TEMPLATE.md` (new) — apply per Westlake release |
| `integration-smoke.sh` 77-demo PNG diff regression methodology | `16.14-Island/integration-smoke.sh` | V3 currently has no equivalent regression sweep. The "build → push → restart-daemon → run-each-Activity → grep `SAVED` → recv-PNG → SUMMARY.md → auto-golden-sync" loop is generic and ports directly | `scripts/v3-smoke.sh` (new) running V3-side mock APKs equivalent (W5 mock APK feeds into this) |
| `demo/run-live.sh` "never get stuck in old state" pre-flight pattern | `16.14-Island/demo/run-live.sh` | The 6-step pre-flight (`mountpoint -q ... mount --bind`, `pkill stale`, `pgrep daemon \|\| start`, `force-stop viewer`, `power-shell wakeup`) is exactly what V3's `scripts/v3/aa-launch.sh precheck` should be doing | `scripts/v3/aa-launch.sh` precheck expansion (already PARTIAL per V3-WORKSTREAMS W3) |
| Single-command recovery + golden-baseline auto-sync | `integration-smoke.sh` lines 193-209 | When smoke ALL GREEN, auto-trigger golden backup + show last demo on physical screen. V3 has no equivalent; would catch regressions earlier | `scripts/v3/golden-baseline.sh` (new, gated by smoke pass) |

### 2.2 ADAPT-BORROW (concept applies, needs V3-shaped reimplementation)

These have clear V3 analogs but the Island implementation depends on
process-isolation / chroot details that don't map onto HBC's substrate.

| Item | Source | Why borrow | What to adapt |
|---|---|---|---|
| `NpBinder` R5 NeverDie fallback in `transact()` | `NpBinder.java:83-105` | Any handler `Throwable` → `reply.writeNoException(); reply.writeInt(0); return true` — prevents a broken/missing service from killing the Activity. V3 has no such gate today; HBC adapter throws straight through to AOSP framework | Adopt as a Westlake-side `IBinder` wrapper around HBC adapter `IXxx.Stub` subclasses; or as an `InvocationHandler` decorator that catches throwables from `OHEnvironment.getXxxAdapter()` returned proxies |
| `StubContext.getSystemService` 7+-case real-ctor + safe-stub matrix (B.1, B.4-1..7) | `IslandBootstrap.java:938-1166` | Real `ctor.newInstance(ipm, looper)` for ActivityManager/PowerManager/WifiManager/BatteryManager/NotificationManager/AlarmManager/IMM/AudioManager/ConnectivityManager + WindowManagerImpl real ctor + Dynamic Proxy fallback. V3 W4's adapter customization (top-10 packages) needs this exact pattern, just routed through HBC's `OHEnvironment.getXxxAdapter()` instead of NpBinder | Use as the **shape** for V3 W4's adapter-route entries: every system service that 03-Req scopes (top-10) should have (a) real-ctor-via-HBC, (b) Dynamic Proxy fallback, (c) NeverDie wrapper. Document in `docs/engine/V3-SYSTEMSERVICE-ROUTING.md` |
| `flushViewRunQueue` + `drainPendingMessages` lifecycle force-drive | `IslandBootstrap.java:395-475` + `APKRunner.java:350-415` | Reflectively walks view tree, pulls `View.mRunQueue` (HandlerActionQueue), inline-runs PerformClick with bypass that reflects `getListenerInfo.mOnClickListener` directly to fire `onClick(view)` without going through AccessibilityManager / Autofill chain. Pairs with reflective `MessageQueue.next` drain. **Generalizes** the V2 lifecycle-drive-to-Resumed pattern (5-pillar #5) without `Looper.loop` blocking. V3's engine layer needs this for headless/mock-APK validation (W5) | Adapt as `westlake-engine-lifecycle/LifecycleDriver.java` — same reflection but anchored on Activity/Fragment lifecycle methods (CR59 lesson: post via `Handler(Looper.getMainLooper())` at JNI seam, not direct). Keep `flushViewRunQueue` exactly as-is (chroot-agnostic; pure ART reflection). |
| `installChoreographerStub` Unsafe.allocateInstance bypass | `IslandBootstrap.java:480-559` | Sets up Choreographer instance without `ChoreographerImpl.<init>` (which depends on SurfaceFlinger), wires `mCallbackQueues × 5` with `this$0` correctly set so `addCallbackLocked` doesn't NPE. V3 hits the same shape of problem if HBC's adapter Choreographer wiring is incomplete | V3 contract says NO `Unsafe.allocateInstance` (macro-shim contract). Adapt by **opening a HBC-side bug** (per CR-FF "blame adapter first" RCA) for missing Choreographer wiring rather than mirroring the Unsafe pattern. If HBC declines, shadow `android.view.Choreographer` in Westlake's `oh-adapter-runtime.jar` per CR-FF Pattern 2 (dual-classloader) — this is the macro-shim-permitted escape |
| `APKRunner` `-loop=fps -loop-budget=ms` redraw + PNG-atomic-rename + FbWriter blit | `APKRunner.java:161-237` + `FbWriter.java` | Live demo driver — every frame redraws + atomic-renames PNG so external viewer sees complete file. V3 needs an equivalent for mock-APK validation (W5) and for noice/McD render-loop on OHOS (W6/W7) | Adapt as `scripts/v3/run-live.sh` driving HBC's render path (which goes through real RenderService → IBufferProducer; no FbWriter needed because HWUI handles compositing). Keep the loop budget + atomic-rename pattern for any PNG-based debug capture path |
| `TouchBridge` `/dev/input/event5` raw `input_event` daemon | `TouchBridge.java` | 147 LOC daemon parses ABS_MT touch protocol, dispatches via `MotionEvent.obtain` + `v.post(...)`. Concept = "framework-side input dispatch from below the InputManager". V3 doesn't need this on HBC (real InputManager works), but the **shape** is the right escape valve if HBC's InputManager bridge has gaps | Keep as reference for emergency-only use; don't preemptively port |
| `IslandBootstrap.hookAconfigFlags` V15 aconfig FEATURE_FLAGS Proxy | `IslandBootstrap.java:146-210` | 26 known V15 aconfig Flags classes — each gets `FEATURE_FLAGS` static field replaced with Proxy returning primitive defaults, bypassing aconfigd IPC. V3 will hit identical V15 aconfig pain if HBC's `framework.jar` is V15-based (HBC is V14 per CR-EE, so V3 may dodge this for now — but is a known landmine when V3 eventually upgrades) | Document as "V15-readiness checklist item"; bookmark IslandBootstrap.java:148-176 (the 26-pair list) for the V3→V15 upgrade play |
| 14-service "real NP + safe-stub" coverage matrix | `NpBinderRegistry.installAll()` + `STACK-INTERFACES-AND-CONTAINER-COMPARISON.md` §3.1 | The 14-service install pattern + descriptor whitelist (`android.app.IActivityManager`, etc.) is the right **scope** for V3 W4 — Phase-1 apps need these services live; HBC currently bridges 11 (5 forward + 6 reverse). The remaining 3+ (input/input_method/audio safe-stubs at minimum) should also be installed in V3 to keep framework paths alive | Map each Island service to either (a) HBC-bridged real, (b) HBC-bridged safe-stub (request from HBC), (c) Westlake-side safe-stub via the adapter dual-classloader (CR-FF Pattern 2). Document in V3 W4 brief |

### 2.3 ARCH-SPECIFIC (only makes sense with process-isolation; not portable to V3)

These ARE the Island architecture. They are the redline-#36 enforcement.
Borrowing any of them implies adopting the architecture.

| Item | Source | Why arch-specific |
|---|---|---|
| `np_daemon.c` AF_UNIX + `service_id` dispatch loop | `runtime-musl/np_daemon.c` | The daemon **only exists** because the host is OH-musl and the chroot is Bionic-AOSP. V3 has no equivalent need: HBC's adapter bridges call OH services directly via `liboh_adapter_bridge.so` linking real `libipc_core` (innerAPI). |
| Neutral Protocol TLV wire format (`np_proto.h`) | `runtime-host/np_proto.h` | Solves "how do you talk between Bionic and Musl without dual-libc disasters". V3 has no Bionic-Musl boundary in the runtime; HBC's `libbionic_compat.so` *is* the in-process compat layer. Wire format moot. |
| chroot mount strategy (bind-mount `/proc`, `/dev`, `/host_tmp`) | `demo/run-live.sh` + `REDLINE-REVIEW-2026-05-17.md` "灰色区 注 1" | chroot is the process boundary. V3 deploys directly to `/system/{lib,framework}` via HBC's DEPLOY_SOP. No chroot, no bind-mount, no `bionic-root/`. |
| SCM_RIGHTS fd-passing (only permitted cross-domain mechanism) | `np_proto.h` §0x30 + `Agent-B-B5-zerocopy.md` B.3 | The redline's one escape valve for media zero-copy. V3 doesn't need this because HBC bridges go through real `ANativeWindow → OHNativeWindow → IBufferProducer → RS BufferQueue`. |
| `libproperty_stub.so` + `libfakelog.so` LD_PRELOAD scaffolding | `V15-SWITCH-2026-05-18-COMPLETE.md` | These exist because chroot Bionic has no property service / liblog backend. V3 doesn't preload; HBC provides `libbionic_compat.so` for ABI compat and real OH hilog through innerAPI. |
| Cross-APK relay via `pendingNextApkPath` (Hello110 / Hello111) | `APKRunner.java:128-151` | Bionic-internal DexClassLoader hopping inside the chroot. V3 cross-pkg launches go through HBC's `AbilitySchedulerAdapter → aa start <bundle>` → real AMS → real ActivityThread (which spawns a separate appspawn-x child per bundle). Different launch model entirely. |
| `R4 NEVERDIE_FORK=1` fork-per-client daemon isolation | `np_daemon.c:206-271` | Only applies to AF_UNIX daemons. V3 has no daemon. |
| `SIGSEGV → OH hilog FATAL` async-signal-safe shim | `np_daemon.c:121-132` | Daemon-pattern specific. |

### 2.4 REDLINE-INFORMATIVE (rules educate even when not implemented)

V3 doesn't need to enforce Island's redlines because V3's architecture
(in-process HBC) explicitly violates redline #36 ("bionic process must
not directly link OH Musl .so" — HBC's `libbionic_compat.so` IS that link).
But the failure modes the redlines guard against are real and V3 must
have its own answers.

| Island redline | Why it exists | V3's equivalent guard |
|---|---|---|
| **#36** Bionic process must not directly link OH Musl `.so` | dual-libc allocator pointer-ownership conflicts; incompatible pthread/TLS; hidden libc global state conflicts | V3 accepts the link through HBC's `libbionic_compat.so` — but the shim must be **the only point** of musl↔bionic crossing. V3 self-audit gate should grep for unauthorized direct OH `.so` loads in Westlake-owned code: `readelf -d westlake-*.so \| grep "Shared library:" \| grep -E '^\s+\[lib(ipc|samgr|ability|hilog)'` should return only HBC-bridged libs |
| **#37** Musl process must not `dlopen()` APK Bionic `.so` | reverse direction — same dual-libc fate | V3 has no separate Musl process. The risk transforms into "real OH service host process must not load HBC's adapter `.so`" (since those carry bionic-compat-shim ABI) |
| **#38** Cross-domain payload must not carry C++ object / `JNIEnv*` / `jobject` / malloc pointer | Java↔native cross-runtime object lifetime + ABI mismatch | V3 has no cross-domain payload — every callable is either Java↔Java (BCP) or JNI within one process. But CR59 lifecycle lesson (`Handler(Looper.getMainLooper()).post(...)` at JNI seam, not direct invoke from binder thread) is the V3 analog of this redline at the threading layer |
| **#39** Cross-domain malloc/free or pthread/TLS across islands | "who allocates, frees" + thread-state ownership | V3: macro-shim contract already forbids `Unsafe.allocateInstance` on framework internals + `Field.setAccessible(true)` reflective sets on framework classes. The contract serves the same purpose at the Java layer |
| **#40** Media data plane must not go through ordinary per-frame RPC | per-frame Binder/RPC = framedrop | V3 inherits from HBC: real HWUI render thread + EGL eglSwapBuffers + real IBufferProducer ring buffer; no per-frame RPC |
| 12 P0 emergency items | onboarding gate for new contributors | V3 has its own enumeration: `V3-WORKSTREAMS.md` W1-W13 acceptance criteria. The Island P0 #10/11/12 ("Activity/AMS minimum facade", "Window/WMS minimum facade", "crash/exit diagnostics") map cleanly to V3 W4 acceptance gates |

---

## 3. Top-5 highest-value borrows for V3 (with effort estimates)

Each row: what + why + V3 file path + workstream + effort + macro-shim audit.

### Borrow #1 — `NpBinder` R5 NeverDie fallback at the IBinder seam

**What.** `IBinder.transact()` catches any handler `Throwable` →
`reply.writeNoException(); reply.writeInt(0); return true`. Missing
service or broken handler degrades to "false / 0 / null" instead of
crashing the calling Activity.

**Why borrow.** V3's current contract (CR-FF Pattern 1, the 4 L5 patches)
falls through to AOSP `ServiceManager.getService` on `OHEnvironment.isOHEnvironment()`
returning true but `getXxxAdapter()` throwing. If HBC's adapter
implementation throws a `RuntimeException` from inside an adapter
method, that exception currently propagates straight to the calling
framework code → app crash. The 5-pillar pattern already does
"safe-context bind stub" at one layer; this extends the discipline to
every adapter method.

**V3 landing path.**
`westlake-host-gradle/app/src/main/java/com/westlake/host/NeverDieAdapterDecorator.java`
(new) — an `InvocationHandler` wrapping the proxy that HBC's
`OHEnvironment.newAdapterReflective(className)` returns, catching
`InvocationTargetException` → returning AOSP-default for the method's
return type.

**Workstream.** V3 W4 (adapter customization) + W6/W7 acceptance gates
(noice/McD ride on the decorator).

**Effort.** ~½ day (50 LOC + 4 unit tests).

**Macro-shim audit.** Decorator lives on a Westlake-owned class
(`NeverDieAdapterDecorator`), bodies are case (b) "safe primitive" per
§7 of V3-ARCHITECTURE.md. Zero `Unsafe`, zero `setAccessible`, zero
per-app branch. PASS.

### Borrow #2 — `flushViewRunQueue` + `drainPendingMessages` lifecycle-drive pair

**What.** Two helpers totaling ~150 LOC that (a) reflectively walk a
View tree pulling `View.mRunQueue` and inline-runs each Runnable with
PerformClick bypass that reflects `getListenerInfo.mOnClickListener` to
fire `onClick(view)` directly, and (b) reflectively dequeues
`MessageQueue.next` + dispatchMessage to drain the main Looper without
`Looper.loop` blocking.

**Why borrow.** V3 W5 (mock APK validation) and the V2 5-pillar lifecycle
drive both need to "wait for onClick to fire after setContentView returns
without calling `Looper.loop`". The current V2 5-pillar pattern hand-rolls
this per-app in `NoiceInProcessActivity.kt` / `McdInProcessActivity.kt`;
extracting it to a generic helper removes per-app variation while keeping
the macro-shim contract (because it operates on Westlake-owned helper,
not framework internals).

**V3 landing path.**
`westlake-host-gradle/app/src/main/java/com/westlake/host/LifecycleDriver.java`
(new). Move the per-app drive sequences out of NoiceInProcessActivity /
McdInProcessActivity into `LifecycleDriver.driveToResumed(activity)`.

**Workstream.** V3 W5 (mock APK validation), W6 (noice), W7 (McD), W11 (V2
carryforward audit — this is the cleanest part of V2 to carry forward).

**Effort.** 1 day (extract + adapt + verify both Phase-1 apps still
PASS 14/14 regression).

**Macro-shim audit.** Reflection targets are `View.mRunQueue`,
`HandlerActionQueue.mActions/mCount`, `Message.target/when` — framework
internals. **CAVEAT:** these reflective reads were already in V2 5-pillar
("lifecycle drive to Resumed"); V3-ARCHITECTURE.md §7 says
`Field.setAccessible(true)` on framework internals is forbidden under V3.
**Either (a) borrow with explicit V3 amendment carving this out for the
lifecycle-drive seam, OR (b) request HBC to expose a lifecycle-drive API
on AbilitySchedulerAdapter that we can call from Java without
reflection.** Path (b) is preferred per CR-FF "blame adapter first" RCA.
If (b) is open-bug'd and not closed in 1 week, fall back to (a) as a
documented exception. Flag in PR description.

### Borrow #3 — `StubContext.getSystemService` real-ctor + safe-stub matrix as the W4 routing template

**What.** ~230 LOC switch covering 13 system services
(LAYOUT_INFLATER, INPUT_METHOD, AUDIO, WINDOW, BATTERY, WIFI, ALARM,
CONNECTIVITY, NOTIFICATION, ACTIVITY, POWER, LAYOUT_INFLATER, plus the
SystemServiceRegistry fallback) — each case either calls the real AOSP
constructor (reflective access to package-private `(Context, IXxx, Looper)`
ctors) with NpBinder-backed `IXxx` proxies, OR returns a Dynamic Proxy
returning primitive defaults.

**Why borrow.** V3 W4 (adapter customization, GH issue #629) currently
has no concrete "what does each adapter look like" specification. This
is exactly that spec, in code form, for 13 services. Each route maps
cleanly to one of V7.0's 6 routes (`OH_DIRECT` / `OH_ADAPTER` /
`JAVA_SYNTH` / `UNSUPPORTED_EXPLICIT_FAILURE` / `HUMAN_DECISION_REQUIRED`
/ `DISCOVERY_REQUIRED`).

**V3 landing path.**
`docs/engine/V3-SYSTEMSERVICE-ROUTING.md` (new spec, one row per service
× one of V7.0 routes × HBC adapter source / safe-stub policy) and a
companion `westlake-host-gradle/app/src/main/java/com/westlake/host/
SystemServiceRouter.java` (new) wrapping `HBC.getXxxAdapter()` with the
real-ctor + safe-stub policy.

**Workstream.** V3 W4 (consumes this as the routing catalog the V3
top-10 03-Req packages need).

**Effort.** 1-2 days (extract spec, table-ify to V7.0 schema, plumb
through HBC adapter calls).

**Macro-shim audit.** The Island code uses `ctor.setAccessible(true)`
+ `unsafe.allocateInstance(android.media.AudioManager.class)` — both
forbidden under V3. Adapt by routing through HBC's existing
`AbilitySchedulerAdapter` / `WindowManagerAdapter` (public APIs, no
reflection). For services HBC doesn't bridge yet (most of the 13),
borrow #1 (NeverDieAdapterDecorator) returning safe-default until HBC
adds the real bridge. **No Unsafe / setAccessible in V3 borrow.** PASS.

### Borrow #4 — Smoke-test 77-demo regression methodology

**What.** `integration-smoke.sh` runs 77 `Hello*Activity` demos, greps
`SAVED` log line + recv PNG, writes SUMMARY.md, auto-syncs golden +
auto-displays last demo on physical screen if ALL GREEN.

**Why borrow.** V3 currently has acceptance-criteria checklists per
workstream (W1-W13) but no continuous-regression sweep that runs every
N hours. The "single command, every-demo PASS/FAIL grid" methodology
is generic and the Island team uses it to gate every commit.

**V3 landing path.**
`scripts/v3-smoke.sh` (new) — `mock APK suite` + `noice via V3` + `McD
via V3` run on DAYU200 via `aa start`. Output `artifacts/v3/smoke/
<timestamp>/SUMMARY.md` + per-app screen PNGs (where possible) +
hilog grep counts.

**Workstream.** V3 W5 + V3 W6 + V3 W7. Hooks into W9 (DEPLOY_SOP
inheritance — same pre-flight as Island's `run-live.sh`).

**Effort.** 1-2 days (the scaffold is mostly the Island shell script
with paths swapped; the mock APK suite is W5 deliverable).

**Macro-shim audit.** Shell script, no Java, no reflection. PASS.

### Borrow #5 — `BIONIC-MUSL-REDLINES.md` + `REDLINE-REVIEW-<date>.md` audit methodology

**What.** Island maintains a `REDLINES.md` listing 5 architecture rules
with one-line rationale + 6 CI scripts that enforce them. Every change
batch produces a `REDLINE-REVIEW-<date>.md` reclassifying each commit
against the 5 rules (✅/⚠️/❌). The Island team's audit on 2026-05-17
caught 0 violations across 9 changes.

**Why borrow.** V3 has `feedback_macro_shim_contract.md` rules but no
mechanical audit cadence. CR-completion currently relies on agent
self-grep — quality variable. The Island REDLINE-REVIEW template makes
the audit a deliverable artifact.

**V3 landing path.**
- `docs/engine/V3-REDLINES.md` (new) — V3-equivalent 5 rules:
  (i) No `Unsafe.allocateInstance` on framework classes,
  (ii) No `Field.setAccessible(true)` on framework internals,
  (iii) No per-app branches,
  (iv) No edits under `third_party/hbc-runtime/`,
  (v) No new shadow classes for `android.*` framework classes.
- `scripts/v3-redline-ci/` — 5 grep scripts (one per rule).
- `docs/engine/V3-REDLINE-REVIEW-TEMPLATE.md` — table template for
  weekly audit.

**Workstream.** V3 W9 (DEPLOY_SOP / discipline borrow); referenced from
every future V3 CR's self-audit section in V3-ARCHITECTURE.md §9.

**Effort.** ½ day (write 5 greps + template + first weekly application
across recent commits).

**Macro-shim audit.** Meta — this IS the macro-shim contract enforcement
infrastructure. Trivially PASS.

---

## 4. 3-way service coverage table

Coverage on 20 services/packages most relevant to noice + McD (Phase-1
critical core per `03-REQUIREMENT-INDEX.md` Top-10 + select expansions).

Legend:
- **Island**: `NP-real` (real handler with /proc /sys /shell-out backing) /
  `safe-stub` (NoOp NpBinder returning 0/null) / `(none)`
- **HBC**: `forward-bridge` (extends IXxx.Stub, real OH adapter) /
  `reverse-bridge` (C++ Stub → JNI → Java sandwich) / `(none)`
- **03-Req**: `V7-Pilot-Ready` / `V7-Workspace` / `V5-DISCOVERY_REQUIRED`
  (default for all 50 packages today)

| # | Service / package | Island | HBC | 03-Req | Notes |
|---:|---|---|---|---|---|
| 1 | `activity` (`android.app.IActivityManager`) | **NP-real** (`handler_ams.c`: `getCurrentUserId`/`getProcessLimit`/`startActivity`/`startOHAbility`/`getRunningTasks`) | **forward-bridge** (`ActivityManagerAdapter`) | V5 DISCOVERY (top-10 #1) | All three teams converge on this. HBC's coverage is broadest. |
| 2 | `window` (`android.view.IWindowManager`) | **NP-real** (`wms_dispatch`: `getDefaultDisplayRotation`/`isKeyguardLocked`) | **forward-bridge** (`WindowManagerAdapter` + `WindowSessionAdapter`) | V5 DISCOVERY (top-10 #4) | HBC's coverage materially deeper (real PhoneWindow → ViewRootImpl path). Island is minimum-viable. |
| 3 | `power` (`android.os.IPowerManager`) | **NP-real** (`handler_power_manager.c`: `isInteractive`/`brightness`) | (none enumerated in CR-EE §1.3) | V5 DISCOVERY (in `android.os`, top-10 #3) | Island has runtime; HBC may forward through `OHEnvironment` to OH PowerManager via innerAPI |
| 4 | `battery` (`android.os.IBatteryStats`) | **NP-real** (`handler_battery_manager.c`) | (none enumerated) | V5 DISCOVERY (in `android.os`) | noice: medium use; McD: low |
| 5 | `wifi` (`android.net.wifi.IWifiManager`) | **NP-real** (`handler_wifi_manager.c`: `isEnabled` via `/sys/class/net/wlan0/operstate`) | (none enumerated) | **V7 ORACLE_PILOT_READY** (`android.net.wifi`) | The only top-50 package with substantive V7 spec; Island has runtime; HBC may have nothing |
| 6 | `thermalservice` (`android.os.IThermalService`) | **NP-real** (`handler_thermal_manager.c`) | (none) | V5 DISCOVERY (in `android.os`) | not in Westlake-relevant top-10 |
| 7 | `sysinfo` (`android.os.ISystemInfo`) | **NP-real** (`handler_system_info.c`: `/proc/meminfo`) | (none) | V5 DISCOVERY | not in Westlake-relevant top-10 |
| 8 | `activity_task` (`android.app.IActivityTaskManager`) | **safe-stub** (`buildActivityTaskStub`: returns non-null `IActivityClientController` binder) | **forward-bridge** (`ActivityTaskManagerAdapter`) | V5 DISCOVERY (in `android.app`, top-10 #1) | HBC's coverage deeper. Island's safe-stub is the minimum to avoid AppCompat `setTaskDescription` NPE |
| 9 | `display` (`android.hardware.display.IDisplayManager`) | **safe-stub** | (none enumerated; transitively used by HBC's `WindowSessionAdapter`) | **V5 DISCOVERY** (`android.hardware.display`, only 25 rows — thinnest matrix in corpus) | HBC's coverage TBD; Island's safe-stub bare minimum |
| 10 | `input` (`android.hardware.input.IInputManager`) | **safe-stub** (also NP `handler_input.c` for `injectTap`) | (none enumerated) | V5 DISCOVERY (`android.hardware.input`, 9 rows) | Island NP-real for injectTap; HBC needs to forward |
| 11 | `input_method` (`com.android.internal.view.IInputMethodManager`) | **safe-stub** | (none enumerated) | V5 DISCOVERY (in `android.view`, top-10 #4) | Both apps need IMM existence (not real keyboard) |
| 12 | `audio` (`android.media.IAudioService`) | **safe-stub** + `StubContext.getSystemService(AUDIO)` does `Unsafe.allocateInstance(AudioManager.class)` | (none enumerated) | V5 DISCOVERY (`android.media`, 2584 rows — second-largest) | noice = primary audio app, uses ExoPlayer/OpenSL ES which bypass `android.media.AudioManager`; McD uses ringtone |
| 13 | `content` (`android.content.IContentService`) | **safe-stub** + `StubContentResolver` w/ `acquireProvider→null` | (none enumerated; HBC's `framework.jar` ships real `ContentResolver`) | V5 DISCOVERY (top-10 #2 `android.content`) | Both apps query providers via Settings.Global / MediaStore — must work |
| 14 | `notification` (`android.app.INotificationManager`) | **safe-stub** + real `NotificationManager(Context, Handler)` ctor with `sService = Proxy` | (none enumerated) | V5 DISCOVERY (in `android.app`, top-10 #1) | McD posts notifications; noice posts media notifications |
| 15 | `package` (`android.content.pm.PackageManager`) | `StubPackageManager` (94-method stub, 3 real overrides) | **forward-bridge** (`PackageManagerAdapter` via L5 patch to `ActivityThread.sPackageManager`) | V5 DISCOVERY (top-10 #7 `android.content.pm`, 836 rows) | HBC is best. Island has 9-layer hand-rolled stub. |
| 16 | `accessibility` / `vibrator` / `vibrator_manager` / `appops` / `accessibility_manager` / `captioning` / `user` / `device_policy` / `permission_checker` / `media_session` / `uimode` | **safe-stub × 11** in `NpBinderRegistry.installAll` final loop | (none) | V5 DISCOVERY for each (most outside top-10) | Cache-warming only — keeps Activity init paths from NPE |
| 17 | `location` (`android.location.LocationManager`) | (none) | (none enumerated) | V5 DISCOVERY (`android.location`, 333 rows, Σmcd=3) | **Gap for McD store-finder.** None of 3 teams have a route. |
| 18 | `camera2` (`android.hardware.camera2`) | (none) | (none enumerated) | V5 DISCOVERY (`android.hardware.camera2`, 338 rows, Σmcd=3) | **Gap for McD QR scanner.** None of 3 teams have a route. |
| 19 | `biometric` / `keystore` (`android.security.keystore`) | (none) | (none enumerated) | V5 DISCOVERY (`android.security.keystore`, 104 rows, Σmcd=2) | **Gap for McD biometric login.** None of 3 teams have a route. |
| 20 | `connectivity` (`android.net.ConnectivityManager`) | `StubContext.getSystemService(CONNECTIVITY)` does `Unsafe.allocateInstance(ConnectivityManager.class)` | (none enumerated; transitively in HBC's `framework.jar`) | V5 DISCOVERY (`android.net`, 485 rows) | Both apps need ConnectivityManager check; Island uses Unsafe (forbidden under V3) |

### Top-10 03-Req critical packages — coverage cross-check

The 03-Req `Top-10 Critical Packages for V3 W4`:

| # | Package | Island runtime evidence | HBC adapter evidence | 03-Req V7 status |
|---:|---|---|---|---|
|  1 | `android.app` | partial (ActivityManager + Application stub via injectApplication) | yes (4 L5 patches, ActivityManagerAdapter + ActivityTaskManagerAdapter) | V5 DISCOVERY (no V7 yet) |
|  2 | `android.content` | partial (StubContext + StubContentResolver) | partial (PackageManagerAdapter; ContentResolver via real framework) | V5 DISCOVERY |
|  3 | `android.os` | partial (Bundle/Handler real-AOSP; injectCurrentActivityThread stub) | yes (real-AOSP; CR59 lifecycle fix applies here) | V5 DISCOVERY |
|  4 | `android.view` | partial (WindowManagerImpl real-ctor; Choreographer Unsafe stub) | yes (WindowManagerAdapter + WindowSessionAdapter; real PhoneWindow → ViewRootImpl) | V5 DISCOVERY |
|  5 | `android.widget` | yes (real-AOSP widgets render in Hello67 etc.) | yes (real-AOSP from framework.jar) | V5 DISCOVERY |
|  6 | `android.graphics` | partial (real-AOSP Bitmap/Canvas; software path via fb0) | yes (real libhwui + libskia via skia_rtti_shim) | V5 DISCOVERY |
|  7 | `android.content.pm` | partial (StubPackageManager 94 stubs + 3 real) | yes (PackageManagerAdapter forward-bridge) | V5 DISCOVERY |
|  8 | `android.content.res` | partial (Resources + AssetManager.addAssetPath × 3) | yes (real framework AssetManager + libandroidfw .patch) | V5 DISCOVERY |
|  9 | `android.util` | n/a (real-AOSP) | n/a (real-AOSP) | V5 DISCOVERY |
| 10 | `android.text` | n/a (real-AOSP) | n/a (real-AOSP, with V15 aconfig flags landmine) | V5 DISCOVERY |

**Surfacing gaps:**
- **`android.location`** + **`android.hardware.camera2`** + **`android.security.keystore`** — Σmcd=3/3/2, McD-critical, but **none of 3 teams** have runtime evidence. V3 W7 (McD on V3) cannot land complete without at least safe-stub coverage. Suggest adding to V3 W4 scope as new rows.
- **`androidx.fragment` / `androidx.lifecycle` / `androidx.compose` / `androidx.room`** — entirely absent from 50-pkg corpus. Island sidesteps because Hello* demos don't use them. HBC handles via real Java BCP. V3 W6 (noice) hits hard because noice = Compose-heavy fragment app.
- **Top-10 coverage**: HBC wins 7/10, Island wins 3/10 partial, 03-Req covers 0/10 with V7 spec. HBC is the right substrate; Island patterns help fill gaps; 03-Req is too early to use as routing source.

---

## 5. Architectural verdict + pivot recommendation

**Verdict: V3 HBC-reuse + Island operational-borrow is the right balance. A third pivot to Island's process-isolation architecture is NOT justified today.**

### Evidence

**For Island's architecture (the case for pivot):**
1. **Island has reached "real third-party APK renders" earlier on the
   wallclock** — SimpleCalc Calculator UI on 2026-05-17 (`L15-Hello111-ULTIMATE.md`),
   77/77 smoke pass, V15 baseline complete on 2026-05-18.
2. **Island has working physical-screen integration** — `P3-SURFACEFLINGER-BRIDGE.md`
   pivoted (after Phase-A spike rejected wrapper-exe) to PNG → MediaLibrary →
   Photos viewer. Works today. V3 still depends on HBC's render integration
   landing on Westlake's board.
3. **Island has working physical-touch integration** — `TouchBridge.java`
   reads `/dev/input/event5` and routes via `dispatchTouchEvent`. V3 inherits
   InputManager from HBC; not yet verified end-to-end on DAYU200.
4. **Island has 77-demo regression discipline** that V3 lacks.
5. **Island's redline-CI methodology** is a stronger guardrail than V3's
   current grep-based macro-shim contract.

**Against Island's architecture (the case for staying with V3):**
1. **`feedback_two_pivots_in_two_days.md` evidence bar (2026-05-16):**
   "Two pivots in two days. The next one needs evidence harder than V2→V3."
   The V2→V3 bar was: independent empirical demonstration of significantly
   deeper reach (HBC at MainActivity.onCreate line 83 with real Application/
   Activity.attach/`addToDisplayAsUser ADD_OKAY=0`). Island reaches
   *farther on a different axis* (physical-screen + cross-APK rendering)
   but **HBC reaches deeper on the runtime-fidelity axis** (real
   framework.jar, real HWUI, real Skia, real ActivityThread). Same class of
   result, different layers. Not "evidence harder than V2→V3".
2. **Island's redline #36** ("Bionic must not directly link Musl .so") is
   **architecturally incompatible** with V3's `libbionic_compat.so` premise.
   Adopting Island means re-deleting `libbionic_compat.so` and HBC's
   adapter framework — undoing the V2→V3 pivot. That's a regression, not
   progress.
3. **Island's render path** (480×800 PNG → fb0 blit or PNG → Photos
   viewer) is **inherently lower-fidelity** than HBC's path (real HWUI
   render thread + EGL eglSwapBuffers + real IBufferProducer ring buffer
   into RenderService BufferQueue). Island works because the demo apps
   draw simple UI; noice (Compose) and McD (Skia drawing + SurfaceView
   canvas blit) need GPU-accelerated render that Island's path can't
   sustain at 60fps with real animation.
4. **Island's reach is largely Bionic-internal**. The "cross-APK Calculator
   render" works because (a) APKRunner does `new DexClassLoader(...)` for
   the second APK in the same Bionic ART process, (b) onCreate is caught
   on exception with `-force-layout=0xRESID` fallback. This is not real
   cross-process AMS launch — that path is the `aa start -b <bundle>` NP
   bridge to the host OH userspace, which then launches **OH Photos**, not
   a second Bionic Activity. Compared with HBC's `appspawn-x` model
   (one bionic-compat child per Activity), Island's app-isolation story
   is weaker.
5. **Island's per-component ratchet (9 layers of stubs)** is exactly the
   "additive shim" anti-pattern formalized in `feedback_additive_shim_vs_
   architectural_pivot.md`. Each new third-party APK that fails adds 1–5
   stubs. The roadmap (`Agent-C-real-apks.md`) projects 3–15 layers per
   APK (Termux 3, F-Droid 7, Anki 15). HBC's coverage scales differently
   (530+ AIDL methods, but each one is real-bridged, not stubbed). The
   long-run scaling story favors HBC.
6. **Westlake's macro-shim contract** explicitly forbids `Unsafe.allocateInstance`
   and `Field.setAccessible(true)` on framework internals. Island uses BOTH
   pervasively (ChoreographerStub, AudioManager allocate, ActivityThread
   allocate, Display Unsafe.allocateInstance, FbWriter is fine but
   IslandBootstrap is 12+ Unsafe sites). Adopting Island wholesale means
   tearing up the macro-shim contract — itself an architectural pivot.

### Recommendation

**Stay with V3 HBC-reuse as the substrate. Borrow Island's 5 patterns
(see §3) into V3's engine + ops layer. Acknowledge Island as the source
team in V3 CR descriptions.**

Concretely:
1. Open V3 W14 (new workstream): "Island borrow integration — 5 patterns,
   ~5 person-days total per §3 estimates above".
2. Add `docs/engine/V3-REDLINES.md` + `scripts/v3-redline-ci/` as the V9
   discipline-borrow deliverable (Island's redline methodology).
3. Add `scripts/v3-smoke.sh` as the V5/W6/W7 regression sweep using
   Island's `integration-smoke.sh` as the structural template.
4. Defer `Choreographer Unsafe stub` borrow — file as **HBC bug** first
   (per CR-FF "blame adapter first") and only port the Island pattern as
   shadow-class fallback if HBC declines.
5. Cross-link this doc from V3-WORKSTREAMS.md W14, from `MEMORY.md`, and
   from the next handoff doc.

### When would a pivot to Island be justified?

Only if **all three** become true:
- (a) HBC's substrate hits a hard architectural blocker on Westlake's
  board that's not fixable within HBC's current adapter framework (e.g.,
  `libbionic_compat.so` ABI gap that breaks musl pthread/TLS in a way
  HBC can't patch); AND
- (b) V3 W2 (boot HBC standalone) fails ACCEPTANCE after multiple
  remediation rounds (currently PENDING per V3-WORKSTREAMS); AND
- (c) Island demonstrates that **a real production Android app** (not
  Hello* demo, not SimpleCalc with forced-setContentView fallback) runs
  end-to-end on its substrate — noice or McD ideal, third-party Anki/
  AntennaPod acceptable.

Currently 0/3. Re-evaluate at V3 W2 acceptance gate (estimated +2 weeks
per current V3-WORKSTREAMS effort estimate).

---

## 6. Honest caveats

1. **I have NOT run Island locally.** All claims about Island runtime
   behavior come from reading its source + docs + the
   `STACK-INTERFACES-AND-CONTAINER-COMPARISON.md` AlexYang authored.
   Source-trust is high (well-commented, code matches docs), but PNGs in
   `evidence/` were not viewed (would need image-render). The "77/77
   smoke" claim is sourced from `STATUS-FINAL.md` and the smoke script
   structure — not from a local rerun.
2. **HBC's coverage figures** ("5 forward + 6 reverse bridges, 530+ AIDL
   methods") come from V3-ARCHITECTURE.md citing CR-EE. CR-EE itself was
   read-only against HBC's `~/adapter/` on GZ05, not pulled into this
   repo as artifacts. Numbers are accurate per the chain of citation but
   not re-verified in this analysis.
3. **03-Req status** is from `03-REQUIREMENT-INDEX.md` (2026-05-19 today;
   agent 56's same-day analysis). Latest snapshot. wifi V7 + app.admin V7
   are the only filled pkgs; remaining 48 are workspace skeletons.
4. **Island's compatibility with V15 framework.jar** (claimed COMPLETE
   2026-05-18) was demonstrated with Hello30 + Hello67 ONLY. The 77-demo
   smoke is still on V14 per `integration-smoke.sh` (`libfakelog.so` +
   `libproperty_stub.so` LD_PRELOAD'd — that's the V15 incantation, so
   the smoke IS now V15, but the 77 individual demos haven't been re-run
   per the doc which lists "smoke 77 全套验证 (期望全绿 in V15)" as a
   future step).
5. **My evidence for "HBC has fewer per-app branches than Island"** is
   indirect: HBC's APK transparency invariant is explicit in CR-EE §11.1,
   and CR-FF §"Pattern 1" shows the 4 L5 patches handle ServiceManager
   delegation generically. Island's `APKRunner` has `if (apkPath.contains("openCalc"))`
   special-case (line 115-117 of APKRunner.java), and `-force-layout=0xRESID`
   per-APK CLI flag. Island has more demo-level per-app code than HBC has
   substrate-level per-app code — but Island doesn't have noice/McD
   substrate code yet either, so the comparison is uneven.
6. **Effort estimates in §3** are by-analogy and 50%-confidence. The
   `NeverDieAdapterDecorator` could be ½ day or 2 days depending on
   whether HBC's `OHEnvironment.newAdapterReflective` returns a
   `Proxy` (decorator straightforward) or a real adapter instance
   (decorator needs to bytecode-rewrite or proxy-wrap).
7. **I did not investigate AlexYang's team / IP licensing.** START-HERE.md
   says "code handoff" implying intended for Westlake consumption, but
   if the borrow becomes substantial (more than 5 patterns), license
   should be confirmed before integration.

---

## 7. Cross-references to V3 workstreams

| V3 W# | Workstream | Island borrow item |
|---|---|---|
| W4 | Adapter customization for Westlake scope | Borrow #3 (StubContext system-service routing matrix → V3-SYSTEMSERVICE-ROUTING.md) — directly feeds the W4 spec gap; Borrow #1 (NeverDieAdapterDecorator) wraps the W4 deliverable |
| W5 | Mock APK validation | Borrow #2 (LifecycleDriver) + Borrow #4 (smoke methodology) — mock APK validation IS V3's analog of Island's `integration-smoke.sh` |
| W6 | noice on OHOS via V3 | Borrow #2 (LifecycleDriver replaces V2 5-pillar lifecycle drive code in NoiceInProcessActivity); Borrow #4 (smoke includes noice render check) |
| W7 | McD on OHOS via V3 | Same as W6 for McdInProcessActivity. **GAP raised**: location / camera2 / biometric Σmcd=3 packages have no team coverage; flag as W14 sub-item or W4 expansion |
| W9 | Borrow HBC Tier-1 patterns (ScopedJniAttach, DEPLOY_SOP, restore script) | Add Borrow #4 (smoke script) + Borrow #5 (redline CI) to the W9 scope. These are Island Tier-1 borrows complementing HBC Tier-1 borrows |
| W11 | V2→V3 carryforward audit | Borrow #2 (LifecycleDriver) IS the canonical V2→V3 carryforward of the 5-pillar lifecycle drive pattern — explicit cross-link in W11 audit |
| **W14 (proposed)** | Island borrow integration | NEW workstream collecting all 5 borrows into one PR (~5 person-days). Subworkstreams: W14.1 NeverDieAdapterDecorator (½ day), W14.2 LifecycleDriver (1 day, with HBC-bug-first fallback path), W14.3 SystemServiceRouter + V3-SYSTEMSERVICE-ROUTING.md (1-2 days), W14.4 smoke-script (1-2 days), W14.5 V3-REDLINES.md + CI (½ day) |

### Cross-references to memory

- `feedback_two_pivots_in_two_days.md` — verdict §5 honors this rule:
  third pivot's evidence bar not cleared.
- `feedback_additive_shim_vs_architectural_pivot.md` — §5 invoked
  against Island's 9-layer per-APK stub-ratchet roadmap.
- `feedback_macro_shim_contract.md` — Borrow #2 caveats explicitly flag
  the `setAccessible(true)` conflict; Borrow #3 / #5 explicitly preserve
  the contract.
- `project_v3_hbc_reuse_direction.md` (referenced from V3-ARCHITECTURE
  §0) — V3 commitment unchanged; this doc extends rather than amends.

### Cross-references to artifacts

- Island source tree (extracted, read-only):
  `artifacts/v3-w3-westlake-stack/16.14-Island/`
- Island handoff packet: `artifacts/v3-w3-westlake-stack/westlake-stack-code-handoff/`
- V3 architecture: `docs/engine/V3-ARCHITECTURE.md`
- V3 workstreams: `docs/engine/V3-WORKSTREAMS.md`
- HBC structural overview: `docs/engine/CR-EE-HANBINGCHEN-ARCHITECTURE-ANALYSIS.md`
- HBC tactical patterns already borrowed: `docs/engine/CR-FF-HBC-BORROWABLE-PATTERNS.md`
- 03-Req corpus index: `docs/engine/03-REQUIREMENT-INDEX.md`
- V11 carryforward audit (related W11 deliverable): `docs/engine/V3-W11-CARRYFORWARD-AUDIT.md`

---

## Self-audit (per task contract)

- [x] All required files in §2 enumerated (12 Java + 1 C + 1 smoke + 1 demo + 4 NP runtime + 14 musl handlers + 8 docs)
- [x] All 4 borrow categories populated (AS-IS: 5, ADAPT: 8, ARCH-SPECIFIC: 8, REDLINE-INFO: 5)
- [x] Top-5 borrows have effort + V3 workstream cross-ref (W4, W5, W6, W7, W9, W11, proposed W14)
- [x] 3-way table covers 20 services/packages explicitly + top-10 03-Req cross-check
- [x] Architectural verdict has evidence anchors (5 for, 6 against, with citations)
- [ ] Local commit, no push (next step — caller handles)
