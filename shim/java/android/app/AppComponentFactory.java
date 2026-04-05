package android.app;

import android.content.Intent;

/**
 * AppComponentFactory -- controls instantiation of manifest elements.
 *
 * Mirrors AOSP's android.app.AppComponentFactory. This is the hook that
 * dependency injection frameworks (Hilt, Dagger) use to inject dependencies
 * into Activities, Services, and other components at creation time.
 *
 * Subclasses override instantiateActivity() etc. to perform injection before
 * the component's lifecycle methods are called. The default implementation
 * simply uses reflection to create instances.
 *
 * In Hilt apps, the generated HiltAppComponentFactory subclass is declared
 * in AndroidManifest.xml via android:appComponentFactory="...".
 */
public class AppComponentFactory {

    public AppComponentFactory() {}

    /**
     * Create an Activity instance. Hilt overrides this to inject dependencies.
     *
     * @param cl        The ClassLoader to use.
     * @param className The fully-qualified Activity class name.
     * @param intent    The launching Intent (may be null).
     * @return A new Activity instance.
     */
    public Activity instantiateActivity(ClassLoader cl, String className, Intent intent)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        try {
            return (Activity) cl.loadClass(className).getDeclaredConstructor().newInstance();
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) throw (RuntimeException) cause;
            if (cause instanceof Error) throw (Error) cause;
            throw new InstantiationException(className + ": " + cause);
        } catch (NoSuchMethodException e) {
            // No zero-arg constructor via getDeclaredConstructor, fall back to newInstance
            return (Activity) cl.loadClass(className).newInstance();
        }
    }

    /**
     * Create an Application instance. Hilt overrides this to inject dependencies.
     *
     * @param cl        The ClassLoader to use.
     * @param className The fully-qualified Application class name.
     * @return A new Application instance.
     */
    public Application instantiateApplication(ClassLoader cl, String className)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        try {
            return (Application) cl.loadClass(className).getDeclaredConstructor().newInstance();
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) throw (RuntimeException) cause;
            if (cause instanceof Error) throw (Error) cause;
            throw new InstantiationException(className + ": " + cause);
        } catch (NoSuchMethodException e) {
            return (Application) cl.loadClass(className).newInstance();
        }
    }

    /**
     * Create a Service instance.
     *
     * @param cl        The ClassLoader to use.
     * @param className The fully-qualified Service class name.
     * @param intent    The launching Intent (may be null).
     * @return A new Service instance.
     */
    public Service instantiateService(ClassLoader cl, String className, Intent intent)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        try {
            return (Service) cl.loadClass(className).getDeclaredConstructor().newInstance();
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) throw (RuntimeException) cause;
            if (cause instanceof Error) throw (Error) cause;
            throw new InstantiationException(className + ": " + cause);
        } catch (NoSuchMethodException e) {
            return (Service) cl.loadClass(className).newInstance();
        }
    }

    /**
     * Create a BroadcastReceiver instance.
     *
     * @param cl        The ClassLoader to use.
     * @param className The fully-qualified BroadcastReceiver class name.
     * @param intent    The launching Intent (may be null).
     * @return A new BroadcastReceiver instance.
     */
    public android.content.BroadcastReceiver instantiateReceiver(
            ClassLoader cl, String className, Intent intent)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        try {
            return (android.content.BroadcastReceiver)
                    cl.loadClass(className).getDeclaredConstructor().newInstance();
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) throw (RuntimeException) cause;
            if (cause instanceof Error) throw (Error) cause;
            throw new InstantiationException(className + ": " + cause);
        } catch (NoSuchMethodException e) {
            return (android.content.BroadcastReceiver) cl.loadClass(className).newInstance();
        }
    }

    /**
     * Create a ContentProvider instance.
     *
     * @param cl        The ClassLoader to use.
     * @param className The fully-qualified ContentProvider class name.
     * @return A new ContentProvider instance.
     */
    public android.content.ContentProvider instantiateProvider(
            ClassLoader cl, String className)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        try {
            return (android.content.ContentProvider)
                    cl.loadClass(className).getDeclaredConstructor().newInstance();
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) throw (RuntimeException) cause;
            if (cause instanceof Error) throw (Error) cause;
            throw new InstantiationException(className + ": " + cause);
        } catch (NoSuchMethodException e) {
            return (android.content.ContentProvider) cl.loadClass(className).newInstance();
        }
    }

    /** Default factory instance. Used when no custom factory is configured. */
    public static final AppComponentFactory DEFAULT = new AppComponentFactory();
}
