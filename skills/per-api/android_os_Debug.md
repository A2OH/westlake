# SKILL: android.os.Debug

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.Debug`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.Debug` |
| **Package** | `android.os` |
| **Total Methods** | 30 |
| **Avg Score** | 6.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 18 (60%) |
| **Partial/Composite** | 12 (40%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 27 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getNativeHeapAllocatedSize` | `static long getNativeHeapAllocatedSize()` | 10 | direct | trivial | `getNativeHeapAllocatedSize` | `getNativeHeapAllocatedSize(): bigint` |
| `getNativeHeapFreeSize` | `static long getNativeHeapFreeSize()` | 10 | direct | trivial | `getNativeHeapFreeSize` | `getNativeHeapFreeSize(): bigint` |
| `getNativeHeapSize` | `static long getNativeHeapSize()` | 10 | direct | trivial | `getNativeHeapSize` | `getNativeHeapSize(): bigint` |
| `getPss` | `static long getPss()` | 10 | direct | trivial | `getPss` | `getPss(): bigint` |
| `dumpHprofData` | `static void dumpHprofData(String) throws java.io.IOException` | 8 | direct | easy | `dumpHeapData` | `dumpHeapData(filename: string): void` |
| `isDebuggerConnected` | `static boolean isDebuggerConnected()` | 7 | near | easy | `isConnected` | `isConnected(): boolean` |
| `getRuntimeStat` | `static String getRuntimeStat(String)` | 7 | near | moderate | `getUptime` | `getUptime(timeType: TimeType, isNanoseconds?: boolean): number` |
| `getMemoryInfo` | `static void getMemoryInfo(android.os.Debug.MemoryInfo)` | 7 | near | moderate | `getRemoteAbilityInfo` | `getRemoteAbilityInfo(elementName: ElementName, callback: AsyncCallback<RemoteAbilityInfo>): void` |
| `getRuntimeStats` | `static java.util.Map<java.lang.String,java.lang.String> getRuntimeStats()` | 7 | near | moderate | `getBatteryStats` | `getBatteryStats(): Promise<Array<BatteryStatsInfo>>` |
| `startMethodTracing` | `static void startMethodTracing()` | 6 | near | moderate | `startTrace` | `startTrace(name: string, taskId: number, expectedTime?: number): void` |
| `startMethodTracing` | `static void startMethodTracing(String)` | 6 | near | moderate | `startTrace` | `startTrace(name: string, taskId: number, expectedTime?: number): void` |
| `startMethodTracing` | `static void startMethodTracing(String, int)` | 6 | near | moderate | `startTrace` | `startTrace(name: string, taskId: number, expectedTime?: number): void` |
| `startMethodTracing` | `static void startMethodTracing(String, int, int)` | 6 | near | moderate | `startTrace` | `startTrace(name: string, taskId: number, expectedTime?: number): void` |
| `startNativeTracing` | `static void startNativeTracing()` | 6 | near | moderate | `startTrace` | `startTrace(name: string, taskId: number, expectedTime?: number): void` |
| `getBinderSentTransactions` | `static int getBinderSentTransactions()` | 6 | near | moderate | `getCurrentFunctions` | `getCurrentFunctions(): FunctionType` |
| `stopNativeTracing` | `static void stopNativeTracing()` | 6 | near | moderate | `stopTimer` | `stopTimer(timer: number, callback: AsyncCallback<void>): void` |
| `getLoadedClassCount` | `static int getLoadedClassCount()` | 6 | near | moderate | `getDisplayCountry` | `getDisplayCountry(country: string, locale: string, sentenceCase?: boolean): string` |
| `dumpService` | `static boolean dumpService(String, java.io.FileDescriptor, String[])` | 6 | near | moderate | `newSEService` | `newSEService(type: 'serviceState', callback: Callback<ServiceState>): SEService` |
| `getBinderDeathObjectCount` | `static int getBinderDeathObjectCount()` | 6 | partial | moderate | `getListenerCount` | `getListenerCount(eventId: number | string): number` |
| `getBinderLocalObjectCount` | `static int getBinderLocalObjectCount()` | 6 | partial | moderate | `getListenerCount` | `getListenerCount(eventId: number | string): number` |
| `getBinderProxyObjectCount` | `static int getBinderProxyObjectCount()` | 6 | partial | moderate | `getListenerCount` | `getListenerCount(eventId: number | string): number` |
| `getBinderReceivedTransactions` | `static int getBinderReceivedTransactions()` | 6 | partial | moderate | `getCurrentFunctions` | `getCurrentFunctions(): FunctionType` |
| `threadCpuTimeNanos` | `static long threadCpuTimeNanos()` | 6 | partial | moderate | `getPastCpuTime` | `getPastCpuTime(): number` |
| `attachJvmtiAgent` | `static void attachJvmtiAgent(@NonNull String, @Nullable String, @Nullable ClassLoader) throws java.io.IOException` | 6 | partial | moderate | `wantAgent` | `wantAgent?: WantAgent` |
| `startMethodTracingSampling` | `static void startMethodTracingSampling(String, int, int)` | 6 | partial | moderate | `startProfiling` | `startProfiling(filename: string): void` |
| `stopMethodTracing` | `static void stopMethodTracing()` | 5 | partial | moderate | `getInputMethodEngine` | `getInputMethodEngine(): InputMethodEngine` |
| `enableEmulatorTraceOutput` | `static void enableEmulatorTraceOutput()` | 5 | partial | moderate | `enableAlertBeforeBackPage` | `enableAlertBeforeBackPage(options: EnableAlertOptions): void` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `printLoadedClasses` | 4 | partial | throw UnsupportedOperationException |
| `waitingForDebugger` | 4 | partial | throw UnsupportedOperationException |
| `waitForDebugger` | 4 | composite | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 27 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.Debug`:


## Quality Gates

Before marking `android.os.Debug` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 30 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 27 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
