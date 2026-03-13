# SKILL: android.os.ParcelFileDescriptor

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.ParcelFileDescriptor`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.ParcelFileDescriptor` |
| **Package** | `android.os` |
| **Total Methods** | 24 |
| **Avg Score** | 6.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 16 (66%) |
| **Partial/Composite** | 7 (29%) |
| **No Mapping** | 1 (4%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 19 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `close` | `void close() throws java.io.IOException` | 10 | direct | trivial | `close` | `close(fd: number): Promise<void>` |
| `getFileDescriptor` | `java.io.FileDescriptor getFileDescriptor()` | 10 | direct | trivial | `getFileDescriptor` | `getFileDescriptor(pipe: USBDevicePipe): number` |
| `open` | `static android.os.ParcelFileDescriptor open(java.io.File, int) throws java.io.FileNotFoundException` | 10 | direct | trivial | `open` | `open(path: string, flags?: number, mode?: number): Promise<number>` |
| `open` | `static android.os.ParcelFileDescriptor open(java.io.File, int, android.os.Handler, android.os.ParcelFileDescriptor.OnCloseListener) throws java.io.IOException` | 10 | direct | trivial | `open` | `open(path: string, flags?: number, mode?: number): Promise<number>` |
| `getStatSize` | `long getStatSize()` | 8 | direct | easy | `getState` | `getState(): BluetoothState` |
| `ParcelFileDescriptor` | `ParcelFileDescriptor(android.os.ParcelFileDescriptor)` | 8 | direct | easy | `getFileDescriptor` | `getFileDescriptor(pipe: USBDevicePipe): number` |
| `dup` | `static android.os.ParcelFileDescriptor dup(java.io.FileDescriptor) throws java.io.IOException` | 8 | direct | easy | `dup` | `int dup(int)` |
| `dup` | `android.os.ParcelFileDescriptor dup() throws java.io.IOException` | 8 | direct | easy | `dup` | `int dup(int)` |
| `fromFd` | `static android.os.ParcelFileDescriptor fromFd(int) throws java.io.IOException` | 8 | direct | easy | `from` | `from(array: number[]): Buffer` |
| `getFd` | `int getFd()` | 8 | direct | easy | `getId` | `getId(): HiTraceId` |
| `createPipe` | `static android.os.ParcelFileDescriptor[] createPipe() throws java.io.IOException` | 8 | near | easy | `createTimer` | `createTimer(options: TimerOptions, callback: AsyncCallback<number>): void` |
| `createSocketPair` | `static android.os.ParcelFileDescriptor[] createSocketPair() throws java.io.IOException` | 7 | near | moderate | `createSpan` | `createSpan(): HiTraceId` |
| `checkError` | `void checkError() throws java.io.IOException` | 7 | near | moderate | `error` | `error(domain: number, tag: string, format: string, ...args: any[]): void` |
| `parseMode` | `static int parseMode(String)` | 7 | near | moderate | `parseUUID` | `parseUUID(uuid: string): Uint8Array` |
| `closeWithError` | `void closeWithError(String) throws java.io.IOException` | 6 | near | moderate | `closePipe` | `closePipe(pipe: USBDevicePipe): number` |
| `createReliablePipe` | `static android.os.ParcelFileDescriptor[] createReliablePipe() throws java.io.IOException` | 6 | near | moderate | `createStream` | `createStream(path: string, mode: string): Promise<Stream>` |
| `createReliableSocketPair` | `static android.os.ParcelFileDescriptor[] createReliableSocketPair() throws java.io.IOException` | 6 | partial | moderate | `createUriData` | `createUriData(uri: string): PasteData` |
| `fromSocket` | `static android.os.ParcelFileDescriptor fromSocket(java.net.Socket)` | 6 | partial | moderate | `from` | `from(array: number[]): Buffer` |
| `writeToParcel` | `void writeToParcel(android.os.Parcel, int)` | 6 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `adoptFd` | 4 | partial | throw UnsupportedOperationException |
| `fromDatagramSocket` | 4 | partial | throw UnsupportedOperationException |
| `detachFd` | 4 | partial | throw UnsupportedOperationException |
| `canDetectErrors` | 3 | composite | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 19 methods that have score >= 5
2. Stub 5 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.ParcelFileDescriptor`:


## Quality Gates

Before marking `android.os.ParcelFileDescriptor` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 24 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 19 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
