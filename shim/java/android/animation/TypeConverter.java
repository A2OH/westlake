package android.animation;

/** Stub for AOSP compilation. Converts between value types in animations. */
public abstract class TypeConverter<T, V> {
    private Class<T> mFromClass;
    private Class<V> mToClass;

    public TypeConverter(Class<T> fromClass, Class<V> toClass) {
        mFromClass = fromClass;
        mToClass = toClass;
    }

    public Class<T> getSourceType() { return mFromClass; }
    public Class<V> getTargetType() { return mToClass; }

    public abstract V convert(T value);
}
