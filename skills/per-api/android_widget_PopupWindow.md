# SKILL: android.widget.PopupWindow

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.PopupWindow`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.PopupWindow` |
| **Package** | `android.widget` |
| **Total Methods** | 69 |
| **Avg Score** | 2.3 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 47 (68%) |
| **No Mapping** | 22 (31%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 60 |
| **Has Async Gap** | 60 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 69 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getAnimationStyle` | 3 | composite | Return safe default (null/false/0/empty) |
| `getBackground` | 3 | composite | Return safe default (null/false/0/empty) |
| `getContentView` | 3 | composite | Return safe default (null/false/0/empty) |
| `getElevation` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHeight` | 3 | composite | Return safe default (null/false/0/empty) |
| `getInputMethodMode` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMaxAvailableHeight` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMaxAvailableHeight` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMaxAvailableHeight` | 3 | composite | Return safe default (null/false/0/empty) |
| `getOverlapAnchor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSoftInputMode` | 3 | composite | Return safe default (null/false/0/empty) |
| `getWidth` | 3 | composite | Return safe default (null/false/0/empty) |
| `getWindowLayoutType` | 3 | composite | Return safe default (null/false/0/empty) |
| `isSplitTouchEnabled` | 3 | composite | Return safe default (null/false/0/empty) |
| `isTouchModal` | 3 | composite | Return safe default (null/false/0/empty) |
| `setAnimationStyle` | 3 | composite | Log warning + no-op |
| `setAttachedInDecor` | 3 | composite | Log warning + no-op |
| `setBackgroundDrawable` | 3 | composite | Log warning + no-op |
| `setClippingEnabled` | 3 | composite | Log warning + no-op |
| `setElevation` | 3 | composite | Log warning + no-op |
| `setEnterTransition` | 3 | composite | Log warning + no-op |
| `setEpicenterBounds` | 3 | composite | Log warning + no-op |
| `setExitTransition` | 3 | composite | Log warning + no-op |
| `setHeight` | 3 | composite | Log warning + no-op |
| `setInputMethodMode` | 3 | composite | Log warning + no-op |
| `setIsClippedToScreen` | 3 | composite | Return safe default (null/false/0/empty) |
| `setIsLaidOutInScreen` | 3 | composite | Return safe default (null/false/0/empty) |
| `setOnDismissListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setOutsideTouchable` | 3 | composite | Log warning + no-op |
| `setOverlapAnchor` | 3 | composite | Log warning + no-op |
| `setSoftInputMode` | 3 | composite | Log warning + no-op |
| `setSplitTouchEnabled` | 3 | composite | Log warning + no-op |
| `setTouchInterceptor` | 3 | composite | Log warning + no-op |
| `setTouchModal` | 3 | composite | Log warning + no-op |
| `setTouchable` | 3 | composite | Log warning + no-op |
| `setWidth` | 3 | composite | Log warning + no-op |
| `setWindowLayoutType` | 3 | composite | Log warning + no-op |
| `update` | 3 | composite | Log warning + no-op |
| `update` | 3 | composite | Log warning + no-op |
| `update` | 3 | composite | Log warning + no-op |
| `update` | 3 | composite | Log warning + no-op |
| `update` | 3 | composite | Log warning + no-op |
| `update` | 3 | composite | Log warning + no-op |
| `setFocusable` | 3 | composite | Log warning + no-op |
| `setIgnoreCheekPress` | 3 | composite | Log warning + no-op |
| `setContentView` | 3 | composite | Log warning + no-op |
| `isClippedToScreen` | 2 | composite | Return safe default (null/false/0/empty) |
| `PopupWindow` | 1 | none | throw UnsupportedOperationException |
| `PopupWindow` | 1 | none | throw UnsupportedOperationException |
| `PopupWindow` | 1 | none | throw UnsupportedOperationException |
| `PopupWindow` | 1 | none | throw UnsupportedOperationException |
| `PopupWindow` | 1 | none | throw UnsupportedOperationException |
| `PopupWindow` | 1 | none | throw UnsupportedOperationException |
| `PopupWindow` | 1 | none | throw UnsupportedOperationException |
| `PopupWindow` | 1 | none | throw UnsupportedOperationException |
| `PopupWindow` | 1 | none | throw UnsupportedOperationException |
| `dismiss` | 1 | none | Return safe default (null/false/0/empty) |
| `isAboveAnchor` | 1 | none | Return safe default (null/false/0/empty) |
| `isAttachedInDecor` | 1 | none | Return safe default (null/false/0/empty) |
| `isClippingEnabled` | 1 | none | Return safe default (null/false/0/empty) |
| `isFocusable` | 1 | none | Return safe default (null/false/0/empty) |
| `isLaidOutInScreen` | 1 | none | Return safe default (null/false/0/empty) |
| `isOutsideTouchable` | 1 | none | Return safe default (null/false/0/empty) |
| `isShowing` | 1 | none | Return safe default (null/false/0/empty) |
| `isTouchable` | 1 | none | Return safe default (null/false/0/empty) |
| `showAsDropDown` | 1 | none | throw UnsupportedOperationException |
| `showAsDropDown` | 1 | none | throw UnsupportedOperationException |
| `showAsDropDown` | 1 | none | throw UnsupportedOperationException |
| `showAtLocation` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.PopupWindow`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.PopupWindow` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 69 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
