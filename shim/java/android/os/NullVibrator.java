package android.os;

/** Stub for AOSP compilation. A no-op vibrator. */
public class NullVibrator extends Vibrator {
    private static final NullVibrator sInstance = new NullVibrator();

    private NullVibrator() {}

    public static NullVibrator getInstance() { return sInstance; }

    public boolean hasVibrator() { return false; }
    public void vibrate(long milliseconds) {}
    public void vibrate(long[] pattern, int repeat) {}
    public void cancel() {}
}
