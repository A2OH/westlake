# SKILL: android.text.Annotation

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.text.Annotation`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.text.Annotation` |
| **Package** | `android.text` |
| **Total Methods** | 7 |
| **Avg Score** | 3.2 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 1 (14%) |
| **Partial/Composite** | 3 (42%) |
| **No Mapping** | 3 (42%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getKey` | `String getKey()` | 7 | near | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getSpanTypeId` | `int getSpanTypeId()` | 6 | partial | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |
| `getValue` | `String getValue()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 2 | composite | Log warning + no-op |
| `Annotation` | 1 | none | Store callback, never fire |
| `Annotation` | 1 | none | Store callback, never fire |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.text.Annotation`:


## Quality Gates

Before marking `android.text.Annotation` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
