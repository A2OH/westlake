package com.google.android.material.appbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Minimal CollapsingToolbarLayout shim — behaves as a FrameLayout.
 */
public class CollapsingToolbarLayout extends FrameLayout {
    public CollapsingToolbarLayout(Context context) { super(context); }
    public CollapsingToolbarLayout(Context context, AttributeSet attrs) { super(context, attrs); }

    public void setTitle(CharSequence title) {}
    public CharSequence getTitle() { return ""; }
    public void setExpandedTitleColor(int color) {}
    public void setCollapsedTitleTextColor(int color) {}
    public void setContentScrimColor(int color) {}
}
