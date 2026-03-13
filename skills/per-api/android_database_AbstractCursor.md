# SKILL: android.database.AbstractCursor

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.database.AbstractCursor`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.database.AbstractCursor` |
| **Package** | `android.database` |
| **Total Methods** | 39 |
| **Avg Score** | 6.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 21 (53%) |
| **Partial/Composite** | 11 (28%) |
| **No Mapping** | 7 (17%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 28 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getPosition` | `final int getPosition()` | 10 | direct | trivial | `getPosition` | `getPosition(): number` |
| `isAfterLast` | `final boolean isAfterLast()` | 10 | direct | trivial | `isAfterLast` | `isAfterLast(): boolean` |
| `isBeforeFirst` | `final boolean isBeforeFirst()` | 10 | direct | trivial | `isBeforeFirst` | `isBeforeFirst(): boolean` |
| `isFirst` | `final boolean isFirst()` | 10 | direct | trivial | `isFirst` | `isFirst(): boolean` |
| `isLast` | `final boolean isLast()` | 10 | direct | trivial | `isLast` | `isLast(): boolean` |
| `move` | `final boolean move(int)` | 10 | direct | trivial | `move` | `move(offset: number): boolean` |
| `moveToFirst` | `final boolean moveToFirst()` | 10 | direct | trivial | `moveToFirst` | `moveToFirst(): boolean` |
| `moveToLast` | `final boolean moveToLast()` | 10 | direct | trivial | `moveToLast` | `moveToLast(): boolean` |
| `moveToNext` | `final boolean moveToNext()` | 10 | direct | trivial | `moveToNext` | `moveToNext(): boolean` |
| `moveToPosition` | `final boolean moveToPosition(int)` | 10 | direct | trivial | `moveToPosition` | `moveToPosition(position: number): boolean` |
| `moveToPrevious` | `final boolean moveToPrevious()` | 10 | direct | trivial | `moveToPrevious` | `moveToPrevious(): boolean` |
| `close` | `void close()` | 8 | direct | easy | `close` | `close(file: number | File): Promise<void>` |
| `deactivate` | `void deactivate()` | 8 | direct | easy | `deactivate` | `deactivate(isUnchained: boolean, callback: AsyncCallback<void>): void` |
| `onMove` | `boolean onMove(int, int)` | 8 | direct | easy | `move` | `move(offset: number): boolean` |
| `checkPosition` | `void checkPosition()` | 8 | near | easy | `position` | `position: number): boolean` |
| `getColumnCount` | `int getColumnCount()` | 7 | near | easy | `getCount` | `getCount(): number` |
| `getType` | `int getType(int)` | 7 | near | easy | `type` | `type: ValueType` |
| `getExtras` | `android.os.Bundle getExtras()` | 7 | near | easy | `getEntry` | `getEntry(): Entry` |
| `getColumnName` | `String getColumnName(int)` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `getColumnIndex` | `int getColumnIndex(String)` | 6 | near | moderate | `getCount` | `getCount(): number` |
| `getNotificationUri` | `android.net.Uri getNotificationUri()` | 6 | near | moderate | `getPosition` | `getPosition(): number` |
| `isClosed` | `boolean isClosed()` | 6 | partial | moderate | `isLast` | `isLast(): boolean` |
| `getBlob` | `byte[] getBlob(int)` | 6 | partial | moderate | `getRdbStore` | `getRdbStore(context: Context, config: StoreConfig, version: number, callback: AsyncCallback<RdbStore>): void` |
| `getColumnIndexOrThrow` | `int getColumnIndexOrThrow(String)` | 6 | partial | moderate | `getCount` | `getCount(): number` |
| `registerDataSetObserver` | `void registerDataSetObserver(android.database.DataSetObserver)` | 5 | partial | moderate | `createDataShareHelper` | `createDataShareHelper(context: Context, uri: string, callback: AsyncCallback<DataShareHelper>): void` |
| `onChange` | `void onChange(boolean)` | 5 | partial | moderate | `changePrivilege` | `changePrivilege(sharingResource: string,
      participants: Array<Participant>,
      callback: AsyncCallback<Result<Array<Result<Participant>>>>): void` |
| `unregisterDataSetObserver` | `void unregisterDataSetObserver(android.database.DataSetObserver)` | 5 | partial | moderate | `createDataShareHelper` | `createDataShareHelper(context: Context, uri: string, callback: AsyncCallback<DataShareHelper>): void` |
| `getWindow` | `android.database.CursorWindow getWindow()` | 5 | partial | moderate | `getCount` | `getCount(): number` |

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setExtras` | 5 | partial | Log warning + no-op |
| `copyStringToBuffer` | 4 | composite | throw UnsupportedOperationException |
| `getWantsAllOnMoveCalls` | 4 | composite | Return safe default (null/false/0/empty) |
| `setNotificationUri` | 4 | composite | Log warning + no-op |
| `AbstractCursor` | 1 | none | throw UnsupportedOperationException |
| `fillWindow` | 1 | none | throw UnsupportedOperationException |
| `finalize` | 1 | none | throw UnsupportedOperationException |
| `registerContentObserver` | 1 | none | Return safe default (null/false/0/empty) |
| `requery` | 1 | none | Return safe default (null/false/0/empty) |
| `respond` | 1 | none | Store callback, never fire |
| `unregisterContentObserver` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 28 methods that have score >= 5
2. Stub 11 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.database.AbstractCursor`:

- `android.database.Cursor` (already shimmed)

## Quality Gates

Before marking `android.database.AbstractCursor` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 39 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 28 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
