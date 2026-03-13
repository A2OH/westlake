# SKILL: android.telephony.SubscriptionPlan

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.SubscriptionPlan`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.SubscriptionPlan` |
| **Package** | `android.telephony` |
| **Total Methods** | 7 |
| **Avg Score** | 3.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 5 (71%) |
| **No Mapping** | 2 (28%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getDataUsageBytes` | `long getDataUsageBytes()` | 6 | partial | moderate | `getCellularDataState` | `getCellularDataState(callback: AsyncCallback<DataConnectState>): void` |
| `getDataUsageTime` | `long getDataUsageTime()` | 6 | partial | moderate | `getCellularDataState` | `getCellularDataState(callback: AsyncCallback<DataConnectState>): void` |
| `getDataLimitBytes` | `long getDataLimitBytes()` | 6 | partial | moderate | `getCellularDataFlowType` | `getCellularDataFlowType(callback: AsyncCallback<DataFlowType>): void` |
| `getDataLimitBehavior` | `int getDataLimitBehavior()` | 5 | partial | moderate | `getCellularDataState` | `getCellularDataState(callback: AsyncCallback<DataConnectState>): void` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `cycleIterator` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 4 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.SubscriptionPlan`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.SubscriptionPlan` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
