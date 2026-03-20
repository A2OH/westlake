package android.animation;

import android.graphics.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Stub for AOSP compilation. A set of Keyframes for animation. */
public class KeyframeSet implements Keyframes {
    private List<Keyframe> mKeyframes;
    private TypeEvaluator mEvaluator;

    public KeyframeSet(Keyframe... keyframes) {
        mKeyframes = new ArrayList<Keyframe>(Arrays.asList(keyframes));
    }

    public static KeyframeSet ofInt(int... values) {
        int numKeyframes = values.length;
        Keyframe[] keyframes = new Keyframe[Math.max(numKeyframes, 2)];
        if (numKeyframes == 1) {
            keyframes[0] = Keyframe.ofInt(0f);
            keyframes[1] = Keyframe.ofInt(1f, values[0]);
        } else {
            for (int i = 0; i < numKeyframes; ++i) {
                keyframes[i] = Keyframe.ofInt((float) i / (numKeyframes - 1), values[i]);
            }
        }
        return new KeyframeSet(keyframes);
    }

    public static KeyframeSet ofFloat(float... values) {
        int numKeyframes = values.length;
        Keyframe[] keyframes = new Keyframe[Math.max(numKeyframes, 2)];
        if (numKeyframes == 1) {
            keyframes[0] = Keyframe.ofFloat(0f);
            keyframes[1] = Keyframe.ofFloat(1f, values[0]);
        } else {
            for (int i = 0; i < numKeyframes; ++i) {
                keyframes[i] = Keyframe.ofFloat((float) i / (numKeyframes - 1), values[i]);
            }
        }
        return new KeyframeSet(keyframes);
    }

    public static KeyframeSet ofKeyframe(Keyframe... keyframes) {
        return new KeyframeSet(keyframes);
    }

    public static KeyframeSet ofObject(Object... values) {
        int numKeyframes = values.length;
        Keyframe[] keyframes = new Keyframe[Math.max(numKeyframes, 2)];
        if (numKeyframes == 1) {
            keyframes[0] = Keyframe.ofObject(0f);
            keyframes[1] = Keyframe.ofObject(1f, values[0]);
        } else {
            for (int i = 0; i < numKeyframes; ++i) {
                keyframes[i] = Keyframe.ofObject((float) i / (numKeyframes - 1), values[i]);
            }
        }
        return new KeyframeSet(keyframes);
    }

    public static PathKeyframes ofPath(Path path) {
        return new PathKeyframes(path);
    }

    public void setEvaluator(TypeEvaluator evaluator) {
        mEvaluator = evaluator;
    }

    public Class getType() {
        if (mKeyframes != null && !mKeyframes.isEmpty()) {
            return mKeyframes.get(0).getType();
        }
        return Object.class;
    }

    public Object getValue(float fraction) {
        if (mKeyframes == null || mKeyframes.isEmpty()) return null;
        if (mKeyframes.size() == 1) return mKeyframes.get(0).getValue();
        // Simple linear: return first or last
        if (fraction <= 0f) return mKeyframes.get(0).getValue();
        if (fraction >= 1f) return mKeyframes.get(mKeyframes.size() - 1).getValue();
        // For stub, return last keyframe value
        return mKeyframes.get(mKeyframes.size() - 1).getValue();
    }

    public List<Keyframe> getKeyframes() {
        return mKeyframes;
    }

    public KeyframeSet clone() {
        try {
            KeyframeSet newSet = (KeyframeSet) super.clone();
            newSet.mKeyframes = new ArrayList<Keyframe>(mKeyframes);
            return newSet;
        } catch (CloneNotSupportedException e) {
            return new KeyframeSet();
        }
    }
}
