# SKILL: android.telephony.SubscriptionManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.SubscriptionManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.SubscriptionManager` |
| **Package** | `android.telephony` |
| **Total Methods** | 20 |
| **Avg Score** | 4.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 6 (30%) |
| **Partial/Composite** | 13 (65%) |
| **No Mapping** | 1 (5%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 9 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getDefaultSmsSubscriptionId` | `static int getDefaultSmsSubscriptionId()` | 8 | direct | easy | `getDefaultSmsSlotId` | `getDefaultSmsSlotId(callback: AsyncCallback<number>): void` |
| `getDefaultVoiceSubscriptionId` | `static int getDefaultVoiceSubscriptionId()` | 8 | direct | easy | `getDefaultVoiceSlotId` | `getDefaultVoiceSlotId(callback: AsyncCallback<number>): void` |
| `getDefaultSubscriptionId` | `static int getDefaultSubscriptionId()` | 7 | near | easy | `getDefaultSmsSimId` | `getDefaultSmsSimId(callback: AsyncCallback<number>): void` |
| `getDefaultDataSubscriptionId` | `static int getDefaultDataSubscriptionId()` | 7 | near | moderate | `getDefaultCellularDataSlotId` | `getDefaultCellularDataSlotId(callback: AsyncCallback<number>): void` |
| `getSlotIndex` | `static int getSlotIndex(int)` | 7 | near | moderate | `getNrOptionMode` | `getNrOptionMode(slotId: number, callback: AsyncCallback<NrOptionMode>): void` |
| `getAccessibleSubscriptionInfoList` | `java.util.List<android.telephony.SubscriptionInfo> getAccessibleSubscriptionInfoList()` | 6 | near | moderate | `getActiveSimAccountInfoList` | `getActiveSimAccountInfoList(callback: AsyncCallback<Array<IccAccountInfo>>): void` |
| `getActiveDataSubscriptionId` | `static int getActiveDataSubscriptionId()` | 6 | partial | moderate | `getActiveSimAccountInfoList` | `getActiveSimAccountInfoList(callback: AsyncCallback<Array<IccAccountInfo>>): void` |
| `getActiveSubscriptionInfoCountMax` | `int getActiveSubscriptionInfoCountMax()` | 5 | partial | moderate | `getActiveSimAccountInfoList` | `getActiveSimAccountInfoList(callback: AsyncCallback<Array<IccAccountInfo>>): void` |
| `isNetworkRoaming` | `boolean isNetworkRoaming(int)` | 5 | partial | moderate | `disableCellularDataRoaming` | `disableCellularDataRoaming(slotId: number, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setSubscriptionPlans` | 5 | partial | Log warning + no-op |
| `isValidSubscriptionId` | 5 | partial | Return safe default (null/false/0/empty) |
| `setSubscriptionOverrideCongested` | 4 | partial | Log warning + no-op |
| `setSubscriptionOverrideUnmetered` | 4 | partial | Log warning + no-op |
| `isUsableSubscriptionId` | 4 | partial | Return safe default (null/false/0/empty) |
| `addOnSubscriptionsChangedListener` | 4 | composite | Return safe default (null/false/0/empty) |
| `addOnSubscriptionsChangedListener` | 4 | composite | Return safe default (null/false/0/empty) |
| `addOnOpportunisticSubscriptionsChangedListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `removeOnSubscriptionsChangedListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `removeOnOpportunisticSubscriptionsChangedListener` | 2 | composite | Return safe default (null/false/0/empty) |
| `canManageSubscription` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 9 methods that have score >= 5
2. Stub 11 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.SubscriptionManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.SubscriptionManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 20 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 9 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
