# SKILL: android.app.Instrumentation.ActivityMonitor

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.Instrumentation.ActivityMonitor`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.Instrumentation.ActivityMonitor` |
| **Package** | `android.app.Instrumentation` |
| **Total Methods** | 11 |
| **Avg Score** | 4.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (27%) |
| **Partial/Composite** | 5 (45%) |
| **No Mapping** | 3 (27%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `onStartActivity` | `android.app.Instrumentation.ActivityResult onStartActivity(android.content.Intent)` | 7 | near | easy | `startAbility` | `startAbility(parameter: StartAbilityParameter, callback: AsyncCallback<number>): void` |
| `getHits` | `final int getHits()` | 7 | near | moderate | `getId` | `getId(uri: string): number` |
| `getResult` | `final android.app.Instrumentation.ActivityResult getResult()` | 6 | near | moderate | `getRequestInfo` | `getRequestInfo(want: Want): RequestInfo` |
| `getFilter` | `final android.content.IntentFilter getFilter()` | 6 | partial | moderate | `getId` | `getId(uri: string): number` |
| `getLastActivity` | `final android.app.Activity getLastActivity()` | 5 | partial | moderate | `getWantAgent` | `getWantAgent(info: WantAgentInfo, callback: AsyncCallback<WantAgent>): void` |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isBlocking` | 5 | partial | Return safe default (null/false/0/empty) |
| `waitForActivityWithTimeout` | 4 | partial | throw UnsupportedOperationException |
| `waitForActivity` | 4 | composite | throw UnsupportedOperationException |
| `ActivityMonitor` | 1 | none | Store callback, never fire |
| `ActivityMonitor` | 1 | none | Store callback, never fire |
| `ActivityMonitor` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 6 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.Instrumentation.ActivityMonitor`:


## Quality Gates

Before marking `android.app.Instrumentation.ActivityMonitor` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
