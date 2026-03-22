// art_gcc_compat.h - Pre-included header for GCC compatibility with ART
// This must be -include'd before any ART/AOSP headers

#ifndef ART_GCC_COMPAT_H
#define ART_GCC_COMPAT_H

// Force standard library headers that ART code assumes are available
#include <cstring>
#include <cstdlib>
#include <cmath>
#include <functional>

// ART frame size limit (used by instruction_set.cc)
#ifndef ART_FRAME_SIZE_LIMIT
#define ART_FRAME_SIZE_LIMIT 7400
#endif

// IMT (Interface Method Table) size (used by runtime headers)
#ifndef IMT_SIZE
#define IMT_SIZE 43
#endif

// For GCC compatibility with offsetof
#include <cstdint>

// HACK: Skip object_array-inl.h entirely.
// It contains a const-correctness violation that GCC rejects:
// ObjectArray::cend() const calls non-const Array::GetLength().
// The compiler code never uses cbegin/cend/ConstIterate.
// The object_array.h (non-inl) header still provides declarations.
#define ART_RUNTIME_MIRROR_OBJECT_ARRAY_INL_H_

// ART compact DEX level (needed by dex2oat)
#ifndef ART_DEFAULT_COMPACT_DEX_LEVEL
#define ART_DEFAULT_COMPACT_DEX_LEVEL fast
#endif

// Enable all code generators
#ifndef ART_ENABLE_CODEGEN_arm
#define ART_ENABLE_CODEGEN_arm
#endif
#ifndef ART_ENABLE_CODEGEN_arm64
#define ART_ENABLE_CODEGEN_arm64
#endif
#ifndef ART_ENABLE_CODEGEN_x86
#define ART_ENABLE_CODEGEN_x86
#endif
#ifndef ART_ENABLE_CODEGEN_x86_64
#define ART_ENABLE_CODEGEN_x86_64
#endif

// Ensure we're a host build - undefine any target/android macros
#ifdef ART_TARGET
#undef ART_TARGET
#endif
#ifdef ART_TARGET_ANDROID
#undef ART_TARGET_ANDROID
#endif
#ifdef __ANDROID__
#undef __ANDROID__
#endif
#ifdef ANDROID
#undef ANDROID
#endif

#endif  // ART_GCC_COMPAT_H
