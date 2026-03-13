# SKILL: android.app.DownloadManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.DownloadManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.DownloadManager` |
| **Package** | `android.app` |
| **Total Methods** | 8 |
| **Avg Score** | 4.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (25%) |
| **Partial/Composite** | 4 (50%) |
| **No Mapping** | 2 (25%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `remove` | `int remove(long...)` | 8 | direct | easy | `remove` | `remove(uri: string): void` |
| `query` | `android.database.Cursor query(android.app.DownloadManager.Query)` | 7 | near | easy | `query` | `query(faultType: FaultType, callback: AsyncCallback<Array<FaultLogInfo>>): void` |
| `getUriForDownloadedFile` | `android.net.Uri getUriForDownloadedFile(long)` | 5 | partial | moderate | `getRunningFormInfosByFilter` | `getRunningFormInfosByFilter(formProviderFilter: formInfo.FormProviderFilter): Promise<Array<formInfo.RunningFormInfo>>` |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getMaxBytesOverMobile` | 5 | partial | Return safe default (null/false/0/empty) |
| `getRecommendedMaxBytesOverMobile` | 5 | partial | Return safe default (null/false/0/empty) |
| `getMimeTypeForDownloadedFile` | 4 | partial | Return safe default (null/false/0/empty) |
| `enqueue` | 1 | none | throw UnsupportedOperationException |
| `openDownloadedFile` | 1 | none | Return dummy instance / no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 5 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.DownloadManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.DownloadManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
