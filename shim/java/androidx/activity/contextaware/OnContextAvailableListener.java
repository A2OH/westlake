package androidx.activity.contextaware;

import android.content.Context;

public interface OnContextAvailableListener {
    void onContextAvailable(Context context);
    // Obfuscated alias (R8 renames onContextAvailable → a)
    default void a(Context context) { onContextAvailable(context); }
}
