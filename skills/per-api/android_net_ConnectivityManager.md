# SKILL: android.net.ConnectivityManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.ConnectivityManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.ConnectivityManager` |
| **Package** | `android.net` |
| **Total Methods** | 16 |
| **Avg Score** | 7.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 14 (87%) |
| **Partial/Composite** | 2 (12%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 16 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getConnectionOwnerUid` | `int getConnectionOwnerUid(int, @NonNull java.net.InetSocketAddress, @NonNull java.net.InetSocketAddress)` | 10 | direct | trivial | `getConnectionProperties` | `getConnectionProperties(netHandle: NetHandle, callback: AsyncCallback<ConnectionProperties>): void` |
| `reportNetworkConnectivity` | `void reportNetworkConnectivity(@Nullable android.net.Network, boolean)` | 9 | direct | trivial | `reportNetConnected` | `reportNetConnected(netHandle: NetHandle, callback: AsyncCallback<void>): void` |
| `isDefaultNetworkActive` | `boolean isDefaultNetworkActive()` | 9 | direct | easy | `isDefaultNetMetered` | `isDefaultNetMetered(callback: AsyncCallback<boolean>): void` |
| `addDefaultNetworkActiveListener` | `void addDefaultNetworkActiveListener(android.net.ConnectivityManager.OnNetworkActiveListener)` | 8 | near | easy | `isDefaultNetMetered` | `isDefaultNetMetered(callback: AsyncCallback<boolean>): void` |
| `removeDefaultNetworkActiveListener` | `void removeDefaultNetworkActiveListener(@NonNull android.net.ConnectivityManager.OnNetworkActiveListener)` | 7 | near | easy | `isDefaultNetMetered` | `isDefaultNetMetered(callback: AsyncCallback<boolean>): void` |
| `releaseNetworkRequest` | `void releaseNetworkRequest(@NonNull android.app.PendingIntent)` | 7 | near | easy | `createNetConnection` | `createNetConnection(netSpecifier?: NetSpecifier, timeout?: number): NetConnection` |
| `requestNetwork` | `void requestNetwork(@NonNull android.net.NetworkRequest, @NonNull android.net.ConnectivityManager.NetworkCallback)` | 6 | near | moderate | `getDefaultNet` | `getDefaultNet(callback: AsyncCallback<NetHandle>): void` |
| `requestNetwork` | `void requestNetwork(@NonNull android.net.NetworkRequest, @NonNull android.net.ConnectivityManager.NetworkCallback, @NonNull android.os.Handler)` | 6 | near | moderate | `getDefaultNet` | `getDefaultNet(callback: AsyncCallback<NetHandle>): void` |
| `requestNetwork` | `void requestNetwork(@NonNull android.net.NetworkRequest, @NonNull android.net.ConnectivityManager.NetworkCallback, int)` | 6 | near | moderate | `getDefaultNet` | `getDefaultNet(callback: AsyncCallback<NetHandle>): void` |
| `requestNetwork` | `void requestNetwork(@NonNull android.net.NetworkRequest, @NonNull android.net.ConnectivityManager.NetworkCallback, @NonNull android.os.Handler, int)` | 6 | near | moderate | `getDefaultNet` | `getDefaultNet(callback: AsyncCallback<NetHandle>): void` |
| `requestNetwork` | `void requestNetwork(@NonNull android.net.NetworkRequest, @NonNull android.app.PendingIntent)` | 6 | near | moderate | `getDefaultNet` | `getDefaultNet(callback: AsyncCallback<NetHandle>): void` |
| `bindProcessToNetwork` | `boolean bindProcessToNetwork(@Nullable android.net.Network)` | 6 | near | moderate | `isDefaultNetMetered` | `isDefaultNetMetered(callback: AsyncCallback<boolean>): void` |
| `unregisterNetworkCallback` | `void unregisterNetworkCallback(@NonNull android.net.ConnectivityManager.NetworkCallback)` | 6 | near | moderate | `createNetConnection` | `createNetConnection(netSpecifier?: NetSpecifier, timeout?: number): NetConnection` |
| `unregisterNetworkCallback` | `void unregisterNetworkCallback(@NonNull android.app.PendingIntent)` | 6 | near | moderate | `createNetConnection` | `createNetConnection(netSpecifier?: NetSpecifier, timeout?: number): NetConnection` |
| `requestBandwidthUpdate` | `boolean requestBandwidthUpdate(@NonNull android.net.Network)` | 6 | partial | moderate | `removeCustomDnsRule` | `removeCustomDnsRule(host: string, callback: AsyncCallback<void>): void` |
| `getRestrictBackgroundStatus` | `int getRestrictBackgroundStatus()` | 6 | partial | moderate | `getAddressesByName` | `getAddressesByName(host: string, callback: AsyncCallback<Array<NetAddress>>): void` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 16 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.net.ConnectivityManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.ConnectivityManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 16 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 16 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
