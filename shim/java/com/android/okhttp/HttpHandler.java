package com.android.okhttp;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * Minimal Android built-in HTTP URL handler.
 *
 * Android libcore's java.net.URL directly reflects this class for "http"
 * URLs. Stock APKs hit that path even when they only construct a URL during
 * crypto/provider initialization, before any network I/O is attempted.
 */
public class HttpHandler extends URLStreamHandler {
    public HttpHandler() {
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        return new WestlakeHttpURLConnection(url);
    }

    @Override
    protected URLConnection openConnection(URL url, Proxy proxy) throws IOException {
        if (url == null || proxy == null) {
            throw new IllegalArgumentException("url == null || proxy == null");
        }
        return new WestlakeHttpURLConnection(url);
    }

    @Override
    protected int getDefaultPort() {
        return 80;
    }
}
