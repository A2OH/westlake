# SKILL: android.app.RemoteAction

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.RemoteAction`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.RemoteAction` |
| **Package** | `android.app` |
| **Total Methods** | 9 |
| **Avg Score** | 2.7 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 1 (11%) |
| **Partial/Composite** | 3 (33%) |
| **No Mapping** | 5 (55%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isEnabled` | `boolean isEnabled()` | 8 | direct | easy | `isEnabled` | `readonly isEnabled?: boolean` |

## Stub APIs (score < 5): 8 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setEnabled` | 4 | partial | Log warning + no-op |
| `setShouldShowIcon` | 4 | composite | Log warning + no-op |
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `RemoteAction` | 1 | none | Store callback, never fire |
| `clone` | 1 | none | Store callback, never fire |
| `describeContents` | 1 | none | Store callback, never fire |
| `dump` | 1 | none | throw UnsupportedOperationException |
| `shouldShowIcon` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.app.RemoteAction`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.RemoteAction` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
