package android.util;

/**
 * Shim: android.util.Property
 * Pure Java — abstract property accessor for named properties on objects.
 */
public abstract class Property<T, V> {

    private final Class<V> mType;
    private final String mName;

    public Property(Class<V> type, String name) {
        this.mType = type;
        this.mName = name;
    }

    public abstract V get(T object);

    public void set(T object, V value) {
        throw new UnsupportedOperationException("Property " + mName + " is read-only");
    }

    public boolean isReadOnly() {
        return true;
    }

    public String getName() {
        return mName;
    }

    public Class<V> getType() {
        return mType;
    }

    @SuppressWarnings("unchecked")
    public static <T, V> Property<T, V> of(Class<T> hostType, Class<V> valueType, String name) {
        // Return a reflection-based property
        return new Property<T, V>(valueType, name) {
            @Override
            public V get(T object) {
                try {
                    String getterName = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
                    java.lang.reflect.Method m = hostType.getMethod(getterName);
                    return (V) m.invoke(object);
                } catch (Exception e) {
                    try {
                        java.lang.reflect.Field f = hostType.getField(name);
                        return (V) f.get(object);
                    } catch (Exception e2) {
                        return null;
                    }
                }
            }

            @Override
            public void set(T object, V value) {
                try {
                    String setterName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
                    java.lang.reflect.Method m = hostType.getMethod(setterName, valueType);
                    m.invoke(object, value);
                } catch (Exception e) {
                    try {
                        java.lang.reflect.Field f = hostType.getField(name);
                        f.set(object, value);
                    } catch (Exception e2) {
                        throw new UnsupportedOperationException("Property " + name + " not settable");
                    }
                }
            }

            @Override
            public boolean isReadOnly() {
                return false;
            }
        };
    }
}
