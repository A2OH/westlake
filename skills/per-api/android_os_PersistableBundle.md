# SKILL: android.os.PersistableBundle

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.PersistableBundle`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.PersistableBundle` |
| **Package** | `android.os` |
| **Total Methods** | 9 |
| **Avg Score** | 5.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 6 (66%) |
| **Partial/Composite** | 1 (11%) |
| **No Mapping** | 2 (22%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `writeToStream` | `void writeToStream(@NonNull java.io.OutputStream) throws java.io.IOException` | 7 | near | easy | `createStream` | `createStream(path: string, mode: string): Promise<Stream>` |
| `deepCopy` | `android.os.PersistableBundle deepCopy()` | 7 | near | moderate | `copy` | `copy(logType: string, logName: string, dest: string): Promise<void>` |
| `putPersistableBundle` | `void putPersistableBundle(@Nullable String, @Nullable android.os.PersistableBundle)` | 6 | near | moderate | `publishAsBundle` | `publishAsBundle(request: NotificationRequest,
    representativeBundle: string,
    userId: number,
    callback: AsyncCallback<void>): void` |
| `PersistableBundle` | `PersistableBundle()` | 6 | near | moderate | `publishAsBundle` | `publishAsBundle(request: NotificationRequest,
    representativeBundle: string,
    userId: number,
    callback: AsyncCallback<void>): void` |
| `PersistableBundle` | `PersistableBundle(int)` | 6 | near | moderate | `publishAsBundle` | `publishAsBundle(request: NotificationRequest,
    representativeBundle: string,
    userId: number,
    callback: AsyncCallback<void>): void` |
| `PersistableBundle` | `PersistableBundle(android.os.PersistableBundle)` | 6 | near | moderate | `publishAsBundle` | `publishAsBundle(request: NotificationRequest,
    representativeBundle: string,
    userId: number,
    callback: AsyncCallback<void>): void` |
| `writeToParcel` | `void writeToParcel(android.os.Parcel, int)` | 6 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `clone` | 1 | none | Store callback, never fire |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 7 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.PersistableBundle`:


## Quality Gates

Before marking `android.os.PersistableBundle` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
