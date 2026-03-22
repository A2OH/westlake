// link_stubs.cc - Stub implementations for external C library symbols
// that dex2oat needs but we don't have in our partial build.
// ART runtime C++ symbols are handled via --unresolved-symbols=ignore-in-object-files
// which makes them resolve to nullptr (crashing only if actually called).

#include <cstdint>
#include <cstdlib>
#include <cstdio>
#include <cstring>
#include <iostream>

extern "C" {

// === liblog ===
// __android_log_buf_print is the main logging entry point for logd
#include <stdarg.h>
int __android_log_buf_print(int, int priority, const char* tag, const char* fmt, ...) {
    va_list ap;
    va_start(ap, fmt);
    fprintf(stderr, "%s: ", tag ? tag : "dex2oat");
    vfprintf(stderr, fmt, ap);
    fprintf(stderr, "\n");
    va_end(ap);
    return 0;
}
int __android_log_is_loggable(int, const char*, int) { return 1; }
void __android_log_set_logger(void*) {}
void __android_log_write_log_message(void*) {}
void __android_log_logd_logger(void*) {}
void __android_log_stderr_logger(void*) {}
void __android_log_set_aborter(void*) {}
void __android_log_call_aborter(const char* msg) { fprintf(stderr, "ABORT: %s\n", msg); abort(); }
void __android_log_default_aborter(const char* msg) { fprintf(stderr, "ABORT: %s\n", msg); abort(); }
int __android_log_set_minimum_priority(int p) { return p; }
int __android_log_get_minimum_priority() { return 0; }
void __android_log_set_default_tag(const char*) {}

// === LZ4 ===
int LZ4_compressBound(int inputSize) { return inputSize + inputSize / 255 + 16; }
int LZ4_compress_default(const char* src, char* dst, int srcSize, int dstCapacity) {
    if (srcSize > dstCapacity) return 0;
    memcpy(dst, src, srcSize);
    return srcSize;
}
int LZ4_compress_HC(const char* src, char* dst, int srcSize, int dstCapacity, int) {
    return LZ4_compress_default(src, dst, srcSize, dstCapacity);
}
int LZ4_decompress_safe(const char* src, char* dst, int compressedSize, int dstCapacity) {
    if (compressedSize > dstCapacity) return -1;
    memcpy(dst, src, compressedSize);
    return compressedSize;
}

// === libziparchive ===
struct ZipArchive;
struct ZipEntry {
    uint32_t uncompressed_length;
    uint32_t compressed_length;
    uint32_t crc32;
    uint64_t offset;
};

int OpenArchive(const char*, ZipArchive**) { return -1; }
int OpenArchiveFd(int, const char*, ZipArchive**, bool) { return -1; }
int OpenArchiveFromMemory(const void*, unsigned long, const char*, ZipArchive**) { return -1; }
void CloseArchive(ZipArchive*) {}
// FindEntry takes string_view in AOSP 11
int FindEntry(ZipArchive*, const void*, ZipEntry*) { return -1; }
const char* ErrorCodeString(int) { return "stub-ziparchive"; }
int ExtractToMemory(ZipArchive*, ZipEntry*, unsigned char*, unsigned int) { return -1; }
int ExtractEntryToFile(ZipArchive*, ZipEntry*, int) { return -1; }
int GetFileDescriptor(ZipArchive*) { return -1; }

// === ART palette ===
int PaletteTraceEnabled(bool* enabled) { if (enabled) *enabled = false; return 0; }
int PaletteTraceBegin(const char*) { return 0; }
int PaletteTraceEnd() { return 0; }
int PaletteTraceIntegerValue(const char*, int) { return 0; }

}  // extern "C"
