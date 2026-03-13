# SKILL: android.os.StrictMode

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.StrictMode`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.StrictMode` |
| **Package** | `android.os` |
| **Total Methods** | 8 |
| **Avg Score** | 5.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (50%) |
| **Partial/Composite** | 3 (37%) |
| **No Mapping** | 1 (12%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getThreadPolicy` | `static android.os.StrictMode.ThreadPolicy getThreadPolicy()` | 8 | direct | easy | `getThreadPriority` | `getThreadPriority(v: number): number` |
| `setThreadPolicy` | `static void setThreadPolicy(android.os.StrictMode.ThreadPolicy)` | 8 | near | easy | `getThreadPriority` | `getThreadPriority(v: number): number` |
| `enableDefaults` | `static void enableDefaults()` | 7 | near | moderate | `enableFlag` | `enableFlag(id: HiTraceId, flag: HiTraceFlag): void` |
| `getVmPolicy` | `static android.os.StrictMode.VmPolicy getVmPolicy()` | 7 | near | moderate | `getDLPGatheringPolicy` | `getDLPGatheringPolicy(): Promise<GatheringPolicyType>` |
| `allowThreadDiskWrites` | `static android.os.StrictMode.ThreadPolicy allowThreadDiskWrites()` | 5 | partial | moderate | `getThreadPriority` | `getThreadPriority(v: number): number` |
| `setVmPolicy` | `static void setVmPolicy(android.os.StrictMode.VmPolicy)` | 5 | partial | moderate | `setValue` | `setValue(value: number): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `allowThreadDiskReads` | 4 | partial | Return safe default (null/false/0/empty) |
| `noteSlowCall` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 6 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.StrictMode`:


## Quality Gates

Before marking `android.os.StrictMode` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
