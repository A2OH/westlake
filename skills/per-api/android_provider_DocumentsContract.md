# SKILL: android.provider.DocumentsContract

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.provider.DocumentsContract`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.provider.DocumentsContract` |
| **Package** | `android.provider` |
| **Total Methods** | 21 |
| **Avg Score** | 4.3 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 3 (14%) |
| **Partial/Composite** | 17 (80%) |
| **No Mapping** | 1 (4%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `deleteDocument` | `static boolean deleteDocument(@NonNull android.content.ContentResolver, @NonNull android.net.Uri) throws java.io.FileNotFoundException` | 7 | near | moderate | `deleteEntries` | `deleteEntries: Entry[]` |
| `getDocumentId` | `static String getDocumentId(android.net.Uri)` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `getRootId` | `static String getRootId(android.net.Uri)` | 6 | near | moderate | `getPosition` | `getPosition(): number` |
| `getTreeDocumentId` | `static String getTreeDocumentId(android.net.Uri)` | 6 | partial | moderate | `getCount` | `getCount(): number` |
| `getSearchDocumentsQuery` | `static String getSearchDocumentsQuery(android.net.Uri)` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `isRootUri` | `static boolean isRootUri(@NonNull android.content.Context, @Nullable android.net.Uri)` | 5 | partial | moderate | `uri` | `uri: string` |
| `isTreeUri` | `static boolean isTreeUri(android.net.Uri)` | 5 | partial | moderate | `uri` | `uri: string` |
| `removeDocument` | `static boolean removeDocument(@NonNull android.content.ContentResolver, @NonNull android.net.Uri, @NonNull android.net.Uri) throws java.io.FileNotFoundException` | 5 | partial | moderate | `removeStorageFromCacheSync` | `removeStorageFromCacheSync(path: string): void` |

## Stub APIs (score < 5): 13 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isRootsUri` | 5 | partial | Return safe default (null/false/0/empty) |
| `buildRootUri` | 4 | partial | throw UnsupportedOperationException |
| `buildTreeDocumentUri` | 4 | partial | throw UnsupportedOperationException |
| `isDocumentUri` | 4 | composite | Return safe default (null/false/0/empty) |
| `isChildDocument` | 4 | composite | Return safe default (null/false/0/empty) |
| `buildRootsUri` | 4 | composite | throw UnsupportedOperationException |
| `buildSearchDocumentsUri` | 4 | composite | throw UnsupportedOperationException |
| `buildChildDocumentsUri` | 3 | composite | throw UnsupportedOperationException |
| `buildDocumentUri` | 3 | composite | throw UnsupportedOperationException |
| `buildRecentDocumentsUri` | 3 | composite | throw UnsupportedOperationException |
| `buildChildDocumentsUriUsingTree` | 3 | composite | throw UnsupportedOperationException |
| `buildDocumentUriUsingTree` | 3 | composite | throw UnsupportedOperationException |
| `ejectRoot` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.provider.DocumentsContract`:


## Quality Gates

Before marking `android.provider.DocumentsContract` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 21 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
