# SKILL: android.os.Debug.MemoryInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.Debug.MemoryInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.Debug.MemoryInfo` |
| **Package** | `android.os.Debug` |
| **Total Methods** | 12 |
| **Avg Score** | 6.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 7 (58%) |
| **Partial/Composite** | 4 (33%) |
| **No Mapping** | 1 (8%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 10 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getTotalPrivateDirty` | `int getTotalPrivateDirty()` | 9 | direct | easy | `getPrivateDirty` | `getPrivateDirty(): bigint` |
| `getTotalSharedDirty` | `int getTotalSharedDirty()` | 8 | direct | easy | `getSharedDirty` | `getSharedDirty(): bigint` |
| `getMemoryStats` | `java.util.Map<java.lang.String,java.lang.String> getMemoryStats()` | 8 | near | easy | `getBatteryStats` | `getBatteryStats(): Promise<Array<BatteryStatsInfo>>` |
| `getTotalPss` | `int getTotalPss()` | 8 | near | easy | `getTotalBytes` | `getTotalBytes(path: string, callback: AsyncCallback<number>): void` |
| `getMemoryStat` | `String getMemoryStat(String)` | 7 | near | easy | `getBatteryStats` | `getBatteryStats(): Promise<Array<BatteryStatsInfo>>` |
| `getTotalSharedClean` | `int getTotalSharedClean()` | 6 | near | moderate | `getAllScreens` | `getAllScreens(callback: AsyncCallback<Array<Screen>>): void` |
| `getTotalPrivateClean` | `int getTotalPrivateClean()` | 6 | near | moderate | `getTotalBytes` | `getTotalBytes(path: string, callback: AsyncCallback<number>): void` |
| `MemoryInfo` | `Debug.MemoryInfo()` | 6 | partial | moderate | `info` | `info(domain: number, tag: string, format: string, ...args: any[]): void` |
| `writeToParcel` | `void writeToParcel(android.os.Parcel, int)` | 6 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |
| `getTotalSwappablePss` | `int getTotalSwappablePss()` | 5 | partial | moderate | `getTotalBytes` | `getTotalBytes(path: string, callback: AsyncCallback<number>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `readFromParcel` | 5 | partial | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 10 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.Debug.MemoryInfo`:


## Quality Gates

Before marking `android.os.Debug.MemoryInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 12 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 10 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
