// WESTLAKE §636 (2026-08-15) — three Class-1 natives Toutiao reaches that were never bound.
//
// Shipped as a STANDALONE .so rather than folded into liboh_adapter_bridge.so, because the
// bridge source tree currently fails to compile 11 of its 97 TUs (PointerCoords::clear()
// redefinition, missing sqlite3.h, ...), so rebuilding the bridge produces a 2.4 MB binary
// instead of the working 3.9 MB one and loses core natives. Same westlake boundary either way:
// ART resolves natives with dlsym(RTLD_DEFAULT, "Java_..."), so ANY library in the global scope
// that exports the symbol wins — no RegisterNatives, no timing dependency.
// Load via LD_PRELOAD (see run_tt.sh); children of appspawn-x inherit it.
//
// ★The symptom of an unbound native on this port is NOT UnsatisfiedLinkError: a reflective call
// goes Method.invoke -> ArtMethod::Invoke -> EnterInterpreterFromInvoke and enters
// ExecuteSwitchImplCpp with a NULL instruction pointer, faulting at `ldrh w25,[x28]` addr=0.
// So "SIGSEGV addr=0 in the switch interpreter" reads as "an unbound native was invoked";
// `grep -a JNIMISS` then names it.
#include <jni.h>
#include <unistd.h>
#include <sys/inotify.h>
#include <errno.h>
#include <dlfcn.h>
#include <pthread.h>
#include <time.h>
#include <cstdint>
#include <cstdio>
#include <cstring>

// Diagnostic: the Android "main" thread is observed in nanosleep while its MessageQueue backs
// up. Name the native caller; bounded and main-thread-only so background timer noise stays out.
extern "C" int nanosleep(const struct timespec* req, struct timespec* rem) {
    using Fn = int (*)(const struct timespec*, struct timespec*);
    static Fn real = nullptr;
    static int logs = 0;
    static thread_local bool resolving = false;
    if (real == nullptr && !resolving) {
        resolving = true;
        real = reinterpret_cast<Fn>(dlsym(RTLD_NEXT, "nanosleep"));
        resolving = false;
    }
    char name[16] = {};
    pthread_getname_np(pthread_self(), name, sizeof(name));
    if (logs < 100 && strcmp(name, "main") == 0) {
        ++logs;
        void* caller = __builtin_return_address(0);
        Dl_info info = {};
        dladdr(caller, &info);
        fprintf(stderr, "[WESTLAKE-NATIVE-SLEEP] sec=%lld nsec=%ld caller=%p %s!%s+0x%llx\n",
                req ? static_cast<long long>(req->tv_sec) : -1LL,
                req ? req->tv_nsec : -1L, caller,
                info.dli_fname ? info.dli_fname : "?",
                info.dli_sname ? info.dli_sname : "?",
                info.dli_saddr ? static_cast<unsigned long long>(
                    reinterpret_cast<uintptr_t>(caller) - reinterpret_cast<uintptr_t>(info.dli_saddr)) : 0ULL);
        fflush(stderr);
    }
    if (real != nullptr) return real(req, rem);
    errno = ENOSYS;
    return -1;
}

extern "C" void wl_ensure_watcher();   // §651b: starts the DUMPNOW watcher in THIS process

static int wl_fd_of(JNIEnv* env, jobject fdObj) {
    if (env == nullptr || fdObj == nullptr) return -1;
    jclass c = env->GetObjectClass(fdObj);
    jfieldID f = c ? env->GetFieldID(c, "descriptor", "I") : nullptr;
    if (f == nullptr) { if (env->ExceptionCheck()) env->ExceptionClear(); return -1; }
    return static_cast<int>(env->GetIntField(fdObj, f));
}

// Mira probes the host ABI to pick which plugin .so to load. This build is arm64-v8a only.
// ⚠️The name does not state its polarity; read as is64Bit(), the sane answer for a single-ABI
// arm64 host. If Mira starts loading 32-bit plugin libraries, invert this.
extern "C" jboolean
Java_com_bytedance_mira_plugin_core_utils_HostAbiUtils_native_1get_1host_1abi(JNIEnv*, jobject) {
    wl_ensure_watcher();
    return (sizeof(void*) == 8) ? JNI_TRUE : JNI_FALSE;
}

// android.os.FileObserver$ObserverThread.init() -> an inotify fd  ('$' mangles to _00024)
extern "C" jint
Java_android_os_FileObserver_00024ObserverThread_init(JNIEnv*, jobject) {
    wl_ensure_watcher();
    int fd = ::inotify_init1(IN_CLOEXEC | IN_NONBLOCK);
    if (fd < 0) fd = ::inotify_init();
    return static_cast<jint>(fd);
}

// sun.nio.ch.FileChannelImpl.position0(FileDescriptor fd, long offset)
// Negative offset means "report the current position"; otherwise seek, then report.
extern "C" jlong
Java_sun_nio_ch_FileChannelImpl_position0(JNIEnv* env, jobject, jobject fdObj, jlong offset) {
    wl_ensure_watcher();
    const int fd = wl_fd_of(env, fdObj);
    if (fd < 0) return -1;
    const off_t r = (offset < 0) ? ::lseek(fd, 0, SEEK_CUR)
                                 : ::lseek(fd, static_cast<off_t>(offset), SEEK_SET);
    return static_cast<jlong>(r);
}

// ── WESTLAKE §647 (2026-08-15) — the rest of android.os.FileObserver$ObserverThread ──
// §636 bound init() only, so the app then died with
//   UnsatisfiedLinkError: No implementation found for void
//   android.os.FileObserver$ObserverThread.observe(int)
// which killed the ObserverThread outright (its uncaughtException handler is a no-op here).
// Toutiao uses FileObserver for tamper monitoring, so a faithful-but-inert implementation is the
// right trade: register the watches for real, drain the event stream, and do NOT dispatch
// onEvent() callbacks. That keeps the thread alive and the fd healthy without inventing events.
extern "C" void
Java_android_os_FileObserver_00024ObserverThread_startWatching(
    JNIEnv* env, jobject, jint fd, jobjectArray paths, jint mask, jintArray out_wds) {
    wl_ensure_watcher();
    if (env == nullptr || fd < 0 || paths == nullptr) return;
    const jsize n = env->GetArrayLength(paths);
    for (jsize i = 0; i < n; ++i) {
        jstring jp = (jstring)env->GetObjectArrayElement(paths, i);
        jint wd = -1;
        if (jp != nullptr) {
            const char* p = env->GetStringUTFChars(jp, nullptr);
            if (p != nullptr) {
                wd = (jint)::inotify_add_watch(fd, p, (uint32_t)mask);
                env->ReleaseStringUTFChars(jp, p);
            }
            env->DeleteLocalRef(jp);
        }
        if (out_wds != nullptr && i < env->GetArrayLength(out_wds)) {
            env->SetIntArrayRegion(out_wds, i, 1, &wd);
        }
    }
}

extern "C" void
Java_android_os_FileObserver_00024ObserverThread_stopWatching(
    JNIEnv* env, jobject, jint fd, jintArray wds) {
    wl_ensure_watcher();
    if (env == nullptr || fd < 0 || wds == nullptr) return;
    const jsize n = env->GetArrayLength(wds);
    jint* w = env->GetIntArrayElements(wds, nullptr);
    if (w == nullptr) return;
    for (jsize i = 0; i < n; ++i) {
        if (w[i] >= 0) ::inotify_rm_watch(fd, w[i]);
    }
    env->ReleaseIntArrayElements(wds, w, JNI_ABORT);
}

// The ObserverThread's run loop: block here until the fd is closed by stopWatching/close.
// Returning immediately would spin the Java thread; never returning would wedge shutdown.
extern "C" void
Java_android_os_FileObserver_00024ObserverThread_observe(JNIEnv*, jobject, jint fd) {
    wl_ensure_watcher();
    if (fd < 0) return;
    char buf[4096];
    for (;;) {
        const ssize_t n = ::read(fd, buf, sizeof(buf));
        if (n > 0) continue;                 // events drained, not dispatched (see note above)
        if (n < 0 && errno == EINTR) continue;
        break;                               // fd closed or hard error -> let the thread exit
    }
}

// ── WESTLAKE §648 (2026-08-15) — the last two UnsatisfiedLinkErrors before bind ──
// sun.nio.ch.IOUtil.iovMax(): static, returns the platform's writev/readv vector limit. musl
// exposes it via _SC_IOV_MAX; IOV_MAX (1024) is the POSIX floor and a safe fallback.
extern "C" jint Java_sun_nio_ch_IOUtil_iovMax(JNIEnv*, jobject) {
    wl_ensure_watcher();
    const long v = ::sysconf(_SC_IOV_MAX);
    return static_cast<jint>(v > 0 ? v : 1024);
}

// com.bytedance.bdauditsdkbase.jnihook.JniHookController.OOO(): ByteDance's JNI-hook installer
// (obfuscated). Deliberately a NO-OP: it exists to install JNI hooks for their audit SDK, which
// this port neither needs nor wants, and letting it throw UnsatisfiedLinkError kills the calling
// thread. Returning quietly is the same "let the compat layer decline" answer used throughout.
extern "C" void Java_com_bytedance_bdauditsdkbase_jnihook_JniHookController_OOO(JNIEnv*, jobject) {
    wl_ensure_watcher();
}
