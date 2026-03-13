# SKILL: android.media.VolumeProvider

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.VolumeProvider`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.VolumeProvider` |
| **Package** | `android.media` |
| **Total Methods** | 8 |
| **Avg Score** | 5.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (25%) |
| **Partial/Composite** | 6 (75%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getMaxVolume` | `final int getMaxVolume()` | 7 | near | moderate | `getAlbums` | `getAlbums(options: MediaFetchOptions, callback: AsyncCallback<Array<Album>>): void` |
| `getVolumeControl` | `final int getVolumeControl()` | 6 | near | moderate | `getAVCastController` | `getAVCastController(sessionId: string, callback: AsyncCallback<AVCastController>): void` |
| `onSetVolumeTo` | `void onSetVolumeTo(int)` | 6 | partial | moderate | `OH_AVPlayer_SetVolume` | `OH_AVErrCode OH_AVPlayer_SetVolume(OH_AVPlayer *player, float leftVolume, float rightVolume)` |
| `getCurrentVolume` | `final int getCurrentVolume()` | 6 | partial | moderate | `getCount` | `getCount(): number` |
| `onAdjustVolume` | `void onAdjustVolume(int)` | 6 | partial | moderate | `OH_AVPlayer_SetVolume` | `OH_AVErrCode OH_AVPlayer_SetVolume(OH_AVPlayer *player, float leftVolume, float rightVolume)` |
| `setCurrentVolume` | `final void setCurrentVolume(int)` | 5 | partial | moderate | `OH_AVPlayer_GetCurrentTime` | `OH_AVErrCode OH_AVPlayer_GetCurrentTime(OH_AVPlayer *player, int32_t *currentTime)` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `VolumeProvider` | 3 | composite | throw UnsupportedOperationException |
| `VolumeProvider` | 3 | composite | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 6 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.VolumeProvider`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.VolumeProvider` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
