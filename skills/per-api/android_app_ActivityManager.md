# SKILL: android.app.ActivityManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.ActivityManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.ActivityManager` |
| **Package** | `android.app` |
| **Total Methods** | 27 |
| **Avg Score** | 5.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 13 (48%) |
| **Partial/Composite** | 14 (51%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 21 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `clearApplicationUserData` | `boolean clearApplicationUserData()` | 9 | direct | easy | `clearUpApplicationData` | `clearUpApplicationData(bundleName: string): Promise<void>` |
| `getProcessMemoryInfo` | `android.os.Debug.MemoryInfo[] getProcessMemoryInfo(int[])` | 8 | direct | easy | `getProcessMemoryByPid` | `getProcessMemoryByPid(pid: number): Promise<number>` |
| `isLowRamDevice` | `boolean isLowRamDevice()` | 8 | near | easy | `isRamConstrainedDevice` | `isRamConstrainedDevice(): Promise<boolean>` |
| `getMemoryInfo` | `void getMemoryInfo(android.app.ActivityManager.MemoryInfo)` | 7 | near | easy | `getFormsInfo` | `getFormsInfo(bundleName: string, callback: AsyncCallback<Array<formInfo.FormInfo>>): void` |
| `getMyMemoryState` | `static void getMyMemoryState(android.app.ActivityManager.RunningAppProcessInfo)` | 7 | near | moderate | `getAppMemorySize` | `getAppMemorySize(): Promise<number>` |
| `getRunningAppProcesses` | `java.util.List<android.app.ActivityManager.RunningAppProcessInfo> getRunningAppProcesses()` | 7 | near | moderate | `getRunningProcessInformation` | `getRunningProcessInformation(): Promise<Array<ProcessInformation>>` |
| `getDeviceConfigurationInfo` | `android.content.pm.ConfigurationInfo getDeviceConfigurationInfo()` | 7 | near | moderate | `updateConfiguration` | `updateConfiguration(config: Configuration, callback: AsyncCallback<void>): void` |
| `getMemoryClass` | `int getMemoryClass()` | 7 | near | moderate | `getAppMemorySize` | `getAppMemorySize(): Promise<number>` |
| `isRunningInUserTestHarness` | `static boolean isRunningInUserTestHarness()` | 7 | near | moderate | `isRunningInStabilityTest` | `isRunningInStabilityTest(callback: AsyncCallback<boolean>): void` |
| `getLargeMemoryClass` | `int getLargeMemoryClass()` | 6 | near | moderate | `getAppMemorySize` | `getAppMemorySize(): Promise<number>` |
| `getProcessesInErrorState` | `java.util.List<android.app.ActivityManager.ProcessErrorStateInfo> getProcessesInErrorState()` | 6 | near | moderate | `getRunningProcessInformation` | `getRunningProcessInformation(): Promise<Array<ProcessInformation>>` |
| `setProcessStateSummary` | `void setProcessStateSummary(@Nullable byte[])` | 6 | near | moderate | `getProcessMemoryByPid` | `getProcessMemoryByPid(pid: number): Promise<number>` |
| `getAppTaskThumbnailSize` | `android.util.Size getAppTaskThumbnailSize()` | 6 | near | moderate | `getAppMemorySize` | `getAppMemorySize(): Promise<number>` |
| `isBackgroundRestricted` | `boolean isBackgroundRestricted()` | 6 | partial | moderate | `startBackgroundRunning` | `startBackgroundRunning(id: number, request: NotificationRequest, callback: AsyncCallback<void>): void` |
| `getRunningServiceControlPanel` | `android.app.PendingIntent getRunningServiceControlPanel(android.content.ComponentName) throws java.lang.SecurityException` | 6 | partial | moderate | `getRunningProcessInfoByBundleName` | `getRunningProcessInfoByBundleName(bundleName: string, callback: AsyncCallback<Array<ProcessInformation>>): void` |
| `getAppTasks` | `java.util.List<android.app.ActivityManager.AppTask> getAppTasks()` | 6 | partial | moderate | `getWant` | `getWant(callback: AsyncCallback<Want>): void` |
| `addAppTask` | `int addAppTask(@NonNull android.app.Activity, @NonNull android.content.Intent, @Nullable android.app.ActivityManager.TaskDescription, @NonNull android.graphics.Bitmap)` | 5 | partial | moderate | `saveAppState` | `saveAppState(): boolean` |
| `isUserAMonkey` | `static boolean isUserAMonkey()` | 5 | partial | moderate | `isSystemReady` | `isSystemReady(callback: AsyncCallback<void>): void` |
| `clearWatchHeapLimit` | `void clearWatchHeapLimit()` | 5 | partial | moderate | `clearUpApplicationData` | `clearUpApplicationData(bundleName: string): Promise<void>` |
| `getLauncherLargeIconDensity` | `int getLauncherLargeIconDensity()` | 5 | partial | moderate | `getWantAgent` | `getWantAgent(info: WantAgentInfo, callback: AsyncCallback<WantAgent>): void` |
| `getLauncherLargeIconSize` | `int getLauncherLargeIconSize()` | 5 | partial | moderate | `getArguments` | `getArguments(): AbilityDelegatorArgs` |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setVrThread` | 5 | partial | Return safe default (null/false/0/empty) |
| `isLowMemoryKillReportSupported` | 5 | partial | Return safe default (null/false/0/empty) |
| `getLockTaskModeState` | 5 | partial | Return safe default (null/false/0/empty) |
| `setWatchHeapLimit` | 4 | partial | Log warning + no-op |
| `isActivityStartAllowedOnDisplay` | 4 | partial | Return dummy instance / no-op |
| `appNotResponding` | 4 | composite | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 21 methods that have score >= 5
2. Stub 6 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.ActivityManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.ActivityManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 27 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 21 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
