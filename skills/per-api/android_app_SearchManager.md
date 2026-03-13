# SKILL: android.app.SearchManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.SearchManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.SearchManager` |
| **Package** | `android.app` |
| **Total Methods** | 8 |
| **Avg Score** | 5.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (37%) |
| **Partial/Composite** | 4 (50%) |
| **No Mapping** | 1 (12%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `triggerSearch` | `void triggerSearch(String, android.content.ComponentName, android.os.Bundle)` | 7 | near | easy | `trigger` | `trigger(agent: WantAgent, triggerInfo: TriggerInfo, callback?: AsyncCallback<CompleteData>): void` |
| `setOnDismissListener` | `void setOnDismissListener(android.app.SearchManager.OnDismissListener)` | 7 | near | moderate | `registerMissionListener` | `registerMissionListener(listener: MissionListener): number` |
| `getSearchableInfo` | `android.app.SearchableInfo getSearchableInfo(android.content.ComponentName)` | 7 | near | moderate | `getRequestInfo` | `getRequestInfo(want: Want): RequestInfo` |
| `setOnCancelListener` | `void setOnCancelListener(android.app.SearchManager.OnCancelListener)` | 6 | partial | moderate | `registerMissionListener` | `registerMissionListener(listener: MissionListener): number` |
| `startSearch` | `void startSearch(String, boolean, android.content.ComponentName, android.os.Bundle, boolean)` | 5 | partial | moderate | `startAbility` | `startAbility(parameter: StartAbilityParameter, callback: AsyncCallback<number>): void` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getSearchablesInGlobalSearch` | 5 | partial | Return safe default (null/false/0/empty) |
| `getGlobalSearchActivity` | 5 | partial | Return safe default (null/false/0/empty) |
| `stopSearch` | 1 | none | No-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.SearchManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.SearchManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
