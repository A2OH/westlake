# SKILL: android.widget.RemoteViews

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.RemoteViews`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.RemoteViews` |
| **Package** | `android.widget` |
| **Total Methods** | 57 |
| **Avg Score** | 2.6 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 48 (84%) |
| **No Mapping** | 9 (15%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 53 |
| **Has Async Gap** | 53 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 57 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `addView` | 3 | composite | Log warning + no-op |
| `getLayoutId` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPackage` | 3 | composite | Return safe default (null/false/0/empty) |
| `removeAllViews` | 3 | composite | Log warning + no-op |
| `setAccessibilityTraversalAfter` | 3 | composite | Log warning + no-op |
| `setAccessibilityTraversalBefore` | 3 | composite | Log warning + no-op |
| `setBitmap` | 3 | composite | Log warning + no-op |
| `setBoolean` | 3 | composite | Log warning + no-op |
| `setBundle` | 3 | composite | Log warning + no-op |
| `setByte` | 3 | composite | Log warning + no-op |
| `setChar` | 3 | composite | Log warning + no-op |
| `setCharSequence` | 3 | composite | Log warning + no-op |
| `setChronometer` | 3 | composite | Log warning + no-op |
| `setChronometerCountDown` | 3 | composite | Log warning + no-op |
| `setFloat` | 3 | composite | Log warning + no-op |
| `setIcon` | 3 | composite | Log warning + no-op |
| `setImageViewBitmap` | 3 | composite | Log warning + no-op |
| `setImageViewIcon` | 3 | composite | Log warning + no-op |
| `setImageViewResource` | 3 | composite | Log warning + no-op |
| `setImageViewUri` | 3 | composite | Log warning + no-op |
| `setInt` | 3 | composite | Log warning + no-op |
| `setIntent` | 3 | composite | Log warning + no-op |
| `setLabelFor` | 3 | composite | Log warning + no-op |
| `setLightBackgroundLayoutId` | 3 | composite | Log warning + no-op |
| `setOnClickPendingIntent` | 3 | composite | Log warning + no-op |
| `setOnClickResponse` | 3 | composite | Log warning + no-op |
| `setPendingIntentTemplate` | 3 | composite | Log warning + no-op |
| `setRelativeScrollPosition` | 3 | composite | Log warning + no-op |
| `setRemoteAdapter` | 3 | composite | Log warning + no-op |
| `setScrollPosition` | 3 | composite | Log warning + no-op |
| `setShort` | 3 | composite | Log warning + no-op |
| `setString` | 3 | composite | Log warning + no-op |
| `setTextColor` | 3 | composite | Log warning + no-op |
| `setTextViewCompoundDrawables` | 3 | composite | Log warning + no-op |
| `setTextViewCompoundDrawablesRelative` | 3 | composite | Log warning + no-op |
| `setTextViewText` | 3 | composite | Log warning + no-op |
| `setViewVisibility` | 3 | composite | Return safe default (null/false/0/empty) |
| `setEmptyView` | 3 | composite | Log warning + no-op |
| `setTextViewTextSize` | 3 | composite | Log warning + no-op |
| `setUri` | 3 | composite | Log warning + no-op |
| `setProgressBar` | 3 | composite | Log warning + no-op |
| `setViewPadding` | 3 | composite | Log warning + no-op |
| `setLong` | 3 | composite | Log warning + no-op |
| `setOnClickFillInIntent` | 3 | composite | Log warning + no-op |
| `setDouble` | 3 | composite | Log warning + no-op |
| `setDisplayedChild` | 3 | composite | Return safe default (null/false/0/empty) |
| `setContentDescription` | 2 | composite | Log warning + no-op |
| `writeToParcel` | 2 | composite | Log warning + no-op |
| `RemoteViews` | 1 | none | throw UnsupportedOperationException |
| `RemoteViews` | 1 | none | throw UnsupportedOperationException |
| `RemoteViews` | 1 | none | throw UnsupportedOperationException |
| `RemoteViews` | 1 | none | throw UnsupportedOperationException |
| `apply` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |
| `reapply` | 1 | none | throw UnsupportedOperationException |
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

Check if these related classes are already shimmed before generating `android.widget.RemoteViews`:

- `android.view.View` (already shimmed)
- `android.content.Intent` (already shimmed)

## Quality Gates

Before marking `android.widget.RemoteViews` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 57 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
