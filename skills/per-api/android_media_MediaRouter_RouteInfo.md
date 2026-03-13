# SKILL: android.media.MediaRouter.RouteInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaRouter.RouteInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaRouter.RouteInfo` |
| **Package** | `android.media.MediaRouter` |
| **Total Methods** | 21 |
| **Avg Score** | 5.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 9 (42%) |
| **Partial/Composite** | 12 (57%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 19 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getDeviceType` | `int getDeviceType()` | 9 | direct | easy | `deviceType` | `readonly deviceType: DeviceType` |
| `getVolume` | `int getVolume()` | 7 | near | moderate | `getAlbums` | `getAlbums(options: MediaFetchOptions, callback: AsyncCallback<Array<Album>>): void` |
| `requestSetVolume` | `void requestSetVolume(int)` | 7 | near | moderate | `OH_AVPlayer_SetVolume` | `OH_AVErrCode OH_AVPlayer_SetVolume(OH_AVPlayer *player, float leftVolume, float rightVolume)` |
| `getDescription` | `CharSequence getDescription()` | 6 | near | moderate | `getAllSessionDescriptors` | `getAllSessionDescriptors(callback: AsyncCallback<Array<Readonly<AVSessionDescriptor>>>): void` |
| `getGroup` | `android.media.MediaRouter.RouteGroup getGroup()` | 6 | near | moderate | `getCount` | `getCount(): number` |
| `getName` | `CharSequence getName()` | 6 | near | moderate | `getAlbums` | `getAlbums(options: MediaFetchOptions, callback: AsyncCallback<Array<Album>>): void` |
| `getName` | `CharSequence getName(android.content.Context)` | 6 | near | moderate | `getAlbums` | `getAlbums(options: MediaFetchOptions, callback: AsyncCallback<Array<Album>>): void` |
| `getPlaybackType` | `int getPlaybackType()` | 6 | near | moderate | `OH_AVPlayer_GetPlaybackSpeed` | `OH_AVErrCode OH_AVPlayer_GetPlaybackSpeed(OH_AVPlayer *player, AVPlaybackSpeed *speed)` |
| `isConnecting` | `boolean isConnecting()` | 6 | near | moderate | `isOnline` | `readonly isOnline: boolean` |
| `getCategory` | `android.media.MediaRouter.RouteCategory getCategory()` | 6 | partial | moderate | `getCameraManager` | `getCameraManager(context: Context): CameraManager` |
| `getPlaybackStream` | `int getPlaybackStream()` | 6 | partial | moderate | `OH_AVPlayer_GetPlaybackSpeed` | `OH_AVErrCode OH_AVPlayer_GetPlaybackSpeed(OH_AVPlayer *player, AVPlaybackSpeed *speed)` |
| `getTag` | `Object getTag()` | 6 | partial | moderate | `getCount` | `getCount(): number` |
| `getVolumeMax` | `int getVolumeMax()` | 6 | partial | moderate | `getAlbums` | `getAlbums(options: MediaFetchOptions, callback: AsyncCallback<Array<Album>>): void` |
| `getSupportedTypes` | `int getSupportedTypes()` | 6 | partial | moderate | `OH_AVCapability_GetSupportedProfiles` | `OH_AVErrCode OH_AVCapability_GetSupportedProfiles(OH_AVCapability *capability, const int32_t **profiles,
                                                  uint32_t *profileNum)` |
| `getStatus` | `CharSequence getStatus()` | 5 | partial | moderate | `getLastObject` | `getLastObject(callback: AsyncCallback<FileAsset>): void` |
| `getIconDrawable` | `android.graphics.drawable.Drawable getIconDrawable()` | 5 | partial | moderate | `getAVCastController` | `getAVCastController(sessionId: string, callback: AsyncCallback<AVCastController>): void` |
| `getVolumeHandling` | `int getVolumeHandling()` | 5 | partial | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |
| `isEnabled` | `boolean isEnabled()` | 5 | partial | moderate | `isDirectory` | `isDirectory(callback: AsyncCallback<boolean>): void` |
| `setTag` | `void setTag(Object)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getPresentationDisplay` | 5 | partial | Return safe default (null/false/0/empty) |
| `requestUpdateVolume` | 4 | partial | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 19 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaRouter.RouteInfo`:


## Quality Gates

Before marking `android.media.MediaRouter.RouteInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 21 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 19 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
