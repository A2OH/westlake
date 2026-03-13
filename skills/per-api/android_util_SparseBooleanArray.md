# SKILL: android.util.SparseBooleanArray

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.SparseBooleanArray`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.SparseBooleanArray` |
| **Package** | `android.util` |
| **Total Methods** | 16 |
| **Avg Score** | 6.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 12 (75%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 4 (25%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 12 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `clear` | `void clear()` | 10 | direct | trivial | `clear` | `clear(): void` |
| `get` | `boolean get(int)` | 10 | direct | trivial | `getOAID` | `getOAID(callback: AsyncCallback<string>): void` |
| `get` | `boolean get(int, boolean)` | 10 | direct | trivial | `getOAID` | `getOAID(callback: AsyncCallback<string>): void` |
| `size` | `int size()` | 10 | direct | trivial | `size` | `size: number` |
| `setValueAt` | `void setValueAt(int, boolean)` | 9 | direct | easy | `setValue` | `setValue(value: number): void` |
| `removeAt` | `void removeAt(int)` | 9 | direct | easy | `remove` | `remove(logType: string, logName: string): void` |
| `valueAt` | `boolean valueAt(int)` | 8 | direct | easy | `value` | `value: number` |
| `append` | `void append(int, boolean)` | 8 | direct | easy | `append` | `append?: boolean` |
| `indexOfKey` | `int indexOfKey(int)` | 8 | near | easy | `indexOfLink` | `indexOfLink?: string` |
| `keyAt` | `int keyAt(int)` | 8 | near | easy | `key` | `key: string]: BundleStateInfo` |
| `indexOfValue` | `int indexOfValue(boolean)` | 7 | near | moderate | `indexOfLink` | `indexOfLink?: string` |
| `delete` | `void delete(int)` | 6 | near | moderate | `deleteContact` | `deleteContact(key: string, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `SparseBooleanArray` | 1 | none | throw UnsupportedOperationException |
| `SparseBooleanArray` | 1 | none | throw UnsupportedOperationException |
| `clone` | 1 | none | Store callback, never fire |
| `put` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 12 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.util.SparseBooleanArray`:


## Quality Gates

Before marking `android.util.SparseBooleanArray` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 16 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 12 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
