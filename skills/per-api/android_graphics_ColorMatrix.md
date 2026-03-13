# SKILL: android.graphics.ColorMatrix

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.ColorMatrix`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.ColorMatrix` |
| **Package** | `android.graphics` |
| **Total Methods** | 15 |
| **Avg Score** | 4.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (20%) |
| **Partial/Composite** | 10 (66%) |
| **No Mapping** | 2 (13%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `reset` | `void reset()` | 8 | direct | easy | `reset` | `reset(wallpaperType: WallpaperType, callback: AsyncCallback<void>): void` |
| `set` | `void set(android.graphics.ColorMatrix)` | 8 | direct | easy | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `set` | `void set(float[])` | 8 | direct | easy | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `getArray` | `final float[] getArray()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `ColorMatrix` | 5 | partial | throw UnsupportedOperationException |
| `ColorMatrix` | 5 | partial | throw UnsupportedOperationException |
| `ColorMatrix` | 5 | partial | throw UnsupportedOperationException |
| `setSaturation` | 5 | partial | Log warning + no-op |
| `setConcat` | 5 | partial | Log warning + no-op |
| `setRotate` | 4 | partial | Log warning + no-op |
| `setScale` | 4 | partial | Log warning + no-op |
| `setRGB2YUV` | 4 | composite | Log warning + no-op |
| `setYUV2RGB` | 4 | composite | Log warning + no-op |
| `postConcat` | 1 | none | Store callback, never fire |
| `preConcat` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 4 methods that have score >= 5
2. Stub 11 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.ColorMatrix`:


## Quality Gates

Before marking `android.graphics.ColorMatrix` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 15 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
