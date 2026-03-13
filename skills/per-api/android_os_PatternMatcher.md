# SKILL: android.os.PatternMatcher

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.PatternMatcher`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.PatternMatcher` |
| **Package** | `android.os` |
| **Total Methods** | 7 |
| **Avg Score** | 4.1 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 2 (28%) |
| **Partial/Composite** | 2 (28%) |
| **No Mapping** | 3 (42%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getType` | `final int getType()` | 8 | near | easy | `eventType` | `eventType: EventType` |
| `getPath` | `final String getPath()` | 7 | near | easy | `getDate` | `getDate(callback: AsyncCallback<Date>): void` |
| `writeToParcel` | `void writeToParcel(android.os.Parcel, int)` | 6 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |
| `match` | `boolean match(String)` | 5 | partial | moderate | `matchMediaSync` | `matchMediaSync(condition: string): MediaQueryListener` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `PatternMatcher` | 1 | none | throw UnsupportedOperationException |
| `PatternMatcher` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.os.PatternMatcher`:


## Quality Gates

Before marking `android.os.PatternMatcher` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
