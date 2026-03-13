# SKILL: android.database.AbstractWindowedCursor

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.database.AbstractWindowedCursor`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.database.AbstractWindowedCursor` |
| **Package** | `android.database` |
| **Total Methods** | 10 |
| **Avg Score** | 5.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 6 (60%) |
| **Partial/Composite** | 2 (20%) |
| **No Mapping** | 2 (20%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getString` | `String getString(int)` | 7 | near | easy | `getStorage` | `getStorage(path: string, callback: AsyncCallback<Storage>): void` |
| `getInt` | `int getInt(int)` | 7 | near | easy | `getCount` | `getCount(): number` |
| `getLong` | `long getLong(int)` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `getShort` | `short getShort(int)` | 7 | near | moderate | `getStorage` | `getStorage(path: string, callback: AsyncCallback<Storage>): void` |
| `getFloat` | `float getFloat(int)` | 6 | near | moderate | `getCount` | `getCount(): number` |
| `getDouble` | `double getDouble(int)` | 6 | near | moderate | `getRdbStore` | `getRdbStore(context: Context, config: StoreConfig, version: number, callback: AsyncCallback<RdbStore>): void` |
| `isNull` | `boolean isNull(int)` | 5 | partial | moderate | `isFirst` | `isFirst(): boolean` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setWindow` | 4 | composite | Log warning + no-op |
| `AbstractWindowedCursor` | 1 | none | throw UnsupportedOperationException |
| `hasWindow` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 7 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.database.AbstractWindowedCursor`:


## Quality Gates

Before marking `android.database.AbstractWindowedCursor` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
