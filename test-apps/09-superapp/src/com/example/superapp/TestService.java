package com.example.superapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Simple Service subclass for testing lifecycle.
 */
public class TestService extends Service {
    public static boolean sCreated = false;
    public static boolean sDestroyed = false;
    public static int sStartCommandCount = 0;
    public static int sLastStartId = -1;
    public static Intent sLastIntent = null;

    public static void reset() {
        sCreated = false;
        sDestroyed = false;
        sStartCommandCount = 0;
        sLastStartId = -1;
        sLastIntent = null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sCreated = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sStartCommandCount++;
        sLastStartId = startId;
        sLastIntent = intent;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sDestroyed = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
