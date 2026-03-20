package android.animation;

/** Stub for AOSP compilation. */
public class TimeAnimator extends ValueAnimator {

    private TimeListener mListener;
    private long mPreviousTime = -1;

    @Override
    public void start() {
        mPreviousTime = -1;
        super.start();
    }

    public void setTimeListener(TimeListener listener) {
        mListener = listener;
    }

    public static interface TimeListener {
        void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime);
    }
}
