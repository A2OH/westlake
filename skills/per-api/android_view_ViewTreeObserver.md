# SKILL: android.view.ViewTreeObserver

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.ViewTreeObserver`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.ViewTreeObserver` |
| **Package** | `android.view` |
| **Total Methods** | 24 |
| **Avg Score** | 2.8 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 23 (95%) |
| **No Mapping** | 1 (4%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 24 |
| **Has Async Gap** | 24 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 24 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `addOnDrawListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `addOnGlobalFocusChangeListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `addOnGlobalLayoutListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `addOnPreDrawListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `addOnScrollChangedListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `addOnSystemGestureExclusionRectsChangedListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `addOnTouchModeChangeListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `addOnWindowAttachListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `addOnWindowFocusChangeListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `registerFrameCommitCallback` | 3 | composite | Return safe default (null/false/0/empty) |
| `removeOnGlobalFocusChangeListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `removeOnScrollChangedListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `removeOnSystemGestureExclusionRectsChangedListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `removeOnTouchModeChangeListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `removeOnWindowFocusChangeListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `unregisterFrameCommitCallback` | 3 | composite | Return safe default (null/false/0/empty) |
| `removeOnDrawListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `removeOnGlobalLayoutListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `removeOnWindowAttachListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `dispatchOnGlobalLayout` | 3 | composite | Return safe default (null/false/0/empty) |
| `removeOnPreDrawListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `dispatchOnDraw` | 2 | composite | Return safe default (null/false/0/empty) |
| `dispatchOnPreDraw` | 2 | composite | Return safe default (null/false/0/empty) |
| `isAlive` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.ViewTreeObserver`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.view.ViewTreeObserver` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 24 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
