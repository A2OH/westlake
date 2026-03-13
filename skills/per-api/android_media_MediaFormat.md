# SKILL: android.media.MediaFormat

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaFormat`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaFormat` |
| **Package** | `android.media` |
| **Total Methods** | 20 |
| **Avg Score** | 5.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 8 (40%) |
| **Partial/Composite** | 11 (55%) |
| **No Mapping** | 1 (5%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 16 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `MediaFormat` | `MediaFormat()` | 7 | near | moderate | `sampleFormat` | `sampleFormat: AudioSampleFormat` |
| `MediaFormat` | `MediaFormat(@NonNull android.media.MediaFormat)` | 7 | near | moderate | `sampleFormat` | `sampleFormat: AudioSampleFormat` |
| `getFloat` | `float getFloat(@NonNull String)` | 7 | near | moderate | `getFileAssets` | `getFileAssets(callback: AsyncCallback<FetchFileResult>): void` |
| `getFloat` | `float getFloat(@NonNull String, float)` | 7 | near | moderate | `getFileAssets` | `getFileAssets(callback: AsyncCallback<FetchFileResult>): void` |
| `getLong` | `long getLong(@NonNull String)` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `getLong` | `long getLong(@NonNull String, long)` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `getInteger` | `int getInteger(@NonNull String)` | 6 | near | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |
| `getInteger` | `int getInteger(@NonNull String, int)` | 6 | near | moderate | `getAudioManager` | `getAudioManager(): AudioManager` |
| `setFeatureEnabled` | `void setFeatureEnabled(@NonNull String, boolean)` | 6 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `getFeatureEnabled` | `boolean getFeatureEnabled(@NonNull String)` | 6 | partial | moderate | `getThumbnail` | `getThumbnail(callback: AsyncCallback<image.PixelMap>): void` |
| `getValueTypeForKey` | `int getValueTypeForKey(@NonNull String)` | 6 | partial | moderate | `getAllPeers` | `getAllPeers(callback: AsyncCallback<Array<PeerInfo>>): void` |
| `setByteBuffer` | `void setByteBuffer(@NonNull String, @Nullable java.nio.ByteBuffer)` | 5 | partial | moderate | `OH_AVFormat_SetBuffer` | `bool OH_AVFormat_SetBuffer(struct OH_AVFormat *format, const char *key, const uint8_t *addr, size_t size)` |
| `setString` | `void setString(@NonNull String, @Nullable String)` | 5 | partial | moderate | `OH_AVFormat_SetStringValue` | `bool OH_AVFormat_SetStringValue(struct OH_AVFormat *format, const char *key, const char *value)` |
| `setFloat` | `void setFloat(@NonNull String, float)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `setInteger` | `void setInteger(@NonNull String, int)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `setLong` | `void setLong(@NonNull String, long)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `removeKey` | 5 | partial | Log warning + no-op |
| `removeFeature` | 4 | composite | Log warning + no-op |
| `containsKey` | 4 | composite | Store callback, never fire |
| `containsFeature` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 16 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaFormat`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaFormat` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 20 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 16 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
