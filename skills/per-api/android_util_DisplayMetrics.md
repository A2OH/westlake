# SKILL: android.util.DisplayMetrics

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.DisplayMetrics`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.DisplayMetrics` |
| **Package** | `android.util` |
| **Total Methods** | 4 |
| **Avg Score** | 5.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (50%) |
| **Partial/Composite** | 1 (25%) |
| **No Mapping** | 1 (25%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `setTo` | `void setTo(android.util.DisplayMetrics)` | 8 | near | easy | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `DisplayMetrics` | `DisplayMetrics()` | 7 | near | easy | `displayName` | `displayName: string` |
| `setToDefaults` | `void setToDefaults()` | 6 | partial | moderate | `setPortRoles` | `setPortRoles(portId: number, powerRole: PowerRoleType, dataRole: DataRoleType): Promise<boolean>` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `equals` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.util.DisplayMetrics`:


## Quality Gates

Before marking `android.util.DisplayMetrics` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
