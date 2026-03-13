package android.hardware;

import java.io.Closeable;

/**
 * Android-compatible HardwareBuffer shim. Stub wrapping buffer metadata.
 */
public class HardwareBuffer implements Closeable {
    public static final int RGBA_8888 = 1;
    public static final int RGBX_8888 = 2;
    public static final int RGB_888 = 3;
    public static final int RGB_565 = 4;
    public static final int BLOB = 0x21;

    public static final int USAGE_CPU_READ_RARELY = 2;
    public static final int USAGE_CPU_READ_OFTEN = 3;
    public static final int USAGE_CPU_WRITE_RARELY = 0x20;
    public static final int USAGE_CPU_WRITE_OFTEN = 0x30;
    public static final int USAGE_GPU_SAMPLED_IMAGE = 0x100;
    public static final int USAGE_GPU_COLOR_OUTPUT = 0x200;

    private final int mWidth, mHeight, mLayers, mFormat;
    private final long mUsage;
    private boolean mClosed;

    private HardwareBuffer(int width, int height, int format, int layers, long usage) {
        mWidth = width; mHeight = height; mFormat = format;
        mLayers = layers; mUsage = usage;
    }

    public static HardwareBuffer create(int width, int height, int format, int layers, long usage) {
        return new HardwareBuffer(width, height, format, layers, usage);
    }

    public int getWidth() { return mWidth; }
    public int getHeight() { return mHeight; }
    public int getFormat() { return mFormat; }
    public int getLayers() { return mLayers; }
    public long getUsage() { return mUsage; }

    public boolean isClosed() { return mClosed; }

    @Override
    public void close() { mClosed = true; }

    public static boolean isSupported(int width, int height, int format, int layers, long usage) {
        return width > 0 && height > 0 && layers > 0;
    }
}
