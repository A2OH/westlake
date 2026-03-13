# SKILL: android.app.TimePickerDialog

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.TimePickerDialog`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.TimePickerDialog` |
| **Package** | `android.app` |
| **Total Methods** | 5 |
| **Avg Score** | 5.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (20%) |
| **Partial/Composite** | 4 (80%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `updateTime` | `void updateTime(int, int)` | 8 | near | easy | `updateId` | `updateId(uri: string, id: number): string` |
| `onClick` | `void onClick(android.content.DialogInterface, int)` | 5 | partial | moderate | `on` | `on(type: 'abilityForegroundState', observer: AbilityForegroundStateObserver): void` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onTimeChanged` | 5 | partial | Store callback, never fire |
| `TimePickerDialog` | 4 | partial | throw UnsupportedOperationException |
| `TimePickerDialog` | 4 | partial | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 2 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.TimePickerDialog`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.TimePickerDialog` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
