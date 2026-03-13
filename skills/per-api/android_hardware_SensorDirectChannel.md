# SKILL: android.hardware.SensorDirectChannel

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.hardware.SensorDirectChannel`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.hardware.SensorDirectChannel` |
| **Package** | `android.hardware` |
| **Total Methods** | 3 |
| **Avg Score** | 9.3 |
| **Scenario** | S1: Direct Mapping (Thin Wrapper) |
| **Strategy** | Simple delegation to OHBridge |
| **Direct/Near** | 3 (100%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `close` | `void close()` | 10 | direct | trivial | `close` | `close(fd: number): Promise<void>` |
| `configure` | `int configure(android.hardware.Sensor, int)` | 10 | direct | trivial | `configure` | `configure(config: ConfigOption): boolean` |
| `isOpen` | `boolean isOpen()` | 8 | direct | easy | `open` | `open(path: string, flags?: number, mode?: number): Promise<number>` |

## AI Agent Instructions

**Scenario: S1 — Direct Mapping (Thin Wrapper)**

1. Create Java shim at `shim/java/android/hardware/SensorDirectChannel.java`
2. For each method, delegate to `OHBridge.xxx()` — one bridge call per Android call
3. Add `static native` declarations to `OHBridge.java`
4. Add mock implementations to `test-apps/mock/.../OHBridge.java`
5. Add test section to `HeadlessTest.java` — call each method with valid + edge inputs
6. Test null args, boundary values, return types

## Dependencies

Check if these related classes are already shimmed before generating `android.hardware.SensorDirectChannel`:


## Quality Gates

Before marking `android.hardware.SensorDirectChannel` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 3 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
