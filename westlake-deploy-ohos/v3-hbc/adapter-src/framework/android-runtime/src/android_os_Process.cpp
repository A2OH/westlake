// ============================================================================
// android_os_Process.cpp
//
// JNI bindings for android.os.Process. Mirrors AOSP
// frameworks/base/core/jni/android_util_Process.cpp, minimal subset Hello
// World startup actually invokes.
//
// Current set:
//   Process.setArgV0Native(String)   — uses prctl(PR_SET_NAME) for
//                                       visible process name in ps / top.
//
// AOSP's real setArgV0 also overwrites argv[0] by reallocating the main
// thread's stack base. We omit that (Hello World doesn't need it; ps
// shows PR_SET_NAME value which is what matters for debugging). Add
// argv[0] rewrite only if a concrete caller needs it.
//
// Additional Process natives (setUid, setGid, getPids, killProcess, etc.)
// are not registered here yet — add incrementally as new UnsatisfiedLinkErrors
// surface during child launch.
// ============================================================================

#include "AndroidRuntime.h"

#include <jni.h>
#include <string.h>
#include <sys/prctl.h>
#include <sys/resource.h>
#include <sys/syscall.h>
#include <sys/types.h>
#include <signal.h>
#include <unistd.h>
#include <errno.h>
#include <pwd.h>   // getpwnam (getUidForName)
#include <grp.h>   // getgrnam (getGidForName)
#include <time.h>  // clock_gettime (Process timing natives)

namespace android {

namespace {

void JNICALL
Process_setArgV0Native(JNIEnv* env, jclass /*clazz*/, jstring name) {
    if (name == nullptr) {
        return;
    }
    const char* utf = env->GetStringUTFChars(name, nullptr);
    if (!utf) {
        return;
    }
    // PR_SET_NAME takes at most 15 bytes + NUL. ps/top show this value as
    // comm. AOSP's String8/argv0 patching for /proc/self/cmdline is skipped
    // for adapter — not on the Hello World critical path.
    char buf[16] = {0};
    strncpy(buf, utf, sizeof(buf) - 1);
    prctl(PR_SET_NAME, reinterpret_cast<unsigned long>(buf), 0, 0, 0);
    env->ReleaseStringUTFChars(name, utf);
}

// B.37 (2026-04-29): sendSignal JNI binding.  When OH AMS times out the app
// and asks ActivityThread.H to handle EXIT_APPLICATION, AOSP calls
// Process.killProcess(pid) → Process.sendSignal(pid, SIGNAL_KILL).  Without
// it registered, child throws UnsatisfiedLinkError and the timeout cascade
// is opaque.  Real impl uses kill(2) which Linux musl exposes directly;
// matches AOSP semantics of frameworks/base/core/jni/android_util_Process.cpp.
//
// IMPORTANT: only register methods we KNOW exist on android.os.Process for
// our boot-image-matching framework.jar.  RegisterNatives fails with -1
// (whole batch) if any one method-name lookup misses.  Don't speculate —
// add new entries only after the corresponding UnsatisfiedLinkError surfaces.
void JNICALL
Process_sendSignal(JNIEnv* /*env*/, jclass /*clazz*/, jint pid, jint sig) {
    if (pid > 0) {
        kill(static_cast<pid_t>(pid), sig);
    }
}

// 2026-05-11 G2.14at — single-arg setThreadPriority(int).  AOSP impl:
// setpriority(PRIO_PROCESS, gettid(), priority).  Used by HandlerThread.run()
// on activity teardown; missing this caused helloworld FATAL UnsatisfiedLinkError
// → System.exit on onDestroy.
void JNICALL
Process_setThreadPriority1(JNIEnv* env, jclass /*clazz*/, jint priority) {
    pid_t tid = static_cast<pid_t>(syscall(SYS_gettid));
    if (setpriority(PRIO_PROCESS, tid, priority) != 0) {
        // ignore errors — adapter doesn't enforce thread-priority semantics
        // beyond best-effort; failure here is non-fatal for our purposes.
    }
}

// Two-arg setThreadPriority(int tid, int priority).
void JNICALL
Process_setThreadPriority2(JNIEnv* env, jclass /*clazz*/, jint tid, jint priority) {
    if (tid <= 0) return;
    setpriority(PRIO_PROCESS, static_cast<id_t>(tid), priority);
}

// 2026-05-30 (C/Netflix) — getThreadPriority(int tid) -> int.  AOSP impl:
// getpriority(PRIO_PROCESS, tid), throwing IllegalArgumentException on error.
// Netflix's init reads its own thread priority and threw UnsatisfiedLinkError
// without this.  getpriority returns the nice value (-20..19) and may legitimately
// return -1, so errno distinguishes a real error; on error we return 0
// (THREAD_PRIORITY_DEFAULT), best-effort, rather than throw — non-fatal for adapter.
jint JNICALL
Process_getThreadPriority(JNIEnv* /*env*/, jclass /*clazz*/, jint tid) {
    errno = 0;
    int pri = getpriority(PRIO_PROCESS, static_cast<id_t>(tid));
    if (pri == -1 && errno != 0) {
        return 0;
    }
    return pri;
}

// 2026-05-30 (C/Netflix) — getUidForName(String)/getGidForName(String) -> int.
// Ancient AOSP Process natives (frameworks/base/core/jni/android_util_Process.cpp).
// AOSP also parses Android synthetic names (u0_aNN / app_NN / "system" ...); here we
// do the /etc-backed lookup (getpwnam/getgrnam) and return -1 ("no such name") otherwise,
// which is AOSP's documented contract for unknown names — callers handle -1.
jint JNICALL
Process_getUidForName(JNIEnv* env, jclass /*clazz*/, jstring name) {
    if (name == nullptr) return -1;
    const char* n = env->GetStringUTFChars(name, nullptr);
    if (!n) return -1;
    jint r = -1;
    struct passwd* pw = getpwnam(n);
    if (pw) r = static_cast<jint>(pw->pw_uid);
    env->ReleaseStringUTFChars(name, n);
    return r;
}
jint JNICALL
Process_getGidForName(JNIEnv* env, jclass /*clazz*/, jstring name) {
    if (name == nullptr) return -1;
    const char* n = env->GetStringUTFChars(name, nullptr);
    if (!n) return -1;
    jint r = -1;
    struct group* gr = getgrnam(n);
    if (gr) r = static_cast<jint>(gr->gr_gid);
    env->ReleaseStringUTFChars(name, n);
    return r;
}

// 2026-05-30 (C/Netflix) — getElapsedCpuTime() -> long (real process CPU time, ms).
// Confirmed native by Netflix's own UnsatisfiedLinkError. NOTE: the process-start-time
// getters (getStartUptimeMillis / getStartElapsedRealtime / ...Requested...) were tried as a
// batch and ABORTED register_android_os_Process (RegisterNatives -1 -> startReg OrDie -> no
// appspawn-x): at least one is NOT a native on this framework.jar's Process (Java-implemented),
// and RegisterNatives fails the WHOLE batch on a single non-native/sig miss. Add such methods
// ONLY after their own UnsatisfiedLinkError surfaces (file rule). Keeping only getElapsedCpuTime.
static jlong w14_clock_ms(clockid_t c) {
    struct timespec ts;
    if (clock_gettime(c, &ts) != 0) return 0;
    return static_cast<jlong>(ts.tv_sec) * 1000 + ts.tv_nsec / 1000000;
}
jlong JNICALL Process_getElapsedCpuTime(JNIEnv*, jclass) { return w14_clock_ms(CLOCK_PROCESS_CPUTIME_ID); }

const JNINativeMethod kProcessMethods[] = {
    { "setArgV0Native",
      "(Ljava/lang/String;)V",
      reinterpret_cast<void*>(Process_setArgV0Native) },
    { "sendSignal",
      "(II)V",
      reinterpret_cast<void*>(Process_sendSignal) },
    { "setThreadPriority",
      "(I)V",
      reinterpret_cast<void*>(Process_setThreadPriority1) },
    { "setThreadPriority",
      "(II)V",
      reinterpret_cast<void*>(Process_setThreadPriority2) },
    { "getThreadPriority",
      "(I)I",
      reinterpret_cast<void*>(Process_getThreadPriority) },
    { "getUidForName",
      "(Ljava/lang/String;)I",
      reinterpret_cast<void*>(Process_getUidForName) },
    { "getGidForName",
      "(Ljava/lang/String;)I",
      reinterpret_cast<void*>(Process_getGidForName) },
    { "getElapsedCpuTime", "()J",
      reinterpret_cast<void*>(Process_getElapsedCpuTime) },
};

}  // namespace

int register_android_os_Process(JNIEnv* env) {
    jclass clazz = env->FindClass("android/os/Process");
    if (!clazz) {
        return -1;
    }
    jint rc = env->RegisterNatives(clazz, kProcessMethods,
                                    sizeof(kProcessMethods) / sizeof(kProcessMethods[0]));
    env->DeleteLocalRef(clazz);
    return rc == JNI_OK ? 0 : -1;
}

}  // namespace android
