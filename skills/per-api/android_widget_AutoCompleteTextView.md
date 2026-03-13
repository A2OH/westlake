# SKILL: android.widget.AutoCompleteTextView

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.AutoCompleteTextView`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.AutoCompleteTextView` |
| **Package** | `android.widget` |
| **Total Methods** | 50 |
| **Avg Score** | 2.4 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 35 (70%) |
| **No Mapping** | 15 (30%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 45 |
| **Has Async Gap** | 45 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 50 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `convertSelectionToString` | 3 | composite | Store callback, never fire |
| `getAdapter` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCompletionHint` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDropDownAnchor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDropDownBackground` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDropDownHeight` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDropDownHorizontalOffset` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDropDownVerticalOffset` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDropDownWidth` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFilter` | 3 | composite | Return safe default (null/false/0/empty) |
| `getInputMethodMode` | 3 | composite | Return safe default (null/false/0/empty) |
| `getListSelection` | 3 | composite | Return safe default (null/false/0/empty) |
| `getOnItemClickListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `getOnItemSelectedListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `getThreshold` | 3 | composite | Return safe default (null/false/0/empty) |
| `getValidator` | 3 | composite | Return safe default (null/false/0/empty) |
| `onFilterComplete` | 3 | composite | Store callback, never fire |
| `setAdapter` | 3 | composite | Log warning + no-op |
| `setCompletionHint` | 3 | composite | Log warning + no-op |
| `setDropDownAnchor` | 3 | composite | Log warning + no-op |
| `setDropDownBackgroundResource` | 3 | composite | Log warning + no-op |
| `setDropDownHorizontalOffset` | 3 | composite | Log warning + no-op |
| `setDropDownVerticalOffset` | 3 | composite | Log warning + no-op |
| `setInputMethodMode` | 3 | composite | Log warning + no-op |
| `setListSelection` | 3 | composite | Return safe default (null/false/0/empty) |
| `setOnDismissListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setOnItemClickListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setOnItemSelectedListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setText` | 3 | composite | Log warning + no-op |
| `setThreshold` | 3 | composite | Log warning + no-op |
| `setValidator` | 3 | composite | Log warning + no-op |
| `setDropDownWidth` | 3 | composite | Log warning + no-op |
| `setDropDownHeight` | 3 | composite | Log warning + no-op |
| `setDropDownBackgroundDrawable` | 2 | composite | Log warning + no-op |
| `enoughToFilter` | 2 | composite | throw UnsupportedOperationException |
| `AutoCompleteTextView` | 1 | none | throw UnsupportedOperationException |
| `AutoCompleteTextView` | 1 | none | throw UnsupportedOperationException |
| `AutoCompleteTextView` | 1 | none | throw UnsupportedOperationException |
| `AutoCompleteTextView` | 1 | none | throw UnsupportedOperationException |
| `AutoCompleteTextView` | 1 | none | throw UnsupportedOperationException |
| `clearListSelection` | 1 | none | Return safe default (null/false/0/empty) |
| `dismissDropDown` | 1 | none | Return safe default (null/false/0/empty) |
| `isPerformingCompletion` | 1 | none | Return safe default (null/false/0/empty) |
| `isPopupShowing` | 1 | none | Return safe default (null/false/0/empty) |
| `performCompletion` | 1 | none | Store callback, never fire |
| `performFiltering` | 1 | none | throw UnsupportedOperationException |
| `performValidation` | 1 | none | Store callback, never fire |
| `refreshAutoCompleteResults` | 1 | none | throw UnsupportedOperationException |
| `replaceText` | 1 | none | throw UnsupportedOperationException |
| `showDropDown` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.AutoCompleteTextView`:

- `android.widget.EditText` (already shimmed)
- `android.widget.TextView` (already shimmed)

## Quality Gates

Before marking `android.widget.AutoCompleteTextView` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 50 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
