# SKILL: android.media.MediaCodec.CryptoInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaCodec.CryptoInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaCodec.CryptoInfo` |
| **Package** | `android.media.MediaCodec` |
| **Total Methods** | 3 |
| **Avg Score** | 5.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 3 (100%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `CryptoInfo` | `MediaCodec.CryptoInfo()` | 5 | partial | moderate | `rendererInfo` | `rendererInfo?: AudioRendererInfo` |
| `set` | `void set(int, @NonNull int[], @NonNull int[], @NonNull byte[], @NonNull byte[], int)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `setPattern` | `void setPattern(android.media.MediaCodec.CryptoInfo.Pattern)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaCodec.CryptoInfo`:


## Quality Gates

Before marking `android.media.MediaCodec.CryptoInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 3 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
