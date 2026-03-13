# SKILL: android.os.VibrationAttributes

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.VibrationAttributes`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.VibrationAttributes` |
| **Package** | `android.os` |
| **Total Methods** | 6 |
| **Avg Score** | 5.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (66%) |
| **Partial/Composite** | 1 (16%) |
| **No Mapping** | 1 (16%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getUsage` | `int getUsage()` | 8 | direct | easy | `getCpuUsage` | `getCpuUsage(): number` |
| `getFlags` | `int getFlags()` | 7 | near | moderate | `getFoldStatus` | `getFoldStatus(): FoldStatus` |
| `getUsageClass` | `int getUsageClass()` | 7 | near | moderate | `getCpuUsage` | `getCpuUsage(): number` |
| `isFlagSet` | `boolean isFlagSet(int)` | 7 | near | moderate | `isFlagEnabled` | `isFlagEnabled(id: HiTraceId, flag: HiTraceFlag): boolean` |
| `writeToParcel` | `void writeToParcel(@NonNull android.os.Parcel, int)` | 6 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.VibrationAttributes`:


## Quality Gates

Before marking `android.os.VibrationAttributes` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 6 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
