package com.google.firebase;

import android.content.Context;

/**
 * Stub FirebaseOptions for stock APKs that include obfuscated Firebase calls.
 */
public class FirebaseOptions {
    private final String applicationId;
    private final String apiKey;
    private final String databaseUrl;
    private final String trackingId;
    private final String gcmSenderId;
    private final String storageBucket;
    private final String projectId;

    public FirebaseOptions(String applicationId, String apiKey, String databaseUrl,
            String trackingId, String gcmSenderId, String storageBucket, String projectId) {
        this.applicationId = nonEmpty(applicationId, "westlake-firebase-app");
        this.apiKey = nonEmpty(apiKey, "westlake-api-key");
        this.databaseUrl = nonEmpty(databaseUrl, "https://westlake.invalid");
        this.trackingId = nonEmpty(trackingId, "westlake-tracking");
        this.gcmSenderId = nonEmpty(gcmSenderId, "westlake-sender");
        this.storageBucket = nonEmpty(storageBucket, "westlake.appspot.com");
        this.projectId = nonEmpty(projectId, "westlake-project");
    }

    public static FirebaseOptions a(Context context) {
        String pkg = "westlake";
        try {
            if (context != null && context.getPackageName() != null) {
                pkg = context.getPackageName();
            }
        } catch (Throwable ignored) {
        }
        return new FirebaseOptions(pkg + ".firebase", "westlake-api-key",
                "https://westlake.invalid", "westlake-tracking",
                "westlake-sender", pkg + ".appspot.com", pkg);
    }

    public String b() { return apiKey; }
    public String c() { return applicationId; }
    public String d() { return databaseUrl; }
    public String e() { return gcmSenderId; }
    public String f() { return projectId; }

    public String getApiKey() { return apiKey; }
    public String getApplicationId() { return applicationId; }
    public String getDatabaseUrl() { return databaseUrl; }
    public String getGaTrackingId() { return trackingId; }
    public String getGcmSenderId() { return gcmSenderId; }
    public String getStorageBucket() { return storageBucket; }
    public String getProjectId() { return projectId; }

    private static String nonEmpty(String value, String fallback) {
        return value != null && value.length() > 0 ? value : fallback;
    }
}
