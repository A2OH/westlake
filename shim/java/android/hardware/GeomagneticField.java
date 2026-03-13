package android.hardware;

/**
 * Android-compatible GeomagneticField shim. Uses WMM-2020 simplified model.
 * Returns approximate values for magnetic declination/inclination/strength.
 */
public class GeomagneticField {
    private final float mDeclination;
    private final float mInclination;
    private final float mFieldStrength;

    public GeomagneticField(float gdLatitudeDeg, float gdLongitudeDeg,
                            float altitudeMeters, long timeMillis) {
        // Simplified model — return approximate values
        // Real implementation would use full WMM coefficients
        mDeclination = estimateDeclination(gdLatitudeDeg, gdLongitudeDeg);
        mInclination = estimateInclination(gdLatitudeDeg);
        mFieldStrength = estimateFieldStrength(gdLatitudeDeg);
    }

    public float getDeclination() { return mDeclination; }
    public float getInclination() { return mInclination; }
    public float getFieldStrength() { return mFieldStrength; }

    public float getHorizontalStrength() {
        return (float)(mFieldStrength * Math.cos(Math.toRadians(mInclination)));
    }

    public float getX() { return (float)(getHorizontalStrength() * Math.cos(Math.toRadians(mDeclination))); }
    public float getY() { return (float)(getHorizontalStrength() * Math.sin(Math.toRadians(mDeclination))); }
    public float getZ() { return (float)(mFieldStrength * Math.sin(Math.toRadians(mInclination))); }

    private static float estimateDeclination(float lat, float lon) {
        // Very rough approximation
        return (float)(10.0 * Math.sin(Math.toRadians(lon)) * Math.cos(Math.toRadians(lat)));
    }

    private static float estimateInclination(float lat) {
        return (float)(2.0 * Math.toDegrees(Math.atan(2.0 * Math.tan(Math.toRadians(lat)))));
    }

    private static float estimateFieldStrength(float lat) {
        // Earth's field: ~25000-65000 nT
        double latRad = Math.toRadians(lat);
        return (float)(30000 + 20000 * Math.abs(Math.sin(latRad)));
    }
}
