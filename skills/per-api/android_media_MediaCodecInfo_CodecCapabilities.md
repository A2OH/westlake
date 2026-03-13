# SKILL: android.media.MediaCodecInfo.CodecCapabilities

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaCodecInfo.CodecCapabilities`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaCodecInfo.CodecCapabilities` |
| **Package** | `android.media.MediaCodecInfo` |
| **Total Methods** | 11 |
| **Avg Score** | 6.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 5 (45%) |
| **Partial/Composite** | 6 (54%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 9 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getMimeType` | `String getMimeType()` | 8 | direct | easy | `mimeType` | `mimeType: string` |
| `getMaxSupportedInstances` | `int getMaxSupportedInstances()` | 8 | near | easy | `OH_AVCapability_GetMaxSupportedInstances` | `int32_t OH_AVCapability_GetMaxSupportedInstances(OH_AVCapability *capability)` |
| `isFeatureSupported` | `boolean isFeatureSupported(String)` | 7 | near | moderate | `isMediaKeySystemSupported` | `isMediaKeySystemSupported(name: string, mimeType: string, level: ContentProtectionLevel): boolean` |
| `getAudioCapabilities` | `android.media.MediaCodecInfo.AudioCapabilities getAudioCapabilities()` | 7 | near | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |
| `isFormatSupported` | `boolean isFormatSupported(android.media.MediaFormat)` | 7 | near | moderate | `isMediaKeySystemSupported` | `isMediaKeySystemSupported(name: string, mimeType: string, level: ContentProtectionLevel): boolean` |
| `createFromProfileLevel` | `static android.media.MediaCodecInfo.CodecCapabilities createFromProfileLevel(String, int, int)` | 6 | partial | moderate | `OH_ImageSource_CreateFromRawFile` | `int32_t OH_ImageSource_CreateFromRawFile(napi_env env, RawFileDescriptor rawFile,
    struct OhosImageSourceOps* ops, napi_value *res)` |
| `getDefaultFormat` | `android.media.MediaFormat getDefaultFormat()` | 5 | partial | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |
| `getEncoderCapabilities` | `android.media.MediaCodecInfo.EncoderCapabilities getEncoderCapabilities()` | 5 | partial | moderate | `OH_AVCapability_GetEncoderComplexityRange` | `OH_AVErrCode OH_AVCapability_GetEncoderComplexityRange(OH_AVCapability *capability, OH_AVRange *complexityRange)` |
| `getVideoCapabilities` | `android.media.MediaCodecInfo.VideoCapabilities getVideoCapabilities()` | 5 | partial | moderate | `OH_CaptureSession_GetVideoStabilizationMode` | `Camera_ErrorCode OH_CaptureSession_GetVideoStabilizationMode(Camera_CaptureSession* session,
    Camera_VideoStabilizationMode* mode)` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isFeatureRequired` | 4 | partial | Return safe default (null/false/0/empty) |
| `CodecCapabilities` | 4 | partial | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 9 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaCodecInfo.CodecCapabilities`:


## Quality Gates

Before marking `android.media.MediaCodecInfo.CodecCapabilities` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 9 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
