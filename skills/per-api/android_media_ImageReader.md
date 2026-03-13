# SKILL: android.media.ImageReader

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.ImageReader`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.ImageReader` |
| **Package** | `android.media` |
| **Total Methods** | 10 |
| **Avg Score** | 6.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 5 (50%) |
| **Partial/Composite** | 5 (50%) |
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
| `close` | `void close()` | 10 | direct | trivial | `close` | `close(fd: number, callback: AsyncCallback<void>): void` |
| `getHeight` | `int getHeight()` | 8 | direct | easy | `height` | `readonly height: number` |
| `getWidth` | `int getWidth()` | 8 | near | easy | `width` | `readonly width: number` |
| `getImageFormat` | `int getImageFormat()` | 8 | near | easy | `OH_Image_Format` | `int32_t OH_Image_Format(const ImageNative* native, int32_t* format)` |
| `getMaxImages` | `int getMaxImages()` | 7 | near | moderate | `images` | `images: Array<string>, index: number, callback: AsyncCallback<void>): void` |
| `discardFreeBuffers` | `void discardFreeBuffers()` | 6 | partial | moderate | `OH_AudioCodec_FreeOutputBuffer` | `OH_AVErrCode OH_AudioCodec_FreeOutputBuffer(OH_AVCodec *codec, uint32_t index)` |
| `getSurface` | `android.view.Surface getSurface()` | 6 | partial | moderate | `OH_VideoEncoder_GetSurface` | `OH_AVErrCode OH_VideoEncoder_GetSurface(OH_AVCodec *codec, OHNativeWindow **window)` |
| `acquireLatestImage` | `android.media.Image acquireLatestImage()` | 6 | partial | moderate | `createAVImageGenerator` | `createAVImageGenerator(): Promise<AVImageGenerator>` |
| `acquireNextImage` | `android.media.Image acquireNextImage()` | 5 | partial | moderate | `OH_Image_Receiver_ReadNextImage` | `int32_t OH_Image_Receiver_ReadNextImage(const ImageReceiverNative* native, napi_value* image)` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setOnImageAvailableListener` | 5 | partial | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 9 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.ImageReader`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.ImageReader` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 9 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
