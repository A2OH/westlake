package sun.security.provider;

import java.security.MessageDigestSpi;

/**
 * Minimal SHA-2 provider implementations used by the standalone Westlake
 * security provider. This covers the MessageDigest.SHA-256 service that Glide
 * uses for cache key generation.
 */
public class SHA2 {
    public static final class SHA256 extends MessageDigestSpi {
        private byte[] buffer = new byte[0];

        @Override
        protected void engineUpdate(byte input) {
            byte[] old = buffer;
            buffer = new byte[old.length + 1];
            System.arraycopy(old, 0, buffer, 0, old.length);
            buffer[old.length] = input;
        }

        @Override
        protected void engineUpdate(byte[] input, int offset, int len) {
            if (input == null || len <= 0) {
                return;
            }
            byte[] old = buffer;
            buffer = new byte[old.length + len];
            System.arraycopy(old, 0, buffer, 0, old.length);
            System.arraycopy(input, offset, buffer, old.length, len);
        }

        @Override
        protected byte[] engineDigest() {
            byte[] digest = digest(buffer);
            engineReset();
            return digest;
        }

        @Override
        protected void engineReset() {
            buffer = new byte[0];
        }

        @Override
        protected int engineGetDigestLength() {
            return 32;
        }

        private static byte[] digest(byte[] message) {
            int[] h = new int[] {
                    0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a,
                    0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
            };
            int[] k = new int[] {
                    0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5,
                    0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
                    0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3,
                    0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
                    0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc,
                    0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
                    0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7,
                    0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
                    0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
                    0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
                    0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3,
                    0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
                    0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5,
                    0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
                    0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208,
                    0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
            };

            int msgLen = message.length;
            int padLen = 64 - ((msgLen + 9) % 64);
            if (padLen == 64) {
                padLen = 0;
            }
            byte[] padded = new byte[msgLen + 1 + padLen + 8];
            System.arraycopy(message, 0, padded, 0, msgLen);
            padded[msgLen] = (byte) 0x80;
            long bitLen = (long) msgLen * 8L;
            for (int i = 0; i < 8; i++) {
                padded[padded.length - 1 - i] = (byte) (bitLen >>> (i * 8));
            }

            int[] w = new int[64];
            for (int chunk = 0; chunk < padded.length; chunk += 64) {
                for (int i = 0; i < 16; i++) {
                    int p = chunk + i * 4;
                    w[i] = ((padded[p] & 0xff) << 24)
                            | ((padded[p + 1] & 0xff) << 16)
                            | ((padded[p + 2] & 0xff) << 8)
                            | (padded[p + 3] & 0xff);
                }
                for (int i = 16; i < 64; i++) {
                    int s0 = rotr(w[i - 15], 7) ^ rotr(w[i - 15], 18)
                            ^ (w[i - 15] >>> 3);
                    int s1 = rotr(w[i - 2], 17) ^ rotr(w[i - 2], 19)
                            ^ (w[i - 2] >>> 10);
                    w[i] = w[i - 16] + s0 + w[i - 7] + s1;
                }

                int a = h[0];
                int b = h[1];
                int c = h[2];
                int d = h[3];
                int e = h[4];
                int f = h[5];
                int g = h[6];
                int hh = h[7];
                for (int i = 0; i < 64; i++) {
                    int s1 = rotr(e, 6) ^ rotr(e, 11) ^ rotr(e, 25);
                    int ch = (e & f) ^ ((~e) & g);
                    int temp1 = hh + s1 + ch + k[i] + w[i];
                    int s0 = rotr(a, 2) ^ rotr(a, 13) ^ rotr(a, 22);
                    int maj = (a & b) ^ (a & c) ^ (b & c);
                    int temp2 = s0 + maj;
                    hh = g;
                    g = f;
                    f = e;
                    e = d + temp1;
                    d = c;
                    c = b;
                    b = a;
                    a = temp1 + temp2;
                }
                h[0] += a;
                h[1] += b;
                h[2] += c;
                h[3] += d;
                h[4] += e;
                h[5] += f;
                h[6] += g;
                h[7] += hh;
            }

            byte[] out = new byte[32];
            for (int i = 0; i < h.length; i++) {
                out[i * 4] = (byte) (h[i] >>> 24);
                out[i * 4 + 1] = (byte) (h[i] >>> 16);
                out[i * 4 + 2] = (byte) (h[i] >>> 8);
                out[i * 4 + 3] = (byte) h[i];
            }
            return out;
        }

        private static int rotr(int value, int bits) {
            return (value >>> bits) | (value << (32 - bits));
        }
    }
}
