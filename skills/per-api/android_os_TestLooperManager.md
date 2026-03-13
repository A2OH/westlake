# SKILL: android.os.TestLooperManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.TestLooperManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.TestLooperManager` |
| **Package** | `android.os` |
| **Total Methods** | 7 |
| **Avg Score** | 4.9 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 3 (42%) |
| **Partial/Composite** | 2 (28%) |
| **No Mapping** | 2 (28%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `execute` | `void execute(android.os.Message)` | 10 | direct | trivial | `execute` | `execute(func: Function, ...args: Object[]): Promise<Object>` |
| `getMessageQueue` | `android.os.MessageQueue getMessageQueue()` | 6 | near | moderate | `message` | `readonly message: string` |
| `release` | `void release()` | 6 | near | moderate | `releaseInterface` | `releaseInterface(pipe: USBDevicePipe, iface: USBInterface): number` |
| `hasMessages` | `boolean hasMessages(android.os.Handler, Object, int)` | 5 | partial | moderate | `hasRight` | `hasRight(deviceName: string): boolean` |
| `hasMessages` | `boolean hasMessages(android.os.Handler, Object, Runnable)` | 5 | partial | moderate | `hasRight` | `hasRight(deviceName: string): boolean` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `next` | 1 | none | throw UnsupportedOperationException |
| `recycle` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.os.TestLooperManager`:


## Quality Gates

Before marking `android.os.TestLooperManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
