package android.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferencesImpl;

/**
 * Shim: android.preference.PreferenceManager
 *
 * Provides getDefaultSharedPreferences() which many apps use to store settings.
 * The default name follows Android's convention: "<package>_preferences".
 */
public class PreferenceManager {
    private static final String TAG = "PreferenceManager";

    public PreferenceManager() {}

    private static Context resolveContext(Context context) {
        if (context != null) {
            return context;
        }
        Context fallback = null;
        try {
            fallback = android.app.WestlakeActivityThread.currentApplication();
        } catch (Throwable ignored) {
        }
        if (fallback == null) {
            try {
                fallback = android.app.MiniServer.currentApplication();
            } catch (Throwable ignored) {
            }
        }
        if (fallback == null) {
            try {
                fallback = android.app.ActivityThread.currentApplication();
            } catch (Throwable ignored) {
            }
        }
        if (fallback != null) {
            android.util.Log.w(TAG,
                    "getDefaultSharedPreferences(null): using current application context");
        } else {
            android.util.Log.w(TAG,
                    "getDefaultSharedPreferences(null): no context; using shim store");
        }
        return fallback;
    }

    private static String defaultName(Context context) {
        String packageName = null;
        if (context != null) {
            try {
                packageName = context.getPackageName();
            } catch (Throwable ignored) {
            }
        }
        if (packageName == null || packageName.length() == 0) {
            try {
                packageName = android.app.MiniServer.currentPackageName();
            } catch (Throwable ignored) {
            }
        }
        if (packageName == null || packageName.length() == 0) {
            try {
                packageName = System.getProperty("westlake.apk.package");
            } catch (Throwable ignored) {
            }
        }
        if (packageName == null || packageName.length() == 0) {
            packageName = "default";
        }
        return packageName + "_preferences";
    }

    /**
     * Gets a SharedPreferences instance using the default name for the given context's package.
     * This matches Android's behavior: "<package_name>_preferences" in MODE_PRIVATE.
     */
    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        Context resolved = resolveContext(context);
        String name = defaultName(resolved);
        if (resolved != null) {
            return resolved.getSharedPreferences(name, Context.MODE_PRIVATE);
        }
        return SharedPreferencesImpl.getInstance(name);
    }

    /**
     * Returns the default shared preferences name for the given context.
     */
    public static String getDefaultSharedPreferencesName(Context context) {
        return defaultName(resolveContext(context));
    }

    public static int getDefaultSharedPreferencesMode() {
        return Context.MODE_PRIVATE;
    }

    public SharedPreferences getSharedPreferences() {
        return SharedPreferencesImpl.getInstance("default_preferences");
    }

    public void setSharedPreferencesName(String sharedPreferencesName) {}
    public void setSharedPreferencesMode(int sharedPreferencesMode) {}
}
