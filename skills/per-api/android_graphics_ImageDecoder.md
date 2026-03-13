# SKILL: android.graphics.ImageDecoder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.ImageDecoder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.ImageDecoder` |
| **Package** | `android.graphics` |
| **Total Methods** | 18 |
| **Avg Score** | 4.3 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 1 (5%) |
| **Partial/Composite** | 17 (94%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `close` | `void close()` | 8 | direct | easy | `close` | `close(file: number | File): Promise<void>` |
| `setOnPartialImageListener` | `void setOnPartialImageListener(@Nullable android.graphics.ImageDecoder.OnPartialImageListener)` | 6 | partial | moderate | `OH_NativeImage_Destroy` | `void OH_NativeImage_Destroy(OH_NativeImage** image)` |
| `setTargetColorSpace` | `void setTargetColorSpace(android.graphics.ColorSpace)` | 5 | partial | moderate | `OH_NativeBuffer_SetColorSpace` | `int32_t OH_NativeBuffer_SetColorSpace(OH_NativeBuffer *buffer, OH_NativeBuffer_ColorSpace colorSpace)` |
| `setTargetSampleSize` | `void setTargetSampleSize(@IntRange(from=1) int)` | 5 | partial | moderate | `OH_Drawing_SetTextStyleFontSize` | `void OH_Drawing_SetTextStyleFontSize(OH_Drawing_TextStyle*, double)` |
| `setTargetSize` | `void setTargetSize(@IntRange(from=1) @Px int, @IntRange(from=1) @Px int)` | 5 | partial | moderate | `OH_Drawing_FontSetTextSize` | `void OH_Drawing_FontSetTextSize(OH_Drawing_Font*, float textSize)` |
| `getAllocator` | `int getAllocator()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |

## Stub APIs (score < 5): 12 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setAllocator` | 4 | partial | Log warning + no-op |
| `setMemorySizePolicy` | 4 | composite | Log warning + no-op |
| `setCrop` | 4 | composite | Log warning + no-op |
| `setPostProcessor` | 4 | composite | Log warning + no-op |
| `setMutableRequired` | 4 | composite | Log warning + no-op |
| `getMemorySizePolicy` | 4 | composite | Return safe default (null/false/0/empty) |
| `isMimeTypeSupported` | 4 | composite | Return safe default (null/false/0/empty) |
| `setUnpremultipliedRequired` | 3 | composite | Log warning + no-op |
| `setDecodeAsAlphaMaskEnabled` | 3 | composite | Log warning + no-op |
| `isDecodeAsAlphaMaskEnabled` | 3 | composite | Return safe default (null/false/0/empty) |
| `isMutableRequired` | 3 | composite | Return safe default (null/false/0/empty) |
| `isUnpremultipliedRequired` | 3 | composite | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.ImageDecoder`:


## Quality Gates

Before marking `android.graphics.ImageDecoder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 18 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
