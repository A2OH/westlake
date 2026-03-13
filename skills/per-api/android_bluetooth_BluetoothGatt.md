# SKILL: android.bluetooth.BluetoothGatt

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothGatt`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothGatt` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 23 |
| **Avg Score** | 4.9 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 9 (39%) |
| **Partial/Composite** | 9 (39%) |
| **No Mapping** | 5 (21%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 12 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getConnectedDevices` | `java.util.List<android.bluetooth.BluetoothDevice> getConnectedDevices()` | 10 | direct | trivial | `getConnectedBLEDevices` | `getConnectedBLEDevices(): Array<string>` |
| `getConnectionState` | `int getConnectionState(android.bluetooth.BluetoothDevice)` | 9 | direct | trivial | `getBtConnectionState` | `getBtConnectionState(): ProfileConnectionState` |
| `close` | `void close()` | 8 | direct | easy | `close` | `close(file: number | File): Promise<void>` |
| `getDevice` | `android.bluetooth.BluetoothDevice getDevice()` | 7 | near | easy | `getPairedDevices` | `getPairedDevices(): Array<string>` |
| `getDevicesMatchingConnectionStates` | `java.util.List<android.bluetooth.BluetoothDevice> getDevicesMatchingConnectionStates(int[])` | 7 | near | easy | `getBtConnectionState` | `getBtConnectionState(): ProfileConnectionState` |
| `connect` | `boolean connect()` | 7 | near | easy | `connect` | `int connect(int, const struct sockaddr *, socklen_t)` |
| `disconnect` | `void disconnect()` | 7 | near | easy | `disconnect` | `disconnect(): boolean` |
| `getServices` | `java.util.List<android.bluetooth.BluetoothGattService> getServices()` | 7 | near | moderate | `getPairedDevices` | `getPairedDevices(): Array<string>` |
| `getService` | `android.bluetooth.BluetoothGattService getService(java.util.UUID)` | 6 | near | moderate | `getPairedDevices` | `getPairedDevices(): Array<string>` |
| `requestConnectionPriority` | `boolean requestConnectionPriority(int)` | 6 | partial | moderate | `getBtConnectionState` | `getBtConnectionState(): ProfileConnectionState` |
| `readRemoteRssi` | `boolean readRemoteRssi()` | 6 | partial | moderate | `getRemoteProductId` | `getRemoteProductId(deviceId: string): string` |
| `setCharacteristicNotification` | `boolean setCharacteristicNotification(android.bluetooth.BluetoothGattCharacteristic, boolean)` | 6 | partial | moderate | `setDevicePairingConfirmation` | `setDevicePairingConfirmation(deviceId: string, accept: boolean): void` |

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeDescriptor` | 4 | partial | Log warning + no-op |
| `abortReliableWrite` | 4 | composite | Log warning + no-op |
| `beginReliableWrite` | 4 | composite | Log warning + no-op |
| `writeCharacteristic` | 4 | composite | Return safe default (null/false/0/empty) |
| `executeReliableWrite` | 4 | composite | Log warning + no-op |
| `setPreferredPhy` | 3 | composite | Log warning + no-op |
| `discoverServices` | 1 | none | Return safe default (null/false/0/empty) |
| `readCharacteristic` | 1 | none | Return safe default (null/false/0/empty) |
| `readDescriptor` | 1 | none | Return safe default (null/false/0/empty) |
| `readPhy` | 1 | none | Return safe default (null/false/0/empty) |
| `requestMtu` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothGatt`:


## Quality Gates

Before marking `android.bluetooth.BluetoothGatt` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 23 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 12 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
