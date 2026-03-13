package android.net;

import java.io.*;
import java.net.*;

/**
 * Android-compatible LocalServerSocket shim. Uses TCP loopback as substitute
 * for Unix domain sockets (which require JDK 16+ UnixDomainSocketAddress).
 */
public class LocalServerSocket implements Closeable {
    private final ServerSocket mServer;
    private final String mName;

    public LocalServerSocket(String name) throws IOException {
        mName = name;
        mServer = new ServerSocket(0, 5, InetAddress.getLoopbackAddress());
    }

    public LocalSocket accept() throws IOException {
        Socket s = mServer.accept();
        return new LocalSocket(s);
    }

    public int getLocalPort() {
        return mServer.getLocalPort();
    }

    public String getName() { return mName; }

    @Override
    public void close() throws IOException {
        mServer.close();
    }
}
