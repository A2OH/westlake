package android.view;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

/** Stub for AOSP compilation. */
public final class Choreographer {
    public static final int CALLBACK_INPUT = 0;
    public static final int CALLBACK_ANIMATION = 1;
    public static final int CALLBACK_TRAVERSAL = 2;
    public static final int CALLBACK_COMMIT = 3;
    public static final int CALLBACK_INSETS_ANIMATION = 4;

    private static final ThreadLocal<Choreographer> sThreadInstance =
            new ThreadLocal<Choreographer>() {
                @Override
                protected Choreographer initialValue() {
                    return new Choreographer();
                }
            };

    private long mLastFrameTimeNanos;
    private long mFrameIntervalNanos = (long)(1000000000 / 60.0);

    private Choreographer() {
        mLastFrameTimeNanos = System.nanoTime();
    }

    public static Choreographer getInstance() {
        return sThreadInstance.get();
    }

    public static long getFrameDelay() { return 10; }
    public static void setFrameDelay(long delay) {}

    public static long subtractFrameDelay(long delayMillis) {
        long delay = getFrameDelay();
        return delayMillis <= delay ? 0 : delayMillis - delay;
    }

    public long getFrameTime() {
        return mLastFrameTimeNanos / 1000000;
    }

    public long getFrameTimeNanos() {
        return mLastFrameTimeNanos;
    }

    public long getLastFrameTimeNanos() {
        return mLastFrameTimeNanos;
    }

    public long getFrameIntervalNanos() {
        return mFrameIntervalNanos;
    }

    public void postCallback(int callbackType, Runnable action, Object token) {}
    public void postCallbackDelayed(int callbackType, Runnable action, Object token, long delayMillis) {}
    public void removeCallbacks(int callbackType, Runnable action, Object token) {}

    public void postFrameCallback(FrameCallback callback) {}
    public void postFrameCallbackDelayed(FrameCallback callback, long delayMillis) {}
    public void removeFrameCallback(FrameCallback callback) {}

    public interface FrameCallback {
        void doFrame(long frameTimeNanos);
    }
}
