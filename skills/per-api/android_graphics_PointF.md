# SKILL: android.graphics.PointF

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.PointF`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.PointF` |
| **Package** | `android.graphics` |
| **Total Methods** | 14 |
| **Avg Score** | 5.4 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 5 (35%) |
| **Partial/Composite** | 6 (42%) |
| **No Mapping** | 3 (21%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 10 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `length` | `final float length()` | 8 | direct | easy | `length` | `length?: number` |
| `length` | `static float length(float, float)` | 8 | direct | easy | `length` | `length?: number` |
| `offset` | `final void offset(float, float)` | 8 | direct | easy | `offset` | `offset: number): boolean` |
| `set` | `final void set(float, float)` | 8 | direct | easy | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `set` | `final void set(@NonNull android.graphics.PointF)` | 8 | direct | easy | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `PointF` | `PointF()` | 6 | partial | moderate | `whitePointX` | `whitePointX: number` |
| `PointF` | `PointF(float, float)` | 6 | partial | moderate | `whitePointX` | `whitePointX: number` |
| `PointF` | `PointF(@NonNull android.graphics.Point)` | 6 | partial | moderate | `whitePointX` | `whitePointX: number` |
| `PointF` | `PointF(@NonNull android.graphics.PointF)` | 6 | partial | moderate | `whitePointX` | `whitePointX: number` |
| `readFromParcel` | `void readFromParcel(@NonNull android.os.Parcel)` | 5 | partial | moderate | `createFromBuilder` | `createFromBuilder(builder: CustomBuilder, callback: AsyncCallback<image.PixelMap>): void` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 4 | composite | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |
| `equals` | 1 | none | throw UnsupportedOperationException |
| `negate` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 10 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.PointF`:


## Quality Gates

Before marking `android.graphics.PointF` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 10 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
