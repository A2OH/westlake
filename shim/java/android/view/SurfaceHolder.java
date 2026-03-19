package android.view;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Android-compatible SurfaceHolder shim.
 * OH mapping: OH_NativeWindow lifecycle callbacks
 */
public interface SurfaceHolder {

    @Deprecated
    int SURFACE_TYPE_NORMAL = 0;
    @Deprecated
    int SURFACE_TYPE_HARDWARE = 1;
    @Deprecated
    int SURFACE_TYPE_GPU = 2;
    @Deprecated
    int SURFACE_TYPE_PUSH_BUFFERS = 3;

    interface Callback {
        void surfaceCreated(SurfaceHolder holder);
        void surfaceChanged(SurfaceHolder holder, int format, int width, int height);
        void surfaceDestroyed(SurfaceHolder holder);
    }

    interface Callback2 extends Callback {
        void surfaceRedrawNeeded(SurfaceHolder holder);
        default void surfaceRedrawNeededAsync(SurfaceHolder holder, Runnable drawingFinished) {
            surfaceRedrawNeeded(holder);
            if (drawingFinished != null) drawingFinished.run();
        }
    }

    void addCallback(Callback callback);
    void removeCallback(Callback callback);

    boolean isCreating();

    @Deprecated
    void setType(int type);

    void setFormat(int format);
    void setFixedSize(int width, int height);
    void setSizeFromLayout();
    void setKeepScreenOn(boolean screenOn);

    Canvas lockCanvas();
    Canvas lockCanvas(Rect dirty);
    Canvas lockHardwareCanvas();
    void unlockCanvasAndPost(Canvas canvas);

    Rect getSurfaceFrame();
    Surface getSurface();
}
