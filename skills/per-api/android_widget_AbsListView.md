# SKILL: android.widget.AbsListView

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.AbsListView`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.AbsListView` |
| **Package** | `android.widget` |
| **Total Methods** | 78 |
| **Avg Score** | 2.5 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 58 (74%) |
| **No Mapping** | 20 (25%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 74 |
| **Has Async Gap** | 74 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 78 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `deferNotifyDataSetChanged` | 3 | composite | Log warning + no-op |
| `getCheckedItemCount` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCheckedItemIds` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCheckedItemPosition` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCheckedItemPositions` | 3 | composite | Return safe default (null/false/0/empty) |
| `getChoiceMode` | 3 | composite | Return safe default (null/false/0/empty) |
| `getListPaddingBottom` | 3 | composite | Return safe default (null/false/0/empty) |
| `getListPaddingLeft` | 3 | composite | Return safe default (null/false/0/empty) |
| `getListPaddingRight` | 3 | composite | Return safe default (null/false/0/empty) |
| `getListPaddingTop` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSelector` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTextFilter` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTranscriptMode` | 3 | composite | Return safe default (null/false/0/empty) |
| `handleDataChanged` | 3 | composite | throw UnsupportedOperationException |
| `onFilterComplete` | 3 | composite | Store callback, never fire |
| `onGlobalLayout` | 3 | composite | Store callback, never fire |
| `onRemoteAdapterConnected` | 3 | composite | Return dummy instance / no-op |
| `onRemoteAdapterDisconnected` | 3 | composite | Return dummy instance / no-op |
| `onRestoreInstanceState` | 3 | composite | Store callback, never fire |
| `onSaveInstanceState` | 3 | composite | Store callback, never fire |
| `onTextChanged` | 3 | composite | Store callback, never fire |
| `onTouchModeChanged` | 3 | composite | Store callback, never fire |
| `pointToPosition` | 3 | composite | Store callback, never fire |
| `pointToRowId` | 3 | composite | throw UnsupportedOperationException |
| `setAdapter` | 3 | composite | Log warning + no-op |
| `setCacheColorHint` | 3 | composite | Log warning + no-op |
| `setChoiceMode` | 3 | composite | Log warning + no-op |
| `setEdgeEffectColor` | 3 | composite | Log warning + no-op |
| `setFastScrollAlwaysVisible` | 3 | composite | Return safe default (null/false/0/empty) |
| `setFastScrollEnabled` | 3 | composite | Log warning + no-op |
| `setFastScrollStyle` | 3 | composite | Log warning + no-op |
| `setFilterText` | 3 | composite | Log warning + no-op |
| `setFriction` | 3 | composite | Log warning + no-op |
| `setItemChecked` | 3 | composite | Log warning + no-op |
| `setMultiChoiceModeListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setOnScrollListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setRecyclerListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setRemoteViewsAdapter` | 3 | composite | Log warning + no-op |
| `setScrollIndicators` | 3 | composite | Log warning + no-op |
| `setScrollingCacheEnabled` | 3 | composite | Log warning + no-op |
| `setSelectionFromTop` | 3 | composite | Log warning + no-op |
| `setSelector` | 3 | composite | Log warning + no-op |
| `setSelector` | 3 | composite | Log warning + no-op |
| `setSmoothScrollbarEnabled` | 3 | composite | Log warning + no-op |
| `setStackFromBottom` | 3 | composite | Log warning + no-op |
| `setTextFilterEnabled` | 3 | composite | Log warning + no-op |
| `setTopEdgeEffectColor` | 3 | composite | Log warning + no-op |
| `setTranscriptMode` | 3 | composite | Log warning + no-op |
| `setVelocityScale` | 3 | composite | Log warning + no-op |
| `smoothScrollBy` | 3 | composite | throw UnsupportedOperationException |
| `smoothScrollToPositionFromTop` | 3 | composite | Store callback, never fire |
| `smoothScrollToPositionFromTop` | 3 | composite | Store callback, never fire |
| `scrollListBy` | 3 | composite | Return safe default (null/false/0/empty) |
| `setDrawSelectorOnTop` | 3 | composite | Log warning + no-op |
| `smoothScrollToPosition` | 2 | composite | Store callback, never fire |
| `smoothScrollToPosition` | 2 | composite | Store callback, never fire |
| `setBottomEdgeEffectColor` | 2 | composite | Log warning + no-op |
| `onInitializeAccessibilityNodeInfoForItem` | 2 | composite | Return dummy instance / no-op |
| `isDrawSelectorOnTop` | 2 | none | Return safe default (null/false/0/empty) |
| `AbsListView` | 1 | none | Return safe default (null/false/0/empty) |
| `AbsListView` | 1 | none | Return safe default (null/false/0/empty) |
| `AbsListView` | 1 | none | Return safe default (null/false/0/empty) |
| `AbsListView` | 1 | none | Return safe default (null/false/0/empty) |
| `afterTextChanged` | 1 | none | throw UnsupportedOperationException |
| `beforeTextChanged` | 1 | none | throw UnsupportedOperationException |
| `canScrollList` | 1 | none | Return safe default (null/false/0/empty) |
| `clearChoices` | 1 | none | throw UnsupportedOperationException |
| `clearTextFilter` | 1 | none | throw UnsupportedOperationException |
| `fling` | 1 | none | throw UnsupportedOperationException |
| `generateLayoutParams` | 1 | none | throw UnsupportedOperationException |
| `hasTextFilter` | 1 | none | Return safe default (null/false/0/empty) |
| `invalidateViews` | 1 | none | throw UnsupportedOperationException |
| `isFastScrollAlwaysVisible` | 1 | none | Return safe default (null/false/0/empty) |
| `isInFilterMode` | 1 | none | Return safe default (null/false/0/empty) |
| `isItemChecked` | 1 | none | Return safe default (null/false/0/empty) |
| `layoutChildren` | 1 | none | throw UnsupportedOperationException |
| `reclaimViews` | 1 | none | throw UnsupportedOperationException |
| `verifyDrawable` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.AbsListView`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.AbsListView` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 78 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
