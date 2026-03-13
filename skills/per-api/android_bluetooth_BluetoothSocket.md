# SKILL: android.bluetooth.BluetoothSocket

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothSocket`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothSocket` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 9 |
| **Avg Score** | 6.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (44%) |
| **Partial/Composite** | 5 (55%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getRemoteDevice` | `android.bluetooth.BluetoothDevice getRemoteDevice()` | 9 | direct | easy | `getRemoteDeviceName` | `getRemoteDeviceName(deviceId: string): string` |
| `getConnectionType` | `int getConnectionType()` | 8 | direct | easy | `getBtConnectionState` | `getBtConnectionState(): ProfileConnectionState` |
| `close` | `void close() throws java.io.IOException` | 8 | direct | easy | `close` | `close(file: number | File): Promise<void>` |
| `connect` | `void connect() throws java.io.IOException` | 7 | near | easy | `connect` | `int connect(int, const struct sockaddr *, socklen_t)` |
| `getInputStream` | `java.io.InputStream getInputStream() throws java.io.IOException` | 5 | partial | moderate | `getState` | `getState(): BluetoothState` |
| `isConnected` | `boolean isConnected()` | 5 | partial | moderate | `getConnectedBLEDevices` | `getConnectedBLEDevices(): Array<string>` |
| `getOutputStream` | `java.io.OutputStream getOutputStream() throws java.io.IOException` | 5 | partial | moderate | `getState` | `getState(): BluetoothState` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getMaxReceivePacketSize` | 5 | partial | Return safe default (null/false/0/empty) |
| `getMaxTransmitPacketSize` | 4 | partial | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 7 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothSocket`:


## Quality Gates

Before marking `android.bluetooth.BluetoothSocket` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
