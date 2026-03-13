# SKILL: android.app.Instrumentation.ActivityResult

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.Instrumentation.ActivityResult`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.Instrumentation.ActivityResult` |
| **Package** | `android.app.Instrumentation` |
| **Total Methods** | 3 |
| **Avg Score** | 6.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (33%) |
| **Partial/Composite** | 2 (66%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `ActivityResult` | `Instrumentation.ActivityResult(int, android.content.Intent)` | 7 | near | moderate | `startAbilityForResult` | `startAbilityForResult(parameter: StartAbilityParameter, callback: AsyncCallback<AbilityResult>): void` |
| `getResultCode` | `int getResultCode()` | 6 | partial | moderate | `getRequestInfo` | `getRequestInfo(want: Want): RequestInfo` |
| `getResultData` | `android.content.Intent getResultData()` | 5 | partial | moderate | `getUid` | `getUid(agent: WantAgent, callback: AsyncCallback<number>): void` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.Instrumentation.ActivityResult`:


## Quality Gates

Before marking `android.app.Instrumentation.ActivityResult` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 3 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
