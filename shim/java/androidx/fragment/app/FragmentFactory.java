package androidx.fragment.app;

/**
 * AndroidX FragmentFactory stub. Creates Fragment instances by class name.
 */
public class FragmentFactory {

    public FragmentFactory() {}

    /**
     * Create a new instance of the given Fragment class.
     *
     * @param classLoader The ClassLoader to use for loading the class.
     * @param className   The fully qualified class name of the Fragment to instantiate.
     * @return A new Fragment instance.
     */
    public Fragment instantiate(ClassLoader classLoader, String className) {
        try {
            Class<?> clazz = classLoader.loadClass(className);
            return (Fragment) clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate fragment " + className
                + ": make sure class exists, is public, and has an empty constructor", e);
        }
    }

    /**
     * Utility to load a Fragment class by name.
     */
    public static Class<? extends Fragment> loadFragmentClass(
            ClassLoader classLoader, String className) {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Fragment> clazz =
                (Class<? extends Fragment>) classLoader.loadClass(className);
            return clazz;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to load fragment class " + className, e);
        }
    }
}
