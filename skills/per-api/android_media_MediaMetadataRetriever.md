# SKILL: android.media.MediaMetadataRetriever

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaMetadataRetriever`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaMetadataRetriever` |
| **Package** | `android.media` |
| **Total Methods** | 9 |
| **Avg Score** | 6.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (22%) |
| **Partial/Composite** | 7 (77%) |
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
| `release` | `void release()` | 10 | direct | trivial | `release` | `release(callback: AsyncCallback<void>): void` |
| `MediaMetadataRetriever` | `MediaMetadataRetriever()` | 6 | partial | moderate | `createAVMetadataExtractor` | `createAVMetadataExtractor(): Promise<AVMetadataExtractor>` |
| `setDataSource` | `void setDataSource(String) throws java.lang.IllegalArgumentException` | 6 | partial | moderate | `OH_AVPlayer_SetFDSource` | `OH_AVErrCode OH_AVPlayer_SetFDSource(OH_AVPlayer *player, int32_t fd, int64_t offset, int64_t size)` |
| `setDataSource` | `void setDataSource(String, java.util.Map<java.lang.String,java.lang.String>) throws java.lang.IllegalArgumentException` | 6 | partial | moderate | `OH_AVPlayer_SetFDSource` | `OH_AVErrCode OH_AVPlayer_SetFDSource(OH_AVPlayer *player, int32_t fd, int64_t offset, int64_t size)` |
| `setDataSource` | `void setDataSource(java.io.FileDescriptor, long, long) throws java.lang.IllegalArgumentException` | 6 | partial | moderate | `OH_AVPlayer_SetFDSource` | `OH_AVErrCode OH_AVPlayer_SetFDSource(OH_AVPlayer *player, int32_t fd, int64_t offset, int64_t size)` |
| `setDataSource` | `void setDataSource(java.io.FileDescriptor) throws java.lang.IllegalArgumentException` | 6 | partial | moderate | `OH_AVPlayer_SetFDSource` | `OH_AVErrCode OH_AVPlayer_SetFDSource(OH_AVPlayer *player, int32_t fd, int64_t offset, int64_t size)` |
| `setDataSource` | `void setDataSource(android.content.Context, android.net.Uri) throws java.lang.IllegalArgumentException, java.lang.SecurityException` | 6 | partial | moderate | `OH_AVPlayer_SetFDSource` | `OH_AVErrCode OH_AVPlayer_SetFDSource(OH_AVPlayer *player, int32_t fd, int64_t offset, int64_t size)` |
| `setDataSource` | `void setDataSource(android.media.MediaDataSource) throws java.lang.IllegalArgumentException` | 6 | partial | moderate | `OH_AVPlayer_SetFDSource` | `OH_AVErrCode OH_AVPlayer_SetFDSource(OH_AVPlayer *player, int32_t fd, int64_t offset, int64_t size)` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 9 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaMetadataRetriever`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaMetadataRetriever` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 9 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
