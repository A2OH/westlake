# SKILL: android.os.BaseBundle

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.BaseBundle`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.BaseBundle` |
| **Package** | `android.os` |
| **Total Methods** | 26 |
| **Avg Score** | 5.4 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 15 (57%) |
| **Partial/Composite** | 3 (11%) |
| **No Mapping** | 8 (30%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 18 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `clear` | `void clear()` | 10 | direct | trivial | `clear` | `clear(): void` |
| `remove` | `void remove(String)` | 10 | direct | trivial | `remove` | `remove(logType: string, logName: string): void` |
| `size` | `int size()` | 10 | direct | trivial | `size` | `size: number` |
| `containsKey` | `boolean containsKey(String)` | 8 | direct | easy | `contains` | `contains(rule: bigint): boolean` |
| `getString` | `String getString(@Nullable String, String)` | 8 | near | easy | `getErrorString` | `getErrorString(errno: number): string` |
| `getInt` | `int getInt(String)` | 8 | near | easy | `getWant` | `getWant(agent: WantAgent, callback: AsyncCallback<Want>): void` |
| `getInt` | `int getInt(String, int)` | 8 | near | easy | `getWant` | `getWant(agent: WantAgent, callback: AsyncCallback<Want>): void` |
| `getDouble` | `double getDouble(String)` | 8 | near | easy | `getRule` | `getRule(): bigint` |
| `getDouble` | `double getDouble(String, double)` | 8 | near | easy | `getRule` | `getRule(): bigint` |
| `getLong` | `long getLong(String)` | 8 | near | easy | `getLength` | `getLength(): string` |
| `getLong` | `long getLong(String, long)` | 8 | near | easy | `getLength` | `getLength(): string` |
| `keySet` | `java.util.Set<java.lang.String> keySet()` | 7 | near | moderate | `key` | `key: string]: BundleStateInfo` |
| `putString` | `void putString(@Nullable String, @Nullable String)` | 6 | near | moderate | `errnoToString` | `errnoToString(errno: number): string` |
| `getBoolean` | `boolean getBoolean(String)` | 6 | near | moderate | `getLength` | `getLength(): string` |
| `getBoolean` | `boolean getBoolean(String, boolean)` | 6 | near | moderate | `getLength` | `getLength(): string` |
| `isEmpty` | `boolean isEmpty()` | 6 | partial | moderate | `isKeepData` | `isKeepData: boolean` |
| `putStringArray` | `void putStringArray(@Nullable String, @Nullable String[])` | 5 | partial | moderate | `errnoToString` | `errnoToString(errno: number): string` |
| `putAll` | `void putAll(android.os.PersistableBundle)` | 5 | partial | moderate | `cancelAll` | `cancelAll(callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 8 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `putBoolean` | 1 | none | Log warning + no-op |
| `putBooleanArray` | 1 | none | Log warning + no-op |
| `putDouble` | 1 | none | Log warning + no-op |
| `putDoubleArray` | 1 | none | Log warning + no-op |
| `putInt` | 1 | none | Log warning + no-op |
| `putIntArray` | 1 | none | Log warning + no-op |
| `putLong` | 1 | none | Log warning + no-op |
| `putLongArray` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 18 methods that have score >= 5
2. Stub 8 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.BaseBundle`:


## Quality Gates

Before marking `android.os.BaseBundle` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 26 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 18 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
