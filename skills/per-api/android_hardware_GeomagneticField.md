# SKILL: android.hardware.GeomagneticField

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.hardware.GeomagneticField`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.hardware.GeomagneticField` |
| **Package** | `android.hardware` |
| **Total Methods** | 8 |
| **Avg Score** | 7.1 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 7 (87%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 1 (12%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getX` | `float getX()` | 10 | direct | trivial | `getOAID` | `getOAID(callback: AsyncCallback<string>): void` |
| `getY` | `float getY()` | 10 | direct | trivial | `getOAID` | `getOAID(callback: AsyncCallback<string>): void` |
| `getZ` | `float getZ()` | 10 | direct | trivial | `getOAID` | `getOAID(callback: AsyncCallback<string>): void` |
| `getFieldStrength` | `float getFieldStrength()` | 7 | near | easy | `getLength` | `getLength(): string` |
| `getDeclination` | `float getDeclination()` | 6 | near | moderate | `getStations` | `getStations(): Array<StationInfo>` |
| `getInclination` | `float getInclination()` | 6 | near | moderate | `getStations` | `getStations(): Array<StationInfo>` |
| `getHorizontalStrength` | `float getHorizontalStrength()` | 6 | near | moderate | `getLength` | `getLength(): string` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `GeomagneticField` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.hardware.GeomagneticField`:


## Quality Gates

Before marking `android.hardware.GeomagneticField` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
