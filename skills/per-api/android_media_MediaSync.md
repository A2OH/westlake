# SKILL: android.media.MediaSync

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaSync`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaSync` |
| **Package** | `android.media` |
| **Total Methods** | 13 |
| **Avg Score** | 6.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 6 (46%) |
| **Partial/Composite** | 6 (46%) |
| **No Mapping** | 1 (7%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 10 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Callback` | `MediaSync.Callback()` | 10 | direct | trivial | `callback` | `callback: AsyncCallback<boolean>): void` |
| `release` | `void release()` | 10 | direct | trivial | `release` | `release(callback: AsyncCallback<void>): void` |
| `setCallback` | `void setCallback(@Nullable android.media.MediaSync.Callback, @Nullable android.os.Handler)` | 8 | direct | easy | `callback` | `callback: AsyncCallback<boolean>): void` |
| `MediaSync` | `MediaSync()` | 7 | near | moderate | `mediaType` | `readonly mediaType: MediaType` |
| `setAudioTrack` | `void setAudioTrack(@Nullable android.media.AudioTrack)` | 6 | near | moderate | `castAudio` | `castAudio(session: SessionToken | 'all', audioDevices: Array<audio.AudioDeviceDescriptor>, callback: AsyncCallback<void>): void` |
| `setPlaybackParams` | `void setPlaybackParams(@NonNull android.media.PlaybackParams)` | 6 | near | moderate | `startAVPlayback` | `startAVPlayback(bundleName: string, assetId: string): Promise<void>` |
| `onAudioBufferConsumed` | `abstract void onAudioBufferConsumed(@NonNull android.media.MediaSync, @NonNull java.nio.ByteBuffer, int)` | 6 | partial | moderate | `OH_AudioDecoder_Configure` | `OH_AVErrCode OH_AudioDecoder_Configure(OH_AVCodec *codec, OH_AVFormat *format)` |
| `setSurface` | `void setSurface(@Nullable android.view.Surface)` | 6 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `queueAudio` | `void queueAudio(@NonNull java.nio.ByteBuffer, int, long)` | 5 | partial | moderate | `castAudio` | `castAudio(session: SessionToken | 'all', audioDevices: Array<audio.AudioDeviceDescriptor>, callback: AsyncCallback<void>): void` |
| `setSyncParams` | `void setSyncParams(@NonNull android.media.SyncParams)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setOnErrorListener` | 5 | partial | Return safe default (null/false/0/empty) |
| `flush` | 4 | partial | throw UnsupportedOperationException |
| `finalize` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 10 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaSync`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaSync` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 13 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 10 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
