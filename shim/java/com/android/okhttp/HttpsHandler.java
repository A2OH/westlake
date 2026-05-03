package com.android.okhttp;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

/**
 * Minimal Android built-in HTTPS URL handler.
 *
 * The full Android implementation delegates to the platform OkHttp stack.
 * Westlake keeps this shim portable and only claims the URL handler class
 * contract here; actual HTTPS transport remains a separate southbound bridge.
 */
public final class HttpsHandler extends HttpHandler {
    public HttpsHandler() {
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        return new WestlakeHttpsURLConnection(url);
    }

    @Override
    protected URLConnection openConnection(URL url, Proxy proxy) throws IOException {
        if (url == null || proxy == null) {
            throw new IllegalArgumentException("url == null || proxy == null");
        }
        return new WestlakeHttpsURLConnection(url);
    }

    @Override
    protected int getDefaultPort() {
        return 443;
    }
}
