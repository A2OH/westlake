package android.graphics;

/**
 * Shim: android.graphics.Paint
 * OH mapping: drawing.OH_Drawing_Pen / OH_Drawing_Brush
 *
 * Pure Java stub — stores pa(int attributes; no actual rendering.
 */
public class Paint {

    // ── Flags ────────────────────────────────────────────────────────────────

    public static final int ANTI_ALIAS_FLAG    = 1;
    public static final int FILTER_BITMAP_FLAG = 2;

    // ── Style ────────────────────────────────────────────────────────────────

    public enum Style {
        FILL,
        STROKE,
        FILL_AND_STROKE
    }

    // ── Cap ──────────────────────────────────────────────────────────────────

    public enum Cap {
        BUTT,   // 0 — OH_Drawing: LINE_FLAT_CAP
        ROUND,  // 1 — OH_Drawing: LINE_ROUND_CAP
        SQUARE  // 2 — OH_Drawing: LINE_SQUARE_CAP
    }

    // ── Join ─────────────────────────────────────────────────────────────────

    public enum Join {
        MITER,  // 0 — OH_Drawing: LINE_MITER_JOIN
        ROUND,  // 1 — OH_Drawing: LINE_ROUND_JOIN
        BEVEL   // 2 — OH_Drawing: LINE_BEVEL_JOIN
    }

    // ── Align ────────────────────────────────────────────────────────────────

    public enum Align {
        LEFT,
        CENTER,
        RIGHT
    }

    // ── State ────────────────────────────────────────────────────────────────

    private int   flags;
    private int   color     = 0xFF000000; // opaque black
    private Style style     = Style.FILL;
    private float strokeWidth = 0f;
    private float textSize    = 12f;
    private Cap   strokeCap   = Cap.BUTT;
    private Join  strokeJoin  = Join.MITER;
    private Align textAlign   = Align.LEFT;
    private float strokeMiter = 4f;

    // ── Constructors ─────────────────────────────────────────────────────────

    public Paint() {
        this.flags = 0;
    }

    public Paint(int flags) {
        this.flags = flags;
    }

    public Paint(Paint paint) {
        if (paint != null) {
            this.flags       = paint.flags;
            this.color       = paint.color;
            this.style       = paint.style;
            this.strokeWidth = paint.strokeWidth;
            this.textSize    = paint.textSize;
            this.strokeCap   = paint.strokeCap;
            this.strokeJoin  = paint.strokeJoin;
            this.textAlign   = paint.textAlign;
            this.strokeMiter = paint.strokeMiter;
        }
    }

    // ── Color ────────────────────────────────────────────────────────────────

    public void setColor(int color) { this.color = color; }
    public int  getColor()          { return color; }

    public void setAlpha(int alpha) {
        color = (color & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }

    public int getAlpha() {
        return (color >>> 24);
    }

    // ── Style ────────────────────────────────────────────────────────────────

    public void  setStyle(Style style) { this.style = (style != null) ? style : Style.FILL; }
    public Style getStyle()            { return style; }

    // ── Stroke ───────────────────────────────────────────────────────────────

    public void  setStrokeWidth(float width) { this.strokeWidth = width; }
    public float getStrokeWidth()            { return strokeWidth; }

    public void setStrokeCap(Cap cap) { this.strokeCap = (cap != null) ? cap : Cap.BUTT; }
    public Cap  getStrokeCap()        { return strokeCap; }

    public void setStrokeJoin(Join join) { this.strokeJoin = (join != null) ? join : Join.MITER; }
    public Join getStrokeJoin()          { return strokeJoin; }

    public void  setStrokeMiter(float miter) { this.strokeMiter = miter; }
    public float getStrokeMiter()            { return strokeMiter; }

    // ── Text ─────────────────────────────────────────────────────────────────

    public void  setTextSize(float size) { this.textSize = size; }
    public float getTextSize()           { return textSize; }

    public void  setTextAlign(Align align) { this.textAlign = (align != null) ? align : Align.LEFT; }
    public Align getTextAlign()            { return textAlign; }

    /**
     * Rough width estimate: each character is ~0.6 em wide.
     */
    public float measureText(String text) {
        if (text == null) return 0f;
        return text.length() * textSize * 0.6f;
    }

    public float measureText(char[] text, int index, int count) {
        if (text == null || count <= 0) return 0f;
        return count * textSize * 0.6f;
    }

    public float measureText(String text, int start, int end) {
        if (text == null) return 0f;
        return (end - start) * textSize * 0.6f;
    }

    // ── FontMetrics ──────────────────────────────────────────────────────────

    public static class FontMetrics {
        public float top;
        public float ascent;
        public float descent;
        public float bottom;
        public float leading;
    }

    public static class FontMetricsInt {
        public int top;
        public int ascent;
        public int descent;
        public int bottom;
        public int leading;
    }

    public FontMetrics getFontMetrics() {
        FontMetrics fm = new FontMetrics();
        fm.top     = -textSize * 1.08f;
        fm.ascent  = -textSize * 0.93f;
        fm.descent =  textSize * 0.24f;
        fm.bottom  =  textSize * 0.28f;
        fm.leading = 0;
        return fm;
    }

    public float getFontMetrics(FontMetrics metrics) {
        if (metrics == null) metrics = new FontMetrics();
        metrics.top     = -textSize * 1.08f;
        metrics.ascent  = -textSize * 0.93f;
        metrics.descent =  textSize * 0.24f;
        metrics.bottom  =  textSize * 0.28f;
        metrics.leading = 0;
        return metrics.descent - metrics.ascent;
    }

    public FontMetricsInt getFontMetricsInt() {
        FontMetricsInt fm = new FontMetricsInt();
        fm.top     = Math.round(-textSize * 1.08f);
        fm.ascent  = Math.round(-textSize * 0.93f);
        fm.descent = Math.round(textSize * 0.24f);
        fm.bottom  = Math.round(textSize * 0.28f);
        fm.leading = 0;
        return fm;
    }

    public float getFontSpacing() {
        return textSize * 0.93f + textSize * 0.24f;
    }

    // ── Anti-alias ───────────────────────────────────────────────────────────

    public void    setAntiAlias(boolean aa) {
        if (aa) flags |= ANTI_ALIAS_FLAG;
        else    flags &= ~ANTI_ALIAS_FLAG;
    }
    public boolean isAntiAlias() { return (flags & ANTI_ALIAS_FLAG) != 0; }

    // ── Flags ────────────────────────────────────────────────────────────────

    public void setFlags(int flags) { this.flags = flags; }
    public int  getFlags()          { return flags; }

    // ── Shadow layer ──────────────────────────────────────────────────────

    public void setShadowLayer(float radius, float dx, float dy, int shadowColor) { /* no-op */ }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Paint(color=0x" + Integer.toHexString(color)
             + ", style=" + style
             + ", strokeWidth=" + strokeWidth
             + ", textSize=" + textSize + ")";
    }
}
