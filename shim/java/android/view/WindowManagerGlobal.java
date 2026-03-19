package android.view;

import android.os.IBinder;

/** Stub for WindowManagerGlobal. */
public class WindowManagerGlobal {
    private static final WindowManagerGlobal sInstance = new WindowManagerGlobal();

    public WindowManagerGlobal() {}

    public static WindowManagerGlobal getInstance() { return sInstance; }

    public View getWindowView(IBinder windowToken) { return null; }
}
