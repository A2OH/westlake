# SKILL: android.telephony.SubscriptionPlan.Builder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.SubscriptionPlan.Builder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.SubscriptionPlan.Builder` |
| **Package** | `android.telephony.SubscriptionPlan` |
| **Total Methods** | 7 |
| **Avg Score** | 4.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (14%) |
| **Partial/Composite** | 5 (71%) |
| **No Mapping** | 1 (14%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `setSummary` | `android.telephony.SubscriptionPlan.Builder setSummary(@Nullable CharSequence)` | 7 | near | moderate | `setSmscAddr` | `setSmscAddr(slotId: number, smscAddr: string, callback: AsyncCallback<void>): void` |
| `createRecurring` | `static android.telephony.SubscriptionPlan.Builder createRecurring(java.time.ZonedDateTime, java.time.Period)` | 6 | partial | moderate | `createMessage` | `createMessage(pdu: Array<number>, specification: string, callback: AsyncCallback<ShortMessage>): void` |
| `setDataUsage` | `android.telephony.SubscriptionPlan.Builder setDataUsage(long, long)` | 6 | partial | moderate | `getCellularDataState` | `getCellularDataState(callback: AsyncCallback<DataConnectState>): void` |
| `setDataLimit` | `android.telephony.SubscriptionPlan.Builder setDataLimit(long, int)` | 5 | partial | moderate | `setCallWaiting` | `setCallWaiting(slotId: number, activate: boolean, callback: AsyncCallback<void>): void` |
| `createNonrecurring` | `static android.telephony.SubscriptionPlan.Builder createNonrecurring(java.time.ZonedDateTime, java.time.ZonedDateTime)` | 5 | partial | moderate | `createMessage` | `createMessage(pdu: Array<number>, specification: string, callback: AsyncCallback<ShortMessage>): void` |
| `setTitle` | `android.telephony.SubscriptionPlan.Builder setTitle(@Nullable CharSequence)` | 5 | partial | moderate | `setCBConfig` | `setCBConfig(options: CBConfigOptions, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `build` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 6 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.SubscriptionPlan.Builder`:


## Quality Gates

Before marking `android.telephony.SubscriptionPlan.Builder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
