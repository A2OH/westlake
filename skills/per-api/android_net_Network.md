# SKILL: android.net.Network

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.Network`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.Network` |
| **Package** | `android.net` |
| **Total Methods** | 12 |
| **Avg Score** | 5.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (33%) |
| **Partial/Composite** | 5 (41%) |
| **No Mapping** | 3 (25%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 9 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getAllByName` | `java.net.InetAddress[] getAllByName(String) throws java.net.UnknownHostException` | 8 | near | easy | `getAddressesByName` | `getAddressesByName(host: string, callback: AsyncCallback<Array<NetAddress>>): void` |
| `getByName` | `java.net.InetAddress getByName(String) throws java.net.UnknownHostException` | 8 | near | easy | `getAddressesByName` | `getAddressesByName(host: string, callback: AsyncCallback<Array<NetAddress>>): void` |
| `openConnection` | `java.net.URLConnection openConnection(java.net.URL) throws java.io.IOException` | 7 | near | easy | `createNetConnection` | `createNetConnection(netSpecifier?: NetSpecifier, timeout?: number): NetConnection` |
| `openConnection` | `java.net.URLConnection openConnection(java.net.URL, java.net.Proxy) throws java.io.IOException` | 7 | near | easy | `createNetConnection` | `createNetConnection(netSpecifier?: NetSpecifier, timeout?: number): NetConnection` |
| `getNetworkHandle` | `long getNetworkHandle()` | 6 | partial | moderate | `getNetQuotaPolicies` | `getNetQuotaPolicies(callback: AsyncCallback<Array<NetQuotaPolicy>>): void` |
| `bindSocket` | `void bindSocket(java.net.DatagramSocket) throws java.io.IOException` | 6 | partial | moderate | `createWebSocket` | `createWebSocket(): WebSocket` |
| `bindSocket` | `void bindSocket(java.net.Socket) throws java.io.IOException` | 6 | partial | moderate | `createWebSocket` | `createWebSocket(): WebSocket` |
| `bindSocket` | `void bindSocket(java.io.FileDescriptor) throws java.io.IOException` | 6 | partial | moderate | `createWebSocket` | `createWebSocket(): WebSocket` |
| `getSocketFactory` | `javax.net.SocketFactory getSocketFactory()` | 5 | partial | moderate | `getStatsTotalBytes` | `getStatsTotalBytes(callback: AsyncCallback<number>): void` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `describeContents` | 1 | none | Store callback, never fire |
| `fromNetworkHandle` | 1 | none | throw UnsupportedOperationException |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 9 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.net.Network`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.Network` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 12 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 9 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
