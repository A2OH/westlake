# SKILL: android.graphics.Outline

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Outline`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Outline` |
| **Package** | `android.graphics` |
| **Total Methods** | 18 |
| **Avg Score** | 4.4 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (16%) |
| **Partial/Composite** | 12 (66%) |
| **No Mapping** | 3 (16%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `offset` | `void offset(int, int)` | 8 | direct | easy | `offset` | `offset: number): boolean` |
| `set` | `void set(@NonNull android.graphics.Outline)` | 8 | direct | easy | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `getRect` | `boolean getRect(@NonNull android.graphics.Rect)` | 6 | near | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |
| `getAlpha` | `float getAlpha()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `setAlpha` | `void setAlpha(@FloatRange(from=0.0, to=1.0) float)` | 5 | partial | moderate | `OH_Drawing_PenSetAlpha` | `void OH_Drawing_PenSetAlpha(OH_Drawing_Pen*, uint8_t alpha)` |
| `getRadius` | `float getRadius()` | 5 | partial | moderate | `getDragPreview` | `getDragPreview(): DragPreview` |
| `setRoundRect` | `void setRoundRect(int, int, int, int, float)` | 5 | partial | moderate | `OH_Drawing_RoundRectCreate` | `endif


OH_Drawing_RoundRect* OH_Drawing_RoundRectCreate(const OH_Drawing_Rect*, float xRad, float yRad)` |
| `setRoundRect` | `void setRoundRect(@NonNull android.graphics.Rect, float)` | 5 | partial | moderate | `OH_Drawing_RoundRectCreate` | `endif


OH_Drawing_RoundRect* OH_Drawing_RoundRectCreate(const OH_Drawing_Rect*, float xRad, float yRad)` |

## Stub APIs (score < 5): 10 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setPath` | 4 | partial | Log warning + no-op |
| `canClip` | 4 | partial | Return safe default (null/false/0/empty) |
| `setRect` | 4 | composite | Log warning + no-op |
| `setRect` | 4 | composite | Log warning + no-op |
| `setOval` | 3 | composite | Log warning + no-op |
| `setOval` | 3 | composite | Log warning + no-op |
| `setEmpty` | 3 | composite | Log warning + no-op |
| `isEmpty` | 2 | none | Return safe default (null/false/0/empty) |
| `Outline` | 1 | none | throw UnsupportedOperationException |
| `Outline` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 8 methods that have score >= 5
2. Stub 10 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Outline`:


## Quality Gates

Before marking `android.graphics.Outline` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 18 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
