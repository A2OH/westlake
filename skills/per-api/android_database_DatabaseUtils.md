# SKILL: android.database.DatabaseUtils

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.database.DatabaseUtils`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.database.DatabaseUtils` |
| **Package** | `android.database` |
| **Total Methods** | 48 |
| **Avg Score** | 3.5 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 4 (8%) |
| **Partial/Composite** | 33 (68%) |
| **No Mapping** | 11 (22%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 10 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `queryNumEntries` | `static long queryNumEntries(android.database.sqlite.SQLiteDatabase, String)` | 6 | near | moderate | `insertEntries` | `insertEntries: Entry[]` |
| `queryNumEntries` | `static long queryNumEntries(android.database.sqlite.SQLiteDatabase, String, String)` | 6 | near | moderate | `insertEntries` | `insertEntries: Entry[]` |
| `queryNumEntries` | `static long queryNumEntries(android.database.sqlite.SQLiteDatabase, String, String, String[])` | 6 | near | moderate | `insertEntries` | `insertEntries: Entry[]` |
| `getCollationKey` | `static String getCollationKey(String)` | 6 | near | moderate | `getPosition` | `getPosition(): number` |
| `createDbFromSqlStatements` | `static void createDbFromSqlStatements(android.content.Context, String, int, String)` | 6 | partial | moderate | `createRdbPredicates` | `createRdbPredicates(name: string, dataAbilityPredicates: DataAbilityPredicates): rdb.RdbPredicates` |
| `getHexCollationKey` | `static String getHexCollationKey(String)` | 6 | partial | moderate | `getPosition` | `getPosition(): number` |
| `getSqlStatementType` | `static int getSqlStatementType(String)` | 5 | partial | moderate | `getEntry` | `getEntry(): Entry` |
| `blobFileDescriptorForQuery` | `static android.os.ParcelFileDescriptor blobFileDescriptorForQuery(android.database.sqlite.SQLiteDatabase, String, String[])` | 5 | partial | moderate | `getTypeDescriptor` | `getTypeDescriptor(typeId: string): TypeDescriptor` |
| `blobFileDescriptorForQuery` | `static android.os.ParcelFileDescriptor blobFileDescriptorForQuery(android.database.sqlite.SQLiteStatement, String[])` | 5 | partial | moderate | `getTypeDescriptor` | `getTypeDescriptor(typeId: string): TypeDescriptor` |
| `dumpCursorToString` | `static String dumpCursorToString(android.database.Cursor)` | 5 | partial | moderate | `moveToPosition` | `moveToPosition(position: number): boolean` |

## Stub APIs (score < 5): 38 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `readExceptionFromParcel` | 5 | partial | Return safe default (null/false/0/empty) |
| `appendValueToSql` | 5 | partial | throw UnsupportedOperationException |
| `longForQuery` | 5 | partial | Return safe default (null/false/0/empty) |
| `longForQuery` | 5 | partial | Return safe default (null/false/0/empty) |
| `cursorStringToInsertHelper` | 5 | partial | throw UnsupportedOperationException |
| `dumpCurrentRowToString` | 4 | partial | throw UnsupportedOperationException |
| `stringForQuery` | 4 | partial | Return safe default (null/false/0/empty) |
| `stringForQuery` | 4 | partial | Return safe default (null/false/0/empty) |
| `bindObjectToProgram` | 4 | partial | throw UnsupportedOperationException |
| `readExceptionWithOperationApplicationExceptionFromParcel` | 4 | composite | Return safe default (null/false/0/empty) |
| `cursorDoubleToContentValues` | 3 | composite | Store callback, never fire |
| `writeExceptionToParcel` | 3 | composite | Log warning + no-op |
| `cursorDoubleToCursorValues` | 3 | composite | throw UnsupportedOperationException |
| `readExceptionWithFileNotFoundExceptionFromParcel` | 3 | composite | Return safe default (null/false/0/empty) |
| `cursorDoubleToContentValuesIfPresent` | 3 | composite | Store callback, never fire |
| `cursorFloatToContentValuesIfPresent` | 3 | composite | Store callback, never fire |
| `cursorIntToContentValues` | 3 | composite | Store callback, never fire |
| `cursorIntToContentValues` | 3 | composite | Store callback, never fire |
| `cursorRowToContentValues` | 3 | composite | Store callback, never fire |
| `cursorLongToContentValues` | 3 | composite | Store callback, never fire |
| `cursorLongToContentValues` | 3 | composite | Store callback, never fire |
| `cursorIntToContentValuesIfPresent` | 3 | composite | Store callback, never fire |
| `cursorStringToContentValues` | 3 | composite | Store callback, never fire |
| `cursorStringToContentValues` | 3 | composite | Store callback, never fire |
| `cursorLongToContentValuesIfPresent` | 3 | composite | Store callback, never fire |
| `cursorShortToContentValuesIfPresent` | 3 | composite | Store callback, never fire |
| `cursorStringToContentValuesIfPresent` | 3 | composite | Store callback, never fire |
| `DatabaseUtils` | 1 | none | throw UnsupportedOperationException |
| `appendEscapedSQLString` | 1 | none | throw UnsupportedOperationException |
| `appendSelectionArgs` | 1 | none | Store callback, never fire |
| `concatenateWhere` | 1 | none | Store callback, never fire |
| `dumpCurrentRow` | 1 | none | throw UnsupportedOperationException |
| `dumpCurrentRow` | 1 | none | throw UnsupportedOperationException |
| `dumpCurrentRow` | 1 | none | throw UnsupportedOperationException |
| `dumpCursor` | 1 | none | throw UnsupportedOperationException |
| `dumpCursor` | 1 | none | throw UnsupportedOperationException |
| `dumpCursor` | 1 | none | throw UnsupportedOperationException |
| `sqlEscapeString` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.database.DatabaseUtils`:


## Quality Gates

Before marking `android.database.DatabaseUtils` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 48 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 10 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
