# SKILL: android.content.AsyncQueryHandler

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.AsyncQueryHandler`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.AsyncQueryHandler` |
| **Package** | `android.content` |
| **Total Methods** | 11 |
| **Avg Score** | 5.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 8 (72%) |
| **Partial/Composite** | 3 (27%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 10 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `createHandler` | `android.os.Handler createHandler(android.os.Looper)` | 6 | near | moderate | `createDataShareHelper` | `createDataShareHelper(context: Context, uri: string, callback: AsyncCallback<DataShareHelper>): void` |
| `cancelOperation` | `final void cancelOperation(int)` | 6 | near | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `startDelete` | `final void startDelete(int, Object, android.net.Uri, String, String[])` | 6 | near | moderate | `deleteId` | `deleteId(uri: string): string` |
| `startUpdate` | `final void startUpdate(int, Object, android.net.Uri, android.content.ContentValues, String, String[])` | 6 | near | moderate | `updateId` | `updateId(uri: string, id: number): string` |
| `onDeleteComplete` | `void onDeleteComplete(int, Object, int)` | 6 | near | moderate | `deleteEntries` | `deleteEntries: Entry[]` |
| `onInsertComplete` | `void onInsertComplete(int, Object, android.net.Uri)` | 6 | near | moderate | `insertEntries` | `insertEntries: Entry[]` |
| `onUpdateComplete` | `void onUpdateComplete(int, Object, int)` | 6 | near | moderate | `updateEntries` | `updateEntries: Entry[]` |
| `startInsert` | `final void startInsert(int, Object, android.net.Uri, android.content.ContentValues)` | 6 | near | moderate | `startAbility` | `startAbility(parameter: StartAbilityParameter, callback: AsyncCallback<number>): void` |
| `startQuery` | `void startQuery(int, Object, android.net.Uri, String[], String, String[], String)` | 5 | partial | moderate | `startAbility` | `startAbility(parameter: StartAbilityParameter, callback: AsyncCallback<number>): void` |
| `onQueryComplete` | `void onQueryComplete(int, Object, android.database.Cursor)` | 5 | partial | moderate | `queryParticipants` | `queryParticipants(sharingResource: string, callback: AsyncCallback<Result<Array<Participant>>>): void` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `AsyncQueryHandler` | 5 | partial | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 10 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.content.AsyncQueryHandler`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.AsyncQueryHandler` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 10 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
