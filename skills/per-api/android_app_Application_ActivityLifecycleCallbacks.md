# SKILL: android.app.Application.ActivityLifecycleCallbacks

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.Application.ActivityLifecycleCallbacks`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.Application.ActivityLifecycleCallbacks` |
| **Package** | `android.app.Application` |
| **Total Methods** | 21 |
| **Avg Score** | 4.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 21 (100%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `onActivityPrePaused` | `default void onActivityPrePaused(@NonNull android.app.Activity)` | 6 | partial | moderate | `onPrepare` | `onPrepare(): void` |
| `onActivityPreStarted` | `default void onActivityPreStarted(@NonNull android.app.Activity)` | 6 | partial | moderate | `onPrepare` | `onPrepare(): void` |
| `onActivitySaveInstanceState` | `void onActivitySaveInstanceState(@NonNull android.app.Activity, @NonNull android.os.Bundle)` | 5 | partial | moderate | `notifySaveAsResult` | `notifySaveAsResult(parameter: AbilityResult, requestCode: number, callback: AsyncCallback<void>): void` |
| `onActivityPreSaveInstanceState` | `default void onActivityPreSaveInstanceState(@NonNull android.app.Activity, @NonNull android.os.Bundle)` | 5 | partial | moderate | `notifySaveAsResult` | `notifySaveAsResult(parameter: AbilityResult, requestCode: number, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 17 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onActivityPostSaveInstanceState` | 5 | partial | Store callback, never fire |
| `onActivityPreCreated` | 5 | partial | Return dummy instance / no-op |
| `onActivityPreResumed` | 5 | partial | Store callback, never fire |
| `onActivityPreStopped` | 5 | partial | No-op |
| `onActivityPaused` | 5 | partial | Store callback, never fire |
| `onActivityPostCreated` | 5 | partial | Return dummy instance / no-op |
| `onActivityCreated` | 5 | partial | Return dummy instance / no-op |
| `onActivityResumed` | 5 | partial | Store callback, never fire |
| `onActivityStarted` | 5 | partial | Return dummy instance / no-op |
| `onActivityPostPaused` | 4 | partial | Store callback, never fire |
| `onActivityPostResumed` | 4 | partial | Store callback, never fire |
| `onActivityPostStarted` | 4 | partial | Return dummy instance / no-op |
| `onActivityPreDestroyed` | 4 | composite | No-op |
| `onActivityStopped` | 4 | composite | No-op |
| `onActivityDestroyed` | 4 | composite | No-op |
| `onActivityPostStopped` | 3 | composite | No-op |
| `onActivityPostDestroyed` | 3 | composite | No-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 4 methods that have score >= 5
2. Stub 17 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.Application.ActivityLifecycleCallbacks`:


## Quality Gates

Before marking `android.app.Application.ActivityLifecycleCallbacks` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 21 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
