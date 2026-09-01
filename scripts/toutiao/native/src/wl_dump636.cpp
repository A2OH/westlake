// WESTLAKE §651 (2026-08-15) — ART managed-thread dump from an LD_PRELOADed library.
//
// Why here and not in the bridge: the §624 DUMPNOW hook exists only in bridge SOURCE; the deployed
// liboh_adapter_bridge.so has zero hits for it, and the bridge cannot currently be rebuilt (11 of
// its 97 TUs fail to compile and it links anyway, producing a binary that loses core natives).
// This library is already LD_PRELOADed into the zygote and inherited by forked children, so it is
// the cheapest place to put runtime instrumentation.
//
// ⚠️MUST be compiled with the OHOS prebuilt clang + libcxx-ohos, NOT the NDK clang: libart's
// DumpForSigQuit takes a `std::__h::ostream&`, and the NDK toolchain emits `std::__n1`, so the
// symbol simply would not resolve (same trap as §649's appspawn-x link failure).
//
// ⚠️`kill -3` is forbidden on this port: a SIGQUIT kills the child with ZERO dump bytes.
// Trigger instead with:  touch /data/local/tmp/asx/DUMPNOW
#include <dlfcn.h>
#include <fcntl.h>
#include <jni.h>
#include <pthread.h>
#include <stdio.h>
#include <string.h>
#include <sys/stat.h>
#include <unistd.h>
#include <sstream>
#include <string>

namespace {

// §651c: PER-PID trigger, in the world-writable dir. Two things were wrong with a single shared
// trigger under /data/local/tmp/asx: (1) the zygote's watcher consumed it first and dumped the
// ZYGOTE (where AttachCurrentThreadAsDaemon fails — it is -Xzygote), and (2) the forked child runs
// as an app uid and cannot unlink from that root-owned directory anyway.
//   touch /data/service/el1/public/appspawnx/DUMPNOW.<child pid>
char g_trigger[128];
char g_outpath[128];
void wl_paths_init() {
    snprintf(g_trigger, sizeof(g_trigger),
             "/data/service/el1/public/appspawnx/DUMPNOW.%d", (int)getpid());
    snprintf(g_outpath, sizeof(g_outpath),
             "/data/service/el1/public/appspawnx/threaddump_%d.txt", (int)getpid());
}

// §651b: ALWAYS mirror to fd 2. The forked child drops to an app uid after setcon, so it may not
// be able to create files under /data/local/tmp/asx — the first attempt produced no file at all,
// not even the breadcrumb. Its stderr is already open and lands in adapter_child_<pid>.stderr.
void wl_write_all(const char* path, const char* data, size_t len) {
    { const char* p = data; size_t l = len; while (l > 0) { ssize_t n = ::write(2, p, l);
        if (n <= 0) break; p += n; l -= (size_t)n; } }
    int fd = ::open(path, O_WRONLY | O_CREAT | O_TRUNC, 0666);
    if (fd < 0) return;
    while (len > 0) {
        ssize_t n = ::write(fd, data, len);
        if (n <= 0) break;
        data += n; len -= (size_t)n;
    }
    ::close(fd);
}

void wl_do_dump() {
    // Leave a breadcrumb BEFORE dumping. If the dump never returns — which is what a
    // suspend/checkpoint stall looks like — the presence of this file with no body is itself the
    // finding, and distinguishes "hung" from "hook never ran".
    wl_write_all(g_outpath, "[WESTLAKE-651] dump STARTED\n", 28);

    // Attach so ART has a Thread* for us; a dump from an unattached thread is not safe.
    JavaVM* vm = nullptr; jsize n = 0;
    auto getvms = (jint(*)(JavaVM**, jsize, jsize*))dlsym(RTLD_DEFAULT, "JNI_GetCreatedJavaVMs");
    if (getvms == nullptr || getvms(&vm, 1, &n) != JNI_OK || n < 1 || vm == nullptr) {
        wl_write_all(g_outpath, "[WESTLAKE-651] no JavaVM\n", 25);
        return;
    }
    JNIEnv* env = nullptr;
    if (vm->AttachCurrentThreadAsDaemon(&env, nullptr) != JNI_OK) {
        wl_write_all(g_outpath, "[WESTLAKE-651] attach failed\n", 29);
        return;
    }

    void* cur = dlsym(RTLD_DEFAULT, "_ZN3art7Runtime7CurrentEv");
    void* dmp = dlsym(RTLD_DEFAULT,
        "_ZN3art7Runtime14DumpForSigQuitERNSt3__h13basic_ostreamIcNS1_11char_traitsIcEEEE");
    if (cur == nullptr || dmp == nullptr) {
        wl_write_all(g_outpath, "[WESTLAKE-651] symbols missing\n", 31);
        return;
    }
    void* runtime = ((void*(*)())cur)();
    if (runtime == nullptr) {
        wl_write_all(g_outpath, "[WESTLAKE-651] Runtime::Current()==null\n", 40);
        return;
    }
    std::ostringstream oss;
    ((void(*)(void*, std::ostream&))dmp)(runtime, oss);
    const std::string s = oss.str();
    wl_write_all(g_outpath, s.data(), s.size());
}

// WESTLAKE §654 (2026-08-15): start the Activity LATER, from here, without rebuilding any jar.
// §653 proved the defect is ordering — binding the Application and immediately starting
// MainActivity makes Toutiao throw "请先初始化调度器" (its lego InitTaskDispatcher is still starting),
// which AppSpawnXInit turns into System.exit(1) and thence a process-wide deadlock.
// The proper fix is a settle window inside AppSchedulerBridge.directLaunchNoBms, but that class
// lives in adapter-runtime-bcp.jar and its build chain needs an AOSP tree this machine does not
// have. AppSchedulerBridge.nativeOnScheduleLaunchAbility is `public static`, so we can simply call
// it over JNI once the app has settled:
//     echo "<pkg> <activityFqn>" > /data/service/el1/public/appspawnx/LAUNCH.<child pid>
void wl_do_launch(const char* pkg, const char* activity) {
    dprintf(2, "[WESTLAKE-654] launching %s / %s\n", pkg, activity);
    JavaVM* vm = nullptr; jsize n = 0;
    auto getvms = (jint(*)(JavaVM**, jsize, jsize*))dlsym(RTLD_DEFAULT, "JNI_GetCreatedJavaVMs");
    if (getvms == nullptr || getvms(&vm, 1, &n) != JNI_OK || n < 1 || vm == nullptr) {
        dprintf(2, "[WESTLAKE-654] no JavaVM\n"); return;
    }
    JNIEnv* env = nullptr;
    if (vm->AttachCurrentThreadAsDaemon(&env, nullptr) != JNI_OK) {
        dprintf(2, "[WESTLAKE-654] attach failed\n"); return;
    }
    jclass cls = env->FindClass("adapter/activity/AppSchedulerBridge");
    if (cls == nullptr) { env->ExceptionClear(); dprintf(2, "[WESTLAKE-654] class not found\n"); return; }
    jmethodID mid = env->GetStaticMethodID(cls, "nativeOnScheduleLaunchAbility",
        "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;J)V");
    if (mid == nullptr) { env->ExceptionClear(); dprintf(2, "[WESTLAKE-654] method not found\n"); return; }
    jstring jpkg = env->NewStringUTF(pkg);
    jstring jact = env->NewStringUTF(activity);
    // §654b: use CallStaticVoidMethod**A** with an explicit jvalue[] rather than the varargs form.
    // The varargs call delivered `ability=null` to Java even though the C side printed the right
    // string — a bare `nullptr` (and the mixed jint/jlong) going through C varargs does not
    // reliably promote to the widths JNI expects on aarch64, and the arguments shifted.
    // The array form has no promotion rules at all.
    jvalue args[7];
    args[0].l = nullptr;      // appThread
    args[1].l = jpkg;         // bundleName
    args[2].l = jact;         // abilityName
    args[3].i = 1;            // abilityRecordId
    args[4].l = nullptr;      // abilityJson
    args[5].l = nullptr;      // wantJson
    args[6].j = 0;            // ohTokenAddr
    env->CallStaticVoidMethodA(cls, mid, args);
    if (env->ExceptionCheck()) {
        dprintf(2, "[WESTLAKE-654] launch threw\n");
        env->ExceptionDescribe();
        env->ExceptionClear();
    } else {
        dprintf(2, "[WESTLAKE-654] launch call returned\n");
    }
    env->DeleteLocalRef(jpkg); env->DeleteLocalRef(jact);
}

// WESTLAKE §655 (2026-08-15): call Application.onCreate() — our bind path never does.
// Measured in the child log: ensureBindApplication=4, makeApplication=5, attachBaseContext=8,
// but **callApplicationOnCreate=0**. Android's real sequence is
//   LoadedApk.makeApplication() -> attachBaseContext -> installContentProviders ->
//   Instrumentation.callApplicationOnCreate(app) -> app.onCreate()
// and we stop after the first step. ByteDance initialises its lego InitTaskDispatcher in
// onCreate, which is exactly why the scheduler is never set up and MainActivity dies with
// "请先初始化调度器" — and why a 45 s settle (§654) changed nothing.
// Application.onCreate() is public, so it can be driven from here while the jar stays unbuildable:
//   touch /data/service/el1/public/appspawnx/ONCREATE.<child pid>
// §655b: printStackTrace()/ExceptionDescribe() are noop'd on this port ("[RT] Throwable
// .printStackTrace (fork-safe noop)"), so an exception has to be interrogated by hand.
void wl_report_exception(JNIEnv* env, const char* who) {
    jthrowable t = env->ExceptionOccurred();
    if (t == nullptr) return;
    env->ExceptionClear();
    jclass thr = env->FindClass("java/lang/Throwable");
    jmethodID mMsg = env->GetMethodID(thr, "getMessage", "()Ljava/lang/String;");
    jmethodID mCause = env->GetMethodID(thr, "getCause", "()Ljava/lang/Throwable;");
    jclass clsCls = env->FindClass("java/lang/Class");
    jmethodID mName = env->GetMethodID(clsCls, "getName", "()Ljava/lang/String;");
    jthrowable cur = t;
    for (int depth = 0; cur != nullptr && depth < 6; ++depth) {
        jclass c = env->GetObjectClass(cur);
        jstring jn = (jstring)env->CallObjectMethod(c, mName);
        jstring jm = (jstring)env->CallObjectMethod(cur, mMsg);
        const char* n = jn ? env->GetStringUTFChars(jn, nullptr) : nullptr;
        const char* m = jm ? env->GetStringUTFChars(jm, nullptr) : nullptr;
        dprintf(2, "[WESTLAKE-655] %s: %s%s: %s\n", who, depth ? "caused by " : "",
                n ? n : "<class?>", m ? m : "<no message>");
        if (n) env->ReleaseStringUTFChars(jn, n);
        if (m) env->ReleaseStringUTFChars(jm, m);
        // §655c: walk getStackTrace() — plain Java, so it works where printStackTrace() is
        // noop'd (the §210 lesson: "getStackTrace() has neither problem").
        jmethodID mSt = env->GetMethodID(thr, "getStackTrace", "()[Ljava/lang/StackTraceElement;");
        jobjectArray st = (jobjectArray)env->CallObjectMethod(cur, mSt);
        if (!env->ExceptionCheck() && st != nullptr) {
            jsize fn = env->GetArrayLength(st);
            jclass steCls = env->FindClass("java/lang/StackTraceElement");
            jmethodID mTs = env->GetMethodID(steCls, "toString", "()Ljava/lang/String;");
            for (jsize i = 0; i < fn && i < 14; ++i) {
                jobject fe = env->GetObjectArrayElement(st, i);
                jstring fs = (jstring)env->CallObjectMethod(fe, mTs);
                const char* f = fs ? env->GetStringUTFChars(fs, nullptr) : nullptr;
                dprintf(2, "[WESTLAKE-655]     at %s\n", f ? f : "?");
                if (f) env->ReleaseStringUTFChars(fs, f);
                env->DeleteLocalRef(fe);
            }
        }
        if (env->ExceptionCheck()) env->ExceptionClear();
        cur = (jthrowable)env->CallObjectMethod(cur, mCause);
        if (env->ExceptionCheck()) { env->ExceptionClear(); break; }
    }
}

void wl_do_oncreate() {
    JavaVM* vm = nullptr; jsize n = 0;
    auto getvms = (jint(*)(JavaVM**, jsize, jsize*))dlsym(RTLD_DEFAULT, "JNI_GetCreatedJavaVMs");
    if (getvms == nullptr || getvms(&vm, 1, &n) != JNI_OK || n < 1 || vm == nullptr) {
        dprintf(2, "[WESTLAKE-655] no JavaVM\n"); return;
    }
    JNIEnv* env = nullptr;
    if (vm->AttachCurrentThreadAsDaemon(&env, nullptr) != JNI_OK) {
        dprintf(2, "[WESTLAKE-655] attach failed\n"); return;
    }
    jclass at = env->FindClass("android/app/ActivityThread");
    if (at == nullptr) { env->ExceptionClear(); dprintf(2, "[WESTLAKE-655] no ActivityThread\n"); return; }
    jmethodID cur = env->GetStaticMethodID(at, "currentApplication", "()Landroid/app/Application;");
    if (cur == nullptr) { env->ExceptionClear(); dprintf(2, "[WESTLAKE-655] no currentApplication\n"); return; }
    jobject app = env->CallStaticObjectMethod(at, cur);
    if (env->ExceptionCheck()) { env->ExceptionDescribe(); env->ExceptionClear(); return; }
    if (app == nullptr) { dprintf(2, "[WESTLAKE-655] currentApplication()==null (bind incomplete)\n"); return; }
    jclass appCls = env->GetObjectClass(app);
    jmethodID oc = env->GetMethodID(appCls, "onCreate", "()V");
    if (oc == nullptr) { env->ExceptionClear(); dprintf(2, "[WESTLAKE-655] no onCreate\n"); return; }
    dprintf(2, "[WESTLAKE-655] calling Application.onCreate()\n");
    env->CallVoidMethod(app, oc);
    if (env->ExceptionCheck()) {
        dprintf(2, "[WESTLAKE-655] onCreate THREW\n");
        wl_report_exception(env, "onCreate");
    } else {
        dprintf(2, "[WESTLAKE-655] onCreate returned OK\n");
    }
}

// WESTLAKE §671 (2026-08-16): drive the Activity to RESUME.
// `AppSchedulerBridge.directLaunchNoBms` does THREE steps: ensureBindApplication ->
// nativeOnScheduleLaunchAbility -> **directResume(recordId)**. §654's LAUNCH trigger only did the
// middle one, so MainActivity was created but never resumed — and a window (ViewRootImpl) is only
// attached at resume. Measured against a known-good noice launch:
//     noice:   ViewRootImpl=951  Surface=1116  side-channels=1   (DIRECT-LAUNCH=6, incl. directResume)
//     toutiao: ViewRootImpl=0    Surface=16    side-channels=0
// while BOTH reach `[B47-SLA] BEFORE/AFTER scheduleTransaction`, so the transaction was fine.
// ⚠️`directResume` is PRIVATE static — JNI's GetStaticMethodID ignores access modifiers, so no
// reflection/setAccessible dance is needed.
//     touch /data/service/el1/public/appspawnx/RESUME.<child pid>
void wl_do_resume() {
    JavaVM* vm = nullptr; jsize n = 0;
    auto getvms = (jint(*)(JavaVM**, jsize, jsize*))dlsym(RTLD_DEFAULT, "JNI_GetCreatedJavaVMs");
    if (getvms == nullptr || getvms(&vm, 1, &n) != JNI_OK || n < 1 || vm == nullptr) {
        dprintf(2, "[WESTLAKE-671] no JavaVM\n"); return;
    }
    JNIEnv* env = nullptr;
    if (vm->AttachCurrentThreadAsDaemon(&env, nullptr) != JNI_OK) {
        dprintf(2, "[WESTLAKE-671] attach failed\n"); return;
    }
    jclass cls = env->FindClass("adapter/activity/AppSchedulerBridge");
    if (cls == nullptr) { env->ExceptionClear(); dprintf(2, "[WESTLAKE-671] no class\n"); return; }
    jmethodID mid = env->GetStaticMethodID(cls, "directResume", "(I)V");
    if (mid == nullptr) { env->ExceptionClear(); dprintf(2, "[WESTLAKE-671] no directResume\n"); return; }
    dprintf(2, "[WESTLAKE-671] calling directResume(1)\n");
    jvalue a[1]; a[0].i = 1;
    env->CallStaticVoidMethodA(cls, mid, a);
    if (env->ExceptionCheck()) {
        dprintf(2, "[WESTLAKE-671] directResume THREW\n");
        wl_report_exception(env, "directResume");
    } else {
        dprintf(2, "[WESTLAKE-671] directResume returned OK\n");
    }
}

void* wl_watch(void*) {
    for (;;) {
        struct stat st;
        if (::stat(g_trigger, &st) == 0) {
            ::unlink(g_trigger);
            wl_do_dump();
        }
        char rt[160];
        snprintf(rt, sizeof(rt), "/data/service/el1/public/appspawnx/RESUME.%d", (int)getpid());
        if (::stat(rt, &st) == 0) { ::unlink(rt); wl_do_resume(); }
        char ot[160];
        snprintf(ot, sizeof(ot), "/data/service/el1/public/appspawnx/ONCREATE.%d", (int)getpid());
        if (::stat(ot, &st) == 0) { ::unlink(ot); wl_do_oncreate(); }
        char lt[160];
        snprintf(lt, sizeof(lt), "/data/service/el1/public/appspawnx/LAUNCH.%d", (int)getpid());
        if (::stat(lt, &st) == 0) {
            char buf[512] = {0};
            int fd = ::open(lt, O_RDONLY);
            if (fd >= 0) { ssize_t r = ::read(fd, buf, sizeof(buf)-1); (void)r; ::close(fd); }
            ::unlink(lt);
            char* sp = strchr(buf, ' ');
            if (sp != nullptr) {
                *sp = '\0';
                char* act = sp + 1;
                char* nl = strpbrk(act, " \r\n");
                if (nl != nullptr) *nl = '\0';
                wl_do_launch(buf, act);
            } else {
                dprintf(2, "[WESTLAKE-654] bad trigger contents (need '<pkg> <activity>')\n");
            }
        }
        ::usleep(500 * 1000);
    }
    return nullptr;
}

// §651b: the watcher thread does NOT survive fork(), so a constructor-started thread only ever
// exists in the ZYGOTE — the app child has none. Every exported native in this library calls
// wl_ensure_watcher(), which starts it lazily in whatever process actually runs app code. It must
// not start earlier than that: appspawn-x children must stay single-threaded until their SELinux
// setcon completes, or setcon fails with EPERM.
static pthread_once_t wl_once = PTHREAD_ONCE_INIT;
static void wl_start_watcher() {
    wl_paths_init();
    dprintf(2, "[WESTLAKE-651] trigger=%s\n", g_trigger);
    pthread_t t;
    if (pthread_create(&t, nullptr, wl_watch, nullptr) == 0) {
        pthread_detach(t);
        dprintf(2, "[WESTLAKE-651] watcher started in pid=%d\n", (int)getpid());
    }
}

// §651c: NO constructor-started watcher. A thread created here lives only in the zygote (threads
// do not survive fork), and it would consume the trigger before the child ever saw it.
// wl_ensure_watcher() is called from every exported native instead — i.e. in the process that is
// actually running app code, and only after its SELinux setcon has completed.

}  // namespace

extern "C" void wl_ensure_watcher() { pthread_once(&wl_once, wl_start_watcher); }
