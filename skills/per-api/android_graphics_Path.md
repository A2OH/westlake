# SKILL: android.graphics.Path

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Path`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Path` |
| **Package** | `android.graphics` |
| **Total Methods** | 45 |
| **Avg Score** | 4.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 8 (17%) |
| **Partial/Composite** | 32 (71%) |
| **No Mapping** | 5 (11%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 17 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Path` | `Path()` | 8 | direct | easy | `path` | `path: string` |
| `Path` | `Path(@Nullable android.graphics.Path)` | 8 | direct | easy | `path` | `path: string` |
| `close` | `void close()` | 8 | direct | easy | `close` | `close(file: number | File): Promise<void>` |
| `offset` | `void offset(float, float, @Nullable android.graphics.Path)` | 8 | direct | easy | `offset` | `offset: number): boolean` |
| `offset` | `void offset(float, float)` | 8 | direct | easy | `offset` | `offset: number): boolean` |
| `reset` | `void reset()` | 8 | direct | easy | `reset` | `reset(wallpaperType: WallpaperType, callback: AsyncCallback<void>): void` |
| `rewind` | `void rewind()` | 8 | direct | easy | `rewind` | `void rewind(FILE *)` |
| `set` | `void set(@NonNull android.graphics.Path)` | 8 | direct | easy | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `rCubicTo` | `void rCubicTo(float, float, float, float, float, float)` | 5 | partial | moderate | `OH_Drawing_PathCubicTo` | `void OH_Drawing_PathCubicTo(OH_Drawing_Path*, float ctrlX1, float ctrlY1, float ctrlX2, float ctrlY2, float endX, float endY)` |
| `addRoundRect` | `void addRoundRect(@NonNull android.graphics.RectF, float, float, @NonNull android.graphics.Path.Direction)` | 5 | partial | moderate | `OH_Drawing_RoundRectCreate` | `endif


OH_Drawing_RoundRect* OH_Drawing_RoundRectCreate(const OH_Drawing_Rect*, float xRad, float yRad)` |
| `addRoundRect` | `void addRoundRect(float, float, float, float, float, float, @NonNull android.graphics.Path.Direction)` | 5 | partial | moderate | `OH_Drawing_RoundRectCreate` | `endif


OH_Drawing_RoundRect* OH_Drawing_RoundRectCreate(const OH_Drawing_Rect*, float xRad, float yRad)` |
| `addRoundRect` | `void addRoundRect(@NonNull android.graphics.RectF, @NonNull float[], @NonNull android.graphics.Path.Direction)` | 5 | partial | moderate | `OH_Drawing_RoundRectCreate` | `endif


OH_Drawing_RoundRect* OH_Drawing_RoundRectCreate(const OH_Drawing_Rect*, float xRad, float yRad)` |
| `addRoundRect` | `void addRoundRect(float, float, float, float, @NonNull float[], @NonNull android.graphics.Path.Direction)` | 5 | partial | moderate | `OH_Drawing_RoundRectCreate` | `endif


OH_Drawing_RoundRect* OH_Drawing_RoundRectCreate(const OH_Drawing_Rect*, float xRad, float yRad)` |
| `setLastPoint` | `void setLastPoint(float, float)` | 5 | partial | moderate | `whitePointX` | `whitePointX: number` |
| `rLineTo` | `void rLineTo(float, float)` | 5 | partial | moderate | `OH_Drawing_PathLineTo` | `void OH_Drawing_PathLineTo(OH_Drawing_Path*, float x, float y)` |
| `rMoveTo` | `void rMoveTo(float, float)` | 5 | partial | moderate | `OH_Drawing_PathMoveTo` | `void OH_Drawing_PathMoveTo(OH_Drawing_Path*, float x, float y)` |
| `rQuadTo` | `void rQuadTo(float, float, float, float)` | 5 | partial | moderate | `OH_Drawing_PathQuadTo` | `void OH_Drawing_PathQuadTo(OH_Drawing_Path*, float ctrlX, float ctrlY, float endX, float endY)` |

## Stub APIs (score < 5): 28 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `cubicTo` | 5 | partial | throw UnsupportedOperationException |
| `setFillType` | 5 | partial | Log warning + no-op |
| `addCircle` | 4 | partial | Log warning + no-op |
| `lineTo` | 4 | partial | throw UnsupportedOperationException |
| `moveTo` | 4 | partial | throw UnsupportedOperationException |
| `quadTo` | 4 | partial | throw UnsupportedOperationException |
| `transform` | 4 | partial | throw UnsupportedOperationException |
| `transform` | 4 | partial | throw UnsupportedOperationException |
| `addArc` | 4 | partial | Log warning + no-op |
| `addArc` | 4 | partial | Log warning + no-op |
| `addOval` | 4 | partial | Log warning + no-op |
| `addOval` | 4 | partial | Log warning + no-op |
| `arcTo` | 4 | partial | throw UnsupportedOperationException |
| `arcTo` | 4 | partial | throw UnsupportedOperationException |
| `arcTo` | 4 | partial | throw UnsupportedOperationException |
| `isRect` | 4 | composite | Return safe default (null/false/0/empty) |
| `addRect` | 4 | composite | Log warning + no-op |
| `addRect` | 4 | composite | Log warning + no-op |
| `addPath` | 4 | composite | Log warning + no-op |
| `addPath` | 4 | composite | Log warning + no-op |
| `addPath` | 4 | composite | Log warning + no-op |
| `isInverseFillType` | 3 | composite | Return safe default (null/false/0/empty) |
| `toggleInverseFillType` | 3 | composite | throw UnsupportedOperationException |
| `isEmpty` | 2 | none | Return safe default (null/false/0/empty) |
| `computeBounds` | 1 | none | Log warning + no-op |
| `incReserve` | 1 | none | throw UnsupportedOperationException |
| `op` | 1 | none | throw UnsupportedOperationException |
| `op` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 17 methods that have score >= 5
2. Stub 28 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Path`:


## Quality Gates

Before marking `android.graphics.Path` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 45 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 17 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
