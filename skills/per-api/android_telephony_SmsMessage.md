# SKILL: android.telephony.SmsMessage

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.SmsMessage`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.SmsMessage` |
| **Package** | `android.telephony` |
| **Total Methods** | 29 |
| **Avg Score** | 5.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 16 (55%) |
| **Partial/Composite** | 11 (37%) |
| **No Mapping** | 2 (6%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 24 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getProtocolIdentifier` | `int getProtocolIdentifier()` | 7 | near | easy | `getVoiceMailIdentifier` | `getVoiceMailIdentifier(slotId: number, callback: AsyncCallback<string>): void` |
| `getStatus` | `int getStatus()` | 7 | near | easy | `getSimState` | `getSimState(slotId: number, callback: AsyncCallback<SimState>): void` |
| `isMWISetMessage` | `boolean isMWISetMessage()` | 7 | near | moderate | `sendMessage` | `sendMessage(options: SendMessageOptions): void` |
| `getStatusOnIcc` | `int getStatusOnIcc()` | 7 | near | moderate | `getSimStateSync` | `getSimStateSync(slotId: number): SimState` |
| `getMessageClass` | `android.telephony.SmsMessage.MessageClass getMessageClass()` | 7 | near | moderate | `getAllSimMessages` | `getAllSimMessages(slotId: number, callback: AsyncCallback<Array<SimShortMessage>>): void` |
| `getMessageBody` | `String getMessageBody()` | 7 | near | moderate | `createMessage` | `createMessage(pdu: Array<number>, specification: string, callback: AsyncCallback<ShortMessage>): void` |
| `isMWIClearMessage` | `boolean isMWIClearMessage()` | 7 | near | moderate | `createMessage` | `createMessage(pdu: Array<number>, specification: string, callback: AsyncCallback<ShortMessage>): void` |
| `isStatusReportMessage` | `boolean isStatusReportMessage()` | 6 | near | moderate | `sendShortMessage` | `sendShortMessage(options: SendMessageOptions, callback: AsyncCallback<void>): void` |
| `getEmailBody` | `String getEmailBody()` | 6 | near | moderate | `getMainCallId` | `getMainCallId(callId: number, callback: AsyncCallback<number>): void` |
| `getSubmitPdu` | `static android.telephony.SmsMessage.SubmitPdu getSubmitPdu(String, String, String, boolean)` | 6 | near | moderate | `getSimGid1` | `getSimGid1(slotId: number, callback: AsyncCallback<string>): void` |
| `getSubmitPdu` | `static android.telephony.SmsMessage.SubmitPdu getSubmitPdu(String, String, short, byte[], boolean)` | 6 | near | moderate | `getSimGid1` | `getSimGid1(slotId: number, callback: AsyncCallback<string>): void` |
| `isCphsMwiMessage` | `boolean isCphsMwiMessage()` | 6 | near | moderate | `addSimMessage` | `addSimMessage(options: SimMessageOptions, callback: AsyncCallback<void>): void` |
| `getPdu` | `byte[] getPdu()` | 6 | near | moderate | `getMEID` | `getMEID(slotId: number, callback: AsyncCallback<string>): void` |
| `getDisplayMessageBody` | `String getDisplayMessageBody()` | 6 | near | moderate | `splitMessage` | `splitMessage(content: string, callback: AsyncCallback<Array<string>>): void` |
| `getEmailFrom` | `String getEmailFrom()` | 6 | near | moderate | `getVoiceMailNumber` | `getVoiceMailNumber(slotId: number, callback: AsyncCallback<string>): void` |
| `getIndexOnIcc` | `int getIndexOnIcc()` | 6 | near | moderate | `getIMEI` | `getIMEI(slotId: number, callback: AsyncCallback<string>): void` |
| `getUserData` | `byte[] getUserData()` | 6 | partial | moderate | `getCellularDataState` | `getCellularDataState(callback: AsyncCallback<DataConnectState>): void` |
| `getTimestampMillis` | `long getTimestampMillis()` | 6 | partial | moderate | `getIMEI` | `getIMEI(slotId: number, callback: AsyncCallback<string>): void` |
| `isEmail` | `boolean isEmail()` | 6 | partial | moderate | `isSimActive` | `isSimActive(slotId: number, callback: AsyncCallback<boolean>): void` |
| `createFromPdu` | `static android.telephony.SmsMessage createFromPdu(byte[], String)` | 5 | partial | moderate | `createMessage` | `createMessage(pdu: Array<number>, specification: string, callback: AsyncCallback<ShortMessage>): void` |
| `getServiceCenterAddress` | `String getServiceCenterAddress()` | 5 | partial | moderate | `getVoiceMailIdentifier` | `getVoiceMailIdentifier(slotId: number, callback: AsyncCallback<string>): void` |
| `isMwiDontStore` | `boolean isMwiDontStore()` | 5 | partial | moderate | `isNrSupported` | `isNrSupported(): boolean` |
| `getPseudoSubject` | `String getPseudoSubject()` | 5 | partial | moderate | `getSubCallIdList` | `getSubCallIdList(callId: number, callback: AsyncCallback<Array<string>>): void` |
| `isReplace` | `boolean isReplace()` | 5 | partial | moderate | `isNRSupported` | `isNRSupported(): boolean` |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getTPLayerLengthForPDU` | 5 | partial | Return safe default (null/false/0/empty) |
| `getDisplayOriginatingAddress` | 5 | partial | Return safe default (null/false/0/empty) |
| `isReplyPathPresent` | 4 | partial | Return safe default (null/false/0/empty) |
| `calculateLength` | 1 | none | throw UnsupportedOperationException |
| `calculateLength` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 24 methods that have score >= 5
2. Stub 5 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.SmsMessage`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.SmsMessage` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 29 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 24 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
