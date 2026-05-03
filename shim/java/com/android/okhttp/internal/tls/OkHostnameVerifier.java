package com.android.okhttp.internal.tls;

import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public final class OkHostnameVerifier implements HostnameVerifier {
    public static final OkHostnameVerifier INSTANCE = new OkHostnameVerifier();

    private OkHostnameVerifier() {
    }

    @Override
    public boolean verify(String host, SSLSession session) {
        return host != null && host.length() > 0;
    }

    public boolean verify(String host, X509Certificate certificate) {
        return host != null && host.length() > 0 && certificate != null;
    }

    public List<String> allSubjectAltNames(X509Certificate certificate) {
        return Collections.emptyList();
    }
}
