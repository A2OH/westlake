# SKILL: android.media.MediaPlayer

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaPlayer`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaPlayer` |
| **Package** | `android.media` |
| **Total Methods** | 85 |
| **Avg Score** | 6.4 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 59 (69%) |
| **Partial/Composite** | 26 (30%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 82 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `MediaPlayer` | `MediaPlayer()` | 8 | direct | easy | `createAudioPlayer` | `createAudioPlayer(): AudioPlayer` |
| `setNextMediaPlayer` | `void setNextMediaPlayer(android.media.MediaPlayer)` | 8 | direct | easy | `createAudioPlayer` | `createAudioPlayer(): AudioPlayer` |
| `create` | `static android.media.MediaPlayer create(android.content.Context, android.net.Uri)` | 8 | direct | easy | `createAVPlayer` | `createAVPlayer(callback: AsyncCallback<AVPlayer>): void` |
| `create` | `static android.media.MediaPlayer create(android.content.Context, android.net.Uri, android.view.SurfaceHolder)` | 8 | direct | easy | `createAVPlayer` | `createAVPlayer(callback: AsyncCallback<AVPlayer>): void` |
| `create` | `static android.media.MediaPlayer create(android.content.Context, android.net.Uri, android.view.SurfaceHolder, android.media.AudioAttributes, int)` | 8 | direct | easy | `createAVPlayer` | `createAVPlayer(callback: AsyncCallback<AVPlayer>): void` |
| `create` | `static android.media.MediaPlayer create(android.content.Context, int)` | 8 | direct | easy | `createAVPlayer` | `createAVPlayer(callback: AsyncCallback<AVPlayer>): void` |
| `create` | `static android.media.MediaPlayer create(android.content.Context, int, android.media.AudioAttributes, int)` | 8 | direct | easy | `createAVPlayer` | `createAVPlayer(callback: AsyncCallback<AVPlayer>): void` |
| `setDisplay` | `void setDisplay(android.view.SurfaceHolder)` | 8 | near | easy | `createAudioPlayer` | `createAudioPlayer(): AudioPlayer` |
| `setVideoScalingMode` | `void setVideoScalingMode(int)` | 8 | near | easy | `createVideoRecorder` | `createVideoRecorder(callback: AsyncCallback<VideoRecorder>): void` |
| `getAudioSessionId` | `int getAudioSessionId()` | 8 | near | easy | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `setAudioSessionId` | `void setAudioSessionId(int) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException` | 8 | near | easy | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `prepareDrm` | `void prepareDrm(@NonNull java.util.UUID) throws android.media.MediaPlayer.ProvisioningNetworkErrorException, android.media.MediaPlayer.ProvisioningServerErrorException, android.media.ResourceBusyException, android.media.UnsupportedSchemeException` | 7 | near | easy | `createAVRecorder` | `createAVRecorder(callback: AsyncCallback<AVRecorder>): void` |
| `releaseDrm` | `void releaseDrm() throws android.media.MediaPlayer.NoDrmSchemeException` | 7 | near | easy | `createAVRecorder` | `createAVRecorder(callback: AsyncCallback<AVRecorder>): void` |
| `setWakeMode` | `void setWakeMode(android.content.Context, int)` | 7 | near | easy | `createAVRecorder` | `createAVRecorder(callback: AsyncCallback<AVRecorder>): void` |
| `getVideoHeight` | `int getVideoHeight()` | 7 | near | easy | `createVideoPlayer` | `createVideoPlayer(callback: AsyncCallback<VideoPlayer>): void` |
| `setAudioAttributes` | `void setAudioAttributes(android.media.AudioAttributes) throws java.lang.IllegalArgumentException` | 7 | near | easy | `createAudioPlayer` | `createAudioPlayer(): AudioPlayer` |
| `getVideoWidth` | `int getVideoWidth()` | 7 | near | easy | `createVideoRecorder` | `createVideoRecorder(callback: AsyncCallback<VideoRecorder>): void` |
| `pause` | `void pause() throws java.lang.IllegalStateException` | 7 | near | easy | `pause` | `pause(): void` |
| `setDataSource` | `void setDataSource(@NonNull android.content.Context, @NonNull android.net.Uri) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.SecurityException` | 7 | near | easy | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `setDataSource` | `void setDataSource(@NonNull android.content.Context, @NonNull android.net.Uri, @Nullable java.util.Map<java.lang.String,java.lang.String>, @Nullable java.util.List<java.net.HttpCookie>) throws java.io.IOException` | 7 | near | easy | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `setDataSource` | `void setDataSource(@NonNull android.content.Context, @NonNull android.net.Uri, @Nullable java.util.Map<java.lang.String,java.lang.String>) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.SecurityException` | 7 | near | easy | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `setDataSource` | `void setDataSource(String) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException, java.lang.SecurityException` | 7 | near | easy | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `setDataSource` | `void setDataSource(@NonNull android.content.res.AssetFileDescriptor) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` | 7 | near | easy | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `setDataSource` | `void setDataSource(java.io.FileDescriptor) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` | 7 | near | easy | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `setDataSource` | `void setDataSource(java.io.FileDescriptor, long, long) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` | 7 | near | easy | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `setDataSource` | `void setDataSource(android.media.MediaDataSource) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException` | 7 | near | easy | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `setOnVideoSizeChangedListener` | `void setOnVideoSizeChangedListener(android.media.MediaPlayer.OnVideoSizeChangedListener)` | 7 | near | easy | `createVideoRecorder` | `createVideoRecorder(callback: AsyncCallback<VideoRecorder>): void` |
| `start` | `void start() throws java.lang.IllegalStateException` | 7 | near | easy | `start` | `start(sinkDeviceDescriptor: string, srcInputDeviceId: number, callback: AsyncCallback<void>): void` |
| `stop` | `void stop() throws java.lang.IllegalStateException` | 7 | near | easy | `stop` | `stop(callback: AsyncCallback<void>): void` |
| `provideKeyResponse` | `byte[] provideKeyResponse(@Nullable byte[], @NonNull byte[]) throws android.media.DeniedByServerException, android.media.MediaPlayer.NoDrmSchemeException` | 7 | near | moderate | `createVideoRecorder` | `createVideoRecorder(callback: AsyncCallback<VideoRecorder>): void` |
| `prepare` | `void prepare() throws java.io.IOException, java.lang.IllegalStateException` | 7 | near | moderate | `createAVPlayer` | `createAVPlayer(callback: AsyncCallback<AVPlayer>): void` |
| `release` | `void release()` | 7 | near | moderate | `createAVPlayer` | `createAVPlayer(callback: AsyncCallback<AVPlayer>): void` |
| `restoreKeys` | `void restoreKeys(@NonNull byte[]) throws android.media.MediaPlayer.NoDrmSchemeException` | 7 | near | moderate | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `setOnTimedMetaDataAvailableListener` | `void setOnTimedMetaDataAvailableListener(android.media.MediaPlayer.OnTimedMetaDataAvailableListener)` | 7 | near | moderate | `createAVMetadataExtractor` | `createAVMetadataExtractor(): Promise<AVMetadataExtractor>` |
| `setVolume` | `void setVolume(float, float)` | 7 | near | moderate | `createVideoPlayer` | `createVideoPlayer(callback: AsyncCallback<VideoPlayer>): void` |
| `setPlaybackParams` | `void setPlaybackParams(@NonNull android.media.PlaybackParams)` | 7 | near | moderate | `createAVPlayer` | `createAVPlayer(callback: AsyncCallback<AVPlayer>): void` |
| `setScreenOnWhilePlaying` | `void setScreenOnWhilePlaying(boolean)` | 6 | near | moderate | `createAudioPlayer` | `createAudioPlayer(): AudioPlayer` |
| `getSelectedTrack` | `int getSelectedTrack(int) throws java.lang.IllegalStateException` | 6 | near | moderate | `createAVRecorder` | `createAVRecorder(callback: AsyncCallback<AVRecorder>): void` |
| `setOnPreparedListener` | `void setOnPreparedListener(android.media.MediaPlayer.OnPreparedListener)` | 6 | near | moderate | `createAVRecorder` | `createAVRecorder(callback: AsyncCallback<AVRecorder>): void` |
| `setLooping` | `void setLooping(boolean)` | 6 | near | moderate | `OH_AVPlayer_SetLooping` | `OH_AVErrCode OH_AVPlayer_SetLooping(OH_AVPlayer *player, bool loop)` |
| `getCurrentPosition` | `int getCurrentPosition()` | 6 | near | moderate | `createSoundPool` | `createSoundPool(maxStreams: number,
    audioRenderInfo: audio.AudioRendererInfo,
    callback: AsyncCallback<SoundPool>): void` |
| `clearOnSubtitleDataListener` | `void clearOnSubtitleDataListener()` | 6 | near | moderate | `createAVMetadataExtractor` | `createAVMetadataExtractor(): Promise<AVMetadataExtractor>` |
| `setSurface` | `void setSurface(android.view.Surface)` | 6 | near | moderate | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `getRoutedDevice` | `android.media.AudioDeviceInfo getRoutedDevice()` | 6 | near | moderate | `createVideoRecorder` | `createVideoRecorder(callback: AsyncCallback<VideoRecorder>): void` |
| `setOnDrmConfigHelper` | `void setOnDrmConfigHelper(android.media.MediaPlayer.OnDrmConfigHelper)` | 6 | near | moderate | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `setOnTimedTextListener` | `void setOnTimedTextListener(android.media.MediaPlayer.OnTimedTextListener)` | 6 | near | moderate | `createAVImageGenerator` | `createAVImageGenerator(): Promise<AVImageGenerator>` |
| `addOnRoutingChangedListener` | `void addOnRoutingChangedListener(android.media.AudioRouting.OnRoutingChangedListener, android.os.Handler)` | 6 | near | moderate | `createAVImageGenerator` | `createAVImageGenerator(): Promise<AVImageGenerator>` |
| `removeOnRoutingChangedListener` | `void removeOnRoutingChangedListener(android.media.AudioRouting.OnRoutingChangedListener)` | 6 | near | moderate | `createVideoRecorder` | `createVideoRecorder(callback: AsyncCallback<VideoRecorder>): void` |
| `clearOnMediaTimeDiscontinuityListener` | `void clearOnMediaTimeDiscontinuityListener()` | 6 | near | moderate | `createAVImageGenerator` | `createAVImageGenerator(): Promise<AVImageGenerator>` |
| `addTimedTextSource` | `void addTimedTextSource(String, String) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` | 6 | near | moderate | `createAVImageGenerator` | `createAVImageGenerator(): Promise<AVImageGenerator>` |
| `addTimedTextSource` | `void addTimedTextSource(android.content.Context, android.net.Uri, String) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` | 6 | near | moderate | `createAVImageGenerator` | `createAVImageGenerator(): Promise<AVImageGenerator>` |
| `addTimedTextSource` | `void addTimedTextSource(java.io.FileDescriptor, String) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException` | 6 | near | moderate | `createAVImageGenerator` | `createAVImageGenerator(): Promise<AVImageGenerator>` |
| `addTimedTextSource` | `void addTimedTextSource(java.io.FileDescriptor, long, long, String) throws java.lang.IllegalArgumentException, java.lang.IllegalStateException` | 6 | near | moderate | `createAVImageGenerator` | `createAVImageGenerator(): Promise<AVImageGenerator>` |
| `attachAuxEffect` | `void attachAuxEffect(int)` | 6 | near | moderate | `createAVMetadataExtractor` | `createAVMetadataExtractor(): Promise<AVMetadataExtractor>` |
| `getMetrics` | `android.os.PersistableBundle getMetrics()` | 6 | near | moderate | `createAVMetadataExtractor` | `createAVMetadataExtractor(): Promise<AVMetadataExtractor>` |
| `isLooping` | `boolean isLooping()` | 6 | near | moderate | `OH_AVPlayer_IsLooping` | `bool OH_AVPlayer_IsLooping(OH_AVPlayer *player)` |
| `setOnDrmPreparedListener` | `void setOnDrmPreparedListener(android.media.MediaPlayer.OnDrmPreparedListener)` | 6 | near | moderate | `createAVRecorder` | `createAVRecorder(callback: AsyncCallback<AVRecorder>): void` |
| `setOnDrmPreparedListener` | `void setOnDrmPreparedListener(android.media.MediaPlayer.OnDrmPreparedListener, android.os.Handler)` | 6 | near | moderate | `createAVRecorder` | `createAVRecorder(callback: AsyncCallback<AVRecorder>): void` |
| `setOnErrorListener` | `void setOnErrorListener(android.media.MediaPlayer.OnErrorListener)` | 6 | near | moderate | `createAVImageGenerator` | `createAVImageGenerator(): Promise<AVImageGenerator>` |
| `setOnSeekCompleteListener` | `void setOnSeekCompleteListener(android.media.MediaPlayer.OnSeekCompleteListener)` | 6 | partial | moderate | `createAVRecorder` | `createAVRecorder(callback: AsyncCallback<AVRecorder>): void` |
| `getDuration` | `int getDuration()` | 6 | partial | moderate | `createAVMetadataExtractor` | `createAVMetadataExtractor(): Promise<AVMetadataExtractor>` |
| `selectTrack` | `void selectTrack(int) throws java.lang.IllegalStateException` | 6 | partial | moderate | `createAVMetadataExtractor` | `createAVMetadataExtractor(): Promise<AVMetadataExtractor>` |
| `isPlaying` | `boolean isPlaying()` | 6 | partial | moderate | `createAudioPlayer` | `createAudioPlayer(): AudioPlayer` |
| `setOnSubtitleDataListener` | `void setOnSubtitleDataListener(@NonNull android.media.MediaPlayer.OnSubtitleDataListener, @NonNull android.os.Handler)` | 6 | partial | moderate | `createAVImageGenerator` | `createAVImageGenerator(): Promise<AVImageGenerator>` |
| `setOnSubtitleDataListener` | `void setOnSubtitleDataListener(@NonNull android.media.MediaPlayer.OnSubtitleDataListener)` | 6 | partial | moderate | `createAVImageGenerator` | `createAVImageGenerator(): Promise<AVImageGenerator>` |
| `getPreferredDevice` | `android.media.AudioDeviceInfo getPreferredDevice()` | 6 | partial | moderate | `createVideoRecorder` | `createVideoRecorder(callback: AsyncCallback<VideoRecorder>): void` |
| `getTrackInfo` | `android.media.MediaPlayer.TrackInfo[] getTrackInfo() throws java.lang.IllegalStateException` | 6 | partial | moderate | `createAVMetadataExtractor` | `createAVMetadataExtractor(): Promise<AVMetadataExtractor>` |
| `setDrmPropertyString` | `void setDrmPropertyString(@NonNull String, @NonNull String) throws android.media.MediaPlayer.NoDrmSchemeException` | 6 | partial | moderate | `createAudioPlayer` | `createAudioPlayer(): AudioPlayer` |
| `setPreferredDevice` | `boolean setPreferredDevice(android.media.AudioDeviceInfo)` | 6 | partial | moderate | `createVideoRecorder` | `createVideoRecorder(callback: AsyncCallback<VideoRecorder>): void` |
| `deselectTrack` | `void deselectTrack(int) throws java.lang.IllegalStateException` | 6 | partial | moderate | `createAVPlayer` | `createAVPlayer(callback: AsyncCallback<AVPlayer>): void` |
| `prepareAsync` | `void prepareAsync() throws java.lang.IllegalStateException` | 6 | partial | moderate | `createSoundPool` | `createSoundPool(maxStreams: number,
    audioRenderInfo: audio.AudioRendererInfo,
    callback: AsyncCallback<SoundPool>): void` |
| `setOnMediaTimeDiscontinuityListener` | `void setOnMediaTimeDiscontinuityListener(@NonNull android.media.MediaPlayer.OnMediaTimeDiscontinuityListener, @NonNull android.os.Handler)` | 6 | partial | moderate | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `setOnMediaTimeDiscontinuityListener` | `void setOnMediaTimeDiscontinuityListener(@NonNull android.media.MediaPlayer.OnMediaTimeDiscontinuityListener)` | 6 | partial | moderate | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `setOnBufferingUpdateListener` | `void setOnBufferingUpdateListener(android.media.MediaPlayer.OnBufferingUpdateListener)` | 6 | partial | moderate | `createAVImageGenerator` | `createAVImageGenerator(): Promise<AVImageGenerator>` |
| `setOnInfoListener` | `void setOnInfoListener(android.media.MediaPlayer.OnInfoListener)` | 6 | partial | moderate | `createAVImageGenerator` | `createAVImageGenerator(): Promise<AVImageGenerator>` |
| `setOnCompletionListener` | `void setOnCompletionListener(android.media.MediaPlayer.OnCompletionListener)` | 6 | partial | moderate | `createAVImageGenerator` | `createAVImageGenerator(): Promise<AVImageGenerator>` |
| `setAuxEffectSendLevel` | `void setAuxEffectSendLevel(float)` | 6 | partial | moderate | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `getDrmInfo` | `android.media.MediaPlayer.DrmInfo getDrmInfo()` | 5 | partial | moderate | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `setOnDrmInfoListener` | `void setOnDrmInfoListener(android.media.MediaPlayer.OnDrmInfoListener)` | 5 | partial | moderate | `createSoundPool` | `createSoundPool(maxStreams: number,
    audioRenderInfo: audio.AudioRendererInfo,
    callback: AsyncCallback<SoundPool>): void` |
| `setOnDrmInfoListener` | `void setOnDrmInfoListener(android.media.MediaPlayer.OnDrmInfoListener, android.os.Handler)` | 5 | partial | moderate | `createSoundPool` | `createSoundPool(maxStreams: number,
    audioRenderInfo: audio.AudioRendererInfo,
    callback: AsyncCallback<SoundPool>): void` |
| `reset` | `void reset()` | 5 | partial | moderate | `createAVPlayer` | `createAVPlayer(callback: AsyncCallback<AVPlayer>): void` |
| `setSyncParams` | `void setSyncParams(@NonNull android.media.SyncParams)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `finalize` | 5 | partial | throw UnsupportedOperationException |
| `seekTo` | 4 | partial | throw UnsupportedOperationException |
| `seekTo` | 4 | partial | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 82 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaPlayer`:

- `android.content.Context` (already shimmed)
- `android.net.Uri` (already shimmed)

## Quality Gates

Before marking `android.media.MediaPlayer` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 85 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 82 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
