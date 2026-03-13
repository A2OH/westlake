# SKILL: android.graphics.Matrix

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Matrix`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Matrix` |
| **Package** | `android.graphics` |
| **Total Methods** | 48 |
| **Avg Score** | 4.0 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 3 (6%) |
| **Partial/Composite** | 43 (89%) |
| **No Mapping** | 2 (4%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `invert` | `boolean invert(android.graphics.Matrix)` | 8 | direct | easy | `invert` | `invert(): Matrix4Transit` |
| `reset` | `void reset()` | 8 | direct | easy | `reset` | `reset(wallpaperType: WallpaperType, callback: AsyncCallback<void>): void` |
| `set` | `void set(android.graphics.Matrix)` | 8 | direct | easy | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `postTranslate` | `boolean postTranslate(float, float)` | 6 | partial | moderate | `OH_Drawing_CanvasTranslate` | `void OH_Drawing_CanvasTranslate(OH_Drawing_Canvas*, float dx, float dy)` |
| `preTranslate` | `boolean preTranslate(float, float)` | 5 | partial | moderate | `OH_Drawing_CanvasTranslate` | `void OH_Drawing_CanvasTranslate(OH_Drawing_Canvas*, float dx, float dy)` |
| `setTranslate` | `void setTranslate(float, float)` | 5 | partial | moderate | `OH_Drawing_CanvasTranslate` | `void OH_Drawing_CanvasTranslate(OH_Drawing_Canvas*, float dx, float dy)` |
| `getValues` | `void getValues(float[])` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |

## Stub APIs (score < 5): 41 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `postRotate` | 5 | partial | throw UnsupportedOperationException |
| `postRotate` | 5 | partial | throw UnsupportedOperationException |
| `postScale` | 5 | partial | throw UnsupportedOperationException |
| `postScale` | 5 | partial | throw UnsupportedOperationException |
| `setConcat` | 5 | partial | Log warning + no-op |
| `preRotate` | 4 | partial | throw UnsupportedOperationException |
| `preRotate` | 4 | partial | throw UnsupportedOperationException |
| `setRotate` | 4 | partial | Log warning + no-op |
| `setRotate` | 4 | partial | Log warning + no-op |
| `setScale` | 4 | partial | Log warning + no-op |
| `setScale` | 4 | partial | Log warning + no-op |
| `Matrix` | 4 | partial | throw UnsupportedOperationException |
| `Matrix` | 4 | partial | throw UnsupportedOperationException |
| `rectStaysRect` | 4 | partial | throw UnsupportedOperationException |
| `setRectToRect` | 4 | partial | Log warning + no-op |
| `setSkew` | 4 | partial | Log warning + no-op |
| `setSkew` | 4 | partial | Log warning + no-op |
| `postSkew` | 4 | partial | throw UnsupportedOperationException |
| `postSkew` | 4 | partial | throw UnsupportedOperationException |
| `preScale` | 4 | partial | throw UnsupportedOperationException |
| `preScale` | 4 | partial | throw UnsupportedOperationException |
| `setSinCos` | 4 | composite | Log warning + no-op |
| `setSinCos` | 4 | composite | Log warning + no-op |
| `toShortString` | 4 | composite | throw UnsupportedOperationException |
| `mapRect` | 4 | composite | throw UnsupportedOperationException |
| `mapRect` | 4 | composite | throw UnsupportedOperationException |
| `preSkew` | 4 | composite | throw UnsupportedOperationException |
| `preSkew` | 4 | composite | throw UnsupportedOperationException |
| `setPolyToPoly` | 3 | composite | Log warning + no-op |
| `setValues` | 3 | composite | Log warning + no-op |
| `mapPoints` | 3 | composite | throw UnsupportedOperationException |
| `mapPoints` | 3 | composite | throw UnsupportedOperationException |
| `mapPoints` | 3 | composite | throw UnsupportedOperationException |
| `isIdentity` | 3 | composite | Return safe default (null/false/0/empty) |
| `isAffine` | 2 | composite | Return safe default (null/false/0/empty) |
| `mapRadius` | 2 | composite | throw UnsupportedOperationException |
| `mapVectors` | 2 | composite | throw UnsupportedOperationException |
| `mapVectors` | 2 | composite | throw UnsupportedOperationException |
| `mapVectors` | 2 | composite | throw UnsupportedOperationException |
| `postConcat` | 1 | none | Store callback, never fire |
| `preConcat` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Matrix`:


## Quality Gates

Before marking `android.graphics.Matrix` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 48 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
