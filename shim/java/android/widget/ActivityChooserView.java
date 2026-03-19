package android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/** Stub for android.widget.ActivityChooserView used by ShareActionProvider. */
public class ActivityChooserView extends ViewGroup {
    public ActivityChooserView(Context context) { super(context); }
    public ActivityChooserView(Context context, AttributeSet attrs) { super(context, attrs); }
    public ActivityChooserView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }
    public ActivityChooserView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) { super(context, attrs, defStyleAttr, defStyleRes); }

    public void setProvider(android.view.ActionProvider provider) {}
    public boolean isShown() { return false; }
    public void setDefaultActionButtonContentDescription(int resourceId) {}
    public void setInitialActivityCount(int itemCount) {}
    public void setExpandActivityOverflowButtonContentDescription(int resourceId) {}
    public void setExpandActivityOverflowButtonDrawable(android.graphics.drawable.Drawable drawable) {}
    public void dismissPopup() {}
    public void setActivityChooserModel(ActivityChooserModel dataModel) {}

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {}
}
