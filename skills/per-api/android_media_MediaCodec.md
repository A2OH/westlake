# SKILL: android.media.MediaCodec

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaCodec`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaCodec` |
| **Package** | `android.media` |
| **Total Methods** | 24 |
| **Avg Score** | 6.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 11 (45%) |
| **Partial/Composite** | 12 (50%) |
| **No Mapping** | 1 (4%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 21 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `release` | `void release()` | 10 | direct | trivial | `release` | `release(callback: AsyncCallback<void>): void` |
| `setCallback` | `void setCallback(@Nullable android.media.MediaCodec.Callback, @Nullable android.os.Handler)` | 8 | direct | easy | `callback` | `callback: AsyncCallback<boolean>): void` |
| `setCallback` | `void setCallback(@Nullable android.media.MediaCodec.Callback)` | 8 | direct | easy | `callback` | `callback: AsyncCallback<boolean>): void` |
| `reset` | `void reset()` | 8 | direct | easy | `reset` | `reset(wallpaperType: WallpaperType, callback: AsyncCallback<void>): void` |
| `start` | `void start()` | 7 | near | easy | `start` | `start(sinkDeviceDescriptor: string, srcInputDeviceId: number, callback: AsyncCallback<void>): void` |
| `stop` | `void stop()` | 7 | near | easy | `stop` | `stop(callback: AsyncCallback<void>): void` |
| `dequeueOutputBuffer` | `int dequeueOutputBuffer(@NonNull android.media.MediaCodec.BufferInfo, long)` | 7 | near | moderate | `OH_AudioCodec_FreeOutputBuffer` | `OH_AVErrCode OH_AudioCodec_FreeOutputBuffer(OH_AVCodec *codec, uint32_t index)` |
| `setParameters` | `void setParameters(@Nullable android.os.Bundle)` | 6 | near | moderate | `OH_AVBuffer_SetParameter` | `OH_AVErrCode OH_AVBuffer_SetParameter(OH_AVBuffer *buffer, const OH_AVFormat *format)` |
| `queueInputBuffer` | `void queueInputBuffer(int, int, int, long, int) throws android.media.MediaCodec.CryptoException` | 6 | near | moderate | `OH_AudioCodec_PushInputBuffer` | `OH_AVErrCode OH_AudioCodec_PushInputBuffer(OH_AVCodec *codec, uint32_t index)` |
| `releaseOutputBuffer` | `void releaseOutputBuffer(int, boolean)` | 6 | near | moderate | `OH_AudioCodec_FreeOutputBuffer` | `OH_AVErrCode OH_AudioCodec_FreeOutputBuffer(OH_AVCodec *codec, uint32_t index)` |
| `releaseOutputBuffer` | `void releaseOutputBuffer(int, long)` | 6 | near | moderate | `OH_AudioCodec_FreeOutputBuffer` | `OH_AVErrCode OH_AudioCodec_FreeOutputBuffer(OH_AVCodec *codec, uint32_t index)` |
| `dequeueInputBuffer` | `int dequeueInputBuffer(long)` | 6 | partial | moderate | `OH_AudioCodec_PushInputBuffer` | `OH_AVErrCode OH_AudioCodec_PushInputBuffer(OH_AVCodec *codec, uint32_t index)` |
| `queueSecureInputBuffer` | `void queueSecureInputBuffer(int, int, @NonNull android.media.MediaCodec.CryptoInfo, long, int) throws android.media.MediaCodec.CryptoException` | 6 | partial | moderate | `OH_AudioCodec_PushInputBuffer` | `OH_AVErrCode OH_AudioCodec_PushInputBuffer(OH_AVCodec *codec, uint32_t index)` |
| `setVideoScalingMode` | `void setVideoScalingMode(int)` | 6 | partial | moderate | `createVideoRecorder` | `createVideoRecorder(callback: AsyncCallback<VideoRecorder>): void` |
| `getMetrics` | `android.os.PersistableBundle getMetrics()` | 6 | partial | moderate | `getAllPeers` | `getAllPeers(callback: AsyncCallback<Array<PeerInfo>>): void` |
| `setAudioPresentation` | `void setAudioPresentation(@NonNull android.media.AudioPresentation)` | 6 | partial | moderate | `OH_AudioCapturer_Start` | `OH_AudioStream_Result OH_AudioCapturer_Start(OH_AudioCapturer* capturer)` |
| `configure` | `void configure(@Nullable android.media.MediaFormat, @Nullable android.view.Surface, @Nullable android.media.MediaCrypto, int)` | 6 | partial | moderate | `OH_AudioCodec_Configure` | `OH_AVErrCode OH_AudioCodec_Configure(OH_AVCodec *codec, const OH_AVFormat *format)` |
| `configure` | `void configure(@Nullable android.media.MediaFormat, @Nullable android.view.Surface, int, @Nullable android.media.MediaDescrambler)` | 6 | partial | moderate | `OH_AudioCodec_Configure` | `OH_AVErrCode OH_AudioCodec_Configure(OH_AVCodec *codec, const OH_AVFormat *format)` |
| `setInputSurface` | `void setInputSurface(@NonNull android.view.Surface)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `setOutputSurface` | `void setOutputSurface(@NonNull android.view.Surface)` | 5 | partial | moderate | `OH_PhotoOutput_Capture` | `Camera_ErrorCode OH_PhotoOutput_Capture(Camera_PhotoOutput* photoOutput)` |
| `signalEndOfInputStream` | `void signalEndOfInputStream()` | 5 | partial | moderate | `OH_VideoEncoder_NotifyEndOfStream` | `OH_AVErrCode OH_VideoEncoder_NotifyEndOfStream(OH_AVCodec *codec)` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `flush` | 4 | partial | throw UnsupportedOperationException |
| `setOnFrameRenderedListener` | 4 | composite | Return safe default (null/false/0/empty) |
| `finalize` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 21 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaCodec`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaCodec` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 24 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 21 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
