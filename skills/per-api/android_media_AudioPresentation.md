# SKILL: android.media.AudioPresentation

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.AudioPresentation`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.AudioPresentation` |
| **Package** | `android.media` |
| **Total Methods** | 8 |
| **Avg Score** | 4.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (25%) |
| **Partial/Composite** | 5 (62%) |
| **No Mapping** | 1 (12%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getLabels` | `java.util.Map<java.util.Locale,java.lang.String> getLabels()` | 7 | near | moderate | `getAlbums` | `getAlbums(options: MediaFetchOptions, callback: AsyncCallback<Array<Album>>): void` |
| `getLocale` | `java.util.Locale getLocale()` | 6 | near | moderate | `getAllPeers` | `getAllPeers(callback: AsyncCallback<Array<PeerInfo>>): void` |
| `hasAudioDescription` | `boolean hasAudioDescription()` | 6 | partial | moderate | `OH_AudioCodec_Start` | `OH_AVErrCode OH_AudioCodec_Start(OH_AVCodec *codec)` |
| `getPresentationId` | `int getPresentationId()` | 5 | partial | moderate | `sessionId` | `readonly sessionId: string` |
| `getMasteringIndication` | `int getMasteringIndication()` | 5 | partial | moderate | `getLastObject` | `getLastObject(callback: AsyncCallback<FileAsset>): void` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getProgramId` | 5 | partial | Return safe default (null/false/0/empty) |
| `hasDialogueEnhancement` | 2 | composite | Return safe default (null/false/0/empty) |
| `hasSpokenSubtitles` | 2 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.AudioPresentation`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.AudioPresentation` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
