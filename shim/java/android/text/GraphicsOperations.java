package android.text;

import android.graphics.BaseCanvas;
import android.graphics.Paint;

/** AOSP compilation stub - must be interface for CharWrapper to implement. */
public interface GraphicsOperations extends CharSequence {
    void drawText(BaseCanvas c, int start, int end, float x, float y, Paint p);
    void drawTextRun(BaseCanvas c, int start, int end,
            int contextStart, int contextEnd, float x, float y, boolean isRtl, Paint p);
    float measureText(int start, int end, Paint p);
    int getTextWidths(int start, int end, float[] widths, Paint p);
    float getTextRunAdvances(int start, int end, int contextStart, int contextEnd,
            boolean isRtl, float[] advances, int advancesIndex, Paint p);
    int getTextRunCursor(int contextStart, int contextEnd, boolean isRtl, int offset, int cursorOpt, Paint p);
}
