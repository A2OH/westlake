# Westlake Engine: Android Phone Test Plan

**Date:** 2026-03-23
**Status:** Planning — x86_64 e2e proven, ready for ARM64 native testing

---

## Goal

Use an Android phone as a native ARM64 test platform for the Westlake engine. The phone provides real hardware (CPU, display, touch, SQLite, filesystem) while our engine runs as a self-contained ART runtime with the full Westlake shim layer.

This tests **95% of the Westlake codebase** at native speed — the only untested parts are OHOS-specific kernel/service behaviors (<5%).

---

## Why

QEMU ARM64 emulation adds ~100x overhead. ART's `InitNativeMethods` takes 30+ minutes on emulated ARM64 vs <1 second on native. An Android phone provides native ARM64 execution for free.

| Platform | ART startup | Frame rate | Display |
|----------|------------|------------|---------|
| x86_64 host | <1s | 60fps | No (stubs) |
| QEMU ARM64 | 30+ min (hangs) | untested | VNC (blocked) |
| Android phone | ~1-2s (projected) | 60fps (projected) | Real screen |

---

## Three phases

### Phase 1: CLI validation (Termux, no display)

**Effort:** 1 day
**Requires:** Any ARM64 Android phone, Termux from F-Droid, USB cable

Run dalvikvm as a command-line process in Termux. Validates ART runtime, Westlake Java classes, Activity lifecycle, and all non-display functionality.

```
Phone (Termux)
└── dalvikvm (11MB ARM64 static binary)
    ├── boot.art + boot.oat (AOT compiled)
    ├── core-oj.jar + core-libart.jar + core-icu4j.jar
    ├── aosp-shim.dex (Westlake 3400+ classes)
    ├── liboh_bridge.so (stub — log to stdout, no display)
    └── app.dex (MockDonalds or target APK's DEX)
```

**Setup:**
```bash
# On PC
adb push deploy-package/ /data/local/tmp/a2oh/
# Or via Termux SSH:
scp -r deploy-package/ phone:~/a2oh/

# On phone (Termux or adb shell)
cd /data/local/tmp/a2oh  # or ~/a2oh in Termux
chmod +x dalvikvm
export ANDROID_DATA=$PWD ANDROID_ROOT=$PWD
./dalvikvm \
  -Xbootclasspath:core-oj.jar:core-libart.jar:core-icu4j.jar:aosp-shim.dex \
  -Ximage:boot.art -Xverify:none \
  -Djava.library.path=$PWD \
  -classpath app.dex \
  com.example.mockdonalds.MockDonaldsApp
```

**What's tested:**
- [x] ART runtime boot with boot image on real ARM64
- [x] Class loading from DEX (boot classpath + app classpath)
- [x] System.loadLibrary → dlopen → JNI_OnLoad → RegisterNatives
- [x] Activity lifecycle: create → start → resume → pause → stop → destroy
- [x] View tree: measure → layout → draw pipeline
- [x] Intent extras, Bundle serialization
- [x] SharedPreferences (stub or file-backed)
- [x] SQLite database operations (stub)
- [x] BaseAdapter + ListView population
- [x] Canvas drawing API calls (no-op stubs, validates call chain)
- [x] ScrollView touch interception logic
- [x] DefaultTheme + drawable rendering calls
- [x] Activity navigation (startActivity, finish, onActivityResult)

**Success criteria:** Same output as x86_64 test:
```
[MockDonaldsApp] MenuActivity launched
[MockDonaldsApp] Creating surface 480x800
[MockDonaldsApp] Initial frame rendered
[MockDonaldsApp] Frame 600 activity=MenuActivity
```

**Not tested:** Display output, touch input, real persistence.

---

### Phase 2: Display + touch (Android host APK)

**Effort:** 3-5 days
**Requires:** Android Studio (or manual APK build), same phone

Build an Android APK that hosts the Westlake engine. The APK provides a `SurfaceView` for real pixel rendering and forwards touch events into the engine.

```
Android APK (WestlakeHost.apk)
├── WestlakeHostActivity.java
│   ├── SurfaceView (provides ANativeWindow for rendering)
│   ├── onTouchEvent() → forwards to Westlake engine
│   └── Thread: runs dalvikvm or loads engine via JNI
├── lib/arm64-v8a/
│   ├── liboh_bridge_android.so (Android-backed bridge)
│   └── libwestlake_engine.so (optional: engine as shared lib)
├── assets/
│   ├── boot.art, boot.oat, core-*.jar, aosp-shim.dex
│   └── app.dex (target application)
└── AndroidManifest.xml
```

**Android-backed liboh_bridge.so implementation:**

| OHBridge JNI method | Android NDK implementation |
|---|---|
| `surfaceCreate(w,h)` | `ANativeWindow_fromSurface()` from host SurfaceView |
| `surfaceFlush()` | `ANativeWindow_unlockAndPost()` |
| `surfaceGetCanvas()` | `ANativeWindow_lock()` → pixel buffer |
| `canvasDrawRect/Text/...` | Skia `SkCanvas` (link against phone's libskia or bundle our own) |
| `rdbStoreOpen(name)` | `sqlite3_open()` from `/system/lib64/libsqlite.so` |
| `rdbStoreQuery(sql)` | `sqlite3_prepare/step/finalize` |
| `preferencesOpen/Get/Put` | Read/write JSON file in app's `filesDir` |
| `logDebug/Info/Warn/Error` | `__android_log_print(ANDROID_LOG_DEBUG, tag, msg)` |
| `fontMeasureText` | FreeType `FT_Load_Char` + advance width |
| `fontGetMetrics` | FreeType `FT_Face->size->metrics` |
| `showToast` | JNI callback to host Activity → `Toast.makeText()` |
| Touch input | Host `onTouchEvent` → JNI → `OHBridge.dispatchTouchEvent` |

**What's newly tested:**
- [x] Real pixel rendering on phone screen
- [x] Real touch input → View.onTouchEvent → click handlers
- [x] Real SQLite database (create table, insert, query, update, delete)
- [x] Real file-backed SharedPreferences
- [x] Real font measurement and text rendering
- [x] Scroll physics with real finger gestures
- [x] Activity transition rendering (screen redraws on navigation)
- [x] Full MockDonalds user flow: browse menu → tap item → add to cart → checkout

**Success criteria:** User can interact with MockDonalds on the phone screen — tap menu items, scroll the list, add to cart, and complete checkout. All rendered by Westlake, not by Android's own View system.

---

### Phase 3: Run real APKs (APK compatibility testing)

**Effort:** Ongoing
**Requires:** Phase 2 complete

Extract `classes.dex` from real APKs and run them through the Westlake engine. This validates API coverage and identifies missing shim classes.

```bash
# Extract DEX from any APK
unzip SomeApp.apk classes.dex -d /tmp/
# Package as target app
cp /tmp/classes.dex assets/app.dex
# Launch through WestlakeHost
adb install WestlakeHost.apk
adb shell am start -n com.westlake.host/.WestlakeHostActivity \
  --es target_class "com.example.someapp.MainActivity"
```

**Target APK categories (by complexity):**

| Tier | App type | Example | Key APIs needed |
|------|----------|---------|-----------------|
| A | Calculator/converter | Simple Calculator | Button, TextView, LinearLayout, onClick |
| B | List-based apps | Todo list, Notes | ListView, SQLite, EditText, RecyclerView |
| C | Multi-activity | Settings app | Multiple Activities, PreferenceScreen, Intent |
| D | Network apps | RSS reader | HttpURLConnection, JSON parsing, AsyncTask |
| E | Complex UI | Social media | Fragment, ViewPager, RecyclerView, ImageView |

**Phase 3a (Tier A+B):** Simple apps with programmatic UI or basic XML layouts.

**Phase 3b (Tier C+D):** Requires XML layout inflation — needs `LayoutInflater` + compiled XML resource parser for `resources.arsc`. This is the **single biggest gap** to close for real APK compatibility.

**Phase 3c (Tier E):** Requires Fragments, RecyclerView, and other AndroidX/support library shims.

---

## Deployment package

All files needed for phone testing, built by Agent A:

```
deploy-package/            (~30MB)
├── dalvikvm               ARM64 static binary (11MB)
├── boot.art               Pre-compiled boot image (684KB)
├── boot.oat               AOT compiled code (10MB)
├── boot.vdex              Verification data (7KB)
├── arm64/                 ISA subdirectory (copies of boot*)
├── core-oj.jar            OpenJDK core classes (5MB)
├── core-libart.jar        ART-specific classes (660KB)
├── core-icu4j.jar         ICU4J Unicode/i18n (2.6MB)
├── aosp-shim.dex          Westlake shim layer (3.7MB)
├── liboh_bridge.so        JNI bridge (ARM64 shared lib)
├── libicu_jni.so          ICU JNI stubs
├── libjavacore.so         Core I/O native methods
├── libopenjdk.so          File/Math/ZipFile natives
├── app.dex                Target application
├── dalvik-cache/arm64/    Empty (ART creates cache here)
└── run.sh                 Launch script
```

**run.sh:**
```bash
#!/bin/sh
DIR=$(dirname "$0")
cd "$DIR"
export ANDROID_DATA="$DIR" ANDROID_ROOT="$DIR"
export LD_LIBRARY_PATH="$DIR"
chmod +x dalvikvm 2>/dev/null
./dalvikvm \
  -Xbootclasspath:core-oj.jar:core-libart.jar:core-icu4j.jar:aosp-shim.dex \
  -Ximage:boot.art \
  -Xverify:none \
  -Djava.library.path="$DIR" \
  -classpath app.dex \
  "${1:-com.example.mockdonalds.MockDonaldsApp}" \
  2>&1
```

---

## Test matrix

| Test case | Phase 1 (CLI) | Phase 2 (Display) | Phase 3 (APK) |
|-----------|:---:|:---:|:---:|
| ART boot + boot image | ✅ | ✅ | ✅ |
| Activity lifecycle | ✅ | ✅ | ✅ |
| View measure/layout/draw | ✅ (no pixels) | ✅ | ✅ |
| Canvas drawing | calls only | real pixels | real pixels |
| Touch events | — | ✅ | ✅ |
| SQLite CRUD | stubs | real | real |
| SharedPreferences | stubs | real | real |
| ListView + Adapter | ✅ | ✅ | ✅ |
| ScrollView scroll | logic only | real gestures | real gestures |
| Activity navigation | ✅ | ✅ | ✅ |
| Font rendering | stubs | real FreeType | real FreeType |
| XML layout inflation | — | — | Phase 3b |
| Network I/O | — | — | Phase 3c |
| Real APK DEX | — | — | ✅ |

---

## Hardware requirements

**Minimum:** Any ARM64 Android phone, Android 7+, 100MB free storage
**Recommended:** Android 10+, 4+ cores, 4GB+ RAM (for smooth Phase 2 rendering)
**Budget option:** Used Redmi/Samsung ~$30-50

No root required for any phase. Phase 1 needs only Termux or USB debugging. Phase 2 needs the WestlakeHost APK installed (sideload, no Play Store needed).

---

## Relationship to OHOS QEMU testing

| Concern | Android phone | OHOS QEMU |
|---------|:---:|:---:|
| Westlake Java layer | ✅ Primary | Secondary |
| ART runtime correctness | ✅ Native ARM64 | ❌ Too slow |
| Real display output | ✅ Phone screen | ⏳ VNC (blocked) |
| Real touch input | ✅ Touchscreen | ⏳ virtio-tablet |
| OHOS kernel behavior | ❌ | ✅ |
| OHOS init/services | ❌ | ✅ |
| OHOS ArkUI integration | ❌ | Future P2 |
| Performance benchmarking | ✅ Real hardware | ❌ Emulated |

**Strategy:** Develop and validate on Android phone (fast iteration), final integration test on OHOS QEMU or real OHOS hardware.
