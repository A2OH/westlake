package android.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

/**
 * MiniServiceManager — manages Service lifecycle for a single app.
 *
 * Replaces Android's ActivityManagerService service management.
 * Handles:
 * - startService / stopService
 * - bindService / unbindService
 * - Service instantiation via reflection
 * - Lifecycle dispatch (onCreate → onStartCommand → onDestroy)
 */
public class MiniServiceManager {
    private static final String TAG = "MiniServiceManager";

    private final MiniServer mServer;
    private final Map<String, ServiceRecord> mRunning = new HashMap<>();
    private int mNextStartId = 1;

    MiniServiceManager(MiniServer server) {
        mServer = server;
    }

    /**
     * Start a service. Creates it if not already running.
     * @return the ComponentName of the started service, or null on failure
     */
    public ComponentName startService(Intent intent) {
        ComponentName component = resolveService(intent);
        if (component == null) {
            Log.w(TAG, "startService: cannot resolve " + intent);
            return null;
        }

        String className = component.getClassName();
        ServiceRecord record = mRunning.get(className);

        if (record == null) {
            // Create and start the service
            Service service = instantiateService(className);
            if (service == null) return null;

            record = new ServiceRecord();
            record.service = service;
            record.component = component;
            record.started = true;
            mRunning.put(className, record);

            service.onCreate();
        }

        int startId = mNextStartId++;
        record.service.onStartCommand(intent, 0, startId);
        record.started = true;

        Log.d(TAG, "startService: " + className + " startId=" + startId);
        return component;
    }

    /**
     * Stop a service.
     * @return true if the service was running and stopped
     */
    public boolean stopService(Intent intent) {
        ComponentName component = resolveService(intent);
        if (component == null) return false;

        String className = component.getClassName();
        ServiceRecord record = mRunning.get(className);
        if (record == null) return false;

        if (record.bindCount > 0) {
            // Still bound — mark as not started, will destroy on last unbind
            record.started = false;
            Log.d(TAG, "stopService: " + className + " still bound, deferring destroy");
            return true;
        }

        destroyService(className, record);
        return true;
    }

    /**
     * Bind to a service. Creates it if not already running.
     * @return true if successfully bound
     */
    public boolean bindService(Intent intent, ServiceConnection conn) {
        ComponentName component = resolveService(intent);
        if (component == null) {
            Log.w(TAG, "bindService: cannot resolve " + intent);
            return false;
        }

        String className = component.getClassName();
        ServiceRecord record = mRunning.get(className);

        if (record == null) {
            Service service = instantiateService(className);
            if (service == null) return false;

            record = new ServiceRecord();
            record.service = service;
            record.component = component;
            mRunning.put(className, record);

            service.onCreate();
        }

        record.bindCount++;
        record.connections.put(conn, true);

        // Call onBind and deliver the binder to the connection
        IBinder binder = record.service.onBind(intent);
        conn.onServiceConnected(component, binder);

        Log.d(TAG, "bindService: " + className + " bindCount=" + record.bindCount);
        return true;
    }

    /**
     * Unbind from a service.
     */
    public void unbindService(ServiceConnection conn) {
        for (Map.Entry<String, ServiceRecord> entry : mRunning.entrySet()) {
            ServiceRecord record = entry.getValue();
            if (record.connections.remove(conn) != null) {
                record.bindCount--;
                Log.d(TAG, "unbindService: " + entry.getKey()
                        + " bindCount=" + record.bindCount);

                conn.onServiceDisconnected(record.component);

                if (record.bindCount <= 0 && !record.started) {
                    destroyService(entry.getKey(), record);
                }
                return;
            }
        }
        Log.w(TAG, "unbindService: connection not found");
    }

    /**
     * Get a running service by class name.
     */
    public Service getService(String className) {
        ServiceRecord record = mRunning.get(className);
        return record != null ? record.service : null;
    }

    /**
     * Get the number of running services.
     */
    public int getRunningCount() {
        return mRunning.size();
    }

    /**
     * Stop all services (shutdown).
     */
    public void stopAll() {
        for (Map.Entry<String, ServiceRecord> entry :
                new HashMap<>(mRunning).entrySet()) {
            destroyService(entry.getKey(), entry.getValue());
        }
    }

    // ── Internal ────────────────────────────────────────────────────────────

    private ComponentName resolveService(Intent intent) {
        ComponentName component = intent.getComponent();
        if (component != null) return component;

        // Implicit resolution via MiniPackageManager
        android.content.pm.MiniPackageManager pm = mServer.getPackageManager();
        if (pm != null) {
            android.content.pm.ResolveInfo ri = pm.resolveService(intent);
            if (ri != null && ri.resolvedComponentName != null) {
                return ri.resolvedComponentName;
            }
        }
        return null;
    }

    private Service instantiateService(String className) {
        try {
            Class<?> cls = Class.forName(className);
            return (Service) cls.newInstance();
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Service class not found: " + className);
        } catch (Exception e) {
            Log.e(TAG, "Failed to instantiate " + className + ": " + e.getMessage());
        }
        return null;
    }

    private void destroyService(String className, ServiceRecord record) {
        Log.d(TAG, "destroyService: " + className);
        record.service.onDestroy();
        mRunning.remove(className);
    }

    static class ServiceRecord {
        Service service;
        ComponentName component;
        boolean started;
        int bindCount;
        Map<ServiceConnection, Boolean> connections = new HashMap<>();
    }
}
