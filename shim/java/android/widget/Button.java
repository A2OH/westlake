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

    public Button(android.content.Context context) {
        this();
    }

    public Button(android.content.Context context, android.util.AttributeSet attrs) {
        this();
    }

    public Button(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        this();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Account for default button padding if no explicit padding set
        int padL = getPaddingLeft() > 0 ? getPaddingLeft() : DEFAULT_PADDING;
        int padR = getPaddingRight() > 0 ? getPaddingRight() : DEFAULT_PADDING;
        int padT = getPaddingTop() > 0 ? getPaddingTop() : DEFAULT_PADDING;
        int padB = getPaddingBottom() > 0 ? getPaddingBottom() : DEFAULT_PADDING;

        float ts = getTextSize() > 0 ? getTextSize() : 14;
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setTextSize(ts);
        android.graphics.Paint.FontMetrics fm = paint.getFontMetrics();
        int textHeight = (int) Math.ceil(-fm.ascent + fm.descent) + padT + padB;
        int textWidth = 0;
        CharSequence text = getText();
        if (text != null && text.length() > 0) {
            textWidth = (int) Math.ceil(paint.measureText(text.toString()));
        }
        textWidth += padL + padR;

        int wMode = android.view.View.MeasureSpec.getMode(widthMeasureSpec);
        int wSize = android.view.View.MeasureSpec.getSize(widthMeasureSpec);
        int hMode = android.view.View.MeasureSpec.getMode(heightMeasureSpec);
        int hSize = android.view.View.MeasureSpec.getSize(heightMeasureSpec);

        int measuredW;
        if (wMode == android.view.View.MeasureSpec.EXACTLY) {
            measuredW = wSize;
        } else if (wMode == android.view.View.MeasureSpec.AT_MOST) {
            measuredW = Math.min(textWidth, wSize);
        } else {
            measuredW = textWidth;
        }

        int measuredH;
        if (hMode == android.view.View.MeasureSpec.EXACTLY) {
            measuredH = hSize;
        } else if (hMode == android.view.View.MeasureSpec.AT_MOST) {
            measuredH = Math.min(textHeight, hSize);
        } else {
            measuredH = textHeight;
        }

        setMeasuredDimension(measuredW, measuredH);
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

        // Draw text centered, respecting padding
        CharSequence text = getText();
        if (text != null && text.length() > 0) {
            android.graphics.Paint textPaint = new android.graphics.Paint();
            textPaint.setColor(getCurrentTextColor() != 0 ? getCurrentTextColor() : 0xFF000000);
            float ts = getTextSize() > 0 ? getTextSize() : 14;
            textPaint.setTextSize(ts);
            textPaint.setStyle(android.graphics.Paint.Style.FILL);
            android.graphics.Paint.FontMetrics fm = textPaint.getFontMetrics();
            int padL = getPaddingLeft() > 0 ? getPaddingLeft() : DEFAULT_PADDING;
            int padR = getPaddingRight() > 0 ? getPaddingRight() : DEFAULT_PADDING;
            float textWidth = textPaint.measureText(text.toString());
            // Center horizontally within padded area
            float availWidth = getWidth() - padL - padR;
            float x = padL + Math.max(0, (availWidth - textWidth) / 2);
            // Center vertically using font metrics
            float y = (getHeight() - fm.ascent - fm.descent) / 2 - fm.ascent;
            // Clamp to padded top
            y = Math.max(getPaddingTop() - fm.ascent, y);
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
