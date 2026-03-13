# SKILL: android.view.Menu

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.Menu`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.Menu` |
| **Package** | `android.view` |
| **Total Methods** | 25 |
| **Avg Score** | 2.7 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 22 (88%) |
| **No Mapping** | 3 (12%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 25 |
| **Has Async Gap** | 25 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 25 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `add` | 3 | composite | Log warning + no-op |
| `add` | 3 | composite | Log warning + no-op |
| `add` | 3 | composite | Log warning + no-op |
| `add` | 3 | composite | Log warning + no-op |
| `addIntentOptions` | 3 | composite | Log warning + no-op |
| `addSubMenu` | 3 | composite | Log warning + no-op |
| `addSubMenu` | 3 | composite | Log warning + no-op |
| `addSubMenu` | 3 | composite | Log warning + no-op |
| `addSubMenu` | 3 | composite | Log warning + no-op |
| `clear` | 3 | composite | throw UnsupportedOperationException |
| `close` | 3 | composite | No-op |
| `getItem` | 3 | composite | Return safe default (null/false/0/empty) |
| `isShortcutKey` | 3 | composite | Return safe default (null/false/0/empty) |
| `performIdentifierAction` | 3 | composite | Store callback, never fire |
| `removeGroup` | 3 | composite | Log warning + no-op |
| `removeItem` | 3 | composite | Log warning + no-op |
| `setGroupCheckable` | 3 | composite | Log warning + no-op |
| `setGroupVisible` | 3 | composite | Return safe default (null/false/0/empty) |
| `size` | 3 | composite | throw UnsupportedOperationException |
| `setQwertyMode` | 3 | composite | Log warning + no-op |
| `setGroupEnabled` | 3 | composite | Log warning + no-op |
| `setGroupDividerEnabled` | 3 | composite | Log warning + no-op |
| `findItem` | 1 | none | Return safe default (null/false/0/empty) |
| `hasVisibleItems` | 1 | none | Return safe default (null/false/0/empty) |
| `performShortcut` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.Menu`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.view.Menu` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 25 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
