# SKILL: android.telephony.CellLocation

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.CellLocation`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.CellLocation` |
| **Package** | `android.telephony` |
| **Total Methods** | 3 |
| **Avg Score** | 5.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (33%) |
| **Partial/Composite** | 2 (66%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `CellLocation` | `CellLocation()` | 7 | near | moderate | `getCellInformation` | `getCellInformation(slotId: number, callback: AsyncCallback<Array<CellInformation>>): void` |
| `getEmpty` | `static android.telephony.CellLocation getEmpty()` | 6 | partial | moderate | `getSimSpn` | `getSimSpn(slotId: number, callback: AsyncCallback<string>): void` |
| `requestLocationUpdate` | `static void requestLocationUpdate()` | 5 | partial | moderate | `sendUpdateCellLocationRequest` | `sendUpdateCellLocationRequest(slotId: number, callback: AsyncCallback<void>): void` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.CellLocation`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.CellLocation` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 3 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
