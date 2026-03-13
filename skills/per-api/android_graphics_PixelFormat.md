# SKILL: android.graphics.PixelFormat

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.PixelFormat`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.PixelFormat` |
| **Package** | `android.graphics` |
| **Total Methods** | 3 |
| **Avg Score** | 3.4 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 2 (66%) |
| **No Mapping** | 1 (33%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `formatHasAlpha` | `static boolean formatHasAlpha(int)` | 5 | partial | moderate | `OH_Drawing_PenGetAlpha` | `uint8_t OH_Drawing_PenGetAlpha(const OH_Drawing_Pen*)` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getPixelFormatInfo` | 4 | partial | Return safe default (null/false/0/empty) |
| `PixelFormat` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 1 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.PixelFormat`:


## Quality Gates

Before marking `android.graphics.PixelFormat` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 3 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
