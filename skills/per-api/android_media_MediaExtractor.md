# SKILL: android.media.MediaExtractor

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaExtractor`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaExtractor` |
| **Package** | `android.media` |
| **Total Methods** | 27 |
| **Avg Score** | 5.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 10 (37%) |
| **Partial/Composite** | 15 (55%) |
| **No Mapping** | 2 (7%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 23 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `release` | `void release()` | 10 | direct | trivial | `release` | `release(callback: AsyncCallback<void>): void` |
| `getTrackCount` | `int getTrackCount()` | 8 | near | easy | `getCount` | `getCount(): number` |
| `setMediaCas` | `void setMediaCas(@NonNull android.media.MediaCas)` | 7 | near | moderate | `storeMediaAsset` | `storeMediaAsset(option: MediaAssetOption, callback: AsyncCallback<string>): void` |
| `MediaExtractor` | `MediaExtractor()` | 7 | near | moderate | `createAVMetadataExtractor` | `createAVMetadataExtractor(): Promise<AVMetadataExtractor>` |
| `selectTrack` | `void selectTrack(int)` | 6 | near | moderate | `OH_AVPlayer_SelectTrack` | `OH_AVErrCode OH_AVPlayer_SelectTrack(OH_AVPlayer *player, int32_t index)` |
| `getCachedDuration` | `long getCachedDuration()` | 6 | near | moderate | `duration` | `readonly duration: number` |
| `getCasInfo` | `android.media.MediaExtractor.CasInfo getCasInfo(int)` | 6 | near | moderate | `OH_GetImageInfo` | `int32_t OH_GetImageInfo(napi_env env, napi_value value, OhosPixelMapInfo *info)` |
| `getSampleFlags` | `int getSampleFlags()` | 6 | near | moderate | `sampleFormat` | `sampleFormat: AudioSampleFormat` |
| `readSampleData` | `int readSampleData(@NonNull java.nio.ByteBuffer, int)` | 6 | near | moderate | `sampleFormat` | `sampleFormat: AudioSampleFormat` |
| `unselectTrack` | `void unselectTrack(int)` | 6 | near | moderate | `OH_AVPlayer_SelectTrack` | `OH_AVErrCode OH_AVPlayer_SelectTrack(OH_AVPlayer *player, int32_t index)` |
| `getMetrics` | `android.os.PersistableBundle getMetrics()` | 6 | partial | moderate | `getAllPeers` | `getAllPeers(callback: AsyncCallback<Array<PeerInfo>>): void` |
| `getSampleTrackIndex` | `int getSampleTrackIndex()` | 6 | partial | moderate | `getCameraManager` | `getCameraManager(context: Context): CameraManager` |
| `getSampleTime` | `long getSampleTime()` | 6 | partial | moderate | `sampleFormat` | `sampleFormat: AudioSampleFormat` |
| `setDataSource` | `void setDataSource(@NonNull android.media.MediaDataSource) throws java.io.IOException` | 6 | partial | moderate | `OH_AVPlayer_SetFDSource` | `OH_AVErrCode OH_AVPlayer_SetFDSource(OH_AVPlayer *player, int32_t fd, int64_t offset, int64_t size)` |
| `setDataSource` | `void setDataSource(@NonNull android.content.Context, @NonNull android.net.Uri, @Nullable java.util.Map<java.lang.String,java.lang.String>) throws java.io.IOException` | 6 | partial | moderate | `OH_AVPlayer_SetFDSource` | `OH_AVErrCode OH_AVPlayer_SetFDSource(OH_AVPlayer *player, int32_t fd, int64_t offset, int64_t size)` |
| `setDataSource` | `void setDataSource(@NonNull String, @Nullable java.util.Map<java.lang.String,java.lang.String>) throws java.io.IOException` | 6 | partial | moderate | `OH_AVPlayer_SetFDSource` | `OH_AVErrCode OH_AVPlayer_SetFDSource(OH_AVPlayer *player, int32_t fd, int64_t offset, int64_t size)` |
| `setDataSource` | `void setDataSource(@NonNull String) throws java.io.IOException` | 6 | partial | moderate | `OH_AVPlayer_SetFDSource` | `OH_AVErrCode OH_AVPlayer_SetFDSource(OH_AVPlayer *player, int32_t fd, int64_t offset, int64_t size)` |
| `setDataSource` | `void setDataSource(@NonNull android.content.res.AssetFileDescriptor) throws java.io.IOException, java.lang.IllegalArgumentException, java.lang.IllegalStateException` | 6 | partial | moderate | `OH_AVPlayer_SetFDSource` | `OH_AVErrCode OH_AVPlayer_SetFDSource(OH_AVPlayer *player, int32_t fd, int64_t offset, int64_t size)` |
| `setDataSource` | `void setDataSource(@NonNull java.io.FileDescriptor) throws java.io.IOException` | 6 | partial | moderate | `OH_AVPlayer_SetFDSource` | `OH_AVErrCode OH_AVPlayer_SetFDSource(OH_AVPlayer *player, int32_t fd, int64_t offset, int64_t size)` |
| `setDataSource` | `void setDataSource(@NonNull java.io.FileDescriptor, long, long) throws java.io.IOException` | 6 | partial | moderate | `OH_AVPlayer_SetFDSource` | `OH_AVErrCode OH_AVPlayer_SetFDSource(OH_AVPlayer *player, int32_t fd, int64_t offset, int64_t size)` |
| `getSampleSize` | `long getSampleSize()` | 5 | partial | moderate | `getFileAssets` | `getFileAssets(callback: AsyncCallback<FetchFileResult>): void` |
| `getSampleCryptoInfo` | `boolean getSampleCryptoInfo(@NonNull android.media.MediaCodec.CryptoInfo)` | 5 | partial | moderate | `OH_GetImageInfo` | `int32_t OH_GetImageInfo(napi_env env, napi_value value, OhosPixelMapInfo *info)` |
| `hasCacheReachedEndOfStream` | `boolean hasCacheReachedEndOfStream()` | 5 | partial | moderate | `OH_VideoEncoder_NotifyEndOfStream` | `OH_AVErrCode OH_VideoEncoder_NotifyEndOfStream(OH_AVCodec *codec)` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getDrmInitData` | 5 | partial | Return dummy instance / no-op |
| `seekTo` | 4 | partial | throw UnsupportedOperationException |
| `advance` | 1 | none | throw UnsupportedOperationException |
| `finalize` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 23 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaExtractor`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaExtractor` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 27 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 23 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
