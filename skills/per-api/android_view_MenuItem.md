# SKILL: android.view.MenuItem

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.MenuItem`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.MenuItem` |
| **Package** | `android.view` |
| **Total Methods** | 51 |
| **Avg Score** | 2.9 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 48 (94%) |
| **No Mapping** | 3 (5%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 51 |
| **Has Async Gap** | 51 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 51 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `collapseActionView` | 3 | composite | Store callback, never fire |
| `expandActionView` | 3 | composite | Store callback, never fire |
| `getActionProvider` | 3 | composite | Return safe default (null/false/0/empty) |
| `getActionView` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAlphabeticModifiers` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAlphabeticShortcut` | 3 | composite | Return safe default (null/false/0/empty) |
| `getContentDescription` | 3 | composite | Return safe default (null/false/0/empty) |
| `getGroupId` | 3 | composite | Return safe default (null/false/0/empty) |
| `getIcon` | 3 | composite | Return safe default (null/false/0/empty) |
| `getIntent` | 3 | composite | Return safe default (null/false/0/empty) |
| `getItemId` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMenuInfo` | 3 | composite | Return safe default (null/false/0/empty) |
| `getNumericModifiers` | 3 | composite | Return safe default (null/false/0/empty) |
| `getNumericShortcut` | 3 | composite | Return safe default (null/false/0/empty) |
| `getOrder` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSubMenu` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTitle` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTitleCondensed` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTooltipText` | 3 | composite | Return safe default (null/false/0/empty) |
| `isActionViewExpanded` | 3 | composite | Return safe default (null/false/0/empty) |
| `isEnabled` | 3 | composite | Return safe default (null/false/0/empty) |
| `isVisible` | 3 | composite | Return safe default (null/false/0/empty) |
| `setActionProvider` | 3 | composite | Log warning + no-op |
| `setActionView` | 3 | composite | Log warning + no-op |
| `setActionView` | 3 | composite | Log warning + no-op |
| `setCheckable` | 3 | composite | Log warning + no-op |
| `setChecked` | 3 | composite | Log warning + no-op |
| `setEnabled` | 3 | composite | Log warning + no-op |
| `setIcon` | 3 | composite | Log warning + no-op |
| `setIcon` | 3 | composite | Log warning + no-op |
| `setIconTintList` | 3 | composite | Return safe default (null/false/0/empty) |
| `setIntent` | 3 | composite | Log warning + no-op |
| `setNumericShortcut` | 3 | composite | Log warning + no-op |
| `setNumericShortcut` | 3 | composite | Log warning + no-op |
| `setOnActionExpandListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setOnMenuItemClickListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setShortcut` | 3 | composite | Log warning + no-op |
| `setShortcut` | 3 | composite | Log warning + no-op |
| `setShowAsAction` | 3 | composite | Log warning + no-op |
| `setShowAsActionFlags` | 3 | composite | Log warning + no-op |
| `setTitle` | 3 | composite | Log warning + no-op |
| `setTitle` | 3 | composite | Log warning + no-op |
| `setTitleCondensed` | 3 | composite | Log warning + no-op |
| `setVisible` | 3 | composite | Return safe default (null/false/0/empty) |
| `setAlphabeticShortcut` | 3 | composite | Log warning + no-op |
| `setAlphabeticShortcut` | 3 | composite | Log warning + no-op |
| `setTooltipText` | 3 | composite | Log warning + no-op |
| `setContentDescription` | 2 | composite | Log warning + no-op |
| `hasSubMenu` | 1 | none | Return safe default (null/false/0/empty) |
| `isCheckable` | 1 | none | Return safe default (null/false/0/empty) |
| `isChecked` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.MenuItem`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.view.MenuItem` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 51 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
