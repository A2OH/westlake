# SKILL: android.media.MediaRouter

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaRouter`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaRouter` |
| **Package** | `android.media` |
| **Total Methods** | 26 |
| **Avg Score** | 5.4 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 11 (42%) |
| **Partial/Composite** | 15 (57%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 15 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Callback` | `MediaRouter.Callback()` | 10 | direct | trivial | `callback` | `callback: AsyncCallback<boolean>): void` |
| `addCallback` | `void addCallback(int, android.media.MediaRouter.Callback)` | 8 | direct | easy | `callback` | `callback: AsyncCallback<boolean>): void` |
| `addCallback` | `void addCallback(int, android.media.MediaRouter.Callback, int)` | 8 | direct | easy | `callback` | `callback: AsyncCallback<boolean>): void` |
| `getRouteCount` | `int getRouteCount()` | 8 | near | easy | `getCount` | `getCount(): number` |
| `removeCallback` | `void removeCallback(android.media.MediaRouter.Callback)` | 7 | near | easy | `callback` | `callback: AsyncCallback<boolean>): void` |
| `createUserRoute` | `android.media.MediaRouter.UserRouteInfo createUserRoute(android.media.MediaRouter.RouteCategory)` | 7 | near | moderate | `createAsset` | `createAsset(mediaType: MediaType, displayName: string, relativePath: string, callback: AsyncCallback<FileAsset>): void` |
| `getCategoryCount` | `int getCategoryCount()` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `getRouteAt` | `android.media.MediaRouter.RouteInfo getRouteAt(int)` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `onRouteAdded` | `abstract void onRouteAdded(android.media.MediaRouter, android.media.MediaRouter.RouteInfo)` | 7 | near | moderate | `dateAdded` | `readonly dateAdded: number` |
| `createRouteCategory` | `android.media.MediaRouter.RouteCategory createRouteCategory(CharSequence, boolean)` | 6 | near | moderate | `createAudioCapturer` | `createAudioCapturer(options: AudioCapturerOptions, callback: AsyncCallback<AudioCapturer>): void` |
| `createRouteCategory` | `android.media.MediaRouter.RouteCategory createRouteCategory(int, boolean)` | 6 | near | moderate | `createAudioCapturer` | `createAudioCapturer(options: AudioCapturerOptions, callback: AsyncCallback<AudioCapturer>): void` |
| `getSelectedRoute` | `android.media.MediaRouter.RouteInfo getSelectedRoute(int)` | 6 | partial | moderate | `getCount` | `getCount(): number` |
| `getCategoryAt` | `android.media.MediaRouter.RouteCategory getCategoryAt(int)` | 6 | partial | moderate | `getCameraManager` | `getCameraManager(context: Context): CameraManager` |
| `getDefaultRoute` | `android.media.MediaRouter.RouteInfo getDefaultRoute()` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `selectRoute` | `void selectRoute(int, @NonNull android.media.MediaRouter.RouteInfo)` | 5 | partial | moderate | `OH_AVPlayer_SelectBitRate` | `OH_AVErrCode OH_AVPlayer_SelectBitRate(OH_AVPlayer *player, uint32_t bitRate)` |

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onRouteVolumeChanged` | 4 | partial | Store callback, never fire |
| `onRoutePresentationDisplayChanged` | 4 | partial | Return safe default (null/false/0/empty) |
| `removeUserRoute` | 4 | partial | Log warning + no-op |
| `onRouteChanged` | 3 | composite | Store callback, never fire |
| `onRouteGrouped` | 3 | composite | Store callback, never fire |
| `onRouteRemoved` | 3 | composite | Log warning + no-op |
| `onRouteSelected` | 3 | composite | Store callback, never fire |
| `onRouteUngrouped` | 3 | composite | Store callback, never fire |
| `onRouteUnselected` | 3 | composite | Store callback, never fire |
| `addUserRoute` | 3 | composite | Log warning + no-op |
| `clearUserRoutes` | 2 | composite | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaRouter`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaRouter` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 26 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 15 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
