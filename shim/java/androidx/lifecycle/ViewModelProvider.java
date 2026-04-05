package androidx.lifecycle;

public class ViewModelProvider {
    public interface Factory {
        <T extends ViewModel> T create(Class<T> modelClass);
    }

    private final Factory mFactory;

    public ViewModelProvider(ViewModelStoreOwner owner) {
        mFactory = new Factory() {
            @Override public <T extends ViewModel> T create(Class<T> c) {
                try { return c.newInstance(); } catch (Exception e) { throw new RuntimeException(e); }
            }
        };
    }

    public ViewModelProvider(ViewModelStoreOwner owner, Factory factory) {
        mFactory = factory;
    }

    public <T extends ViewModel> T get(Class<T> modelClass) {
        return mFactory.create(modelClass);
    }

    /** Obfuscated alias (R8 renames get → a) */
    public <T extends ViewModel> T a(Class<T> modelClass) {
        return get(modelClass);
    }

    /** get(String, Class) variant */
    public <T extends ViewModel> T get(String key, Class<T> modelClass) {
        return get(modelClass);
    }
}
