package com.android.org.conscrypt;

import java.security.SecureRandomSpi;

/**
 * Minimal SecureRandom implementation using /dev/urandom.
 * Enough for UUID.randomUUID() and basic crypto seeding.
 */
public class OpenSSLRandom extends SecureRandomSpi {
    private java.util.Random fallback;

    public OpenSSLRandom() {
        this.fallback = new java.util.Random(System.nanoTime() ^ System.currentTimeMillis());
    }

    @Override
    protected void engineSetSeed(byte[] seed) {
        long s = 0;
        for (int i = 0; i < Math.min(seed.length, 8); i++) {
            s = (s << 8) | (seed[i] & 0xFF);
        }
        fallback = new java.util.Random(s);
    }

    @Override
    protected void engineNextBytes(byte[] bytes) {
        // Try /dev/urandom first
        try {
            java.io.FileInputStream fis = new java.io.FileInputStream("/dev/urandom");
            try {
                int off = 0;
                while (off < bytes.length) {
                    int n = fis.read(bytes, off, bytes.length - off);
                    if (n <= 0) break;
                    off += n;
                }
                if (off == bytes.length) return;
            } finally {
                fis.close();
            }
        } catch (Exception e) {
            // fall through to java.util.Random
        }
        // Fallback
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) fallback.nextInt(256);
        }
    }

    @Override
    protected byte[] engineGenerateSeed(int numBytes) {
        byte[] seed = new byte[numBytes];
        engineNextBytes(seed);
        return seed;
    }
}
