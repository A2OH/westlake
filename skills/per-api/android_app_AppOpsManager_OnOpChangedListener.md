# SKILL: android.app.AppOpsManager.OnOpChangedListener

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.AppOpsManager.OnOpChangedListener`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.AppOpsManager.OnOpChangedListener` |
| **Package** | `android.app.AppOpsManager` |
| **Total Methods** | 5 |
| **Avg Score** | 4.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 5 (100%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `OnOpNotedCallback` | `AppOpsManager.OnOpNotedCallback()` | 6 | partial | moderate | `getRequestCallback` | `getRequestCallback(want: Want): RequestCallback` |
| `onOpChanged` | `void onOpChanged(String, String)` | 5 | partial | moderate | `onPrepare` | `onPrepare(): void` |
| `onNoted` | `abstract void onNoted(@NonNull android.app.SyncNotedAppOp)` | 5 | partial | moderate | `on` | `on(type: 'abilityForegroundState', observer: AbilityForegroundStateObserver): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onAsyncNoted` | 5 | partial | Store callback, never fire |
| `onSelfNoted` | 4 | partial | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.AppOpsManager.OnOpChangedListener`:


## Quality Gates

Before marking `android.app.AppOpsManager.OnOpChangedListener` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
