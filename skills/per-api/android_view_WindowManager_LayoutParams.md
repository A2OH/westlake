# SKILL: android.view.WindowManager.LayoutParams

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.WindowManager.LayoutParams`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.WindowManager.LayoutParams` |
| **Package** | `android.view.WindowManager` |
| **Total Methods** | 22 |
| **Avg Score** | 2.0 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 12 (54%) |
| **No Mapping** | 10 (45%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 15 |
| **Has Async Gap** | 15 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 22 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `copyFrom` | 3 | composite | throw UnsupportedOperationException |
| `debug` | 3 | composite | throw UnsupportedOperationException |
| `getColorMode` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFitInsetsSides` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFitInsetsTypes` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTitle` | 3 | composite | Return safe default (null/false/0/empty) |
| `setFitInsetsIgnoringVisibility` | 3 | composite | Return safe default (null/false/0/empty) |
| `setFitInsetsSides` | 3 | composite | Log warning + no-op |
| `setFitInsetsTypes` | 3 | composite | Log warning + no-op |
| `setTitle` | 3 | composite | Log warning + no-op |
| `setColorMode` | 3 | composite | Log warning + no-op |
| `writeToParcel` | 2 | composite | Log warning + no-op |
| `LayoutParams` | 1 | none | throw UnsupportedOperationException |
| `LayoutParams` | 1 | none | throw UnsupportedOperationException |
| `LayoutParams` | 1 | none | throw UnsupportedOperationException |
| `LayoutParams` | 1 | none | throw UnsupportedOperationException |
| `LayoutParams` | 1 | none | throw UnsupportedOperationException |
| `LayoutParams` | 1 | none | throw UnsupportedOperationException |
| `LayoutParams` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |
| `isFitInsetsIgnoringVisibility` | 1 | none | Return safe default (null/false/0/empty) |
| `mayUseInputMethod` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.WindowManager.LayoutParams`:


## Quality Gates

Before marking `android.view.WindowManager.LayoutParams` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 22 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
