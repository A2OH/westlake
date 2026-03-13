# SKILL: android.telephony.CellSignalStrengthCdma

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.CellSignalStrengthCdma`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.CellSignalStrengthCdma` |
| **Package** | `android.telephony` |
| **Total Methods** | 11 |
| **Avg Score** | 5.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (27%) |
| **Partial/Composite** | 7 (63%) |
| **No Mapping** | 1 (9%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 9 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getCdmaEcio` | `int getCdmaEcio()` | 7 | near | moderate | `getIMEI` | `getIMEI(slotId: number, callback: AsyncCallback<string>): void` |
| `getDbm` | `int getDbm()` | 6 | near | moderate | `getIMEI` | `getIMEI(slotId: number, callback: AsyncCallback<string>): void` |
| `getEvdoEcio` | `int getEvdoEcio()` | 6 | near | moderate | `getRadioTech` | `getRadioTech(slotId: number, callback: AsyncCallback<NetworkRadioTech>): void` |
| `getEvdoDbm` | `int getEvdoDbm()` | 6 | partial | moderate | `getMEID` | `getMEID(slotId: number, callback: AsyncCallback<string>): void` |
| `getEvdoSnr` | `int getEvdoSnr()` | 6 | partial | moderate | `getMEID` | `getMEID(slotId: number, callback: AsyncCallback<string>): void` |
| `getCdmaLevel` | `int getCdmaLevel()` | 6 | partial | moderate | `getCallState` | `getCallState(callback: AsyncCallback<CallState>): void` |
| `getCdmaDbm` | `int getCdmaDbm()` | 6 | partial | moderate | `getSmscAddr` | `getSmscAddr(slotId: number, callback: AsyncCallback<string>): void` |
| `getEvdoLevel` | `int getEvdoLevel()` | 5 | partial | moderate | `getMEID` | `getMEID(slotId: number, callback: AsyncCallback<string>): void` |
| `getAsuLevel` | `int getAsuLevel()` | 5 | partial | moderate | `getCallState` | `getCallState(callback: AsyncCallback<CallState>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 9 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.CellSignalStrengthCdma`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.CellSignalStrengthCdma` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 9 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
