#!/usr/bin/env python3
"""Small development HTTP proxy for Westlake phone proofs.

The Android host bridge can call this through `adb reverse` when the test phone
has no active default network. This is a development adapter only: OHOS should
provide the same southbound contract with its native networking stack.
"""

from __future__ import annotations

import argparse
import http.server
import io
import socketserver
import sys
import urllib.error
import urllib.parse
import urllib.request


HOP_BY_HOP_HEADERS = {
    "connection",
    "keep-alive",
    "proxy-authenticate",
    "proxy-authorization",
    "te",
    "trailer",
    "transfer-encoding",
    "upgrade",
    "host",
    "content-length",
}


class ThreadingHTTPServer(socketserver.ThreadingMixIn, http.server.HTTPServer):
    daemon_threads = True


class ProxyHandler(http.server.BaseHTTPRequestHandler):
    protocol_version = "HTTP/1.1"

    def do_GET(self) -> None:
        self._proxy()

    def do_POST(self) -> None:
        self._proxy()

    def do_PUT(self) -> None:
        self._proxy()

    def do_PATCH(self) -> None:
        self._proxy()

    def do_DELETE(self) -> None:
        self._proxy()

    def do_HEAD(self) -> None:
        self._proxy()

    def do_OPTIONS(self) -> None:
        self._proxy()

    def log_message(self, fmt: str, *args: object) -> None:
        sys.stderr.write("%s - - [%s] %s\n" % (
            self.address_string(),
            self.log_date_time_string(),
            fmt % args,
        ))

    def _proxy(self) -> None:
        parsed = urllib.parse.urlparse(self.path)
        if parsed.path != "/proxy":
            self._send_error(404, b"westlake proxy path not found")
            return

        params = urllib.parse.parse_qs(parsed.query, keep_blank_values=False)
        target = (params.get("url") or [""])[0]
        if not (target.startswith("http://") or target.startswith("https://")):
            self._send_error(400, b"missing or unsupported target url")
            return

        length = int(self.headers.get("Content-Length") or "0")
        body = self.rfile.read(length) if length > 0 else None
        headers = {
            key: value
            for key, value in self.headers.items()
            if key.lower() not in HOP_BY_HOP_HEADERS
        }
        headers.setdefault("User-Agent", "Westlake-Dev-HTTP-Proxy/1.0")

        request = urllib.request.Request(
            target,
            data=body,
            headers=headers,
            method=self.command,
        )
        try:
            with urllib.request.urlopen(request, timeout=30) as response:
                response_body = b"" if self.command == "HEAD" else response.read()
                self._send_response(response.status, response.headers, response_body)
        except urllib.error.HTTPError as exc:
            response_body = b"" if self.command == "HEAD" else exc.read()
            self._send_response(exc.code, exc.headers, response_body)
        except Exception as exc:
            message = ("westlake proxy error: %s: %s" % (
                exc.__class__.__name__,
                exc,
            )).encode("utf-8", "replace")
            self._send_error(599, message)

    def _send_response(self, status: int, headers: object, body: bytes) -> None:
        self.send_response(status)
        for key, value in headers.items():
            if key.lower() in HOP_BY_HOP_HEADERS:
                continue
            self.send_header(key, value)
        self.send_header("Content-Length", str(len(body)))
        self.send_header("Connection", "close")
        self.end_headers()
        if self.command != "HEAD":
            self.wfile.write(body)

    def _send_error(self, status: int, body: bytes) -> None:
        self.send_response(status)
        self.send_header("Content-Type", "text/plain; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.send_header("Connection", "close")
        self.end_headers()
        if self.command != "HEAD":
            self.wfile.write(body)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--host", default="127.0.0.1")
    parser.add_argument("--port", type=int, default=18080)
    args = parser.parse_args()

    server = ThreadingHTTPServer((args.host, args.port), ProxyHandler)
    print("westlake_dev_http_proxy=%s:%d" % (args.host, args.port), flush=True)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        return 130
    finally:
        server.server_close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
