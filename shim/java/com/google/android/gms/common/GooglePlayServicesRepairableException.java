package com.google.android.gms.common;

import android.content.Intent;

public class GooglePlayServicesRepairableException extends Exception {
    private final int mConnectionStatusCode;
    private final Intent mIntent;

    public GooglePlayServicesRepairableException(int statusCode, String message, Intent intent) {
        super(message);
        mConnectionStatusCode = statusCode;
        mIntent = intent;
    }

    public int getConnectionStatusCode() {
        return mConnectionStatusCode;
    }

    public Intent getIntent() {
        return mIntent;
    }
}
