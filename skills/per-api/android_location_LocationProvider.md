# SKILL: android.location.LocationProvider

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.location.LocationProvider`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.location.LocationProvider` |
| **Package** | `android.location` |
| **Total Methods** | 11 |
| **Avg Score** | 4.9 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 6 (54%) |
| **Partial/Composite** | 2 (18%) |
| **No Mapping** | 3 (27%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getAccuracy` | `int getAccuracy()` | 8 | direct | easy | `accuracy` | `accuracy: number` |
| `getName` | `String getName()` | 7 | near | easy | `getLocalName` | `getLocalName(): string` |
| `getPowerRequirement` | `int getPowerRequirement()` | 7 | near | moderate | `getPowerMode` | `getPowerMode(): DevicePowerMode` |
| `supportsAltitude` | `boolean supportsAltitude()` | 7 | near | moderate | `altitude` | `altitude: number` |
| `requiresNetwork` | `boolean requiresNetwork()` | 6 | near | moderate | `removeAllNetwork` | `removeAllNetwork(): void` |
| `requiresSatellite` | `boolean requiresSatellite()` | 6 | near | moderate | `satelliteIds` | `satelliteIds: Array<number>` |
| `supportsSpeed` | `boolean supportsSpeed()` | 6 | partial | moderate | `speed` | `speed: number` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `hasMonetaryCost` | 3 | composite | Return safe default (null/false/0/empty) |
| `meetsCriteria` | 1 | none | throw UnsupportedOperationException |
| `requiresCell` | 1 | none | throw UnsupportedOperationException |
| `supportsBearing` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.location.LocationProvider`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.location.LocationProvider` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
