package android.location;

/**
 * Android-compatible Location shim. Stores GPS fix data.
 */
public class Location {
    private String mProvider;
    private double mLatitude;
    private double mLongitude;
    private double mAltitude;
    private float mSpeed;
    private float mBearing;
    private float mAccuracy;
    private long mTime;
    private long mElapsedRealtimeNanos;
    private boolean mHasAltitude;
    private boolean mHasSpeed;
    private boolean mHasBearing;
    private boolean mHasAccuracy;

    public Location(String provider) {
        mProvider = provider;
    }

    public Location(Location l) {
        set(l);
    }

    public void set(Location l) {
        mProvider = l.mProvider;
        mLatitude = l.mLatitude;
        mLongitude = l.mLongitude;
        mAltitude = l.mAltitude;
        mSpeed = l.mSpeed;
        mBearing = l.mBearing;
        mAccuracy = l.mAccuracy;
        mTime = l.mTime;
        mElapsedRealtimeNanos = l.mElapsedRealtimeNanos;
        mHasAltitude = l.mHasAltitude;
        mHasSpeed = l.mHasSpeed;
        mHasBearing = l.mHasBearing;
        mHasAccuracy = l.mHasAccuracy;
    }

    public void reset() {
        mProvider = null;
        mLatitude = 0.0;
        mLongitude = 0.0;
        mAltitude = 0.0;
        mSpeed = 0.0f;
        mBearing = 0.0f;
        mAccuracy = 0.0f;
        mTime = 0L;
        mElapsedRealtimeNanos = 0L;
        mHasAltitude = false;
        mHasSpeed = false;
        mHasBearing = false;
        mHasAccuracy = false;
    }

    public String getProvider() { return mProvider; }
    public void setProvider(String provider) { mProvider = provider; }

    public double getLatitude() { return mLatitude; }
    public void setLatitude(double lat) { mLatitude = lat; }

    public double getLongitude() { return mLongitude; }
    public void setLongitude(double lon) { mLongitude = lon; }

    public boolean hasAltitude() { return mHasAltitude; }
    public double getAltitude() { return mAltitude; }
    public void setAltitude(double alt) { mAltitude = alt; mHasAltitude = true; }
    public void removeAltitude() { mAltitude = 0.0; mHasAltitude = false; }

    public boolean hasSpeed() { return mHasSpeed; }
    public float getSpeed() { return mSpeed; }
    public void setSpeed(float speed) { mSpeed = speed; mHasSpeed = true; }
    public void removeSpeed() { mSpeed = 0.0f; mHasSpeed = false; }

    public boolean hasBearing() { return mHasBearing; }
    public float getBearing() { return mBearing; }
    public void setBearing(float bearing) { mBearing = bearing; mHasBearing = true; }
    public void removeBearing() { mBearing = 0.0f; mHasBearing = false; }

    public boolean hasAccuracy() { return mHasAccuracy; }
    public float getAccuracy() { return mAccuracy; }
    public void setAccuracy(float accuracy) { mAccuracy = accuracy; mHasAccuracy = true; }
    public void removeAccuracy() { mAccuracy = 0.0f; mHasAccuracy = false; }

    public long getTime() { return mTime; }
    public void setTime(long time) { mTime = time; }

    public long getElapsedRealtimeNanos() { return mElapsedRealtimeNanos; }
    public void setElapsedRealtimeNanos(long nanos) { mElapsedRealtimeNanos = nanos; }

    public float distanceTo(Location dest) {
        return distanceBetween(mLatitude, mLongitude, dest.mLatitude, dest.mLongitude);
    }

    public static float distanceBetween(double startLat, double startLon,
                                         double endLat, double endLon) {
        double R = 6371000; // Earth radius in meters
        double dLat = Math.toRadians(endLat - startLat);
        double dLon = Math.toRadians(endLon - startLon);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat)) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return (float)(R * c);
    }

    @Override
    public String toString() {
        return "Location[" + mProvider + " " + mLatitude + "," + mLongitude + "]";
    }
}
