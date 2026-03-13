# SKILL: android.media.MediaRecorder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaRecorder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaRecorder` |
| **Package** | `android.media` |
| **Total Methods** | 52 |
| **Avg Score** | 5.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 20 (38%) |
| **Partial/Composite** | 31 (59%) |
| **No Mapping** | 1 (1%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 42 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `release` | `void release()` | 10 | direct | trivial | `release` | `release(callback: AsyncCallback<void>): void` |
| `reset` | `void reset()` | 8 | direct | easy | `reset` | `reset(wallpaperType: WallpaperType, callback: AsyncCallback<void>): void` |
| `resume` | `void resume() throws java.lang.IllegalStateException` | 8 | direct | easy | `resume` | `resume(): void` |
| `setAudioEncoder` | `void setAudioEncoder(int) throws java.lang.IllegalStateException` | 8 | near | easy | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `setVideoEncoder` | `void setVideoEncoder(int) throws java.lang.IllegalStateException` | 8 | near | easy | `createVideoRecorder` | `createVideoRecorder(callback: AsyncCallback<VideoRecorder>): void` |
| `setOrientationHint` | `void setOrientationHint(int)` | 8 | near | easy | `orientation` | `orientation: number` |
| `setAudioSamplingRate` | `void setAudioSamplingRate(int)` | 8 | near | easy | `samplingRate` | `samplingRate: AudioSamplingRate` |
| `setMaxDuration` | `void setMaxDuration(int) throws java.lang.IllegalArgumentException` | 7 | near | easy | `duration` | `readonly duration: number` |
| `pause` | `void pause() throws java.lang.IllegalStateException` | 7 | near | easy | `pause` | `pause(): void` |
| `start` | `void start() throws java.lang.IllegalStateException` | 7 | near | easy | `start` | `start(sinkDeviceDescriptor: string, srcInputDeviceId: number, callback: AsyncCallback<void>): void` |
| `stop` | `void stop() throws java.lang.IllegalStateException` | 7 | near | easy | `stop` | `stop(callback: AsyncCallback<void>): void` |
| `MediaRecorder` | `MediaRecorder()` | 7 | near | moderate | `createAVRecorder` | `createAVRecorder(callback: AsyncCallback<AVRecorder>): void` |
| `getActiveMicrophones` | `java.util.List<android.media.MicrophoneInfo> getActiveMicrophones() throws java.io.IOException` | 7 | near | moderate | `getActivePeers` | `getActivePeers(callback: AsyncCallback<Array<PeerInfo>>): void` |
| `setAudioChannels` | `void setAudioChannels(int)` | 7 | near | moderate | `channels` | `channels: AudioChannel` |
| `setVideoSource` | `void setVideoSource(int) throws java.lang.IllegalStateException` | 6 | near | moderate | `OH_AVPlayer_SetVideoSurface` | `OH_AVErrCode OH_AVPlayer_SetVideoSurface(OH_AVPlayer *player, OHNativeWindow *window)` |
| `getAudioSourceMax` | `static final int getAudioSourceMax()` | 6 | near | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |
| `registerAudioRecordingCallback` | `void registerAudioRecordingCallback(@NonNull java.util.concurrent.Executor, @NonNull android.media.AudioManager.AudioRecordingCallback)` | 6 | near | moderate | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `setVideoEncodingBitRate` | `void setVideoEncodingBitRate(int)` | 6 | near | moderate | `OH_VideoEncoder_GetSurface` | `OH_AVErrCode OH_VideoEncoder_GetSurface(OH_AVCodec *codec, OHNativeWindow **window)` |
| `setAudioSource` | `void setAudioSource(int) throws java.lang.IllegalStateException` | 6 | near | moderate | `castAudio` | `castAudio(session: SessionToken | 'all', audioDevices: Array<audio.AudioDeviceDescriptor>, callback: AsyncCallback<void>): void` |
| `setCaptureRate` | `void setCaptureRate(double)` | 6 | near | moderate | `OH_AVScreenCapture_Release` | `OH_AVSCREEN_CAPTURE_ErrCode OH_AVScreenCapture_Release(struct OH_AVScreenCapture *capture)` |
| `setMaxFileSize` | `void setMaxFileSize(long) throws java.lang.IllegalArgumentException` | 6 | partial | moderate | `getFileAssets` | `getFileAssets(callback: AsyncCallback<FetchFileResult>): void` |
| `setOutputFormat` | `void setOutputFormat(int) throws java.lang.IllegalStateException` | 6 | partial | moderate | `sampleFormat` | `sampleFormat: AudioSampleFormat` |
| `setAudioEncodingBitRate` | `void setAudioEncodingBitRate(int)` | 6 | partial | moderate | `OH_AudioEncoder_Start` | `OH_AVErrCode OH_AudioEncoder_Start(OH_AVCodec *codec)` |
| `setVideoFrameRate` | `void setVideoFrameRate(int) throws java.lang.IllegalStateException` | 6 | partial | moderate | `createVideoPlayer` | `createVideoPlayer(callback: AsyncCallback<VideoPlayer>): void` |
| `unregisterAudioRecordingCallback` | `void unregisterAudioRecordingCallback(@NonNull android.media.AudioManager.AudioRecordingCallback)` | 6 | partial | moderate | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `getMetrics` | `android.os.PersistableBundle getMetrics()` | 6 | partial | moderate | `getAllPeers` | `getAllPeers(callback: AsyncCallback<Array<PeerInfo>>): void` |
| `getSurface` | `android.view.Surface getSurface()` | 6 | partial | moderate | `OH_VideoEncoder_GetSurface` | `OH_AVErrCode OH_VideoEncoder_GetSurface(OH_AVCodec *codec, OHNativeWindow **window)` |
| `isPrivacySensitive` | `boolean isPrivacySensitive()` | 6 | partial | moderate | `privacyType` | `privacyType?: AudioPrivacyType` |
| `setVideoSize` | `void setVideoSize(int, int) throws java.lang.IllegalStateException` | 6 | partial | moderate | `createVideoPlayer` | `createVideoPlayer(callback: AsyncCallback<VideoPlayer>): void` |
| `setVideoEncodingProfileLevel` | `void setVideoEncodingProfileLevel(int, int)` | 5 | partial | moderate | `OH_VideoEncoder_Prepare` | `OH_AVErrCode OH_VideoEncoder_Prepare(OH_AVCodec *codec)` |
| `setLocation` | `void setLocation(float, float)` | 5 | partial | moderate | `OH_AVMuxer_SetRotation` | `OH_AVErrCode OH_AVMuxer_SetRotation(OH_AVMuxer *muxer, int32_t rotation)` |
| `prepare` | `void prepare() throws java.io.IOException, java.lang.IllegalStateException` | 5 | partial | moderate | `OH_AVPlayer_Prepare` | `OH_AVErrCode OH_AVPlayer_Prepare(OH_AVPlayer *player)` |
| `setInputSurface` | `void setInputSurface(@NonNull android.view.Surface)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `setNextOutputFile` | `void setNextOutputFile(java.io.FileDescriptor) throws java.io.IOException` | 5 | partial | moderate | `getNextObject` | `getNextObject(callback: AsyncCallback<FileAsset>): void` |
| `setNextOutputFile` | `void setNextOutputFile(java.io.File) throws java.io.IOException` | 5 | partial | moderate | `getNextObject` | `getNextObject(callback: AsyncCallback<FileAsset>): void` |
| `setPrivacySensitive` | `void setPrivacySensitive(boolean)` | 5 | partial | moderate | `privacyType` | `privacyType?: AudioPrivacyType` |
| `setPreviewDisplay` | `void setPreviewDisplay(android.view.Surface)` | 5 | partial | moderate | `startImagePreview` | `startImagePreview(images: Array<string>, index: number, callback: AsyncCallback<void>): void` |
| `setOutputFile` | `void setOutputFile(java.io.FileDescriptor) throws java.lang.IllegalStateException` | 5 | partial | moderate | `OH_MetadataOutput_Release` | `Camera_ErrorCode OH_MetadataOutput_Release(Camera_MetadataOutput* metadataOutput)` |
| `setOutputFile` | `void setOutputFile(java.io.File)` | 5 | partial | moderate | `OH_MetadataOutput_Release` | `Camera_ErrorCode OH_MetadataOutput_Release(Camera_MetadataOutput* metadataOutput)` |
| `setOutputFile` | `void setOutputFile(String) throws java.lang.IllegalStateException` | 5 | partial | moderate | `OH_MetadataOutput_Release` | `Camera_ErrorCode OH_MetadataOutput_Release(Camera_MetadataOutput* metadataOutput)` |
| `getRoutedDevice` | `android.media.AudioDeviceInfo getRoutedDevice()` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `setProfile` | `void setProfile(android.media.CamcorderProfile)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 10 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setOnErrorListener` | 5 | partial | Return safe default (null/false/0/empty) |
| `getPreferredDevice` | 5 | partial | Return safe default (null/false/0/empty) |
| `getMaxAmplitude` | 5 | partial | Return safe default (null/false/0/empty) |
| `setOnInfoListener` | 4 | partial | Return safe default (null/false/0/empty) |
| `setPreferredDevice` | 4 | partial | Log warning + no-op |
| `setPreferredMicrophoneDirection` | 4 | partial | Log warning + no-op |
| `setPreferredMicrophoneFieldDimension` | 4 | partial | Log warning + no-op |
| `removeOnRoutingChangedListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `addOnRoutingChangedListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `finalize` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 42 methods that have score >= 5
2. Stub 10 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaRecorder`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaRecorder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 52 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 42 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
