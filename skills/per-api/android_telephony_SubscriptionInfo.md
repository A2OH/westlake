# SKILL: android.telephony.SubscriptionInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.SubscriptionInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.SubscriptionInfo` |
| **Package** | `android.telephony` |
| **Total Methods** | 17 |
| **Avg Score** | 5.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 11 (64%) |
| **Partial/Composite** | 5 (29%) |
| **No Mapping** | 1 (5%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 15 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getIccId` | `String getIccId()` | 8 | direct | easy | `getSimIccId` | `getSimIccId(slotId: number, callback: AsyncCallback<string>): void` |
| `getIconTint` | `int getIconTint()` | 7 | near | easy | `getSimAccountInfo` | `getSimAccountInfo(slotId: number, callback: AsyncCallback<IccAccountInfo>): void` |
| `getSimSlotIndex` | `int getSimSlotIndex()` | 7 | near | easy | `getPrimarySlotId` | `getPrimarySlotId(callback: AsyncCallback<number>): void` |
| `getCardId` | `int getCardId()` | 7 | near | easy | `getCardType` | `getCardType(slotId: number, callback: AsyncCallback<CardType>): void` |
| `getCountryIso` | `String getCountryIso()` | 7 | near | moderate | `getSimAccountInfo` | `getSimAccountInfo(slotId: number, callback: AsyncCallback<IccAccountInfo>): void` |
| `getNumber` | `String getNumber()` | 7 | near | moderate | `getVoiceMailNumber` | `getVoiceMailNumber(slotId: number, callback: AsyncCallback<string>): void` |
| `getSubscriptionType` | `int getSubscriptionType()` | 7 | near | moderate | `getCardType` | `getCardType(slotId: number, callback: AsyncCallback<CardType>): void` |
| `getCarrierId` | `int getCarrierId()` | 6 | near | moderate | `getIMEI` | `getIMEI(slotId: number, callback: AsyncCallback<string>): void` |
| `getSubscriptionId` | `int getSubscriptionId()` | 6 | near | moderate | `getNrOptionMode` | `getNrOptionMode(slotId: number, callback: AsyncCallback<NrOptionMode>): void` |
| `getDataRoaming` | `int getDataRoaming()` | 6 | near | moderate | `enableCellularDataRoaming` | `enableCellularDataRoaming(slotId: number, callback: AsyncCallback<void>): void` |
| `getDisplayName` | `CharSequence getDisplayName()` | 6 | near | moderate | `getSimSpn` | `getSimSpn(slotId: number, callback: AsyncCallback<string>): void` |
| `getCarrierName` | `CharSequence getCarrierName()` | 6 | partial | moderate | `getIMEI` | `getIMEI(slotId: number, callback: AsyncCallback<string>): void` |
| `createIconBitmap` | `android.graphics.Bitmap createIconBitmap(android.content.Context)` | 6 | partial | moderate | `createMessage` | `createMessage(pdu: Array<number>, specification: string, callback: AsyncCallback<ShortMessage>): void` |
| `isEmbedded` | `boolean isEmbedded()` | 5 | partial | moderate | `isNRSupported` | `isNRSupported(): boolean` |
| `isOpportunistic` | `boolean isOpportunistic()` | 5 | partial | moderate | `isNrSupported` | `isNrSupported(): boolean` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 15 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.SubscriptionInfo`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.SubscriptionInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 17 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 15 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
