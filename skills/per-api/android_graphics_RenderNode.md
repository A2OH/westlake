# SKILL: android.graphics.RenderNode

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.RenderNode`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.RenderNode` |
| **Package** | `android.graphics` |
| **Total Methods** | 63 |
| **Avg Score** | 4.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (4%) |
| **Partial/Composite** | 56 (88%) |
| **No Mapping** | 4 (6%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 26 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getTop` | `int getTop()` | 7 | near | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getScaleY` | `float getScaleY()` | 6 | near | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |
| `getLeft` | `int getLeft()` | 6 | near | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getScaleX` | `float getScaleX()` | 6 | partial | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |
| `getAlpha` | `float getAlpha()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getRight` | `int getRight()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getWidth` | `int getWidth()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `setAlpha` | `boolean setAlpha(float)` | 5 | partial | moderate | `OH_Drawing_PenSetAlpha` | `void OH_Drawing_PenSetAlpha(OH_Drawing_Pen*, uint8_t alpha)` |
| `getUseCompositingLayer` | `boolean getUseCompositingLayer()` | 5 | partial | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |
| `getMatrix` | `void getMatrix(@NonNull android.graphics.Matrix)` | 5 | partial | moderate | `getDragPreview` | `getDragPreview(): DragPreview` |
| `getUniqueId` | `long getUniqueId()` | 5 | partial | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |
| `getTranslationX` | `float getTranslationX()` | 5 | partial | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |
| `getTranslationY` | `float getTranslationY()` | 5 | partial | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |
| `getTranslationZ` | `float getTranslationZ()` | 5 | partial | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |
| `getHeight` | `int getHeight()` | 5 | partial | moderate | `OH_Drawing_BitmapGetHeight` | `uint32_t OH_Drawing_BitmapGetHeight(OH_Drawing_Bitmap*)` |
| `setCameraDistance` | `boolean setCameraDistance(@FloatRange(from=0.0f, to=java.lang.Float.MAX_VALUE) float)` | 5 | partial | moderate | `napi_set_instance_data` | `NAPI_EXTERN napi_status napi_set_instance_data(napi_env env,
                                               void* data,
                                               napi_finalize finalize_cb,
                                               void* finalize_hint)` |
| `endRecording` | `void endRecording()` | 5 | partial | moderate | `end` | `end(scene: string): void` |
| `getBottom` | `int getBottom()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getElevation` | `float getElevation()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getPivotX` | `float getPivotX()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getPivotY` | `float getPivotY()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getRotationX` | `float getRotationX()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getRotationY` | `float getRotationY()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getRotationZ` | `float getRotationZ()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `hasIdentityMatrix` | `boolean hasIdentityMatrix()` | 5 | partial | moderate | `OH_Drawing_MatrixCreate` | `endif


OH_Drawing_Matrix* OH_Drawing_MatrixCreate(void)` |
| `setClipRect` | `boolean setClipRect(@Nullable android.graphics.Rect)` | 5 | partial | moderate | `OH_Drawing_CanvasClipRect` | `void OH_Drawing_CanvasClipRect(OH_Drawing_Canvas*, const OH_Drawing_Rect*,
    OH_Drawing_CanvasClipOp clipOp, bool doAntiAlias)` |

## Stub APIs (score < 5): 37 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setElevation` | 5 | partial | Log warning + no-op |
| `getClipToBounds` | 5 | partial | Return safe default (null/false/0/empty) |
| `getInverseMatrix` | 4 | partial | Return safe default (null/false/0/empty) |
| `setAmbientShadowColor` | 4 | partial | Log warning + no-op |
| `setSpotShadowColor` | 4 | partial | Log warning + no-op |
| `setTranslationX` | 4 | partial | Log warning + no-op |
| `setTranslationY` | 4 | partial | Log warning + no-op |
| `setTranslationZ` | 4 | partial | Log warning + no-op |
| `setScaleX` | 4 | partial | Log warning + no-op |
| `setScaleY` | 4 | partial | Log warning + no-op |
| `setPosition` | 4 | partial | Log warning + no-op |
| `setPosition` | 4 | partial | Log warning + no-op |
| `setRotationX` | 4 | partial | Log warning + no-op |
| `setRotationY` | 4 | partial | Log warning + no-op |
| `setRotationZ` | 4 | partial | Log warning + no-op |
| `setUseCompositingLayer` | 4 | partial | Log warning + no-op |
| `computeApproximateMemoryUsage` | 4 | composite | Log warning + no-op |
| `getClipToOutline` | 4 | composite | Return safe default (null/false/0/empty) |
| `setOutline` | 4 | composite | Log warning + no-op |
| `setForceDarkAllowed` | 4 | composite | Log warning + no-op |
| `setClipToOutline` | 4 | composite | Log warning + no-op |
| `offsetLeftAndRight` | 4 | composite | Log warning + no-op |
| `setProjectionReceiver` | 4 | composite | Log warning + no-op |
| `isPivotExplicitlySet` | 4 | composite | Return safe default (null/false/0/empty) |
| `offsetTopAndBottom` | 4 | composite | Log warning + no-op |
| `setProjectBackwards` | 4 | composite | Log warning + no-op |
| `setHasOverlappingRendering` | 4 | composite | Return safe default (null/false/0/empty) |
| `resetPivot` | 3 | composite | Log warning + no-op |
| `setClipToBounds` | 3 | composite | Log warning + no-op |
| `setPivotX` | 3 | composite | Log warning + no-op |
| `setPivotY` | 3 | composite | Log warning + no-op |
| `isForceDarkAllowed` | 2 | composite | Return safe default (null/false/0/empty) |
| `RenderNode` | 2 | composite | throw UnsupportedOperationException |
| `discardDisplayList` | 1 | none | Return safe default (null/false/0/empty) |
| `hasDisplayList` | 1 | none | Return safe default (null/false/0/empty) |
| `hasOverlappingRendering` | 1 | none | Return safe default (null/false/0/empty) |
| `hasShadow` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 26 methods that have score >= 5
2. Stub 37 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.RenderNode`:


## Quality Gates

Before marking `android.graphics.RenderNode` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 63 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 26 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
