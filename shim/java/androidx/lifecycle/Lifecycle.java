package androidx.lifecycle;

public abstract class Lifecycle {
    public enum State { DESTROYED, INITIALIZED, CREATED, STARTED, RESUMED }
    public enum Event { ON_CREATE, ON_START, ON_RESUME, ON_PAUSE, ON_STOP, ON_DESTROY, ON_ANY }
    public abstract void addObserver(LifecycleObserver observer);
    public abstract void removeObserver(LifecycleObserver observer);
    public abstract State getCurrentState();
    // Obfuscated aliases (R8/ProGuard renames in McDonald's DEX)
    public void c(LifecycleObserver observer) { addObserver(observer); }
    public void d(LifecycleObserver observer) { removeObserver(observer); }
    public void a(LifecycleObserver observer) { addObserver(observer); }
    public void b(LifecycleObserver observer) { removeObserver(observer); }
}
