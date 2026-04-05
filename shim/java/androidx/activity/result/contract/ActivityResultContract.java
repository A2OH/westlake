package androidx.activity.result.contract;

import android.content.Context;
import android.content.Intent;

public abstract class ActivityResultContract<I, O> {
    public abstract Intent createIntent(Context context, I input);
    public abstract O parseResult(int resultCode, Intent intent);
}
