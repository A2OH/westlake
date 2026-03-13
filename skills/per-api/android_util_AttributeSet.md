# SKILL: android.util.AttributeSet

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.AttributeSet`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.AttributeSet` |
| **Package** | `android.util` |
| **Total Methods** | 23 |
| **Avg Score** | 6.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 11 (47%) |
| **Partial/Composite** | 12 (52%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 23 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getAttributeName` | `String getAttributeName(int)` | 7 | near | moderate | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `getAttributeCount` | `int getAttributeCount()` | 7 | near | moderate | `getListenerCount` | `getListenerCount(eventId: number | string): number` |
| `getAttributeValue` | `String getAttributeValue(int)` | 7 | near | moderate | `getValue` | `getValue(dataAbilityHelper: DataAbilityHelper, name: string, callback: AsyncCallback<object>): void` |
| `getAttributeValue` | `String getAttributeValue(String, String)` | 7 | near | moderate | `getValue` | `getValue(dataAbilityHelper: DataAbilityHelper, name: string, callback: AsyncCallback<object>): void` |
| `getIdAttribute` | `String getIdAttribute()` | 7 | near | moderate | `getId` | `getId(): HiTraceId` |
| `getPositionDescription` | `String getPositionDescription()` | 7 | near | moderate | `description` | `description: string` |
| `getStyleAttribute` | `int getStyleAttribute()` | 6 | near | moderate | `getState` | `getState(): BluetoothState` |
| `getAttributeResourceValue` | `int getAttributeResourceValue(String, String, int)` | 6 | near | moderate | `getSystemResourceManager` | `getSystemResourceManager(): ResourceManager` |
| `getAttributeResourceValue` | `int getAttributeResourceValue(int, int)` | 6 | near | moderate | `getSystemResourceManager` | `getSystemResourceManager(): ResourceManager` |
| `getAttributeListValue` | `int getAttributeListValue(String, String, String[], int)` | 6 | near | moderate | `getAttestStatus` | `getAttestStatus(callback: AsyncCallback<AttestResultInfo>): void` |
| `getAttributeListValue` | `int getAttributeListValue(int, String[], int)` | 6 | near | moderate | `getAttestStatus` | `getAttestStatus(callback: AsyncCallback<AttestResultInfo>): void` |
| `getAttributeNamespace` | `default String getAttributeNamespace(int)` | 6 | partial | moderate | `getBundleName` | `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void` |
| `getIdAttributeResourceValue` | `int getIdAttributeResourceValue(int)` | 6 | partial | moderate | `getSystemResourceManager` | `getSystemResourceManager(): ResourceManager` |
| `getAttributeNameResource` | `int getAttributeNameResource(int)` | 6 | partial | moderate | `getSystemResourceManager` | `getSystemResourceManager(): ResourceManager` |
| `getClassAttribute` | `String getClassAttribute()` | 6 | partial | moderate | `getPastCpuTime` | `getPastCpuTime(): number` |
| `getAttributeIntValue` | `int getAttributeIntValue(String, String, int)` | 6 | partial | moderate | `getLineInstance` | `getLineInstance(locale: string): BreakIterator` |
| `getAttributeIntValue` | `int getAttributeIntValue(int, int)` | 6 | partial | moderate | `getLineInstance` | `getLineInstance(locale: string): BreakIterator` |
| `getAttributeUnsignedIntValue` | `int getAttributeUnsignedIntValue(String, String, int)` | 6 | partial | moderate | `getHardwareUnitPowerValue` | `getHardwareUnitPowerValue(type: ConsumptionType): number` |
| `getAttributeUnsignedIntValue` | `int getAttributeUnsignedIntValue(int, int)` | 6 | partial | moderate | `getHardwareUnitPowerValue` | `getHardwareUnitPowerValue(type: ConsumptionType): number` |
| `getAttributeFloatValue` | `float getAttributeFloatValue(String, String, float)` | 5 | partial | moderate | `getBatteryStats` | `getBatteryStats(): Promise<Array<BatteryStatsInfo>>` |
| `getAttributeFloatValue` | `float getAttributeFloatValue(int, float)` | 5 | partial | moderate | `getBatteryStats` | `getBatteryStats(): Promise<Array<BatteryStatsInfo>>` |
| `getAttributeBooleanValue` | `boolean getAttributeBooleanValue(String, String, boolean)` | 5 | partial | moderate | `attributeValueCallbackFunction` | `attributeValueCallbackFunction?: (name: string, value: string) => boolean` |
| `getAttributeBooleanValue` | `boolean getAttributeBooleanValue(int, boolean)` | 5 | partial | moderate | `attributeValueCallbackFunction` | `attributeValueCallbackFunction?: (name: string, value: string) => boolean` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 23 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.util.AttributeSet`:


## Quality Gates

Before marking `android.util.AttributeSet` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 23 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 23 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
