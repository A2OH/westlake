package android.graphics.text;

/**
 * Android-compatible LineBreaker shim (API 29+).
 * Computes line-break positions for styled text; stubbed for A2OH migration.
 */
public class LineBreaker {

    // ── Break strategy constants ──────────────────────────────────────
    public static final int BREAK_STRATEGY_SIMPLE       = 0;
    public static final int BREAK_STRATEGY_HIGH_QUALITY = 1;
    public static final int BREAK_STRATEGY_BALANCED     = 2;

    // ── Hyphenation frequency constants ───────────────────────────────
    public static final int HYPHENATION_FREQUENCY_NONE   = 0;
    public static final int HYPHENATION_FREQUENCY_NORMAL = 1;
    public static final int HYPHENATION_FREQUENCY_FULL   = 2;

    // ── Justification mode constants ──────────────────────────────────
    public static final int JUSTIFICATION_MODE_NONE       = 0;
    public static final int JUSTIFICATION_MODE_INTER_WORD = 1;

    private int mBreakStrategy;
    private int mHyphenationFrequency;
    private int mJustificationMode;

    private LineBreaker(int breakStrategy, int hyphenationFrequency,
                        int justificationMode) {
        mBreakStrategy       = breakStrategy;
        mHyphenationFrequency = hyphenationFrequency;
        mJustificationMode   = justificationMode;
    }

    /**
     * Compute line breaks for the given measured text using character widths
     * and the paragraph width constraint.
     */
    public Result computeLineBreaks(Object measuredText,
                                    Object constraints,
                                    int lineNumber) {
        float maxWidth = 0;
        if (constraints instanceof ParagraphConstraints) {
            maxWidth = ((ParagraphConstraints) constraints).getWidth();
        }
        if (maxWidth <= 0) maxWidth = Float.MAX_VALUE;

        float[] charWidths = null;
        char[] chars = null;
        int textLen = 0;
        if (measuredText instanceof MeasuredText) {
            MeasuredText mt = (MeasuredText) measuredText;
            charWidths = mt.getCharWidths();
            chars = mt.getChars();
            textLen = (chars != null) ? chars.length : 0;
        }
        if (textLen == 0) {
            // Empty text: one line at offset 0
            return new Result(new int[]{0}, new float[]{0}, 1);
        }

        // Simple greedy line-breaking algorithm
        java.util.List<Integer> breakOffsets = new java.util.ArrayList<Integer>();
        java.util.List<Float> lineWidthsList = new java.util.ArrayList<Float>();

        float lineWidth = 0;
        int lineStart = 0;
        int lastBreakablePos = -1;
        float widthAtLastBreak = 0;

        for (int i = 0; i < textLen; i++) {
            char c = (chars != null) ? chars[i] : ' ';
            float cw = (charWidths != null && i < charWidths.length) ? charWidths[i] : 0;

            // Hard line break
            if (c == '\n') {
                breakOffsets.add(Integer.valueOf(i + 1));
                lineWidthsList.add(Float.valueOf(lineWidth));
                lineWidth = 0;
                lineStart = i + 1;
                lastBreakablePos = -1;
                continue;
            }

            // Track breakable positions (after spaces)
            if (c == ' ' || c == '\t') {
                lastBreakablePos = i + 1;
                widthAtLastBreak = lineWidth + cw;
            }

            lineWidth += cw;

            // Check if we exceed max width
            if (lineWidth > maxWidth && i > lineStart) {
                if (lastBreakablePos > lineStart) {
                    // Break at last space
                    breakOffsets.add(Integer.valueOf(lastBreakablePos));
                    lineWidthsList.add(Float.valueOf(widthAtLastBreak));
                    // Recompute width from break point
                    lineWidth = 0;
                    for (int j = lastBreakablePos; j <= i; j++) {
                        lineWidth += (charWidths != null && j < charWidths.length) ? charWidths[j] : 0;
                    }
                    lineStart = lastBreakablePos;
                    lastBreakablePos = -1;
                } else {
                    // No break point found; break at current char
                    breakOffsets.add(Integer.valueOf(i));
                    lineWidthsList.add(Float.valueOf(lineWidth - cw));
                    lineWidth = cw;
                    lineStart = i;
                    lastBreakablePos = -1;
                }
            }
        }

        // Final line (skip if last break already covers the end)
        if (breakOffsets.isEmpty() || breakOffsets.get(breakOffsets.size() - 1).intValue() < textLen) {
            breakOffsets.add(Integer.valueOf(textLen));
            lineWidthsList.add(Float.valueOf(lineWidth));
        }

        int count = breakOffsets.size();
        int[] offsets = new int[count];
        float[] widths = new float[count];
        for (int i = 0; i < count; i++) {
            offsets[i] = breakOffsets.get(i).intValue();
            widths[i] = lineWidthsList.get(i).floatValue();
        }
        return new Result(offsets, widths, count);
    }

    // ── Result ────────────────────────────────────────────────────────

    /**
     * Holds the result of a line-break computation.
     */
    public static class Result {

        private final int[] mBreakOffsets;
        private final float[] mLineWidths;
        private final int mLineCount;

        Result() {
            mBreakOffsets = new int[0];
            mLineWidths = new float[0];
            mLineCount = 0;
        }

        Result(int[] breakOffsets, float[] lineWidths, int lineCount) {
            mBreakOffsets = breakOffsets;
            mLineWidths = lineWidths;
            mLineCount = lineCount;
        }

        public int getLineCount() { return mLineCount; }

        public int getLineBreakOffset(int lineIndex) {
            return (lineIndex >= 0 && lineIndex < mBreakOffsets.length) ? mBreakOffsets[lineIndex] : 0;
        }

        public float getLineWidth(int lineIndex) {
            return (lineIndex >= 0 && lineIndex < mLineWidths.length) ? mLineWidths[lineIndex] : 0.0f;
        }

        public float getLineAscent(int lineIndex) { return 0.0f; }

        public float getLineDescent(int lineIndex) { return 0.0f; }

        public boolean hasLineTab(int lineIndex) { return false; }

        public int getLineHyphenEdit(int lineIndex) { return 0; }
        public int getStartLineHyphenEdit(int lineIndex) { return 0; }
        public int getEndLineHyphenEdit(int lineIndex) { return 0; }
    }

    // ── ParagraphConstraints ──────────────────────────────────────────

    /**
     * Describes width constraints for a paragraph.
     */
    public static class ParagraphConstraints {

        private float mWidth;
        private float mFirstWidth;
        private int   mFirstWidthLineCount;
        private float[] mTabStops;

        public ParagraphConstraints() {}

        public float getWidth() { return mWidth; }

        public float getFirstWidth() { return mFirstWidth; }

        public int getFirstWidthLineCount() { return mFirstWidthLineCount; }

        public float[] getTabStops() { return mTabStops; }

        public void setWidth(float width) { mWidth = width; }

        public void setFirstWidth(float firstWidth) { mFirstWidth = firstWidth; }

        public void setFirstWidthLineCount(int firstWidthLineCount) {
            mFirstWidthLineCount = firstWidthLineCount;
        }

        public void setTabStops(float[] tabStops) { mTabStops = tabStops; }
        public void setTabStops(float[] tabStops, float defaultTab) { mTabStops = tabStops; }

        public void setIndent(float firstWidth, int firstWidthLineCount) {
            mFirstWidth = firstWidth;
            mFirstWidthLineCount = firstWidthLineCount;
        }
    }

    // ── Builder ───────────────────────────────────────────────────────

    /**
     * Builder for {@link LineBreaker}.
     */
    public static class Builder {

        private int mBreakStrategy       = BREAK_STRATEGY_SIMPLE;
        private int mHyphenationFrequency = HYPHENATION_FREQUENCY_NONE;
        private int mJustificationMode   = JUSTIFICATION_MODE_NONE;

        public Builder() {}

        public Builder setBreakStrategy(int breakStrategy) {
            mBreakStrategy = breakStrategy;
            return this;
        }

        public Builder setHyphenationFrequency(int hyphenationFrequency) {
            mHyphenationFrequency = hyphenationFrequency;
            return this;
        }

        public Builder setJustificationMode(int justificationMode) {
            mJustificationMode = justificationMode;
            return this;
        }

        public Builder setIndents(int[] indents) {
            return this;
        }

        public LineBreaker build() {
            return new LineBreaker(mBreakStrategy, mHyphenationFrequency,
                                  mJustificationMode);
        }
    }
}
