package android.text;

import android.graphics.Paint;

/**
 * Stub: MeasuredParagraph — pre-measured paragraph metrics.
 * Used by StaticLayout for line breaking computations.
 */
public class MeasuredParagraph {

    private char[] mChars;
    private float[] mWidths;
    private int mTextLength;
    private int mParaStart;  // absolute start position in source text
    private int mParaEnd;    // absolute end position in source text
    private Paint mPaint;

    private MeasuredParagraph() {}

    public static MeasuredParagraph buildForBidi(CharSequence text, int start, int end,
            TextDirectionHeuristic textDir, MeasuredParagraph recycle) {
        MeasuredParagraph mp = (recycle != null) ? recycle : new MeasuredParagraph();
        mp.mTextLength = end - start;
        mp.mParaStart = start;
        mp.mParaEnd = end;
        mp.mChars = new char[mp.mTextLength];
        TextUtils.getChars(text, start, end, mp.mChars, 0);
        mp.mWidths = new float[mp.mTextLength];
        return mp;
    }

    public static MeasuredParagraph buildForMeasurement(TextPaint paint, CharSequence text,
            int start, int end, TextDirectionHeuristic textDir, MeasuredParagraph recycle) {
        MeasuredParagraph mp = buildForBidi(text, start, end, textDir, recycle);
        mp.mPaint = paint;
        if (paint != null && mp.mTextLength > 0) {
            String s = text.toString().substring(start, end);
            float w = paint.measureText(s);
            if (mp.mWidths != null && mp.mWidths.length > 0) {
                float each = w / mp.mTextLength;
                for (int i = 0; i < mp.mTextLength; i++) {
                    mp.mWidths[i] = each;
                }
            }
        }
        return mp;
    }

    public static MeasuredParagraph buildForStaticLayout(TextPaint paint, CharSequence text,
            int start, int end, TextDirectionHeuristic textDir, boolean computeHyphenation,
            boolean computeLayout, MeasuredParagraph recycle) {
        return buildForMeasurement(paint, text, start, end, textDir, recycle);
    }

    public int getTextLength() { return mTextLength; }
    public char[] getChars() { return mChars; }
    public float[] getWidths() { return mWidths; }
    public int getParagraphDir() { return Layout.DIR_LEFT_TO_RIGHT; }

    public float getWidth(int i) {
        return (mWidths != null && i >= 0 && i < mWidths.length) ? mWidths[i] : 0;
    }

    public float getCharWidthAt(int i) {
        return getWidth(i);
    }

    public Layout.Directions getDirections(int start, int end) {
        return Layout.DIRS_ALL_LEFT_TO_RIGHT;
    }

    public android.util.IntArray getSpanEndCache() {
        // AOSP StaticLayout expects absolute positions in source text.
        // Return one span covering the whole paragraph.
        android.util.IntArray arr = new android.util.IntArray();
        arr.add(mParaEnd);
        return arr;
    }

    public android.util.IntArray getFontMetrics() {
        // 4 entries per span: top, bottom, ascent, descent.
        // Must match span count from getSpanEndCache().
        android.util.IntArray arr = new android.util.IntArray();
        int fontSize = (mPaint != null) ? (int) mPaint.getTextSize() : 14;
        int ascent = -fontSize;
        int descent = fontSize / 4;
        if (descent < 1) descent = 4;
        arr.add(ascent);       // top
        arr.add(descent);      // bottom
        arr.add(ascent);       // ascent
        arr.add(descent);      // descent
        return arr;
    }

    public android.graphics.text.MeasuredText getMeasuredText() {
        android.graphics.text.MeasuredText.Builder b =
            new android.graphics.text.MeasuredText.Builder(
                new String(mChars != null ? mChars : new char[0]));
        android.graphics.text.MeasuredText mt = b.build();
        if (mWidths != null) {
            mt.setCharWidths(mWidths);
        }
        return mt;
    }

    public void recycle() {
        mChars = null;
        mWidths = null;
        mTextLength = 0;
    }

    public long getNativePtr() { return 0; }

    /**
     * Get a span range within measured paragraph.
     */
    public android.text.style.MetricAffectingSpan[] getSpans(int start, int end) {
        return new android.text.style.MetricAffectingSpan[0];
    }
}
