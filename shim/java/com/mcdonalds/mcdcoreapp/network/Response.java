package com.mcdonalds.mcdcoreapp.network;

import java.net.HttpURLConnection;

public class Response<T> {
    public final Request a;
    public int b;
    public String c;
    public Object d;
    public Object e;
    public HttpURLConnection f;

    public Response(Request request) {
        a = request;
    }

    public Object a() {
        return d;
    }

    public boolean b() {
        return b / 100 == 2;
    }

    public void c(Object value) {
        d = value;
    }
}
