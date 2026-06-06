# SCREEN-VALIDATED: noice network works + library renders (clean device)

**Validated by reading the actual device screen** (`screenshots/SCREEN-VALIDATED-library-network-works.jpeg`): noice renders the populated 声音库 (Birds/Crickets/Heartbeat/Purring Cat with tags, stars, control rows, icons, FAB, bottom nav).

**Proof the network actually exchanges data** (instrumented libtlsjni SSL read/write → `CONNECTIVITY-PROOF-tls-ssl-io.log`):
```
api.trynoice.com: handshake OK -> SSL_write 130B (request) -> SSL_read 433B (response received)
cdn.trynoice.com: SSL_write 128B -> SSL_read 294B (response received)
```
So noice's bundled okhttp3 sends requests AND receives responses over the native OpenSSL TLS (libtlsjni).

## Requirements / procedure (ALWAYS end by reading the screen)
1. CLEAN reboot (NOT a churn-degraded device — repeated launches break spawn/compositing → noice backgrounds, nav fails).
2. Bringup (start_asx, setenforce 0, AppSpawnX socket chcon, power-shell wakeup).
3. Tight bpf regrant: `nohup sh -c 'while true; do /data/local/tmp/bpfgrant 13731 oh_sock_permission_map >/dev/null 2>&1; done' &` (netsys sets noice's perm=0 at spawn; the grant value STICKS once set, so the clean fix is declaring noice's INTERNET permission).
4. Restore noice-room.db.bak + cdn-cache.bak; cold-launch MainActivity.
5. `snapshot_display -f x.jpeg` -> `hdc file recv` -> VIEW the image. The screen is ground truth.

## Residual (NOT network)
- Subscription page errors on a separate app/runtime bug: noice's Retrofit dynamic proxy `$Proxy9` (`SubscriptionApi.q6.d.b`) hits a runtime iftable "no candidate" method-resolution failure. The library proves the network works; the subscription needs the Retrofit-proxy/runtime fix.
- Lesson: a synthetic NetTest HTTP 200 was NOT sufficient proof — only reading the screen (after a clean reboot) revealed the true working state.
