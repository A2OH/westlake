# SKILL: android.content.ClipDescription

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.ClipDescription`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.ClipDescription` |
| **Package** | `android.content` |
| **Total Methods** | 13 |
| **Avg Score** | 4.3 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 5 (38%) |
| **Partial/Composite** | 4 (30%) |
| **No Mapping** | 4 (30%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getExtras` | `android.os.PersistableBundle getExtras()` | 7 | near | easy | `getEntry` | `getEntry(): Entry` |
| `getMimeType` | `String getMimeType(int)` | 7 | near | moderate | `getOperationType` | `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void` |
| `getMimeTypeCount` | `int getMimeTypeCount()` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `hasMimeType` | `boolean hasMimeType(String)` | 6 | near | moderate | `userType` | `userType?: UserType` |
| `setExtras` | `void setExtras(android.os.PersistableBundle)` | 6 | near | moderate | `setRestartWant` | `setRestartWant(want: Want): void` |
| `getLabel` | `CharSequence getLabel()` | 6 | partial | moderate | `getTopAbility` | `getTopAbility(): Promise<ElementName>` |
| `getTimestamp` | `long getTimestamp()` | 5 | partial | moderate | `getContext` | `getContext(): Context` |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeToParcel` | 4 | partial | Log warning + no-op |
| `filterMimeTypes` | 3 | composite | throw UnsupportedOperationException |
| `ClipDescription` | 1 | none | Store callback, never fire |
| `ClipDescription` | 1 | none | Store callback, never fire |
| `compareMimeTypes` | 1 | none | throw UnsupportedOperationException |
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

Check if these related classes are already shimmed before generating `android.content.ClipDescription`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.ClipDescription` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 13 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
