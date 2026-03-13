# SKILL: android.media.AudioDeviceCallback

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.AudioDeviceCallback`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.AudioDeviceCallback` |
| **Package** | `android.media` |
| **Total Methods** | 3 |
| **Avg Score** | 6.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (100%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `AudioDeviceCallback` | `AudioDeviceCallback()` | 7 | near | easy | `OH_AudioDecoder_SetCallback` | `OH_AVErrCode OH_AudioDecoder_SetCallback(OH_AVCodec *codec, OH_AVCodecAsyncCallback callback, void *userData)` |
| `onAudioDevicesAdded` | `void onAudioDevicesAdded(android.media.AudioDeviceInfo[])` | 6 | near | moderate | `OH_AudioCodec_Reset` | `OH_AVErrCode OH_AudioCodec_Reset(OH_AVCodec *codec)` |
| `onAudioDevicesRemoved` | `void onAudioDevicesRemoved(android.media.AudioDeviceInfo[])` | 6 | near | moderate | `OH_AudioCodec_Destroy` | `OH_AVErrCode OH_AudioCodec_Destroy(OH_AVCodec *codec)` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.AudioDeviceCallback`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.AudioDeviceCallback` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 3 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
