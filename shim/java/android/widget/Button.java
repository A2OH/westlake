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

        float ts = getTextSize() > 0 ? getTextSize() : 24;
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
        // Draw button background with 2px margin gap and 1px border
        int bgColor = getBackgroundColor() != 0 ? getBackgroundColor() : DEFAULT_BG_COLOR;
        int m = 2; // margin gap
        float radius = Math.min(DEFAULT_CORNER_RADIUS, Math.min(getWidth() - m * 2, getHeight() - m * 2) / 4f);
        if (radius < 2) radius = 2;
        android.graphics.Paint borderPaint = new android.graphics.Paint();
        borderPaint.setColor(0xFFBBBBBB);
        borderPaint.setStyle(android.graphics.Paint.Style.FILL);
        canvas.drawRoundRect(m, m, getWidth() - m, getHeight() - m,
                radius, radius, borderPaint);

        // Draw button background inset by 1px from border
        android.graphics.Paint bgPaint = new android.graphics.Paint();
        bgPaint.setColor(bgColor);
        bgPaint.setStyle(android.graphics.Paint.Style.FILL);
        canvas.drawRoundRect(m + 1, m + 1, getWidth() - m - 1, getHeight() - m - 1,
                radius, radius, bgPaint);

        // Draw text centered
        CharSequence text = getText();
        if (text != null && text.length() > 0) {
            android.graphics.Paint textPaint = new android.graphics.Paint();
            textPaint.setColor(getCurrentTextColor() != 0 ? getCurrentTextColor() : 0xFF000000);
            float ts = getTextSize() > 0 ? getTextSize() : 24;
            textPaint.setTextSize(ts);
            textPaint.setStyle(android.graphics.Paint.Style.FILL);
            android.graphics.Paint.FontMetrics fm = textPaint.getFontMetrics();
            float textWidth = textPaint.measureText(text.toString());
            // Center horizontally within button bounds
            float x = (getWidth() - textWidth) / 2f;
            // Center vertically using font metrics
            float textHeight = fm.descent - fm.ascent;
            float y = (getHeight() - textHeight) / 2f + (-fm.ascent);
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
