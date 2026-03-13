# SKILL: android.media.MediaCodecInfo.EncoderCapabilities

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaCodecInfo.EncoderCapabilities`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaCodecInfo.EncoderCapabilities` |
| **Package** | `android.media.MediaCodecInfo` |
| **Total Methods** | 3 |
| **Avg Score** | 6.4 |
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
| `isBitrateModeSupported` | `boolean isBitrateModeSupported(int)` | 7 | near | moderate | `isMediaKeySystemSupported` | `isMediaKeySystemSupported(name: string, mimeType: string, level: ContentProtectionLevel): boolean` |
| `getComplexityRange` | `android.util.Range<java.lang.Integer> getComplexityRange()` | 6 | near | moderate | `getCameraManager` | `getCameraManager(context: Context): CameraManager` |
| `getQualityRange` | `android.util.Range<java.lang.Integer> getQualityRange()` | 6 | near | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaCodecInfo.EncoderCapabilities`:


## Quality Gates

Before marking `android.media.MediaCodecInfo.EncoderCapabilities` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 3 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
