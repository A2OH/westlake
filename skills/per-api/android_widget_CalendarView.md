# SKILL: android.widget.CalendarView

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.CalendarView`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.CalendarView` |
| **Package** | `android.widget` |
| **Total Methods** | 16 |
| **Avg Score** | 2.5 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 12 (75%) |
| **No Mapping** | 4 (25%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 12 |
| **Has Async Gap** | 12 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 16 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getDate` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFirstDayOfWeek` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMaxDate` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMinDate` | 3 | composite | Return safe default (null/false/0/empty) |
| `setDate` | 3 | composite | Log warning + no-op |
| `setDate` | 3 | composite | Log warning + no-op |
| `setDateTextAppearance` | 3 | composite | Log warning + no-op |
| `setFirstDayOfWeek` | 3 | composite | Log warning + no-op |
| `setMaxDate` | 3 | composite | Log warning + no-op |
| `setMinDate` | 3 | composite | Log warning + no-op |
| `setOnDateChangeListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setWeekDayTextAppearance` | 3 | composite | Log warning + no-op |
| `CalendarView` | 1 | none | throw UnsupportedOperationException |
| `CalendarView` | 1 | none | throw UnsupportedOperationException |
| `CalendarView` | 1 | none | throw UnsupportedOperationException |
| `CalendarView` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.CalendarView`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.CalendarView` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 16 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
