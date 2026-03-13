# SKILL: android.view.ActionMode

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.ActionMode`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.ActionMode` |
| **Package** | `android.view` |
| **Total Methods** | 23 |
| **Avg Score** | 2.7 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 19 (82%) |
| **No Mapping** | 4 (17%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 22 |
| **Has Async Gap** | 22 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 23 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `ActionMode` | 5 | partial | Store callback, never fire |
| `finish` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCustomView` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMenu` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMenuInflater` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSubtitle` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTag` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTitle` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTitleOptionalHint` | 3 | composite | Return safe default (null/false/0/empty) |
| `getType` | 3 | composite | Return safe default (null/false/0/empty) |
| `setCustomView` | 3 | composite | Log warning + no-op |
| `setSubtitle` | 3 | composite | Log warning + no-op |
| `setSubtitle` | 3 | composite | Log warning + no-op |
| `setTag` | 3 | composite | Log warning + no-op |
| `setTitle` | 3 | composite | Log warning + no-op |
| `setTitle` | 3 | composite | Log warning + no-op |
| `setType` | 3 | composite | Log warning + no-op |
| `setTitleOptionalHint` | 3 | composite | Log warning + no-op |
| `onWindowFocusChanged` | 2 | composite | Store callback, never fire |
| `hide` | 1 | none | throw UnsupportedOperationException |
| `invalidate` | 1 | none | throw UnsupportedOperationException |
| `invalidateContentRect` | 1 | none | Store callback, never fire |
| `isTitleOptional` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.ActionMode`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.view.ActionMode` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 23 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
