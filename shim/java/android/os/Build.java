package android.os;

import com.ohos.shim.bridge.OHBridge;
import com.ohos.shim.bridge.OHBridgeState;

/**
 * Shim: android.os.Build → @ohos.deviceInfo
 * Tier 1 — direct mapping of device info fields.
 */
public class Build {
    private static boolean isStandaloneGuest() {
        try {
            return System.getProperty("westlake.apk.package") != null
                    || System.getProperty("westlake.apk.path") != null
                    || System.getenv("WESTLAKE_APK_PACKAGE") != null
                    || System.getenv("WESTLAKE_APK_PATH") != null;
        } catch (Throwable ignored) {
            return true;
        }
    }
    private static boolean shouldUseBridge() {
        if (isStandaloneGuest()) {
            return false;
        }
        try {
            return OHBridgeState.nativeAvailableSnapshot || !OHBridgeState.subprocessSnapshot;
        } catch (Throwable ignored) {
            return false;
        }
    }
    private static String safeGetString(String method) {
        if (!shouldUseBridge()) {
            return "Westlake";
        }
        try { return (String) OHBridge.class.getMethod(method).invoke(null); }
        catch (Throwable e) { return "Westlake"; }
    }
    private static int safeGetInt(String method) {
        if (!shouldUseBridge()) {
            return 30;
        }
        try { return (int) OHBridge.class.getMethod(method).invoke(null); }
        catch (Throwable e) { return 30; }
    }
    public static final String BRAND = safeGetString("getDeviceBrand");
    public static final String MODEL = safeGetString("getDeviceModel");
    public static final String MANUFACTURER = BRAND;
    public static final String DEVICE = MODEL;
    public static final String PRODUCT = MODEL;
    public static final String DISPLAY = safeGetString("getOSVersion");
    public static final String FINGERPRINT = BRAND + "/" + PRODUCT + "/" + DEVICE + ":Westlake";
    public static final String HARDWARE = "westlake";
    public static final String HOST = "localhost";
    public static final String ID = "WESTLAKE";
    public static final String BOARD = "westlake";
    public static final String BOOTLOADER = "unknown";
    public static final String CPU_ABI = "arm64-v8a";
    public static final String CPU_ABI2 = "armeabi-v7a";
    public static final String[] SUPPORTED_ABIS = new String[] {"arm64-v8a", "armeabi-v7a", "armeabi"};
    public static final String[] SUPPORTED_32_BIT_ABIS = new String[] {"armeabi-v7a", "armeabi"};
    public static final String[] SUPPORTED_64_BIT_ABIS = new String[] {"arm64-v8a"};
    public static final String TYPE = "userdebug";
    public static final String TAGS = "release-keys";
    public static final String USER = "westlake";
    public static final String SERIAL = "unknown";
    public static final long TIME = 0L;

    public static class VERSION {
        public static final String RELEASE = safeGetString("getOSVersion");
        public static final int SDK_INT = safeGetInt("getSDKVersion");
    }

    public static class VERSION_CODES {
        public static final int BASE = 1;
        public static final int DONUT = 4;
        public static final int ECLAIR = 7;
        public static final int FROYO = 8;
        public static final int GINGERBREAD = 9;
        public static final int HONEYCOMB = 11;
        public static final int ICE_CREAM_SANDWICH = 14;
        public static final int JELLY_BEAN = 16;
        public static final int JELLY_BEAN_MR1 = 17;
        public static final int JELLY_BEAN_MR2 = 18;
        public static final int KITKAT = 19;
        public static final int LOLLIPOP = 21;
        public static final int LOLLIPOP_MR1 = 22;
        public static final int M = 23;
        public static final int N = 24;
        public static final int N_MR1 = 25;
        public static final int O = 26;
        public static final int O_MR1 = 27;
        public static final int P = 28;
        public static final int Q = 29;
        public static final int R = 30;
        public static final int S = 31;
        public static final int TIRAMISU = 33;
        public static final int CUR_DEVELOPMENT = 10000;
    }
}
