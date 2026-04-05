package androidx.drawerlayout.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Shim DrawerLayout — only shows the first child (main content).
 * Drawer panels (children 2+) are hidden by default.
 */
public class DrawerLayout extends android.widget.FrameLayout {

    public DrawerLayout(Context context) {
        super(context);
    }

    public DrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        // Hide drawer panels (children after the first one)
        if (getChildCount() > 1) {
            child.setVisibility(View.GONE);
        }
    }

    // DrawerLayout-specific methods (no-ops for shim)
    public void setDrawerLockMode(int lockMode) {}
    public void setDrawerLockMode(int lockMode, int edgeGravity) {}
    public void setDrawerLockMode(int lockMode, View drawerView) {}
    public int getDrawerLockMode(int edgeGravity) { return 0; }
    public void openDrawer(int gravity) {}
    public void openDrawer(View drawerView) {}
    public void closeDrawer(int gravity) {}
    public void closeDrawer(View drawerView) {}
    public void closeDrawers() {}
    public boolean isDrawerOpen(int drawerGravity) { return false; }
    public boolean isDrawerOpen(View drawer) { return false; }
    public boolean isDrawerVisible(int drawerGravity) { return false; }
    public void setScrimColor(int color) {}
    public void setDrawerElevation(float elevation) {}
    public void addDrawerListener(Object listener) {}
    public void removeDrawerListener(Object listener) {}
    public void setStatusBarBackgroundColor(int color) {}

    // Constants
    public static final int LOCK_MODE_UNLOCKED = 0;
    public static final int LOCK_MODE_LOCKED_CLOSED = 1;
    public static final int LOCK_MODE_LOCKED_OPEN = 2;
    public static final int STATE_IDLE = 0;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_SETTLING = 2;
}
