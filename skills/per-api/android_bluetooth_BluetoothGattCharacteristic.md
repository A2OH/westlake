# SKILL: android.bluetooth.BluetoothGattCharacteristic

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothGattCharacteristic`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothGattCharacteristic` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 21 |
| **Avg Score** | 5.4 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 13 (61%) |
| **Partial/Composite** | 6 (28%) |
| **No Mapping** | 2 (9%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 18 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getUuid` | `java.util.UUID getUuid()` | 7 | near | easy | `uuid` | `uuid: string` |
| `getInstanceId` | `int getInstanceId()` | 7 | near | moderate | `getState` | `getState(): BluetoothState` |
| `getFloatValue` | `Float getFloatValue(int, int)` | 6 | near | moderate | `getLocalName` | `getLocalName(): string` |
| `getStringValue` | `String getStringValue(int)` | 6 | near | moderate | `getState` | `getState(): BluetoothState` |
| `getIntValue` | `Integer getIntValue(int, int)` | 6 | near | moderate | `getState` | `getState(): BluetoothState` |
| `getValue` | `byte[] getValue()` | 6 | near | moderate | `getState` | `getState(): BluetoothState` |
| `getProperties` | `int getProperties()` | 6 | near | moderate | `getPairedDevices` | `getPairedDevices(): Array<string>` |
| `getService` | `android.bluetooth.BluetoothGattService getService()` | 6 | near | moderate | `getPairedDevices` | `getPairedDevices(): Array<string>` |
| `setValue` | `boolean setValue(byte[])` | 6 | near | moderate | `setLocalName` | `setLocalName(name: string): void` |
| `setValue` | `boolean setValue(int, int, int)` | 6 | near | moderate | `setLocalName` | `setLocalName(name: string): void` |
| `setValue` | `boolean setValue(int, int, int, int)` | 6 | near | moderate | `setLocalName` | `setLocalName(name: string): void` |
| `setValue` | `boolean setValue(String)` | 6 | near | moderate | `setLocalName` | `setLocalName(name: string): void` |
| `setWriteType` | `void setWriteType(int)` | 6 | near | moderate | `sppWrite` | `sppWrite(clientSocket: number, data: ArrayBuffer): boolean` |
| `getWriteType` | `int getWriteType()` | 5 | partial | moderate | `getProfile` | `getProfile(profileId: ProfileId): A2dpSourceProfile | HandsFreeAudioGatewayProfile` |
| `BluetoothGattCharacteristic` | `BluetoothGattCharacteristic(java.util.UUID, int, int)` | 5 | partial | moderate | `isBluetoothDiscovering` | `isBluetoothDiscovering(): boolean` |
| `getDescriptor` | `android.bluetooth.BluetoothGattDescriptor getDescriptor(java.util.UUID)` | 5 | partial | moderate | `getState` | `getState(): BluetoothState` |
| `getDescriptors` | `java.util.List<android.bluetooth.BluetoothGattDescriptor> getDescriptors()` | 5 | partial | moderate | `getState` | `getState(): BluetoothState` |
| `getPermissions` | `int getPermissions()` | 5 | partial | moderate | `getState` | `getState(): BluetoothState` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 5 | partial | Log warning + no-op |
| `addDescriptor` | 1 | none | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 18 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothGattCharacteristic`:


## Quality Gates

Before marking `android.bluetooth.BluetoothGattCharacteristic` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 21 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 18 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
