package dagger.hilt;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public final class EntryPoints {
    private static void log(String message) {
        try {
            java.io.PrintStream err = System.err;
            if (err != null) err.println(message);
        } catch (Throwable ignored) {
        }
    }

    private static final InvocationHandler STUB_HANDLER = new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            if ("toString".equals(name)) return "EntryPointProxy";
            if ("hashCode".equals(name)) return 0;
            if ("equals".equals(name)) return false;
            Class<?> rt = method.getReturnType();
            if (rt == void.class) return null;
            if (rt == boolean.class) return false;
            if (rt == int.class) return 0;
            if (rt == long.class) return 0L;
            if (rt == float.class) return 0.0f;
            if (rt == double.class) return 0.0;
            if (rt == String.class) return "";
            // For interface return types, return a nested proxy
            if (rt.isInterface()) {
                try {
                    return Proxy.newProxyInstance(rt.getClassLoader(), new Class<?>[]{ rt }, this);
                } catch (Throwable t) { return null; }
            }
            return null;
        }
    };

    private EntryPoints() {}

    @SuppressWarnings("unchecked")
    public static <T> T get(Object component, Class<T> entryPoint) {
        log("[EntryPoints] get(" + (component != null ? component.getClass().getSimpleName() : "null") + ", " + entryPoint.getSimpleName() + ")");
        // Extract the real DI component from the Application/holder
        Object realComponent = component;
        // Try generatedComponent() (unobfuscated)
        realComponent = tryExtractComponent(component, "generatedComponent");
        // Try obfuscated names: a(), b(), c()
        if (realComponent == component) realComponent = tryExtractComponent(component, "a");
        if (realComponent == component) realComponent = tryExtractComponent(component, "b");
        // Try componentManager().generatedComponent() chain
        if (realComponent == component) {
            Object cm = tryCallMethod(component, "componentManager");
            if (cm == null) cm = tryCallMethod(component, "a");
            if (cm != null) {
                Object gc = tryCallMethod(cm, "generatedComponent");
                if (gc == null) gc = tryCallMethod(cm, "a");
                if (gc != null) realComponent = gc;
            }
        }
        // Also try the static singleton component directly
        if (realComponent == component || realComponent == null) {
            Object singleton = dagger.hilt.android.internal.managers.ApplicationComponentManager.singletonComponent;
            if (singleton != null) realComponent = singleton;
        }

        log("[EntryPoints] realComponent=" + (realComponent != null ? realComponent.getClass().getSimpleName() : "null")
                + " isInstance=" + (realComponent != null && entryPoint.isInstance(realComponent)));

        if (realComponent != null && entryPoint.isInstance(realComponent)) {
            return (T) realComponent;
        }
        // Create proxy for the interface
        try {
            if (entryPoint.isInterface()) {
                // Use the entryPoint's classloader (app DEX) — component may have boot classloader
                ClassLoader cl = entryPoint.getClassLoader();
                if (cl == null) cl = component != null ? component.getClass().getClassLoader() : ClassLoader.getSystemClassLoader();
                return (T) Proxy.newProxyInstance(cl, new Class<?>[]{ entryPoint }, STUB_HANDLER);
            }
        } catch (Exception e) {
            log("[EntryPoints] Proxy failed: " + e);
        }
        log("[EntryPoints] WARNING: returning null for " + entryPoint.getName());
        return null;
    }

    private static Object tryExtractComponent(Object obj, String methodName) {
        try {
            Method m = obj.getClass().getMethod(methodName);
            if (m.getParameterTypes().length == 0) {
                Object result = m.invoke(obj);
                if (result != null) return result;
            }
        } catch (Throwable t) { /* not found */ }
        return obj;
    }

    private static Object tryCallMethod(Object obj, String methodName) {
        try {
            Method m = obj.getClass().getMethod(methodName);
            if (m.getParameterTypes().length == 0) return m.invoke(obj);
        } catch (Throwable t) { /* not found */ }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Object a(Object component, Class<?> entryPoint) {
        return get(component, entryPoint);
    }
}
