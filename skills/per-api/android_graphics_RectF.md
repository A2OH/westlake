# SKILL: android.graphics.RectF

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.RectF`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.RectF` |
| **Package** | `android.graphics` |
| **Total Methods** | 33 |
| **Avg Score** | 4.0 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 10 (30%) |
| **Partial/Composite** | 10 (30%) |
| **No Mapping** | 13 (39%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 11 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `contains` | `boolean contains(float, float)` | 8 | direct | easy | `contains` | `contains(rule: bigint): boolean` |
| `contains` | `boolean contains(float, float, float, float)` | 8 | direct | easy | `contains` | `contains(rule: bigint): boolean` |
| `contains` | `boolean contains(@NonNull android.graphics.RectF)` | 8 | direct | easy | `contains` | `contains(rule: bigint): boolean` |
| `height` | `final float height()` | 8 | direct | easy | `height` | `height: number` |
| `offset` | `void offset(float, float)` | 8 | direct | easy | `offset` | `offset: number): boolean` |
| `round` | `void round(@NonNull android.graphics.Rect)` | 8 | direct | easy | `round` | `double round(double)` |
| `set` | `void set(float, float, float, float)` | 8 | direct | easy | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `set` | `void set(@NonNull android.graphics.RectF)` | 8 | direct | easy | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `set` | `void set(@NonNull android.graphics.Rect)` | 8 | direct | easy | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `width` | `final float width()` | 8 | direct | easy | `width` | `width: number` |
| `readFromParcel` | `void readFromParcel(@NonNull android.os.Parcel)` | 5 | partial | moderate | `createFromBuilder` | `createFromBuilder(builder: CustomBuilder, callback: AsyncCallback<image.PixelMap>): void` |

## Stub APIs (score < 5): 22 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setIntersect` | 5 | partial | Log warning + no-op |
| `writeToParcel` | 4 | composite | Log warning + no-op |
| `roundOut` | 4 | composite | throw UnsupportedOperationException |
| `setEmpty` | 3 | composite | Log warning + no-op |
| `RectF` | 3 | composite | throw UnsupportedOperationException |
| `RectF` | 3 | composite | throw UnsupportedOperationException |
| `RectF` | 3 | composite | throw UnsupportedOperationException |
| `RectF` | 3 | composite | throw UnsupportedOperationException |
| `offsetTo` | 3 | composite | Log warning + no-op |
| `isEmpty` | 2 | none | Return safe default (null/false/0/empty) |
| `centerX` | 1 | none | throw UnsupportedOperationException |
| `centerY` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |
| `inset` | 1 | none | Log warning + no-op |
| `intersect` | 1 | none | throw UnsupportedOperationException |
| `intersect` | 1 | none | throw UnsupportedOperationException |
| `intersects` | 1 | none | throw UnsupportedOperationException |
| `intersects` | 1 | none | throw UnsupportedOperationException |
| `sort` | 1 | none | throw UnsupportedOperationException |
| `union` | 1 | none | Store callback, never fire |
| `union` | 1 | none | Store callback, never fire |
| `union` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.RectF`:


## Quality Gates

Before marking `android.graphics.RectF` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 33 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 11 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
