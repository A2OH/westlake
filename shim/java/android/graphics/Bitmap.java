package android.graphics;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.graphics.Bitmap
 * OH mapping: image.PixelMap / drawing.OH_Drawing_Bitmap
 *
 * Wraps a native OH_Drawing_Bitmap handle for pixel-level operations.
 */
public class Bitmap {

    public static final int DENSITY_NONE = 0;

    public enum Config {
        ALPHA_8,
        RGB_565,
        ARGB_4444,
        ARGB_8888,
        RGBA_F16,
        HARDWARE
    }

    public enum CompressFormat {
        JPEG, PNG, WEBP, WEBP_LOSSY, WEBP_LOSSLESS
    }

    public boolean compress(CompressFormat format, int quality, java.io.OutputStream stream) {
        // Stub — real implementation on app_process64 via framework Bitmap
        return false;
    }

    private final int width;
    private final int height;
    private final Config config;
    private boolean recycled = false;
    private long nativeHandle;
    /** Raw image file bytes (PNG/JPEG/WebP) for pipe-mode rendering */
    public byte[] mImageData;
    /** Decoded ARGB pixel data */
    public int[] mPixels;
    public int mWidth;
    public int mHeight;

    private Bitmap(int width, int height, Config config) {
        this.width  = width;
        this.height = height;
        this.config = config;
    }

    // ── Factory ──

    public static Bitmap createBitmap(int width, int height, Config config) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Bitmap dimensions must be positive");
        }
        Bitmap bmp = new Bitmap(width, height, config);
        bmp.nativeHandle = OHBridge.bitmapCreate(width, height, configToFormat(config));
        return bmp;
    }

    public static Bitmap createBitmap(android.util.DisplayMetrics display, int width, int height, Config config) {
        return createBitmap(width, height, config);
    }

    public static Bitmap createBitmap(Bitmap src) {
        if (src == null) throw new NullPointerException("src must not be null");
        Bitmap bmp = new Bitmap(src.width, src.height, src.config);
        bmp.nativeHandle = OHBridge.bitmapCreate(src.width, src.height, configToFormat(src.config));
        return bmp;
    }

    /** Create a Bitmap holding raw image file bytes for pipe-mode rendering */
    public static Bitmap createFromImageData(byte[] imageData, int width, int height) {
        Bitmap bmp = new Bitmap(width > 0 ? width : 1, height > 0 ? height : 1, Config.ARGB_8888);
        bmp.mImageData = imageData;
        bmp.mWidth = width;
        bmp.mHeight = height;
        return bmp;
    }

    private static int configToFormat(Config config) {
        if (config == null) return 0;
        switch (config) {
            case ALPHA_8:   return 1;
            case RGB_565:   return 2;
            case ARGB_4444: return 3;
            default:        return 0; // ARGB_8888
        }
    }

    // ── Native handle ──

    public long getNativeHandle() { return nativeHandle; }

    // ── Properties ──

    public int getWidth()  { return width; }
    public int getHeight() { return height; }
    public Config getConfig() { return config; }
    public boolean isRecycled() { return recycled; }

    public void setPixels(int[] pixels, int offset, int stride, int x, int y, int w, int h) {
        if (mPixels == null) mPixels = new int[width * height];
        for (int row = 0; row < h; row++) {
            System.arraycopy(pixels, offset + row * stride, mPixels, (y + row) * width + x, w);
        }
    }

    public void getPixels(int[] pixels, int offset, int stride, int x, int y, int w, int h) {
        if (mPixels == null) return;
        for (int row = 0; row < h; row++) {
            System.arraycopy(mPixels, (y + row) * width + x, pixels, offset + row * stride, w);
        }
    }

    public int getPixel(int x, int y) {
        if (mPixels != null && x >= 0 && y >= 0 && x < width && y < height) return mPixels[y * width + x];
        return 0;
    }

    public void recycle() {
        if (nativeHandle != 0) { OHBridge.bitmapDestroy(nativeHandle); nativeHandle = 0; }
        recycled = true;
    }

    public int getByteCount() {
        int bytesPerPixel;
        switch (config) {
            case ALPHA_8:   bytesPerPixel = 1; break;
            case RGB_565:
            case ARGB_4444: bytesPerPixel = 2; break;
            case RGBA_F16:  bytesPerPixel = 8; break;
            default:        bytesPerPixel = 4; break;
        }
        return width * height * bytesPerPixel;
    }

    // ── Pixel access ──

    public void setPixel(int x, int y, int color) {
        if (nativeHandle != 0) OHBridge.bitmapSetPixel(nativeHandle, x, y, color);
    }

    // getPixel already defined above with mPixels support

    // Methods needed for View.java compilation
    public void eraseColor(int color) { /* no-op */ }
    public void setDensity(int density) { /* no-op */ }
    public void setHasAlpha(boolean hasAlpha) { /* no-op */ }
    public boolean hasAlpha() { return true; }
    public int getDensity() { return 160; }

    public byte[] getNinePatchChunk() { return null; }
    public void getOpticalInsets(Rect outInsets) { if (outInsets != null) outInsets.set(0, 0, 0, 0); }

    public int getScaledWidth(int targetDensity) {
        int d = getDensity();
        return d == 0 ? width : width * targetDensity / d;
    }

    public int getScaledHeight(int targetDensity) {
        int d = getDensity();
        return d == 0 ? height : height * targetDensity / d;
    }

    public int getScaledWidth(Canvas canvas) { return getScaledWidth(canvas.getDensity()); }
    public int getScaledHeight(Canvas canvas) { return getScaledHeight(canvas.getDensity()); }

    public boolean hasMipMap() { return false; }
    public void setHasMipMap(boolean hasMipMap) { /* no-op */ }
    public NinePatch.InsetStruct getNinePatchInsets() { return null; }

    public static int scaleFromDensity(int size, int sourceDensity, int targetDensity) {
        if (sourceDensity == 0 || targetDensity == 0 || sourceDensity == targetDensity) return size;
        return (size * targetDensity + (sourceDensity >> 1)) / sourceDensity;
    }

    public Bitmap extractAlpha() {
        return createBitmap(width, height, Config.ALPHA_8);
    }

    public Bitmap extractAlpha(Paint paint, int[] offsetXY) {
        if (offsetXY != null && offsetXY.length >= 2) {
            offsetXY[0] = 0;
            offsetXY[1] = 0;
        }
        return createBitmap(width, height, Config.ALPHA_8);
    }

    @Override
    public String toString() {
        return "Bitmap(" + width + "x" + height + ", " + config + ")";
    }
}
