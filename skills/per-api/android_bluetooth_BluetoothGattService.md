# SKILL: android.bluetooth.BluetoothGattService

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothGattService`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothGattService` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 11 |
| **Avg Score** | 4.8 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 5 (45%) |
| **Partial/Composite** | 3 (27%) |
| **No Mapping** | 3 (27%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getType` | `int getType()` | 7 | near | easy | `type` | `type: SppType` |
| `getUuid` | `java.util.UUID getUuid()` | 7 | near | easy | `uuid` | `uuid: string` |
| `getInstanceId` | `int getInstanceId()` | 7 | near | moderate | `getState` | `getState(): BluetoothState` |
| `getIncludedServices` | `java.util.List<android.bluetooth.BluetoothGattService> getIncludedServices()` | 6 | near | moderate | `getConnectedBLEDevices` | `getConnectedBLEDevices(): Array<string>` |
| `BluetoothGattService` | `BluetoothGattService(java.util.UUID, int)` | 6 | near | moderate | `isBluetoothDiscovering` | `isBluetoothDiscovering(): boolean` |
| `getCharacteristic` | `android.bluetooth.BluetoothGattCharacteristic getCharacteristic(java.util.UUID)` | 6 | partial | moderate | `getPairState` | `getPairState(deviceId: string): BondState` |
| `getCharacteristics` | `java.util.List<android.bluetooth.BluetoothGattCharacteristic> getCharacteristics()` | 5 | partial | moderate | `getLocalAddress` | `getLocalAddress(): string` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 5 | partial | Log warning + no-op |
| `addCharacteristic` | 1 | none | Return safe default (null/false/0/empty) |
| `addService` | 1 | none | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothGattService`:


## Quality Gates

Before marking `android.bluetooth.BluetoothGattService` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
