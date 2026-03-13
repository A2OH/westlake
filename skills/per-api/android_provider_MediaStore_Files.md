# SKILL: android.provider.MediaStore.Files

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.provider.MediaStore.Files`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.provider.MediaStore.Files` |
| **Package** | `android.provider.MediaStore` |
| **Total Methods** | 3 |
| **Avg Score** | 7.1 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 3 (100%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Files` | `MediaStore.Files()` | 8 | direct | easy | `files` | `files: Array<RequestFile>` |
| `getContentUri` | `static android.net.Uri getContentUri(String)` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `getContentUri` | `static android.net.Uri getContentUri(String, long)` | 7 | near | moderate | `getCount` | `getCount(): number` |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.provider.MediaStore.Files`:


## Quality Gates

Before marking `android.provider.MediaStore.Files` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 3 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
