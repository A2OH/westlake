# SKILL: android.media.MediaCodecInfo.VideoCapabilities

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaCodecInfo.VideoCapabilities`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaCodecInfo.VideoCapabilities` |
| **Package** | `android.media.MediaCodecInfo` |
| **Total Methods** | 11 |
| **Avg Score** | 6.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 7 (63%) |
| **Partial/Composite** | 4 (36%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 11 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isSizeSupported` | `boolean isSizeSupported(int, int)` | 6 | near | moderate | `isMediaKeySystemSupported` | `isMediaKeySystemSupported(name: string, mimeType: string, level: ContentProtectionLevel): boolean` |
| `getBitrateRange` | `android.util.Range<java.lang.Integer> getBitrateRange()` | 6 | near | moderate | `getCameraManager` | `getCameraManager(context: Context): CameraManager` |
| `getSupportedFrameRates` | `android.util.Range<java.lang.Integer> getSupportedFrameRates()` | 6 | near | moderate | `OH_ImageSource_GetSupportedFormats` | `int32_t OH_ImageSource_GetSupportedFormats(struct OhosImageSourceSupportedFormatList* res)` |
| `areSizeAndRateSupported` | `boolean areSizeAndRateSupported(int, int, double)` | 6 | near | moderate | `OH_AVCapability_AreVideoSizeAndFrameRateSupported` | `bool OH_AVCapability_AreVideoSizeAndFrameRateSupported(OH_AVCapability *capability, int32_t width, int32_t height,
                                                       int32_t frameRate)` |
| `getHeightAlignment` | `int getHeightAlignment()` | 6 | near | moderate | `OH_AVCapability_GetVideoHeightAlignment` | `OH_AVErrCode OH_AVCapability_GetVideoHeightAlignment(OH_AVCapability *capability, int32_t *heightAlignment)` |
| `getWidthAlignment` | `int getWidthAlignment()` | 6 | near | moderate | `OH_AVCapability_GetVideoWidthAlignment` | `OH_AVErrCode OH_AVCapability_GetVideoWidthAlignment(OH_AVCapability *capability, int32_t *widthAlignment)` |
| `getSupportedFrameRatesFor` | `android.util.Range<java.lang.Double> getSupportedFrameRatesFor(int, int)` | 6 | near | moderate | `OH_ImageSource_GetSupportedFormats` | `int32_t OH_ImageSource_GetSupportedFormats(struct OhosImageSourceSupportedFormatList* res)` |
| `getSupportedWidthsFor` | `android.util.Range<java.lang.Integer> getSupportedWidthsFor(int)` | 5 | partial | moderate | `OH_ImageSource_GetSupportedFormats` | `int32_t OH_ImageSource_GetSupportedFormats(struct OhosImageSourceSupportedFormatList* res)` |
| `getSupportedWidths` | `android.util.Range<java.lang.Integer> getSupportedWidths()` | 5 | partial | moderate | `OH_ImageSource_GetSupportedFormats` | `int32_t OH_ImageSource_GetSupportedFormats(struct OhosImageSourceSupportedFormatList* res)` |
| `getSupportedHeightsFor` | `android.util.Range<java.lang.Integer> getSupportedHeightsFor(int)` | 5 | partial | moderate | `OH_ImageSource_GetSupportedFormats` | `int32_t OH_ImageSource_GetSupportedFormats(struct OhosImageSourceSupportedFormatList* res)` |
| `getSupportedHeights` | `android.util.Range<java.lang.Integer> getSupportedHeights()` | 5 | partial | moderate | `OH_ImageSource_GetSupportedFormats` | `int32_t OH_ImageSource_GetSupportedFormats(struct OhosImageSourceSupportedFormatList* res)` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 11 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaCodecInfo.VideoCapabilities`:


## Quality Gates

Before marking `android.media.MediaCodecInfo.VideoCapabilities` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 11 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
