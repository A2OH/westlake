package com.android.okhttp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

final class WestlakeHttpsURLConnection extends HttpsURLConnection {
    private final Map<String, List<String>> requestHeaders = new LinkedHashMap<String, List<String>>();
    private ByteArrayOutputStream requestBody;
    private WestlakeHttpTransport.Response response;

    WestlakeHttpsURLConnection(URL url) {
        super(url);
    }

    @Override
    public void connect() throws IOException {
        if (connected) {
            return;
        }
        response = WestlakeHttpTransport.execute(url, method, doOutput,
                getInstanceFollowRedirects(), requestHeaders,
                WestlakeHttpTransport.bytes(requestBody),
                new WestlakeHttpTransport.SocketFactory() {
                    @Override
                    public Socket open(URL openUrl, int port) throws IOException {
                        return WestlakeHttpTransport.openTlsSocket(openUrl, port,
                                getSSLSocketFactory(), getHostnameVerifier());
                    }
                });
        responseCode = response.code;
        responseMessage = response.message;
        connected = true;
    }

    @Override
    public void disconnect() {
        if (response != null && response.socket != null) {
            try {
                response.socket.close();
            } catch (IOException ignored) {
            }
        }
        connected = false;
    }

    @Override
    public boolean usingProxy() {
        return false;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (connected) {
            throw new ProtocolException("already connected");
        }
        doOutput = true;
        if (requestBody == null) {
            requestBody = new ByteArrayOutputStream();
        }
        return requestBody;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        connect();
        if (responseCode >= HTTP_BAD_REQUEST) {
            throw new IOException("HTTP " + responseCode + " " + responseMessage);
        }
        return response.body;
    }

    @Override
    public InputStream getErrorStream() {
        try {
            connect();
            return responseCode >= HTTP_BAD_REQUEST ? response.body : null;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public int getResponseCode() throws IOException {
        connect();
        return responseCode;
    }

    @Override
    public String getResponseMessage() throws IOException {
        connect();
        return responseMessage;
    }

    @Override
    public String getHeaderField(String name) {
        try {
            connect();
        } catch (IOException e) {
            return null;
        }
        return WestlakeHttpTransport.firstHeader(response.headers, name);
    }

    @Override
    public String getHeaderField(int n) {
        try {
            connect();
        } catch (IOException e) {
            return null;
        }
        return WestlakeHttpTransport.headerFieldAt(response.headers, n, responseCode, responseMessage);
    }

    @Override
    public String getHeaderFieldKey(int n) {
        try {
            connect();
        } catch (IOException e) {
            return null;
        }
        return WestlakeHttpTransport.headerFieldKeyAt(response.headers, n);
    }

    @Override
    public Map<String, List<String>> getHeaderFields() {
        try {
            connect();
        } catch (IOException e) {
            return WestlakeHttpTransport.emptyHeaders();
        }
        return response.headers;
    }

    @Override
    public void setRequestProperty(String key, String value) {
        checkHeaderKey(key);
        ArrayList<String> values = new ArrayList<String>();
        values.add(value);
        requestHeaders.put(key.toLowerCase(Locale.US), values);
    }

    @Override
    public void addRequestProperty(String key, String value) {
        checkHeaderKey(key);
        String normalized = key.toLowerCase(Locale.US);
        List<String> values = requestHeaders.get(normalized);
        if (values == null) {
            values = new ArrayList<String>();
            requestHeaders.put(normalized, values);
        }
        values.add(value);
    }

    @Override
    public String getRequestProperty(String key) {
        if (key == null) {
            return null;
        }
        List<String> values = requestHeaders.get(key.toLowerCase(Locale.US));
        return values == null || values.isEmpty() ? null : values.get(values.size() - 1);
    }

    @Override
    public Map<String, List<String>> getRequestProperties() {
        return WestlakeHttpTransport.immutableHeaderCopy(requestHeaders);
    }

    @Override
    public String getContentType() {
        return getHeaderField("content-type");
    }

    @Override
    public int getContentLength() {
        String value = getHeaderField("content-length");
        if (value == null) {
            return -1;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public long getContentLengthLong() {
        try {
            connect();
        } catch (IOException e) {
            return -1L;
        }
        return WestlakeHttpTransport.contentLengthLong(response.headers);
    }

    @Override
    public String getContentEncoding() {
        return getHeaderField("content-encoding");
    }

    @Override
    public String getCipherSuite() {
        SSLSession session = response == null ? null : response.sslSession;
        return session == null ? null : session.getCipherSuite();
    }

    @Override
    public Certificate[] getLocalCertificates() {
        SSLSession session = response == null ? null : response.sslSession;
        return session == null ? null : session.getLocalCertificates();
    }

    @Override
    public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
        SSLSession session = response == null ? null : response.sslSession;
        return WestlakeHttpTransport.serverCertificates(session);
    }

    @Override
    public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
        SSLSession session = response == null ? null : response.sslSession;
        return WestlakeHttpTransport.peerPrincipal(session);
    }

    @Override
    public Principal getLocalPrincipal() {
        SSLSession session = response == null ? null : response.sslSession;
        return WestlakeHttpTransport.localPrincipal(session);
    }

    private static void checkHeaderKey(String key) {
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException("header key is empty");
        }
    }
}
