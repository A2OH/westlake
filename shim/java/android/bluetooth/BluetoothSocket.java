package android.bluetooth;

import java.io.*;

/**
 * Android-compatible BluetoothSocket shim. Stub.
 */
public class BluetoothSocket implements Closeable {
    private boolean mConnected;

    public void connect() throws IOException {
        throw new IOException("Bluetooth not available in shim layer");
    }

    public boolean isConnected() { return mConnected; }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(new byte[0]);
    }

    public OutputStream getOutputStream() throws IOException {
        return new ByteArrayOutputStream();
    }

    @Override
    public void close() throws IOException {
        mConnected = false;
    }
}
