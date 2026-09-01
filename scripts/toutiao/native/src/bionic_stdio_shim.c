/*
 * Namespace-local compatibility for Android's legacy __sF standard streams.
 *
 * Old bionic exposed stdin/stdout/stderr as three inline FILE objects in
 * __sF. OH musl uses pointer variables and a different private FILE layout.
 * Android code is allowed to pass &__sF[n] to stdio, but not inspect FILE's
 * opaque bytes. Translate those three sentinel addresses at each stdio entry
 * point and leave all ordinary FILE* values untouched.
 *
 * The appspawn launcher preloads this at the Android/OH process boundary.
 * Every ordinary OH FILE* is forwarded unchanged; only the three otherwise
 * invalid Android sentinel addresses are translated. Standalone A/B probes
 * cover both ordinary stderr and APK DSOs importing versioned __sF@LIBC.
 */
#include <dlfcn.h>
#include <stdarg.h>
#include <stddef.h>
#include <stdio.h>

typedef struct BionicLegacyFile {
    unsigned char opaque[152];
} BionicLegacyFile;

__attribute__((visibility("default"))) BionicLegacyFile __sF[3];

static FILE* westlake_translate_file(FILE* stream) {
    if ((void*)stream == (void*)&__sF[0]) return stdin;
    if ((void*)stream == (void*)&__sF[1]) return stdout;
    if ((void*)stream == (void*)&__sF[2]) return stderr;
    return stream;
}

int vfprintf(FILE* stream, const char* format, va_list args) {
    typedef int (*Fn)(FILE*, const char*, va_list);
    static Fn real;
    if (real == NULL) real = (Fn)dlsym(RTLD_NEXT, "vfprintf");
    return real != NULL ? real(westlake_translate_file(stream), format, args) : -1;
}

int fprintf(FILE* stream, const char* format, ...) {
    va_list args;
    va_start(args, format);
    int result = vfprintf(stream, format, args);
    va_end(args);
    return result;
}

size_t fread(void* buffer, size_t size, size_t count, FILE* stream) {
    typedef size_t (*Fn)(void*, size_t, size_t, FILE*);
    static Fn real;
    if (real == NULL) real = (Fn)dlsym(RTLD_NEXT, "fread");
    return real != NULL ? real(buffer, size, count, westlake_translate_file(stream)) : 0;
}

size_t fwrite(const void* buffer, size_t size, size_t count, FILE* stream) {
    typedef size_t (*Fn)(const void*, size_t, size_t, FILE*);
    static Fn real;
    if (real == NULL) real = (Fn)dlsym(RTLD_NEXT, "fwrite");
    return real != NULL ? real(buffer, size, count, westlake_translate_file(stream)) : 0;
}

int fflush(FILE* stream) {
    typedef int (*Fn)(FILE*);
    static Fn real;
    if (real == NULL) real = (Fn)dlsym(RTLD_NEXT, "fflush");
    return real != NULL ? real(westlake_translate_file(stream)) : EOF;
}

int fputc(int character, FILE* stream) {
    typedef int (*Fn)(int, FILE*);
    static Fn real;
    if (real == NULL) real = (Fn)dlsym(RTLD_NEXT, "fputc");
    return real != NULL ? real(character, westlake_translate_file(stream)) : EOF;
}

int fputs(const char* value, FILE* stream) {
    typedef int (*Fn)(const char*, FILE*);
    static Fn real;
    if (real == NULL) real = (Fn)dlsym(RTLD_NEXT, "fputs");
    return real != NULL ? real(value, westlake_translate_file(stream)) : EOF;
}

char* fgets(char* buffer, int size, FILE* stream) {
    typedef char* (*Fn)(char*, int, FILE*);
    static Fn real;
    if (real == NULL) real = (Fn)dlsym(RTLD_NEXT, "fgets");
    return real != NULL ? real(buffer, size, westlake_translate_file(stream)) : NULL;
}

int fseek(FILE* stream, long offset, int origin) {
    typedef int (*Fn)(FILE*, long, int);
    static Fn real;
    if (real == NULL) real = (Fn)dlsym(RTLD_NEXT, "fseek");
    return real != NULL ? real(westlake_translate_file(stream), offset, origin) : -1;
}

long ftell(FILE* stream) {
    typedef long (*Fn)(FILE*);
    static Fn real;
    if (real == NULL) real = (Fn)dlsym(RTLD_NEXT, "ftell");
    return real != NULL ? real(westlake_translate_file(stream)) : -1;
}

int feof(FILE* stream) {
    typedef int (*Fn)(FILE*);
    static Fn real;
    if (real == NULL) real = (Fn)dlsym(RTLD_NEXT, "feof");
    return real != NULL ? real(westlake_translate_file(stream)) : 0;
}

int ferror(FILE* stream) {
    typedef int (*Fn)(FILE*);
    static Fn real;
    if (real == NULL) real = (Fn)dlsym(RTLD_NEXT, "ferror");
    return real != NULL ? real(westlake_translate_file(stream)) : 1;
}

int fclose(FILE* stream) {
    typedef int (*Fn)(FILE*);
    static Fn real;
    if (real == NULL) real = (Fn)dlsym(RTLD_NEXT, "fclose");
    return real != NULL ? real(westlake_translate_file(stream)) : EOF;
}
