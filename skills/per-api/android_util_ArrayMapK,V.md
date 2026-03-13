# SKILL: android.util.ArrayMap<K, V>

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.ArrayMap<K, V>`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.ArrayMap<K, V>` |
| **Package** | `android.util` |
| **Total Methods** | 26 |
| **Avg Score** | 6.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 15 (57%) |
| **Partial/Composite** | 8 (30%) |
| **No Mapping** | 3 (11%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 20 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `clear` | `void clear()` | 10 | direct | trivial | `clear` | `clear(): void` |
| `get` | `V get(Object)` | 10 | direct | trivial | `getOAID` | `getOAID(callback: AsyncCallback<string>): void` |
| `remove` | `V remove(Object)` | 10 | direct | trivial | `remove` | `remove(logType: string, logName: string): void` |
| `removeAll` | `boolean removeAll(java.util.Collection<?>)` | 10 | direct | trivial | `removeAll` | `removeAll(bundle: BundleOption, callback: AsyncCallback<void>): void` |
| `size` | `int size()` | 10 | direct | trivial | `size` | `size: number` |
| `setValueAt` | `V setValueAt(int, V)` | 9 | direct | easy | `setValue` | `setValue(value: number): void` |
| `removeAt` | `V removeAt(int)` | 9 | direct | easy | `remove` | `remove(logType: string, logName: string): void` |
| `containsAll` | `boolean containsAll(java.util.Collection<?>)` | 8 | direct | easy | `contains` | `contains(rule: bigint): boolean` |
| `containsKey` | `boolean containsKey(Object)` | 8 | direct | easy | `contains` | `contains(rule: bigint): boolean` |
| `valueAt` | `V valueAt(int)` | 8 | direct | easy | `value` | `value: number` |
| `containsValue` | `boolean containsValue(Object)` | 8 | near | easy | `contains` | `contains(rule: bigint): boolean` |
| `indexOfKey` | `int indexOfKey(Object)` | 8 | near | easy | `indexOfLink` | `indexOfLink?: string` |
| `keyAt` | `K keyAt(int)` | 8 | near | easy | `key` | `key: string]: BundleStateInfo` |
| `indexOfValue` | `int indexOfValue(Object)` | 7 | near | moderate | `indexOfLink` | `indexOfLink?: string` |
| `keySet` | `java.util.Set<K> keySet()` | 7 | near | moderate | `key` | `key: string]: BundleStateInfo` |
| `isEmpty` | `boolean isEmpty()` | 6 | partial | moderate | `isKeepData` | `isKeepData: boolean` |
| `retainAll` | `boolean retainAll(java.util.Collection<?>)` | 6 | partial | moderate | `removeAll` | `removeAll(bundle: BundleOption, callback: AsyncCallback<void>): void` |
| `entrySet` | `java.util.Set<java.util.Map.Entry<K,V>> entrySet()` | 5 | partial | moderate | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `putAll` | `void putAll(android.util.ArrayMap<? extends K,? extends V>)` | 5 | partial | moderate | `cancelAll` | `cancelAll(callback: AsyncCallback<void>): void` |
| `putAll` | `void putAll(java.util.Map<? extends K,? extends V>)` | 5 | partial | moderate | `cancelAll` | `cancelAll(callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `ArrayMap` | 3 | composite | throw UnsupportedOperationException |
| `ArrayMap` | 3 | composite | throw UnsupportedOperationException |
| `ArrayMap` | 3 | composite | throw UnsupportedOperationException |
| `ensureCapacity` | 1 | none | throw UnsupportedOperationException |
| `put` | 1 | none | Log warning + no-op |
| `values` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 20 methods that have score >= 5
2. Stub 6 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.util.ArrayMap<K, V>`:


## Quality Gates

Before marking `android.util.ArrayMap<K, V>` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 26 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 20 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
