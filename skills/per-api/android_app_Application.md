# SKILL: android.app.Application

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.Application`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.Application` |
| **Package** | `android.app` |
| **Total Methods** | 6 |
| **Avg Score** | 6.4 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (66%) |
| **Partial/Composite** | 2 (33%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Application` | `Application()` | 7 | near | easy | `isApplicationRunning` | `isApplicationRunning(bundleName: string): Promise<boolean>` |
| `unregisterOnProvideAssistDataListener` | `void unregisterOnProvideAssistDataListener(android.app.Application.OnProvideAssistDataListener)` | 7 | near | easy | `unregisterMissionListener` | `unregisterMissionListener(listenerId: number, callback: AsyncCallback<void>): void` |
| `registerOnProvideAssistDataListener` | `void registerOnProvideAssistDataListener(android.app.Application.OnProvideAssistDataListener)` | 7 | near | moderate | `registerMissionListener` | `registerMissionListener(listener: MissionListener): number` |
| `getProcessName` | `static String getProcessName()` | 7 | near | moderate | `getProcessMemoryByPid` | `getProcessMemoryByPid(pid: number): Promise<number>` |
| `unregisterActivityLifecycleCallbacks` | `void unregisterActivityLifecycleCallbacks(android.app.Application.ActivityLifecycleCallbacks)` | 5 | partial | moderate | `unregisterMissionListener` | `unregisterMissionListener(listenerId: number, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `registerActivityLifecycleCallbacks` | 5 | partial | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.Application`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.Application` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 6 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
