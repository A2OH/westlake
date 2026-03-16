# WS1: Canvas → OH_Drawing — Remaining Work

## What's Already Done (Phase 1 — Java side complete)

All Java shim wiring and mock testing is finished:

| File | Status | What was done |
|------|--------|---------------|
| `shim/java/com/ohos/shim/bridge/OHBridge.java` | DONE | 45 native method declarations added (canvas, pen, brush, path, bitmap, font) |
| `test-apps/mock/com/ohos/shim/bridge/OHBridge.java` | DONE | Full mock: `DrawRecord` logging, handle tracking, `isNativeAvailable()`, `getDrawLog()` / `clearDrawLog()` helpers |
| `shim/java/android/graphics/Canvas.java` | DONE | Native handle, pen/brush/font cache, all draw ops route through OHBridge, save/restore/clip/transform wired, `release()` cleanup |
| `shim/java/android/graphics/Bitmap.java` | DONE | `nativeHandle`, `getNativeHandle()`, `setPixel()`/`getPixel()` via OHBridge, `recycle()` destroys native, `configToFormat()` |
| `shim/java/android/graphics/Path.java` | DONE | `nativeHandle`, all ops forwarded (moveTo/lineTo/quadTo/cubicTo/close/reset/addRect/addCircle), `release()` |
| `shim/java/android/graphics/Paint.java` | DONE | No changes needed — Canvas syncs Paint state to native pen/brush per-draw |
| `test-apps/02-headless-cli/src/HeadlessTest.java` | DONE | `testCanvasBridge()` — 36 checks (drawRect, drawCircle, drawLine, drawText, drawPath, drawColor, save/restore, Path lifecycle, Bitmap lifecycle, DrawRecord verification, release) |

Test results: 790 pass, 9 fail (all failures pre-existing).

---

## What Remains

### Task 1: Missing Canvas Draw Operations (Java — pure Java, testable now)

Canvas.java has these methods as no-ops that should be wired:

```java
// These exist but are no-ops — need OHBridge native declarations + mock + wiring:
drawArc(float l, float t, float r, float b, float startAngle, float sweepAngle, boolean useCenter, Paint paint)
drawOval(float l, float t, float r, float b, Paint paint)
drawRoundRect(...)  // currently falls back to drawRect — needs OH_Drawing_RoundRect
```

**What to do:**
1. Add to `OHBridge.java` (real):
   ```java
   public static native void canvasDrawArc(long canvas, float l, float t, float r, float b, float startAngle, float sweepAngle, long pen, long brush);
   public static native void canvasDrawOval(long canvas, float l, float t, float r, float b, long pen, long brush);
   public static native void canvasDrawRoundRect(long canvas, float l, float t, float r, float b, float rx, float ry, long pen, long brush);
   ```
2. Add mock implementations in `test-apps/mock/.../OHBridge.java`
3. Wire in `Canvas.java` (replace no-op comments)
4. Add tests in `HeadlessTest.java`

**Effort:** Small (~30 min)

---

### Task 2: Missing Paint Properties (Java — testable now)

Paint has these AOSP properties that apps use but aren't stored yet:

```java
// Missing from Paint.java:
Cap strokeCap;      // BUTT, ROUND, SQUARE — maps to OH_Drawing penSetCap
Join strokeJoin;     // MITER, ROUND, BEVEL — maps to OH_Drawing penSetJoin
Align textAlign;     // LEFT, CENTER, RIGHT
Typeface typeface;   // font family
ColorFilter colorFilter;
Shader shader;       // LinearGradient, RadialGradient, BitmapShader
MaskFilter maskFilter;
PathEffect pathEffect;
Xfermode xfermode;   // PorterDuff blend mode
```

**What to do:**
1. Add `Cap`, `Join` enums and fields to `Paint.java`
2. Update `Canvas.ensurePen()` to sync cap/join to OHBridge
3. Add `penSetCap` / `penSetJoin` calls (declarations already exist in OHBridge)
4. Other properties (shader, colorFilter, etc.) can be stored as fields but won't have native backing until the Rust bridge exists

**Effort:** Small (~20 min for Cap/Join, ~30 min for all other field stubs)

---

### Task 3: BitmapFactory / ImageDecoder (Java — medium complexity)

Apps load images via `BitmapFactory.decodeFile()`, `BitmapFactory.decodeStream()`, `BitmapFactory.decodeByteArray()`. Currently these are stubs returning null.

**What to do:**
1. `BitmapFactory.decodeByteArray()` — detect PNG/JPEG headers, extract width/height from header bytes, create Bitmap with native handle
2. For JVM testing: parse image headers only (don't decode pixels)
3. For OHOS: add `OHBridge.bitmapDecode(byte[] data, int offset, int length)` → `OH_ImageSource` NDK
4. Wire `BitmapFactory.Options` (inSampleSize, inJustDecodeBounds, outWidth, outHeight)

**Effort:** Medium (~2 hours for header-only JVM path, large for full decode)

---

### Task 4: Matrix Transform Support (Java — testable now)

`Canvas.concat(Matrix)`, `Canvas.setMatrix(Matrix)`, `Canvas.getMatrix()` are missing. Matrix.java already has a full 3x3 implementation. Need:

1. Add `OHBridge.canvasConcat(long canvas, float[] matrix9)` native declaration + mock
2. Wire `Canvas.concat()` and `Canvas.setMatrix()`
3. Add `canvasGetMatrix` if needed (rare in apps)

**Effort:** Small (~30 min)

---

### Task 5: OH_Drawing FFI Declarations (Rust — `oh_ffi.rs`)

File: `shim/bridge/rust/src/oh_ffi.rs`

Add OH_Drawing C NDK bindings. These map 1:1 to the OH NDK headers:

```rust
// ═══════════════════════════════════════════════════════════════════
// OH_Drawing — native_drawing NDK (libace_ndk.z.so or libnative_drawing.z.so)
// Headers: native_drawing/drawing_canvas.h, drawing_pen.h, etc.
// ═══════════════════════════════════════════════════════════════════

pub type OH_Drawing_Canvas = c_void;
pub type OH_Drawing_Pen = c_void;
pub type OH_Drawing_Brush = c_void;
pub type OH_Drawing_Path = c_void;
pub type OH_Drawing_Bitmap = c_void;
pub type OH_Drawing_Font = c_void;
pub type OH_Drawing_TextBlob = c_void;
pub type OH_Drawing_Rect = c_void;
pub type OH_Drawing_RoundRect = c_void;
pub type OH_Drawing_Point = c_void;

extern "C" {
    // ── Bitmap ──
    pub fn OH_Drawing_BitmapCreate() -> *mut OH_Drawing_Bitmap;
    pub fn OH_Drawing_BitmapDestroy(bitmap: *mut OH_Drawing_Bitmap);
    pub fn OH_Drawing_BitmapBuild(
        bitmap: *mut OH_Drawing_Bitmap,
        width: u32, height: u32,
        format: *const OH_Drawing_BitmapFormat,
    ) -> bool;
    pub fn OH_Drawing_BitmapGetWidth(bitmap: *mut OH_Drawing_Bitmap) -> u32;
    pub fn OH_Drawing_BitmapGetHeight(bitmap: *mut OH_Drawing_Bitmap) -> u32;
    pub fn OH_Drawing_BitmapGetPixels(bitmap: *mut OH_Drawing_Bitmap) -> *mut c_void;

    // ── Canvas ──
    pub fn OH_Drawing_CanvasCreate() -> *mut OH_Drawing_Canvas;
    pub fn OH_Drawing_CanvasDestroy(canvas: *mut OH_Drawing_Canvas);
    pub fn OH_Drawing_CanvasBind(canvas: *mut OH_Drawing_Canvas, bitmap: *mut OH_Drawing_Bitmap);
    pub fn OH_Drawing_CanvasAttachPen(canvas: *mut OH_Drawing_Canvas, pen: *const OH_Drawing_Pen);
    pub fn OH_Drawing_CanvasDetachPen(canvas: *mut OH_Drawing_Canvas);
    pub fn OH_Drawing_CanvasAttachBrush(canvas: *mut OH_Drawing_Canvas, brush: *const OH_Drawing_Brush);
    pub fn OH_Drawing_CanvasDetachBrush(canvas: *mut OH_Drawing_Canvas);
    pub fn OH_Drawing_CanvasDrawRect(canvas: *mut OH_Drawing_Canvas, rect: *const OH_Drawing_Rect);
    pub fn OH_Drawing_CanvasDrawCircle(canvas: *mut OH_Drawing_Canvas, cx: f32, cy: f32, radius: f32);
    pub fn OH_Drawing_CanvasDrawLine(canvas: *mut OH_Drawing_Canvas, x1: f32, y1: f32, x2: f32, y2: f32);
    pub fn OH_Drawing_CanvasDrawPath(canvas: *mut OH_Drawing_Canvas, path: *const OH_Drawing_Path);
    pub fn OH_Drawing_CanvasDrawBitmap(canvas: *mut OH_Drawing_Canvas, bitmap: *const OH_Drawing_Bitmap, x: f32, y: f32);
    pub fn OH_Drawing_CanvasDrawTextBlob(canvas: *mut OH_Drawing_Canvas, blob: *const OH_Drawing_TextBlob, x: f32, y: f32);
    pub fn OH_Drawing_CanvasSave(canvas: *mut OH_Drawing_Canvas);
    pub fn OH_Drawing_CanvasRestore(canvas: *mut OH_Drawing_Canvas);
    pub fn OH_Drawing_CanvasTranslate(canvas: *mut OH_Drawing_Canvas, dx: f32, dy: f32);
    pub fn OH_Drawing_CanvasScale(canvas: *mut OH_Drawing_Canvas, sx: f32, sy: f32);
    pub fn OH_Drawing_CanvasRotate(canvas: *mut OH_Drawing_Canvas, degrees: f32, px: f32, py: f32);
    pub fn OH_Drawing_CanvasClipRect(canvas: *mut OH_Drawing_Canvas, rect: *const OH_Drawing_Rect, op: c_int, aa: bool);
    pub fn OH_Drawing_CanvasClipPath(canvas: *mut OH_Drawing_Canvas, path: *const OH_Drawing_Path, op: c_int, aa: bool);
    pub fn OH_Drawing_CanvasClear(canvas: *mut OH_Drawing_Canvas, color: u32);

    // ── Pen (stroke) ──
    pub fn OH_Drawing_PenCreate() -> *mut OH_Drawing_Pen;
    pub fn OH_Drawing_PenDestroy(pen: *mut OH_Drawing_Pen);
    pub fn OH_Drawing_PenSetColor(pen: *mut OH_Drawing_Pen, color: u32);
    pub fn OH_Drawing_PenSetWidth(pen: *mut OH_Drawing_Pen, width: f32);
    pub fn OH_Drawing_PenSetAntiAlias(pen: *mut OH_Drawing_Pen, aa: bool);
    pub fn OH_Drawing_PenSetCap(pen: *mut OH_Drawing_Pen, cap: c_int);    // 0=flat, 1=square, 2=round
    pub fn OH_Drawing_PenSetJoin(pen: *mut OH_Drawing_Pen, join: c_int);   // 0=miter, 1=round, 2=bevel

    // ── Brush (fill) ──
    pub fn OH_Drawing_BrushCreate() -> *mut OH_Drawing_Brush;
    pub fn OH_Drawing_BrushDestroy(brush: *mut OH_Drawing_Brush);
    pub fn OH_Drawing_BrushSetColor(brush: *mut OH_Drawing_Brush, color: u32);
    pub fn OH_Drawing_BrushSetAntiAlias(brush: *mut OH_Drawing_Brush, aa: bool);

    // ── Path ──
    pub fn OH_Drawing_PathCreate() -> *mut OH_Drawing_Path;
    pub fn OH_Drawing_PathDestroy(path: *mut OH_Drawing_Path);
    pub fn OH_Drawing_PathMoveTo(path: *mut OH_Drawing_Path, x: f32, y: f32);
    pub fn OH_Drawing_PathLineTo(path: *mut OH_Drawing_Path, x: f32, y: f32);
    pub fn OH_Drawing_PathQuadTo(path: *mut OH_Drawing_Path, cx: f32, cy: f32, x: f32, y: f32);
    pub fn OH_Drawing_PathCubicTo(path: *mut OH_Drawing_Path, cx1: f32, cy1: f32, cx2: f32, cy2: f32, x: f32, y: f32);
    pub fn OH_Drawing_PathClose(path: *mut OH_Drawing_Path);
    pub fn OH_Drawing_PathReset(path: *mut OH_Drawing_Path);
    pub fn OH_Drawing_PathAddRect(path: *mut OH_Drawing_Path, l: f32, t: f32, r: f32, b: f32, dir: c_int);
    pub fn OH_Drawing_PathAddCircle(path: *mut OH_Drawing_Path, cx: f32, cy: f32, radius: f32, dir: c_int);

    // ── Rect helper ──
    pub fn OH_Drawing_RectCreate(l: f32, t: f32, r: f32, b: f32) -> *mut OH_Drawing_Rect;
    pub fn OH_Drawing_RectDestroy(rect: *mut OH_Drawing_Rect);

    // ── Font + TextBlob ──
    pub fn OH_Drawing_FontCreate() -> *mut OH_Drawing_Font;
    pub fn OH_Drawing_FontDestroy(font: *mut OH_Drawing_Font);
    pub fn OH_Drawing_FontSetSize(font: *mut OH_Drawing_Font, size: f32);
    pub fn OH_Drawing_TextBlobCreateFromString(text: *const c_char, font: *const OH_Drawing_Font, encoding: c_int) -> *mut OH_Drawing_TextBlob;
    pub fn OH_Drawing_TextBlobDestroy(blob: *mut OH_Drawing_TextBlob);
}

/// OH_Drawing_BitmapFormat
#[repr(C)]
pub struct OH_Drawing_BitmapFormat {
    pub color_type: c_int,   // 0=unknown, 3=ARGB_8888, 4=ALPHA_8, ...
    pub alpha_type: c_int,   // 0=unknown, 1=opaque, 2=premul, 3=unpremul
}
```

**Effort:** Small (~30 min, just declarations)

---

### Task 6: Rust JNI Bridge Implementation (Rust — `oh_drawing.rs`)

File to create: `shim/bridge/rust/src/oh_drawing.rs`

This is the **critical bridge** — ~300 lines of Rust mapping 45 Java native methods to OH_Drawing C calls.

**Architecture per method:**

```
Java: OHBridge.canvasDrawRect(long canvas, float l, float t, float r, float b, long pen, long brush)
  → JNI → Rust: Java_com_ohos_shim_bridge_OHBridge_canvasDrawRect(env, class, canvas, l, t, r, b, pen, brush)
    → Rust casts jlong handles back to *mut OH_Drawing_* pointers
    → Calls OH_Drawing_CanvasAttachPen(canvas, pen)  // if pen != 0
    → Calls OH_Drawing_CanvasAttachBrush(canvas, brush) // if brush != 0
    → Creates OH_Drawing_Rect from l,t,r,b
    → Calls OH_Drawing_CanvasDrawRect(canvas, rect)
    → Destroys temp rect
    → Detaches pen/brush
```

**Key implementation details:**

1. **Handle storage**: Java passes `long` handles. Rust stores raw pointers as `jlong` via `ptr as jlong` / `handle as *mut T`. All handles are opaque — Java never dereferences them.

2. **OH_Drawing uses attach/detach model**: Before drawing, you attach a Pen and/or Brush to the Canvas. After drawing, detach them. This differs from the Java model where Paint is passed per-call. Our bridge handles this:
   ```rust
   // Per draw call:
   if pen != 0 { OH_Drawing_CanvasAttachPen(canvas, pen as *const _); }
   if brush != 0 { OH_Drawing_CanvasAttachBrush(canvas, brush as *const _); }
   OH_Drawing_CanvasDrawRect(canvas, rect);
   OH_Drawing_CanvasDetachPen(canvas);
   OH_Drawing_CanvasDetachBrush(canvas);
   ```

3. **Rect is a helper object**: OH_Drawing_CanvasDrawRect takes a Rect object, not 4 floats. Create/destroy per call (or cache).

4. **TextBlob for text**: `drawText` requires creating an `OH_Drawing_TextBlob` from the string + font, drawing it, then destroying it.

5. **Thread safety**: All JNI calls come from the Java thread. OH_Drawing objects are NOT thread-safe. Our Java side ensures single-thread access per Canvas (one Canvas = one Activity's render thread).

**Skeleton:**

```rust
//! oh_drawing.rs — JNI bridge for OH_Drawing (Canvas/Pen/Brush/Path/Bitmap/Font)

use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jboolean, jfloat, jint, jlong, JNI_FALSE};
use std::ffi::CString;

use crate::oh_ffi;

// ── Canvas ──────────────────────────────────────────────────────

#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasCreate(
    _env: JNIEnv, _class: JClass, bitmap_handle: jlong,
) -> jlong {
    let canvas = oh_ffi::OH_Drawing_CanvasCreate();
    if bitmap_handle != 0 {
        oh_ffi::OH_Drawing_CanvasBind(canvas, bitmap_handle as *mut oh_ffi::OH_Drawing_Bitmap);
    }
    canvas as jlong
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasDestroy(
    _env: JNIEnv, _class: JClass, canvas: jlong,
) {
    if canvas != 0 {
        oh_ffi::OH_Drawing_CanvasDestroy(canvas as *mut oh_ffi::OH_Drawing_Canvas);
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_canvasDrawRect(
    _env: JNIEnv, _class: JClass, canvas: jlong,
    l: jfloat, t: jfloat, r: jfloat, b: jfloat,
    pen: jlong, brush: jlong,
) {
    let c = canvas as *mut oh_ffi::OH_Drawing_Canvas;
    attach_pen_brush(c, pen, brush);
    let rect = oh_ffi::OH_Drawing_RectCreate(l, t, r, b);
    oh_ffi::OH_Drawing_CanvasDrawRect(c, rect);
    oh_ffi::OH_Drawing_RectDestroy(rect);
    detach_pen_brush(c);
}

// ... (same pattern for drawCircle, drawLine, drawPath, drawBitmap, drawText,
//      save, restore, translate, scale, rotate, clipRect, clipPath, drawColor)

// ── Pen ─────────────────────────────────────────────────────────

#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_penCreate(
    _env: JNIEnv, _class: JClass,
) -> jlong {
    oh_ffi::OH_Drawing_PenCreate() as jlong
}

// penDestroy, penSetColor, penSetWidth, penSetAntiAlias, penSetCap, penSetJoin
// ... (trivial 1-line wrappers)

// ── Brush ───────────────────────────────────────────────────────
// brushCreate, brushDestroy, brushSetColor — same pattern

// ── Path ────────────────────────────────────────────────────────
// pathCreate, pathDestroy, pathMoveTo, pathLineTo, pathQuadTo, pathCubicTo,
// pathClose, pathReset, pathAddRect, pathAddCircle — same pattern

// ── Bitmap ──────────────────────────────────────────────────────

#[no_mangle]
pub unsafe extern "system" fn Java_com_ohos_shim_bridge_OHBridge_bitmapCreate(
    _env: JNIEnv, _class: JClass, width: jint, height: jint, format: jint,
) -> jlong {
    let bmp = oh_ffi::OH_Drawing_BitmapCreate();
    let fmt = oh_ffi::OH_Drawing_BitmapFormat {
        color_type: if format == 0 { 3 } else { format }, // 3 = ARGB_8888
        alpha_type: 2, // premul
    };
    oh_ffi::OH_Drawing_BitmapBuild(bmp, width as u32, height as u32, &fmt);
    bmp as jlong
}

// bitmapDestroy, bitmapGetWidth, bitmapGetHeight, bitmapSetPixel, bitmapGetPixel
// setPixel/getPixel: use OH_Drawing_BitmapGetPixels() to get raw pointer, then index into it

// ── Font ────────────────────────────────────────────────────────
// fontCreate, fontDestroy, fontSetSize — same pattern

// ── Helpers ─────────────────────────────────────────────────────

unsafe fn attach_pen_brush(
    canvas: *mut oh_ffi::OH_Drawing_Canvas, pen: jlong, brush: jlong,
) {
    if pen != 0 {
        oh_ffi::OH_Drawing_CanvasAttachPen(canvas, pen as *const oh_ffi::OH_Drawing_Pen);
    }
    if brush != 0 {
        oh_ffi::OH_Drawing_CanvasAttachBrush(canvas, brush as *const oh_ffi::OH_Drawing_Brush);
    }
}

unsafe fn detach_pen_brush(canvas: *mut oh_ffi::OH_Drawing_Canvas) {
    oh_ffi::OH_Drawing_CanvasDetachPen(canvas);
    oh_ffi::OH_Drawing_CanvasDetachBrush(canvas);
}
```

**Effort:** Medium (~2-3 hours for all 45 JNI methods)

---

### Task 7: Register `oh_drawing` Module in `lib.rs`

File: `shim/bridge/rust/src/lib.rs`

Add one line:

```rust
mod oh_drawing;
```

**Effort:** Trivial

---

### Task 8: Link OH_Drawing NDK Library in `build.rs`

File: `shim/bridge/rust/build.rs`

Add:

```rust
println!("cargo:rustc-link-lib=dylib=native_drawing");  // libnative_drawing.z.so (OH_Drawing)
```

And add the include path:

```rust
format!("{}/interface/sdk_c/graphic/graphic_2d/include/drawing", oh_sdk),
```

**Effort:** Trivial

---

### Task 9: Bitmap Pixel Access (Rust — setPixel/getPixel)

OH_Drawing doesn't have per-pixel set/get. You must:
1. Call `OH_Drawing_BitmapGetPixels(bmp)` to get a raw `*mut u8` pointer to the pixel buffer
2. Index into it: `offset = (y * width + x) * 4` for ARGB_8888
3. Read/write 4 bytes (A, R, G, B)

Store width per bitmap handle (use a `HashMap<jlong, u32>`) or retrieve via `OH_Drawing_BitmapGetWidth()`.

**Effort:** Small (~30 min)

---

### Task 10: Surface Integration (Rust/C++ — for actual display output)

This connects Canvas rendering to the screen via XComponent:

1. **XComponent** provides a `NativeWindow` (Surface) when the app's ArkUI page loads
2. Our bridge:
   - Creates an OH_Drawing_Bitmap sized to the window
   - Creates an OH_Drawing_Canvas bound to that bitmap
   - Passes the canvas handle to Java's `View.draw(Canvas)`
   - After draw completes, blits the bitmap pixels to the NativeWindow buffer
3. Wiring:
   - `shim_xcomponent_on_surface_created(window, width, height)` → creates canvas + bitmap
   - `shim_xcomponent_on_frame()` → calls Java `Activity.getWindow().getDecorView().draw(canvas)`
   - `shim_xcomponent_on_surface_destroyed()` → cleanup

**Existing code:** `oh_ffi.rs` already declares `shim_xcomponent_attach_root`. The `view.rs` has event dispatch. But the Canvas↔Surface loop is NOT wired.

**Effort:** Large (~4-6 hours, requires OHOS device testing)

---

## Priority Order

| Priority | Task | Prereqs | Can do without OHOS device? |
|----------|------|---------|----------------------------|
| 1 | Task 1: Missing draw ops (arc/oval/roundrect) | None | Yes |
| 2 | Task 2: Paint Cap/Join properties | None | Yes |
| 3 | Task 4: Matrix transform support | None | Yes |
| 4 | Task 5: OH_Drawing FFI declarations | None | Yes (just code) |
| 5 | Task 6: Rust JNI bridge (`oh_drawing.rs`) | Task 5 | Yes (code only, can't compile without OHOS SDK) |
| 6 | Task 7: Register module in `lib.rs` | Task 6 | Yes |
| 7 | Task 8: Link library in `build.rs` | Task 6 | Yes |
| 8 | Task 9: Bitmap pixel access | Task 5, Task 6 | No (needs pixel buffer format from device) |
| 9 | Task 3: BitmapFactory/ImageDecoder | None | Partially (header parsing yes, full decode no) |
| 10 | Task 10: Surface integration | Task 5-9 | No (requires XComponent + NativeWindow on device) |

---

## Key Files Reference

| File | Role |
|------|------|
| `shim/java/android/graphics/Canvas.java` | Java Canvas → OHBridge routing (DONE) |
| `shim/java/android/graphics/Bitmap.java` | Java Bitmap with native handle (DONE) |
| `shim/java/android/graphics/Path.java` | Java Path with native handle (DONE) |
| `shim/java/android/graphics/Paint.java` | Pure Java data holder (DONE, extend for Cap/Join) |
| `shim/java/com/ohos/shim/bridge/OHBridge.java` | Native method declarations (DONE, extend for arc/oval) |
| `test-apps/mock/com/ohos/shim/bridge/OHBridge.java` | Mock bridge for JVM testing (DONE, extend for arc/oval) |
| `shim/bridge/rust/src/oh_ffi.rs` | C FFI declarations (add OH_Drawing) |
| `shim/bridge/rust/src/oh_drawing.rs` | **NEW** — Rust JNI → OH_Drawing bridge |
| `shim/bridge/rust/src/lib.rs` | Module registration (add `mod oh_drawing`) |
| `shim/bridge/rust/build.rs` | Link flags (add `libnative_drawing`) |
| `shim/bridge/rust/Cargo.toml` | No changes needed |

## OH_Drawing API Reference

OH NDK headers location: `interface/sdk_c/graphic/graphic_2d/include/drawing/`

Key headers:
- `drawing_canvas.h` — Canvas operations
- `drawing_pen.h` — Pen (stroke) properties
- `drawing_brush.h` — Brush (fill) properties
- `drawing_path.h` — Path construction
- `drawing_bitmap.h` — Bitmap creation and pixel access
- `drawing_font.h` — Font configuration
- `drawing_text_blob.h` — Text layout and rendering
- `drawing_rect.h` — Rect helper
- `drawing_round_rect.h` — RoundRect helper
- `drawing_types.h` — Enums and type definitions

## How OH_Drawing Differs from Android Canvas

| Concept | Android | OH_Drawing |
|---------|---------|------------|
| Stroke/Fill | `Paint.setStyle(STROKE/FILL)` then pass Paint | Attach `Pen` (stroke) and/or `Brush` (fill) to Canvas before drawing |
| Rect param | `drawRect(float, float, float, float, Paint)` | `drawRect(OH_Drawing_Rect*)` — must create/destroy Rect object |
| Text | `drawText(String, float, float, Paint)` | Create `TextBlob` from String + Font, then `drawTextBlob(blob, x, y)` |
| Color format | ARGB (0xAARRGGBB) | Same ARGB format (u32) |
| Save/Restore | Returns save count | Void — track count in Java |
| Clip | `clipRect(float,float,float,float)` | `clipRect(Rect*, ClipOp, antiAlias)` — needs Rect object + op enum |
