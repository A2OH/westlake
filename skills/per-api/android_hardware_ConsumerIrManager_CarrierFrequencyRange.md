# SKILL: android.hardware.ConsumerIrManager.CarrierFrequencyRange

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.hardware.ConsumerIrManager.CarrierFrequencyRange`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.hardware.ConsumerIrManager.CarrierFrequencyRange` |
| **Package** | `android.hardware.ConsumerIrManager` |
| **Total Methods** | 3 |
| **Avg Score** | 6.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (66%) |
| **Partial/Composite** | 1 (33%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `CarrierFrequencyRange` | `ConsumerIrManager.CarrierFrequencyRange(int, int)` | 8 | direct | easy | `carrierFrequencies` | `carrierFrequencies: Array<number>` |
| `getMinFrequency` | `int getMinFrequency()` | 7 | near | moderate | `getMinHeight` | `getMinHeight(callback: AsyncCallback<number>): void` |
| `getMaxFrequency` | `int getMaxFrequency()` | 6 | partial | moderate | `getValueSync` | `getValueSync(dataAbilityHelper: DataAbilityHelper, name: string, defValue: string): string` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.hardware.ConsumerIrManager.CarrierFrequencyRange`:


## Quality Gates

Before marking `android.hardware.ConsumerIrManager.CarrierFrequencyRange` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 3 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
