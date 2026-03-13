# SKILL: android.widget.Spinner

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.Spinner`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.Spinner` |
| **Package** | `android.widget` |
| **Total Methods** | 23 |
| **Avg Score** | 2.4 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 16 (69%) |
| **No Mapping** | 7 (30%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 16 |
| **Has Async Gap** | 16 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 23 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getDropDownHorizontalOffset` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDropDownVerticalOffset` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDropDownWidth` | 3 | composite | Return safe default (null/false/0/empty) |
| `getGravity` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPopupBackground` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPopupContext` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPrompt` | 3 | composite | Return safe default (null/false/0/empty) |
| `onClick` | 3 | composite | Store callback, never fire |
| `setDropDownHorizontalOffset` | 3 | composite | Log warning + no-op |
| `setDropDownVerticalOffset` | 3 | composite | Log warning + no-op |
| `setGravity` | 3 | composite | Log warning + no-op |
| `setPopupBackgroundDrawable` | 3 | composite | Log warning + no-op |
| `setPromptId` | 3 | composite | Log warning + no-op |
| `setPopupBackgroundResource` | 3 | composite | Log warning + no-op |
| `setDropDownWidth` | 3 | composite | Log warning + no-op |
| `setPrompt` | 3 | composite | Log warning + no-op |
| `Spinner` | 1 | none | throw UnsupportedOperationException |
| `Spinner` | 1 | none | throw UnsupportedOperationException |
| `Spinner` | 1 | none | throw UnsupportedOperationException |
| `Spinner` | 1 | none | throw UnsupportedOperationException |
| `Spinner` | 1 | none | throw UnsupportedOperationException |
| `Spinner` | 1 | none | throw UnsupportedOperationException |
| `Spinner` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.Spinner`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.Spinner` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 23 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
