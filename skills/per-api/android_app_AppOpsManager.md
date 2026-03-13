# SKILL: android.app.AppOpsManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.AppOpsManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.AppOpsManager` |
| **Package** | `android.app` |
| **Total Methods** | 18 |
| **Avg Score** | 3.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (5%) |
| **Partial/Composite** | 8 (44%) |
| **No Mapping** | 9 (50%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `noteProxyOp` | `int noteProxyOp(@NonNull String, @Nullable String, int, @Nullable String, @Nullable String)` | 6 | near | moderate | `setRouterProxy` | `setRouterProxy(formIds: Array<string>, proxy: Callback<Want>, callback: AsyncCallback<void>): void` |
| `startWatchingMode` | `void startWatchingMode(@NonNull String, @Nullable String, @NonNull android.app.AppOpsManager.OnOpChangedListener)` | 6 | partial | moderate | `startChildProcess` | `startChildProcess(srcEntry: string, startMode: StartMode): Promise<number>` |
| `startWatchingMode` | `void startWatchingMode(@NonNull String, @Nullable String, int, @NonNull android.app.AppOpsManager.OnOpChangedListener)` | 6 | partial | moderate | `startChildProcess` | `startChildProcess(srcEntry: string, startMode: StartMode): Promise<number>` |
| `setOnOpNotedCallback` | `void setOnOpNotedCallback(@Nullable java.util.concurrent.Executor, @Nullable android.app.AppOpsManager.OnOpNotedCallback)` | 6 | partial | moderate | `getRequestCallback` | `getRequestCallback(want: Want): RequestCallback` |
| `startWatchingActive` | `void startWatchingActive(@NonNull String[], @NonNull java.util.concurrent.Executor, @NonNull android.app.AppOpsManager.OnOpActiveChangedListener)` | 6 | partial | moderate | `startChildProcess` | `startChildProcess(srcEntry: string, startMode: StartMode): Promise<number>` |
| `startOp` | `int startOp(@NonNull String, int, @Nullable String, @Nullable String, @Nullable String)` | 5 | partial | moderate | `startAbility` | `startAbility(parameter: StartAbilityParameter, callback: AsyncCallback<number>): void` |
| `startOpNoThrow` | `int startOpNoThrow(@NonNull String, int, @NonNull String, @NonNull String, @Nullable String)` | 5 | partial | moderate | `startChildProcess` | `startChildProcess(srcEntry: string, startMode: StartMode): Promise<number>` |
| `noteProxyOpNoThrow` | `int noteProxyOpNoThrow(@NonNull String, @Nullable String, int, @Nullable String, @Nullable String)` | 5 | partial | moderate | `setRouterProxy` | `setRouterProxy(formIds: Array<string>, proxy: Callback<Want>, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 10 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isOpActive` | 4 | partial | Return safe default (null/false/0/empty) |
| `finishOp` | 1 | none | Return safe default (null/false/0/empty) |
| `noteOp` | 1 | none | throw UnsupportedOperationException |
| `noteOpNoThrow` | 1 | none | throw UnsupportedOperationException |
| `stopWatchingActive` | 1 | none | No-op |
| `stopWatchingMode` | 1 | none | No-op |
| `unsafeCheckOp` | 1 | none | throw UnsupportedOperationException |
| `unsafeCheckOpNoThrow` | 1 | none | throw UnsupportedOperationException |
| `unsafeCheckOpRaw` | 1 | none | throw UnsupportedOperationException |
| `unsafeCheckOpRawNoThrow` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 8 methods that have score >= 5
2. Stub 10 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.AppOpsManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.AppOpsManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 18 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
