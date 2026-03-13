# SKILL: android.provider.SyncStateContract.Helpers

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.provider.SyncStateContract.Helpers`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.provider.SyncStateContract.Helpers` |
| **Package** | `android.provider.SyncStateContract` |
| **Total Methods** | 8 |
| **Avg Score** | 5.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (37%) |
| **Partial/Composite** | 4 (50%) |
| **No Mapping** | 1 (12%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `set` | `static void set(android.content.ContentProviderClient, android.net.Uri, android.accounts.Account, byte[]) throws android.os.RemoteException` | 8 | direct | easy | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `insert` | `static android.net.Uri insert(android.content.ContentProviderClient, android.net.Uri, android.accounts.Account, byte[]) throws android.os.RemoteException` | 8 | near | easy | `insertData` | `insertData(options: Options, data: UnifiedData, callback: AsyncCallback<string>): void` |
| `update` | `static void update(android.content.ContentProviderClient, android.net.Uri, byte[]) throws android.os.RemoteException` | 7 | near | easy | `update` | `update(query: AssetMap, attributesToUpdate: AssetMap): Promise<void>` |
| `newUpdateOperation` | `static android.content.ContentProviderOperation newUpdateOperation(android.net.Uri, byte[])` | 6 | partial | moderate | `updateEntries` | `updateEntries: Entry[]` |
| `getWithUri` | `static android.util.Pair<android.net.Uri,byte[]> getWithUri(android.content.ContentProviderClient, android.net.Uri, android.accounts.Account) throws android.os.RemoteException` | 6 | partial | moderate | `getPosition` | `getPosition(): number` |
| `get` | `static byte[] get(android.content.ContentProviderClient, android.net.Uri, android.accounts.Account) throws android.os.RemoteException` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `newSetOperation` | `static android.content.ContentProviderOperation newSetOperation(android.net.Uri, android.accounts.Account, byte[])` | 5 | partial | moderate | `OH_Rdb_SetVersion` | `int OH_Rdb_SetVersion(OH_Rdb_Store *store, int version)` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `Helpers` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 7 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.provider.SyncStateContract.Helpers`:


## Quality Gates

Before marking `android.provider.SyncStateContract.Helpers` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
