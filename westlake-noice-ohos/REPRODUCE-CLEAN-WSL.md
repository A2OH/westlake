# REPRODUCE-CLEAN-WSL — noice on OpenHarmony, from a fresh WSL machine

The single authoritative, ordered guide to reproduce the **current deployed**
state: the Android app **noice** (`com.github.ashutoshgngwr.noice`) running on an
**OpenHarmony DAYU200 / RK3568** device via the **appspawn-x** AOSP adapter, with
**every fix from this project applied**.

> **Two reproduction paths.** Most people want the **fast path**: deploy the
> prebuilt fixed binaries this repo ships / `collect-artifacts.sh` pulls, then bring
> up and launch. The **build-from-source path** (§2) is for rebuilding each fix.
> Both end at the same device state (§3–§4).
>
> Companion files: **`MANIFEST.md`** (artifact→path→md5→source→build table),
> **`collect-artifacts.sh`** (pull the prebuilt set off a working device),
> **`REPRODUCE.md`** (the older, deeper root-cause narrative — still valid for the
> *why*; md5s there are from an earlier deploy, this file supersedes them),
> **`docs/`** (full root-cause history).
>
> **GAPS that would block a 100%-from-scratch clean-WSL repro are listed in §6.**
> Read §6 before assuming you can build everything.

Deployed component md5s this guide targets (verified on device 2026-06-07):

| Component | md5 |
|-----------|-----|
| `libart.so` (proxy fix) | `7b856a2d` |
| `adapter-mainline-stubs.jar` (SSLSockets) | `41834c1f` |
| `oh-adapter-framework.jar` (share-chooser) | `300581d1` |
| `adapter-runtime-bcp.jar` (TLS `$Sf`) | `d5d39a05` |
| `framework.jar` (universal service fixes) | `8524dc56` |
| `liboh_adapter_bridge.so` (drag bridge) | `82b0d82a` |
| `libhwui.so` (G3.8 + EGL) | `8b8f84ec` |
| `liboh_android_runtime.so` (un-rebuildable base) | `f82d8cdc` |
| native shims (setgid/dns/jdns/netlog/w14/v4force/tlsjni), `tlsjni-extra.dex` | see MANIFEST §B |

---

## 0. Prerequisites + tool install (clean WSL)

A clean Ubuntu-on-WSL2. Install host tools:

```bash
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk unzip zip python3 git curl rsync
# Android SDK command-line tools (for d8 / apksigner / zipalign / aapt2 / baksmali)
```

Provide / install these exact prerequisites. The **paths below are the ones this
project uses**; if you relocate them, edit the build scripts accordingly.

| Tool | Path used here | How to get it | Notes |
|------|----------------|---------------|-------|
| **OHOS clang prebuilt** | `$HOME/openharmony/prebuilts/clang/ohos/linux-x86_64/llvm/bin/clang(++)` | Part of the OpenHarmony source tree (`prebuilts/`) | arm32 target `--target=arm-linux-ohos`; clang 15.0.4 |
| **OHOS source tree** (`OH_ROOT`) | `$HOME/openharmony` | `repo init/sync` OHOS 4.x (RK3568); large (~100 GB built) | Needed for the musl sysroot + clang + headers |
| **OHOS musl sysroot** | `$OH_ROOT/out/rk3568/obj/third_party/musl/usr` + `.../lib/arm-linux-ohos/libc.so` | produced by building OHOS for rk3568 | `-isystem` + link `libc.so` for the shims |
| **AOSP source tree** (`AOSP_ROOT`) | `$HOME/bridge-build/aosp` | AOSP checkout matching the adapter's base | bridge + libhwui includes |
| **Android NDK / jni.h** | (jni.h dir under the OHOS interface SDK or an NDK) | NDK r25+, or OHOS `interface/sdk_c` | for `libtlsjni.c` |
| **Android SDK build-tools 34.0.0** | `$HOME/android-sdk/build-tools/34.0.0/{d8,apksigner,zipalign,aapt2}` | `sdkmanager "build-tools;34.0.0"` | dex compile / sign / align |
| **android.jar (android-34)** | `$HOME/android-sdk/platforms/android-34/android.jar` | `sdkmanager "platforms;android-34"` | compile Java targets; device matches API 34 |
| **smali / baksmali jars** | under `$SDK/cmdline-tools/latest/lib/external/...` (`smali-baksmali-3.0.3.jar`, `smali-dexlib2`, `smali-util`, `guava`, `jcommander`) | bundled with cmdline-tools, or download from the smali project | baksmali (disassemble) |
| **apktool.jar 2.9.3** | `$HOME/apktool.jar` | apktool release | APK decode/build **and** `brut.androlib.mod.SmaliMod` (the assembler `SmaliAssemble.java` wraps) |
| **dex2oat64 + libsigchain.so** | `$HOME/tools/dex2oat64`, `$HOME/tools/lib64/libsigchain.so` | host ART dex2oat for arm target | boot-image regen |
| **libart build bundle** | `$HOME/libart-32arm-cache/libart-32arm-pathA-bundle/{oh,aosp,adapter}` | the pre-staged libart source+headers bundle | needed by `build_libart_pathA.sh` |
| **hdc.exe** | `/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe` | OpenHarmony SDK (Windows) | invoked from WSL; see device-access quirk below |

**Device access (hdc from WSL via Windows hdc.exe):**

```bash
HDC=/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe
WINDIR='C:\Users\dspfa\Dev\ohos-tools'    # same dir, Windows view
WSLWIN=/mnt/c/Users/dspfa/Dev/ohos-tools  # same dir, WSL view
sh() { $HDC shell "$1" 2>&1 | tr -d '\r' | grep -v WSL; }   # strip CRLF + WSL noise
```

**File-send quirk (critical):** `hdc.exe file send` with a WSL/relative *source*
mangles the destination. Always copy the file into the Windows tools dir first and
send the **Windows** path; verify remote size == local; retry big files up to 5×:

```bash
cp myfile.so "$WSLWIN/x.so"
$HDC file send "$WINDIR\\x.so" /data/local/tmp/x.so 2>&1 | tr -d '\r' | grep -iE 'finish|fail'
```

**File-recv quirk:** recv to a path UNDER the Windows tools dir, then cp into WSL
(recv to an arbitrary absolute WSL path mangles).

---

## 1. Get sources

```bash
git clone <A2OH/westlake>  /tmp/a2oh-westlake
cd /tmp/a2oh-westlake/westlake-noice-ohos
```

This repo already contains: all fix sources (bridge/libhwui cpp, libart snippet,
SSLSockets, IntentWantConverter, native-TLS, native-shim `.c`), the smali patches,
the committed small fixed binaries (`prebuilt-jars/`, `prebuilt-native/`,
`bridge-drag/`, `runtime-proxy-fix/`, `native-libs/`, `native-tls/`, `ca-store/`),
the boot-regen script reference, and the test fixtures.

The large external inputs (OHOS tree, AOSP tree, libart bundle, the stock noice
APK, the boot image, `framework.jar`) are **not** in the repo — see §0 table and
§6 GAPS for how to obtain each.

---

## 2. Build each fix from source

> Each fix loads at a different layer. **Per-child** loaders (bridge, libhwui,
> base.apk) need only a force-stop + relaunch. **BCP** loaders (any of the 10 jars,
> libart, native shims) need a boot-image regen and/or appspawn-x restart + reboot.

Common env for the build scripts:

```bash
export OH_ROOT=$HOME/openharmony
export AOSP_ROOT=$HOME/bridge-build/aosp
export SDK=$HOME/android-sdk
export BT=$SDK/build-tools/34.0.0
export APKTOOL=$HOME/apktool.jar
CLANG=$OH_ROOT/prebuilts/clang/ohos/linux-x86_64/llvm/bin/clang
SR=$OH_ROOT/out/rk3568/obj/third_party/musl/usr            # musl sysroot
LIBC=$SR/lib/arm-linux-ohos/libc.so                        # adjust to your tree
```

### 2.0 — smali assembler helper (used by several fixes)

apktool's bundled `baksmali.Main` has no assembler `main()`, so this repo ships
`scripts/SmaliAssemble.java` wrapping `brut.androlib.mod.SmaliMod`:

```bash
javac -cp "$APKTOOL" scripts/SmaliAssemble.java -d /tmp/sa
# assemble:    java -cp "$APKTOOL:/tmp/sa" SmaliAssemble <smali_dir> <out_classes.dex> [api]
# disassemble: java -cp "<smali-baksmali+dexlib2+util+guava+jcommander jars>" \
#                com.android.tools.smali.baksmali.Main d classes.dex -o out_smali
```

### 2.1 — libart proxy iftable fix → `libart.so` (`7b856a2d`)

Bug: the custom FIX-IFTABLE pass damaged dynamic-proxy ($ProxyN) iftables →
`AbstractMethodError` on every Retrofit/OkHttp suspend call → "network unreachable".
Fix: gate the pass with `&& !klass->IsProxyClass()` (class_linker.cc ~9450). See
`runtime-proxy-fix/README.md` + `class_linker.cc.FIX-IFTABLE-PROXY.snippet`.

```bash
bash $HOME/libart-pathA-work/build_libart_pathA.sh
# -> $HOME/libart-pathA-work/out/libart.so   (md5 7b856a2d)
```
Deploy: plain push to `/system/android/lib/libart.so`, **no boot regen**, reboot (§3).

### 2.2 — SSLSockets BCP stub → `adapter-mainline-stubs.jar` (`41834c1f`)

Bug: bundled okhttp `Android10SocketAdapter` calls
`android.net.ssl.SSLSockets.isSupportedSocket` — class absent from BCP →
`NoClassDefFoundError` → call canceled → "网络无法访问". Fix: add a functional stub
(`sslsockets-fix/SSLSockets.java`).

```bash
mkdir -p /tmp/sslfix/classes
javac -source 8 -target 8 -bootclasspath $SDK/platforms/android-34/android.jar \
  -d /tmp/sslfix/classes sslsockets-fix/SSLSockets.java
$BT/d8 --min-api 26 --output /tmp/sslfix/stubdex \
  $(find /tmp/sslfix/classes -name '*.class')
# pull the deployed jar, MERGE the stub dex INTO its existing classes.dex (v39):
# the existing dex has NO smali assembler available, so use d8 merge, NOT baksmali.
cp prebuilt-jars/adapter-mainline-stubs.jar.41834c1f /tmp/sslfix/orig.jar  # or collect from device
mkdir -p /tmp/sslfix/merged
( cd /tmp/sslfix && unzip -o orig.jar classes.dex -d /tmp/sslfix/origdex )
$BT/d8 --min-api 28 --output /tmp/sslfix/merged \
  /tmp/sslfix/origdex/classes.dex /tmp/sslfix/stubdex/classes.dex
cp /tmp/sslfix/orig.jar /tmp/sslfix/new.jar
( cd /tmp/sslfix/merged && zip -j /tmp/sslfix/new.jar classes.dex )   # keep META-INF/MANIFEST.MF
md5sum /tmp/sslfix/new.jar   # expect 41834c1f
```
Deploy: push to `/system/android/framework/adapter-mainline-stubs.jar` + **boot
regen** (§2.11) — this jar IS in the BCP.

### 2.3 — share/createChooser fix → `oh-adapter-framework.jar` (`300581d1`)

Bug: `Intent.createChooser` makes an `ACTION_CHOOSER` intent (real `ACTION_SEND`
nested in `EXTRA_INTENT`); `IntentWantConverter.mapAction` had no `ACTION_CHOOSER`
case → bogus Want → StartAbility crash. Fix: at the top of `intentToWant`, if
action == `android.intent.action.CHOOSER`, replace the intent with its
`android.intent.extra.INTENT` parcelable. Source: `share-fix/IntentWantConverter.java`.
`IntentWantConverter` lives in **oh-adapter-framework.jar** (NOT adapter-runtime-bcp).

```bash
# baksmali adapter/activity/IntentWantConverter.smali out of the jar, inject the
# ACTION_CHOOSER unwrap right after `.registers 6` (method has free regs v0/v1 —
# no .locals bump), SmaliAssemble back, rezip into a copy of the jar (keep META-INF).
java -cp "<baksmali jars>" com.android.tools.smali.baksmali.Main d \
  prebuilt-jars/oh-adapter-framework.jar.300581d1 -o /tmp/ohaf_smali   # or collect
#   ... edit /tmp/ohaf_smali/adapter/activity/IntentWantConverter.smali ...
java -cp "$APKTOOL:/tmp/sa" SmaliAssemble /tmp/ohaf_smali /tmp/ohaf_classes.dex 30
cp prebuilt-jars/oh-adapter-framework.jar.300581d1 /tmp/ohaf.jar
( cd /tmp && zip -j ohaf.jar ohaf_classes.dex && mv ohaf_classes.dex classes.dex )  # name must be classes.dex
md5sum /tmp/ohaf.jar   # expect 300581d1
```
Deploy: push to `/system/android/framework/oh-adapter-framework.jar` + **boot regen**
(it is the LAST jar in the BCP).

### 2.4 — native-TLS `$Sf` ctor → `adapter-runtime-bcp.jar` (`d5d39a05`)

The stub `TlsShimProvider$Sf.createSocket` is patched to load `TlsJniSocket` via a
DexClassLoader from the out-of-BCP `tlsjni-extra.dex`. Source/smali:
`native-tls/TlsShimProvider_Sf_*.smali`, `TlsShimProvider_Sf_DexClassLoader.java`.
Build per §5F of `REPRODUCE.md` (splice the DexClassLoader createSocket body into
`$Sf` inside the jar's classes.dex via SmaliAssemble). Deploy + **boot regen**.
**Leave the `$Sf` ctor field byte-identical** — the share fix (§2.3) is in a
*different* jar specifically to avoid disturbing it.

### 2.5 — universal framework service fixes → `framework.jar` (`8524dc56`)

Patches the framework BCP jar so `getSystemService(...)` fetchers return non-null
managers + manager methods null-guard (ShortcutManager, AlarmManager,
ContentResolver). Smali in `framework-smali-patches/`. The universal win: an
unguarded noice survives the Saved + add-alarm crashes with only this fix.

```bash
# baksmali each patched class out of framework.jar, apply the smali from
# framework-smali-patches/, SmaliAssemble, rezip (preserve manifest), re-baksmali to verify.
```
Deploy + **boot regen** (§2.11).

### 2.6 — input/drag bridge → `liboh_adapter_bridge.so` (`82b0d82a`)

In-process D-pad + touch + **drag** dispatch + the `/data/local/tmp/noice_tap`
control channel (`"x y"`=tap, `"x1 y1 x2 y2"`=drag, `"N"`=nav tab) + the
VelocityTracker JNI stub. Source: `bridge-drag/oh_input_bridge.cpp` (+
`bridge-src/oh_window_manager_client.cpp`, `oh_input_bridge.h`).

```bash
cd $HOME/bridge-build
OH_ROOT=$HOME/openharmony AOSP_ROOT=$HOME/bridge-build/aosp \
ADAPTER_ROOT=$HOME/bridge-build BRIDGE_TMP=/tmp/bridge_build \
bash build/build_adapter.sh --target=liboh_adapter_bridge.so
# -> out/adapter/liboh_adapter_bridge.so  (md5 82b0d82a)
```
Deploy: push to **BOTH** `/system/lib/liboh_adapter_bridge.so` **and**
`/system/android/lib/liboh_adapter_bridge.so` (noice loads `/system/lib/`).
Per-child → force-stop + clear `$BUNDLE/oat` + relaunch (no reboot).

### 2.7 — libhwui → `libhwui.so` (`8b8f84ec`)

G3.8 `ASurfaceControl_release` no-op + gated glReadPixels + new-surface EGL fix.
Source: `bridge-src/hwui_oh_abi_patch.cpp`.

```bash
cd $HOME/bridge-build
OH_ROOT=$HOME/openharmony AOSP_ROOT=$HOME/bridge-build/aosp \
ADAPTER_ROOT=$HOME/bridge-build BRIDGE_TMP=/tmp/bridge_build \
bash build/build_aosp_lib.sh --target=libhwui.so
```
> **GAP:** rebuilding `libhwui.so` to the exact deployed `8b8f84ec` needs the HBC
> build base; user/clean-WSL builds may differ (§6). Use the collected prebuilt
> if your rebuild regresses.

### 2.8 — native LD_PRELOAD shims + native TLS

`libjdnshook` (v2) and `libv4force` have committed source; `libtlsjni` too. Build
pattern (OHOS clang, arm32, against the musl sysroot):

```bash
$CLANG --target=arm-linux-ohos -fPIC -shared -O2 -nostdlib \
  -isystem $SR/include/arm-linux-ohos \
  -o /tmp/libv4force.so native-libs/libv4force.c $LIBC
$CLANG --target=arm-linux-ohos -fPIC -shared -O2 -nostdlib \
  -isystem $SR/include/arm-linux-ohos \
  -o /tmp/libjdnshook.so native-libs/libjdnshook_v2.c $LIBC
# libtlsjni additionally needs jni.h:
$CLANG --target=arm-linux-ohos -fPIC -shared -O2 -nostdlib \
  -I<jni.h dir> -isystem $SR/include/arm-linux-ohos \
  -o /tmp/libtlsjni.so native-tls/libtlsjni.c $LIBC
```
> **GAP:** `libsetgidhook`, `libdnshook`, `libnetlog`, `libw14supp` have **no
> committed source** (§6). Use the committed prebuilt binaries in
> `prebuilt-native/` (md5s match deployed).

`tlsjni-extra.dex` (TlsJni + TlsJniSocket, out-of-BCP): compile against android-34
`-source 8 -target 8`, d8 `--min-api 28`; do NOT implement `getPeerCertificateChain`
and use named nested classes only (§5F of REPRODUCE.md). Committed:
`native-tls/tlsjni-extra.dex`.

### 2.9 — CA trust store

`ca-store/cacerts.tgz` (md5 `888d018d`) → extract into
`/system/etc/security/cacerts/`, `chmod 644 *.0`, chcon `system_file`.

### 2.10 — noice APK (coroutine fix, debug-signed)

The only APK-level patch still required: `noice-smali-patches/kotlinx-coroutines/a.smali`
(`handleCoroutineException` swallows bg-coroutine exceptions; only kills on main).
The runtime intercepts uncaught coroutine exceptions before any Java handler, so
the framework UEH can't do this — it must live in the APK.

```bash
java -jar $APKTOOL d <stock-noice>.apk -o noice_dec
cp noice-smali-patches/kotlinx-coroutines/a.smali \
   noice_dec/smali*/kotlinx/coroutines/a.smali       # match the right classesN
java -jar $APKTOOL b noice_dec -o noice-unsigned.apk
$BT/zipalign -p -f 4 noice-unsigned.apk noice-aligned.apk
$BT/apksigner sign --ks ~/.android/debug.keystore --ks-pass pass:android \
  --out noice-signed.apk noice-aligned.apk
```
> **GAP:** the stock noice base APK is not committed (license). Obtain it from
> F-Droid / the noice project. Deployed patched md5 `5c58abca`.

### 2.11 — boot-image regen (required by §2.2–2.5)

Any change to a BCP jar needs the boot image regenerated. **Inputs already staged**
at `/tmp/tagsoup-boot/jars/` (all 10 jars; md5s match the deployed BCP). Replace the
jar you rebuilt, then:

```bash
bash /tmp/tagsoup-boot/regen.sh
# (canonical script: docs/engine/V3-5APP-V2-EVIDENCE/regen_boot.sh)
# -> /tmp/tagsoup-boot/out/boot*.{art,oat,vdex}  (30 segments, ~16 s)
```
The script drives host `dex2oat64` over the 10 jars in **BCP order**
(core-oj core-libart core-icu4j okhttp bouncycastle apache-xml
adapter-mainline-stubs framework adapter-runtime-bcp oh-adapter-framework),
`--instruction-set=arm --base=0x70000000 --compiler-filter=speed`.

---

## 3. Deploy to device

> Snapshot the current working boot+jars to `/data/local/tmp/<rollback>` BEFORE
> deploying anything BCP-level. The companion fast path: just push the committed /
> collected prebuilt set — same md5s, no build needed.

Per-target deploy (push via the Windows-path quirk §0; chcon as shown):

| Target | Push to | chcon | Reboot/restart |
|--------|---------|-------|----------------|
| `libart.so` | `/system/android/lib/libart.so` | `system_lib_file` | **reboot** (no boot regen) |
| BCP jars (`adapter-mainline-stubs`, `framework`, `adapter-runtime-bcp`, `oh-adapter-framework`) | `/system/android/framework/<jar>` | `system_file` | **boot regen** then reboot |
| boot image (30 segs) | `/system/android/framework/arm/` | `system_file` | with the jar; `rm -rf /data/misc/appspawnx/dalvik-cache/*` then **reboot** |
| `liboh_adapter_bridge.so` | `/system/lib/` **and** `/system/android/lib/` | `system_lib_file` | force-stop noice + clear `$BUNDLE/oat` + relaunch |
| `libhwui.so` | `/system/android/lib/libhwui.so` | `system_lib_file` | force-stop + relaunch |
| native shims (`lib*.so`) | `/system/android/lib/` | `system_lib_file` | add to `start_asx.sh` LD_PRELOAD + restart appspawn-x |
| `tlsjni-extra.dex` | `/system/android/framework/tlsjni-extra.dex` | `system_file` | (with boot regen) |
| noice `base.apk` | `$BUNDLE/android/base.apk` (chmod 644, chown 0:0) | `app_install_file` | clear `$BUNDLE/oat`+`code_cache`, force-stop + relaunch |
| `cacerts.tgz` | extract to `/system/etc/security/cacerts/` | `system_file` | none |

where `$BUNDLE=/data/app/el1/bundle/public/com.github.ashutoshgngwr.noice`.

**Boot-image deploy detail** (30 large files): push each `boot*.{art,oat,vdex}` to
`/system/android/framework/arm/` (size-verify + retry), then the jar to
`/system/android/framework/`, chcon `system_file`,
`rm -rf /data/misc/appspawnx/dalvik-cache/*`, **reboot**. HW-gate before relying on
it: HelloWorld (or noice) reaches onResume with no `Class mismatch | InitWithoutImage
| cppcrash | Fatal`.

---

## 4. Bring up + launch noice + verify

> appspawn-x launched by init runs AT_SECURE and **strips LD_PRELOAD** → shims
> never load. Launch it **from the hdc shell domain** via `start_asx.sh`, then fix
> the socket label. Run this whole sequence after every reboot.

`start_asx.sh` (exact deployed content; LD_PRELOAD order matters — `libjdnshook`
**after** `libdnshook`):

```sh
#!/bin/sh
rm -f /dev/unix/socket/AppSpawnX
rm -f /data/local/tmp/asx_run.{out,err}
rm -f /data/service/el1/public/appspawnx/adapter_child_*.stderr
export LD_PRELOAD=/system/android/lib/libsetgidhook.so:/system/android/lib/libw14supp.so:/system/android/lib/libdnshook.so:/system/android/lib/libnetlog.so:/system/android/lib/libjdnshook.so:/system/android/lib/libv4force.so
exec /system/bin/appspawn-x --socket-name AppSpawnX > /data/local/tmp/asx_run.out 2> /data/local/tmp/asx_run.err
```

Bring-up:

```bash
# USB convenience (set once): keep hdc on, kill the USB-mode dialog that backgrounds noice
sh "param set persist.sys.usb.config hdc_debug; param set persist.usb.setting.gadget_conn_prompt false"

# 1. SELinux permissive + the perf cgroup the adapter expects
sh "mkdir -p /dev/memcg/perf_sensitive 2>/dev/null; setenforce 0"
# 2. Launch appspawn-x detached from the shell domain (preloads survive)
sh "setsid sh /data/local/tmp/start_asx.sh </dev/null >/dev/null 2>&1 &"; sleep 14
# 3. MANDATORY: relabel the AppSpawnX socket or AMS can't reach it (silent no-spawn)
sh "chmod 0666 /dev/unix/socket/AppSpawnX; chcon u:object_r:appspawn_socket:s0 /dev/unix/socket/AppSpawnX; setenforce 0"
# 4. Keep the screen awake (idle lock steals focus)
sh "power-shell wakeup; power-shell timeout -o 86400000"
# 5. Grant noice's uid internet in the netsys eBPF map (re-run; netsys may reset)
sh "/data/local/tmp/bpfgrant 13731 oh_sock_permission_map"
sh "/data/local/tmp/bpfgrant 13731 broker_sock_permission_map"
```

Verify exactly ONE appspawn-x has the preloads:
```bash
sh "grep -l setgidhook /proc/*/maps"   # exactly one match; two = socket conflict, kill stragglers + re-run
```

Launch noice (populated, from the cached library so the list isn't blank):
```bash
BASE=/data/app/el1/0/base/com.github.ashutoshgngwr.noice
sh "cp /data/local/tmp/noice-room.db.bak \$BASE/databases/com.github.ashutoshgngwr.noice.db 2>/dev/null
    cp /data/local/tmp/noice-cdn-cache.bak/* \$BASE/cache/cdn-cache/ 2>/dev/null
    chown -R 13731:13731 \$BASE/databases \$BASE/cache 2>/dev/null"
sh "power-shell wakeup; aa start -a com.github.ashutoshgngwr.noice.activity.MainActivity -b com.github.ashutoshgngwr.noice"
```
A COLD launch (after force-stop) is the only reliable way to land WMS focus. Budget
~4 force-stops per reboot before AMS degrades, then reboot.

**Verify (in-process truth, not pixels):**
- **Alive/crash:** find the uid-13731 pid before/after an action; same pid = survived.
- **Render:** `snapshot_display -f /data/local/tmp/u.jpeg` then recv; **>45 KB = populated**.
- **Nav:** class markers in `/data/service/el1/public/appspawnx/adapter_child_<pid>.stderr`.
- **Drive the UI:** `echo "X Y" > /data/local/tmp/noice_tap` (tap), `echo "x1 y1 x2 y2"`
  (drag/scroll), `echo "N"` (nav tab 1–5) — focus-independent control channel.
- **Regression fixtures:** `bash test-fixtures/ux_full_fixture.sh` (per-page/submenu),
  `bash test-fixtures/uxfixture.sh` (13-step). Coordinate map + PASS/FAIL model in
  `REPRODUCE.md` §8.

**Expected end state** (all validated; screenshots in `screenshots/`):
library renders (LIFE group + Birds/Crickets/Heartbeat/Purring-Cat, name·tags·★ +
4-button row); SoundInfo / volume sheet / Saved / sleep-timer / alarms / add-alarm
TimePicker / account + submenus all crash-free; **subscription page loads plans +
prices** (the SSLSockets + libart-proxy + native-TLS + drag-scroll stack); **share
("与朋友分享") no longer crashes** (createChooser fix); D-pad + touch + drag all work.

---

## 5. Quick reference — what each fix unblocks

| Fix | Unblocks |
|-----|----------|
| libart proxy guard (`7b856a2d`) | Retrofit/OkHttp dynamic proxies → subscription network calls dispatch |
| SSLSockets stub (`41834c1f`) | okhttp Android10 TLS path → no `NoClassDefFoundError` → subscription plans load |
| share-chooser unwrap (`300581d1`) | "与朋友分享" no longer crashes (ACTION_CHOOSER → sendData) |
| native-TLS `$Sf` (`d5d39a05`) | createSocket returns a real native-OpenSSL socket |
| framework service fixes (`8524dc56`) | ShortcutManager (Saved) + AlarmManager (add-alarm) + ContentResolver — universal |
| bridge drag/input (`82b0d82a`) | D-pad + touch + scroll/drag + tap control channel |
| libhwui (`8b8f84ec`) | stable render, SoundInfo 2nd surface, no teardown SIGSEGV |
| native shims + bpfgrant | inet gid, DNS, AF_INET, cgroup-eBPF socket grant |
| noice `a.smali` coroutine fix | subscription/Register survive a flaky network fetch |

---

## 6. GAPS — what would still block a 100% clean-WSL repro

Honest list. Items marked **(prebuilt shipped)** still reproduce via the committed /
collected binary; only *building them from source* is blocked.

1. **`liboh_android_runtime.so` (`f82d8cdc`) — un-rebuildable base, no source.**
   Every local rebuild regresses noice at render-thread init. Treated as a fixed
   blob. **Must be collected from a working device / device image.** This is the
   single hardest dependency: a truly fresh device with a *different* runtime base
   may not match. **(prebuilt: collect only — not committed, 1.3 MB+, GAP).**

2. **Native shims with no committed source:** `libsetgidhook.so`, `libdnshook.so`,
   `libnetlog.so`, `libw14supp.so`. Binaries are committed in `prebuilt-native/`
   (md5s match deployed) so reproduction works, but you **cannot rebuild them** —
   no `.c` exists anywhere in the trees. `libw14supp.so` in particular is the
   required substrate. **(prebuilt shipped; source GAP).**

3. **`libtlsjni.so` / `libv4force.so` — committed binary md5 ≠ deployed md5.**
   Committed `native-tls/libtlsjni.so` is `e248cc47`, deployed is `409b24a2`;
   committed `native-libs/libv4force.so` is `7c3e5ece`, deployed is `afe84b5f`.
   Source IS committed and rebuilds a functional shim, but the exact deployed bytes
   are a later build. Use the committed source-built shim (functionally
   equivalent) or collect the exact deployed binary. **(source shipped; exact-byte GAP).**

4. **`libhwui.so` (`8b8f84ec`) — source committed, exact-byte rebuild needs the HBC
   base.** Clean-WSL `build_aosp_lib.sh` may produce a functionally-similar but
   not byte-identical lib, and historically regressed; collect the prebuilt if your
   rebuild misbehaves. **(source shipped; exact-byte/regression GAP).**

5. **`framework.jar` (`8524dc56`) — 15 MB, not committed.** Rebuildable from
   `framework-smali-patches/` + a pulled stock `framework.jar` + boot regen, or
   collect. **(build or collect; not committed for size).**

6. **Boot image (30 segments, ~143 MB) — not committed.** Fully regenerable from
   the 10 BCP jars (`/tmp/tagsoup-boot/regen.sh`; inputs staged) or collect.
   **(regenerable; not committed for size).**

7. **Stock noice APK — not committed (license).** Obtain from F-Droid / the noice
   project, then apply `noice-smali-patches/`. **(external dependency).**

8. **Device-side tools `apk_install`, `bpfgrant`, `conntest` — binaries only.**
   `bpf-analysis/netsys-ebpf.c` documents the eBPF map scheme `bpfgrant` writes,
   but there is no committed build for the tools themselves. Collect from device.
   **(collect; build GAP).**

9. **External source trees (OHOS `OH_ROOT`, AOSP `AOSP_ROOT`, the libart bundle)
   are huge and not in the repo.** They must be checked out / staged at the exact
   paths in §0 (or the build scripts edited). The libart bundle
   (`$HOME/libart-32arm-cache/...`) is a pre-staged source+header set; a
   clean WSL must obtain it to run `build_libart_pathA.sh`. **(external dependency).**

10. **Device starting state.** This package **patches an existing appspawn-x AOSP
    adapter install** under `/system/android/`; it does **not** bootstrap that
    adapter onto a stock OHOS image. A truly clean device needs the base adapter
    flashed first (out of scope here). **(device-image prerequisite, out of scope).**

11. **Live HTTPS still not 100% end-to-end** (functional gap, not a repro gap):
    DNS/gids/socket-family/CA/native-TLS-createSocket + the SSLSockets + libart-proxy
    fixes get the subscription page to load, but the deeper okhttp↔conscrypt coupling
    and a cold-reboot-flaky cgroup-BPF grant remain (see `STATUS.md`). The UI is
    populated from a cached library for determinism.

**Bottom line:** with the committed prebuilt set + `collect-artifacts.sh` for the
large/binary-only pieces + a matching device, the **deploy-and-run path (§3–§4) is
fully reproducible today**. The **build-everything-from-source path is blocked on
the un-rebuildable runtime (gap 1), 4 source-less shims (gap 2), and the external
trees (gap 9)**; everything else builds from sources in this repo.
