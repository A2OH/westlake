# SKILL: android.os.PowerManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.PowerManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.PowerManager` |
| **Package** | `android.os` |
| **Total Methods** | 14 |
| **Avg Score** | 6.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 8 (57%) |
| **Partial/Composite** | 6 (42%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 14 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isInteractive` | `boolean isInteractive()` | 10 | direct | trivial | `isActive` | `isActive(): boolean` |
| `isPowerSaveMode` | `boolean isPowerSaveMode()` | 9 | direct | trivial | `setPowerMode` | `setPowerMode(mode: DevicePowerMode, callback: AsyncCallback<void>): void` |
| `getLocationPowerSaveMode` | `int getLocationPowerSaveMode()` | 9 | direct | easy | `getPowerMode` | `getPowerMode(): DevicePowerMode` |
| `isDeviceIdleMode` | `boolean isDeviceIdleMode()` | 7 | near | easy | `setPowerMode` | `setPowerMode(mode: DevicePowerMode, callback: AsyncCallback<void>): void` |
| `newWakeLock` | `android.os.PowerManager.WakeLock newWakeLock(int, String)` | 7 | near | moderate | `wakeup` | `wakeup(detail: string): void` |
| `getThermalHeadroom` | `float getThermalHeadroom(@IntRange(from=0, to=60) int)` | 7 | near | moderate | `getPowerMode` | `getPowerMode(): DevicePowerMode` |
| `isRebootingUserspaceSupported` | `boolean isRebootingUserspaceSupported()` | 6 | near | moderate | `rebootDevice` | `rebootDevice(reason: string): void` |
| `isWakeLockLevelSupported` | `boolean isWakeLockLevelSupported(int)` | 6 | near | moderate | `wakeup` | `wakeup(detail: string): void` |
| `isSustainedPerformanceModeSupported` | `boolean isSustainedPerformanceModeSupported()` | 6 | partial | moderate | `setPowerMode` | `setPowerMode(mode: DevicePowerMode, callback: AsyncCallback<void>): void` |
| `getCurrentThermalStatus` | `int getCurrentThermalStatus()` | 5 | partial | moderate | `getPowerMode` | `getPowerMode(): DevicePowerMode` |
| `isIgnoringBatteryOptimizations` | `boolean isIgnoringBatteryOptimizations(String)` | 5 | partial | moderate | `getBatteryStats` | `getBatteryStats(): Promise<Array<BatteryStatsInfo>>` |
| `addThermalStatusListener` | `void addThermalStatusListener(@NonNull android.os.PowerManager.OnThermalStatusChangedListener)` | 5 | partial | moderate | `suspend` | `suspend(isImmediate?: boolean): void` |
| `addThermalStatusListener` | `void addThermalStatusListener(@NonNull java.util.concurrent.Executor, @NonNull android.os.PowerManager.OnThermalStatusChangedListener)` | 5 | partial | moderate | `suspend` | `suspend(isImmediate?: boolean): void` |
| `removeThermalStatusListener` | `void removeThermalStatusListener(@NonNull android.os.PowerManager.OnThermalStatusChangedListener)` | 5 | partial | moderate | `getPowerMode` | `getPowerMode(): DevicePowerMode` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 14 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.PowerManager`:


## Quality Gates

Before marking `android.os.PowerManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 14 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
