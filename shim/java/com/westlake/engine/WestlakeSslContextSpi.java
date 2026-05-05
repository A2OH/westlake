// PF-noice-002a (2026-05-04): permissive SSLContextSpi so
// SSLContext.getInstance("TLS") succeeds in the Westlake guest. INSECURE
// — bypasses verification — but unblocks noice's network-stack init so
// UI can paint. Pair with WestlakeTrustManagerFactorySpi.
package com.westlake.engine;

import java.security.KeyManagementException;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContextSpi;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class WestlakeSslContextSpi extends SSLContextSpi {
    @Override
    protected void engineInit(KeyManager[] km, TrustManager[] tm, SecureRandom sr)
            throws KeyManagementException {
        // no-op
    }

    @Override
    protected SSLSocketFactory engineGetSocketFactory() {
        // PF-noice (2026-05-04): return a stub factory that constructs but
        // throws on actual createSocket() calls. Avoids recursing into
        // SSLSocketFactory.getDefault() which goes back through SSLContext.
        return new SSLSocketFactory() {
            @Override public String[] getDefaultCipherSuites() { return new String[0]; }
            @Override public String[] getSupportedCipherSuites() { return new String[0]; }
            @Override public java.net.Socket createSocket(java.net.Socket s, String host, int port, boolean autoClose) {
                throw new UnsupportedOperationException("Westlake stub SSLSocketFactory");
            }
            @Override public java.net.Socket createSocket(String host, int port) {
                throw new UnsupportedOperationException("Westlake stub SSLSocketFactory");
            }
            @Override public java.net.Socket createSocket(String host, int port, java.net.InetAddress localHost, int localPort) {
                throw new UnsupportedOperationException("Westlake stub SSLSocketFactory");
            }
            @Override public java.net.Socket createSocket(java.net.InetAddress host, int port) {
                throw new UnsupportedOperationException("Westlake stub SSLSocketFactory");
            }
            @Override public java.net.Socket createSocket(java.net.InetAddress address, int port, java.net.InetAddress localAddress, int localPort) {
                throw new UnsupportedOperationException("Westlake stub SSLSocketFactory");
            }
        };
    }

    @Override
    protected SSLServerSocketFactory engineGetServerSocketFactory() {
        throw new UnsupportedOperationException(
                "Westlake permissive SSLContext: engineGetServerSocketFactory not implemented");
    }

    @Override
    protected SSLEngine engineCreateSSLEngine() {
        throw new UnsupportedOperationException("Westlake permissive SSLContext: SSLEngine not implemented");
    }

    @Override
    protected SSLEngine engineCreateSSLEngine(String host, int port) {
        return engineCreateSSLEngine();
    }

    @Override
    protected SSLSessionContext engineGetServerSessionContext() {
        return null;
    }

    @Override
    protected SSLSessionContext engineGetClientSessionContext() {
        return null;
    }

    @Override
    protected SSLParameters engineGetDefaultSSLParameters() {
        return new SSLParameters();
    }

    @Override
    protected SSLParameters engineGetSupportedSSLParameters() {
        return new SSLParameters();
    }
}
