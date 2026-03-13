# SKILL: android.media.ThumbnailUtils

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.ThumbnailUtils`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.ThumbnailUtils` |
| **Package** | `android.media` |
| **Total Methods** | 3 |
| **Avg Score** | 7.5 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 3 (100%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `extractThumbnail` | `static android.graphics.Bitmap extractThumbnail(android.graphics.Bitmap, int, int)` | 8 | near | easy | `getThumbnail` | `getThumbnail(callback: AsyncCallback<image.PixelMap>): void` |
| `extractThumbnail` | `static android.graphics.Bitmap extractThumbnail(android.graphics.Bitmap, int, int, int)` | 8 | near | easy | `getThumbnail` | `getThumbnail(callback: AsyncCallback<image.PixelMap>): void` |
| `ThumbnailUtils` | `ThumbnailUtils()` | 7 | near | moderate | `getThumbnail` | `getThumbnail(callback: AsyncCallback<image.PixelMap>): void` |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.media.ThumbnailUtils`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.ThumbnailUtils` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 3 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
