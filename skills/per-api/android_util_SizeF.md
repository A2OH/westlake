# SKILL: android.util.SizeF

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.SizeF`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.SizeF` |
| **Package** | `android.util` |
| **Total Methods** | 4 |
| **Avg Score** | 8.0 |
| **Scenario** | S1: Direct Mapping (Thin Wrapper) |
| **Strategy** | Simple delegation to OHBridge |
| **Direct/Near** | 4 (100%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `SizeF` | `SizeF(float, float)` | 9 | direct | easy | `size` | `size: number` |
| `getHeight` | `float getHeight()` | 9 | direct | easy | `getMinHeight` | `getMinHeight(callback: AsyncCallback<number>): void` |
| `getWidth` | `float getWidth()` | 8 | direct | easy | `getMinWidth` | `getMinWidth(callback: AsyncCallback<number>): void` |
| `parseSizeF` | `static android.util.SizeF parseSizeF(String) throws java.lang.NumberFormatException` | 6 | near | moderate | `imageSize` | `imageSize?: Size` |

## AI Agent Instructions

**Scenario: S1 — Direct Mapping (Thin Wrapper)**

1. Create Java shim at `shim/java/android/util/SizeF.java`
2. For each method, delegate to `OHBridge.xxx()` — one bridge call per Android call
3. Add `static native` declarations to `OHBridge.java`
4. Add mock implementations to `test-apps/mock/.../OHBridge.java`
5. Add test section to `HeadlessTest.java` — call each method with valid + edge inputs
6. Test null args, boundary values, return types

## Dependencies

Check if these related classes are already shimmed before generating `android.util.SizeF`:


## Quality Gates

Before marking `android.util.SizeF` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
