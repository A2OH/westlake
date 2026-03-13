# SKILL: android.media.MediaSession2

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaSession2`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaSession2` |
| **Package** | `android.media` |
| **Total Methods** | 5 |
| **Avg Score** | 6.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (80%) |
| **Partial/Composite** | 1 (20%) |
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
| `setPlaybackActive` | `void setPlaybackActive(boolean)` | 6 | near | moderate | `startAVPlayback` | `startAVPlayback(bundleName: string, assetId: string): Promise<void>` |
| `broadcastSessionCommand` | `void broadcastSessionCommand(@NonNull android.media.Session2Command, @Nullable android.os.Bundle)` | 6 | near | moderate | `OH_CaptureSession_CommitConfig` | `Camera_ErrorCode OH_CaptureSession_CommitConfig(Camera_CaptureSession* session)` |
| `cancelSessionCommand` | `void cancelSessionCommand(@NonNull android.media.MediaSession2.ControllerInfo, @NonNull Object)` | 6 | near | moderate | `OH_CaptureSession_CommitConfig` | `Camera_ErrorCode OH_CaptureSession_CommitConfig(Camera_CaptureSession* session)` |
| `isPlaybackActive` | `boolean isPlaybackActive()` | 6 | partial | moderate | `startAVPlayback` | `startAVPlayback(bundleName: string, assetId: string): Promise<void>` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaSession2`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaSession2` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
