package com.example.superapp;

import android.content.BroadcastReceiver;

/**
 * Simple BroadcastReceiver subclass for testing.
 */
public class TestReceiver extends BroadcastReceiver {
    public static boolean sReceived = false;
    public static Object sLastContext = null;
    public static Object sLastIntent = null;

    public static void reset() {
        sReceived = false;
        sLastContext = null;
        sLastIntent = null;
    }

    @Override
    public void onReceive(Object context, Object intent) {
        sReceived = true;
        sLastContext = context;
        sLastIntent = intent;
    }
}
