# SKILL: android.provider.ContactsContract.Contacts

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.provider.ContactsContract.Contacts`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.provider.ContactsContract.Contacts` |
| **Package** | `android.provider.ContactsContract` |
| **Total Methods** | 6 |
| **Avg Score** | 2.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 3 (50%) |
| **No Mapping** | 3 (50%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getLookupUri` | `static android.net.Uri getLookupUri(android.content.ContentResolver, android.net.Uri)` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `getLookupUri` | `static android.net.Uri getLookupUri(long, String)` | 5 | partial | moderate | `getCount` | `getCount(): number` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isEnterpriseContactId` | 5 | partial | Return safe default (null/false/0/empty) |
| `lookupContact` | 1 | none | Store callback, never fire |
| `openContactPhotoInputStream` | 1 | none | Return dummy instance / no-op |
| `openContactPhotoInputStream` | 1 | none | Return dummy instance / no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 2 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.provider.ContactsContract.Contacts`:


## Quality Gates

Before marking `android.provider.ContactsContract.Contacts` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 6 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
