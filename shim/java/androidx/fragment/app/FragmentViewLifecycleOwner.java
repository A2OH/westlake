package androidx.fragment.app;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryController;
import androidx.savedstate.SavedStateRegistryOwner;
import android.os.Bundle;

public class FragmentViewLifecycleOwner implements LifecycleOwner,
        ViewModelStoreOwner, SavedStateRegistryOwner {
    private final Fragment mFragment;
    private final LifecycleRegistry mLifecycleRegistry;
    private final Runnable mRestoreViewSavedStateRunnable;
    private ViewModelStore mViewModelStore;
    private SavedStateRegistryController mSavedStateRegistryController;
    private boolean mInitialized;

    public FragmentViewLifecycleOwner(Fragment fragment) {
        mFragment = fragment;
        mLifecycleRegistry = new LifecycleRegistry(this);
        mRestoreViewSavedStateRunnable = null;
    }

    public FragmentViewLifecycleOwner(Fragment fragment, ViewModelStore viewModelStore,
            Runnable restoreViewSavedStateRunnable) {
        mFragment = fragment;
        mLifecycleRegistry = new LifecycleRegistry(this);
        mViewModelStore = viewModelStore;
        mRestoreViewSavedStateRunnable = restoreViewSavedStateRunnable;
    }

    @Override
    public Lifecycle getLifecycle() {
        ensureSavedStateRegistryController();
        syncLifecycleFromFragment();
        return mLifecycleRegistry;
    }

    public LifecycleRegistry getLifecycleRegistry() {
        ensureSavedStateRegistryController();
        syncLifecycleFromFragment();
        return mLifecycleRegistry;
    }

    public LifecycleRegistry e() {
        ensureSavedStateRegistryController();
        syncLifecycleFromFragment();
        return mLifecycleRegistry;
    }

    public void handleLifecycleEvent(Lifecycle.Event event) {
        ensureSavedStateRegistryController();
        mLifecycleRegistry.handleLifecycleEvent(event);
    }

    public void setCurrentState(Lifecycle.State state) {
        ensureSavedStateRegistryController();
        mLifecycleRegistry.setCurrentState(state);
    }

    public void markState(Lifecycle.State state) {
        mLifecycleRegistry.markState(state);
    }

    public void a() {
        // AndroidX FragmentViewLifecycleOwner.initialize() alias in some builds.
        ensureSavedStateRegistryController();
    }

    public void a(Lifecycle.Event event) {
        handleLifecycleEvent(event);
    }

    public void b() {
        // AndroidX FragmentViewLifecycleOwner.initialize() alias.
        ensureSavedStateRegistryController();
    }

    public void b(Lifecycle.Event event) {
        handleLifecycleEvent(event);
    }

    public void c(Lifecycle.Event event) {
        handleLifecycleEvent(event);
    }

    public boolean c() {
        return mInitialized;
    }

    public void d(Lifecycle.State state) {
        setCurrentState(state);
    }

    public void d(Bundle savedState) {
        ensureSavedStateRegistryController().performRestore(savedState);
    }

    public void e(Bundle outState) {
        ensureSavedStateRegistryController().performSave(outState);
    }

    public void f(Lifecycle.Event event) {
        handleLifecycleEvent(event);
    }

    public void f(Lifecycle.State state) {
        setCurrentState(state);
    }

    public void g(Lifecycle.State state) {
        setCurrentState(state);
    }

    public void l(Lifecycle.Event event) {
        handleLifecycleEvent(event);
    }

    public void n(Lifecycle.State state) {
        setCurrentState(state);
    }

    public void o(Lifecycle.State state) {
        setCurrentState(state);
    }

    public void p(Lifecycle.State state) {
        setCurrentState(state);
    }

    public void q(Lifecycle.State state) {
        setCurrentState(state);
    }

    public void h() {
        ensureSavedStateRegistryController();
    }

    @Override
    public ViewModelStore getViewModelStore() {
        if (mViewModelStore == null) {
            try {
                mViewModelStore = mFragment != null ? mFragment.getViewModelStore() : null;
            } catch (Throwable ignored) {
            }
            if (mViewModelStore == null) {
                mViewModelStore = new ViewModelStore();
            }
        }
        return mViewModelStore;
    }

    @Override
    public SavedStateRegistry getSavedStateRegistry() {
        return ensureSavedStateRegistryController().getSavedStateRegistry();
    }

    private SavedStateRegistryController ensureSavedStateRegistryController() {
        if (mSavedStateRegistryController == null) {
            mSavedStateRegistryController = SavedStateRegistryController.create(this);
            try {
                mSavedStateRegistryController.performRestore(null);
            } catch (Throwable ignored) {
            }
            if (mRestoreViewSavedStateRunnable != null) {
                try {
                    mRestoreViewSavedStateRunnable.run();
                } catch (Throwable ignored) {
                }
            }
            mInitialized = true;
        }
        return mSavedStateRegistryController;
    }

    private void syncLifecycleFromFragment() {
        if (mFragment == null
                || mLifecycleRegistry.getCurrentState() == Lifecycle.State.DESTROYED) {
            return;
        }
        Object view = readField(mFragment, "mView");
        if (view == null) {
            view = invokeNoArg(mFragment, "getView");
        }
        if (view == null) {
            return;
        }
        int state = readIntField(mFragment, "mState", -1);
        boolean resumed = readBooleanField(mFragment, "mResumed", false);
        boolean started = readBooleanField(mFragment, "mStarted", false);
        boolean created = readBooleanField(mFragment, "mCreated", false);
        Object resumedMethod = invokeNoArg(mFragment, "isResumed");
        if (resumedMethod instanceof Boolean) {
            resumed = ((Boolean) resumedMethod).booleanValue();
        }
        Object addedMethod = invokeNoArg(mFragment, "isAdded");
        if (addedMethod instanceof Boolean && ((Boolean) addedMethod).booleanValue()) {
            created = true;
        }
        if (resumed || state >= 7) {
            moveAtLeast(Lifecycle.State.RESUMED);
        } else if (started || state >= 5) {
            moveAtLeast(Lifecycle.State.STARTED);
        } else if (created || state >= 1) {
            moveAtLeast(Lifecycle.State.CREATED);
        }
    }

    private void moveAtLeast(Lifecycle.State state) {
        if (state == null) {
            return;
        }
        Lifecycle.State current = mLifecycleRegistry.getCurrentState();
        if (current != Lifecycle.State.DESTROYED && !current.isAtLeast(state)) {
            mLifecycleRegistry.setCurrentState(state);
        }
    }

    private static Object readField(Object target, String name) {
        java.lang.reflect.Field field = findField(target, name);
        if (field == null) {
            return null;
        }
        try {
            field.setAccessible(true);
            return field.get(target);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Object invokeNoArg(Object target, String name) {
        if (target == null || name == null) {
            return null;
        }
        for (Class<?> c = target.getClass(); c != null && c != Object.class;
                c = c.getSuperclass()) {
            java.lang.reflect.Method[] methods = c.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                java.lang.reflect.Method method = methods[i];
                if (!name.equals(method.getName()) || method.getParameterTypes().length != 0) {
                    continue;
                }
                try {
                    method.setAccessible(true);
                    return method.invoke(target);
                } catch (Throwable ignored) {
                    return null;
                }
            }
        }
        return null;
    }

    private static boolean readBooleanField(Object target, String name, boolean fallback) {
        java.lang.reflect.Field field = findField(target, name);
        if (field == null || field.getType() != Boolean.TYPE) {
            return fallback;
        }
        try {
            field.setAccessible(true);
            return field.getBoolean(target);
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private static int readIntField(Object target, String name, int fallback) {
        java.lang.reflect.Field field = findField(target, name);
        if (field == null || field.getType() != Integer.TYPE) {
            return fallback;
        }
        try {
            field.setAccessible(true);
            return field.getInt(target);
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private static java.lang.reflect.Field findField(Object target, String name) {
        if (target == null || name == null) {
            return null;
        }
        for (Class<?> c = target.getClass(); c != null && c != Object.class;
                c = c.getSuperclass()) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
            }
        }
        return null;
    }
}
