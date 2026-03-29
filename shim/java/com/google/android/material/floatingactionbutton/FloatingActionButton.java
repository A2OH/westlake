package com.google.android.material.floatingactionbutton;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Minimal FloatingActionButton shim — renders as a circular colored ImageView.
 */
public class FloatingActionButton extends ImageView {
    private int mFabSize = 0; // 0=normal, 1=mini
    private int mBackgroundTintColor = 0xFF2196F3; // Material blue

    public FloatingActionButton(Context context) { super(context); applyStyle(); }
    public FloatingActionButton(Context context, AttributeSet attrs) { super(context, attrs); applyStyle(); }

    private void applyStyle() {
        int size = mFabSize == 1 ? 80 : 112;
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(mBackgroundTintColor);
        setBackground(bg);
        setMinimumWidth(size);
        setMinimumHeight(size);
        setPadding(24, 24, 24, 24);
        setClickable(true);
    }

    public void setSize(int size) { mFabSize = size; applyStyle(); }
    public int getSize() { return mFabSize; }
    public void setRippleColor(int color) {}
    public void show() { setVisibility(VISIBLE); }
    public void hide() { setVisibility(GONE); }
    public void setCompatElevation(float elevation) {}

    public static final int SIZE_NORMAL = 0;
    public static final int SIZE_MINI = 1;
    public static final int SIZE_AUTO = -1;
}
