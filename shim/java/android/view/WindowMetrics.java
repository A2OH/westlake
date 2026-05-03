package android.view;
import android.graphics.Rect;

/**
 * Shim: android.view.WindowMetrics (API 30+)
 * Reports the current metrics of a window, including bounds and insets.
 */
public class WindowMetrics {

    private final Rect bounds;
    private final WindowInsets windowInsets;

    // ── Constructor ─────────────────────────────────────────────────────────

    public WindowMetrics(Rect bounds, Object windowInsets) {
        this(bounds, windowInsets instanceof WindowInsets
                ? (WindowInsets) windowInsets : new WindowInsets());
    }

    public WindowMetrics(Rect bounds, WindowInsets windowInsets) {
        this.bounds = bounds;
        this.windowInsets = windowInsets != null ? windowInsets : new WindowInsets();
    }

    // ── Accessors ───────────────────────────────────────────────────────────

    /** Returns the bounds of the window. */
    public Rect getBounds() {
        return bounds;
    }

    /**
     * Returns the window insets.
     */
    public WindowInsets getWindowInsets() {
        return windowInsets;
    }
}
