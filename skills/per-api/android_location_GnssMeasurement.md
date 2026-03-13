# SKILL: android.location.GnssMeasurement

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.location.GnssMeasurement`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.location.GnssMeasurement` |
| **Package** | `android.location` |
| **Total Methods** | 28 |
| **Avg Score** | 5.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 9 (32%) |
| **Partial/Composite** | 18 (64%) |
| **No Mapping** | 1 (3%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 20 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getState` | `int getState()` | 10 | direct | trivial | `getState` | `getState(): BluetoothState` |
| `getSvid` | `int getSvid()` | 8 | direct | easy | `getId` | `getId(): HiTraceId` |
| `getCarrierFrequencyHz` | `float getCarrierFrequencyHz()` | 8 | near | easy | `carrierFrequencies` | `carrierFrequencies: Array<number>` |
| `hasCarrierFrequencyHz` | `boolean hasCarrierFrequencyHz()` | 8 | near | easy | `carrierFrequencies` | `carrierFrequencies: Array<number>` |
| `getSnrInDb` | `double getSnrInDb()` | 7 | near | easy | `getUserId` | `getUserId(name: string): string` |
| `hasCodeType` | `boolean hasCodeType()` | 7 | near | easy | `coordType` | `coordType?: string` |
| `getReceivedSvTimeNanos` | `long getReceivedSvTimeNanos()` | 7 | near | moderate | `getRealActiveTime` | `getRealActiveTime(isNano: boolean, callback: AsyncCallback<number>): void` |
| `getConstellationType` | `int getConstellationType()` | 6 | near | moderate | `getBtConnectionState` | `getBtConnectionState(): ProfileConnectionState` |
| `getTimeOffsetNanos` | `double getTimeOffsetNanos()` | 6 | near | moderate | `getTimeZone` | `getTimeZone(zoneID?: string): TimeZone` |
| `getAccumulatedDeltaRangeMeters` | `double getAccumulatedDeltaRangeMeters()` | 6 | partial | moderate | `getCalendarManager` | `getCalendarManager(context: Context): CalendarManager` |
| `getMultipathIndicator` | `int getMultipathIndicator()` | 6 | partial | moderate | `getDLPGatheringPolicy` | `getDLPGatheringPolicy(): Promise<GatheringPolicyType>` |
| `writeToParcel` | `void writeToParcel(android.os.Parcel, int)` | 6 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |
| `hasSnrInDb` | `boolean hasSnrInDb()` | 6 | partial | moderate | `hasRight` | `hasRight(deviceName: string): boolean` |
| `getAccumulatedDeltaRangeState` | `int getAccumulatedDeltaRangeState()` | 6 | partial | moderate | `getCalendarManager` | `getCalendarManager(context: Context): CalendarManager` |
| `getAutomaticGainControlLevelDb` | `double getAutomaticGainControlLevelDb()` | 5 | partial | moderate | `getThermalLevel` | `getThermalLevel(): ThermalLevel` |
| `getPseudorangeRateMetersPerSecond` | `double getPseudorangeRateMetersPerSecond()` | 5 | partial | moderate | `getDLPSupportedFileTypes` | `getDLPSupportedFileTypes(): Promise<Array<string>>` |
| `getFullInterSignalBiasNanos` | `double getFullInterSignalBiasNanos()` | 5 | partial | moderate | `getOriginalFileName` | `getOriginalFileName(fileName: string): string` |
| `getReceivedSvTimeUncertaintyNanos` | `long getReceivedSvTimeUncertaintyNanos()` | 5 | partial | moderate | `getRealActiveTime` | `getRealActiveTime(isNano: boolean, callback: AsyncCallback<number>): void` |
| `getSatelliteInterSignalBiasNanos` | `double getSatelliteInterSignalBiasNanos()` | 5 | partial | moderate | `satelliteIds` | `satelliteIds: Array<number>` |
| `hasSatelliteInterSignalBiasNanos` | `boolean hasSatelliteInterSignalBiasNanos()` | 5 | partial | moderate | `satelliteIds` | `satelliteIds: Array<number>` |

## Stub APIs (score < 5): 8 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getAccumulatedDeltaRangeUncertaintyMeters` | 5 | partial | Return safe default (null/false/0/empty) |
| `getPseudorangeRateUncertaintyMetersPerSecond` | 5 | partial | Return safe default (null/false/0/empty) |
| `hasBasebandCn0DbHz` | 4 | partial | Return safe default (null/false/0/empty) |
| `hasAutomaticGainControlLevelDb` | 4 | partial | Return safe default (null/false/0/empty) |
| `hasSatelliteInterSignalBiasUncertaintyNanos` | 4 | partial | Return safe default (null/false/0/empty) |
| `hasFullInterSignalBiasNanos` | 3 | composite | Return safe default (null/false/0/empty) |
| `hasFullInterSignalBiasUncertaintyNanos` | 3 | composite | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 20 methods that have score >= 5
2. Stub 8 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.location.GnssMeasurement`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.location.GnssMeasurement` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 28 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 20 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
