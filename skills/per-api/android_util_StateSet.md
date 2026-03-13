# SKILL: android.util.StateSet

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.StateSet`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.StateSet` |
| **Package** | `android.util` |
| **Total Methods** | 5 |
| **Avg Score** | 5.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (20%) |
| **Partial/Composite** | 4 (80%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `trimStateSet` | `static int[] trimStateSet(int[], int)` | 6 | near | moderate | `getState` | `getState(): BluetoothState` |
| `stateSetMatches` | `static boolean stateSetMatches(int[], int[])` | 6 | partial | moderate | `stateOccurredTime` | `stateOccurredTime?: number` |
| `stateSetMatches` | `static boolean stateSetMatches(int[], int)` | 6 | partial | moderate | `stateOccurredTime` | `stateOccurredTime?: number` |
| `isWildCard` | `static boolean isWildCard(int[])` | 5 | partial | moderate | `isStarted` | `isStarted: boolean` |
| `dump` | `static String dump(int[])` | 5 | partial | moderate | `dumpHeapData` | `dumpHeapData(filename: string): void` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.util.StateSet`:


## Quality Gates

Before marking `android.util.StateSet` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
