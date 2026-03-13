# SKILL: android.app.ApplicationExitInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.ApplicationExitInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.ApplicationExitInfo` |
| **Package** | `android.app` |
| **Total Methods** | 12 |
| **Avg Score** | 5.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 5 (41%) |
| **Partial/Composite** | 6 (50%) |
| **No Mapping** | 1 (8%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 10 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getPid` | `int getPid()` | 9 | direct | trivial | `getId` | `getId(uri: string): number` |
| `getRealUid` | `int getRealUid()` | 8 | near | easy | `getUid` | `getUid(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `getDefiningUid` | `int getDefiningUid()` | 7 | near | moderate | `getUid` | `getUid(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `getPackageUid` | `int getPackageUid()` | 7 | near | moderate | `getUid` | `getUid(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `getReason` | `int getReason()` | 6 | near | moderate | `getWant` | `getWant(callback: AsyncCallback<Want>): void` |
| `getStatus` | `int getStatus()` | 6 | partial | moderate | `getWantAgent` | `getWantAgent(info: WantAgentInfo, callback: AsyncCallback<WantAgent>): void` |
| `getRss` | `long getRss()` | 6 | partial | moderate | `getArguments` | `getArguments(): AbilityDelegatorArgs` |
| `getPss` | `long getPss()` | 5 | partial | moderate | `getId` | `getId(uri: string): number` |
| `getTimestamp` | `long getTimestamp()` | 5 | partial | moderate | `getContext` | `getContext(): Context` |
| `getImportance` | `int getImportance()` | 5 | partial | moderate | `getContext` | `getContext(): Context` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 10 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.ApplicationExitInfo`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.ApplicationExitInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 12 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 10 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
