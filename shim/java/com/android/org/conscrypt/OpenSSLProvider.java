package com.android.org.conscrypt;

import java.security.Provider;

/**
 * Stub Conscrypt OpenSSL provider.
 * Satisfies sun.security.jca.Providers initialization check.
 * Real crypto operations fall through to BouncyCastle.
 */
public final class OpenSSLProvider extends Provider {
    public OpenSSLProvider() {
        super("AndroidOpenSSL", 1.0, "Westlake Conscrypt stub");
        // Register a minimal SecureRandom so UUID.randomUUID() works
        put("SecureRandom.SHA1PRNG", "com.android.org.conscrypt.OpenSSLRandom");
    }
}
