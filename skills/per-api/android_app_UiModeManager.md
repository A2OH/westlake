# SKILL: android.app.UiModeManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.UiModeManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.UiModeManager` |
| **Package** | `android.app` |
| **Total Methods** | 7 |
| **Avg Score** | 5.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (28%) |
| **Partial/Composite** | 5 (71%) |
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
| `disableCarMode` | `void disableCarMode(int)` | 7 | near | moderate | `disableFormsUpdate` | `disableFormsUpdate(formIds: Array<string>, callback: AsyncCallback<void>): void` |
| `enableCarMode` | `void enableCarMode(int)` | 7 | near | moderate | `enableFormsUpdate` | `enableFormsUpdate(formIds: Array<string>, callback: AsyncCallback<void>): void` |
| `getNightMode` | `int getNightMode()` | 6 | partial | moderate | `getId` | `getId(uri: string): number` |
| `getCurrentModeType` | `int getCurrentModeType()` | 6 | partial | moderate | `getContext` | `getContext(): Context` |
| `setCustomNightModeStart` | `void setCustomNightModeStart(@NonNull java.time.LocalTime)` | 5 | partial | moderate | `setApplicationAutoStartup` | `setApplicationAutoStartup(info: AutoStartupInfo, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setNightMode` | 5 | partial | Log warning + no-op |
| `setCustomNightModeEnd` | 5 | partial | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.UiModeManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.UiModeManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
