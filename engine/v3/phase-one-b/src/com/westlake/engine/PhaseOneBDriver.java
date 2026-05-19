/*
 * Copyright (c) 2026 Westlake Engine.
 *
 * V3 PhaseOneBDriver — chroot-spawned McD/Noice mini-launcher.
 *
 * Architecture: docs/engine/V3-PHASE-ONE-B-DRIVER.md
 * Phase 1b spec: docs/engine/V3-W2-PHASE-1B-PROGRESS.md
 *
 * Entry point invoked by HBC's AppSpawnXInit.invokeStaticMain when the
 * appspawn-x parent forks a child for procName=com.<targetapp>.app with
 * the text-protocol spawn message setting targetClass=com.westlake.engine.PhaseOneBDriver.
 *
 * Argv (forwarded via spawn message args field; default empty if absent):
 *   [0] = "com.mcdonalds.app/com.mcdonalds.mcdcoreapp.common.activity.SplashActivity"
 *   [1] = "/data/app/com.mcdonalds.app/base.apk"
 *
 * Pillars ported from V2-Phone (READ-ONLY reference, NOT modified):
 *   westlake-host-gradle/app/src/main/java/com/westlake/host/McdInProcessActivity.kt
 *
 * Self-audit (from feedback_macro_shim_contract.md, see README §7):
 *   - No Unsafe.allocateInstance: confirmed zero references in this file.
 *   - No setAccessible(true): only safe-method getDeclaredMethods + filter.
 *   - No per-app branches: package/class accepted as argv.
 *   - No HBC fork: only consumes HBC framework via reflection.
 *
 * STATUS: stub. Build/test BLOCKED on substrate gap (liboh_android_runtime.so
 * not loaded in child → Log.i throws). See progress doc §3.
 *
 * When substrate unblocks, implement bodies of Pillars (sibling files in same
 * package), then this main() drives them in order.
 */
package com.westlake.engine;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class PhaseOneBDriver {

    private static final String TAG = "PhaseOneBDriver";

    // Default targets (overridable via argv).
    private static final String DEFAULT_TARGET_SPEC =
        "com.mcdonalds.app/com.mcdonalds.mcdcoreapp.common.activity.SplashActivity";
    private static final String DEFAULT_APK_PATH =
        "/data/app/com.mcdonalds.app/base.apk";

    private PhaseOneBDriver() { /* utility class — instances via main() only */ }

    public static void main(String[] args) {
        // Stage 0: emit entry marker via the only known-working log channel in
        // child after fork — AppSpawnXInit.appLog → nativeHiLog → HiLogPrint.
        // Cannot call android.util.Log.* yet; framework JNI may not be bound
        // (see progress doc §1 "Phase 1b.2 PARTIAL" — Log.println_native
        // UnsatisfiedLinkError without liboh_android_runtime.so loaded).
        Marker.emit(TAG, "main entry, args.length=" + args.length);

        String spec  = args.length > 0 ? args[0] : DEFAULT_TARGET_SPEC;
        String apk   = args.length > 1 ? args[1] : DEFAULT_APK_PATH;
        String[] sp  = spec.split("/", 2);
        String pkg   = sp[0];
        String cls   = sp.length > 1
            ? sp[1]
            : "com.mcdonalds.mcdcoreapp.common.activity.SplashActivity";

        Marker.emit(TAG, "pkg=" + pkg + " cls=" + cls + " apk=" + apk);

        try {
            // Pillar 1: bypass hidden API blocklist so subsequent reflection on
            // ActivityThread / LoadedApk / ContextImpl private fields works.
            // Verbatim port of V2's bypassHiddenApiRestrictions.
            BypassHiddenApi.exemptAll();
            Marker.emit(TAG, "BypassHiddenApi OK");

            // Pillar 2: swallow uncaught background exceptions so coroutine
            // crashes don't kill the foreground driver. Main-thread crashes
            // chain to prev. Verbatim from V2.
            SwallowingUncaughtHandler.install();
            Marker.emit(TAG, "SwallowingUncaughtHandler installed");

            // Pillar 3: prepare main Looper. Unlike V2 (which inherited Looper
            // from being an Activity), we are the targetClass invoked by
            // appspawn-x — no Looper has been prepared. Must do it before
            // any Handler.post-using framework code (Hilt, Compose, etc.).
            MainLooperBoot.prepareMainLooperIfNeeded();
            Marker.emit(TAG, "MainLooperBoot OK");

            // Pillar 4: synthesize a package Context for the target APK.
            // V2 used createPackageContext(pkg, INCLUDE_CODE | IGNORE_SECURITY)
            // which requires PMS-installed package. Our chroot has no PMS,
            // so we synthesize via getPackageArchiveInfo + LoadedApk ctor.
            android.content.Context pkgCtx = PackageContext.synthesize(pkg, apk);
            Marker.emit(TAG, "PackageContext synthesized");

            // Pillar 5: redirect target's dataDir to a host-writable path so
            // SharedPreferences / DB writes succeed. Verbatim from V2.
            LoadedApkRedirect.patchDataDirs(pkgCtx, "/data/local/tmp/v3-app-data/" + pkg);
            Marker.emit(TAG, "LoadedApkRedirect OK");

            // Pillar 6: hook LocaleManager binder (if present) so
            // getApplicationLocales returns empty instead of SecurityException.
            // Conditional — OH chroot may not have LocaleManager service.
            LocaleManagerStub.installIfPresent(pkgCtx);
            Marker.emit(TAG, "LocaleManagerStub OK (or skipped)");

            // Pillar 7: instantiate the target's Application class, wire
            // LoadedApk.mApplication, call Application.onCreate. Verbatim V2.
            android.app.Application app =
                ApplicationBootstrap.instantiateAndAttach(pkgCtx);
            Marker.emit(TAG, "Application.onCreate OK");

            // Pillar 8: instantiate target Activity, plant
            // ActivityThread.sCurrentActivityThread, call Activity.attach
            // (19+ arg reflection) → Activity.onCreate(null). Verbatim V2.
            android.app.Activity activity =
                ActivityAttacher.attach(pkgCtx, app, cls);
            Marker.emit(TAG, "Activity.attach + onCreate OK for " + cls);

            // Pillar 9: drive lifecycle to RESUMED so Fragment / Compose / VM
            // wiring fires (without this the view tree stays inert; Compose
            // emits nothing). Verbatim V2.
            LifecycleDriver.driveToResumed(activity);
            Marker.emit(TAG, "Lifecycle reached RESUMED for " + cls);

            // We have no display, so Looper.loop() runs and frame work no-ops.
            // For Phase 1b that's enough — marker proves call chain executed.
            // For pixels (later W6 work) we'd attach a hwui virtual display
            // OR an Island-style framebuffer (~/Borrows W6-Renderer #1).
            Marker.emit(TAG, "entering Looper.loop()");
            android.os.Looper.loop();

        } catch (Throwable t) {
            Marker.emit(TAG, "FATAL " + t.getClass().getName() + " — " + t.getMessage());
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            for (String line : sw.toString().split("\n")) {
                Marker.emit(TAG, "  " + line);
            }
            // Don't System.exit — the spawned process should exit naturally so
            // appspawn-x's SIGCHLD handler reaps it cleanly.
        }
    }
}
