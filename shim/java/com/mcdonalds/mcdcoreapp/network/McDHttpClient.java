package com.mcdonalds.mcdcoreapp.network;

import com.westlake.engine.WestlakeLauncher;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

public class McDHttpClient {
    public SSLSocketFactory a;
    public HostnameVerifier b;

    public final String a(String url) {
        if (url == null) {
            throw new IllegalArgumentException("pathOrUri must not be null");
        }
        return url;
    }

    public void b(boolean closeConnection, HttpURLConnection connection, InputStream stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (Throwable ignored) {
        }
        if (closeConnection && connection != null) {
            try {
                connection.disconnect();
            } catch (Throwable ignored) {
            }
        }
    }

    public Response c(Request request, Class responseClass) {
        Response response = d(request, responseClass);
        if (response == null) {
            throw new IllegalStateException("Network response must not be null");
        }
        return response;
    }

    public final Response d(Request request, Class responseClass) {
        Response response = new Response(request);
        response.b = 204;
        response.c = "No Content";
        response.d = "{}";

        String url = request != null ? request.c : null;
        int timeoutMs = request != null ? request.d : 8000;
        if (url == null || url.length() == 0) {
            marker("PFCUT-MCD-NET shadow request empty url");
            return response;
        }

        try {
            WestlakeLauncher.BridgeHttpResponse bridge =
                    WestlakeLauncher.bridgeHttpRequest(
                            url, "GET", "{}", null, 4 * 1024 * 1024,
                            timeoutMs > 0 ? timeoutMs : 8000, true);
            response.b = bridge.status;
            response.c = bridge.error == null ? "OK" : bridge.error;
            String json = new String(bridge.body, "UTF-8");
            response.d = parseResponse(responseClass, json);
            marker("PFCUT-MCD-NET shadow client response status=" + bridge.status
                    + " bytes=" + bridge.body.length + " url=" + safeToken(url));
        } catch (Throwable t) {
            response.b = 500;
            response.c = t.getClass().getName();
            response.d = "{}";
            marker("PFCUT-MCD-NET shadow client failed err=" + t.getClass().getName()
                    + " url=" + safeToken(url));
        }
        return response;
    }

    public Request e(String url, int timeoutMs) {
        return new Request(this, Request.Method.GET, a(url), timeoutMs);
    }

    public final String f(Request request, String url) {
        return url;
    }

    public final void g(HttpURLConnection connection) {
    }

    public static class AutoDisconnectInputStream extends FilterInputStream {
        public final HttpURLConnection a;

        public AutoDisconnectInputStream(HttpURLConnection connection, InputStream stream) {
            super(stream);
            a = connection;
        }

        public void close() throws java.io.IOException {
            try {
                super.close();
            } finally {
                if (a != null) {
                    a.disconnect();
                }
            }
        }

        public int read(byte[] buffer, int offset, int count) throws java.io.IOException {
            return super.read(buffer, offset, count);
        }
    }

    private static Object parseResponse(Class target, String json) {
        if (json == null) {
            return null;
        }
        if (target == null || target == String.class || Object.class.equals(target)) {
            return json;
        }
        try {
            ClassLoader cl = target.getClassLoader();
            Class gsonClass = cl != null
                    ? cl.loadClass("com.google.gson.Gson")
                    : Class.forName("com.google.gson.Gson");
            Object gson = gsonClass.newInstance();
            return gsonClass.getMethod("fromJson", String.class, Class.class)
                    .invoke(gson, json, target);
        } catch (Throwable t) {
            marker("PFCUT-MCD-NET shadow client gson failed target="
                    + target.getName() + " err=" + t.getClass().getName());
            return json;
        }
    }

    private static void marker(String message) {
        try {
            WestlakeLauncher.marker(message);
        } catch (Throwable ignored) {
        }
    }

    private static String safeToken(String value) {
        if (value == null) {
            return "none";
        }
        StringBuilder sb = new StringBuilder();
        int limit = Math.min(value.length(), 96);
        for (int i = 0; i < limit; i++) {
            char ch = value.charAt(i);
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')
                    || (ch >= '0' && ch <= '9')) {
                sb.append(ch);
            } else {
                sb.append('_');
            }
        }
        return sb.toString();
    }
}
