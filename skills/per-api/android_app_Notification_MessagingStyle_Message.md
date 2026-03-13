# SKILL: android.app.Notification.MessagingStyle.Message

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.Notification.MessagingStyle.Message`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.Notification.MessagingStyle.Message` |
| **Package** | `android.app.Notification.MessagingStyle` |
| **Total Methods** | 14 |
| **Avg Score** | 5.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 5 (35%) |
| **Partial/Composite** | 7 (50%) |
| **No Mapping** | 2 (14%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 10 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getText` | `CharSequence getText()` | 8 | direct | easy | `getContext` | `getContext(): Context` |
| `Message` | `Notification.MessagingStyle.Message(@NonNull CharSequence, long, @Nullable android.app.Person)` | 8 | direct | easy | `message` | `readonly message: string` |
| `Style` | `Notification.Style()` | 8 | direct | easy | `style` | `style: CaptionsStyle` |
| `getDataMimeType` | `String getDataMimeType()` | 6 | near | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `getExtras` | `android.os.Bundle getExtras()` | 6 | near | moderate | `getContext` | `getContext(): Context` |
| `getDataUri` | `android.net.Uri getDataUri()` | 6 | partial | moderate | `getWant` | `getWant(callback: AsyncCallback<Want>): void` |
| `getTimestamp` | `long getTimestamp()` | 5 | partial | moderate | `getContext` | `getContext(): Context` |
| `setData` | `android.app.Notification.MessagingStyle.Message setData(String, android.net.Uri)` | 5 | partial | moderate | `acquireShareData` | `acquireShareData(missionId: number, callback: AsyncCallback<Record<string, Object>>): void` |
| `getStandardView` | `android.widget.RemoteViews getStandardView(int)` | 5 | partial | moderate | `getWindow` | `getWindow(callback: AsyncCallback<window.Window>): void` |
| `setBuilder` | `void setBuilder(android.app.Notification.Builder)` | 5 | partial | moderate | `setRouterProxy` | `setRouterProxy(formIds: Array<string>, proxy: Callback<Want>, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `internalSetSummaryText` | 4 | partial | Log warning + no-op |
| `internalSetBigContentTitle` | 4 | partial | Log warning + no-op |
| `build` | 1 | none | throw UnsupportedOperationException |
| `checkBuilder` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 10 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.Notification.MessagingStyle.Message`:


## Quality Gates

Before marking `android.app.Notification.MessagingStyle.Message` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 10 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
