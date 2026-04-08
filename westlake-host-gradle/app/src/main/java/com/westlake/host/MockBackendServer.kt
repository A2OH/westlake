package com.westlake.host

import android.util.Log
import org.json.JSONObject
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.security.KeyStore
import javax.net.ssl.*

/**
 * MITM HTTP proxy that serves cached McDonald's API responses.
 * Handles both HTTP and HTTPS (via CONNECT tunneling with TLS termination).
 *
 * Usage: MockBackendServer.start(dataDir) → returns port
 * Then set -Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=PORT for dalvikvm
 */
object MockBackendServer {
    private const val TAG = "MockBackend"
    private var serverSocket: ServerSocket? = null
    private var sslContext: SSLContext? = null
    var port: Int = 0
        private set

    // URL pattern → CachedResponse
    private val cache = mutableMapOf<String, CachedResponse>()

    data class CachedResponse(
        val file: String,
        val status: Int,
        val contentType: String,
        val method: String,
        val size: Int,
        var body: ByteArray? = null
    )

    fun start(dataDir: String): Int {
        if (serverSocket != null) {
            Log.i(TAG, "Already running on port $port")
            return port
        }

        // Load URL map
        loadUrlMap(dataDir)

        // Load TLS keystore
        initTls(dataDir)

        // Start server
        serverSocket = ServerSocket(0) // random port
        port = serverSocket!!.localPort
        Log.i(TAG, "Mock backend started on port $port with ${cache.size} cached URLs")

        Thread({
            while (true) {
                try {
                    val client = serverSocket?.accept() ?: break
                    Thread { handleClient(client, dataDir) }.start()
                } catch (e: Exception) {
                    if (serverSocket?.isClosed != true) {
                        Log.e(TAG, "Accept error: $e")
                    }
                    break
                }
            }
        }, "MockBackend-Accept").apply { isDaemon = true; start() }

        return port
    }

    private fun loadUrlMap(dataDir: String) {
        try {
            val mapFile = File(dataDir, "url-map.json")
            if (!mapFile.exists()) {
                Log.e(TAG, "url-map.json not found at $dataDir")
                return
            }
            val json = JSONObject(mapFile.readText())
            for (key in json.keys()) {
                val obj = json.getJSONObject(key)
                cache[key] = CachedResponse(
                    file = obj.getString("file"),
                    status = obj.getInt("status"),
                    contentType = obj.optString("content_type", "application/octet-stream"),
                    method = obj.optString("method", "GET"),
                    size = obj.optInt("size", 0)
                )
            }
            Log.i(TAG, "Loaded ${cache.size} URL patterns from url-map.json")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load URL map: $e")
        }
    }

    private fun initTls(dataDir: String) {
        try {
            val ksFile = File(dataDir, "mock-keystore.p12")
            if (!ksFile.exists()) {
                Log.e(TAG, "mock-keystore.p12 not found")
                return
            }
            val ks = KeyStore.getInstance("PKCS12")
            FileInputStream(ksFile).use { ks.load(it, "westlake".toCharArray()) }

            val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            kmf.init(ks, "westlake".toCharArray())

            sslContext = SSLContext.getInstance("TLS")
            sslContext!!.init(kmf.keyManagers, null, null)
            Log.i(TAG, "TLS initialized with mock-keystore.p12")
        } catch (e: Exception) {
            Log.e(TAG, "TLS init failed: $e")
        }
    }

    private fun handleClient(client: Socket, dataDir: String) {
        try {
            client.soTimeout = 10000
            val input = BufferedInputStream(client.getInputStream())
            val output = client.getOutputStream()

            // Read the HTTP request line
            val requestLine = readLine(input) ?: return
            val parts = requestLine.split(" ")
            if (parts.size < 3) return

            val method = parts[0]
            val target = parts[1]

            // Read headers
            val headers = mutableMapOf<String, String>()
            while (true) {
                val line = readLine(input) ?: break
                if (line.isEmpty()) break
                val colonIdx = line.indexOf(':')
                if (colonIdx > 0) {
                    headers[line.substring(0, colonIdx).trim().lowercase()] = line.substring(colonIdx + 1).trim()
                }
            }

            if (method == "CONNECT") {
                // HTTPS tunnel
                handleConnect(target, input, output, client, dataDir)
            } else {
                // Direct HTTP proxy request
                val cached = findCached(target, method)
                if (cached != null) {
                    val body = loadBody(cached, dataDir)
                    sendResponse(output, cached.status, cached.contentType, body)
                    Log.d(TAG, "CACHE HIT: $method $target (${body.size} bytes)")
                } else {
                    // Not cached — return 502
                    sendResponse(output, 502, "text/plain", "Not cached: $target".toByteArray())
                    Log.d(TAG, "CACHE MISS: $method $target")
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Client handler error: ${e.message}")
        } finally {
            try { client.close() } catch (_: Exception) {}
        }
    }

    private fun handleConnect(target: String, input: InputStream, output: OutputStream, client: Socket, dataDir: String) {
        // Send 200 to establish tunnel
        output.write("HTTP/1.1 200 Connection Established\r\n\r\n".toByteArray())
        output.flush()

        val ctx = sslContext ?: run {
            Log.e(TAG, "No SSL context for CONNECT $target")
            return
        }

        // Wrap the socket in SSL (server-side)
        val sslSocketFactory = ctx.serverSocketFactory as? SSLServerSocketFactory
        // Use SSLEngine for wrapping existing socket
        val host = target.split(":")[0]
        val sslEngine = ctx.createSSLEngine(host, 443)
        // Actually, simpler: use SSLSocketFactory to wrap
        val sf = ctx.socketFactory as SSLSocketFactory
        // Create SSL socket wrapping the existing connection
        val sslSocket = sf.createSocket(client, host, 443, true) as SSLSocket
        sslSocket.useClientMode = false
        sslSocket.startHandshake()

        val sslInput = BufferedInputStream(sslSocket.getInputStream())
        val sslOutput = sslSocket.getOutputStream()

        // Now read the actual HTTP request inside the tunnel
        try {
            while (!sslSocket.isClosed) {
                val innerRequest = readLine(sslInput) ?: break
                val innerParts = innerRequest.split(" ")
                if (innerParts.size < 3) break

                val innerMethod = innerParts[0]
                val innerPath = innerParts[1]

                // Read inner headers
                var contentLength = 0
                while (true) {
                    val line = readLine(sslInput) ?: break
                    if (line.isEmpty()) break
                    if (line.lowercase().startsWith("content-length:")) {
                        contentLength = line.substringAfter(":").trim().toIntOrNull() ?: 0
                    }
                }

                // Read body if POST
                if (contentLength > 0) {
                    val bodyBuf = ByteArray(contentLength)
                    var read = 0
                    while (read < contentLength) {
                        val n = sslInput.read(bodyBuf, read, contentLength - read)
                        if (n < 0) break
                        read += n
                    }
                }

                // Reconstruct full URL
                val fullUrl = "https://$host$innerPath"
                val cached = findCached(fullUrl, innerMethod)
                if (cached != null) {
                    val body = loadBody(cached, dataDir)
                    sendResponse(sslOutput, cached.status, cached.contentType, body)
                    Log.d(TAG, "CACHE HIT (TLS): $innerMethod $host$innerPath (${body.size} bytes)")
                } else {
                    sendResponse(sslOutput, 502, "text/plain", "Not cached: $fullUrl".toByteArray())
                    Log.d(TAG, "CACHE MISS (TLS): $innerMethod $host$innerPath")
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "TLS tunnel error for $target: ${e.message}")
        }
    }

    private fun findCached(url: String, method: String): CachedResponse? {
        // Try exact URL (without query params)
        val baseUrl = url.split("?")[0]
        cache[baseUrl]?.let { if (it.method == method || method == "HEAD") return it }

        // Try matching by path pattern
        for ((pattern, resp) in cache) {
            if (resp.method != method && method != "HEAD") continue
            // Check if the URL path matches (ignoring query params)
            val patternBase = pattern.split("?")[0]
            if (baseUrl == patternBase) return resp
            // Partial match: same host + path prefix
            if (baseUrl.startsWith(patternBase) || patternBase.startsWith(baseUrl)) return resp
        }
        return null
    }

    private fun loadBody(cached: CachedResponse, dataDir: String): ByteArray {
        cached.body?.let { return it }
        return try {
            val body = File(dataDir, cached.file).readBytes()
            if (body.size < 1_000_000) cached.body = body // cache small responses in memory
            body
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load ${cached.file}: $e")
            "{}".toByteArray()
        }
    }

    private fun sendResponse(output: OutputStream, status: Int, contentType: String, body: ByteArray) {
        val statusText = when (status) {
            200 -> "OK"; 201 -> "Created"; 304 -> "Not Modified"; 404 -> "Not Found"; else -> "OK"
        }
        val header = "HTTP/1.1 $status $statusText\r\n" +
                "Content-Type: $contentType\r\n" +
                "Content-Length: ${body.size}\r\n" +
                "Connection: close\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "\r\n"
        output.write(header.toByteArray())
        output.write(body)
        output.flush()
    }

    private fun readLine(input: InputStream): String? {
        val sb = StringBuilder()
        while (true) {
            val b = input.read()
            if (b < 0) return if (sb.isEmpty()) null else sb.toString()
            if (b == '\n'.code) return sb.toString().trimEnd('\r')
            sb.append(b.toChar())
        }
    }

    fun stop() {
        try { serverSocket?.close() } catch (_: Exception) {}
        serverSocket = null
        port = 0
        cache.clear()
    }
}
