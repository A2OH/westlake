# SKILL: android.app.VoiceInteractor.Prompt

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.VoiceInteractor.Prompt`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.VoiceInteractor.Prompt` |
| **Package** | `android.app.VoiceInteractor` |
| **Total Methods** | 12 |
| **Avg Score** | 5.0 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 5 (41%) |
| **Partial/Composite** | 3 (25%) |
| **No Mapping** | 4 (33%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `cancel` | `void cancel()` | 10 | direct | trivial | `cancel` | `cancel(agent: WantAgent, callback: AsyncCallback<void>): void` |
| `getContext` | `android.content.Context getContext()` | 10 | direct | trivial | `getContext` | `getContext(): Context` |
| `onCancel` | `void onCancel()` | 9 | direct | easy | `cancel` | `cancel(agent: WantAgent, callback: AsyncCallback<void>): void` |
| `getName` | `String getName()` | 7 | near | easy | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `getActivity` | `android.app.Activity getActivity()` | 7 | near | moderate | `getTopAbility` | `getTopAbility(): Promise<ElementName>` |
| `onDetached` | `void onDetached()` | 5 | partial | moderate | `onPrepare` | `onPrepare(): void` |
| `onAttached` | `void onAttached(android.app.Activity)` | 5 | partial | moderate | `on` | `on(type: 'abilityForegroundState', observer: AbilityForegroundStateObserver): void` |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `Prompt` | 1 | none | throw UnsupportedOperationException |
| `Prompt` | 1 | none | throw UnsupportedOperationException |
| `countVoicePrompts` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.app.VoiceInteractor.Prompt`:


## Quality Gates

Before marking `android.app.VoiceInteractor.Prompt` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 12 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
