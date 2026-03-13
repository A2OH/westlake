# SKILL: android.app.Instrumentation

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.Instrumentation`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.Instrumentation` |
| **Package** | `android.app` |
| **Total Methods** | 66 |
| **Avg Score** | 4.4 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 16 (24%) |
| **Partial/Composite** | 39 (59%) |
| **No Mapping** | 11 (16%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 22 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getContext` | `android.content.Context getContext()` | 10 | direct | trivial | `getContext` | `getContext(): Context` |
| `stopProfiling` | `void stopProfiling()` | 8 | direct | easy | `stopProfiling` | `stopProfiling(): void` |
| `getTargetContext` | `android.content.Context getTargetContext()` | 8 | near | easy | `getContext` | `getContext(): Context` |
| `onCreate` | `void onCreate(android.os.Bundle)` | 7 | near | easy | `onPrepare` | `onPrepare(): void` |
| `finish` | `void finish(int, android.os.Bundle)` | 7 | near | easy | `terminateSelf` | `terminateSelf(callback: AsyncCallback<void>): void` |
| `onDestroy` | `void onDestroy()` | 7 | near | easy | `onDestroy` | `onDestroy?: () => void` |
| `start` | `void start()` | 7 | near | easy | `start` | `start(sinkDeviceDescriptor: string, srcInputDeviceId: number, callback: AsyncCallback<void>): void` |
| `startActivitySync` | `android.app.Activity startActivitySync(android.content.Intent)` | 7 | near | moderate | `startAbility` | `startAbility(parameter: StartAbilityParameter, callback: AsyncCallback<number>): void` |
| `acquireLooperManager` | `android.os.TestLooperManager acquireLooperManager(android.os.Looper)` | 7 | near | moderate | `acquireFormData` | `acquireFormData(formId: string, callback: AsyncCallback<Record<string, Object>>): void` |
| `getProcessName` | `String getProcessName()` | 7 | near | moderate | `getProcessMemoryByPid` | `getProcessMemoryByPid(pid: number): Promise<number>` |
| `callApplicationOnCreate` | `void callApplicationOnCreate(android.app.Application)` | 7 | near | moderate | `clearUpApplicationData` | `clearUpApplicationData(bundleName: string): Promise<void>` |
| `getComponentName` | `android.content.ComponentName getComponentName()` | 7 | near | moderate | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `newApplication` | `android.app.Application newApplication(ClassLoader, String, android.content.Context) throws java.lang.ClassNotFoundException, java.lang.IllegalAccessException, java.lang.InstantiationException` | 7 | near | moderate | `clearUpApplicationData` | `clearUpApplicationData(bundleName: string): Promise<void>` |
| `newApplication` | `static android.app.Application newApplication(Class<?>, android.content.Context) throws java.lang.ClassNotFoundException, java.lang.IllegalAccessException, java.lang.InstantiationException` | 7 | near | moderate | `clearUpApplicationData` | `clearUpApplicationData(bundleName: string): Promise<void>` |
| `getAllocCounts` | `android.os.Bundle getAllocCounts()` | 6 | near | moderate | `getArguments` | `getArguments(): AbilityDelegatorArgs` |
| `startProfiling` | `void startProfiling()` | 6 | near | moderate | `startAbility` | `startAbility(parameter: StartAbilityParameter, callback: AsyncCallback<number>): void` |
| `startPerformanceSnapshot` | `void startPerformanceSnapshot()` | 6 | partial | moderate | `startChildProcess` | `startChildProcess(srcEntry: string, startMode: StartMode): Promise<number>` |
| `getBinderCounts` | `android.os.Bundle getBinderCounts()` | 6 | partial | moderate | `getWindow` | `getWindow(callback: AsyncCallback<window.Window>): void` |
| `getUiAutomation` | `android.app.UiAutomation getUiAutomation()` | 6 | partial | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `getUiAutomation` | `android.app.UiAutomation getUiAutomation(int)` | 6 | partial | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `onStart` | `void onStart()` | 5 | partial | moderate | `startAbility` | `startAbility(parameter: StartAbilityParameter, callback: AsyncCallback<number>): void` |
| `onException` | `boolean onException(Object, Throwable)` | 5 | partial | moderate | `on` | `on(type: 'abilityForegroundState', observer: AbilityForegroundStateObserver): void` |

## Stub APIs (score < 5): 44 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `invokeContextMenuAction` | 5 | partial | Store callback, never fire |
| `callActivityOnRestoreInstanceState` | 5 | partial | Store callback, never fire |
| `callActivityOnRestoreInstanceState` | 5 | partial | Store callback, never fire |
| `callActivityOnSaveInstanceState` | 5 | partial | Store callback, never fire |
| `callActivityOnSaveInstanceState` | 5 | partial | Store callback, never fire |
| `sendStatus` | 5 | partial | throw UnsupportedOperationException |
| `callActivityOnRestart` | 5 | partial | Return dummy instance / no-op |
| `isProfiling` | 5 | partial | Return safe default (null/false/0/empty) |
| `sendKeySync` | 4 | partial | throw UnsupportedOperationException |
| `setAutomaticPerformanceSnapshots` | 4 | partial | Log warning + no-op |
| `waitForIdleSync` | 4 | partial | throw UnsupportedOperationException |
| `waitForIdle` | 4 | partial | throw UnsupportedOperationException |
| `setInTouchMode` | 4 | partial | Log warning + no-op |
| `callActivityOnPostCreate` | 4 | partial | Return dummy instance / no-op |
| `callActivityOnPostCreate` | 4 | partial | Return dummy instance / no-op |
| `sendCharacterSync` | 4 | partial | throw UnsupportedOperationException |
| `sendTrackballEventSync` | 4 | partial | throw UnsupportedOperationException |
| `waitForMonitorWithTimeout` | 4 | partial | Store callback, never fire |
| `callActivityOnCreate` | 4 | partial | Return dummy instance / no-op |
| `callActivityOnCreate` | 4 | partial | Return dummy instance / no-op |
| `sendStringSync` | 4 | partial | throw UnsupportedOperationException |
| `waitForMonitor` | 4 | partial | Store callback, never fire |
| `sendPointerSync` | 4 | composite | throw UnsupportedOperationException |
| `runOnMainSync` | 4 | composite | Store callback, never fire |
| `sendKeyDownUpSync` | 4 | composite | throw UnsupportedOperationException |
| `callActivityOnPause` | 4 | composite | Store callback, never fire |
| `callActivityOnResume` | 3 | composite | Store callback, never fire |
| `callActivityOnPictureInPictureRequested` | 3 | composite | Store callback, never fire |
| `callActivityOnStart` | 3 | composite | Return dummy instance / no-op |
| `callActivityOnUserLeaving` | 3 | composite | Store callback, never fire |
| `callActivityOnDestroy` | 3 | composite | No-op |
| `callActivityOnNewIntent` | 2 | composite | Store callback, never fire |
| `callActivityOnStop` | 2 | composite | No-op |
| `Instrumentation` | 1 | none | Store callback, never fire |
| `addMonitor` | 1 | none | Log warning + no-op |
| `addMonitor` | 1 | none | Log warning + no-op |
| `addMonitor` | 1 | none | Log warning + no-op |
| `addResults` | 1 | none | Log warning + no-op |
| `checkMonitorHit` | 1 | none | Store callback, never fire |
| `endPerformanceSnapshot` | 1 | none | throw UnsupportedOperationException |
| `invokeMenuActionSync` | 1 | none | Store callback, never fire |
| `newActivity` | 1 | none | throw UnsupportedOperationException |
| `newActivity` | 1 | none | throw UnsupportedOperationException |
| `removeMonitor` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 22 methods that have score >= 5
2. Stub 44 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.Instrumentation`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.Instrumentation` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 66 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 22 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
