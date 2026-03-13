# SKILL: android.database.CursorWindow

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.database.CursorWindow`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.database.CursorWindow` |
| **Package** | `android.database` |
| **Total Methods** | 27 |
| **Avg Score** | 4.6 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 10 (37%) |
| **Partial/Composite** | 13 (48%) |
| **No Mapping** | 4 (14%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 13 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getStartPosition` | `int getStartPosition()` | 8 | direct | easy | `getPosition` | `getPosition(): number` |
| `clear` | `void clear()` | 8 | direct | easy | `clear` | `clear(): void` |
| `setStartPosition` | `void setStartPosition(int)` | 7 | near | easy | `getPosition` | `getPosition(): number` |
| `getString` | `String getString(int, int)` | 7 | near | easy | `getStorage` | `getStorage(path: string, callback: AsyncCallback<Storage>): void` |
| `getType` | `int getType(int, int)` | 7 | near | easy | `type` | `type: ValueType` |
| `getInt` | `int getInt(int, int)` | 7 | near | easy | `getCount` | `getCount(): number` |
| `getLong` | `long getLong(int, int)` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `getShort` | `short getShort(int, int)` | 7 | near | moderate | `getStorage` | `getStorage(path: string, callback: AsyncCallback<Storage>): void` |
| `getFloat` | `float getFloat(int, int)` | 6 | near | moderate | `getCount` | `getCount(): number` |
| `getDouble` | `double getDouble(int, int)` | 6 | near | moderate | `getRdbStore` | `getRdbStore(context: Context, config: StoreConfig, version: number, callback: AsyncCallback<RdbStore>): void` |
| `getBlob` | `byte[] getBlob(int, int)` | 6 | partial | moderate | `getRdbStore` | `getRdbStore(context: Context, config: StoreConfig, version: number, callback: AsyncCallback<RdbStore>): void` |
| `getNumRows` | `int getNumRows()` | 6 | partial | moderate | `getEntry` | `getEntry(): Entry` |
| `freeLastRow` | `void freeLastRow()` | 5 | partial | moderate | `isAfterLast` | `isAfterLast(): boolean` |

## Stub APIs (score < 5): 14 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `allocRow` | 5 | partial | throw UnsupportedOperationException |
| `newFromParcel` | 5 | partial | throw UnsupportedOperationException |
| `writeToParcel` | 4 | partial | Log warning + no-op |
| `copyStringToBuffer` | 4 | composite | throw UnsupportedOperationException |
| `putString` | 4 | composite | Log warning + no-op |
| `setNumColumns` | 3 | composite | Log warning + no-op |
| `putDouble` | 3 | composite | Log warning + no-op |
| `putBlob` | 2 | composite | Log warning + no-op |
| `putLong` | 2 | composite | Log warning + no-op |
| `putNull` | 2 | composite | Log warning + no-op |
| `CursorWindow` | 1 | none | throw UnsupportedOperationException |
| `CursorWindow` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |
| `onAllReferencesReleased` | 1 | none | No-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.database.CursorWindow`:


## Quality Gates

Before marking `android.database.CursorWindow` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 27 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 13 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
