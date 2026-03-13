# SKILL: android.app.ActivityManager.AppTask

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.ActivityManager.AppTask`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.ActivityManager.AppTask` |
| **Package** | `android.app.ActivityManager` |
| **Total Methods** | 5 |
| **Avg Score** | 5.4 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (60%) |
| **Partial/Composite** | 1 (20%) |
| **No Mapping** | 1 (20%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `moveToFront` | `void moveToFront()` | 8 | near | easy | `moveMissionToFront` | `moveMissionToFront(missionId: number, callback: AsyncCallback<void>): void` |
| `startActivity` | `void startActivity(android.content.Context, android.content.Intent, android.os.Bundle)` | 7 | near | easy | `startAbility` | `startAbility(parameter: StartAbilityParameter, callback: AsyncCallback<number>): void` |
| `getTaskInfo` | `android.app.ActivityManager.RecentTaskInfo getTaskInfo()` | 7 | near | moderate | `getFormsInfo` | `getFormsInfo(bundleName: string, callback: AsyncCallback<Array<formInfo.FormInfo>>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setExcludeFromRecents` | 5 | partial | Log warning + no-op |
| `finishAndRemoveTask` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.ActivityManager.AppTask`:


## Quality Gates

Before marking `android.app.ActivityManager.AppTask` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
