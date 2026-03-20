package android.view;

import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManagerGlobal;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

/** Stub for AOSP compilation. */
public final class Display {
    public static final int DEFAULT_DISPLAY = 0;
    public static final int INVALID_DISPLAY = -1;

    public static final int STATE_UNKNOWN = 0;
    public static final int STATE_OFF = 1;
    public static final int STATE_ON = 2;
    public static final int STATE_DOZE = 3;
    public static final int STATE_DOZE_SUSPEND = 4;
    public static final int STATE_VR = 5;
    public static final int STATE_ON_SUSPEND = 6;

    public static final int FLAG_SUPPORTS_PROTECTED_BUFFERS = 1 << 0;
    public static final int FLAG_SECURE = 1 << 1;
    public static final int FLAG_PRIVATE = 1 << 2;
    public static final int FLAG_PRESENTATION = 1 << 3;
    public static final int FLAG_ROUND = 1 << 4;
    public static final int FLAG_CAN_SHOW_WITH_INSECURE_KEYGUARD = 1 << 5;
    public static final int FLAG_SHOULD_SHOW_SYSTEM_DECORATIONS = 1 << 6;
    public static final int FLAG_TRUSTED = 1 << 7;
    public static final int FLAG_SCALING_DISABLED = 1 << 30;

    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_INTERNAL = 1;
    public static final int TYPE_EXTERNAL = 2;
    public static final int TYPE_WIFI = 3;
    public static final int TYPE_OVERLAY = 4;
    public static final int TYPE_VIRTUAL = 5;

    public static final int COLOR_MODE_INVALID = -1;
    public static final int COLOR_MODE_DEFAULT = 0;
    public static final int COLOR_MODE_BT601_625 = 1;
    public static final int COLOR_MODE_BT601_625_UNADJUSTED = 2;
    public static final int COLOR_MODE_BT601_525 = 3;
    public static final int COLOR_MODE_BT601_525_UNADJUSTED = 4;
    public static final int COLOR_MODE_BT709 = 5;
    public static final int COLOR_MODE_DCI_P3 = 6;
    public static final int COLOR_MODE_SRGB = 7;
    public static final int COLOR_MODE_ADOBE_RGB = 8;
    public static final int COLOR_MODE_DISPLAY_P3 = 9;

    public static final int REMOVE_MODE_MOVE_CONTENT_TO_PRIMARY = 0;
    public static final int REMOVE_MODE_DESTROY_CONTENT = 1;

    /** @hide */
    @Retention(RetentionPolicy.SOURCE)
    public @interface ColorMode {}

    private final DisplayManagerGlobal mGlobal;
    private final int mDisplayId;
    private final int mFlags;
    private Resources mResources;

    public Display(DisplayManagerGlobal global, int displayId) {
        mGlobal = global;
        mDisplayId = displayId;
        mFlags = 0;
    }

    public int getDisplayId() { return mDisplayId; }
    public boolean isValid() { return true; }
    public String getName() { return "Built-in Screen"; }
    public int getFlags() { return mFlags; }
    public int getType() { return TYPE_INTERNAL; }
    public int getState() { return STATE_ON; }
    public String getUniqueId() { return "local:0"; }
    public int getLayerStack() { return 0; }
    public int getOwnerUid() { return 0; }
    public String getOwnerPackageName() { return null; }

    public void getSize(Point outSize) {
        outSize.x = 1080; outSize.y = 1920;
    }
    public void getRealSize(Point outSize) {
        outSize.x = 1080; outSize.y = 1920;
    }
    public void getRectSize(Rect outSize) {
        outSize.set(0, 0, 1080, 1920);
    }
    public void getCurrentSizeRange(Point outSmallestSize, Point outLargestSize) {
        outSmallestSize.x = 1080; outSmallestSize.y = 1920;
        outLargestSize.x = 1920; outLargestSize.y = 1080;
    }
    public int getMaximumSizeDimension() { return 1920; }
    public int getWidth() { return 1080; }
    public int getHeight() { return 1920; }

    public int getRotation() { return Surface.ROTATION_0; }
    public int getOrientation() { return getRotation(); }
    public int getPixelFormat() { return 1; }

    public float getRefreshRate() { return 60.0f; }
    public float[] getSupportedRefreshRates() { return new float[] { 60.0f }; }

    public Mode getMode() { return new Mode(0, 1080, 1920, 60.0f); }
    public Mode[] getSupportedModes() { return new Mode[] { getMode() }; }

    public void getRealMetrics(DisplayMetrics outMetrics) {
        outMetrics.widthPixels = 1080;
        outMetrics.heightPixels = 1920;
        outMetrics.density = 2.0f;
        outMetrics.densityDpi = 320;
        outMetrics.scaledDensity = 2.0f;
        outMetrics.xdpi = 320;
        outMetrics.ydpi = 320;
    }
    public void getMetrics(DisplayMetrics outMetrics) { getRealMetrics(outMetrics); }

    public boolean isWideColorGamut() { return false; }
    public boolean isHdr() { return false; }
    public boolean isMinimalPostProcessingSupported() { return false; }
    public int getColorMode() { return COLOR_MODE_DEFAULT; }
    public int[] getSupportedColorModes() { return new int[] { COLOR_MODE_DEFAULT }; }
    public int getRemoveMode() { return REMOVE_MODE_MOVE_CONTENT_TO_PRIMARY; }
    public HdrCapabilities getHdrCapabilities() { return new HdrCapabilities(); }

    public long getAppVsyncOffsetNanos() { return 0; }
    public long getPresentationDeadlineNanos() { return 0; }

    public boolean hasAccess(int uid) { return true; }
    public static boolean hasAccess(int uid, int flags, int ownerUid, int displayId) { return true; }
    public boolean isPublicPresentation() { return false; }
    public boolean isTrusted() { return true; }
    public DisplayCutout getCutout() { return null; }
    public void requestColorMode(int colorMode) {}

    public static String typeToString(int type) {
        switch (type) {
            case TYPE_UNKNOWN: return "UNKNOWN";
            case TYPE_INTERNAL: return "INTERNAL";
            case TYPE_EXTERNAL: return "EXTERNAL";
            case TYPE_WIFI: return "WIFI";
            case TYPE_OVERLAY: return "OVERLAY";
            case TYPE_VIRTUAL: return "VIRTUAL";
            default: return Integer.toString(type);
        }
    }

    public static String stateToString(int state) {
        switch (state) {
            case STATE_UNKNOWN: return "UNKNOWN";
            case STATE_OFF: return "OFF";
            case STATE_ON: return "ON";
            case STATE_DOZE: return "DOZE";
            case STATE_DOZE_SUSPEND: return "DOZE_SUSPEND";
            case STATE_VR: return "VR";
            case STATE_ON_SUSPEND: return "ON_SUSPEND";
            default: return Integer.toString(state);
        }
    }

    public static boolean isSuspendedState(int state) {
        return state == STATE_OFF || state == STATE_DOZE_SUSPEND || state == STATE_ON_SUSPEND;
    }
    public static boolean isDozeState(int state) {
        return state == STATE_DOZE || state == STATE_DOZE_SUSPEND;
    }
    public static boolean isActiveState(int state) {
        return state == STATE_ON || state == STATE_VR;
    }

    public static final class Mode implements Parcelable {
        public static final Mode[] EMPTY_ARRAY = new Mode[0];
        private final int mModeId;
        private final int mWidth;
        private final int mHeight;
        private final float mRefreshRate;

        public Mode(int modeId, int width, int height, float refreshRate) {
            mModeId = modeId; mWidth = width; mHeight = height; mRefreshRate = refreshRate;
        }
        public int getModeId() { return mModeId; }
        public int getPhysicalWidth() { return mWidth; }
        public int getPhysicalHeight() { return mHeight; }
        public float getRefreshRate() { return mRefreshRate; }
        public boolean matches(int width, int height, float refreshRate) {
            return mWidth == width && mHeight == height &&
                    Float.floatToIntBits(mRefreshRate) == Float.floatToIntBits(refreshRate);
        }
        @Override public int describeContents() { return 0; }
        @Override public void writeToParcel(Parcel out, int flags) {}
        public static final Parcelable.Creator<Mode> CREATOR = new Parcelable.Creator<Mode>() {
            public Mode createFromParcel(Parcel in) { return new Mode(0, 0, 0, 0); }
            public Mode[] newArray(int size) { return new Mode[size]; }
        };
    }

    public static final class HdrCapabilities implements Parcelable {
        public static final float INVALID_LUMINANCE = -1;
        public static final int HDR_TYPE_DOLBY_VISION = 1;
        public static final int HDR_TYPE_HDR10 = 2;
        public static final int HDR_TYPE_HLG = 3;
        public static final int HDR_TYPE_HDR10_PLUS = 4;

        public HdrCapabilities() {}
        public HdrCapabilities(int[] types, float max, float maxAvg, float min) {}
        public int[] getSupportedHdrTypes() { return new int[0]; }
        public float getDesiredMaxLuminance() { return INVALID_LUMINANCE; }
        public float getDesiredMaxAverageLuminance() { return INVALID_LUMINANCE; }
        public float getDesiredMinLuminance() { return INVALID_LUMINANCE; }
        @Override public int describeContents() { return 0; }
        @Override public void writeToParcel(Parcel dest, int flags) {}
        public static final Creator<HdrCapabilities> CREATOR = new Creator<HdrCapabilities>() {
            public HdrCapabilities createFromParcel(Parcel in) { return new HdrCapabilities(); }
            public HdrCapabilities[] newArray(int size) { return new HdrCapabilities[size]; }
        };
    }
}
