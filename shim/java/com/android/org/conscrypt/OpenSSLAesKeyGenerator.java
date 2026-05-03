package com.android.org.conscrypt;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.KeyGeneratorSpi;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Portable AES key generator for the Westlake provider surface.
 */
public final class OpenSSLAesKeyGenerator extends KeyGeneratorSpi {
    private int keySizeBits = 128;
    private SecureRandom random;

    @Override
    protected void engineInit(SecureRandom random) {
        this.random = random;
    }

    @Override
    protected void engineInit(AlgorithmParameterSpec params, SecureRandom random)
            throws InvalidAlgorithmParameterException {
        if (params != null) {
            throw new InvalidAlgorithmParameterException("AES key generation takes no parameters");
        }
        this.random = random;
    }

    @Override
    protected void engineInit(int keysize, SecureRandom random) {
        if (keysize != 128 && keysize != 192 && keysize != 256) {
            throw new InvalidParameterException("Unsupported AES key size: " + keysize);
        }
        this.keySizeBits = keysize;
        this.random = random;
    }

    @Override
    protected SecretKey engineGenerateKey() {
        SecureRandom rng = random != null ? random : new SecureRandom();
        byte[] key = new byte[keySizeBits / 8];
        rng.nextBytes(key);
        return new SecretKeySpec(key, "AES");
    }
}
