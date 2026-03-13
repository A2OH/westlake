# SKILL: android.app.AlarmManager.AlarmClockInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.AlarmManager.AlarmClockInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.AlarmManager.AlarmClockInfo` |
| **Package** | `android.app.AlarmManager` |
| **Total Methods** | 5 |
| **Avg Score** | 4.8 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 2 (40%) |
| **Partial/Composite** | 2 (40%) |
| **No Mapping** | 1 (20%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getShowIntent` | `android.app.PendingIntent getShowIntent()` | 7 | near | easy | `getWantAgent` | `getWantAgent(info: WantAgentInfo, callback: AsyncCallback<WantAgent>): void` |
| `getTriggerTime` | `long getTriggerTime()` | 7 | near | moderate | `trigger` | `trigger(agent: WantAgent, triggerInfo: TriggerInfo, callback?: AsyncCallback<CompleteData>): void` |
| `AlarmClockInfo` | `AlarmManager.AlarmClockInfo(long, android.app.PendingIntent)` | 6 | partial | moderate | `getAllFormsInfo` | `getAllFormsInfo(callback: AsyncCallback<Array<formInfo.FormInfo>>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 3 | composite | Log warning + no-op |
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

Check if these related classes are already shimmed before generating `android.app.AlarmManager.AlarmClockInfo`:


## Quality Gates

Before marking `android.app.AlarmManager.AlarmClockInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
