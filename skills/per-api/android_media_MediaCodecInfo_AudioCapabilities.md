# SKILL: android.media.MediaCodecInfo.AudioCapabilities

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaCodecInfo.AudioCapabilities`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaCodecInfo.AudioCapabilities` |
| **Package** | `android.media.MediaCodecInfo` |
| **Total Methods** | 5 |
| **Avg Score** | 6.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (80%) |
| **Partial/Composite** | 1 (20%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getSupportedSampleRates` | `int[] getSupportedSampleRates()` | 7 | near | moderate | `OH_AVCapability_GetAudioSupportedSampleRates` | `OH_AVErrCode OH_AVCapability_GetAudioSupportedSampleRates(OH_AVCapability *capability, const int32_t **sampleRates,
                                                          uint32_t *sampleRateNum)` |
| `getBitrateRange` | `android.util.Range<java.lang.Integer> getBitrateRange()` | 6 | near | moderate | `getCameraManager` | `getCameraManager(context: Context): CameraManager` |
| `getSupportedSampleRateRanges` | `android.util.Range<java.lang.Integer>[] getSupportedSampleRateRanges()` | 6 | near | moderate | `OH_AVCapability_GetAudioSupportedSampleRates` | `OH_AVErrCode OH_AVCapability_GetAudioSupportedSampleRates(OH_AVCapability *capability, const int32_t **sampleRates,
                                                          uint32_t *sampleRateNum)` |
| `isSampleRateSupported` | `boolean isSampleRateSupported(int)` | 6 | near | moderate | `isMediaKeySystemSupported` | `isMediaKeySystemSupported(name: string, mimeType: string, level: ContentProtectionLevel): boolean` |
| `getMaxInputChannelCount` | `int getMaxInputChannelCount()` | 5 | partial | moderate | `OH_AudioCapturer_GetChannelCount` | `OH_AudioStream_Result OH_AudioCapturer_GetChannelCount(OH_AudioCapturer* capturer, int32_t* channelCount)` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaCodecInfo.AudioCapabilities`:


## Quality Gates

Before marking `android.media.MediaCodecInfo.AudioCapabilities` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
