package android.view;

import android.os.IBinder;
import android.graphics.Rect;

/** Stub for WindowManagerGlobal. */
public class WindowManagerGlobal implements WindowManager {
    private static final WindowManagerGlobal sInstance = new WindowManagerGlobal();
    private Display mDefaultDisplay;

    public WindowManagerGlobal() {}

    public static WindowManagerGlobal getInstance() { return sInstance; }

    public View getWindowView(IBinder windowToken) { return null; }

    @Override
    public void addView(View view, ViewGroup.LayoutParams params) {
        if (view == null) {
            return;
        }
        if (params != null) {
            view.setLayoutParams(params);
        }
    }

    @Override
    public void updateViewLayout(View view, ViewGroup.LayoutParams params) {
        if (view != null && params != null) {
            view.setLayoutParams(params);
        }
    }

    @Override
    public void removeView(View view) {
        if (view == null) {
            return;
        }
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        }
    }

    @Override
    public void removeViewImmediate(View view) {
        removeView(view);
    }

    @Override
    public Display getDefaultDisplay() {
        if (mDefaultDisplay == null) {
            android.view.DisplayInfo info = new android.view.DisplayInfo();
            android.hardware.display.DisplayManagerGlobal global =
                    android.hardware.display.DisplayManagerGlobal.getInstance();
            mDefaultDisplay = new Display(global, Display.DEFAULT_DISPLAY, info,
                    new DisplayAdjustments());
        }
        return mDefaultDisplay;
    }

    @Override
    public WindowMetrics getCurrentWindowMetrics() {
        return buildMetrics();
    }

    @Override
    public WindowMetrics getMaximumWindowMetrics() {
        return buildMetrics();
    }

    private WindowMetrics buildMetrics() {
        DisplayInfo info = new DisplayInfo();
        return new WindowMetrics(new Rect(0, 0, info.logicalWidth, info.logicalHeight),
                new WindowInsets());
    }
}
