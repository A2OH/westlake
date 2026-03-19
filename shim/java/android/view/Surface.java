package android.view;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Android-compatible Surface shim. Stub implementation.
 */
public class Surface {
    private final String mName;

    public Surface() { mName = "Surface"; }

    public Surface(String name) { mName = name; }

    public boolean isValid() { return true; }

    public android.graphics.Canvas lockCanvas(android.graphics.Rect dirty) {
        return new android.graphics.Canvas();
    }

    public void unlockCanvasAndPost(android.graphics.Canvas canvas) {
        // stub: no-op
    }

    public void release() { /* stub: no-op */ }
    public void destroy() { /* stub: no-op */ }
    public void copyFrom(SurfaceControl sc) { /* stub: no-op */ }
    public int getGenerationId() { return 0; }
    public Canvas lockHardwareCanvas() { return new Canvas(); }
    public void setCompatibilityTranslator(android.content.res.CompatibilityInfo.Translator translator) {}
    public void forceScopedDisconnect() {}

    public static Surface createFrom(SurfaceControl sc) {
        return new Surface();
    }

    @Override
    public String toString() { return "Surface{" + mName + "}"; }
}
