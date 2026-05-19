/*
 * Marker — child-safe logging bridge for the V3 PhaseOneBDriver.
 *
 * Why not Log.i: the child's framework JNI may be unbound after fork (see
 * docs/engine/V3-W2-PHASE-1B-PROGRESS.md §1). Log.println_native throws
 * UnsatisfiedLinkError without liboh_android_runtime.so loaded. The only
 * reliable channel in the child is AppSpawnXInit.appLog (HBC native that
 * uses HiLogPrint directly via dlopen handle established in parent
 * pre-fork, inherited via fork's address-space COW).
 *
 * If AppSpawnXInit is unavailable for any reason, falls back to System.err
 * (which the child has redirected to /data/local/tmp/adapter_child_<pid>.stderr
 * by child_main.cpp's stderr redirect probe, line 64).
 */
package com.westlake.engine;

import java.lang.reflect.Method;

final class Marker {

    private static final boolean USE_APPLOG;
    private static final Method APPLOG_METHOD;
    static {
        Method m = null;
        boolean ok = false;
        try {
            Class<?> c = Class.forName("com.android.internal.os.AppSpawnXInit");
            // appLog signature: static void appLog(String)
            m = c.getDeclaredMethod("appLog", String.class);
            ok = true;
        } catch (Throwable t) {
            // AppSpawnXInit not available or method shape changed; fall back.
        }
        APPLOG_METHOD = m;
        USE_APPLOG = ok;
    }

    private Marker() { /* static-only */ }

    static void emit(String tag, String msg) {
        String line = "[" + tag + "] " + msg;
        if (USE_APPLOG && APPLOG_METHOD != null) {
            try {
                APPLOG_METHOD.invoke(null, line);
                return;
            } catch (Throwable t) {
                // Fall through to System.err
            }
        }
        try {
            System.err.println(line);
            System.err.flush();
        } catch (Throwable t) {
            // Last resort; nothing more we can do.
        }
    }
}
