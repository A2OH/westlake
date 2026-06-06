# SSLSockets BCP stub — okhttp Android10 TLS path fix

## Bug
Bundled okhttp picks `Android10Platform` (SDK_INT≥29). Its
`Android10SocketAdapter.matchesSocket` calls
`android.net.ssl.SSLSockets.isSupportedSocket(SSLSocket)` — that class is **absent
from the device boot class path** → `NoClassDefFoundError: Failed resolution of:
Landroid/net/ssl/SSLSockets` → okhttp **cancels** the call → "网络无法访问".

## Fix
Add a functional `android.net.ssl.SSLSockets` stub
(`isSupportedSocket` → false, `setUseSessionTickets` → no-op) to
**`adapter-mainline-stubs.jar`** (which IS in the BCP).

- `SSLSockets.java` — the stub source.
- `SSLSockets.smali` — its compiled smali (for reference / smali-merge paths).

## Build / deploy
See `../REPRODUCE-CLEAN-WSL.md` §2.2: `javac` → `d8` → **merge the stub dex INTO
the jar's existing `classes.dex` with `d8 --min-api 28`** (the existing dex is v39
and there is no smali assembler for it — use d8 merge, not baksmali) → rezip into
the jar keeping `META-INF/MANIFEST.MF`. Result md5 `41834c1f`. The jar is in the
BCP, so a **boot-image regen** is required (§2.11).

## Verified
Per-child stderr `Failed resolution of: Landroid/net/ssl/SSLSockets` = 0,
`canceled due to` = 0; subscription page loads plans + prices
(`screenshots/sslfix-FINAL-subscription.jpeg`).
