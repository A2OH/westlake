# SKILL: android.content.IntentSender

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.IntentSender`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.IntentSender` |
| **Package** | `android.content` |
| **Total Methods** | 9 |
| **Avg Score** | 4.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (22%) |
| **Partial/Composite** | 6 (66%) |
| **No Mapping** | 1 (11%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getCreatorPackage` | `String getCreatorPackage()` | 7 | near | moderate | `getStorage` | `getStorage(path: string, callback: AsyncCallback<Storage>): void` |
| `getCreatorUid` | `int getCreatorUid()` | 7 | near | moderate | `getUid` | `getUid(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `getCreatorUserHandle` | `android.os.UserHandle getCreatorUserHandle()` | 5 | partial | moderate | `getPreferences` | `getPreferences(context: Context, name: string, callback: AsyncCallback<Preferences>): void` |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `sendIntent` | 5 | partial | throw UnsupportedOperationException |
| `sendIntent` | 5 | partial | throw UnsupportedOperationException |
| `readIntentSenderOrNullFromParcel` | 4 | partial | Return safe default (null/false/0/empty) |
| `writeToParcel` | 4 | partial | Log warning + no-op |
| `writeIntentSenderOrNullToParcel` | 3 | composite | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 6 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.content.IntentSender`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.IntentSender` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
