package android.widget;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

/**
 * Stub for android.widget.FastScroller.
 */
public class FastScroller {
    public static final int STATE_NONE = 0;
    public static final int STATE_VISIBLE = 1;
    public static final int STATE_DRAGGING = 2;
    public static final int STATE_EXIT = 3;

    public FastScroller(AbsListView listView, int styleResId) {}

    public void remove() {}
    public void setEnabled(boolean enabled) {}
    public boolean isEnabled() { return false; }
    public void setAlwaysShow(boolean alwaysShow) {}
    public boolean isAlwaysShowEnabled() { return false; }
    public void setStyle(int styleResId) {}
    public void setScrollbarPosition(int position) {}
    public int getWidth() { return 0; }
    public void onSizeChanged(int w, int h, int oldw, int oldh) {}
    public void onSectionsChanged() {}
    public void onItemCountChanged(int childCount, int itemCount) {}
    public boolean onInterceptTouchEvent(MotionEvent ev) { return false; }
    public boolean onInterceptHoverEvent(MotionEvent ev) { return false; }
    public boolean onTouchEvent(MotionEvent me) { return false; }
    public void updateLayout() {}

    public int getState() { return STATE_NONE; }
    public void stop() {}
    public void setScrollBarStyle(int style) {}
    public void onScroll(int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
    public android.view.PointerIcon onResolvePointerIcon(android.view.MotionEvent event, int pointerIndex) { return null; }
}
