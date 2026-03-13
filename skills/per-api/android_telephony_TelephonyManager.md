# SKILL: android.telephony.TelephonyManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.TelephonyManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.TelephonyManager` |
| **Package** | `android.telephony` |
| **Total Methods** | 53 |
| **Avg Score** | 6.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 42 (79%) |
| **Partial/Composite** | 11 (20%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 52 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getCallState` | `int getCallState()` | 10 | direct | trivial | `getCallState` | `getCallState(callback: AsyncCallback<CallState>): void` |
| `isEmergencyNumber` | `boolean isEmergencyNumber(@NonNull String)` | 10 | direct | trivial | `isEmergencyPhoneNumber` | `isEmergencyPhoneNumber(phoneNumber: string, options: EmergencyNumberOptions, callback: AsyncCallback<boolean>): void` |
| `getDataState` | `int getDataState()` | 10 | direct | trivial | `getCallState` | `getCallState(callback: AsyncCallback<CallState>): void` |
| `isVoiceCapable` | `boolean isVoiceCapable()` | 10 | direct | trivial | `hasVoiceCapability` | `hasVoiceCapability(): boolean` |
| `getSimState` | `int getSimState()` | 9 | direct | easy | `getCallState` | `getCallState(callback: AsyncCallback<CallState>): void` |
| `getSimState` | `int getSimState(int)` | 9 | direct | easy | `getCallState` | `getCallState(callback: AsyncCallback<CallState>): void` |
| `hasIccCard` | `boolean hasIccCard()` | 8 | near | easy | `hasCall` | `hasCall(callback: AsyncCallback<boolean>): void` |
| `getSimCarrierId` | `int getSimCarrierId()` | 8 | near | easy | `getMainCallId` | `getMainCallId(callId: number, callback: AsyncCallback<number>): void` |
| `getSubscriptionId` | `int getSubscriptionId()` | 7 | near | easy | `getSubCallIdList` | `getSubCallIdList(callId: number, callback: AsyncCallback<Array<string>>): void` |
| `isNetworkRoaming` | `boolean isNetworkRoaming()` | 7 | near | easy | `setCallWaiting` | `setCallWaiting(slotId: number, activate: boolean, callback: AsyncCallback<void>): void` |
| `getCardIdForDefaultEuicc` | `int getCardIdForDefaultEuicc()` | 7 | near | easy | `getCallIdListForConference` | `getCallIdListForConference(callId: number, callback: AsyncCallback<Array<string>>): void` |
| `setVoiceMailNumber` | `boolean setVoiceMailNumber(String, String)` | 7 | near | easy | `formatPhoneNumber` | `formatPhoneNumber(phoneNumber: string, options: NumberFormatOptions, callback: AsyncCallback<string>): void` |
| `getVoicemailRingtoneUri` | `android.net.Uri getVoicemailRingtoneUri(android.telecom.PhoneAccountHandle)` | 7 | near | easy | `getCallWaitingStatus` | `getCallWaitingStatus(slotId: number, callback: AsyncCallback<CallWaitingStatus>): void` |
| `getIccAuthentication` | `String getIccAuthentication(int, int, String)` | 7 | near | easy | `getCallState` | `getCallState(callback: AsyncCallback<CallState>): void` |
| `isWorldPhone` | `boolean isWorldPhone()` | 7 | near | easy | `isEmergencyPhoneNumber` | `isEmergencyPhoneNumber(phoneNumber: string, options: EmergencyNumberOptions, callback: AsyncCallback<boolean>): void` |
| `isVoicemailVibrationEnabled` | `boolean isVoicemailVibrationEnabled(android.telecom.PhoneAccountHandle)` | 7 | near | moderate | `hasVoiceCapability` | `hasVoiceCapability(): boolean` |
| `CellInfoCallback` | `TelephonyManager.CellInfoCallback()` | 7 | near | moderate | `getMainCallId` | `getMainCallId(callId: number, callback: AsyncCallback<number>): void` |
| `getPhoneType` | `int getPhoneType()` | 7 | near | moderate | `formatPhoneNumber` | `formatPhoneNumber(phoneNumber: string, options: NumberFormatOptions, callback: AsyncCallback<string>): void` |
| `sendDialerSpecialCode` | `void sendDialerSpecialCode(String)` | 7 | near | moderate | `dialCall` | `dialCall(phoneNumber: string, options: DialCallOptions, callback: AsyncCallback<void>): void` |
| `UssdResponseCallback` | `TelephonyManager.UssdResponseCallback()` | 7 | near | moderate | `answerCall` | `answerCall(callId: number, callback: AsyncCallback<void>): void` |
| `getSimOperatorName` | `String getSimOperatorName()` | 7 | near | moderate | `getCallState` | `getCallState(callback: AsyncCallback<CallState>): void` |
| `isSmsCapable` | `boolean isSmsCapable()` | 7 | near | moderate | `hasVoiceCapability` | `hasVoiceCapability(): boolean` |
| `sendVisualVoicemailSms` | `void sendVisualVoicemailSms(String, int, String, android.app.PendingIntent)` | 7 | near | moderate | `dialCall` | `dialCall(phoneNumber: string, options: DialCallOptions, callback: AsyncCallback<void>): void` |
| `getSimOperator` | `String getSimOperator()` | 7 | near | moderate | `getCallState` | `getCallState(callback: AsyncCallback<CallState>): void` |
| `getNetworkOperatorName` | `String getNetworkOperatorName()` | 7 | near | moderate | `getCallIdListForConference` | `getCallIdListForConference(callId: number, callback: AsyncCallback<Array<string>>): void` |
| `listen` | `void listen(android.telephony.PhoneStateListener, int)` | 7 | near | moderate | `getCallStateSync` | `getCallStateSync(): CallState` |
| `getMmsUAProfUrl` | `String getMmsUAProfUrl()` | 7 | near | moderate | `getSubCallIdList` | `getSubCallIdList(callId: number, callback: AsyncCallback<Array<string>>): void` |
| `getMmsUserAgent` | `String getMmsUserAgent()` | 7 | near | moderate | `getSubCallIdList` | `getSubCallIdList(callId: number, callback: AsyncCallback<Array<string>>): void` |
| `hasCarrierPrivileges` | `boolean hasCarrierPrivileges()` | 7 | near | moderate | `hasCallSync` | `hasCallSync(): boolean` |
| `setVisualVoicemailSmsFilterSettings` | `void setVisualVoicemailSmsFilterSettings(android.telephony.VisualVoicemailSmsFilterSettings)` | 6 | near | moderate | `setCallWaiting` | `setCallWaiting(slotId: number, activate: boolean, callback: AsyncCallback<void>): void` |
| `getNetworkSpecifier` | `String getNetworkSpecifier()` | 6 | near | moderate | `getCallIdListForConference` | `getCallIdListForConference(callId: number, callback: AsyncCallback<Array<string>>): void` |
| `getSimSpecificCarrierId` | `int getSimSpecificCarrierId()` | 6 | near | moderate | `getMainCallId` | `getMainCallId(callId: number, callback: AsyncCallback<number>): void` |
| `onCellInfo` | `abstract void onCellInfo(@NonNull java.util.List<android.telephony.CellInfo>)` | 6 | near | moderate | `holdCall` | `holdCall(callId: number, callback: AsyncCallback<void>): void` |
| `getCarrierIdFromSimMccMnc` | `int getCarrierIdFromSimMccMnc()` | 6 | near | moderate | `getCallStateSync` | `getCallStateSync(): CallState` |
| `setLine1NumberForDisplay` | `boolean setLine1NumberForDisplay(String, String)` | 6 | near | moderate | `formatPhoneNumber` | `formatPhoneNumber(phoneNumber: string, options: NumberFormatOptions, callback: AsyncCallback<string>): void` |
| `getActiveModemCount` | `int getActiveModemCount()` | 6 | near | moderate | `getMainCallId` | `getMainCallId(callId: number, callback: AsyncCallback<number>): void` |
| `getSimCountryIso` | `String getSimCountryIso()` | 6 | near | moderate | `getSubCallIdList` | `getSubCallIdList(callId: number, callback: AsyncCallback<Array<string>>): void` |
| `getNetworkCountryIso` | `String getNetworkCountryIso()` | 6 | near | moderate | `getCallIdListForConference` | `getCallIdListForConference(callId: number, callback: AsyncCallback<Array<string>>): void` |
| `getDataActivity` | `int getDataActivity()` | 6 | near | moderate | `hasVoiceCapability` | `hasVoiceCapability(): boolean` |
| `isHearingAidCompatibilitySupported` | `boolean isHearingAidCompatibilitySupported()` | 6 | near | moderate | `hasVoiceCapability` | `hasVoiceCapability(): boolean` |
| `createForSubscriptionId` | `android.telephony.TelephonyManager createForSubscriptionId(int)` | 6 | near | moderate | `getSubCallIdList` | `getSubCallIdList(callId: number, callback: AsyncCallback<Array<string>>): void` |
| `getNetworkOperator` | `String getNetworkOperator()` | 6 | near | moderate | `getCallIdListForConference` | `getCallIdListForConference(callId: number, callback: AsyncCallback<Array<string>>): void` |
| `getSupportedModemCount` | `int getSupportedModemCount()` | 6 | partial | moderate | `getCallIdListForConference` | `getCallIdListForConference(callId: number, callback: AsyncCallback<Array<string>>): void` |
| `updateAvailableNetworks` | `void updateAvailableNetworks(@NonNull java.util.List<android.telephony.AvailableNetworkInfo>, @Nullable java.util.concurrent.Executor, @Nullable java.util.function.Consumer<java.lang.Integer>)` | 6 | partial | moderate | `muteRinger` | `muteRinger(callback: AsyncCallback<void>): void` |
| `isConcurrentVoiceAndDataSupported` | `boolean isConcurrentVoiceAndDataSupported()` | 6 | partial | moderate | `combineConference` | `combineConference(callId: number, callback: AsyncCallback<void>): void` |
| `setOperatorBrandOverride` | `boolean setOperatorBrandOverride(String)` | 6 | partial | moderate | `muteRinger` | `muteRinger(callback: AsyncCallback<void>): void` |
| `onError` | `void onError(int, @Nullable Throwable)` | 6 | partial | moderate | `muteRinger` | `muteRinger(callback: AsyncCallback<void>): void` |
| `isRttSupported` | `boolean isRttSupported()` | 5 | partial | moderate | `startDTMF` | `startDTMF(callId: number, character: string, callback: AsyncCallback<void>): void` |
| `canChangeDtmfToneLength` | `boolean canChangeDtmfToneLength()` | 5 | partial | moderate | `getCallWaitingStatus` | `getCallWaitingStatus(slotId: number, callback: AsyncCallback<CallWaitingStatus>): void` |
| `onReceiveUssdResponse` | `void onReceiveUssdResponse(android.telephony.TelephonyManager, String, CharSequence)` | 5 | partial | moderate | `getCallStateSync` | `getCallStateSync(): CallState` |
| `setPreferredNetworkTypeToGlobal` | `boolean setPreferredNetworkTypeToGlobal()` | 5 | partial | moderate | `getCallIdListForConference` | `getCallIdListForConference(callId: number, callback: AsyncCallback<Array<string>>): void` |
| `onReceiveUssdResponseFailed` | `void onReceiveUssdResponseFailed(android.telephony.TelephonyManager, String, int)` | 5 | partial | moderate | `hasVoiceCapability` | `hasVoiceCapability(): boolean` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setPreferredOpportunisticDataSubscription` | 4 | partial | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 52 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.TelephonyManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.TelephonyManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 53 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 52 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
