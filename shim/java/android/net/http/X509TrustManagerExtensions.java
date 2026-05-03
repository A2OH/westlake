package android.net.http;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.X509TrustManager;

public class X509TrustManagerExtensions {
    private final X509TrustManager trustManager;

    public X509TrustManagerExtensions() {
        this.trustManager = null;
    }

    public X509TrustManagerExtensions(X509TrustManager trustManager) {
        this.trustManager = trustManager;
    }

    public List<X509Certificate> checkServerTrusted(
            X509Certificate[] chain, String authType, String host) throws CertificateException {
        if (chain == null || chain.length == 0) {
            return Collections.emptyList();
        }
        if (trustManager != null) {
            trustManager.checkServerTrusted(chain, authType);
        }
        return Arrays.asList(chain);
    }

    public boolean isSameTrustConfiguration(String host1, String host2) {
        return host1 == null ? host2 == null : host1.equals(host2);
    }

    public boolean isUserAddedCertificate(X509Certificate cert) { return false; }

    public Object checkServerTrusted(Object p0, Object p1, Object p2) { return null; }
    public boolean isSameTrustConfiguration(Object p0, Object p1) { return false; }
    public boolean isUserAddedCertificate(Object p0) { return false; }
}
