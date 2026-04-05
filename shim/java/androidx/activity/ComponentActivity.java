package androidx.activity;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryController;
import androidx.savedstate.SavedStateRegistryOwner;

public class ComponentActivity extends FragmentActivity
        implements LifecycleOwner, SavedStateRegistryOwner {

    private final LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    private final SavedStateRegistryController mSavedStateRegistryController =
            SavedStateRegistryController.create(this);

    private final java.util.List<androidx.activity.contextaware.OnContextAvailableListener> mContextListeners =
            new java.util.ArrayList<>();
    private boolean mContextAvailableFired = false;

    public ComponentActivity() {}

    @Override
    public Lifecycle getLifecycle() { return mLifecycleRegistry; }

    @Override
    public SavedStateRegistry getSavedStateRegistry() {
        return mSavedStateRegistryController.getSavedStateRegistry();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSavedStateRegistryController.performRestore(savedInstanceState);
        // Fire OnContextAvailableListeners BEFORE super.onCreate()
        // This is where Hilt's inject() callback runs
        if (!mContextAvailableFired) {
            mContextAvailableFired = true;
            for (androidx.activity.contextaware.OnContextAvailableListener l :
                    new java.util.ArrayList<>(mContextListeners)) {
                try {
                    // Try obfuscated name first (R8 renames onContextAvailable → a)
                    try {
                        java.lang.reflect.Method m = l.getClass().getMethod("a", android.content.Context.class);
                        m.invoke(l, (android.content.Context) this);
                    } catch (NoSuchMethodException nsm) {
                        l.onContextAvailable(this);
                    }
                } catch (Throwable t) {
                    System.err.println("[ComponentActivity] OnContextAvailable error: " + t);
                }
            }
        }
        try {
            super.onCreate(savedInstanceState);
        } catch (Throwable t) {
            // Catch NPEs from super chain (e.g., refreshBasketLayout) so the Activity's
            // own onCreate code (fragment setup, ViewModels) can still run
            System.err.println("[ComponentActivity] super.onCreate caught: " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSavedStateRegistryController.performSave(outState);
    }

    public void addOnContextAvailableListener(androidx.activity.contextaware.OnContextAvailableListener listener) {
        mContextListeners.add(listener);
        // If context already available, fire immediately
        if (mContextAvailableFired) {
            try { listener.onContextAvailable(this); } catch (Throwable t) {}
        }
    }
    public void removeOnContextAvailableListener(androidx.activity.contextaware.OnContextAvailableListener listener) {
        mContextListeners.remove(listener);
    }
    public void addOnBackPressedCallback(Object callback) { /* stub */ }
    private final OnBackPressedDispatcher mOnBackPressedDispatcher = new OnBackPressedDispatcher();
    public OnBackPressedDispatcher getOnBackPressedDispatcher() { return mOnBackPressedDispatcher; }
    public Object getViewModelStore() { return null; }

    public <I, O> androidx.activity.result.ActivityResultLauncher<I> registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContract<I, O> contract,
            androidx.activity.result.ActivityResultCallback<O> callback) {
        return new androidx.activity.result.ActivityResultLauncher<I>() {
            @Override public void launch(I input) { /* stub — no real activity result */ }
            @Override public void unregister() {}
            @Override public Object getContract() { return contract; }
        };
    }

    public <I, O> androidx.activity.result.ActivityResultLauncher<I> registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContract<I, O> contract,
            androidx.activity.result.ActivityResultRegistry registry,
            androidx.activity.result.ActivityResultCallback<O> callback) {
        return registerForActivityResult(contract, callback);
    }
}
