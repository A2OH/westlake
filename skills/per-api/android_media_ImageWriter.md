# SKILL: android.media.ImageWriter

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.ImageWriter`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.ImageWriter` |
| **Package** | `android.media` |
| **Total Methods** | 6 |
| **Avg Score** | 6.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (66%) |
| **Partial/Composite** | 2 (33%) |
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
| `close` | `void close()` | 10 | direct | trivial | `close` | `close(fd: number, callback: AsyncCallback<void>): void` |
| `getFormat` | `int getFormat()` | 7 | near | moderate | `sampleFormat` | `sampleFormat: AudioSampleFormat` |
| `getMaxImages` | `int getMaxImages()` | 7 | near | moderate | `images` | `images: Array<string>, index: number, callback: AsyncCallback<void>): void` |
| `setOnImageReleasedListener` | `void setOnImageReleasedListener(android.media.ImageWriter.OnImageReleasedListener, android.os.Handler)` | 6 | near | moderate | `OH_Image_Release` | `int32_t OH_Image_Release(ImageNative* native)` |
| `queueInputImage` | `void queueInputImage(android.media.Image)` | 5 | partial | moderate | `TestGetImageInfo` | `static napi_value TestGetImageInfo(napi_env env, napi_callback_info info)` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `dequeueInputImage` | 5 | partial | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.ImageWriter`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.ImageWriter` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 6 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
