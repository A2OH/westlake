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
    public PreferenceManager() {}

    /**
     * Gets a SharedPreferences instance using the default name for the given context's package.
     * This matches Android's behavior: "<package_name>_preferences" in MODE_PRIVATE.
     */
    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        String packageName = context.getPackageName();
        if (packageName == null) packageName = "default";
        return context.getSharedPreferences(packageName + "_preferences", Context.MODE_PRIVATE);
    }

    /**
     * Returns the default shared preferences name for the given context.
     */
    public static String getDefaultSharedPreferencesName(Context context) {
        String packageName = context.getPackageName();
        if (packageName == null) packageName = "default";
        return packageName + "_preferences";
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
