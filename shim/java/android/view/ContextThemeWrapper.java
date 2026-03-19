package android.view;

import android.content.Context;
import android.content.res.Resources;

/**
 * Stub for android.view.ContextThemeWrapper.
 */
public class ContextThemeWrapper extends Context {
    public ContextThemeWrapper() {}
    public ContextThemeWrapper(Context base, int themeResId) {}
    public ContextThemeWrapper(Context base, Resources.Theme theme) {}

    public void applyOverrideConfiguration(Object p0) {}
    public void onApplyThemeResource(Object p0, Object p1, Object p2) {}
    public void setTheme(int resid) {}
    public Resources.Theme getTheme() { return null; }
}
