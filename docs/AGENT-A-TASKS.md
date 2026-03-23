# Agent A Tasks: OHOS Native Side — Make Pixels Appear

**Date:** 2026-03-22 (updated)
**Status:** artFindNativeMethod FIXED. New dalvikvm deployed. Ready for rendering.

---

## What Agent B fixed (latest)

### artFindNativeMethod — NO LONGER A STUB

The `"STUB: artFindNativeMethod called"` blocker is **fixed**. The new dalvikvm binary uses the real ART `artFindNativeMethod` implementation which does `dlsym()` lookup in loaded shared libraries.

**What this means:** When ART encounters a native method (e.g. `OHBridge.canvasDrawRect`), it will automatically find it via `dlsym` in `liboh_bridge.so`. No more null returns.

**New binary deployed to:** `/tmp/ohos-arm64-images/userdata-art.img` (a2oh/dalvikvm)

Source binary: `/home/dspfac/art-universal-build/build-ohos-arm64/bin/dalvikvm` (11MB ARM64 static)

---

## What Agent A has built (infrastructure)

- ARM64 kernel with DRM + virtio-gpu + VNC framebuffer (1280x800 on :5900)
- Custom init: mounts /data, creates /dev/fb0, runs dalvikvm
- liboh_bridge.so recompiled for ARM64 with real surface implementation
- QEMU: source-built qemu-system-aarch64 with virtio-gpu + VNC + virtio-tablet
- Surface API: surfaceCreate → surfaceGetCanvas → draw → surfaceFlush → fb0 → VNC

---

## What Agent A needs to do next

### 1. Redeploy and test with new dalvikvm

The new binary is already in userdata-art.img. Boot and check if MockDonalds gets further:

```bash
bash /tmp/boot_arm64.sh
```

Expected: no more `"STUB: artFindNativeMethod"`. Instead, ART will do real `dlsym` lookups. If a native method is still missing, the error will now say **which method** (e.g. `UnsatisfiedLinkError: no implementation found for ...`).

### 2. If native methods are still missing

The real artFindNativeMethod calls `JavaVMExt::FindCodeForNativeMethod()` which does:
1. Check registered methods (from RegisterNatives in JNI_OnLoad)
2. dlsym in loaded libraries using JNI naming convention: `Java_com_ohos_shim_bridge_OHBridge_canvasDrawRect`

For dlsym to find OHBridge methods, `liboh_bridge.so` must either:
- Export JNI-named functions (`Java_com_ohos_shim_bridge_OHBridge_*`), OR
- Register them via `RegisterNatives` in `JNI_OnLoad`

Check which approach `liboh_bridge.so` uses:
```bash
nm -D liboh_bridge.so | grep "Java_com_ohos" | head -5
# If empty, check for JNI_OnLoad:
nm -D liboh_bridge.so | grep JNI_OnLoad
```

### 3. The rendering loop (Agent A's surface API)

```java
// In MockDonaldsApp or MiniActivityManager:
long surface = OHBridge.surfaceCreate(0, 480, 800);
Activity activity = startActivity(MenuActivity.class);

while (running) {
    long canvas = OHBridge.surfaceGetCanvas(surface);
    OHBridge.canvasDrawColor(canvas, 0xFFFFFFFF); // clear
    activity.getDecorView().draw(new Canvas(canvas));
    OHBridge.surfaceFlush(surface);  // → fb0 → VNC
    Thread.sleep(16); // 60fps
}
```

### 4. Quick fb0 test from Java

```java
long bmp = OHBridge.bitmapCreate(1280, 800, 0);
long canvas = OHBridge.canvasCreate(bmp);
OHBridge.canvasDrawColor(canvas, 0xFF4CAF50); // green
OHBridge.bitmapBlitToFb0(bmp, 0); // → green screen on VNC
```

---

## File locations

| What | Path |
|------|------|
| ARM64 dalvikvm (NEW) | `/home/dspfac/art-universal-build/build-ohos-arm64/bin/dalvikvm` |
| Userdata image | `/tmp/ohos-arm64-images/userdata-art.img` |
| liboh_bridge.so source | `dalvik-port/compat/arkui_bridge.cpp` |
| Boot script | `/tmp/boot_arm64.sh` |
| QEMU binary | Source-built qemu-system-aarch64 |

## Cross-compile liboh_bridge.so
```bash
clang++ --target=aarch64-linux-ohos --sysroot=$SYSROOT -shared -fPIC -O2 \
  -o liboh_bridge.so arkui_bridge.cpp
```

## Deploy new files to image
```bash
debugfs -w /tmp/ohos-arm64-images/userdata-art.img -R "rm a2oh/dalvikvm"
debugfs -w /tmp/ohos-arm64-images/userdata-art.img -R "write <path>/dalvikvm a2oh/dalvikvm"
```

---

## Success criteria

1. `artFindNativeMethod` finds OHBridge methods via dlsym ✓ (fixed)
2. MockDonalds MenuActivity launches and calls View.draw()
3. Pixels appear on VNC via fb0
4. Touch input navigates between Activities
