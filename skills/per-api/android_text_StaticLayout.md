# SKILL: android.text.StaticLayout

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.text.StaticLayout`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.text.StaticLayout` |
| **Package** | `android.text` |
| **Total Methods** | 11 |
| **Avg Score** | 4.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 11 (100%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getParagraphDirection` | `int getParagraphDirection(int)` | 6 | partial | moderate | `getDragPreview` | `getDragPreview(): DragPreview` |
| `getLineStart` | `int getLineStart(int)` | 5 | partial | moderate | `napi_get_instance_data` | `NAPI_EXTERN napi_status napi_get_instance_data(napi_env env, void** data)` |
| `getLineDescent` | `int getLineDescent(int)` | 5 | partial | moderate | `napi_get_instance_data` | `NAPI_EXTERN napi_status napi_get_instance_data(napi_env env, void** data)` |

## Stub APIs (score < 5): 8 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getLineDirections` | 5 | partial | Return safe default (null/false/0/empty) |
| `getTopPadding` | 5 | partial | Return safe default (null/false/0/empty) |
| `getLineContainsTab` | 5 | partial | Return safe default (null/false/0/empty) |
| `getLineTop` | 5 | partial | Return safe default (null/false/0/empty) |
| `getBottomPadding` | 4 | partial | Return safe default (null/false/0/empty) |
| `getLineCount` | 4 | partial | Return safe default (null/false/0/empty) |
| `getEllipsisStart` | 4 | partial | Return dummy instance / no-op |
| `getEllipsisCount` | 4 | composite | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 8 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.text.StaticLayout`:


## Quality Gates

Before marking `android.text.StaticLayout` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
