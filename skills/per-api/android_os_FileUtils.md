# SKILL: android.os.FileUtils

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.FileUtils`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.FileUtils` |
| **Package** | `android.os` |
| **Total Methods** | 4 |
| **Avg Score** | 10.0 |
| **Scenario** | S1: Direct Mapping (Thin Wrapper) |
| **Strategy** | Simple delegation to OHBridge |
| **Direct/Near** | 4 (100%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `copy` | `static long copy(@NonNull java.io.InputStream, @NonNull java.io.OutputStream) throws java.io.IOException` | 10 | direct | trivial | `copy` | `copy(logType: string, logName: string, dest: string): Promise<void>` |
| `copy` | `static long copy(@NonNull java.io.InputStream, @NonNull java.io.OutputStream, @Nullable android.os.CancellationSignal, @Nullable java.util.concurrent.Executor, @Nullable android.os.FileUtils.ProgressListener) throws java.io.IOException` | 10 | direct | trivial | `copy` | `copy(logType: string, logName: string, dest: string): Promise<void>` |
| `copy` | `static long copy(@NonNull java.io.FileDescriptor, @NonNull java.io.FileDescriptor) throws java.io.IOException` | 10 | direct | trivial | `copy` | `copy(logType: string, logName: string, dest: string): Promise<void>` |
| `copy` | `static long copy(@NonNull java.io.FileDescriptor, @NonNull java.io.FileDescriptor, @Nullable android.os.CancellationSignal, @Nullable java.util.concurrent.Executor, @Nullable android.os.FileUtils.ProgressListener) throws java.io.IOException` | 10 | direct | trivial | `copy` | `copy(logType: string, logName: string, dest: string): Promise<void>` |

## AI Agent Instructions

**Scenario: S1 — Direct Mapping (Thin Wrapper)**

1. Create Java shim at `shim/java/android/os/FileUtils.java`
2. For each method, delegate to `OHBridge.xxx()` — one bridge call per Android call
3. Add `static native` declarations to `OHBridge.java`
4. Add mock implementations to `test-apps/mock/.../OHBridge.java`
5. Add test section to `HeadlessTest.java` — call each method with valid + edge inputs
6. Test null args, boundary values, return types

## Dependencies

Check if these related classes are already shimmed before generating `android.os.FileUtils`:


## Quality Gates

Before marking `android.os.FileUtils` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
