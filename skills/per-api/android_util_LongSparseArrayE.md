# SKILL: android.util.LongSparseArray<E>

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.LongSparseArray<E>`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.LongSparseArray<E>` |
| **Package** | `android.util` |
| **Total Methods** | 17 |
| **Avg Score** | 6.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 13 (76%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 4 (23%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 13 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `clear` | `void clear()` | 10 | direct | trivial | `clear` | `clear(): void` |
| `get` | `E get(long)` | 10 | direct | trivial | `getOAID` | `getOAID(callback: AsyncCallback<string>): void` |
| `get` | `E get(long, E)` | 10 | direct | trivial | `getOAID` | `getOAID(callback: AsyncCallback<string>): void` |
| `remove` | `void remove(long)` | 10 | direct | trivial | `remove` | `remove(logType: string, logName: string): void` |
| `size` | `int size()` | 10 | direct | trivial | `size` | `size: number` |
| `setValueAt` | `void setValueAt(int, E)` | 9 | direct | easy | `setValue` | `setValue(value: number): void` |
| `removeAt` | `void removeAt(int)` | 9 | direct | easy | `remove` | `remove(logType: string, logName: string): void` |
| `valueAt` | `E valueAt(int)` | 8 | direct | easy | `value` | `value: number` |
| `append` | `void append(long, E)` | 8 | direct | easy | `append` | `append?: boolean` |
| `indexOfKey` | `int indexOfKey(long)` | 8 | near | easy | `indexOfLink` | `indexOfLink?: string` |
| `keyAt` | `long keyAt(int)` | 8 | near | easy | `key` | `key: string]: BundleStateInfo` |
| `indexOfValue` | `int indexOfValue(E)` | 7 | near | moderate | `indexOfLink` | `indexOfLink?: string` |
| `delete` | `void delete(long)` | 6 | near | moderate | `deleteContact` | `deleteContact(key: string, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `LongSparseArray` | 1 | none | Store callback, never fire |
| `LongSparseArray` | 1 | none | Store callback, never fire |
| `clone` | 1 | none | Store callback, never fire |
| `put` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 13 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.util.LongSparseArray<E>`:


## Quality Gates

Before marking `android.util.LongSparseArray<E>` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 17 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 13 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
