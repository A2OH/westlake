# SKILL: android.bluetooth.BluetoothGattCallback

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothGattCallback`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothGattCallback` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 13 |
| **Avg Score** | 4.2 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 1 (7%) |
| **Partial/Composite** | 12 (92%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `onConnectionStateChange` | `void onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)` | 7 | near | moderate | `getBtConnectionState` | `getBtConnectionState(): ProfileConnectionState` |
| `onDescriptorWrite` | `void onDescriptorWrite(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattDescriptor, int)` | 6 | partial | moderate | `sppWrite` | `sppWrite(clientSocket: number, data: ArrayBuffer): boolean` |
| `BluetoothGattCallback` | `BluetoothGattCallback()` | 5 | partial | moderate | `setBluetoothScanMode` | `setBluetoothScanMode(mode: ScanMode, duration: number): void` |
| `onReadRemoteRssi` | `void onReadRemoteRssi(android.bluetooth.BluetoothGatt, int, int)` | 5 | partial | moderate | `getRemoteProductId` | `getRemoteProductId(deviceId: string): string` |

## Stub APIs (score < 5): 9 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onCharacteristicWrite` | 4 | partial | Return safe default (null/false/0/empty) |
| `onPhyRead` | 4 | composite | Return safe default (null/false/0/empty) |
| `onCharacteristicChanged` | 3 | composite | Return safe default (null/false/0/empty) |
| `onCharacteristicRead` | 3 | composite | Return safe default (null/false/0/empty) |
| `onDescriptorRead` | 3 | composite | Return safe default (null/false/0/empty) |
| `onMtuChanged` | 3 | composite | Store callback, never fire |
| `onPhyUpdate` | 3 | composite | Log warning + no-op |
| `onServicesDiscovered` | 3 | composite | Return safe default (null/false/0/empty) |
| `onReliableWriteCompleted` | 3 | composite | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothGattCallback`:


## Quality Gates

Before marking `android.bluetooth.BluetoothGattCallback` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 13 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
