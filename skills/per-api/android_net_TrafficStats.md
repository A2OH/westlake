# SKILL: android.net.TrafficStats

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.TrafficStats`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.TrafficStats` |
| **Package** | `android.net` |
| **Total Methods** | 30 |
| **Avg Score** | 5.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 16 (53%) |
| **Partial/Composite** | 10 (33%) |
| **No Mapping** | 4 (13%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 23 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getUidRxBytes` | `static long getUidRxBytes(int)` | 10 | direct | trivial | `getUidRxBytes` | `getUidRxBytes(uid: number, callback: AsyncCallback<number>): void` |
| `getUidTxBytes` | `static long getUidTxBytes(int)` | 10 | direct | trivial | `getUidTxBytes` | `getUidTxBytes(uid: number, callback: AsyncCallback<number>): void` |
| `getTotalRxBytes` | `static long getTotalRxBytes()` | 9 | direct | easy | `getAllRxBytes` | `getAllRxBytes(callback: AsyncCallback<number>): void` |
| `getTotalTxBytes` | `static long getTotalTxBytes()` | 9 | direct | easy | `getAllTxBytes` | `getAllTxBytes(callback: AsyncCallback<number>): void` |
| `getMobileRxBytes` | `static long getMobileRxBytes()` | 8 | near | easy | `getIfaceRxBytes` | `getIfaceRxBytes(nic: string, callback: AsyncCallback<number>): void` |
| `getMobileTxBytes` | `static long getMobileTxBytes()` | 8 | near | easy | `getIfaceTxBytes` | `getIfaceTxBytes(nic: string, callback: AsyncCallback<number>): void` |
| `getThreadStatsUid` | `static int getThreadStatsUid()` | 8 | near | easy | `getTrafficStatsByUid` | `getTrafficStatsByUid(uidInfo: UidInfo, callback: AsyncCallback<NetStatsInfo>): void` |
| `TrafficStats` | `TrafficStats()` | 8 | near | easy | `getTrafficStatsByUid` | `getTrafficStatsByUid(uidInfo: UidInfo, callback: AsyncCallback<NetStatsInfo>): void` |
| `getUidRxPackets` | `static long getUidRxPackets(int)` | 8 | near | easy | `getUidRxBytes` | `getUidRxBytes(uid: number, callback: AsyncCallback<number>): void` |
| `getUidTxPackets` | `static long getUidTxPackets(int)` | 8 | near | easy | `getUidTxBytes` | `getUidTxBytes(uid: number, callback: AsyncCallback<number>): void` |
| `setThreadStatsUid` | `static void setThreadStatsUid(int)` | 7 | near | easy | `getTrafficStatsByUid` | `getTrafficStatsByUid(uidInfo: UidInfo, callback: AsyncCallback<NetStatsInfo>): void` |
| `getRxPackets` | `static long getRxPackets(@NonNull String)` | 6 | near | moderate | `getAllNets` | `getAllNets(callback: AsyncCallback<Array<NetHandle>>): void` |
| `getTxPackets` | `static long getTxPackets(@NonNull String)` | 6 | near | moderate | `getAllNets` | `getAllNets(callback: AsyncCallback<Array<NetHandle>>): void` |
| `getTotalTxPackets` | `static long getTotalTxPackets()` | 6 | near | moderate | `getStatsTotalBytes` | `getStatsTotalBytes(callback: AsyncCallback<number>): void` |
| `getThreadStatsTag` | `static int getThreadStatsTag()` | 6 | near | moderate | `getTrafficStatsByIface` | `getTrafficStatsByIface(ifaceInfo: IfaceInfo, callback: AsyncCallback<NetStatsInfo>): void` |
| `getTotalRxPackets` | `static long getTotalRxPackets()` | 6 | near | moderate | `getNetQuotaPolicies` | `getNetQuotaPolicies(callback: AsyncCallback<Array<NetQuotaPolicy>>): void` |
| `tagSocket` | `static void tagSocket(java.net.Socket) throws java.net.SocketException` | 6 | partial | moderate | `createWebSocket` | `createWebSocket(): WebSocket` |
| `setThreadStatsTag` | `static void setThreadStatsTag(int)` | 6 | partial | moderate | `getTrafficStatsByIface` | `getTrafficStatsByIface(ifaceInfo: IfaceInfo, callback: AsyncCallback<NetStatsInfo>): void` |
| `getMobileRxPackets` | `static long getMobileRxPackets()` | 6 | partial | moderate | `getSharableRegexes` | `getSharableRegexes(type: SharingIfaceType, callback: AsyncCallback<Array<string>>): void` |
| `getMobileTxPackets` | `static long getMobileTxPackets()` | 5 | partial | moderate | `getIfaceRxBytes` | `getIfaceRxBytes(nic: string, callback: AsyncCallback<number>): void` |
| `untagSocket` | `static void untagSocket(java.net.Socket) throws java.net.SocketException` | 5 | partial | moderate | `createWebSocket` | `createWebSocket(): WebSocket` |
| `getAndSetThreadStatsTag` | `static int getAndSetThreadStatsTag(int)` | 5 | partial | moderate | `getPowerSaveTrustlist` | `getPowerSaveTrustlist(callback: AsyncCallback<Array<number>>): void` |
| `tagDatagramSocket` | `static void tagDatagramSocket(java.net.DatagramSocket) throws java.net.SocketException` | 5 | partial | moderate | `createWebSocket` | `createWebSocket(): WebSocket` |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `clearThreadStatsTag` | 5 | partial | Return safe default (null/false/0/empty) |
| `clearThreadStatsUid` | 5 | partial | Return safe default (null/false/0/empty) |
| `untagDatagramSocket` | 5 | partial | throw UnsupportedOperationException |
| `incrementOperationCount` | 1 | none | Store callback, never fire |
| `incrementOperationCount` | 1 | none | Store callback, never fire |
| `tagFileDescriptor` | 1 | none | throw UnsupportedOperationException |
| `untagFileDescriptor` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 23 methods that have score >= 5
2. Stub 7 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.net.TrafficStats`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.TrafficStats` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 30 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 23 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
