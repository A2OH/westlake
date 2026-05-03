package com.newrelic.agent.android.background;

public interface ApplicationStateListener {
    void applicationForegrounded(ApplicationStateEvent event);
    void applicationBackgrounded(ApplicationStateEvent event);
}
