# SKILL: android.location.Address

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.location.Address`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.location.Address` |
| **Package** | `android.location` |
| **Total Methods** | 43 |
| **Avg Score** | 7.4 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 35 (81%) |
| **Partial/Composite** | 7 (16%) |
| **No Mapping** | 1 (2%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 42 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Address` | `Address(java.util.Locale)` | 10 | direct | trivial | `address` | `address: number` |
| `getCountryCode` | `String getCountryCode()` | 10 | direct | trivial | `getCountryCode` | `getCountryCode(callback: AsyncCallback<CountryCode>): void` |
| `setCountryCode` | `void setCountryCode(String)` | 9 | direct | trivial | `getCountryCode` | `getCountryCode(callback: AsyncCallback<CountryCode>): void` |
| `getCountryName` | `String getCountryName()` | 9 | direct | easy | `countryName` | `countryName?: string` |
| `getSubLocality` | `String getSubLocality()` | 9 | direct | easy | `subLocality` | `subLocality?: string` |
| `setCountryName` | `void setCountryName(String)` | 9 | direct | easy | `countryName` | `countryName?: string` |
| `setSubLocality` | `void setSubLocality(String)` | 9 | direct | easy | `subLocality` | `subLocality?: string` |
| `getPostalCode` | `String getPostalCode()` | 9 | direct | easy | `postalCode` | `postalCode?: string` |
| `setPostalCode` | `void setPostalCode(String)` | 9 | direct | easy | `postalCode` | `postalCode?: string` |
| `getLocale` | `java.util.Locale getLocale()` | 9 | direct | easy | `getLocalName` | `getLocalName(): string` |
| `getLongitude` | `double getLongitude()` | 9 | direct | easy | `longitude` | `longitude: number` |
| `hasLongitude` | `boolean hasLongitude()` | 9 | direct | easy | `longitude` | `longitude: number` |
| `setLongitude` | `void setLongitude(double)` | 9 | direct | easy | `longitude` | `longitude: number` |
| `getLatitude` | `double getLatitude()` | 8 | direct | easy | `latitude` | `latitude: number` |
| `getLocality` | `String getLocality()` | 8 | direct | easy | `locality` | `locality?: string` |
| `getPremises` | `String getPremises()` | 8 | direct | easy | `premises` | `premises?: string` |
| `hasLatitude` | `boolean hasLatitude()` | 8 | direct | easy | `latitude` | `latitude: number` |
| `setLatitude` | `void setLatitude(double)` | 8 | direct | easy | `latitude` | `latitude: number` |
| `setLocality` | `void setLocality(String)` | 8 | direct | easy | `locality` | `locality?: string` |
| `setPremises` | `void setPremises(String)` | 8 | direct | easy | `premises` | `premises?: string` |
| `getUrl` | `String getUrl()` | 8 | direct | easy | `getURI` | `getURI(name: string, callback: AsyncCallback<object>): void` |
| `clearLongitude` | `void clearLongitude()` | 8 | near | easy | `longitude` | `longitude: number` |
| `clearLatitude` | `void clearLatitude()` | 8 | near | easy | `latitude` | `latitude: number` |
| `getAddressLine` | `String getAddressLine(int)` | 7 | near | moderate | `getAddressesFromLocation` | `getAddressesFromLocation(request: ReverseGeoCodeRequest, callback: AsyncCallback<Array<GeoAddress>>): void` |
| `getExtras` | `android.os.Bundle getExtras()` | 7 | near | moderate | `getParams` | `getParams(): Object` |
| `getFeatureName` | `String getFeatureName()` | 7 | near | moderate | `getLocalName` | `getLocalName(): string` |
| `getSubAdminArea` | `String getSubAdminArea()` | 7 | near | moderate | `subAdministrativeArea` | `subAdministrativeArea?: string` |
| `setAddressLine` | `void setAddressLine(int, String)` | 7 | near | moderate | `addressUrl` | `addressUrl?: string` |
| `setFeatureName` | `void setFeatureName(String)` | 7 | near | moderate | `setLocalName` | `setLocalName(name: string): void` |
| `setSubAdminArea` | `void setSubAdminArea(String)` | 7 | near | moderate | `subAdministrativeArea` | `subAdministrativeArea?: string` |
| `setUrl` | `void setUrl(String)` | 7 | near | moderate | `setCursor` | `setCursor(value: PointerStyle): void` |
| `getPhone` | `String getPhone()` | 6 | near | moderate | `getTimeZone` | `getTimeZone(zoneID?: string): TimeZone` |
| `setPhone` | `void setPhone(String)` | 6 | near | moderate | `setTimezone` | `setTimezone(timezone: string, callback: AsyncCallback<void>): void` |
| `getAdminArea` | `String getAdminArea()` | 6 | near | moderate | `getValidReminders` | `getValidReminders(callback: AsyncCallback<Array<ReminderRequest>>): void` |
| `setAdminArea` | `void setAdminArea(String)` | 6 | near | moderate | `setDarkMode` | `setDarkMode(mode: DarkMode, callback: AsyncCallback<void>): void` |
| `setThoroughfare` | `void setThoroughfare(String)` | 6 | partial | moderate | `setInterface` | `setInterface(pipe: USBDevicePipe, iface: USBInterface): number` |
| `getMaxAddressLineIndex` | `int getMaxAddressLineIndex()` | 6 | partial | moderate | `getDeviceMacAddress` | `getDeviceMacAddress(): string[]` |
| `setExtras` | `void setExtras(android.os.Bundle)` | 6 | partial | moderate | `setPortRoles` | `setPortRoles(portId: number, powerRole: PowerRoleType, dataRole: DataRoleType): Promise<boolean>` |
| `writeToParcel` | `void writeToParcel(android.os.Parcel, int)` | 6 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |
| `getSubThoroughfare` | `String getSubThoroughfare()` | 6 | partial | moderate | `getRestorer` | `getRestorer(): Restorer` |
| `getThoroughfare` | `String getThoroughfare()` | 5 | partial | moderate | `getRule` | `getRule(): bigint` |
| `setSubThoroughfare` | `void setSubThoroughfare(String)` | 5 | partial | moderate | `setInterface` | `setInterface(pipe: USBDevicePipe, iface: USBInterface): number` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.location.Address`:


## Quality Gates

Before marking `android.location.Address` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 43 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 42 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
