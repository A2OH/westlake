package android.animation;

import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.HashMap;

/** Stub for AOSP compilation. */
@SuppressWarnings("unchecked")
public class ValueAnimator extends Animator implements AnimationHandler.AnimationFrameCallback {
    public static final int RESTART = 1;
    public static final int REVERSE = 2;
    public static final int INFINITE = -1;

    private long mDuration = 300;
    private long mStartDelay = 0;
    private TimeInterpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private int mRepeatCount = 0;
    private int mRepeatMode = RESTART;
    private boolean mRunning = false;
    private boolean mStarted = false;
    private Object mAnimatedValue;
    private PropertyValuesHolder[] mValues;
    private HashMap<String, PropertyValuesHolder> mValuesMap;
    AnimatorUpdateListener mUpdateListener = null;

    public ValueAnimator() {}

    public static ValueAnimator ofInt(int... values) {
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(values);
        return anim;
    }

    public static ValueAnimator ofArgb(int... values) {
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(values);
        return anim;
    }

    public static ValueAnimator ofFloat(float... values) {
        ValueAnimator anim = new ValueAnimator();
        anim.setFloatValues(values);
        return anim;
    }

    public static ValueAnimator ofPropertyValuesHolder(PropertyValuesHolder... values) {
        ValueAnimator anim = new ValueAnimator();
        anim.setValues(values);
        return anim;
    }

    public static ValueAnimator ofObject(TypeEvaluator evaluator, Object... values) {
        ValueAnimator anim = new ValueAnimator();
        anim.setObjectValues(values);
        anim.setEvaluator(evaluator);
        return anim;
    }

    public void setIntValues(int... values) {}
    public void setFloatValues(float... values) {}
    public void setObjectValues(Object... values) {}

    public void setValues(PropertyValuesHolder... values) {
        mValues = values;
        mValuesMap = new HashMap<>(values.length);
        for (PropertyValuesHolder pvh : values) {
            mValuesMap.put(pvh.getPropertyName(), pvh);
        }
    }

    public PropertyValuesHolder[] getValues() { return mValues; }

    public Object getAnimatedValue() { return mAnimatedValue; }
    public Object getAnimatedValue(String propertyName) { return null; }

    @Override
    public void setStartDelay(long startDelay) { mStartDelay = startDelay; }
    @Override
    public long getStartDelay() { return mStartDelay; }

    @Override
    public ValueAnimator setDuration(long duration) { mDuration = duration; return this; }
    @Override
    public long getDuration() { return mDuration; }

    public static float getDurationScale() { return 1.0f; }
    public static void setDurationScale(float durationScale) {}

    public long getCurrentPlayTime() { return 0; }
    public void setCurrentPlayTime(long playTime) {}
    public void setCurrentFraction(float fraction) {}

    public float getAnimatedFraction() { return 0f; }

    public void setRepeatCount(int value) { mRepeatCount = value; }
    public int getRepeatCount() { return mRepeatCount; }
    public void setRepeatMode(int value) { mRepeatMode = value; }
    public int getRepeatMode() { return mRepeatMode; }

    public void setEvaluator(TypeEvaluator value) {}

    @Override
    public void setInterpolator(TimeInterpolator value) { mInterpolator = value; }
    @Override
    public TimeInterpolator getInterpolator() { return mInterpolator; }

    public void addUpdateListener(AnimatorUpdateListener listener) { mUpdateListener = listener; }
    public void removeUpdateListener(AnimatorUpdateListener listener) {
        if (mUpdateListener == listener) mUpdateListener = null;
    }
    public void removeAllUpdateListeners() { mUpdateListener = null; }

    @Override
    public void start() { mStarted = true; mRunning = true; }
    @Override
    public void cancel() { mRunning = false; mStarted = false; }
    @Override
    public void end() { mRunning = false; mStarted = false; }
    @Override
    public void reverse() { start(); }
    @Override
    public boolean canReverse() { return true; }

    @Override
    public boolean isRunning() { return mRunning; }
    @Override
    public boolean isStarted() { return mStarted; }

    public static boolean areAnimatorsEnabled() { return true; }

    @Override
    public ValueAnimator clone() {
        ValueAnimator anim = (ValueAnimator) super.clone();
        return anim;
    }

    public String getNameForTrace() { return "ValueAnimator"; }

    // AnimationHandler.AnimationFrameCallback
    @Override
    public boolean doAnimationFrame(long frameTime) { return false; }
    @Override
    public void commitAnimationFrame(long frameTime) {}

    public AnimationHandler getAnimationHandler() {
        return AnimationHandler.getInstance();
    }

    public static int getCurrentAnimationsCount() { return 0; }

    public static interface AnimatorUpdateListener {
        void onAnimationUpdate(ValueAnimator animation);
    }
}
