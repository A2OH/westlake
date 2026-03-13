# SKILL: android.app.NotificationChannelGroup

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.NotificationChannelGroup`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.NotificationChannelGroup` |
| **Package** | `android.app` |
| **Total Methods** | 10 |
| **Avg Score** | 4.5 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 3 (30%) |
| **Partial/Composite** | 4 (40%) |
| **No Mapping** | 3 (30%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getId` | `String getId()` | 10 | direct | trivial | `getId` | `getId(uri: string): number` |
| `getName` | `CharSequence getName()` | 7 | near | easy | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `getDescription` | `String getDescription()` | 6 | near | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `getChannels` | `java.util.List<android.app.NotificationChannel> getChannels()` | 6 | partial | moderate | `getContext` | `getContext(): Context` |
| `setDescription` | `void setDescription(String)` | 6 | partial | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isBlocked` | 4 | composite | Return safe default (null/false/0/empty) |
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `NotificationChannelGroup` | 1 | none | Store callback, never fire |
| `clone` | 1 | none | Store callback, never fire |
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

Check if these related classes are already shimmed before generating `android.app.NotificationChannelGroup`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.NotificationChannelGroup` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
