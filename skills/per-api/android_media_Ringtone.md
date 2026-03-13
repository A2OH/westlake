# SKILL: android.media.Ringtone

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.Ringtone`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.Ringtone` |
| **Package** | `android.media` |
| **Total Methods** | 11 |
| **Avg Score** | 6.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 9 (81%) |
| **Partial/Composite** | 1 (9%) |
| **No Mapping** | 1 (9%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 10 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getTitle` | `String getTitle(android.content.Context)` | 8 | near | easy | `title` | `title: string` |
| `play` | `void play()` | 7 | near | easy | `play` | `play(): void` |
| `stop` | `void stop()` | 7 | near | easy | `stop` | `stop(callback: AsyncCallback<void>): void` |
| `getAudioAttributes` | `android.media.AudioAttributes getAudioAttributes()` | 7 | near | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |
| `getVolume` | `float getVolume()` | 7 | near | moderate | `getAlbums` | `getAlbums(options: MediaFetchOptions, callback: AsyncCallback<Array<Album>>): void` |
| `setLooping` | `void setLooping(boolean)` | 6 | near | moderate | `OH_AVPlayer_SetLooping` | `OH_AVErrCode OH_AVPlayer_SetLooping(OH_AVPlayer *player, bool loop)` |
| `isLooping` | `boolean isLooping()` | 6 | near | moderate | `OH_AVPlayer_IsLooping` | `bool OH_AVPlayer_IsLooping(OH_AVPlayer *player)` |
| `isPlaying` | `boolean isPlaying()` | 6 | near | moderate | `OH_AVPlayer_IsPlaying` | `bool OH_AVPlayer_IsPlaying(OH_AVPlayer *player)` |
| `setVolume` | `void setVolume(float)` | 6 | near | moderate | `OH_AVPlayer_SetVolume` | `OH_AVErrCode OH_AVPlayer_SetVolume(OH_AVPlayer *player, float leftVolume, float rightVolume)` |
| `setAudioAttributes` | `void setAudioAttributes(android.media.AudioAttributes) throws java.lang.IllegalArgumentException` | 6 | partial | moderate | `createAudioCapturer` | `createAudioCapturer(options: AudioCapturerOptions, callback: AsyncCallback<AudioCapturer>): void` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `finalize` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 10 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.Ringtone`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.Ringtone` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 10 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
