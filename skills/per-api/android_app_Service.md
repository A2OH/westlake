# SKILL: android.app.Service

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.Service`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.Service` |
| **Package** | `android.app` |
| **Total Methods** | 20 |
| **Avg Score** | 5.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 12 (60%) |
| **Partial/Composite** | 6 (30%) |
| **No Mapping** | 2 (10%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 16 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getApplication` | `final android.app.Application getApplication()` | 7 | near | easy | `getForegroundApplications` | `getForegroundApplications(callback: AsyncCallback<Array<AppStateData>>): void` |
| `onCreate` | `void onCreate()` | 7 | near | easy | `onPrepare` | `onPrepare(): void` |
| `onDestroy` | `void onDestroy()` | 7 | near | easy | `onDestroy` | `onDestroy?: () => void` |
| `stopForeground` | `final void stopForeground(boolean)` | 7 | near | moderate | `moveMissionsToForeground` | `moveMissionsToForeground(missionIds: Array<number>, callback: AsyncCallback<void>): void` |
| `stopForeground` | `final void stopForeground(int)` | 7 | near | moderate | `moveMissionsToForeground` | `moveMissionsToForeground(missionIds: Array<number>, callback: AsyncCallback<void>): void` |
| `getForegroundServiceType` | `final int getForegroundServiceType()` | 7 | near | moderate | `getForegroundApplications` | `getForegroundApplications(callback: AsyncCallback<Array<AppStateData>>): void` |
| `onConfigurationChanged` | `void onConfigurationChanged(android.content.res.Configuration)` | 6 | near | moderate | `updateConfiguration` | `updateConfiguration(config: Configuration, callback: AsyncCallback<void>): void` |
| `onRebind` | `void onRebind(android.content.Intent)` | 6 | near | moderate | `onRun` | `onRun(): void` |
| `onUnbind` | `boolean onUnbind(android.content.Intent)` | 6 | near | moderate | `onRun` | `onRun(): void` |
| `startForeground` | `final void startForeground(int, android.app.Notification)` | 6 | near | moderate | `moveMissionsToForeground` | `moveMissionsToForeground(missionIds: Array<number>, callback: AsyncCallback<void>): void` |
| `startForeground` | `final void startForeground(int, @NonNull android.app.Notification, int)` | 6 | near | moderate | `moveMissionsToForeground` | `moveMissionsToForeground(missionIds: Array<number>, callback: AsyncCallback<void>): void` |
| `stopSelfResult` | `final boolean stopSelfResult(int)` | 6 | near | moderate | `sendDialogResult` | `sendDialogResult(dialogSessionId: string, targetWant: Want, isAllowed: boolean): Promise<void>` |
| `onStartCommand` | `int onStartCommand(android.content.Intent, int, int)` | 5 | partial | moderate | `startBackgroundRunning` | `startBackgroundRunning(id: number, request: NotificationRequest, callback: AsyncCallback<void>): void` |
| `onTrimMemory` | `void onTrimMemory(int)` | 5 | partial | moderate | `getAppMemorySize` | `getAppMemorySize(): Promise<number>` |
| `stopSelf` | `final void stopSelf()` | 5 | partial | moderate | `terminateSelf` | `terminateSelf(callback: AsyncCallback<void>): void` |
| `stopSelf` | `final void stopSelf(int)` | 5 | partial | moderate | `terminateSelf` | `terminateSelf(callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onTaskRemoved` | 5 | partial | Log warning + no-op |
| `onLowMemory` | 4 | partial | Store callback, never fire |
| `Service` | 1 | none | throw UnsupportedOperationException |
| `dump` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 16 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.Service`:

- `android.content.Context` (already shimmed)
- `android.content.Intent` (already shimmed)

## Quality Gates

Before marking `android.app.Service` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 20 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 16 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
