# SKILL: android.util.EventLog.Event

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.EventLog.Event`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.EventLog.Event` |
| **Package** | `android.util.EventLog` |
| **Total Methods** | 5 |
| **Avg Score** | 7.5 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 5 (100%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getData` | `Object getData()` | 9 | direct | easy | `getDate` | `getDate(callback: AsyncCallback<Date>): void` |
| `getProcessId` | `int getProcessId()` | 7 | near | easy | `process` | `readonly process: string` |
| `getTimeNanos` | `long getTimeNanos()` | 7 | near | easy | `getTime` | `getTime(isNanoseconds?: boolean): number` |
| `getTag` | `int getTag()` | 7 | near | easy | `getState` | `getState(): BluetoothState` |
| `getThreadId` | `int getThreadId()` | 7 | near | easy | `getThreadPriority` | `getThreadPriority(v: number): number` |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.util.EventLog.Event`:


## Quality Gates

Before marking `android.util.EventLog.Event` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
