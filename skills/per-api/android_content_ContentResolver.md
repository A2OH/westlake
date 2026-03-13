# SKILL: android.content.ContentResolver

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.ContentResolver`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.ContentResolver` |
| **Package** | `android.content` |
| **Total Methods** | 34 |
| **Avg Score** | 5.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 17 (50%) |
| **Partial/Composite** | 16 (47%) |
| **No Mapping** | 1 (2%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 27 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `delete` | `final int delete(@NonNull @RequiresPermission.Write android.net.Uri, @Nullable String, @Nullable String[])` | 9 | direct | easy | `deleteId` | `deleteId(uri: string): string` |
| `delete` | `final int delete(@NonNull @RequiresPermission.Write android.net.Uri, @Nullable android.os.Bundle)` | 9 | direct | easy | `deleteId` | `deleteId(uri: string): string` |
| `unregisterContentObserver` | `final void unregisterContentObserver(@NonNull android.database.ContentObserver)` | 8 | near | easy | `unregisterApplicationStateObserver` | `unregisterApplicationStateObserver(observerId: number, callback: AsyncCallback<void>): void` |
| `registerContentObserver` | `final void registerContentObserver(@NonNull android.net.Uri, boolean, @NonNull android.database.ContentObserver)` | 8 | near | easy | `registerApplicationStateObserver` | `registerApplicationStateObserver(observer: ApplicationStateObserver): number` |
| `cancelSync` | `static void cancelSync(android.accounts.Account, String)` | 8 | near | easy | `cancel` | `cancel(agent: WantAgent, callback: AsyncCallback<void>): void` |
| `cancelSync` | `static void cancelSync(android.content.SyncRequest)` | 8 | near | easy | `cancel` | `cancel(agent: WantAgent, callback: AsyncCallback<void>): void` |
| `update` | `final int update(@NonNull @RequiresPermission.Write android.net.Uri, @Nullable android.content.ContentValues, @Nullable String, @Nullable String[])` | 7 | near | easy | `update` | `update(query: AssetMap, attributesToUpdate: AssetMap): Promise<void>` |
| `update` | `final int update(@NonNull @RequiresPermission.Write android.net.Uri, @Nullable android.content.ContentValues, @Nullable android.os.Bundle)` | 7 | near | easy | `update` | `update(query: AssetMap, attributesToUpdate: AssetMap): Promise<void>` |
| `getCurrentSyncs` | `static java.util.List<android.content.SyncInfo> getCurrentSyncs()` | 7 | near | moderate | `getPreferencesSync` | `getPreferencesSync(context: Context, options: Options): Preferences` |
| `getSyncAutomatically` | `static boolean getSyncAutomatically(android.accounts.Account, String)` | 7 | near | moderate | `getPreferencesSync` | `getPreferencesSync(context: Context, options: Options): Preferences` |
| `releasePersistableUriPermission` | `void releasePersistableUriPermission(@NonNull android.net.Uri, int)` | 7 | near | moderate | `grantUriPermission` | `grantUriPermission(uri: string,
    flag: wantConstant.Flags,
    targetBundleName: string,
    callback: AsyncCallback<number>): void` |
| `getPeriodicSyncs` | `static java.util.List<android.content.PeriodicSync> getPeriodicSyncs(android.accounts.Account, String)` | 6 | near | moderate | `getPreferencesSync` | `getPreferencesSync(context: Context, options: Options): Preferences` |
| `requestSync` | `static void requestSync(android.accounts.Account, String, android.os.Bundle)` | 6 | near | moderate | `getRequestInfo` | `getRequestInfo(want: Want): RequestInfo` |
| `requestSync` | `static void requestSync(android.content.SyncRequest)` | 6 | near | moderate | `getRequestInfo` | `getRequestInfo(want: Want): RequestInfo` |
| `takePersistableUriPermission` | `void takePersistableUriPermission(@NonNull android.net.Uri, int)` | 6 | near | moderate | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `removePeriodicSync` | `static void removePeriodicSync(android.accounts.Account, String, android.os.Bundle)` | 6 | near | moderate | `removeStorageFromCacheSync` | `removeStorageFromCacheSync(path: string): void` |
| `bulkInsert` | `final int bulkInsert(@NonNull @RequiresPermission.Write android.net.Uri, @NonNull android.content.ContentValues[])` | 6 | near | moderate | `OH_Rdb_Insert` | `int OH_Rdb_Insert(OH_Rdb_Store *store, const char *table, OH_VBucket *valuesBucket)` |
| `getIsSyncable` | `static int getIsSyncable(android.accounts.Account, String)` | 6 | partial | moderate | `getStorageSync` | `getStorageSync(path: string): Storage` |
| `getSyncAdapterTypes` | `static android.content.SyncAdapterType[] getSyncAdapterTypes()` | 6 | partial | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `removeStatusChangeListener` | `static void removeStatusChangeListener(Object)` | 6 | partial | moderate | `registerMissionListener` | `registerMissionListener(listener: MissionListener): number` |
| `notifyChange` | `void notifyChange(@NonNull android.net.Uri, @Nullable android.database.ContentObserver)` | 5 | partial | moderate | `notifySaveAsResult` | `notifySaveAsResult(parameter: AbilityResult, requestCode: number, callback: AsyncCallback<void>): void` |
| `notifyChange` | `void notifyChange(@NonNull android.net.Uri, @Nullable android.database.ContentObserver, int)` | 5 | partial | moderate | `notifySaveAsResult` | `notifySaveAsResult(parameter: AbilityResult, requestCode: number, callback: AsyncCallback<void>): void` |
| `notifyChange` | `void notifyChange(@NonNull java.util.Collection<android.net.Uri>, @Nullable android.database.ContentObserver, int)` | 5 | partial | moderate | `notifySaveAsResult` | `notifySaveAsResult(parameter: AbilityResult, requestCode: number, callback: AsyncCallback<void>): void` |
| `isSyncPending` | `static boolean isSyncPending(android.accounts.Account, String)` | 5 | partial | moderate | `isSharedBundleRunning` | `isSharedBundleRunning(bundleName: string, versionCode: number): Promise<boolean>` |
| `addPeriodicSync` | `static void addPeriodicSync(android.accounts.Account, String, android.os.Bundle, long)` | 5 | partial | moderate | `autoSync` | `autoSync?: boolean` |
| `addStatusChangeListener` | `static Object addStatusChangeListener(int, android.content.SyncStatusObserver)` | 5 | partial | moderate | `registerMissionListener` | `registerMissionListener(listener: MissionListener): number` |
| `getMasterSyncAutomatically` | `static boolean getMasterSyncAutomatically()` | 5 | partial | moderate | `getPreferencesSync` | `getPreferencesSync(context: Context, options: Options): Preferences` |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `refresh` | 5 | partial | throw UnsupportedOperationException |
| `setMasterSyncAutomatically` | 5 | partial | Log warning + no-op |
| `setIsSyncable` | 4 | partial | Return safe default (null/false/0/empty) |
| `isSyncActive` | 4 | partial | Return safe default (null/false/0/empty) |
| `setSyncAutomatically` | 4 | partial | Log warning + no-op |
| `validateSyncExtrasBundle` | 4 | partial | throw UnsupportedOperationException |
| `ContentResolver` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 27 methods that have score >= 5
2. Stub 7 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.content.ContentResolver`:

- `android.net.Uri` (already shimmed)
- `android.content.ContentValues` (already shimmed)
- `android.database.Cursor` (already shimmed)

## Quality Gates

Before marking `android.content.ContentResolver` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 34 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 27 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
