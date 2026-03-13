# SKILL: android.content.Entity

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.Entity`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.Entity` |
| **Package** | `android.content` |
| **Total Methods** | 4 |
| **Avg Score** | 4.7 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 2 (50%) |
| **Partial/Composite** | 1 (25%) |
| **No Mapping** | 1 (25%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `addSubValue` | `void addSubValue(android.net.Uri, android.content.ContentValues)` | 6 | near | moderate | `value` | `value: Value` |
| `getEntityValues` | `android.content.ContentValues getEntityValues()` | 6 | near | moderate | `getEntry` | `getEntry(): Entry` |
| `getSubValues` | `java.util.ArrayList<android.content.Entity.NamedContentValues> getSubValues()` | 5 | partial | moderate | `getStorage` | `getStorage(path: string, callback: AsyncCallback<Storage>): void` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `Entity` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.content.Entity`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.Entity` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
