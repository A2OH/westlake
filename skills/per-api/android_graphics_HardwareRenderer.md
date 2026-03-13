# SKILL: android.graphics.HardwareRenderer

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.HardwareRenderer`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.HardwareRenderer` |
| **Package** | `android.graphics` |
| **Total Methods** | 13 |
| **Avg Score** | 4.1 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 2 (15%) |
| **Partial/Composite** | 9 (69%) |
| **No Mapping** | 2 (15%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `start` | `void start()` | 7 | near | easy | `start` | `start(sinkDeviceDescriptor: string, srcInputDeviceId: number, callback: AsyncCallback<void>): void` |
| `stop` | `void stop()` | 7 | near | easy | `stop` | `stop(callback: AsyncCallback<void>): void` |
| `destroy` | `void destroy()` | 6 | partial | moderate | `napi_async_destroy` | `NAPI_EXTERN napi_status napi_async_destroy(napi_env env,
                                           napi_async_context async_context)` |

## Stub APIs (score < 5): 10 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setSurface` | 5 | partial | Log warning + no-op |
| `setLightSourceAlpha` | 4 | partial | Log warning + no-op |
| `setName` | 4 | partial | Log warning + no-op |
| `notifyFramePending` | 4 | partial | throw UnsupportedOperationException |
| `setOpaque` | 4 | composite | Log warning + no-op |
| `setLightSourceGeometry` | 4 | composite | Log warning + no-op |
| `setContentRoot` | 4 | composite | Log warning + no-op |
| `clearContent` | 3 | composite | Store callback, never fire |
| `isOpaque` | 2 | none | Return safe default (null/false/0/empty) |
| `HardwareRenderer` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.HardwareRenderer`:


## Quality Gates

Before marking `android.graphics.HardwareRenderer` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 13 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
