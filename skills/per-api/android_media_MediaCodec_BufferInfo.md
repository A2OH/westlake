# SKILL: android.media.MediaCodec.BufferInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaCodec.BufferInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaCodec.BufferInfo` |
| **Package** | `android.media.MediaCodec` |
| **Total Methods** | 7 |
| **Avg Score** | 5.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (14%) |
| **Partial/Composite** | 6 (85%) |
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
| `Callback` | `MediaCodec.Callback()` | 10 | direct | trivial | `callback` | `callback: AsyncCallback<boolean>): void` |
| `BufferInfo` | `MediaCodec.BufferInfo()` | 5 | partial | moderate | `rendererInfo` | `rendererInfo?: AudioRendererInfo` |
| `onOutputBufferAvailable` | `abstract void onOutputBufferAvailable(@NonNull android.media.MediaCodec, int, @NonNull android.media.MediaCodec.BufferInfo)` | 5 | partial | moderate | `OH_VideoEncoder_FreeOutputBuffer` | `OH_AVErrCode OH_VideoEncoder_FreeOutputBuffer(OH_AVCodec *codec, uint32_t index)` |
| `set` | `void set(int, int, long, int)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `onError` | `abstract void onError(@NonNull android.media.MediaCodec, @NonNull android.media.MediaCodec.CodecException)` | 5 | partial | moderate | `on` | `on(type: 'deviceChange' | 'albumChange' | 'imageChange' | 'audioChange' | 'videoChange' | 'fileChange' | 'remoteFileChange', callback: Callback<void>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onInputBufferAvailable` | 5 | partial | Log warning + no-op |
| `onOutputFormatChanged` | 5 | partial | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaCodec.BufferInfo`:


## Quality Gates

Before marking `android.media.MediaCodec.BufferInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
