# SKILL: android.app.DatePickerDialog

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.DatePickerDialog`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.DatePickerDialog` |
| **Package** | `android.app` |
| **Total Methods** | 8 |
| **Avg Score** | 4.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (12%) |
| **Partial/Composite** | 7 (87%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `updateDate` | `void updateDate(int, int, int)` | 8 | near | easy | `updateId` | `updateId(uri: string, id: number): string` |
| `setOnDateSetListener` | `void setOnDateSetListener(@Nullable android.app.DatePickerDialog.OnDateSetListener)` | 6 | partial | moderate | `registerMissionListener` | `registerMissionListener(listener: MissionListener): number` |
| `onClick` | `void onClick(@NonNull android.content.DialogInterface, int)` | 5 | partial | moderate | `on` | `on(type: 'abilityForegroundState', observer: AbilityForegroundStateObserver): void` |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `DatePickerDialog` | 4 | partial | throw UnsupportedOperationException |
| `DatePickerDialog` | 4 | partial | throw UnsupportedOperationException |
| `DatePickerDialog` | 4 | partial | throw UnsupportedOperationException |
| `DatePickerDialog` | 4 | partial | throw UnsupportedOperationException |
| `onDateChanged` | 4 | composite | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 5 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.DatePickerDialog`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.DatePickerDialog` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
