package android.animation;

import android.os.Looper;
import android.os.SystemClock;
import android.view.Choreographer;

import java.util.ArrayList;

/** Stub for AOSP compilation. Manages animation frame callbacks. */
public class AnimationHandler {
    private static final ThreadLocal<AnimationHandler> sAnimatorHandler =
            new ThreadLocal<AnimationHandler>();
    private final ArrayList<AnimationFrameCallback> mAnimationCallbacks = new ArrayList<AnimationFrameCallback>();
    private long mFrameDelay = 10;

    public AnimationHandler() {}

    public static AnimationHandler getInstance() {
        AnimationHandler handler = sAnimatorHandler.get();
        if (handler == null) {
            handler = new AnimationHandler();
            sAnimatorHandler.set(handler);
        }
        return handler;
    }

    public static int getAnimationCount() { return 0; }

    public long getFrameDelay() { return mFrameDelay; }

    public void setFrameDelay(long frameDelay) { mFrameDelay = frameDelay; }

    public void addAnimationFrameCallback(AnimationFrameCallback callback, long delay) {
        if (!mAnimationCallbacks.contains(callback)) {
            mAnimationCallbacks.add(callback);
        }
    }

    public void addOneShotCommitCallback(AnimationFrameCallback callback) {}

    public void removeCallback(AnimationFrameCallback callback) {
        mAnimationCallbacks.remove(callback);
    }

    public void autoCancelBasedOn(ObjectAnimator objectAnimator) {}

    /** Callback interface for animation frame updates. */
    public interface AnimationFrameCallback {
        boolean doAnimationFrame(long frameTime);
        void commitAnimationFrame(long frameTime);
    }
}
