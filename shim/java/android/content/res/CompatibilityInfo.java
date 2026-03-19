package android.content.res;

/**
 * Stub for CompatibilityInfo and its Translator inner class.
 */
public class CompatibilityInfo {
    public float applicationScale = 1.0f;

    public static final CompatibilityInfo DEFAULT_COMPATIBILITY_INFO = new CompatibilityInfo();

    public CompatibilityInfo() {}

    public Translator getTranslator() { return null; }
    public boolean isScalingRequired() { return false; }
    public boolean supportsScreen() { return true; }

    public class Translator {
        public float applicationScale = 1.0f;
        public float applicationInvertedScale = 1.0f;
        public void translateRectInScreenToAppWinFrame(android.graphics.Rect rect) {}
        public void translateRectInScreenToAppWindow(android.graphics.Rect rect) {}
        public void translateRectInAppWindowToScreen(android.graphics.Rect rect) {}
        public void translateRegionInWindowToScreen(android.graphics.Region region) {}
        public void translateCanvas(android.graphics.Canvas canvas) {}
    }
}
