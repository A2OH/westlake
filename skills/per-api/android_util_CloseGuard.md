# SKILL: android.util.CloseGuard

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.CloseGuard`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.CloseGuard` |
| **Package** | `android.util` |
| **Total Methods** | 4 |
| **Avg Score** | 8.1 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 3 (75%) |
| **Partial/Composite** | 1 (25%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `close` | `void close()` | 10 | direct | trivial | `close` | `close(fd: number): Promise<void>` |
| `open` | `void open(@NonNull String)` | 10 | direct | trivial | `open` | `open(path: string, flags?: number, mode?: number): Promise<number>` |
| `CloseGuard` | `CloseGuard()` | 7 | near | moderate | `close` | `close(fd: number): Promise<void>` |
| `warnIfOpen` | `void warnIfOpen()` | 6 | partial | moderate | `open` | `open(path: string, flags?: number, mode?: number): Promise<number>` |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.util.CloseGuard`:


## Quality Gates

Before marking `android.util.CloseGuard` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
