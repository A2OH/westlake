# SKILL: android.media.AudioRecord

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.AudioRecord`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.AudioRecord` |
| **Package** | `android.media` |
| **Total Methods** | 42 |
| **Avg Score** | 6.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 19 (45%) |
| **Partial/Composite** | 22 (52%) |
| **No Mapping** | 1 (2%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 31 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `release` | `void release()` | 10 | direct | trivial | `release` | `release(callback: AsyncCallback<void>): void` |
| `read` | `int read(@NonNull byte[], int, int)` | 8 | direct | easy | `read` | `read(): Promise<number[]>` |
| `read` | `int read(@NonNull byte[], int, int, int)` | 8 | direct | easy | `read` | `read(): Promise<number[]>` |
| `read` | `int read(@NonNull short[], int, int)` | 8 | direct | easy | `read` | `read(): Promise<number[]>` |
| `read` | `int read(@NonNull short[], int, int, int)` | 8 | direct | easy | `read` | `read(): Promise<number[]>` |
| `read` | `int read(@NonNull float[], int, int, int)` | 8 | direct | easy | `read` | `read(): Promise<number[]>` |
| `read` | `int read(@NonNull java.nio.ByteBuffer, int)` | 8 | direct | easy | `read` | `read(): Promise<number[]>` |
| `read` | `int read(@NonNull java.nio.ByteBuffer, int, int)` | 8 | direct | easy | `read` | `read(): Promise<number[]>` |
| `AudioRecord` | `AudioRecord(int, int, int, int, int) throws java.lang.IllegalArgumentException` | 7 | near | easy | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `getSampleRate` | `int getSampleRate()` | 7 | near | easy | `samplingRate` | `samplingRate: AudioSamplingRate` |
| `stop` | `void stop() throws java.lang.IllegalStateException` | 7 | near | easy | `stop` | `stop(callback: AsyncCallback<void>): void` |
| `getChannelCount` | `int getChannelCount()` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `getAudioSessionId` | `int getAudioSessionId()` | 7 | near | moderate | `sessionId` | `readonly sessionId: string` |
| `startRecording` | `void startRecording() throws java.lang.IllegalStateException` | 7 | near | moderate | `startCasting` | `startCasting(session: SessionToken, device: OutputDeviceInfo, callback: AsyncCallback<void>): void` |
| `startRecording` | `void startRecording(android.media.MediaSyncEvent) throws java.lang.IllegalStateException` | 7 | near | moderate | `startCasting` | `startCasting(session: SessionToken, device: OutputDeviceInfo, callback: AsyncCallback<void>): void` |
| `getAudioFormat` | `int getAudioFormat()` | 7 | near | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |
| `getActiveMicrophones` | `java.util.List<android.media.MicrophoneInfo> getActiveMicrophones() throws java.io.IOException` | 7 | near | moderate | `getActivePeers` | `getActivePeers(callback: AsyncCallback<Array<PeerInfo>>): void` |
| `getAudioSource` | `int getAudioSource()` | 7 | near | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |
| `registerAudioRecordingCallback` | `void registerAudioRecordingCallback(@NonNull java.util.concurrent.Executor, @NonNull android.media.AudioManager.AudioRecordingCallback)` | 6 | near | moderate | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `unregisterAudioRecordingCallback` | `void unregisterAudioRecordingCallback(@NonNull android.media.AudioManager.AudioRecordingCallback)` | 6 | partial | moderate | `createAudioRecorder` | `createAudioRecorder(): AudioRecorder` |
| `getTimestamp` | `int getTimestamp(@NonNull android.media.AudioTimestamp, int)` | 6 | partial | moderate | `OH_AudioCapturer_GetTimestamp` | `OH_AudioStream_Result OH_AudioCapturer_GetTimestamp(OH_AudioCapturer* capturer,
    clockid_t clockId, int64_t* framePosition, int64_t* timestamp)` |
| `getMetrics` | `android.os.PersistableBundle getMetrics()` | 6 | partial | moderate | `getAllPeers` | `getAllPeers(callback: AsyncCallback<Array<PeerInfo>>): void` |
| `getState` | `int getState()` | 6 | partial | moderate | `getLastObject` | `getLastObject(callback: AsyncCallback<FileAsset>): void` |
| `getPositionNotificationPeriod` | `int getPositionNotificationPeriod()` | 6 | partial | moderate | `getPositionObject` | `getPositionObject(index: number, callback: AsyncCallback<FileAsset>): void` |
| `getRecordingState` | `int getRecordingState()` | 6 | partial | moderate | `getCount` | `getCount(): number` |
| `isPrivacySensitive` | `boolean isPrivacySensitive()` | 6 | partial | moderate | `privacyType` | `privacyType?: AudioPrivacyType` |
| `getBufferSizeInFrames` | `int getBufferSizeInFrames()` | 5 | partial | moderate | `OH_AVBuffer_SetParameter` | `OH_AVErrCode OH_AVBuffer_SetParameter(OH_AVBuffer *buffer, const OH_AVFormat *format)` |
| `getRoutedDevice` | `android.media.AudioDeviceInfo getRoutedDevice()` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `setPositionNotificationPeriod` | `int setPositionNotificationPeriod(int)` | 5 | partial | moderate | `getPositionObject` | `getPositionObject(index: number, callback: AsyncCallback<FileAsset>): void` |
| `getChannelConfiguration` | `int getChannelConfiguration()` | 5 | partial | moderate | `OH_AudioCapturer_GetChannelCount` | `OH_AudioStream_Result OH_AudioCapturer_GetChannelCount(OH_AudioCapturer* capturer, int32_t* channelCount)` |
| `getNotificationMarkerPosition` | `int getNotificationMarkerPosition()` | 5 | partial | moderate | `getPositionObject` | `getPositionObject(index: number, callback: AsyncCallback<FileAsset>): void` |

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getMinBufferSize` | 5 | partial | Return safe default (null/false/0/empty) |
| `getPreferredDevice` | 5 | partial | Return safe default (null/false/0/empty) |
| `setRecordPositionUpdateListener` | 5 | partial | Return safe default (null/false/0/empty) |
| `setRecordPositionUpdateListener` | 5 | partial | Return safe default (null/false/0/empty) |
| `setPreferredDevice` | 4 | partial | Log warning + no-op |
| `setNotificationMarkerPosition` | 4 | partial | Log warning + no-op |
| `setPreferredMicrophoneDirection` | 4 | partial | Log warning + no-op |
| `setPreferredMicrophoneFieldDimension` | 4 | partial | Log warning + no-op |
| `removeOnRoutingChangedListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `addOnRoutingChangedListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `finalize` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 31 methods that have score >= 5
2. Stub 11 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.AudioRecord`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.AudioRecord` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 42 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 31 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
