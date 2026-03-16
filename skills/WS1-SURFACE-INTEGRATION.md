# WS1 Task 10: Surface Integration — Canvas → XComponent → NativeWindow

## Goal

Wire the final rendering pipeline so that Android View trees actually draw pixels to the OHOS screen. Currently Canvas draws to an in-memory OH_Drawing_Bitmap but nothing puts those pixels on screen. This task connects:

```
Activity.getWindow().getDecorView().draw(canvas)
  → OH_Drawing_Bitmap pixel buffer
    → OHNativeWindow buffer
      → Display
```

## What Already Exists

### Java side (DONE)
| File | What's there |
|------|-------------|
| `shim/java/android/graphics/Canvas.java` | All draw ops wired to OHBridge. Constructor takes Bitmap, creates native canvas via `OHBridge.canvasCreate(bitmap.getNativeHandle())`. Has pen/brush/font cache. |
| `shim/java/android/graphics/Bitmap.java` | `nativeHandle` (jlong), `getNativeHandle()`, `createBitmap(w,h,config)` → `OHBridge.bitmapCreate()`, `recycle()` → `OHBridge.bitmapDestroy()` |
| `shim/java/android/app/Activity.java` | Has `mWindow` (Window), `getWindow()`, `setContentView(View)` delegates to Window. Lifecycle methods `onCreate`/`onResume`/`onPause` exist. |
| `shim/java/android/view/Window.java` | Has `mDecorView` (FrameLayout), `mContentView`. `setContentView(View)` adds view to decor via `addView()`. `getDecorView()` returns root. |
| `shim/java/android/view/View.java` | Has `nativeHandle`, position fields (`mLeft/mTop/mRight/mBottom`), `layout(l,t,r,b)`. Has `dispatchDraw(Object)` and `onDraw(Object)` but they take **Object not Canvas** — need overloads or fix. No `draw(Canvas)` method exists yet. |
| `shim/java/android/view/ViewGroup.java` | Has `mChildren` list, `addView`, `removeView`, `getChildCount`, `getChildAt`. No `dispatchDraw(Canvas)` yet. |
| `shim/java/com/ohos/shim/bridge/OHBridge.java` | 48 canvas/pen/brush/path/bitmap/font native declarations. No xcomponent/surface methods yet. |

### Rust side (DONE)
| File | What's there |
|------|-------------|
| `shim/bridge/rust/src/oh_drawing.rs` (682 lines) | All 49 JNI→OH_Drawing bridge functions. Canvas create/destroy, all draw ops, pen/brush/path/bitmap/font lifecycle. |
| `shim/bridge/rust/src/oh_ffi.rs` | Full OH_Drawing C FFI declarations (Bitmap, Canvas, Pen, Brush, Path, Rect, RoundRect, Matrix, Font, TextBlob). NO XComponent/NativeWindow declarations. |
| `shim/bridge/rust/src/view.rs` (227 lines) | ArkUI node lifecycle (create/destroy/addChild/removeChild), attribute setters, event dispatch via JNI callback to `OHBridge.dispatchNodeEvent()`. Stores JVM in `OnceCell`. NO frame/surface callbacks. |

### C++ side
| File | What's there |
|------|-------------|
| `shim/bridge/cpp/cpp_shim.cpp` (628 lines) | ArkUI node management, event receiver. Has one XComponent stub at line 623: `shim_xcomponent_attach_root(void* xcomponent, long long root_node)` → calls `OH_NativeXComponent_AttachNativeRootNode()`. NO surface lifecycle callbacks. |
| `shim/bridge/cpp/cpp_shim.h` (142 lines) | Declares `shim_xcomponent_attach_root`. Nothing else for XComponent/NativeWindow. |

### What's NOT there (your job)
1. `View.draw(Canvas)` / `ViewGroup.dispatchDraw(Canvas)` — the Java render traversal
2. XComponent surface lifecycle callbacks (created/destroyed/resized)
3. Frame rendering trigger (vsync or manual)
4. Bitmap pixel buffer → NativeWindow blitting
5. OHBridge Java declarations for surface management
6. Rust surface module managing per-Activity rendering state
7. C++ XComponent callback registration and NativeWindow buffer ops

---

## Implementation Plan

### Step 1: Java View.draw(Canvas) traversal

**Files:** `shim/java/android/view/View.java`, `shim/java/android/view/ViewGroup.java`

The AOSP `View.draw(Canvas)` method follows this sequence:
```java
public void draw(Canvas canvas) {
    // 1. Draw background (skip for now)
    // 2. Save canvas layer if needed
    // 3. Call onDraw(canvas) — subclass draws itself
    // 4. Call dispatchDraw(canvas) — ViewGroup draws children
    // 5. Draw foreground/scrollbars (skip for now)
}
```

Add to **View.java**:
```java
public void draw(android.graphics.Canvas canvas) {
    // Step 3: let subclass draw
    onDraw(canvas);
    // Step 4: dispatch to children (ViewGroup overrides this)
    dispatchDraw(canvas);
}

protected void onDraw(android.graphics.Canvas canvas) {
    // Base View does nothing. Subclasses override.
}

protected void dispatchDraw(android.graphics.Canvas canvas) {
    // Base View has no children. ViewGroup overrides.
}
```

Note: The existing `onDraw(Object)` and `dispatchDraw(Object)` stubs with Object params must remain for binary compatibility. Add Canvas-typed overloads alongside them. If an existing subclass (like a Widget) overrides `onDraw(Object)`, it won't get called by the new typed `draw(Canvas)` — but that's fine because we'll update widgets to override `onDraw(Canvas)` as needed.

Add to **ViewGroup.java**:
```java
@Override
protected void dispatchDraw(android.graphics.Canvas canvas) {
    for (int i = 0; i < mChildren.size(); i++) {
        View child = mChildren.get(i);
        if (child.getVisibility() != GONE) {
            drawChild(canvas, child);
        }
    }
}

protected void drawChild(android.graphics.Canvas canvas, View child) {
    int save = canvas.save();
    // Translate canvas to child's position
    canvas.translate(child.getLeft(), child.getTop());
    // Clip to child bounds
    canvas.clipRect(0, 0, child.getWidth(), child.getHeight());
    child.draw(canvas);
    canvas.restore();
}
```

**Test:** Create a custom View subclass that overrides `onDraw(Canvas)`, put it in a ViewGroup, call `draw(canvas)` on the group, verify the mock draw log has the expected ops.

### Step 2: OHBridge surface declarations (Java + Mock)

**File:** `shim/java/com/ohos/shim/bridge/OHBridge.java`

Add:
```java
// ── Surface / XComponent ────────────────────────────────────
// Returns a surface context handle (opaque jlong)
public static native long surfaceCreate(long xcomponentHandle, int width, int height);
public static native void surfaceDestroy(long surfaceCtx);
public static native void surfaceResize(long surfaceCtx, int width, int height);
// Returns the canvas handle bound to this surface's bitmap
public static native long surfaceGetCanvas(long surfaceCtx);
// Blit the surface bitmap to NativeWindow and flush to display
public static native int surfaceFlush(long surfaceCtx);
```

**File:** `test-apps/mock/com/ohos/shim/bridge/OHBridge.java`

Add mock implementations that:
- `surfaceCreate` → create a Bitmap + Canvas internally, return handle
- `surfaceDestroy` → cleanup
- `surfaceGetCanvas` → return the canvas handle
- `surfaceFlush` → no-op (mock doesn't have a real display)
- Track surface state in a ConcurrentHashMap for testing

### Step 3: Rust surface module

**New file:** `shim/bridge/rust/src/surface.rs`

This module manages per-XComponent rendering state:

```rust
use std::collections::HashMap;
use std::sync::Mutex;
use once_cell::sync::Lazy;
use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::{jint, jlong};
use crate::oh_ffi;

struct SurfaceContext {
    bitmap: *mut oh_ffi::OH_Drawing_Bitmap,
    canvas: *mut oh_ffi::OH_Drawing_Canvas,
    width: u32,
    height: u32,
}

static SURFACES: Lazy<Mutex<HashMap<jlong, SurfaceContext>>> =
    Lazy::new(|| Mutex::new(HashMap::new()));

static NEXT_ID: std::sync::atomic::AtomicI64 = std::sync::atomic::AtomicI64::new(1);

#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_surfaceCreate(
    _env: JNIEnv, _class: JClass,
    _xcomponent: jlong, width: jint, height: jint,
) -> jlong {
    let w = width as u32;
    let h = height as u32;

    // Create bitmap
    let bitmap = oh_ffi::OH_Drawing_BitmapCreate();
    let fmt = oh_ffi::OH_Drawing_BitmapFormat {
        color_type: 3, // ARGB_8888
        alpha_type: 2, // premul
    };
    oh_ffi::OH_Drawing_BitmapBuild(bitmap, w, h, &fmt);

    // Create canvas bound to bitmap
    let canvas = oh_ffi::OH_Drawing_CanvasCreate();
    oh_ffi::OH_Drawing_CanvasBind(canvas, bitmap);

    let id = NEXT_ID.fetch_add(1, std::sync::atomic::Ordering::Relaxed);
    let ctx = SurfaceContext { bitmap, canvas, width: w, height: h };
    SURFACES.lock().unwrap().insert(id, ctx);
    id
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_surfaceDestroy(
    _env: JNIEnv, _class: JClass, surface_ctx: jlong,
) {
    if let Some(ctx) = SURFACES.lock().unwrap().remove(&surface_ctx) {
        oh_ffi::OH_Drawing_CanvasDestroy(ctx.canvas);
        oh_ffi::OH_Drawing_BitmapDestroy(ctx.bitmap);
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_surfaceResize(
    _env: JNIEnv, _class: JClass,
    surface_ctx: jlong, width: jint, height: jint,
) {
    let mut map = SURFACES.lock().unwrap();
    if let Some(ctx) = map.get_mut(&surface_ctx) {
        // Destroy old bitmap+canvas, recreate at new size
        oh_ffi::OH_Drawing_CanvasDestroy(ctx.canvas);
        oh_ffi::OH_Drawing_BitmapDestroy(ctx.bitmap);

        let w = width as u32;
        let h = height as u32;
        let bitmap = oh_ffi::OH_Drawing_BitmapCreate();
        let fmt = oh_ffi::OH_Drawing_BitmapFormat {
            color_type: 3,
            alpha_type: 2,
        };
        oh_ffi::OH_Drawing_BitmapBuild(bitmap, w, h, &fmt);
        let canvas = oh_ffi::OH_Drawing_CanvasCreate();
        oh_ffi::OH_Drawing_CanvasBind(canvas, bitmap);

        ctx.bitmap = bitmap;
        ctx.canvas = canvas;
        ctx.width = w;
        ctx.height = h;
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_surfaceGetCanvas(
    _env: JNIEnv, _class: JClass, surface_ctx: jlong,
) -> jlong {
    let map = SURFACES.lock().unwrap();
    match map.get(&surface_ctx) {
        Some(ctx) => ctx.canvas as jlong,
        None => 0,
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_surfaceFlush(
    _env: JNIEnv, _class: JClass, surface_ctx: jlong,
) -> jint {
    // TODO: Blit bitmap pixels to NativeWindow
    // For now this is a no-op until NativeWindow integration is added.
    //
    // When NativeWindow is available, the flow is:
    // 1. OH_NativeWindow_NativeWindowRequestBuffer(window, &buffer, &fenceFd)
    // 2. memcpy bitmap pixels → buffer pixels
    // 3. OH_NativeWindow_NativeWindowFlushBuffer(window, buffer, fenceFd, region)
    0
}
```

**Register in lib.rs:** Add `mod surface;`

### Step 4: Java rendering integration in Activity

**File:** `shim/java/android/app/Activity.java`

Add surface management and a render method:

```java
// ── Surface rendering ──
private long mSurfaceCtx;    // Handle from OHBridge.surfaceCreate
private int mSurfaceWidth;
private int mSurfaceHeight;

/**
 * Called when the XComponent surface is available.
 * Creates a bitmap-backed canvas for this Activity's view tree.
 */
public void onSurfaceCreated(long xcomponentHandle, int width, int height) {
    mSurfaceWidth = width;
    mSurfaceHeight = height;
    if (OHBridge.isNativeAvailable()) {
        mSurfaceCtx = OHBridge.surfaceCreate(xcomponentHandle, width, height);
    }
}

public void onSurfaceDestroyed() {
    if (mSurfaceCtx != 0) {
        OHBridge.surfaceDestroy(mSurfaceCtx);
        mSurfaceCtx = 0;
    }
}

/**
 * Render one frame: measure → layout → draw the view tree, then flush to screen.
 * Called from native vsync/frame callback or manually.
 */
public void renderFrame() {
    if (mSurfaceCtx == 0 || mWindow == null) return;

    View decorView = mWindow.getDecorView();
    if (decorView == null) return;

    // 1. Layout the view tree to fill the surface
    decorView.layout(0, 0, mSurfaceWidth, mSurfaceHeight);

    // 2. Get the surface's canvas (wraps native OH_Drawing_Canvas)
    long canvasHandle = OHBridge.surfaceGetCanvas(mSurfaceCtx);
    if (canvasHandle == 0) return;

    // 3. Wrap the native canvas handle in a Java Canvas
    //    Need a Canvas constructor that takes a raw native handle
    android.graphics.Canvas canvas = new android.graphics.Canvas(canvasHandle, mSurfaceWidth, mSurfaceHeight);

    // 4. Clear and draw
    canvas.drawColor(0xFFFFFFFF); // white background
    decorView.draw(canvas);

    // 5. Flush pixels to display
    OHBridge.surfaceFlush(mSurfaceCtx);
}
```

**File:** `shim/java/android/graphics/Canvas.java`

Add a constructor that wraps an existing native canvas handle (for the surface case):

```java
/**
 * Wrap an existing native OH_Drawing_Canvas handle (e.g. from a surface).
 * Used by the rendering pipeline — the canvas is NOT owned by this wrapper.
 */
Canvas(long nativeCanvasHandle, int width, int height) {
    this.bitmap = null;
    this.nativeCanvas = nativeCanvasHandle;
    this.surfaceWidth = width;
    this.surfaceHeight = height;
}
```

Add `surfaceWidth`/`surfaceHeight` fields and update `getWidth()`/`getHeight()` to fall back to them when bitmap is null.

### Step 5: C++ XComponent callback registration

**File:** `shim/bridge/cpp/cpp_shim.h`

Add:
```cpp
// XComponent lifecycle callbacks
int shim_xcomponent_register_callbacks(void* xcomponent);
void shim_xcomponent_on_surface_created(void* xcomponent, void* window, int width, int height);
void shim_xcomponent_on_surface_changed(void* xcomponent, int width, int height);
void shim_xcomponent_on_surface_destroyed(void* xcomponent);
void shim_xcomponent_on_frame(void* xcomponent);
```

**File:** `shim/bridge/cpp/cpp_shim.cpp`

Implement XComponent callback registration using OH NDK:

```cpp
#include <native_interface_xcomponent.h>

// Global: maps xcomponent ID → JNI activity reference
static std::unordered_map<std::string, void*> g_xcomponent_map;

static void on_surface_created(OH_NativeXComponent* xcomp, void* window) {
    uint64_t width, height;
    OH_NativeXComponent_GetXComponentSize(xcomp, window, &width, &height);
    // Call into Rust → Java: Activity.onSurfaceCreated(xcomp, width, height)
    shim_surface_on_created(xcomp, window, (int)width, (int)height);
}

static void on_surface_changed(OH_NativeXComponent* xcomp, void* window) {
    uint64_t width, height;
    OH_NativeXComponent_GetXComponentSize(xcomp, window, &width, &height);
    shim_surface_on_changed(xcomp, (int)width, (int)height);
}

static void on_surface_destroyed(OH_NativeXComponent* xcomp, void* window) {
    shim_surface_on_destroyed(xcomp);
}

int shim_xcomponent_register_callbacks(void* xcomp) {
    OH_NativeXComponent_Callback cb = {
        .OnSurfaceCreated = on_surface_created,
        .OnSurfaceChanged = on_surface_changed,
        .OnSurfaceDestroyed = on_surface_destroyed,
    };
    return OH_NativeXComponent_RegisterCallback(
        static_cast<OH_NativeXComponent*>(xcomp), &cb);
}
```

### Step 6: Rust FFI for NativeWindow + XComponent

**File:** `shim/bridge/rust/src/oh_ffi.rs`

Add:
```rust
// ═══════════════════════════════════════════════════════════
// NativeWindow — buffer operations (libnative_window.z.so)
// ═══════════════════════════════════════════════════════════

pub type OHNativeWindow = c_void;
pub type OHNativeWindowBuffer = c_void;

extern "C" {
    pub fn OH_NativeWindow_NativeWindowRequestBuffer(
        window: *mut OHNativeWindow,
        buffer: *mut *mut OHNativeWindowBuffer,
        fence_fd: *mut c_int,
    ) -> c_int;

    pub fn OH_NativeWindow_NativeWindowFlushBuffer(
        window: *mut OHNativeWindow,
        buffer: *mut OHNativeWindowBuffer,
        fence_fd: c_int,
        region: *mut c_void,  // OH_NativeWindow_Region or null
    ) -> c_int;

    pub fn OH_NativeWindow_NativeWindowHandleOpt(
        window: *mut OHNativeWindow,
        code: c_int,
        ...
    ) -> c_int;

    // Get buffer stride/size for memcpy
    pub fn OH_NativeWindow_GetBufferHandleFromNative(
        buffer: *mut OHNativeWindowBuffer,
    ) -> *mut BufferHandle;
}

#[repr(C)]
pub struct BufferHandle {
    pub fd: c_int,
    pub width: c_int,
    pub height: c_int,
    pub stride: c_int,
    pub format: c_int,
    pub usage: u64,
    pub virAddr: *mut c_void,
    // ... more fields exist but we only need these
}
```

### Step 7: Wire surfaceFlush to actually blit pixels

Update `surface.rs` `surfaceFlush` to:
1. Get bitmap pixels via `OH_Drawing_BitmapGetPixels(bitmap)` → raw `*mut u8`
2. Request a NativeWindow buffer
3. memcpy bitmap pixels → window buffer (row by row, respecting stride)
4. Flush the buffer

The SurfaceContext needs to store the NativeWindow handle (passed from C++ on_surface_created).

### Step 8: ArkTS page with XComponent

For the OHOS app, create an ArkTS page that hosts the XComponent:

**File:** `test-apps/ohos-host/entry/src/main/ets/pages/Index.ets` (or similar)

```typescript
@Entry
@Component
struct Index {
  private xComponentId: string = 'android_surface'
  private xComponentType: string = 'surface'

  build() {
    Column() {
      XComponent({
        id: this.xComponentId,
        type: this.xComponentType,
        libraryname: 'oh_bridge'  // loads liboh_bridge.so
      })
      .width('100%')
      .height('100%')
    }
  }
}
```

When XComponent loads, it calls `OH_NativeXComponent_RegisterCallback` which fires `on_surface_created` → our C++ callback → Rust → Java `Activity.onSurfaceCreated()`.

---

## Testing Strategy

### What CAN be tested on JVM (mock bridge)
1. **View.draw(Canvas) traversal** — verify draw log captures ops from View tree
2. **Surface lifecycle** — surfaceCreate/surfaceDestroy/surfaceGetCanvas via mock
3. **Activity.renderFrame()** — mock surface + verify decorView.draw() is called
4. **ViewGroup.dispatchDraw** — child ordering, translate/clip per child

### What CANNOT be tested without OHOS device
- NativeWindow buffer blitting (surfaceFlush real path)
- XComponent callback registration
- Actual pixel output on screen
- Frame timing / vsync

### Required tests (add to HeadlessTest.java)

```java
static void testSurfaceRendering() {
    section("Surface rendering pipeline");

    // Surface lifecycle
    long surfaceCtx = OHBridge.surfaceCreate(0, 320, 480);
    check("surfaceCreate returns handle", surfaceCtx != 0);

    long canvasHandle = OHBridge.surfaceGetCanvas(surfaceCtx);
    check("surfaceGetCanvas returns handle", canvasHandle != 0);

    int flushResult = OHBridge.surfaceFlush(surfaceCtx);
    check("surfaceFlush returns 0", flushResult == 0);

    OHBridge.surfaceDestroy(surfaceCtx);

    // View.draw(Canvas) traversal
    android.graphics.Bitmap bmp = android.graphics.Bitmap.createBitmap(100, 100,
            android.graphics.Bitmap.Config.ARGB_8888);
    android.graphics.Canvas canvas = new android.graphics.Canvas(bmp);

    // Custom view that draws a rect
    android.view.View customView = new android.view.View() {
        @Override
        protected void onDraw(android.graphics.Canvas c) {
            android.graphics.Paint p = new android.graphics.Paint();
            p.setColor(0xFFFF0000);
            c.drawRect(0, 0, 50, 50, p);
        }
    };
    customView.draw(canvas);

    java.util.List<OHBridge.DrawRecord> log = OHBridge.getDrawLog(canvas.getNativeHandle());
    check("custom view onDraw produced drawRect", log.size() > 0 && "drawRect".equals(log.get(0).op));

    // ViewGroup dispatches to children
    OHBridge.clearDrawLog(canvas.getNativeHandle());
    android.view.ViewGroup group = new android.view.ViewGroup() {};
    group.addView(customView);
    customView.layout(10, 20, 60, 70);
    group.draw(canvas);

    log = OHBridge.getDrawLog(canvas.getNativeHandle());
    check("viewgroup draw produces save", log.stream().anyMatch(r -> "save".equals(r.op)));
    check("viewgroup draw produces drawRect from child", log.stream().anyMatch(r -> "drawRect".equals(r.op)));

    // Activity.renderFrame integration
    android.app.Activity activity = new android.app.Activity();
    activity.onCreate(null);
    android.view.View contentView = new android.view.View() {
        @Override
        protected void onDraw(android.graphics.Canvas c) {
            c.drawColor(0xFF00FF00);
        }
    };
    activity.setContentView(contentView);
    activity.onSurfaceCreated(0, 100, 100);
    activity.renderFrame();
    // If we got here without exception, the pipeline works

    canvas.release();
    bmp.recycle();
}
```

---

## File Change Summary

| File | Action |
|------|--------|
| `shim/java/android/view/View.java` | Add `draw(Canvas)`, `onDraw(Canvas)`, `dispatchDraw(Canvas)` |
| `shim/java/android/view/ViewGroup.java` | Override `dispatchDraw(Canvas)`, add `drawChild(Canvas, View)` |
| `shim/java/android/graphics/Canvas.java` | Add constructor `Canvas(long handle, int w, int h)`, surfaceWidth/Height fields |
| `shim/java/android/app/Activity.java` | Add `onSurfaceCreated`, `onSurfaceDestroyed`, `renderFrame()` |
| `shim/java/com/ohos/shim/bridge/OHBridge.java` | Add `surfaceCreate/Destroy/Resize/GetCanvas/Flush` native declarations |
| `test-apps/mock/com/ohos/shim/bridge/OHBridge.java` | Add mock surface methods |
| `shim/bridge/rust/src/surface.rs` | **NEW** — Surface lifecycle + NativeWindow blitting |
| `shim/bridge/rust/src/lib.rs` | Add `mod surface;` |
| `shim/bridge/rust/src/oh_ffi.rs` | Add NativeWindow FFI declarations |
| `shim/bridge/rust/build.rs` | Add `cargo:rustc-link-lib=dylib=native_window` |
| `shim/bridge/cpp/cpp_shim.h` | Add XComponent callback declarations |
| `shim/bridge/cpp/cpp_shim.cpp` | Implement XComponent lifecycle → Rust surface callbacks |
| `test-apps/02-headless-cli/src/HeadlessTest.java` | Add `testSurfaceRendering()` |

---

## OH NDK API Reference

### XComponent (native_interface_xcomponent.h)
```c
OH_NativeXComponent_RegisterCallback(OH_NativeXComponent*, OH_NativeXComponent_Callback*)
OH_NativeXComponent_GetXComponentSize(OH_NativeXComponent*, void* window, uint64_t* w, uint64_t* h)
OH_NativeXComponent_AttachNativeRootNode(OH_NativeXComponent*, ArkUI_NodeHandle) // already used
```

### NativeWindow (native_window.h / buffer_handle.h)
```c
OH_NativeWindow_NativeWindowRequestBuffer(OHNativeWindow*, OHNativeWindowBuffer**, int* fenceFd)
OH_NativeWindow_NativeWindowFlushBuffer(OHNativeWindow*, OHNativeWindowBuffer*, int fenceFd, Region*)
OH_NativeWindow_NativeWindowHandleOpt(OHNativeWindow*, int code, ...) // SET_BUFFER_GEOMETRY etc.
OH_NativeWindow_GetBufferHandleFromNative(OHNativeWindowBuffer*) → BufferHandle*
// BufferHandle.virAddr = mmap'd pixel buffer, .stride = row bytes
```

### Data flow per frame
```
XComponent surface ready
  → C++ on_surface_created: OH_NativeXComponent_GetXComponentSize → width, height
  → Rust surfaceCreate: BitmapCreate(w,h) + CanvasCreate() + CanvasBind(canvas, bitmap)
  → Java Activity.onSurfaceCreated(handle, w, h)

Per frame:
  → Java Activity.renderFrame()
    → decorView.layout(0, 0, w, h)
    → Canvas canvas = new Canvas(surfaceGetCanvas(), w, h)
    → canvas.drawColor(0xFFFFFFFF)
    → decorView.draw(canvas) [recurse through view tree]
    → surfaceFlush()
      → Rust: OH_Drawing_BitmapGetPixels(bitmap) → src pixels
      → Rust: OH_NativeWindow_NativeWindowRequestBuffer → dst buffer
      → Rust: memcpy src → dst (row by row, match stride)
      → Rust: OH_NativeWindow_NativeWindowFlushBuffer → display
```

---

## Priority for CC implementation (no OHOS device)

1. **Step 1** — View.draw(Canvas) traversal (pure Java, fully testable) — DO FIRST
2. **Step 2** — OHBridge surface declarations + mock (fully testable)
3. **Step 3** — Rust surface.rs (code gen, can't compile without OH SDK)
4. **Step 4** — Activity rendering integration (testable with mock)
5. **Step 6** — Rust FFI NativeWindow declarations (code gen)
6. **Step 5** — C++ XComponent callbacks (code gen, can't test without device)
7. **Step 7** — surfaceFlush real implementation (needs device)
8. **Step 8** — ArkTS host page (needs OHOS project)

Steps 1-4 produce a **fully testable rendering pipeline on JVM**. Steps 5-8 are the device-specific wiring that makes it show pixels on screen.
