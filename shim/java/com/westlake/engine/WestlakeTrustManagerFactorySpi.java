// PF-noice-002a (2026-05-04): permissive TrustManagerFactorySpi so
// TrustManagerFactory.getInstance("SunX509") succeeds in the Westlake
// guest runtime where Conscrypt's PKIX impl isn't reachable via the
// alias-only path. This is INSECURE — accepts any cert chain — but
// unblocks noice's network-stack init so its UI can paint. Real SSL
// trust integration is a separate workstream (PF-noice-002a-secure).
package com.westlake.engine;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;

public class WestlakeTrustManagerFactorySpi extends TrustManagerFactorySpi {
    @Override
    protected void engineInit(KeyStore ks) {
        // no-op — accepts all certs at engineGetTrustManagers
    }

    @Override
    protected void engineInit(ManagerFactoryParameters spec) {
        // no-op
    }

    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return new TrustManager[] {
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    // permissive — accept all
                }
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    // permissive — accept all
                }
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }
        };
    }
}
