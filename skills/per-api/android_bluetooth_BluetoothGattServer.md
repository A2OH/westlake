# SKILL: android.bluetooth.BluetoothGattServer

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothGattServer`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothGattServer` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 15 |
| **Avg Score** | 4.7 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 8 (53%) |
| **Partial/Composite** | 1 (6%) |
| **No Mapping** | 6 (40%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getConnectedDevices` | `java.util.List<android.bluetooth.BluetoothDevice> getConnectedDevices()` | 10 | direct | trivial | `getConnectedBLEDevices` | `getConnectedBLEDevices(): Array<string>` |
| `getConnectionState` | `int getConnectionState(android.bluetooth.BluetoothDevice)` | 9 | direct | trivial | `getBtConnectionState` | `getBtConnectionState(): ProfileConnectionState` |
| `close` | `void close()` | 8 | direct | easy | `close` | `close(file: number | File): Promise<void>` |
| `getDevicesMatchingConnectionStates` | `java.util.List<android.bluetooth.BluetoothDevice> getDevicesMatchingConnectionStates(int[])` | 7 | near | easy | `getBtConnectionState` | `getBtConnectionState(): ProfileConnectionState` |
| `connect` | `boolean connect(android.bluetooth.BluetoothDevice, boolean)` | 7 | near | easy | `connect` | `int connect(int, const struct sockaddr *, socklen_t)` |
| `getServices` | `java.util.List<android.bluetooth.BluetoothGattService> getServices()` | 7 | near | moderate | `getPairedDevices` | `getPairedDevices(): Array<string>` |
| `getService` | `android.bluetooth.BluetoothGattService getService(java.util.UUID)` | 6 | near | moderate | `getPairedDevices` | `getPairedDevices(): Array<string>` |
| `cancelConnection` | `void cancelConnection(android.bluetooth.BluetoothDevice)` | 6 | near | moderate | `getBtConnectionState` | `getBtConnectionState(): ProfileConnectionState` |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setPreferredPhy` | 3 | composite | Log warning + no-op |
| `addService` | 1 | none | Log warning + no-op |
| `clearServices` | 1 | none | throw UnsupportedOperationException |
| `notifyCharacteristicChanged` | 1 | none | Return safe default (null/false/0/empty) |
| `readPhy` | 1 | none | Return safe default (null/false/0/empty) |
| `removeService` | 1 | none | Log warning + no-op |
| `sendResponse` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothGattServer`:


## Quality Gates

Before marking `android.bluetooth.BluetoothGattServer` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 15 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
