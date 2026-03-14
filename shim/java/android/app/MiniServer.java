package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.MiniPackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;

/**
 * MiniServer — replaces Android's SystemServer for single-app engine execution.
 *
 * Instead of 80+ system services communicating via Binder IPC, this is a single
 * in-process Java object that manages one app's Activity stack, window, package info,
 * and service lifecycle.
 *
 * Usage:
 *   MiniServer.init(packageName);
 *   MiniServer.get().startActivity(launcherIntent);
 */
public class MiniServer {
    private static MiniServer sInstance;

    private final MiniActivityManager mActivityManager;
    private final MiniServiceManager mServiceManager;
    private final MiniPackageManager mPackageManager;
    private final Application mApplication;
    private String mPackageName;

    private MiniServer(String packageName) {
        mPackageName = packageName;
        mApplication = new Application();
        mApplication.setPackageName(packageName);
        mActivityManager = new MiniActivityManager(this);
        mServiceManager = new MiniServiceManager(this);
        mPackageManager = new MiniPackageManager(packageName);
        SystemServiceRegistry.init();
        // Register LayoutInflater as a system service
        SystemServiceRegistry.registerService(Context.LAYOUT_INFLATER_SERVICE,
                new LayoutInflater(mApplication));
    }

    /** Initialize the MiniServer singleton. Call once at engine startup. */
    public static void init(String packageName) {
        sInstance = new MiniServer(packageName);
        sInstance.mApplication.onCreate();
    }

    /** Get the singleton instance. */
    public static MiniServer get() {
        if (sInstance == null) {
            // Auto-init with default package for testing
            init("com.example.app");
        }
        return sInstance;
    }

    public MiniActivityManager getActivityManager() { return mActivityManager; }
    public MiniServiceManager getServiceManager() { return mServiceManager; }
    public MiniPackageManager getPackageManager() { return mPackageManager; }
    public Application getApplication() { return mApplication; }
    public String getPackageName() { return mPackageName; }

    /**
     * Start an Activity by class name (convenience for testing).
     * Creates an explicit Intent and delegates to MiniActivityManager.
     */
    public void startActivity(String activityClassName) {
        try {
            Class<?> cls = Class.forName(activityClassName);
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(mPackageName, activityClassName));
            mActivityManager.startActivity(null, intent, -1);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Activity class not found: " + activityClassName, e);
        }
    }

    /**
     * Start an Activity from an Intent.
     * Handles implicit intent resolution via MiniPackageManager.
     */
    public void startActivity(Intent intent) {
        // If no component set, try implicit resolution
        if (intent.getComponent() == null) {
            android.content.pm.ResolveInfo ri = mPackageManager.resolveActivity(intent);
            if (ri != null && ri.resolvedComponentName != null) {
                intent.setComponent(ri.resolvedComponentName);
            }
        }
        mActivityManager.startActivity(null, intent, -1);
    }

    /** Shut down: destroy all services and activities, call Application.onTerminate(). */
    public void shutdown() {
        mServiceManager.stopAll();
        mActivityManager.finishAll();
        mApplication.onTerminate();
    }
}
