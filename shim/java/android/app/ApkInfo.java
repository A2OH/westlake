package android.app;

import java.util.ArrayList;
import java.util.List;

/**
 * ApkInfo — parsed APK metadata.
 * Populated by ApkLoader after extracting and parsing an APK file.
 */
public class ApkInfo {
    public String packageName;
    public int versionCode;
    public String versionName;
    public int minSdkVersion;
    public int targetSdkVersion;
    public String launcherActivity;
    public String applicationClassName;

    /** Fully-qualified Activity class names declared in the manifest */
    public final List<String> activities = new ArrayList<>();

    /** Fully-qualified Service class names */
    public final List<String> services = new ArrayList<>();

    /** Requested permissions */
    public final List<String> permissions = new ArrayList<>();

    /** Paths to extracted DEX files (classes.dex, classes2.dex, ...) */
    public final List<String> dexPaths = new ArrayList<>();

    /** Path to extracted APK directory */
    public String extractDir;

    /** Path to extracted assets/ directory */
    public String assetDir;

    /** Path to extracted native libs */
    public String nativeLibDir;

    /** Paths to individual .so files */
    public final List<String> nativeLibPaths = new ArrayList<>();

    /** Path to extracted res/ directory */
    public String resDir;

    /** ResourceTable (not serializable) */
    public transient Object resourceTable;

    @Override
    public String toString() {
        return "ApkInfo{pkg=" + packageName
                + ", ver=" + versionCode
                + ", activities=" + activities.size()
                + ", dex=" + dexPaths.size() + "}";
    }
}
