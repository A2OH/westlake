# SKILL: android.content.OperationApplicationException

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.OperationApplicationException`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.OperationApplicationException` |
| **Package** | `android.content` |
| **Total Methods** | 7 |
| **Avg Score** | 5.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 7 (100%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `OperationApplicationException` | `OperationApplicationException()` | 6 | partial | moderate | `getApplicationQuickFixInfo` | `getApplicationQuickFixInfo(bundleName: string, callback: AsyncCallback<ApplicationQuickFixInfo>): void` |
| `OperationApplicationException` | `OperationApplicationException(String)` | 6 | partial | moderate | `getApplicationQuickFixInfo` | `getApplicationQuickFixInfo(bundleName: string, callback: AsyncCallback<ApplicationQuickFixInfo>): void` |
| `OperationApplicationException` | `OperationApplicationException(String, Throwable)` | 6 | partial | moderate | `getApplicationQuickFixInfo` | `getApplicationQuickFixInfo(bundleName: string, callback: AsyncCallback<ApplicationQuickFixInfo>): void` |
| `OperationApplicationException` | `OperationApplicationException(Throwable)` | 6 | partial | moderate | `getApplicationQuickFixInfo` | `getApplicationQuickFixInfo(bundleName: string, callback: AsyncCallback<ApplicationQuickFixInfo>): void` |
| `OperationApplicationException` | `OperationApplicationException(int)` | 6 | partial | moderate | `getApplicationQuickFixInfo` | `getApplicationQuickFixInfo(bundleName: string, callback: AsyncCallback<ApplicationQuickFixInfo>): void` |
| `OperationApplicationException` | `OperationApplicationException(String, int)` | 6 | partial | moderate | `getApplicationQuickFixInfo` | `getApplicationQuickFixInfo(bundleName: string, callback: AsyncCallback<ApplicationQuickFixInfo>): void` |
| `getNumSuccessfulYieldPoints` | `int getNumSuccessfulYieldPoints()` | 5 | partial | moderate | `successful` | `successful: number` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 7 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.content.OperationApplicationException`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.OperationApplicationException` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
