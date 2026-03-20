package android.graphics;

/** Stub for AOSP compilation. Holds frame timing info. */
public class FrameInfo {
    public FrameInfo() {}
    public void setVsync(long intendedVsync, long vsync) {}
    public void markInputHandlingStart() {}
    public void markAnimationsStart() {}
    public void markPerformTraversalsStart() {}
    public long getIntendedVsync() { return 0; }
}
