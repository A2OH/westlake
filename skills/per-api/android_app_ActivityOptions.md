# SKILL: android.app.ActivityOptions

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.ActivityOptions`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.ActivityOptions` |
| **Package** | `android.app` |
| **Total Methods** | 16 |
| **Avg Score** | 4.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (12%) |
| **Partial/Composite** | 9 (56%) |
| **No Mapping** | 5 (31%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `update` | `void update(android.app.ActivityOptions)` | 7 | near | easy | `update` | `update(query: AssetMap, attributesToUpdate: AssetMap): Promise<void>` |
| `toBundle` | `android.os.Bundle toBundle()` | 7 | near | moderate | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `requestUsageTimeReport` | `void requestUsageTimeReport(android.app.PendingIntent)` | 6 | partial | moderate | `requestAutoSave` | `requestAutoSave(context: UIContext, callback?: AutoSaveCallback): void` |
| `makeScaleUpAnimation` | `static android.app.ActivityOptions makeScaleUpAnimation(android.view.View, int, int, int, int)` | 6 | partial | moderate | `clearUpApplicationData` | `clearUpApplicationData(bundleName: string): Promise<void>` |
| `setAppVerificationBundle` | `android.app.ActivityOptions setAppVerificationBundle(android.os.Bundle)` | 6 | partial | moderate | `setApplicationAutoStartup` | `setApplicationAutoStartup(info: AutoStartupInfo, callback: AsyncCallback<void>): void` |
| `getLaunchDisplayId` | `int getLaunchDisplayId()` | 5 | partial | moderate | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `getLockTaskMode` | `boolean getLockTaskMode()` | 5 | partial | moderate | `getProcessMemoryByPid` | `getProcessMemoryByPid(pid: number): Promise<number>` |

## Stub APIs (score < 5): 9 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `makeThumbnailScaleUpAnimation` | 5 | partial | Store callback, never fire |
| `setLaunchDisplayId` | 5 | partial | Return safe default (null/false/0/empty) |
| `setLockTaskEnabled` | 4 | partial | Log warning + no-op |
| `setLaunchBounds` | 4 | partial | Log warning + no-op |
| `makeBasic` | 1 | none | throw UnsupportedOperationException |
| `makeClipRevealAnimation` | 1 | none | Store callback, never fire |
| `makeCustomAnimation` | 1 | none | Store callback, never fire |
| `makeSceneTransitionAnimation` | 1 | none | Store callback, never fire |
| `makeTaskLaunchBehind` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 7 methods that have score >= 5
2. Stub 9 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.ActivityOptions`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.ActivityOptions` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 16 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
