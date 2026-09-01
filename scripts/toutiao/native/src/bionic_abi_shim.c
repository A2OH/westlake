/*
 * Android bionic assertion ABI on the OpenHarmony libc boundary.
 *
 * Bionic exports __assert2(file, line, function, expression), while OH's musl
 * libc exposes a different assertion entry point. Android NDK shared objects
 * may legitimately import __assert2 even when no assertion ever fires.
 */
#include <stdio.h>
#include <stdlib.h>
#include <dlfcn.h>
#include <fcntl.h>
#include <locale.h>
#include <errno.h>
#include <string.h>
#include <unistd.h>

extern int* __errno_location(void);

#define WESTLAKE_ANDROID_PROP_VALUE_MAX 92

/* Bionic's errno accessor name differs from musl's, but both return the
 * calling thread's errno slot. */
int* __errno(void) {
    return __errno_location();
}

/* Android's fortified two-argument open. Missing a creation mode is a caller
 * contract violation in bionic, so retain the fail-fast behavior. */
int __open_2(const char* path, int flags) {
    int needs_mode = (flags & O_CREAT) != 0;
#ifdef O_TMPFILE
    needs_mode = needs_mode || ((flags & O_TMPFILE) == O_TMPFILE);
#endif
    if (needs_mode) {
        fprintf(stderr,
                "[WESTLAKE-BIONIC-ABI] __open_2 called with creation flags 0x%x\n",
                flags);
        fflush(stderr);
        abort();
    }
    return open(path, flags);
}

/* Android's legacy property read API, backed by OH's parameter service. */
int __system_property_get(const char* name, char* value) {
    typedef int (*ReadParamFn)(const char*, char*, unsigned int*);
    static ReadParamFn read_param;
    static void* beget_handle;
    if (name == NULL || value == NULL) return 0;
    if (read_param == NULL) {
        read_param = (ReadParamFn)dlsym(RTLD_DEFAULT, "SystemReadParam");
        if (read_param == NULL) {
            beget_handle = dlopen("libbegetutil.z.so", RTLD_NOW | RTLD_LOCAL);
            if (beget_handle != NULL) {
                read_param = (ReadParamFn)dlsym(beget_handle, "SystemReadParam");
            }
        }
    }
    value[0] = '\0';
    if (read_param == NULL) return 0;
    unsigned int length = WESTLAKE_ANDROID_PROP_VALUE_MAX;
    if (read_param(name, value, &length) != 0) {
        value[0] = '\0';
        return 0;
    }
    value[WESTLAKE_ANDROID_PROP_VALUE_MAX - 1] = '\0';
    return (int)strlen(value);
}

/* Integer parsing is locale-independent in bionic too; its locale entry
 * points are direct forwards to the ordinary functions. */
long long strtoll_l(const char* value, char** end, int base, locale_t locale) {
    (void)locale;
    return strtoll(value, end, base);
}

unsigned long long strtoull_l(
        const char* value, char** end, int base, locale_t locale) {
    (void)locale;
    return strtoull(value, end, base);
}

/* Bionic's source-bounds-aware fortified strncpy variant.  OH musl already
 * supplies __strncpy_chk (destination bound only), but Android NDK code may
 * emit this five-argument entry point as well. */
char* __strncpy_chk2(char* dst, const char* src, size_t count,
                     size_t dst_size, size_t src_size) {
    if (count > dst_size) {
        fprintf(stderr,
                "[WESTLAKE-BIONIC-ABI] strncpy write of %zu bytes exceeds"
                " %zu-byte destination\n",
                count, dst_size);
        fflush(stderr);
        abort();
    }
    if (count != 0) {
        char* output = dst;
        const char* input = src;
        do {
            if ((size_t)(input - src) >= src_size) {
                fprintf(stderr,
                        "[WESTLAKE-BIONIC-ABI] strncpy read exceeds"
                        " %zu-byte source\n",
                        src_size);
                fflush(stderr);
                abort();
            }
            if ((*output++ = *input++) == '\0') {
                while (--count != 0) *output++ = '\0';
                break;
            }
        } while (--count != 0);
    }
    return dst;
}

/* Bionic exposes the first abort reason to its crash reporter. OH does not
 * consume bionic's private mapping format, so preserve the first-message rule
 * and route the text to the inherited stderr crash log. */
void android_set_abort_message(const char* message) {
    static int message_recorded;
    if (message != NULL &&
        __atomic_exchange_n(&message_recorded, 1, __ATOMIC_ACQ_REL) == 0) {
        fprintf(stderr, "[WESTLAKE-BIONIC-ABI] abort message: %s\n", message);
        fflush(stderr);
    }
}

/* WESTLAKE §755 (2026-08-20): match AOSP bionic's fail-fast contract. */
__attribute__((noreturn, visibility("default")))
void __assert2(const char* file, int line, const char* function,
               const char* failed_expression) {
    fprintf(stderr, "%s:%d: %s: assertion \"%s\" failed\n",
            file ? file : "?", line, function ? function : "?",
            failed_expression ? failed_expression : "?");
    fflush(stderr);
    abort();
}
