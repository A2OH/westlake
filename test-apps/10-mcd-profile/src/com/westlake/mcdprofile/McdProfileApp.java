package com.westlake.mcdprofile;

import android.app.Application;

public final class McdProfileApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        McdProfileLog.mark("APP_ON_CREATE_OK",
                "application=" + getClass().getName() + " package=" + getPackageName());
    }
}
