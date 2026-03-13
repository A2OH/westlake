# SKILL: android.os.CountDownTimer

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.CountDownTimer`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.CountDownTimer` |
| **Package** | `android.os` |
| **Total Methods** | 5 |
| **Avg Score** | 7.3 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 3 (60%) |
| **Partial/Composite** | 2 (40%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `cancel` | `final void cancel()` | 10 | direct | trivial | `cancel` | `cancel(id: number, callback: AsyncCallback<void>): void` |
| `onFinish` | `abstract void onFinish()` | 9 | direct | easy | `finish` | `finish(): void` |
| `start` | `final android.os.CountDownTimer start()` | 7 | near | easy | `start` | `start(sinkDeviceDescriptor: string, srcInputDeviceId: number, callback: AsyncCallback<void>): void` |
| `CountDownTimer` | `CountDownTimer(long, long)` | 6 | partial | moderate | `createTimer` | `createTimer(options: TimerOptions, callback: AsyncCallback<number>): void` |
| `onTick` | `abstract void onTick(long)` | 5 | partial | moderate | `onTrigger` | `onTrigger?: (curRow: number, curSize: number, holder: AppEventPackageHolder) => void` |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.os.CountDownTimer`:


## Quality Gates

Before marking `android.os.CountDownTimer` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
