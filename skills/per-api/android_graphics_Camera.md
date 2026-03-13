# SKILL: android.graphics.Camera

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Camera`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Camera` |
| **Package** | `android.graphics` |
| **Total Methods** | 15 |
| **Avg Score** | 5.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (20%) |
| **Partial/Composite** | 11 (73%) |
| **No Mapping** | 1 (6%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `restore` | `void restore()` | 8 | direct | easy | `restore` | `restore(wallpaperType: WallpaperType, callback: AsyncCallback<void>): void` |
| `rotate` | `void rotate(float, float, float)` | 8 | direct | easy | `rotate` | `rotate(options: RotateOption): Matrix4Transit` |
| `save` | `void save()` | 8 | direct | easy | `save` | `save(options: ScreenshotOptions, callback: AsyncCallback<image.PixelMap>): void` |
| `getMatrix` | `void getMatrix(android.graphics.Matrix)` | 5 | partial | moderate | `getDragPreview` | `getDragPreview(): DragPreview` |
| `translate` | `void translate(float, float, float)` | 5 | partial | moderate | `OH_Drawing_CanvasTranslate` | `void OH_Drawing_CanvasTranslate(OH_Drawing_Canvas*, float dx, float dy)` |
| `getLocationX` | `float getLocationX()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getLocationY` | `float getLocationY()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getLocationZ` | `float getLocationZ()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setLocation` | 5 | partial | Log warning + no-op |
| `applyToCanvas` | 4 | partial | Return safe default (null/false/0/empty) |
| `rotateX` | 4 | partial | throw UnsupportedOperationException |
| `rotateY` | 4 | partial | throw UnsupportedOperationException |
| `rotateZ` | 4 | partial | throw UnsupportedOperationException |
| `dotWithNormal` | 3 | composite | throw UnsupportedOperationException |
| `Camera` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 8 methods that have score >= 5
2. Stub 7 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Camera`:


## Quality Gates

Before marking `android.graphics.Camera` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 15 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
