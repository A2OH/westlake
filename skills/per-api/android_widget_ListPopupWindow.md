# SKILL: android.widget.ListPopupWindow

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.ListPopupWindow`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.ListPopupWindow` |
| **Package** | `android.widget` |
| **Total Methods** | 47 |
| **Avg Score** | 2.5 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 36 (76%) |
| **No Mapping** | 11 (23%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 43 |
| **Has Async Gap** | 43 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 47 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `createDragToOpenListener` | 3 | composite | Return dummy instance / no-op |
| `getHeight` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHorizontalOffset` | 3 | composite | Return safe default (null/false/0/empty) |
| `getInputMethodMode` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPromptPosition` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSelectedItemId` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSelectedItemPosition` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSoftInputMode` | 3 | composite | Return safe default (null/false/0/empty) |
| `getVerticalOffset` | 3 | composite | Return safe default (null/false/0/empty) |
| `getWidth` | 3 | composite | Return safe default (null/false/0/empty) |
| `onKeyDown` | 3 | composite | Store callback, never fire |
| `onKeyPreIme` | 3 | composite | Store callback, never fire |
| `onKeyUp` | 3 | composite | Store callback, never fire |
| `setAdapter` | 3 | composite | Log warning + no-op |
| `setAnchorView` | 3 | composite | Log warning + no-op |
| `setAnimationStyle` | 3 | composite | Log warning + no-op |
| `setBackgroundDrawable` | 3 | composite | Log warning + no-op |
| `setDropDownGravity` | 3 | composite | Log warning + no-op |
| `setEpicenterBounds` | 3 | composite | Log warning + no-op |
| `setHeight` | 3 | composite | Log warning + no-op |
| `setHorizontalOffset` | 3 | composite | Log warning + no-op |
| `setInputMethodMode` | 3 | composite | Log warning + no-op |
| `setListSelector` | 3 | composite | Return safe default (null/false/0/empty) |
| `setModal` | 3 | composite | Log warning + no-op |
| `setOnDismissListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setOnItemClickListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setOnItemSelectedListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setSelection` | 3 | composite | Log warning + no-op |
| `setSoftInputMode` | 3 | composite | Log warning + no-op |
| `setVerticalOffset` | 3 | composite | Log warning + no-op |
| `setWidth` | 3 | composite | Log warning + no-op |
| `setWindowLayoutType` | 3 | composite | Log warning + no-op |
| `show` | 3 | composite | throw UnsupportedOperationException |
| `setPromptView` | 3 | composite | Log warning + no-op |
| `setContentWidth` | 3 | composite | Log warning + no-op |
| `setPromptPosition` | 3 | composite | Log warning + no-op |
| `ListPopupWindow` | 1 | none | Return safe default (null/false/0/empty) |
| `ListPopupWindow` | 1 | none | Return safe default (null/false/0/empty) |
| `ListPopupWindow` | 1 | none | Return safe default (null/false/0/empty) |
| `ListPopupWindow` | 1 | none | Return safe default (null/false/0/empty) |
| `clearListSelection` | 1 | none | Return safe default (null/false/0/empty) |
| `dismiss` | 1 | none | Return safe default (null/false/0/empty) |
| `isInputMethodNotNeeded` | 1 | none | Return safe default (null/false/0/empty) |
| `isModal` | 1 | none | Return safe default (null/false/0/empty) |
| `isShowing` | 1 | none | Return safe default (null/false/0/empty) |
| `performItemClick` | 1 | none | throw UnsupportedOperationException |
| `postShow` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.ListPopupWindow`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.ListPopupWindow` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 47 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
