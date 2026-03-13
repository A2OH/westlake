# SKILL: android.os.Environment

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.Environment`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.Environment` |
| **Package** | `android.os` |
| **Total Methods** | 13 |
| **Avg Score** | 5.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (30%) |
| **Partial/Composite** | 9 (69%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 11 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Environment` | `Environment()` | 8 | near | easy | `getEnvironmentVar` | `getEnvironmentVar(name: string): string` |
| `getDataDirectory` | `static java.io.File getDataDirectory()` | 7 | near | moderate | `getDataSummary` | `getDataSummary(): Array<Summary>` |
| `isExternalStorageManager` | `static boolean isExternalStorageManager()` | 6 | near | moderate | `getSystemResourceManager` | `getSystemResourceManager(): ResourceManager` |
| `isExternalStorageManager` | `static boolean isExternalStorageManager(@NonNull java.io.File)` | 6 | near | moderate | `getSystemResourceManager` | `getSystemResourceManager(): ResourceManager` |
| `getExternalStorageState` | `static String getExternalStorageState()` | 6 | partial | moderate | `getAttestStatus` | `getAttestStatus(callback: AsyncCallback<AttestResultInfo>): void` |
| `getExternalStorageState` | `static String getExternalStorageState(java.io.File)` | 6 | partial | moderate | `getAttestStatus` | `getAttestStatus(callback: AsyncCallback<AttestResultInfo>): void` |
| `getDownloadCacheDirectory` | `static java.io.File getDownloadCacheDirectory()` | 5 | partial | moderate | `getDLPFileAccessRecords` | `getDLPFileAccessRecords(): Promise<Array<AccessedDLPFileInfo>>` |
| `isExternalStorageRemovable` | `static boolean isExternalStorageRemovable()` | 5 | partial | moderate | `isFlagEnabled` | `isFlagEnabled(id: HiTraceId, flag: HiTraceFlag): boolean` |
| `isExternalStorageRemovable` | `static boolean isExternalStorageRemovable(@NonNull java.io.File)` | 5 | partial | moderate | `isFlagEnabled` | `isFlagEnabled(id: HiTraceId, flag: HiTraceFlag): boolean` |
| `isExternalStorageEmulated` | `static boolean isExternalStorageEmulated()` | 5 | partial | moderate | `isFeatureSupported` | `isFeatureSupported(featureId: number): boolean` |
| `isExternalStorageEmulated` | `static boolean isExternalStorageEmulated(@NonNull java.io.File)` | 5 | partial | moderate | `isFeatureSupported` | `isFeatureSupported(featureId: number): boolean` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isExternalStorageLegacy` | 5 | partial | Return safe default (null/false/0/empty) |
| `isExternalStorageLegacy` | 5 | partial | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 11 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.Environment`:


## Quality Gates

Before marking `android.os.Environment` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 13 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 11 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
