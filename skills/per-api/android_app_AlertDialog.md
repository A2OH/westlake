# SKILL: android.app.AlertDialog

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.AlertDialog`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.AlertDialog` |
| **Package** | `android.app` |
| **Total Methods** | 15 |
| **Avg Score** | 5.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 15 (100%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 10 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `setInverseBackgroundForced` | `void setInverseBackgroundForced(boolean)` | 6 | partial | moderate | `startBackgroundRunning` | `startBackgroundRunning(id: number, request: NotificationRequest, callback: AsyncCallback<void>): void` |
| `getButton` | `android.widget.Button getButton(int)` | 5 | partial | moderate | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `setButton` | `void setButton(int, CharSequence, android.os.Message)` | 5 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `setButton` | `void setButton(int, CharSequence, android.content.DialogInterface.OnClickListener)` | 5 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `AlertDialog` | `AlertDialog(android.content.Context)` | 5 | partial | moderate | `sendDialogResult` | `sendDialogResult(dialogSessionId: string, targetWant: Want, isAllowed: boolean): Promise<void>` |
| `AlertDialog` | `AlertDialog(android.content.Context, boolean, android.content.DialogInterface.OnCancelListener)` | 5 | partial | moderate | `sendDialogResult` | `sendDialogResult(dialogSessionId: string, targetWant: Want, isAllowed: boolean): Promise<void>` |
| `AlertDialog` | `AlertDialog(android.content.Context, @StyleRes int)` | 5 | partial | moderate | `sendDialogResult` | `sendDialogResult(dialogSessionId: string, targetWant: Want, isAllowed: boolean): Promise<void>` |
| `getListView` | `android.widget.ListView getListView()` | 5 | partial | moderate | `getId` | `getId(uri: string): number` |
| `setCustomTitle` | `void setCustomTitle(android.view.View)` | 5 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `setMessage` | `void setMessage(CharSequence)` | 5 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setIconAttribute` | 5 | partial | Log warning + no-op |
| `setView` | 5 | partial | Log warning + no-op |
| `setView` | 5 | partial | Log warning + no-op |
| `setIcon` | 4 | partial | Log warning + no-op |
| `setIcon` | 4 | partial | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 10 methods that have score >= 5
2. Stub 5 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.AlertDialog`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.AlertDialog` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 15 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 10 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
