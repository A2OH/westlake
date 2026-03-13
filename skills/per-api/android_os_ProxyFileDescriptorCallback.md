# SKILL: android.os.ProxyFileDescriptorCallback

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.ProxyFileDescriptorCallback`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.ProxyFileDescriptorCallback` |
| **Package** | `android.os` |
| **Total Methods** | 6 |
| **Avg Score** | 7.3 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 6 (100%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `onFsync` | `void onFsync() throws android.system.ErrnoException` | 8 | direct | easy | `fsync` | `fsync(fd: number): Promise<void>` |
| `onWrite` | `int onWrite(long, int, byte[]) throws android.system.ErrnoException` | 8 | direct | easy | `write` | `write(data: number[]): Promise<void>` |
| `onRead` | `int onRead(long, int, byte[]) throws android.system.ErrnoException` | 8 | direct | easy | `read` | `read(): Promise<number[]>` |
| `onGetSize` | `long onGetSize() throws android.system.ErrnoException` | 7 | near | moderate | `imageSize` | `imageSize?: Size` |
| `ProxyFileDescriptorCallback` | `ProxyFileDescriptorCallback()` | 6 | near | moderate | `getFileDescriptor` | `getFileDescriptor(pipe: USBDevicePipe): number` |
| `onRelease` | `abstract void onRelease()` | 6 | near | moderate | `onResponse` | `onResponse?: (notificationId: number, buttonOptions: ButtonOptions) => void` |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.os.ProxyFileDescriptorCallback`:


## Quality Gates

Before marking `android.os.ProxyFileDescriptorCallback` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 6 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
