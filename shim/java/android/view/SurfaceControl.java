package android.view;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

public final class SurfaceControl implements Parcelable {
    public static final int OPAQUE = 0x00000400;
    public static final int HIDDEN = 0x00000004;
    public static final int SECURE = 0x00000080;

    public SurfaceControl() {}

    public int describeContents() { return 0; }
    public boolean isValid() { return false; }
    public void readFromParcel(Parcel p0) {}
    public void release() {}
    public void writeToParcel(Parcel p0, int p1) {}
    public IBinder getHandle() { return null; }

    public static class Builder {
        public Builder(SurfaceSession session) {}
        public Builder() {}
        public Builder setName(String name) { return this; }
        public Builder setBufferSize(int width, int height) { return this; }
        public Builder setFormat(int format) { return this; }
        public Builder setParent(SurfaceControl parent) { return this; }
        public Builder setFlags(int flags) { return this; }
        public Builder setCallsite(String callsite) { return this; }
        public Builder setHidden(boolean hidden) { return this; }
        public Builder setColorLayer() { return this; }
        public Builder setContainerLayer() { return this; }
        public Builder setMetadata(int key, int value) { return this; }
        public Builder setLocalOwnerView(View view) { return this; }
        public Builder setOpaque(boolean opaque) { return this; }
        public SurfaceControl build() { return new SurfaceControl(); }
    }

    public static class Transaction implements java.io.Closeable {
        public Transaction() {}
        public Transaction setVisibility(SurfaceControl sc, boolean visible) { return this; }
        public Transaction setAlpha(SurfaceControl sc, float alpha) { return this; }
        public Transaction setPosition(SurfaceControl sc, float x, float y) { return this; }
        public Transaction setBufferSize(SurfaceControl sc, int w, int h) { return this; }
        public Transaction setLayer(SurfaceControl sc, int z) { return this; }
        public Transaction setRelativeLayer(SurfaceControl sc, SurfaceControl relativeTo, int z) { return this; }
        public Transaction setMatrix(SurfaceControl sc, float a, float b, float c, float d) { return this; }
        public Transaction setWindowCrop(SurfaceControl sc, Rect crop) { return this; }
        public Transaction setWindowCrop(SurfaceControl sc, int width, int height) { return this; }
        public Transaction setCornerRadius(SurfaceControl sc, float cornerRadius) { return this; }
        public Transaction setOpaque(SurfaceControl sc, boolean isOpaque) { return this; }
        public Transaction setSecure(SurfaceControl sc, boolean isSecure) { return this; }
        public Transaction show(SurfaceControl sc) { return this; }
        public Transaction hide(SurfaceControl sc) { return this; }
        public Transaction reparent(SurfaceControl sc, SurfaceControl newParent) { return this; }
        public Transaction setColor(SurfaceControl sc, float[] color) { return this; }
        public Transaction setGeometry(SurfaceControl sc, Rect src, Rect dst, int transform) { return this; }
        public Transaction merge(Transaction other) { return this; }
        public Transaction addTransactionCommittedListener(java.util.concurrent.Executor exec, TransactionCommittedListener listener) { return this; }
        public void apply() {}
        public void close() {}
        public Transaction deferTransactionUntilSurface(SurfaceControl sc, Surface barrier, long frameNumber) { return this; }
        public Transaction deferTransactionUntil(SurfaceControl sc, SurfaceControl barrier, long frameNumber) { return this; }
        public Transaction setInputWindowInfo(SurfaceControl sc, Object inputWindowHandle) { return this; }
        public Transaction remove(SurfaceControl sc) { return this; }
        public Transaction setFrameRate(SurfaceControl sc, float rate, int compatibility) { return this; }
    }

    public interface TransactionCommittedListener {
        void onTransactionCommitted();
    }
}
