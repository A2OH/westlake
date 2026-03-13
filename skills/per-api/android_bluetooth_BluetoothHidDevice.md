# SKILL: android.bluetooth.BluetoothHidDevice

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothHidDevice`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothHidDevice` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 18 |
| **Avg Score** | 4.7 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 7 (38%) |
| **Partial/Composite** | 6 (33%) |
| **No Mapping** | 5 (27%) |
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
| `Callback` | `BluetoothHidDevice.Callback()` | 8 | direct | easy | `callback` | `callback: AsyncCallback<boolean>): void` |
| `getDevicesMatchingConnectionStates` | `java.util.List<android.bluetooth.BluetoothDevice> getDevicesMatchingConnectionStates(int[])` | 7 | near | easy | `getBtConnectionState` | `getBtConnectionState(): ProfileConnectionState` |
| `connect` | `boolean connect(android.bluetooth.BluetoothDevice)` | 7 | near | easy | `connect` | `int connect(int, const struct sockaddr *, socklen_t)` |
| `disconnect` | `boolean disconnect(android.bluetooth.BluetoothDevice)` | 7 | near | easy | `disconnect` | `disconnect(): boolean` |
| `onConnectionStateChanged` | `void onConnectionStateChanged(android.bluetooth.BluetoothDevice, int)` | 7 | near | moderate | `getBtConnectionState` | `getBtConnectionState(): ProfileConnectionState` |
| `onGetReport` | `void onGetReport(android.bluetooth.BluetoothDevice, byte, byte, int)` | 6 | partial | moderate | `getRemoteProductId` | `getRemoteProductId(deviceId: string): string` |

## Stub APIs (score < 5): 10 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onSetProtocol` | 5 | partial | Log warning + no-op |
| `onSetReport` | 4 | partial | Log warning + no-op |
| `onInterruptData` | 4 | partial | Store callback, never fire |
| `onAppStatusChanged` | 2 | composite | Store callback, never fire |
| `onVirtualCableUnplug` | 2 | composite | Store callback, never fire |
| `registerApp` | 1 | none | Return safe default (null/false/0/empty) |
| `replyReport` | 1 | none | throw UnsupportedOperationException |
| `reportError` | 1 | none | throw UnsupportedOperationException |
| `sendReport` | 1 | none | throw UnsupportedOperationException |
| `unregisterApp` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothHidDevice`:


## Quality Gates

Before marking `android.bluetooth.BluetoothHidDevice` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 18 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
