# SKILL: android.hardware.SensorManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.hardware.SensorManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.hardware.SensorManager` |
| **Package** | `android.hardware` |
| **Total Methods** | 30 |
| **Avg Score** | 6.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 24 (80%) |
| **Partial/Composite** | 6 (20%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 28 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getOrientation` | `static float[] getOrientation(float[], float[])` | 9 | direct | easy | `orientation` | `orientation: Orientation` |
| `getAltitude` | `static float getAltitude(float, float)` | 8 | direct | easy | `altitude` | `altitude: number` |
| `unregisterListener` | `void unregisterListener(android.hardware.SensorEventListener, android.hardware.Sensor)` | 8 | direct | easy | `unRegisterMissionListener` | `unRegisterMissionListener(parameter: MissionDeviceInfo, callback: AsyncCallback<void>): void` |
| `unregisterListener` | `void unregisterListener(android.hardware.SensorEventListener)` | 8 | direct | easy | `unRegisterMissionListener` | `unRegisterMissionListener(parameter: MissionDeviceInfo, callback: AsyncCallback<void>): void` |
| `registerListener` | `boolean registerListener(android.hardware.SensorEventListener, android.hardware.Sensor, int)` | 8 | direct | easy | `registerMissionListener` | `registerMissionListener(parameter: MissionDeviceInfo,
    options: MissionCallback,
    callback: AsyncCallback<void>): void` |
| `registerListener` | `boolean registerListener(android.hardware.SensorEventListener, android.hardware.Sensor, int, int)` | 8 | direct | easy | `registerMissionListener` | `registerMissionListener(parameter: MissionDeviceInfo,
    options: MissionCallback,
    callback: AsyncCallback<void>): void` |
| `registerListener` | `boolean registerListener(android.hardware.SensorEventListener, android.hardware.Sensor, int, android.os.Handler)` | 8 | direct | easy | `registerMissionListener` | `registerMissionListener(parameter: MissionDeviceInfo,
    options: MissionCallback,
    callback: AsyncCallback<void>): void` |
| `registerListener` | `boolean registerListener(android.hardware.SensorEventListener, android.hardware.Sensor, int, int, android.os.Handler)` | 8 | direct | easy | `registerMissionListener` | `registerMissionListener(parameter: MissionDeviceInfo,
    options: MissionCallback,
    callback: AsyncCallback<void>): void` |
| `unregisterDynamicSensorCallback` | `void unregisterDynamicSensorCallback(android.hardware.SensorManager.DynamicSensorCallback)` | 8 | near | easy | `unregisterVsyncCallback` | `unregisterVsyncCallback(): void` |
| `registerDynamicSensorCallback` | `void registerDynamicSensorCallback(android.hardware.SensorManager.DynamicSensorCallback)` | 7 | near | easy | `unregisterVsyncCallback` | `unregisterVsyncCallback(): void` |
| `registerDynamicSensorCallback` | `void registerDynamicSensorCallback(android.hardware.SensorManager.DynamicSensorCallback, android.os.Handler)` | 7 | near | easy | `unregisterVsyncCallback` | `unregisterVsyncCallback(): void` |
| `getSensorList` | `java.util.List<android.hardware.Sensor> getSensorList(int)` | 7 | near | easy | `getScanInfoList` | `getScanInfoList(): Array<WifiScanInfo>` |
| `getDefaultSensor` | `android.hardware.Sensor getDefaultSensor(int)` | 7 | near | moderate | `getDefaultDisplay` | `getDefaultDisplay(callback: AsyncCallback<Display>): void` |
| `getDefaultSensor` | `android.hardware.Sensor getDefaultSensor(int, boolean)` | 7 | near | moderate | `getDefaultDisplay` | `getDefaultDisplay(callback: AsyncCallback<Display>): void` |
| `onDynamicSensorConnected` | `void onDynamicSensorConnected(android.hardware.Sensor)` | 7 | near | moderate | `onDisconnect` | `onDisconnect?: () => void` |
| `getRotationMatrix` | `static boolean getRotationMatrix(float[], float[], float[], float[])` | 6 | near | moderate | `getStations` | `getStations(): Array<StationInfo>` |
| `getInclination` | `static float getInclination(float[])` | 6 | near | moderate | `getStations` | `getStations(): Array<StationInfo>` |
| `createDirectChannel` | `android.hardware.SensorDirectChannel createDirectChannel(android.os.MemoryFile)` | 6 | near | moderate | `createDeviceManager` | `createDeviceManager(bundleName: string): DeviceManager` |
| `createDirectChannel` | `android.hardware.SensorDirectChannel createDirectChannel(android.hardware.HardwareBuffer)` | 6 | near | moderate | `createDeviceManager` | `createDeviceManager(bundleName: string): DeviceManager` |
| `getDynamicSensorList` | `java.util.List<android.hardware.Sensor> getDynamicSensorList(int)` | 6 | near | moderate | `getScanInfoList` | `getScanInfoList(): Array<WifiScanInfo>` |
| `requestTriggerSensor` | `boolean requestTriggerSensor(android.hardware.TriggerEventListener, android.hardware.Sensor)` | 6 | near | moderate | `requestRight` | `requestRight(deviceName: string): Promise<boolean>` |
| `getAngleChange` | `static void getAngleChange(float[], float[], float[])` | 6 | near | moderate | `getWantAgent` | `getWantAgent(info: WantAgentInfo, callback: AsyncCallback<WantAgent>): void` |
| `onDynamicSensorDisconnected` | `void onDynamicSensorDisconnected(android.hardware.Sensor)` | 6 | near | moderate | `onDisconnect` | `onDisconnect?: () => void` |
| `cancelTriggerSensor` | `boolean cancelTriggerSensor(android.hardware.TriggerEventListener, android.hardware.Sensor)` | 6 | near | moderate | `cancelPrintJob` | `cancelPrintJob(jobId: string, callback: AsyncCallback<void>): void` |
| `getQuaternionFromVector` | `static void getQuaternionFromVector(float[], float[])` | 6 | partial | moderate | `getPermissionUsedRecord` | `getPermissionUsedRecord(request: PermissionUsedRequest): Promise<PermissionUsedResponse>` |
| `DynamicSensorCallback` | `SensorManager.DynamicSensorCallback()` | 6 | partial | moderate | `callback` | `callback?: () => void` |
| `isDynamicSensorDiscoverySupported` | `boolean isDynamicSensorDiscoverySupported()` | 5 | partial | moderate | `isBandTypeSupported` | `isBandTypeSupported(bandType: WifiBandType): boolean` |
| `remapCoordinateSystem` | `static boolean remapCoordinateSystem(float[], int, int, float[])` | 5 | partial | moderate | `numberingSystem` | `numberingSystem: string` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getRotationMatrixFromVector` | 5 | partial | Return safe default (null/false/0/empty) |
| `flush` | 3 | composite | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 28 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.hardware.SensorManager`:


## Quality Gates

Before marking `android.hardware.SensorManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 30 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 28 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
