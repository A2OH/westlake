# SKILL: android.graphics.Color

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Color`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Color` |
| **Package** | `android.graphics` |
| **Total Methods** | 21 |
| **Avg Score** | 5.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 9 (42%) |
| **Partial/Composite** | 7 (33%) |
| **No Mapping** | 5 (23%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 11 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `blue` | `float blue()` | 10 | direct | trivial | `blueX` | `blueX: number` |
| `blue` | `static float blue(@ColorLong long)` | 10 | direct | trivial | `blueX` | `blueX: number` |
| `green` | `float green()` | 10 | direct | trivial | `greenX` | `greenX: number` |
| `green` | `static float green(@ColorLong long)` | 10 | direct | trivial | `greenX` | `greenX: number` |
| `red` | `float red()` | 10 | direct | trivial | `redX` | `redX: number` |
| `red` | `static float red(@ColorLong long)` | 10 | direct | trivial | `redX` | `redX: number` |
| `alpha` | `float alpha()` | 8 | direct | easy | `alpha` | `alpha: number` |
| `alpha` | `static float alpha(@ColorLong long)` | 8 | direct | easy | `alpha` | `alpha: number` |
| `getComponent` | `float getComponent(@IntRange(from=0, to=4) int)` | 6 | near | moderate | `createComponentObserver` | `createComponentObserver(id: string): ComponentObserver` |
| `isInColorSpace` | `static boolean isInColorSpace(@ColorLong long, @NonNull android.graphics.ColorSpace)` | 6 | partial | moderate | `OH_NativeBuffer_SetColorSpace` | `int32_t OH_NativeBuffer_SetColorSpace(OH_NativeBuffer *buffer, OH_NativeBuffer_ColorSpace colorSpace)` |
| `getModel` | `android.graphics.ColorSpace.Model getModel()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |

## Stub APIs (score < 5): 10 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `Color` | 4 | composite | throw UnsupportedOperationException |
| `colorToHSV` | 4 | composite | throw UnsupportedOperationException |
| `RGBToHSV` | 3 | composite | throw UnsupportedOperationException |
| `isWideGamut` | 2 | composite | Return safe default (null/false/0/empty) |
| `isWideGamut` | 2 | composite | Return safe default (null/false/0/empty) |
| `isSrgb` | 2 | none | Return safe default (null/false/0/empty) |
| `isSrgb` | 2 | none | Return safe default (null/false/0/empty) |
| `luminance` | 1 | none | throw UnsupportedOperationException |
| `luminance` | 1 | none | throw UnsupportedOperationException |
| `luminance` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 11 methods that have score >= 5
2. Stub 10 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Color`:


## Quality Gates

Before marking `android.graphics.Color` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 21 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 11 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
