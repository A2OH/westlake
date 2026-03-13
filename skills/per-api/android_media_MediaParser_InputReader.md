# SKILL: android.media.MediaParser.InputReader

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaParser.InputReader`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaParser.InputReader` |
| **Package** | `android.media.MediaParser` |
| **Total Methods** | 3 |
| **Avg Score** | 7.2 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 2 (66%) |
| **Partial/Composite** | 1 (33%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `read` | `int read(@NonNull byte[], int, int) throws java.io.IOException` | 8 | direct | easy | `read` | `read(): Promise<number[]>` |
| `getPosition` | `long getPosition()` | 8 | near | easy | `getPositionObject` | `getPositionObject(index: number, callback: AsyncCallback<FileAsset>): void` |
| `getLength` | `long getLength()` | 6 | partial | moderate | `getCount` | `getCount(): number` |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaParser.InputReader`:


## Quality Gates

Before marking `android.media.MediaParser.InputReader` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 3 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
