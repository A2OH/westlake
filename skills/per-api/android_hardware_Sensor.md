# SKILL: android.hardware.Sensor

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.hardware.Sensor`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.hardware.Sensor` |
| **Package** | `android.hardware` |
| **Total Methods** | 19 |
| **Avg Score** | 6.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 15 (78%) |
| **Partial/Composite** | 4 (21%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 18 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getId` | `int getId()` | 10 | direct | trivial | `getId` | `getId(): HiTraceId` |
| `getVersion` | `int getVersion()` | 9 | direct | easy | `targetVersion` | `readonly targetVersion: number` |
| `getPower` | `float getPower()` | 8 | direct | easy | `getPowerMode` | `getPowerMode(): DevicePowerMode` |
| `getVendor` | `String getVendor()` | 8 | direct | easy | `vendor` | `readonly vendor: string` |
| `getType` | `int getType()` | 8 | near | easy | `eventType` | `eventType: EventType` |
| `getName` | `String getName()` | 7 | near | easy | `getLocalName` | `getLocalName(): string` |
| `isDirectChannelTypeSupported` | `boolean isDirectChannelTypeSupported(int)` | 7 | near | easy | `isBandTypeSupported` | `isBandTypeSupported(bandType: WifiBandType): boolean` |
| `getReportingMode` | `int getReportingMode()` | 7 | near | easy | `getPowerMode` | `getPowerMode(): DevicePowerMode` |
| `getMinDelay` | `int getMinDelay()` | 7 | near | moderate | `getRemainingDelayTime` | `getRemainingDelayTime(requestId: number, callback: AsyncCallback<number>): void` |
| `getMaxDelay` | `int getMaxDelay()` | 7 | near | moderate | `getAllDisplay` | `getAllDisplay(callback: AsyncCallback<Array<Display>>): void` |
| `getResolution` | `float getResolution()` | 7 | near | moderate | `getRealTime` | `getRealTime(isNano: boolean, callback: AsyncCallback<number>): void` |
| `getStringType` | `String getStringType()` | 7 | near | moderate | `getState` | `getState(): BluetoothState` |
| `isAdditionalInfoSupported` | `boolean isAdditionalInfoSupported()` | 7 | near | moderate | `isBatteryConfigSupported` | `isBatteryConfigSupported(sceneName: string): boolean` |
| `getFifoMaxEventCount` | `int getFifoMaxEventCount()` | 6 | near | moderate | `getUIFontConfig` | `getUIFontConfig(): UIFontConfig` |
| `getMaximumRange` | `float getMaximumRange()` | 6 | near | moderate | `getImage` | `getImage(wallpaperType: WallpaperType, callback: AsyncCallback<image.PixelMap>): void` |
| `getFifoReservedEventCount` | `int getFifoReservedEventCount()` | 6 | partial | moderate | `getListenerCount` | `getListenerCount(eventId: number | string): number` |
| `isWakeUpSensor` | `boolean isWakeUpSensor()` | 6 | partial | moderate | `isRemoteWakeup` | `isRemoteWakeup: boolean` |
| `getHighestDirectReportRateLevel` | `int getHighestDirectReportRateLevel()` | 5 | partial | moderate | `getThermalLevel` | `getThermalLevel(): ThermalLevel` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isDynamicSensor` | 5 | partial | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 18 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.hardware.Sensor`:


## Quality Gates

Before marking `android.hardware.Sensor` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 19 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 18 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
