package com.google.firebase;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

/**
 * Stub FirebaseApp — no-op initialization.
 */
public class FirebaseApp {
    public static final String DEFAULT_APP_NAME = "[DEFAULT]";
    private static FirebaseApp sDefault;
    private final String mName;
    private final Context mContext;
    private final FirebaseOptions mOptions;

    private FirebaseApp(Context context, String name) {
        this(context, name, FirebaseOptions.a(context));
    }

    private FirebaseApp(Context context, String name, FirebaseOptions options) {
        mContext = context;
        mName = name;
        mOptions = options != null ? options : FirebaseOptions.a(context);
    }

    public static FirebaseApp initializeApp(Context context) {
        if (sDefault == null) {
            sDefault = new FirebaseApp(context, DEFAULT_APP_NAME);
        }
        return sDefault;
    }

    public static FirebaseApp initializeApp(Context context, Object options) {
        if (options instanceof FirebaseOptions) {
            return initializeApp(context, (FirebaseOptions) options);
        }
        return initializeApp(context);
    }

    public static FirebaseApp initializeApp(Context context, FirebaseOptions options) {
        if (sDefault == null) {
            sDefault = new FirebaseApp(context, DEFAULT_APP_NAME, options);
        }
        return sDefault;
    }

    public static FirebaseApp initializeApp(Context context, Object options, String name) {
        FirebaseOptions firebaseOptions = options instanceof FirebaseOptions ? (FirebaseOptions) options : null;
        if (DEFAULT_APP_NAME.equals(name)) return initializeApp(context, firebaseOptions);
        return new FirebaseApp(context, name, firebaseOptions);
    }

    public static FirebaseApp initializeApp(Context context, FirebaseOptions options, String name) {
        return initializeApp(context, (Object) options, name);
    }

    public static FirebaseApp getInstance() {
        if (sDefault == null) sDefault = new FirebaseApp(null, DEFAULT_APP_NAME);
        return sDefault;
    }

    public static FirebaseApp getInstance(String name) {
        return getInstance();
    }

    public static FirebaseApp l() {
        return getInstance();
    }

    public static FirebaseApp m(String name) {
        return getInstance(name);
    }

    public static FirebaseApp r(Context context) {
        return initializeApp(context);
    }

    public static FirebaseApp s(Context context, FirebaseOptions options) {
        return initializeApp(context, options);
    }

    public static FirebaseApp t(Context context, FirebaseOptions options, String name) {
        return initializeApp(context, options, name);
    }

    public static List<FirebaseApp> getApps(Context context) {
        List<FirebaseApp> apps = new ArrayList<>();
        if (sDefault != null) apps.add(sDefault);
        return apps;
    }

    public Context getApplicationContext() { return mContext; }
    public String getName() { return mName; }
    public boolean isDefaultApp() { return DEFAULT_APP_NAME.equals(mName); }
    public void delete() { if (this == sDefault) sDefault = null; }

    public void h() {}
    public Context k() { return getApplicationContext(); }
    public String n() { return getName(); }
    public FirebaseOptions o() { return mOptions; }
    public String p() { return mName == null ? DEFAULT_APP_NAME : mName; }

    public Object i(Class cls) {
        if (cls == null) {
            return null;
        }
        try {
            String name = cls.getName();
            if ("com.google.firebase.auth.FirebaseAuth".equals(name)) {
                return com.google.firebase.auth.FirebaseAuth.getInstance(this);
            }
            if ("com.google.firebase.messaging.FirebaseMessaging".equals(name)) {
                return com.google.firebase.messaging.FirebaseMessaging.getInstance();
            }
            if ("com.google.firebase.crashlytics.FirebaseCrashlytics".equals(name)) {
                return com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance();
            }
            return cls.getConstructor().newInstance();
        } catch (Throwable ignored) {
            return null;
        }
    }
}
