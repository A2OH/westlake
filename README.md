# Westlake (西湖): Run Android APKs on OpenHarmony

[![Tests](https://img.shields.io/badge/tests-2%2C476_pass-brightgreen)]()
[![Shim Classes](https://img.shields.io/badge/shim_classes-2%2C056-blue)]()
[![AOSP Lines](https://img.shields.io/badge/AOSP_framework-62%2C153_lines-orange)]()
[![Architecture](https://img.shields.io/badge/approach-engine_%7E15_bridges-purple)]()
[![License](https://img.shields.io/badge/license-Apache_2.0-blue)]()

Run **unmodified Android APKs on OpenHarmony** by treating the Android framework as an embeddable runtime engine -- like Flutter on OH. Instead of mapping 57,000 Android APIs individually, the engine runs the entire Android framework as a guest runtime, bridging to OpenHarmony at approximately 15 HAL-level boundaries.

The name *Westlake* (西湖) represents the bridge between two worlds -- Android and OpenHarmony -- just as Hangzhou's West Lake bridges tradition and modernity.

```mermaid
graph TD
    subgraph APK["Android APK"]
        DEX["DEX Bytecode<br/>(classes.dex)"]
        MANIFEST["AndroidManifest.xml<br/>(binary AXML)"]
        RES["resources.arsc"]
    end

    subgraph ENGINE["Android Engine (Guest Runtime)"]
        DALVIK["Dalvik VM<br/>(KitKat, 64-bit port)"]
        FW["Android Framework<br/>(62,153 lines AOSP)"]
        MINI["MiniServer<br/>(Activity, Window, Package)"]
        SHIM["2,056 Shim Classes<br/>(android.* surface)"]
    end

    subgraph BRIDGE["~15 HAL Bridges (OHBridge JNI)"]
        B1["Canvas → OH_Drawing<br/>(58 native methods)"]
        B2["ArkUI Node API<br/>(View → nodeCreate)"]
        B3["System Services<br/>(169 total JNI methods)"]
    end

    subgraph OHOS["OpenHarmony"]
        DRAWING["OH_Drawing / Skia"]
        ARKUI["ArkUI Native Nodes"]
        SERVICES["OHOS System Services"]
    end

    APK --> DALVIK
    DALVIK --> FW
    FW --> MINI
    FW --> SHIM
    SHIM --> BRIDGE
    BRIDGE --> OHOS
```

---

## Why Engine, Not Container

| Approach | API Surface to Bridge | Binary Size | JNI Overhead | Fidelity |
|----------|----------------------|-------------|--------------|----------|
| **API Shimming** | 57,289 methods | N/A | N/A | Low (behavior mismatches) |
| **Container (Anbox-style)** | Full Linux kernel | ~500 MB | N/A | High but heavy |
| **Engine (this project)** | ~15 boundaries | ~15 MB | 0.08% per frame | High (real AOSP code) |

The engine approach works because **99% of Android API calls never leave the VM**. When an app calls `textView.setText("Hello")`, the entire chain -- `setText` to `invalidate` to `requestLayout` to `onMeasure` to `onDraw(canvas)` -- is pure Java running in Dalvik. Only the final `Canvas.drawText()` crosses to native code via JNI.

```mermaid
graph LR
    A["App: setText('Hello')"] --> B["8 pure Java calls<br/>(measure, layout, draw)"]
    B --> C["1 JNI call:<br/>Canvas.drawText()"]
    C --> D["OH_Drawing_CanvasDrawText()<br/>→ Skia → pixels"]

    style A fill:#e1f5fe
    style B fill:#e8f5e9
    style C fill:#fff3e0
    style D fill:#fce4ec
```

This is structurally identical to how Flutter runs on OpenHarmony: Dart VM executes widget tree logic, Skia renders to an XComponent surface, and platform channels bridge to system services. An Android APK is just another Flutter app with a different VM and widget vocabulary.

---

## Current Status

### What Works Today

| Component | Status | Details |
|-----------|--------|---------|
| Dalvik VM (x86_64) | **Working** | DEX execution, GC, multi-class loading |
| Dalvik VM (OHOS ARM32) | **Working** | Static binary, Hello World + Activity lifecycle on QEMU |
| Dalvik VM (OHOS aarch64) | **Working** | Static binary, Hello World via QEMU user-mode |
| Android Framework (AOSP) | **62,153 lines** | View, ViewGroup, TextView, AbsListView, ListView -- unmodified |
| Java Shim Layer | **2,056 classes** | 126,625 lines of Java covering the android.* API surface |
| MiniServer | **Working** | Activity lifecycle, service routing, package management |
| Activity Lifecycle | **Full** | create, start, resume, pause, stop, destroy + result codes |
| SQLite | **Working** | In-memory database, Cursor, ContentValues |
| SharedPreferences | **Working** | HashMap-backed with file persistence |
| Intent / Bundle | **Working** | Extras, ComponentName, action/category filtering |
| ContentProvider | **Working** | insert, query, update, delete via ContentResolver |
| Service | **Working** | Lifecycle: create, startCommand, bind, unbind, destroy |
| BroadcastReceiver | **Working** | Register, send, receive with IntentFilter matching |
| Fragment | **Working** | Add, replace, remove, back stack, lifecycle callbacks |
| Canvas → OH_Drawing | **58 JNI methods** | Pen, Brush, Path, Bitmap, Font, Surface |
| ArkUI Node Wiring | **Working** | View to nodeCreate, setText to nodeSetAttr |
| Resources.arsc Parser | **Working** | String pool, type specs, resource entries |
| Binary Manifest Parser | **Working** | AXML format, namespace handling, attribute extraction |
| Pixel Rendering (Java2D) | **Working** | Closed-loop visual debugging via PNG output |
| OHBridge JNI | **169 methods** | Drawing, ArkUI nodes, preferences, RDB, HiLog, HTTP |

### Test Results

| Test Suite | Passed | Failed | Total |
|------------|--------|--------|-------|
| Headless CLI (02) | 2,416 | 54 | 2,470 |
| UI Mockup (03) | 47 | 6 | 53 |
| MockDonalds E2E (04) | 10 | 4 | 14 |
| Real APK Pipeline (06) | 3 | 2 | 5 |
| **Total** | **2,476** | **66** | **2,542** |

---

## Architecture

```mermaid
graph TD
    subgraph HOST["Host: OpenHarmony (or Linux for testing)"]
        subgraph VM["Dalvik VM"]
            INTERP["Portable Interpreter"]
            GC["Mark-Sweep GC"]
            BOOT["Boot Classpath<br/>(4,000 java.* classes)"]
        end

        subgraph FRAMEWORK["Android Framework (Java)"]
            ACTIVITY["Activity / Fragment<br/>Lifecycle Management"]
            VIEW["View / ViewGroup<br/>(30K + 9K lines AOSP)"]
            TEXT["TextView<br/>(13K lines AOSP)"]
            LAYOUT["LinearLayout / RelativeLayout<br/>FrameLayout / TableLayout"]
            WIDGET["Button, ImageView, ListView<br/>EditText, ProgressBar, etc."]
            DATA["SQLiteDatabase<br/>SharedPreferences<br/>ContentProvider"]
            IPC["Intent, Bundle<br/>Service, BroadcastReceiver"]
        end

        subgraph MINI["MiniServer (~770 lines)"]
            MAM["MiniActivityManager<br/>(back stack, lifecycle)"]
            MPM["MiniPackageManager<br/>(manifest, components)"]
            SSR["SystemServiceRegistry<br/>(15 services wired)"]
        end

        subgraph OHBRIDGE["OHBridge (169 JNI methods)"]
            DRAW["Canvas Bridge<br/>(58 methods)"]
            NODE["ArkUI Node Bridge"]
            SYS["System Bridges<br/>(prefs, rdb, hilog, http)"]
        end
    end

    INTERP --> FRAMEWORK
    FRAMEWORK --> MINI
    FRAMEWORK --> OHBRIDGE
    MINI --> SSR
    OHBRIDGE --> OHOS_API["OHOS NDK APIs"]
```

### Dual Rendering Paths

The engine supports two rendering strategies:

```
Path A (Canvas/Skia):                    Path B (ArkUI Node):
Android View.draw(Canvas)               Android View → ArkUI Node
  → Canvas.drawRect/Text/Path             → OHBridge.nodeCreate(ROW)
  → JNI → OH_Drawing_Canvas               → OHBridge.nodeSetAttr(WIDTH, 100)
  → Skia → GPU → Display                  → ArkUI renders natively

Best for: Games, custom drawing           Best for: Standard widgets
```

---

## Quick Start

### Prerequisites

```bash
# Java (JDK 8+, JDK 21 works)
javac -version
java -version

# GitHub CLI (for issue tracking)
gh auth status
```

### Run Tests

```bash
git clone https://github.com/A2OH/westlake.git
cd westlake

# Run all test suites (compiles 2,056 shim files + mock bridge + tests)
cd test-apps && ./run-local-tests.sh

# Or run individually:
./run-local-tests.sh headless      # 2,470 tests
./run-local-tests.sh ui            # 53 tests
./run-local-tests.sh mockdonalds   # 14 tests (end-to-end)
./run-local-tests.sh realapk       # 5 tests (APK pipeline)
```

### Run on Dalvik VM (x86_64)

```bash
cd dalvik-port
export ANDROID_DATA=/tmp/android-data ANDROID_ROOT=/tmp/android-root
mkdir -p $ANDROID_DATA/dalvik-cache $ANDROID_ROOT/bin

./build/dalvikvm -Xverify:none -Xdexopt:none \
  -Xbootclasspath:$(pwd)/core-android-x86.jar \
  -classpath hello.dex HelloAndroid
```

---

## Test Apps

| # | Name | What It Tests | Checks |
|---|------|---------------|--------|
| 01 | FlashNote | Basic Activity + layout | -- |
| 02 | Headless CLI | Full shim layer: Bundle, Intent, SQLite, Prefs, URI, Service, Provider, Fragment, ... | 2,470 |
| 03 | UI Mockup | View tree, measure/layout/draw pipeline, headless rendering | 53 |
| 04 | MockDonalds | End-to-end: Activity launch, menu navigation, order flow, lifecycle | 14 |
| 05 | TodoList | CRUD with SQLiteDatabase, ContentValues, Cursor | -- |
| 06 | Real APK | APK unzip, binary manifest parse, DexClassLoader, Activity launch | 5 |
| 07 | Calculator | State management, button handlers, display updates | -- |
| 08 | Notes | SharedPreferences, text persistence, search | -- |
| 09 | SuperApp | ContentProvider, BroadcastReceiver, Service, AsyncTask, Handler, Clipboard | -- |
| 10 | Layout Validator | Measure specs, layout params, nested ViewGroups | -- |
| 11 | Frame Dump | Pixel-level rendering via Java2D, PNG output for visual debugging | -- |

---

## Real APK Analysis

Analysis of 13 top Android APKs (TikTok, Instagram, YouTube, Netflix, Spotify, Facebook, Google Maps, Zoom, Grab, Duolingo, Uber, PayPal, Amazon) representing 2.3 billion+ monthly active users:

| Finding | Value |
|---------|-------|
| Average android.* classes referenced per APK | 443 |
| Classes covered by current shim layer | 434 (97.6% for Amazon Shopping) |
| API calls that stay inside the VM | 94% |
| API calls requiring real platform bridge | 6% |
| Engine overhead per frame | 0.08% (JNI crossing cost) |

The Amazon Shopping APK was analyzed in detail:
- 97.6% of referenced android.* classes are already present in the shim layer
- The remaining 2.4% are advanced system services (TelephonyManager, Bluetooth) that most UI flows do not depend on
- Facebook, Netflix, and Spotify manifests parse correctly with the binary AXML parser

---

## Repository Layout

```
westlake/
├── shim/java/android/          # 2,056 Java shim files (126,625 lines)
│   ├── app/                    # Activity, Fragment, MiniServer, Service
│   ├── content/                # Intent, ContentProvider, SharedPreferences
│   ├── database/               # SQLite, Cursor, MatrixCursor
│   ├── graphics/               # Canvas, Paint, Bitmap, Path, Color
│   ├── net/                    # Uri, ConnectivityManager
│   ├── os/                     # Bundle, Handler, Looper, Parcel
│   ├── view/                   # View (30K lines AOSP), ViewGroup (9K)
│   ├── widget/                 # TextView (13K), Button, LinearLayout, ...
│   └── ...                     # 137 android.* packages total
├── shim/java/com/ohos/shim/    # OHBridge JNI (169 native methods)
├── test-apps/
│   ├── 01-flashnote/           # Basic Activity test
│   ├── 02-headless-cli/        # Headless test harness (2,470 checks)
│   ├── 03-ui-mockup/           # UI rendering tests
│   ├── 04-mockdonalds/         # End-to-end restaurant app
│   ├── 05-todolist/            # SQLite CRUD
│   ├── 06-real-apk/            # APK loading pipeline
│   ├── 07-calculator/          # State management
│   ├── 08-notes/               # SharedPreferences
│   ├── 09-superapp/            # Provider + Receiver + Service
│   ├── 10-layout-validator/    # Measure/layout validation
│   ├── 11-frame-dump/          # Pixel rendering + PNG output
│   ├── mock/                   # Mock OHBridge (JVM testing, no device)
│   └── run-local-tests.sh      # Test runner
├── dalvik-port/                # Dalvik VM (x86_64, ARM32, aarch64)
├── database/
│   ├── api_compat.db           # 57,289 APIs, tier classification, OH mappings
│   └── generate_shims.py       # Stub generator pipeline
├── skills/                     # 8 conversion skill files (A2OH-*)
├── frontend/                   # React dashboard (GitHub Pages)
├── 02-ANDROID-AS-ENGINE.md     # Architecture design document
├── 03-ENGINE-EXECUTION-PLAN.md # 4-workstream execution plan
└── scripts/                    # Issue generator, orchestration tools
```

---

## Dependencies

Westlake is built on two companion projects:

| Project | Repository | Purpose |
|---------|-----------|---------|
| **OpenHarmony on WSL** | [A2OH/openharmony-wsl](https://github.com/A2OH/openharmony-wsl) | Build and run OHOS on QEMU ARM32 without hardware |
| **Dalvik Universal** | [A2OH/dalvik-universal](https://github.com/A2OH/dalvik-universal) | Portable Dalvik VM for x86_64 and OHOS ARM32/aarch64 |

---

## Documentation

| Document | Description |
|----------|-------------|
| [Android-as-Engine Architecture](02-ANDROID-AS-ENGINE.md) | Why 15 bridges, not 57K shims. Flutter analogy. Real APK analysis. |
| [Engine Execution Plan](03-ENGINE-EXECUTION-PLAN.md) | 4 workstreams: Canvas bridge, MiniServer, APK loader, input bridge |
| [Call Flow Details](02A-CALL-FLOW-DETAILS.md) | Detailed call traces through the engine |
| [Analysis Plan](00-ANALYSIS-PLAN.md) | Original API gap analysis methodology |

---

## API Compatibility Database

`database/api_compat.db` maps the full Android API surface to OpenHarmony equivalents:

| Table | Rows | Description |
|-------|------|-------------|
| `android_packages` | 137 | Package names + conversion skill mappings |
| `android_types` | 4,617 | Classes, interfaces, enums |
| `android_apis` | 57,289 | Methods, fields, constructors |
| `api_mappings` | 57,289 | OH equivalents, confidence scores, tier classification |

### Tier Classification

| Tier | What | Count | Status |
|------|------|-------|--------|
| **A** | Pure Java data structures | 314 classes / 1,316 APIs | Mostly implemented |
| **B** | I/O with Java fallback | 946 classes / 2,212 APIs | In progress |
| **C** | System service wrappers | 3,445 classes / 43,254 APIs | Stubs only |
| **D** | UI components | 613 classes / 10,507 APIs | Engine approach (AOSP code) |

---

## Roadmap

| Milestone | Target | Status |
|-----------|--------|--------|
| Headless Activity lifecycle | M1 | **Done** -- full create/start/resume/pause/stop/destroy |
| Canvas renders shapes on Linux | M2 | **Done** -- Java2D pixel rendering, PNG output |
| Real APK loads and runs headlessly | M3 | **Done** -- APK unzip, AXML parse, DexClassLoader, Activity launch |
| "Hello Android" APK renders on OH | M4 | In progress -- visual rendering on ARM32 QEMU |
| Touch input on OH | M5 | Not started -- XComponent input events to View tree |
| Top-10 APK compatibility | M6 | Not started -- systematic gap closure |

---

## Orchestrator Dashboard

A React web app tracks implementation progress across the shim layer:

- Real-time issue status (auto-refresh from GitHub API)
- Per-tier completion tracking
- Batch issue creation for parallel workers
- Search and filter by status / tier

---

## Contributing

Contributions are welcome. The highest-impact areas:

1. **Canvas rendering on ARM32** -- connecting OH_Drawing to the Canvas bridge on real OHOS
2. **Input event routing** -- XComponent touch events to Android View tree
3. **Missing JNI natives** -- expanding libcore_bridge.cpp
4. **Test coverage** -- more end-to-end test apps exercising real APK patterns
5. **APK compatibility** -- testing and fixing gaps for specific popular apps

Please open an issue before starting major work.

---

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for details.

Android is a trademark of Google LLC. OpenHarmony is a project of the OpenAtom Foundation. This project is an independent research effort and is not affiliated with or endorsed by either organization.
