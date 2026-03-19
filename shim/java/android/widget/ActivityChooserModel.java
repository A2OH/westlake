package android.widget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.DataSetObserver;

/** Stub for android.widget.ActivityChooserModel used by ShareActionProvider. */
public class ActivityChooserModel {

    public interface OnChooseActivityListener {
        boolean onChooseActivity(ActivityChooserModel host, Intent intent);
    }

    private ActivityChooserModel() {}

    public static ActivityChooserModel get(Context context, String historyFileName) {
        return new ActivityChooserModel();
    }

    public void setIntent(Intent intent) {}
    public Intent getIntent() { return null; }
    public int getActivityCount() { return 0; }
    public ResolveInfo getActivity(int index) { return null; }
    public int getHistorySize() { return 0; }
    public void setHistoryMaxSize(int maxSize) {}
    public int getHistoryMaxSize() { return 0; }
    public ResolveInfo getDefaultActivity() { return null; }
    public void setDefaultActivity(int index) {}
    public Intent chooseActivity(int index) { return null; }
    public void setOnChooseActivityListener(OnChooseActivityListener listener) {}
    public void registerObserver(DataSetObserver observer) {}
    public void unregisterObserver(DataSetObserver observer) {}
}
