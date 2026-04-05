package com.mcdonalds.mcdcoreapp.common;

import android.content.Context;

/**
 * Stub Crypto — passthrough encryption/decryption for Westlake.
 * Shadows the real Crypto class from McDonald's DEX via boot classpath priority.
 */
public class Crypto {
    public static final String c = "Crypto";
    public static final java.security.SecureRandom d = new java.security.SecureRandom();
    public javax.crypto.SecretKey a;
    public Context b;

    public Crypto(Context context) {
        this.b = context;
    }

    /** Encrypt — passthrough (no real encryption needed for Westlake) */
    public String e(String input) {
        return input != null ? input : "";
    }

    /** Decrypt — passthrough */
    public String f(String input) {
        return input != null ? input : "";
    }

    /** Encrypt bytes */
    public byte[] a(String input) {
        return input != null ? input.getBytes() : new byte[0];
    }

    /** Decrypt bytes */
    public String b(byte[] input) {
        return input != null ? new String(input) : "";
    }
}
