# SKILL: android.net.SocketKeepalive.Callback

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.SocketKeepalive.Callback`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.SocketKeepalive.Callback` |
| **Package** | `android.net.SocketKeepalive` |
| **Total Methods** | 5 |
| **Avg Score** | 5.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (20%) |
| **Partial/Composite** | 4 (80%) |
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
| `Callback` | `SocketKeepalive.Callback()` | 8 | direct | easy | `callback` | `callback: AsyncCallback<boolean>): void` |
| `onError` | `void onError(int)` | 5 | partial | moderate | `on` | `on(type: 'interfaceStateChange', callback: Callback<InterfaceStateInfo>): void` |
| `onStarted` | `void onStarted()` | 5 | partial | moderate | `on` | `on(type: 'interfaceStateChange', callback: Callback<InterfaceStateInfo>): void` |
| `onStopped` | `void onStopped()` | 5 | partial | moderate | `on` | `on(type: 'interfaceStateChange', callback: Callback<InterfaceStateInfo>): void` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onDataReceived` | 3 | composite | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 4 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.net.SocketKeepalive.Callback`:


## Quality Gates

Before marking `android.net.SocketKeepalive.Callback` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
