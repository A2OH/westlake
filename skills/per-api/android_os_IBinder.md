# SKILL: android.os.IBinder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.IBinder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.IBinder` |
| **Package** | `android.os` |
| **Total Methods** | 8 |
| **Avg Score** | 4.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (12%) |
| **Partial/Composite** | 5 (62%) |
| **No Mapping** | 2 (25%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `unlinkToDeath` | `boolean unlinkToDeath(@NonNull android.os.IBinder.DeathRecipient, int)` | 6 | near | moderate | `unlink` | `unlink(path: string): Promise<void>` |
| `isBinderAlive` | `boolean isBinderAlive()` | 6 | partial | moderate | `isIdleState` | `isIdleState(bundleName: string, callback: AsyncCallback<boolean>): void` |
| `getSuggestedMaxIpcSizeBytes` | `static int getSuggestedMaxIpcSizeBytes()` | 5 | partial | moderate | `getSystemPasteboard` | `getSystemPasteboard(): SystemPasteboard` |
| `dump` | `void dump(@NonNull java.io.FileDescriptor, @Nullable String[]) throws android.os.RemoteException` | 5 | partial | moderate | `dumpHeapData` | `dumpHeapData(filename: string): void` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `dumpAsync` | 5 | partial | throw UnsupportedOperationException |
| `linkToDeath` | 4 | composite | throw UnsupportedOperationException |
| `pingBinder` | 1 | none | throw UnsupportedOperationException |
| `transact` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 4 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.IBinder`:


## Quality Gates

Before marking `android.os.IBinder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
