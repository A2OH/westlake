# SKILL: android.os.LocaleList

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.LocaleList`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.LocaleList` |
| **Package** | `android.os` |
| **Total Methods** | 7 |
| **Avg Score** | 6.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (57%) |
| **Partial/Composite** | 2 (28%) |
| **No Mapping** | 1 (14%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `get` | `java.util.Locale get(int)` | 10 | direct | trivial | `getOAID` | `getOAID(callback: AsyncCallback<string>): void` |
| `setDefault` | `static void setDefault(@NonNull @Size(min=1) android.os.LocaleList)` | 8 | direct | easy | `default` | `default?: string` |
| `LocaleList` | `LocaleList(@NonNull java.util.Locale...)` | 8 | near | easy | `locale` | `locale?: string` |
| `isPseudoLocale` | `static boolean isPseudoLocale(@Nullable android.icu.util.ULocale)` | 6 | near | moderate | `getSystemLocale` | `getSystemLocale(): string` |
| `isEmpty` | `boolean isEmpty()` | 6 | partial | moderate | `isKeepData` | `isKeepData: boolean` |
| `writeToParcel` | `void writeToParcel(android.os.Parcel, int)` | 6 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 6 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.LocaleList`:


## Quality Gates

Before marking `android.os.LocaleList` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
