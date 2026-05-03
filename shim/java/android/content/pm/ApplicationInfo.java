package android.content.pm;

import android.os.Bundle;

public class ApplicationInfo extends PackageItemInfo {

    public static final int FLAG_SYSTEM    = 1;
    public static final int FLAG_DEBUGGABLE = 2;

    public String packageName = System.getProperty("westlake.apk.package");
    public String name;
    public String className;
    public String processName = packageName;
    public String appComponentFactory;
    public String sourceDir = System.getProperty("westlake.apk.path", "/data/local/tmp/westlake/com_mcdonalds_app.apk");
    public String publicSourceDir = sourceDir;
    public String[] splitSourceDirs = splitPathList(System.getProperty("westlake.apk.splitSourceDirs"));
    public String[] splitPublicSourceDirs = splitSourceDirs;
    public String[] resourceDirs = new String[0];
    public String dataDir = System.getProperty("westlake.data.dir", "/data/local/tmp/westlake");
    public String nativeLibraryDir = System.getProperty("westlake.native.lib.dir", "/data/local/tmp/westlake/app_lib");
    public String secondaryNativeLibraryDir;
    public String nativeLibraryRootDir = nativeLibraryDir;
    public String primaryCpuAbi = "arm64-v8a";
    public String secondaryCpuAbi = "armeabi-v7a";
    public int    flags;
    public int    uid;
    public int    targetSdkVersion;

    public ApplicationInfo() {
        if (metaData == null) {
            metaData = new Bundle();
        }
    }

    public boolean hasRtlSupport() { return false; }

    private static String[] splitPathList(String value) {
        if (value == null || value.length() == 0) {
            return new String[0];
        }
        java.util.ArrayList<String> result = new java.util.ArrayList<String>();
        int start = 0;
        for (int i = 0; i <= value.length(); i++) {
            if (i == value.length() || value.charAt(i) == ':') {
                if (i > start) {
                    result.add(value.substring(start, i));
                }
                start = i + 1;
            }
        }
        return result.toArray(new String[result.size()]);
    }
}
