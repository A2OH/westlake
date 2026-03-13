# SKILL: android.view.ViewStructure

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.ViewStructure`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.ViewStructure` |
| **Package** | `android.view` |
| **Total Methods** | 60 |
| **Avg Score** | 2.9 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 55 (91%) |
| **No Mapping** | 5 (8%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 57 |
| **Has Async Gap** | 57 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Builder` | `ViewStructure.HtmlInfo.Builder()` | 6 | partial | moderate | `createFromBuilder` | `createFromBuilder(builder: CustomBuilder, callback: AsyncCallback<image.PixelMap>): void` |

## Stub APIs (score < 5): 59 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `addChildCount` | 3 | composite | Log warning + no-op |
| `asyncCommit` | 3 | composite | throw UnsupportedOperationException |
| `asyncNewChild` | 3 | composite | throw UnsupportedOperationException |
| `getChildCount` | 3 | composite | Return safe default (null/false/0/empty) |
| `getExtras` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHint` | 3 | composite | Return safe default (null/false/0/empty) |
| `getText` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTextSelectionEnd` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTextSelectionStart` | 3 | composite | Return dummy instance / no-op |
| `newHtmlInfoBuilder` | 3 | composite | throw UnsupportedOperationException |
| `setAccessibilityFocused` | 3 | composite | Log warning + no-op |
| `setActivated` | 3 | composite | Log warning + no-op |
| `setAlpha` | 3 | composite | Log warning + no-op |
| `setAutofillHints` | 3 | composite | Log warning + no-op |
| `setAutofillId` | 3 | composite | Log warning + no-op |
| `setAutofillId` | 3 | composite | Log warning + no-op |
| `setAutofillOptions` | 3 | composite | Log warning + no-op |
| `setAutofillType` | 3 | composite | Log warning + no-op |
| `setAutofillValue` | 3 | composite | Log warning + no-op |
| `setCheckable` | 3 | composite | Log warning + no-op |
| `setChecked` | 3 | composite | Log warning + no-op |
| `setChildCount` | 3 | composite | Log warning + no-op |
| `setClassName` | 3 | composite | Log warning + no-op |
| `setDataIsSensitive` | 3 | composite | Return safe default (null/false/0/empty) |
| `setDimens` | 3 | composite | Log warning + no-op |
| `setElevation` | 3 | composite | Log warning + no-op |
| `setEnabled` | 3 | composite | Log warning + no-op |
| `setFocused` | 3 | composite | Log warning + no-op |
| `setHint` | 3 | composite | Log warning + no-op |
| `setHintIdEntry` | 3 | composite | Log warning + no-op |
| `setHtmlInfo` | 3 | composite | Log warning + no-op |
| `setId` | 3 | composite | Log warning + no-op |
| `setImportantForAutofill` | 3 | composite | Log warning + no-op |
| `setInputType` | 3 | composite | Log warning + no-op |
| `setLocaleList` | 3 | composite | Return safe default (null/false/0/empty) |
| `setLongClickable` | 3 | composite | Log warning + no-op |
| `setMaxTextLength` | 3 | composite | Log warning + no-op |
| `setMinTextEms` | 3 | composite | Log warning + no-op |
| `setOpaque` | 3 | composite | Log warning + no-op |
| `setSelected` | 3 | composite | Log warning + no-op |
| `setText` | 3 | composite | Log warning + no-op |
| `setText` | 3 | composite | Log warning + no-op |
| `setTextIdEntry` | 3 | composite | Log warning + no-op |
| `setTextLines` | 3 | composite | Log warning + no-op |
| `setTextStyle` | 3 | composite | Log warning + no-op |
| `setTransformation` | 3 | composite | Log warning + no-op |
| `setVisibility` | 3 | composite | Return safe default (null/false/0/empty) |
| `addAttribute` | 3 | composite | Log warning + no-op |
| `setClickable` | 3 | composite | Log warning + no-op |
| `setFocusable` | 3 | composite | Log warning + no-op |
| `setWebDomain` | 3 | composite | Log warning + no-op |
| `setContextClickable` | 3 | composite | Log warning + no-op |
| `setMaxTextEms` | 3 | composite | Log warning + no-op |
| `setContentDescription` | 2 | composite | Log warning + no-op |
| `ViewStructure` | 1 | none | throw UnsupportedOperationException |
| `HtmlInfo` | 1 | none | throw UnsupportedOperationException |
| `hasExtras` | 1 | none | Return safe default (null/false/0/empty) |
| `newChild` | 1 | none | throw UnsupportedOperationException |
| `build` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.ViewStructure`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.view.ViewStructure` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 60 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
