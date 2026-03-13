# SKILL: android.net.NetworkRequest.Builder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.NetworkRequest.Builder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.NetworkRequest.Builder` |
| **Package** | `android.net.NetworkRequest` |
| **Total Methods** | 7 |
| **Avg Score** | 4.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 5 (71%) |
| **No Mapping** | 2 (28%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `removeTransportType` | `android.net.NetworkRequest.Builder removeTransportType(int)` | 6 | partial | moderate | `removeCustomDnsRule` | `removeCustomDnsRule(host: string, callback: AsyncCallback<void>): void` |
| `setNetworkSpecifier` | `android.net.NetworkRequest.Builder setNetworkSpecifier(android.net.NetworkSpecifier)` | 6 | partial | moderate | `setNetQuotaPolicies` | `setNetQuotaPolicies(quotaPolicies: Array<NetQuotaPolicy>, callback: AsyncCallback<void>): void` |
| `removeCapability` | `android.net.NetworkRequest.Builder removeCapability(int)` | 5 | partial | moderate | `removeLocalService` | `removeLocalService(context: Context, serviceInfo: LocalServiceInfo,
    callback: AsyncCallback<LocalServiceInfo>): void` |
| `addTransportType` | `android.net.NetworkRequest.Builder addTransportType(int)` | 5 | partial | moderate | `addCustomDnsRule` | `addCustomDnsRule(host: string, ip: Array<string>, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `addCapability` | 4 | partial | Log warning + no-op |
| `Builder` | 1 | none | throw UnsupportedOperationException |
| `build` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 4 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.net.NetworkRequest.Builder`:


## Quality Gates

Before marking `android.net.NetworkRequest.Builder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
