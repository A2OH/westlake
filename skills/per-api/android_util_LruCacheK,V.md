# SKILL: android.util.LruCache<K, V>

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.LruCache<K, V>`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.LruCache<K, V>` |
| **Package** | `android.util` |
| **Total Methods** | 19 |
| **Avg Score** | 6.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 13 (68%) |
| **Partial/Composite** | 3 (15%) |
| **No Mapping** | 3 (15%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 15 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `create` | `V create(K)` | 10 | direct | trivial | `create` | `create(config: PiPConfiguration): Promise<PiPController>` |
| `get` | `final V get(K)` | 10 | direct | trivial | `getOAID` | `getOAID(callback: AsyncCallback<string>): void` |
| `remove` | `final V remove(K)` | 10 | direct | trivial | `remove` | `remove(logType: string, logName: string): void` |
| `size` | `final int size()` | 10 | direct | trivial | `size` | `size: number` |
| `sizeOf` | `int sizeOf(K, V)` | 8 | direct | easy | `size` | `size: number` |
| `hitCount` | `final int hitCount()` | 8 | near | easy | `count` | `readonly count: number` |
| `putCount` | `final int putCount()` | 8 | near | easy | `count` | `readonly count: number` |
| `toString` | `final String toString()` | 8 | near | easy | `errnoToString` | `errnoToString(errno: number): string` |
| `maxSize` | `final int maxSize()` | 8 | near | easy | `imageSize` | `imageSize?: Size` |
| `createCount` | `final int createCount()` | 7 | near | easy | `createGroup` | `createGroup(config: WifiP2PConfig): void` |
| `missCount` | `final int missCount()` | 7 | near | easy | `count` | `readonly count: number` |
| `evictionCount` | `final int evictionCount()` | 7 | near | moderate | `getActiveNotificationCount` | `getActiveNotificationCount(callback: AsyncCallback<number>): void` |
| `trimToSize` | `void trimToSize(int)` | 6 | near | moderate | `additionSize` | `additionSize?: number` |
| `entryRemoved` | `void entryRemoved(boolean, K, V, V)` | 6 | partial | moderate | `entryModuleName` | `readonly entryModuleName: string` |
| `evictAll` | `final void evictAll()` | 6 | partial | moderate | `removeAll` | `removeAll(bundle: BundleOption, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `LruCache` | 4 | partial | throw UnsupportedOperationException |
| `put` | 1 | none | Log warning + no-op |
| `resize` | 1 | none | throw UnsupportedOperationException |
| `snapshot` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 15 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.util.LruCache<K, V>`:


## Quality Gates

Before marking `android.util.LruCache<K, V>` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 19 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 15 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
