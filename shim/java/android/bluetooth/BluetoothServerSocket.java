package android.bluetooth;

import java.io.Closeable;
import java.io.IOException;

/**
 * Android-compatible BluetoothServerSocket shim. Stub.
 */
public class BluetoothServerSocket implements Closeable {
    private boolean mClosed;

    public BluetoothSocket accept() throws IOException {
        return accept(-1);
    }

    public BluetoothSocket accept(int timeout) throws IOException {
        throw new IOException("Bluetooth not available in shim layer");
    }

    @Override
    public void close() throws IOException {
        mClosed = true;
    }

    public boolean isClosed() { return mClosed; }
}
