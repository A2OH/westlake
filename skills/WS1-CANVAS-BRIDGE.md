# WS1: Canvas → OH_Drawing Rendering Bridge

## Mission

Wire Android's `Canvas.draw*()` calls through JNI to OpenHarmony's OH_Drawing NDK API so that real pixels are produced. This is the single most important workstream — it unlocks ALL Android UI rendering.

## Current State

- `Canvas.java` (96 lines): all draw calls are `/* no-op */`, save/restore stack works
- `Paint.java` (114 lines): pure data holder (color, style, strokeWidth, textSize), no native handle
- `Bitmap.java` (78 lines): dimensions + config only, no pixel buffer
- `Path.java` (78 lines): tracks empty/non-empty, doesn't store actual segments
- `OHBridge.java` (real): 106 native methods declared, NONE for canvas/drawing
- `OHBridge.java` (mock): no canvas methods mocked
- Test suite: 758 pass, 5 pre-existing failures

## Architecture

```
Android app calls:          Canvas.drawRect(l, t, r, b, paint)
                                    ↓
Java shim layer:            Creates/reuses native pen+brush handles from Paint
                            Calls OHBridge.canvasDrawRect(nCanvas, l, t, r, b, nPen, nBrush)
                                    ↓
OHBridge JNI (mock):        For JVM testing: record draw calls in-memory
OHBridge JNI (real):        For OHOS: calls OH_Drawing_CanvasDrawRect() from NDK
                                    ↓
OH_Drawing NDK:             Skia-backed rendering → GPU → display
```

Two rendering paths exist (see 03-ENGINE-EXECUTION-PLAN.md):
- **Path A (Canvas/Skia):** pixel-perfect fidelity — what YOU are implementing
- **Path B (ArkUI Node):** already wired (26 node types) — used for standard widgets

## Step-by-Step Implementation

### Phase 1: Mock Canvas Bridge (JVM-testable, no OHOS device needed)

#### 1.1 Add native method declarations to OHBridge.java

File: `shim/java/com/ohos/shim/bridge/OHBridge.java`

Add these method declarations (they're defined as native in real bridge, but implemented in Java in mock):

```java
// ── OH_Drawing: Canvas ──
native static long canvasCreate(long bitmapHandle);
native static void canvasDestroy(long canvas);
native static void canvasDrawRect(long canvas, float l, float t, float r, float b, long pen, long brush);
native static void canvasDrawCircle(long canvas, float cx, float cy, float r, long pen, long brush);
native static void canvasDrawLine(long canvas, float x1, float y1, float x2, float y2, long pen);
native static void canvasDrawPath(long canvas, long path, long pen, long brush);
native static void canvasDrawBitmap(long canvas, long bitmap, float x, float y);
native static void canvasDrawText(long canvas, String text, float x, float y, long font, long pen, long brush);
native static void canvasSave(long canvas);
native static void canvasRestore(long canvas);
native static void canvasTranslate(long canvas, float dx, float dy);
native static void canvasScale(long canvas, float sx, float sy);
native static void canvasRotate(long canvas, float degrees, float px, float py);
native static void canvasClipRect(long canvas, float l, float t, float r, float b);
native static void canvasClipPath(long canvas, long path);
native static void canvasDrawColor(long canvas, int argb);

// ── OH_Drawing: Pen (stroke) ──
native static long penCreate();
native static void penDestroy(long pen);
native static void penSetColor(long pen, int argb);
native static void penSetWidth(long pen, float width);
native static void penSetAntiAlias(long pen, boolean aa);
native static void penSetCap(long pen, int cap);    // 0=butt, 1=round, 2=square
native static void penSetJoin(long pen, int join);   // 0=miter, 1=round, 2=bevel

// ── OH_Drawing: Brush (fill) ──
native static long brushCreate();
native static void brushDestroy(long brush);
native static void brushSetColor(long brush, int argb);

// ── OH_Drawing: Path ──
native static long pathCreate();
native static void pathDestroy(long path);
native static void pathMoveTo(long path, float x, float y);
native static void pathLineTo(long path, float x, float y);
native static void pathQuadTo(long path, float x1, float y1, float x2, float y2);
native static void pathCubicTo(long path, float x1, float y1, float x2, float y2, float x3, float y3);
native static void pathClose(long path);
native static void pathReset(long path);
native static void pathAddRect(long path, float l, float t, float r, float b, int dir);
native static void pathAddCircle(long path, float cx, float cy, float r, int dir);

// ── OH_Drawing: Bitmap ──
native static long bitmapCreate(int width, int height, int format); // format: 0=ARGB_8888
native static void bitmapDestroy(long bitmap);
native static int bitmapGetWidth(long bitmap);
native static int bitmapGetHeight(long bitmap);
native static void bitmapSetPixel(long bitmap, int x, int y, int argb);
native static int bitmapGetPixel(long bitmap, int x, int y);

// ── OH_Drawing: Font / TextBlob ──
native static long fontCreate();
native static void fontDestroy(long font);
native static void fontSetSize(long font, float size);
```

#### 1.2 Implement mock bridge for JVM testing

File: `test-apps/mock/com/ohos/shim/bridge/OHBridge.java`

Add mock implementations that record draw calls in memory. This lets us test Canvas without OHOS:

```java
// ── Mock Canvas/Drawing ──
// Use a DrawRecord list to capture what was drawn (for assertions)

private static final AtomicLong sHandleCounter = new AtomicLong(1000);

public static class DrawRecord {
    public final String op;      // "drawRect", "drawCircle", "drawText", etc.
    public final float[] args;   // numeric args
    public final String text;    // for drawText
    public final int color;      // pen/brush color at time of draw

    public DrawRecord(String op, float[] args, String text, int color) {
        this.op = op; this.args = args; this.text = text; this.color = color;
    }
}

// Per-canvas draw log
private static final ConcurrentHashMap<Long, java.util.List<DrawRecord>> sCanvasDrawLog = new ConcurrentHashMap<>();
// Per-handle color tracking
private static final ConcurrentHashMap<Long, Integer> sHandleColors = new ConcurrentHashMap<>();
private static final ConcurrentHashMap<Long, Float> sHandleWidths = new ConcurrentHashMap<>();

// Canvas
public static long canvasCreate(long bitmapHandle) {
    long h = sHandleCounter.getAndIncrement();
    sCanvasDrawLog.put(h, java.util.Collections.synchronizedList(new java.util.ArrayList<>()));
    return h;
}
public static void canvasDestroy(long canvas) { sCanvasDrawLog.remove(canvas); }
public static void canvasDrawRect(long canvas, float l, float t, float r, float b, long pen, long brush) {
    record(canvas, "drawRect", new float[]{l, t, r, b}, null, getColor(brush != 0 ? brush : pen));
}
public static void canvasDrawCircle(long canvas, float cx, float cy, float r, long pen, long brush) {
    record(canvas, "drawCircle", new float[]{cx, cy, r}, null, getColor(brush != 0 ? brush : pen));
}
public static void canvasDrawLine(long canvas, float x1, float y1, float x2, float y2, long pen) {
    record(canvas, "drawLine", new float[]{x1, y1, x2, y2}, null, getColor(pen));
}
public static void canvasDrawText(long canvas, String text, float x, float y, long font, long pen, long brush) {
    record(canvas, "drawText", new float[]{x, y}, text, getColor(brush != 0 ? brush : pen));
}
public static void canvasDrawPath(long canvas, long path, long pen, long brush) {
    record(canvas, "drawPath", new float[]{}, null, getColor(brush != 0 ? brush : pen));
}
public static void canvasDrawBitmap(long canvas, long bitmap, float x, float y) {
    record(canvas, "drawBitmap", new float[]{x, y}, null, 0);
}
public static void canvasDrawColor(long canvas, int argb) {
    record(canvas, "drawColor", new float[]{}, null, argb);
}
public static void canvasSave(long canvas) { record(canvas, "save", new float[]{}, null, 0); }
public static void canvasRestore(long canvas) { record(canvas, "restore", new float[]{}, null, 0); }
public static void canvasTranslate(long canvas, float dx, float dy) {}
public static void canvasScale(long canvas, float sx, float sy) {}
public static void canvasRotate(long canvas, float degrees, float px, float py) {}
public static void canvasClipRect(long canvas, float l, float t, float r, float b) {}
public static void canvasClipPath(long canvas, long path) {}

// Pen
public static long penCreate() { long h = sHandleCounter.getAndIncrement(); sHandleColors.put(h, 0xFF000000); return h; }
public static void penDestroy(long pen) { sHandleColors.remove(pen); sHandleWidths.remove(pen); }
public static void penSetColor(long pen, int argb) { sHandleColors.put(pen, argb); }
public static void penSetWidth(long pen, float w) { sHandleWidths.put(pen, w); }
public static void penSetAntiAlias(long pen, boolean aa) {}
public static void penSetCap(long pen, int cap) {}
public static void penSetJoin(long pen, int join) {}

// Brush
public static long brushCreate() { long h = sHandleCounter.getAndIncrement(); sHandleColors.put(h, 0xFF000000); return h; }
public static void brushDestroy(long brush) { sHandleColors.remove(brush); }
public static void brushSetColor(long brush, int argb) { sHandleColors.put(brush, argb); }

// Path
public static long pathCreate() { return sHandleCounter.getAndIncrement(); }
public static void pathDestroy(long path) {}
public static void pathMoveTo(long path, float x, float y) {}
public static void pathLineTo(long path, float x, float y) {}
public static void pathQuadTo(long path, float x1, float y1, float x2, float y2) {}
public static void pathCubicTo(long path, float x1, float y1, float x2, float y2, float x3, float y3) {}
public static void pathClose(long path) {}
public static void pathReset(long path) {}
public static void pathAddRect(long path, float l, float t, float r, float b, int dir) {}
public static void pathAddCircle(long path, float cx, float cy, float r, int dir) {}

// Bitmap
private static final ConcurrentHashMap<Long, int[]> sBitmapDims = new ConcurrentHashMap<>();
public static long bitmapCreate(int w, int h, int fmt) {
    long bh = sHandleCounter.getAndIncrement(); sBitmapDims.put(bh, new int[]{w, h}); return bh;
}
public static void bitmapDestroy(long bmp) { sBitmapDims.remove(bmp); }
public static int bitmapGetWidth(long bmp) { int[] d = sBitmapDims.get(bmp); return d != null ? d[0] : 0; }
public static int bitmapGetHeight(long bmp) { int[] d = sBitmapDims.get(bmp); return d != null ? d[1] : 0; }
public static void bitmapSetPixel(long bmp, int x, int y, int argb) {}
public static int bitmapGetPixel(long bmp, int x, int y) { return 0; }

// Font
public static long fontCreate() { return sHandleCounter.getAndIncrement(); }
public static void fontDestroy(long font) {}
public static void fontSetSize(long font, float size) {}

// Test helpers — retrieve draw log for assertions
public static java.util.List<DrawRecord> getDrawLog(long canvasHandle) {
    java.util.List<DrawRecord> log = sCanvasDrawLog.get(canvasHandle);
    return log != null ? log : java.util.Collections.emptyList();
}
public static void clearDrawLog(long canvasHandle) {
    java.util.List<DrawRecord> log = sCanvasDrawLog.get(canvasHandle);
    if (log != null) log.clear();
}

private static void record(long canvas, String op, float[] args, String text, int color) {
    java.util.List<DrawRecord> log = sCanvasDrawLog.get(canvas);
    if (log != null) log.add(new DrawRecord(op, args, text, color));
}
private static int getColor(long handle) {
    Integer c = sHandleColors.get(handle);
    return c != null ? c : 0;
}
```

#### 1.3 Wire Canvas.java to OHBridge

File: `shim/java/android/graphics/Canvas.java`

Add native handle tracking and route draw calls through OHBridge:

```java
private long nativeCanvas;  // OH_Drawing_Canvas handle
private long nativePenCache;   // reusable pen handle
private long nativeBrushCache; // reusable brush handle

public Canvas() {
    this.bitmap = null;
    // Don't create native canvas for empty constructor (headless mode)
}

public Canvas(Bitmap bitmap) {
    if (bitmap == null) throw new NullPointerException("bitmap must not be null");
    this.bitmap = bitmap;
    if (bitmap.getNativeHandle() != 0 || OHBridge.isNativeAvailable()) {
        this.nativeCanvas = OHBridge.canvasCreate(bitmap.getNativeHandle());
    }
}

// Before each draw, sync Paint state to pen/brush
private long ensurePen(Paint paint) {
    if (nativePenCache == 0) nativePenCache = OHBridge.penCreate();
    if (paint != null) {
        OHBridge.penSetColor(nativePenCache, paint.getColor());
        OHBridge.penSetWidth(nativePenCache, paint.getStrokeWidth());
        OHBridge.penSetAntiAlias(nativePenCache, paint.isAntiAlias());
    }
    return nativePenCache;
}

private long ensureBrush(Paint paint) {
    if (nativeBrushCache == 0) nativeBrushCache = OHBridge.brushCreate();
    if (paint != null) {
        OHBridge.brushSetColor(nativeBrushCache, paint.getColor());
    }
    return nativeBrushCache;
}

// Route draw calls: if native canvas exists, call OHBridge; otherwise no-op
public void drawRect(float l, float t, float r, float b, Paint paint) {
    if (nativeCanvas != 0) {
        long pen = (paint != null && paint.getStyle() != Paint.Style.FILL) ? ensurePen(paint) : 0;
        long brush = (paint != null && paint.getStyle() != Paint.Style.STROKE) ? ensureBrush(paint) : 0;
        OHBridge.canvasDrawRect(nativeCanvas, l, t, r, b, pen, brush);
    }
}
// ... same pattern for drawCircle, drawLine, drawText, drawPath, drawBitmap, drawColor
```

#### 1.4 Wire Paint.java — add Pen/Brush handle tracking

Paint maps to TWO OH_Drawing objects: Pen (stroke) and Brush (fill). But we don't need native handles in Paint — Canvas creates and syncs them per-draw-call for simplicity. Paint stays as pure data.

**No changes needed to Paint.java** — Canvas reads Paint state and syncs to native pen/brush.

#### 1.5 Wire Bitmap.java — add native pixel buffer

```java
private long nativeHandle; // OH_Drawing_Bitmap handle

public long getNativeHandle() { return nativeHandle; }

public static Bitmap createBitmap(int width, int height, Config config) {
    Bitmap bmp = new Bitmap(width, height, config);
    if (OHBridge.isNativeAvailable()) {
        bmp.nativeHandle = OHBridge.bitmapCreate(width, height, configToFormat(config));
    }
    return bmp;
}

public void setPixel(int x, int y, int color) {
    if (nativeHandle != 0) OHBridge.bitmapSetPixel(nativeHandle, x, y, color);
}
public int getPixel(int x, int y) {
    if (nativeHandle != 0) return OHBridge.bitmapGetPixel(nativeHandle, x, y);
    return 0;
}

public void recycle() {
    if (nativeHandle != 0) { OHBridge.bitmapDestroy(nativeHandle); nativeHandle = 0; }
    recycled = true;
}
```

#### 1.6 Wire Path.java — add native path handle

```java
private long nativeHandle;

public long getNativeHandle() { return nativeHandle; }

public Path() {
    if (OHBridge.isNativeAvailable()) nativeHandle = OHBridge.pathCreate();
}

public void moveTo(float x, float y) {
    empty = false;
    if (nativeHandle != 0) OHBridge.pathMoveTo(nativeHandle, x, y);
}
public void lineTo(float x, float y) {
    empty = false;
    if (nativeHandle != 0) OHBridge.pathLineTo(nativeHandle, x, y);
}
// ... same for quadTo, cubicTo, close, reset, addRect, addCircle
```

### Phase 2: Tests

Add to `test-apps/02-headless-cli/src/HeadlessTest.java`:

```java
static void testCanvasBridge() {
    section("Canvas → OHBridge drawing");

    // Create a bitmap-backed canvas
    android.graphics.Bitmap bmp = android.graphics.Bitmap.createBitmap(100, 100,
            android.graphics.Bitmap.Config.ARGB_8888);
    android.graphics.Canvas canvas = new android.graphics.Canvas(bmp);

    check("canvas width == 100", canvas.getWidth() == 100);
    check("canvas height == 100", canvas.getHeight() == 100);

    // Draw a red rectangle
    android.graphics.Paint paint = new android.graphics.Paint();
    paint.setColor(0xFFFF0000);
    paint.setStyle(android.graphics.Paint.Style.FILL);
    canvas.drawRect(10, 10, 50, 50, paint);

    // Draw a blue circle
    paint.setColor(0xFF0000FF);
    canvas.drawCircle(75, 75, 20, paint);

    // Draw text
    paint.setColor(0xFF000000);
    paint.setTextSize(16);
    canvas.drawText("Hello", 10, 90, paint);

    // Save/restore
    int count = canvas.save();
    check("save returns count > 0", count > 0);
    canvas.translate(10, 10);
    canvas.restore();
    check("restore balances save", canvas.getSaveCount() == count - 1);

    // Verify draw log via mock bridge (only works in JVM test mode)
    // The mock OHBridge records all draw calls — verify them
    // ... (test-specific assertions using OHBridge.getDrawLog())

    // Path operations
    android.graphics.Path path = new android.graphics.Path();
    check("new path is empty", path.isEmpty());
    path.moveTo(0, 0);
    path.lineTo(100, 0);
    path.lineTo(50, 100);
    path.close();
    check("path not empty after ops", !path.isEmpty());
    canvas.drawPath(path, paint);

    // Bitmap operations
    check("bitmap not recycled", !bmp.isRecycled());
    check("bitmap byte count > 0", bmp.getByteCount() > 0);
    bmp.recycle();
    check("bitmap recycled", bmp.isRecycled());
}
```

### Phase 3: Real OHOS integration (future, not for this session)

This requires the Rust/C++ bridge (`oh_drawing.rs`) and cross-compilation. Skip for now — the mock bridge gives us full JVM testability.

## Verification

```bash
cd test-apps && ./run-local-tests.sh headless
# Must: compile cleanly, new tests pass, existing 758 tests don't regress
```

## Files to modify (summary)

| File | Action |
|------|--------|
| `shim/java/com/ohos/shim/bridge/OHBridge.java` | Add ~45 native method declarations |
| `test-apps/mock/com/ohos/shim/bridge/OHBridge.java` | Add mock implementations with draw recording |
| `shim/java/android/graphics/Canvas.java` | Add nativeCanvas handle, route draw calls to OHBridge |
| `shim/java/android/graphics/Bitmap.java` | Add nativeHandle, setPixel/getPixel, native create/destroy |
| `shim/java/android/graphics/Path.java` | Add nativeHandle, route path ops to OHBridge |
| `shim/java/android/graphics/Paint.java` | No changes (Canvas syncs Paint state per-draw) |
| `test-apps/02-headless-cli/src/HeadlessTest.java` | Add testCanvasBridge() with ~20 checks |

## Key Rules

- **OHBridge.isNativeAvailable() guard**: All native calls must check this first. When false (JVM testing), the mock bridge handles calls.
- **Handle lifecycle**: Every `*Create()` must have a matching `*Destroy()` — prevent leaks
- **Paint → Pen+Brush**: Canvas owns the pen/brush handles, syncs from Paint before each draw call
- **Don't break existing tests**: 758 pass, 5 pre-existing failures — no regressions
- **Match AOSP signatures exactly**: apps compiled against real Android SDK
