# SKILL: android.graphics.Typeface.Builder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Typeface.Builder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Typeface.Builder` |
| **Package** | `android.graphics.Typeface` |
| **Total Methods** | 11 |
| **Avg Score** | 4.6 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 11 (100%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Builder` | `Typeface.Builder(@NonNull java.io.File)` | 6 | partial | moderate | `createFromBuilder` | `createFromBuilder(builder: CustomBuilder, callback: AsyncCallback<image.PixelMap>): void` |
| `Builder` | `Typeface.Builder(@NonNull java.io.FileDescriptor)` | 6 | partial | moderate | `createFromBuilder` | `createFromBuilder(builder: CustomBuilder, callback: AsyncCallback<image.PixelMap>): void` |
| `Builder` | `Typeface.Builder(@NonNull String)` | 6 | partial | moderate | `createFromBuilder` | `createFromBuilder(builder: CustomBuilder, callback: AsyncCallback<image.PixelMap>): void` |
| `Builder` | `Typeface.Builder(@NonNull android.content.res.AssetManager, @NonNull String)` | 6 | partial | moderate | `createFromBuilder` | `createFromBuilder(builder: CustomBuilder, callback: AsyncCallback<image.PixelMap>): void` |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setItalic` | 5 | partial | Log warning + no-op |
| `setWeight` | 4 | partial | Log warning + no-op |
| `setFontVariationSettings` | 4 | composite | Log warning + no-op |
| `setFontVariationSettings` | 4 | composite | Log warning + no-op |
| `setTtcIndex` | 4 | composite | Log warning + no-op |
| `build` | 4 | composite | throw UnsupportedOperationException |
| `setFallback` | 4 | composite | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Typeface.Builder`:


## Quality Gates

Before marking `android.graphics.Typeface.Builder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
