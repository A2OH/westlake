# SKILL: android.graphics.NinePatch

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.NinePatch`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.NinePatch` |
| **Package** | `android.graphics` |
| **Total Methods** | 15 |
| **Avg Score** | 4.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (6%) |
| **Partial/Composite** | 12 (80%) |
| **No Mapping** | 2 (13%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getName` | `String getName()` | 6 | near | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getPaint` | `android.graphics.Paint getPaint()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getWidth` | `int getWidth()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `hasAlpha` | `final boolean hasAlpha()` | 5 | partial | moderate | `OH_Drawing_PenSetAlpha` | `void OH_Drawing_PenSetAlpha(OH_Drawing_Pen*, uint8_t alpha)` |
| `getTransparentRegion` | `final android.graphics.Region getTransparentRegion(android.graphics.Rect)` | 5 | partial | moderate | `getDragPreview` | `getDragPreview(): DragPreview` |
| `getHeight` | `int getHeight()` | 5 | partial | moderate | `OH_Drawing_BitmapGetHeight` | `uint32_t OH_Drawing_BitmapGetHeight(OH_Drawing_Bitmap*)` |
| `getBitmap` | `android.graphics.Bitmap getBitmap()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getDensity` | `int getDensity()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setPaint` | 4 | partial | Log warning + no-op |
| `draw` | 3 | composite | throw UnsupportedOperationException |
| `draw` | 3 | composite | throw UnsupportedOperationException |
| `draw` | 3 | composite | throw UnsupportedOperationException |
| `isNinePatchChunk` | 2 | composite | Return safe default (null/false/0/empty) |
| `NinePatch` | 1 | none | throw UnsupportedOperationException |
| `NinePatch` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 8 methods that have score >= 5
2. Stub 7 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.NinePatch`:


## Quality Gates

Before marking `android.graphics.NinePatch` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 15 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
