# SKILL: android.provider.ContactsContract.CommonDataKinds.Im

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.provider.ContactsContract.CommonDataKinds.Im`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.provider.ContactsContract.CommonDataKinds.Im` |
| **Package** | `android.provider.ContactsContract.CommonDataKinds` |
| **Total Methods** | 4 |
| **Avg Score** | 5.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (25%) |
| **Partial/Composite** | 3 (75%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getTypeLabel` | `static CharSequence getTypeLabel(android.content.res.Resources, int, CharSequence)` | 7 | near | moderate | `getTypeDescriptor` | `getTypeDescriptor(typeId: string): TypeDescriptor` |
| `getTypeLabelResource` | `static int getTypeLabelResource(int)` | 6 | partial | moderate | `getTypeDescriptor` | `getTypeDescriptor(typeId: string): TypeDescriptor` |
| `getProtocolLabel` | `static CharSequence getProtocolLabel(android.content.res.Resources, int, CharSequence)` | 5 | partial | moderate | `getStorage` | `getStorage(path: string, callback: AsyncCallback<Storage>): void` |
| `getProtocolLabelResource` | `static int getProtocolLabelResource(int)` | 5 | partial | moderate | `getPreferences` | `getPreferences(context: Context, name: string, callback: AsyncCallback<Preferences>): void` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 4 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.provider.ContactsContract.CommonDataKinds.Im`:


## Quality Gates

Before marking `android.provider.ContactsContract.CommonDataKinds.Im` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
