# SKILL: android.os.Binder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.Binder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.Binder` |
| **Package** | `android.os` |
| **Total Methods** | 23 |
| **Avg Score** | 4.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (17%) |
| **Partial/Composite** | 15 (65%) |
| **No Mapping** | 4 (17%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 13 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `attachInterface` | `void attachInterface(@Nullable android.os.IInterface, @Nullable String)` | 7 | near | easy | `setInterface` | `setInterface(pipe: USBDevicePipe, iface: USBInterface): number` |
| `getCallingPid` | `static final int getCallingPid()` | 7 | near | moderate | `getCalendar` | `getCalendar(locale: string, type?: string): Calendar` |
| `getCallingUid` | `static final int getCallingUid()` | 7 | near | moderate | `getCalendar` | `getCalendar(locale: string, type?: string): Calendar` |
| `unlinkToDeath` | `boolean unlinkToDeath(@NonNull android.os.IBinder.DeathRecipient, int)` | 6 | near | moderate | `unlink` | `unlink(path: string): Promise<void>` |
| `isBinderAlive` | `boolean isBinderAlive()` | 6 | partial | moderate | `isIdleState` | `isIdleState(bundleName: string, callback: AsyncCallback<boolean>): void` |
| `getCallingUidOrThrow` | `static final int getCallingUidOrThrow()` | 6 | partial | moderate | `getCalendar` | `getCalendar(locale: string, type?: string): Calendar` |
| `clearCallingIdentity` | `static final long clearCallingIdentity()` | 6 | partial | moderate | `identity` | `identity(): Matrix4Transit` |
| `getCallingWorkSourceUid` | `static final int getCallingWorkSourceUid()` | 6 | partial | moderate | `getLocatingRequiredData` | `getLocatingRequiredData(config: LocatingRequiredDataConfig): Promise<Array<LocatingRequiredData>>` |
| `onTransact` | `boolean onTransact(int, @NonNull android.os.Parcel, @Nullable android.os.Parcel, int) throws android.os.RemoteException` | 6 | partial | moderate | `onCancel` | `onCancel?: (data: SubscribeCallbackData) => void` |
| `restoreCallingIdentity` | `static final void restoreCallingIdentity(long)` | 6 | partial | moderate | `restoreDefault` | `restoreDefault(): void` |
| `joinThreadPool` | `static final void joinThreadPool()` | 5 | partial | moderate | `getThreadPriority` | `getThreadPriority(v: number): number` |
| `dump` | `void dump(@NonNull java.io.FileDescriptor, @Nullable String[])` | 5 | partial | moderate | `dumpHeapData` | `dumpHeapData(filename: string): void` |
| `dump` | `void dump(@NonNull java.io.FileDescriptor, @NonNull java.io.PrintWriter, @Nullable String[])` | 5 | partial | moderate | `dumpHeapData` | `dumpHeapData(filename: string): void` |

## Stub APIs (score < 5): 10 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setCallingWorkSourceUid` | 5 | partial | Log warning + no-op |
| `dumpAsync` | 5 | partial | throw UnsupportedOperationException |
| `restoreCallingWorkSource` | 5 | partial | throw UnsupportedOperationException |
| `flushPendingCommands` | 5 | partial | throw UnsupportedOperationException |
| `clearCallingWorkSource` | 4 | partial | throw UnsupportedOperationException |
| `linkToDeath` | 4 | composite | throw UnsupportedOperationException |
| `Binder` | 1 | none | throw UnsupportedOperationException |
| `Binder` | 1 | none | throw UnsupportedOperationException |
| `pingBinder` | 1 | none | throw UnsupportedOperationException |
| `transact` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 13 methods that have score >= 5
2. Stub 10 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.Binder`:


## Quality Gates

Before marking `android.os.Binder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 23 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 13 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
