package android.view;

import android.os.Looper;
import android.os.MessageQueue;

/** Stub for AOSP compilation. Receives display events (vsync, hotplug). */
public abstract class DisplayEventReceiver {
    public static final int VSYNC_SOURCE_APP = 0;
    public static final int VSYNC_SOURCE_SURFACE_FLINGER = 1;
    public static final int CONFIG_CHANGED_EVENT_SUPPRESS = 0;

    public DisplayEventReceiver(Looper looper) {}
    public DisplayEventReceiver(Looper looper, int vsyncSource) {}
    public DisplayEventReceiver(Looper looper, int vsyncSource, int configChanged) {}

    public void onVsync(long timestampNanos, long physicalDisplayId, int frame) {}
    public void onHotplug(long timestampNanos, long physicalDisplayId, boolean connected) {}
    public void onConfigChanged(long timestampNanos, long physicalDisplayId, int configId) {}

    public void scheduleVsync() {}
    public void dispose() {}
}
