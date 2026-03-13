# SKILL: android.content.BroadcastReceiver.PendingResult

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.BroadcastReceiver.PendingResult`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.BroadcastReceiver.PendingResult` |
| **Package** | `android.content.BroadcastReceiver` |
| **Total Methods** | 11 |
| **Avg Score** | 5.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 7 (63%) |
| **Partial/Composite** | 3 (27%) |
| **No Mapping** | 1 (9%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 9 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `setResult` | `final void setResult(int, String, android.os.Bundle)` | 8 | direct | easy | `result` | `result: number` |
| `finish` | `final void finish()` | 7 | near | easy | `terminateSelf` | `terminateSelf(callback: AsyncCallback<void>): void` |
| `setResultData` | `final void setResultData(String)` | 7 | near | moderate | `insertData` | `insertData(options: Options, data: UnifiedData, callback: AsyncCallback<string>): void` |
| `getResultCode` | `final int getResultCode()` | 6 | near | moderate | `result` | `result: number` |
| `getResultData` | `final String getResultData()` | 6 | near | moderate | `result` | `result: number` |
| `setResultCode` | `final void setResultCode(int)` | 6 | near | moderate | `result` | `result: number` |
| `setResultExtras` | `final void setResultExtras(android.os.Bundle)` | 6 | near | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `getResultExtras` | `final android.os.Bundle getResultExtras(boolean)` | 6 | partial | moderate | `result` | `result: number` |
| `getAbortBroadcast` | `final boolean getAbortBroadcast()` | 5 | partial | moderate | `getForegroundApplications` | `getForegroundApplications(callback: AsyncCallback<Array<AppStateData>>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `clearAbortBroadcast` | 5 | partial | throw UnsupportedOperationException |
| `abortBroadcast` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 9 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.content.BroadcastReceiver.PendingResult`:


## Quality Gates

Before marking `android.content.BroadcastReceiver.PendingResult` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 9 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
