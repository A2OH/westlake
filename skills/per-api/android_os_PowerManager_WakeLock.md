# SKILL: android.os.PowerManager.WakeLock

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.PowerManager.WakeLock`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.PowerManager.WakeLock` |
| **Package** | `android.os.PowerManager` |
| **Total Methods** | 7 |
| **Avg Score** | 4.7 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 4 (57%) |
| **Partial/Composite** | 1 (14%) |
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
| `isHeld` | `boolean isHeld()` | 7 | near | moderate | `isEnabled` | `readonly isEnabled?: boolean` |
| `setReferenceCounted` | `void setReferenceCounted(boolean)` | 6 | near | moderate | `setRetentionState` | `setRetentionState(docUris: Array<string>): Promise<void>` |
| `release` | `void release()` | 6 | near | moderate | `releaseInterface` | `releaseInterface(pipe: USBDevicePipe, iface: USBInterface): number` |
| `release` | `void release(int)` | 6 | near | moderate | `releaseInterface` | `releaseInterface(pipe: USBDevicePipe, iface: USBInterface): number` |
| `setWorkSource` | `void setWorkSource(android.os.WorkSource)` | 6 | partial | moderate | `setDarkMode` | `setDarkMode(mode: DarkMode, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `acquire` | 1 | none | throw UnsupportedOperationException |
| `acquire` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.os.PowerManager.WakeLock`:


## Quality Gates

Before marking `android.os.PowerManager.WakeLock` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
