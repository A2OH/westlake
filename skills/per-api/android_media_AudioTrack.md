# SKILL: android.media.AudioTrack

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.AudioTrack`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.AudioTrack` |
| **Package** | `android.media` |
| **Total Methods** | 65 |
| **Avg Score** | 5.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 24 (36%) |
| **Partial/Composite** | 39 (60%) |
| **No Mapping** | 2 (3%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 47 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `release` | `void release()` | 10 | direct | trivial | `release` | `release(callback: AsyncCallback<void>): void` |
| `write` | `int write(@NonNull byte[], int, int)` | 8 | direct | easy | `write` | `write(data: number[]): Promise<void>` |
| `write` | `int write(@NonNull byte[], int, int, int)` | 8 | direct | easy | `write` | `write(data: number[]): Promise<void>` |
| `write` | `int write(@NonNull short[], int, int)` | 8 | direct | easy | `write` | `write(data: number[]): Promise<void>` |
| `write` | `int write(@NonNull short[], int, int, int)` | 8 | direct | easy | `write` | `write(data: number[]): Promise<void>` |
| `write` | `int write(@NonNull float[], int, int, int)` | 8 | direct | easy | `write` | `write(data: number[]): Promise<void>` |
| `write` | `int write(@NonNull java.nio.ByteBuffer, int, int)` | 8 | direct | easy | `write` | `write(data: number[]): Promise<void>` |
| `write` | `int write(@NonNull java.nio.ByteBuffer, int, int, long)` | 8 | direct | easy | `write` | `write(data: number[]): Promise<void>` |
| `getSampleRate` | `int getSampleRate()` | 7 | near | easy | `samplingRate` | `samplingRate: AudioSamplingRate` |
| `pause` | `void pause() throws java.lang.IllegalStateException` | 7 | near | easy | `pause` | `pause(): void` |
| `play` | `void play() throws java.lang.IllegalStateException` | 7 | near | easy | `play` | `play(): void` |
| `stop` | `void stop() throws java.lang.IllegalStateException` | 7 | near | easy | `stop` | `stop(callback: AsyncCallback<void>): void` |
| `getChannelCount` | `int getChannelCount()` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `getAudioSessionId` | `int getAudioSessionId()` | 7 | near | moderate | `sessionId` | `readonly sessionId: string` |
| `getAudioFormat` | `int getAudioFormat()` | 7 | near | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |
| `getMaxVolume` | `static float getMaxVolume()` | 7 | near | moderate | `getAlbums` | `getAlbums(options: MediaFetchOptions, callback: AsyncCallback<Array<Album>>): void` |
| `getUnderrunCount` | `int getUnderrunCount()` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `setPlaybackRate` | `int setPlaybackRate(int)` | 7 | near | moderate | `startAVPlayback` | `startAVPlayback(bundleName: string, assetId: string): Promise<void>` |
| `getPlayState` | `int getPlayState()` | 6 | near | moderate | `getLastObject` | `getLastObject(callback: AsyncCallback<FileAsset>): void` |
| `setPlaybackParams` | `void setPlaybackParams(@NonNull android.media.PlaybackParams)` | 6 | near | moderate | `startAVPlayback` | `startAVPlayback(bundleName: string, assetId: string): Promise<void>` |
| `AudioTrack` | `AudioTrack(android.media.AudioAttributes, android.media.AudioFormat, int, int, int) throws java.lang.IllegalArgumentException` | 6 | near | moderate | `audioAlbum` | `readonly audioAlbum: string` |
| `getPlaybackRate` | `int getPlaybackRate()` | 6 | near | moderate | `startAVPlayback` | `startAVPlayback(bundleName: string, assetId: string): Promise<void>` |
| `isDirectPlaybackSupported` | `static boolean isDirectPlaybackSupported(@NonNull android.media.AudioFormat, @NonNull android.media.AudioAttributes)` | 6 | near | moderate | `isMediaKeySystemSupported` | `isMediaKeySystemSupported(name: string, mimeType: string, level: ContentProtectionLevel): boolean` |
| `setVolume` | `int setVolume(float)` | 6 | near | moderate | `OH_AVPlayer_SetVolume` | `OH_AVErrCode OH_AVPlayer_SetVolume(OH_AVPlayer *player, float leftVolume, float rightVolume)` |
| `isOffloadedPlayback` | `boolean isOffloadedPlayback()` | 6 | partial | moderate | `startAVPlayback` | `startAVPlayback(bundleName: string, assetId: string): Promise<void>` |
| `unregisterStreamEventCallback` | `void unregisterStreamEventCallback(@NonNull android.media.AudioTrack.StreamEventCallback)` | 6 | partial | moderate | `OH_AudioRenderer_GetFrameSizeInCallback` | `OH_AudioStream_Result OH_AudioRenderer_GetFrameSizeInCallback(OH_AudioRenderer* renderer, int32_t* frameSize)` |
| `getTimestamp` | `boolean getTimestamp(android.media.AudioTimestamp)` | 6 | partial | moderate | `OH_AudioCapturer_GetTimestamp` | `OH_AudioStream_Result OH_AudioCapturer_GetTimestamp(OH_AudioCapturer* capturer,
    clockid_t clockId, int64_t* framePosition, int64_t* timestamp)` |
| `getNativeOutputSampleRate` | `static int getNativeOutputSampleRate(int)` | 6 | partial | moderate | `OH_MetadataOutput_Start` | `Camera_ErrorCode OH_MetadataOutput_Start(Camera_MetadataOutput* metadataOutput)` |
| `registerStreamEventCallback` | `void registerStreamEventCallback(@NonNull java.util.concurrent.Executor, @NonNull android.media.AudioTrack.StreamEventCallback)` | 6 | partial | moderate | `OH_AudioRenderer_GetFrameSizeInCallback` | `OH_AudioStream_Result OH_AudioRenderer_GetFrameSizeInCallback(OH_AudioRenderer* renderer, int32_t* frameSize)` |
| `getMetrics` | `android.os.PersistableBundle getMetrics()` | 6 | partial | moderate | `getAllPeers` | `getAllPeers(callback: AsyncCallback<Array<PeerInfo>>): void` |
| `getMinVolume` | `static float getMinVolume()` | 6 | partial | moderate | `getAlbums` | `getAlbums(options: MediaFetchOptions, callback: AsyncCallback<Array<Album>>): void` |
| `getState` | `int getState()` | 6 | partial | moderate | `getLastObject` | `getLastObject(callback: AsyncCallback<FileAsset>): void` |
| `getAudioDescriptionMixLeveldB` | `float getAudioDescriptionMixLeveldB()` | 6 | partial | moderate | `getAllSessionDescriptors` | `getAllSessionDescriptors(callback: AsyncCallback<Array<Readonly<AVSessionDescriptor>>>): void` |
| `getPositionNotificationPeriod` | `int getPositionNotificationPeriod()` | 6 | partial | moderate | `getPositionObject` | `getPositionObject(index: number, callback: AsyncCallback<FileAsset>): void` |
| `getPlaybackHeadPosition` | `int getPlaybackHeadPosition()` | 6 | partial | moderate | `getPositionObject` | `getPositionObject(index: number, callback: AsyncCallback<FileAsset>): void` |
| `getStreamType` | `int getStreamType()` | 5 | partial | moderate | `eventType` | `eventType: InterruptType` |
| `setPresentation` | `int setPresentation(@NonNull android.media.AudioPresentation)` | 5 | partial | moderate | `OH_AVMuxer_SetRotation` | `OH_AVErrCode OH_AVMuxer_SetRotation(OH_AVMuxer *muxer, int32_t rotation)` |
| `setBufferSizeInFrames` | `int setBufferSizeInFrames(@IntRange(from=0) int)` | 5 | partial | moderate | `OH_AVBuffer_SetParameter` | `OH_AVErrCode OH_AVBuffer_SetParameter(OH_AVBuffer *buffer, const OH_AVFormat *format)` |
| `getPerformanceMode` | `int getPerformanceMode()` | 5 | partial | moderate | `getCameraManager` | `getCameraManager(context: Context): CameraManager` |
| `setPlaybackHeadPosition` | `int setPlaybackHeadPosition(@IntRange(from=0) int)` | 5 | partial | moderate | `startAVPlayback` | `startAVPlayback(bundleName: string, assetId: string): Promise<void>` |
| `getRoutedDevice` | `android.media.AudioDeviceInfo getRoutedDevice()` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `setPositionNotificationPeriod` | `int setPositionNotificationPeriod(int)` | 5 | partial | moderate | `getPositionObject` | `getPositionObject(index: number, callback: AsyncCallback<FileAsset>): void` |
| `getDualMonoMode` | `int getDualMonoMode()` | 5 | partial | moderate | `getAllObject` | `getAllObject(callback: AsyncCallback<Array<FileAsset>>): void` |
| `setOffloadEndOfStream` | `void setOffloadEndOfStream()` | 5 | partial | moderate | `OH_VideoEncoder_NotifyEndOfStream` | `OH_AVErrCode OH_VideoEncoder_NotifyEndOfStream(OH_AVCodec *codec)` |
| `setLoopPoints` | `int setLoopPoints(@IntRange(from=0) int, @IntRange(from=0) int, @IntRange(from=0xffffffff) int)` | 5 | partial | moderate | `OH_AVPlayer_SetLooping` | `OH_AVErrCode OH_AVPlayer_SetLooping(OH_AVPlayer *player, bool loop)` |
| `getChannelConfiguration` | `int getChannelConfiguration()` | 5 | partial | moderate | `OH_AudioCapturer_GetChannelCount` | `OH_AudioStream_Result OH_AudioCapturer_GetChannelCount(OH_AudioCapturer* capturer, int32_t* channelCount)` |
| `getNotificationMarkerPosition` | `int getNotificationMarkerPosition()` | 5 | partial | moderate | `getPositionObject` | `getPositionObject(index: number, callback: AsyncCallback<FileAsset>): void` |

## Stub APIs (score < 5): 18 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `addOnCodecFormatChangedListener` | 5 | partial | Return safe default (null/false/0/empty) |
| `getMinBufferSize` | 5 | partial | Return safe default (null/false/0/empty) |
| `getPreferredDevice` | 5 | partial | Return safe default (null/false/0/empty) |
| `setAudioDescriptionMixLeveldB` | 5 | partial | Log warning + no-op |
| `removeOnCodecFormatChangedListener` | 5 | partial | Return safe default (null/false/0/empty) |
| `setDualMonoMode` | 5 | partial | Log warning + no-op |
| `setAuxEffectSendLevel` | 4 | partial | Log warning + no-op |
| `setPlaybackPositionUpdateListener` | 4 | partial | Return safe default (null/false/0/empty) |
| `setPlaybackPositionUpdateListener` | 4 | partial | Return safe default (null/false/0/empty) |
| `reloadStaticData` | 4 | partial | throw UnsupportedOperationException |
| `setPreferredDevice` | 4 | partial | Log warning + no-op |
| `setNotificationMarkerPosition` | 4 | partial | Log warning + no-op |
| `flush` | 4 | partial | throw UnsupportedOperationException |
| `setOffloadDelayPadding` | 4 | partial | Log warning + no-op |
| `removeOnRoutingChangedListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `addOnRoutingChangedListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `attachAuxEffect` | 1 | none | throw UnsupportedOperationException |
| `finalize` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 47 methods that have score >= 5
2. Stub 18 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.AudioTrack`:

- `android.media.AudioManager` (not yet shimmed)

## Quality Gates

Before marking `android.media.AudioTrack` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 65 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 47 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
