# SKILL: android.media.AudioFormat

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.AudioFormat`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.AudioFormat` |
| **Package** | `android.media` |
| **Total Methods** | 7 |
| **Avg Score** | 5.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (57%) |
| **Partial/Composite** | 2 (28%) |
| **No Mapping** | 1 (14%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getSampleRate` | `int getSampleRate()` | 7 | near | easy | `samplingRate` | `samplingRate: AudioSamplingRate` |
| `getChannelCount` | `int getChannelCount()` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `getEncoding` | `int getEncoding()` | 7 | near | moderate | `encodingType` | `encodingType: AudioEncodingType` |
| `getChannelMask` | `int getChannelMask()` | 6 | near | moderate | `getAlbums` | `getAlbums(options: MediaFetchOptions, callback: AsyncCallback<Array<Album>>): void` |
| `getChannelIndexMask` | `int getChannelIndexMask()` | 5 | partial | moderate | `getAlbums` | `getAlbums(options: MediaFetchOptions, callback: AsyncCallback<Array<Album>>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 4 | partial | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.AudioFormat`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.AudioFormat` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
