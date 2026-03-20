package android.animation;

import android.graphics.Path;
import android.graphics.PointF;
import java.util.ArrayList;
import java.util.List;

/** Stub for AOSP compilation. Keyframes derived from a Path. */
public class PathKeyframes implements Keyframes {
    public PathKeyframes(Path path) {}
    public PathKeyframes(Path path, float error) {}

    public void setEvaluator(TypeEvaluator evaluator) {}
    public Class getType() { return android.graphics.PointF.class; }
    public Object getValue(float fraction) {
        return new PointF(0, 0);
    }
    public List<Keyframe> getKeyframes() {
        return new ArrayList<Keyframe>();
    }
    public PathKeyframes clone() {
        try { return (PathKeyframes) super.clone(); }
        catch (CloneNotSupportedException e) { return new PathKeyframes(new Path()); }
    }

    /** Float X keyframes from a path. */
    public static class FloatKeyframesBase extends SimpleKeyframes implements Keyframes.FloatKeyframes {
        public float getFloatValue(float fraction) { return 0f; }
    }

    /** Int keyframes from a path. */
    public static class IntKeyframesBase extends SimpleKeyframes implements Keyframes.IntKeyframes {
        public int getIntValue(float fraction) { return 0; }
    }

    static class SimpleKeyframes implements Keyframes {
        public void setEvaluator(TypeEvaluator evaluator) {}
        public Class getType() { return Object.class; }
        public Object getValue(float fraction) { return null; }
        public List<Keyframe> getKeyframes() { return new ArrayList<Keyframe>(); }
        public SimpleKeyframes clone() {
            try { return (SimpleKeyframes) super.clone(); }
            catch (CloneNotSupportedException e) { return new SimpleKeyframes(); }
        }
    }
}
