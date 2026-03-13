# SKILL: android.graphics.Point

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Point`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Point` |
| **Package** | `android.graphics` |
| **Total Methods** | 10 |
| **Avg Score** | 4.7 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 5 (50%) |
| **Partial/Composite** | 2 (20%) |
| **No Mapping** | 3 (30%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `offset` | `final void offset(int, int)` | 8 | direct | easy | `offset` | `offset: number): boolean` |
| `set` | `void set(int, int)` | 8 | direct | easy | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `Point` | `Point()` | 6 | near | moderate | `whitePointX` | `whitePointX: number` |
| `Point` | `Point(int, int)` | 6 | near | moderate | `whitePointX` | `whitePointX: number` |
| `Point` | `Point(@NonNull android.graphics.Point)` | 6 | near | moderate | `whitePointX` | `whitePointX: number` |
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

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Point`:


## Quality Gates

Before marking `android.graphics.Point` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
