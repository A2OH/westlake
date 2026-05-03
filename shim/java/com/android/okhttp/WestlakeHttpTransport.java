package com.android.okhttp;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import com.westlake.engine.WestlakeLauncher;

final class WestlakeHttpTransport {
    static final int CONNECT_TIMEOUT_MS = 15000;
    static final int READ_TIMEOUT_MS = 20000;

    static final class Response {
        Socket socket;
        InputStream body;
        Map<String, List<String>> headers;
        int code;
        String message;
        SSLSession sslSession;
    }

    interface SocketFactory {
        Socket open(URL url, int port) throws IOException;
    }

    private WestlakeHttpTransport() {
    }

    static Response execute(URL url, String method, boolean doOutput, boolean followRedirects,
            Map<String, List<String>> requestProperties, byte[] requestBody,
            SocketFactory socketFactory) throws IOException {
        String host = url.getHost();
        if (host == null || host.length() == 0) {
            throw new IOException("missing host: " + url);
        }
        int port = url.getPort() >= 0 ? url.getPort() : url.getDefaultPort();
        if (port <= 0) {
            port = "https".equalsIgnoreCase(url.getProtocol()) ? 443 : 80;
        }

        String requestMethod = method == null ? "GET" : method;
        byte[] body = requestBody == null ? new byte[0] : requestBody;
        if (doOutput && "GET".equals(requestMethod)) {
            requestMethod = "POST";
        }

        Response bridgeResponse = executeViaBridge(url, requestMethod, followRedirects,
                requestProperties, body);
        if (bridgeResponse != null) {
            return bridgeResponse;
        }

        Socket socket = socketFactory.open(url, port);
        socket.setSoTimeout(READ_TIMEOUT_MS);

        OutputStream out = socket.getOutputStream();
        String path = url.getFile();
        if (path == null || path.length() == 0) {
            path = "/";
        }
        StringBuilder request = new StringBuilder();
        request.append(requestMethod).append(' ').append(path).append(" HTTP/1.1\r\n");
        request.append("Host: ").append(host);
        if (!("http".equalsIgnoreCase(url.getProtocol()) && port == 80)
                && !("https".equalsIgnoreCase(url.getProtocol()) && port == 443)) {
            request.append(':').append(port);
        }
        request.append("\r\n");
        request.append("Connection: close\r\n");
        request.append("User-Agent: Westlake/1.0\r\n");
        request.append("Accept: */*\r\n");
        writeRequestHeaders(request, requestProperties, body.length);
        request.append("\r\n");
        out.write(request.toString().getBytes("ISO-8859-1"));
        if (body.length > 0) {
            out.write(body);
        }
        out.flush();

        BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
        String status = readAsciiLine(in);
        if (status == null || !status.startsWith("HTTP/")) {
            throw new IOException("bad HTTP status from " + url + ": " + status);
        }
        Response response = new Response();
        response.socket = socket;
        parseStatus(status, response);
        response.headers = readHeaders(in);
        response.body = maybeGunzip(in, response.headers);
        if (socket instanceof SSLSocket) {
            response.sslSession = ((SSLSocket) socket).getSession();
        }
        return response;
    }

    static Map<String, List<String>> emptyHeaders() {
        return Collections.emptyMap();
    }

    static String firstHeader(Map<String, List<String>> headers, String key) {
        if (headers == null || key == null) {
            return null;
        }
        List<String> values = headers.get(key.toLowerCase(Locale.US));
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    static String headerFieldAt(Map<String, List<String>> headers, int index,
            int code, String message) {
        if (index < 0) {
            return null;
        }
        if (index == 0) {
            return "HTTP/1.1 " + code + " " + (message == null ? "" : message);
        }
        HeaderSlot slot = headerSlotAt(headers, index);
        return slot == null ? null : slot.value;
    }

    static String headerFieldKeyAt(Map<String, List<String>> headers, int index) {
        if (index <= 0) {
            return null;
        }
        HeaderSlot slot = headerSlotAt(headers, index);
        return slot == null ? null : slot.key;
    }

    static long contentLengthLong(Map<String, List<String>> headers) {
        String value = firstHeader(headers, "content-length");
        if (value == null) {
            return -1L;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return -1L;
        }
    }

    static Map<String, List<String>> immutableHeaderCopy(Map<String, List<String>> src) {
        if (src == null || src.isEmpty()) {
            return emptyHeaders();
        }
        LinkedHashMap<String, List<String>> out = new LinkedHashMap<String, List<String>>();
        for (Map.Entry<String, List<String>> entry : src.entrySet()) {
            out.put(entry.getKey(), Collections.unmodifiableList(new ArrayList<String>(entry.getValue())));
        }
        return Collections.unmodifiableMap(out);
    }

    static byte[] bytes(ByteArrayOutputStream out) {
        return out == null ? new byte[0] : out.toByteArray();
    }

    private static HeaderSlot headerSlotAt(Map<String, List<String>> headers, int index) {
        if (headers == null || headers.isEmpty()) {
            return null;
        }
        int current = 1;
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            List<String> values = entry.getValue();
            if (values == null || values.isEmpty()) {
                if (current == index) {
                    return new HeaderSlot(entry.getKey(), "");
                }
                current++;
                continue;
            }
            for (String value : values) {
                if (current == index) {
                    return new HeaderSlot(entry.getKey(), value);
                }
                current++;
            }
        }
        return null;
    }

    private static final class HeaderSlot {
        final String key;
        final String value;

        HeaderSlot(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    static Socket openPlainSocket(URL url, int port) throws IOException {
        Socket socket = new Socket(url.getHost(), port);
        socket.setSoTimeout(READ_TIMEOUT_MS);
        return socket;
    }

    static Socket openTlsSocket(URL url, int port, SSLSocketFactory factory,
            HostnameVerifier verifier) throws IOException {
        SSLSocketFactory sslFactory = factory != null
                ? factory
                : (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket) sslFactory.createSocket(url.getHost(), port);
        socket.setSoTimeout(READ_TIMEOUT_MS);
        socket.startHandshake();
        SSLSession session = socket.getSession();
        HostnameVerifier hostnameVerifier = verifier != null
                ? verifier
                : HttpsURLConnection.getDefaultHostnameVerifier();
        if (hostnameVerifier != null && !hostnameVerifier.verify(url.getHost(), session)) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
            throw new SSLPeerUnverifiedException("hostname not verified: " + url.getHost());
        }
        return socket;
    }

    static Certificate[] serverCertificates(SSLSession session) throws SSLPeerUnverifiedException {
        return session == null ? null : session.getPeerCertificates();
    }

    static Principal peerPrincipal(SSLSession session) throws SSLPeerUnverifiedException {
        return session == null ? null : session.getPeerPrincipal();
    }

    static Principal localPrincipal(SSLSession session) {
        return session == null ? null : session.getLocalPrincipal();
    }

    private static void writeRequestHeaders(StringBuilder request,
            Map<String, List<String>> requestProperties, int bodyLength) {
        boolean hasContentLength = false;
        boolean hasContentType = false;
        if (requestProperties != null) {
            for (Map.Entry<String, List<String>> entry : requestProperties.entrySet()) {
                String key = entry.getKey();
                if (key == null) {
                    continue;
                }
                if ("host".equalsIgnoreCase(key) || "connection".equalsIgnoreCase(key)) {
                    continue;
                }
                if ("content-length".equalsIgnoreCase(key)) {
                    hasContentLength = true;
                }
                if ("content-type".equalsIgnoreCase(key)) {
                    hasContentType = true;
                }
                List<String> values = entry.getValue();
                if (values == null || values.isEmpty()) {
                    request.append(key).append(":\r\n");
                } else {
                    for (String value : values) {
                        request.append(key).append(": ")
                                .append(value == null ? "" : value)
                                .append("\r\n");
                    }
                }
            }
        }
        if (bodyLength > 0) {
            if (!hasContentType) {
                request.append("Content-Type: application/json\r\n");
            }
            if (!hasContentLength) {
                request.append("Content-Length: ").append(bodyLength).append("\r\n");
            }
        }
    }

    private static void parseStatus(String status, Response response) throws IOException {
        int firstSpace = status.indexOf(' ');
        if (firstSpace < 0 || firstSpace + 4 > status.length()) {
            throw new IOException("bad HTTP status: " + status);
        }
        int secondSpace = status.indexOf(' ', firstSpace + 1);
        String code = secondSpace < 0
                ? status.substring(firstSpace + 1)
                : status.substring(firstSpace + 1, secondSpace);
        try {
            response.code = Integer.parseInt(code);
        } catch (NumberFormatException e) {
            throw new IOException("bad HTTP status code: " + status);
        }
        response.message = secondSpace < 0 ? "" : status.substring(secondSpace + 1);
    }

    private static Map<String, List<String>> readHeaders(InputStream in) throws IOException {
        LinkedHashMap<String, List<String>> headers = new LinkedHashMap<String, List<String>>();
        while (true) {
            String line = readAsciiLine(in);
            if (line == null || line.length() == 0) {
                break;
            }
            int colon = line.indexOf(':');
            if (colon <= 0) {
                continue;
            }
            String key = line.substring(0, colon).trim().toLowerCase(Locale.US);
            String value = line.substring(colon + 1).trim();
            List<String> values = headers.get(key);
            if (values == null) {
                values = new ArrayList<String>();
                headers.put(key, values);
            }
            values.add(value);
        }
        return immutableHeaderCopy(headers);
    }

    private static String readAsciiLine(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(80);
        while (true) {
            int b = in.read();
            if (b == -1) {
                return out.size() == 0 ? null : out.toString("ISO-8859-1");
            }
            if (b == '\n') {
                break;
            }
            if (b != '\r') {
                out.write(b);
            }
        }
        return out.toString("ISO-8859-1");
    }

    private static InputStream maybeGunzip(InputStream in, Map<String, List<String>> headers)
            throws IOException {
        String encoding = firstHeader(headers, "content-encoding");
        return encoding != null && "gzip".equalsIgnoreCase(encoding) ? new GZIPInputStream(in) : in;
    }

    private static Response executeViaBridge(URL url, String method, boolean followRedirects,
            Map<String, List<String>> requestProperties, byte[] requestBody) throws IOException {
        WestlakeLauncher.BridgeHttpResponse bridge = WestlakeLauncher.bridgeHttpRequest(
                url.toString(),
                method,
                headersToJson(requestProperties),
                requestBody,
                4 * 1024 * 1024,
                READ_TIMEOUT_MS,
                followRedirects);
        if (bridge == null) {
            return null;
        }
        if (bridge.status == 0 && "missing_bridge_dir".equals(bridge.error)) {
            return null;
        }
        if (bridge.status <= 0) {
            throw new IOException("HTTP bridge " + bridge.status + " " + bridge.error);
        }
        Response response = new Response();
        response.code = bridge.status;
        response.message = bridge.error == null || bridge.error.length() == 0
                ? "HTTP " + bridge.status
                : bridge.error;
        response.headers = parseHeadersJson(bridge.headersJson);
        response.body = new ByteArrayInputStream(bridge.body == null ? new byte[0] : bridge.body);
        return response;
    }

    private static String headersToJson(Map<String, List<String>> headers) {
        if (headers == null || headers.isEmpty()) {
            return "{}";
        }
        StringBuilder out = new StringBuilder();
        out.append('{');
        boolean firstHeader = true;
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String key = entry.getKey();
            if (key == null || key.length() == 0) {
                continue;
            }
            if (!firstHeader) {
                out.append(',');
            }
            firstHeader = false;
            appendJsonString(out, key);
            out.append(':').append('[');
            List<String> values = entry.getValue();
            if (values != null) {
                for (int i = 0; i < values.size(); i++) {
                    if (i > 0) {
                        out.append(',');
                    }
                    appendJsonString(out, values.get(i));
                }
            }
            out.append(']');
        }
        out.append('}');
        return out.toString();
    }

    private static Map<String, List<String>> parseHeadersJson(String json) {
        if (json == null || json.length() == 0) {
            return emptyHeaders();
        }
        JsonCursor cursor = new JsonCursor(json);
        LinkedHashMap<String, List<String>> headers = new LinkedHashMap<String, List<String>>();
        cursor.skipWhitespace();
        if (!cursor.consume('{')) {
            return emptyHeaders();
        }
        while (true) {
            cursor.skipWhitespace();
            if (cursor.consume('}')) {
                break;
            }
            String key = cursor.readString();
            if (key == null) {
                return emptyHeaders();
            }
            cursor.skipWhitespace();
            if (!cursor.consume(':')) {
                return emptyHeaders();
            }
            cursor.skipWhitespace();
            ArrayList<String> values = new ArrayList<String>();
            if (cursor.consume('[')) {
                while (true) {
                    cursor.skipWhitespace();
                    if (cursor.consume(']')) {
                        break;
                    }
                    String value = cursor.readString();
                    if (value == null) {
                        cursor.skipValue();
                    } else {
                        values.add(value);
                    }
                    cursor.skipWhitespace();
                    if (cursor.consume(',')) {
                        continue;
                    }
                    if (cursor.consume(']')) {
                        break;
                    }
                    return emptyHeaders();
                }
            } else {
                String value = cursor.readString();
                if (value != null) {
                    values.add(value);
                } else {
                    cursor.skipValue();
                }
            }
            headers.put(key.toLowerCase(Locale.US), values);
            cursor.skipWhitespace();
            if (cursor.consume(',')) {
                continue;
            }
            if (cursor.consume('}')) {
                break;
            }
            return emptyHeaders();
        }
        return immutableHeaderCopy(headers);
    }

    private static void appendJsonString(StringBuilder out, String value) {
        out.append('"');
        if (value != null) {
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (c == '"' || c == '\\') {
                    out.append('\\').append(c);
                } else if (c == '\n') {
                    out.append("\\n");
                } else if (c == '\r') {
                    out.append("\\r");
                } else if (c == '\t') {
                    out.append("\\t");
                } else if (c < 0x20) {
                    out.append("\\u");
                    String hex = Integer.toHexString(c);
                    for (int j = hex.length(); j < 4; j++) {
                        out.append('0');
                    }
                    out.append(hex);
                } else {
                    out.append(c);
                }
            }
        }
        out.append('"');
    }

    private static final class JsonCursor {
        private final String value;
        private int pos;

        JsonCursor(String value) {
            this.value = value;
        }

        void skipWhitespace() {
            while (pos < value.length()) {
                char c = value.charAt(pos);
                if (c != ' ' && c != '\n' && c != '\r' && c != '\t') {
                    break;
                }
                pos++;
            }
        }

        boolean consume(char expected) {
            if (pos < value.length() && value.charAt(pos) == expected) {
                pos++;
                return true;
            }
            return false;
        }

        String readString() {
            skipWhitespace();
            if (!consume('"')) {
                return null;
            }
            StringBuilder out = new StringBuilder();
            while (pos < value.length()) {
                char c = value.charAt(pos++);
                if (c == '"') {
                    return out.toString();
                }
                if (c != '\\') {
                    out.append(c);
                    continue;
                }
                if (pos >= value.length()) {
                    return null;
                }
                char escaped = value.charAt(pos++);
                if (escaped == '"' || escaped == '\\' || escaped == '/') {
                    out.append(escaped);
                } else if (escaped == 'b') {
                    out.append('\b');
                } else if (escaped == 'f') {
                    out.append('\f');
                } else if (escaped == 'n') {
                    out.append('\n');
                } else if (escaped == 'r') {
                    out.append('\r');
                } else if (escaped == 't') {
                    out.append('\t');
                } else if (escaped == 'u' && pos + 4 <= value.length()) {
                    try {
                        out.append((char) Integer.parseInt(value.substring(pos, pos + 4), 16));
                    } catch (NumberFormatException e) {
                        return null;
                    }
                    pos += 4;
                } else {
                    return null;
                }
            }
            return null;
        }

        void skipValue() {
            skipWhitespace();
            if (pos >= value.length()) {
                return;
            }
            char c = value.charAt(pos);
            if (c == '"') {
                readString();
                return;
            }
            if (c == '[' || c == '{') {
                char open = c;
                char close = c == '[' ? ']' : '}';
                pos++;
                int depth = 1;
                while (pos < value.length() && depth > 0) {
                    c = value.charAt(pos);
                    if (c == '"') {
                        readString();
                    } else {
                        pos++;
                        if (c == open) {
                            depth++;
                        } else if (c == close) {
                            depth--;
                        }
                    }
                }
                return;
            }
            while (pos < value.length()) {
                c = value.charAt(pos);
                if (c == ',' || c == ']' || c == '}') {
                    break;
                }
                pos++;
            }
        }
    }
}
