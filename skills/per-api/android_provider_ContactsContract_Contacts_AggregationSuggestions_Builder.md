# SKILL: android.provider.ContactsContract.Contacts.AggregationSuggestions.Builder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.provider.ContactsContract.Contacts.AggregationSuggestions.Builder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.provider.ContactsContract.Contacts.AggregationSuggestions.Builder` |
| **Package** | `android.provider.ContactsContract.Contacts.AggregationSuggestions` |
| **Total Methods** | 5 |
| **Avg Score** | 3.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 3 (60%) |
| **No Mapping** | 2 (40%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `setContactId` | `android.provider.ContactsContract.Contacts.AggregationSuggestions.Builder setContactId(long)` | 6 | partial | moderate | `sessionId` | `sessionId: string` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `addNameParameter` | 4 | partial | Log warning + no-op |
| `setLimit` | 3 | composite | Log warning + no-op |
| `Builder` | 1 | none | throw UnsupportedOperationException |
| `build` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 1 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.provider.ContactsContract.Contacts.AggregationSuggestions.Builder`:


## Quality Gates

Before marking `android.provider.ContactsContract.Contacts.AggregationSuggestions.Builder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
