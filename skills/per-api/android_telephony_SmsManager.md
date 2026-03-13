# SKILL: android.telephony.SmsManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.SmsManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.SmsManager` |
| **Package** | `android.telephony` |
| **Total Methods** | 17 |
| **Avg Score** | 8.4 |
| **Scenario** | S1: Direct Mapping (Thin Wrapper) |
| **Strategy** | Simple delegation to OHBridge |
| **Direct/Near** | 16 (94%) |
| **Partial/Composite** | 1 (5%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 17 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getDefaultSmsSubscriptionId` | `static int getDefaultSmsSubscriptionId()` | 10 | direct | trivial | `getDefaultSmsSlotId` | `getDefaultSmsSlotId(callback: AsyncCallback<number>): void` |
| `sendDataMessage` | `void sendDataMessage(String, String, short, byte[], android.app.PendingIntent, android.app.PendingIntent)` | 10 | direct | trivial | `sendMessage` | `sendMessage(options: SendMessageOptions): void` |
| `sendTextMessage` | `void sendTextMessage(String, String, String, android.app.PendingIntent, android.app.PendingIntent)` | 10 | direct | trivial | `sendMessage` | `sendMessage(options: SendMessageOptions): void` |
| `sendTextMessage` | `void sendTextMessage(@NonNull String, @Nullable String, @NonNull String, @Nullable android.app.PendingIntent, @Nullable android.app.PendingIntent, long)` | 10 | direct | trivial | `sendMessage` | `sendMessage(options: SendMessageOptions): void` |
| `getDefault` | `static android.telephony.SmsManager getDefault()` | 9 | direct | trivial | `getDefaultSmsSimId` | `getDefaultSmsSimId(callback: AsyncCallback<number>): void` |
| `divideMessage` | `java.util.ArrayList<java.lang.String> divideMessage(String)` | 9 | direct | easy | `addSimMessage` | `addSimMessage(options: SimMessageOptions, callback: AsyncCallback<void>): void` |
| `sendMultimediaMessage` | `void sendMultimediaMessage(android.content.Context, android.net.Uri, String, android.os.Bundle, android.app.PendingIntent)` | 9 | direct | easy | `sendMessage` | `sendMessage(options: SendMessageOptions): void` |
| `sendMultipartTextMessage` | `void sendMultipartTextMessage(String, String, java.util.ArrayList<java.lang.String>, java.util.ArrayList<android.app.PendingIntent>, java.util.ArrayList<android.app.PendingIntent>)` | 8 | direct | easy | `sendShortMessage` | `sendShortMessage(options: SendMessageOptions, callback: AsyncCallback<void>): void` |
| `sendMultipartTextMessage` | `void sendMultipartTextMessage(@NonNull String, @Nullable String, @NonNull java.util.List<java.lang.String>, @Nullable java.util.List<android.app.PendingIntent>, @Nullable java.util.List<android.app.PendingIntent>, long)` | 8 | direct | easy | `sendShortMessage` | `sendShortMessage(options: SendMessageOptions, callback: AsyncCallback<void>): void` |
| `sendMultipartTextMessage` | `void sendMultipartTextMessage(@NonNull String, @Nullable String, @NonNull java.util.List<java.lang.String>, @Nullable java.util.List<android.app.PendingIntent>, @Nullable java.util.List<android.app.PendingIntent>, @NonNull String, @Nullable String)` | 8 | direct | easy | `sendShortMessage` | `sendShortMessage(options: SendMessageOptions, callback: AsyncCallback<void>): void` |
| `onFinancialSmsMessages` | `abstract void onFinancialSmsMessages(android.database.CursorWindow)` | 8 | direct | easy | `getAllSimMessages` | `getAllSimMessages(slotId: number, callback: AsyncCallback<Array<SimShortMessage>>): void` |
| `downloadMultimediaMessage` | `void downloadMultimediaMessage(android.content.Context, String, android.net.Uri, android.os.Bundle, android.app.PendingIntent)` | 8 | direct | easy | `downloadMms` | `downloadMms(context: Context, mmsParams: MmsParams, callback: AsyncCallback<void>): void` |
| `injectSmsPdu` | `void injectSmsPdu(byte[], String, android.app.PendingIntent)` | 7 | near | easy | `setSmscAddr` | `setSmscAddr(slotId: number, smscAddr: string, callback: AsyncCallback<void>): void` |
| `getSmsManagerForSubscriptionId` | `static android.telephony.SmsManager getSmsManagerForSubscriptionId(int)` | 7 | near | easy | `getDefaultSmsSlotId` | `getDefaultSmsSlotId(callback: AsyncCallback<number>): void` |
| `getSubscriptionId` | `int getSubscriptionId()` | 7 | near | easy | `getSmscAddr` | `getSmscAddr(slotId: number, callback: AsyncCallback<string>): void` |
| `createAppSpecificSmsToken` | `String createAppSpecificSmsToken(android.app.PendingIntent)` | 6 | near | moderate | `createMessage` | `createMessage(pdu: Array<number>, specification: string, callback: AsyncCallback<ShortMessage>): void` |
| `FinancialSmsCallback` | `SmsManager.FinancialSmsCallback()` | 6 | partial | moderate | `hasSmsCapability` | `hasSmsCapability(): boolean` |

## AI Agent Instructions

**Scenario: S1 — Direct Mapping (Thin Wrapper)**

1. Create Java shim at `shim/java/android/telephony/SmsManager.java`
2. For each method, delegate to `OHBridge.xxx()` — one bridge call per Android call
3. Add `static native` declarations to `OHBridge.java`
4. Add mock implementations to `test-apps/mock/.../OHBridge.java`
5. Add test section to `HeadlessTest.java` — call each method with valid + edge inputs
6. Test null args, boundary values, return types

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.SmsManager`:

- `android.app.PendingIntent` (already shimmed)

## Quality Gates

Before marking `android.telephony.SmsManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 17 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 17 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
