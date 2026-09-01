#define _GNU_SOURCE
/*
 * Android NDK networking boundary compatibility for OpenHarmony.
 *
 * APK DSOs in the Westlake Android namespace call the normal POSIX socket
 * surface.  Calls selected by WESTLAKE_ANDROID_NATIVE_NET_TARGETS receive the
 * small ABI translations required between Bionic and OH musl; every other
 * caller is forwarded unchanged.  The DSO can therefore be preloaded at the
 * process boundary even though OH's inherited libc wins inside a child loader
 * namespace.  Optional trace output distinguishes ABI mismatches from ordinary
 * network failures without changing call behavior.
 */
#include <arpa/inet.h>
#include <dlfcn.h>
#include <errno.h>
#include <netdb.h>
#include <poll.h>
#include <pthread.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/epoll.h>
#include <sys/syscall.h>

extern long syscall(long number, ...);

static pthread_once_t westlake_libc_once = PTHREAD_ONCE_INIT;
static void *westlake_libc_handle;

static void westlake_open_libc(void) {
    westlake_libc_handle = dlopen("libc.so", RTLD_NOW | RTLD_NOLOAD);
    if (westlake_libc_handle == NULL) {
        westlake_libc_handle = dlopen("/system/lib64/libc.so",
                                      RTLD_NOW | RTLD_NOLOAD);
    }
}

static void *westlake_libc_symbol(const char *name) {
    pthread_once(&westlake_libc_once, westlake_open_libc);
    if (westlake_libc_handle != NULL) {
        void *symbol = dlsym(westlake_libc_handle, name);
        if (symbol != NULL) return symbol;
    }
    return dlsym(RTLD_NEXT, name);
}

static int westlake_network_trace_enabled(void) {
    const char *value = getenv("WESTLAKE_TRACE_ANDROID_NATIVE_NET");
    return value != NULL && value[0] != '\0' && strcmp(value, "0") != 0 &&
           strcmp(value, "false") != 0 && strcmp(value, "FALSE") != 0 &&
           strcmp(value, "off") != 0 && strcmp(value, "OFF") != 0;
}

static int westlake_basename_in_list(const char *path, const char *list) {
    if (path == NULL || list == NULL || list[0] == '\0') return 0;
    const char *base = strrchr(path, '/');
    base = base != NULL ? base + 1 : path;
    const size_t base_length = strlen(base);
    for (const char *item = list; *item != '\0';) {
        const char *end = strchr(item, ':');
        const size_t item_length =
                end != NULL ? (size_t)(end - item) : strlen(item);
        if (item_length == base_length &&
            memcmp(item, base, base_length) == 0) {
            return 1;
        }
        if (end == NULL) break;
        item = end + 1;
    }
    return 0;
}

static int westlake_network_callsite_selected(void *return_address) {
    Dl_info info;
    if (return_address == NULL ||
        dladdr(return_address, &info) == 0 || info.dli_fname == NULL) {
        return 0;
    }
    return westlake_basename_in_list(
            info.dli_fname, getenv("WESTLAKE_ANDROID_NATIVE_NET_TARGETS"));
}

static int westlake_network_trace_callsite(void *return_address) {
    return westlake_network_trace_enabled() &&
           westlake_network_callsite_selected(return_address);
}

/* Android/Bionic and OH/musl expose the same sizeof(struct addrinfo), but the
 * two pointer members are reversed.  Bionic places ai_canonname before
 * ai_addr; OH musl places ai_addr before ai_canonname. */
struct westlake_bionic_addrinfo {
    int ai_flags;
    int ai_family;
    int ai_socktype;
    int ai_protocol;
    socklen_t ai_addrlen;
    char *ai_canonname;
    struct sockaddr *ai_addr;
    struct westlake_bionic_addrinfo *ai_next;
};

_Static_assert(sizeof(struct westlake_bionic_addrinfo) ==
                       sizeof(struct addrinfo),
               "Bionic and OH addrinfo sizes must match");
_Static_assert(offsetof(struct westlake_bionic_addrinfo, ai_canonname) ==
                       offsetof(struct addrinfo, ai_addr),
               "Bionic canonname must occupy OH addr slot");
_Static_assert(offsetof(struct westlake_bionic_addrinfo, ai_addr) ==
                       offsetof(struct addrinfo, ai_canonname),
               "Bionic addr must occupy OH canonname slot");

static void westlake_free_bionic_addrinfo(
        struct westlake_bionic_addrinfo *entry) {
    while (entry != NULL) {
        struct westlake_bionic_addrinfo *next = entry->ai_next;
        free(entry->ai_canonname);
        free(entry->ai_addr);
        free(entry);
        entry = next;
    }
}

static struct westlake_bionic_addrinfo *westlake_copy_bionic_addrinfo(
        const struct addrinfo *source) {
    struct westlake_bionic_addrinfo *head = NULL;
    struct westlake_bionic_addrinfo **tail = &head;
    for (; source != NULL; source = source->ai_next) {
        struct westlake_bionic_addrinfo *entry = calloc(1, sizeof(*entry));
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
    westlake_free_bionic_addrinfo(head);
    return NULL;
}

static void westlake_format_sockaddr(const struct sockaddr *address,
                                     socklen_t address_length,
                                     char *text,
                                     size_t text_length,
                                     unsigned int *port) {
    if (text_length != 0) text[0] = '\0';
    if (port != NULL) *port = 0;
    if (address == NULL) return;
    if (address->sa_family == AF_INET &&
        address_length >= (socklen_t)sizeof(struct sockaddr_in)) {
        const struct sockaddr_in *v4 = (const struct sockaddr_in *)address;
        inet_ntop(AF_INET, &v4->sin_addr, text, (socklen_t)text_length);
        if (port != NULL) *port = (unsigned int)ntohs(v4->sin_port);
    } else if (address->sa_family == AF_INET6 &&
               address_length >= (socklen_t)sizeof(struct sockaddr_in6)) {
        const struct sockaddr_in6 *v6 =
                (const struct sockaddr_in6 *)address;
        inet_ntop(AF_INET6, &v6->sin6_addr, text, (socklen_t)text_length);
        if (port != NULL) *port = (unsigned int)ntohs(v6->sin6_port);
    }
}

int getaddrinfo(const char *node, const char *service,
                const struct addrinfo *hints, struct addrinfo **result) {
    typedef int (*Fn)(const char *, const char *, const struct addrinfo *,
                      struct addrinfo **);
    static Fn real;
    if (real == NULL) real = (Fn)westlake_libc_symbol("getaddrinfo");
    if (real == NULL) return EAI_SYSTEM;
    const void *return_address = __builtin_return_address(0);
    const int selected = westlake_network_callsite_selected(
            (void *)return_address);
    const int trace = westlake_network_trace_enabled() && selected;
    struct addrinfo oh_hints;
    const struct addrinfo *real_hints = hints;
    struct addrinfo *oh_result = NULL;
    struct addrinfo **real_result = result;
    if (selected) {
        if (hints != NULL) {
            memset(&oh_hints, 0, sizeof(oh_hints));
            oh_hints.ai_flags = hints->ai_flags;
            oh_hints.ai_family = hints->ai_family;
            oh_hints.ai_socktype = hints->ai_socktype;
            oh_hints.ai_protocol = hints->ai_protocol;
            real_hints = &oh_hints;
        }
        real_result = &oh_result;
    }
    int rc = real(node, service, real_hints, real_result);
    if (trace) {
        fprintf(stderr,
                "[WESTLAKE-NATIVENET-767] getaddrinfo node=%s service=%s"
                " hints_family=%d hints_socktype=%d hints_protocol=%d"
                " hints_flags=0x%x rc=%d\n",
                node != NULL ? node : "(null)",
                service != NULL ? service : "(null)",
                hints != NULL ? hints->ai_family : -1,
                hints != NULL ? hints->ai_socktype : -1,
                hints != NULL ? hints->ai_protocol : -1,
                hints != NULL ? hints->ai_flags : 0, rc);
        if (rc == 0) {
            const struct addrinfo *trace_result =
                    selected ? oh_result : (result != NULL ? *result : NULL);
            int index = 0;
            for (const struct addrinfo *entry = trace_result;
                 entry != NULL && index < 16; entry = entry->ai_next, ++index) {
                char address[INET6_ADDRSTRLEN];
                unsigned int port;
                westlake_format_sockaddr(entry->ai_addr,
                                         (socklen_t)entry->ai_addrlen,
                                         address, sizeof(address), &port);
                fprintf(stderr,
                        "[WESTLAKE-NATIVENET-767] addr[%d] family=%d"
                        " socktype=%d protocol=%d flags=0x%x len=%u"
                        " address=%s port=%u\n",
                        index, entry->ai_family, entry->ai_socktype,
                        entry->ai_protocol, entry->ai_flags,
                        (unsigned int)entry->ai_addrlen,
                        address[0] != '\0' ? address : "?", port);
            }
        }
        fflush(stderr);
    }
    if (selected && rc == 0) {
        typedef void (*FreeFn)(struct addrinfo *);
        static FreeFn real_free;
        if (real_free == NULL) {
            real_free = (FreeFn)westlake_libc_symbol("freeaddrinfo");
        }
        struct westlake_bionic_addrinfo *bionic_result =
                westlake_copy_bionic_addrinfo(oh_result);
        if (real_free != NULL) real_free(oh_result);
        if (bionic_result == NULL && oh_result != NULL) {
            if (result != NULL) *result = NULL;
            return EAI_MEMORY;
        }
        if (result != NULL) {
            *result = (struct addrinfo *)bionic_result;
        } else {
            westlake_free_bionic_addrinfo(bionic_result);
            return EAI_FAIL;
        }
        if (trace) {
            fprintf(stderr,
                    "[WESTLAKE-NATIVEADDRINFO-769] translated OH addrinfo"
                    " to Bionic field order\n");
            fflush(stderr);
        }
    }
    return rc;
}

void freeaddrinfo(struct addrinfo *result) {
    typedef void (*Fn)(struct addrinfo *);
    if (westlake_network_callsite_selected(__builtin_return_address(0))) {
        westlake_free_bionic_addrinfo(
                (struct westlake_bionic_addrinfo *)result);
        return;
    }
    static Fn real;
    if (real == NULL) real = (Fn)westlake_libc_symbol("freeaddrinfo");
    if (real != NULL) real(result);
}

int socket(int domain, int type, int protocol) {
    typedef int (*Fn)(int, int, int);
    static Fn real;
    if (real == NULL) real = (Fn)westlake_libc_symbol("socket");
    if (real == NULL) {
        errno = ENOSYS;
        return -1;
    }
    int trace = westlake_network_trace_callsite(__builtin_return_address(0));
    int fd = real(domain, type, protocol);
    int saved_errno = errno;
    if (trace) {
        fprintf(stderr,
                "[WESTLAKE-NATIVENET-767] socket domain=%d type=0x%x"
                " protocol=%d result=%d errno=%d\n",
                domain, type, protocol, fd, fd < 0 ? saved_errno : 0);
        fflush(stderr);
    }
    errno = saved_errno;
    return fd;
}

int connect(int fd, const struct sockaddr *address,
            socklen_t address_length) {
    typedef int (*Fn)(int, const struct sockaddr *, socklen_t);
    static Fn real;
    if (real == NULL) real = (Fn)westlake_libc_symbol("connect");
    if (real == NULL) {
        errno = ENOSYS;
        return -1;
    }
    int trace = westlake_network_trace_callsite(__builtin_return_address(0));
    int rc = real(fd, address, address_length);
    int saved_errno = errno;
    if (trace) {
        char text[INET6_ADDRSTRLEN];
        unsigned int port;
        westlake_format_sockaddr(address, address_length, text, sizeof(text),
                                 &port);
        fprintf(stderr,
                "[WESTLAKE-NATIVENET-767] connect fd=%d family=%d len=%u"
                " address=%s port=%u result=%d errno=%d\n",
                fd, address != NULL ? address->sa_family : -1,
                (unsigned int)address_length,
                text[0] != '\0' ? text : "?", port, rc,
                rc < 0 ? saved_errno : 0);
        fflush(stderr);
    }
    errno = saved_errno;
    return rc;
}

int setsockopt(int fd, int level, int option, const void *value,
               socklen_t value_length) {
    typedef int (*Fn)(int, int, int, const void *, socklen_t);
    static Fn real;
    if (real == NULL) real = (Fn)westlake_libc_symbol("setsockopt");
    if (real == NULL) {
        errno = ENOSYS;
        return -1;
    }
    int trace = westlake_network_trace_callsite(__builtin_return_address(0));
    int rc = real(fd, level, option, value, value_length);
    int saved_errno = errno;
    if (trace) {
        fprintf(stderr,
                "[WESTLAKE-NATIVENET-767] setsockopt fd=%d level=%d"
                " option=%d len=%u result=%d errno=%d\n",
                fd, level, option, (unsigned int)value_length, rc,
                rc < 0 ? saved_errno : 0);
        fflush(stderr);
    }
    errno = saved_errno;
    return rc;
}

int getsockopt(int fd, int level, int option, void *value,
               socklen_t *value_length) {
    typedef int (*Fn)(int, int, int, void *, socklen_t *);
    static Fn real;
    if (real == NULL) real = (Fn)westlake_libc_symbol("getsockopt");
    if (real == NULL) {
        errno = ENOSYS;
        return -1;
    }
    int trace = westlake_network_trace_callsite(__builtin_return_address(0));
    int rc = real(fd, level, option, value, value_length);
    int saved_errno = errno;
    if (trace) {
        int int_value = 0;
        if (rc == 0 && value != NULL && value_length != NULL &&
            *value_length >= (socklen_t)sizeof(int)) {
            memcpy(&int_value, value, sizeof(int_value));
        }
        fprintf(stderr,
                "[WESTLAKE-NATIVENET-807] getsockopt fd=%d level=%d"
                " option=%d len=%u value=%d result=%d errno=%d\n",
                fd, level, option,
                value_length != NULL ? (unsigned int)*value_length : 0,
                int_value, rc, rc < 0 ? saved_errno : 0);
        fflush(stderr);
    }
    errno = saved_errno;
    return rc;
}

int epoll_create(int size) {
    typedef int (*Fn)(int);
    static Fn real;
    if (real == NULL) real = (Fn)westlake_libc_symbol("epoll_create");
    if (real == NULL) {
        errno = ENOSYS;
        return -1;
    }
    int trace = westlake_network_trace_callsite(__builtin_return_address(0));
    int rc = real(size);
    int saved_errno = errno;
    if (trace) {
        fprintf(stderr,
                "[WESTLAKE-NATIVENET-807] epoll_create size=%d result=%d"
                " errno=%d\n",
                size, rc, rc < 0 ? saved_errno : 0);
        fflush(stderr);
    }
    errno = saved_errno;
    return rc;
}

int epoll_ctl(int epoll_fd, int operation, int fd,
              struct epoll_event *event) {
    typedef int (*Fn)(int, int, int, struct epoll_event *);
    static Fn real;
    if (real == NULL) real = (Fn)westlake_libc_symbol("epoll_ctl");
    if (real == NULL) {
        errno = ENOSYS;
        return -1;
    }
    int trace = westlake_network_trace_callsite(__builtin_return_address(0));
    int rc = real(epoll_fd, operation, fd, event);
    int saved_errno = errno;
    if (trace) {
        fprintf(stderr,
                "[WESTLAKE-NATIVENET-807] epoll_ctl epfd=%d op=%d fd=%d"
                " events=0x%x data_fd=%d result=%d errno=%d\n",
                epoll_fd, operation, fd, event != NULL ? event->events : 0,
                event != NULL ? event->data.fd : -1, rc,
                rc < 0 ? saved_errno : 0);
        fflush(stderr);
    }
    errno = saved_errno;
    return rc;
}

int epoll_wait(int epoll_fd, struct epoll_event *events, int max_events,
               int timeout) {
    typedef int (*Fn)(int, struct epoll_event *, int, int);
    static Fn real;
    if (real == NULL) real = (Fn)westlake_libc_symbol("epoll_wait");
    if (real == NULL) {
        errno = ENOSYS;
        return -1;
    }
    int trace = westlake_network_trace_callsite(__builtin_return_address(0));
    int rc = real(epoll_fd, events, max_events, timeout);
    int saved_errno = errno;
    if (trace && (rc != 0 || saved_errno != 0)) {
        fprintf(stderr,
                "[WESTLAKE-NATIVENET-807] epoll_wait epfd=%d max=%d"
                " timeout=%d result=%d errno=%d\n",
                epoll_fd, max_events, timeout, rc,
                rc < 0 ? saved_errno : 0);
        const int limit = rc > 8 ? 8 : rc;
        for (int i = 0; i < limit; ++i) {
            fprintf(stderr,
                    "[WESTLAKE-NATIVENET-807] epoll_event[%d] events=0x%x"
                    " data_fd=%d data_u64=%llu\n",
                    i, events[i].events, events[i].data.fd,
                    (unsigned long long)events[i].data.u64);
        }
        fflush(stderr);
    }
    errno = saved_errno;
    return rc;
}

int poll(struct pollfd *fds, nfds_t count, int timeout) {
    typedef int (*Fn)(struct pollfd *, nfds_t, int);
    static Fn real;
    if (real == NULL) real = (Fn)westlake_libc_symbol("poll");
    if (real == NULL) {
        errno = ENOSYS;
        return -1;
    }
    int trace = westlake_network_trace_callsite(__builtin_return_address(0));
    int rc = real(fds, count, timeout);
    int saved_errno = errno;
    if (trace && (rc != 0 || saved_errno != 0)) {
        fprintf(stderr,
                "[WESTLAKE-NATIVENET-807] poll count=%llu timeout=%d"
                " result=%d errno=%d\n",
                (unsigned long long)count, timeout, rc,
                rc < 0 ? saved_errno : 0);
        fflush(stderr);
    }
    errno = saved_errno;
    return rc;
}
