# BUILD_PLAN.md — M6 `westlake-surface-daemon` cross-compile

**Status:** Step 1 done (skeleton); Step 2 TODO (ISurfaceComposer dispatch)
**Companion to:** [`docs/engine/M6_SURFACE_DAEMON_PLAN.md`](../docs/engine/M6_SURFACE_DAEMON_PLAN.md), [`docs/engine/M6_STEP1_REPORT.md`](../docs/engine/M6_STEP1_REPORT.md)
**Reference pattern:** [`aosp-libbinder-port/BUILD_PLAN.md`](../aosp-libbinder-port/BUILD_PLAN.md) (M1 scoping doc)

This document tracks the build scaffold for `westlake-surface-daemon` —
the daemon that registers with our M2 servicemanager as `"SurfaceFlinger"`
and implements the AOSP `ISurfaceComposer` Binder service contract.

---

## 1. Source identification

### 1.1 Targets

- **Build target:** bionic-arm64 (Android NDK r25; matches dalvikvm + libbinder.so deployment on the OnePlus 6 `cfb7c9e3`).
- **Phase 1 deployment:** statically link against
  `aosp-libbinder-port/out/bionic/libbinder.so` (M3-stable substrate).
- **Phase 2 deployment (M12+):** rebuild OHOS-musl variant in `out/`
  alongside the bionic variant — same pattern as aosp-libbinder-port.

### 1.2 Source layout (Step 1 baseline)

```
aosp-surface-daemon-port/
├── native/
│   ├── surfaceflinger_main.cpp      — main() process pattern
│   ├── WestlakeSurfaceComposer.h    — BBinder subclass header
│   └── WestlakeSurfaceComposer.cpp  — Step 1 stub
└── (future Step 2+):
    ├── deps-src/
    │   └── ui-stubs/
    │       └── GraphicBuffer-memfd.cpp  — see CR33 spike §5
    ├── native/
    │   ├── BufferQueueCore.cpp          — producer-side BQ
    │   ├── IGraphicBufferProducer-Bn.cpp — Bn dispatch
    │   └── ComposerHostBackend.cpp      — DLST pipe writer (Phase 1)
    └── aosp-src/                        — vendored libgui / libui sources
```

### 1.3 External dependencies

**Reused from aosp-libbinder-port (NO rebuild):**

| Artifact | Source path | Role |
|----------|-------------|------|
| `libbinder.so` | `../aosp-libbinder-port/out/bionic/libbinder.so` | Binder runtime |
| `libutils_static.a` | `../aosp-libbinder-port/out/bionic/libutils_static.a` | RefBase / String16 / Threads |
| `libcutils_static.a` | `../aosp-libbinder-port/out/bionic/libcutils_static.a` | native_handle / ashmem |
| `libbase_static.a` | `../aosp-libbinder-port/out/bionic/libbase_static.a` | strings / logging-stub / properties-stub |
| AIDL-generated includes | `../aosp-libbinder-port/out/aidl-gen/include` | IServiceManager.h etc. |
| Headers | `../aosp-libbinder-port/aosp-src/libbinder/include` | binder/IPCThreadState.h etc. |

**To-be-vendored (Step 2+):**

- `frameworks/native/libs/gui/include/gui/ISurfaceComposer.h` and `.cpp` (the Android-11 reference at `$HOME/aosp-android-11/`; ~609 + 2300 LOC)
- `frameworks/native/libs/gui/include/gui/IGraphicBufferProducer.h` and `.cpp` (~681 + minimal Bn dispatch)
- `frameworks/native/libs/ui/include/ui/GraphicBuffer.h` (header only; replace `.cpp` with our memfd substitute per CR33 §5)

---

## 2. Build toolchain (Step 1)

Mirrors `aosp-libbinder-port/Makefile` bionic targets verbatim:

| Component | Path |
|-----------|------|
| NDK | `$HOME/android-sdk/ndk/25.2.9519653/toolchains/llvm/prebuilt/linux-x86_64` |
| `clang++` | `${NDK}/bin/clang++` |
| `llvm-strip` | `${NDK}/bin/llvm-strip` |
| Sysroot | `${NDK}/sysroot` |
| Target | `aarch64-linux-android33` |

Flags:
- `-U__ANDROID__` — matches the `BIONIC_DEFINES` path libbinder uses to disable vintf/perfetto/selinux pulls
- `-fno-rtti -fno-exceptions -fvisibility=hidden` — match libbinder.so's hidden-vtable ABI
- `-static-libstdc++` — no libc++_shared.so dependency on the phone
- `-D_GNU_SOURCE` — needed by libbinder headers (e.g., `posix_strerror_r`)

---

## 3. Acceptance criteria

| Step | Acceptance |
|------|-----------|
| Step 1 (done) | Daemon binary builds; `addService("SurfaceFlinger", ...)` returns NO_ERROR on cfb7c9e3; `sm_smoke listServices` includes the name; daemon blocks on `joinThreadPool`. |
| Step 2 | Daemon dispatches at least the 4 Tier-1 ISurfaceComposer methods (`GET_DISPLAY_INFO`, `GET_DISPLAY_CONFIGS`, `GET_PHYSICAL_DISPLAY_TOKEN`, `GET_DISPLAY_STATS`) with canned answers; harness queries via `IBinder::transact` and verifies non-null `reply`. |
| Step 3 | `CREATE_CONNECTION` returns an `ISurfaceComposerClient` Bn-side stub; `createSurface` returns an `IGraphicBufferProducer` Bn that wraps an in-daemon BufferQueueCore. |
| Step 4 | `dequeueBuffer/requestBuffer/queueBuffer` round-trip works in a 2-process harness (parent registers daemon, child issues full BQ cycle). |
| Step 5 | DLST pipe backend forwards queued buffers to Compose host SurfaceView (replaces today's `WestlakeRenderer` writer; see plan §3.6). |
| Step 6 | noice-discover advances past `Activity.getWindow()` NPE — current PHASE G4 blocker per `PHASE_1_STATUS.md` CR30-A. |

---

## 4. Build rules

| Target | Command | Output |
|--------|---------|--------|
| `all` | `bash build.sh` | `out/bionic/surfaceflinger` (37 KB stripped, 53 KB unstripped) |
| `clean` | `bash build.sh clean` | removes `out/` |

The Makefile depends on `../aosp-libbinder-port/out/bionic/libbinder.so`
existing.  `build.sh` invokes `aosp-libbinder-port/build.sh bionic` if not.

---

## 5. Risks / open questions

| ID | Risk | Disposition |
|----|------|-------------|
| R1 | HWUI memfd-coherency on this kernel | **RETIRED** by CR33 spike 2026-05-13 (all 4 phases PASS, see `CR33_M6_SPIKE_REPORT.md`) |
| R2 | ISurfaceComposer AIDL transaction codes Android-11 ↔ Android-16 drift | Open; CR35 will publish discovered codes from real `framework.jar` reflection.  Step 2 is gated on CR35. |
| R3 | DLST pipe magic compatibility w/ existing `WestlakeRenderer` Compose host | Open; existing pipe uses `0x444C5354` magic (per `WestlakeVM.kt`).  Step 5 reuses unchanged. |
| R4 | `getInterfaceDescriptor()` mismatch w/ framework.jar's `ISurfaceComposer.DESCRIPTOR` | Verified Android-11 string is literally `"android.ui.ISurfaceComposer"`; Step 1 daemon uses that.  Step 2 will re-verify against shipped framework.jar via reflection. |
