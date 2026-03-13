# SKILL: android.app.RemoteInput

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.RemoteInput`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.RemoteInput` |
| **Package** | `android.app` |
| **Total Methods** | 16 |
| **Avg Score** | 5.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (25%) |
| **Partial/Composite** | 11 (68%) |
| **No Mapping** | 1 (6%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 10 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getAllowFreeFormInput` | `boolean getAllowFreeFormInput()` | 7 | near | moderate | `getAllFormsInfo` | `getAllFormsInfo(callback: AsyncCallback<Array<formInfo.FormInfo>>): void` |
| `getExtras` | `android.os.Bundle getExtras()` | 6 | near | moderate | `getContext` | `getContext(): Context` |
| `getAllowedDataTypes` | `java.util.Set<java.lang.String> getAllowedDataTypes()` | 6 | near | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `getChoices` | `CharSequence[] getChoices()` | 6 | near | moderate | `getContext` | `getContext(): Context` |
| `getLabel` | `CharSequence getLabel()` | 6 | partial | moderate | `getTopAbility` | `getTopAbility(): Promise<ElementName>` |
| `getResultsFromIntent` | `static android.os.Bundle getResultsFromIntent(android.content.Intent)` | 6 | partial | moderate | `getArguments` | `getArguments(): AbilityDelegatorArgs` |
| `getDataResultsFromIntent` | `static java.util.Map<java.lang.String,android.net.Uri> getDataResultsFromIntent(android.content.Intent, String)` | 6 | partial | moderate | `getArguments` | `getArguments(): AbilityDelegatorArgs` |
| `getResultKey` | `String getResultKey()` | 5 | partial | moderate | `getRequestInfo` | `getRequestInfo(want: Want): RequestInfo` |
| `getResultsSource` | `static int getResultsSource(android.content.Intent)` | 5 | partial | moderate | `getRequestInfo` | `getRequestInfo(want: Want): RequestInfo` |
| `setResultsSource` | `static void setResultsSource(android.content.Intent, int)` | 5 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getEditChoicesBeforeSending` | 5 | partial | Return safe default (null/false/0/empty) |
| `addDataResultToIntent` | 5 | partial | Log warning + no-op |
| `isDataOnly` | 5 | partial | Return safe default (null/false/0/empty) |
| `addResultsToIntent` | 4 | composite | Log warning + no-op |
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 10 methods that have score >= 5
2. Stub 6 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.RemoteInput`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.RemoteInput` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 16 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 10 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
