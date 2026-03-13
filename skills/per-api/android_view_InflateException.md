# SKILL: android.view.InflateException

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.InflateException`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.InflateException` |
| **Package** | `android.view` |
| **Total Methods** | 4 |
| **Avg Score** | 7.2 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 4 (100%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `InflateException` | `InflateException()` | 7 | near | easy | `napi_fatal_exception` | `NAPI_INNER_EXTERN napi_status napi_fatal_exception(napi_env env, napi_value err)` |
| `InflateException` | `InflateException(String, Throwable)` | 7 | near | easy | `napi_fatal_exception` | `NAPI_INNER_EXTERN napi_status napi_fatal_exception(napi_env env, napi_value err)` |
| `InflateException` | `InflateException(String)` | 7 | near | easy | `napi_fatal_exception` | `NAPI_INNER_EXTERN napi_status napi_fatal_exception(napi_env env, napi_value err)` |
| `InflateException` | `InflateException(Throwable)` | 7 | near | easy | `napi_fatal_exception` | `NAPI_INNER_EXTERN napi_status napi_fatal_exception(napi_env env, napi_value err)` |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.view.InflateException`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.view.InflateException` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
