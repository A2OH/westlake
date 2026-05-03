package com.google.android.gms.common;

import android.app.PendingIntent;

public class ConnectionResult implements android.os.Parcelable {
    public static final int SUCCESS = 0;
    public static final int UNKNOWN = -1;
    public static final int SERVICE_MISSING = 1;
    public static final int SERVICE_VERSION_UPDATE_REQUIRED = 2;
    public static final int SERVICE_DISABLED = 3;
    public static final int SIGN_IN_REQUIRED = 4;
    public static final int INVALID_ACCOUNT = 5;
    public static final int RESOLUTION_REQUIRED = 6;
    public static final int NETWORK_ERROR = 7;
    public static final int INTERNAL_ERROR = 8;
    public static final int SERVICE_INVALID = 9;
    public static final int DEVELOPER_ERROR = 10;
    public static final int LICENSE_CHECK_FAILED = 11;
    public static final int CANCELED = 13;
    public static final int TIMEOUT = 14;
    public static final int INTERRUPTED = 15;
    public static final int API_UNAVAILABLE = 16;
    public static final int SIGN_IN_FAILED = 17;
    public static final int SERVICE_UPDATING = 18;
    public static final int SERVICE_MISSING_PERMISSION = 19;
    public static final int RESTRICTED_PROFILE = 20;
    public static final int RESOLUTION_ACTIVITY_NOT_FOUND = 22;
    public static final int API_DISABLED = 23;
    public static final int API_DISABLED_FOR_CONNECTION = 24;

    public static final ConnectionResult RESULT_SUCCESS = new ConnectionResult(SUCCESS);

    private int mStatusCode;
    private PendingIntent mResolution;
    private String mMessage;

    public ConnectionResult(int statusCode) { mStatusCode = statusCode; }

    public ConnectionResult(int statusCode, PendingIntent pendingIntent) {
        this(statusCode, pendingIntent, null);
    }

    public ConnectionResult(int statusCode, PendingIntent pendingIntent, String message) {
        this.mStatusCode = statusCode;
        this.mResolution = pendingIntent;
        this.mMessage = message;
    }

    public ConnectionResult(int versionCode, int statusCode, PendingIntent pendingIntent,
            String message) {
        this(statusCode, pendingIntent, message);
    }

    public int getErrorCode() { return mStatusCode; }
    public boolean isSuccess() { return mStatusCode == SUCCESS; }
    public boolean hasResolution() { return mResolution != null; }
    public PendingIntent getResolution() { return mResolution; }
    public String getErrorMessage() { return mMessage != null ? mMessage : "GMS unavailable"; }

    public void startResolutionForResult(android.app.Activity activity, int requestCode) {
        // Westlake reports GMS as unavailable and does not launch Play Services resolution UI.
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeInt(1);
        dest.writeInt(mStatusCode);
        dest.writeString(mMessage);
    }

    public static final android.os.Parcelable.Creator<ConnectionResult> CREATOR =
            new android.os.Parcelable.Creator<ConnectionResult>() {
                @Override
                public ConnectionResult createFromParcel(android.os.Parcel source) {
                    if (source == null) {
                        return new ConnectionResult(UNKNOWN);
                    }
                    try {
                        source.readInt();
                        int status = source.readInt();
                        String message = source.readString();
                        return new ConnectionResult(status, null, message);
                    } catch (Throwable ignored) {
                        return new ConnectionResult(UNKNOWN);
                    }
                }

                @Override
                public ConnectionResult[] newArray(int size) {
                    return new ConnectionResult[size];
                }
            };
}
