# SKILL: android.bluetooth.BluetoothHidDeviceAppQosSettings

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothHidDeviceAppQosSettings`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothHidDeviceAppQosSettings` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 9 |
| **Avg Score** | 4.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (22%) |
| **Partial/Composite** | 6 (66%) |
| **No Mapping** | 1 (11%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getTokenRate` | `int getTokenRate()` | 7 | near | easy | `getState` | `getState(): BluetoothState` |
| `getLatency` | `int getLatency()` | 7 | near | moderate | `getState` | `getState(): BluetoothState` |
| `getServiceType` | `int getServiceType()` | 5 | partial | moderate | `getRemoteDeviceName` | `getRemoteDeviceName(deviceId: string): string` |
| `BluetoothHidDeviceAppQosSettings` | `BluetoothHidDeviceAppQosSettings(int, int, int, int, int, int)` | 5 | partial | moderate | `isBluetoothDiscovering` | `isBluetoothDiscovering(): boolean` |
| `getTokenBucketSize` | `int getTokenBucketSize()` | 5 | partial | moderate | `getRemoteProductId` | `getRemoteProductId(deviceId: string): string` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getDelayVariation` | 5 | partial | Return safe default (null/false/0/empty) |
| `writeToParcel` | 5 | partial | Log warning + no-op |
| `getPeakBandwidth` | 4 | partial | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothHidDeviceAppQosSettings`:


## Quality Gates

Before marking `android.bluetooth.BluetoothHidDeviceAppQosSettings` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
