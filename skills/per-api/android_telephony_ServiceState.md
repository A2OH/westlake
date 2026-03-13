# SKILL: android.telephony.ServiceState

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.ServiceState`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.ServiceState` |
| **Package** | `android.telephony` |
| **Total Methods** | 23 |
| **Avg Score** | 5.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 13 (56%) |
| **Partial/Composite** | 8 (34%) |
| **No Mapping** | 2 (8%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 20 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getOperatorNumeric` | `String getOperatorNumeric()` | 9 | direct | trivial | `getSimOperatorNumeric` | `getSimOperatorNumeric(slotId: number, callback: AsyncCallback<string>): void` |
| `getState` | `int getState()` | 8 | direct | easy | `getSimState` | `getSimState(slotId: number, callback: AsyncCallback<SimState>): void` |
| `setState` | `void setState(int)` | 7 | near | easy | `getSimState` | `getSimState(slotId: number, callback: AsyncCallback<SimState>): void` |
| `setOperatorName` | `void setOperatorName(String, String, String)` | 7 | near | easy | `getSimOperatorNumeric` | `getSimOperatorNumeric(slotId: number, callback: AsyncCallback<string>): void` |
| `getChannelNumber` | `int getChannelNumber()` | 7 | near | easy | `getVoiceMailNumber` | `getVoiceMailNumber(slotId: number, callback: AsyncCallback<string>): void` |
| `getCellBandwidths` | `int[] getCellBandwidths()` | 7 | near | moderate | `getCellInformation` | `getCellInformation(slotId: number, callback: AsyncCallback<Array<CellInformation>>): void` |
| `getCdmaNetworkId` | `int getCdmaNetworkId()` | 6 | near | moderate | `getNetworkState` | `getNetworkState(slotId: number, callback: AsyncCallback<NetworkState>): void` |
| `getCdmaSystemId` | `int getCdmaSystemId()` | 6 | near | moderate | `getPrimarySlotId` | `getPrimarySlotId(callback: AsyncCallback<number>): void` |
| `setStateOff` | `void setStateOff()` | 6 | near | moderate | `getSimState` | `getSimState(slotId: number, callback: AsyncCallback<SimState>): void` |
| `ServiceState` | `ServiceState()` | 6 | near | moderate | `getSimState` | `getSimState(slotId: number, callback: AsyncCallback<SimState>): void` |
| `ServiceState` | `ServiceState(android.telephony.ServiceState)` | 6 | near | moderate | `getSimState` | `getSimState(slotId: number, callback: AsyncCallback<SimState>): void` |
| `getIsManualSelection` | `boolean getIsManualSelection()` | 6 | near | moderate | `getMainCallId` | `getMainCallId(callId: number, callback: AsyncCallback<number>): void` |
| `setIsManualSelection` | `void setIsManualSelection(boolean)` | 6 | near | moderate | `setNetworkSelectionMode` | `setNetworkSelectionMode(options: NetworkSelectionModeOptions, callback: AsyncCallback<void>): void` |
| `getRoaming` | `boolean getRoaming()` | 6 | partial | moderate | `getIMEI` | `getIMEI(slotId: number, callback: AsyncCallback<string>): void` |
| `getOperatorAlphaLong` | `String getOperatorAlphaLong()` | 6 | partial | moderate | `getSimOperatorNumeric` | `getSimOperatorNumeric(slotId: number, callback: AsyncCallback<string>): void` |
| `setRoaming` | `void setRoaming(boolean)` | 6 | partial | moderate | `setCallWaiting` | `setCallWaiting(slotId: number, activate: boolean, callback: AsyncCallback<void>): void` |
| `getDuplexMode` | `int getDuplexMode()` | 6 | partial | moderate | `getNrOptionMode` | `getNrOptionMode(slotId: number, callback: AsyncCallback<NrOptionMode>): void` |
| `getOperatorAlphaShort` | `String getOperatorAlphaShort()` | 6 | partial | moderate | `getSimOperatorNumeric` | `getSimOperatorNumeric(slotId: number, callback: AsyncCallback<string>): void` |
| `setStateOutOfService` | `void setStateOutOfService()` | 5 | partial | moderate | `getSimStateSync` | `getSimStateSync(slotId: number): SimState` |
| `isSearching` | `boolean isSearching()` | 5 | partial | moderate | `isNRSupported` | `isNRSupported(): boolean` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `copyFrom` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 20 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.ServiceState`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.ServiceState` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 23 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 20 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
