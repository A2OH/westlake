# SKILL: android.os.Looper

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.Looper`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.Looper` |
| **Package** | `android.os` |
| **Total Methods** | 8 |
| **Avg Score** | 5.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 5 (62%) |
| **Partial/Composite** | 1 (12%) |
| **No Mapping** | 2 (25%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `loop` | `static void loop()` | 10 | direct | trivial | `loop` | `loop: boolean` |
| `prepare` | `static void prepare()` | 10 | direct | trivial | `prepare` | `prepare(callback: AsyncCallback<void>): void` |
| `isCurrentThread` | `boolean isCurrentThread()` | 7 | near | moderate | `currentDataRole` | `currentDataRole: number` |
| `getMainLooper` | `static android.os.Looper getMainLooper()` | 6 | near | moderate | `getFile` | `getFile(wallpaperType: WallpaperType, callback: AsyncCallback<number>): void` |
| `setMessageLogging` | `void setMessageLogging(@Nullable android.util.Printer)` | 6 | near | moderate | `statusMessage` | `statusMessage: string` |
| `dump` | `void dump(@NonNull android.util.Printer, @NonNull String)` | 5 | partial | moderate | `dumpHeapData` | `dumpHeapData(filename: string): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `quit` | 1 | none | throw UnsupportedOperationException |
| `quitSafely` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 6 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.Looper`:


## Quality Gates

Before marking `android.os.Looper` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
