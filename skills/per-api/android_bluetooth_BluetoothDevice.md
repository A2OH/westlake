# SKILL: android.bluetooth.BluetoothDevice

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothDevice`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothDevice` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 8 |
| **Avg Score** | 5.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 5 (62%) |
| **Partial/Composite** | 2 (25%) |
| **No Mapping** | 1 (12%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getAddress` | `String getAddress()` | 8 | direct | easy | `getLocalAddress` | `getLocalAddress(): string` |
| `connectGatt` | `android.bluetooth.BluetoothGatt connectGatt(android.content.Context, boolean, android.bluetooth.BluetoothGattCallback)` | 7 | near | moderate | `sppConnect` | `sppConnect(device: string, option: SppOption, callback: AsyncCallback<number>): void` |
| `connectGatt` | `android.bluetooth.BluetoothGatt connectGatt(android.content.Context, boolean, android.bluetooth.BluetoothGattCallback, int)` | 7 | near | moderate | `sppConnect` | `sppConnect(device: string, option: SppOption, callback: AsyncCallback<number>): void` |
| `connectGatt` | `android.bluetooth.BluetoothGatt connectGatt(android.content.Context, boolean, android.bluetooth.BluetoothGattCallback, int, int)` | 7 | near | moderate | `sppConnect` | `sppConnect(device: string, option: SppOption, callback: AsyncCallback<number>): void` |
| `connectGatt` | `android.bluetooth.BluetoothGatt connectGatt(android.content.Context, boolean, android.bluetooth.BluetoothGattCallback, int, int, android.os.Handler)` | 7 | near | moderate | `sppConnect` | `sppConnect(device: string, option: SppOption, callback: AsyncCallback<number>): void` |
| `setPin` | `boolean setPin(byte[])` | 5 | partial | moderate | `setDevicePinCode` | `setDevicePinCode(deviceId: string, code: string, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 5 | partial | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 6 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothDevice`:


## Quality Gates

Before marking `android.bluetooth.BluetoothDevice` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
