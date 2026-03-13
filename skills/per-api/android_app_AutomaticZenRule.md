# SKILL: android.app.AutomaticZenRule

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.AutomaticZenRule`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.AutomaticZenRule` |
| **Package** | `android.app` |
| **Total Methods** | 17 |
| **Avg Score** | 5.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 7 (41%) |
| **Partial/Composite** | 7 (41%) |
| **No Mapping** | 3 (17%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 11 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isEnabled` | `boolean isEnabled()` | 8 | direct | easy | `isEnabled` | `readonly isEnabled?: boolean` |
| `getCreationTime` | `long getCreationTime()` | 7 | near | easy | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `getName` | `String getName()` | 7 | near | easy | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `getConditionId` | `android.net.Uri getConditionId()` | 7 | near | moderate | `getId` | `getId(uri: string): number` |
| `getOwner` | `android.content.ComponentName getOwner()` | 7 | near | moderate | `getWant` | `getWant(callback: AsyncCallback<Want>): void` |
| `setConfigurationActivity` | `void setConfigurationActivity(@Nullable android.content.ComponentName)` | 7 | near | moderate | `updateConfiguration` | `updateConfiguration(config: Configuration, callback: AsyncCallback<void>): void` |
| `setName` | `void setName(String)` | 6 | near | moderate | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `getInterruptionFilter` | `int getInterruptionFilter()` | 6 | partial | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `getZenPolicy` | `android.service.notification.ZenPolicy getZenPolicy()` | 6 | partial | moderate | `getTopAbility` | `getTopAbility(): Promise<ElementName>` |
| `setInterruptionFilter` | `void setInterruptionFilter(int)` | 5 | partial | moderate | `getRunningFormInfosByFilter` | `getRunningFormInfosByFilter(formProviderFilter: formInfo.FormProviderFilter): Promise<Array<formInfo.RunningFormInfo>>` |
| `setZenPolicy` | `void setZenPolicy(android.service.notification.ZenPolicy)` | 5 | partial | moderate | `setRouterProxy` | `setRouterProxy(formIds: Array<string>, proxy: Callback<Want>, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setConditionId` | 4 | partial | Log warning + no-op |
| `setEnabled` | 4 | partial | Log warning + no-op |
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `AutomaticZenRule` | 1 | none | throw UnsupportedOperationException |
| `AutomaticZenRule` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 11 methods that have score >= 5
2. Stub 6 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.AutomaticZenRule`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.AutomaticZenRule` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 17 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 11 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
