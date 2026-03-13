# SKILL: android.app.UiAutomation

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.UiAutomation`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.UiAutomation` |
| **Package** | `android.app` |
| **Total Methods** | 25 |
| **Avg Score** | 5.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 9 (36%) |
| **Partial/Composite** | 13 (52%) |
| **No Mapping** | 3 (12%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 18 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getWindows` | `java.util.List<android.view.accessibility.AccessibilityWindowInfo> getWindows()` | 9 | direct | trivial | `getWindow` | `getWindow(callback: AsyncCallback<window.Window>): void` |
| `revokeRuntimePermission` | `void revokeRuntimePermission(String, String)` | 9 | direct | easy | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `grantRuntimePermission` | `void grantRuntimePermission(String, String)` | 8 | direct | easy | `grantUriPermission` | `grantUriPermission(uri: string,
    flag: wantConstant.Flags,
    targetBundleName: string,
    callback: AsyncCallback<number>): void` |
| `revokeRuntimePermissionAsUser` | `void revokeRuntimePermissionAsUser(String, String, android.os.UserHandle)` | 8 | near | easy | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `grantRuntimePermissionAsUser` | `void grantRuntimePermissionAsUser(String, String, android.os.UserHandle)` | 7 | near | easy | `grantUriPermission` | `grantUriPermission(uri: string,
    flag: wantConstant.Flags,
    targetBundleName: string,
    callback: AsyncCallback<number>): void` |
| `findFocus` | `android.view.accessibility.AccessibilityNodeInfo findFocus(int)` | 7 | near | moderate | `hasWindowFocus` | `hasWindowFocus(callback: AsyncCallback<boolean>): void` |
| `getServiceInfo` | `android.accessibilityservice.AccessibilityServiceInfo getServiceInfo()` | 7 | near | moderate | `getRequestInfo` | `getRequestInfo(want: Want): RequestInfo` |
| `setRotation` | `boolean setRotation(int)` | 6 | near | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `getRootInActiveWindow` | `android.view.accessibility.AccessibilityNodeInfo getRootInActiveWindow()` | 6 | near | moderate | `getWindow` | `getWindow(callback: AsyncCallback<window.Window>): void` |
| `setServiceInfo` | `void setServiceInfo(android.accessibilityservice.AccessibilityServiceInfo)` | 6 | partial | moderate | `getMissionInfo` | `getMissionInfo(deviceId: string, missionId: number, callback: AsyncCallback<MissionInfo>): void` |
| `dropShellPermissionIdentity` | `void dropShellPermissionIdentity()` | 6 | partial | moderate | `revokeUriPermission` | `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void` |
| `clearWindowAnimationFrameStats` | `void clearWindowAnimationFrameStats()` | 5 | partial | moderate | `clearUpApplicationData` | `clearUpApplicationData(bundleName: string): Promise<void>` |
| `executeShellCommand` | `android.os.ParcelFileDescriptor executeShellCommand(String)` | 5 | partial | moderate | `execute` | `execute(param: ExecuteParam, callback: AsyncCallback<insightIntent.ExecuteResult>): void` |
| `adoptShellPermissionIdentity` | `void adoptShellPermissionIdentity()` | 5 | partial | moderate | `grantUriPermission` | `grantUriPermission(uri: string,
    flag: wantConstant.Flags,
    targetBundleName: string,
    callback: AsyncCallback<number>): void` |
| `adoptShellPermissionIdentity` | `void adoptShellPermissionIdentity(@Nullable java.lang.String...)` | 5 | partial | moderate | `grantUriPermission` | `grantUriPermission(uri: string,
    flag: wantConstant.Flags,
    targetBundleName: string,
    callback: AsyncCallback<number>): void` |
| `setOnAccessibilityEventListener` | `void setOnAccessibilityEventListener(android.app.UiAutomation.OnAccessibilityEventListener)` | 5 | partial | moderate | `registerMissionListener` | `registerMissionListener(listener: MissionListener): number` |
| `getWindowContentFrameStats` | `android.view.WindowContentFrameStats getWindowContentFrameStats(int)` | 5 | partial | moderate | `getWindow` | `getWindow(callback: AsyncCallback<window.Window>): void` |
| `setRunAsMonkey` | `void setRunAsMonkey(boolean)` | 5 | partial | moderate | `setRouterProxy` | `setRouterProxy(formIds: Array<string>, proxy: Callback<Want>, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getWindowAnimationFrameStats` | 5 | partial | Return safe default (null/false/0/empty) |
| `executeAndWaitForEvent` | 5 | partial | throw UnsupportedOperationException |
| `clearWindowContentFrameStats` | 4 | partial | Store callback, never fire |
| `waitForIdle` | 4 | partial | throw UnsupportedOperationException |
| `injectInputEvent` | 1 | none | Log warning + no-op |
| `performGlobalAction` | 1 | none | Store callback, never fire |
| `takeScreenshot` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 18 methods that have score >= 5
2. Stub 7 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.UiAutomation`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.UiAutomation` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 25 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 18 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
