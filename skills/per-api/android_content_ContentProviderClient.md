# SKILL: android.content.ContentProviderClient

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.ContentProviderClient`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.ContentProviderClient` |
| **Package** | `android.content` |
| **Total Methods** | 7 |
| **Avg Score** | 7.2 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 6 (85%) |
| **Partial/Composite** | 1 (14%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `delete` | `int delete(@NonNull android.net.Uri, @Nullable String, @Nullable String[]) throws android.os.RemoteException` | 9 | direct | easy | `deleteId` | `deleteId(uri: string): string` |
| `delete` | `int delete(@NonNull android.net.Uri, @Nullable android.os.Bundle) throws android.os.RemoteException` | 9 | direct | easy | `deleteId` | `deleteId(uri: string): string` |
| `close` | `void close()` | 8 | direct | easy | `close` | `close(file: number | File): Promise<void>` |
| `update` | `int update(@NonNull android.net.Uri, @Nullable android.content.ContentValues, @Nullable String, @Nullable String[]) throws android.os.RemoteException` | 7 | near | easy | `update` | `update(query: AssetMap, attributesToUpdate: AssetMap): Promise<void>` |
| `update` | `int update(@NonNull android.net.Uri, @Nullable android.content.ContentValues, @Nullable android.os.Bundle) throws android.os.RemoteException` | 7 | near | easy | `update` | `update(query: AssetMap, attributesToUpdate: AssetMap): Promise<void>` |
| `bulkInsert` | `int bulkInsert(@NonNull android.net.Uri, @NonNull android.content.ContentValues[]) throws android.os.RemoteException` | 6 | near | moderate | `OH_Rdb_Insert` | `int OH_Rdb_Insert(OH_Rdb_Store *store, const char *table, OH_VBucket *valuesBucket)` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `refresh` | 5 | partial | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.content.ContentProviderClient`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.ContentProviderClient` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
