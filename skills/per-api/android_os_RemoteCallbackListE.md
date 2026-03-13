# SKILL: android.os.RemoteCallbackList<E

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.RemoteCallbackList<E`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.RemoteCallbackList<E` |
| **Package** | `android.os` |
| **Total Methods** | 14 |
| **Avg Score** | 7.4 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 12 (85%) |
| **Partial/Composite** | 2 (14%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 14 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `kill` | `void kill()` | 10 | direct | trivial | `kill` | `kill(signal: number, pid: number): boolean` |
| `register` | `boolean register(E)` | 10 | direct | trivial | `register` | `register(callback: AsyncCallback<number>): void` |
| `register` | `boolean register(E, Object)` | 10 | direct | trivial | `register` | `register(callback: AsyncCallback<number>): void` |
| `unregister` | `boolean unregister(E)` | 10 | direct | trivial | `unregister` | `unregister(token: number, callback: AsyncCallback<void>): void` |
| `onCallbackDied` | `void onCallbackDied(E)` | 7 | near | easy | `callback` | `callback?: () => void` |
| `onCallbackDied` | `void onCallbackDied(E, Object)` | 7 | near | easy | `callback` | `callback?: () => void` |
| `finishBroadcast` | `void finishBroadcast()` | 7 | near | moderate | `finishTrace` | `finishTrace(name: string, taskId: number): void` |
| `getRegisteredCallbackItem` | `E getRegisteredCallbackItem(int)` | 7 | near | moderate | `unregisterVsyncCallback` | `unregisterVsyncCallback(): void` |
| `getRegisteredCallbackCount` | `int getRegisteredCallbackCount()` | 7 | near | moderate | `unregisterVsyncCallback` | `unregisterVsyncCallback(): void` |
| `getRegisteredCallbackCookie` | `Object getRegisteredCallbackCookie(int)` | 6 | near | moderate | `unregisterVsyncCallback` | `unregisterVsyncCallback(): void` |
| `RemoteCallbackList` | `RemoteCallbackList()` | 6 | near | moderate | `callback` | `callback?: () => void` |
| `getBroadcastItem` | `E getBroadcastItem(int)` | 6 | near | moderate | `getRealActiveTime` | `getRealActiveTime(isNano: boolean, callback: AsyncCallback<number>): void` |
| `getBroadcastCookie` | `Object getBroadcastCookie(int)` | 6 | partial | moderate | `getPastCpuTime` | `getPastCpuTime(): number` |
| `beginBroadcast` | `int beginBroadcast()` | 5 | partial | moderate | `begin` | `begin(name: string, flags?: number): HiTraceId` |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.os.RemoteCallbackList<E`:


## Quality Gates

Before marking `android.os.RemoteCallbackList<E` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 14 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
