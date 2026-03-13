# SKILL: android.app.VoiceInteractor

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.VoiceInteractor`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.VoiceInteractor` |
| **Package** | `android.app` |
| **Total Methods** | 9 |
| **Avg Score** | 5.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (22%) |
| **Partial/Composite** | 6 (66%) |
| **No Mapping** | 1 (11%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getActiveRequest` | `android.app.VoiceInteractor.Request getActiveRequest(String)` | 7 | near | moderate | `getRequestInfo` | `getRequestInfo(want: Want): RequestInfo` |
| `getActiveRequests` | `android.app.VoiceInteractor.Request[] getActiveRequests()` | 6 | near | moderate | `getRequestInfo` | `getRequestInfo(want: Want): RequestInfo` |
| `submitRequest` | `boolean submitRequest(android.app.VoiceInteractor.Request)` | 6 | partial | moderate | `getRequestInfo` | `getRequestInfo(want: Want): RequestInfo` |
| `submitRequest` | `boolean submitRequest(android.app.VoiceInteractor.Request, String)` | 6 | partial | moderate | `getRequestInfo` | `getRequestInfo(want: Want): RequestInfo` |
| `registerOnDestroyedCallback` | `boolean registerOnDestroyedCallback(@NonNull java.util.concurrent.Executor, @NonNull Runnable)` | 6 | partial | moderate | `getRequestCallback` | `getRequestCallback(want: Want): RequestCallback` |
| `unregisterOnDestroyedCallback` | `boolean unregisterOnDestroyedCallback(@NonNull Runnable)` | 6 | partial | moderate | `unregisterMissionListener` | `unregisterMissionListener(listenerId: number, callback: AsyncCallback<void>): void` |
| `isDestroyed` | `boolean isDestroyed()` | 5 | partial | moderate | `isSystemReady` | `isSystemReady(callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `notifyDirectActionsChanged` | 5 | partial | Store callback, never fire |
| `supportsCommands` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 7 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.VoiceInteractor`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.VoiceInteractor` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
