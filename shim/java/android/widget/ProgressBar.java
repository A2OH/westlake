package android.widget;
import android.view.View;
import android.view.View;

import android.view.View;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.ProgressBar → ARKUI_NODE_PROGRESS
 */
public class ProgressBar extends View {
    static final int NODE_TYPE_PROGRESS = 14;
    static final int ATTR_PROGRESS_VALUE = 14000;
    static final int ATTR_PROGRESS_TOTAL = 14001;

    private int max = 100;
    private int progress = 0;
    private boolean indeterminate = false;

    public ProgressBar() {
        super(NODE_TYPE_PROGRESS);
    }

    public int getMax() { return max; }

    public void setMax(int max) {
        this.max = max;
        if (nativeHandle != 0) {
// FIXME OHBridge: // FIXME OHBridge: // FIXME OHBridge:             OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_PROGRESS_TOTAL, (float) max, 0, 0, 0);
        }
    }

    public int getProgress() { return progress; }

    public void setProgress(int progress) {
        this.progress = progress;
        if (nativeHandle != 0) {
// FIXME OHBridge: // FIXME OHBridge: // FIXME OHBridge:             OHBridge.nodeSetAttrFloat(nativeHandle, ATTR_PROGRESS_VALUE, (float) progress, 0, 0, 0);
        }
    }

    public void setIndeterminate(boolean indeterminate) {
        this.indeterminate = indeterminate;
        // ArkUI LoadingProgress handles indeterminate — different node type
    }

    public boolean isIndeterminate() { return indeterminate; }

    public void incrementProgressBy(int diff) {
        setProgress(progress + diff);
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        // Draw track background
        android.graphics.Paint trackPaint = new android.graphics.Paint();
        trackPaint.setColor(0xFFE0E0E0);
        trackPaint.setStyle(android.graphics.Paint.Style.FILL);
        float barHeight = Math.max(4, h / 3f);
        float barTop = (h - barHeight) / 2;
        canvas.drawRect(0, barTop, w, barTop + barHeight, trackPaint);

        // Draw progress fill
        if (max > 0 && progress > 0) {
            android.graphics.Paint fillPaint = new android.graphics.Paint();
            fillPaint.setColor(0xFF4CAF50); // Material green
            fillPaint.setStyle(android.graphics.Paint.Style.FILL);
            float fillWidth = (float) w * Math.min(progress, max) / max;
            canvas.drawRect(0, barTop, fillWidth, barTop + barHeight, fillPaint);
        }
    }
}
