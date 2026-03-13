# SKILL: android.bluetooth.BluetoothServerSocket

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothServerSocket`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothServerSocket` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 4 |
| **Avg Score** | 7.4 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 3 (75%) |
| **Partial/Composite** | 1 (25%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `accept` | `android.bluetooth.BluetoothSocket accept() throws java.io.IOException` | 8 | direct | easy | `sppAccept` | `sppAccept(serverSocket: number, callback: AsyncCallback<number>): void` |
| `accept` | `android.bluetooth.BluetoothSocket accept(int) throws java.io.IOException` | 8 | direct | easy | `sppAccept` | `sppAccept(serverSocket: number, callback: AsyncCallback<number>): void` |
| `close` | `void close() throws java.io.IOException` | 8 | direct | easy | `close` | `close(file: number | File): Promise<void>` |
| `getPsm` | `int getPsm()` | 6 | partial | moderate | `getState` | `getState(): BluetoothState` |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothServerSocket`:


## Quality Gates

Before marking `android.bluetooth.BluetoothServerSocket` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
