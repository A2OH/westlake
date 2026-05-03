package androidx.lifecycle;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class LifecycleRegistry extends Lifecycle {
    private State mState = State.INITIALIZED;
    private final List<LifecycleObserver> mObservers = new ArrayList<>();
    private final Map<LifecycleObserver, State> mObserverStates = new IdentityHashMap<>();
    private final LifecycleOwner mOwner;

    public LifecycleRegistry(LifecycleOwner owner) { mOwner = owner; }
    public LifecycleRegistry(LifecycleOwner owner, boolean enforceMainThread) { this(owner); }

    @Override public void addObserver(LifecycleObserver observer) {
        if (observer == null) {
            return;
        }
        if (!mObservers.contains(observer)) {
            mObservers.add(observer);
        }
        if (!mObserverStates.containsKey(observer)) {
            mObserverStates.put(observer,
                    getCurrentState() == State.DESTROYED ? State.DESTROYED : State.INITIALIZED);
        }
        syncObserver(observer);
    }
    @Override public void removeObserver(LifecycleObserver observer) {
        mObservers.remove(observer);
        mObserverStates.remove(observer);
    }
    @Override public State getCurrentState() {
        return mState != null ? mState : State.INITIALIZED;
    }

    public void setCurrentState(State state) { moveToState(state); }
    public void handleLifecycleEvent(Event event) {
        if (event == null) {
            return;
        }
        switch (event) {
            case ON_CREATE: mState = State.CREATED; break;
            case ON_START: mState = State.STARTED; break;
            case ON_RESUME: mState = State.RESUMED; break;
            case ON_PAUSE: mState = State.STARTED; break;
            case ON_STOP: mState = State.CREATED; break;
            case ON_DESTROY: mState = State.DESTROYED; break;
            default: break;
        }
        updateOwnerLifecycleSurface(event);
        dispatchEvent(event);
    }
    public void markState(State state) { moveToState(state); }
    public int getObserverCount() { return mObservers.size(); }

    // AndroidX-obfuscated aliases used by app-shipped activity/fragment code.
    public void a(LifecycleObserver observer) { addObserver(observer); }
    public State b() { return getCurrentState(); }
    public void c(LifecycleObserver observer) { addObserver(observer); }
    public State d() { return getCurrentState(); }
    public State e() { return getCurrentState(); }
    public void g(LifecycleObserver observer) { removeObserver(observer); }
    public void h(Event event) { handleLifecycleEvent(event); }
    public void i(Event event) { handleLifecycleEvent(event); }
    public void l(Event event) { handleLifecycleEvent(event); }
    public void m(Event event) { handleLifecycleEvent(event); }
    public void n(State state) { markState(state); }
    public void o(State state) { markState(state); }
    public void p(State state) { markState(state); }
    public void q(State state) { setCurrentState(state); }
    public void r() {
        Event event = eventForCurrentState(getCurrentState());
        updateOwnerLifecycleSurface(event);
        dispatchEvent(event);
        syncAllObservers();
    }
    public void s() {
        Event event = eventForCurrentState(getCurrentState());
        updateOwnerLifecycleSurface(event);
        dispatchEvent(event);
        syncAllObservers();
    }

    private void moveToState(State state) {
        if (state == null) {
            return;
        }
        State current = getCurrentState();
        if (current == state) {
            mState = state;
            Event event = eventForCurrentState(state);
            updateOwnerLifecycleSurface(event);
            dispatchEvent(event);
            syncAllObservers();
            return;
        }
        if (state == State.DESTROYED) {
            handleLifecycleEvent(Event.ON_DESTROY);
            return;
        }
        if (current == State.DESTROYED) {
            mState = State.INITIALIZED;
            current = mState;
        }
        while (current.compareTo(state) < 0) {
            Event event = Event.upFrom(current);
            if (event == null) {
                mState = state;
                syncAllObservers();
                return;
            }
            handleLifecycleEvent(event);
            current = getCurrentState();
        }
        while (current.compareTo(state) > 0) {
            Event event = Event.downFrom(current);
            if (event == null) {
                mState = state;
                syncAllObservers();
                return;
            }
            handleLifecycleEvent(event);
            current = getCurrentState();
        }
    }

    private void syncAllObservers() {
        if (mObservers.isEmpty()) {
            return;
        }
        List<LifecycleObserver> snapshot = new ArrayList<>(mObservers);
        for (int i = 0; i < snapshot.size(); i++) {
            LifecycleObserver observer = snapshot.get(i);
            if (observer != null && mObservers.contains(observer)) {
                syncObserver(observer);
            }
        }
    }

    private void syncObserver(LifecycleObserver observer) {
        if (observer == null || !mObservers.contains(observer)) {
            return;
        }
        State target = getCurrentState();
        State observerState = mObserverStates.containsKey(observer)
                ? mObserverStates.get(observer)
                : (target == State.DESTROYED ? State.DESTROYED : State.INITIALIZED);
        if (observerState == null) {
            observerState = State.INITIALIZED;
        }
        while (observerState.compareTo(target) < 0 && mObservers.contains(observer)) {
            Event event = Event.upFrom(observerState);
            if (event == null || event == Event.ON_ANY) {
                mObserverStates.put(observer, target);
                return;
            }
            dispatchEventToObserver(observer, event);
            observerState = event.getTargetState();
            mObserverStates.put(observer, observerState);
        }
        while (observerState.compareTo(target) > 0 && mObservers.contains(observer)) {
            Event event = Event.downFrom(observerState);
            if (event == null || event == Event.ON_ANY) {
                mObserverStates.put(observer, target);
                return;
            }
            dispatchEventToObserver(observer, event);
            observerState = event.getTargetState();
            mObserverStates.put(observer, observerState);
        }
    }

    private void dispatchEvent(Event event) {
        if (event == null || event == Event.ON_ANY || mObservers.isEmpty()) {
            return;
        }
        List<LifecycleObserver> snapshot = new ArrayList<>(mObservers);
        for (int i = 0; i < snapshot.size(); i++) {
            LifecycleObserver observer = snapshot.get(i);
            if (observer != null && mObservers.contains(observer)) {
                dispatchEventToObserver(observer, event);
                mObserverStates.put(observer, event.getTargetState());
            }
        }
    }

    private void dispatchEventToObserver(LifecycleObserver observer, Event event) {
        if (observer == null || event == null) {
            return;
        }
        if (invokeObserverMethod(observer, "f", mOwner, event)) {
            return;
        }
        if (invokeObserverMethod(observer, "onStateChanged", mOwner, event)) {
            return;
        }
        if (invokeObserverMethod(observer, "d", mOwner, event)
                || invokeObserverMethod(observer, "a", mOwner, event)
                || invokeObserverMethod(observer, "b", mOwner, event)) {
            return;
        }
        invokeObserverMethod(observer, defaultObserverMethodName(event), mOwner);
    }

    private void updateOwnerLifecycleSurface(Event event) {
        if (mOwner == null || event == null || event == Event.ON_ANY) {
            return;
        }
        String ownerName = null;
        try {
            ownerName = mOwner.getClass().getName();
        } catch (Throwable ignored) {
        }
        boolean fragmentOwner = mOwner instanceof androidx.fragment.app.Fragment
                || (ownerName != null
                && ownerName.indexOf("androidx.fragment.app.Fragment") >= 0
                && ownerName.indexOf("FragmentViewLifecycleOwner") < 0);
        if (fragmentOwner) {
            if (event == Event.ON_START || event == Event.ON_RESUME) {
                setBooleanField(mOwner, "mStarted", true);
                setBooleanField(mOwner, "mAdded", true);
            } else if (event == Event.ON_STOP || event == Event.ON_DESTROY) {
                setBooleanField(mOwner, "mStarted", false);
            }
            if (event == Event.ON_RESUME) {
                setBooleanField(mOwner, "mResumed", true);
                setIntField(mOwner, "mState", 7);
            } else if (event == Event.ON_PAUSE || event == Event.ON_STOP
                    || event == Event.ON_DESTROY) {
                setBooleanField(mOwner, "mResumed", false);
            }
            if (event == Event.ON_START) {
                setIntField(mOwner, "mState", 5);
            } else if (event == Event.ON_CREATE) {
                setIntField(mOwner, "mState", 1);
            } else if (event == Event.ON_STOP) {
                setIntField(mOwner, "mState", 4);
            } else if (event == Event.ON_DESTROY) {
                setIntField(mOwner, "mState", 0);
            }
            dispatchFragmentViewLifecycleOwner(event);
        }
    }

    private static Event eventForCurrentState(State state) {
        if (state == null) {
            return null;
        }
        switch (state) {
            case CREATED:
                return Event.ON_CREATE;
            case STARTED:
                return Event.ON_START;
            case RESUMED:
                return Event.ON_RESUME;
            case DESTROYED:
                return Event.ON_DESTROY;
            default:
                return null;
        }
    }

    private void dispatchFragmentViewLifecycleOwner(Event event) {
        Object viewOwner = invokeNoArg(mOwner, "getViewLifecycleOwner");
        if (viewOwner == null || viewOwner == mOwner) {
            return;
        }
        invokeNoArg(viewOwner, "b");
        invokeNoArg(viewOwner, "a");
        Object lifecycle = invokeNoArg(viewOwner, "getLifecycle");
        if (lifecycle == null || lifecycle == this) {
            return;
        }
        boolean dispatched = invokeOneArg(lifecycle, "handleLifecycleEvent", event)
                || invokeOneArg(lifecycle, "l", event);
        if (!dispatched) {
            dispatched = invokeOneArg(viewOwner, "handleLifecycleEvent", event)
                    || invokeOneArg(viewOwner, "a", event)
                    || invokeOneArg(viewOwner, "b", event)
                    || invokeOneArg(viewOwner, "c", event)
                    || invokeOneArg(viewOwner, "f", event)
                    || invokeOneArg(viewOwner, "l", event);
        }
        if (dispatched) {
            return;
        }
        State state = event.getTargetState();
        invokeOneArg(lifecycle, "setCurrentState", state);
        invokeOneArg(lifecycle, "markState", state);
        invokeOneArg(lifecycle, "q", state);
        invokeOneArg(lifecycle, "n", state);
        invokeOneArg(lifecycle, "o", state);
        invokeOneArg(lifecycle, "p", state);
        invokeOneArg(viewOwner, "d", state);
        invokeOneArg(viewOwner, "f", state);
        invokeOneArg(viewOwner, "g", state);
        invokeOneArg(viewOwner, "q", state);
    }

    private static Object invokeNoArg(Object target, String name) {
        if (target == null || name == null) {
            return null;
        }
        for (Class<?> c = target.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                java.lang.reflect.Method method = c.getDeclaredMethod(name);
                method.setAccessible(true);
                return method.invoke(target);
            } catch (NoSuchMethodException ignored) {
            } catch (Throwable ignored) {
                return null;
            }
        }
        return null;
    }

    private static boolean invokeOneArg(Object target, String name, Object arg) {
        if (target == null || name == null || arg == null) {
            return false;
        }
        for (Class<?> c = target.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            java.lang.reflect.Method[] methods = c.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                java.lang.reflect.Method method = methods[i];
                if (!name.equals(method.getName())) {
                    continue;
                }
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 1 || !params[0].isAssignableFrom(arg.getClass())) {
                    continue;
                }
                try {
                    method.setAccessible(true);
                    method.invoke(target, arg);
                    return true;
                } catch (Throwable ignored) {
                }
            }
        }
        return false;
    }

    private static boolean setBooleanField(Object target, String name, boolean value) {
        java.lang.reflect.Field field = findField(target, name);
        if (field == null || field.getType() != Boolean.TYPE) {
            return false;
        }
        try {
            field.setAccessible(true);
            field.setBoolean(target, value);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean setIntField(Object target, String name, int value) {
        java.lang.reflect.Field field = findField(target, name);
        if (field == null || field.getType() != Integer.TYPE) {
            return false;
        }
        try {
            field.setAccessible(true);
            field.setInt(target, value);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static java.lang.reflect.Field findField(Object target, String name) {
        if (target == null || name == null) {
            return null;
        }
        for (Class<?> c = target.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
            }
        }
        return null;
    }

    private static String defaultObserverMethodName(Event event) {
        switch (event) {
            case ON_CREATE: return "onCreate";
            case ON_START: return "onStart";
            case ON_RESUME: return "onResume";
            case ON_PAUSE: return "onPause";
            case ON_STOP: return "onStop";
            case ON_DESTROY: return "onDestroy";
            default: return "";
        }
    }

    private static boolean invokeObserverMethod(Object target, String name, Object... args) {
        if (target == null || name == null || name.length() == 0) {
            return false;
        }
        Class<?>[] parameterTypes = new Class<?>[args != null ? args.length : 0];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypes[i] = args[i] != null ? args[i].getClass() : Object.class;
        }
        for (Class<?> c = target.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            java.lang.reflect.Method[] methods = c.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                java.lang.reflect.Method method = methods[i];
                if (!name.equals(method.getName())) {
                    continue;
                }
                Class<?>[] declared = method.getParameterTypes();
                if (declared.length != parameterTypes.length) {
                    continue;
                }
                boolean compatible = true;
                for (int p = 0; p < declared.length; p++) {
                    if (args[p] != null && !declared[p].isAssignableFrom(parameterTypes[p])) {
                        compatible = false;
                        break;
                    }
                }
                if (!compatible) {
                    continue;
                }
                try {
                    method.setAccessible(true);
                    method.invoke(target, args);
                    return true;
                } catch (Throwable ignored) {
                    return false;
                }
            }
        }
        return false;
    }
}
