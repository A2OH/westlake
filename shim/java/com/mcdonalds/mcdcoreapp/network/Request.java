package com.mcdonalds.mcdcoreapp.network;

import java.util.Map;

public class Request {
    public final McDHttpClient a;
    public final Method b;
    public final String c;
    public final int d;
    public Map e;

    public enum Method {
        GET
    }

    public Request() {
        this(new McDHttpClient(), Method.GET, null, 0);
    }

    public Request(String url, int timeoutMs) {
        this(new McDHttpClient(), Method.GET, url, timeoutMs);
    }

    public Request(McDHttpClient client, Method method, String url, int timeoutMs) {
        a = client != null ? client : new McDHttpClient();
        b = method != null ? method : Method.GET;
        c = url;
        d = timeoutMs;
    }

    public Response a() {
        return a.c(this, String.class);
    }

    public int b() {
        return d;
    }
}
