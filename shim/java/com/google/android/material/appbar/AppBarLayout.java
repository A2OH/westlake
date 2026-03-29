package com.google.android.material.appbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Minimal AppBarLayout shim — behaves as a vertical LinearLayout.
 */
public class AppBarLayout extends LinearLayout {
    public AppBarLayout(Context context) { super(context); setOrientation(VERTICAL); }
    public AppBarLayout(Context context, AttributeSet attrs) { super(context, attrs); setOrientation(VERTICAL); }

    public void setExpanded(boolean expanded) {}
    public void setExpanded(boolean expanded, boolean animate) {}
    public int getTotalScrollRange() { return 0; }

    public interface OnOffsetChangedListener {
        void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset);
    }
    public void addOnOffsetChangedListener(OnOffsetChangedListener listener) {}
    public void removeOnOffsetChangedListener(OnOffsetChangedListener listener) {}

    public static class LayoutParams extends LinearLayout.LayoutParams {
        public static final int SCROLL_FLAG_SCROLL = 1;
        public static final int SCROLL_FLAG_ENTER_ALWAYS = 4;
        public static final int SCROLL_FLAG_EXIT_UNTIL_COLLAPSED = 2;
        public int scrollFlags = 0;

        public LayoutParams(int width, int height) { super(width, height); }
        public void setScrollFlags(int flags) { scrollFlags = flags; }
        public int getScrollFlags() { return scrollFlags; }
    }

    public static class ScrollingViewBehavior {
        public ScrollingViewBehavior() {}
        public ScrollingViewBehavior(Context context, AttributeSet attrs) {}
    }
}
