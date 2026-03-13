# SKILL: android.os.CancellationSignal

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.CancellationSignal`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.CancellationSignal` |
| **Package** | `android.os` |
| **Total Methods** | 5 |
| **Avg Score** | 5.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (60%) |
| **Partial/Composite** | 1 (20%) |
| **No Mapping** | 1 (20%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `cancel` | `void cancel()` | 10 | direct | trivial | `cancel` | `cancel(id: number, callback: AsyncCallback<void>): void` |
| `isCanceled` | `boolean isCanceled()` | 7 | near | moderate | `isLocationEnabled` | `isLocationEnabled(): boolean` |
| `setOnCancelListener` | `void setOnCancelListener(android.os.CancellationSignal.OnCancelListener)` | 6 | near | moderate | `cancelReminder` | `cancelReminder(reminderId: number, callback: AsyncCallback<void>): void` |
| `CancellationSignal` | `CancellationSignal()` | 5 | partial | moderate | `getSignalLevel` | `getSignalLevel(rssi: number, band: number): number` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `throwIfCanceled` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 4 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.CancellationSignal`:


## Quality Gates

Before marking `android.os.CancellationSignal` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
