# SKILL: android.os.MessageQueue

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.MessageQueue`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.MessageQueue` |
| **Package** | `android.os` |
| **Total Methods** | 5 |
| **Avg Score** | 5.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (40%) |
| **Partial/Composite** | 3 (60%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isIdle` | `boolean isIdle()` | 7 | near | easy | `isIdleState` | `isIdleState(bundleName: string, callback: AsyncCallback<boolean>): void` |
| `removeIdleHandler` | `void removeIdleHandler(@NonNull android.os.MessageQueue.IdleHandler)` | 6 | near | moderate | `removeAll` | `removeAll(bundle: BundleOption, callback: AsyncCallback<void>): void` |
| `removeOnFileDescriptorEventListener` | `void removeOnFileDescriptorEventListener(@NonNull java.io.FileDescriptor)` | 6 | partial | moderate | `getFileDescriptor` | `getFileDescriptor(pipe: USBDevicePipe): number` |
| `addOnFileDescriptorEventListener` | `void addOnFileDescriptorEventListener(@NonNull java.io.FileDescriptor, int, @NonNull android.os.MessageQueue.OnFileDescriptorEventListener)` | 6 | partial | moderate | `getFileDescriptor` | `getFileDescriptor(pipe: USBDevicePipe): number` |
| `addIdleHandler` | `void addIdleHandler(@NonNull android.os.MessageQueue.IdleHandler)` | 5 | partial | moderate | `addWatcher` | `addWatcher(watcher: Watcher): void` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 5 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.MessageQueue`:


## Quality Gates

Before marking `android.os.MessageQueue` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
