# SKILL: android.view.WindowId

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.WindowId`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.WindowId` |
| **Package** | `android.view` |
| **Total Methods** | 8 |
| **Avg Score** | 2.6 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 6 (75%) |
| **No Mapping** | 2 (25%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 7 |
| **Has Async Gap** | 7 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `FocusObserver` | `WindowId.FocusObserver()` | 5 | partial | moderate | `createComponentObserver` | `createComponentObserver(id: string): ComponentObserver` |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `registerFocusObserver` | 3 | composite | Return safe default (null/false/0/empty) |
| `unregisterFocusObserver` | 3 | composite | Return safe default (null/false/0/empty) |
| `onFocusGained` | 3 | composite | Store callback, never fire |
| `onFocusLost` | 3 | composite | Store callback, never fire |
| `writeToParcel` | 2 | composite | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |
| `isFocused` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.WindowId`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.view.WindowId` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
