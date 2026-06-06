package android.net.ssl;
import javax.net.ssl.SSLSocket;
public class SSLSockets {
    private SSLSockets() {}
    public static boolean isSupportedSocket(SSLSocket socket) { return false; }
    public static void setUseSessionTickets(SSLSocket socket, boolean useSessionTickets) {}
}
