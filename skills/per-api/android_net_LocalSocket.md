# SKILL: android.net.LocalSocket

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.LocalSocket`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.LocalSocket` |
| **Package** | `android.net` |
| **Total Methods** | 27 |
| **Avg Score** | 5.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 8 (29%) |
| **Partial/Composite** | 17 (62%) |
| **No Mapping** | 2 (7%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 19 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `bind` | `void bind(android.net.LocalSocketAddress) throws java.io.IOException` | 8 | direct | easy | `bind` | `int bind(int, const struct sockaddr *, socklen_t)` |
| `close` | `void close() throws java.io.IOException` | 8 | direct | easy | `close` | `close(file: number | File): Promise<void>` |
| `getFileDescriptor` | `java.io.FileDescriptor getFileDescriptor()` | 8 | direct | easy | `getFileDescriptor` | `getFileDescriptor(pipe: USBDevicePipe): number` |
| `connect` | `void connect(android.net.LocalSocketAddress) throws java.io.IOException` | 7 | near | easy | `connect` | `int connect(int, const struct sockaddr *, socklen_t)` |
| `connect` | `void connect(android.net.LocalSocketAddress, int) throws java.io.IOException` | 7 | near | easy | `connect` | `int connect(int, const struct sockaddr *, socklen_t)` |
| `isConnected` | `boolean isConnected()` | 6 | near | moderate | `reportNetConnected` | `reportNetConnected(netHandle: NetHandle, callback: AsyncCallback<void>): void` |
| `LocalSocket` | `LocalSocket()` | 6 | near | moderate | `addLocalService` | `addLocalService(context: Context, serviceInfo: LocalServiceInfo,
    callback: AsyncCallback<LocalServiceInfo>): void` |
| `LocalSocket` | `LocalSocket(int)` | 6 | near | moderate | `addLocalService` | `addLocalService(context: Context, serviceInfo: LocalServiceInfo,
    callback: AsyncCallback<LocalServiceInfo>): void` |
| `getLocalSocketAddress` | `android.net.LocalSocketAddress getLocalSocketAddress()` | 6 | partial | moderate | `getSockfdRxBytes` | `getSockfdRxBytes(sockfd: number, callback: AsyncCallback<number>): void` |
| `getRemoteSocketAddress` | `android.net.LocalSocketAddress getRemoteSocketAddress()` | 6 | partial | moderate | `getSockfdRxBytes` | `getSockfdRxBytes(sockfd: number, callback: AsyncCallback<number>): void` |
| `getInputStream` | `java.io.InputStream getInputStream() throws java.io.IOException` | 6 | partial | moderate | `getSharingState` | `getSharingState(type: SharingIfaceType, callback: AsyncCallback<SharingIfaceState>): void` |
| `isBound` | `boolean isBound()` | 5 | partial | moderate | `isBackgroundAllowed` | `isBackgroundAllowed(callback: AsyncCallback<boolean>): void` |
| `getOutputStream` | `java.io.OutputStream getOutputStream() throws java.io.IOException` | 5 | partial | moderate | `getStatsRxBytes` | `getStatsRxBytes(callback: AsyncCallback<number>): void` |
| `getReceiveBufferSize` | `int getReceiveBufferSize() throws java.io.IOException` | 5 | partial | moderate | `getAllActiveIfaces` | `getAllActiveIfaces(callback: AsyncCallback<Array<string>>): void` |
| `setReceiveBufferSize` | `void setReceiveBufferSize(int) throws java.io.IOException` | 5 | partial | moderate | `setDeviceIdleTrustlist` | `setDeviceIdleTrustlist(uids: Array<number>, isAllowed: boolean, callback: AsyncCallback<void>): void` |
| `isClosed` | `boolean isClosed()` | 5 | partial | moderate | `isUidNetAllowed` | `isUidNetAllowed(uid: number, isMetered: boolean, callback: AsyncCallback<boolean>): void` |
| `getPeerCredentials` | `android.net.Credentials getPeerCredentials() throws java.io.IOException` | 5 | partial | moderate | `getPowerSaveTrustlist` | `getPowerSaveTrustlist(callback: AsyncCallback<Array<number>>): void` |
| `getAncillaryFileDescriptors` | `java.io.FileDescriptor[] getAncillaryFileDescriptors() throws java.io.IOException` | 5 | partial | moderate | `getAllRxBytes` | `getAllRxBytes(callback: AsyncCallback<number>): void` |
| `getSoTimeout` | `int getSoTimeout() throws java.io.IOException` | 5 | partial | moderate | `getSockfdRxBytes` | `getSockfdRxBytes(sockfd: number, callback: AsyncCallback<number>): void` |

## Stub APIs (score < 5): 8 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isInputShutdown` | 5 | partial | Return safe default (null/false/0/empty) |
| `setSoTimeout` | 5 | partial | Log warning + no-op |
| `getSendBufferSize` | 5 | partial | Return safe default (null/false/0/empty) |
| `setSendBufferSize` | 5 | partial | Log warning + no-op |
| `setFileDescriptorsForSend` | 4 | partial | Log warning + no-op |
| `isOutputShutdown` | 4 | partial | Return safe default (null/false/0/empty) |
| `shutdownInput` | 1 | none | Log warning + no-op |
| `shutdownOutput` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 19 methods that have score >= 5
2. Stub 8 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.net.LocalSocket`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.LocalSocket` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 27 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 19 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
