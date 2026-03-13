# SKILL: android.net.LocalServerSocket

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.LocalServerSocket`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.LocalServerSocket` |
| **Package** | `android.net` |
| **Total Methods** | 6 |
| **Avg Score** | 7.1 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 5 (83%) |
| **Partial/Composite** | 1 (16%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `accept` | `android.net.LocalSocket accept() throws java.io.IOException` | 8 | direct | easy | `accept` | `int accept(int, struct sockaddr *__restrict, socklen_t *__restrict)` |
| `close` | `void close() throws java.io.IOException` | 8 | direct | easy | `close` | `close(file: number | File): Promise<void>` |
| `getFileDescriptor` | `java.io.FileDescriptor getFileDescriptor()` | 8 | direct | easy | `getFileDescriptor` | `getFileDescriptor(pipe: USBDevicePipe): number` |
| `LocalServerSocket` | `LocalServerSocket(String) throws java.io.IOException` | 6 | near | moderate | `addLocalService` | `addLocalService(context: Context, serviceInfo: LocalServiceInfo,
    callback: AsyncCallback<LocalServiceInfo>): void` |
| `LocalServerSocket` | `LocalServerSocket(java.io.FileDescriptor) throws java.io.IOException` | 6 | near | moderate | `addLocalService` | `addLocalService(context: Context, serviceInfo: LocalServiceInfo,
    callback: AsyncCallback<LocalServiceInfo>): void` |
| `getLocalSocketAddress` | `android.net.LocalSocketAddress getLocalSocketAddress()` | 6 | partial | moderate | `getSockfdRxBytes` | `getSockfdRxBytes(sockfd: number, callback: AsyncCallback<number>): void` |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.net.LocalServerSocket`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.LocalServerSocket` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 6 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
