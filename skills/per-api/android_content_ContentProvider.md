# SKILL: android.content.ContentProvider

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.ContentProvider`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.ContentProvider` |
| **Package** | `android.content` |
| **Total Methods** | 20 |
| **Avg Score** | 6.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 13 (65%) |
| **Partial/Composite** | 7 (35%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 19 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `bulkInsert` | `int bulkInsert(@NonNull android.net.Uri, @NonNull android.content.ContentValues[])` | 7 | near | easy | `bundleName` | `bundleName: string` |
| `delete` | `abstract int delete(@NonNull android.net.Uri, @Nullable String, @Nullable String[])` | 7 | near | easy | `bundleName` | `bundleName: string` |
| `delete` | `int delete(@NonNull android.net.Uri, @Nullable android.os.Bundle)` | 7 | near | easy | `bundleName` | `bundleName: string` |
| `update` | `abstract int update(@NonNull android.net.Uri, @Nullable android.content.ContentValues, @Nullable String, @Nullable String[])` | 7 | near | easy | `update` | `update(query: AssetMap, attributesToUpdate: AssetMap): Promise<void>` |
| `update` | `int update(@NonNull android.net.Uri, @Nullable android.content.ContentValues, @Nullable android.os.Bundle)` | 7 | near | easy | `update` | `update(query: AssetMap, attributesToUpdate: AssetMap): Promise<void>` |
| `isTemporary` | `boolean isTemporary()` | 7 | near | moderate | `templateId` | `templateId: TemplateId` |
| `refresh` | `boolean refresh(android.net.Uri, @Nullable android.os.Bundle, @Nullable android.os.CancellationSignal)` | 7 | near | moderate | `result` | `result: number` |
| `onCreate` | `abstract boolean onCreate()` | 6 | near | moderate | `templateId` | `templateId: TemplateId` |
| `ContentProvider` | `ContentProvider()` | 6 | near | moderate | `enableSilentProxy` | `enableSilentProxy(context: Context, uri?: string): Promise<void>` |
| `attachInfo` | `void attachInfo(android.content.Context, android.content.pm.ProviderInfo)` | 6 | near | moderate | `data` | `data: string | ArrayBuffer` |
| `setPathPermissions` | `final void setPathPermissions(@Nullable android.content.pm.PathPermission[])` | 6 | near | moderate | `templateId` | `templateId: TemplateId` |
| `shutdown` | `void shutdown()` | 6 | near | moderate | `result` | `result: number` |
| `setWritePermission` | `final void setWritePermission(@Nullable String)` | 6 | near | moderate | `subscriberId` | `subscriberId: string` |
| `onLowMemory` | `void onLowMemory()` | 6 | partial | moderate | `bundleName` | `bundleName: string` |
| `setReadPermission` | `final void setReadPermission(@Nullable String)` | 6 | partial | moderate | `createDataShareHelper` | `createDataShareHelper(context: Context, uri: string, callback: AsyncCallback<DataShareHelper>): void` |
| `restoreCallingIdentity` | `final void restoreCallingIdentity(@NonNull android.content.ContentProvider.CallingIdentity)` | 6 | partial | moderate | `enableSilentProxy` | `enableSilentProxy(context: Context, uri?: string): Promise<void>` |
| `onTrimMemory` | `void onTrimMemory(int)` | 5 | partial | moderate | `enableSilentProxy` | `enableSilentProxy(context: Context, uri?: string): Promise<void>` |
| `onConfigurationChanged` | `void onConfigurationChanged(android.content.res.Configuration)` | 5 | partial | moderate | `createDataShareHelper` | `createDataShareHelper(context: Context, uri: string, callback: AsyncCallback<DataShareHelper>): void` |
| `onCallingPackageChanged` | `void onCallingPackageChanged()` | 5 | partial | moderate | `createDataShareHelper` | `createDataShareHelper(context: Context, uri: string, callback: AsyncCallback<DataShareHelper>): void` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `dump` | 5 | partial | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 19 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.content.ContentProvider`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.ContentProvider` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 20 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 19 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
