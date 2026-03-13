# SKILL: android.graphics.BitmapRegionDecoder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.BitmapRegionDecoder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.BitmapRegionDecoder` |
| **Package** | `android.graphics` |
| **Total Methods** | 9 |
| **Avg Score** | 4.3 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 4 (44%) |
| **Partial/Composite** | 2 (22%) |
| **No Mapping** | 3 (33%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `newInstance` | `static android.graphics.BitmapRegionDecoder newInstance(byte[], int, int, boolean) throws java.io.IOException` | 6 | near | moderate | `napi_get_instance_data` | `NAPI_EXTERN napi_status napi_get_instance_data(napi_env env, void** data)` |
| `newInstance` | `static android.graphics.BitmapRegionDecoder newInstance(java.io.FileDescriptor, boolean) throws java.io.IOException` | 6 | near | moderate | `napi_get_instance_data` | `NAPI_EXTERN napi_status napi_get_instance_data(napi_env env, void** data)` |
| `newInstance` | `static android.graphics.BitmapRegionDecoder newInstance(java.io.InputStream, boolean) throws java.io.IOException` | 6 | near | moderate | `napi_get_instance_data` | `NAPI_EXTERN napi_status napi_get_instance_data(napi_env env, void** data)` |
| `newInstance` | `static android.graphics.BitmapRegionDecoder newInstance(String, boolean) throws java.io.IOException` | 6 | near | moderate | `napi_get_instance_data` | `NAPI_EXTERN napi_status napi_get_instance_data(napi_env env, void** data)` |
| `getWidth` | `int getWidth()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getHeight` | `int getHeight()` | 5 | partial | moderate | `OH_Drawing_BitmapGetHeight` | `uint32_t OH_Drawing_BitmapGetHeight(OH_Drawing_Bitmap*)` |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isRecycled` | 2 | none | Return safe default (null/false/0/empty) |
| `decodeRegion` | 1 | none | Store callback, never fire |
| `recycle` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.BitmapRegionDecoder`:


## Quality Gates

Before marking `android.graphics.BitmapRegionDecoder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
