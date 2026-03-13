# SKILL: android.view.WindowInsetsAnimation.Bounds

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.WindowInsetsAnimation.Bounds`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.WindowInsetsAnimation.Bounds` |
| **Package** | `android.view.WindowInsetsAnimation` |
| **Total Methods** | 5 |
| **Avg Score** | 3.0 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 4 (80%) |
| **No Mapping** | 1 (20%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 3 |
| **Has Async Gap** | 3 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Callback` | `WindowInsetsAnimation.Callback(int)` | 5 | partial | moderate | `napi_open_callback_scope` | `NAPI_EXTERN napi_status napi_open_callback_scope(napi_env env,
                                                 napi_value resource_object,
                                                 napi_async_context context,
                                                 napi_callback_scope* result)` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getDispatchMode` | 3 | composite | Return safe default (null/false/0/empty) |
| `onEnd` | 3 | composite | Store callback, never fire |
| `onPrepare` | 3 | composite | Store callback, never fire |
| `Bounds` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.WindowInsetsAnimation.Bounds`:


## Quality Gates

Before marking `android.view.WindowInsetsAnimation.Bounds` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
