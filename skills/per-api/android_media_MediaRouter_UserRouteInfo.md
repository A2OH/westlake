# SKILL: android.media.MediaRouter.UserRouteInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaRouter.UserRouteInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaRouter.UserRouteInfo` |
| **Package** | `android.media.MediaRouter` |
| **Total Methods** | 17 |
| **Avg Score** | 5.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 8 (47%) |
| **Partial/Composite** | 9 (52%) |
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
| `setIconDrawable` | `void setIconDrawable(android.graphics.drawable.Drawable)` | 7 | near | easy | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `VolumeCallback` | `MediaRouter.VolumeCallback()` | 7 | near | easy | `callback` | `callback: AsyncCallback<boolean>): void` |
| `setPlaybackType` | `void setPlaybackType(int)` | 7 | near | moderate | `startAVPlayback` | `startAVPlayback(bundleName: string, assetId: string): Promise<void>` |
| `setVolumeCallback` | `void setVolumeCallback(android.media.MediaRouter.VolumeCallback)` | 6 | near | moderate | `callback` | `callback: AsyncCallback<boolean>): void` |
| `getRemoteControlClient` | `android.media.RemoteControlClient getRemoteControlClient()` | 6 | near | moderate | `getAVCastController` | `getAVCastController(sessionId: string, callback: AsyncCallback<AVCastController>): void` |
| `setPlaybackStream` | `void setPlaybackStream(int)` | 6 | near | moderate | `startAVPlayback` | `startAVPlayback(bundleName: string, assetId: string): Promise<void>` |
| `setRemoteControlClient` | `void setRemoteControlClient(android.media.RemoteControlClient)` | 6 | near | moderate | `sendSystemControlCommand` | `sendSystemControlCommand(command: AVControlCommand, callback: AsyncCallback<void>): void` |
| `setVolume` | `void setVolume(int)` | 6 | near | moderate | `OH_AVPlayer_SetVolume` | `OH_AVErrCode OH_AVPlayer_SetVolume(OH_AVPlayer *player, float leftVolume, float rightVolume)` |
| `setName` | `void setName(CharSequence)` | 6 | partial | moderate | `deviceName` | `readonly deviceName: string` |
| `setName` | `void setName(int)` | 6 | partial | moderate | `deviceName` | `readonly deviceName: string` |
| `setVolumeMax` | `void setVolumeMax(int)` | 5 | partial | moderate | `OH_AVPlayer_SetVolume` | `OH_AVErrCode OH_AVPlayer_SetVolume(OH_AVPlayer *player, float leftVolume, float rightVolume)` |
| `setIconResource` | `void setIconResource(@DrawableRes int)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `setDescription` | `void setDescription(CharSequence)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `setStatus` | `void setStatus(CharSequence)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `onVolumeSetRequest` | `abstract void onVolumeSetRequest(android.media.MediaRouter.RouteInfo, int)` | 5 | partial | moderate | `OH_AVPlayer_SetVolume` | `OH_AVErrCode OH_AVPlayer_SetVolume(OH_AVPlayer *player, float leftVolume, float rightVolume)` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onVolumeUpdateRequest` | 5 | partial | Log warning + no-op |
| `setVolumeHandling` | 5 | partial | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 15 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaRouter.UserRouteInfo`:


## Quality Gates

Before marking `android.media.MediaRouter.UserRouteInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 17 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 15 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
