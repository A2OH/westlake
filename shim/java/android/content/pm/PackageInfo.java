package android.content.pm;

public class PackageInfo {

    public String packageName;
    public String versionName;
    public int    versionCode;
    public int    versionCodeMajor;
    public long   firstInstallTime;
    public long   lastUpdateTime;
    public ApplicationInfo applicationInfo;
    public ActivityInfo[] activities;
    public ServiceInfo[] services;
    public ProviderInfo[] providers;
    public PermissionInfo[] permissions;
    public String[] requestedPermissions;
    public Signature[] signatures;
    public SigningInfo signingInfo;
    public String[] splitNames;
    public int[] splitRevisionCodes;
    public int baseRevisionCode;

    public long getLongVersionCode() {
        return ((long) versionCodeMajor << 32) | (versionCode & 0xffffffffL);
    }

    public void setLongVersionCode(long longVersionCode) {
        versionCodeMajor = (int) (longVersionCode >> 32);
        versionCode = (int) longVersionCode;
    }
}
