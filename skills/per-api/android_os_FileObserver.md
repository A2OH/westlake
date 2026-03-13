# SKILL: android.os.FileObserver

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.FileObserver`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.FileObserver` |
| **Package** | `android.os` |
| **Total Methods** | 8 |
| **Avg Score** | 6.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 6 (75%) |
| **Partial/Composite** | 1 (12%) |
| **No Mapping** | 1 (12%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `onEvent` | `abstract void onEvent(int, @Nullable String)` | 10 | direct | trivial | `onEvent` | `onEvent: (info: SysEventInfo) => void` |
| `FileObserver` | `FileObserver(@NonNull java.io.File)` | 6 | near | moderate | `registerKeyObserver` | `registerKeyObserver(context: Context, name: string, domainName: string, observer: AsyncCallback<void>): boolean` |
| `FileObserver` | `FileObserver(@NonNull java.util.List<java.io.File>)` | 6 | near | moderate | `registerKeyObserver` | `registerKeyObserver(context: Context, name: string, domainName: string, observer: AsyncCallback<void>): boolean` |
| `FileObserver` | `FileObserver(@NonNull java.io.File, int)` | 6 | near | moderate | `registerKeyObserver` | `registerKeyObserver(context: Context, name: string, domainName: string, observer: AsyncCallback<void>): boolean` |
| `FileObserver` | `FileObserver(@NonNull java.util.List<java.io.File>, int)` | 6 | near | moderate | `registerKeyObserver` | `registerKeyObserver(context: Context, name: string, domainName: string, observer: AsyncCallback<void>): boolean` |
| `startWatching` | `void startWatching()` | 6 | near | moderate | `startScan` | `startScan(): void` |
| `stopWatching` | `void stopWatching()` | 6 | partial | moderate | `stopTimer` | `stopTimer(timer: number, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `finalize` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 7 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.FileObserver`:


## Quality Gates

Before marking `android.os.FileObserver` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
