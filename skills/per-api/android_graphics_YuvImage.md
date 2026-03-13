# SKILL: android.graphics.YuvImage

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.YuvImage`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.YuvImage` |
| **Package** | `android.graphics` |
| **Total Methods** | 7 |
| **Avg Score** | 4.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 7 (100%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getWidth` | `int getWidth()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getHeight` | `int getHeight()` | 5 | partial | moderate | `OH_Drawing_BitmapGetHeight` | `uint32_t OH_Drawing_BitmapGetHeight(OH_Drawing_Bitmap*)` |
| `getStrides` | `int[] getStrides()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getYuvData` | 5 | partial | Return safe default (null/false/0/empty) |
| `YuvImage` | 4 | partial | throw UnsupportedOperationException |
| `getYuvFormat` | 4 | partial | Return safe default (null/false/0/empty) |
| `compressToJpeg` | 3 | composite | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.YuvImage`:


## Quality Gates

Before marking `android.graphics.YuvImage` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
