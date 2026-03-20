package android.content.res;

/** Stub for AOSP compilation. */
public class ConfigurationBoundResourceCache<T> {
    public ConfigurationBoundResourceCache() {}
    public T getInstance(long key, Resources resources, Resources.Theme theme) { return null; }
    public void put(long key, Resources.Theme theme, ConstantState<T> constantState) {}
    public void onConfigurationChange(int configChanges) {}
}
