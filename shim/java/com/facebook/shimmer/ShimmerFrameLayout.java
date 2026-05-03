package com.facebook.shimmer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class ShimmerFrameLayout extends FrameLayout {
    private boolean mVisible;

    public ShimmerFrameLayout(Context context) {
        super(context);
    }

    public ShimmerFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShimmerFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void startShimmer() {
        mVisible = true;
    }

    public void stopShimmer() {
        mVisible = false;
    }

    public boolean isShimmerStarted() {
        return mVisible;
    }

    public boolean isShimmerVisible() {
        return mVisible;
    }

    public void showShimmer(boolean startShimmer) {
        mVisible = true;
        if (startShimmer) {
            startShimmer();
        }
    }

    public void hideShimmer() {
        mVisible = false;
    }

    public ShimmerFrameLayout setShimmer(Shimmer shimmer) {
        return this;
    }
}
