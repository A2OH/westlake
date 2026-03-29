# Westlake Engine — Complete Newbie Guide

**How to build and run MockDonalds + Dialer on an Android phone**

This guide walks you through the entire process from zero to running multiple apps on a real Android phone using the Westlake engine.

---

## What Is This?

The Westlake Engine runs Android apps on OpenHarmony (OHOS) by providing a shim layer that translates Android API calls. To prove it works, we test on real Android phones first — the same code will run on OHOS once we bridge ~25 native functions.

**What you'll see on the phone:**
- A McDonald's ordering app (MockDonalds) — menu, item details, cart, checkout
- A phone dialer app — keypad, contacts, call history, in-call screen
- A calculator app — inflated from compiled XML layout

All three apps run inside a single host Activity (`WestlakeActivity`) using real Android Views (Button, TextView, LinearLayout, ListView).

---

## Repository Structure

**Repo:** `https://github.com/A2OH/westlake.git`

```
android-to-openharmony-migration/
├── westlake-host/              # Host APK that runs on the phone
│   ├── src/                    # WestlakeActivity.java (host Activity)
│   ├── jni/                    # Native bridge C code (ohbridge_android.c)
│   ├── build/                  # Build output
│   │   ├── assets/             # DEX files + AXML layouts baked into APK
│   │   │   ├── aosp-shim.dex  # 2,168 Android framework shim classes (3.6MB)
│   │   │   ├── app.dex        # MockDonalds + Dialer + shim combined (3.7MB)
│   │   │   └── *.axml         # Compiled XML layouts for testing
│   │   ├── lib/arm64-v8a/     # Native .so libraries
│   │   └── WestlakeEngine.apk # The signed APK you install
│   ├── AndroidManifest.xml    # Package: com.westlake.host, targetSdk=28
│   ├── deploy.sh              # One-command build + install + launch
│   └── build-apk.sh           # Full APK build script
│
├── shim/java/                  # 2,168 Android framework shim classes
│   ├── android/app/            # Activity, MiniServer, MiniActivityManager
│   ├── android/widget/         # Button, TextView, LinearLayout, ListView...
│   ├── android/view/           # View, ViewGroup, MotionEvent...
│   ├── android/graphics/       # Canvas, Paint, Path, Bitmap...
│   ├── android/content/        # Context, Intent, SharedPreferences...
│   ├── android/database/       # SQLiteDatabase, Cursor...
│   ├── com/ohos/shim/bridge/   # OHBridge.java (170 JNI method declarations)
│   └── ...                     # 187 packages total
│
├── test-apps/
│   ├── 04-mockdonalds/         # MockDonalds ordering app
│   │   └── src/com/example/mockdonalds/
│   │       ├── MockDonaldsApp.java   # Entry point (main method)
│   │       ├── MockApp.java          # 5-screen Material Design UI (815 lines)
│   │       ├── MenuItem.java         # Menu item data model
│   │       ├── MenuActivity.java     # Activity-based menu screen
│   │       ├── XmlTestHelper.java    # Binary XML layout inflater
│   │       └── ...
│   ├── 14-dialer/              # Phone dialer app
│   │   └── src/com/example/dialer/
│   │       ├── DialerApp.java        # 7-screen dialer UI (1,373 lines)
│   │       ├── DialerEntry.java      # Launcher/entry point
│   │       ├── Contact.java          # Contact data model
│   │       └── CallRecord.java       # Call history record model
│   └── 07-calculator/          # Calculator with XML layout
│
├── docs/engine/                # Technical documentation
│   ├── WESTLAKE-STATUS.md      # Current status report (English)
│   ├── WESTLAKE-STATUS_CN.md   # Current status report (Chinese)
│   ├── ARCHITECTURE.md         # System architecture
│   └── ...
│
├── README.md                   # Project overview
└── README_CN.md                # Project overview (Chinese)
```

---

## Prerequisites

### Software

| Tool | Purpose | Where to Get It |
|------|---------|-----------------|
| AOSP Android 11 source | Provides `android.jar`, `aapt`, `dx`, `apksigner` | Pre-downloaded at `/home/dspfac/aosp-android-11/` |
| JDK 8+ | Compile Java source to `.class` files | System JDK (`javac`) |
| ADB | Push APK to phone, view logs | [Android Platform Tools](https://developer.android.com/tools/releases/platform-tools) |
| USB cable | Connect phone to computer | Any USB-C / Micro-USB cable |

### Key Tool Paths (on our build machine)

```bash
ANDROID_JAR=/home/dspfac/aosp-android-11/prebuilts/sdk/30/public/android.jar
AAPT=/home/dspfac/aosp-android-11/prebuilts/sdk/tools/linux/bin/aapt
DX_JAR=/home/dspfac/aosp-android-11/prebuilts/sdk/tools/lib/dx.jar
APKSIGNER=/home/dspfac/aosp-android-11/prebuilts/sdk/tools/linux/bin/apksigner

# On Windows (WSL2 environment)
ADB=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe
```

### Phone Requirements

- Any Android phone with **Android 7.0+** (API 24+)
- **USB debugging enabled** (Settings → Developer options → USB debugging)
- Tested on: Huawei Mate 20 Pro (LYA-L29, Android 10)

---

## How It All Works

### Architecture (Big Picture)

```
Android Phone
├── WestlakeActivity.java ← The ONLY platform-specific Java (~230 lines)
│   ├── Creates child-first DexClassLoader
│   ├── Loads aosp-shim.dex (2,168 shim classes)
│   ├── Loads app.dex (MockDonalds + Dialer + Calculator)
│   └── Calls MockDonaldsApp.main()
│
├── app.dex (loaded at runtime by WestlakeActivity)
│   ├── MockDonalds app code
│   ├── Dialer app code
│   ├── Shim classes (android.widget.*, android.view.*, etc.)
│   ├── MiniServer (replaces Android's ActivityManagerService)
│   ├── MiniActivityManager (Activity lifecycle state machine)
│   └── OHBridge Java side (170 JNI method declarations)
│
├── liboh_bridge.so ← The ONLY platform-specific native code (~25 functions)
│   └── Bridges Canvas/Paint/Path/FontMetrics to real Android APIs
│
└── Phone's own Android framework
    └── Provides real Views: LinearLayout, ListView, Button, TextView
```

### ClassLoader Hierarchy

```
BootClassLoader (java.*, javax.*)
  └── PathClassLoader (WestlakeActivity, the host app)
        └── Child-First DexClassLoader (app.dex + aosp-shim.dex)
              ├── Our shim android.widget.* classes (loaded FIRST)
              ├── Our shim android.view.* classes
              ├── MockDonalds classes
              ├── Dialer classes
              └── Parent delegation: only for java.*, android.app.Activity
```

The **child-first** classloader is the key trick: it loads our shim classes
before the phone's framework classes. This lets us intercept and control
Activity lifecycle, View creation, and event dispatch.

### What Happens When the App Starts

1. User taps "Westlake Engine" icon
2. `WestlakeActivity.onCreate()` creates a SurfaceView
3. Surface ready → engine thread starts
4. `extractAsset("aosp-shim.dex")` + `extractAsset("app.dex")` to cache
5. Child-first DexClassLoader created with both DEX files
6. `MockDonaldsApp.main()` called via reflection
7. `MockDonaldsApp` detects real Android (WestlakeActivity.instance != null)
8. `MockApp.init(context)` creates menu data
9. `MockApp.showMenu()` builds real LinearLayout/ListView/Button views
10. `WestlakeActivity.setContentView(root)` displays the view tree
11. User sees the MockDonalds menu with Dialer and Calculator buttons

---

## Step-by-Step Build Process

### Step 1: Clone the Repository

```bash
git clone https://github.com/A2OH/westlake.git
cd westlake
```

### Step 2: Compile the App DEX

This compiles all shim classes + MockDonalds + Dialer into one DEX file.

```bash
# Set paths
REPO=$(pwd)
DX_JAR=/home/dspfac/aosp-android-11/prebuilts/sdk/tools/lib/dx.jar

# Create build directory
rm -rf /tmp/app-build && mkdir -p /tmp/app-build

# Collect Java sources
find shim/java -name "*.java" > /tmp/sources.txt
find test-apps/14-dialer/src -name "*.java" >> /tmp/sources.txt
cat >> /tmp/sources.txt << 'EOF'
test-apps/04-mockdonalds/src/com/example/mockdonalds/MockApp.java
test-apps/04-mockdonalds/src/com/example/mockdonalds/MenuItem.java
test-apps/04-mockdonalds/src/com/example/mockdonalds/MenuActivity.java
test-apps/04-mockdonalds/src/com/example/mockdonalds/XmlTestHelper.java
test-apps/04-mockdonalds/src/com/example/mockdonalds/MenuDbHelper.java
test-apps/04-mockdonalds/src/com/example/mockdonalds/CartManager.java
test-apps/04-mockdonalds/src/com/example/mockdonalds/MockDonaldsApp.java
EOF

# Compile Java → .class files
javac -source 8 -target 8 -d /tmp/app-build \
  -sourcepath "test-apps/14-dialer/src:test-apps/04-mockdonalds/src:shim/java:westlake-host/src" \
  @/tmp/sources.txt

# Convert .class → .dex
java -jar $DX_JAR --dex --min-sdk-version=26 --output=/tmp/app.dex /tmp/app-build

# Copy to assets
cp /tmp/app.dex westlake-host/build/assets/app.dex
```

**Expected output:** ~3,500+ classes compiled, `/tmp/app.dex` is ~3.7MB

### Step 3: Compile the Host APK

```bash
DIR=$REPO/westlake-host
BUILD=$DIR/build
ANDROID_JAR=/home/dspfac/aosp-android-11/prebuilts/sdk/30/public/android.jar
TOOLS=/home/dspfac/aosp-android-11/prebuilts/sdk/tools/linux/bin
DX_JAR=/home/dspfac/aosp-android-11/prebuilts/sdk/tools/lib/dx.jar

# 1. Compile WestlakeActivity.java
rm -rf $BUILD/classes && mkdir -p $BUILD/classes
javac -source 1.8 -target 1.8 -classpath $ANDROID_JAR \
  -d $BUILD/classes $DIR/src/com/westlake/host/WestlakeActivity.java

# 2. Convert to DEX
java -jar $DX_JAR --dex --output=$BUILD/classes.dex $BUILD/classes

# 3. Package APK (includes AndroidManifest + resources + assets)
$TOOLS/aapt package -f \
  -M $DIR/AndroidManifest.xml -S $DIR/res \
  -A $BUILD/assets -I $ANDROID_JAR \
  -F $BUILD/unsigned.apk

# 4. Add DEX + native libs to APK
cd $BUILD
zip -j unsigned.apk classes.dex
zip -r unsigned.apk lib/

# 5. Sign APK
jarsigner -keystore $BUILD/debug.keystore \
  -storepass android -keypass android \
  -signedjar $BUILD/WestlakeEngine.apk \
  $BUILD/unsigned.apk debug

# Result: westlake-host/build/WestlakeEngine.apk (~3MB)
```

### Step 4: Connect Phone and Install

```bash
ADB=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe  # adjust for your setup

# Verify phone is connected
$ADB devices
# Should show: LHS7N18B15001711    device

# Install APK
$ADB install -r $BUILD/WestlakeEngine.apk

# Launch
$ADB shell am start -n com.westlake.host/.WestlakeActivity
```

### Step 5: Use the App

1. **MockDonalds Menu** appears with food items in a scrollable list
2. Tap **Dialer** button (blue) at the top → full phone dialer
3. Tap **Calculator** button (green) → XML-inflated calculator
4. In MockDonalds: tap a food item → detail → add to cart → checkout
5. In Dialer: tap keypad numbers, switch to Recents/Contacts tabs, tap a contact to call

---

## How to Add a New App

To add your own app to the Westlake engine:

### 1. Create Your App

```
test-apps/15-myapp/src/com/example/myapp/
├── MyApp.java          # Your main UI code
└── MyAppEntry.java     # Entry point with launch(Context) method
```

```java
package com.example.myapp;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyApp {
    static Context ctx;
    static Activity hostActivity;

    public static void init(Context context) {
        ctx = context;
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            hostActivity = (Activity) host.getField("instance").get(null);
        } catch (Exception e) {}
    }

    public static void showMain() {
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        TextView hello = new TextView(ctx);
        hello.setText("Hello from MyApp!");
        hello.setTextSize(24);
        root.addView(hello);

        // Display it
        hostActivity.runOnUiThread(new Runnable() {
            public void run() { hostActivity.setContentView(root); }
        });
    }
}
```

### 2. Add a Launch Button

In `MockApp.java`, add a button in `showMenu()`:

```java
Button myAppBtn = new Button(ctx);
myAppBtn.setText("My App");
myAppBtn.setOnClickListener(new View.OnClickListener() {
    public void onClick(View v) {
        com.example.myapp.MyApp.init(ctx);
        com.example.myapp.MyApp.showMain();
    }
});
appsRow.addView(myAppBtn);
```

### 3. Rebuild and Deploy

Follow Steps 2-4 above, adding your new source files to the compile step.

---

## Debugging

### View Logs

```bash
# All app logs
$ADB logcat --pid=$($ADB shell "ps -A | grep westlake" | awk '{print $2}')

# Filter for key tags
$ADB logcat -d | grep -E "Westlake|MockDonalds|System.out"
```

### Common Issues

| Problem | Cause | Fix |
|---------|-------|-----|
| Black screen | ClassNotFoundException | Check all classes are in app.dex (verify with `dexdump`) |
| App crashes on start | Missing shim class | Add the missing class to `shim/java/` |
| Button not visible | Added after `show(root)` | Add views BEFORE calling show, or put below weight=1 ListView |
| Touch not working | WestlakeActivity touch dispatch | Ensure `shimRootView` is set |
| "OHBridge native: UNAVAILABLE" | Normal on phone | liboh_bridge.so JNI registration failed (harmless — app uses native Views) |

### Key Log Messages

```
[MockDonaldsApp] Starting on OHOS + ART ...     # App entry point
[MockDonaldsApp] OHBridge native: UNAVAILABLE    # Normal on phone (no OHOS)
[MockDonaldsApp] Running with native Android Views  # Using real phone Views
[MockDonaldsApp] Menu displayed                  # UI is up and running
```

---

## Platform Coupling Summary

Only **two things** change per platform:

| Component | Lines | What It Does |
|-----------|-------|--------------|
| `WestlakeActivity.java` | ~230 | Host Activity, child-first ClassLoader, asset extraction |
| `liboh_bridge.so` | ~25 functions | Canvas/Paint/Path/FontMetrics JNI bridge |

Everything else (3,500+ classes, all app code) is **platform-independent**.

To port to OpenHarmony: reimplement those ~25 C functions against ArkUI/Skia.

---

## VM Pipeline (Subprocess Mode) — Real Stock APK Support

The Westlake VM pipeline runs Android apps in a **separate dalvikvm process** communicating via stdout pipe. This is the architecture that supports real Play Store APKs.

### Architecture

```
Host Android App (Compose)          dalvikvm subprocess (ART11, ARM64, static)
─────────────────────────           ──────────────────────────────────────────
WestlakeActivity                    WestlakeLauncher.main()
  └── WestlakeVM.kt                  ├── ManifestParser → Application class
       ├── Extract DEX + res          ├── Application.onCreate() [15s timeout]
       ├── Start dalvikvm process     ├── Activity.onCreate() [15s timeout]
       ├── Read pipe (DLST frames)    ├── onSurfaceCreated() + renderFrame()
       └── SurfaceView.lockCanvas()   └── Event loop (touch.dat → dispatch)
            → replay display list          → shim framework → OHBridge
            → unlockCanvasAndPost()        → [DLST][size][ops] → stdout pipe
```

### Tested Apps

| App | Source | DEX Files | Classes | Status |
|-----|--------|-----------|---------|--------|
| MockDonalds | Custom | 1 | ~50 | Full functionality |
| Tip Calculator | Custom | 1 | ~10 | Full functionality |
| TODO List | Custom | 1 | ~20 | Full functionality |
| Counter | Play Store | 1 | ~2K | Full functionality |
| **McDonald's** | **Play Store** | **33** | **119,275** | **Lifecycle complete, renders splash** |

### Running a Real APK (McDonald's Example)

```bash
# 1. Extract DEX files from APK
python3 -c "
import zipfile
z = zipfile.ZipFile('mcdonalds.apk')
for e in z.namelist():
    if e.startswith('classes') and e.endswith('.dex'):
        z.extract(e, '/tmp/mcd-dex/')
"

# 2. Push to phone
adb push dalvikvm /data/local/tmp/westlake/
adb push aosp-shim.dex /data/local/tmp/westlake/
for f in /tmp/mcd-dex/classes*.dex; do
    adb push "$f" "/data/local/tmp/westlake/mcd_$(basename $f)"
done

# 3. Extract and push resources + manifest
# (WestlakeVM.kt does this automatically for in-app launches)

# 4. Generate boot image with shim included
dex2oat --instruction-set=arm64 --compiler-filter=extract \
  --image=boot.art --oat-file=boot.oat \
  --dex-file=core-oj.jar --dex-location=/data/local/tmp/westlake/core-oj.jar \
  --dex-file=core-libart.jar --dex-location=/data/local/tmp/westlake/core-libart.jar \
  --dex-file=core-icu4j.jar --dex-location=/data/local/tmp/westlake/core-icu4j.jar \
  --dex-file=aosp-shim.dex --dex-location=/data/local/tmp/westlake/aosp-shim.dex \
  --runtime-arg -Xverify:none --base=0x70000000
```

### Stubs Added for Stock App Support

| Category | Classes | Purpose |
|----------|---------|---------|
| Google Play Services | GoogleApiAvailability, Task, Tasks, ConnectionResult | GMS availability checks |
| Firebase | FirebaseApp, Analytics, Messaging, Auth, Crashlytics | Firebase init + analytics |
| AndroidX | InitializationProvider | App Startup library |
| Dagger/Hilt | HiltAndroidApp, AndroidEntryPoint, @Inject | DI annotations |
| Native (C) | Inflater/Deflater, JarFile, ICU regex, Character | Core JDK natives |
| Manifest | ManifestParser | Binary AXML → Application class + activity list |

### Known Limitations for Complex Apps

1. **Dagger/Hilt DI**: Interpreter mode too slow for 100K+ class graphs. Application.onCreate() gets a 15s timeout, then Activity proceeds with null injected fields.
2. **Native .so loading**: Static dalvikvm can't dlopen. System.loadLibrary() returns UnsatisfiedLinkError (caught by most apps).
3. **No network**: Apps expecting HTTP responses will get null/timeout.
4. **No Google Play Services**: GMS checks return SERVICE_MISSING.

---

## Quick Reference

| What | Where |
|------|-------|
| GitHub repo | `https://github.com/A2OH/westlake` |
| Host APK (Compose) | `westlake-host-gradle/` |
| VM launcher | `shim/java/com/westlake/engine/WestlakeLauncher.java` |
| Manifest parser | `shim/java/android/content/pm/ManifestParser.java` |
| Shim layer | `shim/java/` (2,200+ classes) |
| GMS/Firebase stubs | `shim/java/com/google/` |
| Native stubs | `art-universal-build/stubs/openjdk_stub.c` |
| Boot image gen | `dex2oat --dex-location=... --compiler-filter=extract` |
| MockDonalds app | `test-apps/04-mockdonalds/` |
| Built shim DEX | `aosp-shim.dex` |
| Status docs | `docs/engine/WESTLAKE-STATUS.md` |
| GitHub issues | `A2OH/westlake #550-#557` (McDonald's blockers) |
