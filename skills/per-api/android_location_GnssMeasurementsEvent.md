# SKILL: android.location.GnssMeasurementsEvent

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.location.GnssMeasurementsEvent`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.location.GnssMeasurementsEvent` |
| **Package** | `android.location` |
| **Total Methods** | 5 |
| **Avg Score** | 5.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (40%) |
| **Partial/Composite** | 2 (40%) |
| **No Mapping** | 1 (20%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Callback` | `GnssMeasurementsEvent.Callback()` | 10 | direct | trivial | `callback` | `callback?: () => void` |
| `onStatusChanged` | `void onStatusChanged(int)` | 6 | near | moderate | `statusMessage` | `statusMessage: string` |
| `writeToParcel` | `void writeToParcel(android.os.Parcel, int)` | 6 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |
| `onGnssMeasurementsReceived` | `void onGnssMeasurementsReceived(android.location.GnssMeasurementsEvent)` | 5 | partial | moderate | `onServiceDied` | `onServiceDied: () => void` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 4 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.location.GnssMeasurementsEvent`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.location.GnssMeasurementsEvent` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
