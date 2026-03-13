# SKILL: android.content.ComponentName

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.ComponentName`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.ComponentName` |
| **Package** | `android.content` |
| **Total Methods** | 12 |
| **Avg Score** | 4.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 10 (83%) |
| **No Mapping** | 2 (16%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getShortClassName` | `String getShortClassName()` | 6 | partial | moderate | `getStorage` | `getStorage(path: string, callback: AsyncCallback<Storage>): void` |
| `readFromParcel` | `static android.content.ComponentName readFromParcel(android.os.Parcel)` | 6 | partial | moderate | `removeStorageFromCache` | `removeStorageFromCache(path: string, callback: AsyncCallback<void>): void` |
| `ComponentName` | `ComponentName(@NonNull String, @NonNull String)` | 5 | partial | moderate | `bundleName` | `bundleName: string` |
| `ComponentName` | `ComponentName(@NonNull android.content.Context, @NonNull String)` | 5 | partial | moderate | `bundleName` | `bundleName: string` |
| `ComponentName` | `ComponentName(@NonNull android.content.Context, @NonNull Class<?>)` | 5 | partial | moderate | `bundleName` | `bundleName: string` |
| `ComponentName` | `ComponentName(android.os.Parcel)` | 5 | partial | moderate | `bundleName` | `bundleName: string` |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 4 | partial | Log warning + no-op |
| `writeToParcel` | 4 | partial | Log warning + no-op |
| `compareTo` | 4 | partial | throw UnsupportedOperationException |
| `toShortString` | 4 | partial | throw UnsupportedOperationException |
| `clone` | 1 | none | Store callback, never fire |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 6 methods that have score >= 5
2. Stub 6 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.content.ComponentName`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.ComponentName` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 12 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
