# Real Android APK on Dalvik/OHOS — Status

## Milestone Achieved

A real Android APK runs end-to-end on the ported Dalvik VM:

```
$ dalvikvm ... com.example.apkloader.ApkRunner hello.apk

=== APK Runner ===
Loading: hello.apk
Manifest: 619 bytes
Package: com.example.hello
Launcher: com.example.hello.HelloActivity
Starting: com.example.hello.HelloActivity
=== REAL APK RUNNING ===
Hello from a REAL Android APK on Dalvik!
Package: com.example.hello
Activity: HelloActivity
Sum 1..10 = 55
Menu items: 3
Burgers: 3
Price parsed: 5.99
Math.sqrt(256) = 16.0
=== REAL APK COMPLETE ===
=== APK Launch COMPLETE ===
```

## Architecture

```
hello.apk (ZIP file)
  ├── AndroidManifest.xml (text or binary AXML)
  └── classes.dex (DEX 035, 2538 classes)
         │
         ▼
┌─ ApkRunner ──────────────────────────────────┐
│  1. Read APK (manual ZIP parse, no Inflater) │
│  2. Extract AndroidManifest.xml              │
│  3. Parse: package + launcher Activity       │
│  4. MiniServer.init(package)                 │
│  5. startActivity(launcher)                  │
└──────────────────────────────────────────────┘
         │
         ▼
┌─ Dalvik VM (KitKat portable interpreter) ────┐
│  64-bit patched (x86_64 + ARM32 OHOS)        │
│  libcore_bridge.cpp: Math, Posix, ICU, Regex │
│  4000 core classes + 1978 shim classes        │
└──────────────────────────────────────────────┘
         │
         ▼
┌─ App: HelloActivity.onCreate() ──────────────┐
│  HashMap.put ✅  Double.parseDouble ✅         │
│  Math.sqrt ✅    System.out.println ✅          │
│  Full Activity lifecycle ✅                    │
└──────────────────────────────────────────────┘
```

## What Works

| Feature | Status | Notes |
|---------|--------|-------|
| APK ZIP extraction | ✅ | Manual ZIP parser (no native Inflater) |
| AndroidManifest.xml parsing | ✅ | Text XML + binary AXML support |
| Package/launcher detection | ✅ | Reads intent-filter for MAIN/LAUNCHER |
| MiniServer app launch | ✅ | Full Activity lifecycle |
| Math natives | ✅ | floor, ceil, sqrt, sin, cos, tan, exp, log, pow (27 methods) |
| Double.parseDouble | ✅ | With exponent support |
| String.split (regex) | ✅ | POSIX regex via Pattern + Matcher natives |
| File I/O (open/read/close) | ✅ | Posix natives with FileDescriptor objects |
| fstat | ✅ | StructStat field population |
| HashMap, ArrayList | ✅ | Standard Java collections |
| Activity lifecycle | ✅ | onCreate→onStart→onResume→onPause→onStop→onDestroy |
| Intent + extras | ✅ | String, int, double, boolean extras |
| SharedPreferences | ✅ | In-memory HashMap-backed |
| SQLite (in-memory) | ✅ | ContentValues CRUD, query, transactions |
| Canvas rendering | ✅ | Headless draw log (no display) |

## What Doesn't Work Yet

| Feature | Issue | Fix |
|---------|-------|-----|
| String.format | LocaleData NPE | Need proper ICU field initialization |
| Compressed APK entries | No Inflater native | Build APKs with STORED entries |
| XML layout inflation | Not implemented | Need binary AXML → View tree |
| resources.arsc | Not implemented | Need resource table parser |
| Real display output | Headless only | Need ArkUI or framebuffer |

## Issue Tracker

All issues at: https://github.com/A2OH/harmony-android-guide/issues?q=label:real-apk

| # | Issue | Status |
|---|-------|--------|
| #483 A6 | Extract real APK DEX | ✅ Closed |
| #484 A7 | Native stubs (Math/ICU) | 90% done |
| #485 A8 | Binary manifest parser | ✅ Closed |
| #486 A9 | Full APK loader | ✅ Closed |

## How to Test

```bash
# Build APK runner DEX
cd android-to-openharmony-migration
JAVAC8=.../jdk8/bin/javac
JAVA8=.../jdk8/bin/java
DX=.../dx.jar

$JAVAC8 -d /tmp/build -sourcepath "test-apps/mock:shim/java:test-apps/hello-world/src" \
  test-apps/hello-world/src/com/example/apkloader/ApkRunner.java \
  test-apps/hello-world/src/com/example/apkloader/BinaryXmlParser.java \
  test-apps/hello-world/src/com/example/hello/HelloActivity.java \
  $(find test-apps/mock shim/java -name "*.java" ! -path "*/OHBridge.java")

$JAVA8 -jar $DX --dex --output=/tmp/apkrunner.dex /tmp/build

# Create APK (STORED, no compression)
python3 -c "
import zipfile
with zipfile.ZipFile('hello.apk','w',zipfile.ZIP_STORED) as z:
    z.write('/tmp/build-classes.dex','classes.dex')
    z.write('AndroidManifest.xml','AndroidManifest.xml')
"

# Run on Dalvik
export ANDROID_DATA=/tmp/android-data ANDROID_ROOT=/tmp/android-root
mkdir -p /tmp/android-data/dalvik-cache /tmp/android-root/bin

dalvik-port/build/dalvikvm -Xverify:none -Xdexopt:none \
  -Xbootclasspath:dalvik-port/core-android-x86.jar:/tmp/apkrunner.dex \
  -classpath /tmp/apkrunner.dex \
  com.example.apkloader.ApkRunner hello.apk
```
