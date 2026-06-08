# Share fix — build notes (chooser-unwrap + negative clamp + pasteboard)

noice's "与朋友分享 / Share with friends" (支持开发 / Support-Development page) is a
standard `Intent.createChooser(ACTION_SEND text/plain)` (see decompiled
`x2/a.smali` pswitch_0: builds ACTION_SEND with EXTRA_TEXT = app description +
Play-Store URL + F-Droid URL, then `createChooser` → `startActivity`).

On the OHOS adapter this failed in three stages; the fix is three layers:

### 1. Chooser-unwrap (`bridge-src/activity-java/IntentWantConverter.java`)
`createChooser` makes an `ACTION_CHOOSER` intent with the real `ACTION_SEND`
nested as `EXTRA_INTENT`. `mapAction()` had no CHOOSER case → bogus Want
`ohos.want.action.android.intent.action.CHOOSER` → StartAbility fails → **crash**.
Fix: at the top of `intentToWant`, if action == `ACTION_CHOOSER`, replace the
intent with its `EXTRA_INTENT` parcelable so the nested `ACTION_SEND` maps to
`ohos.want.action.sendData`.
**Lives in `oh-adapter-framework.jar` (BCP) → needs boot regen.** Deployed in
ohaf `efd3f740`.

### 2. Negative-StartAbility clamp (`ActivityManagerAdapter.bridgeStartAbility`, smali)
After unwrap, `sendData` has no OHOS target on this board → `nativeStartAbility`
returns NEGATIVE → AOSP `Instrumentation.checkStartActivityResult` throws
`ActivityNotFoundException` → **crash**. Fix: clamp negative result → 0
(START_SUCCESS). Also in ohaf `efd3f740` (boot vdex shows the
`clamping to START_SUCCESS` string). Validated: 5/5 share taps, noice survives.

### 3. Pasteboard copy (`bridge-src/activity-jni/oh_ability_manager_client.cpp`)
With 1+2 the app no longer crashes but OHOS shows `com.ohos.amsdialog`
("无法打开此文件") because no app receives a text share. To make the share *useful*,
`startAbilityWithCaller` intercepts implicit `sendData` (empty bundleName),
extracts `android.intent.extra.TEXT` from the extras JSON, and copies it to the
OHOS system clipboard via the NDK pasteboard/UDMF C API — returning success
(suppressing the dead-end dialog). **Bridge-only → plain push, NO boot regen.**
New bridge md5 `60126181`.

Helpers added: `shareJsonUnescape`, `shareExtractKey`, `shareCopyToPasteboard`.

#### Build wiring (in `build/inner/compile_oh_adapter_bridge.sh`)
Add include paths (NDK C headers):
```
-I$OH/foundation/distributeddatamgr/pasteboard/interfaces/ndk/include
-I$OH/foundation/distributeddatamgr/udmf/interfaces/ndk/data
```
Add link libs (NDK .so on device at `/system/lib/ndk/lib{pasteboard,udmf}.so`,
build-time in `$OH/out/rk3568/packages/phone/system/lib/ndk`, already on `-L$NDK`):
```
-lpasteboard -ludmf
```
Verify the resulting `.so` lists `libpasteboard.so` + `libudmf.so` as NEEDED and
`OH_Pasteboard_*` / `OH_Udmf*` / `OH_UdsPlainText_*` as UND (resolved at runtime).

Headers are minimal C (`oh_pasteboard.h` ← `inttypes.h`; `udmf.h` ← `uds.h`);
include order uds → udmf → oh_pasteboard. Destroy each UDMF object independently
(Add* deep-copy internally).

Build cmd:
```
cd <bridge-build> && OH_ROOT=<oh> AOSP_ROOT=<bridge>/aosp ADAPTER_ROOT=<bridge> \
  BRIDGE_TMP=/tmp/bridge_build bash build/build_adapter.sh --target=liboh_adapter_bridge.so
```
Deploy: push to BOTH `/system/lib/liboh_adapter_bridge.so` and
`/system/android/lib/liboh_adapter_bridge.so`, then restart noice (reboot for a
clean AMS↔appspawn-x state). The bridge loads at app-spawn.

> The full edited `oh_ability_manager_client.cpp` (with the 3 helpers + the
> intercept in `startAbilityWithCaller`) and `IntentWantConverter.java`
> (chooser-unwrap) are included in this directory.
