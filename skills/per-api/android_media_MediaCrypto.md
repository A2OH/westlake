# SKILL: android.media.MediaCrypto

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaCrypto`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaCrypto` |
| **Package** | `android.media` |
| **Total Methods** | 6 |
| **Avg Score** | 6.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (66%) |
| **Partial/Composite** | 1 (16%) |
| **No Mapping** | 1 (16%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `release` | `void release()` | 10 | direct | trivial | `release` | `release(callback: AsyncCallback<void>): void` |
| `MediaCrypto` | `MediaCrypto(@NonNull java.util.UUID, @NonNull byte[]) throws android.media.MediaCryptoException` | 7 | near | easy | `mediaType` | `readonly mediaType: MediaType` |
| `setMediaDrmSession` | `void setMediaDrmSession(@NonNull byte[]) throws android.media.MediaCryptoException` | 7 | near | moderate | `createAVSession` | `createAVSession(context: Context, tag: string, type: AVSessionType, callback: AsyncCallback<AVSession>): void` |
| `isCryptoSchemeSupported` | `static boolean isCryptoSchemeSupported(@NonNull java.util.UUID)` | 6 | near | moderate | `isMediaKeySystemSupported` | `isMediaKeySystemSupported(name: string, mimeType: string, level: ContentProtectionLevel): boolean` |
| `requiresSecureDecoderComponent` | `boolean requiresSecureDecoderComponent(@NonNull String)` | 6 | partial | moderate | `OH_MediaKeySession_RequireSecureDecoderModule` | `Drm_ErrCode OH_MediaKeySession_RequireSecureDecoderModule(MediaKeySession *mediaKeySessoin,
    const char *mimeType, bool *status)` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `finalize` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaCrypto`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaCrypto` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 6 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
