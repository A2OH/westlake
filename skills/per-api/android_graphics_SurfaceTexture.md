# SKILL: android.graphics.SurfaceTexture

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.SurfaceTexture`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.SurfaceTexture` |
| **Package** | `android.graphics` |
| **Total Methods** | 14 |
| **Avg Score** | 5.3 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 5 (35%) |
| **Partial/Composite** | 9 (64%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `release` | `void release()` | 8 | direct | easy | `release` | `release(callback: AsyncCallback<void>): void` |
| `setOnFrameAvailableListener` | `void setOnFrameAvailableListener(@Nullable android.graphics.SurfaceTexture.OnFrameAvailableListener)` | 8 | near | easy | `OH_NativeImage_SetOnFrameAvailableListener` | `int32_t OH_NativeImage_SetOnFrameAvailableListener(OH_NativeImage* image, OH_OnFrameAvailableListener listener)` |
| `setOnFrameAvailableListener` | `void setOnFrameAvailableListener(@Nullable android.graphics.SurfaceTexture.OnFrameAvailableListener, @Nullable android.os.Handler)` | 8 | near | easy | `OH_NativeImage_SetOnFrameAvailableListener` | `int32_t OH_NativeImage_SetOnFrameAvailableListener(OH_NativeImage* image, OH_OnFrameAvailableListener listener)` |
| `getTransformMatrix` | `void getTransformMatrix(float[])` | 7 | near | easy | `OH_NativeImage_GetTransformMatrix` | `int32_t OH_NativeImage_GetTransformMatrix(OH_NativeImage* image, float matrix[16])` |
| `getTimestamp` | `long getTimestamp()` | 6 | near | moderate | `OH_NativeImage_GetTimestamp` | `int64_t OH_NativeImage_GetTimestamp(OH_NativeImage* image)` |
| `attachToGLContext` | `void attachToGLContext(int)` | 6 | partial | moderate | `OH_NativeImage_AttachContext` | `int32_t OH_NativeImage_AttachContext(OH_NativeImage* image, uint32_t textureId)` |
| `detachFromGLContext` | `void detachFromGLContext()` | 6 | partial | moderate | `OH_NativeImage_DetachContext` | `int32_t OH_NativeImage_DetachContext(OH_NativeImage* image)` |
| `updateTexImage` | `void updateTexImage()` | 5 | partial | moderate | `OH_NativeImage_UpdateSurfaceImage` | `int32_t OH_NativeImage_UpdateSurfaceImage(OH_NativeImage* image)` |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setDefaultBufferSize` | 4 | partial | Log warning + no-op |
| `SurfaceTexture` | 3 | composite | throw UnsupportedOperationException |
| `SurfaceTexture` | 3 | composite | throw UnsupportedOperationException |
| `SurfaceTexture` | 3 | composite | throw UnsupportedOperationException |
| `releaseTexImage` | 3 | composite | No-op |
| `isReleased` | 3 | composite | No-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.SurfaceTexture`:


## Quality Gates

Before marking `android.graphics.SurfaceTexture` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
