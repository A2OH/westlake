# SKILL: android.graphics.Bitmap

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Bitmap`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Bitmap` |
| **Package** | `android.graphics` |
| **Total Methods** | 56 |
| **Avg Score** | 4.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 18 (32%) |
| **Partial/Composite** | 33 (58%) |
| **No Mapping** | 5 (8%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 34 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `copy` | `android.graphics.Bitmap copy(android.graphics.Bitmap.Config, boolean)` | 8 | direct | easy | `copy` | `copy(srcUri: string, destUri: string, options?: CopyOptions): Promise<void>` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(@NonNull android.graphics.Bitmap)` | 7 | near | moderate | `create` | `create(colorSpaceName: ColorSpace): ColorSpaceManager` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(@NonNull android.graphics.Bitmap, int, int, int, int)` | 7 | near | moderate | `create` | `create(colorSpaceName: ColorSpace): ColorSpaceManager` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(@NonNull android.graphics.Bitmap, int, int, int, int, @Nullable android.graphics.Matrix, boolean)` | 7 | near | moderate | `create` | `create(colorSpaceName: ColorSpace): ColorSpaceManager` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(int, int, @NonNull android.graphics.Bitmap.Config)` | 7 | near | moderate | `create` | `create(colorSpaceName: ColorSpace): ColorSpaceManager` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(@Nullable android.util.DisplayMetrics, int, int, @NonNull android.graphics.Bitmap.Config)` | 7 | near | moderate | `create` | `create(colorSpaceName: ColorSpace): ColorSpaceManager` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(int, int, @NonNull android.graphics.Bitmap.Config, boolean)` | 7 | near | moderate | `create` | `create(colorSpaceName: ColorSpace): ColorSpaceManager` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(int, int, @NonNull android.graphics.Bitmap.Config, boolean, @NonNull android.graphics.ColorSpace)` | 7 | near | moderate | `create` | `create(colorSpaceName: ColorSpace): ColorSpaceManager` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(@Nullable android.util.DisplayMetrics, int, int, @NonNull android.graphics.Bitmap.Config, boolean)` | 7 | near | moderate | `create` | `create(colorSpaceName: ColorSpace): ColorSpaceManager` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(@Nullable android.util.DisplayMetrics, int, int, @NonNull android.graphics.Bitmap.Config, boolean, @NonNull android.graphics.ColorSpace)` | 7 | near | moderate | `create` | `create(colorSpaceName: ColorSpace): ColorSpaceManager` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(@ColorInt @NonNull int[], int, int, int, int, @NonNull android.graphics.Bitmap.Config)` | 7 | near | moderate | `create` | `create(colorSpaceName: ColorSpace): ColorSpaceManager` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(@NonNull android.util.DisplayMetrics, @ColorInt @NonNull int[], int, int, int, int, @NonNull android.graphics.Bitmap.Config)` | 7 | near | moderate | `create` | `create(colorSpaceName: ColorSpace): ColorSpaceManager` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(@ColorInt @NonNull int[], int, int, android.graphics.Bitmap.Config)` | 7 | near | moderate | `create` | `create(colorSpaceName: ColorSpace): ColorSpaceManager` |
| `createBitmap` | `static android.graphics.Bitmap createBitmap(@Nullable android.util.DisplayMetrics, @ColorInt @NonNull int[], int, int, @NonNull android.graphics.Bitmap.Config)` | 7 | near | moderate | `create` | `create(colorSpaceName: ColorSpace): ColorSpaceManager` |
| `setColorSpace` | `void setColorSpace(@NonNull android.graphics.ColorSpace)` | 6 | near | moderate | `OH_NativeBuffer_SetColorSpace` | `int32_t OH_NativeBuffer_SetColorSpace(OH_NativeBuffer *buffer, OH_NativeBuffer_ColorSpace colorSpace)` |
| `getScaledWidth` | `int getScaledWidth(android.graphics.Canvas)` | 6 | near | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |
| `getScaledWidth` | `int getScaledWidth(android.util.DisplayMetrics)` | 6 | near | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |
| `getScaledWidth` | `int getScaledWidth(int)` | 6 | near | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |
| `eraseColor` | `void eraseColor(@ColorInt int)` | 6 | partial | moderate | `OH_Drawing_PenSetColor` | `void OH_Drawing_PenSetColor(OH_Drawing_Pen*, uint32_t color)` |
| `eraseColor` | `void eraseColor(@ColorLong long)` | 6 | partial | moderate | `OH_Drawing_PenSetColor` | `void OH_Drawing_PenSetColor(OH_Drawing_Pen*, uint32_t color)` |
| `getWidth` | `int getWidth()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `copyPixelsFromBuffer` | `void copyPixelsFromBuffer(java.nio.Buffer)` | 5 | partial | moderate | `createFromBuilder` | `createFromBuilder(builder: CustomBuilder, callback: AsyncCallback<image.PixelMap>): void` |
| `hasAlpha` | `boolean hasAlpha()` | 5 | partial | moderate | `OH_Drawing_PenSetAlpha` | `void OH_Drawing_PenSetAlpha(OH_Drawing_Pen*, uint8_t alpha)` |
| `setWidth` | `void setWidth(int)` | 5 | partial | moderate | `OH_Drawing_PenSetWidth` | `void OH_Drawing_PenSetWidth(OH_Drawing_Pen*, float width)` |
| `getConfig` | `android.graphics.Bitmap.Config getConfig()` | 5 | partial | moderate | `OH_NativeBuffer_GetConfig` | `void OH_NativeBuffer_GetConfig(OH_NativeBuffer *buffer, OH_NativeBuffer_Config* config)` |
| `getPixels` | `void getPixels(@ColorInt int[], int, int, int, int, int, int)` | 5 | partial | moderate | `getDragPreview` | `getDragPreview(): DragPreview` |
| `getScaledHeight` | `int getScaledHeight(android.graphics.Canvas)` | 5 | partial | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |
| `getScaledHeight` | `int getScaledHeight(android.util.DisplayMetrics)` | 5 | partial | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |
| `getScaledHeight` | `int getScaledHeight(int)` | 5 | partial | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |
| `getHeight` | `int getHeight()` | 5 | partial | moderate | `OH_Drawing_BitmapGetHeight` | `uint32_t OH_Drawing_BitmapGetHeight(OH_Drawing_Bitmap*)` |
| `copyPixelsToBuffer` | `void copyPixelsToBuffer(java.nio.Buffer)` | 5 | partial | moderate | `OH_Drawing_RegisterFontBuffer` | `uint32_t OH_Drawing_RegisterFontBuffer(OH_Drawing_FontCollection*, const char* fontFamily, uint8_t* fontBuffer,
    size_t length)` |
| `createScaledBitmap` | `static android.graphics.Bitmap createScaledBitmap(@NonNull android.graphics.Bitmap, int, int, boolean)` | 5 | partial | moderate | `create` | `create(colorSpaceName: ColorSpace): ColorSpaceManager` |
| `getDensity` | `int getDensity()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getGenerationId` | `int getGenerationId()` | 5 | partial | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |

## Stub APIs (score < 5): 22 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setHasAlpha` | 5 | partial | Return safe default (null/false/0/empty) |
| `setConfig` | 5 | partial | Log warning + no-op |
| `setHeight` | 5 | partial | Log warning + no-op |
| `setPixels` | 5 | partial | Log warning + no-op |
| `getRowBytes` | 4 | partial | Return safe default (null/false/0/empty) |
| `getByteCount` | 4 | partial | Return safe default (null/false/0/empty) |
| `getAllocationByteCount` | 4 | partial | Return safe default (null/false/0/empty) |
| `hasMipMap` | 4 | partial | Return safe default (null/false/0/empty) |
| `getNinePatchChunk` | 4 | partial | Return safe default (null/false/0/empty) |
| `setPremultiplied` | 4 | composite | Log warning + no-op |
| `setDensity` | 4 | composite | Log warning + no-op |
| `writeToParcel` | 4 | composite | Log warning + no-op |
| `setPixel` | 3 | composite | Log warning + no-op |
| `setHasMipMap` | 3 | composite | Return safe default (null/false/0/empty) |
| `prepareToDraw` | 3 | composite | throw UnsupportedOperationException |
| `isPremultiplied` | 3 | composite | Return safe default (null/false/0/empty) |
| `isMutable` | 3 | composite | Return safe default (null/false/0/empty) |
| `isRecycled` | 2 | none | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |
| `reconfigure` | 1 | none | Store callback, never fire |
| `recycle` | 1 | none | throw UnsupportedOperationException |
| `sameAs` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 34 methods that have score >= 5
2. Stub 22 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Bitmap`:


## Quality Gates

Before marking `android.graphics.Bitmap` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 56 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 34 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
