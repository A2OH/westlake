# SKILL: android.media.AudioRecordingConfiguration

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.AudioRecordingConfiguration`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.AudioRecordingConfiguration` |
| **Package** | `android.media` |
| **Total Methods** | 9 |
| **Avg Score** | 5.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (33%) |
| **Partial/Composite** | 5 (55%) |
| **No Mapping** | 1 (11%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getAudioDevice` | `android.media.AudioDeviceInfo getAudioDevice()` | 7 | near | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |
| `getAudioSource` | `int getAudioSource()` | 7 | near | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |
| `getFormat` | `android.media.AudioFormat getFormat()` | 7 | near | moderate | `sampleFormat` | `sampleFormat: AudioSampleFormat` |
| `getClientAudioSessionId` | `int getClientAudioSessionId()` | 6 | partial | moderate | `createAVSession` | `createAVSession(context: Context, tag: string, type: AVSessionType, callback: AsyncCallback<AVSession>): void` |
| `getClientFormat` | `android.media.AudioFormat getClientFormat()` | 6 | partial | moderate | `OH_Image_Receiver_GetFormat` | `int32_t OH_Image_Receiver_GetFormat(const ImageReceiverNative* native, int32_t* format)` |
| `getClientAudioSource` | `int getClientAudioSource()` | 6 | partial | moderate | `createAudioCapturer` | `createAudioCapturer(options: AudioCapturerOptions, callback: AsyncCallback<AudioCapturer>): void` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isClientSilenced` | 4 | partial | Return safe default (null/false/0/empty) |
| `writeToParcel` | 4 | partial | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 6 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.AudioRecordingConfiguration`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.AudioRecordingConfiguration` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
