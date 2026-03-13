# SKILL: android.net.NetworkCapabilities

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.NetworkCapabilities`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.NetworkCapabilities` |
| **Package** | `android.net` |
| **Total Methods** | 10 |
| **Avg Score** | 4.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (20%) |
| **Partial/Composite** | 6 (60%) |
| **No Mapping** | 2 (20%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `NetworkCapabilities` | `NetworkCapabilities()` | 8 | direct | easy | `getNetCapabilities` | `getNetCapabilities(netHandle: NetHandle, callback: AsyncCallback<NetCapabilities>): void` |
| `NetworkCapabilities` | `NetworkCapabilities(android.net.NetworkCapabilities)` | 8 | direct | easy | `getNetCapabilities` | `getNetCapabilities(netHandle: NetHandle, callback: AsyncCallback<NetCapabilities>): void` |
| `getOwnerUid` | `int getOwnerUid()` | 6 | partial | moderate | `getPowerSaveTrustlist` | `getPowerSaveTrustlist(callback: AsyncCallback<Array<number>>): void` |
| `getSignalStrength` | `int getSignalStrength()` | 6 | partial | moderate | `getSharingState` | `getSharingState(type: SharingIfaceType, callback: AsyncCallback<SharingIfaceState>): void` |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `hasTransport` | 5 | partial | Return safe default (null/false/0/empty) |
| `hasCapability` | 5 | partial | Return safe default (null/false/0/empty) |
| `getLinkUpstreamBandwidthKbps` | 4 | partial | Return safe default (null/false/0/empty) |
| `getLinkDownstreamBandwidthKbps` | 4 | partial | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 4 methods that have score >= 5
2. Stub 6 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.net.NetworkCapabilities`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.NetworkCapabilities` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
