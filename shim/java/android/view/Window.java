package android.view;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.widget.MediaController;
import java.util.List;

public class Window {
    public static final int DECOR_CAPTION_SHADE_AUTO = 0;
    public static final int DECOR_CAPTION_SHADE_DARK = 0;
    public static final int DECOR_CAPTION_SHADE_LIGHT = 0;
    public static final int FEATURE_OPTIONS_PANEL = 0;
    public static final int FEATURE_NO_TITLE = 1;
    public static final int FEATURE_CONTEXT_MENU = 6;
    public static final int FEATURE_CUSTOM_TITLE = 7;
    public static final int FEATURE_ACTION_BAR = 8;
    public static final int FEATURE_ACTION_BAR_OVERLAY = 9;
    public static final int FEATURE_ACTION_MODE_OVERLAY = 10;
    public static final int FEATURE_CONTENT_TRANSITIONS = 12;
    public static final int FEATURE_ACTIVITY_TRANSITIONS = 13;
    public static final int FEATURE_LEFT_ICON = 3;
    public static final int FEATURE_RIGHT_ICON = 4;
    public static final int ID_ANDROID_CONTENT = 0x01020002;
    public static final int NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME = 0;
    public static final int STATUS_BAR_BACKGROUND_TRANSITION_NAME = 0;

    private Context mContext;
    private View mDecorView;
    private View mContentView;
    private int mFeatures;
    private int mFlags;
    private Callback mCallback;

    public interface Callback {
        boolean dispatchKeyEvent(KeyEvent event);
        boolean dispatchTouchEvent(android.view.MotionEvent event);
        boolean dispatchTrackballEvent(android.view.MotionEvent event);
        void onContentChanged();
        void onWindowFocusChanged(boolean hasFocus);
        void onAttachedToWindow();
        void onDetachedFromWindow();
        void onWindowAttributesChanged(WindowManager.LayoutParams attrs);
        boolean onMenuOpened(int featureId, android.view.Menu menu);
        boolean onMenuItemSelected(int featureId, android.view.MenuItem item);
        void onPanelClosed(int featureId, android.view.Menu menu);
        boolean onSearchRequested();
        View onCreatePanelView(int featureId);
        boolean onCreatePanelMenu(int featureId, android.view.Menu menu);
        boolean onPreparePanel(int featureId, View view, android.view.Menu menu);
    }
    private CharSequence mTitle;
    private int mStatusBarColor;
    private int mNavigationBarColor;

    public Window(Context p0) {
        mContext = p0;
        // Create a default FrameLayout as the decor view
        mDecorView = new android.widget.FrameLayout(p0);
    }

    public void addContentView(View p0, Object p1) {
        if (mDecorView instanceof ViewGroup) {
            ((ViewGroup) mDecorView).addView(p0);
        }
    }
    public void addFlags(int p0) { mFlags |= p0; }
    public void addOnFrameMetricsAvailableListener(Object p0, Handler p1) {}
    public void clearFlags(int p0) { mFlags &= ~p0; }
    public void closeAllPanels() {}
    public void closePanel(int p0) {}
    public View getDecorView() { return mDecorView; }
    public LayoutInflater getLayoutInflater() { return LayoutInflater.from(mContext); }

    public <T extends View> T findViewById(int id) {
        if (mDecorView != null) return mDecorView.findViewById(id);
        return null;
    }
    public Object findViewById_legacy(int p0) {
        return findViewById(p0);
    }
    public boolean getAllowEnterTransitionOverlap() { return false; }
    public boolean getAllowReturnTransitionOverlap() { return false; }
    public Object getAttributes() { return null; }
    public Callback getCallback() { return mCallback; }
    public int getColorMode() { return 0; }
    public Window getContainer() { return null; }
    public Scene getContentScene() { return null; }
    public Context getContext() { return mContext; }
    public static int getDefaultFeatures(Context p0) { return 0; }
    public Transition getEnterTransition() { return null; }
    public Transition getExitTransition() { return null; }
    public int getFeatures() { return mFeatures; }
    public int getForcedWindowFlags() { return 0; }
    public int getLocalFeatures() { return 0; }
    public MediaController getMediaController() { return null; }
    public Transition getReenterTransition() { return null; }
    public Transition getReturnTransition() { return null; }
    public Transition getSharedElementEnterTransition() { return null; }
    public Transition getSharedElementExitTransition() { return null; }
    public Transition getSharedElementReenterTransition() { return null; }
    public Transition getSharedElementReturnTransition() { return null; }
    public boolean getSharedElementsUseOverlay() { return false; }
    public long getTransitionBackgroundFadeDuration() { return 0L; }
    public TransitionManager getTransitionManager() { return null; }
    public int getVolumeControlStream() { return 0; }
    public WindowManager getWindowManager() { return null; }
    public TypedArray getWindowStyle() { return null; }
    public boolean hasChildren() { return false; }
    public boolean hasFeature(int p0) { return (mFeatures & (1 << p0)) != 0; }
    public boolean hasSoftInputMode() { return false; }
    public void injectInputEvent(InputEvent p0) {}
    public void invalidatePanelMenu(int p0) {}
    public boolean isActive() { return false; }
    public boolean isFloating() { return false; }
    public boolean isNavigationBarContrastEnforced() { return false; }
    public boolean isShortcutKey(int p0, KeyEvent p1) { return false; }
    public boolean isStatusBarContrastEnforced() { return false; }
    public boolean isWideColorGamut() { return false; }
    public void makeActive() {}
    public void onActive() {}
    public void onConfigurationChanged(Configuration p0) {}
    public void openPanel(int p0, KeyEvent p1) {}
    public View peekDecorView() { return mDecorView; }
    public boolean performContextMenuIdentifierAction(int p0, int p1) { return false; }
    public boolean performPanelIdentifierAction(int p0, int p1, int p2) { return false; }
    public boolean performPanelShortcut(int p0, int p1, KeyEvent p2, int p3) { return false; }
    public void removeOnFrameMetricsAvailableListener(Object p0) {}
    public boolean requestFeature(int p0) { mFeatures |= (1 << p0); return true; }
    public void restoreHierarchyState(Bundle p0) {}
    public Bundle saveHierarchyState() { return null; }
    public void setAllowEnterTransitionOverlap(boolean p0) {}
    public void setAllowReturnTransitionOverlap(boolean p0) {}
    public void setAttributes(Object p0) {}
    public void setBackgroundDrawable(Drawable p0) {}
    public void setBackgroundDrawableResource(int p0) {}
    public void setCallback(Callback p0) { mCallback = p0; }
    public void setCallback(Object p0) { /* legacy */ }
    public void setChildDrawable(int p0, Drawable p1) {}
    public void setChildInt(int p0, int p1) {}
    public void setClipToOutline(boolean p0) {}
    public void setColorMode(int p0) {}
    public void setContainer(Window p0) {}
    public void setContentView(int layoutResID) {
        android.util.Log.i("Window", "setContentView(resId=0x" + Integer.toHexString(layoutResID) + ")");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (inflater == null) { android.util.Log.e("Window", "LayoutInflater is null!"); return; }
        // Inflate into a temporary FrameLayout root (needed for <merge> tags and LayoutParams)
        // Then move children to the decor
        android.widget.FrameLayout tempRoot = new android.widget.FrameLayout(mContext);
        View inflated = null;
        try {
            inflated = inflater.inflate(layoutResID, tempRoot, true);
        } catch (Throwable t) {
            try { inflated = inflater.inflate(layoutResID, null); } catch (Throwable t2) {}
        }
        android.util.Log.i("Window", "inflate result: " + (inflated != null ? inflated.getClass().getSimpleName() : "null")
            + " children=" + tempRoot.getChildCount());
        if (tempRoot.getChildCount() > 0) {
            // Use the first child (the actual content) — avoid wrapping in extra FrameLayout
            View content = tempRoot.getChildAt(0);
            tempRoot.removeAllViews();
            setContentView(content);
        } else if (inflated != null && inflated != tempRoot) {
            setContentView(inflated);
        }
    }
    public void setContentView(View p0) {
        setContentView(p0, null);
    }
    public void setContentView(View p0, Object p1) {
        mContentView = p0;
        if (mDecorView instanceof ViewGroup) {
            ViewGroup decor = (ViewGroup) mDecorView;
            decor.removeAllViews();
            decor.addView(p0);
            System.err.println("[Window] setContentView(View) decor children=" + decor.getChildCount());
        }
        // Tag decor with the owning Activity so View.invalidate() can trigger renderFrame()
        if (mContext instanceof android.app.Activity) {
            mDecorView.setTag(mContext);
        }
    }
    public void setDecorCaptionShade(int p0) {}
    public void setDecorFitsSystemWindows(boolean p0) {}
    public void setDefaultWindowFormat(int p0) {}
    public void setDimAmount(float p0) {}
    public void setElevation(float p0) {}
    public void setEnterTransition(Transition p0) {}
    public void setExitTransition(Transition p0) {}
    public void setFeatureDrawable(int p0, Drawable p1) {}
    public void setFeatureDrawableAlpha(int p0, int p1) {}
    public void setFeatureDrawableResource(int p0, int p1) {}
    public void setFeatureDrawableUri(int p0, Uri p1) {}
    public void setFeatureInt(int p0, int p1) {}
    public void setFlags(int p0, int p1) { mFlags = (mFlags & ~p1) | (p0 & p1); }
    public void setFormat(int p0) {}
    public void setGravity(int p0) {}
    public void setIcon(int p0) {}
    public void setLayout(int p0, int p1) {}
    public void setLocalFocus(boolean p0, boolean p1) {}
    public void setLogo(int p0) {}
    public void setMediaController(MediaController p0) {}
    public void setNavigationBarColor(int p0) { mNavigationBarColor = p0; }
    public void setNavigationBarContrastEnforced(boolean p0) {}
    public void setNavigationBarDividerColor(int p0) {}
    public void setPreferMinimalPostProcessing(boolean p0) {}
    public void setReenterTransition(Transition p0) {}
    public void setResizingCaptionDrawable(Drawable p0) {}
    public void setRestrictedCaptionAreaListener(Object p0) {}
    public void setReturnTransition(Transition p0) {}
    public void setSharedElementEnterTransition(Transition p0) {}
    public void setSharedElementExitTransition(Transition p0) {}
    public void setSharedElementReenterTransition(Transition p0) {}
    public void setSharedElementReturnTransition(Transition p0) {}
    public void setSharedElementsUseOverlay(boolean p0) {}
    public void setSoftInputMode(int p0) {}
    public void setStatusBarColor(int p0) { mStatusBarColor = p0; }
    public void setStatusBarContrastEnforced(boolean p0) {}
    public void setSustainedPerformanceMode(boolean p0) {}
    public void setSystemGestureExclusionRects(java.util.List<Object> p0) {}
    public void setTitle(CharSequence p0) { mTitle = p0; }
    public void setTransitionBackgroundFadeDuration(long p0) {}
    public void setTransitionManager(TransitionManager p0) {}
    public void setType(int p0) {}
    public void setUiOptions(int p0) {}
    public void setUiOptions(int p0, int p1) {}
    public void setVolumeControlStream(int p0) {}
    public void setWindowAnimations(int p0) {}
    public void setWindowManager(WindowManager p0, IBinder p1, String p2) {}
    public void setWindowManager(WindowManager p0, IBinder p1, String p2, boolean p3) {}
    public boolean superDispatchGenericMotionEvent(MotionEvent p0) { return false; }
    public boolean superDispatchKeyEvent(KeyEvent p0) { return false; }
    public boolean superDispatchKeyShortcutEvent(KeyEvent p0) { return false; }
    public boolean superDispatchTouchEvent(MotionEvent p0) { return false; }
    public boolean superDispatchTrackballEvent(MotionEvent p0) { return false; }
    public void takeInputQueue(Object p0) {}
    public void takeKeyEvents(boolean p0) {}
    public void takeSurface(Object p0) {}
    public void togglePanel(int p0, KeyEvent p1) {}

    /** Auto-generated stub. */
    public static interface OnContentApplyWindowInsetsListener {
        android.util.Pair<android.graphics.Insets, WindowInsets> onContentApplyWindowInsets(View view, WindowInsets insets);
    }

    /** Auto-generated stub. */
    public static interface OnFrameMetricsAvailableListener {}
}
