#ifndef _GNU_SOURCE
#define _GNU_SOURCE
#endif

#include <dlfcn.h>
#include <fcntl.h>
#include <locale.h>
#include <netdb.h>
#include <pthread.h>
#include <signal.h>
#include <errno.h>
#include <stddef.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/select.h>
#include <arpa/inet.h>
#include <unistd.h>

static int caller_is_webview(void *return_address, const char **caller_path)
{
    Dl_info caller = {0};
    if (dladdr(return_address, &caller) == 0 || caller.dli_fname == NULL ||
        strstr(caller.dli_fname, "libwebviewchromium.so") == NULL) {
        return 0;
    }
    if (caller_path != NULL) {
        *caller_path = caller.dli_fname;
    }
    return 1;
}

static int caller_uses_bionic_signal_abi(void *return_address,
                                         const char **caller_path)
{
    Dl_info caller = {0};
    if (dladdr(return_address, &caller) == 0 || caller.dli_fname == NULL) {
        return 0;
    }
    if (strstr(caller.dli_fname, "libwebviewchromium.so") == NULL &&
        strstr(caller.dli_fname, "/data/local/tmp/asx/lib/") == NULL) {
        return 0;
    }
    if (caller_path != NULL) {
        *caller_path = caller.dli_fname;
    }
    return 1;
}

/*
 * Android's public arm64 signal ABI is not compatible with OH musl:
 *
 *   bionic: flags@0, callback@8, 64-bit mask@16, restorer@24 (32 bytes)
 *   OH musl: callback@0, 128-byte mask@8, flags@136, restorer@144 (152 bytes)
 *   bionic sigset_t: 8 bytes
 *   OH musl sigset_t: 128 bytes
 *
 * Letting Android-built Chromium call OH sigaction directly makes musl treat
 * the bionic flags word as a callback pointer and overrun a bionic old-action
 * buffer on return.  Letting it call OH sigfillset/sigemptyset directly is
 * equally destructive: musl writes 128 bytes into Chromium's 8-byte set,
 * corrupting the stack and eventually executing an action flags word such as
 * 0x18000004 as a callback.
 *
 * WebView ELF imports are redirected to equal-length wl_* names at deployment
 * time, so these converters are scoped to the Android WebView boundary.
 * Native OH and ART signal handling is untouched.
 */
struct westlake_webview_bionic_sigaction {
    int32_t flags;
    uint32_t flags_padding;
    union {
        void (*handler)(int);
        void (*sigaction)(int, siginfo_t *, void *);
    } callback;
    uint64_t mask;
    void (*restorer)(void);
};

_Static_assert(sizeof(struct westlake_webview_bionic_sigaction) == 32,
               "unexpected bionic arm64 sigaction size");
_Static_assert(offsetof(struct westlake_webview_bionic_sigaction, callback) == 8,
               "unexpected bionic arm64 sigaction callback offset");
_Static_assert(offsetof(struct westlake_webview_bionic_sigaction, mask) == 16,
               "unexpected bionic arm64 sigaction mask offset");
_Static_assert(sizeof(struct sigaction) == 152,
               "unexpected OH arm64 sigaction size");
_Static_assert(offsetof(struct sigaction, sa_mask) == 8,
               "unexpected OH arm64 sigaction mask offset");
_Static_assert(offsetof(struct sigaction, sa_flags) == 136,
               "unexpected OH arm64 sigaction flags offset");
_Static_assert(sizeof(sigset_t) == 128,
               "unexpected OH arm64 sigset_t size");

typedef uint64_t westlake_webview_bionic_sigset_t;

static int westlake_bionic_signal_number_is_valid(int signal_number)
{
    return signal_number > 0 && signal_number <= 64;
}

int wl_emptyset(westlake_webview_bionic_sigset_t *set)
{
    if (set == NULL) {
        errno = EINVAL;
        return -1;
    }
    *set = 0;
    return 0;
}

int wl_fillset(westlake_webview_bionic_sigset_t *set)
{
    if (set == NULL) {
        errno = EINVAL;
        return -1;
    }
    *set = UINT64_MAX;
    return 0;
}

int wl_addset(westlake_webview_bionic_sigset_t *set, int signal_number)
{
    if (set == NULL ||
        !westlake_bionic_signal_number_is_valid(signal_number)) {
        errno = EINVAL;
        return -1;
    }
    *set |= UINT64_C(1) << (signal_number - 1);
    return 0;
}

int wl_ismember(const westlake_webview_bionic_sigset_t *set,
                int signal_number)
{
    if (set == NULL ||
        !westlake_bionic_signal_number_is_valid(signal_number)) {
        errno = EINVAL;
        return -1;
    }
    return ((*set & (UINT64_C(1) << (signal_number - 1))) != 0) ? 1 : 0;
}

int wl_pthread_mask(int how,
                    const westlake_webview_bionic_sigset_t *bionic_set,
                    westlake_webview_bionic_sigset_t *bionic_old_set)
{
    typedef int (*PthreadSigmaskFn)(int, const sigset_t *, sigset_t *);
    static PthreadSigmaskFn real_pthread_sigmask;
    if (real_pthread_sigmask == NULL) {
        real_pthread_sigmask =
                (PthreadSigmaskFn)dlsym(RTLD_NEXT, "pthread_sigmask");
    }
    if (real_pthread_sigmask == NULL) {
        return ENOSYS;
    }

    sigset_t oh_set;
    sigset_t oh_old_set;
    const sigset_t *oh_set_pointer = NULL;
    sigset_t *oh_old_set_pointer = NULL;
    if (bionic_set != NULL) {
        memset(&oh_set, 0, sizeof(oh_set));
        memcpy(&oh_set, bionic_set, sizeof(*bionic_set));
        oh_set_pointer = &oh_set;
    }
    if (bionic_old_set != NULL) {
        memset(&oh_old_set, 0, sizeof(oh_old_set));
        oh_old_set_pointer = &oh_old_set;
    }

    int result = real_pthread_sigmask(how, oh_set_pointer,
                                      oh_old_set_pointer);
    if (result == 0 && bionic_old_set != NULL) {
        memcpy(bionic_old_set, &oh_old_set, sizeof(*bionic_old_set));
    }
    return result;
}

int sigemptyset(sigset_t *set)
{
    if (caller_uses_bionic_signal_abi(__builtin_return_address(0), NULL)) {
        return wl_emptyset((westlake_webview_bionic_sigset_t *)set);
    }
    typedef int (*SigemptysetFn)(sigset_t *);
    static SigemptysetFn real_sigemptyset;
    if (real_sigemptyset == NULL) {
        real_sigemptyset = (SigemptysetFn)dlsym(RTLD_NEXT, "sigemptyset");
    }
    if (real_sigemptyset == NULL) {
        errno = ENOSYS;
        return -1;
    }
    return real_sigemptyset(set);
}

int sigfillset(sigset_t *set)
{
    if (caller_uses_bionic_signal_abi(__builtin_return_address(0), NULL)) {
        return wl_fillset((westlake_webview_bionic_sigset_t *)set);
    }
    typedef int (*SigfillsetFn)(sigset_t *);
    static SigfillsetFn real_sigfillset;
    if (real_sigfillset == NULL) {
        real_sigfillset = (SigfillsetFn)dlsym(RTLD_NEXT, "sigfillset");
    }
    if (real_sigfillset == NULL) {
        errno = ENOSYS;
        return -1;
    }
    return real_sigfillset(set);
}

int sigaddset(sigset_t *set, int signal_number)
{
    if (caller_uses_bionic_signal_abi(__builtin_return_address(0), NULL)) {
        return wl_addset((westlake_webview_bionic_sigset_t *)set,
                         signal_number);
    }
    typedef int (*SigaddsetFn)(sigset_t *, int);
    static SigaddsetFn real_sigaddset;
    if (real_sigaddset == NULL) {
        real_sigaddset = (SigaddsetFn)dlsym(RTLD_NEXT, "sigaddset");
    }
    if (real_sigaddset == NULL) {
        errno = ENOSYS;
        return -1;
    }
    return real_sigaddset(set, signal_number);
}

int sigdelset(sigset_t *set, int signal_number)
{
    if (caller_uses_bionic_signal_abi(__builtin_return_address(0), NULL)) {
        if (set == NULL ||
            !westlake_bionic_signal_number_is_valid(signal_number)) {
            errno = EINVAL;
            return -1;
        }
        *(westlake_webview_bionic_sigset_t *)set &=
                ~(UINT64_C(1) << (signal_number - 1));
        return 0;
    }
    typedef int (*SigdelsetFn)(sigset_t *, int);
    static SigdelsetFn real_sigdelset;
    if (real_sigdelset == NULL) {
        real_sigdelset = (SigdelsetFn)dlsym(RTLD_NEXT, "sigdelset");
    }
    if (real_sigdelset == NULL) {
        errno = ENOSYS;
        return -1;
    }
    return real_sigdelset(set, signal_number);
}

int sigismember(const sigset_t *set, int signal_number)
{
    if (caller_uses_bionic_signal_abi(__builtin_return_address(0), NULL)) {
        return wl_ismember(
                (const westlake_webview_bionic_sigset_t *)set,
                signal_number);
    }
    typedef int (*SigismemberFn)(const sigset_t *, int);
    static SigismemberFn real_sigismember;
    if (real_sigismember == NULL) {
        real_sigismember =
                (SigismemberFn)dlsym(RTLD_NEXT, "sigismember");
    }
    if (real_sigismember == NULL) {
        errno = ENOSYS;
        return -1;
    }
    return real_sigismember(set, signal_number);
}

int pthread_sigmask(int how, const sigset_t *set, sigset_t *old_set)
{
    if (caller_uses_bionic_signal_abi(__builtin_return_address(0), NULL)) {
        return wl_pthread_mask(
                how, (const westlake_webview_bionic_sigset_t *)set,
                (westlake_webview_bionic_sigset_t *)old_set);
    }
    typedef int (*PthreadSigmaskFn)(int, const sigset_t *, sigset_t *);
    static PthreadSigmaskFn real_pthread_sigmask;
    if (real_pthread_sigmask == NULL) {
        real_pthread_sigmask =
                (PthreadSigmaskFn)dlsym(RTLD_NEXT, "pthread_sigmask");
    }
    if (real_pthread_sigmask == NULL) {
        return ENOSYS;
    }
    return real_pthread_sigmask(how, set, old_set);
}

int wl_sigact(int signal_number,
              const struct westlake_webview_bionic_sigaction *bionic_action,
              struct westlake_webview_bionic_sigaction *bionic_old_action)
{
    typedef int (*SigactionFn)(int, const struct sigaction *,
                               struct sigaction *);
    static SigactionFn real_sigaction;
    if (real_sigaction == NULL) {
        real_sigaction = (SigactionFn)dlsym(RTLD_NEXT, "sigaction");
    }
    if (real_sigaction == NULL) {
        errno = ENOSYS;
        return -1;
    }

    struct sigaction oh_action;
    struct sigaction oh_old_action;
    const struct sigaction *oh_action_pointer = NULL;
    struct sigaction *oh_old_action_pointer = NULL;
    if (bionic_action != NULL) {
        memset(&oh_action, 0, sizeof(oh_action));
        oh_action.sa_flags = bionic_action->flags;
        memcpy(&oh_action.sa_mask, &bionic_action->mask,
               sizeof(bionic_action->mask));
        if ((bionic_action->flags & SA_SIGINFO) != 0) {
            oh_action.sa_sigaction = bionic_action->callback.sigaction;
        } else {
            oh_action.sa_handler = bionic_action->callback.handler;
        }
        oh_action.sa_restorer = bionic_action->restorer;
        oh_action_pointer = &oh_action;
    }
    if (bionic_old_action != NULL) {
        memset(&oh_old_action, 0, sizeof(oh_old_action));
        oh_old_action_pointer = &oh_old_action;
    }

    int result = real_sigaction(signal_number, oh_action_pointer,
                                oh_old_action_pointer);
    int saved_errno = errno;
    if (result == 0 && bionic_old_action != NULL) {
        memset(bionic_old_action, 0, sizeof(*bionic_old_action));
        bionic_old_action->flags = oh_old_action.sa_flags;
        memcpy(&bionic_old_action->mask, &oh_old_action.sa_mask,
               sizeof(bionic_old_action->mask));
        if ((oh_old_action.sa_flags & SA_SIGINFO) != 0) {
            bionic_old_action->callback.sigaction = oh_old_action.sa_sigaction;
        } else {
            bionic_old_action->callback.handler = oh_old_action.sa_handler;
        }
        bionic_old_action->restorer = oh_old_action.sa_restorer;
    }

    static unsigned int call_count;
    unsigned int call = __atomic_add_fetch(&call_count, 1, __ATOMIC_RELAXED);
    if (call <= 64) {
        fprintf(stderr,
                "[WESTLAKE-WEBVIEW-SIGNAL] sigaction #%u signal=%d "
                "flags=%#x callback=%p rc=%d errno=%d\n",
                call, signal_number,
                bionic_action != NULL ? bionic_action->flags : 0,
                bionic_action != NULL
                        ? (void *)bionic_action->callback.handler
                        : NULL,
                result, saved_errno);
    }
    errno = saved_errno;
    return result;
}

/*
 * Chromium normally reaches sigaction through its ELF import, which the
 * deployment patch renames to wl_sigact. Some signal-management paths resolve
 * the public name dynamically, though, bypassing that static import rewrite.
 * Passing their bionic struct directly to OH musl stores the flags word in the
 * signal-chain handler slot; the next signal then branches to values such as
 * 0x18000004. Interpose the public name as well, but translate only a direct
 * Android WebView caller. Every OH-native caller is forwarded unchanged.
 */
int sigaction(int signal_number, const struct sigaction *action,
              struct sigaction *old_action)
{
    if (caller_uses_bionic_signal_abi(__builtin_return_address(0), NULL)) {
        return wl_sigact(
                signal_number,
                (const struct westlake_webview_bionic_sigaction *)action,
                (struct westlake_webview_bionic_sigaction *)old_action);
    }

    typedef int (*SigactionFn)(int, const struct sigaction *,
                               struct sigaction *);
    static SigactionFn real_sigaction;
    if (real_sigaction == NULL) {
        real_sigaction = (SigactionFn)dlsym(RTLD_NEXT, "sigaction");
    }
    if (real_sigaction == NULL) {
        errno = ENOSYS;
        return -1;
    }
    return real_sigaction(signal_number, action, old_action);
}

/*
 * Chromium is Android-built, so its struct addrinfo uses Bionic's pointer
 * order: ai_canonname then ai_addr. OH musl reverses those two members while
 * retaining the same total size. The default OH namespace resolves ordinary
 * getaddrinfo imports to libc before an LD_PRELOAD interposer, and moving the
 * complete WebView DSO into the Android-native namespace breaks its JNI/load
 * group. Keep WebView in its proven namespace and expose uniquely named entry
 * points for an equal-length ELF import rewrite at deployment time.
 */
struct westlake_webview_bionic_addrinfo {
    int ai_flags;
    int ai_family;
    int ai_socktype;
    int ai_protocol;
    socklen_t ai_addrlen;
    char *ai_canonname;
    struct sockaddr *ai_addr;
    struct westlake_webview_bionic_addrinfo *ai_next;
};

_Static_assert(sizeof(struct westlake_webview_bionic_addrinfo) ==
                       sizeof(struct addrinfo),
               "Bionic and OH addrinfo sizes must match");
_Static_assert(offsetof(struct westlake_webview_bionic_addrinfo,
                        ai_canonname) == offsetof(struct addrinfo, ai_addr),
               "Bionic canonname must occupy OH addr slot");
_Static_assert(offsetof(struct westlake_webview_bionic_addrinfo, ai_addr) ==
                       offsetof(struct addrinfo, ai_canonname),
               "Bionic addr must occupy OH canonname slot");

static void westlake_webview_free_addrinfo(
        struct westlake_webview_bionic_addrinfo *entry)
{
    while (entry != NULL) {
        struct westlake_webview_bionic_addrinfo *next = entry->ai_next;
        free(entry->ai_canonname);
        free(entry->ai_addr);
        free(entry);
        entry = next;
    }
}

/*
 * Resolve the OH resolver from the already-loaded host libc itself.  A plain
 * getaddrinfo() call from this preload can otherwise be preempted by another
 * Android boundary interposer.  That is particularly harmful here: the
 * generic native-network shim decides whether to translate addrinfo from its
 * immediate return address, which is this shim rather than Chromium, and a
 * transient negative result is then cached by Chromium's host resolver.
 *
 * The result returned by this function is always the OH/musl layout and is
 * copied into Bionic layout below before it crosses back into WebView.
 */
typedef int (*westlake_webview_getaddrinfo_fn)(
        const char *, const char *, const struct addrinfo *,
        struct addrinfo **);
typedef void (*westlake_webview_freeaddrinfo_fn)(struct addrinfo *);

static pthread_once_t westlake_webview_libc_once = PTHREAD_ONCE_INIT;
static void *westlake_webview_libc_handle;
static westlake_webview_getaddrinfo_fn westlake_webview_host_getaddrinfo;
static westlake_webview_freeaddrinfo_fn westlake_webview_host_freeaddrinfo;
static const char *westlake_webview_resolver_path = "(unresolved)";

static void westlake_webview_open_host_libc(void)
{
    westlake_webview_libc_handle =
            dlopen("libc.so", RTLD_NOW | RTLD_NOLOAD);
    if (westlake_webview_libc_handle == NULL) {
        westlake_webview_libc_handle =
                dlopen("/system/lib64/libc.so", RTLD_NOW | RTLD_NOLOAD);
    }
    /* OH's libc.so is a symlink to the dynamic loader.  Keep every spelling
     * NOLOAD-only: opening a different spelling normally can map a second
     * loader instance instead of returning the process's libc provider. */
    if (westlake_webview_libc_handle == NULL) {
        westlake_webview_libc_handle = dlopen(
                "/system/lib/ld-musl-aarch64.so.1",
                RTLD_NOW | RTLD_NOLOAD);
    }
    if (westlake_webview_libc_handle != NULL) {
        westlake_webview_host_getaddrinfo =
                (westlake_webview_getaddrinfo_fn)dlsym(
                        westlake_webview_libc_handle, "getaddrinfo");
        westlake_webview_host_freeaddrinfo =
                (westlake_webview_freeaddrinfo_fn)dlsym(
                        westlake_webview_libc_handle, "freeaddrinfo");
    }

    /* This DSO also exports a dlopen boundary wrapper, so an internal dlopen
     * can have no RTLD_NEXT provider when a test or provider loads the shim
     * after libc.  RTLD_DEFAULT is safe for these uniquely renamed WebView
     * entries: this DSO does not export ordinary getaddrinfo/freeaddrinfo.
     * It resolves either OH libc directly or the generic native-network
     * boundary.  The latter selects ABI translation by immediate caller and
     * therefore forwards this shim's calls in OH layout, exactly as required
     * before westlake_webview_copy_addrinfo converts the result to Bionic. */
    if (westlake_webview_host_getaddrinfo == NULL) {
        westlake_webview_host_getaddrinfo =
                (westlake_webview_getaddrinfo_fn)dlsym(
                        RTLD_DEFAULT, "getaddrinfo");
    }
    if (westlake_webview_host_freeaddrinfo == NULL) {
        westlake_webview_host_freeaddrinfo =
                (westlake_webview_freeaddrinfo_fn)dlsym(
                        RTLD_DEFAULT, "freeaddrinfo");
    }

    /* Keep a final fallback for unusual loader namespaces. */
    if (westlake_webview_host_getaddrinfo == NULL) {
        westlake_webview_host_getaddrinfo =
                (westlake_webview_getaddrinfo_fn)dlsym(
                        RTLD_NEXT, "getaddrinfo");
    }
    if (westlake_webview_host_freeaddrinfo == NULL) {
        westlake_webview_host_freeaddrinfo =
                (westlake_webview_freeaddrinfo_fn)dlsym(
                        RTLD_NEXT, "freeaddrinfo");
    }

    if (westlake_webview_host_getaddrinfo != NULL) {
        Dl_info info = {0};
        if (dladdr((void *)westlake_webview_host_getaddrinfo, &info) != 0 &&
            info.dli_fname != NULL) {
            westlake_webview_resolver_path = info.dli_fname;
        }
    }
}

static int westlake_webview_resolve_host(
        const char *node, const char *service, const struct addrinfo *hints,
        struct addrinfo **result)
{
    pthread_once(&westlake_webview_libc_once,
                 westlake_webview_open_host_libc);
    if (westlake_webview_host_getaddrinfo == NULL) return EAI_SYSTEM;
    return westlake_webview_host_getaddrinfo(node, service, hints, result);
}

static void westlake_webview_release_host_addrinfo(struct addrinfo *result)
{
    pthread_once(&westlake_webview_libc_once,
                 westlake_webview_open_host_libc);
    if (westlake_webview_host_freeaddrinfo != NULL) {
        westlake_webview_host_freeaddrinfo(result);
    }
}

static struct westlake_webview_bionic_addrinfo *
westlake_webview_copy_addrinfo(const struct addrinfo *source)
{
    struct westlake_webview_bionic_addrinfo *head = NULL;
    struct westlake_webview_bionic_addrinfo **tail = &head;
    for (; source != NULL; source = source->ai_next) {
        struct westlake_webview_bionic_addrinfo *entry =
                calloc(1, sizeof(*entry));
        if (entry == NULL) goto fail;
        entry->ai_flags = source->ai_flags;
        entry->ai_family = source->ai_family;
        entry->ai_socktype = source->ai_socktype;
        entry->ai_protocol = source->ai_protocol;
        entry->ai_addrlen = source->ai_addrlen;
        if (source->ai_canonname != NULL) {
            entry->ai_canonname = strdup(source->ai_canonname);
            if (entry->ai_canonname == NULL) {
                free(entry);
                goto fail;
            }
        }
        if (source->ai_addr != NULL && source->ai_addrlen != 0) {
            entry->ai_addr = malloc(source->ai_addrlen);
            if (entry->ai_addr == NULL) {
                free(entry->ai_canonname);
                free(entry);
                goto fail;
            }
            memcpy(entry->ai_addr, source->ai_addr, source->ai_addrlen);
        }
        *tail = entry;
        tail = &entry->ai_next;
    }
    return head;

fail:
    westlake_webview_free_addrinfo(head);
    return NULL;
}

/* Both exported names deliberately have the same byte length as the imports
 * they replace: getaddrinfo -> wl_getai_oh and freeaddrinfo -> wl_freeai_oh. */
int wl_getai_oh(const char *node, const char *service,
                const struct addrinfo *bionic_hints,
                struct addrinfo **bionic_result)
{
    struct addrinfo oh_hints;
    const struct addrinfo *real_hints = NULL;
    if (bionic_hints != NULL) {
        memset(&oh_hints, 0, sizeof(oh_hints));
        oh_hints.ai_flags = bionic_hints->ai_flags;
        oh_hints.ai_family = bionic_hints->ai_family;
        oh_hints.ai_socktype = bionic_hints->ai_socktype;
        oh_hints.ai_protocol = bionic_hints->ai_protocol;
        real_hints = &oh_hints;
    }

    struct addrinfo *oh_result = NULL;
    /*
     * Chromium can issue its first host lookup while the OH resolver service
     * is still settling after a network/reconnect notification.  OH musl has
     * returned both EAI_AGAIN and EAI_NONAME for that transient window, even
     * though the same hostname resolves immediately afterwards through the
     * same getaddrinfo entry point.  Chromium caches the first negative result
     * long enough to leave a newly opened WebView permanently blank.
     *
     * Retry only name-resolution failures, keep the delay bounded, and retain
     * the original result for every other error.  This remains a generic
     * Android-to-OH boundary repair: no hostnames or applications are singled
     * out, and a genuinely absent name costs at most 300 ms.
     */
    int rc = 0;
    unsigned int attempts = 0;
    do {
        ++attempts;
        oh_result = NULL;
        rc = westlake_webview_resolve_host(
                node, service, real_hints, &oh_result);
        if (rc != EAI_AGAIN && rc != EAI_NONAME) break;
        if (attempts >= 3) break;
        usleep(attempts * 100 * 1000);
    } while (1);
    static unsigned int lookup_count;
    unsigned int lookup = __atomic_add_fetch(&lookup_count, 1,
                                              __ATOMIC_RELAXED);
    if (lookup <= 256) {
        fprintf(stderr,
                "[WESTLAKE-WEBVIEW-NET] dns #%u host=%s service=%s "
                "family=%d socktype=%d attempts=%u rc=%d resolver=%s\n",
                lookup, node != NULL ? node : "(null)",
                service != NULL ? service : "(null)",
                real_hints != NULL ? real_hints->ai_family : 0,
                real_hints != NULL ? real_hints->ai_socktype : 0,
                attempts, rc, westlake_webview_resolver_path);
    }
    if (rc != 0) return rc;

    struct westlake_webview_bionic_addrinfo *translated =
            westlake_webview_copy_addrinfo(oh_result);
    westlake_webview_release_host_addrinfo(oh_result);
    if (translated == NULL && oh_result != NULL) {
        if (bionic_result != NULL) *bionic_result = NULL;
        return EAI_MEMORY;
    }
    if (bionic_result == NULL) {
        westlake_webview_free_addrinfo(translated);
        return EAI_FAIL;
    }
    *bionic_result = (struct addrinfo *)translated;

    return 0;
}

void wl_freeai_oh(struct addrinfo *result)
{
    westlake_webview_free_addrinfo(
            (struct westlake_webview_bionic_addrinfo *)result);
}

static pthread_once_t westlake_webview_dns_once = PTHREAD_ONCE_INIT;
static struct in_addr westlake_webview_ipv4_dns;
static int westlake_webview_has_ipv4_dns;

static int westlake_webview_equivalent_ipv4_dns(
        const struct in6_addr *ipv6, struct in_addr *ipv4)
{
    static const unsigned char google_primary_v6[16] = {
        0x20, 0x01, 0x48, 0x60, 0x48, 0x60, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x88, 0x88,
    };
    static const unsigned char google_secondary_v6[16] = {
        0x20, 0x01, 0x48, 0x60, 0x48, 0x60, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x88, 0x44,
    };

    if (memcmp(ipv6->s6_addr, google_primary_v6,
               sizeof(google_primary_v6)) == 0) {
        return inet_pton(AF_INET, "8.8.8.8", ipv4) == 1;
    }
    if (memcmp(ipv6->s6_addr, google_secondary_v6,
               sizeof(google_secondary_v6)) == 0) {
        return inet_pton(AF_INET, "8.8.4.4", ipv4) == 1;
    }
    return 0;
}

static void westlake_webview_read_ipv4_dns(void)
{
    FILE *resolver = fopen("/etc/resolv.conf", "r");
    if (resolver == NULL) return;

    char line[256];
    while (fgets(line, sizeof(line), resolver) != NULL) {
        char address[INET_ADDRSTRLEN];
        if (sscanf(line, " nameserver %15s", address) == 1 &&
            inet_pton(AF_INET, address, &westlake_webview_ipv4_dns) == 1) {
            westlake_webview_has_ipv4_dns = 1;
            break;
        }
    }
    fclose(resolver);
}

/* Equal-length import target for connect -> wl_conn.  sockaddr has the same
 * layout at this boundary. Chromium may select an IPv6 DNS server from the
 * Android network facade even when the OH board has only an IPv4 default
 * route. Retry only that immediate ENETUNREACH case through an IPv4-mapped
 * address. Prefer the OH host's configured IPv4 resolver: an equivalent
 * public-vendor address may be syntactically reachable but filtered by the
 * current network, while /etc/resolv.conf is the resolver that OH libc has
 * already selected for that network. Use the vendor equivalent only when the
 * host has no IPv4 resolver. All ordinary destinations and successful IPv6
 * connections remain untouched. */
int wl_conn(int fd, const struct sockaddr *address, socklen_t address_length)
{
    typedef int (*ConnectFn)(int, const struct sockaddr *, socklen_t);
    static ConnectFn real_connect;
    if (real_connect == NULL) {
        real_connect = (ConnectFn)dlsym(RTLD_NEXT, "connect");
    }
    if (real_connect == NULL) {
        errno = ENOSYS;
        return -1;
    }

    char host[INET6_ADDRSTRLEN] = "?";
    unsigned int port = 0;
    int family = address != NULL ? address->sa_family : 0;
    if (address != NULL && family == AF_INET &&
        address_length >= sizeof(struct sockaddr_in)) {
        const struct sockaddr_in *in = (const struct sockaddr_in *)address;
        inet_ntop(AF_INET, &in->sin_addr, host, sizeof(host));
        port = ntohs(in->sin_port);
    } else if (address != NULL && family == AF_INET6 &&
               address_length >= sizeof(struct sockaddr_in6)) {
        const struct sockaddr_in6 *in6 =
                (const struct sockaddr_in6 *)address;
        inet_ntop(AF_INET6, &in6->sin6_addr, host, sizeof(host));
        port = ntohs(in6->sin6_port);
    }

    int result = real_connect(fd, address, address_length);
    int saved_errno = errno;
    int used_dns_fallback = 0;
    char fallback_host[INET_ADDRSTRLEN] = "-";
    if (result == -1 && saved_errno == ENETUNREACH && family == AF_INET6 &&
        port == 53 && address_length >= sizeof(struct sockaddr_in6)) {
        const struct sockaddr_in6 *requested =
                (const struct sockaddr_in6 *)address;
        struct in_addr fallback_dns;
        pthread_once(&westlake_webview_dns_once,
                     westlake_webview_read_ipv4_dns);
        int has_fallback = westlake_webview_has_ipv4_dns;
        if (has_fallback) {
            fallback_dns = westlake_webview_ipv4_dns;
        } else {
            has_fallback = westlake_webview_equivalent_ipv4_dns(
                    &requested->sin6_addr, &fallback_dns);
        }
        if (has_fallback) {
            struct sockaddr_in6 mapped;
            memset(&mapped, 0, sizeof(mapped));
            mapped.sin6_family = AF_INET6;
            mapped.sin6_port = requested->sin6_port;
            mapped.sin6_addr.s6_addr[10] = 0xff;
            mapped.sin6_addr.s6_addr[11] = 0xff;
            memcpy(&mapped.sin6_addr.s6_addr[12],
                   &fallback_dns, sizeof(fallback_dns));
            inet_ntop(AF_INET, &fallback_dns,
                      fallback_host, sizeof(fallback_host));
            result = real_connect(fd, (const struct sockaddr *)&mapped,
                                  sizeof(mapped));
            saved_errno = errno;
            used_dns_fallback = 1;
        }
    }
    static unsigned int connect_count;
    unsigned int call = __atomic_add_fetch(&connect_count, 1,
                                            __ATOMIC_RELAXED);
    if (call <= 512) {
        fprintf(stderr,
                "[WESTLAKE-WEBVIEW-NET] connect #%u fd=%d family=%d "
                "address=%s port=%u rc=%d errno=%d dns_v4_fallback=%s\n",
                call, fd, family, host, port, result,
                result == 0 ? 0 : saved_errno,
                used_dns_fallback ? fallback_host : "-");
    }
    errno = saved_errno;
    return result;
}

/*
 * WESTLAKE §734: select the working OHOS GLES entry-point library for the
 * Android-built WebView.
 *
 * Chromium asks Android's loader for libGLESv2.so.  On this board that name
 * resolves to /system/lib64/ndk/libGLESv2.so.  The NDK facade requires an
 * Android EGL thread hook table which OHOS's platform EGL does not install:
 * even with a valid current context, glGetString returns NULL and
 * glGetIntegerv leaves GL_MAX_VERTEX_ATTRIBS untouched.  The platform GLESv3
 * library exports the GLES2 ABI as well and reports the live Mali context
 * correctly through the same EGL display/context.
 *
 * Keep the translation at the Android/OH boundary and scope it to direct
 * libwebviewchromium callers.  OH-native code and every other library request
 * retain the system loader's normal behavior.
 */
void *dlopen(const char *filename, int flags)
{
    typedef void *(*DlopenFn)(const char *, int);
    static DlopenFn real_dlopen;
    if (real_dlopen == NULL) {
        real_dlopen = (DlopenFn)dlsym(RTLD_NEXT, "dlopen");
    }
    if (real_dlopen == NULL) {
        errno = ENOSYS;
        return NULL;
    }

    const char *actual_filename = filename;
    const char *basename = filename != NULL ? strrchr(filename, '/') : NULL;
    basename = basename != NULL ? basename + 1 : filename;
    if (basename != NULL && strcmp(basename, "libGLESv2.so") == 0 &&
        caller_is_webview(__builtin_return_address(0), NULL)) {
        actual_filename = "/system/lib64/platformsdk/libGLESv3.so";
        static int logged;
        if (!__atomic_exchange_n(&logged, 1, __ATOMIC_RELAXED)) {
            fprintf(stderr,
                    "[WESTLAKE-WEBVIEW-BIONIC] GLES library translated "
                    "%s -> %s for libwebviewchromium.so\n",
                    filename, actual_filename);
        }
    }
    return real_dlopen(actual_filename, flags);
}

/*
 * WESTLAKE §680: translate Android/bionic sysconf selectors at the WebView
 * libc boundary.
 *
 * Bionic assigns _SC_PAGESIZE=39 and _SC_PAGE_SIZE=40.  OH musl assigns both
 * spellings value 30; its selectors 39 and 40 mean unrelated limits.  Passing
 * Chromium's selector 39 straight to musl therefore reports 1000 bytes, which
 * violates Chromium's allocator alignment invariant and deliberately traps.
 *
 * The memory selectors differ too. Bionic uses 98 and 99 for _SC_PHYS_PAGES
 * and _SC_AVPHYS_PAGES, while OH musl uses its own selector namespace. Passing
 * Chromium's _SC_PHYS_PAGES straight through makes its physical-memory query
 * fail and collapses Blink's maximum decoded-image budget to zero; valid JPEG
 * files then fail JPEGImageDecoder::SetSize before pixel decoding starts.
 *
 * This shim is process-wide through LD_PRELOAD, so do not globally reinterpret
 * bionic selector numbers: OH-native libraries legitimately use the musl
 * namespace. Translate only direct callers from the Android-built WebView DSO,
 * and pass every other call to the next (musl) definition unchanged.
 */
long sysconf(int name)
{
    enum {
        BIONIC_SC_PAGESIZE = 0x27,
        BIONIC_SC_PAGE_SIZE = 0x28,
        BIONIC_SC_PHYS_PAGES = 0x62,
        BIONIC_SC_AVPHYS_PAGES = 0x63,
    };

    const char *caller_path = NULL;
    if (caller_is_webview(__builtin_return_address(0), &caller_path)) {
        if (name == BIONIC_SC_PAGESIZE || name == BIONIC_SC_PAGE_SIZE) {
            long page_size = getpagesize();
            static int logged;
            if (!__atomic_exchange_n(&logged, 1, __ATOMIC_RELAXED)) {
                fprintf(stderr,
                        "[WESTLAKE-WEBVIEW-BIONIC] sysconf selector=%d "
                        "translated bionic->musl page_size=%ld caller=%s\n",
                        name, page_size, caller_path);
            }
            return page_size;
        }
    }

    typedef long (*SysconfFn)(int);
    static SysconfFn real_sysconf;
    if (real_sysconf == NULL) {
        real_sysconf = (SysconfFn)dlsym(RTLD_NEXT, "sysconf");
    }
    if (real_sysconf == NULL) {
        errno = ENOSYS;
        return -1;
    }

    if (caller_path != NULL) {
        int native_name = -1;
        unsigned int log_bit = 0;
        switch (name) {
            case BIONIC_SC_PHYS_PAGES:
                native_name = _SC_PHYS_PAGES;
                log_bit = 1U << 0;
                break;
            case BIONIC_SC_AVPHYS_PAGES:
                native_name = _SC_AVPHYS_PAGES;
                log_bit = 1U << 1;
                break;
            default:
                break;
        }
        if (native_name >= 0) {
            long result = real_sysconf(native_name);
            static unsigned int logged_mask;
            unsigned int previous = __atomic_fetch_or(
                    &logged_mask, log_bit, __ATOMIC_RELAXED);
            if ((previous & log_bit) == 0) {
                fprintf(stderr,
                        "[WESTLAKE-WEBVIEW-BIONIC] sysconf selector=%d "
                        "translated bionic->musl selector=%d result=%ld "
                        "caller=%s\n",
                        name, native_name, result, caller_path);
            }
            return result;
        }
    }
    return real_sysconf(name);
}

/*
 * WESTLAKE §682: present the Android filesystem root to Android WebView while
 * preserving appspawn-x's OH-specific ART root.
 *
 * appspawn-x must use ANDROID_ROOT=/system/android for its staged ART/runtime
 * files. Chromium's Android Skia backend, however, computes its font directory
 * as getenv("ANDROID_ROOT") + "/fonts". On this OH board the Android-standard
 * font/config view is assembled under /data/local/tmp/asx/android-root. Its
 * fonts entry points at the board's /system/fonts and its etc/fonts.xml is the
 * complete adapter configuration. The system partition contains an older,
 * read-only bootstrap fonts.xml without script-fallback families, so returning
 * /system would make Chromium render Latin while dropping every CJK glyph.
 *
 * Translate only direct calls from libwebviewchromium.so. All framework, ART,
 * and OH-native callers continue to see the real process environment.
 */
char *getenv(const char *name)
{
    typedef char *(*GetenvFn)(const char *);
    static GetenvFn real_getenv;
    if (real_getenv == NULL) {
        real_getenv = (GetenvFn)dlsym(RTLD_NEXT, "getenv");
    }

    char *value = real_getenv != NULL ? real_getenv(name) : NULL;
    if (name != NULL && strcmp(name, "ANDROID_ROOT") == 0 &&
        caller_is_webview(__builtin_return_address(0), NULL)) {
        static char android_root[] = "/data/local/tmp/asx/android-root";
        static int logged;
        if (!__atomic_exchange_n(&logged, 1, __ATOMIC_RELAXED)) {
            fprintf(stderr,
                    "[WESTLAKE-WEBVIEW-BIONIC] ANDROID_ROOT translated "
                    "%s -> %s for libwebviewchromium.so\n",
                    value != NULL ? value : "<unset>", android_root);
        }
        return android_root;
    }
    return value;
}

/*
 * WESTLAKE §732: Skia's Android font parser uses a compile-time absolute
 * /system/etc/fonts.xml path even though it uses ANDROID_ROOT for the font-file
 * directory. The board's system partition is read-only and contains the early
 * bootstrap config, which has no script fallback families. Redirect only
 * Chromium's read of that one Android config file to the complete staged
 * adapter config; all other fopen callers and paths retain their native
 * behavior.
 */
FILE *fopen(const char *path, const char *mode)
{
    typedef FILE *(*FopenFn)(const char *, const char *);
    static FopenFn real_fopen;
    if (real_fopen == NULL) {
        real_fopen = (FopenFn)dlsym(RTLD_NEXT, "fopen");
    }
    if (real_fopen == NULL) {
        errno = ENOSYS;
        return NULL;
    }

    const int from_webview = caller_is_webview(__builtin_return_address(0), NULL);
    const char *actual_path = path;
    if (path != NULL && strcmp(path, "/system/etc/fonts.xml") == 0 &&
        from_webview) {
        actual_path = "/data/local/tmp/asx/android-root/etc/fonts.xml";
        static int logged;
        if (!__atomic_exchange_n(&logged, 1, __ATOMIC_RELAXED)) {
            fprintf(stderr,
                    "[WESTLAKE-WEBVIEW-BIONIC] font config translated %s -> %s\n",
                    path, actual_path);
        }
    }
    return real_fopen(actual_path, mode);
}

int __register_atfork(void (*prepare)(void), void (*parent)(void),
                      void (*child)(void), void *dso)
{
    (void)dso;
    fprintf(stderr, "[WESTLAKE-WEBVIEW-BIONIC] __register_atfork\n");
    return pthread_atfork(prepare, parent, child);
}

/* Diagnostic companion for the Android-sized setjmp boundary.  The assembly
 * calls this before saving the caller context, so a codec probe can establish
 * whether Chromium actually reaches the redirected import. */
void wl_sjp_note(void *environment, void *caller)
{
    if (access("/data/local/tmp/asx/trace_webview_setjmp", F_OK) != 0) {
        return;
    }
    static unsigned int calls;
    unsigned int call = __atomic_add_fetch(&calls, 1, __ATOMIC_RELAXED);
    if (call <= 256) {
        fprintf(stderr,
                "[WESTLAKE-WEBVIEW-SJP] call=%u env=%p caller=%p\n",
                call, environment, caller);
    }
}

void wl_ljmp_note(void *environment, int value, void *caller)
{
    if (access("/data/local/tmp/asx/trace_webview_setjmp", F_OK) != 0) {
        return;
    }
    static unsigned int calls;
    unsigned int call = __atomic_add_fetch(&calls, 1, __ATOMIC_RELAXED);
    if (call <= 256) {
        fprintf(stderr,
                "[WESTLAKE-WEBVIEW-LJMP] call=%u env=%p value=%d caller=%p\n",
                call, environment, value, caller);
    }
}

int *__errno(void)
{
    return __errno_location();
}

char *__gnu_strerror_r(int error_number, char *buffer, size_t buffer_size)
{
    if (buffer == NULL || buffer_size == 0) {
        return (char *)strerror(error_number);
    }
    if (strerror_r(error_number, buffer, buffer_size) != 0) {
        snprintf(buffer, buffer_size, "Unknown error %d", error_number);
    }
    return buffer;
}

#define WEBVIEW_PROP_NAME_MAX 128
#define WEBVIEW_PROP_VALUE_MAX 256
#define WEBVIEW_PROP_CACHE_SIZE 64

struct prop_info {
    char name[WEBVIEW_PROP_NAME_MAX];
    char value[WEBVIEW_PROP_VALUE_MAX];
    uint32_t serial;
};

static struct prop_info g_property_cache[WEBVIEW_PROP_CACHE_SIZE];
static size_t g_property_count;
static uint32_t g_property_serial;
static pthread_mutex_t g_property_mutex = PTHREAD_MUTEX_INITIALIZER;

static int read_oh_property(const char *name, char *value, size_t value_size)
{
    typedef int (*ReadParamFn)(const char *, char *, uint32_t *);
    static ReadParamFn read_param;
    static int resolved;
    if (!resolved) {
        read_param = (ReadParamFn)dlsym(RTLD_DEFAULT, "SystemReadParam");
        resolved = 1;
    }
    value[0] = '\0';
    if (read_param == NULL) {
        return 0;
    }
    uint32_t length = (uint32_t)value_size;
    if (read_param(name, value, &length) != 0) {
        value[0] = '\0';
        return 0;
    }
    value[value_size - 1] = '\0';
    return (int)strlen(value);
}

int __system_property_get(const char *name, char *value)
{
    if (name == NULL || value == NULL) {
        return 0;
    }
    int length = read_oh_property(name, value, WEBVIEW_PROP_VALUE_MAX);
    fprintf(stderr, "[WESTLAKE-WEBVIEW-BIONIC] property %s=%s\n", name, value);
    return length;
}

const struct prop_info *__system_property_find(const char *name)
{
    if (name == NULL) {
        return NULL;
    }
    char value[WEBVIEW_PROP_VALUE_MAX];
    if (read_oh_property(name, value, sizeof(value)) == 0) {
        return NULL;
    }

    pthread_mutex_lock(&g_property_mutex);
    struct prop_info *entry = NULL;
    for (size_t i = 0; i < g_property_count; ++i) {
        if (strcmp(g_property_cache[i].name, name) == 0) {
            entry = &g_property_cache[i];
            break;
        }
    }
    if (entry == NULL && g_property_count < WEBVIEW_PROP_CACHE_SIZE) {
        entry = &g_property_cache[g_property_count++];
        snprintf(entry->name, sizeof(entry->name), "%s", name);
    }
    if (entry != NULL) {
        snprintf(entry->value, sizeof(entry->value), "%s", value);
        entry->serial = ++g_property_serial;
    }
    pthread_mutex_unlock(&g_property_mutex);
    return entry;
}

void __system_property_read_callback(
        const struct prop_info *property,
        void (*callback)(void *, const char *, const char *, uint32_t),
        void *cookie)
{
    if (property != NULL && callback != NULL) {
        callback(cookie, property->name, property->value, property->serial);
    }
}

int __open_2(const char *path, int flags)
{
    if ((flags & O_CREAT) != 0) {
        fprintf(stderr,
                "[WESTLAKE-WEBVIEW-BIONIC] __open_2 rejected O_CREAT path=%s\n",
                path != NULL ? path : "<null>");
        abort();
    }
    return open(path, flags);
}

struct cmsghdr *__cmsg_nxthdr(struct msghdr *message, struct cmsghdr *control)
{
    struct cmsghdr *next = (struct cmsghdr *)((char *)control +
            CMSG_ALIGN(control->cmsg_len));
    size_t length = (size_t)((char *)(next + 1) - (char *)message->msg_control);
    return length > message->msg_controllen ? NULL : next;
}

void __FD_CLR_chk(int fd, fd_set *set, size_t set_size)
{
    if (fd < 0 || (size_t)fd >= set_size * 8) {
        abort();
    }
    FD_CLR(fd, set);
}

int __FD_ISSET_chk(int fd, const fd_set *set, size_t set_size)
{
    if (fd < 0 || (size_t)fd >= set_size * 8) {
        abort();
    }
    return FD_ISSET(fd, set);
}

void __FD_SET_chk(int fd, fd_set *set, size_t set_size)
{
    if (fd < 0 || (size_t)fd >= set_size * 8) {
        abort();
    }
    FD_SET(fd, set);
}

ssize_t __pread64_chk(int fd, void *buffer, size_t count, int64_t offset,
                      size_t buffer_size)
{
    if (count > buffer_size) {
        abort();
    }
    return pread(fd, buffer, count, (off_t)offset);
}

ssize_t __pwrite64_chk(int fd, const void *buffer, size_t count, int64_t offset,
                       size_t buffer_size)
{
    if (count > buffer_size) {
        abort();
    }
    return pwrite(fd, buffer, count, (off_t)offset);
}

long long strtoll_l(const char *text, char **end, int base, locale_t locale)
{
    (void)locale;
    return strtoll(text, end, base);
}

unsigned long long strtoull_l(const char *text, char **end, int base, locale_t locale)
{
    (void)locale;
    return strtoull(text, end, base);
}

void android_fdsan_exchange_owner_tag(int fd, uint64_t expected_tag, uint64_t new_tag)
{
    (void)fd;
    (void)expected_tag;
    (void)new_tag;
}

void android_set_abort_message(const char *message)
{
    fprintf(stderr, "[WESTLAKE-WEBVIEW-BIONIC] abort message: %s\n",
            message != NULL ? message : "<null>");
}

/*
 * WESTLAKE §709: do not export bionic/jemalloc's optional sdallocx API.
 *
 * Chromium weak-links sdallocx and deliberately falls back to its own
 * deallocator when the process allocator does not provide it.  OH uses musl,
 * not bionic's jemalloc.  The former shim advertised sdallocx and forwarded it
 * to musl free(), causing Chromium-owned allocations to crash in musl get_meta
 * as soon as §708 made WebView's native looper callbacks live.  Absence is the
 * correct ABI answer at this boundary.
 */

static char g_bionic_ctype[257];
const char *_ctype_ = g_bionic_ctype;

__attribute__((constructor)) static void initialize_bionic_ctype(void)
{
    enum {
        CTYPE_UPPER = 0x01,
        CTYPE_LOWER = 0x02,
        CTYPE_DIGIT = 0x04,
        CTYPE_SPACE = 0x08,
        CTYPE_PUNCT = 0x10,
        CTYPE_CONTROL = 0x20,
        CTYPE_HEX = 0x40,
        CTYPE_BLANK = 0x80,
    };
    for (int value = 0; value <= 255; ++value) {
        unsigned char flags = 0;
        if (value < 32 || value == 127) flags |= CTYPE_CONTROL;
        if (value >= 9 && value <= 13) flags |= CTYPE_SPACE;
        if (value == 32) flags |= CTYPE_SPACE | CTYPE_BLANK;
        if (value >= '0' && value <= '9') flags |= CTYPE_DIGIT;
        if (value >= 'A' && value <= 'Z') flags |= CTYPE_UPPER;
        if (value >= 'a' && value <= 'z') flags |= CTYPE_LOWER;
        if ((value >= 'A' && value <= 'F') ||
            (value >= 'a' && value <= 'f')) flags |= CTYPE_HEX;
        if ((value >= 33 && value <= 47) ||
            (value >= 58 && value <= 64) ||
            (value >= 91 && value <= 96) ||
            (value >= 123 && value <= 126)) flags |= CTYPE_PUNCT;
        g_bionic_ctype[value + 1] = (char)flags;
    }
}

char *__strncpy_chk2(char *dst, const char *src, size_t count,
                     size_t dst_size, size_t src_size)
{
    if (count > dst_size) {
        fprintf(stderr,
                "[WESTLAKE-WEBVIEW-BIONIC] __strncpy_chk2 write overflow count=%zu dst=%zu\n",
                count, dst_size);
        abort();
    }

    char *result = dst;
    const char *source_start = src;
    while (count != 0) {
        if ((size_t)(src - source_start) >= src_size) {
            fprintf(stderr,
                    "[WESTLAKE-WEBVIEW-BIONIC] __strncpy_chk2 read overflow src=%zu\n",
                    src_size);
            abort();
        }
        *dst = *src;
        ++dst;
        ++src;
        --count;
        if (dst[-1] == '\0') {
            memset(dst, 0, count);
            break;
        }
    }
    return result;
}
