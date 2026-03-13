# SKILL: android.content.SyncAdapterType

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.SyncAdapterType`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.SyncAdapterType` |
| **Package** | `android.content` |
| **Total Methods** | 10 |
| **Avg Score** | 4.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (30%) |
| **Partial/Composite** | 4 (40%) |
| **No Mapping** | 3 (30%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `newKey` | `static android.content.SyncAdapterType newKey(String, String)` | 7 | near | moderate | `key` | `key: string` |
| `SyncAdapterType` | `SyncAdapterType(String, String, boolean, boolean)` | 6 | near | moderate | `userType` | `userType?: UserType` |
| `SyncAdapterType` | `SyncAdapterType(android.os.Parcel)` | 6 | near | moderate | `userType` | `userType?: UserType` |
| `isUserVisible` | `boolean isUserVisible()` | 6 | partial | moderate | `notifyFormsVisible` | `notifyFormsVisible(formIds: Array<string>, isVisible: boolean, callback: AsyncCallback<void>): void` |
| `getSettingsActivity` | `String getSettingsActivity()` | 5 | partial | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isAlwaysSyncable` | 4 | partial | Return safe default (null/false/0/empty) |
| `writeToParcel` | 4 | partial | Log warning + no-op |
| `allowParallelSyncs` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |
| `supportsUploading` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 5 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.content.SyncAdapterType`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.SyncAdapterType` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
