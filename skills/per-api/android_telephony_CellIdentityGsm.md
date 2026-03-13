# SKILL: android.telephony.CellIdentityGsm

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.CellIdentityGsm`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.CellIdentityGsm` |
| **Package** | `android.telephony` |
| **Total Methods** | 5 |
| **Avg Score** | 5.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (40%) |
| **Partial/Composite** | 3 (60%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getCid` | `int getCid()` | 8 | near | easy | `getMEID` | `getMEID(slotId: number, callback: AsyncCallback<string>): void` |
| `getBsic` | `int getBsic()` | 7 | near | easy | `getIMSI` | `getIMSI(slotId: number, callback: AsyncCallback<string>): void` |
| `getLac` | `int getLac()` | 6 | partial | moderate | `getCallState` | `getCallState(callback: AsyncCallback<CallState>): void` |
| `getArfcn` | `int getArfcn()` | 5 | partial | moderate | `getMaxSimCount` | `getMaxSimCount(): number` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 3 | composite | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 4 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.CellIdentityGsm`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.CellIdentityGsm` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
