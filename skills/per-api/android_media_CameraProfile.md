# SKILL: android.media.CameraProfile

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.CameraProfile`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.CameraProfile` |
| **Package** | `android.media` |
| **Total Methods** | 3 |
| **Avg Score** | 5.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 3 (100%) |
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
| `CameraProfile` | `CameraProfile()` | 6 | partial | moderate | `OH_CameraInput_Release` | `Camera_ErrorCode OH_CameraInput_Release(Camera_Input* cameraInput)` |
| `getJpegEncodingQualityParameter` | `static int getJpegEncodingQualityParameter(int)` | 6 | partial | moderate | `encodingType` | `encodingType: AudioEncodingType` |
| `getJpegEncodingQualityParameter` | `static int getJpegEncodingQualityParameter(int, int)` | 6 | partial | moderate | `encodingType` | `encodingType: AudioEncodingType` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.CameraProfile`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.CameraProfile` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 3 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
