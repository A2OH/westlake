# SKILL: android.location.LocationManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.location.LocationManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.location.LocationManager` |
| **Package** | `android.location` |
| **Total Methods** | 14 |
| **Avg Score** | 7.1 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 10 (71%) |
| **Partial/Composite** | 4 (28%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 14 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isLocationEnabled` | `boolean isLocationEnabled()` | 10 | direct | trivial | `isLocationEnabled` | `isLocationEnabled(): boolean` |
| `sendExtraCommand` | `boolean sendExtraCommand(@NonNull String, @NonNull String, @Nullable android.os.Bundle)` | 10 | direct | trivial | `sendCommand` | `sendCommand(command: LocationCommand, callback: AsyncCallback<void>): void` |
| `isProviderEnabled` | `boolean isProviderEnabled(@NonNull String)` | 9 | direct | easy | `isLocationEnabled` | `isLocationEnabled(): boolean` |
| `setTestProviderLocation` | `void setTestProviderLocation(@NonNull String, @NonNull android.location.Location)` | 8 | direct | easy | `setMockedLocations` | `setMockedLocations(config: LocationMockConfig): void` |
| `removeUpdates` | `void removeUpdates(@NonNull android.app.PendingIntent)` | 7 | near | easy | `setMockedLocations` | `setMockedLocations(config: LocationMockConfig): void` |
| `setTestProviderEnabled` | `void setTestProviderEnabled(@NonNull String, boolean)` | 7 | near | easy | `isLocationEnabled` | `isLocationEnabled(): boolean` |
| `unregisterGnssNavigationMessageCallback` | `void unregisterGnssNavigationMessageCallback(@NonNull android.location.GnssNavigationMessage.Callback)` | 7 | near | moderate | `getCachedGnssLocationsSize` | `getCachedGnssLocationsSize(callback: AsyncCallback<number>): void` |
| `getGnssYearOfHardware` | `int getGnssYearOfHardware()` | 6 | near | moderate | `getAddressesFromLocationName` | `getAddressesFromLocationName(request: GeoCodeRequest, callback: AsyncCallback<Array<GeoAddress>>): void` |
| `removeTestProvider` | `void removeTestProvider(@NonNull String)` | 6 | near | moderate | `getCountryCode` | `getCountryCode(callback: AsyncCallback<CountryCode>): void` |
| `addTestProvider` | `void addTestProvider(@NonNull String, boolean, boolean, boolean, boolean, boolean, boolean, boolean, int, int)` | 6 | near | moderate | `getAddressesFromLocationName` | `getAddressesFromLocationName(request: GeoCodeRequest, callback: AsyncCallback<Array<GeoAddress>>): void` |
| `unregisterAntennaInfoListener` | `void unregisterAntennaInfoListener(@NonNull android.location.GnssAntennaInfo.Listener)` | 6 | partial | moderate | `isLocationEnabled` | `isLocationEnabled(): boolean` |
| `removeNmeaListener` | `void removeNmeaListener(@NonNull android.location.OnNmeaMessageListener)` | 6 | partial | moderate | `enableLocation` | `enableLocation(callback: AsyncCallback<void>): void` |
| `unregisterGnssStatusCallback` | `void unregisterGnssStatusCallback(@NonNull android.location.GnssStatus.Callback)` | 6 | partial | moderate | `getCachedGnssLocationsSize` | `getCachedGnssLocationsSize(callback: AsyncCallback<number>): void` |
| `unregisterGnssMeasurementsCallback` | `void unregisterGnssMeasurementsCallback(@NonNull android.location.GnssMeasurementsEvent.Callback)` | 5 | partial | moderate | `getCurrentLocation` | `getCurrentLocation(request: CurrentLocationRequest, callback: AsyncCallback<Location>): void` |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.location.LocationManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.location.LocationManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 14 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
