# SKILL: android.app.Presentation

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.Presentation`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.Presentation` |
| **Package** | `android.app` |
| **Total Methods** | 6 |
| **Avg Score** | 3.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (16%) |
| **Partial/Composite** | 3 (50%) |
| **No Mapping** | 2 (33%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getResources` | `android.content.res.Resources getResources()` | 6 | near | moderate | `getRequestInfo` | `getRequestInfo(want: Want): RequestInfo` |
| `getDisplay` | `android.view.Display getDisplay()` | 5 | partial | moderate | `getId` | `getId(uri: string): number` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onDisplayRemoved` | 5 | partial | Return safe default (null/false/0/empty) |
| `onDisplayChanged` | 4 | partial | Return safe default (null/false/0/empty) |
| `Presentation` | 1 | none | Store callback, never fire |
| `Presentation` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 2 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.Presentation`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.Presentation` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 6 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
