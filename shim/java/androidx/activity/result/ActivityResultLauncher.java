package androidx.activity.result;

import android.content.Intent;

public abstract class ActivityResultLauncher<I> {
    public abstract void launch(I input);
    public void launch(I input, Object options) { launch(input); }
    public abstract void unregister();
    public abstract Object getContract();
}
