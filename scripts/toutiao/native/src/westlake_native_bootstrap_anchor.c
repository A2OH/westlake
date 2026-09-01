#define _GNU_SOURCE
/*
 * Generic load-group bootstrap for Android-native namespace compatibility.
 *
 * Some OH loader versions do not place a DSO inherited from the parent ahead
 * of the inherited libc when resolving a later APK DSO.  This tiny anchor is
 * opened as the first Android-native load group.  Its ordered DT_NEEDED list
 * can therefore install an ABI-boundary DSO in the child namespace before the
 * real APK library.  Once loaded, the launcher-selected environment values
 * switch the ordinary anchor configuration back for subsequent libraries.
 *
 * This file deliberately names neither an app nor an APK library.  The link
 * inputs and both next-anchor values are deployment policy.
 */
#include <stdio.h>
#include <stdlib.h>

__attribute__((constructor))
static void westlake_android_namespace_bootstrap(void) {
    const char *next_target =
            getenv("WESTLAKE_ANDROID_NATIVE_NEXT_ANCHOR_TARGET");
    const char *next_anchor =
            getenv("WESTLAKE_ANDROID_NATIVE_NEXT_ANCHOR");
    int target_rc = 0;
    int anchor_rc = 0;
    if (next_target != NULL && next_target[0] != '\0') {
        target_rc = setenv("WESTLAKE_ANDROID_NATIVE_ANCHOR_TARGET",
                           next_target, 1);
    }
    if (next_anchor != NULL && next_anchor[0] != '\0') {
        anchor_rc = setenv("WESTLAKE_ANDROID_NATIVE_ANCHOR",
                           next_anchor, 1);
    }
    fprintf(stderr,
            "[WESTLAKE-NATIVENS-772] next_target=%s target_rc=%d"
            " next_anchor=%s anchor_rc=%d\n",
            next_target != NULL ? next_target : "(null)", target_rc,
            next_anchor != NULL ? next_anchor : "(null)", anchor_rc);
    fflush(stderr);
}

__attribute__((visibility("default")))
int westlake_android_namespace_bootstrap_anchor(void) {
    return 0;
}
