package androidx.cardview.widget;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Minimal CardView shim — renders as a FrameLayout with rounded corners and slight background.
 */
public class CardView extends FrameLayout {
    private float mCardElevation = 4f;
    private float mCardRadius = 8f;
    private int mCardBackgroundColor = 0xFF424242;

    public CardView(Context context) { super(context); applyStyle(); }
    public CardView(Context context, AttributeSet attrs) { super(context, attrs); applyStyle(); }

    private void applyStyle() {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(mCardBackgroundColor);
        bg.setCornerRadius(mCardRadius);
        setBackground(bg);
    }

    public void setCardElevation(float elevation) { mCardElevation = elevation; }
    public float getCardElevation() { return mCardElevation; }
    public void setRadius(float radius) { mCardRadius = radius; applyStyle(); }
    public float getRadius() { return mCardRadius; }
    public void setCardBackgroundColor(int color) { mCardBackgroundColor = color; applyStyle(); }
    public int getCardBackgroundColor() { return mCardBackgroundColor; }
    public void setMaxCardElevation(float elevation) {}
    public void setPreventCornerOverlap(boolean prevent) {}
    public void setUseCompatPadding(boolean use) {}
    public void setContentPadding(int l, int t, int r, int b) { setPadding(l, t, r, b); }
}
