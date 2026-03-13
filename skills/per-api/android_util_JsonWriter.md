# SKILL: android.util.JsonWriter

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.JsonWriter`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.JsonWriter` |
| **Package** | `android.util` |
| **Total Methods** | 17 |
| **Avg Score** | 7.3 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 12 (70%) |
| **Partial/Composite** | 4 (23%) |
| **No Mapping** | 1 (5%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 15 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `close` | `void close() throws java.io.IOException` | 10 | direct | trivial | `close` | `close(fd: number): Promise<void>` |
| `name` | `android.util.JsonWriter name(String) throws java.io.IOException` | 10 | direct | trivial | `name` | `name: string` |
| `value` | `android.util.JsonWriter value(String) throws java.io.IOException` | 10 | direct | trivial | `value` | `value: number` |
| `value` | `android.util.JsonWriter value(boolean) throws java.io.IOException` | 10 | direct | trivial | `value` | `value: number` |
| `value` | `android.util.JsonWriter value(double) throws java.io.IOException` | 10 | direct | trivial | `value` | `value: number` |
| `value` | `android.util.JsonWriter value(long) throws java.io.IOException` | 10 | direct | trivial | `value` | `value: number` |
| `value` | `android.util.JsonWriter value(Number) throws java.io.IOException` | 10 | direct | trivial | `value` | `value: number` |
| `nullValue` | `android.util.JsonWriter nullValue() throws java.io.IOException` | 7 | near | easy | `value` | `value: number` |
| `setIndent` | `void setIndent(String)` | 7 | near | easy | `setId` | `setId(id: HiTraceId): void` |
| `beginArray` | `android.util.JsonWriter beginArray() throws java.io.IOException` | 7 | near | moderate | `begin` | `begin(name: string, flags?: number): HiTraceId` |
| `isLenient` | `boolean isLenient()` | 6 | near | moderate | `isEncoding` | `isEncoding(encoding: string): boolean` |
| `beginObject` | `android.util.JsonWriter beginObject() throws java.io.IOException` | 6 | near | moderate | `begin` | `begin(name: string, flags?: number): HiTraceId` |
| `setLenient` | `void setLenient(boolean)` | 6 | partial | moderate | `setTimezone` | `setTimezone(timezone: string, callback: AsyncCallback<void>): void` |
| `endArray` | `android.util.JsonWriter endArray() throws java.io.IOException` | 5 | partial | moderate | `end` | `end(id: HiTraceId): void` |
| `endObject` | `android.util.JsonWriter endObject() throws java.io.IOException` | 5 | partial | moderate | `end` | `end(id: HiTraceId): void` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `flush` | 3 | composite | throw UnsupportedOperationException |
| `JsonWriter` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.util.JsonWriter`:


## Quality Gates

Before marking `android.util.JsonWriter` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 17 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 15 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
