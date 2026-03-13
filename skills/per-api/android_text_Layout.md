# SKILL: android.text.Layout

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.text.Layout`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.text.Layout` |
| **Package** | `android.text` |
| **Total Methods** | 49 |
| **Avg Score** | 4.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 1 (2%) |
| **Partial/Composite** | 42 (85%) |
| **No Mapping** | 6 (12%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 13 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getText` | `final CharSequence getText()` | 6 | near | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getParagraphDirection` | `abstract int getParagraphDirection(int)` | 6 | partial | moderate | `getDragPreview` | `getDragPreview(): DragPreview` |
| `getPaint` | `final android.text.TextPaint getPaint()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getWidth` | `final int getWidth()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getParagraphLeft` | `final int getParagraphLeft(int)` | 5 | partial | moderate | `getDragPreview` | `getDragPreview(): DragPreview` |
| `getLineStart` | `abstract int getLineStart(int)` | 5 | partial | moderate | `napi_get_instance_data` | `NAPI_EXTERN napi_status napi_get_instance_data(napi_env env, void** data)` |
| `getParagraphRight` | `final int getParagraphRight(int)` | 5 | partial | moderate | `getDragPreview` | `getDragPreview(): DragPreview` |
| `getLineAscent` | `final int getLineAscent(int)` | 5 | partial | moderate | `napi_get_instance_data` | `NAPI_EXTERN napi_status napi_get_instance_data(napi_env env, void** data)` |
| `getParagraphAlignment` | `final android.text.Layout.Alignment getParagraphAlignment(int)` | 5 | partial | moderate | `getDragPreview` | `getDragPreview(): DragPreview` |
| `getAlignment` | `final android.text.Layout.Alignment getAlignment()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getHeight` | `int getHeight()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getLineDescent` | `abstract int getLineDescent(int)` | 5 | partial | moderate | `napi_get_instance_data` | `NAPI_EXTERN napi_status napi_get_instance_data(napi_env env, void** data)` |
| `getLineWidth` | `float getLineWidth(int)` | 5 | partial | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |

## Stub APIs (score < 5): 36 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getLineDirections` | 5 | partial | Return safe default (null/false/0/empty) |
| `getDesiredWidth` | 5 | partial | Return safe default (null/false/0/empty) |
| `getDesiredWidth` | 5 | partial | Return safe default (null/false/0/empty) |
| `getLineBounds` | 5 | partial | Return safe default (null/false/0/empty) |
| `getSpacingAdd` | 5 | partial | Return safe default (null/false/0/empty) |
| `getTopPadding` | 5 | partial | Return safe default (null/false/0/empty) |
| `getEllipsizedWidth` | 5 | partial | Return safe default (null/false/0/empty) |
| `getLineContainsTab` | 5 | partial | Return safe default (null/false/0/empty) |
| `getLineEnd` | 5 | partial | Return safe default (null/false/0/empty) |
| `getLineMax` | 5 | partial | Return safe default (null/false/0/empty) |
| `getLineTop` | 5 | partial | Return safe default (null/false/0/empty) |
| `getLineBaseline` | 5 | partial | Return safe default (null/false/0/empty) |
| `getLineVisibleEnd` | 5 | partial | Return safe default (null/false/0/empty) |
| `getLineLeft` | 4 | partial | Return safe default (null/false/0/empty) |
| `getOffsetToRightOf` | 4 | partial | Return safe default (null/false/0/empty) |
| `getSpacingMultiplier` | 4 | partial | Return safe default (null/false/0/empty) |
| `getBottomPadding` | 4 | partial | Return safe default (null/false/0/empty) |
| `getSelectionPath` | 4 | partial | Return safe default (null/false/0/empty) |
| `getLineCount` | 4 | partial | Return safe default (null/false/0/empty) |
| `getOffsetToLeftOf` | 4 | partial | Return safe default (null/false/0/empty) |
| `getEllipsisStart` | 4 | partial | Return dummy instance / no-op |
| `getSecondaryHorizontal` | 4 | partial | Return safe default (null/false/0/empty) |
| `getLineBottom` | 4 | partial | Return safe default (null/false/0/empty) |
| `getLineRight` | 4 | partial | Return safe default (null/false/0/empty) |
| `getLineForOffset` | 4 | partial | Return safe default (null/false/0/empty) |
| `getLineForVertical` | 4 | partial | Return safe default (null/false/0/empty) |
| `getPrimaryHorizontal` | 4 | composite | Return safe default (null/false/0/empty) |
| `getCursorPath` | 4 | composite | Return safe default (null/false/0/empty) |
| `getEllipsisCount` | 4 | composite | Return safe default (null/false/0/empty) |
| `getOffsetForHorizontal` | 4 | composite | Return safe default (null/false/0/empty) |
| `increaseWidthTo` | 2 | none | throw UnsupportedOperationException |
| `Layout` | 1 | none | throw UnsupportedOperationException |
| `draw` | 1 | none | throw UnsupportedOperationException |
| `draw` | 1 | none | throw UnsupportedOperationException |
| `isRtlCharAt` | 1 | none | Return safe default (null/false/0/empty) |
| `isSpanned` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 13 methods that have score >= 5
2. Stub 36 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.text.Layout`:


## Quality Gates

Before marking `android.text.Layout` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 49 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 13 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
