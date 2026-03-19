package android.view;

import android.graphics.SurfaceTexture;

/** Stub for android.view.TextureLayer used by TextureView. */
public class TextureLayer {
    public TextureLayer() {}
    public boolean isValid() { return false; }
    public void destroy() {}
    public long getDeferredLayerUpdater() { return 0L; }
    public boolean copyInto(android.graphics.Bitmap bitmap) { return false; }
    public void prepare(int width, int height, boolean isOpaque) {}
    public void setLayerPaint(android.graphics.Paint paint) {}
    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {}
    public SurfaceTexture detachSurfaceTexture() { return null; }
    public void updateSurfaceTexture() {}
    public void setTransform(android.graphics.Matrix matrix) {}
}
