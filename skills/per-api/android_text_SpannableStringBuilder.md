# SKILL: android.text.SpannableStringBuilder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.text.SpannableStringBuilder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.text.SpannableStringBuilder` |
| **Package** | `android.text` |
| **Total Methods** | 29 |
| **Avg Score** | 4.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 8 (27%) |
| **Partial/Composite** | 14 (48%) |
| **No Mapping** | 7 (24%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 15 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `append` | `android.text.SpannableStringBuilder append(CharSequence)` | 8 | direct | easy | `append` | `append?: boolean` |
| `append` | `android.text.SpannableStringBuilder append(CharSequence, Object, int)` | 8 | direct | easy | `append` | `append?: boolean` |
| `append` | `android.text.SpannableStringBuilder append(CharSequence, int, int)` | 8 | direct | easy | `append` | `append?: boolean` |
| `append` | `android.text.SpannableStringBuilder append(char)` | 8 | direct | easy | `append` | `append?: boolean` |
| `clear` | `void clear()` | 8 | direct | easy | `clear` | `clear(): void` |
| `length` | `int length()` | 8 | direct | easy | `length` | `length?: number` |
| `replace` | `android.text.SpannableStringBuilder replace(int, int, CharSequence)` | 8 | direct | easy | `replace` | `replace(options: RouterOptions): void` |
| `replace` | `android.text.SpannableStringBuilder replace(int, int, CharSequence, int, int)` | 8 | direct | easy | `replace` | `replace(options: RouterOptions): void` |
| `getChars` | `void getChars(int, int, char[], int)` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getSpans` | `<T> T[] getSpans(int, int, @Nullable Class<T>)` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getSpanEnd` | `int getSpanEnd(Object)` | 5 | partial | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |
| `SpannableStringBuilder` | `SpannableStringBuilder()` | 5 | partial | moderate | `createFromBuilder` | `createFromBuilder(builder: CustomBuilder, callback: AsyncCallback<image.PixelMap>): void` |
| `SpannableStringBuilder` | `SpannableStringBuilder(CharSequence)` | 5 | partial | moderate | `createFromBuilder` | `createFromBuilder(builder: CustomBuilder, callback: AsyncCallback<image.PixelMap>): void` |
| `SpannableStringBuilder` | `SpannableStringBuilder(CharSequence, int, int)` | 5 | partial | moderate | `createFromBuilder` | `createFromBuilder(builder: CustomBuilder, callback: AsyncCallback<image.PixelMap>): void` |
| `getFilters` | `android.text.InputFilter[] getFilters()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |

## Stub APIs (score < 5): 14 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getSpanStart` | 5 | partial | Return dummy instance / no-op |
| `removeSpan` | 4 | partial | Log warning + no-op |
| `getSpanFlags` | 4 | partial | Return safe default (null/false/0/empty) |
| `setSpan` | 4 | partial | Log warning + no-op |
| `setFilters` | 4 | composite | Log warning + no-op |
| `valueOf` | 4 | composite | throw UnsupportedOperationException |
| `getTextWatcherDepth` | 3 | composite | Return safe default (null/false/0/empty) |
| `charAt` | 1 | none | throw UnsupportedOperationException |
| `clearSpans` | 1 | none | throw UnsupportedOperationException |
| `delete` | 1 | none | throw UnsupportedOperationException |
| `insert` | 1 | none | throw UnsupportedOperationException |
| `insert` | 1 | none | throw UnsupportedOperationException |
| `nextSpanTransition` | 1 | none | Store callback, never fire |
| `subSequence` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 15 methods that have score >= 5
2. Stub 14 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.text.SpannableStringBuilder`:


## Quality Gates

Before marking `android.text.SpannableStringBuilder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 29 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 15 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
