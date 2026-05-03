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
        put("KeyGenerator.AES", "com.android.org.conscrypt.OpenSSLAesKeyGenerator");
        put("Alg.Alias.KeyGenerator.2.16.840.1.101.3.4.1", "AES");
        put("Cipher.AES", "com.android.org.bouncycastle.jcajce.provider.symmetric.AES$ECB");
        put("Cipher.AES/CBC", "com.android.org.bouncycastle.jcajce.provider.symmetric.AES$CBC");
        put("Cipher.AES/CBC/PKCS5Padding", "com.android.org.bouncycastle.jcajce.provider.symmetric.AES$CBC");
        put("Alg.Alias.Cipher.2.16.840.1.101.3.4.2", "AES");
        put("Alg.Alias.Cipher.2.16.840.1.101.3.4.22", "AES");
        put("Alg.Alias.Cipher.2.16.840.1.101.3.4.42", "AES");
    }
}
