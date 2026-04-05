package androidx.activity;

public abstract class OnBackPressedCallback {
    private boolean mEnabled;
    public OnBackPressedCallback(boolean enabled) { mEnabled = enabled; }
    public void setEnabled(boolean enabled) { mEnabled = enabled; }
    public boolean isEnabled() { return mEnabled; }
    public abstract void handleOnBackPressed();
    public void remove() {}
}
