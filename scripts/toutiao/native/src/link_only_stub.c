/*
 * Link-only input used to record an ordered DT_NEEDED entry in namespace
 * anchors.  This DSO is never staged or deployed; the real APK/platform DSO
 * with the same SONAME must be present in the target loader namespace.
 */
__attribute__((visibility("default")))
int westlake_link_only_stub(void) {
    return 0;
}
