# SKILL: android.content.SyncRequest.Builder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.SyncRequest.Builder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.SyncRequest.Builder` |
| **Package** | `android.content.SyncRequest` |
| **Total Methods** | 13 |
| **Avg Score** | 4.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (7%) |
| **Partial/Composite** | 10 (76%) |
| **No Mapping** | 2 (15%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `setExtras` | `android.content.SyncRequest.Builder setExtras(android.os.Bundle)` | 6 | near | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `setNoRetry` | `android.content.SyncRequest.Builder setNoRetry(boolean)` | 6 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `setIgnoreSettings` | `android.content.SyncRequest.Builder setIgnoreSettings(boolean)` | 6 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `setRequiresCharging` | `android.content.SyncRequest.Builder setRequiresCharging(boolean)` | 5 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `syncOnce` | `android.content.SyncRequest.Builder syncOnce()` | 5 | partial | moderate | `autoSync` | `autoSync?: boolean` |

## Stub APIs (score < 5): 8 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setExpedited` | 5 | partial | Log warning + no-op |
| `setSyncAdapter` | 5 | partial | Log warning + no-op |
| `setDisallowMetered` | 4 | partial | Return safe default (null/false/0/empty) |
| `setManual` | 4 | partial | Log warning + no-op |
| `setIgnoreBackoff` | 4 | partial | Log warning + no-op |
| `syncPeriodic` | 4 | partial | throw UnsupportedOperationException |
| `Builder` | 1 | none | throw UnsupportedOperationException |
| `build` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 8 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.content.SyncRequest.Builder`:


## Quality Gates

Before marking `android.content.SyncRequest.Builder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 13 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
