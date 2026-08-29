// android.net.LinkProperties — mainline stub (WESTLAKE §418).
package android.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;

public class LinkProperties {
    private static final List<InetAddress> SYSTEM_DNS_SERVERS = readSystemDnsServers();

    public LinkProperties() {}
    public String getInterfaceName() { return "wlan0"; }
    /**
     * Return the name servers exported by the OH host network boundary.
     *
     * Chromium's Android DnsConfigService treats an empty list as an unusable
     * configuration.  The earlier compatibility shell always returned an
     * empty list even though libc and /etc/resolv.conf had working resolvers;
     * top-level HTML injected by an app could therefore render while linked
     * CSS, scripts, and images all failed DNS resolution.  Parse the host's
     * resolver file once instead of baking a board- or app-specific address
     * into the Android facade.
     */
    public List<InetAddress> getDnsServers() { return SYSTEM_DNS_SERVERS; }

    private static List<InetAddress> readSystemDnsServers() {
        ArrayList<InetAddress> servers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("/etc/resolv.conf"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0 || line.charAt(0) == '#') continue;
                String[] fields = line.split("\\s+");
                if (fields.length >= 2 && "nameserver".equals(fields[0])) {
                    try {
                        InetAddress address = InetAddress.getByName(fields[1]);
                        if (!servers.contains(address)) servers.add(address);
                    } catch (Exception ignored) { }
                }
            }
        } catch (Exception ignored) { }
        return Collections.unmodifiableList(servers);
    }
    public List<LinkAddress> getLinkAddresses() { return new ArrayList<>(); }
    public List<RouteInfo> getRoutes() { return new ArrayList<>(); }
    public String getDomains() { return null; }
    public ProxyInfo getHttpProxy() { return null; }
    public int getMtu() { return 1500; }
    public boolean isPrivateDnsActive() { return false; }
    public String getPrivateDnsServerName() { return null; }
}
