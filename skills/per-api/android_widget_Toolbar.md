# SKILL: android.widget.Toolbar

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.Toolbar`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.Toolbar` |
| **Package** | `android.widget` |
| **Total Methods** | 67 |
| **Avg Score** | 2.6 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 55 (82%) |
| **No Mapping** | 12 (17%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 63 |
| **Has Async Gap** | 63 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 67 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `collapseActionView` | 3 | composite | Store callback, never fire |
| `getContentInsetEnd` | 3 | composite | Return safe default (null/false/0/empty) |
| `getContentInsetEndWithActions` | 3 | composite | Return safe default (null/false/0/empty) |
| `getContentInsetLeft` | 3 | composite | Return safe default (null/false/0/empty) |
| `getContentInsetRight` | 3 | composite | Return safe default (null/false/0/empty) |
| `getContentInsetStart` | 3 | composite | Return dummy instance / no-op |
| `getContentInsetStartWithNavigation` | 3 | composite | Return dummy instance / no-op |
| `getCurrentContentInsetEnd` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCurrentContentInsetLeft` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCurrentContentInsetRight` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCurrentContentInsetStart` | 3 | composite | Return dummy instance / no-op |
| `getLogo` | 3 | composite | Return safe default (null/false/0/empty) |
| `getLogoDescription` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMenu` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPopupTheme` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSubtitle` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTitle` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTitleMarginBottom` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTitleMarginEnd` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTitleMarginStart` | 3 | composite | Return dummy instance / no-op |
| `getTitleMarginTop` | 3 | composite | Return safe default (null/false/0/empty) |
| `hasExpandedActionView` | 3 | composite | Return safe default (null/false/0/empty) |
| `setContentInsetEndWithActions` | 3 | composite | Log warning + no-op |
| `setContentInsetStartWithNavigation` | 3 | composite | Return dummy instance / no-op |
| `setContentInsetsAbsolute` | 3 | composite | Log warning + no-op |
| `setContentInsetsRelative` | 3 | composite | Log warning + no-op |
| `setNavigationIcon` | 3 | composite | Log warning + no-op |
| `setNavigationIcon` | 3 | composite | Log warning + no-op |
| `setNavigationOnClickListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setOnMenuItemClickListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setSubtitle` | 3 | composite | Log warning + no-op |
| `setSubtitle` | 3 | composite | Log warning + no-op |
| `setSubtitleTextAppearance` | 3 | composite | Log warning + no-op |
| `setTitle` | 3 | composite | Log warning + no-op |
| `setTitle` | 3 | composite | Log warning + no-op |
| `setTitleMarginBottom` | 3 | composite | Log warning + no-op |
| `setTitleMarginEnd` | 3 | composite | Log warning + no-op |
| `setTitleMarginStart` | 3 | composite | Return dummy instance / no-op |
| `setTitleMarginTop` | 3 | composite | Log warning + no-op |
| `setTitleTextAppearance` | 3 | composite | Log warning + no-op |
| `setTitleTextColor` | 3 | composite | Log warning + no-op |
| `setPopupTheme` | 3 | composite | Log warning + no-op |
| `setSubtitleTextColor` | 3 | composite | Log warning + no-op |
| `setTitleMargin` | 3 | composite | Log warning + no-op |
| `setCollapseContentDescription` | 3 | composite | Log warning + no-op |
| `setCollapseContentDescription` | 3 | composite | Log warning + no-op |
| `setCollapseIcon` | 3 | composite | Log warning + no-op |
| `setCollapseIcon` | 3 | composite | Log warning + no-op |
| `setNavigationContentDescription` | 3 | composite | Log warning + no-op |
| `setNavigationContentDescription` | 3 | composite | Log warning + no-op |
| `setLogo` | 2 | composite | Log warning + no-op |
| `setLogo` | 2 | composite | Log warning + no-op |
| `setLogoDescription` | 2 | composite | Log warning + no-op |
| `setLogoDescription` | 2 | composite | Log warning + no-op |
| `setOverflowIcon` | 2 | composite | Log warning + no-op |
| `Toolbar` | 1 | none | throw UnsupportedOperationException |
| `Toolbar` | 1 | none | throw UnsupportedOperationException |
| `Toolbar` | 1 | none | throw UnsupportedOperationException |
| `Toolbar` | 1 | none | throw UnsupportedOperationException |
| `dismissPopupMenus` | 1 | none | Return safe default (null/false/0/empty) |
| `generateDefaultLayoutParams` | 1 | none | throw UnsupportedOperationException |
| `generateLayoutParams` | 1 | none | throw UnsupportedOperationException |
| `generateLayoutParams` | 1 | none | throw UnsupportedOperationException |
| `hideOverflowMenu` | 1 | none | throw UnsupportedOperationException |
| `inflateMenu` | 1 | none | throw UnsupportedOperationException |
| `isOverflowMenuShowing` | 1 | none | Return safe default (null/false/0/empty) |
| `showOverflowMenu` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.Toolbar`:

- `android.view.View` (already shimmed)
- `android.view.ViewGroup` (already shimmed)

## Quality Gates

Before marking `android.widget.Toolbar` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 67 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
