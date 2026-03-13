# SKILL: android.hardware.HardwareBuffer

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.hardware.HardwareBuffer`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.hardware.HardwareBuffer` |
| **Package** | `android.hardware` |
| **Total Methods** | 10 |
| **Avg Score** | 7.7 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 8 (80%) |
| **Partial/Composite** | 1 (10%) |
| **No Mapping** | 1 (10%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 9 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `close` | `void close()` | 10 | direct | trivial | `close` | `close(fd: number): Promise<void>` |
| `isClosed` | `boolean isClosed()` | 10 | direct | trivial | `isClosed` | `isClosed: boolean` |
| `isSupported` | `static boolean isSupported(@IntRange(from=1) int, @IntRange(from=1) int, int, @IntRange(from=1) int, long)` | 10 | direct | trivial | `isSupported` | `isSupported(type: RunningLockType): boolean` |
| `getHeight` | `int getHeight()` | 9 | direct | easy | `getMinHeight` | `getMinHeight(callback: AsyncCallback<number>): void` |
| `getUsage` | `long getUsage()` | 8 | direct | easy | `getCpuUsage` | `getCpuUsage(): number` |
| `getWidth` | `int getWidth()` | 8 | direct | easy | `getMinWidth` | `getMinWidth(callback: AsyncCallback<number>): void` |
| `getFormat` | `int getFormat()` | 8 | direct | easy | `format` | `format(format: string, ...args: Object[]): string` |
| `getLayers` | `int getLayers()` | 7 | near | moderate | `getParams` | `getParams(): Object` |
| `writeToParcel` | `void writeToParcel(android.os.Parcel, int)` | 6 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.hardware.HardwareBuffer`:


## Quality Gates

Before marking `android.hardware.HardwareBuffer` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 9 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
