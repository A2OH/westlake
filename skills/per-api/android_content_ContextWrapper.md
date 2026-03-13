# SKILL: android.content.ContextWrapper

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.ContextWrapper`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.ContextWrapper` |
| **Package** | `android.content` |
| **Total Methods** | 94 |
| **Avg Score** | 6.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 50 (53%) |
| **Partial/Composite** | 41 (43%) |
| **No Mapping** | 3 (3%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 83 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `grantUriPermission` | `void grantUriPermission(String, android.net.Uri, int)` | 10 | direct | trivial | `grantUriPermission` | `grantUriPermission(uri: string,
    flag: wantConstant.Flags,
    targetBundleName: string,
    callback: AsyncCallback<number>): void` |
| `revokeUriPermission` | `void revokeUriPermission(android.net.Uri, int)` | 10 | direct | trivial | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `revokeUriPermission` | `void revokeUriPermission(String, android.net.Uri, int)` | 10 | direct | trivial | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `deleteSharedPreferences` | `boolean deleteSharedPreferences(String)` | 8 | direct | easy | `deletePreferences` | `deletePreferences(context: Context, name: string, callback: AsyncCallback<void>): void` |
| `deleteDatabase` | `boolean deleteDatabase(String)` | 8 | direct | easy | `deleteData` | `deleteData(options: Options, callback: AsyncCallback<Array<UnifiedData>>): void` |
| `getBaseContext` | `android.content.Context getBaseContext()` | 8 | direct | easy | `getContext` | `getContext(): Context` |
| `enforceUriPermission` | `void enforceUriPermission(android.net.Uri, int, int, int, String)` | 8 | direct | easy | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `enforceUriPermission` | `void enforceUriPermission(android.net.Uri, String, String, int, int, int, String)` | 8 | direct | easy | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `getApplicationInfo` | `android.content.pm.ApplicationInfo getApplicationInfo()` | 8 | direct | easy | `getApplicationQuickFixInfo` | `getApplicationQuickFixInfo(bundleName: string, callback: AsyncCallback<ApplicationQuickFixInfo>): void` |
| `checkUriPermission` | `int checkUriPermission(android.net.Uri, int, int, int)` | 8 | direct | easy | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `checkUriPermission` | `int checkUriPermission(android.net.Uri, String, String, int, int, int)` | 8 | direct | easy | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `fileList` | `String[] fileList()` | 8 | direct | easy | `fileList` | `fileList: Array<FileResponse>` |
| `deleteFile` | `boolean deleteFile(String)` | 8 | near | easy | `deleteId` | `deleteId(uri: string): string` |
| `moveSharedPreferencesFrom` | `boolean moveSharedPreferencesFrom(android.content.Context, String)` | 7 | near | easy | `removePreferencesFromCache` | `removePreferencesFromCache(context: Context, name: string, callback: AsyncCallback<void>): void` |
| `getDir` | `java.io.File getDir(String, int)` | 7 | near | easy | `getId` | `getId(uri: string): number` |
| `enforcePermission` | `void enforcePermission(String, int, int, String)` | 7 | near | easy | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `createConfigurationContext` | `android.content.Context createConfigurationContext(android.content.res.Configuration)` | 7 | near | easy | `updateConfiguration` | `updateConfiguration(config: Configuration, callback: AsyncCallback<void>): void` |
| `checkPermission` | `int checkPermission(String, int, int)` | 7 | near | easy | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `getSharedPreferences` | `android.content.SharedPreferences getSharedPreferences(String, int)` | 7 | near | easy | `getPreferences` | `getPreferences(context: Context, name: string, callback: AsyncCallback<Preferences>): void` |
| `registerReceiver` | `android.content.Intent registerReceiver(android.content.BroadcastReceiver, android.content.IntentFilter)` | 7 | near | easy | `subscribe` | `subscribe(subscriber: CommonEventSubscriber, callback: AsyncCallback<CommonEventData>): void` |
| `registerReceiver` | `android.content.Intent registerReceiver(android.content.BroadcastReceiver, android.content.IntentFilter, int)` | 7 | near | easy | `subscribe` | `subscribe(subscriber: CommonEventSubscriber, callback: AsyncCallback<CommonEventData>): void` |
| `registerReceiver` | `android.content.Intent registerReceiver(android.content.BroadcastReceiver, android.content.IntentFilter, String, android.os.Handler)` | 7 | near | easy | `subscribe` | `subscribe(subscriber: CommonEventSubscriber, callback: AsyncCallback<CommonEventData>): void` |
| `registerReceiver` | `android.content.Intent registerReceiver(android.content.BroadcastReceiver, android.content.IntentFilter, String, android.os.Handler, int)` | 7 | near | easy | `subscribe` | `subscribe(subscriber: CommonEventSubscriber, callback: AsyncCallback<CommonEventData>): void` |
| `sendBroadcast` | `void sendBroadcast(android.content.Intent)` | 7 | near | easy | `publish` | `publish(event: string, callback: AsyncCallback<void>): void` |
| `sendBroadcast` | `void sendBroadcast(android.content.Intent, String)` | 7 | near | easy | `publish` | `publish(event: string, callback: AsyncCallback<void>): void` |
| `startActivity` | `void startActivity(android.content.Intent)` | 7 | near | easy | `startAbility` | `startAbility(parameter: StartAbilityParameter, callback: AsyncCallback<number>): void` |
| `startActivity` | `void startActivity(android.content.Intent, android.os.Bundle)` | 7 | near | easy | `startAbility` | `startAbility(parameter: StartAbilityParameter, callback: AsyncCallback<number>): void` |
| `unregisterReceiver` | `void unregisterReceiver(android.content.BroadcastReceiver)` | 7 | near | easy | `unsubscribe` | `unsubscribe(subscriber: CommonEventSubscriber, callback?: AsyncCallback<void>): void` |
| `checkCallingUriPermission` | `int checkCallingUriPermission(android.net.Uri, int)` | 7 | near | moderate | `grantUriPermission` | `grantUriPermission(uri: string,
    flag: wantConstant.Flags,
    targetBundleName: string,
    callback: AsyncCallback<number>): void` |
| `enforceCallingUriPermission` | `void enforceCallingUriPermission(android.net.Uri, int, String)` | 7 | near | moderate | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `getApplicationContext` | `android.content.Context getApplicationContext()` | 7 | near | moderate | `setApplicationAutoStartup` | `setApplicationAutoStartup(info: AutoStartupInfo, callback: AsyncCallback<void>): void` |
| `getResources` | `android.content.res.Resources getResources()` | 7 | near | moderate | `getPreferences` | `getPreferences(context: Context, name: string, callback: AsyncCallback<Preferences>): void` |
| `checkSelfPermission` | `int checkSelfPermission(String)` | 7 | near | moderate | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `ContextWrapper` | `ContextWrapper(android.content.Context)` | 7 | near | moderate | `context` | `context: BaseContext` |
| `getAssets` | `android.content.res.AssetManager getAssets()` | 7 | near | moderate | `getArguments` | `getArguments(): AbilityDelegatorArgs` |
| `getPackageName` | `String getPackageName()` | 7 | near | moderate | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `startActivities` | `void startActivities(android.content.Intent[])` | 7 | near | moderate | `startAbility` | `startAbility(parameter: StartAbilityParameter, callback: AsyncCallback<number>): void` |
| `startActivities` | `void startActivities(android.content.Intent[], android.os.Bundle)` | 7 | near | moderate | `startAbility` | `startAbility(parameter: StartAbilityParameter, callback: AsyncCallback<number>): void` |
| `enforceCallingPermission` | `void enforceCallingPermission(String, String)` | 7 | near | moderate | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `isDeviceProtectedStorage` | `boolean isDeviceProtectedStorage()` | 6 | near | moderate | `deleteStorage` | `deleteStorage(path: string, callback: AsyncCallback<void>): void` |
| `getContentResolver` | `android.content.ContentResolver getContentResolver()` | 6 | near | moderate | `getContext` | `getContext(): Context` |
| `checkCallingOrSelfUriPermission` | `int checkCallingOrSelfUriPermission(android.net.Uri, int)` | 6 | near | moderate | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `checkCallingPermission` | `int checkCallingPermission(String)` | 6 | near | moderate | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `getClassLoader` | `ClassLoader getClassLoader()` | 6 | near | moderate | `createAssetLoaderStub` | `createAssetLoaderStub(instance: AssetLoader): Promise<rpc.RemoteObject>` |
| `getFilesDir` | `java.io.File getFilesDir()` | 6 | near | moderate | `getId` | `getId(uri: string): number` |
| `enforceCallingOrSelfUriPermission` | `void enforceCallingOrSelfUriPermission(android.net.Uri, int, String)` | 6 | near | moderate | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `getDatabasePath` | `java.io.File getDatabasePath(String)` | 6 | near | moderate | `OH_Data_Asset_SetPath` | `int OH_Data_Asset_SetPath(Data_Asset *asset, const char *path)` |
| `createDisplayContext` | `android.content.Context createDisplayContext(android.view.Display)` | 6 | near | moderate | `getContext` | `getContext(): Context` |
| `createPackageContext` | `android.content.Context createPackageContext(String, int) throws android.content.pm.PackageManager.NameNotFoundException` | 6 | near | moderate | `getContext` | `getContext(): Context` |
| `getDataDir` | `java.io.File getDataDir()` | 6 | near | moderate | `insertData` | `insertData(options: Options, data: UnifiedData, callback: AsyncCallback<string>): void` |
| `attachBaseContext` | `void attachBaseContext(android.content.Context)` | 6 | partial | moderate | `getContext` | `getContext(): Context` |
| `startForegroundService` | `android.content.ComponentName startForegroundService(android.content.Intent)` | 6 | partial | moderate | `startBackgroundRunning` | `startBackgroundRunning(id: number, request: NotificationRequest, callback: AsyncCallback<void>): void` |
| `getExternalCacheDirs` | `java.io.File[] getExternalCacheDirs()` | 6 | partial | moderate | `getPreferences` | `getPreferences(context: Context, name: string, callback: AsyncCallback<Preferences>): void` |
| `startService` | `android.content.ComponentName startService(android.content.Intent)` | 6 | partial | moderate | `createShareServiceStub` | `createShareServiceStub(instance: ShareCenter): Promise<rpc.RemoteObject>` |
| `openOrCreateDatabase` | `android.database.sqlite.SQLiteDatabase openOrCreateDatabase(String, int, android.database.sqlite.SQLiteDatabase.CursorFactory)` | 6 | partial | moderate | `createDataShareHelper` | `createDataShareHelper(context: Context, uri: string, callback: AsyncCallback<DataShareHelper>): void` |
| `openOrCreateDatabase` | `android.database.sqlite.SQLiteDatabase openOrCreateDatabase(String, int, android.database.sqlite.SQLiteDatabase.CursorFactory, android.database.DatabaseErrorHandler)` | 6 | partial | moderate | `createDataShareHelper` | `createDataShareHelper(context: Context, uri: string, callback: AsyncCallback<DataShareHelper>): void` |
| `createContextForSplit` | `android.content.Context createContextForSplit(String) throws android.content.pm.PackageManager.NameNotFoundException` | 6 | partial | moderate | `getContext` | `getContext(): Context` |
| `enforceCallingOrSelfPermission` | `void enforceCallingOrSelfPermission(String, String)` | 6 | partial | moderate | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `getObbDir` | `java.io.File getObbDir()` | 6 | partial | moderate | `getId` | `getId(uri: string): number` |
| `getTheme` | `android.content.res.Resources.Theme getTheme()` | 6 | partial | moderate | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `getMainLooper` | `android.os.Looper getMainLooper()` | 6 | partial | moderate | `getFormsInfo` | `getFormsInfo(bundleName: string, callback: AsyncCallback<Array<formInfo.FormInfo>>): void` |
| `getPackageResourcePath` | `String getPackageResourcePath()` | 6 | partial | moderate | `allocResourceAndShare` | `allocResourceAndShare(storeId: string,
      predicates: relationalStore.RdbPredicates,
      participants: Array<Participant>,
      columns?: Array<string>): Promise<relationalStore.ResultSet>` |
| `checkCallingOrSelfPermission` | `int checkCallingOrSelfPermission(String)` | 6 | partial | moderate | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `getPackageManager` | `android.content.pm.PackageManager getPackageManager()` | 6 | partial | moderate | `getWantAgent` | `getWantAgent(info: WantAgentInfo, callback: AsyncCallback<WantAgent>): void` |
| `getExternalCacheDir` | `java.io.File getExternalCacheDir()` | 5 | partial | moderate | `getPreferences` | `getPreferences(context: Context, name: string, callback: AsyncCallback<Preferences>): void` |
| `getExternalFilesDir` | `java.io.File getExternalFilesDir(String)` | 5 | partial | moderate | `getPreferences` | `getPreferences(context: Context, name: string, callback: AsyncCallback<Preferences>): void` |
| `getExternalMediaDirs` | `java.io.File[] getExternalMediaDirs()` | 5 | partial | moderate | `getExtensionRunningInfos` | `getExtensionRunningInfos(upperLimit: number): Promise<Array<ExtensionRunningInfo>>` |
| `stopService` | `boolean stopService(android.content.Intent)` | 5 | partial | moderate | `createCloudServiceStub` | `createCloudServiceStub(instance: CloudService): Promise<rpc.RemoteObject>` |
| `moveDatabaseFrom` | `boolean moveDatabaseFrom(android.content.Context, String)` | 5 | partial | moderate | `moveToLast` | `moveToLast(): boolean` |
| `getObbDirs` | `java.io.File[] getObbDirs()` | 5 | partial | moderate | `getId` | `getId(uri: string): number` |
| `getPackageCodePath` | `String getPackageCodePath()` | 5 | partial | moderate | `getWantAgent` | `getWantAgent(info: WantAgentInfo, callback: AsyncCallback<WantAgent>): void` |
| `getSystemService` | `Object getSystemService(String)` | 5 | partial | moderate | `getPreferences` | `getPreferences(context: Context, name: string, callback: AsyncCallback<Preferences>): void` |
| `getExternalFilesDirs` | `java.io.File[] getExternalFilesDirs(String)` | 5 | partial | moderate | `getPreferences` | `getPreferences(context: Context, name: string, callback: AsyncCallback<Preferences>): void` |
| `getCacheDir` | `java.io.File getCacheDir()` | 5 | partial | moderate | `getEntry` | `getEntry(): Entry` |
| `getFileStreamPath` | `java.io.File getFileStreamPath(String)` | 5 | partial | moderate | `getStorage` | `getStorage(path: string, callback: AsyncCallback<Storage>): void` |
| `unbindService` | `void unbindService(android.content.ServiceConnection)` | 5 | partial | moderate | `createCloudServiceStub` | `createCloudServiceStub(instance: CloudService): Promise<rpc.RemoteObject>` |
| `getCodeCacheDir` | `java.io.File getCodeCacheDir()` | 5 | partial | moderate | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `getNoBackupFilesDir` | `java.io.File getNoBackupFilesDir()` | 5 | partial | moderate | `getTopAbility` | `getTopAbility(): Promise<ElementName>` |
| `getSystemServiceName` | `String getSystemServiceName(Class<?>)` | 5 | partial | moderate | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `sendOrderedBroadcast` | `void sendOrderedBroadcast(android.content.Intent, String)` | 5 | partial | moderate | `sendDialogResult` | `sendDialogResult(dialogSessionId: string, targetWant: Want, isAllowed: boolean): Promise<void>` |
| `sendOrderedBroadcast` | `void sendOrderedBroadcast(android.content.Intent, String, android.content.BroadcastReceiver, android.os.Handler, int, String, android.os.Bundle)` | 5 | partial | moderate | `sendDialogResult` | `sendDialogResult(dialogSessionId: string, targetWant: Want, isAllowed: boolean): Promise<void>` |
| `sendOrderedBroadcast` | `void sendOrderedBroadcast(@NonNull @RequiresPermission android.content.Intent, int, @Nullable String, @Nullable String, @Nullable android.content.BroadcastReceiver, @Nullable android.os.Handler, @Nullable String, @Nullable android.os.Bundle, @Nullable android.os.Bundle)` | 5 | partial | moderate | `sendDialogResult` | `sendDialogResult(dialogSessionId: string, targetWant: Want, isAllowed: boolean): Promise<void>` |
| `startInstrumentation` | `boolean startInstrumentation(android.content.ComponentName, String, android.os.Bundle)` | 5 | partial | moderate | `startAbility` | `startAbility(parameter: StartAbilityParameter, callback: AsyncCallback<number>): void` |

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `createDeviceProtectedStorageContext` | 5 | partial | Return dummy instance / no-op |
| `bindService` | 5 | partial | throw UnsupportedOperationException |
| `startIntentSender` | 5 | partial | Return dummy instance / no-op |
| `startIntentSender` | 5 | partial | Return dummy instance / no-op |
| `setTheme` | 5 | partial | Log warning + no-op |
| `sendOrderedBroadcastAsUser` | 4 | partial | throw UnsupportedOperationException |
| `sendBroadcastAsUser` | 4 | partial | throw UnsupportedOperationException |
| `sendBroadcastAsUser` | 4 | partial | throw UnsupportedOperationException |
| `databaseList` | 1 | none | Return safe default (null/false/0/empty) |
| `openFileInput` | 1 | none | Return dummy instance / no-op |
| `openFileOutput` | 1 | none | Return dummy instance / no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 83 methods that have score >= 5
2. Stub 11 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.content.ContextWrapper`:

- `android.content.Context` (already shimmed)
- `android.content.Intent` (already shimmed)

## Quality Gates

Before marking `android.content.ContextWrapper` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 94 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 83 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
