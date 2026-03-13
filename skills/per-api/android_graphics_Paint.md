# SKILL: android.graphics.Paint

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Paint`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Paint` |
| **Package** | `android.graphics` |
| **Total Methods** | 115 |
| **Avg Score** | 4.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 18 (15%) |
| **Partial/Composite** | 94 (81%) |
| **No Mapping** | 3 (2%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 43 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `reset` | `void reset()` | 8 | direct | easy | `reset` | `reset(wallpaperType: WallpaperType, callback: AsyncCallback<void>): void` |
| `set` | `void set(android.graphics.Paint)` | 8 | direct | easy | `set` | `set(key: string, value: string, callback: AsyncCallback<void>): void` |
| `setFakeBoldText` | `void setFakeBoldText(boolean)` | 7 | near | moderate | `OH_Drawing_FontSetFakeBoldText` | `void OH_Drawing_FontSetFakeBoldText(OH_Drawing_Font*, bool isFakeBoldText)` |
| `isFakeBoldText` | `final boolean isFakeBoldText()` | 6 | near | moderate | `OH_Drawing_FontSetFakeBoldText` | `void OH_Drawing_FontSetFakeBoldText(OH_Drawing_Font*, bool isFakeBoldText)` |
| `setLinearText` | `void setLinearText(boolean)` | 6 | near | moderate | `OH_Drawing_FontSetLinearText` | `void OH_Drawing_FontSetLinearText(OH_Drawing_Font*, bool isLinearText)` |
| `setAntiAlias` | `void setAntiAlias(boolean)` | 6 | near | moderate | `OH_Drawing_PenSetAntiAlias` | `void OH_Drawing_PenSetAntiAlias(OH_Drawing_Pen*, bool)` |
| `getColorFilter` | `android.graphics.ColorFilter getColorFilter()` | 6 | near | moderate | `OH_Drawing_FilterSetColorFilter` | `void OH_Drawing_FilterSetColorFilter(OH_Drawing_Filter*, OH_Drawing_ColorFilter*)` |
| `setColorFilter` | `android.graphics.ColorFilter setColorFilter(android.graphics.ColorFilter)` | 6 | near | moderate | `OH_Drawing_FilterSetColorFilter` | `void OH_Drawing_FilterSetColorFilter(OH_Drawing_Filter*, OH_Drawing_ColorFilter*)` |
| `setTextLocale` | `void setTextLocale(@NonNull java.util.Locale)` | 6 | near | moderate | `OH_Drawing_SetTextStyleLocale` | `void OH_Drawing_SetTextStyleLocale(OH_Drawing_TextStyle*, const char*)` |
| `getLetterSpacing` | `float getLetterSpacing()` | 6 | near | moderate | `OH_Drawing_SetTextStyleLetterSpacing` | `void OH_Drawing_SetTextStyleLetterSpacing(OH_Drawing_TextStyle*, double)` |
| `getTextSkewX` | `float getTextSkewX()` | 6 | near | moderate | `OH_Drawing_FontSetTextSkewX` | `void OH_Drawing_FontSetTextSkewX(OH_Drawing_Font*, float skewX)` |
| `setLetterSpacing` | `void setLetterSpacing(float)` | 6 | near | moderate | `OH_Drawing_SetTextStyleLetterSpacing` | `void OH_Drawing_SetTextStyleLetterSpacing(OH_Drawing_TextStyle*, double)` |
| `setTextSkewX` | `void setTextSkewX(float)` | 6 | near | moderate | `OH_Drawing_FontSetTextSkewX` | `void OH_Drawing_FontSetTextSkewX(OH_Drawing_Font*, float skewX)` |
| `isAntiAlias` | `final boolean isAntiAlias()` | 6 | near | moderate | `OH_Drawing_PenIsAntiAlias` | `bool OH_Drawing_PenIsAntiAlias(const OH_Drawing_Pen*)` |
| `getMaskFilter` | `android.graphics.MaskFilter getMaskFilter()` | 6 | near | moderate | `OH_Drawing_FilterSetMaskFilter` | `void OH_Drawing_FilterSetMaskFilter(OH_Drawing_Filter*, OH_Drawing_MaskFilter*)` |
| `setMaskFilter` | `android.graphics.MaskFilter setMaskFilter(android.graphics.MaskFilter)` | 6 | near | moderate | `OH_Drawing_FilterSetMaskFilter` | `void OH_Drawing_FilterSetMaskFilter(OH_Drawing_Filter*, OH_Drawing_MaskFilter*)` |
| `setTextLocales` | `void setTextLocales(@NonNull @Size(min=1) android.os.LocaleList)` | 6 | near | moderate | `OH_Drawing_SetTextStyleLocale` | `void OH_Drawing_SetTextStyleLocale(OH_Drawing_TextStyle*, const char*)` |
| `isLinearText` | `final boolean isLinearText()` | 6 | near | moderate | `OH_Drawing_FontSetLinearText` | `void OH_Drawing_FontSetLinearText(OH_Drawing_Font*, bool isLinearText)` |
| `getTextSize` | `float getTextSize()` | 6 | partial | moderate | `OH_Drawing_FontSetTextSize` | `void OH_Drawing_FontSetTextSize(OH_Drawing_Font*, float textSize)` |
| `getTypeface` | `android.graphics.Typeface getTypeface()` | 6 | partial | moderate | `OH_Drawing_FontSetTypeface` | `void OH_Drawing_FontSetTypeface(OH_Drawing_Font*, OH_Drawing_Typeface*)` |
| `setTextSize` | `void setTextSize(float)` | 6 | partial | moderate | `OH_Drawing_FontSetTextSize` | `void OH_Drawing_FontSetTextSize(OH_Drawing_Font*, float textSize)` |
| `setTypeface` | `android.graphics.Typeface setTypeface(android.graphics.Typeface)` | 6 | partial | moderate | `OH_Drawing_FontSetTypeface` | `void OH_Drawing_FontSetTypeface(OH_Drawing_Font*, OH_Drawing_Typeface*)` |
| `setWordSpacing` | `void setWordSpacing(@Px float)` | 6 | partial | moderate | `OH_Drawing_SetTextStyleWordSpacing` | `void OH_Drawing_SetTextStyleWordSpacing(OH_Drawing_TextStyle*, double)` |
| `getTextAlign` | `android.graphics.Paint.Align getTextAlign()` | 6 | partial | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |
| `getTextScaleX` | `float getTextScaleX()` | 6 | partial | moderate | `OH_Drawing_SetTextStyleLocale` | `void OH_Drawing_SetTextStyleLocale(OH_Drawing_TextStyle*, const char*)` |
| `setTextScaleX` | `void setTextScaleX(float)` | 6 | partial | moderate | `OH_Drawing_SetTextStyleLocale` | `void OH_Drawing_SetTextStyleLocale(OH_Drawing_TextStyle*, const char*)` |
| `getAlpha` | `int getAlpha()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getFlags` | `int getFlags()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getStyle` | `android.graphics.Paint.Style getStyle()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `isElegantTextHeight` | `boolean isElegantTextHeight()` | 5 | partial | moderate | `OH_Drawing_SetTextStyleFontHeight` | `void OH_Drawing_SetTextStyleFontHeight(OH_Drawing_TextStyle*, double)` |
| `setAlpha` | `void setAlpha(int)` | 5 | partial | moderate | `OH_Drawing_PenSetAlpha` | `void OH_Drawing_PenSetAlpha(OH_Drawing_Pen*, uint8_t alpha)` |
| `setColor` | `void setColor(@ColorInt int)` | 5 | partial | moderate | `OH_Drawing_PenSetColor` | `void OH_Drawing_PenSetColor(OH_Drawing_Pen*, uint32_t color)` |
| `setColor` | `void setColor(@ColorLong long)` | 5 | partial | moderate | `OH_Drawing_PenSetColor` | `void OH_Drawing_PenSetColor(OH_Drawing_Pen*, uint32_t color)` |
| `setTextAlign` | `void setTextAlign(android.graphics.Paint.Align)` | 5 | partial | moderate | `OH_Drawing_SetTypographyTextAlign` | `void OH_Drawing_SetTypographyTextAlign(OH_Drawing_TypographyStyle*, int)` |
| `getRunAdvance` | `float getRunAdvance(char[], int, int, int, int, boolean, int)` | 5 | partial | moderate | `getDragPreview` | `getDragPreview(): DragPreview` |
| `getRunAdvance` | `float getRunAdvance(CharSequence, int, int, int, int, boolean, int)` | 5 | partial | moderate | `getDragPreview` | `getDragPreview(): DragPreview` |
| `isUnderlineText` | `final boolean isUnderlineText()` | 5 | partial | moderate | `OH_Drawing_FontSetLinearText` | `void OH_Drawing_FontSetLinearText(OH_Drawing_Font*, bool isLinearText)` |
| `getHinting` | `int getHinting()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getShader` | `android.graphics.Shader getShader()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `getStrokeCap` | `android.graphics.Paint.Cap getStrokeCap()` | 5 | partial | moderate | `getRectangleById` | `getRectangleById(id: string): ComponentInfo` |
| `getStrokeMiter` | `float getStrokeMiter()` | 5 | partial | moderate | `getDragPreview` | `getDragPreview(): DragPreview` |
| `getXfermode` | `android.graphics.Xfermode getXfermode()` | 5 | partial | moderate | `get` | `get(id: string, callback: AsyncCallback<image.PixelMap>): void` |
| `setUnderlineText` | `void setUnderlineText(boolean)` | 5 | partial | moderate | `OH_Drawing_FontSetLinearText` | `void OH_Drawing_FontSetLinearText(OH_Drawing_Font*, bool isLinearText)` |

## Stub APIs (score < 5): 72 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setElegantTextHeight` | 5 | partial | Log warning + no-op |
| `getPathEffect` | 5 | partial | Return safe default (null/false/0/empty) |
| `getTextBounds` | 5 | partial | Return safe default (null/false/0/empty) |
| `getTextBounds` | 5 | partial | Return safe default (null/false/0/empty) |
| `getTextBounds` | 5 | partial | Return safe default (null/false/0/empty) |
| `getTextWidths` | 5 | partial | Return safe default (null/false/0/empty) |
| `getTextWidths` | 5 | partial | Return safe default (null/false/0/empty) |
| `getTextWidths` | 5 | partial | Return safe default (null/false/0/empty) |
| `getTextWidths` | 5 | partial | Return safe default (null/false/0/empty) |
| `setPathEffect` | 5 | partial | Log warning + no-op |
| `setFilterBitmap` | 5 | partial | Log warning + no-op |
| `setShader` | 5 | partial | Log warning + no-op |
| `getOffsetForAdvance` | 5 | partial | Return safe default (null/false/0/empty) |
| `getOffsetForAdvance` | 5 | partial | Return safe default (null/false/0/empty) |
| `getFontSpacing` | 5 | partial | Return safe default (null/false/0/empty) |
| `getStrokeWidth` | 5 | partial | Return safe default (null/false/0/empty) |
| `setARGB` | 5 | partial | Log warning + no-op |
| `getFontMetricsInt` | 5 | partial | Return safe default (null/false/0/empty) |
| `getFontMetricsInt` | 5 | partial | Return safe default (null/false/0/empty) |
| `setStyle` | 5 | partial | Log warning + no-op |
| `getTextRunCursor` | 5 | partial | Return safe default (null/false/0/empty) |
| `getTextRunCursor` | 5 | partial | Return safe default (null/false/0/empty) |
| `isFilterBitmap` | 5 | partial | Return safe default (null/false/0/empty) |
| `setBlendMode` | 4 | partial | Log warning + no-op |
| `getFontMetrics` | 4 | partial | Return safe default (null/false/0/empty) |
| `getFontMetrics` | 4 | partial | Return safe default (null/false/0/empty) |
| `getStrokeJoin` | 4 | partial | Return safe default (null/false/0/empty) |
| `setStrokeWidth` | 4 | partial | Log warning + no-op |
| `getStartHyphenEdit` | 4 | partial | Return dummy instance / no-op |
| `setDither` | 4 | partial | Log warning + no-op |
| `setHinting` | 4 | partial | Log warning + no-op |
| `isStrikeThruText` | 4 | partial | Return safe default (null/false/0/empty) |
| `getTextPath` | 4 | partial | Return safe default (null/false/0/empty) |
| `getTextPath` | 4 | partial | Return safe default (null/false/0/empty) |
| `getFillPath` | 4 | partial | Return safe default (null/false/0/empty) |
| `setSubpixelText` | 4 | partial | Log warning + no-op |
| `setStrikeThruText` | 4 | partial | Log warning + no-op |
| `getFontFeatureSettings` | 4 | partial | Return safe default (null/false/0/empty) |
| `setShadowLayer` | 4 | partial | Log warning + no-op |
| `setShadowLayer` | 4 | partial | Log warning + no-op |
| `getTextRunAdvances` | 4 | partial | Return safe default (null/false/0/empty) |
| `getShadowLayerRadius` | 4 | partial | Return safe default (null/false/0/empty) |
| `setStrokeCap` | 4 | partial | Log warning + no-op |
| `setStrokeJoin` | 4 | partial | Log warning + no-op |
| `breakText` | 4 | partial | throw UnsupportedOperationException |
| `breakText` | 4 | partial | throw UnsupportedOperationException |
| `breakText` | 4 | partial | throw UnsupportedOperationException |
| `setStrokeMiter` | 4 | partial | Log warning + no-op |
| `getFontVariationSettings` | 4 | partial | Return safe default (null/false/0/empty) |
| `setStartHyphenEdit` | 4 | partial | Return dummy instance / no-op |
| `isSubpixelText` | 4 | composite | Return safe default (null/false/0/empty) |
| `setFontVariationSettings` | 4 | composite | Log warning + no-op |
| `setFontFeatureSettings` | 4 | composite | Log warning + no-op |
| `measureText` | 4 | composite | throw UnsupportedOperationException |
| `measureText` | 4 | composite | throw UnsupportedOperationException |
| `measureText` | 4 | composite | throw UnsupportedOperationException |
| `measureText` | 4 | composite | throw UnsupportedOperationException |
| `getEndHyphenEdit` | 4 | composite | Return safe default (null/false/0/empty) |
| `getShadowLayerDx` | 4 | composite | Return safe default (null/false/0/empty) |
| `getShadowLayerDy` | 4 | composite | Return safe default (null/false/0/empty) |
| `setEndHyphenEdit` | 4 | composite | Log warning + no-op |
| `equalsForTextMeasurement` | 4 | composite | throw UnsupportedOperationException |
| `setFlags` | 4 | composite | Log warning + no-op |
| `setXfermode` | 4 | composite | Log warning + no-op |
| `Paint` | 3 | composite | throw UnsupportedOperationException |
| `Paint` | 3 | composite | throw UnsupportedOperationException |
| `Paint` | 3 | composite | throw UnsupportedOperationException |
| `clearShadowLayer` | 3 | composite | throw UnsupportedOperationException |
| `hasGlyph` | 2 | composite | Return safe default (null/false/0/empty) |
| `isDither` | 2 | none | Return safe default (null/false/0/empty) |
| `ascent` | 1 | none | throw UnsupportedOperationException |
| `descent` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 43 methods that have score >= 5
2. Stub 72 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Paint`:


## Quality Gates

Before marking `android.graphics.Paint` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 115 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 43 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
