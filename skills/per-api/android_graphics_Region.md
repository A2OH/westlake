# SKILL: android.graphics.Region

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Region`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Region` |
| **Package** | `android.graphics` |
| **Total Methods** | 30 |
| **Avg Score** | 3.8 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 8 (26%) |
| **Partial/Composite** | 8 (26%) |
| **No Mapping** | 14 (46%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 11 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Region` | `Region()` | 8 | direct | easy | `region` | `region?: Rect` |
| `Region` | `Region(@NonNull android.graphics.Region)` | 8 | direct | easy | `region` | `region?: Rect` |
| `Region` | `Region(@NonNull android.graphics.Rect)` | 8 | direct | easy | `region` | `region?: Rect` |
| `Region` | `Region(int, int, int, int)` | 8 | direct | easy | `region` | `region?: Rect` |
| `contains` | `boolean contains(int, int)` | 8 | direct | easy | `contains` | `contains(rule: bigint): boolean` |
| `set` | `boolean set(@NonNull android.graphics.Region)` | 8 | direct | easy | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `set` | `boolean set(@NonNull android.graphics.Rect)` | 8 | direct | easy | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `set` | `boolean set(int, int, int, int)` | 8 | direct | easy | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `translate` | `void translate(int, int)` | 5 | partial | moderate | `OH_Drawing_CanvasTranslate` | `void OH_Drawing_CanvasTranslate(OH_Drawing_Canvas*, float dx, float dy)` |
| `translate` | `void translate(int, int, android.graphics.Region)` | 5 | partial | moderate | `OH_Drawing_CanvasTranslate` | `void OH_Drawing_CanvasTranslate(OH_Drawing_Canvas*, float dx, float dy)` |
| `getBounds` | `boolean getBounds(@NonNull android.graphics.Rect)` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |

## Stub APIs (score < 5): 19 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setPath` | 4 | partial | Log warning + no-op |
| `getBoundaryPath` | 4 | partial | Return safe default (null/false/0/empty) |
| `isRect` | 4 | composite | Return safe default (null/false/0/empty) |
| `writeToParcel` | 4 | composite | Log warning + no-op |
| `setEmpty` | 3 | composite | Log warning + no-op |
| `isEmpty` | 2 | none | Return safe default (null/false/0/empty) |
| `isComplex` | 2 | none | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |
| `op` | 1 | none | throw UnsupportedOperationException |
| `op` | 1 | none | throw UnsupportedOperationException |
| `op` | 1 | none | throw UnsupportedOperationException |
| `op` | 1 | none | throw UnsupportedOperationException |
| `op` | 1 | none | throw UnsupportedOperationException |
| `quickContains` | 1 | none | Store callback, never fire |
| `quickContains` | 1 | none | Store callback, never fire |
| `quickReject` | 1 | none | throw UnsupportedOperationException |
| `quickReject` | 1 | none | throw UnsupportedOperationException |
| `quickReject` | 1 | none | throw UnsupportedOperationException |
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

Check if these related classes are already shimmed before generating `android.graphics.Region`:


## Quality Gates

Before marking `android.graphics.Region` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 30 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 11 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
