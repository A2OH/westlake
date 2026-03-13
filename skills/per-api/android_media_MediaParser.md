# SKILL: android.media.MediaParser

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaParser`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaParser` |
| **Package** | `android.media` |
| **Total Methods** | 4 |
| **Avg Score** | 5.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (25%) |
| **Partial/Composite** | 2 (50%) |
| **No Mapping** | 1 (25%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `release` | `void release()` | 10 | direct | trivial | `release` | `release(callback: AsyncCallback<void>): void` |
| `supportsParameter` | `boolean supportsParameter(@NonNull String)` | 5 | partial | moderate | `OH_AVBuffer_SetParameter` | `OH_AVErrCode OH_AVBuffer_SetParameter(OH_AVBuffer *buffer, const OH_AVFormat *format)` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `seek` | 4 | partial | throw UnsupportedOperationException |
| `advance` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 2 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaParser`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaParser` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
