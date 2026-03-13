# SKILL: android.net.LocalSocketAddress

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.LocalSocketAddress`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.LocalSocketAddress` |
| **Package** | `android.net` |
| **Total Methods** | 4 |
| **Avg Score** | 5.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (25%) |
| **Partial/Composite** | 3 (75%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getName` | `String getName()` | 6 | near | moderate | `getAppNet` | `getAppNet(callback: AsyncCallback<NetHandle>): void` |
| `LocalSocketAddress` | `LocalSocketAddress(String, android.net.LocalSocketAddress.Namespace)` | 6 | partial | moderate | `constructLocalSocketInstance` | `constructLocalSocketInstance(): LocalSocket` |
| `LocalSocketAddress` | `LocalSocketAddress(String)` | 6 | partial | moderate | `constructLocalSocketInstance` | `constructLocalSocketInstance(): LocalSocket` |
| `getNamespace` | `android.net.LocalSocketAddress.Namespace getNamespace()` | 6 | partial | moderate | `getAppNetSync` | `getAppNetSync(): NetHandle` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 4 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.net.LocalSocketAddress`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.LocalSocketAddress` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
