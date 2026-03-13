# SKILL: android.telephony.CellSignalStrengthLte

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.CellSignalStrengthLte`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.CellSignalStrengthLte` |
| **Package** | `android.telephony` |
| **Total Methods** | 10 |
| **Avg Score** | 5.4 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 5 (50%) |
| **Partial/Composite** | 4 (40%) |
| **No Mapping** | 1 (10%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getRssi` | `int getRssi()` | 7 | near | easy | `getIMSI` | `getIMSI(slotId: number, callback: AsyncCallback<string>): void` |
| `getRssnr` | `int getRssnr()` | 7 | near | easy | `getSimSpn` | `getSimSpn(slotId: number, callback: AsyncCallback<string>): void` |
| `getRsrp` | `int getRsrp()` | 6 | near | moderate | `getSimSpn` | `getSimSpn(slotId: number, callback: AsyncCallback<string>): void` |
| `getCqi` | `int getCqi()` | 6 | near | moderate | `getIMEI` | `getIMEI(slotId: number, callback: AsyncCallback<string>): void` |
| `getDbm` | `int getDbm()` | 6 | near | moderate | `getIMEI` | `getIMEI(slotId: number, callback: AsyncCallback<string>): void` |
| `getRsrq` | `int getRsrq()` | 6 | partial | moderate | `getIMSI` | `getIMSI(slotId: number, callback: AsyncCallback<string>): void` |
| `getTimingAdvance` | `int getTimingAdvance()` | 6 | partial | moderate | `getMainCallId` | `getMainCallId(callId: number, callback: AsyncCallback<number>): void` |
| `getAsuLevel` | `int getAsuLevel()` | 5 | partial | moderate | `getCallState` | `getCallState(callback: AsyncCallback<CallState>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 8 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.CellSignalStrengthLte`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.CellSignalStrengthLte` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
