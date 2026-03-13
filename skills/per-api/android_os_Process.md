# SKILL: android.os.Process

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.Process`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.Process` |
| **Package** | `android.os` |
| **Total Methods** | 19 |
| **Avg Score** | 7.9 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 15 (78%) |
| **Partial/Composite** | 4 (21%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 18 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Process` | `Process()` | 10 | direct | trivial | `process` | `readonly process: string` |
| `getThreadPriority` | `static final int getThreadPriority(int) throws java.lang.IllegalArgumentException` | 10 | direct | trivial | `getThreadPriority` | `getThreadPriority(v: number): number` |
| `getUidForName` | `static final int getUidForName(String)` | 10 | direct | trivial | `getUidForName` | `getUidForName(v: string): number` |
| `is64Bit` | `static final boolean is64Bit()` | 10 | direct | trivial | `is64Bit` | `is64Bit(): boolean` |
| `setThreadPriority` | `static final void setThreadPriority(int, int) throws java.lang.IllegalArgumentException, java.lang.SecurityException` | 9 | direct | trivial | `getThreadPriority` | `getThreadPriority(v: number): number` |
| `setThreadPriority` | `static final void setThreadPriority(int) throws java.lang.IllegalArgumentException, java.lang.SecurityException` | 9 | direct | trivial | `getThreadPriority` | `getThreadPriority(v: number): number` |
| `getGidForName` | `static final int getGidForName(String)` | 9 | direct | trivial | `getUidForName` | `getUidForName(v: string): number` |
| `getStartElapsedRealtime` | `static final long getStartElapsedRealtime()` | 8 | direct | easy | `getStartRealtime` | `getStartRealtime(): number` |
| `killProcess` | `static final void killProcess(int)` | 8 | near | easy | `process` | `readonly process: string` |
| `getElapsedCpuTime` | `static final long getElapsedCpuTime()` | 8 | near | easy | `getPastCpuTime` | `getPastCpuTime(): number` |
| `isApplicationUid` | `static boolean isApplicationUid(int)` | 8 | near | easy | `applicationInfo` | `readonly applicationInfo: ApplicationInfo` |
| `myPid` | `static final int myPid()` | 8 | near | easy | `pid` | `readonly pid: number` |
| `myUid` | `static final int myUid()` | 8 | near | easy | `uid` | `uid: number` |
| `isIsolated` | `static final boolean isIsolated()` | 7 | near | easy | `isIsolatedProcess` | `isIsolatedProcess(): boolean` |
| `getStartUptimeMillis` | `static final long getStartUptimeMillis()` | 7 | near | moderate | `getStartRealtime` | `getStartRealtime(): number` |
| `sendSignal` | `static final void sendSignal(int, int)` | 6 | partial | moderate | `getSignalLevel` | `getSignalLevel(rssi: number, band: number): number` |
| `getExclusiveCores` | `static final int[] getExclusiveCores()` | 6 | partial | moderate | `getRestorer` | `getRestorer(): Restorer` |
| `myUserHandle` | `static android.os.UserHandle myUserHandle()` | 6 | partial | moderate | `userId` | `userId: number` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `myTid` | 4 | composite | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.os.Process`:


## Quality Gates

Before marking `android.os.Process` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 19 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 18 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
