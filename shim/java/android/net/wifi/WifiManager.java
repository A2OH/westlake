package android.net.wifi;
import android.net.wifi.hotspot2.PasspointConfiguration;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class WifiManager {
    public static final int ACTION_PICK_WIFI_NETWORK = 0;
    public static final int ACTION_REQUEST_SCAN_ALWAYS_AVAILABLE = 0;
    public static final int ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION = 0;
    public static final int ACTION_WIFI_SCAN_AVAILABILITY_CHANGED = 0;
    public static final int EXTRA_NETWORK_INFO = 0;
    public static final int EXTRA_NETWORK_SUGGESTION = 0;
    public static final int EXTRA_NEW_RSSI = 0;
    public static final int EXTRA_PREVIOUS_WIFI_STATE = 0;
    public static final int EXTRA_RESULTS_UPDATED = 0;
    public static final int EXTRA_SCAN_AVAILABLE = 0;
    public static final int EXTRA_WIFI_STATE = 0;
    public static final int NETWORK_IDS_CHANGED_ACTION = 0;
    public static final int NETWORK_STATE_CHANGED_ACTION = 0;
    public static final int RSSI_CHANGED_ACTION = 0;
    public static final int SCAN_RESULTS_AVAILABLE_ACTION = 0;
    public static final int STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE = 0;
    public static final int STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_EXCEEDS_MAX_PER_APP = 0;
    public static final int STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_INVALID = 0;
    public static final int STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_NOT_ALLOWED = 0;
    public static final int STATUS_NETWORK_SUGGESTIONS_ERROR_APP_DISALLOWED = 0;
    public static final int STATUS_NETWORK_SUGGESTIONS_ERROR_INTERNAL = 0;
    public static final int STATUS_NETWORK_SUGGESTIONS_ERROR_REMOVE_INVALID = 0;
    public static final int STATUS_NETWORK_SUGGESTIONS_SUCCESS = 0;
    public static final int STATUS_SUGGESTION_CONNECTION_FAILURE_ASSOCIATION = 0;
    public static final int STATUS_SUGGESTION_CONNECTION_FAILURE_AUTHENTICATION = 0;
    public static final int STATUS_SUGGESTION_CONNECTION_FAILURE_IP_PROVISIONING = 0;
    public static final int STATUS_SUGGESTION_CONNECTION_FAILURE_UNKNOWN = 0;
    public static final int UNKNOWN_SSID = 0;
    public static final int WIFI_MODE_FULL_HIGH_PERF = 0;
    public static final int WIFI_MODE_FULL_LOW_LATENCY = 0;
    public static final int WIFI_STATE_CHANGED_ACTION = 0;
    public static final int WIFI_STATE_DISABLED = 1;
    public static final int WIFI_STATE_DISABLING = 0;
    public static final int WIFI_STATE_ENABLED = 3;
    public static final int WIFI_STATE_ENABLING = 2;
    public static final int WIFI_STATE_UNKNOWN = 4;

    public WifiManager() {}

    public boolean isWifiEnabled() {
        return com.ohos.shim.bridge.OHBridge.wifiIsEnabled();
    }

    public boolean setWifiEnabled(boolean enabled) {
        return com.ohos.shim.bridge.OHBridge.wifiSetEnabled(enabled);
    }

    public int getWifiState() {
        return com.ohos.shim.bridge.OHBridge.wifiGetState();
    }

    public boolean startScan() { return true; }

    public List<ScanResult> getScanResults() {
        return new ArrayList<>();
    }

    public WifiInfo getConnectionInfo() {
        return new WifiInfo(
            com.ohos.shim.bridge.OHBridge.wifiGetSSID(),
            "00:00:00:00:00:00",
            com.ohos.shim.bridge.OHBridge.wifiGetRssi(),
            com.ohos.shim.bridge.OHBridge.wifiGetLinkSpeed(),
            com.ohos.shim.bridge.OHBridge.wifiGetFrequency(),
            "02:00:00:00:00:00", 0, 0);
    }

    public DhcpInfo getDhcpInfo() {
        return new DhcpInfo();
    }

    public static int calculateSignalLevel(int rssi, int numLevels) {
        int MIN_RSSI = -100;
        int MAX_RSSI = -55;
        if (rssi <= MIN_RSSI) return 0;
        if (rssi >= MAX_RSSI) return numLevels - 1;
        float inputRange = MAX_RSSI - MIN_RSSI;
        float outputRange = numLevels - 1;
        return (int)((float)(rssi - MIN_RSSI) * outputRange / inputRange);
    }

    public static int compareSignalLevel(int rssiA, int rssiB) {
        return rssiA - rssiB;
    }

    public void addOrUpdatePasspointConfiguration(PasspointConfiguration p0) {}
    public Object createMulticastLock(String p0) { return null; }
    public Object createWifiLock(int p0, String p1) { return null; }
    public int getMaxNumberOfNetworkSuggestionsPerApp() { return 0; }
    public boolean is5GHzBandSupported() { return false; }
    public boolean is6GHzBandSupported() { return false; }
    public boolean isEasyConnectSupported() { return false; }
    public boolean isEnhancedOpenSupported() { return false; }
    public boolean isEnhancedPowerReportingSupported() { return false; }
    public boolean isP2pSupported() { return false; }
    public boolean isPreferredNetworkOffloadSupported() { return false; }
    public boolean isStaApConcurrencySupported() { return false; }
    public boolean isTdlsSupported() { return false; }
    public boolean isWapiSupported() { return false; }
    public boolean isWifiStandardSupported(int p0) { return false; }
    public boolean isWpa3SaeSupported() { return false; }
    public boolean isWpa3SuiteBSupported() { return false; }
    public void setTdlsEnabled(InetAddress p0, boolean p1) {}
    public void setTdlsEnabledWithMacAddress(String p0, boolean p1) {}
}
