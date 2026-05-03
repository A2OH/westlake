package com.newrelic.agent.android.background;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class ApplicationStateMonitor {
    private static ApplicationStateMonitor instance = new ApplicationStateMonitor();

    private final AtomicLong activityCount = new AtomicLong();
    protected final ArrayList applicationStateListeners = new ArrayList();
    protected final ExecutorService executor = Executors.newSingleThreadExecutor();
    protected final AtomicBoolean foregrounded = new AtomicBoolean();

    public static ApplicationStateMonitor getInstance() {
        if (instance == null) {
            instance = new ApplicationStateMonitor();
        }
        return instance;
    }

    public static void setInstance(ApplicationStateMonitor monitor) {
        instance = monitor != null ? monitor : new ApplicationStateMonitor();
    }

    public static boolean isAppInBackground() {
        return !getInstance().getForegrounded();
    }

    public void activityStarted() {
        long count = activityCount.incrementAndGet();
        if (count > 0 && foregrounded.compareAndSet(false, true)) {
            notifyApplicationInForeground();
        }
    }

    public void activityStopped() {
        long count = activityCount.decrementAndGet();
        if (count <= 0) {
            activityCount.set(0);
            uiHidden();
        }
    }

    public void uiHidden() {
        if (foregrounded.compareAndSet(true, false)) {
            notifyApplicationInBackground();
        }
    }

    public void addApplicationStateListener(ApplicationStateListener listener) {
        if (listener != null && !applicationStateListeners.contains(listener)) {
            applicationStateListeners.add(listener);
        }
    }

    public void removeApplicationStateListener(ApplicationStateListener listener) {
        applicationStateListeners.remove(listener);
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public boolean getForegrounded() {
        return foregrounded.get();
    }

    private void notifyApplicationInForeground() {
        for (int i = 0; i < applicationStateListeners.size(); i++) {
            try {
                ((ApplicationStateListener) applicationStateListeners.get(i))
                        .applicationForegrounded(ApplicationStateEvent.FOREGROUND);
            } catch (Throwable ignored) {
            }
        }
    }

    private void notifyApplicationInBackground() {
        for (int i = 0; i < applicationStateListeners.size(); i++) {
            try {
                ((ApplicationStateListener) applicationStateListeners.get(i))
                        .applicationBackgrounded(ApplicationStateEvent.BACKGROUND);
            } catch (Throwable ignored) {
            }
        }
    }
}
