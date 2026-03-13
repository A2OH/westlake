# SKILL: android.telephony.PhoneStateListener

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.PhoneStateListener`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.PhoneStateListener` |
| **Package** | `android.telephony` |
| **Total Methods** | 16 |
| **Avg Score** | 4.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (6%) |
| **Partial/Composite** | 15 (93%) |
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
| `onCallStateChanged` | `void onCallStateChanged(int, String)` | 6 | near | moderate | `getCallState` | `getCallState(callback: AsyncCallback<CallState>): void` |
| `onCellInfoChanged` | `void onCellInfoChanged(java.util.List<android.telephony.CellInfo>)` | 6 | partial | moderate | `getCellInformation` | `getCellInformation(slotId: number, callback: AsyncCallback<Array<CellInformation>>): void` |
| `onCellLocationChanged` | `void onCellLocationChanged(android.telephony.CellLocation)` | 6 | partial | moderate | `sendUpdateCellLocationRequest` | `sendUpdateCellLocationRequest(slotId: number, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 13 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `PhoneStateListener` | 5 | partial | Return safe default (null/false/0/empty) |
| `PhoneStateListener` | 5 | partial | Return safe default (null/false/0/empty) |
| `onDataConnectionStateChanged` | 5 | partial | Return dummy instance / no-op |
| `onDataConnectionStateChanged` | 5 | partial | Return dummy instance / no-op |
| `onUserMobileDataStateChanged` | 5 | partial | Store callback, never fire |
| `onServiceStateChanged` | 4 | partial | Store callback, never fire |
| `onCallForwardingIndicatorChanged` | 4 | partial | Store callback, never fire |
| `onMessageWaitingIndicatorChanged` | 4 | partial | Store callback, never fire |
| `onActiveDataSubscriptionIdChanged` | 4 | partial | Store callback, never fire |
| `onBarringInfoChanged` | 4 | composite | Store callback, never fire |
| `onRegistrationFailed` | 4 | composite | Return safe default (null/false/0/empty) |
| `onSignalStrengthsChanged` | 4 | composite | Store callback, never fire |
| `onDataActivity` | 4 | composite | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 13 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.PhoneStateListener`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.PhoneStateListener` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 16 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
