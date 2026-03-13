# SKILL: android.media.MediaMuxer

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaMuxer`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaMuxer` |
| **Package** | `android.media` |
| **Total Methods** | 9 |
| **Avg Score** | 6.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 6 (66%) |
| **Partial/Composite** | 3 (33%) |
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
| `release` | `void release()` | 10 | direct | trivial | `release` | `release(callback: AsyncCallback<void>): void` |
| `setOrientationHint` | `void setOrientationHint(int)` | 8 | near | easy | `orientation` | `orientation: number` |
| `start` | `void start()` | 7 | near | easy | `start` | `start(sinkDeviceDescriptor: string, srcInputDeviceId: number, callback: AsyncCallback<void>): void` |
| `stop` | `void stop()` | 7 | near | easy | `stop` | `stop(callback: AsyncCallback<void>): void` |
| `MediaMuxer` | `MediaMuxer(@NonNull String, int) throws java.io.IOException` | 6 | near | moderate | `mediaType` | `readonly mediaType: MediaType` |
| `MediaMuxer` | `MediaMuxer(@NonNull java.io.FileDescriptor, int) throws java.io.IOException` | 6 | near | moderate | `mediaType` | `readonly mediaType: MediaType` |
| `writeSampleData` | `void writeSampleData(int, @NonNull java.nio.ByteBuffer, @NonNull android.media.MediaCodec.BufferInfo)` | 6 | partial | moderate | `OH_AVMuxer_WriteSample` | `OH_AVErrCode OH_AVMuxer_WriteSample(OH_AVMuxer *muxer, uint32_t trackIndex,
    OH_AVMemory *sample, OH_AVCodecBufferAttr info)` |
| `addTrack` | `int addTrack(@NonNull android.media.MediaFormat)` | 6 | partial | moderate | `OH_AVMuxer_AddTrack` | `OH_AVErrCode OH_AVMuxer_AddTrack(OH_AVMuxer *muxer, int32_t *trackIndex, OH_AVFormat *trackFormat)` |
| `setLocation` | `void setLocation(float, float)` | 5 | partial | moderate | `OH_AVMuxer_SetRotation` | `OH_AVErrCode OH_AVMuxer_SetRotation(OH_AVMuxer *muxer, int32_t rotation)` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 9 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaMuxer`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaMuxer` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 9 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
