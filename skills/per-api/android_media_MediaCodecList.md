# SKILL: android.media.MediaCodecList

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaCodecList`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaCodecList` |
| **Package** | `android.media` |
| **Total Methods** | 4 |
| **Avg Score** | 5.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 4 (100%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getCodecInfos` | `android.media.MediaCodecInfo[] getCodecInfos()` | 6 | partial | moderate | `getCount` | `getCount(): number` |
| `MediaCodecList` | `MediaCodecList(int)` | 6 | partial | moderate | `OH_AudioCodec_Stop` | `OH_AVErrCode OH_AudioCodec_Stop(OH_AVCodec *codec)` |
| `findDecoderForFormat` | `String findDecoderForFormat(android.media.MediaFormat)` | 5 | partial | moderate | `OH_VideoDecoder_Start` | `OH_AVErrCode OH_VideoDecoder_Start(OH_AVCodec *codec)` |
| `findEncoderForFormat` | `String findEncoderForFormat(android.media.MediaFormat)` | 5 | partial | moderate | `OH_VideoEncoder_Start` | `OH_AVErrCode OH_VideoEncoder_Start(OH_AVCodec *codec)` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 4 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaCodecList`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaCodecList` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
