/*
 * Minimal Android liblog ABI at the OpenHarmony process boundary.
 *
 * The bridge only needs these entry points to satisfy Android DSOs.  Log text
 * is kept observable by forwarding it to the inherited stderr stream.  The
 * logger configuration APIs are intentionally inert because OH does not use
 * Android's private logd transport or struct __android_log_message layout.
 */
#include <stdarg.h>
#include <stdio.h>
#include <stdlib.h>

struct __android_log_message;

int __android_log_print(int priority, const char *tag, const char *format, ...) {
    va_list args;
    fprintf(stderr, "[%d/%s] ", priority, tag != NULL ? tag : "?");
    va_start(args, format);
    vfprintf(stderr, format, args);
    va_end(args);
    fprintf(stderr, "\n");
    return 1;
}

int __android_log_write(int priority, const char *tag, const char *text) {
    fprintf(stderr, "[%d/%s] %s\n", priority, tag != NULL ? tag : "?",
            text != NULL ? text : "");
    return 1;
}

__attribute__((noreturn))
void __android_log_assert(const char *condition, const char *tag,
                          const char *format, ...) {
    (void)condition;
    fprintf(stderr, "ASSERT[%s] ", tag != NULL ? tag : "?");
    if (format != NULL) {
        va_list args;
        va_start(args, format);
        vfprintf(stderr, format, args);
        va_end(args);
    }
    fprintf(stderr, "\n");
    abort();
}

void __android_log_write_log_message(
        const struct __android_log_message *message) {
    (void)message;
}

void __android_log_logd_logger(const struct __android_log_message *message) {
    (void)message;
}

void __android_log_stderr_logger(const struct __android_log_message *message) {
    (void)message;
}

void __android_log_set_logger(
        void (*logger)(const struct __android_log_message *message)) {
    (void)logger;
}

void __android_log_set_aborter(void (*aborter)(const char *message)) {
    (void)aborter;
}

__attribute__((noreturn))
void __android_log_call_aborter(const char *message) {
    fprintf(stderr, "ABORT: %s\n", message != NULL ? message : "");
    abort();
}

__attribute__((noreturn))
void __android_log_default_aborter(const char *message) {
    fprintf(stderr, "ABORT: %s\n", message != NULL ? message : "");
    abort();
}

int __android_log_set_minimum_priority(int priority) {
    (void)priority;
    return 0;
}

int __android_log_get_minimum_priority(void) {
    return 0;
}

void __android_log_set_default_tag(const char *tag) {
    (void)tag;
}

/* libart's optional binder bootstrap import expects JNI_VERSION_1_6. */
int JNI_OnLoad_binder_with_cl(void *vm, void *class_loader) {
    (void)vm;
    (void)class_loader;
    return 0x00010006;
}
