# SKILL: android.net.ConnectivityManager.NetworkCallback

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.ConnectivityManager.NetworkCallback`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.ConnectivityManager.NetworkCallback` |
| **Package** | `android.net.ConnectivityManager` |
| **Total Methods** | 8 |
| **Avg Score** | 4.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (25%) |
| **Partial/Composite** | 5 (62%) |
| **No Mapping** | 1 (12%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `onCapabilitiesChanged` | `void onCapabilitiesChanged(@NonNull android.net.Network, @NonNull android.net.NetworkCapabilities)` | 7 | near | moderate | `getNetCapabilities` | `getNetCapabilities(netHandle: NetHandle, callback: AsyncCallback<NetCapabilities>): void` |
| `onLinkPropertiesChanged` | `void onLinkPropertiesChanged(@NonNull android.net.Network, @NonNull android.net.LinkProperties)` | 6 | near | moderate | `getConnectionProperties` | `getConnectionProperties(netHandle: NetHandle, callback: AsyncCallback<ConnectionProperties>): void` |
| `onAvailable` | `void onAvailable(@NonNull android.net.Network)` | 5 | partial | moderate | `on` | `on(type: 'interfaceStateChange', callback: Callback<InterfaceStateInfo>): void` |
| `onLosing` | `void onLosing(@NonNull android.net.Network, int)` | 5 | partial | moderate | `on` | `on(type: 'interfaceStateChange', callback: Callback<InterfaceStateInfo>): void` |
| `onLost` | `void onLost(@NonNull android.net.Network)` | 5 | partial | moderate | `on` | `on(type: 'interfaceStateChange', callback: Callback<InterfaceStateInfo>): void` |
| `onUnavailable` | `void onUnavailable()` | 5 | partial | moderate | `on` | `on(type: 'interfaceStateChange', callback: Callback<InterfaceStateInfo>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onBlockedStatusChanged` | 3 | composite | Store callback, never fire |
| `NetworkCallback` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 6 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.net.ConnectivityManager.NetworkCallback`:


## Quality Gates

Before marking `android.net.ConnectivityManager.NetworkCallback` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
