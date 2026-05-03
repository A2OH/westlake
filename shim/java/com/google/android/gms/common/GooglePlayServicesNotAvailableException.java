package com.google.android.gms.common;

public class GooglePlayServicesNotAvailableException extends Exception {
    public final int errorCode;

    public GooglePlayServicesNotAvailableException(int errorCode) {
        super("Google Play Services not available: " + errorCode);
        this.errorCode = errorCode;
    }
}
