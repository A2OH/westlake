# Agent A Tasks: OHOS Native Side — Make Pixels Appear

**Date:** 2026-03-22
**Status:** ART runtime + AOSP framework running on OHOS ARM64 QEMU. No display output yet.

---

## What's proven (VM side — done)

The full stack runs end-to-end on QEMU aarch64:

```
MockDonalds DEX → ART C++ interpreter → AOSP framework (193K lines)
  → OHBridge JNI (172 methods) → liboh_bridge.so → OHOS native APIs
```

Log from OHOS ARM64 QEMU:
```
[MockDonaldsApp] Starting on OHOS + ART ...
[MockDonaldsApp] OHBridge native: LOADED
[MockDonaldsApp] arkuiInit() = -1446460720
[MockDonaldsApp] MiniServer initialized
[MockDonaldsApp] ERROR: MenuActivity failed to launch
```

OHBridge loads, arkuiInit() is called, MiniServer runs. But **no pixels** — MenuActivity fails because there's no rendering surface.

---

## What Agent A needs to do

### P0: OH_Drawing Canvas (10x rendering speedup)

**Current:** `arkui_bridge.cpp` uses software canvas (stb_truetype). The `OH_Drawing` functions are loaded via `dlopen("liboh_drawing.z.so")` with fallback.

**Task:** Make OH_Drawing work on the ARM64 OHOS QEMU build.

1. Check if `liboh_drawing.z.so` exists in the OHOS ARM64 system image at `/usr/lib64/`
2. If not, the minimal build (`qemu-arm64-linux-min`) may not include graphics. Need to either:
   - Add `graphic_2d` subsystem to the product config at `/home/dspfac/openharmony/vendor/ohemu/qemu_arm64_linux_min/config.json`
   - Or use software canvas with `/dev/fb0` framebuffer output
3. Wire `canvasDrawRect`, `canvasDrawText`, `canvasDrawBitmap` etc. to either OH_Drawing or software canvas + fb0
4. Implement `bitmapBlitToFb0()` to write ARGB8888 pixels to `/dev/fb0`

**Files:**
- `dalvik-port/compat/arkui_bridge.cpp` — main bridge (56KB)
- `dalvik-port/compat/oh_drawing_bridge.c` — OH_Drawing wrapper (50KB)
- `dalvik-port/compat/software_canvas.h` — software renderer

### P1: Direct JNI Input (20x input speedup)

**Current:** Touch input via file IPC (~100ms latency).

**Task:** Implement direct touch input callback via QEMU virtio-input or shared memory.

1. The QEMU shared memory layout is defined in `dalvik_ohos_runner.c`:
   ```
   Header [0-31]: width, height, frame_counter, touch_action, touch_x, touch_y
   Pixels [32+]: ARGB8888 frame buffer
   ```
2. Create a native thread in `liboh_bridge.so` that polls the shared memory for touch events
3. Call back into Java via JNI: `Activity.dispatchTouchEvent(MotionEvent)`
4. Target: <10ms touch-to-Java latency

**Files:**
- `dalvik-port/compat/arkui_bridge.cpp` — add input polling thread
- `openharmony-wsl/scripts/dalvik_ohos_runner.c` — shared memory manager

### P2: 16ms Vsync Frame Loop

**Task:** Implement a 16ms frame loop that:
1. Calls `Activity.renderFrame()` which does measure/layout/draw
2. Blits the resulting bitmap to display (fb0 or shared memory)
3. Syncs at 60fps

**Implementation:**
```c
while (running) {
    // 1. Call Java: activity.renderFrame()
    env->CallVoidMethod(activity, renderFrame_mid);

    // 2. Get bitmap pixels from Canvas
    void* pixels = env->CallLongMethod(canvas, getPixels_mid);

    // 3. Blit to framebuffer
    memcpy(fb0_mmap, pixels, width * height * 4);

    // 4. Wait for vsync (16ms)
    usleep(16000 - elapsed);
}
```

---

## Environment

### OHOS ARM64 QEMU
- Kernel: Linux 5.10 ARM64 at `/home/dspfac/openharmony-arm64/images/Image`
- System: `/tmp/ohos-arm64-images/system.img` (OHOS minimal)
- Userdata with ART: `/tmp/ohos-arm64-images/userdata-art.img`
- QEMU: `/home/dspfac/openharmony/tools/qemu-extracted/usr/bin/qemu-system-aarch64`

### Boot command
```bash
QEMU_DIR=/home/dspfac/openharmony/tools/qemu-extracted/usr
LD_LIBRARY_PATH=$QEMU_DIR/lib/x86_64-linux-gnu \
$QEMU_DIR/bin/qemu-system-aarch64 \
  -M virt -cpu cortex-a57 -smp 2 -m 1024 -nographic \
  -L $QEMU_DIR/share/qemu -nic none \
  -drive if=none,file=userdata-art.img,format=raw,id=userdata -device virtio-blk-device,drive=userdata \
  -drive if=none,file=vendor.img,format=raw,id=vendor -device virtio-blk-device,drive=vendor \
  -drive if=none,file=system.img,format=raw,id=system -device virtio-blk-device,drive=system \
  -kernel Image -initrd initramfs.img \
  -append 'console=ttyAMA0,115200 rdinit=/init'
```

### ART deployment on OHOS
All files at `/data/a2oh/`:
```
dalvikvm          11MB   ART ARM64 static binary
boot.art          668KB  Pre-initialized heap
boot.oat          9.8MB  AOT compiled core library (ARM64)
core-oj.jar       4.8MB  Core library DEX
core-libart.jar   645KB
core-icu4j.jar    2.6MB
aosp-shim.dex     3.5MB  AOSP framework (3,378 classes)
app.dex           21KB   MockDonalds app
liboh_bridge.so   341KB  172 JNI native methods (ARM64)
```

### Cross-compile liboh_bridge.so for ARM64
```bash
OHOS_CXX=/home/dspfac/openharmony/prebuilts/clang/ohos/linux-x86_64/llvm/bin/clang++
OHOS_SYSROOT=/home/dspfac/android-to-openharmony-migration/dalvik-port/ohos-sysroot
$OHOS_CXX --target=aarch64-linux-ohos --sysroot=$OHOS_SYSROOT \
  -shared -fPIC -O2 -I$COMPAT \
  -I/home/dspfac/aosp-android-11/libnativehelper/include_jni \
  -o liboh_bridge.so arkui_bridge.cpp
```

### Rebuild OHOS with graphics (if needed)
Edit `/home/dspfac/openharmony/vendor/ohemu/qemu_arm64_linux_min/config.json`, add:
```json
{ "subsystem": "graphic", "components": [
    { "component": "graphic_2d", "features": [] }
]}
```
Then: `cd /home/dspfac/openharmony && ./build.sh --product-name qemu-arm64-linux-min --ccache --no-prebuilt-sdk -j8`

---

## Success criteria

1. MockDonalds menu renders to framebuffer — visible via VNC or PNG dump
2. Touch input navigates between Activities
3. 30+ fps frame rate on QEMU ARM64
4. Side-by-side screenshot comparison with real Android

## Priority order

**P0 → P1 → P2** (rendering first, then input, then smooth frames)
