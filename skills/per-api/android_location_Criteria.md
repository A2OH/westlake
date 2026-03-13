# SKILL: android.location.Criteria

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.location.Criteria`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.location.Criteria` |
| **Package** | `android.location` |
| **Total Methods** | 24 |
| **Avg Score** | 5.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 16 (66%) |
| **Partial/Composite** | 5 (20%) |
| **No Mapping** | 3 (12%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 21 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getAccuracy` | `int getAccuracy()` | 8 | direct | easy | `accuracy` | `accuracy: number` |
| `setAccuracy` | `void setAccuracy(int)` | 8 | direct | easy | `accuracy` | `accuracy: number` |
| `isCostAllowed` | `boolean isCostAllowed()` | 7 | near | easy | `isOperationAllowed` | `isOperationAllowed(callback: AsyncCallback<boolean>): void` |
| `setCostAllowed` | `void setCostAllowed(boolean)` | 7 | near | easy | `setScanAlwaysAllowed` | `setScanAlwaysAllowed(isScanAlwaysAllowed: boolean): void` |
| `setBearingRequired` | `void setBearingRequired(boolean)` | 7 | near | moderate | `getLocatingRequiredData` | `getLocatingRequiredData(config: LocatingRequiredDataConfig): Promise<Array<LocatingRequiredData>>` |
| `getPowerRequirement` | `int getPowerRequirement()` | 7 | near | moderate | `getPowerMode` | `getPowerMode(): DevicePowerMode` |
| `getSpeedAccuracy` | `int getSpeedAccuracy()` | 7 | near | moderate | `accuracy` | `accuracy: number` |
| `setPowerRequirement` | `void setPowerRequirement(int)` | 7 | near | moderate | `setPowerMode` | `setPowerMode(mode: DevicePowerMode, callback: AsyncCallback<void>): void` |
| `setSpeedAccuracy` | `void setSpeedAccuracy(int)` | 7 | near | moderate | `accuracy` | `accuracy: number` |
| `isBearingRequired` | `boolean isBearingRequired()` | 6 | near | moderate | `getLocatingRequiredData` | `getLocatingRequiredData(config: LocatingRequiredDataConfig): Promise<Array<LocatingRequiredData>>` |
| `getBearingAccuracy` | `int getBearingAccuracy()` | 6 | near | moderate | `maxAccuracy` | `maxAccuracy?: number` |
| `setBearingAccuracy` | `void setBearingAccuracy(int)` | 6 | near | moderate | `maxAccuracy` | `maxAccuracy?: number` |
| `setAltitudeRequired` | `void setAltitudeRequired(boolean)` | 6 | near | moderate | `getLocatingRequiredData` | `getLocatingRequiredData(config: LocatingRequiredDataConfig): Promise<Array<LocatingRequiredData>>` |
| `isAltitudeRequired` | `boolean isAltitudeRequired()` | 6 | near | moderate | `altitude` | `altitude: number` |
| `getVerticalAccuracy` | `int getVerticalAccuracy()` | 6 | near | moderate | `maxAccuracy` | `maxAccuracy?: number` |
| `setVerticalAccuracy` | `void setVerticalAccuracy(int)` | 6 | near | moderate | `maxAccuracy` | `maxAccuracy?: number` |
| `isSpeedRequired` | `boolean isSpeedRequired()` | 6 | partial | moderate | `isSelfPowered` | `isSelfPowered: boolean` |
| `getHorizontalAccuracy` | `int getHorizontalAccuracy()` | 6 | partial | moderate | `maxAccuracy` | `maxAccuracy?: number` |
| `setHorizontalAccuracy` | `void setHorizontalAccuracy(int)` | 6 | partial | moderate | `maxAccuracy` | `maxAccuracy?: number` |
| `writeToParcel` | `void writeToParcel(android.os.Parcel, int)` | 6 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |
| `setSpeedRequired` | `void setSpeedRequired(boolean)` | 5 | partial | moderate | `getLocatingRequiredData` | `getLocatingRequiredData(config: LocatingRequiredDataConfig): Promise<Array<LocatingRequiredData>>` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `Criteria` | 1 | none | throw UnsupportedOperationException |
| `Criteria` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 21 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.location.Criteria`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.location.Criteria` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 24 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 21 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
