# SKILL: android.app.Notification.MessagingStyle

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.Notification.MessagingStyle`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.Notification.MessagingStyle` |
| **Package** | `android.app.Notification` |
| **Total Methods** | 9 |
| **Avg Score** | 3.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 5 (55%) |
| **No Mapping** | 4 (44%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getMessages` | `java.util.List<android.app.Notification.MessagingStyle.Message> getMessages()` | 6 | partial | moderate | `getMissionSnapShot` | `getMissionSnapShot(deviceId: string, missionId: number, callback: AsyncCallback<MissionSnapshot>): void` |
| `setGroupConversation` | `android.app.Notification.MessagingStyle setGroupConversation(boolean)` | 5 | partial | moderate | `setRouterProxy` | `setRouterProxy(formIds: Array<string>, proxy: Callback<Want>, callback: AsyncCallback<void>): void` |
| `setConversationTitle` | `android.app.Notification.MessagingStyle setConversationTitle(@Nullable CharSequence)` | 5 | partial | moderate | `setFormNextRefreshTime` | `setFormNextRefreshTime(formId: string, minute: number, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getHistoricMessages` | 5 | partial | Return safe default (null/false/0/empty) |
| `isGroupConversation` | 5 | partial | Return safe default (null/false/0/empty) |
| `MessagingStyle` | 1 | none | throw UnsupportedOperationException |
| `addHistoricMessage` | 1 | none | Return safe default (null/false/0/empty) |
| `addMessage` | 1 | none | Log warning + no-op |
| `addMessage` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 6 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.Notification.MessagingStyle`:


## Quality Gates

Before marking `android.app.Notification.MessagingStyle` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
