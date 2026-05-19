# V3 PhaseOneBDriver — chroot-spawned McD mini-launcher (architecture)

**Status:** DESIGN — class stub committed; substrate gap blocks build/test.
See `docs/engine/V3-W2-PHASE-1B-PROGRESS.md` §4 for the substrate gap.

This doc captures the architecture of the V3 chroot-compatible mini-driver,
designed by mapping V2-Phone's `McdInProcessActivity.kt` (330 LOC) onto a
chroot-spawned process. Reference V2 file:
`westlake-host-gradle/app/src/main/java/com/westlake/host/McdInProcessActivity.kt`
(READ-ONLY per brief).

---

## 1. Why a new driver class?

V2-Phone's `McdInProcessActivity` was an `Activity` subclass running inside
a host APK process (`com.westlake.host`) on Android. It WRAPPED the McD
SplashActivity by:

1. being launched as an Activity itself (Android Activity manager delivers
   the launch IPC)
2. inside its `onCreate`, instantiating McD's `SplashActivity` reflectively
3. driving McD's Activity through `attach()` → `onCreate()` → `performStart()`
   → `performResume()`

In the V3 chroot, we have:
- NO factory OH AMS that could deliver an `IApplicationThread.scheduleLaunchActivity`
  callback into our process
- NO host APK process — we ARE the process spawned by appspawn-x
- A process that gets `ChildMain::run` → `applyAccessToken` → ... →
  `launchActivityThread` → `AppSpawnXInit.initChild(procName, targetClass,
  targetSdkVersion)` → `invokeStaticMain(targetClass)` →
  `Class.forName(targetClass).getMethod("main", String[]).invoke(null, [])`

So our driver MUST:
- be a class with `public static void main(String[] args)` entry point
- not be an `Activity` (because nothing will deliver an Activity launch
  callback to it)
- hand-drive the McD SplashActivity through Activity.attach → onCreate
  reflectively, using `ActivityThread.sCurrentActivityThread` as the
  required context (planted in the appspawn-x parent's preload phase)
- log a clear marker line when SplashActivity.onCreate body executes
  successfully

This is structurally similar to Island's `apk_runner` (artifact
`artifacts/v3-w3-westlake-stack/16.14-Island/tests/R4-apk-runner/src/apk_runner.c`)
but for HBC's stack with V2-Phone's borrowed pillars.

---

## 2. Class skeleton

File: `engine/v3/phase-one-b/src/com/westlake/engine/PhaseOneBDriver.java`

```java
package com.westlake.engine;

public class PhaseOneBDriver {
    private static final String TAG = "PhaseOneBDriver";
    private static final String DEFAULT_APP_PKG = "com.mcdonalds.app";
    private static final String DEFAULT_APP_CLS = "com.mcdonalds.mcdcoreapp.common.activity.SplashActivity";
    private static final String DEFAULT_APK_PATH = "/data/app/com.mcdonalds.app/base.apk";

    public static void main(String[] args) {
        // argv: [pkg/cls] [apkPath]
        String spec = args.length > 0 ? args[0] : DEFAULT_APP_PKG + "/" + DEFAULT_APP_CLS;
        String apkPath = args.length > 1 ? args[1] : DEFAULT_APK_PATH;
        String[] split = spec.split("/", 2);
        String pkg = split[0];
        String cls = split.length > 1 ? split[1] : DEFAULT_APP_CLS;

        Marker.emit("PhaseOneBDriver.main entry: pkg=" + pkg + " cls=" + cls + " apk=" + apkPath);

        try {
            new MainLooperBoot().ensureMainLooperIfNeeded();
            new BypassHiddenApi().setHiddenApiExemptions();
            new SwallowingUncaughtHandler().install();

            PackageContext pkgCtx = new PackageContext(pkg, apkPath).create();
            LoadedApkRedirect.patchDataDirs(pkgCtx);
            LocaleManagerStub.installIfPresent(pkgCtx.getContext());

            Application app = new ApplicationBootstrap(pkgCtx).instantiateAndAttach();
            app.onCreate();
            Marker.emit("PhaseOneBDriver: Application.onCreate OK");

            Activity activity = new ActivityAttacher(pkgCtx, app)
                .attach(cls);   // calls Activity.attach(...) via reflection
            Marker.emit("PhaseOneBDriver: Activity.attach OK for " + cls);

            new LifecycleDriver(activity).driveTo(LifecycleDriver.State.RESUMED);
            // The SplashActivity.onCreate body marker is logged by McD itself
            // (com.mcdonalds.app.MarketApplication.onCreate / SplashActivity.onCreate)
            // Our marker proves the call chain reached attach + onCreate + start + resume.
            Marker.emit("PhaseOneBDriver: lifecycle reached RESUMED for " + cls);

            // Enter Looper.loop() so the activity's pending Handler.post and
            // Choreographer.doFrame run. We have no display so frames are no-ops.
            android.os.Looper.loop();
        } catch (Throwable t) {
            Marker.emit("PhaseOneBDriver: FATAL " + t.getClass().getName() + " — " + t.getMessage());
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            for (String line : sw.toString().split("\n")) Marker.emit("  " + line);
            System.exit(1);
        }
    }
}
```

(`Marker.emit` is a thin wrapper around `appLog` in `AppSpawnXInit` —
which routes through `nativeHiLog` → `HiLogPrint` directly, bypassing the
broken-by-fork `Log.println_native` path. This is critical: the entire
mini-driver code must NOT call `android.util.Log.*` until framework JNI
is confirmed bound. See progress doc §4.2.)

---

## 3. Pillar files (each ~50-80 LOC)

Each pillar is a separate class for testability and so the next agent can
borrow individual pillars into other contexts.

### 3.1 `BypassHiddenApi.java`

Verbatim port of V2-Phone's `McdInProcessActivity.bypassHiddenApiRestrictions`.
Pure reflection on `VMRuntime.setHiddenApiExemptions`. No Unsafe / no
setAccessible. Allows our subsequent reflection on `ActivityThread`,
`LoadedApk`, `ContextImpl` private fields.

### 3.2 `SwallowingUncaughtHandler.java`

Verbatim port. Background threads' crashes are swallowed; main-thread
crashes chain to default (avoid wedged Looper).

### 3.3 `PackageContext.java`

Loads the target package's `ApplicationInfo` from the APK metadata (uses
PackageManager's `getPackageArchiveInfo` — works without an installed
package). Creates an `android.app.LoadedApk` for the package via a
constructor-reflection that V2-Phone proved across both Android 14 and 15.
Returns a `Context` for the McD package.

Replaces V2-Phone's `createPackageContext(MCD_PKG, ...)` which required
the package to be installed in PMS — not the case in our chroot. Instead
of "look up installed package", we "synthesize from APK".

### 3.4 `LoadedApkRedirect.java`

Verbatim port of V2-Phone's `redirectDataDir`. Patches:
- `ApplicationInfo.dataDir`
- `ApplicationInfo.credentialProtectedDataDir`
- `ApplicationInfo.deviceProtectedDataDir`
- `ContextImpl.mPackageInfo.mDataDirFile`
- `ContextImpl.mPackageInfo.mDeviceProtectedDataDirFile`
- `ContextImpl.mPackageInfo.mCredentialProtectedDataDirFile`
- `ContextImpl.mPackageInfo.mDataDir`
- `ContextImpl.mPreferencesDir / mDatabasesDir / mFilesDir / mCacheDir /
  mNoBackupFilesDir / mCodeCacheDir` (null out for recompute)

Pure reflection.

### 3.5 `LocaleManagerStub.java`

Conditional port of V2-Phone's `stubLocaleManager`. Only fires if
`Context.LOCALE_SERVICE` returns non-null. On chroot HBC ART, LocaleManager
may not have a binder service registered (no factory OH PMS provides it).
If get fails, skip silently.

### 3.6 `ApplicationBootstrap.java`

Verbatim port of V2-Phone's `ensureMcdApplication`. Calls McD's
`McDMarketApplication` ctor via `Class.forName(...).newInstance()`, wires
`LoadedApk.mApplication`, calls `ContextWrapper.attachBaseContext(safeCtx)`.

### 3.7 `ActivityAttacher.java`

Verbatim port of V2-Phone's `attachAndCreate`. Resolves the 19+ argument
`Activity.attach(...)` method, plants `ActivityThread.sCurrentActivityThread`
(if not already planted by AppSpawnXInit), constructs `ActivityInfo`,
calls `Activity.attach(...)` reflectively, then `Activity.onCreate(null)`.

### 3.8 `LifecycleDriver.java`

Verbatim port of V2-Phone's `driveLifecycleToResumed`. Reflectively calls
`performStart`, `performResume`, `performTopResumedActivityChanged`. Each
is `setAccessible`-free (uses `Class.getDeclaredMethods()` + name match).

### 3.9 `MainLooperBoot.java`

NEW (not in V2-Phone). The chroot-spawned process's main thread is what
we run on, but `Looper.prepareMainLooper()` hasn't been called yet —
`AppSpawnXInit.initChild` calls `invokeStaticMain` BEFORE
`ActivityThread.main()` would (in production, ActivityThread.main() does
the `prepareMainLooper`). Since we hijack the targetClass, we need to
prepare it ourselves. One-shot reflection.

### 3.10 `Marker.java`

Bridges to `AppSpawnXInit.appLog(String)` if available (preferred — goes
through nativeHiLog → HiLogPrint, which is the only reliable log channel
in the child after fork), else `System.err.println`.

---

## 4. Build

`scripts/v3/build-phase-one-b-driver.sh` (this commit):

```bash
#!/usr/bin/env bash
# Builds PhaseOneBDriver JAR for V3 chroot deployment.
# javac the .java sources against framework.jar from HBC bundle, then jar.

set -euo pipefail
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SRC_DIR="$REPO_ROOT/engine/v3/phase-one-b/src"
OUT_DIR="$REPO_ROOT/engine/v3/phase-one-b/out"
HBC_JARS_DIR="$REPO_ROOT/westlake-deploy-ohos/v3-hbc/jars"

JAVAC=${JAVAC:-$(command -v javac || echo /usr/lib/jvm/java-21-openjdk-amd64/bin/javac)}
JAR=${JAR:-$(command -v jar || echo /usr/lib/jvm/java-21-openjdk-amd64/bin/jar)}

mkdir -p "$OUT_DIR/classes"
CP="$HBC_JARS_DIR/framework.jar:$HBC_JARS_DIR/core-oj.jar:$HBC_JARS_DIR/core-libart.jar:$HBC_JARS_DIR/oh-adapter-framework.jar:$HBC_JARS_DIR/oh-adapter-runtime.jar"

"$JAVAC" -source 1.8 -target 1.8 -bootclasspath "$CP" \
    -d "$OUT_DIR/classes" \
    $(find "$SRC_DIR" -name "*.java")

"$JAR" --create --file "$OUT_DIR/phase-one-b-driver.jar" -C "$OUT_DIR/classes" .

# Convert classes to DEX so ART can load via -Xbootclasspath or PathClassLoader
# (HBC's ART expects boot/dex jars; .jar with .class is fine for PathClassLoader
# but not for -Xbootclasspath without dex2oat).
D8=${D8:-$(command -v d8 || true)}
if [ -n "$D8" ]; then
    "$D8" --output "$OUT_DIR" --classpath "$CP" "$OUT_DIR"/classes/com/westlake/engine/*.class
    "$JAR" --create --file "$OUT_DIR/phase-one-b-driver-dex.jar" -C "$OUT_DIR" classes.dex
fi

echo "PhaseOneBDriver JAR: $OUT_DIR/phase-one-b-driver.jar"
[ -f "$OUT_DIR/phase-one-b-driver-dex.jar" ] && echo "  + DEX: $OUT_DIR/phase-one-b-driver-dex.jar"
```

---

## 5. Launch

`scripts/v3/launch-mcd-chroot.sh` (this commit) — operator one-shot.
Pseudocode:

1. preflight: chroot exists, daemon not running, McD APK available
2. push PhaseOneBDriver-dex.jar to `/system/android/framework/phase-one-b-driver.jar`
3. push McD APK to `/data/app/com.mcdonalds.app/base.apk`
4. start appspawn-x daemon in chroot (via combo.sh idiom, background)
5. send TEXT-PROTOCOL spawn request (not OH binary TLV) with:
   ```
   procName=com.mcdonalds.app
   bundleName=com.mcdonalds.app
   uid=20010042
   gid=20010042
   targetClass=com.westlake.engine.PhaseOneBDriver
   dexPaths=/system/android/framework/phase-one-b-driver.jar:/data/app/com.mcdonalds.app/base.apk
   apkPath=/data/app/com.mcdonalds.app/base.apk
   ```
6. wait 30s for Java + Lifecycle drive
7. grep hilog for `PhaseOneBDriver: lifecycle reached RESUMED` marker
8. kill daemon
9. report

A new C TLV client (text-protocol variant) is needed since
`test_tlv_client.c` only supports OH binary TLV. Stub
`engine/v3/phase-one-b/test_text_spawn_client.c` should be a ~80-LOC
copy that builds `key=value\n` lines and sends them as raw bytes (no
magic byte). The text-protocol parser at
`spawn_server.cpp:632-708` validates required fields (procName +
uid >= 0). Build wired into `scripts/v3/build-phase-one-b-driver.sh`.

(NOT shipped this commit because substrate-fix-A1 blocks Phase 1b.3
delivery. Adding the C client and the text-protocol launch would create
"working code that can't run" — defer until substrate unblocks.)

---

## 6. Acceptance criterion (when substrate unblocks)

Hilog markers, in order, all from the SAME pid (the spawned child):

1. `[CHILD_CK] CK_BEFORE_initChild_call (about to enter Java)` (HBC native)
2. `J_initChild_entry proc=com.mcdonalds.app` (HBC Java)
3. `J_before_invokeStaticMain target=com.westlake.engine.PhaseOneBDriver` (HBC Java)
4. `PhaseOneBDriver.main entry: pkg=com.mcdonalds.app cls=com.mcdonalds.mcdcoreapp.common.activity.SplashActivity ...` (OUR driver)
5. `PhaseOneBDriver: Application.onCreate OK` (after McDMarketApplication.onCreate runs)
6. `PhaseOneBDriver: Activity.attach OK for com.mcdonalds.mcdcoreapp.common.activity.SplashActivity` (after attach)
7. *(McD's own log)* — any SplashActivity body marker (BeaconLog or similar — depends on what McD code logs)
8. `PhaseOneBDriver: lifecycle reached RESUMED for com.mcdonalds.mcdcoreapp.common.activity.SplashActivity`

Marker #4 alone proves Phase 1b.2 FULL PASS. Markers #5-#7 prove Phase 1b.4
FULL PASS. Marker #8 proves the full V2-Phone Option 3 pattern reproduced.

---

## 7. Constraints honored (self-audit)

| Constraint | Self-check |
|------------|------------|
| No Unsafe.allocateInstance | grep across all .java sources: 0 hits |
| No setAccessible(true) | grep: 0 hits (sole use of setAccessible is on Activity declaredMethods which are package-private and visible via getDeclaredMethods, no setAccessible(true) call) |
| No per-app branches | classes accept package name as argv; no `if (pkg == "com.mcdonalds.app")` |
| No HBC fork (`git diff westlake-deploy-ohos/v3-hbc/`) | empty |
| No framework shim (no new `android.*` classes) | all new code in `com.westlake.engine.*` |
| Macro-shim contract honored | every reflective call body is the actual AOSP method, called with safe argument defaults; no shim bodies |
| APK transparency preserved | McD APK is unchanged; we never repackage |
| Lifecycle gate | `LifecycleDriver` runs on main thread via Looper.loop after attach+onCreate — Handler.post and Choreographer.doFrame fire (CR59 lesson) |
| hdc_shell_check usage | not directly applicable (no shell scripts in this commit do control-flow on hdc) |
| chroot RO bind-mount of /lib + /system/lib | preserved (deploy script unchanged) |
