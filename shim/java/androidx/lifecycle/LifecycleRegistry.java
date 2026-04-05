package androidx.lifecycle;

import java.util.ArrayList;
import java.util.List;

public class LifecycleRegistry extends Lifecycle {
    private State mState = State.INITIALIZED;
    private final List<LifecycleObserver> mObservers = new ArrayList<>();
    private final LifecycleOwner mOwner;

    public LifecycleRegistry(LifecycleOwner owner) { mOwner = owner; }

    @Override public void addObserver(LifecycleObserver observer) {
        if (observer != null) mObservers.add(observer);
    }
    @Override public void removeObserver(LifecycleObserver observer) {
        mObservers.remove(observer);
    }
    @Override public State getCurrentState() { return mState; }

    public void setCurrentState(State state) { mState = state; }
    public void handleLifecycleEvent(Event event) {
        switch (event) {
            case ON_CREATE: mState = State.CREATED; break;
            case ON_START: mState = State.STARTED; break;
            case ON_RESUME: mState = State.RESUMED; break;
            case ON_PAUSE: mState = State.STARTED; break;
            case ON_STOP: mState = State.CREATED; break;
            case ON_DESTROY: mState = State.DESTROYED; break;
            default: break;
        }
    }
    public void markState(State state) { mState = state; }
    public int getObserverCount() { return mObservers.size(); }
}
