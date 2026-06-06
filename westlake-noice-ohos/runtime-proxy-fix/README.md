# Universal Java dynamic-proxy fix (libart) — makes Retrofit work for ALL apps

## Bug
The custom `FIX-IFTABLE-A` iftable-repair pass in `libart.so` ran on dynamic
proxy classes ($ProxyN from java.lang.reflect.Proxy / Retrofit / OkHttp / Mockito).
A proxy method's GetName()/GetParameterTypeList() resolve the *interface* method
index against the *$Proxy class's* dex cache (= wrong dex) -> garbage proto ->
"impl==iface but NO candidate found" for every (Kotlin-suspend, Continuation-param)
interface method -> proxy iftable left broken -> AbstractMethodError on dispatch
-> every Retrofit API call throws before networking -> app mislabels "network unreachable".

## Fix (universal, one line)
Gate the repair pass with `&& !klass->IsProxyClass()` (class_linker.cc ~9450).
ART already builds proxy iftables correctly via SetupInterfaceLookupTable; the
pass is purely additive damage for proxies. Fixes ALL dynamic-proxy users at once.

## Build/deploy (libart IS rebuildable; the un-rebuildable .so is liboh_android_runtime.so)
`bash $HOME/libart-pathA-work/build_libart_pathA.sh` -> out/libart.so
-> push to /system/android/lib/libart.so (PLAIN PUSH, no boot regen) -> reboot.
md5 7b856a2d450184f500d2b4044335afec (was d9e59a33).

## Verified on device
- "$ProxyN ... NO candidate found" lines: 8+ -> 0.
- noice's subscription Retrofit proxy now DISPATCHES and reaches the network
  (tls.log: ctor host=api.trynoice.com + SSL_write/SSL_read; before: empty).
- noice still boots + library renders (no regression).
- Credit: root-caused by codex agent analysis.
