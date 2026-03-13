# SKILL: android.util.JsonReader

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.JsonReader`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.JsonReader` |
| **Package** | `android.util` |
| **Total Methods** | 18 |
| **Avg Score** | 4.4 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 7 (38%) |
| **Partial/Composite** | 4 (22%) |
| **No Mapping** | 7 (38%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 11 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `close` | `void close() throws java.io.IOException` | 10 | direct | trivial | `close` | `close(fd: number): Promise<void>` |
| `skipValue` | `void skipValue() throws java.io.IOException` | 7 | near | easy | `value` | `value: number` |
| `nextString` | `String nextString() throws java.io.IOException` | 7 | near | moderate | `errnoToString` | `errnoToString(errno: number): string` |
| `beginArray` | `void beginArray() throws java.io.IOException` | 7 | near | moderate | `begin` | `begin(name: string, flags?: number): HiTraceId` |
| `nextName` | `String nextName() throws java.io.IOException` | 7 | near | moderate | `bundleName` | `bundleName?: string` |
| `isLenient` | `boolean isLenient()` | 6 | near | moderate | `isEncoding` | `isEncoding(encoding: string): boolean` |
| `beginObject` | `void beginObject() throws java.io.IOException` | 6 | near | moderate | `begin` | `begin(name: string, flags?: number): HiTraceId` |
| `setLenient` | `void setLenient(boolean)` | 6 | partial | moderate | `setTimezone` | `setTimezone(timezone: string, callback: AsyncCallback<void>): void` |
| `endArray` | `void endArray() throws java.io.IOException` | 5 | partial | moderate | `end` | `end(id: HiTraceId): void` |
| `hasNext` | `boolean hasNext() throws java.io.IOException` | 5 | partial | moderate | `hasRight` | `hasRight(deviceName: string): boolean` |
| `endObject` | `void endObject() throws java.io.IOException` | 5 | partial | moderate | `end` | `end(id: HiTraceId): void` |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `JsonReader` | 1 | none | Return safe default (null/false/0/empty) |
| `nextBoolean` | 1 | none | throw UnsupportedOperationException |
| `nextDouble` | 1 | none | throw UnsupportedOperationException |
| `nextInt` | 1 | none | throw UnsupportedOperationException |
| `nextLong` | 1 | none | Store callback, never fire |
| `nextNull` | 1 | none | throw UnsupportedOperationException |
| `peek` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.util.JsonReader`:


## Quality Gates

Before marking `android.util.JsonReader` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 18 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 11 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
