package androidx.activity.result;

import androidx.activity.result.contract.ActivityResultContract;

public abstract class ActivityResultRegistry {
    public abstract <I, O> ActivityResultLauncher<I> register(String key,
            ActivityResultContract<I, O> contract, ActivityResultCallback<O> callback);
}
