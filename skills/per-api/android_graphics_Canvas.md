# SKILL: android.graphics.Canvas

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Canvas`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Canvas` |
| **Package** | `android.graphics` |
| **Total Methods** | 90 |
| **Avg Score** | 4.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 16 (17%) |
| **Partial/Composite** | 68 (75%) |
| **No Mapping** | 6 (6%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 45 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `concat` | `void concat(@Nullable android.graphics.Matrix)` | 8 | direct | easy | `concat` | `concat(list: Buffer[] | Uint8Array[], totalLength?: number): Buffer` |
| `restore` | `void restore()` | 8 | direct | easy | `restore` | `restore(wallpaperType: WallpaperType, callback: AsyncCallback<void>): void` |
| `rotate` | `void rotate(float)` | 8 | direct | easy | `rotate` | `rotate(options: RotateOption): Matrix4Transit` |
| `rotate` | `final void rotate(float, float, float)` | 8 | direct | easy | `rotate` | `rotate(options: RotateOption): Matrix4Transit` |
| `save` | `int save()` | 8 | direct | easy | `save` | `save(options: ScreenshotOptions, callback: AsyncCallback<image.PixelMap>): void` |
| `scale` | `void scale(float, float)` | 8 | direct | easy | `scale` | `scale(options: ScaleOption): Matrix4Transit` |
| `scale` | `final void scale(float, float, float, float)` | 8 | direct | easy | `scale` | `scale(options: ScaleOption): Matrix4Transit` |
| `drawRoundRect` | `void drawRoundRect(@NonNull android.graphics.RectF, float, float, @NonNull android.graphics.Paint)` | 7 | near | moderate | `OH_Drawing_RoundRectCreate` | `endif


OH_Drawing_RoundRect* OH_Drawing_RoundRectCreate(const OH_Drawing_Rect*, float xRad, float yRad)` |
| `drawRoundRect` | `void drawRoundRect(float, float, float, float, float, float, @NonNull android.graphics.Paint)` | 7 | near | moderate | `OH_Drawing_RoundRectCreate` | `endif


OH_Drawing_RoundRect* OH_Drawing_RoundRectCreate(const OH_Drawing_Rect*, float xRad, float yRad)` |
| `drawBitmapMesh` | `void drawBitmapMesh(@NonNull android.graphics.Bitmap, int, int, @NonNull float[], int, @Nullable int[], int, @Nullable android.graphics.Paint)` | 6 | near | moderate | `OH_Drawing_BitmapDestroy` | `void OH_Drawing_BitmapDestroy(OH_Drawing_Bitmap*)` |
| `drawBitmap` | `void drawBitmap(@NonNull android.graphics.Bitmap, float, float, @Nullable android.graphics.Paint)` | 6 | near | moderate | `OH_Drawing_BitmapBuild` | `void OH_Drawing_BitmapBuild(OH_Drawing_Bitmap*, const uint32_t width, const uint32_t height, const OH_Drawing_BitmapFormat*)` |
| `drawBitmap` | `void drawBitmap(@NonNull android.graphics.Bitmap, @Nullable android.graphics.Rect, @NonNull android.graphics.RectF, @Nullable android.graphics.Paint)` | 6 | near | moderate | `OH_Drawing_BitmapBuild` | `void OH_Drawing_BitmapBuild(OH_Drawing_Bitmap*, const uint32_t width, const uint32_t height, const OH_Drawing_BitmapFormat*)` |
| `drawBitmap` | `void drawBitmap(@NonNull android.graphics.Bitmap, @Nullable android.graphics.Rect, @NonNull android.graphics.Rect, @Nullable android.graphics.Paint)` | 6 | near | moderate | `OH_Drawing_BitmapBuild` | `void OH_Drawing_BitmapBuild(OH_Drawing_Bitmap*, const uint32_t width, const uint32_t height, const OH_Drawing_BitmapFormat*)` |
| `drawBitmap` | `void drawBitmap(@NonNull android.graphics.Bitmap, @NonNull android.graphics.Matrix, @Nullable android.graphics.Paint)` | 6 | near | moderate | `OH_Drawing_BitmapBuild` | `void OH_Drawing_BitmapBuild(OH_Drawing_Bitmap*, const uint32_t width, const uint32_t height, const OH_Drawing_BitmapFormat*)` |
| `restoreToCount` | `void restoreToCount(int)` | 6 | near | moderate | `OH_Drawing_CanvasRestoreToCount` | `void OH_Drawing_CanvasRestoreToCount(OH_Drawing_Canvas*, uint32_t saveCount)` |
| `drawPoint` | `void drawPoint(float, float, @NonNull android.graphics.Paint)` | 6 | near | moderate | `whitePointX` | `whitePointX: number` |
| `getSaveCount` | `int getSaveCount()` | 6 | partial | moderate | `OH_Drawing_CanvasGetSaveCount` | `uint32_t OH_Drawing_CanvasGetSaveCount(OH_Drawing_Canvas*)` |
| `drawColor` | `void drawColor(@ColorInt int)` | 6 | partial | moderate | `OH_Drawing_PenGetColor` | `uint32_t OH_Drawing_PenGetColor(const OH_Drawing_Pen*)` |
| `drawColor` | `void drawColor(@ColorLong long)` | 6 | partial | moderate | `OH_Drawing_PenGetColor` | `uint32_t OH_Drawing_PenGetColor(const OH_Drawing_Pen*)` |
| `drawColor` | `void drawColor(@ColorInt int, @NonNull android.graphics.PorterDuff.Mode)` | 6 | partial | moderate | `OH_Drawing_PenGetColor` | `uint32_t OH_Drawing_PenGetColor(const OH_Drawing_Pen*)` |
| `drawColor` | `void drawColor(@ColorInt int, @NonNull android.graphics.BlendMode)` | 6 | partial | moderate | `OH_Drawing_PenGetColor` | `uint32_t OH_Drawing_PenGetColor(const OH_Drawing_Pen*)` |
| `drawColor` | `void drawColor(@ColorLong long, @NonNull android.graphics.BlendMode)` | 6 | partial | moderate | `OH_Drawing_PenGetColor` | `uint32_t OH_Drawing_PenGetColor(const OH_Drawing_Pen*)` |
| `drawDoubleRoundRect` | `void drawDoubleRoundRect(@NonNull android.graphics.RectF, float, float, @NonNull android.graphics.RectF, float, float, @NonNull android.graphics.Paint)` | 6 | partial | moderate | `OH_Drawing_RoundRectCreate` | `endif


OH_Drawing_RoundRect* OH_Drawing_RoundRectCreate(const OH_Drawing_Rect*, float xRad, float yRad)` |
| `drawDoubleRoundRect` | `void drawDoubleRoundRect(@NonNull android.graphics.RectF, @NonNull float[], @NonNull android.graphics.RectF, @NonNull float[], @NonNull android.graphics.Paint)` | 6 | partial | moderate | `OH_Drawing_RoundRectCreate` | `endif


OH_Drawing_RoundRect* OH_Drawing_RoundRectCreate(const OH_Drawing_Rect*, float xRad, float yRad)` |
| `drawPath` | `void drawPath(@NonNull android.graphics.Path, @NonNull android.graphics.Paint)` | 6 | partial | moderate | `OH_Drawing_PathArcTo` | `void OH_Drawing_PathArcTo(OH_Drawing_Path*, float x1, float y1, float x2, float y2, float startDeg, float sweepDeg)` |
| `setDrawFilter` | `void setDrawFilter(@Nullable android.graphics.DrawFilter)` | 6 | partial | moderate | `OH_Drawing_FilterCreate` | `endif


OH_Drawing_Filter* OH_Drawing_FilterCreate(void)` |
| `drawLine` | `void drawLine(float, float, float, float, @NonNull android.graphics.Paint)` | 6 | partial | moderate | `OH_Drawing_PathLineTo` | `void OH_Drawing_PathLineTo(OH_Drawing_Path*, float x, float y)` |
| `drawRect` | `void drawRect(@NonNull android.graphics.RectF, @NonNull android.graphics.Paint)` | 6 | partial | moderate | `OH_Drawing_RectCreate` | `endif


OH_Drawing_Rect* OH_Drawing_RectCreate(float left, float top, float right, float bottom)` |
| `drawRect` | `void drawRect(@NonNull android.graphics.Rect, @NonNull android.graphics.Paint)` | 6 | partial | moderate | `OH_Drawing_RectCreate` | `endif


OH_Drawing_Rect* OH_Drawing_RectCreate(float left, float top, float right, float bottom)` |
| `drawRect` | `void drawRect(float, float, float, float, @NonNull android.graphics.Paint)` | 6 | partial | moderate | `OH_Drawing_RectCreate` | `endif


OH_Drawing_Rect* OH_Drawing_RectCreate(float left, float top, float right, float bottom)` |
| `getWidth` | `int getWidth()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getMaximumBitmapHeight` | `int getMaximumBitmapHeight()` | 5 | partial | moderate | `OH_Drawing_BitmapGetHeight` | `uint32_t OH_Drawing_BitmapGetHeight(OH_Drawing_Bitmap*)` |
| `drawCircle` | `void drawCircle(float, float, float, @NonNull android.graphics.Paint)` | 5 | partial | moderate | `OH_Drawing_CanvasDrawCircle` | `void OH_Drawing_CanvasDrawCircle(OH_Drawing_Canvas*, const OH_Drawing_Point*, float radius)` |
| `drawTextOnPath` | `void drawTextOnPath(@NonNull char[], int, int, @NonNull android.graphics.Path, float, float, @NonNull android.graphics.Paint)` | 5 | partial | moderate | `OH_Drawing_PathArcTo` | `void OH_Drawing_PathArcTo(OH_Drawing_Path*, float x1, float y1, float x2, float y2, float startDeg, float sweepDeg)` |
| `drawTextOnPath` | `void drawTextOnPath(@NonNull String, @NonNull android.graphics.Path, float, float, @NonNull android.graphics.Paint)` | 5 | partial | moderate | `OH_Drawing_PathArcTo` | `void OH_Drawing_PathArcTo(OH_Drawing_Path*, float x1, float y1, float x2, float y2, float startDeg, float sweepDeg)` |
| `getMaximumBitmapWidth` | `int getMaximumBitmapWidth()` | 5 | partial | moderate | `OH_Drawing_BitmapGetWidth` | `uint32_t OH_Drawing_BitmapGetWidth(OH_Drawing_Bitmap*)` |
| `drawArc` | `void drawArc(@NonNull android.graphics.RectF, float, float, boolean, @NonNull android.graphics.Paint)` | 5 | partial | moderate | `OH_Drawing_PathArcTo` | `void OH_Drawing_PathArcTo(OH_Drawing_Path*, float x1, float y1, float x2, float y2, float startDeg, float sweepDeg)` |
| `drawArc` | `void drawArc(float, float, float, float, float, float, boolean, @NonNull android.graphics.Paint)` | 5 | partial | moderate | `OH_Drawing_PathArcTo` | `void OH_Drawing_PathArcTo(OH_Drawing_Path*, float x1, float y1, float x2, float y2, float startDeg, float sweepDeg)` |
| `clipOutPath` | `boolean clipOutPath(@NonNull android.graphics.Path)` | 5 | partial | moderate | `napi_run_script_path` | `NAPI_EXTERN napi_status napi_run_script_path(napi_env env, const char* path, napi_value* result)` |
| `drawPaint` | `void drawPaint(@NonNull android.graphics.Paint)` | 5 | partial | moderate | `OH_Drawing_TypographyPaint` | `void OH_Drawing_TypographyPaint(OH_Drawing_Typography*, OH_Drawing_Canvas*,
    double , double)` |
| `getHeight` | `int getHeight()` | 5 | partial | moderate | `OH_Drawing_BitmapGetHeight` | `uint32_t OH_Drawing_BitmapGetHeight(OH_Drawing_Bitmap*)` |
| `setMatrix` | `void setMatrix(@Nullable android.graphics.Matrix)` | 5 | partial | moderate | `OH_Drawing_MatrixSetMatrix` | `void OH_Drawing_MatrixSetMatrix(OH_Drawing_Matrix*, float scaleX, float skewX, float transX,
    float skewY, float scaleY, float transY, float persp0, float persp1, float persp2)` |
| `translate` | `void translate(float, float)` | 5 | partial | moderate | `OH_Drawing_CanvasTranslate` | `void OH_Drawing_CanvasTranslate(OH_Drawing_Canvas*, float dx, float dy)` |
| `clipPath` | `boolean clipPath(@NonNull android.graphics.Path)` | 5 | partial | moderate | `napi_run_script_path` | `NAPI_EXTERN napi_status napi_run_script_path(napi_env env, const char* path, napi_value* result)` |
| `getDensity` | `int getDensity()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |

## Stub APIs (score < 5): 45 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `drawTextRun` | 5 | partial | throw UnsupportedOperationException |
| `drawTextRun` | 5 | partial | throw UnsupportedOperationException |
| `drawTextRun` | 5 | partial | throw UnsupportedOperationException |
| `clipRect` | 5 | partial | throw UnsupportedOperationException |
| `clipRect` | 5 | partial | throw UnsupportedOperationException |
| `clipRect` | 5 | partial | throw UnsupportedOperationException |
| `clipRect` | 5 | partial | throw UnsupportedOperationException |
| `drawOval` | 5 | partial | throw UnsupportedOperationException |
| `drawOval` | 5 | partial | throw UnsupportedOperationException |
| `getClipBounds` | 5 | partial | Return safe default (null/false/0/empty) |
| `drawLines` | 5 | partial | throw UnsupportedOperationException |
| `drawLines` | 5 | partial | throw UnsupportedOperationException |
| `drawText` | 5 | partial | throw UnsupportedOperationException |
| `drawText` | 5 | partial | throw UnsupportedOperationException |
| `drawText` | 5 | partial | throw UnsupportedOperationException |
| `drawText` | 5 | partial | throw UnsupportedOperationException |
| `Canvas` | 4 | partial | Return safe default (null/false/0/empty) |
| `Canvas` | 4 | partial | Return safe default (null/false/0/empty) |
| `clipOutRect` | 4 | partial | throw UnsupportedOperationException |
| `clipOutRect` | 4 | partial | throw UnsupportedOperationException |
| `clipOutRect` | 4 | partial | throw UnsupportedOperationException |
| `clipOutRect` | 4 | partial | throw UnsupportedOperationException |
| `drawPicture` | 4 | partial | throw UnsupportedOperationException |
| `drawPicture` | 4 | partial | throw UnsupportedOperationException |
| `drawPicture` | 4 | partial | throw UnsupportedOperationException |
| `saveLayerAlpha` | 4 | partial | throw UnsupportedOperationException |
| `saveLayerAlpha` | 4 | partial | throw UnsupportedOperationException |
| `drawPoints` | 4 | partial | throw UnsupportedOperationException |
| `drawPoints` | 4 | partial | throw UnsupportedOperationException |
| `drawARGB` | 4 | partial | throw UnsupportedOperationException |
| `drawVertices` | 4 | composite | throw UnsupportedOperationException |
| `setBitmap` | 4 | composite | Log warning + no-op |
| `setDensity` | 4 | composite | Log warning + no-op |
| `drawRenderNode` | 4 | composite | throw UnsupportedOperationException |
| `drawRGB` | 4 | composite | throw UnsupportedOperationException |
| `saveLayer` | 3 | composite | throw UnsupportedOperationException |
| `saveLayer` | 3 | composite | throw UnsupportedOperationException |
| `skew` | 3 | composite | throw UnsupportedOperationException |
| `isHardwareAccelerated` | 2 | composite | Return safe default (null/false/0/empty) |
| `isOpaque` | 2 | none | Return safe default (null/false/0/empty) |
| `disableZ` | 1 | none | Return safe default (null/false/0/empty) |
| `enableZ` | 1 | none | throw UnsupportedOperationException |
| `quickReject` | 1 | none | throw UnsupportedOperationException |
| `quickReject` | 1 | none | throw UnsupportedOperationException |
| `quickReject` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 45 methods that have score >= 5
2. Stub 45 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Canvas`:

- `android.graphics.Paint` (not yet shimmed)
- `android.graphics.Bitmap` (not yet shimmed)

## Quality Gates

Before marking `android.graphics.Canvas` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 90 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 45 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
