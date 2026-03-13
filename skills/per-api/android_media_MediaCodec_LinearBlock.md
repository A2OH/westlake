# SKILL: android.media.MediaCodec.LinearBlock

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaCodec.LinearBlock`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaCodec.LinearBlock` |
| **Package** | `android.media.MediaCodec` |
| **Total Methods** | 4 |
| **Avg Score** | 3.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 2 (50%) |
| **No Mapping** | 2 (50%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isCodecCopyFreeCompatible` | `static boolean isCodecCopyFreeCompatible(@NonNull String[])` | 5 | partial | moderate | `OH_AudioCodec_FreeOutputBuffer` | `OH_AVErrCode OH_AudioCodec_FreeOutputBuffer(OH_AVCodec *codec, uint32_t index)` |
| `isMappable` | `boolean isMappable()` | 5 | partial | moderate | `isDirectory` | `isDirectory(callback: AsyncCallback<boolean>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `finalize` | 1 | none | throw UnsupportedOperationException |
| `recycle` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 2 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaCodec.LinearBlock`:


## Quality Gates

Before marking `android.media.MediaCodec.LinearBlock` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
