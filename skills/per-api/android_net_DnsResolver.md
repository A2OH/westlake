# SKILL: android.net.DnsResolver

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.DnsResolver`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.DnsResolver` |
| **Package** | `android.net` |
| **Total Methods** | 4 |
| **Avg Score** | 4.0 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 2 (50%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 2 (50%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `query` | `void query(@Nullable android.net.Network, @NonNull String, int, @NonNull java.util.concurrent.Executor, @Nullable android.os.CancellationSignal, @NonNull android.net.DnsResolver.Callback<? super java.util.List<java.net.InetAddress>>)` | 7 | near | easy | `query` | `query(faultType: FaultType, callback: AsyncCallback<Array<FaultLogInfo>>): void` |
| `query` | `void query(@Nullable android.net.Network, @NonNull String, int, int, @NonNull java.util.concurrent.Executor, @Nullable android.os.CancellationSignal, @NonNull android.net.DnsResolver.Callback<? super java.util.List<java.net.InetAddress>>)` | 7 | near | easy | `query` | `query(faultType: FaultType, callback: AsyncCallback<Array<FaultLogInfo>>): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `rawQuery` | 1 | none | Return safe default (null/false/0/empty) |
| `rawQuery` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.net.DnsResolver`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.DnsResolver` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
