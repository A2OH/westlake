# SKILL: android.text.SpannableString

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.text.SpannableString`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.text.SpannableString` |
| **Package** | `android.text` |
| **Total Methods** | 14 |
| **Avg Score** | 4.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (7%) |
| **Partial/Composite** | 10 (71%) |
| **No Mapping** | 3 (21%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `length` | `final int length()` | 8 | direct | easy | `length` | `length?: number` |
| `getChars` | `final void getChars(int, int, char[], int)` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getSpans` | `<T> T[] getSpans(int, int, Class<T>)` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getSpanEnd` | `int getSpanEnd(Object)` | 5 | partial | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |

## Stub APIs (score < 5): 10 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `SpannableString` | 5 | partial | throw UnsupportedOperationException |
| `getSpanStart` | 5 | partial | Return dummy instance / no-op |
| `removeSpan` | 4 | partial | Log warning + no-op |
| `toString` | 4 | partial | throw UnsupportedOperationException |
| `getSpanFlags` | 4 | partial | Return safe default (null/false/0/empty) |
| `setSpan` | 4 | partial | Log warning + no-op |
| `valueOf` | 4 | composite | throw UnsupportedOperationException |
| `charAt` | 1 | none | throw UnsupportedOperationException |
| `nextSpanTransition` | 1 | none | Store callback, never fire |
| `subSequence` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 4 methods that have score >= 5
2. Stub 10 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.text.SpannableString`:


## Quality Gates

Before marking `android.text.SpannableString` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
