# SKILL: android.util.Size

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.Size`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.Size` |
| **Package** | `android.util` |
| **Total Methods** | 4 |
| **Avg Score** | 8.4 |
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
| `Size` | `Size(int, int)` | 10 | direct | trivial | `size` | `size: number` |
| `getHeight` | `int getHeight()` | 9 | direct | easy | `getMinHeight` | `getMinHeight(callback: AsyncCallback<number>): void` |
| `getWidth` | `int getWidth()` | 8 | direct | easy | `getMinWidth` | `getMinWidth(callback: AsyncCallback<number>): void` |
| `parseSize` | `static android.util.Size parseSize(String) throws java.lang.NumberFormatException` | 7 | near | moderate | `imageSize` | `imageSize?: Size` |

## AI Agent Instructions

**Scenario: S1 — Direct Mapping (Thin Wrapper)**

1. Create Java shim at `shim/java/android/util/Size.java`
2. For each method, delegate to `OHBridge.xxx()` — one bridge call per Android call
3. Add `static native` declarations to `OHBridge.java`
4. Add mock implementations to `test-apps/mock/.../OHBridge.java`
5. Add test section to `HeadlessTest.java` — call each method with valid + edge inputs
6. Test null args, boundary values, return types

## Dependencies

Check if these related classes are already shimmed before generating `android.util.Size`:


## Quality Gates

Before marking `android.util.Size` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
