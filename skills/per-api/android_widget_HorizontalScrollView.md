# SKILL: android.widget.HorizontalScrollView

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.HorizontalScrollView`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.HorizontalScrollView` |
| **Package** | `android.widget` |
| **Total Methods** | 20 |
| **Avg Score** | 1.9 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 9 (45%) |
| **No Mapping** | 11 (55%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 16 |
| **Has Async Gap** | 16 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 20 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `computeScrollDeltaToGetChildRectOnScreen` | 3 | composite | Return safe default (null/false/0/empty) |
| `executeKeyEvent` | 3 | composite | throw UnsupportedOperationException |
| `getMaxScrollAmount` | 3 | composite | Return safe default (null/false/0/empty) |
| `setEdgeEffectColor` | 3 | composite | Log warning + no-op |
| `setFillViewport` | 3 | composite | Log warning + no-op |
| `setRightEdgeEffectColor` | 3 | composite | Log warning + no-op |
| `smoothScrollBy` | 3 | composite | throw UnsupportedOperationException |
| `setSmoothScrollingEnabled` | 3 | composite | Log warning + no-op |
| `setLeftEdgeEffectColor` | 3 | composite | Log warning + no-op |
| `smoothScrollTo` | 2 | none | throw UnsupportedOperationException |
| `HorizontalScrollView` | 1 | none | Store callback, never fire |
| `HorizontalScrollView` | 1 | none | Store callback, never fire |
| `HorizontalScrollView` | 1 | none | Store callback, never fire |
| `HorizontalScrollView` | 1 | none | Store callback, never fire |
| `arrowScroll` | 1 | none | throw UnsupportedOperationException |
| `fling` | 1 | none | throw UnsupportedOperationException |
| `fullScroll` | 1 | none | throw UnsupportedOperationException |
| `isFillViewport` | 1 | none | Return safe default (null/false/0/empty) |
| `isSmoothScrollingEnabled` | 1 | none | Return safe default (null/false/0/empty) |
| `pageScroll` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.HorizontalScrollView`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.HorizontalScrollView` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 20 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
