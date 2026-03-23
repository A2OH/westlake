# Agent A Tasks: OHOS Native Side — Make Pixels Appear

**Date:** 2026-03-23
**Status:** MockDonalds RUNS end-to-end on x86_64 ART. Activity lifecycle + rendering loop working.

---

## Milestone: MockDonalds Running (2026-03-23)

MockDonalds app runs the full Android Activity lifecycle on ART:

```
[MockDonaldsApp] Starting on OHOS + ART ...
[OHBridge x86] 169 methods
[MockDonaldsApp] OHBridge native: LOADED
[MockDonaldsApp] arkuiInit() = 0
[MockDonaldsApp] MiniServer initialized
[D] MiniActivityManager: startActivity: com.example.mockdonalds.MenuActivity
[D] MiniActivityManager:   performCreate: com.example.mockdonalds.MenuActivity
[D] MiniActivityManager:   performStart: com.example.mockdonalds.MenuActivity
[D] MiniActivityManager:   performResume: com.example.mockdonalds.MenuActivity
[MockDonaldsApp] MenuActivity launched: com.example.mockdonalds.MenuActivity
[MockDonaldsApp] Creating surface 480x800
[MockDonaldsApp] Initial frame rendered
[MockDonaldsApp] Entering event loop...
[MockDonaldsApp] Frame 600 activity=MenuActivity
```

**Performance:** ~60 fps on x86_64 host (native ART, no emulation)

---

## What was fixed to get here

### Agent B fixes
1. **artFindNativeMethod** — replaced stub with real `dlsym()` lookup in loaded libraries
2. **MiniActivityManager classloader** — `Thread.currentThread().getContextClassLoader()` with `getSystemClassLoader()` fallback, so boot classpath code can find app classes
3. **aosp-shim.dex** — rebuilt with overlapping classes excluded, correct DEX headers

### Agent A fixes (platform/native side)
1. **Runtime_nativeLoad** — was a no-op stub returning NULL ("success" without `dlopen`). Fixed to actually call `dlopen()` + `JNI_OnLoad`
2. **registerNativesOrSkip** — changed from batch-or-nothing to per-method registration so one bad signature doesn't block all methods
3. **ZipFile native methods** — implemented `open`, `close`, `getEntry`, `read`, `startsWithLOC`, `getTotal` + 8 more using mmap-based ZIP reading. Required by `ClassLoader.loadClass()` to scan boot classpath JARs
4. **UnixFileSystem natives** — `initIDs`, `getBooleanAttributes0`, `canonicalize0`, `getLastModifiedTime0`, `createDirectory0`, `list0`, etc. Required for `java.io.File` operations
5. **Math/StrictMath natives** — `sin`, `cos`, `tan`, `sqrt`, `pow`, `ceil`, `floor`, `exp`, `log` + 20 more. Wraps libc math functions. Required by `EdgeEffect` and rendering code
6. **x86_64 OHBridge stub** — auto-generated 169 JNI method stubs matching exact DEX signatures from `aosp-shim.dex`, with working log output to stdout
7. **x86_64 boot image** — generated with `dex2oat` for fast host testing (1.2s compile, <1s startup)

---

## Current architecture (x86_64 fast path)

```
Host x86_64
├── dalvikvm (x86_64, dynamically linked)
├── Boot image: boot.art + boot.oat (AOT compiled, 4 components)
├── Boot classpath: core-oj.jar + core-libart.jar + core-icu4j.jar + aosp-shim.dex
├── Native libraries:
│   ├── libicu_jni.so — charset converter stubs
│   ├── libjavacore.so — Linux I/O native methods
│   ├── libopenjdk.so — File I/O, Math, ZipFile, UnixFileSystem, Runtime.nativeLoad
│   └── liboh_bridge.so — 169 OHBridge JNI stubs (canvas, bitmap, surface, preferences, rdb)
└── app.dex — MockDonalds (MenuActivity, CartActivity, CheckoutActivity, etc.)
```

---

## ARM64 QEMU status

**Blocked:** ART hangs at `InitNativeMethods` for 30+ minutes on emulated ARM64.

- Boot image loads successfully (22ms)
- Class tables added
- Hangs during `RegisterRuntimeNativeMethods` or `WellKnownClasses::Init`
- QEMU ARM64 emulation adds ~100x overhead vs native
- stub libraries (libicu_jni.so, libopenjdk.so) deployed but static dalvikvm's dlopen may not find them

**Path forward:** Either:
1. Build a faster kernel/QEMU (KVM acceleration if available)
2. Pre-link the stub libraries into the dalvikvm binary statically
3. Use x86_64 for development, deploy to real ARM64 hardware for VNC demo

---

## Remaining work for P0 (pixels on VNC)

### Option A: ARM64 QEMU (blocked by speed)
- Fix `InitNativeMethods` hang (need stub libs statically linked or faster emulation)
- The ARM64 liboh_bridge.so already has real surface → fb0 → VNC pipeline

### Option B: x86_64 with image output (works now)
- Modify OHBridge stub to render to an image file instead of fb0
- View rendered frames as PNG/PPM files
- No VNC needed — just file output

### P1 (touch input)
- ARM64 QEMU has virtio-tablet-pci for touch
- x86_64 would need a different input mechanism

---

## File locations

| What | Path |
|------|------|
| x86_64 dalvikvm | `$HOME/art-universal-build/build/bin/dalvikvm` |
| x86_64 boot image | `/tmp/a2ohd/x86_64/boot.art` + `boot.oat` |
| x86_64 OHBridge stub | `/tmp/oh_bridge_x86_auto.c` (auto-generated) |
| openjdk stub (Math+ZipFile) | `$HOME/art-universal-build/stubs/openjdk_stub.c` |
| javacore stub | `$HOME/art-universal-build/stubs/javacore_stub.c` |
| ARM64 dalvikvm | `$HOME/art-universal-build/build-ohos-arm64/bin/dalvikvm` |
| ARM64 userdata image | `/tmp/ohos-arm64-images/userdata-art.img` |
| Test data dir (x86_64) | `/tmp/a2ohd/` |
| QEMU (system) | `/tmp/qemu-8.2.2/build/qemu-system-aarch64` |
| QEMU (user-mode) | `/tmp/qemu-8.2.2/build-user/qemu-aarch64` |

## Run x86_64 test
```bash
ART=$HOME/art-universal-build/build
LD_LIBRARY_PATH=$ART/bin:$ART/lib:/tmp/a2ohd \
ANDROID_DATA=/tmp/a2ohd ANDROID_ROOT=/tmp/a2ohd \
$ART/bin/dalvikvm \
  -Xbootclasspath:/tmp/a2ohd/core-oj.jar:/tmp/a2ohd/core-libart.jar:/tmp/a2ohd/core-icu4j.jar:/tmp/a2ohd/aosp-shim.dex \
  -Ximage:/tmp/a2ohd/boot.art -Xverify:none \
  -Djava.library.path=/tmp/a2ohd:$ART/lib \
  -classpath /tmp/a2ohd/app.dex \
  com.example.mockdonalds.MockDonaldsApp
```

---

## Success criteria

1. ~~artFindNativeMethod finds OHBridge methods via dlsym~~ ✅
2. ~~MockDonalds MenuActivity launches and calls View.draw()~~ ✅
3. Pixels appear on VNC via fb0 ⏳ (blocked by ARM64 speed)
4. Touch input navigates between Activities ⏳
