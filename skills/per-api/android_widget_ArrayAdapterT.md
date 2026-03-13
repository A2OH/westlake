# SKILL: android.widget.ArrayAdapter<T>

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.ArrayAdapter<T>`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.ArrayAdapter<T>` |
| **Package** | `android.widget` |
| **Total Methods** | 19 |
| **Avg Score** | 2.1 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 11 (57%) |
| **No Mapping** | 8 (42%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 13 |
| **Has Async Gap** | 13 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 19 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `add` | 3 | composite | Log warning + no-op |
| `addAll` | 3 | composite | Log warning + no-op |
| `addAll` | 3 | composite | Log warning + no-op |
| `clear` | 3 | composite | throw UnsupportedOperationException |
| `getCount` | 3 | composite | Return safe default (null/false/0/empty) |
| `getItemId` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPosition` | 3 | composite | Return safe default (null/false/0/empty) |
| `remove` | 3 | composite | Log warning + no-op |
| `setDropDownViewResource` | 3 | composite | Log warning + no-op |
| `setNotifyOnChange` | 3 | composite | Log warning + no-op |
| `setDropDownViewTheme` | 2 | composite | Log warning + no-op |
| `ArrayAdapter` | 1 | none | throw UnsupportedOperationException |
| `ArrayAdapter` | 1 | none | throw UnsupportedOperationException |
| `ArrayAdapter` | 1 | none | throw UnsupportedOperationException |
| `ArrayAdapter` | 1 | none | throw UnsupportedOperationException |
| `ArrayAdapter` | 1 | none | throw UnsupportedOperationException |
| `ArrayAdapter` | 1 | none | throw UnsupportedOperationException |
| `insert` | 1 | none | throw UnsupportedOperationException |
| `sort` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.ArrayAdapter<T>`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.ArrayAdapter<T>` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 19 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
