package android.os;

import java.io.*;

/**
 * Android-compatible MemoryFile shim. Uses byte array as backing store
 * (real Android uses ashmem shared memory).
 */
public class MemoryFile {
    private byte[] mBuffer;
    private int mLength;
    private boolean mAllowPurging;

    public MemoryFile(String name, int length) throws IOException {
        mLength = length;
        mBuffer = new byte[length];
    }

    public int length() {
        return mLength;
    }

    public void writeBytes(byte[] buffer, int srcOffset, int destOffset, int count) throws IOException {
        checkActive();
        System.arraycopy(buffer, srcOffset, mBuffer, destOffset, count);
    }

    public int readBytes(byte[] buffer, int srcOffset, int destOffset, int count) throws IOException {
        checkActive();
        int avail = Math.min(count, mLength - srcOffset);
        System.arraycopy(mBuffer, srcOffset, buffer, destOffset, avail);
        return avail;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(mBuffer, 0, mLength);
    }

    public OutputStream getOutputStream() {
        return new OutputStream() {
            private int pos = 0;
            @Override
            public void write(int b) throws IOException {
                checkActive();
                if (pos < mLength) mBuffer[pos++] = (byte) b;
            }
            @Override
            public void write(byte[] buf, int off, int len) throws IOException {
                checkActive();
                int count = Math.min(len, mLength - pos);
                System.arraycopy(buf, off, mBuffer, pos, count);
                pos += count;
            }
        };
    }

    public boolean allowPurging(boolean allowPurging) throws IOException {
        boolean old = mAllowPurging;
        mAllowPurging = allowPurging;
        return old;
    }

    public boolean isPurgingAllowed() {
        return mAllowPurging;
    }

    public void close() {
        mBuffer = null;
    }

    private void checkActive() throws IOException {
        if (mBuffer == null) throw new IOException("MemoryFile has been closed");
    }
}
