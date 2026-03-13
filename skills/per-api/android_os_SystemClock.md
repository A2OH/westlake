# SKILL: android.os.SystemClock

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.SystemClock`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.SystemClock` |
| **Package** | `android.os` |
| **Total Methods** | 6 |
| **Avg Score** | 6.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (66%) |
| **Partial/Composite** | 2 (33%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `sleep` | `static void sleep(long)` | 8 | direct | easy | `sleep` | `unsigned sleep(unsigned)` |
| `setCurrentTimeMillis` | `static boolean setCurrentTimeMillis(long)` | 8 | near | easy | `getCurrentTime` | `getCurrentTime(isNano: boolean, callback: AsyncCallback<number>): void` |
| `uptimeMillis` | `static long uptimeMillis()` | 7 | near | moderate | `uptime` | `uptime(): number` |
| `elapsedRealtime` | `static long elapsedRealtime()` | 6 | near | moderate | `getStartRealtime` | `getStartRealtime(): number` |
| `currentThreadTimeMillis` | `static long currentThreadTimeMillis()` | 6 | partial | moderate | `getCurrentTime` | `getCurrentTime(isNano: boolean, callback: AsyncCallback<number>): void` |
| `elapsedRealtimeNanos` | `static long elapsedRealtimeNanos()` | 6 | partial | moderate | `getStartRealtime` | `getStartRealtime(): number` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 6 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.SystemClock`:


## Quality Gates

Before marking `android.os.SystemClock` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 6 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
