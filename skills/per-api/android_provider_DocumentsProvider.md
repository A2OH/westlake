# SKILL: android.provider.DocumentsProvider

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.provider.DocumentsProvider`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.provider.DocumentsProvider` |
| **Package** | `android.provider` |
| **Total Methods** | 35 |
| **Avg Score** | 4.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 12 (34%) |
| **Partial/Composite** | 13 (37%) |
| **No Mapping** | 10 (28%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 21 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `delete` | `final int delete(android.net.Uri, String, String[])` | 8 | near | easy | `deleteData` | `deleteData(options: Options, callback: AsyncCallback<Array<UnifiedData>>): void` |
| `insert` | `final android.net.Uri insert(android.net.Uri, android.content.ContentValues)` | 8 | near | easy | `insertData` | `insertData(options: Options, data: UnifiedData, callback: AsyncCallback<string>): void` |
| `getType` | `final String getType(android.net.Uri)` | 7 | near | easy | `type` | `type: ValueType` |
| `query` | `final android.database.Cursor query(android.net.Uri, String[], String, String[], String)` | 7 | near | easy | `query` | `query(faultType: FaultType, callback: AsyncCallback<Array<FaultLogInfo>>): void` |
| `query` | `final android.database.Cursor query(android.net.Uri, String[], android.os.Bundle, android.os.CancellationSignal)` | 7 | near | easy | `query` | `query(faultType: FaultType, callback: AsyncCallback<Array<FaultLogInfo>>): void` |
| `update` | `final int update(android.net.Uri, android.content.ContentValues, String, String[])` | 7 | near | easy | `update` | `update(query: AssetMap, attributesToUpdate: AssetMap): Promise<void>` |
| `createDocument` | `String createDocument(String, String, String) throws java.io.FileNotFoundException` | 7 | near | moderate | `createTime` | `createTime: string` |
| `deleteDocument` | `void deleteDocument(String) throws java.io.FileNotFoundException` | 7 | near | moderate | `deleteEntries` | `deleteEntries: Entry[]` |
| `getDocumentType` | `String getDocumentType(String) throws java.io.FileNotFoundException` | 7 | near | moderate | `getTypeDescriptor` | `getTypeDescriptor(typeId: string): TypeDescriptor` |
| `moveDocument` | `String moveDocument(String, String, String) throws java.io.FileNotFoundException` | 6 | near | moderate | `moveToNext` | `moveToNext(): boolean` |
| `queryDocument` | `abstract android.database.Cursor queryDocument(String, String[]) throws java.io.FileNotFoundException` | 6 | near | moderate | `queryData` | `queryData(options: Options, callback: AsyncCallback<Array<UnifiedData>>): void` |
| `queryRoots` | `abstract android.database.Cursor queryRoots(String[]) throws java.io.FileNotFoundException` | 6 | near | moderate | `queryData` | `queryData(options: Options, callback: AsyncCallback<Array<UnifiedData>>): void` |
| `querySearchDocuments` | `android.database.Cursor querySearchDocuments(String, String, String[]) throws java.io.FileNotFoundException` | 6 | partial | moderate | `queryParticipants` | `queryParticipants(sharingResource: string, callback: AsyncCallback<Result<Array<Participant>>>): void` |
| `queryChildDocuments` | `abstract android.database.Cursor queryChildDocuments(String, String[], String) throws java.io.FileNotFoundException` | 6 | partial | moderate | `queryParticipants` | `queryParticipants(sharingResource: string, callback: AsyncCallback<Result<Array<Participant>>>): void` |
| `queryChildDocuments` | `android.database.Cursor queryChildDocuments(String, @Nullable String[], @Nullable android.os.Bundle) throws java.io.FileNotFoundException` | 6 | partial | moderate | `queryParticipants` | `queryParticipants(sharingResource: string, callback: AsyncCallback<Result<Array<Participant>>>): void` |
| `queryRecentDocuments` | `android.database.Cursor queryRecentDocuments(String, String[]) throws java.io.FileNotFoundException` | 5 | partial | moderate | `queryParticipants` | `queryParticipants(sharingResource: string, callback: AsyncCallback<Result<Array<Participant>>>): void` |
| `getDocumentStreamTypes` | `String[] getDocumentStreamTypes(String, String)` | 5 | partial | moderate | `getEntry` | `getEntry(): Entry` |
| `createWebLinkIntent` | `android.content.IntentSender createWebLinkIntent(String, @Nullable android.os.Bundle) throws java.io.FileNotFoundException` | 5 | partial | moderate | `createRdbPredicates` | `createRdbPredicates(name: string, dataAbilityPredicates: DataAbilityPredicates): rdb.RdbPredicates` |
| `openTypedAssetFile` | `final android.content.res.AssetFileDescriptor openTypedAssetFile(android.net.Uri, String, android.os.Bundle) throws java.io.FileNotFoundException` | 5 | partial | moderate | `createAssetLoaderStub` | `createAssetLoaderStub(instance: AssetLoader): Promise<rpc.RemoteObject>` |
| `openTypedAssetFile` | `final android.content.res.AssetFileDescriptor openTypedAssetFile(android.net.Uri, String, android.os.Bundle, android.os.CancellationSignal) throws java.io.FileNotFoundException` | 5 | partial | moderate | `createAssetLoaderStub` | `createAssetLoaderStub(instance: AssetLoader): Promise<rpc.RemoteObject>` |
| `removeDocument` | `void removeDocument(String, String) throws java.io.FileNotFoundException` | 5 | partial | moderate | `removeStorageFromCacheSync` | `removeStorageFromCacheSync(path: string): void` |

## Stub APIs (score < 5): 14 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `openAssetFile` | 5 | partial | Return dummy instance / no-op |
| `openAssetFile` | 5 | partial | Return dummy instance / no-op |
| `findDocumentPath` | 4 | partial | Return safe default (null/false/0/empty) |
| `isChildDocument` | 4 | composite | Return safe default (null/false/0/empty) |
| `DocumentsProvider` | 1 | none | throw UnsupportedOperationException |
| `copyDocument` | 1 | none | throw UnsupportedOperationException |
| `ejectRoot` | 1 | none | throw UnsupportedOperationException |
| `openDocument` | 1 | none | Return dummy instance / no-op |
| `openDocumentThumbnail` | 1 | none | Return dummy instance / no-op |
| `openFile` | 1 | none | Return dummy instance / no-op |
| `openFile` | 1 | none | Return dummy instance / no-op |
| `openTypedDocument` | 1 | none | Return dummy instance / no-op |
| `renameDocument` | 1 | none | throw UnsupportedOperationException |
| `revokeDocumentPermission` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 21 methods that have score >= 5
2. Stub 14 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.provider.DocumentsProvider`:


## Quality Gates

Before marking `android.provider.DocumentsProvider` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 35 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 21 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
