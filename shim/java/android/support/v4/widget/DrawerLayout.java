package android.support.v4.widget;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

public class DrawerLayout extends ViewGroup {

    public static final int LOCK_MODE_UNLOCKED      = 0;
    public static final int LOCK_MODE_LOCKED_CLOSED = 1;
    public static final int LOCK_MODE_LOCKED_OPEN   = 2;
    public static final int STATE_IDLE    = 0;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_SETTLING = 2;

    private boolean mDrawerOpen = false;
    private int mLockMode = LOCK_MODE_UNLOCKED;
    private DrawerListener mDrawerListener;

    public DrawerLayout(Context context) { super(context); }
    public DrawerLayout(Context context, AttributeSet attrs) { super(context, attrs); }
    public DrawerLayout(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        super.addView(child, index, params);
        // Hide drawer panels (all children after the first are drawers)
        if (getChildCount() > 1 && child != getChildAt(0)) {
            child.setVisibility(View.GONE);
        }
    }

    @Override
    protected void dispatchDraw(android.graphics.Canvas canvas) {
        // Only draw the first child (main content). Drawer panels are hidden.
        if (getChildCount() > 0) {
            View content = getChildAt(0);
            if (content.getVisibility() != GONE) {
                content.draw(canvas);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);

        // Measure children: first child = content (full size), others = drawers
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            if (i == 0) {
                // Content view: fill parent
                int cw = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
                int ch = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
                child.measure(cw, ch);
            } else {
                // Drawer view: use its own layout params width, full height
                LayoutParams lp = child.getLayoutParams();
                int drawerWidth = (lp != null && lp.width > 0) ? lp.width : width * 4 / 5;
                int cw = MeasureSpec.makeMeasureSpec(drawerWidth, MeasureSpec.EXACTLY);
                int ch = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
                child.measure(cw, ch);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            if (i == 0) {
                // Content view: fill the entire area
                child.layout(0, 0, width, height);
            } else {
                // Drawer view: positioned off-screen to the left (closed)
                int cw = child.getMeasuredWidth();
                if (mDrawerOpen) {
                    child.layout(0, 0, cw, height);
                } else {
                    child.layout(-cw, 0, 0, height);
                }
            }
        }
    }

    public void openDrawer(View drawerView) { mDrawerOpen = true; if (mDrawerListener != null) mDrawerListener.onDrawerOpened(drawerView); }
    public void openDrawer(int gravity) { openDrawer((View) null); }
    public void closeDrawer(View drawerView) { mDrawerOpen = false; if (mDrawerListener != null) mDrawerListener.onDrawerClosed(drawerView); }
    public void closeDrawer(int gravity) { closeDrawer((View) null); }
    public void closeDrawers() { closeDrawer((View) null); }
    public boolean isDrawerOpen(View drawer) { return mDrawerOpen; }
    public boolean isDrawerOpen(int drawerGravity) { return mDrawerOpen; }
    public boolean isDrawerVisible(View drawer) { return mDrawerOpen; }
    public boolean isDrawerVisible(int drawerGravity) { return mDrawerOpen; }
    public void setDrawerLockMode(int lockMode) { this.mLockMode = lockMode; }
    public void setDrawerLockMode(int lockMode, int edgeGravity) { this.mLockMode = lockMode; }
    public void setDrawerLockMode(int lockMode, View drawerView) { this.mLockMode = lockMode; }
    public int getDrawerLockMode(int edgeGravity) { return mLockMode; }
    public int getDrawerLockMode(View drawerView) { return mLockMode; }
    public void setDrawerListener(DrawerListener listener) { this.mDrawerListener = listener; }
    public void addDrawerListener(DrawerListener listener) { this.mDrawerListener = listener; }
    public void removeDrawerListener(DrawerListener listener) { if (mDrawerListener == listener) mDrawerListener = null; }
    public void setStatusBarBackgroundColor(int color) {}
    public void setStatusBarBackground(Object drawable) {}
    public void setScrimColor(int color) {}
    public void setDrawerTitle(int edgeGravity, CharSequence title) {}
    public CharSequence getDrawerTitle(int edgeGravity) { return null; }

    public interface DrawerListener {
        void onDrawerSlide(View drawerView, float slideOffset);
        void onDrawerOpened(View drawerView);
        void onDrawerClosed(View drawerView);
        void onDrawerStateChanged(int newState);
    }
    public static class SimpleDrawerListener implements DrawerListener {
        @Override public void onDrawerSlide(View drawerView, float slideOffset) {}
        @Override public void onDrawerOpened(View drawerView) {}
        @Override public void onDrawerClosed(View drawerView) {}
        @Override public void onDrawerStateChanged(int newState) {}
    }
}
