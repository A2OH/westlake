# SKILL: android.util.AtomicFile

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.AtomicFile`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.AtomicFile` |
| **Package** | `android.util` |
| **Total Methods** | 8 |
| **Avg Score** | 6.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 8 (100%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getBaseFile` | `java.io.File getBaseFile()` | 8 | near | easy | `getFile` | `getFile(wallpaperType: WallpaperType, callback: AsyncCallback<number>): void` |
| `finishWrite` | `void finishWrite(java.io.FileOutputStream)` | 7 | near | easy | `finishTrace` | `finishTrace(name: string, taskId: number): void` |
| `failWrite` | `void failWrite(java.io.FileOutputStream)` | 7 | near | easy | `write` | `write(data: number[]): Promise<void>` |
| `startWrite` | `java.io.FileOutputStream startWrite() throws java.io.IOException` | 7 | near | easy | `startTrace` | `startTrace(name: string, taskId: number, expectedTime?: number): void` |
| `openRead` | `java.io.FileInputStream openRead() throws java.io.FileNotFoundException` | 7 | near | moderate | `read` | `read(): Promise<number[]>` |
| `delete` | `void delete()` | 6 | near | moderate | `deleteContact` | `deleteContact(key: string, callback: AsyncCallback<void>): void` |
| `readFully` | `byte[] readFully() throws java.io.IOException` | 6 | near | moderate | `read` | `read(): Promise<number[]>` |
| `AtomicFile` | `AtomicFile(java.io.File)` | 6 | near | moderate | `getOneCfgFile` | `getOneCfgFile(relPath: string): Promise<string>` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 8 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.util.AtomicFile`:


## Quality Gates

Before marking `android.util.AtomicFile` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
