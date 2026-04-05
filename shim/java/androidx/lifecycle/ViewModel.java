package androidx.lifecycle;

public abstract class ViewModel {
    protected void onCleared() {}
    public void clear() { onCleared(); }
}
