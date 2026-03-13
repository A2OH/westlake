# SKILL: android.text.Editable

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.text.Editable`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.text.Editable` |
| **Package** | `android.text` |
| **Total Methods** | 12 |
| **Avg Score** | 5.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 6 (50%) |
| **Partial/Composite** | 2 (16%) |
| **No Mapping** | 4 (33%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `append` | `android.text.Editable append(CharSequence)` | 8 | direct | easy | `append` | `append?: boolean` |
| `append` | `android.text.Editable append(CharSequence, int, int)` | 8 | direct | easy | `append` | `append?: boolean` |
| `append` | `android.text.Editable append(char)` | 8 | direct | easy | `append` | `append?: boolean` |
| `clear` | `void clear()` | 8 | direct | easy | `clear` | `clear(): void` |
| `replace` | `android.text.Editable replace(int, int, CharSequence, int, int)` | 8 | direct | easy | `replace` | `replace(options: RouterOptions): void` |
| `replace` | `android.text.Editable replace(int, int, CharSequence)` | 8 | direct | easy | `replace` | `replace(options: RouterOptions): void` |
| `getFilters` | `android.text.InputFilter[] getFilters()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setFilters` | 4 | composite | Log warning + no-op |
| `clearSpans` | 1 | none | throw UnsupportedOperationException |
| `delete` | 1 | none | throw UnsupportedOperationException |
| `insert` | 1 | none | throw UnsupportedOperationException |
| `insert` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 7 methods that have score >= 5
2. Stub 5 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.text.Editable`:


## Quality Gates

Before marking `android.text.Editable` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 12 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
