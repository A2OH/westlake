# SKILL: android.util.Rational

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.Rational`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.Rational` |
| **Package** | `android.util` |
| **Total Methods** | 13 |
| **Avg Score** | 6.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 10 (76%) |
| **Partial/Composite** | 2 (15%) |
| **No Mapping** | 1 (7%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 12 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `compareTo` | `int compareTo(android.util.Rational)` | 9 | direct | easy | `compare` | `compare(buf1: Buffer | Uint8Array, buf2: Buffer | Uint8Array): -1 | 0 | 1` |
| `intValue` | `int intValue()` | 8 | near | easy | `value` | `value: number` |
| `longValue` | `long longValue()` | 7 | near | easy | `value` | `value: number` |
| `floatValue` | `float floatValue()` | 7 | near | moderate | `setValue` | `setValue(value: number): void` |
| `getDenominator` | `int getDenominator()` | 6 | near | moderate | `getEnvironmentVar` | `getEnvironmentVar(name: string): string` |
| `doubleValue` | `double doubleValue()` | 6 | near | moderate | `headerValue` | `headerValue: string` |
| `isInfinite` | `boolean isInfinite()` | 6 | near | moderate | `isWifiActive` | `isWifiActive(): boolean` |
| `isNaN` | `boolean isNaN()` | 6 | near | moderate | `isInSandbox` | `isInSandbox(): Promise<boolean>` |
| `getNumerator` | `int getNumerator()` | 6 | near | moderate | `getRestorer` | `getRestorer(): Restorer` |
| `isFinite` | `boolean isFinite()` | 6 | near | moderate | `isWifiActive` | `isWifiActive(): boolean` |
| `isZero` | `boolean isZero()` | 6 | partial | moderate | `isBuffer` | `isBuffer(obj: Object): boolean` |
| `parseRational` | `static android.util.Rational parseRational(String) throws java.lang.NumberFormatException` | 5 | partial | moderate | `parseUUID` | `parseUUID(uuid: string): Uint8Array` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `Rational` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 12 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.util.Rational`:


## Quality Gates

Before marking `android.util.Rational` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 13 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 12 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
