package androidx.activity;

public class OnBackPressedDispatcher {
    public void addCallback(OnBackPressedCallback callback) {}
    public void addCallback(androidx.lifecycle.LifecycleOwner owner, OnBackPressedCallback callback) {}
    // Obfuscated aliases (R8 renames addCallback → i, a, b, etc.)
    public void i(androidx.lifecycle.LifecycleOwner owner, OnBackPressedCallback callback) { addCallback(owner, callback); }
    public void a(OnBackPressedCallback callback) { addCallback(callback); }
    public void a(androidx.lifecycle.LifecycleOwner owner, OnBackPressedCallback callback) { addCallback(owner, callback); }
    public void b(OnBackPressedCallback callback) { addCallback(callback); }
    public void b(androidx.lifecycle.LifecycleOwner owner, OnBackPressedCallback callback) { addCallback(owner, callback); }
    public void onBackPressed() {}
    public boolean hasEnabledCallbacks() { return false; }
}
