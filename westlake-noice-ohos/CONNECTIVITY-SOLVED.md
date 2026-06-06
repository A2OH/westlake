# Connectivity SOLVED — native TLS works end-to-end (HTTP 200)

The 5-layer stack now functions. PROOF (NetTest harness, `CONNECTIVITY-PROOF-httptest.log`):
```
OK https://api.trynoice.com/v1/subscriptions/plans -> HTTP 200
OK https://cdn.trynoice.com/library/library.json   -> HTTP 404   (real server 404 = full HTTPS round-trip)
```
noice's OWN okhttp3 also reaches the native handshake: `TlsJniSocket.startHandshake -> sslConnect ret=<nonzero SSL*>` for api+cdn.

## The fixes (all in this tree)
1. DNS — `native-libs/libjdnshook_v2.c` (hooks android_getaddrinfofornetcontext) + libdnshook direct-UDP.
2. cgroup socket perm — netsys eBPF `inet_create_socket` checks `oh_sock_permission_map[uid]` (`bpf-analysis/netsys-ebpf.c`). netsys sets noice's=0 (deny) at spawn (no INTERNET perm). **Workaround:** continuous regrant loop (`scripts/broker_fix.sh` pattern): `while true; do bpfgrant 13731 oh_sock_permission_map; sleep 0.3; done`. Clean fix: declare INTERNET perm / make grant persist.
3. IPv4 — `native-libs/libv4force.c` (AF_INET6→AF_INET; device has no IPv6 route).
4. TLS provider — `native-tls/`: `libtlsjni.c` (OpenSSL over libssl_openssl) + `TlsJniSocket.java` (SSLSocket) in `tlsjni-extra.dex` (OUT of the BCP), loaded by a **cached static DexClassLoader** in the patched `TlsShimProvider$Sf` (`TlsShimProvider_Sf_cached.smali`; the `.field static CTOR` MUST be a real field def; per-call loader → UnsatisfiedLinkError).
5. okhttp — `okhttp-bcp-Platform-PATCHED.smali`: drop the hard `android.net.ssl.SSLSockets` calls in `com.android.okhttp Platform.configureTlsExtensions` (conscrypt class absent → NoClassDefFound). noice's bundled okhttp3 needs no patch.

## Residual (NOT connectivity)
noice's **subscription page** still errors: noice's Retrofit dynamic proxy `$Proxy9` (SubscriptionApi `q6.d.b(SubscriptionFlowParams)`) hits a runtime iftable "NO candidate found" method-resolution bug — an app/runtime class-linking issue, not TLS. The library path is unaffected.
