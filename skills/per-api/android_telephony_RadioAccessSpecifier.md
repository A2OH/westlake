# SKILL: android.telephony.RadioAccessSpecifier

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.RadioAccessSpecifier`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.RadioAccessSpecifier` |
| **Package** | `android.telephony` |
| **Total Methods** | 6 |
| **Avg Score** | 4.4 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (16%) |
| **Partial/Composite** | 4 (66%) |
| **No Mapping** | 1 (16%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getChannels` | `int[] getChannels()` | 6 | near | moderate | `getCallState` | `getCallState(callback: AsyncCallback<CallState>): void` |
| `getBands` | `int[] getBands()` | 6 | partial | moderate | `getSubCallIdList` | `getSubCallIdList(callId: number, callback: AsyncCallback<Array<string>>): void` |
| `getRadioAccessNetwork` | `int getRadioAccessNetwork()` | 6 | partial | moderate | `getNetworkState` | `getNetworkState(slotId: number, callback: AsyncCallback<NetworkState>): void` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `RadioAccessSpecifier` | 5 | partial | throw UnsupportedOperationException |
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.RadioAccessSpecifier`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.RadioAccessSpecifier` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 6 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
