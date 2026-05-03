package com.newrelic.agent.android.instrumentation;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.TypedValue;
import java.io.FileDescriptor;
import java.io.InputStream;

public final class BitmapFactoryInstrumentation {
    private BitmapFactoryInstrumentation() {}

    public static Bitmap decodeByteArray(byte[] data, int offset, int length) {
        return BitmapFactory.decodeByteArray(data, offset, length);
    }

    public static Bitmap decodeByteArray(
            byte[] data, int offset, int length, BitmapFactory.Options opts) {
        return BitmapFactory.decodeByteArray(data, offset, length, opts);
    }

    public static Bitmap decodeFile(String pathName) {
        return BitmapFactory.decodeFile(pathName);
    }

    public static Bitmap decodeFile(String pathName, BitmapFactory.Options opts) {
        return BitmapFactory.decodeFile(pathName, opts);
    }

    public static Bitmap decodeFileDescriptor(FileDescriptor fd) {
        return BitmapFactory.decodeFileDescriptor(fd);
    }

    public static Bitmap decodeFileDescriptor(
            FileDescriptor fd, Rect outPadding, BitmapFactory.Options opts) {
        return BitmapFactory.decodeFileDescriptor(fd, outPadding, opts);
    }

    public static Bitmap decodeResource(Resources res, int id) {
        return BitmapFactory.decodeResource(res, id);
    }

    public static Bitmap decodeResource(Resources res, int id, BitmapFactory.Options opts) {
        return BitmapFactory.decodeResource(res, id, opts);
    }

    public static Bitmap decodeResourceStream(
            Resources res, TypedValue value, InputStream is, Rect pad, BitmapFactory.Options opts) {
        return BitmapFactory.decodeResourceStream(res, value, is, pad, opts);
    }

    public static Bitmap decodeStream(InputStream is) {
        return BitmapFactory.decodeStream(is);
    }

    public static Bitmap decodeStream(InputStream is, Rect outPadding, BitmapFactory.Options opts) {
        return BitmapFactory.decodeStream(is, outPadding, opts);
    }
}
