# WS2: MiniServer Engine — Status & Remaining Work

## Status: COMPLETE (all 6 tasks done)

All WS2 tasks from WS2-MINISERVER-COMPLETE.md have been implemented. The engine can parse an APK manifest, register activities/services, instantiate a custom Application class, and launch the main activity — all with a real message queue backing Handler/Looper.

## What Was Built

### Task 1: ApkLoader → MiniServer.loadApk() ✅
- `MiniServer.loadApk(String apkPath)` — parses APK, registers activities/services, instantiates Application class
- `MiniServer.loadAndLaunch(String apkPath)` — loads APK then starts launcher activity
- **File:** `shim/java/android/app/MiniServer.java` (lines 100-150)

### Task 2: Handler/Looper/MessageQueue ✅
- `MessageQueue` — PriorityQueue-based with time-ordered delivery, `drainReady()`, `drainAll()`
- `Handler` — queue-based `post()`, `postDelayed()`, `sendMessage()` routing to Looper's MessageQueue
- `Looper` — `getQueue()`, `pumpMessages()`, `flushAll()` static helpers for engine main loop
- `Message` — object pool, `target`/`callback`/`when` fields, all `obtain()` variants
- **Files:** `shim/java/android/os/{MessageQueue,Handler,Looper,Message}.java`

### Task 3: Resources Stub ✅
- `Resources.getDisplayMetrics()` returns DisplayMetrics with xhdpi defaults (density=2.0, 1080x1920)
- `Resources.getConfiguration()` returns Configuration
- `Resources.getString(int)` returns fallback string
- `Context.getResources()` lazy-initializes non-null Resources
- **Files:** `shim/java/android/content/res/Resources.java`, `shim/java/android/content/Context.java`

### Task 4: ContentResolver Wiring ✅
- `query()`, `insert()`, `update()`, `delete()` all route to ContentProviders via `MiniPackageManager.resolveProvider(authority)`
- **File:** `shim/java/android/content/ContentResolver.java`

### Task 5: Application Class Wiring ✅
- `ApkInfo.applicationClassName` field present
- `BinaryXmlParser` parses `<application android:name="...">` from AXML manifests
- `MiniServer.loadApk()` instantiates custom Application subclass via `Class.forName()`, falls back to default
- **Files:** `shim/java/android/app/{ApkInfo,BinaryXmlParser,MiniServer}.java`

### Task 6: Tests ✅
- 840 tests passing, 0 failures
- Handler/Looper/MessageQueue, MiniServer lifecycle, ApkLoader, BinaryXmlParser all covered in HeadlessTest.java

## What Comes Next (Post-WS2)

WS2 delivered the **headless engine runtime**. The next workstreams build on top of it:

### WS1: Canvas → OH_Drawing Bridge (NOT STARTED — highest priority)
- Wire `Canvas.drawRect()`, `drawCircle()`, `drawText()` through JNI to OH_Drawing NDK (Skia-backed)
- ~45 new native methods in OHBridge (Canvas, Pen, Brush, Path, Bitmap, Font)
- Currently all Canvas methods are no-ops
- **This is the single gate to any visible rendering**
- Skill file: `skills/WS1-CANVAS-BRIDGE.md`

### WS4: Input Bridge (NOT STARTED — no skill file yet)
- Route native OHOS touch/key events into Android View tree dispatch
- Requires WS1 (rendering) to be useful — no point dispatching touch events if nothing is drawn
- Need to implement: `View.dispatchTouchEvent()`, `MotionEvent`, `KeyEvent` flow

### OHBridge Native Side (~100 JNI methods declared, 0 implemented)
- All methods in `shim/java/com/ohos/shim/bridge/OHBridge.java` are `native` declarations
- Rust native implementation (`liboh_bridge.so`) needs to be built
- Categories: SharedPreferences, RdbStore, Notification, Network, WiFi, Audio, MediaPlayer, Location, Telephony
- Currently only tested via mock (`test-apps/mock/com/ohos/shim/bridge/OHBridge.java`)

### On-Device Validation (NEVER TESTED)
- Dalvik VM builds for x86_64, OHOS aarch64, OHOS ARM32
- Full stack (Dalvik + shims + OHBridge + OH_Drawing) has never run end-to-end on a real device
- Need: hdc push, device test, real API calls through OHBridge

### Shim Quality (560/1,967 files still contain `return null`)
- Engine approach deprioritizes bulk shimming — only ~15 platform boundaries matter
- App-specific APIs may need implementation as real APKs are tested
