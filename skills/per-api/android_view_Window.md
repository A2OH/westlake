# SKILL: android.view.Window

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.Window`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.Window` |
| **Package** | `android.view` |
| **Total Methods** | 129 |
| **Avg Score** | 2.7 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 111 (86%) |
| **No Mapping** | 18 (13%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 128 |
| **Has Async Gap** | 128 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 129 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `addContentView` | 3 | composite | Log warning + no-op |
| `addFlags` | 3 | composite | Log warning + no-op |
| `addOnFrameMetricsAvailableListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `closeAllPanels` | 3 | composite | No-op |
| `closePanel` | 3 | composite | No-op |
| `findViewById` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAllowEnterTransitionOverlap` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAllowReturnTransitionOverlap` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAttributes` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCallback` | 3 | composite | Return safe default (null/false/0/empty) |
| `getColorMode` | 3 | composite | Return safe default (null/false/0/empty) |
| `getContainer` | 3 | composite | Return safe default (null/false/0/empty) |
| `getContentScene` | 3 | composite | Return safe default (null/false/0/empty) |
| `getContext` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDefaultFeatures` | 3 | composite | Return safe default (null/false/0/empty) |
| `getEnterTransition` | 3 | composite | Return safe default (null/false/0/empty) |
| `getExitTransition` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFeatures` | 3 | composite | Return safe default (null/false/0/empty) |
| `getForcedWindowFlags` | 3 | composite | Return safe default (null/false/0/empty) |
| `getLocalFeatures` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMediaController` | 3 | composite | Return safe default (null/false/0/empty) |
| `getReenterTransition` | 3 | composite | Return safe default (null/false/0/empty) |
| `getReturnTransition` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSharedElementEnterTransition` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSharedElementExitTransition` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSharedElementReenterTransition` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSharedElementReturnTransition` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSharedElementsUseOverlay` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTransitionBackgroundFadeDuration` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTransitionManager` | 3 | composite | Return safe default (null/false/0/empty) |
| `getVolumeControlStream` | 3 | composite | Return safe default (null/false/0/empty) |
| `getWindowManager` | 3 | composite | Return safe default (null/false/0/empty) |
| `getWindowStyle` | 3 | composite | Return safe default (null/false/0/empty) |
| `injectInputEvent` | 3 | composite | Log warning + no-op |
| `isActive` | 3 | composite | Return safe default (null/false/0/empty) |
| `isShortcutKey` | 3 | composite | Return safe default (null/false/0/empty) |
| `onActive` | 3 | composite | Store callback, never fire |
| `onConfigurationChanged` | 3 | composite | Store callback, never fire |
| `openPanel` | 3 | composite | Return dummy instance / no-op |
| `performContextMenuIdentifierAction` | 3 | composite | Store callback, never fire |
| `performPanelIdentifierAction` | 3 | composite | Store callback, never fire |
| `removeOnFrameMetricsAvailableListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setAllowEnterTransitionOverlap` | 3 | composite | Log warning + no-op |
| `setAllowReturnTransitionOverlap` | 3 | composite | Log warning + no-op |
| `setBackgroundDrawable` | 3 | composite | Log warning + no-op |
| `setBackgroundDrawableResource` | 3 | composite | Log warning + no-op |
| `setCallback` | 3 | composite | Log warning + no-op |
| `setChildDrawable` | 3 | composite | Log warning + no-op |
| `setChildInt` | 3 | composite | Log warning + no-op |
| `setClipToOutline` | 3 | composite | Log warning + no-op |
| `setContainer` | 3 | composite | Log warning + no-op |
| `setDecorCaptionShade` | 3 | composite | Log warning + no-op |
| `setDecorFitsSystemWindows` | 3 | composite | Log warning + no-op |
| `setDefaultWindowFormat` | 3 | composite | Log warning + no-op |
| `setDimAmount` | 3 | composite | Log warning + no-op |
| `setElevation` | 3 | composite | Log warning + no-op |
| `setEnterTransition` | 3 | composite | Log warning + no-op |
| `setExitTransition` | 3 | composite | Log warning + no-op |
| `setFeatureDrawable` | 3 | composite | Log warning + no-op |
| `setFeatureDrawableAlpha` | 3 | composite | Log warning + no-op |
| `setFeatureDrawableResource` | 3 | composite | Log warning + no-op |
| `setFeatureDrawableUri` | 3 | composite | Log warning + no-op |
| `setFeatureInt` | 3 | composite | Log warning + no-op |
| `setFormat` | 3 | composite | Log warning + no-op |
| `setGravity` | 3 | composite | Log warning + no-op |
| `setIcon` | 3 | composite | Log warning + no-op |
| `setLayout` | 3 | composite | Log warning + no-op |
| `setMediaController` | 3 | composite | Log warning + no-op |
| `setNavigationBarColor` | 3 | composite | Log warning + no-op |
| `setNavigationBarDividerColor` | 3 | composite | Log warning + no-op |
| `setPreferMinimalPostProcessing` | 3 | composite | Log warning + no-op |
| `setReenterTransition` | 3 | composite | Log warning + no-op |
| `setResizingCaptionDrawable` | 3 | composite | Log warning + no-op |
| `setRestrictedCaptionAreaListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setReturnTransition` | 3 | composite | Log warning + no-op |
| `setSharedElementEnterTransition` | 3 | composite | Log warning + no-op |
| `setSharedElementExitTransition` | 3 | composite | Log warning + no-op |
| `setSharedElementReenterTransition` | 3 | composite | Log warning + no-op |
| `setSharedElementReturnTransition` | 3 | composite | Log warning + no-op |
| `setSharedElementsUseOverlay` | 3 | composite | Log warning + no-op |
| `setSoftInputMode` | 3 | composite | Log warning + no-op |
| `setStatusBarColor` | 3 | composite | Log warning + no-op |
| `setStatusBarContrastEnforced` | 3 | composite | Log warning + no-op |
| `setSustainedPerformanceMode` | 3 | composite | Log warning + no-op |
| `setTitle` | 3 | composite | Log warning + no-op |
| `setTransitionBackgroundFadeDuration` | 3 | composite | Log warning + no-op |
| `setTransitionManager` | 3 | composite | Log warning + no-op |
| `setType` | 3 | composite | Log warning + no-op |
| `setUiOptions` | 3 | composite | Log warning + no-op |
| `setUiOptions` | 3 | composite | Log warning + no-op |
| `setVolumeControlStream` | 3 | composite | Log warning + no-op |
| `setWindowAnimations` | 3 | composite | Log warning + no-op |
| `setWindowManager` | 3 | composite | Log warning + no-op |
| `setWindowManager` | 3 | composite | Log warning + no-op |
| `superDispatchGenericMotionEvent` | 3 | composite | Return safe default (null/false/0/empty) |
| `superDispatchKeyEvent` | 3 | composite | Return safe default (null/false/0/empty) |
| `superDispatchKeyShortcutEvent` | 3 | composite | Return safe default (null/false/0/empty) |
| `superDispatchTouchEvent` | 3 | composite | Return safe default (null/false/0/empty) |
| `superDispatchTrackballEvent` | 3 | composite | Return safe default (null/false/0/empty) |
| `takeInputQueue` | 3 | composite | Log warning + no-op |
| `takeKeyEvents` | 3 | composite | throw UnsupportedOperationException |
| `setNavigationBarContrastEnforced` | 3 | composite | Log warning + no-op |
| `setColorMode` | 3 | composite | Log warning + no-op |
| `setAttributes` | 3 | composite | Log warning + no-op |
| `setLocalFocus` | 3 | composite | Log warning + no-op |
| `setContentView` | 3 | composite | Log warning + no-op |
| `setContentView` | 3 | composite | Log warning + no-op |
| `setContentView` | 3 | composite | Log warning + no-op |
| `setSystemGestureExclusionRects` | 3 | composite | Log warning + no-op |
| `setFlags` | 3 | composite | Log warning + no-op |
| `setLogo` | 2 | composite | Log warning + no-op |
| `Window` | 1 | none | throw UnsupportedOperationException |
| `clearFlags` | 1 | none | throw UnsupportedOperationException |
| `hasChildren` | 1 | none | Return safe default (null/false/0/empty) |
| `hasFeature` | 1 | none | Return safe default (null/false/0/empty) |
| `hasSoftInputMode` | 1 | none | Return safe default (null/false/0/empty) |
| `invalidatePanelMenu` | 1 | none | throw UnsupportedOperationException |
| `isFloating` | 1 | none | Return safe default (null/false/0/empty) |
| `isNavigationBarContrastEnforced` | 1 | none | Return safe default (null/false/0/empty) |
| `isStatusBarContrastEnforced` | 1 | none | Return safe default (null/false/0/empty) |
| `isWideColorGamut` | 1 | none | Return safe default (null/false/0/empty) |
| `makeActive` | 1 | none | throw UnsupportedOperationException |
| `peekDecorView` | 1 | none | throw UnsupportedOperationException |
| `performPanelShortcut` | 1 | none | throw UnsupportedOperationException |
| `requestFeature` | 1 | none | throw UnsupportedOperationException |
| `restoreHierarchyState` | 1 | none | throw UnsupportedOperationException |
| `saveHierarchyState` | 1 | none | throw UnsupportedOperationException |
| `takeSurface` | 1 | none | throw UnsupportedOperationException |
| `togglePanel` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.Window`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.view.Window` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 129 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
