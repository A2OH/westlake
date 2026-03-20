package android.animation;

/** Stub for AOSP compilation. Two-way type converter for animations. */
public abstract class BidirectionalTypeConverter<T, V> extends TypeConverter<T, V> {
    public BidirectionalTypeConverter(Class<T> fromClass, Class<V> toClass) {
        super(fromClass, toClass);
    }

    public abstract T convertBack(V value);

    public BidirectionalTypeConverter<V, T> invert() {
        return null;
    }
}
