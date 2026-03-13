# SKILL: android.media.Image

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.Image`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.Image` |
| **Package** | `android.media` |
| **Total Methods** | 12 |
| **Avg Score** | 6.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 7 (58%) |
| **Partial/Composite** | 5 (41%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 12 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `close` | `abstract void close()` | 10 | direct | trivial | `close` | `close(fd: number, callback: AsyncCallback<void>): void` |
| `getHeight` | `abstract int getHeight()` | 8 | direct | easy | `height` | `readonly height: number` |
| `getWidth` | `abstract int getWidth()` | 8 | near | easy | `width` | `readonly width: number` |
| `getFormat` | `abstract int getFormat()` | 7 | near | moderate | `sampleFormat` | `sampleFormat: AudioSampleFormat` |
| `getCropRect` | `android.graphics.Rect getCropRect()` | 6 | near | moderate | `getFirstObject` | `getFirstObject(callback: AsyncCallback<FileAsset>): void` |
| `getPlanes` | `abstract android.media.Image.Plane[] getPlanes()` | 6 | near | moderate | `getFileAssets` | `getFileAssets(callback: AsyncCallback<FetchFileResult>): void` |
| `getBuffer` | `abstract java.nio.ByteBuffer getBuffer()` | 6 | near | moderate | `OH_AVFormat_GetBuffer` | `bool OH_AVFormat_GetBuffer(struct OH_AVFormat *format, const char *key, uint8_t **addr, size_t *size)` |
| `getTimestamp` | `abstract long getTimestamp()` | 6 | partial | moderate | `OH_AudioCapturer_GetTimestamp` | `OH_AudioStream_Result OH_AudioCapturer_GetTimestamp(OH_AudioCapturer* capturer,
    clockid_t clockId, int64_t* framePosition, int64_t* timestamp)` |
| `getRowStride` | `abstract int getRowStride()` | 6 | partial | moderate | `getPositionObject` | `getPositionObject(index: number, callback: AsyncCallback<FileAsset>): void` |
| `setTimestamp` | `void setTimestamp(long)` | 5 | partial | moderate | `OH_AudioCapturer_GetTimestamp` | `OH_AudioStream_Result OH_AudioCapturer_GetTimestamp(OH_AudioCapturer* capturer,
    clockid_t clockId, int64_t* framePosition, int64_t* timestamp)` |
| `getPixelStride` | `abstract int getPixelStride()` | 5 | partial | moderate | `getLastObject` | `getLastObject(callback: AsyncCallback<FileAsset>): void` |
| `setCropRect` | `void setCropRect(android.graphics.Rect)` | 5 | partial | moderate | `OH_Image_ClipRect` | `int32_t OH_Image_ClipRect(const ImageNative* native, struct OhosImageRect* rect)` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 12 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.Image`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.Image` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 12 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 12 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
