# SKILL: android.os.Message

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.Message`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.Message` |
| **Package** | `android.os` |
| **Total Methods** | 23 |
| **Avg Score** | 4.6 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 11 (47%) |
| **Partial/Composite** | 2 (8%) |
| **No Mapping** | 10 (43%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 13 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Message` | `Message()` | 10 | direct | trivial | `message` | `readonly message: string` |
| `getData` | `android.os.Bundle getData()` | 9 | direct | easy | `getDate` | `getDate(callback: AsyncCallback<Date>): void` |
| `setData` | `void setData(android.os.Bundle)` | 9 | direct | easy | `setDate` | `setDate(date: Date, callback: AsyncCallback<void>): void` |
| `getCallback` | `Runnable getCallback()` | 8 | direct | easy | `callback` | `callback?: () => void` |
| `getTarget` | `android.os.Handler getTarget()` | 8 | direct | easy | `target` | `target: USBRequestTargetType` |
| `setTarget` | `void setTarget(android.os.Handler)` | 8 | direct | easy | `target` | `target: USBRequestTargetType` |
| `getWhen` | `long getWhen()` | 7 | near | easy | `when` | `when: string` |
| `copyFrom` | `void copyFrom(android.os.Message)` | 7 | near | moderate | `from` | `from(array: number[]): Buffer` |
| `peekData` | `android.os.Bundle peekData()` | 7 | near | moderate | `data` | `data: string[]` |
| `sendToTarget` | `void sendToTarget()` | 7 | near | moderate | `target` | `target: USBRequestTargetType` |
| `setAsynchronous` | `void setAsynchronous(boolean)` | 6 | near | moderate | `setSync` | `setSync(key: string, value: string): void` |
| `writeToParcel` | `void writeToParcel(android.os.Parcel, int)` | 6 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |
| `isAsynchronous` | `boolean isAsynchronous()` | 5 | partial | moderate | `isBuffer` | `isBuffer(obj: Object): boolean` |

## Stub APIs (score < 5): 10 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `describeContents` | 1 | none | Store callback, never fire |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtain` | 1 | none | throw UnsupportedOperationException |
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

Check if these related classes are already shimmed before generating `android.os.Message`:


## Quality Gates

Before marking `android.os.Message` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 23 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 13 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
