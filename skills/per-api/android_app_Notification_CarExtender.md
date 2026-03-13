# SKILL: android.app.Notification.CarExtender

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.Notification.CarExtender`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.Notification.CarExtender` |
| **Package** | `android.app.Notification` |
| **Total Methods** | 8 |
| **Avg Score** | 3.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (12%) |
| **Partial/Composite** | 4 (50%) |
| **No Mapping** | 3 (37%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getLargeIcon` | `android.graphics.Bitmap getLargeIcon()` | 7 | near | moderate | `getArguments` | `getArguments(): AbilityDelegatorArgs` |
| `getUnreadConversation` | `android.app.Notification.CarExtender.UnreadConversation getUnreadConversation()` | 6 | partial | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setUnreadConversation` | 5 | partial | Return safe default (null/false/0/empty) |
| `setLargeIcon` | 5 | partial | Log warning + no-op |
| `setColor` | 5 | partial | Log warning + no-op |
| `CarExtender` | 1 | none | throw UnsupportedOperationException |
| `CarExtender` | 1 | none | throw UnsupportedOperationException |
| `extend` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 2 methods that have score >= 5
2. Stub 6 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.Notification.CarExtender`:


## Quality Gates

Before marking `android.app.Notification.CarExtender` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
