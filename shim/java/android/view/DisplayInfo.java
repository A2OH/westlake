package android.view;

import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;

/** Stub for AOSP compilation. Describes the characteristics of a display. */
public final class DisplayInfo implements Parcelable {
    public String name = "Built-in Screen";
    public String uniqueId = "local:0";
    public int type = Display.TYPE_INTERNAL;
    public DisplayAddress address;
    public int flags;
    public int layerStack;
    public int ownerUid;
    public String ownerPackageName;
    public int logicalWidth = 1080;
    public int logicalHeight = 1920;
    public int rotation = Surface.ROTATION_0;
    public int state = Display.STATE_ON;
    public int colorMode = 0;
    public int removeMode = 0;
    public int smallestNominalAppWidth = 1080;
    public int smallestNominalAppHeight = 1920;
    public int largestNominalAppWidth = 1920;
    public int largestNominalAppHeight = 1080;
    public long appVsyncOffsetNanos;
    public long presentationDeadlineNanos;
    public boolean minimalPostProcessingSupported;
    public Display.Mode[] supportedModes = Display.Mode.EMPTY_ARRAY;
    public int[] supportedColorModes = new int[0];
    public Display.HdrCapabilities hdrCapabilities;
    public DisplayCutout displayCutout;

    public DisplayInfo() {}

    public DisplayInfo(DisplayInfo other) {
        copyFrom(other);
    }

    public void copyFrom(DisplayInfo other) {
        if (other == null) return;
        name = other.name;
        uniqueId = other.uniqueId;
        type = other.type;
        address = other.address;
        flags = other.flags;
        layerStack = other.layerStack;
        ownerUid = other.ownerUid;
        ownerPackageName = other.ownerPackageName;
        logicalWidth = other.logicalWidth;
        logicalHeight = other.logicalHeight;
        rotation = other.rotation;
        state = other.state;
        colorMode = other.colorMode;
        removeMode = other.removeMode;
        smallestNominalAppWidth = other.smallestNominalAppWidth;
        smallestNominalAppHeight = other.smallestNominalAppHeight;
        largestNominalAppWidth = other.largestNominalAppWidth;
        largestNominalAppHeight = other.largestNominalAppHeight;
        appVsyncOffsetNanos = other.appVsyncOffsetNanos;
        presentationDeadlineNanos = other.presentationDeadlineNanos;
        minimalPostProcessingSupported = other.minimalPostProcessingSupported;
        supportedModes = other.supportedModes;
        supportedColorModes = other.supportedColorModes;
        hdrCapabilities = other.hdrCapabilities;
        displayCutout = other.displayCutout;
    }

    public Display.Mode getMode() {
        return new Display.Mode(0, logicalWidth, logicalHeight, 60.0f);
    }

    public float[] getDefaultRefreshRates() {
        return new float[]{ 60.0f };
    }

    public boolean isHdr() { return false; }
    public boolean isWideColorGamut() { return false; }

    public void getAppMetrics(DisplayMetrics outMetrics, DisplayAdjustments adj) {
        outMetrics.widthPixels = logicalWidth;
        outMetrics.heightPixels = logicalHeight;
        outMetrics.density = 2.0f;
        outMetrics.densityDpi = 320;
        outMetrics.scaledDensity = 2.0f;
        outMetrics.xdpi = 320.0f;
        outMetrics.ydpi = 320.0f;
    }

    public void getLogicalMetrics(DisplayMetrics outMetrics,
            CompatibilityInfo compatInfo, Configuration config) {
        getAppMetrics(outMetrics, null);
    }

    public int describeContents() { return 0; }
    public void writeToParcel(Parcel dest, int flags) {}
    public static final Parcelable.Creator<DisplayInfo> CREATOR =
            new Parcelable.Creator<DisplayInfo>() {
        public DisplayInfo createFromParcel(Parcel in) { return new DisplayInfo(); }
        public DisplayInfo[] newArray(int size) { return new DisplayInfo[size]; }
    };
}
