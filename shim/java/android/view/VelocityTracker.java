package android.view;

/**
 * Stub for android.view.VelocityTracker.
 * The real AOSP implementation requires native JNI methods (nativeInitialize, etc.)
 * which are not available in the shim layer. This stub provides a pure-Java
 * no-op implementation that returns safe defaults.
 */
public final class VelocityTracker {
    public VelocityTracker() {}

    public void addMovement(MotionEvent p0) {}
    public void clear() {}
    public void computeCurrentVelocity(int p0) {}
    public void computeCurrentVelocity(int p0, float p1) {}
    public float getXVelocity() { return 0f; }
    public float getXVelocity(int p0) { return 0f; }
    public float getYVelocity() { return 0f; }
    public float getYVelocity(int p0) { return 0f; }
    public static VelocityTracker obtain() { return new VelocityTracker(); }
    public static VelocityTracker obtain(String strategy) { return new VelocityTracker(); }
    public void recycle() {}
    public boolean isAxisSupported(int axis) { return false; }
}
