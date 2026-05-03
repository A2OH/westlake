package android.location;
import android.app.PendingIntent;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocationManager {
    private final Set<LocationListener> mListeners = new HashSet<>();
    public static final String EXTRA_LOCATION_ENABLED = "location_enabled";
    public static final String EXTRA_PROVIDER_ENABLED = "provider_enabled";
    public static final String EXTRA_PROVIDER_NAME = "provider_name";
    public static final String GPS_PROVIDER = "gps";
    public static final String KEY_LOCATION_CHANGED = "location_changed";
    public static final String KEY_PROVIDER_ENABLED = "provider_enabled";
    public static final String KEY_PROXIMITY_ENTERING = "proximity_entering";
    public static final String MODE_CHANGED_ACTION = "android.location.MODE_CHANGED";
    public static final String NETWORK_PROVIDER = "network";
    public static final String PASSIVE_PROVIDER = "passive";
    public static final String PROVIDERS_CHANGED_ACTION = "android.location.PROVIDERS_CHANGED";

    public LocationManager() {}

    public Location getLastKnownLocation(String provider) {
        Location loc = new Location(provider != null ? provider : "gps");
        loc.setLatitude(37.7749);
        loc.setLongitude(-122.4194);
        loc.setAltitude(0.0);
        loc.setAccuracy(1000.0f);
        loc.setTime(System.currentTimeMillis());
        return loc;
    }

    public List<String> getProviders(boolean enabledOnly) {
        List<String> providers = new ArrayList<>();
        providers.add(GPS_PROVIDER);
        providers.add(NETWORK_PROVIDER);
        providers.add(PASSIVE_PROVIDER);
        return providers;
    }

    public String getBestProvider(Criteria criteria, boolean enabledOnly) {
        if (criteria != null && criteria.getAccuracy() == Criteria.ACCURACY_FINE) {
            return GPS_PROVIDER;
        }
        return NETWORK_PROVIDER;
    }

    public void requestLocationUpdates(String provider, long minTime, float minDistance, LocationListener listener) {
        if (listener != null) mListeners.add(listener);
    }

    public void removeUpdates(LocationListener listener) {
        if (listener != null) mListeners.remove(listener);
    }

    public boolean hasListener(LocationListener listener) {
        return mListeners.contains(listener);
    }

    public int getListenerCount() {
        return mListeners.size();
    }

    public void addTestProvider(String p0, boolean p1, boolean p2, boolean p3, boolean p4, boolean p5, boolean p6, boolean p7, int p8, int p9) {}
    public int getGnssYearOfHardware() { return 0; }
    public boolean isLocationEnabled() { return true; }
    public boolean isProviderEnabled(String p0) { return true; }
    public void removeNmeaListener(OnNmeaMessageListener p0) {}
    public void removeTestProvider(String p0) {}
    public void removeUpdates(PendingIntent p0) {}
    public boolean sendExtraCommand(String p0, String p1, Bundle p2) { return false; }
    public void setTestProviderEnabled(String p0, boolean p1) {}
    public void setTestProviderLocation(String p0, Location p1) {}
    public void unregisterAntennaInfoListener(Object p0) {}
    public void unregisterGnssMeasurementsCallback(Object p0) {}
    public void unregisterGnssNavigationMessageCallback(Object p0) {}
    public void unregisterGnssStatusCallback(Object p0) {}
}
