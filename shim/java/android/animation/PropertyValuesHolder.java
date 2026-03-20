package android.animation;

import android.graphics.Path;
import android.util.Property;

/** Stub for AOSP compilation. */
public class PropertyValuesHolder implements Cloneable {
    String mPropertyName;
    protected Property mProperty;
    Class mValueType;
    Keyframes mKeyframes = null;
    TypeEvaluator mEvaluator;
    Object mAnimatedValue;

    private PropertyValuesHolder(String propertyName) { mPropertyName = propertyName; }
    private PropertyValuesHolder(Property property) {
        mProperty = property;
        if (property != null) mPropertyName = property.getName();
    }

    public static PropertyValuesHolder ofInt(String propertyName, int... values) {
        return new PropertyValuesHolder(propertyName);
    }
    public static PropertyValuesHolder ofInt(Property<?, Integer> property, int... values) {
        return new PropertyValuesHolder(property);
    }
    public static PropertyValuesHolder ofMultiInt(String propertyName, int[][] values) {
        return new PropertyValuesHolder(propertyName);
    }
    public static PropertyValuesHolder ofMultiInt(String propertyName, Path path) {
        return new PropertyValuesHolder(propertyName);
    }
    public static PropertyValuesHolder ofMultiInt(String propertyName,
            TypeConverter converter, TypeEvaluator evaluator, Keyframe... values) {
        return new PropertyValuesHolder(propertyName);
    }
    public static PropertyValuesHolder ofMultiInt(String propertyName,
            TypeConverter converter, TypeEvaluator evaluator, Object... values) {
        return new PropertyValuesHolder(propertyName);
    }
    public static PropertyValuesHolder ofFloat(String propertyName, float... values) {
        return new PropertyValuesHolder(propertyName);
    }
    public static PropertyValuesHolder ofFloat(Property<?, Float> property, float... values) {
        return new PropertyValuesHolder(property);
    }
    public static PropertyValuesHolder ofMultiFloat(String propertyName, float[][] values) {
        return new PropertyValuesHolder(propertyName);
    }
    public static PropertyValuesHolder ofMultiFloat(String propertyName, Path path) {
        return new PropertyValuesHolder(propertyName);
    }
    public static PropertyValuesHolder ofMultiFloat(String propertyName,
            TypeConverter converter, TypeEvaluator evaluator, Keyframe... values) {
        return new PropertyValuesHolder(propertyName);
    }
    public static PropertyValuesHolder ofMultiFloat(String propertyName,
            TypeConverter converter, TypeEvaluator evaluator, Object... values) {
        return new PropertyValuesHolder(propertyName);
    }
    public static PropertyValuesHolder ofObject(String propertyName,
            TypeEvaluator evaluator, Object... values) {
        return new PropertyValuesHolder(propertyName);
    }
    public static PropertyValuesHolder ofObject(String propertyName,
            TypeConverter converter, Path path) {
        return new PropertyValuesHolder(propertyName);
    }
    public static PropertyValuesHolder ofObject(Property property,
            TypeEvaluator evaluator, Object... values) {
        return new PropertyValuesHolder(property);
    }
    public static PropertyValuesHolder ofObject(Property property,
            TypeConverter converter, TypeEvaluator evaluator, Object... values) {
        return new PropertyValuesHolder(property);
    }
    public static PropertyValuesHolder ofObject(Property property,
            TypeConverter converter, Path path) {
        return new PropertyValuesHolder(property);
    }
    public static PropertyValuesHolder ofKeyframe(String propertyName, Keyframe... values) {
        return new PropertyValuesHolder(propertyName);
    }
    public static PropertyValuesHolder ofKeyframe(Property property, Keyframe... values) {
        return new PropertyValuesHolder(property);
    }
    public static PropertyValuesHolder ofKeyframes(String propertyName, Keyframes keyframes) {
        return new PropertyValuesHolder(propertyName);
    }
    public static PropertyValuesHolder ofKeyframes(Property property, Keyframes keyframes) {
        return new PropertyValuesHolder(property);
    }

    public void setPropertyName(String propertyName) { mPropertyName = propertyName; }
    public String getPropertyName() { return mPropertyName; }
    public void setProperty(Property property) { mProperty = property; }

    public void setIntValues(int... values) {}
    public void setFloatValues(float... values) {}
    public void setKeyframes(Keyframe... values) {}
    public void setObjectValues(Object... values) {}
    public void setEvaluator(TypeEvaluator evaluator) { mEvaluator = evaluator; }
    public void setConverter(TypeConverter converter) {}

    public Object getAnimatedValue() { return mAnimatedValue; }

    void init() {}
    void setupSetter(Class targetClass) {}
    void setupGetter(Class targetClass) {}

    @Override
    public PropertyValuesHolder clone() {
        try {
            return (PropertyValuesHolder) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
