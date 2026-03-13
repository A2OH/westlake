# SKILL: android.location.GnssStatus.Builder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.location.GnssStatus.Builder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.location.GnssStatus.Builder` |
| **Package** | `android.location.GnssStatus` |
| **Total Methods** | 6 |
| **Avg Score** | 5.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (33%) |
| **Partial/Composite** | 3 (50%) |
| **No Mapping** | 1 (16%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Callback` | `GnssStatus.Callback()` | 10 | direct | trivial | `callback` | `callback?: () => void` |
| `onStarted` | `void onStarted()` | 8 | near | easy | `isStarted` | `isStarted: boolean` |
| `onSatelliteStatusChanged` | `void onSatelliteStatusChanged(@NonNull android.location.GnssStatus)` | 6 | partial | moderate | `satelliteIds` | `satelliteIds: Array<number>` |
| `onStopped` | `void onStopped()` | 6 | partial | moderate | `onDestroy` | `onDestroy?: () => void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onFirstFix` | 5 | partial | Store callback, never fire |
| `Builder` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 4 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.location.GnssStatus.Builder`:


## Quality Gates

Before marking `android.location.GnssStatus.Builder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 6 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
