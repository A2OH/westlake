# V3 — Bionic↔Musl ABI: Solved for UI Apps, Open for Prebuilt Native Engines

**Date:** 2026-06-24
**Status:** Updates and supersedes `V3-BIONIC-MUSL-ANALYSIS-2026-05-24.md` (HBC-vs-Alex peer review) and `V3-NDK-BIONIC-ABI-2026-05-27.md` (NDK L1/L2/L3 enumeration). Those remain valid for their corpus (McD/Netflix/TikTok/… production apps); this doc re-frames the gap around **two concrete practices we drove end-to-end since then** — the **Material Catalog** app (the *solved* case) and a real **Unity** game (the *open* case) — and adds the **detection methodology** (how to identify a Class-2 mismatch, statically and at runtime).
**Method:** All numbers below are *measured on this box*: bionic sizes from the NDK r25 sysroot (`.../sysroot/usr/include/bits/pthread_types.h`, `signal_types.h`, `setjmp.h`), musl sizes from the OHOS rk3568 sysroot (`out/rk3568/obj/third_party/musl/usr/include/arm-linux-ohos/bits/alltypes.h`), symbol surfaces via `nm -D`/`readelf` over the real `libunity.so` (extracted from `unity-apks/bt.apk`, `com.JCxYIS.UnityBluetooth`) and the OHOS musl `libc.so`. Device behavior from the deployed Catalog/Unity runs (see MEMORY START-HERE entries + `docs/engine/V3-CATALOG-*`).
**Target reminder:** we deploy **arm32 (lp32)** on rk3568. The 05-24/05-27 docs cited `aarch64` sizes (`pthread_mutex_t ≈ 40 B`); the *measured arm32* number is **24 B** (still a 6× mismatch). All tables below are arm32.

---

## §0. Executive answer (one page)

**The bionic↔musl ABI gap is SOLVED for the app class that dominates the store — Java/Kotlin UI apps — and remains OPEN for apps that ship heavy prebuilt bionic native engines.** Two practices prove each half:

- **SOLVED — Material Catalog (`io.material.catalog`).** A Material Design Java/Kotlin app. It renders end-to-end on OHOS: grid (L1) → demo activities (L2) → shared-element morph animating frame-by-frame (L3) → deep nav + Date Picker calendar (L4), with a real launcher logo+label. **Crucially, *none* of the bugs we fixed to get there were bionic↔musl libc problems** — they were all *above* the libc layer (hwui off-screen surface init, `ValueAnimator` duration-scale, ART vtable routing, launcher resource resolution). The libc boundary was already absorbed by the recompiled stack and never surfaced. That is what "solved" looks like in practice.

- **OPEN — Unity (`com.JCxYIS.UnityBluetooth`, real 17 MB `libunity.so`).** A prebuilt bionic native engine. It deterministically **deadlocks in a C++ static initializer on a `pthread_mutex`/`pthread_once`** during `dlopen`, on both Unity 2015 and 2023. Root cause is a **Class-2 struct-ABI mismatch** (`pthread_mutex_t` is 4 B in the bionic ABI Unity was compiled against, 24 B in OHOS musl) **in code we cannot recompile**. The recompile-to-musl strategy that solves Catalog *structurally cannot* reach it.

**Why the split is fundamental:** the gap is closed by **recompiling against musl** (`libbionic_compat` is a *link-time* bridge). That works for everything *we* build — ART, hwui, framework JNI — so the app's bytecode runs on a musl-native runtime and the gap disappears. It does nothing for an app's **own prebuilt bionic `.so`**: that binary froze bionic's struct layouts into its data and code, and now hands them to musl. The two strategies the 05-24 doc compared map directly onto the two fixes for this residue: **HBC in-process recompile** (solves Catalog; can't touch prebuilt) vs **Alex cross-process real-bionic isolation** (the only thing that fixes a Unity-class app, at ~300× per-call cost). A third, cheaper middle option — a **focused bionic-layout interposer** — is the lowest-risk next experiment for Unity specifically.

---

## §1. The ABI surface: three layers, three classes

The 05-27 doc decomposed the bionic ABI an APK's native code expects into three concentric layers. Our detection work added an orthogonal *severity* classification (Class-0/1/2). They line up:

| Layer (05-27 doc) | What it is | Our class | Detectable how |
|---|---|---|---|
| **L1 — bionic-the-libc** (2,033 syms; 554 bionic-only; ~50 practical) | bionic's libc/libm/libdl extensions musl lacks or renames | **Class-1** (bionic-only names) + **Class-0** (POSIX overlap, ~95%, safe) | ELF `.dynsym` − musl exports → **definitive** |
| **L2 — NDK higher-layer libs** (2,486 syms across 21 `.so`: libandroid, libmediandk, EGL/GLES, …) | `A*`-named NDK APIs (`ANativeWindow_*`, `__android_log_*`, `egl*`, …) | (framework surface, not a libc-ABI problem) | ELF DT_NEEDED + UND → adapter provides OH equivalents |
| **L3 — bionic linker/runtime behaviors** (~10 families: TLS slot-0, **`pthread_mutex_t` size**, FORTIFY, namespaces, …) | ABI-*shape* differences, not missing symbols | **Class-2** (same name, different struct layout/encoding) | ELF flags *candidates*; **runtime/DWARF confirms** |

The 05-27 doc called L3's `pthread_mutex_t` size mismatch **"the single biggest bionic-musl ABI hazard"** and **"the only L3 hazard that has actually bitten in production,"** but judged it **"mostly an AOSP-framework problem, not an app-NDK problem (apps almost universally use `pthread_mutex_init`)."** **Unity is the app-side exception that prediction missed** (§4).

---

## §2. The verified arm32 ABI size table (Class-2 made computable)

Class-2 = a function present in **both** libcs under the **same name** that operates on an **opaque struct whose size/layout/encoding differs**. It is computable from a size table. Both columns below are measured from headers on this box (bionic = NDK r25 lp32; musl = OHOS rk3568 lp32):

| opaque type | bionic (NDK r25) | musl (OHOS) | verdict |
|---|---|---|---|
| `pthread_mutex_t` | `int32[1]` = **4 B** | `int[6]` = **24 B** | **6× — DEADLY (the Unity wall)** |
| `pthread_cond_t` | `int32[1]` = **4 B** | `int[12]` = **48 B** | 12× — DEADLY |
| `sigset_t` | **4 B** (sigset64 = 8) | `ulong[32]` = **128 B** | 32× — DEADLY (sigaction/sigprocmask) |
| `pthread_rwlock_t` | `int32[10]` = **40 B** | `int[8]` = **32 B** | size mismatch |
| `pthread_barrier_t` | `int32[8]` = **32 B** | `int[5]` = **20 B** | size mismatch |
| `pthread_attr_t` | **24 B** | `int[9]` = **36 B** | size mismatch (`pthread_create`) |
| `jmp_buf` | `long[64]` = **256 B** | differs | size mismatch (`setjmp`/`longjmp`) |
| `pthread_once_t` | `int` = **4 B** | `int` = **4 B** | **same size, different sentinel encoding** → subtle |
| `pthread_key_t` | `int` = 4 B | `int` = 4 B | compatible |

**Two flavors of Class-2:**
1. **Size mismatch** (mutex/cond/sigset/…): the bionic binary reserved its smaller size; musl reads/writes its larger size → smashes adjacent fields **and** the internal futex/state word lands at the wrong offset → corruption + hang. **Statically detectable** (symbol → type → size delta).
2. **Same size, different encoding** (`pthread_once_t`, 4 == 4): no corruption, but the state word is interpreted under the wrong rules → mis-routing/deadlock. **Not** caught by size; needs semantic/runtime check.

> **Correction to the 05-24/05-27 docs:** they state musl `pthread_mutex_t ≈ 40 B` (the aarch64 value). On our arm32 target it is **24 B**. The mechanism and conclusion are unchanged; the magnitude is 6× rather than 10×.

---

## §3. How the gap is SOLVED for recompiled libs — the Catalog win

### §3.1 The mechanism (unchanged from HBC; restated precisely)
We do not run bionic binaries. We **cross-compile the entire AOSP native stack against the OHOS musl sysroot** (`build/inner/cross_compile_arm32.sh`): OH Clang `--target=arm-linux-ohos`, musl sysroot, then bridge the residue:
- **`libbionic_compat.so`** — bionic→musl bridge, **DT_NEEDED-linked** (`-lbionic_compat`) into every AOSP lib (NOT `LD_PRELOAD`). ~33 functions: `__system_property_*` (8) → OH `SystemReadParam`; `__android_log_*` (via one `__android_log_set_logger` registration) → HiLog; `android_fdsan_*`/`android_mallopt`/`android_dlwarning` stubs; bionic compiler libcalls (`__sync_*`, `adler32`, `__memcmp16`).
- **libc++ compat** — force-included `libcxx_compat.h` (`nullptr_t`, `std::__promote`, math macros) + `-isystem libcxx_array_aosp` (fixes OHOS libc++ `std::array<T,0>::data()` ABI bug for ART), `-fno-rtti`.
- **link/ABI fixups** — `libsigchain.so` binary-patched (`libc_musl`→`libc`), `-Wl,-Bsymbolic` for ART, TLS slot-0 handled in ART asm (`TLS_SLOT_ART_THREAD_SELF=7`).

### §3.2 Why Class-2 vanishes when you recompile
Recompiling the AOSP libs against musl makes their `sizeof(pthread_mutex_t)` = **24** everywhere — internally consistent. A Class-2 mismatch can only occur **at a boundary between a bionic-compiled lib and a musl-compiled lib that disagree on size.** When *every* lib in the process is musl-built, there is no such boundary. The app's Java/Kotlin runs on the musl-native ART; its UI renders through musl-built hwui/Skia; the libc boundary is crossed only by code we compiled. **The gap is absorbed once, at build time.**

### §3.3 Catalog as proof
`io.material.catalog` (deployed APK + adapter) renders the full demo tree on OHOS:

| Catalog level | Status | The bug we fixed to get there | Layer of that bug |
|---|---|---|---|
| L1 grid | ✅ | — | — |
| L2 demo activities | ✅ | `createHardwareBitmap` read an uninitialized `ANativeWindow` → SIGBUS on RenderThread (libhwui `0c82b1db` + bridge `20ab65a6`) | **hwui / surface** — not libc |
| L3 container-transform morph | ✅ animates | `ValueAnimator.sDurationScale==0` disabled all animation; `setDurationScale(1.0f)` | **framework animator** — not libc |
| L4 deep nav + **Date Picker calendar** | ✅ | libart W9 vtable-routing skipped for `super_vtable_length>500` (deep `GridView` hierarchy) → raised gate (libart `275eb104`) | **ART vtable** — not libc |
| Launcher logo + label | ✅ | adapter never built the bundle's `entry.hap` → resourceManager couldn't resolve icon/label | **launcher resource** — not libc |

**The signal is the absence:** every Catalog wall was *above* the libc boundary. The bionic↔musl layer never produced a Catalog bug, because it was already solved by §3.1–§3.2. This is the empirical confirmation that recompile-to-musl closes the gap for Java/Kotlin UI apps.

---

## §4. Why prebuilt native engines still break — the Unity wall

### §4.1 The subject is real (not a mockup)
`libunity.so` extracted from `unity-apks/bt.apk`: 17 MB ELF32/ARM `DYN`, with Unity's internal **GfxDevice GL-backend table** baked in (`<OpenGL ES 2.0>`…`<OpenGL 4.5>`), `.comment` = real **Android NDK clang** (8.0.2 / 9.0.8, `r339409`/`r365631`), and the host APK carries the full **Unity Mono managed tree** (`assets/bin/Data/Managed/BluetoothUnity.dll`, `Unity.2D.*.dll`, `Mono.Posix.dll`, …). Genuine Unity engine.

### §4.2 DEX is blind; the ELF is the surface
The bionic↔musl conflict lives in **native** code, not the DEX:
- `classes.dex` = **126 KB** husk (only the Java↔native boundary: `nativeRender`, `nativeSurfaceTextureReady`, `loadLibrary("main")`). Useful for framework/JNI shim coverage; **blind** to libc ABI.
- `libunity.so` = 17 MB of engine. `nm -D -u` = **370** external (UND) symbols — a small surface for 17 MB (the "many internal calls, few external" pattern). Blind spots even in the ELF: `dlopen`/`dlsym` (present → runtime string resolution), inline `svc` (Go/anti-cheat/static-libc — none found in libunity, but the system `objdump` can't even decode ARM here, so treat as best-effort).

### §4.3 The classification, computed by set-difference
`U` = libunity UND (370). `M` = OHOS musl `libc.so` exports (1,901). With the §2 table:

- **Class-1 (definitive, `U \ M`)** — split into:
  - *L2/framework* (adapter provides OH equivalents): `ANativeWindow_*`, `ALooper_*`, `ASensor*`, `egl*`, `__android_log_*`, `inflate*`.
  - *true bionic-libc*: `__errno` (musl: `__errno_location`), `__sF`, `__assert2`, `__system_property_*`, `pthread_cond_timedwait_relative_np`. Small, shimmable.
- **Class-2 candidates (`U ∩ M ∩ table`)**: `pthread_mutex_lock/init/destroy/trylock`, `pthread_mutexattr_settype`, `pthread_cond_*`, `pthread_once`, `sem_*`, `setjmp/longjmp`, `sigaction`, `sigaltstack`. These link silently against musl — `readelf -r` shows `R_ARM_JUMP_SLOT pthread_mutex_lock@LIBC` etc., proving the bind to musl (the `@LIBC` version musl ignores).

### §4.4 The mechanism and root cause
The crash stack is `libunity` static-init → `__pthread_mutex_timedlock` via `do_init_fini` ← `dlopen` ← `libmain` ← `System.loadLibrary`. Reading it against §2:
- `pthread_mutex_t` is **4 B** in Unity's bionic ABI, **24 B** in musl. libunity embeds a mutex in one of its own globals (53,265 `R_ARM_RELATIVE` relocs = a lot of global state), reserving 4 B; musl's `pthread_mutex_lock` touches 24 B, so the futex word lands ~20 B past where bionic put it → the lock never acquires → single-threaded static-init hang.
- **Refinement:** libunity does **not** import `__cxa_guard_acquire` (only `__cxa_atexit`), so this is **not** the compiler's C++ static-init guard — it is libunity's **own explicit `pthread_once`/`pthread_mutex`** in a global constructor. (`pthread_once` is the *same-size/different-encoding* Class-2 subtype — equally fatal, invisible to size checks.)
- **Version-independent:** Unity 2015 and 2023 both hang → not a version bug; a structural ABI mismatch.

### §4.5 Why recompile-to-musl can't help
§3.2's fix requires recompiling the lib so its structs become musl-sized. **We cannot recompile a prebuilt `libunity.so`** (no source). So its bionic-4-byte mutexes meet musl's 24-byte implementation with no boundary to eliminate. This is exactly the residue the 05-24 doc said only Alex's approach covers.

---

## §5. Identifying a Class-2 mismatch — can it be done without running the app?

**Substantially yes, for the common cases; a bounded residue needs a run (or sidestep via §6 Option B).** Static depth ladder, cheapest → deepest, with what `libunity` showed:

- **T0 — import × size table** → the *suspect list* (necessary condition). 100% static. (libunity: the §4.3 Class-2 set.)
- **T1 — relocation anchors + global sizing.** `readelf -r` proves the musl bind (`R_ARM_JUMP_SLOT … @LIBC`). For a **global/static** lock the compiler reserved bionic's bytes in `.bss`; read `st_size` if symbols exist, else bound by the gap via disasm. **Static-only win:** a `PTHREAD_MUTEX_INITIALIZER` global is 4 zero bytes with *no init call* — invisible to a runtime `pthread_mutex_init` hook but plainly a 4-byte object where musl needs 24.
- **T2 — DWARF / `abidiff` (gold standard, when symbols exist).** If the `.so` (or a companion `.sym`) ships `.debug_info`, it records the exact compiled `sizeof(pthread_mutex_t)=4` → compare to musl 24 → **definitive, automated, no run.** `abidiff` (libabigail) mechanizes this against the musl libc ABI. **This is the high-leverage path for customer NDK libs**, which often *do* ship symbolized/debug builds. (libunity is **stripped** — `.debug` sections: 0 — so T2 is N/A for it; T3 needed.)
- **T3 — disassembly xref (Ghidra/IDA/angr).** For a stripped release lib, trace each Class-2 call's pointer arg (`r0`) to its origin: a `.bss` global (reserved size), a `malloc(N)`/`operator new(N)` (read `N`), or a stack frame (`sub sp,#N`). High-confidence, fully static, real-tool work (note: stock `objdump` here can't decode ARM).

**The honest static limit (needs runtime, or §6-B):**
1. **indirect/data-dependent pointer origins** (virtual dispatch / computed indirection static data-flow can't resolve);
2. **same-size/different-encoding** (`pthread_once_t` — live in libunity);
3. **reachability** (static finds the embedding, not whether the device executes it; though "embedded + handed to musl anywhere" usually suffices to fail it).

**Runtime confirmation (when needed):** LD_PRELOAD interpose the Class-2 functions + fingerprints — (a) `__pthread_mutex_timedlock` on an **uncontended, freshly-init'd, single-threaded** lock in static init → ABI not logic; (b) **works on bionic, hangs only on musl, same binary** (Unity 2015+2023 → confirmed); (c) **canary** past musl's `sizeof` clobbers the app's adjacent field → proven undersize; (d) bogus mutex *kind* readback → wrong offset.

**Verdict table:**

| | enumerate from ELF? | classify with confidence from ELF? |
|---|---|---|
| **Class-1** | yes | **definitive** (in musl exports or not) |
| **Class-2** | yes | **candidate only** — confirm via DWARF/`abidiff` (static) or runtime fingerprint |

---

## §6. Fix paths for prebuilt bionic `.so`

The 05-24 doc's two camps are exactly the two fixes for this residue; a cheaper middle option exists.

- **Option A — focused bionic-layout pthread/sync interposer (NEW, lowest-risk).** Preload a shim ahead of musl that re-implements *only* the ABI-sensitive surface (`pthread_mutex/cond/once/key`, `sem_*`, `setjmp`, signals) using **bionic's struct layouts**, backed by **raw `futex`/`clone` syscalls** (kernel ABI is stable and shared by both libcs). Bounded and buildable. **Constraint:** such an object must never cross to musl-side OHOS code (true for Unity's internal static-init locks). `pthread_create` is the risky member (new thread runs musl TLS). The *detector* in §5 and this *fix* are the same LD_PRELOAD hook. **Recommended next experiment for Unity.**
- **Option B — real bionic in a linker namespace (HBC's "Alex" approach / ARC++/Houdini/Waydroid).** Give the app's native libs their own namespace where `libc.so/libm.so/libdl.so/libc++_shared.so` resolve to an **actual bionic**, which talks to the OHOS kernel via syscalls. Class-2 vanishes by construction. Cost moves to the JNI/EGL/ANativeWindow boundary, which must pass only C-ABI handles (never a libc struct). The robust, general answer.
- **Option not-applicable — recompile (HBC's in-process model).** Solves Catalog; cannot touch a prebuilt `.so`.

**Performance context (from 05-24 §5):** in-process shim ≈ **1 µs/call**; cross-process NP-TLV ≈ **288 µs/call** (~300×). So Option B is correct but expensive; Option A keeps the prebuilt lib in-process and pays only the interposer's ~ns–µs per sync call — *if* the cross-boundary constraint holds.

---

## §7. Per-app practice summary

| App | Type | bionic↔musl status | Why |
|---|---|---|---|
| **Material Catalog** | Java/Kotlin UI (Skia) | ✅ **SOLVED** | runs on recompiled-to-musl ART+hwui; app ships no heavy bionic native code; all walls were above libc |
| **Unity (`com.JCxYIS.UnityBluetooth`)** | prebuilt bionic native engine | ❌ **OPEN** | 17 MB `libunity.so` deadlocks on a Class-2 `pthread_mutex`/`pthread_once` in static init; cannot be recompiled |

**Generalization:** an app's bionic↔musl risk ∝ (how much **prebuilt bionic native code** it ships) × (whether that code **embeds sync/signal structs** and operates on them via libc). Pure Java/Kotlin → ~0 risk (Catalog). Thin NDK glue using only `pthread_mutex_init` on heap objects it over-allocates → low. Heavy engines with global/static locks (Unity, some games, anti-cheat) → high, and unsolvable by recompile.

---

## §8. A no-board static portability gate (actionable)

The §5 method packages into a single pre-screen (no device, no execution):

```
APK → per-.so:
  1. DT_NEEDED + nm -D -u            → external surface
  2. (U \ musl_exports)              → Class-1: split L2-framework vs true-libc   [DEFINITIVE]
  3. (U ∩ musl_exports ∩ size-table) → Class-2 candidates                          [FLAG]
  4. readelf -S → DWARF? → abidiff vs musl libc  → Class-2 VERDICT where symbols exist
  5. flag blind spots: dlopen/dlsym strings, inline svc, static libc
  6. emit an LD_PRELOAD detector stub for the Class-2 hits (doubles as Option-A skeleton)
```

Decides statically: **all of Class-1**, and **Class-2 wherever DWARF/symbols exist**. Flags for runtime: stripped + indirect-pointer + `once`-encoding cases. This turns "will app X survive bionic→musl on OHOS" into a CI-able score before anyone touches a board.

---

## §9. Cross-references & evidence

| Source | Relevance |
|---|---|
| `V3-BIONIC-MUSL-ANALYSIS-2026-05-24.md` | HBC (recompile, ~1 µs) vs Alex (chroot+NP, ~288 µs) peer review; §3.3 ABI-shape analysis. Basis for §6. |
| `V3-NDK-BIONIC-ABI-2026-05-27.md` | L1/L2/L3 enumeration (2,033 / 2,486 / ~10); per-app NDK-dep map. Basis for §1. |
| `V3-BIONIC-COUNT-2026-05-27.md` | Raw bionic-libc symbol enumeration (554 bionic-only / ~50 practical). |
| `docs/en/application-dev/reference/native-lib/musl*.md` | OHOS official: supported musl symbols + peculiar/permission-controlled symbols. |
| `docs/engine/V3-CATALOG-L2FIX-EVIDENCE/`, `…-L3-MORPH-EVIDENCE/`, `…-LAUNCHER-ICON-EVIDENCE/` | Catalog render evidence (§3.3). |
| Memory: `bionic-musl-class2-abi-detection.md` | Detection method + verified table; this doc's §2/§5 source. |

**Re-runnable spot checks** (host-side, no board):
```bash
# verified size table inputs
sed -n '60,90p' $NDK/sysroot/usr/include/bits/pthread_types.h          # bionic
grep -nE 'pthread_(mutex|cond)_t|sigset_t' $OH_MUSL/bits/alltypes.h     # musl
# libunity surface + classification
nm -D -u libunity.so | awk '$1=="U"{print $2}' | sed 's/@.*//' | sort -u > U
nm -D --defined-only $OH_MUSL_LIBC | awk '{print $NF}' | sort -u        > M
comm -23 U M    # Class-1 (filter L2-framework from true-libc)
readelf -r libunity.so | grep -E 'pthread_(mutex|once|cond)'            # JUMP_SLOT → musl bind
readelf -S libunity.so | grep -c '\.debug'                             # 0 = stripped → T3 not T2
```

---

## §10. Headline answers (restated)

- **Is bionic↔musl solved?** **For Java/Kotlin UI apps, yes** — recompile-to-musl + `libbionic_compat` absorbs it (Catalog renders L1–L4 with zero libc-layer walls). **For prebuilt bionic native engines, no.**
- **What breaks, exactly?** **Class-2** struct-ABI mismatches — chiefly `pthread_mutex_t` (bionic 4 B → musl 24 B on arm32) — in code we can't recompile. Unity's static-init `pthread_mutex`/`pthread_once` deadlock is the concrete instance of the hazard the 05-27 doc flagged as "mostly a framework problem"; Unity is the app exception.
- **Can you detect it without running?** **Mostly.** Definitive for Class-1, and for Class-2 when DWARF/symbols exist (`abidiff`); for stripped libs, static gets you to "provably corrupting" for global locks + a high-confidence suspect list, with indirect/`once`-encoding/reachability needing a runtime fingerprint.
- **How would you fix Unity-class apps?** **Option A** (focused bionic-layout sync interposer on raw syscalls — cheapest, in-process, recommended next) or **Option B** (real bionic in a linker namespace — robust, ~300× per-call cost at the boundary). Recompile cannot reach them.
