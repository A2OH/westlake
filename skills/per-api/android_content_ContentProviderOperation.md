# SKILL: android.content.ContentProviderOperation

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.ContentProviderOperation`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.ContentProviderOperation` |
| **Package** | `android.content` |
| **Total Methods** | 11 |
| **Avg Score** | 5.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 5 (45%) |
| **Partial/Composite** | 5 (45%) |
| **No Mapping** | 1 (9%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isDelete` | `boolean isDelete()` | 8 | near | easy | `deleteId` | `deleteId(uri: string): string` |
| `isUpdate` | `boolean isUpdate()` | 8 | near | easy | `updateId` | `updateId(uri: string, id: number): string` |
| `isInsert` | `boolean isInsert()` | 7 | near | moderate | `isFirst` | `isFirst(): boolean` |
| `isReadOperation` | `boolean isReadOperation()` | 6 | near | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `isWriteOperation` | `boolean isWriteOperation()` | 6 | near | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `isAssertQuery` | `boolean isAssertQuery()` | 5 | partial | moderate | `isLast` | `isLast(): boolean` |
| `isCall` | `boolean isCall()` | 5 | partial | moderate | `isFirst` | `isFirst(): boolean` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 4 | partial | Log warning + no-op |
| `isYieldAllowed` | 4 | partial | Return safe default (null/false/0/empty) |
| `isExceptionAllowed` | 4 | composite | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 7 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.content.ContentProviderOperation`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.ContentProviderOperation` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
