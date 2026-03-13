# SKILL: android.net.LinkProperties

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.LinkProperties`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.LinkProperties` |
| **Package** | `android.net` |
| **Total Methods** | 16 |
| **Avg Score** | 5.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 7 (43%) |
| **Partial/Composite** | 7 (43%) |
| **No Mapping** | 2 (12%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 13 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `setHttpProxy` | `void setHttpProxy(@Nullable android.net.ProxyInfo)` | 9 | direct | easy | `setAppHttpProxy` | `setAppHttpProxy(httpProxy: HttpProxy): void` |
| `clear` | `void clear()` | 8 | direct | easy | `clear` | `clear(): void` |
| `isPrivateDnsActive` | `boolean isPrivateDnsActive()` | 7 | near | easy | `isIfaceActive` | `isIfaceActive(iface: string, callback: AsyncCallback<number>): void` |
| `isWakeOnLanSupported` | `boolean isWakeOnLanSupported()` | 7 | near | moderate | `isSharingSupported` | `isSharingSupported(callback: AsyncCallback<boolean>): void` |
| `LinkProperties` | `LinkProperties()` | 6 | near | moderate | `getConnectionProperties` | `getConnectionProperties(netHandle: NetHandle, callback: AsyncCallback<ConnectionProperties>): void` |
| `setLinkAddresses` | `void setLinkAddresses(@NonNull java.util.Collection<android.net.LinkAddress>)` | 6 | near | moderate | `getAddressesByName` | `getAddressesByName(host: string, callback: AsyncCallback<Array<NetAddress>>): void` |
| `setInterfaceName` | `void setInterfaceName(@Nullable String)` | 6 | near | moderate | `setIfaceConfig` | `setIfaceConfig(iface: string, ic: InterfaceConfiguration, callback: AsyncCallback<void>): void` |
| `setNat64Prefix` | `void setNat64Prefix(@Nullable android.net.IpPrefix)` | 6 | partial | moderate | `setAppHttpProxy` | `setAppHttpProxy(httpProxy: HttpProxy): void` |
| `setDhcpServerAddress` | `void setDhcpServerAddress(@Nullable java.net.Inet4Address)` | 5 | partial | moderate | `setPowerSaveTrustlist` | `setPowerSaveTrustlist(uids: Array<number>, isAllowed: boolean, callback: AsyncCallback<void>): void` |
| `getMtu` | `int getMtu()` | 5 | partial | moderate | `getAppNet` | `getAppNet(callback: AsyncCallback<NetHandle>): void` |
| `setMtu` | `void setMtu(int)` | 5 | partial | moderate | `setAppNet` | `setAppNet(netHandle: NetHandle, callback: AsyncCallback<void>): void` |
| `setDomains` | `void setDomains(@Nullable String)` | 5 | partial | moderate | `setAppNet` | `setAppNet(netHandle: NetHandle, callback: AsyncCallback<void>): void` |
| `addRoute` | `boolean addRoute(@NonNull android.net.RouteInfo)` | 5 | partial | moderate | `addCustomDnsRule` | `addCustomDnsRule(host: string, ip: Array<string>, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setDnsServers` | 5 | partial | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 13 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.net.LinkProperties`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.LinkProperties` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 16 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 13 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
