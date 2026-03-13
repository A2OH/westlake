# SKILL: android.media.MediaRouter2.RoutingController

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaRouter2.RoutingController`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaRouter2.RoutingController` |
| **Package** | `android.media.MediaRouter2` |
| **Total Methods** | 12 |
| **Avg Score** | 5.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (33%) |
| **Partial/Composite** | 8 (66%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 10 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `release` | `void release()` | 10 | direct | trivial | `release` | `release(callback: AsyncCallback<void>): void` |
| `TransferCallback` | `MediaRouter2.TransferCallback()` | 7 | near | moderate | `callback` | `callback: AsyncCallback<boolean>): void` |
| `getVolume` | `int getVolume()` | 7 | near | moderate | `getAlbums` | `getAlbums(options: MediaFetchOptions, callback: AsyncCallback<Array<Album>>): void` |
| `setVolume` | `void setVolume(int)` | 6 | near | moderate | `OH_AVPlayer_SetVolume` | `OH_AVErrCode OH_AVPlayer_SetVolume(OH_AVPlayer *player, float leftVolume, float rightVolume)` |
| `isReleased` | `boolean isReleased()` | 6 | partial | moderate | `isTrash` | `isTrash(callback: AsyncCallback<boolean>): void` |
| `getVolumeMax` | `int getVolumeMax()` | 6 | partial | moderate | `getAlbums` | `getAlbums(options: MediaFetchOptions, callback: AsyncCallback<Array<Album>>): void` |
| `getVolumeHandling` | `int getVolumeHandling()` | 5 | partial | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |
| `selectRoute` | `void selectRoute(@NonNull android.media.MediaRoute2Info)` | 5 | partial | moderate | `OH_AVPlayer_SelectBitRate` | `OH_AVErrCode OH_AVPlayer_SelectBitRate(OH_AVPlayer *player, uint32_t bitRate)` |
| `onStop` | `void onStop(@NonNull android.media.MediaRouter2.RoutingController)` | 5 | partial | moderate | `on` | `on(type: 'deviceChange' | 'albumChange' | 'imageChange' | 'audioChange' | 'videoChange' | 'fileChange' | 'remoteFileChange', callback: Callback<void>): void` |
| `onTransfer` | `void onTransfer(@NonNull android.media.MediaRouter2.RoutingController, @NonNull android.media.MediaRouter2.RoutingController)` | 5 | partial | moderate | `on` | `on(type: 'deviceChange' | 'albumChange' | 'imageChange' | 'audioChange' | 'videoChange' | 'fileChange' | 'remoteFileChange', callback: Callback<void>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `deselectRoute` | 5 | partial | throw UnsupportedOperationException |
| `onTransferFailure` | 3 | composite | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 10 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaRouter2.RoutingController`:


## Quality Gates

Before marking `android.media.MediaRouter2.RoutingController` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 12 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 10 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
