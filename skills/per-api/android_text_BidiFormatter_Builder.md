# SKILL: android.text.BidiFormatter.Builder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.text.BidiFormatter.Builder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.text.BidiFormatter.Builder` |
| **Package** | `android.text.BidiFormatter` |
| **Total Methods** | 6 |
| **Avg Score** | 3.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 4 (66%) |
| **No Mapping** | 2 (33%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Builder` | `BidiFormatter.Builder()` | 6 | partial | moderate | `createFromBuilder` | `createFromBuilder(builder: CustomBuilder, callback: AsyncCallback<image.PixelMap>): void` |
| `Builder` | `BidiFormatter.Builder(boolean)` | 6 | partial | moderate | `createFromBuilder` | `createFromBuilder(builder: CustomBuilder, callback: AsyncCallback<image.PixelMap>): void` |
| `Builder` | `BidiFormatter.Builder(java.util.Locale)` | 6 | partial | moderate | `createFromBuilder` | `createFromBuilder(builder: CustomBuilder, callback: AsyncCallback<image.PixelMap>): void` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setTextDirectionHeuristic` | 3 | composite | Return safe default (null/false/0/empty) |
| `build` | 1 | none | throw UnsupportedOperationException |
| `stereoReset` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.text.BidiFormatter.Builder`:


## Quality Gates

Before marking `android.text.BidiFormatter.Builder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 6 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
