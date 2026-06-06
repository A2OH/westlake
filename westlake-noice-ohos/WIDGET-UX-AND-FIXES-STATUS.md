# noice on OHOS — UX + fixes status (2026-06-06)

## Working (screen / device validated)
- **Subscription network**: FIXED — plans + prices render (订阅计划: 按年/每两年/按季/按月, CNY/INR toggle). screenshots/sc_3_sub_scroll2.jpeg + sslfix-FINAL-subscription.jpeg. Fix stack:
  - `android.net.ssl.SSLSockets` BCP stub in adapter-mainline-stubs.jar (sslsockets-fix/) — okhttp Android10SocketAdapter NoClassDefFound → fixed.
  - libart `FIX-IFTABLE-PROXY` guard (runtime-proxy-fix/) — dynamic-proxy iftable → Retrofit works.
  - Native TLS (native-tls/) + DNS/bpf (CONNECTIVITY-*).
- **Scroll**: works via the new bridge drag (revealed the price cards).
- **Dial** (alarm time picker): renders + works on real touch (screenshots/e_dial_open.jpeg). Slow = rendering perf.
- **Library / tabs / buttons / info / volume sheet**: render + tap.

## Bridge drag support (bridge-drag/)
Added `dispatchDragViaViewRoot(x1,y1,x2,y2)` = DOWN+14*MOVE+UP to liboh_adapter_bridge.so (md5 82b0d82a). Control channel `/data/local/tmp/noice_tap`: `"x1 y1 x2 y2"`=drag, `"x y"`=tap, `"N"`=nav tab. **Deploy path: /system/lib/liboh_adapter_bridge.so** (NOT /system/android/lib/ — noice loads /system/lib/). Prior bridge was tap-only (sscanf %d %d) → why sliders/scroll/dial looked broken in automated tests (fixture limitation, not noice bug).

## Share-with-friends crash — fix (share-fix/)
createChooser → ACTION_CHOOSER intent → adapter IntentWantConverter.mapAction had no ACTION_CHOOSER case → bogus Want → StartAbility crash. FIX: unwrap ACTION_CHOOSER's EXTRA_INTENT (real ACTION_SEND) in intentToWant before converting → routes to ohos.want.action.sendData. See IntentWantConverter.java. (Deployed as an in-place smali patch of adapter-runtime-bcp.jar to preserve the $Sf TLS patch; boot regen required.)

## Known limitations (not noice bugs)
- Automated control-channel taps/drags reach the MAIN window, not dialog/sheet windows → slider/dial drag not automatable (real touch works). Bridge refinement: target the focused dialog window.
- Dial rendering is slow (hwui/render-thread perf).
