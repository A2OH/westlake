# SKILL: android.telephony.CellIdentityCdma

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.CellIdentityCdma`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.CellIdentityCdma` |
| **Package** | `android.telephony` |
| **Total Methods** | 6 |
| **Avg Score** | 5.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (50%) |
| **Partial/Composite** | 3 (50%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getNetworkId` | `int getNetworkId()` | 7 | near | easy | `getNetworkState` | `getNetworkState(slotId: number, callback: AsyncCallback<NetworkState>): void` |
| `getSystemId` | `int getSystemId()` | 7 | near | moderate | `getMEID` | `getMEID(slotId: number, callback: AsyncCallback<string>): void` |
| `getLatitude` | `int getLatitude()` | 6 | near | moderate | `getCallState` | `getCallState(callback: AsyncCallback<CallState>): void` |
| `getBasestationId` | `int getBasestationId()` | 6 | partial | moderate | `getSimState` | `getSimState(slotId: number, callback: AsyncCallback<SimState>): void` |
| `getLongitude` | `int getLongitude()` | 6 | partial | moderate | `getUniqueDeviceId` | `getUniqueDeviceId(slotId: number, callback: AsyncCallback<string>): void` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 3 | composite | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.CellIdentityCdma`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.CellIdentityCdma` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 6 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
