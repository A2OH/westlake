package com.google.android.gms.common;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Minimal Play Services availability surface. Westlake does not provide GMS;
 * this class reports a stable missing-service state so callers take fallback
 * paths instead of failing method resolution.
 */
public class GoogleApiAvailabilityLight {
    public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE = 12451000;

    public static final int SUCCESS = 0;
    public static final int SERVICE_MISSING = 1;
    public static final int SERVICE_VERSION_UPDATE_REQUIRED = 2;
    public static final int SERVICE_DISABLED = 3;
    public static final int SERVICE_INVALID = 9;

    private static final GoogleApiAvailabilityLight sInstance =
            new GoogleApiAvailabilityLight();

    public static GoogleApiAvailabilityLight getInstance() {
        return sInstance;
    }

    public int isGooglePlayServicesAvailable(Context context) {
        return SERVICE_MISSING;
    }

    public int isGooglePlayServicesAvailable(Context context, int minVersion) {
        return SERVICE_MISSING;
    }

    public int getApkVersion(Context context) {
        return 0;
    }

    public boolean isUserResolvableError(int errorCode) {
        return false;
    }

    public Intent getErrorResolutionIntent(Context context, int errorCode, String trackingSource) {
        return null;
    }

    public PendingIntent getErrorResolutionPendingIntent(
            Context context, int errorCode, int requestCode) {
        return null;
    }

    public PendingIntent getErrorResolutionPendingIntent(
            Context context, int errorCode, int requestCode, String trackingSource) {
        return null;
    }

    public void verifyGooglePlayServicesIsAvailable(Context context, int minVersion)
            throws GooglePlayServicesRepairableException,
            GooglePlayServicesNotAvailableException {
        throw new GooglePlayServicesNotAvailableException(SERVICE_MISSING);
    }

    public String getErrorString(int errorCode) {
        return "Google Play Services not available (Westlake)";
    }
}
