# OpenHarmony 4.1 QEMU Headless Testing Environment

**Date:** 2026-03-15
**Scope:** Building and booting OpenHarmony on QEMU ARM32 for headless API testing
**Author:** Claude Opus 4.6 + user

---

## Overview

We built a fully functional OpenHarmony 4.1 headless testing environment running on QEMU, enabling ability framework API testing without real hardware or a display. The system boots in ~5 seconds and runs the full service stack including ability_manager, bundle_manager, and app_manager.

## Architecture

```
┌─────────────────────────────────────────────────┐
│                  QEMU ARM32                      │
│         (cortex-a7, 1GB RAM, 4 CPUs)            │
│                                                  │
│  ┌─────────────┐  ┌──────────────────────────┐  │
│  │   Kernel     │  │     System Services       │  │
│  │ Linux 5.10   │  │  samgr, hilogd, hdcd,    │  │
│  │              │  │  faultloggerd, softbus,   │  │
│  │  ARM32 SMP   │  │  accesstoken, deviceauth │  │
│  └─────────────┘  └──────────────────────────┘  │
│                                                  │
│  ┌──────────────────────────────────────────┐   │
│  │           Foundation (sa_main)             │   │
│  │  SA 180: libabilityms.z.so (Ability Mgr) │   │
│  │  SA 401: libbms.z.so (Bundle Mgr)        │   │
│  │  SA 501: libappms.z.so (App Mgr)         │   │
│  └──────────────────────────────────────────┘   │
│                                                  │
│  ┌────────────┐  ┌──────────┐  ┌────────────┐  │
│  │  466 libs   │  │  console │  │ /dev/binder│  │
│  │  (.z.so)    │  │   (sh)   │  │  IPC ready │  │
│  └────────────┘  └──────────┘  └────────────┘  │
│                                                  │
│  Drive Layout (virtio-mmio, reverse order):      │
│  vda=updater, vdb=system(256MB), vdc=vendor,    │
│  vdd=userdata                                    │
└─────────────────────────────────────────────────┘
```

## Quick Start

### 1. Build Minimal System (~2.5 min)
```bash
export SOURCE_ROOT_DIR=$HOME/openharmony
export PATH=${SOURCE_ROOT_DIR}/prebuilts/python/linux-x86/current/bin:${SOURCE_ROOT_DIR}/prebuilts/build-tools/linux-x86/bin:$HOME/miniconda3/bin:$PATH
python3 ${SOURCE_ROOT_DIR}/build/hb/main.py build --product-name qemu-arm-linux-min --no-prebuilt-sdk
```

### 2. Prepare Images
```bash
./tools/prepare_images.sh
```

### 3. Boot
```bash
./tools/qemu_boot.sh
```

### 4. Verify
```
# ps -A
  PID CMD
    1 init
   88 hilogd
   89 device_manager
   90 hdcd
   91 sh
   92 samgr
   93 param_watcher
  100 deviceauth_serv
  105 faultloggerd
  106 softbus_server
  107 accesstoken_ser

# param get ohos.boot.hardware
qemu.arm.linux

# param get const.ohos.fullname
OpenHarmony-4.1.10.5
```

## What's Running

### Core Services (11+)
| Service | PID | Description |
|---------|-----|-------------|
| init | 1 | System init (fixed use-after-free bug) |
| samgr | 92 | System Ability Manager |
| hilogd | 88 | System logging |
| faultloggerd | 105 | Crash handler |
| softbus_server | 106 | Distributed bus |
| accesstoken_service | 107 | Permission management |
| deviceauth_service | 100 | Device authentication |
| device_manager | 89 | Device management |
| param_watcher | 93 | Parameter service |
| hdcd | 90 | Debug daemon (hdc) |
| console | 91 | Interactive shell |

### IPC Infrastructure
- `/dev/binder` — Android-style binder IPC
- `/dev/vndbinder` — Vendor binder
- 9 unix domain sockets (paramservice, hilog, faultloggerd, hdcd, etc.)

### Headless Build (466 libs, ability framework)
With the headless build, foundation runs with:
- **SA 180**: `libabilityms.z.so` — Ability lifecycle management
- **SA 401**: `libbms.z.so` — Bundle management (HAP install)
- **SA 501**: `libappms.z.so` — App process management

## Bugs Found & Fixed

### 1. Init Trigger Use-After-Free (Critical)
**File:** `base/startup/init/services/param/trigger/trigger_processor.c`

`ExecuteQueueWork()` called `GetTriggerName(trigger)` AFTER `StartTriggerExecute_()` which frees triggers with `TRIGGER_FLAGS_ONCE`. The freed trigger's `type` field was corrupted, causing SIGSEGV in `GetTriggerName()`.

**Fix:** Save trigger name to stack buffer before execution.

### 2. OpenSSL Build Target Wrong
**File:** `third_party/openssl/make_openssl_build_all_generated.sh`

Used `make build_generated` instead of `make build_all_generated`, missing `buildinf.h` and `der_*.h` headers.

### 3. GN defines Clobbering (321 files, 837 lines)
Systemic bug: `defines = []` inside `if` blocks resets accumulated defines from parent scope. Found in 321 BUILD.gn files across the entire codebase.

### 4. Missing SUPPORT_GRAPHICS Guards (30+ files)
The ability_runtime framework declares many functions inside `#ifdef SUPPORT_GRAPHICS` but implements them without guards. When building headless (`ability_runtime_graphics=false`), these cause compile errors.

### 5. Skia GPU Symbol References Without Guards
10 files in `render_service_base` reference GPU APIs (`GetGPUContext`, `GrBackendTexture`, etc.) unconditionally. Added `#ifdef RS_ENABLE_GL` guards.

### 6. LTO Template Vtable Stripping
`RSCurveValueEstimator<float>` explicit template instantiation was stripped by LTO when linked from a static library. Fixed by adding `RSB_EXPORT` to specialization declaration and disabling `-fwhole-program-vtables`.

## Patch Summary

| Category | Files Modified | Description |
|----------|---------------|-------------|
| GN defines clobbering | 321 | Remove redundant `defines = []` resets |
| SUPPORT_GRAPHICS guards | 30+ | ability_runtime, window_manager, access_token |
| Skia/GPU guards | 13 | render_service_base `#ifdef RS_ENABLE_GL` |
| EGL/GL/HDI stubs | 12 | Stub headers in sysroot |
| Ruby 3.4 compat | 5 | `ostruct`, `stringio`, `File.exist?` |
| Init bug fix | 2 | trigger_processor.c, trigger_manager.c |
| OpenSSL build | 2 | make target, buildinf.h |
| LTO/vtable fix | 2 | rs_value_estimator.h, render_service_client BUILD.gn |
| Misc stubs | 6 | RenderContext, CmdList, UIContent, MissionDataStorage |
| **Total** | **~75** | |

## Test Capabilities

### Currently Working
- Boot to shell with 11+ services
- IPC/binder communication
- System parameter get/set
- Service registration via samgr
- Ability manager service loaded (SA 180)
- Bundle manager service loaded (SA 401)

### Next Steps
- `bm install -p /path/to/test.hap` — Install test HAP
- `aa start -a TestAbility -b com.example.test` — Launch ability
- Validate ability lifecycle callbacks
- Test IPC between abilities
- CI/CD integration

## File Locations

| Item | Path |
|------|------|
| Boot script | `tools/qemu_boot.sh` |
| Image prep | `tools/prepare_images.sh` |
| QEMU binary | `tools/qemu-extracted/usr/bin/qemu-system-arm` |
| Build output | `out/qemu-arm-linux/packages/phone/images/` |
| System libs | `out/qemu-arm-linux/packages/phone/system/lib/` |
| SA profiles | `out/qemu-arm-linux/packages/phone/system/profile/` |
| Init configs | `out/qemu-arm-linux/packages/phone/system/etc/init/` |
