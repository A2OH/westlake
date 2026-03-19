package android.view;

import android.os.IBinder;

/**
 * Stub for SurfacePackage used by SurfaceView for embedded surfaces.
 */
public class SurfacePackage {
    public SurfacePackage() {}
    public SurfacePackage(SurfaceControl sc) {}

    public SurfaceControl getSurfaceControl() { return new SurfaceControl(); }
    public IBinder getInputToken() { return null; }
    public void release() {}
}
