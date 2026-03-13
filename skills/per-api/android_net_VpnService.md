# SKILL: android.net.VpnService

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.VpnService`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.VpnService` |
| **Package** | `android.net` |
| **Total Methods** | 10 |
| **Avg Score** | 4.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (30%) |
| **Partial/Composite** | 4 (40%) |
| **No Mapping** | 3 (30%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `prepare` | `static android.content.Intent prepare(android.content.Context)` | 8 | direct | easy | `prepare` | `prepare(callback: AsyncCallback<void>): void` |
| `isAlwaysOn` | `final boolean isAlwaysOn()` | 7 | near | moderate | `isAlwaysOnVpnEnabled` | `isAlwaysOnVpnEnabled(bundleName: string): Promise<boolean>` |
| `isLockdownEnabled` | `final boolean isLockdownEnabled()` | 6 | near | moderate | `isAlwaysOnVpnEnabled` | `isAlwaysOnVpnEnabled(bundleName: string): Promise<boolean>` |
| `VpnService` | `VpnService()` | 6 | partial | moderate | `removeLocalService` | `removeLocalService(context: Context, serviceInfo: LocalServiceInfo,
    callback: AsyncCallback<LocalServiceInfo>): void` |
| `onBind` | `android.os.IBinder onBind(android.content.Intent)` | 5 | partial | moderate | `on` | `on(type: 'interfaceStateChange', callback: Callback<InterfaceStateInfo>): void` |
| `onRevoke` | `void onRevoke()` | 5 | partial | moderate | `on` | `on(type: 'interfaceStateChange', callback: Callback<InterfaceStateInfo>): void` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setUnderlyingNetworks` | 5 | partial | Log warning + no-op |
| `protect` | 1 | none | throw UnsupportedOperationException |
| `protect` | 1 | none | throw UnsupportedOperationException |
| `protect` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 6 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.net.VpnService`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.VpnService` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
