# SKILL: android.widget.SearchView

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.SearchView`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.SearchView` |
| **Package** | `android.widget` |
| **Total Methods** | 31 |
| **Avg Score** | 2.5 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 24 (77%) |
| **No Mapping** | 7 (22%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 27 |
| **Has Async Gap** | 27 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 31 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getImeOptions` | 3 | composite | Return safe default (null/false/0/empty) |
| `getInputType` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMaxWidth` | 3 | composite | Return safe default (null/false/0/empty) |
| `getQuery` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSuggestionsAdapter` | 3 | composite | Return safe default (null/false/0/empty) |
| `isIconifiedByDefault` | 3 | composite | Return safe default (null/false/0/empty) |
| `onActionViewCollapsed` | 3 | composite | Store callback, never fire |
| `onActionViewExpanded` | 3 | composite | Store callback, never fire |
| `setIconified` | 3 | composite | Log warning + no-op |
| `setIconifiedByDefault` | 3 | composite | Log warning + no-op |
| `setImeOptions` | 3 | composite | Log warning + no-op |
| `setInputType` | 3 | composite | Log warning + no-op |
| `setMaxWidth` | 3 | composite | Log warning + no-op |
| `setOnCloseListener` | 3 | composite | No-op |
| `setOnQueryTextFocusChangeListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setOnQueryTextListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setOnSearchClickListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setOnSuggestionListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setQueryHint` | 3 | composite | Return safe default (null/false/0/empty) |
| `setQueryRefinementEnabled` | 3 | composite | Return safe default (null/false/0/empty) |
| `setSubmitButtonEnabled` | 3 | composite | Log warning + no-op |
| `setSuggestionsAdapter` | 3 | composite | Log warning + no-op |
| `setSearchableInfo` | 3 | composite | Log warning + no-op |
| `setQuery` | 3 | composite | Return safe default (null/false/0/empty) |
| `SearchView` | 1 | none | throw UnsupportedOperationException |
| `SearchView` | 1 | none | throw UnsupportedOperationException |
| `SearchView` | 1 | none | throw UnsupportedOperationException |
| `SearchView` | 1 | none | throw UnsupportedOperationException |
| `isIconified` | 1 | none | Return safe default (null/false/0/empty) |
| `isQueryRefinementEnabled` | 1 | none | Return safe default (null/false/0/empty) |
| `isSubmitButtonEnabled` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.SearchView`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.SearchView` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 31 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
