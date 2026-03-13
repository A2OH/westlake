# SKILL: android.widget.AdapterViewAnimator

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.AdapterViewAnimator`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.AdapterViewAnimator` |
| **Package** | `android.widget` |
| **Total Methods** | 28 |
| **Avg Score** | 2.4 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 20 (71%) |
| **No Mapping** | 8 (28%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 24 |
| **Has Async Gap** | 24 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 28 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `deferNotifyDataSetChanged` | 3 | composite | Log warning + no-op |
| `getAdapter` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCurrentView` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDisplayedChild` | 3 | composite | Return safe default (null/false/0/empty) |
| `getInAnimation` | 3 | composite | Return safe default (null/false/0/empty) |
| `getOutAnimation` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSelectedView` | 3 | composite | Return safe default (null/false/0/empty) |
| `onRemoteAdapterConnected` | 3 | composite | Return dummy instance / no-op |
| `onRemoteAdapterDisconnected` | 3 | composite | Return dummy instance / no-op |
| `onRestoreInstanceState` | 3 | composite | Store callback, never fire |
| `onSaveInstanceState` | 3 | composite | Store callback, never fire |
| `setAdapter` | 3 | composite | Log warning + no-op |
| `setAnimateFirstView` | 3 | composite | Log warning + no-op |
| `setInAnimation` | 3 | composite | Log warning + no-op |
| `setInAnimation` | 3 | composite | Log warning + no-op |
| `setOutAnimation` | 3 | composite | Log warning + no-op |
| `setOutAnimation` | 3 | composite | Log warning + no-op |
| `setRemoteViewsAdapter` | 3 | composite | Log warning + no-op |
| `setSelection` | 3 | composite | Log warning + no-op |
| `setDisplayedChild` | 3 | composite | Return safe default (null/false/0/empty) |
| `fyiWillBeAdvancedByHostKThx` | 1 | none | throw UnsupportedOperationException |
| `AdapterViewAnimator` | 1 | none | throw UnsupportedOperationException |
| `AdapterViewAnimator` | 1 | none | throw UnsupportedOperationException |
| `AdapterViewAnimator` | 1 | none | throw UnsupportedOperationException |
| `AdapterViewAnimator` | 1 | none | throw UnsupportedOperationException |
| `advance` | 1 | none | throw UnsupportedOperationException |
| `showNext` | 1 | none | throw UnsupportedOperationException |
| `showPrevious` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.AdapterViewAnimator`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.AdapterViewAnimator` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 28 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
