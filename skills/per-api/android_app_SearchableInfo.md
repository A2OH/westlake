# SKILL: android.app.SearchableInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.SearchableInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.SearchableInfo` |
| **Package** | `android.app` |
| **Total Methods** | 23 |
| **Avg Score** | 4.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (13%) |
| **Partial/Composite** | 19 (82%) |
| **No Mapping** | 1 (4%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 13 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getHintId` | `int getHintId()` | 7 | near | easy | `getId` | `getId(uri: string): number` |
| `getInputType` | `int getInputType()` | 7 | near | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `getImeOptions` | `int getImeOptions()` | 6 | near | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `getSuggestPackage` | `String getSuggestPackage()` | 6 | partial | moderate | `getRequestCallback` | `getRequestCallback(want: Want): RequestCallback` |
| `getSuggestIntentAction` | `String getSuggestIntentAction()` | 6 | partial | moderate | `getRunningProcessInformation` | `getRunningProcessInformation(): Promise<Array<ProcessInformation>>` |
| `getSearchActivity` | `android.content.ComponentName getSearchActivity()` | 5 | partial | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `getSuggestSelection` | `String getSuggestSelection()` | 5 | partial | moderate | `getRequestInfo` | `getRequestInfo(want: Want): RequestInfo` |
| `getSuggestIntentData` | `String getSuggestIntentData()` | 5 | partial | moderate | `getRequestInfo` | `getRequestInfo(want: Want): RequestInfo` |
| `getVoiceSearchEnabled` | `boolean getVoiceSearchEnabled()` | 5 | partial | moderate | `getProcessMemoryByPid` | `getProcessMemoryByPid(pid: number): Promise<number>` |
| `getSuggestPath` | `String getSuggestPath()` | 5 | partial | moderate | `getTopAbility` | `getTopAbility(): Promise<ElementName>` |
| `getSettingsDescriptionId` | `int getSettingsDescriptionId()` | 5 | partial | moderate | `getId` | `getId(uri: string): number` |
| `getSuggestAuthority` | `String getSuggestAuthority()` | 5 | partial | moderate | `getTopAbility` | `getTopAbility(): Promise<ElementName>` |
| `getVoiceMaxResults` | `int getVoiceMaxResults()` | 5 | partial | moderate | `getContext` | `getContext(): Context` |

## Stub APIs (score < 5): 10 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getSuggestThreshold` | 5 | partial | Return safe default (null/false/0/empty) |
| `getVoiceSearchLaunchRecognizer` | 5 | partial | Return safe default (null/false/0/empty) |
| `shouldRewriteQueryFromData` | 4 | partial | Return safe default (null/false/0/empty) |
| `getVoiceSearchLaunchWebSearch` | 4 | partial | Return safe default (null/false/0/empty) |
| `queryAfterZeroResults` | 4 | partial | Return safe default (null/false/0/empty) |
| `autoUrlDetect` | 4 | composite | throw UnsupportedOperationException |
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `shouldIncludeInGlobalSearch` | 3 | composite | throw UnsupportedOperationException |
| `shouldRewriteQueryFromText` | 3 | composite | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 13 methods that have score >= 5
2. Stub 10 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.SearchableInfo`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.SearchableInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 23 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 13 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
