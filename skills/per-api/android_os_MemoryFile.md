# SKILL: android.os.MemoryFile

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.MemoryFile`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.MemoryFile` |
| **Package** | `android.os` |
| **Total Methods** | 7 |
| **Avg Score** | 7.5 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 6 (85%) |
| **Partial/Composite** | 1 (14%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `close` | `void close()` | 10 | direct | trivial | `close` | `close(fd: number): Promise<void>` |
| `length` | `int length()` | 10 | direct | trivial | `length` | `length: number` |
| `readBytes` | `int readBytes(byte[], int, int, int) throws java.io.IOException` | 7 | near | easy | `readText` | `readText(filePath: string,
  options?: {
    position?: number;
    length?: number;
    encoding?: string;
  }): Promise<string>` |
| `MemoryFile` | `MemoryFile(String, int) throws java.io.IOException` | 7 | near | moderate | `copyFile` | `copyFile(src: string | number, dest: string | number, mode?: number): Promise<void>` |
| `writeBytes` | `void writeBytes(byte[], int, int, int) throws java.io.IOException` | 7 | near | moderate | `write` | `write(data: number[]): Promise<void>` |
| `getInputStream` | `java.io.InputStream getInputStream()` | 6 | near | moderate | `getInstance` | `getInstance(locale?: string): IndexUtil` |
| `getOutputStream` | `java.io.OutputStream getOutputStream()` | 6 | partial | moderate | `createStream` | `createStream(path: string, mode: string): Promise<Stream>` |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.os.MemoryFile`:


## Quality Gates

Before marking `android.os.MemoryFile` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
