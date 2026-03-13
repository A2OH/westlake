# SKILL: android.app.MediaRouteButton

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.MediaRouteButton`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.MediaRouteButton` |
| **Package** | `android.app` |
| **Total Methods** | 10 |
| **Avg Score** | 3.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (20%) |
| **Partial/Composite** | 4 (40%) |
| **No Mapping** | 4 (40%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `setRouteTypes` | `void setRouteTypes(int)` | 7 | near | moderate | `setRouterProxy` | `setRouterProxy(formIds: Array<string>, proxy: Callback<Want>, callback: AsyncCallback<void>): void` |
| `getRouteTypes` | `int getRouteTypes()` | 6 | near | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `onDetachedFromWindow` | `void onDetachedFromWindow()` | 6 | partial | moderate | `getWindow` | `getWindow(callback: AsyncCallback<window.Window>): void` |
| `showDialog` | `void showDialog()` | 5 | partial | moderate | `sendDialogResult` | `sendDialogResult(dialogSessionId: string, targetWant: Want, isAllowed: boolean): Promise<void>` |
| `onAttachedToWindow` | `void onAttachedToWindow()` | 5 | partial | moderate | `getWindow` | `getWindow(callback: AsyncCallback<window.Window>): void` |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setExtendedSettingsClickListener` | 4 | partial | Return safe default (null/false/0/empty) |
| `MediaRouteButton` | 1 | none | Store callback, never fire |
| `MediaRouteButton` | 1 | none | Store callback, never fire |
| `MediaRouteButton` | 1 | none | Store callback, never fire |
| `MediaRouteButton` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 5 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.MediaRouteButton`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.MediaRouteButton` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
