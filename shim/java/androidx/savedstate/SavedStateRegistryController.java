package androidx.savedstate;

import android.os.Bundle;

public final class SavedStateRegistryController {
    private final SavedStateRegistryOwner mOwner;
    private final SavedStateRegistry mRegistry;

    private SavedStateRegistryController(SavedStateRegistryOwner owner) {
        mOwner = owner;
        mRegistry = new SavedStateRegistry();
    }

    public static SavedStateRegistryController create(SavedStateRegistryOwner owner) {
        return new SavedStateRegistryController(owner);
    }

    public SavedStateRegistry getSavedStateRegistry() { return mRegistry; }

    public void performRestore(Bundle savedState) { mRegistry.performRestore(savedState); }
    public void performSave(Bundle outBundle) { mRegistry.performSave(outBundle); }
}
