# SKILL: android.os.TokenWatcher

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.TokenWatcher`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.TokenWatcher` |
| **Package** | `android.os` |
| **Total Methods** | 9 |
| **Avg Score** | 3.8 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 3 (33%) |
| **Partial/Composite** | 2 (22%) |
| **No Mapping** | 4 (44%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `TokenWatcher` | `TokenWatcher(android.os.Handler, String)` | 7 | near | easy | `createWatcher` | `createWatcher(filename: string, events: number, callback: AsyncCallback<number>): Watcher` |
| `isAcquired` | `boolean isAcquired()` | 7 | near | moderate | `isActive` | `isActive(): boolean` |
| `release` | `void release(android.os.IBinder)` | 6 | near | moderate | `releaseInterface` | `releaseInterface(pipe: USBDevicePipe, iface: USBInterface): number` |
| `dump` | `void dump()` | 5 | partial | moderate | `dumpHeapData` | `dumpHeapData(filename: string): void` |
| `dump` | `void dump(java.io.PrintWriter)` | 5 | partial | moderate | `dumpHeapData` | `dumpHeapData(filename: string): void` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `acquire` | 1 | none | throw UnsupportedOperationException |
| `acquired` | 1 | none | throw UnsupportedOperationException |
| `cleanup` | 1 | none | throw UnsupportedOperationException |
| `released` | 1 | none | No-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.os.TokenWatcher`:


## Quality Gates

Before marking `android.os.TokenWatcher` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
