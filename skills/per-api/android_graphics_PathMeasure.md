# SKILL: android.graphics.PathMeasure

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.PathMeasure`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.PathMeasure` |
| **Package** | `android.graphics` |
| **Total Methods** | 9 |
| **Avg Score** | 4.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (11%) |
| **Partial/Composite** | 7 (77%) |
| **No Mapping** | 1 (11%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isClosed` | `boolean isClosed()` | 8 | direct | easy | `isClosed` | `isClosed: boolean` |
| `getMatrix` | `boolean getMatrix(float, android.graphics.Matrix, int)` | 5 | partial | moderate | `getDragPreview` | `getDragPreview(): DragPreview` |
| `getLength` | `float getLength()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getPosTan` | `boolean getPosTan(float, float[], float[])` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getSegment` | `boolean getSegment(float, float, android.graphics.Path, boolean)` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setPath` | 4 | partial | Log warning + no-op |
| `PathMeasure` | 4 | partial | throw UnsupportedOperationException |
| `PathMeasure` | 4 | partial | throw UnsupportedOperationException |
| `nextContour` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.PathMeasure`:


## Quality Gates

Before marking `android.graphics.PathMeasure` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
