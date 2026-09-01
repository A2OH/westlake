/* Android OpenSL ES extension identifiers on the OpenHarmony audio boundary. */
#include <dlfcn.h>
#include <stdint.h>

typedef struct SlInterfaceId {
    uint32_t time_low;
    uint16_t time_mid;
    uint16_t time_high_and_version;
    uint16_t clock_sequence;
    uint8_t node[6];
} SlInterfaceId;

typedef const SlInterfaceId* SlInterfaceIdPtr;

static const SlInterfaceId kAndroidConfiguration = {
    0x89f6a7e0, 0xbeac, 0x11df, 0x8b5c,
    {0x00, 0x02, 0xa5, 0xd5, 0xc5, 0x1b},
};
static const SlInterfaceId kAndroidSimpleBufferQueue = {
    0x198e4940, 0xc5d7, 0x11df, 0xa2a6,
    {0x00, 0x02, 0xa5, 0xd5, 0xc5, 0x1b},
};

/* ELF data symbols: Android clients load the pointer stored in each one. */
__attribute__((visibility("default")))
SlInterfaceIdPtr SL_IID_ANDROIDCONFIGURATION = &kAndroidConfiguration;

__attribute__((visibility("default")))
SlInterfaceIdPtr SL_IID_ANDROIDSIMPLEBUFFERQUEUE = &kAndroidSimpleBufferQueue;

__attribute__((constructor))
static void westlake_map_simple_buffer_queue(void) {
    /* OH implements the standard buffer queue and compares IID pointers.
     * Android's simple queue is API-compatible for PCM playback, so use the
     * exact IID pointer exported by OH when it is present. */
    SlInterfaceIdPtr* oh_buffer_queue =
            (SlInterfaceIdPtr*)dlsym(RTLD_DEFAULT, "SL_IID_BUFFERQUEUE");
    if (oh_buffer_queue != 0 && *oh_buffer_queue != 0) {
        SL_IID_ANDROIDSIMPLEBUFFERQUEUE = *oh_buffer_queue;
    }
}
