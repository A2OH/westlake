package android.animation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Stub for AOSP compilation. */
public final class AnimatorSet extends Animator {
    private long mDuration = -1;
    private long mStartDelay = 0;
    private TimeInterpolator mInterpolator;

    public AnimatorSet() {}

    public void playTogether(Animator... items) {}
    public void playTogether(Collection<Animator> items) {}
    public void playSequentially(Animator... items) {}
    public void playSequentially(List<Animator> items) {}

    public ArrayList<Animator> getChildAnimations() { return new ArrayList<>(); }

    @Override
    public void setTarget(Object target) {}

    public void setInterpolator(TimeInterpolator interpolator) { mInterpolator = interpolator; }
    public TimeInterpolator getInterpolator() { return mInterpolator; }

    public Builder play(Animator anim) { return new Builder(anim); }

    @Override
    public void cancel() {}
    @Override
    public void end() {}
    @Override
    public boolean isRunning() { return false; }
    @Override
    public boolean isStarted() { return false; }
    @Override
    public long getStartDelay() { return mStartDelay; }
    @Override
    public void setStartDelay(long startDelay) { mStartDelay = startDelay; }
    @Override
    public AnimatorSet setDuration(long duration) { mDuration = duration; return this; }
    @Override
    public long getDuration() { return mDuration; }
    @Override
    public void setupStartValues() {}
    @Override
    public void setupEndValues() {}
    @Override
    public void start() {}
    @Override
    public AnimatorSet clone() { return (AnimatorSet) super.clone(); }
    @Override
    public boolean canReverse() { return false; }
    @Override
    public void reverse() {}

    public void setCurrentPlayTime(long playTime) {}
    public long getCurrentPlayTime() { return 0; }

    public class Builder {
        private Animator mCurrentNode;
        Builder(Animator anim) { mCurrentNode = anim; }
        public Builder with(Animator anim) { return this; }
        public Builder before(Animator anim) { return this; }
        public Builder after(Animator anim) { return this; }
        public Builder after(long delay) { return this; }
    }
}
