# SKILL: android.util.Half

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.Half`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.Half` |
| **Package** | `android.util` |
| **Total Methods** | 26 |
| **Avg Score** | 4.6 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 13 (50%) |
| **Partial/Composite** | 4 (15%) |
| **No Mapping** | 9 (34%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 14 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `compare` | `static int compare(@HalfFloat short, @HalfFloat short)` | 10 | direct | trivial | `compare` | `compare(buf1: Buffer | Uint8Array, buf2: Buffer | Uint8Array): -1 | 0 | 1` |
| `compareTo` | `int compareTo(@NonNull android.util.Half)` | 9 | direct | easy | `compare` | `compare(buf1: Buffer | Uint8Array, buf2: Buffer | Uint8Array): -1 | 0 | 1` |
| `intValue` | `int intValue()` | 8 | near | easy | `value` | `value: number` |
| `getSign` | `static int getSign(@HalfFloat short)` | 7 | near | easy | `getSync` | `getSync(key: string, def?: string): string` |
| `longValue` | `long longValue()` | 7 | near | easy | `value` | `value: number` |
| `floatValue` | `float floatValue()` | 7 | near | moderate | `setValue` | `setValue(value: number): void` |
| `hashCode` | `static int hashCode(@HalfFloat short)` | 7 | near | moderate | `hash` | `hash(path: string, algorithm: string): Promise<string>` |
| `isNormalized` | `static boolean isNormalized(@HalfFloat short)` | 7 | near | moderate | `isEnabled` | `readonly isEnabled?: boolean` |
| `getExponent` | `static int getExponent(@HalfFloat short)` | 6 | near | moderate | `getEnvironmentVar` | `getEnvironmentVar(name: string): string` |
| `doubleValue` | `double doubleValue()` | 6 | near | moderate | `headerValue` | `headerValue: string` |
| `isInfinite` | `static boolean isInfinite(@HalfFloat short)` | 6 | near | moderate | `isWifiActive` | `isWifiActive(): boolean` |
| `isNaN` | `boolean isNaN()` | 6 | near | moderate | `isInSandbox` | `isInSandbox(): Promise<boolean>` |
| `isNaN` | `static boolean isNaN(@HalfFloat short)` | 6 | near | moderate | `isInSandbox` | `isInSandbox(): Promise<boolean>` |
| `getSignificand` | `static int getSignificand(@HalfFloat short)` | 6 | partial | moderate | `getSync` | `getSync(key: string, def?: string): string` |

## Stub APIs (score < 5): 12 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `halfToIntBits` | 5 | partial | throw UnsupportedOperationException |
| `halfToRawIntBits` | 4 | partial | throw UnsupportedOperationException |
| `toFloat` | 3 | composite | throw UnsupportedOperationException |
| `Half` | 1 | none | throw UnsupportedOperationException |
| `Half` | 1 | none | throw UnsupportedOperationException |
| `Half` | 1 | none | throw UnsupportedOperationException |
| `Half` | 1 | none | throw UnsupportedOperationException |
| `equals` | 1 | none | throw UnsupportedOperationException |
| `greater` | 1 | none | throw UnsupportedOperationException |
| `greaterEquals` | 1 | none | throw UnsupportedOperationException |
| `less` | 1 | none | throw UnsupportedOperationException |
| `lessEquals` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.util.Half`:


## Quality Gates

Before marking `android.util.Half` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 26 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 14 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
