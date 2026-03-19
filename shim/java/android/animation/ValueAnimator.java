package android.animation;

public class ValueAnimator extends Animator {
    public ValueAnimator() {}

    public static final int INFINITE = 0;
    public static final int RESTART = 0;
    public static final int REVERSE = 0;

    public interface AnimatorUpdateListener {
        void onAnimationUpdate(ValueAnimator animation);
    }

    public void addUpdateListener(AnimatorUpdateListener listener) {}
    public void addUpdateListener(Object p0) {}
    public static boolean areAnimatorsEnabled() { return false; }
    public ValueAnimator clone() { return null; }
    public float getAnimatedFraction() { return 0f; }
    public Object getAnimatedValue() { return null; }
    public Object getAnimatedValue(Object p0) { return null; }
    public long getCurrentPlayTime() { return 0L; }
    public long getDuration() { return 0L; }
    public static long getFrameDelay() { return 0L; }
    public TimeInterpolator getInterpolator() { return null; }
    public int getRepeatCount() { return 0; }
    public int getRepeatMode() { return 0; }
    public long getStartDelay() { return 0L; }
    public Object getValues() { return null; }
    public boolean isRunning() { return false; }
    public static ValueAnimator ofArgb(int... values) { return new ValueAnimator(); }
    public static ValueAnimator ofFloat(float... values) { return new ValueAnimator(); }
    public static ValueAnimator ofInt(int... values) { return new ValueAnimator(); }
    public static ValueAnimator ofObject(TypeEvaluator evaluator, Object... values) { return new ValueAnimator(); }
    public static ValueAnimator ofPropertyValuesHolder(PropertyValuesHolder... values) { return new ValueAnimator(); }
    public void removeAllUpdateListeners() {}
    public void removeUpdateListener(AnimatorUpdateListener listener) {}
    public void removeUpdateListener(Object p0) {}
    public void reverse() {}
    public void setCurrentFraction(Object p0) {}
    public void setCurrentPlayTime(Object p0) {}
    public ValueAnimator setDuration(long duration) { return this; }
    public void setEvaluator(Object p0) {}
    public void setFloatValues(float... values) {}
    public static void setFrameDelay(Object p0) {}
    public void setIntValues(int... values) {}
    public void setInterpolator(Object p0) {}
    public void setObjectValues(Object... values) {}
    public void setRepeatCount(int value) {}
    public void setRepeatMode(int value) {}
    public void setStartDelay(long startDelay) {}
    public void setValues(PropertyValuesHolder... values) {}
}
