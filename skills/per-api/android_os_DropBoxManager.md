# SKILL: android.os.DropBoxManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.DropBoxManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.DropBoxManager` |
| **Package** | `android.os` |
| **Total Methods** | 5 |
| **Avg Score** | 7.4 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 4 (80%) |
| **Partial/Composite** | 1 (20%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isTagEnabled` | `boolean isTagEnabled(String)` | 9 | direct | easy | `isFlagEnabled` | `isFlagEnabled(id: HiTraceId, flag: HiTraceFlag): boolean` |
| `addText` | `void addText(String, String)` | 8 | direct | easy | `readText` | `readText(filePath: string,
  options?: {
    position?: number;
    length?: number;
    encoding?: string;
  }): Promise<string>` |
| `addData` | `void addData(String, byte[], int)` | 7 | near | easy | `data` | `data: string[]` |
| `addFile` | `void addFile(String, java.io.File, int) throws java.io.IOException` | 7 | near | easy | `addRule` | `addRule(rule: bigint): void` |
| `DropBoxManager` | `DropBoxManager()` | 6 | partial | moderate | `getCalendarManager` | `getCalendarManager(context: Context): CalendarManager` |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.os.DropBoxManager`:


## Quality Gates

Before marking `android.os.DropBoxManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
