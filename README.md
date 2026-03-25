# Westlake Engine — Android App Compatibility Layer for OpenHarmony

**[English](README.md)** | **[中文](README_CN.md)**

[![Tests](https://img.shields.io/badge/tests-2%2C476_pass-brightgreen)]()
[![Shim Classes](https://img.shields.io/badge/shim_classes-2%2C056-blue)]()
[![AOSP Lines](https://img.shields.io/badge/AOSP_framework-193K_lines_(166_files)-orange)]()
[![Architecture](https://img.shields.io/badge/approach-engine_%7E15_bridges-purple)]()
[![License](https://img.shields.io/badge/license-Apache_2.0-blue)]()

Westlake runs **unmodified Android APKs on OpenHarmony** by providing a shim layer that translates Android API calls to OHOS equivalents. Rather than mapping 57,000 Android APIs individually, the engine embeds the Android framework as a guest runtime — the same way OpenHarmony hosts Flutter — and bridges to OHOS at roughly 15 HAL-level boundaries.

The name *Westlake* (西湖) represents the bridge between two worlds — Android and OpenHarmony — just as Hangzhou's West Lake bridges tradition and modernity.

## Current Status

**MockDonalds demo running on a real phone with native Android Views.**

- Real Android APK inflated and rendered via the phone's native ART runtime
- 5 screens: menu browsing, category filter, item detail, cart, checkout
- Native Android `View`, `TextView`, `ImageView`, `RecyclerView` all working
- Binary XML layout inflation from real APK resources
- 2,476 tests pass across 13 test apps
- 193K+ lines of unmodified AOSP framework code (166 files) compiles and runs

## Architecture

```
┌─────────────────────────────────────────────────┐
│              Android APK (unmodified)            │
│         DEX bytecode + resources + manifest      │
├─────────────────────────────────────────────────┤
│           Android Framework (guest runtime)       │
│   View tree · Activity lifecycle · LayoutInflater │
│   193K lines AOSP code running on Dalvik/ART      │
├─────────────────────────────────────────────────┤
│              Westlake Engine Layer                │
│   ShimCompat · OHBridge · MiniActivityManager     │
│   MiniServer · BinaryXmlParser                    │
├─────────────────────────────────────────────────┤
│         ~25 C bridge functions + 1 host Activity  │
├─────────────────────────────────────────────────┤
│              OpenHarmony OS                       │
│   XComponent surface · input · system services    │
└─────────────────────────────────────────────────┘
```

**Platform coupling:** only **25 C functions** and **1 host Activity** connect the Android guest runtime to OpenHarmony. Everything above the bridge layer is stock AOSP code running unmodified.

## Key Components

| Component | Location | Purpose |
|-----------|----------|---------|
| **MiniServer** | `shim/java/com/` | Lightweight replacement for Android's `system_server`; manages services without a full OS |
| **MiniActivityManager** | `shim/java/com/` | Activity lifecycle management — `startActivity()`, back stack, intent routing |
| **OHBridge** | `westlake-host/jni/` | JNI bridge between the Android guest runtime and OpenHarmony native APIs (C) |
| **ShimCompat** | `shim/java/android/` | 2,056 shim classes that satisfy Android framework dependencies with minimal implementations |
| **BinaryXmlParser** | `shim/java/android/` | Parses Android binary XML (AXML) layouts and `resources.arsc` from real APKs |
| **Westlake Host** | `westlake-host/` | The single OpenHarmony Activity that hosts the Android guest runtime |
| **OHOS Deploy** | `ohos-deploy/` | Dalvik VM, ART boot images, and `liboh_bridge.so` for on-device deployment |

## Repository Layout

```
├── shim/              # Android framework shim layer (Java + C++ + Rust bridges)
│   ├── java/          # 2,056 shim classes (android.*, dalvik.*, libcore.*)
│   └── bridge/        # Native bridge code (C++, Rust)
├── westlake-host/     # OpenHarmony host app (1 Activity + JNI bridge)
├── ohos-deploy/       # On-device runtime: Dalvik VM, ART boot images, libs
├── dalvik-port/       # Dalvik VM port to OpenHarmony (KitKat, 64-bit)
├── test-apps/         # 13 test applications (MockDonalds, calculator, etc.)
├── docs/engine/       # Detailed technical documentation
├── backend/           # Analysis backend (Python/FastAPI)
├── frontend/          # Web frontend
└── scripts/           # Build and deployment scripts
```

## Building and Testing

### Prerequisites

- Android SDK (API 30+) with `d8`, `aapt2`, and `adb`
- OpenHarmony device or emulator (for on-device testing)
- Java 11+ (for shim compilation)

### Build the shim DEX

```bash
# Compile all shim classes and package into a DEX
cd shim/
./build.sh          # produces aosp-shim.dex
```

### Build the host APK

```bash
cd westlake-host/
./build-apk.sh      # produces westlake-host.apk
```

### Deploy and run on device

```bash
cd westlake-host/
./deploy.sh          # pushes runtime + APK, launches on device
```

### Run tests

```bash
# Tests run on-device via the Dalvik/ART runtime
cd test-apps/
./run-tests.sh       # 2,476 tests across 13 apps
```

## Documentation

Detailed technical docs live in `docs/engine/`:

| Document | Description |
|----------|-------------|
| [Architecture](docs/engine/ARCHITECTURE.md) | Full engine design: why Android is "just another Flutter app" on OH |
| [Architecture (CN)](docs/engine/ARCHITECTURE_CN.md) | 架构设计文档（中文） |
| [Westlake Status](docs/engine/WESTLAKE-STATUS.md) | Current milestone tracker and roadmap |
| [Call Flows](docs/engine/CALL-FLOWS.md) | Detailed call flow diagrams for key operations |
| [Performance Analysis](docs/engine/PERFORMANCE-ANALYSIS.md) | 7-layer performance gap analysis: Dalvik vs ART, rendering, input |
| [Real APK Status](docs/engine/REAL-APK-STATUS.md) | Status of real APK compatibility testing |
| [Execution Plan](docs/engine/EXECUTION-PLAN.md) | Step-by-step implementation plan |

## License

Apache 2.0 — see [LICENSE](LICENSE).
