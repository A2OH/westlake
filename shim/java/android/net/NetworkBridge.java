package android.net;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Bridges ConnectivityManager to the phone's real implementation.
 * On phone: delegates to framework's ConnectivityManager via reflection.
 * On OHOS/headless: returns stubs indicating connectivity available.
 */
public class NetworkBridge {

    private static final int OH_NET_TYPE_WIFI = 1;
    private static final int OH_NET_TYPE_CELLULAR = 2;
    private static final int OH_NET_TYPE_ETHERNET = 3;

    private static boolean phoneDetected;
    private static final String WESTLAKE_HTTP_PROXY_BASE_FILE =
            "/sdcard/Android/data/com.westlake.host/files/westlake_http_proxy_base.txt";

    static {
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            Object instance = host.getField("instance").get(null);
            phoneDetected = (instance != null);
        } catch (Exception e) {
            phoneDetected = false;
        }
    }

    public static boolean isOnPhone() { return phoneDetected; }

    private static boolean hasPortableHttpBridge() {
        File f = new File(WESTLAKE_HTTP_PROXY_BASE_FILE);
        return f.exists() && f.isFile();
    }

    /**
     * Get the real ConnectivityManager from the phone's Context.
     */
    public static Object getRealConnectivityManager() {
        if (!phoneDetected) return null;
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            Object instance = host.getField("instance").get(null);
            // Call getSystemService("connectivity")
            Method gsm = instance.getClass().getMethod("getSystemService", String.class);
            return gsm.invoke(instance, "connectivity");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Check if network is available via the real ConnectivityManager.
     */
    public static boolean isNetworkAvailable() {
        if (hasPortableHttpBridge()) {
            return true;
        }
        Object cm = getRealConnectivityManager();
        if (cm == null) return true; // assume available in headless mode
        try {
            Method m = cm.getClass().getMethod("getActiveNetworkInfo");
            Object ni = m.invoke(cm);
            if (ni == null) return false;
            Method isConnected = ni.getClass().getMethod("isConnected");
            return (Boolean) isConnected.invoke(ni);
        } catch (Exception e) {
            return true; // assume available if reflection fails
        }
    }

    /**
     * Return the portable OH-style network type used by ConnectivityManager.
     */
    public static int getNetworkType() {
        Object cm = getRealConnectivityManager();
        if (cm == null) return OH_NET_TYPE_WIFI;
        try {
            Method m = cm.getClass().getMethod("getActiveNetworkInfo");
            Object ni = m.invoke(cm);
            if (ni == null) return OH_NET_TYPE_WIFI;
            Method getType = ni.getClass().getMethod("getType");
            int androidType = ((Integer) getType.invoke(ni)).intValue();
            if (androidType == ConnectivityManager.TYPE_MOBILE) return OH_NET_TYPE_CELLULAR;
            if (androidType == ConnectivityManager.TYPE_ETHERNET) return OH_NET_TYPE_ETHERNET;
            return OH_NET_TYPE_WIFI;
        } catch (Exception e) {
            return OH_NET_TYPE_WIFI;
        }
    }
}
