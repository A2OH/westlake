# Android-as-Engine: Execution Plan

**Date:** 2026-03-14
**Replaces:** Per-API shimming approach
**Builds on:** 02-ANDROID-AS-ENGINE.md (architecture), 00-ANALYSIS-PLAN.md (findings)

---

## Current State Assessment

### What We Have

| Component | Status | Details |
|-----------|--------|---------|
| **Dalvik VM** | Working | Runs single-DEX on x86_64, OHOS aarch64, ARM32. Activity lifecycle executes. GC works. |
| **Boot JARs** | Working | core-android-x86.jar (~4000 classes), core-kitkat.jar (280 classes) |
| **Java Shim Layer** | 1,968 stubs | 535 tests pass. ~50 classes have real logic (Bundle, Intent, SharedPreferences, SQLiteDatabase, Notification, etc.) |
| **OHBridge JNI** | 106 native methods | Java declarations + Rust bridge + C++ wrapper. Connects to OH Preferences, RdbStore, HiLog, ArkUI Node API, Notification, HTTP, DeviceInfo |
| **Mock Bridge** | Working | In-memory mock for JVM testing without OHOS device |
| **ArkUI Node API** | Wired | 26 node types, 400+ attributes, 14 event types. Event dispatch path: ArkUI → C++ → Rust → Java |
| **Canvas/Paint/Bitmap** | Stubs only | No OH_Drawing integration. Canvas.draw*() are all no-ops |
| **Rendering bridge** | Not started | No Canvas → OH_Drawing_Canvas connection |
| **MiniServer** | Not started | No Activity stack, no View tree management, no manifest parsing |
| **APK loader** | Not started | No AndroidManifest.xml parsing, no multi-DEX loading |

### Key Insight: Two Rendering Paths Exist

The bridge already has **ArkUI Node API** integration (node create/dispose, attributes, events). This is a second rendering path alongside the planned **Canvas → OH_Drawing** Skia path:

```
Path A (Skia/Canvas):                    Path B (ArkUI Node):
Android View.draw(Canvas)               Android View → ArkUI Node
  → Canvas.drawRect/Text/Path             → OHBridge.nodeCreate(ROW)
  → JNI → OH_Drawing_Canvas               → OHBridge.nodeSetAttr(WIDTH, 100)
  → Skia → GPU → Display                  → ArkUI renders natively

Pros: Perfect pixel fidelity              Pros: Native look & feel
Cons: Custom rendering, no native theme   Cons: Paradigm mismatch
Best for: Games, custom UI, WebView       Best for: Standard UI widgets
```

**Recommendation:** Start with Path B (ArkUI nodes) for standard widgets — it's already wired. Add Path A (Canvas/Skia) later for custom drawing.

---

## Execution Plan: 4 Workstreams

### WS1: Canvas → OH_Drawing Rendering Bridge

**Goal:** Android Canvas.draw*() calls produce real pixels on OH

**Why first:** Unlocks ALL Android UI — every View, every layout, every animation.

**Approach:** Wire Canvas JNI methods to OH_Drawing_Canvas NDK API.

```
Current:  Canvas.drawRect() → no-op (returns immediately)
Target:   Canvas.drawRect() → JNI → OH_Drawing_CanvasDrawRect() → pixels
```

**Tasks:**

| # | Task | Effort | File(s) |
|---|------|--------|---------|
| 1.1 | Add OH_Drawing native methods to OHBridge.java | S | OHBridge.java |
| 1.2 | Implement Canvas JNI bridge in Rust (oh_drawing.rs) | L | bridge/rust/src/oh_drawing.rs |
| 1.3 | Wire Canvas.java to OHBridge drawing calls | M | graphics/Canvas.java |
| 1.4 | Wire Paint.java to OH_Drawing_Pen/Brush | M | graphics/Paint.java |
| 1.5 | Wire Bitmap.java to OH_Drawing_Bitmap | M | graphics/Bitmap.java |
| 1.6 | Wire Path.java to OH_Drawing_Path | M | graphics/Path.java |
| 1.7 | Surface/XComponent integration (draw buffer lifecycle) | L | new: SurfaceBridge.java |
| 1.8 | Test: render colored rectangles, text, bitmaps | M | test app |

**OH_Drawing methods to bridge (minimum viable):**

```java
// In OHBridge.java — new native declarations
// Canvas
native static long canvasCreate(long bitmap);
native static void canvasDrawRect(long canvas, float l, float t, float r, float b, long pen, long brush);
native static void canvasDrawCircle(long canvas, float cx, float cy, float r, long pen, long brush);
native static void canvasDrawLine(long canvas, float x1, float y1, float x2, float y2, long pen);
native static void canvasDrawPath(long canvas, long path, long pen, long brush);
native static void canvasDrawBitmap(long canvas, long bitmap, float x, float y);
native static void canvasDrawText(long canvas, String text, float x, float y, long font, long pen, long brush);
native static void canvasSave(long canvas);
native static void canvasRestore(long canvas);
native static void canvasTranslate(long canvas, float dx, float dy);
native static void canvasScale(long canvas, float sx, float sy);
native static void canvasRotate(long canvas, float degrees, float px, float py);
native static void canvasClipRect(long canvas, float l, float t, float r, float b);
native static void canvasClipPath(long canvas, long path);
// Pen (stroke) / Brush (fill) — maps to Android Paint
native static long penCreate();
native static void penSetColor(long pen, int argb);
native static void penSetWidth(long pen, float width);
native static void penSetAntiAlias(long pen, boolean aa);
native static long brushCreate();
native static void brushSetColor(long brush, int argb);
// Bitmap
native static long bitmapCreate(int width, int height, int format);
native static void bitmapDestroy(long bitmap);
// Path
native static long pathCreate();
native static void pathMoveTo(long path, float x, float y);
native static void pathLineTo(long path, float x, float y);
native static void pathClose(long path);
// Font / Text
native static long fontCreate();
native static void fontSetSize(long font, float size);
```

**OH NDK headers to link:**
```c
#include <native_drawing/drawing_canvas.h>
#include <native_drawing/drawing_pen.h>
#include <native_drawing/drawing_brush.h>
#include <native_drawing/drawing_bitmap.h>
#include <native_drawing/drawing_path.h>
#include <native_drawing/drawing_font.h>
#include <native_drawing/drawing_text_blob.h>
```

---

### WS2: MiniServer (Activity/Window/Package Management)

**Goal:** One Java class (~2000 lines) that replaces Android's SystemServer for single-app execution.

**Why:** Without this, `startActivity()`, `setContentView()`, `getSystemService()` are all no-ops. No app can actually run.

**Architecture:**

```java
public class MiniServer {
    // Singleton — created at engine startup
    private static MiniServer sInstance;

    // Sub-managers
    final MiniActivityManager activities;
    final MiniWindowManager windows;
    final MiniPackageManager packages;
    final MiniContentResolver content;

    // Entry point
    public void launchApp(String apkPath) {
        packages.parseManifest(apkPath);
        ComponentName launcher = packages.getLauncherActivity();
        activities.startActivity(new Intent().setComponent(launcher));
    }
}
```

**Tasks:**

| # | Task | Effort | File(s) |
|---|------|--------|---------|
| 2.1 | MiniActivityManager: Activity stack + lifecycle dispatch | L | new: app/MiniActivityManager.java |
| 2.2 | Wire Activity.startActivity() → MiniActivityManager | M | app/Activity.java |
| 2.3 | Wire Activity.setContentView() → MiniWindowManager | M | app/Activity.java, view/Window.java |
| 2.4 | Wire Context.getSystemService() → return mini managers | M | content/Context.java |
| 2.5 | MiniWindowManager: View tree per Activity, layout/draw dispatch | L | new: view/MiniWindowManager.java |
| 2.6 | MiniPackageManager: Parse AndroidManifest.xml, resolve Intents | L | new: content/pm/MiniPackageManager.java |
| 2.7 | MiniContentResolver: Route to app's ContentProviders | M | new: content/MiniContentResolver.java |
| 2.8 | MiniServer: Orchestrate startup, wire everything together | M | new: app/MiniServer.java |
| 2.9 | Test: headless Activity lifecycle (onCreate→onResume→finish→onDestroy) | M | test app |
| 2.10 | Test: startActivityForResult round-trip | M | test app |

**MiniActivityManager core (~500 lines):**

```java
public class MiniActivityManager {
    private final ArrayList<ActivityRecord> mStack = new ArrayList<>();
    private ActivityRecord mResumed; // only 1 at a time

    public void startActivity(Intent intent) {
        // 1. Resolve component (explicit or via MiniPackageManager)
        // 2. Instantiate Activity class via reflection
        // 3. Pause current top activity
        // 4. Push new ActivityRecord onto stack
        // 5. Call onCreate(savedInstanceState) → onStart() → onResume()
    }

    public void finishActivity(Activity activity) {
        // 1. Call onPause() → onStop() → onDestroy()
        // 2. Pop from stack
        // 3. If result pending, call caller's onActivityResult()
        // 4. Resume new top activity
    }

    static class ActivityRecord {
        Activity activity;
        Intent intent;
        ComponentName component;
        int resultCode;
        Intent resultData;
        ActivityRecord caller;
        int requestCode;
    }
}
```

**MiniPackageManager core (~400 lines):**

```java
public class MiniPackageManager {
    // Parsed from AndroidManifest.xml
    private String packageName;
    private final List<ActivityInfo> activities = new ArrayList<>();
    private final List<ServiceInfo> services = new ArrayList<>();
    private final List<ProviderInfo> providers = new ArrayList<>();
    private final List<String> permissions = new ArrayList<>();
    private final Map<String, List<IntentFilter>> intentFilters = new HashMap<>();

    public void parseManifest(String apkPath) {
        // Use Android's XmlPullParser or binary XML parser
        // Extract package name, activities, services, intent-filters
    }

    public ComponentName getLauncherActivity() {
        // Find activity with action=MAIN + category=LAUNCHER
    }

    public ResolveInfo resolveActivity(Intent intent) {
        // Explicit: return matching component directly
        // Implicit: match action + categories against intent-filters
    }
}
```

---

### WS3: APK Loader (DEX + Manifest + Resources)

**Goal:** Load a real .apk file, parse its manifest, load its DEX files, and launch the main Activity.

**Why:** This is the "demo moment" — load a real APK and see it do something.

**Tasks:**

| # | Task | Effort | File(s) |
|---|------|--------|---------|
| 3.1 | APK unzip (APK is a ZIP file) | S | new: apk/ApkLoader.java or C++ |
| 3.2 | Binary AndroidManifest.xml parser | L | new: apk/BinaryXmlParser.java |
| 3.3 | Multi-DEX class loading (TikTok has 45 DEX files!) | M | Dalvik VM: classloader changes |
| 3.4 | Resource table parser (resources.arsc) — basic string/layout | L | new: apk/ResourceTable.java |
| 3.5 | Wire to MiniServer: ApkLoader.load() → MiniServer.launchApp() | M | MiniServer.java |
| 3.6 | Test: load a simple APK, print its manifest activities | M | test |
| 3.7 | Test: load multi-DEX APK, instantiate classes from DEX 2+ | M | test |

**Note on binary XML:** Android's compiled AndroidManifest.xml uses a binary format (not text XML). We need a parser for this. Options:
- Port AOSP's `ResXMLParser` (C++, ~1000 lines)
- Use an existing Java library (e.g., AXMLParser)
- Write a minimal parser for just `<activity>`, `<service>`, `<intent-filter>` tags

**Note on resources:** Full resource resolution (`R.layout.activity_main` → XML → View inflation) is complex. For Phase 1, skip resource inflation — apps set content views programmatically or we return stub views.

---

### WS4: Input Bridge (Touch/Key Events)

**Goal:** OH touch/key events reach Android View.dispatchTouchEvent() / dispatchKeyEvent()

**Why:** Without input, apps render but can't be interacted with.

**Approach:** Already partially wired — ArkUI event dispatch exists. Need to add:

```
XComponent.DispatchTouchEvent(x, y, type)
  → JNI → Java MotionEvent
  → MiniWindowManager.currentActivity().getWindow().getDecorView()
  → View.dispatchTouchEvent(event)    ← Android View tree handles this
```

**Tasks:**

| # | Task | Effort | File(s) |
|---|------|--------|---------|
| 4.1 | MotionEvent: implement with x, y, action, pointerId | M | view/MotionEvent.java |
| 4.2 | KeyEvent: implement with keyCode, action, meta | M | view/KeyEvent.java |
| 4.3 | Wire XComponent touch → MotionEvent → View tree | M | MiniWindowManager + bridge |
| 4.4 | Wire XComponent key → KeyEvent → View tree | S | MiniWindowManager + bridge |
| 4.5 | Back button → Activity.onBackPressed() → finish() | S | MiniActivityManager |
| 4.6 | Test: touch event reaches View.onTouchEvent() | M | test app |

---

## Dependency Graph

```
                    ┌──────────────┐
                    │  WS3: APK    │
                    │  Loader      │
                    └──────┬───────┘
                           │ loads manifest + DEX
                           ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│  WS1: Canvas │    │  WS2: Mini   │    │  WS4: Input  │
│  → OH_Drawing│    │  Server      │    │  Bridge      │
└──────┬───────┘    └──────┬───────┘    └──────┬───────┘
       │                   │                   │
       │    ┌──────────────┘                   │
       │    │                                  │
       ▼    ▼                                  ▼
┌─────────────────────────────────────────────────────┐
│  MILESTONE: "Hello Android" APK renders on OH       │
│  Load APK → parse manifest → start Activity →       │
│  setContentView → View.draw(Canvas) → OH_Drawing →  │
│  pixels on screen → touch events work               │
└─────────────────────────────────────────────────────┘
```

**WS1 and WS2 can be developed in parallel.** WS3 and WS4 depend on WS2.

---

## Milestone Targets

### M1: Headless Activity Lifecycle (WS2 only, no rendering)
**Target:** 2 weeks
- MiniActivityManager manages Activity stack
- startActivity() → onCreate → onResume works
- finish() → onPause → onStop → onDestroy works
- startActivityForResult() round-trip works
- MiniPackageManager parses hardcoded manifest data
- All testable headlessly (no device needed)

### M2: Canvas Rendering on Linux (WS1, no device)
**Target:** 3 weeks
- Canvas → OH_Drawing_Canvas bridge compiles
- Test app draws colored rectangles, text, circles to a bitmap
- Bitmap can be saved to PNG for visual verification
- Runs on x86_64 Linux using OH_Drawing CPU path

### M3: APK Loading (WS3)
**Target:** 4 weeks
- Load real .apk file (unzip, parse binary manifest)
- Multi-DEX class loading works
- Launcher Activity is identified and instantiated
- Combined with M1: APK's Activity lifecycle runs headlessly

### M4: "Hello Android" Demo (WS1 + WS2 + WS3 + WS4)
**Target:** 6 weeks
- Simple APK with a Button and TextView
- Loads, renders UI, responds to touch
- Running on OHOS aarch64 (real device or QEMU)
- Video-worthy demo

---

## What Happens to the Shim Work?

The 1,968 Java shim classes and 535 tests remain valuable:

1. **Boot classpath** — Dalvik needs `android.*` classes on the classpath. Our stubs provide the compile target. At runtime, real AOSP framework code replaces the stubs for complex classes, but simple data classes (Bundle, Intent, ContentValues) use our implementations directly.

2. **Testing infrastructure** — The headless test harness validates API behavior without a device. Keep running and expanding tests.

3. **Fallback** — For classes where AOSP source is too complex to port (Camera2 internals, MediaCodec), our stubs provide graceful degradation (return null instead of crash).

4. **Developer reference** — The shim implementations document expected behavior.

**No more bulk shimming.** Future shim work should be targeted at classes needed by WS1-4, not broad API coverage.

---

## Parallel Work Allocation

| Workstream | Can be parallelized? | Ideal worker |
|------------|---------------------|--------------|
| WS1: Canvas bridge | Yes (independent C/Rust) | C/C++ developer |
| WS2: MiniServer | Yes (pure Java) | Java developer (CC worker) |
| WS3: APK loader | After WS2 manifest parsing | Java + binary format expertise |
| WS4: Input bridge | After WS2 window management | C/Java bridge developer |

CC workers can immediately start on **WS2** — it's pure Java, self-contained, and testable headlessly.

---

## Files to Create

```
NEW FILES:
  shim/java/android/app/MiniServer.java           ← WS2: orchestrator
  shim/java/android/app/MiniActivityManager.java   ← WS2: activity stack
  shim/java/android/view/MiniWindowManager.java    ← WS2: view tree mgmt
  shim/java/android/content/pm/MiniPackageManager.java ← WS2: manifest + resolution
  shim/java/android/content/MiniContentResolver.java   ← WS2: provider routing
  shim/bridge/rust/src/oh_drawing.rs               ← WS1: Canvas bridge
  shim/java/android/apk/ApkLoader.java             ← WS3: APK loading
  shim/java/android/apk/BinaryXmlParser.java        ← WS3: manifest parser

MODIFY:
  shim/java/android/graphics/Canvas.java           ← WS1: wire to OHBridge
  shim/java/android/graphics/Paint.java            ← WS1: wire to Pen/Brush
  shim/java/android/graphics/Bitmap.java           ← WS1: wire to OH_Drawing_Bitmap
  shim/java/android/graphics/Path.java             ← WS1: wire to OH_Drawing_Path
  shim/java/android/app/Activity.java              ← WS2: wire to MiniServer
  shim/java/android/content/Context.java           ← WS2: getSystemService()
  shim/java/android/view/Window.java               ← WS2: setContentView()
  shim/java/com/ohos/shim/bridge/OHBridge.java     ← WS1: add canvas native methods
  shim/bridge/rust/src/lib.rs                      ← WS1: register new JNI methods
  shim/bridge/rust/build.rs                        ← WS1: link OH_Drawing libs
```
