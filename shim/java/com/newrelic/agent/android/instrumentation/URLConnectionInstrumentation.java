package com.newrelic.agent.android.instrumentation;

import java.net.URLConnection;

/**
 * Portable New Relic URLConnection wrapper cut.
 *
 * The real agent wraps HTTP connections for telemetry. Westlake keeps the
 * stock app behavior moving by making that wrapper inert and returning the
 * platform connection unchanged.
 */
public final class URLConnectionInstrumentation {
    private URLConnectionInstrumentation() {
    }

    public static URLConnection openConnection(URLConnection connection) {
        return connection;
    }

    public static URLConnection openConnectionWithProxy(URLConnection connection) {
        return connection;
    }
}
