# SKILL: android.media.AudioManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.AudioManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.AudioManager` |
| **Package** | `android.media` |
| **Total Methods** | 52 |
| **Avg Score** | 6.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 33 (63%) |
| **Partial/Composite** | 19 (36%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 48 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getStreamMinVolume` | `int getStreamMinVolume(int)` | 8 | direct | easy | `streamInfo` | `streamInfo: AudioStreamInfo` |
| `generateAudioSessionId` | `int generateAudioSessionId()` | 8 | near | easy | `createAudioRenderer` | `createAudioRenderer(options: AudioRendererOptions, callback: AsyncCallback<AudioRenderer>): void` |
| `getRingerMode` | `int getRingerMode()` | 8 | near | easy | `getAudioManager` | `getAudioManager(): AudioManager` |
| `getStreamVolume` | `int getStreamVolume(int)` | 8 | near | easy | `streamInfo` | `streamInfo: AudioStreamInfo` |
| `setStreamVolume` | `void setStreamVolume(int, int, int)` | 8 | near | easy | `streamInfo` | `streamInfo: AudioStreamInfo` |
| `requestAudioFocus` | `int requestAudioFocus(@NonNull android.media.AudioFocusRequest)` | 8 | near | easy | `createAudioCapturer` | `createAudioCapturer(options: AudioCapturerOptions, callback: AsyncCallback<AudioCapturer>): void` |
| `isStreamMute` | `boolean isStreamMute(int)` | 7 | near | easy | `streamInfo` | `streamInfo: AudioStreamInfo` |
| `getAllowedCapturePolicy` | `int getAllowedCapturePolicy()` | 7 | near | easy | `createAudioCapturer` | `createAudioCapturer(options: AudioCapturerOptions, callback: AsyncCallback<AudioCapturer>): void` |
| `setAllowedCapturePolicy` | `void setAllowedCapturePolicy(int)` | 7 | near | easy | `createAudioCapturer` | `createAudioCapturer(options: AudioCapturerOptions, callback: AsyncCallback<AudioCapturer>): void` |
| `registerAudioDeviceCallback` | `void registerAudioDeviceCallback(android.media.AudioDeviceCallback, @Nullable android.os.Handler)` | 7 | near | easy | `createAudioRenderer` | `createAudioRenderer(options: AudioRendererOptions, callback: AsyncCallback<AudioRenderer>): void` |
| `setRingerMode` | `void setRingerMode(int)` | 7 | near | easy | `streamInfo` | `streamInfo: AudioStreamInfo` |
| `getStreamVolumeDb` | `float getStreamVolumeDb(int, int, int)` | 7 | near | easy | `streamInfo` | `streamInfo: AudioStreamInfo` |
| `adjustStreamVolume` | `void adjustStreamVolume(int, int, int)` | 7 | near | easy | `streamInfo` | `streamInfo: AudioStreamInfo` |
| `getParameters` | `String getParameters(String)` | 7 | near | easy | `getAudioManager` | `getAudioManager(): AudioManager` |
| `getStreamMaxVolume` | `int getStreamMaxVolume(int)` | 7 | near | easy | `streamInfo` | `streamInfo: AudioStreamInfo` |
| `unregisterAudioDeviceCallback` | `void unregisterAudioDeviceCallback(android.media.AudioDeviceCallback)` | 7 | near | easy | `createAudioRenderer` | `createAudioRenderer(options: AudioRendererOptions, callback: AsyncCallback<AudioRenderer>): void` |
| `registerAudioRecordingCallback` | `void registerAudioRecordingCallback(@NonNull android.media.AudioManager.AudioRecordingCallback, @Nullable android.os.Handler)` | 7 | near | moderate | `createAudioCapturer` | `createAudioCapturer(options: AudioCapturerOptions, callback: AsyncCallback<AudioCapturer>): void` |
| `setParameters` | `void setParameters(String)` | 7 | near | moderate | `samplingRate` | `samplingRate: AudioSamplingRate` |
| `setMode` | `void setMode(int)` | 7 | near | moderate | `streamInfo` | `streamInfo: AudioStreamInfo` |
| `unregisterAudioRecordingCallback` | `void unregisterAudioRecordingCallback(@NonNull android.media.AudioManager.AudioRecordingCallback)` | 7 | near | moderate | `createAudioCapturer` | `createAudioCapturer(options: AudioCapturerOptions, callback: AsyncCallback<AudioCapturer>): void` |
| `abandonAudioFocusRequest` | `int abandonAudioFocusRequest(@NonNull android.media.AudioFocusRequest)` | 7 | near | moderate | `createAudioCapturer` | `createAudioCapturer(options: AudioCapturerOptions, callback: AsyncCallback<AudioCapturer>): void` |
| `getProperty` | `String getProperty(String)` | 7 | near | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |
| `registerAudioPlaybackCallback` | `void registerAudioPlaybackCallback(@NonNull android.media.AudioManager.AudioPlaybackCallback, @Nullable android.os.Handler)` | 7 | near | moderate | `createAudioCapturer` | `createAudioCapturer(options: AudioCapturerOptions, callback: AsyncCallback<AudioCapturer>): void` |
| `getMicrophones` | `java.util.List<android.media.MicrophoneInfo> getMicrophones() throws java.io.IOException` | 7 | near | moderate | `channels` | `channels: AudioChannel` |
| `getMode` | `int getMode()` | 7 | near | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |
| `isCallScreeningModeSupported` | `boolean isCallScreeningModeSupported()` | 6 | near | moderate | `samplingRate` | `samplingRate: AudioSamplingRate` |
| `onRecordingConfigChanged` | `void onRecordingConfigChanged(java.util.List<android.media.AudioRecordingConfiguration>)` | 6 | near | moderate | `encodingType` | `encodingType: AudioEncodingType` |
| `unregisterAudioPlaybackCallback` | `void unregisterAudioPlaybackCallback(@NonNull android.media.AudioManager.AudioPlaybackCallback)` | 6 | near | moderate | `createAudioCapturer` | `createAudioCapturer(options: AudioCapturerOptions, callback: AsyncCallback<AudioCapturer>): void` |
| `isMusicActive` | `boolean isMusicActive()` | 6 | near | moderate | `privacyType` | `privacyType?: AudioPrivacyType` |
| `setMicrophoneMute` | `void setMicrophoneMute(boolean)` | 6 | near | moderate | `samplingRate` | `samplingRate: AudioSamplingRate` |
| `AudioRecordingCallback` | `AudioManager.AudioRecordingCallback()` | 6 | near | moderate | `encodingType` | `encodingType: AudioEncodingType` |
| `getDevices` | `android.media.AudioDeviceInfo[] getDevices(int)` | 6 | near | moderate | `rendererId` | `rendererId?: number` |
| `onPlaybackConfigChanged` | `void onPlaybackConfigChanged(java.util.List<android.media.AudioPlaybackConfiguration>)` | 6 | near | moderate | `encodingType` | `encodingType: AudioEncodingType` |
| `AudioPlaybackCallback` | `AudioManager.AudioPlaybackCallback()` | 6 | partial | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |
| `adjustSuggestedStreamVolume` | `void adjustSuggestedStreamVolume(int, int, int)` | 6 | partial | moderate | `streamInfo` | `streamInfo: AudioStreamInfo` |
| `isHapticPlaybackSupported` | `static boolean isHapticPlaybackSupported()` | 6 | partial | moderate | `samplingRate` | `samplingRate: AudioSamplingRate` |
| `isMicrophoneMute` | `boolean isMicrophoneMute()` | 6 | partial | moderate | `createTonePlayer` | `createTonePlayer(options: AudioRendererInfo, callback: AsyncCallback<TonePlayer>): void` |
| `isSpeakerphoneOn` | `boolean isSpeakerphoneOn()` | 6 | partial | moderate | `createTonePlayer` | `createTonePlayer(options: AudioRendererInfo, callback: AsyncCallback<TonePlayer>): void` |
| `adjustVolume` | `void adjustVolume(int, int)` | 6 | partial | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |
| `playSoundEffect` | `void playSoundEffect(int)` | 6 | partial | moderate | `sampleFormat` | `sampleFormat: AudioSampleFormat` |
| `playSoundEffect` | `void playSoundEffect(int, float)` | 6 | partial | moderate | `sampleFormat` | `sampleFormat: AudioSampleFormat` |
| `setSpeakerphoneOn` | `void setSpeakerphoneOn(boolean)` | 6 | partial | moderate | `streamInfo` | `streamInfo: AudioStreamInfo` |
| `setBluetoothScoOn` | `void setBluetoothScoOn(boolean)` | 6 | partial | moderate | `createTonePlayer` | `createTonePlayer(options: AudioRendererInfo, callback: AsyncCallback<TonePlayer>): void` |
| `isOffloadedPlaybackSupported` | `static boolean isOffloadedPlaybackSupported(@NonNull android.media.AudioFormat, @NonNull android.media.AudioAttributes)` | 6 | partial | moderate | `samplingRate` | `samplingRate: AudioSamplingRate` |
| `dispatchMediaKeyEvent` | `void dispatchMediaKeyEvent(android.view.KeyEvent)` | 5 | partial | moderate | `eventType` | `eventType: InterruptType` |
| `unloadSoundEffects` | `void unloadSoundEffects()` | 5 | partial | moderate | `encodingType` | `encodingType: AudioEncodingType` |
| `isVolumeFixed` | `boolean isVolumeFixed()` | 5 | partial | moderate | `sampleFormat` | `sampleFormat: AudioSampleFormat` |
| `loadSoundEffects` | `void loadSoundEffects()` | 5 | partial | moderate | `rendererId` | `rendererId?: number` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `startBluetoothSco` | 5 | partial | Return dummy instance / no-op |
| `stopBluetoothSco` | 4 | partial | No-op |
| `isBluetoothScoOn` | 4 | composite | Return safe default (null/false/0/empty) |
| `isBluetoothScoAvailableOffCall` | 3 | composite | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 48 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.AudioManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.AudioManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 52 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 48 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
