package com.mcdonalds.mcdcoreapp.network;

import com.mcdonalds.mcdcoreapp.listeners.McDListener;
import com.westlake.engine.WestlakeLauncher;

public class McDRequestManager {
    public static McDHttpClient a;
    public static McDRequestManager b;

    public McDRequestManager() {
    }

    public static Response a(RequestProvider provider) {
        return d(provider);
    }

    public static void b() {
        if (a == null) {
            a = new McDHttpClient();
        }
    }

    public static McDRequestManager c() {
        if (b == null) {
            b = new McDRequestManager();
        }
        return b;
    }

    public static Response d(RequestProvider provider) {
        b();
        String url = null;
        int timeoutMs = 8000;
        try {
            if (provider != null) {
                url = provider.b();
                int requested = provider.a();
                if (requested > 0) {
                    timeoutMs = requested;
                }
            }
        } catch (Throwable t) {
            marker("PFCUT-MCD-NET provider read failed err=" + t.getClass().getName());
        }

        Response response = new Response(url != null ? new Request(url, timeoutMs) : null);
        response.b = 204;
        response.c = "No Content";
        response.d = "{}";

        if (url == null || url.length() == 0) {
            marker("PFCUT-MCD-NET shadow empty provider url");
            return response;
        }

        try {
            WestlakeLauncher.BridgeHttpResponse bridge =
                    WestlakeLauncher.bridgeHttpRequest(
                            url, "GET", "{}", null, 4 * 1024 * 1024,
                            timeoutMs > 0 ? timeoutMs : 8000, true);
            response.b = bridge.status;
            response.c = bridge.error == null ? "OK" : bridge.error;
            response.d = new String(bridge.body, "UTF-8");
            marker("PFCUT-MCD-NET shadow bridge response status=" + bridge.status
                    + " bytes=" + bridge.body.length + " url=" + safeToken(url));
        } catch (Throwable t) {
            response.b = 500;
            response.c = t.getClass().getName();
            response.d = "{}";
            marker("PFCUT-MCD-NET shadow bridge failed err=" + t.getClass().getName()
                    + " url=" + safeToken(url));
        }
        return response;
    }

    public void e(RequestProvider provider, McDListener listener) {
        Response response = d(provider);
        Object parsed = response.a();
        if (response.b() && parsed instanceof String) {
            parsed = parseResponse(provider, (String) parsed);
        }
        try {
            if (listener != null) {
                listener.onResponse(parsed, null, null);
            }
        } catch (Throwable t) {
            marker("PFCUT-MCD-NET shadow listener failed err=" + t.getClass().getName());
        }
    }

    private static Object parseResponse(RequestProvider provider, String json) {
        if (json == null) {
            return null;
        }
        Class target = null;
        try {
            target = provider != null ? provider.c() : null;
        } catch (Throwable ignored) {
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
            marker("PFCUT-MCD-NET shadow gson failed target="
                    + target.getName() + " err=" + t.getClass().getName());
            return null;
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
