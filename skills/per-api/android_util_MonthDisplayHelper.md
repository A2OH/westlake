# SKILL: android.util.MonthDisplayHelper

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.MonthDisplayHelper`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.MonthDisplayHelper` |
| **Package** | `android.util` |
| **Total Methods** | 15 |
| **Avg Score** | 6.4 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 8 (53%) |
| **Partial/Composite** | 7 (46%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 15 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getDayAt` | `int getDayAt(int, int)` | 8 | direct | easy | `getDate` | `getDate(callback: AsyncCallback<Date>): void` |
| `getOffset` | `int getOffset()` | 8 | direct | easy | `offset` | `offset: number` |
| `getMonth` | `int getMonth()` | 8 | near | easy | `month` | `month: number` |
| `getYear` | `int getYear()` | 7 | near | easy | `year` | `year: number` |
| `nextMonth` | `void nextMonth()` | 7 | near | easy | `month` | `month: number` |
| `getColumnOf` | `int getColumnOf(int)` | 7 | near | easy | `getColors` | `getColors(wallpaperType: WallpaperType, callback: AsyncCallback<Array<RgbaColor>>): void` |
| `getWeekStartDay` | `int getWeekStartDay()` | 6 | near | moderate | `getState` | `getState(): BluetoothState` |
| `getRowOf` | `int getRowOf(int)` | 6 | near | moderate | `getPowerMode` | `getPowerMode(): DevicePowerMode` |
| `getDigitsForRow` | `int[] getDigitsForRow(int)` | 6 | partial | moderate | `getIpInfo` | `getIpInfo(): IpInfo` |
| `isWithinCurrentMonth` | `boolean isWithinCurrentMonth(int, int)` | 6 | partial | moderate | `currentMode` | `currentMode: number` |
| `getFirstDayOfMonth` | `int getFirstDayOfMonth()` | 6 | partial | moderate | `getCfgDirListSync` | `getCfgDirListSync(): Array<string>` |
| `previousMonth` | `void previousMonth()` | 6 | partial | moderate | `month` | `month: number` |
| `MonthDisplayHelper` | `MonthDisplayHelper(int, int, int)` | 6 | partial | moderate | `displayName` | `displayName: string` |
| `MonthDisplayHelper` | `MonthDisplayHelper(int, int)` | 6 | partial | moderate | `displayName` | `displayName: string` |
| `getNumberOfDaysInMonth` | `int getNumberOfDaysInMonth()` | 5 | partial | moderate | `getLineDash` | `getLineDash: Array<number>` |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 15 methods that have score >= 5
2. Stub 0 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.util.MonthDisplayHelper`:


## Quality Gates

Before marking `android.util.MonthDisplayHelper` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 15 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 15 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
