# SKILL: android.location.GnssClock

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.location.GnssClock`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.location.GnssClock` |
| **Package** | `android.location` |
| **Total Methods** | 22 |
| **Avg Score** | 5.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 6 (27%) |
| **Partial/Composite** | 15 (68%) |
| **No Mapping** | 1 (4%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 12 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getTimeNanos` | `long getTimeNanos()` | 7 | near | easy | `getTime` | `getTime(isNanoseconds?: boolean): number` |
| `getElapsedRealtimeNanos` | `long getElapsedRealtimeNanos()` | 6 | near | moderate | `getRealTime` | `getRealTime(isNano: boolean, callback: AsyncCallback<number>): void` |
| `getLeapSecond` | `int getLeapSecond()` | 6 | near | moderate | `getLastLocation` | `getLastLocation(): Location` |
| `hasLeapSecond` | `boolean hasLeapSecond()` | 6 | near | moderate | `second` | `second?: number` |
| `hasReferenceCarrierFrequencyHzForIsb` | `boolean hasReferenceCarrierFrequencyHzForIsb()` | 6 | near | moderate | `carrierFrequencies` | `carrierFrequencies: Array<number>` |
| `getBiasNanos` | `double getBiasNanos()` | 6 | near | moderate | `getInstance` | `getInstance(locale?: string): IndexUtil` |
| `getDriftNanosPerSecond` | `double getDriftNanosPerSecond()` | 6 | partial | moderate | `getListenerCount` | `getListenerCount(eventId: number | string): number` |
| `writeToParcel` | `void writeToParcel(android.os.Parcel, int)` | 6 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |
| `getFullBiasNanos` | `long getFullBiasNanos()` | 6 | partial | moderate | `getAllScreens` | `getAllScreens(callback: AsyncCallback<Array<Screen>>): void` |
| `getHardwareClockDiscontinuityCount` | `int getHardwareClockDiscontinuityCount()` | 5 | partial | moderate | `getHardwareUnitPowerValue` | `getHardwareUnitPowerValue(type: ConsumptionType): number` |
| `getReferenceConstellationTypeForIsb` | `int getReferenceConstellationTypeForIsb()` | 5 | partial | moderate | `getRemoteAbilityInfos` | `getRemoteAbilityInfos(elementNames: Array<ElementName>,
    callback: AsyncCallback<Array<RemoteAbilityInfo>>): void` |
| `hasBiasNanos` | `boolean hasBiasNanos()` | 5 | partial | moderate | `hasPrivateWindow` | `hasPrivateWindow(displayId: number): boolean` |

## Stub APIs (score < 5): 10 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `hasDriftNanosPerSecond` | 5 | partial | Return safe default (null/false/0/empty) |
| `hasBiasUncertaintyNanos` | 5 | partial | Return safe default (null/false/0/empty) |
| `hasElapsedRealtimeNanos` | 5 | partial | Return safe default (null/false/0/empty) |
| `hasTimeUncertaintyNanos` | 5 | partial | Return safe default (null/false/0/empty) |
| `hasFullBiasNanos` | 4 | partial | Return safe default (null/false/0/empty) |
| `hasReferenceCodeTypeForIsb` | 4 | partial | Return safe default (null/false/0/empty) |
| `hasDriftUncertaintyNanosPerSecond` | 4 | partial | Return safe default (null/false/0/empty) |
| `hasReferenceConstellationTypeForIsb` | 4 | composite | Return safe default (null/false/0/empty) |
| `hasElapsedRealtimeUncertaintyNanos` | 4 | composite | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 12 methods that have score >= 5
2. Stub 10 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.location.GnssClock`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.location.GnssClock` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 22 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 12 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
