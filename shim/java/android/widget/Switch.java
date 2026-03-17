package android.widget;
import android.view.View;
import com.ohos.shim.bridge.OHBridge;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shim: android.widget.Switch → ARKUI_NODE_TOGGLE (switch type)
 */
public class Switch extends TextView {
    static final int NODE_TYPE_TOGGLE = 5;
    static final int ATTR_TOGGLE_STATE = 5001;
    static final int EVENT_TOGGLE_ON_CHANGE = 5000;

    // Drawing constants
    private static final int TRACK_COLOR_OFF = 0xFFBDBDBD;    // gray
    private static final int TRACK_COLOR_ON = 0xFFA5D6A7;     // light green
    private static final int THUMB_COLOR_OFF = 0xFFFAFAFA;    // white-ish
    private static final int THUMB_COLOR_ON = 0xFF4CAF50;     // green
    private static final int TEXT_COLOR = 0xFF212121;

    private boolean checked = false;
    private OnCheckedChangeListener onCheckedChangeListener;
    private int toggleEventId;
    private static final AtomicInteger sNextEventId = new AtomicInteger(30000);

    private CharSequence textOn = "ON";
    private CharSequence textOff = "OFF";

    public Switch() {
        super(NODE_TYPE_TOGGLE);
        registerToggleEvent();
    }

    private void registerToggleEvent() {
        if (nativeHandle != 0) {
            toggleEventId = sNextEventId.getAndIncrement();
            OHBridge.nodeRegisterEvent(nativeHandle, EVENT_TOGGLE_ON_CHANGE, toggleEventId);
        }
    }

    public boolean isChecked() { return checked; }

    public void setChecked(boolean checked) {
        this.checked = checked;
        if (nativeHandle != 0) {
            OHBridge.nodeSetAttrInt(nativeHandle, ATTR_TOGGLE_STATE, checked ? 1 : 0);
        }
    }

    public void setTextOn(CharSequence text) { textOn = text; }
    public CharSequence getTextOn() { return textOn; }

    public void setTextOff(CharSequence text) { textOff = text; }
    public CharSequence getTextOff() { return textOff; }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setStyle(android.graphics.Paint.Style.FILL);

        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        // Track dimensions
        float trackWidth = Math.min(48f, w * 0.4f);
        float trackHeight = 24f;
        float trackLeft = w - getPaddingRight() - trackWidth;
        float trackTop = (h - trackHeight) / 2f;
        float trackRadius = trackHeight / 2f;

        // Step 1: Draw track (rounded rect background)
        paint.setColor(checked ? TRACK_COLOR_ON : TRACK_COLOR_OFF);
        canvas.drawRoundRect(trackLeft, trackTop,
            trackLeft + trackWidth, trackTop + trackHeight,
            trackRadius, trackRadius, paint);

        // Step 2: Draw thumb (circle, positioned left/right based on checked state)
        float thumbRadius = trackHeight / 2f - 2f;
        float thumbCx;
        if (checked) {
            thumbCx = trackLeft + trackWidth - trackRadius;
        } else {
            thumbCx = trackLeft + trackRadius;
        }
        float thumbCy = trackTop + trackHeight / 2f;

        paint.setColor(checked ? THUMB_COLOR_ON : THUMB_COLOR_OFF);
        canvas.drawCircle(thumbCx, thumbCy, thumbRadius, paint);

        // Step 3: Draw on/off text label to the left of the track
        CharSequence label = checked ? textOn : textOff;
        if (label != null && label.length() > 0) {
            float textSize = getTextSize() > 0 ? getTextSize() : 14f;
            paint.setColor(getCurrentTextColor() != 0 ? getCurrentTextColor() : TEXT_COLOR);
            paint.setTextSize(textSize);
            paint.setStyle(android.graphics.Paint.Style.FILL);

            android.graphics.Paint.FontMetrics fm = paint.getFontMetrics();
            float textX = getPaddingLeft();
            float textY = (h / 2f) + (-fm.ascent - fm.descent) / 2f;
            canvas.drawText(label.toString(), textX, textY, paint);
        }
    }

    public void onNativeEvent(int eventId, int eventKind, String stringData) {
        if (eventKind == EVENT_TOGGLE_ON_CHANGE) {
            checked = !checked;
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(this, checked);
            }
        }
        super.onNativeEvent(eventId, eventKind, stringData);
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(Switch buttonView, boolean isChecked);
    }
}
