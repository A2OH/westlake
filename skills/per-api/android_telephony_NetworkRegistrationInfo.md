# SKILL: android.telephony.NetworkRegistrationInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.NetworkRegistrationInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.NetworkRegistrationInfo` |
| **Package** | `android.telephony` |
| **Total Methods** | 8 |
| **Avg Score** | 4.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (37%) |
| **Partial/Composite** | 4 (50%) |
| **No Mapping** | 1 (12%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getTransportType` | `int getTransportType()` | 7 | near | moderate | `getCardType` | `getCardType(slotId: number, callback: AsyncCallback<CardType>): void` |
| `getDomain` | `int getDomain()` | 6 | near | moderate | `getMainCallId` | `getMainCallId(callId: number, callback: AsyncCallback<number>): void` |
| `getAccessNetworkTechnology` | `int getAccessNetworkTechnology()` | 6 | near | moderate | `getNetworkSearchInformation` | `getNetworkSearchInformation(slotId: number, callback: AsyncCallback<NetworkSearchResult>): void` |
| `isRegistered` | `boolean isRegistered()` | 6 | partial | moderate | `isNrSupported` | `isNrSupported(): boolean` |
| `isRoaming` | `boolean isRoaming()` | 6 | partial | moderate | `isRadioOn` | `isRadioOn(slotId: number, callback: AsyncCallback<boolean>): void` |
| `isSearching` | `boolean isSearching()` | 5 | partial | moderate | `isNRSupported` | `isNRSupported(): boolean` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 6 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.NetworkRegistrationInfo`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.NetworkRegistrationInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
