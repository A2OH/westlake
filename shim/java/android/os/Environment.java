package android.os;

import java.io.File;

/**
 * Android-compatible Environment shim. Maps to standard Linux paths.
 */
public class Environment {
    public static final String MEDIA_MOUNTED = "mounted";
    public static final String MEDIA_REMOVED = "removed";
    public static final String MEDIA_UNMOUNTED = "unmounted";

    public static final String DIRECTORY_MUSIC = "Music";
    public static final String DIRECTORY_PODCASTS = "Podcasts";
    public static final String DIRECTORY_RINGTONES = "Ringtones";
    public static final String DIRECTORY_ALARMS = "Alarms";
    public static final String DIRECTORY_NOTIFICATIONS = "Notifications";
    public static final String DIRECTORY_PICTURES = "Pictures";
    public static final String DIRECTORY_MOVIES = "Movies";
    public static final String DIRECTORY_DOWNLOADS = "Download";
    public static final String DIRECTORY_DCIM = "DCIM";
    public static final String DIRECTORY_DOCUMENTS = "Documents";

    public static File getDataDirectory() {
        return new File("/data");
    }

    public static File getExternalStorageDirectory() {
        String home = System.getProperty("user.home", "/sdcard");
        return new File(home);
    }

    public static File getExternalStoragePublicDirectory(String type) {
        return new File(getExternalStorageDirectory(), type);
    }

    public static String getExternalStorageState() {
        return MEDIA_MOUNTED;
    }

    public static File getRootDirectory() {
        return new File("/system");
    }

    public static File getDownloadCacheDirectory() {
        return new File("/data/cache");
    }

    public static boolean isExternalStorageEmulated() {
        return true;
    }

    public static boolean isExternalStorageRemovable() {
        return false;
    }
}
