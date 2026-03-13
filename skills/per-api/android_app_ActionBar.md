# SKILL: android.app.ActionBar

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.ActionBar`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.ActionBar` |
| **Package** | `android.app` |
| **Total Methods** | 44 |
| **Avg Score** | 4.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 5 (11%) |
| **Partial/Composite** | 37 (84%) |
| **No Mapping** | 2 (4%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 15 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `show` | `abstract void show()` | 8 | direct | easy | `show` | `show(uri: string, type: string): Promise<void>` |
| `getThemedContext` | `android.content.Context getThemedContext()` | 8 | near | easy | `getContext` | `getContext(): Context` |
| `getElevation` | `float getElevation()` | 6 | near | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `getTitle` | `abstract CharSequence getTitle()` | 6 | near | moderate | `getId` | `getId(uri: string): number` |
| `setBackgroundDrawable` | `abstract void setBackgroundDrawable(@Nullable android.graphics.drawable.Drawable)` | 6 | near | moderate | `startBackgroundRunning` | `startBackgroundRunning(id: number, request: NotificationRequest, callback: AsyncCallback<void>): void` |
| `getSubtitle` | `abstract CharSequence getSubtitle()` | 6 | partial | moderate | `getUid` | `getUid(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `setSplitBackgroundDrawable` | `void setSplitBackgroundDrawable(android.graphics.drawable.Drawable)` | 6 | partial | moderate | `startBackgroundRunning` | `startBackgroundRunning(id: number, request: NotificationRequest, callback: AsyncCallback<void>): void` |
| `getDisplayOptions` | `abstract int getDisplayOptions()` | 6 | partial | moderate | `getForegroundApplications` | `getForegroundApplications(callback: AsyncCallback<Array<AppStateData>>): void` |
| `getHeight` | `abstract int getHeight()` | 6 | partial | moderate | `getId` | `getId(uri: string): number` |
| `setStackedBackgroundDrawable` | `void setStackedBackgroundDrawable(android.graphics.drawable.Drawable)` | 6 | partial | moderate | `startBackgroundRunning` | `startBackgroundRunning(id: number, request: NotificationRequest, callback: AsyncCallback<void>): void` |
| `getHideOffset` | `int getHideOffset()` | 6 | partial | moderate | `getId` | `getId(uri: string): number` |
| `setElevation` | `void setElevation(float)` | 5 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `getCustomView` | `abstract android.view.View getCustomView()` | 5 | partial | moderate | `getUid` | `getUid(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `setDisplayOptions` | `abstract void setDisplayOptions(int)` | 5 | partial | moderate | `setApplicationAutoStartup` | `setApplicationAutoStartup(info: AutoStartupInfo, callback: AsyncCallback<void>): void` |
| `setDisplayOptions` | `abstract void setDisplayOptions(int, int)` | 5 | partial | moderate | `setApplicationAutoStartup` | `setApplicationAutoStartup(info: AutoStartupInfo, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 29 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `removeOnMenuVisibilityListener` | 5 | partial | Return safe default (null/false/0/empty) |
| `setHomeAsUpIndicator` | 5 | partial | Log warning + no-op |
| `setHomeAsUpIndicator` | 5 | partial | Log warning + no-op |
| `setSubtitle` | 5 | partial | Log warning + no-op |
| `setSubtitle` | 5 | partial | Log warning + no-op |
| `setLogo` | 5 | partial | Log warning + no-op |
| `setLogo` | 5 | partial | Log warning + no-op |
| `isShowing` | 5 | partial | Return safe default (null/false/0/empty) |
| `setHomeActionContentDescription` | 5 | partial | Log warning + no-op |
| `setHomeActionContentDescription` | 5 | partial | Log warning + no-op |
| `setTitle` | 5 | partial | Log warning + no-op |
| `setTitle` | 5 | partial | Log warning + no-op |
| `setCustomView` | 4 | partial | Log warning + no-op |
| `setCustomView` | 4 | partial | Log warning + no-op |
| `setCustomView` | 4 | partial | Log warning + no-op |
| `setHideOffset` | 4 | partial | Log warning + no-op |
| `setDisplayHomeAsUpEnabled` | 4 | partial | Return safe default (null/false/0/empty) |
| `setIcon` | 4 | partial | Log warning + no-op |
| `setIcon` | 4 | partial | Log warning + no-op |
| `setDisplayShowCustomEnabled` | 4 | partial | Return safe default (null/false/0/empty) |
| `setHomeButtonEnabled` | 4 | partial | Log warning + no-op |
| `addOnMenuVisibilityListener` | 4 | partial | Return safe default (null/false/0/empty) |
| `setDisplayShowTitleEnabled` | 4 | composite | Return safe default (null/false/0/empty) |
| `isHideOnContentScrollEnabled` | 4 | composite | Return safe default (null/false/0/empty) |
| `setHideOnContentScrollEnabled` | 4 | composite | Log warning + no-op |
| `setDisplayUseLogoEnabled` | 4 | composite | Return safe default (null/false/0/empty) |
| `setDisplayShowHomeEnabled` | 4 | composite | Return safe default (null/false/0/empty) |
| `ActionBar` | 1 | none | Store callback, never fire |
| `hide` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 15 methods that have score >= 5
2. Stub 29 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.ActionBar`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.ActionBar` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 44 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 15 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
