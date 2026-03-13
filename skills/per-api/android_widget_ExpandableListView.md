# SKILL: android.widget.ExpandableListView

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.ExpandableListView`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.ExpandableListView` |
| **Package** | `android.widget` |
| **Total Methods** | 32 |
| **Avg Score** | 2.5 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 24 (75%) |
| **No Mapping** | 8 (25%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 28 |
| **Has Async Gap** | 28 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 32 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getExpandableListAdapter` | 3 | composite | Return safe default (null/false/0/empty) |
| `getExpandableListPosition` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFlatListPosition` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPackedPositionChild` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPackedPositionForChild` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPackedPositionForGroup` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPackedPositionGroup` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPackedPositionType` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSelectedId` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSelectedPosition` | 3 | composite | Return safe default (null/false/0/empty) |
| `setAdapter` | 3 | composite | Log warning + no-op |
| `setChildIndicator` | 3 | composite | Log warning + no-op |
| `setChildIndicatorBounds` | 3 | composite | Log warning + no-op |
| `setChildIndicatorBoundsRelative` | 3 | composite | Log warning + no-op |
| `setGroupIndicator` | 3 | composite | Log warning + no-op |
| `setIndicatorBounds` | 3 | composite | Log warning + no-op |
| `setIndicatorBoundsRelative` | 3 | composite | Log warning + no-op |
| `setOnChildClickListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setOnGroupClickListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setOnGroupExpandListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setSelectedChild` | 3 | composite | Log warning + no-op |
| `setSelectedGroup` | 3 | composite | Log warning + no-op |
| `setOnGroupCollapseListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setChildDivider` | 3 | composite | Log warning + no-op |
| `ExpandableListView` | 1 | none | Return safe default (null/false/0/empty) |
| `ExpandableListView` | 1 | none | Return safe default (null/false/0/empty) |
| `ExpandableListView` | 1 | none | Return safe default (null/false/0/empty) |
| `ExpandableListView` | 1 | none | Return safe default (null/false/0/empty) |
| `collapseGroup` | 1 | none | throw UnsupportedOperationException |
| `expandGroup` | 1 | none | throw UnsupportedOperationException |
| `expandGroup` | 1 | none | throw UnsupportedOperationException |
| `isGroupExpanded` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.ExpandableListView`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.ExpandableListView` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 32 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
