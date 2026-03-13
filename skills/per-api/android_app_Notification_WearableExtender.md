# SKILL: android.app.Notification.WearableExtender

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.Notification.WearableExtender`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.Notification.WearableExtender` |
| **Package** | `android.app.Notification` |
| **Total Methods** | 20 |
| **Avg Score** | 3.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (20%) |
| **Partial/Composite** | 10 (50%) |
| **No Mapping** | 6 (30%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `clearActions` | `android.app.Notification.WearableExtender clearActions()` | 7 | near | easy | `clearAllMissions` | `clearAllMissions(callback: AsyncCallback<void>): void` |
| `getContentAction` | `int getContentAction()` | 7 | near | moderate | `getContext` | `getContext(): Context` |
| `getDismissalId` | `String getDismissalId()` | 7 | near | moderate | `getId` | `getId(uri: string): number` |
| `getActions` | `java.util.List<android.app.Notification.Action> getActions()` | 6 | near | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `getBridgeTag` | `String getBridgeTag()` | 6 | partial | moderate | `getId` | `getId(uri: string): number` |
| `setBridgeTag` | `android.app.Notification.WearableExtender setBridgeTag(String)` | 5 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `setStartScrollBottom` | `android.app.Notification.WearableExtender setStartScrollBottom(boolean)` | 5 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |

## Stub APIs (score < 5): 13 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getStartScrollBottom` | 5 | partial | Return dummy instance / no-op |
| `setContentAction` | 5 | partial | Log warning + no-op |
| `getContentIntentAvailableOffline` | 4 | partial | Return safe default (null/false/0/empty) |
| `setDismissalId` | 4 | partial | Return safe default (null/false/0/empty) |
| `setHintContentIntentLaunchesActivity` | 4 | composite | Log warning + no-op |
| `getHintContentIntentLaunchesActivity` | 4 | composite | Return safe default (null/false/0/empty) |
| `setContentIntentAvailableOffline` | 4 | composite | Log warning + no-op |
| `WearableExtender` | 1 | none | throw UnsupportedOperationException |
| `WearableExtender` | 1 | none | throw UnsupportedOperationException |
| `addAction` | 1 | none | Log warning + no-op |
| `addActions` | 1 | none | Log warning + no-op |
| `clone` | 1 | none | Store callback, never fire |
| `extend` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 7 methods that have score >= 5
2. Stub 13 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.Notification.WearableExtender`:


## Quality Gates

Before marking `android.app.Notification.WearableExtender` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 20 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
