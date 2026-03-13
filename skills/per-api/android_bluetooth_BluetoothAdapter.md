# SKILL: android.bluetooth.BluetoothAdapter

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothAdapter`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothAdapter` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 17 |
| **Avg Score** | 7.5 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 13 (76%) |
| **Partial/Composite** | 4 (23%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 17 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getBluetoothLeScanner` | `android.bluetooth.le.BluetoothLeScanner getBluetoothLeScanner()` | 10 | direct | trivial | `getBluetoothScanMode` | `getBluetoothScanMode(): ScanMode` |
| `getRemoteDevice` | `android.bluetooth.BluetoothDevice getRemoteDevice(String)` | 10 | direct | trivial | `getRemoteDeviceName` | `getRemoteDeviceName(deviceId: string): string` |
| `getRemoteDevice` | `android.bluetooth.BluetoothDevice getRemoteDevice(byte[])` | 10 | direct | trivial | `getRemoteDeviceName` | `getRemoteDeviceName(deviceId: string): string` |
| `getName` | `String getName()` | 9 | direct | trivial | `getLocalName` | `getLocalName(): string` |
| `checkBluetoothAddress` | `static boolean checkBluetoothAddress(String)` | 8 | direct | easy | `setBluetoothScanMode` | `setBluetoothScanMode(mode: ScanMode, duration: number): void` |
| `getBluetoothLeAdvertiser` | `android.bluetooth.le.BluetoothLeAdvertiser getBluetoothLeAdvertiser()` | 8 | direct | easy | `stopBluetoothDiscovery` | `stopBluetoothDiscovery(): void` |
| `getProfileProxy` | `boolean getProfileProxy(android.content.Context, android.bluetooth.BluetoothProfile.ServiceListener, int)` | 8 | near | easy | `getLocalProfileUuids` | `getLocalProfileUuids(callback: AsyncCallback<Array<ProfileUuids>>): void` |
| `closeProfileProxy` | `void closeProfileProxy(int, android.bluetooth.BluetoothProfile)` | 8 | near | easy | `connectAllowedProfiles` | `connectAllowedProfiles(deviceId: string, callback: AsyncCallback<void>): void` |
| `getDefaultAdapter` | `static android.bluetooth.BluetoothAdapter getDefaultAdapter()` | 8 | near | easy | `getPairState` | `getPairState(deviceId: string): BondState` |
| `isLeExtendedAdvertisingSupported` | `boolean isLeExtendedAdvertisingSupported()` | 7 | near | moderate | `setDevicePinCode` | `setDevicePinCode(deviceId: string, code: string, callback: AsyncCallback<void>): void` |
| `getLeMaximumAdvertisingDataLength` | `int getLeMaximumAdvertisingDataLength()` | 6 | near | moderate | `getPairState` | `getPairState(deviceId: string): BondState` |
| `isOffloadedFilteringSupported` | `boolean isOffloadedFilteringSupported()` | 6 | near | moderate | `disconnectAllowedProfiles` | `disconnectAllowedProfiles(deviceId: string, callback: AsyncCallback<void>): void` |
| `isLe2MPhySupported` | `boolean isLe2MPhySupported()` | 6 | near | moderate | `isBluetoothDiscovering` | `isBluetoothDiscovering(): boolean` |
| `isLeCodedPhySupported` | `boolean isLeCodedPhySupported()` | 6 | partial | moderate | `getProfileConnectionState` | `getProfileConnectionState(profileId?: ProfileId): ProfileConnectionState` |
| `isMultipleAdvertisementSupported` | `boolean isMultipleAdvertisementSupported()` | 6 | partial | moderate | `isBluetoothDiscovering` | `isBluetoothDiscovering(): boolean` |
| `isOffloadedScanBatchingSupported` | `boolean isOffloadedScanBatchingSupported()` | 6 | partial | moderate | `isBluetoothDiscovering` | `isBluetoothDiscovering(): boolean` |
| `isLePeriodicAdvertisingSupported` | `boolean isLePeriodicAdvertisingSupported()` | 6 | partial | moderate | `getRemoteProductId` | `getRemoteProductId(deviceId: string): string` |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothAdapter`:


## Quality Gates

Before marking `android.bluetooth.BluetoothAdapter` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 17 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 17 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
