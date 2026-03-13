# SKILL: android.os.DropBoxManager.Entry

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.DropBoxManager.Entry`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.DropBoxManager.Entry` |
| **Package** | `android.os.DropBoxManager` |
| **Total Methods** | 13 |
| **Avg Score** | 5.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 6 (46%) |
| **Partial/Composite** | 6 (46%) |
| **No Mapping** | 1 (7%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 12 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `close` | `void close()` | 10 | direct | trivial | `close` | `close(fd: number): Promise<void>` |
| `getTag` | `String getTag()` | 7 | near | easy | `getState` | `getState(): BluetoothState` |
| `getText` | `String getText(int)` | 7 | near | easy | `getTime` | `getTime(isNanoseconds?: boolean): number` |
| `getTimeMillis` | `long getTimeMillis()` | 7 | near | easy | `getTime` | `getTime(isNanoseconds?: boolean): number` |
| `getFlags` | `int getFlags()` | 7 | near | moderate | `getFoldStatus` | `getFoldStatus(): FoldStatus` |
| `getInputStream` | `java.io.InputStream getInputStream() throws java.io.IOException` | 6 | near | moderate | `getInstance` | `getInstance(locale?: string): IndexUtil` |
| `writeToParcel` | `void writeToParcel(android.os.Parcel, int)` | 6 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |
| `Entry` | `DropBoxManager.Entry(String, long)` | 5 | partial | moderate | `entryModuleName` | `readonly entryModuleName: string` |
| `Entry` | `DropBoxManager.Entry(String, long, String)` | 5 | partial | moderate | `entryModuleName` | `readonly entryModuleName: string` |
| `Entry` | `DropBoxManager.Entry(String, long, byte[], int)` | 5 | partial | moderate | `entryModuleName` | `readonly entryModuleName: string` |
| `Entry` | `DropBoxManager.Entry(String, long, android.os.ParcelFileDescriptor, int)` | 5 | partial | moderate | `entryModuleName` | `readonly entryModuleName: string` |
| `Entry` | `DropBoxManager.Entry(String, long, java.io.File, int) throws java.io.IOException` | 5 | partial | moderate | `entryModuleName` | `readonly entryModuleName: string` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 12 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.DropBoxManager.Entry`:


## Quality Gates

Before marking `android.os.DropBoxManager.Entry` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 13 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 12 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
