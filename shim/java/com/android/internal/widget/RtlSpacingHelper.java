package com.android.internal.widget;

/** Stub for AOSP RtlSpacingHelper used by Toolbar. */
public class RtlSpacingHelper {
    public static final int UNDEFINED = Integer.MIN_VALUE;

    public RtlSpacingHelper() {}

    public int getStart() { return 0; }
    public int getEnd() { return 0; }
    public int getLeft() { return 0; }
    public int getRight() { return 0; }
    public void setAbsolute(int left, int right) {}
    public void setRelative(int start, int end) {}
    public void setDirection(boolean isRtl) {}
}
