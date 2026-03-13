# SKILL: android.os.Handler

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.Handler`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.Handler` |
| **Package** | `android.os` |
| **Total Methods** | 26 |
| **Avg Score** | 5.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 13 (50%) |
| **Partial/Composite** | 10 (38%) |
| **No Mapping** | 3 (11%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 22 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `sendMessage` | `final boolean sendMessage(@NonNull android.os.Message)` | 8 | near | easy | `message` | `readonly message: string` |
| `dispatchMessage` | `void dispatchMessage(@NonNull android.os.Message)` | 8 | near | easy | `disableMessage` | `readonly disableMessage: string` |
| `removeCallbacks` | `final void removeCallbacks(@NonNull Runnable)` | 8 | near | easy | `removeAll` | `removeAll(bundle: BundleOption, callback: AsyncCallback<void>): void` |
| `removeCallbacks` | `final void removeCallbacks(@NonNull Runnable, @Nullable Object)` | 8 | near | easy | `removeAll` | `removeAll(bundle: BundleOption, callback: AsyncCallback<void>): void` |
| `handleMessage` | `void handleMessage(@NonNull android.os.Message)` | 7 | near | easy | `disableMessage` | `readonly disableMessage: string` |
| `postAtTime` | `final boolean postAtTime(@NonNull Runnable, long)` | 7 | near | easy | `setTime` | `setTime(time: number, callback: AsyncCallback<void>): void` |
| `postAtTime` | `final boolean postAtTime(@NonNull Runnable, @Nullable Object, long)` | 7 | near | easy | `setTime` | `setTime(time: number, callback: AsyncCallback<void>): void` |
| `postDelayed` | `final boolean postDelayed(@NonNull Runnable, long)` | 6 | near | moderate | `executeDelayed` | `executeDelayed(delayTime: number, task: Task, priority?: Priority): Promise<Object>` |
| `postDelayed` | `final boolean postDelayed(@NonNull Runnable, @Nullable Object, long)` | 6 | near | moderate | `executeDelayed` | `executeDelayed(delayTime: number, task: Task, priority?: Priority): Promise<Object>` |
| `sendMessageDelayed` | `final boolean sendMessageDelayed(@NonNull android.os.Message, long)` | 6 | near | moderate | `executeDelayed` | `executeDelayed(delayTime: number, task: Task, priority?: Priority): Promise<Object>` |
| `removeMessages` | `final void removeMessages(int)` | 6 | near | moderate | `removeProcessor` | `removeProcessor(id: number): void` |
| `removeMessages` | `final void removeMessages(int, @Nullable Object)` | 6 | near | moderate | `removeProcessor` | `removeProcessor(id: number): void` |
| `sendEmptyMessage` | `final boolean sendEmptyMessage(int)` | 6 | near | moderate | `statusMessage` | `statusMessage: string` |
| `sendEmptyMessageDelayed` | `final boolean sendEmptyMessageDelayed(int, long)` | 6 | partial | moderate | `executeDelayed` | `executeDelayed(delayTime: number, task: Task, priority?: Priority): Promise<Object>` |
| `sendMessageAtTime` | `boolean sendMessageAtTime(@NonNull android.os.Message, long)` | 6 | partial | moderate | `setTime` | `setTime(time: number, callback: AsyncCallback<void>): void` |
| `removeCallbacksAndMessages` | `final void removeCallbacksAndMessages(@Nullable Object)` | 6 | partial | moderate | `removeAllSlots` | `removeAllSlots(callback: AsyncCallback<void>): void` |
| `sendEmptyMessageAtTime` | `final boolean sendEmptyMessageAtTime(int, long)` | 5 | partial | moderate | `statusMessage` | `statusMessage: string` |
| `dump` | `final void dump(@NonNull android.util.Printer, @NonNull String)` | 5 | partial | moderate | `dumpHeapData` | `dumpHeapData(filename: string): void` |
| `hasCallbacks` | `final boolean hasCallbacks(@NonNull Runnable)` | 5 | partial | moderate | `hasRight` | `hasRight(deviceName: string): boolean` |
| `hasMessages` | `final boolean hasMessages(int)` | 5 | partial | moderate | `hasRight` | `hasRight(deviceName: string): boolean` |
| `hasMessages` | `final boolean hasMessages(int, @Nullable Object)` | 5 | partial | moderate | `hasRight` | `hasRight(deviceName: string): boolean` |
| `sendMessageAtFrontOfQueue` | `final boolean sendMessageAtFrontOfQueue(@NonNull android.os.Message)` | 5 | partial | moderate | `messageCode` | `messageCode: number` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `postAtFrontOfQueue` | 4 | partial | Store callback, never fire |
| `Handler` | 1 | none | throw UnsupportedOperationException |
| `Handler` | 1 | none | throw UnsupportedOperationException |
| `post` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 22 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.Handler`:

- `android.os.Looper` (not yet shimmed)
- `android.os.Message` (not yet shimmed)

## Quality Gates

Before marking `android.os.Handler` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 26 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 22 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
