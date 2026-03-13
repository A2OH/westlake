# SKILL: android.app.Dialog

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.Dialog`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.Dialog` |
| **Package** | `android.app` |
| **Total Methods** | 80 |
| **Avg Score** | 4.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 13 (16%) |
| **Partial/Composite** | 54 (67%) |
| **No Mapping** | 13 (16%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 36 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `cancel` | `void cancel()` | 10 | direct | trivial | `cancel` | `cancel(agent: WantAgent, callback: AsyncCallback<void>): void` |
| `create` | `void create()` | 8 | direct | easy | `create` | `create(config: PiPConfiguration): Promise<PiPController>` |
| `show` | `void show()` | 8 | direct | easy | `show` | `show(uri: string, type: string): Promise<void>` |
| `onPreparePanel` | `boolean onPreparePanel(int, @Nullable android.view.View, @NonNull android.view.Menu)` | 8 | near | easy | `onPrepare` | `onPrepare(): void` |
| `onCreate` | `void onCreate(android.os.Bundle)` | 7 | near | easy | `onPrepare` | `onPrepare(): void` |
| `setOnDismissListener` | `void setOnDismissListener(@Nullable android.content.DialogInterface.OnDismissListener)` | 7 | near | moderate | `registerMissionListener` | `registerMissionListener(listener: MissionListener): number` |
| `onWindowFocusChanged` | `void onWindowFocusChanged(boolean)` | 6 | near | moderate | `hasWindowFocus` | `hasWindowFocus(callback: AsyncCallback<boolean>): void` |
| `openContextMenu` | `void openContextMenu(@NonNull android.view.View)` | 6 | near | moderate | `getContext` | `getContext(): Context` |
| `onCreateContextMenu` | `void onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)` | 6 | near | moderate | `getContext` | `getContext(): Context` |
| `onPrepareOptionsMenu` | `boolean onPrepareOptionsMenu(@NonNull android.view.Menu)` | 6 | near | moderate | `onPrepare` | `onPrepare(): void` |
| `setOnKeyListener` | `void setOnKeyListener(@Nullable android.content.DialogInterface.OnKeyListener)` | 6 | near | moderate | `registerMissionListener` | `registerMissionListener(listener: MissionListener): number` |
| `unregisterForContextMenu` | `void unregisterForContextMenu(@NonNull android.view.View)` | 6 | near | moderate | `unregisterMissionListener` | `unregisterMissionListener(listenerId: number, callback: AsyncCallback<void>): void` |
| `setOnShowListener` | `void setOnShowListener(@Nullable android.content.DialogInterface.OnShowListener)` | 6 | near | moderate | `registerMissionListener` | `registerMissionListener(listener: MissionListener): number` |
| `requestWindowFeature` | `final boolean requestWindowFeature(int)` | 6 | partial | moderate | `getRequestInfo` | `getRequestInfo(want: Want): RequestInfo` |
| `registerForContextMenu` | `void registerForContextMenu(@NonNull android.view.View)` | 6 | partial | moderate | `registerMissionListener` | `registerMissionListener(listener: MissionListener): number` |
| `setOnCancelListener` | `void setOnCancelListener(@Nullable android.content.DialogInterface.OnCancelListener)` | 6 | partial | moderate | `registerMissionListener` | `registerMissionListener(listener: MissionListener): number` |
| `setContentView` | `void setContentView(@LayoutRes int)` | 6 | partial | moderate | `setFormNextRefreshTime` | `setFormNextRefreshTime(formId: string, minute: number, callback: AsyncCallback<void>): void` |
| `setContentView` | `void setContentView(@NonNull android.view.View)` | 6 | partial | moderate | `setFormNextRefreshTime` | `setFormNextRefreshTime(formId: string, minute: number, callback: AsyncCallback<void>): void` |
| `setContentView` | `void setContentView(@NonNull android.view.View, @Nullable android.view.ViewGroup.LayoutParams)` | 6 | partial | moderate | `setFormNextRefreshTime` | `setFormNextRefreshTime(formId: string, minute: number, callback: AsyncCallback<void>): void` |
| `onDetachedFromWindow` | `void onDetachedFromWindow()` | 6 | partial | moderate | `getWindow` | `getWindow(callback: AsyncCallback<window.Window>): void` |
| `Dialog` | `Dialog(@NonNull android.content.Context)` | 5 | partial | moderate | `sendDialogResult` | `sendDialogResult(dialogSessionId: string, targetWant: Want, isAllowed: boolean): Promise<void>` |
| `Dialog` | `Dialog(@NonNull android.content.Context, @StyleRes int)` | 5 | partial | moderate | `sendDialogResult` | `sendDialogResult(dialogSessionId: string, targetWant: Want, isAllowed: boolean): Promise<void>` |
| `Dialog` | `Dialog(@NonNull android.content.Context, boolean, @Nullable android.content.DialogInterface.OnCancelListener)` | 5 | partial | moderate | `sendDialogResult` | `sendDialogResult(dialogSessionId: string, targetWant: Want, isAllowed: boolean): Promise<void>` |
| `onBackPressed` | `void onBackPressed()` | 5 | partial | moderate | `onSuccess` | `onSuccess(): void` |
| `setCancelMessage` | `void setCancelMessage(@Nullable android.os.Message)` | 5 | partial | moderate | `cancel` | `cancel(agent: WantAgent, callback: AsyncCallback<void>): void` |
| `onCreatePanelMenu` | `boolean onCreatePanelMenu(int, @NonNull android.view.Menu)` | 5 | partial | moderate | `onPrepare` | `onPrepare(): void` |
| `onCreatePanelView` | `android.view.View onCreatePanelView(int)` | 5 | partial | moderate | `onPrepare` | `onPrepare(): void` |
| `getVolumeControlStream` | `final int getVolumeControlStream()` | 5 | partial | moderate | `getArguments` | `getArguments(): AbilityDelegatorArgs` |
| `onStart` | `void onStart()` | 5 | partial | moderate | `startAbility` | `startAbility(parameter: StartAbilityParameter, callback: AsyncCallback<number>): void` |
| `onAttachedToWindow` | `void onAttachedToWindow()` | 5 | partial | moderate | `getWindow` | `getWindow(callback: AsyncCallback<window.Window>): void` |
| `setFeatureDrawableUri` | `final void setFeatureDrawableUri(int, @Nullable android.net.Uri)` | 5 | partial | moderate | `setFormNextRefreshTime` | `setFormNextRefreshTime(formId: string, minute: number, callback: AsyncCallback<void>): void` |
| `setVolumeControlStream` | `final void setVolumeControlStream(int)` | 5 | partial | moderate | `setApplicationAutoStartup` | `setApplicationAutoStartup(info: AutoStartupInfo, callback: AsyncCallback<void>): void` |
| `onKeyUp` | `boolean onKeyUp(int, @NonNull android.view.KeyEvent)` | 5 | partial | moderate | `onPrepare` | `onPrepare(): void` |
| `onStop` | `void onStop()` | 5 | partial | moderate | `on` | `on(type: 'abilityForegroundState', observer: AbilityForegroundStateObserver): void` |
| `setFeatureDrawable` | `final void setFeatureDrawable(int, @Nullable android.graphics.drawable.Drawable)` | 5 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `setFeatureDrawableResource` | `final void setFeatureDrawableResource(int, @DrawableRes int)` | 5 | partial | moderate | `setFormNextRefreshTime` | `setFormNextRefreshTime(formId: string, minute: number, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 44 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setFeatureDrawableAlpha` | 5 | partial | Log warning + no-op |
| `onContextMenuClosed` | 5 | partial | No-op |
| `onMenuOpened` | 5 | partial | Return dummy instance / no-op |
| `onTouchEvent` | 5 | partial | Store callback, never fire |
| `onRestoreInstanceState` | 5 | partial | Store callback, never fire |
| `setOwnerActivity` | 5 | partial | Log warning + no-op |
| `isShowing` | 5 | partial | Return safe default (null/false/0/empty) |
| `onSearchRequested` | 5 | partial | Store callback, never fire |
| `onSearchRequested` | 5 | partial | Store callback, never fire |
| `onKeyMultiple` | 5 | partial | Store callback, never fire |
| `onPanelClosed` | 5 | partial | No-op |
| `setTitle` | 5 | partial | Log warning + no-op |
| `setTitle` | 5 | partial | Log warning + no-op |
| `onContextItemSelected` | 5 | partial | Store callback, never fire |
| `onKeyShortcut` | 4 | partial | Store callback, never fire |
| `onKeyLongPress` | 4 | partial | Store callback, never fire |
| `onCreateOptionsMenu` | 4 | partial | Return dummy instance / no-op |
| `onKeyDown` | 4 | partial | Store callback, never fire |
| `onOptionsMenuClosed` | 4 | partial | No-op |
| `findViewById` | 4 | partial | Return safe default (null/false/0/empty) |
| `onWindowAttributesChanged` | 4 | partial | Store callback, never fire |
| `onContentChanged` | 4 | partial | Store callback, never fire |
| `onTrackballEvent` | 4 | partial | Store callback, never fire |
| `setCanceledOnTouchOutside` | 4 | partial | Return safe default (null/false/0/empty) |
| `setDismissMessage` | 4 | composite | Return safe default (null/false/0/empty) |
| `setCancelable` | 4 | composite | Return safe default (null/false/0/empty) |
| `onWindowStartingActionMode` | 4 | composite | Return dummy instance / no-op |
| `onWindowStartingActionMode` | 4 | composite | Return dummy instance / no-op |
| `onOptionsItemSelected` | 3 | composite | Store callback, never fire |
| `onMenuItemSelected` | 3 | composite | Store callback, never fire |
| `onGenericMotionEvent` | 3 | composite | Store callback, never fire |
| `addContentView` | 1 | none | Log warning + no-op |
| `closeOptionsMenu` | 1 | none | No-op |
| `dismiss` | 1 | none | Return safe default (null/false/0/empty) |
| `dispatchGenericMotionEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `dispatchKeyEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `dispatchKeyShortcutEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `dispatchPopulateAccessibilityEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `dispatchTouchEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `dispatchTrackballEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `hide` | 1 | none | throw UnsupportedOperationException |
| `invalidateOptionsMenu` | 1 | none | Store callback, never fire |
| `openOptionsMenu` | 1 | none | Return dummy instance / no-op |
| `takeKeyEvents` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 36 methods that have score >= 5
2. Stub 44 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.Dialog`:

- `android.content.Context` (already shimmed)
- `android.view.View` (already shimmed)

## Quality Gates

Before marking `android.app.Dialog` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 80 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 36 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
