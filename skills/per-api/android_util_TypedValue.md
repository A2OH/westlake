# SKILL: android.util.TypedValue

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.TypedValue`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.TypedValue` |
| **Package** | `android.util` |
| **Total Methods** | 15 |
| **Avg Score** | 5.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 9 (60%) |
| **Partial/Composite** | 5 (33%) |
| **No Mapping** | 1 (6%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 9 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getFloat` | `final float getFloat()` | 8 | direct | easy | `getSlot` | `getSlot(slotType: SlotType, callback: AsyncCallback<NotificationSlot>): void` |
| `isColorType` | `boolean isColorType()` | 8 | direct | easy | `coordType` | `coordType?: string` |
| `setTo` | `void setTo(android.util.TypedValue)` | 8 | near | easy | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `coerceToString` | `final CharSequence coerceToString()` | 7 | near | easy | `errnoToString` | `errnoToString(errno: number): string` |
| `coerceToString` | `static final String coerceToString(int, int)` | 7 | near | easy | `errnoToString` | `errnoToString(errno: number): string` |
| `getFraction` | `float getFraction(float, float)` | 7 | near | easy | `getStations` | `getStations(): Array<StationInfo>` |
| `getDimension` | `float getDimension(android.util.DisplayMetrics)` | 7 | near | moderate | `getTimeZone` | `getTimeZone(zoneID?: string): TimeZone` |
| `TypedValue` | `TypedValue()` | 7 | near | moderate | `setValue` | `setValue(value: number): void` |
| `getComplexUnit` | `int getComplexUnit()` | 6 | near | moderate | `getLength` | `getLength(): string` |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `complexToDimension` | 5 | partial | Store callback, never fire |
| `complexToDimensionPixelSize` | 5 | partial | Store callback, never fire |
| `complexToFraction` | 4 | partial | Store callback, never fire |
| `complexToDimensionPixelOffset` | 4 | partial | Log warning + no-op |
| `complexToFloat` | 4 | partial | throw UnsupportedOperationException |
| `applyDimension` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 9 methods that have score >= 5
2. Stub 6 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.util.TypedValue`:


## Quality Gates

Before marking `android.util.TypedValue` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 15 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 9 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
