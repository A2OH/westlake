package android.view;

import android.content.res.Configuration;
import android.graphics.Point;
import android.util.DisplayMetrics;

/** Stub for AOSP compilation. Adjustments applied to display info. */
public class DisplayAdjustments {
    private Configuration mConfiguration;

    public DisplayAdjustments() {
        mConfiguration = new Configuration();
    }

    public DisplayAdjustments(Configuration config) {
        mConfiguration = config != null ? config : new Configuration();
    }

    public DisplayAdjustments(DisplayAdjustments other) {
        mConfiguration = other != null ? other.mConfiguration : new Configuration();
    }

    public Configuration getConfiguration() { return mConfiguration; }
    public void setConfiguration(Configuration config) { mConfiguration = config; }

    public int getRotation(int rotation) { return rotation; }
    public DisplayCutout getDisplayCutout(DisplayCutout cutout) { return cutout; }
    public void adjustSize(Point size, int rotation) {}
    public void adjustMetrics(DisplayMetrics metrics, int rotation) {}

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisplayAdjustments)) return false;
        return true;
    }

    public int hashCode() { return 0; }
}
