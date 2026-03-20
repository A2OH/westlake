package android.animation;

import android.graphics.Path;
import android.util.Property;

import java.lang.ref.WeakReference;

/** Stub for AOSP compilation. */
public final class ObjectAnimator extends ValueAnimator {
    private WeakReference<Object> mTarget;
    private String mPropertyName;
    private Property mProperty;

    public ObjectAnimator() {}

    public void setTarget(Object target) { mTarget = new WeakReference<>(target); }
    public Object getTarget() { return mTarget != null ? mTarget.get() : null; }

    public void setPropertyName(String propertyName) { mPropertyName = propertyName; }
    public String getPropertyName() { return mPropertyName; }
    public void setProperty(Property property) { mProperty = property; }
    public Property getProperty() { return mProperty; }

    public void setAutoCancel(boolean cancel) {}

    public static ObjectAnimator ofInt(Object target, String propertyName, int... values) {
        ObjectAnimator anim = new ObjectAnimator();
        anim.setTarget(target);
        anim.setPropertyName(propertyName);
        anim.setIntValues(values);
        return anim;
    }

    public static ObjectAnimator ofInt(Object target, Property<?, Integer> property, int... values) {
        ObjectAnimator anim = new ObjectAnimator();
        anim.setTarget(target);
        anim.setProperty(property);
        anim.setIntValues(values);
        return anim;
    }

    public static ObjectAnimator ofInt(Object target, String xPropertyName, String yPropertyName,
            Path path) {
        return new ObjectAnimator();
    }

    public static ObjectAnimator ofInt(Object target, Property<?, Integer> xProperty,
            Property<?, Integer> yProperty, Path path) {
        return new ObjectAnimator();
    }

    public static ObjectAnimator ofMultiInt(Object target, String propertyName, int[][] values) {
        return new ObjectAnimator();
    }

    public static ObjectAnimator ofMultiInt(Object target, String propertyName, Path path) {
        return new ObjectAnimator();
    }

    public static ObjectAnimator ofMultiInt(Object target, String propertyName,
            TypeConverter converter, TypeEvaluator evaluator, Object... values) {
        return new ObjectAnimator();
    }

    public static ObjectAnimator ofFloat(Object target, String propertyName, float... values) {
        ObjectAnimator anim = new ObjectAnimator();
        anim.setTarget(target);
        anim.setPropertyName(propertyName);
        anim.setFloatValues(values);
        return anim;
    }

    public static ObjectAnimator ofFloat(Object target, Property<?, Float> property,
            float... values) {
        ObjectAnimator anim = new ObjectAnimator();
        anim.setTarget(target);
        anim.setProperty(property);
        anim.setFloatValues(values);
        return anim;
    }

    public static ObjectAnimator ofFloat(Object target, String xPropertyName, String yPropertyName,
            Path path) {
        return new ObjectAnimator();
    }

    public static ObjectAnimator ofFloat(Object target, Property<?, Float> xProperty,
            Property<?, Float> yProperty, Path path) {
        return new ObjectAnimator();
    }

    public static ObjectAnimator ofMultiFloat(Object target, String propertyName,
            float[][] values) {
        return new ObjectAnimator();
    }

    public static ObjectAnimator ofMultiFloat(Object target, String propertyName, Path path) {
        return new ObjectAnimator();
    }

    public static ObjectAnimator ofMultiFloat(Object target, String propertyName,
            TypeConverter converter, TypeEvaluator evaluator, Object... values) {
        return new ObjectAnimator();
    }

    public static ObjectAnimator ofObject(Object target, String propertyName,
            TypeEvaluator evaluator, Object... values) {
        ObjectAnimator anim = new ObjectAnimator();
        anim.setTarget(target);
        anim.setPropertyName(propertyName);
        return anim;
    }

    public static ObjectAnimator ofObject(Object target, Property property,
            TypeEvaluator evaluator, Object... values) {
        ObjectAnimator anim = new ObjectAnimator();
        anim.setTarget(target);
        anim.setProperty(property);
        return anim;
    }

    public static ObjectAnimator ofObject(Object target, String propertyName,
            TypeConverter converter, Path path) {
        return new ObjectAnimator();
    }

    public static ObjectAnimator ofObject(Object target, Property property,
            TypeConverter converter, TypeEvaluator evaluator, Object... values) {
        return new ObjectAnimator();
    }

    public static ObjectAnimator ofObject(Object target, Property property,
            TypeConverter converter, Path path) {
        return new ObjectAnimator();
    }

    public static ObjectAnimator ofPropertyValuesHolder(Object target,
            PropertyValuesHolder... values) {
        ObjectAnimator anim = new ObjectAnimator();
        anim.setTarget(target);
        anim.setValues(values);
        return anim;
    }

    @Override
    public ObjectAnimator setDuration(long duration) {
        super.setDuration(duration);
        return this;
    }

    @Override
    public ObjectAnimator clone() {
        return (ObjectAnimator) super.clone();
    }
}
