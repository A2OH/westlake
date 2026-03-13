# SKILL: android.net.Credentials

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.Credentials`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.Credentials` |
| **Package** | `android.net` |
| **Total Methods** | 4 |
| **Avg Score** | 4.6 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 2 (50%) |
| **Partial/Composite** | 1 (25%) |
| **No Mapping** | 1 (25%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getUid` | `int getUid()` | 6 | near | moderate | `getUidRxBytes` | `getUidRxBytes(uid: number, callback: AsyncCallback<number>): void` |
| `getPid` | `int getPid()` | 6 | near | moderate | `getPolicyByUid` | `getPolicyByUid(uid: number, callback: AsyncCallback<NetUidPolicy>): void` |
| `getGid` | `int getGid()` | 5 | partial | moderate | `getUidRxBytes` | `getUidRxBytes(uid: number, callback: AsyncCallback<number>): void` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `Credentials` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.net.Credentials`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.Credentials` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
