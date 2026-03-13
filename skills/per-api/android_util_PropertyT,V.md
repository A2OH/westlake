# SKILL: android.util.Property<T, V>

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.Property<T, V>`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.Property<T, V>` |
| **Package** | `android.util` |
| **Total Methods** | 7 |
| **Avg Score** | 7.3 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 6 (85%) |
| **Partial/Composite** | 1 (14%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `get` | `abstract V get(T)` | 10 | direct | trivial | `getOAID` | `getOAID(callback: AsyncCallback<string>): void` |
| `set` | `void set(T, V)` | 10 | direct | trivial | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `getType` | `Class<V> getType()` | 8 | near | easy | `eventType` | `eventType: EventType` |
| `getName` | `String getName()` | 7 | near | easy | `getLocalName` | `getLocalName(): string` |
| `Property` | `Property(Class<V>, String)` | 7 | near | moderate | `setUserProperty` | `setUserProperty(name: string, value: string): void` |
| `isReadOnly` | `boolean isReadOnly()` | 6 | near | moderate | `isScreenOn` | `isScreenOn(callback: AsyncCallback<boolean>): void` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `of` | 3 | composite | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.util.Property<T, V>`:


## Quality Gates

Before marking `android.util.Property<T, V>` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
