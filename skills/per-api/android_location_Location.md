# SKILL: android.location.Location

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.location.Location`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.location.Location` |
| **Package** | `android.location` |
| **Total Methods** | 49 |
| **Avg Score** | 6.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 30 (61%) |
| **Partial/Composite** | 16 (32%) |
| **No Mapping** | 3 (6%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 39 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getTime` | `long getTime()` | 10 | direct | trivial | `getTime` | `getTime(isNanoseconds?: boolean): number` |
| `reset` | `void reset()` | 10 | direct | trivial | `resetOAID` | `resetOAID(): void` |
| `set` | `void set(android.location.Location)` | 10 | direct | trivial | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `setTime` | `void setTime(long)` | 10 | direct | trivial | `setTime` | `setTime(time: number, callback: AsyncCallback<void>): void` |
| `getLongitude` | `double getLongitude()` | 9 | direct | easy | `longitude` | `longitude: number` |
| `setLongitude` | `void setLongitude(double)` | 9 | direct | easy | `longitude` | `longitude: number` |
| `getAccuracy` | `float getAccuracy()` | 8 | direct | easy | `accuracy` | `accuracy: number` |
| `getAltitude` | `double getAltitude()` | 8 | direct | easy | `altitude` | `altitude: number` |
| `getLatitude` | `double getLatitude()` | 8 | direct | easy | `latitude` | `latitude: number` |
| `hasAccuracy` | `boolean hasAccuracy()` | 8 | direct | easy | `accuracy` | `accuracy: number` |
| `hasAltitude` | `boolean hasAltitude()` | 8 | direct | easy | `altitude` | `altitude: number` |
| `setAccuracy` | `void setAccuracy(float)` | 8 | direct | easy | `accuracy` | `accuracy: number` |
| `setAltitude` | `void setAltitude(double)` | 8 | direct | easy | `altitude` | `altitude: number` |
| `setLatitude` | `void setLatitude(double)` | 8 | direct | easy | `latitude` | `latitude: number` |
| `getSpeed` | `float getSpeed()` | 8 | near | easy | `speed` | `speed: number` |
| `hasSpeed` | `boolean hasSpeed()` | 8 | near | easy | `speed` | `speed: number` |
| `setSpeed` | `void setSpeed(float)` | 8 | near | easy | `speed` | `speed: number` |
| `setProvider` | `void setProvider(String)` | 7 | near | easy | `setVideo` | `setVideo(source: string, wallpaperType: WallpaperType, callback: AsyncCallback<void>): void` |
| `Location` | `Location(String)` | 7 | near | easy | `enableLocation` | `enableLocation(callback: AsyncCallback<void>): void` |
| `Location` | `Location(android.location.Location)` | 7 | near | easy | `enableLocation` | `enableLocation(callback: AsyncCallback<void>): void` |
| `getProvider` | `String getProvider()` | 7 | near | moderate | `getPowerMode` | `getPowerMode(): DevicePowerMode` |
| `distanceTo` | `float distanceTo(android.location.Location)` | 7 | near | moderate | `distanceInterval` | `distanceInterval?: number` |
| `getBearing` | `float getBearing()` | 7 | near | moderate | `getErrorString` | `getErrorString(errno: number): string` |
| `getExtras` | `android.os.Bundle getExtras()` | 7 | near | moderate | `getParams` | `getParams(): Object` |
| `hasBearing` | `boolean hasBearing()` | 7 | near | moderate | `hasRight` | `hasRight(deviceName: string): boolean` |
| `hasSpeedAccuracy` | `boolean hasSpeedAccuracy()` | 7 | near | moderate | `maxAccuracy` | `maxAccuracy?: number` |
| `getElapsedRealtimeNanos` | `long getElapsedRealtimeNanos()` | 6 | near | moderate | `getRealTime` | `getRealTime(isNano: boolean, callback: AsyncCallback<number>): void` |
| `setBearing` | `void setBearing(float)` | 6 | near | moderate | `setUserId` | `setUserId(name: string, value: string): void` |
| `hasBearingAccuracy` | `boolean hasBearingAccuracy()` | 6 | near | moderate | `maxAccuracy` | `maxAccuracy?: number` |
| `hasVerticalAccuracy` | `boolean hasVerticalAccuracy()` | 6 | near | moderate | `maxAccuracy` | `maxAccuracy?: number` |
| `distanceBetween` | `static void distanceBetween(double, double, double, double, float[])` | 6 | partial | moderate | `distanceInterval` | `distanceInterval?: number` |
| `setExtras` | `void setExtras(android.os.Bundle)` | 6 | partial | moderate | `setPortRoles` | `setPortRoles(portId: number, powerRole: PowerRoleType, dataRole: DataRoleType): Promise<boolean>` |
| `setElapsedRealtimeNanos` | `void setElapsedRealtimeNanos(long)` | 6 | partial | moderate | `getStartRealtime` | `getStartRealtime(): number` |
| `writeToParcel` | `void writeToParcel(android.os.Parcel, int)` | 6 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |
| `getBearingAccuracyDegrees` | `float getBearingAccuracyDegrees()` | 5 | partial | moderate | `getDeviceMacAddress` | `getDeviceMacAddress(): string[]` |
| `getVerticalAccuracyMeters` | `float getVerticalAccuracyMeters()` | 5 | partial | moderate | `getAllScreens` | `getAllScreens(callback: AsyncCallback<Array<Screen>>): void` |
| `dump` | `void dump(android.util.Printer, String)` | 5 | partial | moderate | `dumpHeapData` | `dumpHeapData(filename: string): void` |
| `setBearingAccuracyDegrees` | `void setBearingAccuracyDegrees(float)` | 5 | partial | moderate | `maxAccuracy` | `maxAccuracy?: number` |
| `setVerticalAccuracyMeters` | `void setVerticalAccuracyMeters(float)` | 5 | partial | moderate | `maxAccuracy` | `maxAccuracy?: number` |

## Stub APIs (score < 5): 10 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getElapsedRealtimeUncertaintyNanos` | 5 | partial | Return safe default (null/false/0/empty) |
| `setElapsedRealtimeUncertaintyNanos` | 5 | partial | Log warning + no-op |
| `getSpeedAccuracyMetersPerSecond` | 5 | partial | Return safe default (null/false/0/empty) |
| `isFromMockProvider` | 5 | partial | Return safe default (null/false/0/empty) |
| `bearingTo` | 5 | partial | throw UnsupportedOperationException |
| `setSpeedAccuracyMetersPerSecond` | 4 | partial | Log warning + no-op |
| `hasElapsedRealtimeUncertaintyNanos` | 4 | composite | Return safe default (null/false/0/empty) |
| `convert` | 1 | none | Store callback, never fire |
| `convert` | 1 | none | Store callback, never fire |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 39 methods that have score >= 5
2. Stub 10 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.location.Location`:


## Quality Gates

Before marking `android.location.Location` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 49 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 39 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
