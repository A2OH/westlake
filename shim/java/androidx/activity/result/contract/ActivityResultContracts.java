package androidx.activity.result.contract;

import android.content.Context;
import android.content.Intent;

public final class ActivityResultContracts {
    public static class StartActivityForResult extends ActivityResultContract<Intent, Object> {
        @Override public Intent createIntent(Context ctx, Intent input) { return input; }
        @Override public Object parseResult(int resultCode, Intent intent) { return null; }
    }
    public static class RequestPermission extends ActivityResultContract<String, Boolean> {
        @Override public Intent createIntent(Context ctx, String input) { return new Intent(); }
        @Override public Boolean parseResult(int resultCode, Intent intent) { return true; }
    }
    public static class RequestMultiplePermissions extends ActivityResultContract<String[], java.util.Map<String, Boolean>> {
        @Override public Intent createIntent(Context ctx, String[] input) { return new Intent(); }
        @Override public java.util.Map<String, Boolean> parseResult(int rc, Intent i) { return new java.util.HashMap<>(); }
    }
}
