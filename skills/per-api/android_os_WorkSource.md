# SKILL: android.os.WorkSource

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.WorkSource`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.WorkSource` |
| **Package** | `android.os` |
| **Total Methods** | 9 |
| **Avg Score** | 6.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 6 (66%) |
| **Partial/Composite** | 1 (11%) |
| **No Mapping** | 2 (22%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `add` | `boolean add(android.os.WorkSource)` | 10 | direct | trivial | `add` | `add: (bundleName: string, userId: number) => void` |
| `clear` | `void clear()` | 10 | direct | trivial | `clear` | `clear(): void` |
| `remove` | `boolean remove(android.os.WorkSource)` | 10 | direct | trivial | `remove` | `remove(logType: string, logName: string): void` |
| `set` | `void set(android.os.WorkSource)` | 10 | direct | trivial | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `WorkSource` | `WorkSource()` | 6 | near | moderate | `sourceMode` | `readonly sourceMode: ScreenSourceMode` |
| `WorkSource` | `WorkSource(android.os.WorkSource)` | 6 | near | moderate | `sourceMode` | `readonly sourceMode: ScreenSourceMode` |
| `writeToParcel` | `void writeToParcel(android.os.Parcel, int)` | 6 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `describeContents` | 1 | none | Store callback, never fire |
| `diff` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 7 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.WorkSource`:


## Quality Gates

Before marking `android.os.WorkSource` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
