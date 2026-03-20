package android.hardware.display;

import android.graphics.ColorSpace;
import android.view.Display;
import android.view.DisplayInfo;

/** Stub for AOSP compilation. */
public class DisplayManagerGlobal {
    private static final DisplayManagerGlobal sInstance = new DisplayManagerGlobal();
    public DisplayManagerGlobal() {}
    public static DisplayManagerGlobal getInstance() { return sInstance; }

    public Display getCompatibleDisplay(int displayId, android.content.res.Resources resources) {
        return null;
    }

    public Display getRealDisplay(int displayId) { return null; }

    public DisplayInfo getDisplayInfo(int displayId) {
        return new DisplayInfo();
    }

    public void requestColorMode(int displayId, int colorMode) {}

    public ColorSpace getPreferredWideGamutColorSpace() {
        return ColorSpace.get(ColorSpace.Named.SRGB);
    }

    public boolean isUidPresentOnDisplay(int uid, int displayId) {
        return true;
    }

    public boolean hasOverrideDisplayAdjustments() { return false; }
}
