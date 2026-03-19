package android.widget;

import android.content.Intent;

/**
 * Stub for android.widget.RemoteViewsAdapter.
 */
public class RemoteViewsAdapter extends BaseAdapter {
    public interface RemoteAdapterConnectionCallback {
        boolean onRemoteAdapterConnected();
        void onRemoteAdapterDisconnected();
        default void setRemoteViewsAdapter(android.content.Intent intent, boolean isAsync) {}
        default void deferNotifyDataSetChanged() {}
    }

    public RemoteViewsAdapter(android.content.Context context, Intent intent,
            RemoteAdapterConnectionCallback callback, boolean useAsyncLoader) {
    }

    public boolean isDataReady() { return false; }

    public void setRemoteViewsOnClickHandler(RemoteViews.OnClickHandler handler) {}

    public void saveRemoteViewsCache() {}

    public Intent getRemoteViewsServiceIntent() { return null; }
    public void superNotifyDataSetChanged() {}
    public void setVisibleRangeHint(int start, int end) {}

    public static class AsyncRemoteAdapterAction implements Runnable {
        public AsyncRemoteAdapterAction(AbsListView listView, Intent intent) {}
        @Override
        public void run() {}
    }
}
