# SKILL: android.content.IntentFilter

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.IntentFilter`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.IntentFilter` |
| **Package** | `android.content` |
| **Total Methods** | 53 |
| **Avg Score** | 4.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 7 (13%) |
| **Partial/Composite** | 31 (58%) |
| **No Mapping** | 15 (28%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 23 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `create` | `static android.content.IntentFilter create(String, String)` | 10 | direct | trivial | `create` | `create(context: Context, source: object): DataObject` |
| `getAction` | `final String getAction(int)` | 7 | near | easy | `getPosition` | `getPosition(): number` |
| `getDataType` | `final String getDataType(int)` | 7 | near | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `getPriority` | `final int getPriority()` | 7 | near | moderate | `getTopAbility` | `getTopAbility(): Promise<ElementName>` |
| `getCategory` | `final String getCategory(int)` | 6 | near | moderate | `getEntry` | `getEntry(): Entry` |
| `matchData` | `final int matchData(String, String, android.net.Uri)` | 6 | near | moderate | `updateData` | `updateData(options: Options, data: UnifiedData, callback: AsyncCallback<void>): void` |
| `getDataPath` | `final android.os.PatternMatcher getDataPath(int)` | 6 | near | moderate | `getWantAgent` | `getWantAgent(info: WantAgentInfo, callback: AsyncCallback<WantAgent>): void` |
| `getDataScheme` | `final String getDataScheme(int)` | 6 | partial | moderate | `createDataShareHelper` | `createDataShareHelper(context: Context, uri: string, callback: AsyncCallback<DataShareHelper>): void` |
| `hasDataPath` | `final boolean hasDataPath(String)` | 6 | partial | moderate | `OH_Data_Asset_SetPath` | `int OH_Data_Asset_SetPath(Data_Asset *asset, const char *path)` |
| `setPriority` | `final void setPriority(int)` | 6 | partial | moderate | `setRouterProxy` | `setRouterProxy(formIds: Array<string>, proxy: Callback<Want>, callback: AsyncCallback<void>): void` |
| `hasDataType` | `final boolean hasDataType(String)` | 5 | partial | moderate | `kvStoreType` | `kvStoreType?: KVStoreType` |
| `countDataSchemes` | `final int countDataSchemes()` | 5 | partial | moderate | `createDataShareHelper` | `createDataShareHelper(context: Context, uri: string, callback: AsyncCallback<DataShareHelper>): void` |
| `addDataPath` | `final void addDataPath(String, int)` | 5 | partial | moderate | `data` | `data: string | ArrayBuffer` |
| `addDataType` | `final void addDataType(String) throws android.content.IntentFilter.MalformedMimeTypeException` | 5 | partial | moderate | `data` | `data: string | ArrayBuffer` |
| `addDataScheme` | `final void addDataScheme(String)` | 5 | partial | moderate | `createDataShareHelper` | `createDataShareHelper(context: Context, uri: string, callback: AsyncCallback<DataShareHelper>): void` |
| `hasDataScheme` | `final boolean hasDataScheme(String)` | 5 | partial | moderate | `createDataShareHelper` | `createDataShareHelper(context: Context, uri: string, callback: AsyncCallback<DataShareHelper>): void` |
| `countDataTypes` | `final int countDataTypes()` | 5 | partial | moderate | `acquireDataAbilityHelper` | `acquireDataAbilityHelper(uri: string): DataAbilityHelper` |
| `getDataSchemeSpecificPart` | `final android.os.PatternMatcher getDataSchemeSpecificPart(int)` | 5 | partial | moderate | `createDataShareHelper` | `createDataShareHelper(context: Context, uri: string, callback: AsyncCallback<DataShareHelper>): void` |
| `getDataAuthority` | `final android.content.IntentFilter.AuthorityEntry getDataAuthority(int)` | 5 | partial | moderate | `getRdbStore` | `getRdbStore(context: Context, config: StoreConfig, version: number, callback: AsyncCallback<RdbStore>): void` |
| `countDataPaths` | `final int countDataPaths()` | 5 | partial | moderate | `OH_Data_Asset_SetPath` | `int OH_Data_Asset_SetPath(Data_Asset *asset, const char *path)` |
| `countDataAuthorities` | `final int countDataAuthorities()` | 5 | partial | moderate | `OH_Data_Asset_SetModifyTime` | `int OH_Data_Asset_SetModifyTime(Data_Asset *asset, int64_t modifyTime)` |
| `countActions` | `final int countActions()` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `hasDataAuthority` | `final boolean hasDataAuthority(android.net.Uri)` | 5 | partial | moderate | `OH_Data_Asset_SetUri` | `int OH_Data_Asset_SetUri(Data_Asset *asset, const char *uri)` |

## Stub APIs (score < 5): 30 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `countDataSchemeSpecificParts` | 5 | partial | throw UnsupportedOperationException |
| `addDataSchemeSpecificPart` | 5 | partial | Log warning + no-op |
| `hasDataSchemeSpecificPart` | 5 | partial | Return safe default (null/false/0/empty) |
| `matchDataAuthority` | 5 | partial | throw UnsupportedOperationException |
| `IntentFilter` | 5 | partial | throw UnsupportedOperationException |
| `IntentFilter` | 5 | partial | throw UnsupportedOperationException |
| `IntentFilter` | 5 | partial | throw UnsupportedOperationException |
| `IntentFilter` | 5 | partial | throw UnsupportedOperationException |
| `addDataAuthority` | 4 | partial | Log warning + no-op |
| `writeToParcel` | 4 | partial | Log warning + no-op |
| `countCategories` | 4 | partial | throw UnsupportedOperationException |
| `readFromXml` | 4 | partial | Return safe default (null/false/0/empty) |
| `writeToXml` | 4 | partial | Log warning + no-op |
| `hasAction` | 3 | composite | Return safe default (null/false/0/empty) |
| `hasCategory` | 3 | composite | Return safe default (null/false/0/empty) |
| `actionsIterator` | 1 | none | Store callback, never fire |
| `addAction` | 1 | none | Log warning + no-op |
| `addCategory` | 1 | none | Log warning + no-op |
| `authoritiesIterator` | 1 | none | throw UnsupportedOperationException |
| `categoriesIterator` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |
| `dump` | 1 | none | throw UnsupportedOperationException |
| `match` | 1 | none | throw UnsupportedOperationException |
| `match` | 1 | none | throw UnsupportedOperationException |
| `matchAction` | 1 | none | Store callback, never fire |
| `matchCategories` | 1 | none | throw UnsupportedOperationException |
| `pathsIterator` | 1 | none | throw UnsupportedOperationException |
| `schemeSpecificPartsIterator` | 1 | none | throw UnsupportedOperationException |
| `schemesIterator` | 1 | none | throw UnsupportedOperationException |
| `typesIterator` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 23 methods that have score >= 5
2. Stub 30 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.content.IntentFilter`:

- `android.content.Intent` (already shimmed)

## Quality Gates

Before marking `android.content.IntentFilter` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 53 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 23 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
