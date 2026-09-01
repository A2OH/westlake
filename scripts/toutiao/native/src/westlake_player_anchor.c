/*
 * A load-group anchor has no behavior of its own.  Its ordered DT_NEEDED
 * entries keep Android ABI compatibility DSOs and the target APK DSO in one
 * OpenHarmony namespace load operation.
 */
#include <sys/socket.h>
#include <unistd.h>

__attribute__((visibility("default")))
int westlake_namespace_anchor(void) {
    return 0;
}

/* Diagnostic entry point used only by native_namespace_probe. */
__attribute__((visibility("default")))
int westlake_namespace_socket_probe(void) {
    int fd = socket(AF_INET, SOCK_STREAM, 0);
    if (fd >= 0) close(fd);
    return fd;
}
