package androidx.exifinterface.media;

import java.io.File;
import java.io.FileDescriptor;
import java.io.InputStream;

/**
 * Minimal AndroidX ExifInterface shadow for guest image pipelines.
 *
 * Glide uses this library parser only to get image orientation before bitmap
 * decode. Returning normal orientation keeps the stock decode path moving
 * without depending on AndroidX's large TIFF/JPEG parser in the bootstrap shim.
 */
public class ExifInterface {
    public static final int ORIENTATION_NORMAL = 1;

    public ExifInterface(InputStream inputStream) {
        log("stream");
    }

    public ExifInterface(String filename) {
        log("path");
    }

    public ExifInterface(File file) {
        log("file");
    }

    public ExifInterface(FileDescriptor fileDescriptor) {
        log("fd");
    }

    public int i(String tag, int defaultValue) {
        return getAttributeInt(tag, defaultValue);
    }

    public int getAttributeInt(String tag, int defaultValue) {
        if ("Orientation".equals(tag)) {
            return ORIENTATION_NORMAL;
        }
        return defaultValue;
    }

    public String getAttribute(String tag) {
        return null;
    }

    public void saveAttributes() {
    }

    public void setAttribute(String tag, String value) {
    }

    private static void log(String source) {
        try {
            System.out.println("[WestlakeLauncher] MCD_EXIFINTERFACE_STUB source=" + source);
        } catch (Throwable ignored) {
        }
    }
}
