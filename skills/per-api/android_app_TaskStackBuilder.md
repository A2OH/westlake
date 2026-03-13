# SKILL: android.app.TaskStackBuilder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.TaskStackBuilder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.TaskStackBuilder` |
| **Package** | `android.app` |
| **Total Methods** | 12 |
| **Avg Score** | 4.3 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 4 (33%) |
| **Partial/Composite** | 4 (33%) |
| **No Mapping** | 4 (33%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `create` | `static android.app.TaskStackBuilder create(android.content.Context)` | 8 | direct | easy | `create` | `create(config: PiPConfiguration): Promise<PiPController>` |
| `startActivities` | `void startActivities()` | 7 | near | moderate | `startAbility` | `startAbility(parameter: StartAbilityParameter, callback: AsyncCallback<number>): void` |
| `startActivities` | `void startActivities(android.os.Bundle)` | 7 | near | moderate | `startAbility` | `startAbility(parameter: StartAbilityParameter, callback: AsyncCallback<number>): void` |
| `getIntentCount` | `int getIntentCount()` | 6 | near | moderate | `getWantAgent` | `getWantAgent(info: WantAgentInfo, callback: AsyncCallback<WantAgent>): void` |
| `getPendingIntent` | `android.app.PendingIntent getPendingIntent(int, int)` | 6 | partial | moderate | `getProcessRunningInfos` | `getProcessRunningInfos(): Promise<Array<ProcessRunningInfo>>` |
| `getPendingIntent` | `android.app.PendingIntent getPendingIntent(int, int, android.os.Bundle)` | 6 | partial | moderate | `getProcessRunningInfos` | `getProcessRunningInfos(): Promise<Array<ProcessRunningInfo>>` |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `addNextIntentWithParentStack` | 5 | partial | Log warning + no-op |
| `addNextIntent` | 3 | composite | Log warning + no-op |
| `addParentStack` | 1 | none | Log warning + no-op |
| `addParentStack` | 1 | none | Log warning + no-op |
| `addParentStack` | 1 | none | Log warning + no-op |
| `editIntentAt` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.app.TaskStackBuilder`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.TaskStackBuilder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 12 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
