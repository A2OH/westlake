# SKILL: android.widget.CursorTreeAdapter

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.CursorTreeAdapter`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.CursorTreeAdapter` |
| **Package** | `android.widget` |
| **Total Methods** | 27 |
| **Avg Score** | 2.3 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 18 (66%) |
| **No Mapping** | 9 (33%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 25 |
| **Has Async Gap** | 25 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 27 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `convertToString` | 3 | composite | Store callback, never fire |
| `getChild` | 3 | composite | Return safe default (null/false/0/empty) |
| `getChildId` | 3 | composite | Return safe default (null/false/0/empty) |
| `getChildView` | 3 | composite | Return safe default (null/false/0/empty) |
| `getChildrenCount` | 3 | composite | Return safe default (null/false/0/empty) |
| `getChildrenCursor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCursor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFilter` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFilterQueryProvider` | 3 | composite | Return safe default (null/false/0/empty) |
| `getGroup` | 3 | composite | Return safe default (null/false/0/empty) |
| `getGroupCount` | 3 | composite | Return safe default (null/false/0/empty) |
| `getGroupId` | 3 | composite | Return safe default (null/false/0/empty) |
| `getGroupView` | 3 | composite | Return safe default (null/false/0/empty) |
| `notifyDataSetChanged` | 3 | composite | Log warning + no-op |
| `runQueryOnBackgroundThread` | 3 | composite | Return safe default (null/false/0/empty) |
| `setChildrenCursor` | 3 | composite | Log warning + no-op |
| `setFilterQueryProvider` | 3 | composite | Return safe default (null/false/0/empty) |
| `setGroupCursor` | 2 | composite | Log warning + no-op |
| `CursorTreeAdapter` | 1 | none | throw UnsupportedOperationException |
| `CursorTreeAdapter` | 1 | none | throw UnsupportedOperationException |
| `bindChildView` | 1 | none | throw UnsupportedOperationException |
| `bindGroupView` | 1 | none | throw UnsupportedOperationException |
| `changeCursor` | 1 | none | throw UnsupportedOperationException |
| `hasStableIds` | 1 | none | Return safe default (null/false/0/empty) |
| `isChildSelectable` | 1 | none | Return safe default (null/false/0/empty) |
| `newChildView` | 1 | none | throw UnsupportedOperationException |
| `newGroupView` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.CursorTreeAdapter`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.CursorTreeAdapter` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 27 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
