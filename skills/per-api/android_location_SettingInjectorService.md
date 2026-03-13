# SKILL: android.location.SettingInjectorService

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.location.SettingInjectorService`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.location.SettingInjectorService` |
| **Package** | `android.location` |
| **Total Methods** | 7 |
| **Avg Score** | 6.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 5 (71%) |
| **Partial/Composite** | 2 (28%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `onStart` | `final void onStart(android.content.Intent, int)` | 8 | direct | easy | `start` | `start(): void` |
| `onGetSummary` | `abstract String onGetSummary()` | 8 | near | easy | `getDataSummary` | `getDataSummary(): Array<Summary>` |
| `onGetEnabled` | `abstract boolean onGetEnabled()` | 7 | near | easy | `enabled` | `readonly enabled: boolean` |
| `onStartCommand` | `final int onStartCommand(android.content.Intent, int, int)` | 7 | near | moderate | `startScan` | `startScan(): void` |
| `refreshSettings` | `static final void refreshSettings(@NonNull android.content.Context)` | 6 | near | moderate | `refreshRate` | `refreshRate: number` |
| `SettingInjectorService` | `SettingInjectorService(String)` | 5 | partial | moderate | `newSEService` | `newSEService(type: 'serviceState', callback: Callback<ServiceState>): SEService` |
| `onBind` | `final android.os.IBinder onBind(android.content.Intent)` | 5 | partial | moderate | `on` | `on(type: 'bluetoothDeviceFind', callback: Callback<Array<string>>): void` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 7 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.location.SettingInjectorService`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.location.SettingInjectorService` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
