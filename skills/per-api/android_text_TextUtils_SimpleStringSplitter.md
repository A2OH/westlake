# SKILL: android.text.TextUtils.SimpleStringSplitter

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.text.TextUtils.SimpleStringSplitter`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.text.TextUtils.SimpleStringSplitter` |
| **Package** | `android.text.TextUtils` |
| **Total Methods** | 5 |
| **Avg Score** | 2.4 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 2 (40%) |
| **No Mapping** | 3 (60%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setString` | 5 | partial | Log warning + no-op |
| `SimpleStringSplitter` | 4 | partial | throw UnsupportedOperationException |
| `hasNext` | 1 | none | Return safe default (null/false/0/empty) |
| `iterator` | 1 | none | throw UnsupportedOperationException |
| `next` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 0 methods that have score >= 5
2. Stub 5 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.text.TextUtils.SimpleStringSplitter`:


## Quality Gates

Before marking `android.text.TextUtils.SimpleStringSplitter` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
