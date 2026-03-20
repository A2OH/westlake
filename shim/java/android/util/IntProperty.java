package android.util;

/** Stub for AOSP compilation. Property wrapper for int fields. */
public abstract class IntProperty<T> extends Property<T, Integer> {
    public IntProperty(String name) {
        super(Integer.class, name);
    }

    public abstract void setValue(T object, int value);

    @Override
    public final void set(T object, Integer value) {
        setValue(object, value != null ? value : 0);
    }
}
