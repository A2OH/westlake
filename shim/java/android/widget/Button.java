package android.widget;

import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.Button → ARKUI_NODE_BUTTON
 *
 * ArkUI Button has a label attribute. Text styling from TextView base class
 * applies to the button label.
 */
public class Button extends TextView {
    static final int NODE_TYPE_BUTTON = 13;
    static final int ATTR_BUTTON_LABEL = 13000;

    private static final int DEFAULT_BG_COLOR = 0xFFDDDDDD;
    private static final float DEFAULT_CORNER_RADIUS = 8f;
    private static final int DEFAULT_PADDING = 16;

    public Button() {
        super(NODE_TYPE_BUTTON);
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        // Draw button background (rounded rect)
        int bgColor = getBackgroundColor() != 0 ? getBackgroundColor() : DEFAULT_BG_COLOR;
        android.graphics.Paint bgPaint = new android.graphics.Paint();
        bgPaint.setColor(bgColor);
        bgPaint.setStyle(android.graphics.Paint.Style.FILL);
        canvas.drawRoundRect(0, 0, getWidth(), getHeight(),
                DEFAULT_CORNER_RADIUS, DEFAULT_CORNER_RADIUS, bgPaint);

        // Draw text centered
        CharSequence text = getText();
        if (text != null && text.length() > 0) {
            android.graphics.Paint textPaint = new android.graphics.Paint();
            textPaint.setColor(getCurrentTextColor() != 0 ? getCurrentTextColor() : 0xFF000000);
            float ts = getTextSize() > 0 ? getTextSize() : 14;
            textPaint.setTextSize(ts);
            textPaint.setStyle(android.graphics.Paint.Style.FILL);
            float x = DEFAULT_PADDING;
            float y = (getHeight() + ts) / 2;
            canvas.drawText(text.toString(), x, y, textPaint);
        }
    }


    public void setText(CharSequence text) {
        super.setText(text);
        // ArkUI Button uses BUTTON_LABEL for its text
        if (nativeHandle != 0 && text != null) {
            OHBridge.nodeSetAttrString(nativeHandle, ATTR_BUTTON_LABEL, text.toString());
        }
    }
}
