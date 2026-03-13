# SKILL: android.database.MatrixCursor

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.database.MatrixCursor`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.database.MatrixCursor` |
| **Package** | `android.database` |
| **Total Methods** | 14 |
| **Avg Score** | 4.7 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 8 (57%) |
| **Partial/Composite** | 1 (7%) |
| **No Mapping** | 5 (35%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 9 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getCount` | `int getCount()` | 10 | direct | trivial | `getCount` | `getCount(): number` |
| `getString` | `String getString(int)` | 7 | near | easy | `getStorage` | `getStorage(path: string, callback: AsyncCallback<Storage>): void` |
| `getInt` | `int getInt(int)` | 7 | near | easy | `getCount` | `getCount(): number` |
| `getLong` | `long getLong(int)` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `getShort` | `short getShort(int)` | 7 | near | moderate | `getStorage` | `getStorage(path: string, callback: AsyncCallback<Storage>): void` |
| `getColumnNames` | `String[] getColumnNames()` | 6 | near | moderate | `getCount` | `getCount(): number` |
| `getFloat` | `float getFloat(int)` | 6 | near | moderate | `getCount` | `getCount(): number` |
| `getDouble` | `double getDouble(int)` | 6 | near | moderate | `getRdbStore` | `getRdbStore(context: Context, config: StoreConfig, version: number, callback: AsyncCallback<RdbStore>): void` |
| `isNull` | `boolean isNull(int)` | 5 | partial | moderate | `isFirst` | `isFirst(): boolean` |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `MatrixCursor` | 1 | none | throw UnsupportedOperationException |
| `MatrixCursor` | 1 | none | throw UnsupportedOperationException |
| `addRow` | 1 | none | Log warning + no-op |
| `addRow` | 1 | none | Log warning + no-op |
| `newRow` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.database.MatrixCursor`:


## Quality Gates

Before marking `android.database.MatrixCursor` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 9 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
