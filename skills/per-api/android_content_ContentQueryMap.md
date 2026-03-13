# SKILL: android.content.ContentQueryMap

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.ContentQueryMap`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.ContentQueryMap` |
| **Package** | `android.content` |
| **Total Methods** | 6 |
| **Avg Score** | 5.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (66%) |
| **Partial/Composite** | 1 (16%) |
| **No Mapping** | 1 (16%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `close` | `void close()` | 8 | direct | easy | `close` | `close(file: number | File): Promise<void>` |
| `getValues` | `android.content.ContentValues getValues(String)` | 7 | near | moderate | `getArguments` | `getArguments(): AbilityDelegatorArgs` |
| `setKeepUpdated` | `void setKeepUpdated(boolean)` | 7 | near | moderate | `updated` | `updated: Array<string> | Array<number>` |
| `getRows` | `java.util.Map<java.lang.String,android.content.ContentValues> getRows()` | 6 | near | moderate | `getWindow` | `getWindow(callback: AsyncCallback<window.Window>): void` |
| `ContentQueryMap` | `ContentQueryMap(android.database.Cursor, String, boolean, android.os.Handler)` | 5 | partial | moderate | `queryData` | `queryData(options: Options, callback: AsyncCallback<Array<UnifiedData>>): void` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `requery` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.content.ContentQueryMap`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.ContentQueryMap` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 6 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
