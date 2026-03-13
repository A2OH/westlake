# SKILL: android.media.MediaMetadata.Builder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaMetadata.Builder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaMetadata.Builder` |
| **Package** | `android.media.MediaMetadata` |
| **Total Methods** | 8 |
| **Avg Score** | 2.4 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 4 (50%) |
| **No Mapping** | 4 (50%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 8 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `Builder` | 4 | partial | throw UnsupportedOperationException |
| `Builder` | 4 | partial | throw UnsupportedOperationException |
| `putString` | 4 | partial | Log warning + no-op |
| `putLong` | 3 | composite | Log warning + no-op |
| `build` | 1 | none | throw UnsupportedOperationException |
| `putBitmap` | 1 | none | Log warning + no-op |
| `putRating` | 1 | none | Log warning + no-op |
| `putText` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 0 methods that have score >= 5
2. Stub 8 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaMetadata.Builder`:


## Quality Gates

Before marking `android.media.MediaMetadata.Builder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
