# SKILL: android.net.ProxyInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.ProxyInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.ProxyInfo` |
| **Package** | `android.net` |
| **Total Methods** | 11 |
| **Avg Score** | 4.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (18%) |
| **Partial/Composite** | 7 (63%) |
| **No Mapping** | 2 (18%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getExclusionList` | `String[] getExclusionList()` | 6 | near | moderate | `getDeviceIdleTrustlist` | `getDeviceIdleTrustlist(callback: AsyncCallback<Array<number>>): void` |
| `getPort` | `int getPort()` | 6 | near | moderate | `getAppNet` | `getAppNet(callback: AsyncCallback<NetHandle>): void` |
| `getHost` | `String getHost()` | 5 | partial | moderate | `getSharingState` | `getSharingState(type: SharingIfaceType, callback: AsyncCallback<SharingIfaceState>): void` |
| `getPacFileUrl` | `android.net.Uri getPacFileUrl()` | 5 | partial | moderate | `getAllNets` | `getAllNets(callback: AsyncCallback<Array<NetHandle>>): void` |
| `buildPacProxy` | `static android.net.ProxyInfo buildPacProxy(android.net.Uri)` | 5 | partial | moderate | `getGlobalHttpProxy` | `getGlobalHttpProxy(callback: AsyncCallback<HttpProxy>): void` |
| `isValid` | `boolean isValid()` | 5 | partial | moderate | `isSharing` | `isSharing(callback: AsyncCallback<boolean>): void` |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `buildDirectProxy` | 5 | partial | throw UnsupportedOperationException |
| `buildDirectProxy` | 5 | partial | throw UnsupportedOperationException |
| `ProxyInfo` | 4 | partial | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 6 methods that have score >= 5
2. Stub 5 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.net.ProxyInfo`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.ProxyInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
