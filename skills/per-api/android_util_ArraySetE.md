# SKILL: android.util.ArraySet<E>

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.ArraySet<E>`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.ArraySet<E>` |
| **Package** | `android.util` |
| **Total Methods** | 24 |
| **Avg Score** | 6.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 13 (54%) |
| **Partial/Composite** | 9 (37%) |
| **No Mapping** | 2 (8%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 20 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `add` | `boolean add(E)` | 10 | direct | trivial | `add` | `add: (bundleName: string, userId: number) => void` |
| `clear` | `void clear()` | 10 | direct | trivial | `clear` | `clear(): void` |
| `contains` | `boolean contains(Object)` | 10 | direct | trivial | `contains` | `contains(rule: bigint): boolean` |
| `remove` | `boolean remove(Object)` | 10 | direct | trivial | `remove` | `remove(logType: string, logName: string): void` |
| `removeAll` | `boolean removeAll(android.util.ArraySet<? extends E>)` | 10 | direct | trivial | `removeAll` | `removeAll(bundle: BundleOption, callback: AsyncCallback<void>): void` |
| `removeAll` | `boolean removeAll(java.util.Collection<?>)` | 10 | direct | trivial | `removeAll` | `removeAll(bundle: BundleOption, callback: AsyncCallback<void>): void` |
| `size` | `int size()` | 10 | direct | trivial | `size` | `size: number` |
| `removeAt` | `E removeAt(int)` | 9 | direct | easy | `remove` | `remove(logType: string, logName: string): void` |
| `containsAll` | `boolean containsAll(java.util.Collection<?>)` | 8 | direct | easy | `contains` | `contains(rule: bigint): boolean` |
| `indexOf` | `int indexOf(Object)` | 8 | direct | easy | `index` | `index: number` |
| `valueAt` | `E valueAt(int)` | 8 | direct | easy | `value` | `value: number` |
| `addAll` | `void addAll(android.util.ArraySet<? extends E>)` | 7 | near | moderate | `add` | `add: (bundleName: string, userId: number) => void` |
| `addAll` | `boolean addAll(java.util.Collection<? extends E>)` | 7 | near | moderate | `add` | `add: (bundleName: string, userId: number) => void` |
| `isEmpty` | `boolean isEmpty()` | 6 | partial | moderate | `isKeepData` | `isKeepData: boolean` |
| `retainAll` | `boolean retainAll(java.util.Collection<?>)` | 6 | partial | moderate | `removeAll` | `removeAll(bundle: BundleOption, callback: AsyncCallback<void>): void` |
| `ArraySet` | `ArraySet()` | 5 | partial | moderate | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `ArraySet` | `ArraySet(int)` | 5 | partial | moderate | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `ArraySet` | `ArraySet(android.util.ArraySet<E>)` | 5 | partial | moderate | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `ArraySet` | `ArraySet(java.util.Collection<? extends E>)` | 5 | partial | moderate | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `ArraySet` | `ArraySet(@Nullable E[])` | 5 | partial | moderate | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `toArray` | 3 | composite | throw UnsupportedOperationException |
| `toArray` | 3 | composite | throw UnsupportedOperationException |
| `ensureCapacity` | 1 | none | throw UnsupportedOperationException |
| `iterator` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 20 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.util.ArraySet<E>`:


## Quality Gates

Before marking `android.util.ArraySet<E>` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 24 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 20 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
