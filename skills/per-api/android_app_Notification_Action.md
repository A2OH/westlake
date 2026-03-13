# SKILL: android.app.Notification.Action

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.Notification.Action`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.Notification.Action` |
| **Package** | `android.app.Notification` |
| **Total Methods** | 10 |
| **Avg Score** | 4.4 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (20%) |
| **Partial/Composite** | 6 (60%) |
| **No Mapping** | 2 (20%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getIcon` | `android.graphics.drawable.Icon getIcon()` | 7 | near | easy | `getContext` | `getContext(): Context` |
| `getExtras` | `android.os.Bundle getExtras()` | 6 | near | moderate | `getContext` | `getContext(): Context` |
| `getRemoteInputs` | `android.app.RemoteInput[] getRemoteInputs()` | 6 | partial | moderate | `getArguments` | `getArguments(): AbilityDelegatorArgs` |
| `getSemanticAction` | `int getSemanticAction()` | 6 | partial | moderate | `getForegroundApplications` | `getForegroundApplications(callback: AsyncCallback<Array<AppStateData>>): void` |
| `getAllowGeneratedReplies` | `boolean getAllowGeneratedReplies()` | 5 | partial | moderate | `getArguments` | `getArguments(): AbilityDelegatorArgs` |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getDataOnlyRemoteInputs` | 5 | partial | Return safe default (null/false/0/empty) |
| `isContextual` | 4 | partial | Return safe default (null/false/0/empty) |
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `clone` | 1 | none | Store callback, never fire |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 5 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.Notification.Action`:


## Quality Gates

Before marking `android.app.Notification.Action` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
