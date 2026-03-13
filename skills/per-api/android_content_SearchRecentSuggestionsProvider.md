# SKILL: android.content.SearchRecentSuggestionsProvider

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.SearchRecentSuggestionsProvider`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.SearchRecentSuggestionsProvider` |
| **Package** | `android.content` |
| **Total Methods** | 8 |
| **Avg Score** | 6.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 6 (75%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 2 (25%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `delete` | `int delete(android.net.Uri, String, String[])` | 9 | direct | easy | `deleteId` | `deleteId(uri: string): string` |
| `onCreate` | `boolean onCreate()` | 9 | direct | easy | `create` | `create(context: Context, source: object): DataObject` |
| `insert` | `android.net.Uri insert(android.net.Uri, android.content.ContentValues)` | 8 | near | easy | `insertData` | `insertData(options: Options, data: UnifiedData, callback: AsyncCallback<string>): void` |
| `getType` | `String getType(android.net.Uri)` | 7 | near | easy | `type` | `type: ValueType` |
| `query` | `android.database.Cursor query(android.net.Uri, String[], String, String[], String)` | 7 | near | easy | `query` | `query(faultType: FaultType, callback: AsyncCallback<Array<FaultLogInfo>>): void` |
| `update` | `int update(android.net.Uri, android.content.ContentValues, String, String[])` | 7 | near | easy | `update` | `update(query: AssetMap, attributesToUpdate: AssetMap): Promise<void>` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `SearchRecentSuggestionsProvider` | 1 | none | Store callback, never fire |
| `setupSuggestions` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 6 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.content.SearchRecentSuggestionsProvider`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.SearchRecentSuggestionsProvider` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
