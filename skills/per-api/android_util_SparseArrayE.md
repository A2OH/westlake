# SKILL: android.util.SparseArray<E>

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.SparseArray<E>`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.SparseArray<E>` |
| **Package** | `android.util` |
| **Total Methods** | 19 |
| **Avg Score** | 7.0 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 15 (78%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 4 (21%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 15 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `clear` | `void clear()` | 10 | direct | trivial | `clear` | `clear(): void` |
| `contains` | `boolean contains(int)` | 10 | direct | trivial | `contains` | `contains(rule: bigint): boolean` |
| `get` | `E get(int)` | 10 | direct | trivial | `getOAID` | `getOAID(callback: AsyncCallback<string>): void` |
| `get` | `E get(int, E)` | 10 | direct | trivial | `getOAID` | `getOAID(callback: AsyncCallback<string>): void` |
| `remove` | `void remove(int)` | 10 | direct | trivial | `remove` | `remove(logType: string, logName: string): void` |
| `size` | `int size()` | 10 | direct | trivial | `size` | `size: number` |
| `setValueAt` | `void setValueAt(int, E)` | 9 | direct | easy | `setValue` | `setValue(value: number): void` |
| `removeAt` | `void removeAt(int)` | 9 | direct | easy | `remove` | `remove(logType: string, logName: string): void` |
| `valueAt` | `E valueAt(int)` | 8 | direct | easy | `value` | `value: number` |
| `append` | `void append(int, E)` | 8 | direct | easy | `append` | `append?: boolean` |
| `indexOfKey` | `int indexOfKey(int)` | 8 | near | easy | `indexOfLink` | `indexOfLink?: string` |
| `keyAt` | `int keyAt(int)` | 8 | near | easy | `key` | `key: string]: BundleStateInfo` |
| `indexOfValue` | `int indexOfValue(E)` | 7 | near | moderate | `indexOfLink` | `indexOfLink?: string` |
| `removeAtRange` | `void removeAtRange(int, int)` | 7 | near | moderate | `removeRule` | `removeRule(rule: bigint): void` |
| `delete` | `void delete(int)` | 6 | near | moderate | `deleteContact` | `deleteContact(key: string, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `SparseArray` | 1 | none | throw UnsupportedOperationException |
| `SparseArray` | 1 | none | throw UnsupportedOperationException |
| `clone` | 1 | none | Store callback, never fire |
| `put` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.util.SparseArray<E>`:


## Quality Gates

Before marking `android.util.SparseArray<E>` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 19 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 15 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
