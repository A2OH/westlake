# SKILL: android.text.BidiFormatter

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.text.BidiFormatter`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.text.BidiFormatter` |
| **Package** | `android.text` |
| **Total Methods** | 13 |
| **Avg Score** | 3.6 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 5 (38%) |
| **Partial/Composite** | 1 (7%) |
| **No Mapping** | 7 (53%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isRtl` | `boolean isRtl(String)` | 8 | direct | easy | `isRTL` | `isRTL(locale: string): boolean` |
| `isRtl` | `boolean isRtl(CharSequence)` | 8 | direct | easy | `isRTL` | `isRTL(locale: string): boolean` |
| `getInstance` | `static android.text.BidiFormatter getInstance()` | 7 | near | moderate | `napi_get_instance_data` | `NAPI_EXTERN napi_status napi_get_instance_data(napi_env env, void** data)` |
| `getInstance` | `static android.text.BidiFormatter getInstance(boolean)` | 7 | near | moderate | `napi_get_instance_data` | `NAPI_EXTERN napi_status napi_get_instance_data(napi_env env, void** data)` |
| `getInstance` | `static android.text.BidiFormatter getInstance(java.util.Locale)` | 7 | near | moderate | `napi_get_instance_data` | `NAPI_EXTERN napi_status napi_get_instance_data(napi_env env, void** data)` |

## Stub APIs (score < 5): 8 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getStereoReset` | 4 | partial | Return safe default (null/false/0/empty) |
| `isRtlContext` | 1 | none | Return safe default (null/false/0/empty) |
| `unicodeWrap` | 1 | none | throw UnsupportedOperationException |
| `unicodeWrap` | 1 | none | throw UnsupportedOperationException |
| `unicodeWrap` | 1 | none | throw UnsupportedOperationException |
| `unicodeWrap` | 1 | none | throw UnsupportedOperationException |
| `unicodeWrap` | 1 | none | throw UnsupportedOperationException |
| `unicodeWrap` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.text.BidiFormatter`:


## Quality Gates

Before marking `android.text.BidiFormatter` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 13 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
