# SKILL: android.telephony.BarringInfo.BarringServiceInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.BarringInfo.BarringServiceInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.BarringInfo.BarringServiceInfo` |
| **Package** | `android.telephony.BarringInfo` |
| **Total Methods** | 7 |
| **Avg Score** | 4.5 |
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

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getBarringType` | `int getBarringType()` | 7 | near | easy | `getCardType` | `getCardType(slotId: number, callback: AsyncCallback<CardType>): void` |
| `isBarred` | `boolean isBarred()` | 6 | partial | moderate | `isNrSupported` | `isNrSupported(): boolean` |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isConditionallyBarred` | 5 | partial | Return safe default (null/false/0/empty) |
| `getConditionalBarringFactor` | 5 | partial | Return safe default (null/false/0/empty) |
| `getConditionalBarringTimeSeconds` | 5 | partial | Return safe default (null/false/0/empty) |
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 2 methods that have score >= 5
2. Stub 5 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.BarringInfo.BarringServiceInfo`:


## Quality Gates

Before marking `android.telephony.BarringInfo.BarringServiceInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
