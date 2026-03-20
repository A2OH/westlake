package android.animation;

import java.util.List;

/** Stub for AOSP compilation. Interface for keyframe collections. */
public interface Keyframes extends Cloneable {
    void setEvaluator(TypeEvaluator evaluator);
    Class getType();
    Object getValue(float fraction);
    List<Keyframe> getKeyframes();
    Keyframes clone();

    /** Keyframes that return int values. */
    public interface IntKeyframes extends Keyframes {
        int getIntValue(float fraction);
    }

    /** Keyframes that return float values. */
    public interface FloatKeyframes extends Keyframes {
        float getFloatValue(float fraction);
    }
}
