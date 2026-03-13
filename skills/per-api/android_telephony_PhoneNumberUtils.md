# SKILL: android.telephony.PhoneNumberUtils

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.PhoneNumberUtils`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.PhoneNumberUtils` |
| **Package** | `android.telephony` |
| **Total Methods** | 35 |
| **Avg Score** | 5.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 13 (37%) |
| **Partial/Composite** | 18 (51%) |
| **No Mapping** | 4 (11%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 21 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `formatNumberToE164` | `static String formatNumberToE164(String, String)` | 9 | direct | easy | `formatPhoneNumberToE164` | `formatPhoneNumberToE164(phoneNumber: string, countryCode: string, callback: AsyncCallback<string>): void` |
| `isVoiceMailNumber` | `static boolean isVoiceMailNumber(String)` | 9 | direct | easy | `getVoiceMailNumber` | `getVoiceMailNumber(slotId: number, callback: AsyncCallback<string>): void` |
| `formatNumber` | `static String formatNumber(String, String)` | 8 | direct | easy | `formatPhoneNumber` | `formatPhoneNumber(phoneNumber: string, options: NumberFormatOptions, callback: AsyncCallback<string>): void` |
| `formatNumber` | `static String formatNumber(String, String, String)` | 8 | direct | easy | `formatPhoneNumber` | `formatPhoneNumber(phoneNumber: string, options: NumberFormatOptions, callback: AsyncCallback<string>): void` |
| `compare` | `static boolean compare(String, String)` | 8 | direct | easy | `compare` | `compare(buf1: Buffer | Uint8Array, buf2: Buffer | Uint8Array): -1 | 0 | 1` |
| `compare` | `static boolean compare(android.content.Context, String, String)` | 8 | direct | easy | `compare` | `compare(buf1: Buffer | Uint8Array, buf2: Buffer | Uint8Array): -1 | 0 | 1` |
| `formatNumberToRFC3966` | `static String formatNumberToRFC3966(String, String)` | 8 | near | easy | `formatPhoneNumberToE164` | `formatPhoneNumberToE164(phoneNumber: string, countryCode: string, callback: AsyncCallback<string>): void` |
| `isGlobalPhoneNumber` | `static boolean isGlobalPhoneNumber(String)` | 8 | near | easy | `isEmergencyPhoneNumber` | `isEmergencyPhoneNumber(phoneNumber: string, options: EmergencyNumberOptions, callback: AsyncCallback<boolean>): void` |
| `createTtsSpan` | `static android.text.style.TtsSpan createTtsSpan(String)` | 7 | near | moderate | `createMessage` | `createMessage(pdu: Array<number>, specification: string, callback: AsyncCallback<ShortMessage>): void` |
| `normalizeNumber` | `static String normalizeNumber(String)` | 7 | near | moderate | `formatPhoneNumber` | `formatPhoneNumber(phoneNumber: string, options: NumberFormatOptions, callback: AsyncCallback<string>): void` |
| `PhoneNumberUtils` | `PhoneNumberUtils()` | 7 | near | moderate | `formatPhoneNumber` | `formatPhoneNumber(phoneNumber: string, options: NumberFormatOptions, callback: AsyncCallback<string>): void` |
| `createTtsSpannable` | `static CharSequence createTtsSpannable(CharSequence)` | 6 | near | moderate | `createMessage` | `createMessage(pdu: Array<number>, specification: string, callback: AsyncCallback<ShortMessage>): void` |
| `extractNetworkPortion` | `static String extractNetworkPortion(String)` | 6 | near | moderate | `getNetworkSearchInformation` | `getNetworkSearchInformation(slotId: number, callback: AsyncCallback<NetworkSearchResult>): void` |
| `isReallyDialable` | `static final boolean isReallyDialable(char)` | 6 | partial | moderate | `isCellularDataEnabled` | `isCellularDataEnabled(callback: AsyncCallback<boolean>): void` |
| `isNonSeparator` | `static final boolean isNonSeparator(char)` | 6 | partial | moderate | `isOperatorSimCard` | `isOperatorSimCard(slotId: number, operator: OperatorSimCard): boolean` |
| `getStrippedReversed` | `static String getStrippedReversed(String)` | 6 | partial | moderate | `getUniqueDeviceId` | `getUniqueDeviceId(slotId: number, callback: AsyncCallback<string>): void` |
| `addTtsSpan` | `static void addTtsSpan(android.text.Spannable, int, int)` | 5 | partial | moderate | `addSimMessage` | `addSimMessage(options: SimMessageOptions, callback: AsyncCallback<void>): void` |
| `isDialable` | `static final boolean isDialable(char)` | 5 | partial | moderate | `isCellularDataEnabled` | `isCellularDataEnabled(callback: AsyncCallback<boolean>): void` |
| `getNumberFromIntent` | `static String getNumberFromIntent(android.content.Intent, android.content.Context)` | 5 | partial | moderate | `getVoiceMailNumber` | `getVoiceMailNumber(slotId: number, callback: AsyncCallback<string>): void` |
| `is12Key` | `static final boolean is12Key(char)` | 5 | partial | moderate | `isNRSupported` | `isNRSupported(): boolean` |
| `isISODigit` | `static boolean isISODigit(char)` | 5 | partial | moderate | `isNRSupported` | `isNRSupported(): boolean` |

## Stub APIs (score < 5): 14 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `networkPortionToCalledPartyBCD` | 5 | partial | Store callback, never fire |
| `isStartsPostDial` | 4 | partial | Return dummy instance / no-op |
| `networkPortionToCalledPartyBCDWithLength` | 4 | partial | Store callback, never fire |
| `isWellFormedSmsAddress` | 4 | partial | Return safe default (null/false/0/empty) |
| `numberToCalledPartyBCD` | 4 | partial | throw UnsupportedOperationException |
| `extractPostDialPortion` | 3 | composite | Store callback, never fire |
| `toCallerIDMinMatch` | 3 | composite | throw UnsupportedOperationException |
| `calledPartyBCDToString` | 2 | composite | throw UnsupportedOperationException |
| `convertKeypadLettersToDigits` | 2 | composite | Store callback, never fire |
| `calledPartyBCDFragmentToString` | 2 | composite | throw UnsupportedOperationException |
| `replaceUnicodeDigits` | 1 | none | throw UnsupportedOperationException |
| `stringFromStringAndTOA` | 1 | none | throw UnsupportedOperationException |
| `stripSeparators` | 1 | none | throw UnsupportedOperationException |
| `toaFromString` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 21 methods that have score >= 5
2. Stub 14 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.PhoneNumberUtils`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.PhoneNumberUtils` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 35 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 21 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
