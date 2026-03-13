# SKILL: android.view.MotionEvent.PointerProperties

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.MotionEvent.PointerProperties`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.MotionEvent.PointerProperties` |
| **Package** | `android.view.MotionEvent` |
| **Total Methods** | 4 |
| **Avg Score** | 4.2 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 4 (100%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 2 |
| **Has Async Gap** | 2 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `PointerProperties` | `MotionEvent.PointerProperties()` | 5 | partial | moderate | `napi_create_object_with_properties` | `NAPI_EXTERN napi_status napi_create_object_with_properties(napi_env env,
                                                           napi_value* result,
                                                           size_t property_count,
                                                           const napi_property_descriptor* properties)` |
| `PointerProperties` | `MotionEvent.PointerProperties(android.view.MotionEvent.PointerProperties)` | 5 | partial | moderate | `napi_create_object_with_properties` | `NAPI_EXTERN napi_status napi_create_object_with_properties(napi_env env,
                                                           napi_value* result,
                                                           size_t property_count,
                                                           const napi_property_descriptor* properties)` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `clear` | 3 | composite | throw UnsupportedOperationException |
| `copyFrom` | 3 | composite | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.view.MotionEvent.PointerProperties`:


## Quality Gates

Before marking `android.view.MotionEvent.PointerProperties` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
