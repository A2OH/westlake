# SKILL: android.bluetooth.BluetoothHeadset

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothHeadset`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothHeadset` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 7 |
| **Avg Score** | 6.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (42%) |
| **Partial/Composite** | 4 (57%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getConnectedDevices` | `java.util.List<android.bluetooth.BluetoothDevice> getConnectedDevices()` | 10 | direct | trivial | `getConnectedBLEDevices` | `getConnectedBLEDevices(): Array<string>` |
| `getConnectionState` | `int getConnectionState(android.bluetooth.BluetoothDevice)` | 9 | direct | trivial | `getBtConnectionState` | `getBtConnectionState(): ProfileConnectionState` |
| `getDevicesMatchingConnectionStates` | `java.util.List<android.bluetooth.BluetoothDevice> getDevicesMatchingConnectionStates(int[])` | 7 | near | easy | `getBtConnectionState` | `getBtConnectionState(): ProfileConnectionState` |
| `startVoiceRecognition` | `boolean startVoiceRecognition(android.bluetooth.BluetoothDevice)` | 6 | partial | moderate | `startAdvertising` | `startAdvertising(setting: AdvertiseSetting, advData: AdvertiseData, advResponse?: AdvertiseData): void` |
| `stopVoiceRecognition` | `boolean stopVoiceRecognition(android.bluetooth.BluetoothDevice)` | 6 | partial | moderate | `stopAdvertising` | `stopAdvertising(): void` |
| `sendVendorSpecificResultCode` | `boolean sendVendorSpecificResultCode(android.bluetooth.BluetoothDevice, String, String)` | 5 | partial | moderate | `setDevicePinCode` | `setDevicePinCode(deviceId: string, code: string, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isAudioConnected` | 5 | partial | Return dummy instance / no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 6 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothHeadset`:


## Quality Gates

Before marking `android.bluetooth.BluetoothHeadset` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
