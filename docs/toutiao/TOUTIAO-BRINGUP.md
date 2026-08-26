# Toutiao (今日头条 13.9.0) bring-up — work log + plan

Board: aarch64 OHOS 6.1.0.31 · libart `be828a5e` (shipped, JIT off) · imageless ART · musl.
Constraint honoured throughout: **no ART source changes**; every fix is a shim at an ABI boundary
(the "westlake principle"). Companion docs: `BIONIC-MUSL-PLAN.md`, memory
`toutiao-bringup-attempt-2026-08-14.md`.

---

## 0. Status in one line

The app launches, loads its 138 native libs, clears ByteDance's SafeMode + free-reflection layers,
and reaches real Application init — then **livelocks**. Two root causes are now **named and proven
with live measurements, and both are in OUR code, not upstream ART**:
1. our §440/§551 invoke-validator dereferences a **cross-dex** `ArtMethod` → SIGSEGV;
2. our musl sigchain shim **discards ART's `bool` claim-or-chain return** → the fault repeats forever
   instead of crashing.
Neither is fixed yet. `handleBindApplication` never returns, so there is no UI.

⛔**RETRACTED (2026-08-15):** an earlier revision of this document named
`ClassLinker::LinkMethodsHelper<k32>` / `image_pointer_size_` as the root cause. That was a
symbolization error (§4). It is wrong; do not build on it.

---

## 1. The APK

Monolithic **arm64-v8a**, 132 MB, `com.ss.android.article.news`, launch activity
`…activity.MainActivity`, targetSdk 30, **21 dex files**, **138 native `.so` (64 MB)**.
Application class = `…mute.MuteArticleApplicationStub` → a **Tinker** hot-patch stub that loads a
delegate app; **Mira** plugin framework; 15 declared process names.

**Download recipe (apkcombo)** — the page has no static link:
1. POST form (`package_name`, empty `version`) to `/<name>/<pkg>/<xid>/dl` (xid is in the page JS)
   → variant list.
2. Variant href `…/d?u=<base64>` decodes to a `download.pureapk.com` CDN URL.
3. Token from **POST** `apkcombo.com/checkin` → `fp=…&ip=…`.
4. Fetch the CDN URL with **no Referer** and `&fp=…&ip=…` appended. A Referer triggers a
   Cloudflare-403 bounce.

---

## 2. Walls cleared (all shims, no ART changes)

| # | Wall | Root cause | Fix |
|---|---|---|---|
| 1 | `UnsatisfiedLinkError` on every app native | 138 libs are **compressed inside the APK**; `nativeLibraryDir` was empty | extract all `.so` → `/data/local/tmp/asx/lib/arm64/` |
| 2 | libs still not found (bare-name `dlopen`) | app ClassLoader's search path lacked the dir | append dir to `LD_LIBRARY_PATH` in `run_tt.sh` |
| 3 | `FlippedV2Impl.getDeclaredMethod` JNI-unbound | ByteDance "Flipped" free-reflection native, registered from a Mira `.so` under non-standard mangling | **export the mangled symbol from the bridge**, implemented as plain JNI reflection |

★**Wall 2 ordering is load-bearing — a genuine bionic/musl collision.** Placing the app lib dir
*early* on `LD_LIBRARY_PATH` made the linker resolve **appspawn-x's own `libc++_shared.so` against
Toutiao's bionic copy** → `Error relocating appspawn-x: __thread_local_data / basic_string::append` →
**the zygote would not start**. App libs must go **LAST**, so system/bridge libs win every soname.

★**Wall 3 mechanism worth reusing:** ART resolves natives by **`dlsym`**, so a plain exported
`Java_…` symbol from the bridge wins — no `RegisterNatives`, no timing. Same pattern as
`memfd_create` (§611d). Flipped exists only to dodge hidden-API enforcement, which this imageless
port never implements, so plain reflection is a *complete* answer, not a stub.

---

## 3. The livelock — symptoms

- `handleBindApplication` never returns; no side-channels; no UI.
- Java `main` thread: **`nanosleep`** poll loop (not the Looper's `epoll_wait`).
- **Two threads spin in pure userspace** (`wchan=0`, `/proc/tid/syscall == "running"`), ~2 cores.
- ART daemons idle normally; no Java exception; no cross-process binder calls.
- stderr **byte-frozen** while CPU burns.

---

## 4. ROOT CAUSE (two defects, both ours)

### 4.0 First, the error that cost four rounds — and how it is caught
A runtime PC becomes a symbol in **two** steps, and I skipped the second:
```
file_off = pc − seg_base + seg_p_offset          # owning mapping in /proc/<pid>/maps
vaddr    = file_off + (p_vaddr − p_offset)       # owning PT_LOAD — nm/objdump index by VADDR
```
libart's exec segment: **`p_offset=0x6de000`, `p_vaddr=0x6df000` — a 0x1000 skew.**

| | address | symbol |
|---|---|---|
| reported before | `0x799e44` (file offset) | `LinkMethodsHelper<k32>::LinkMethods` +0x4178 |
| **correct** | **`0x79ae44`** (vaddr) | **`ArtMethod::GetNameView()` +0x40** |

One page off lands you in a *neighbouring, plausible-sounding* function with no error anywhere.
★**The check that catches it: can the instruction at your answer actually fault?** The retracted
answer blamed `add x10, x10, #1` — a register-only ADD can never SIGSEGV. codex flagged the same
contradiction independently. ✅Use `sym.py <same-run maps> <pc> <host.so>`, and capture the maps in
the **same run** as the PC (ASLR), **before** any ptrace.

### 4.1 Defect 1 — the fault: a cross-dex ArtMethod dereferenced by our own validator
```
  79ae44: ldr w9, [x10, x9, lsl #2]     ← FAULTING PC, ArtMethod::GetNameView()+0x40
          x10 = dex_file->string_ids_ , x9 = MethodId.name_idx_
  lr    = art::FindMethodToCall<(InvokeType)4 = kInterface>+0x730
```
Measured live (`pttrap2` for registers/siginfo, `memrd` for memory — no ptrace needed for the latter):

| quantity | value |
|---|---|
| `si_code` | 1 = `SEGV_MAPERR` |
| `si_addr` | **exactly** `x10 + x9*4`, on **both** spinning threads |
| `called_method` ArtMethod | `declaring_class_=0x14825400`, `access_flags_=0x1a300011`, `method_index_=13` |
| `called_method->dex_method_index_` | **62215** |
| that dex's `method_ids_size` (header @+88) | **41100** |
| resulting `name_idx` | **812,646,432** (other thread: 1,713,832,474) |
| that dex's `string_ids_size` (header @+56) | 39,578 |
| `expected_name` (read from memory) | `"tag"` |

**The method index is out of range for its own dex file.** `MethodId.name_idx_` is therefore read
from unrelated dex bytes, and the StringId load walks ~3 GB past the mapping.

★**The call site is a westlake patch, not upstream.** In `entrypoint_utils-inl.h`:
```cpp
const bool signature_mismatch =
    called_method->GetNameView() != expected_name ||      // ← faults here
    called_method->GetSignature() != expected_signature;
if (UNLIKELY(signature_mismatch)) { self->GetInterpreterCache()->Clear(self); ... }
```
That is the §440/§551 invoke-interface/proxy validator. **Upstream AOSP never calls `GetNameView()`
here.** Toutiao's 21 dex files plus Tinker/Mira multi-classloader are what make a cross-dex
`called_method` reachable at all; noice and the catalog never produce one.

### 4.2 Defect 2 — why it livelocks instead of crashing
`art-latest/stubs/sigchain_musl.cc`, `AddSpecialSignalHandlerFn()`:
```cpp
// sc_sigaction returns bool; the kernel ignores the return of an SA_SIGINFO
// handler, so casting to the void-returning type is safe in practice.   ← WRONG
act.sa_sigaction = reinterpret_cast<void (*)(int, siginfo_t*, void*)>(sca->sc_sigaction);
```
In AOSP sigchain that `bool` is the **claim-or-chain decision**, not a status code. ART's
FaultManager returns `false` for a PC inside native runtime code, meaning "not mine, pass it down".
Our shim discards it, the handler returns to the faulting context, and the instruction re-executes —
**forever**. This is a defect at an ABI boundary in our own shim, fixable without touching ART, and
it is very likely the mechanism behind the whole §436 "refault spin" family.

**Why the two together explain everything:** Defect 1 is upstream of all app code, which is why
removing five ByteDance subsystems changed nothing; Defect 2 makes the fault invisible, which is why
`CHILDSEGV` read **6** while the true rate was **~40/s**.

## 5. Instruments built (reusable)

| Tool | Where | Why it was needed |
|---|---|---|
| **Thread-dump hook** `wl_dump_threads_now` | bridge (`AndroidRuntime.cpp`), trigger `touch /data/local/tmp/asx/DUMPNOW` | `kill -3` yields **zero** bytes here. Calls exported `Runtime::Current` + `Runtime::DumpForSigQuit` via `dlsym`. ★Its **failure to return** was itself evidence. |
| **ptrace PC sampler** `ptsample.c` | `/data/local/tmp/ptsample <tid> <n>` | spinners block signals and never syscall ⇒ `/proc` and SIGPROF both blind. `PTRACE_ATTACH`+`GETREGSET`. |
| **Signal-delivery observer** `pttrap.c` | `/data/local/tmp/pttrap <tid> <secs>` | `PTRACE_SEIZE`+`CONT` **re-delivering** the signal. This is what proved SIGSEGV (not SIGTRAP) and gave the fault rate. |
| **★Observer v2** `pttrap2.c` | `/data/local/tmp/pttrap2 <tid> <secs> <maxprint>` | adds `si_code`, `si_addr` and **all 31 GPRs**. Callee-saved x19–x28 still hold the *caller's* locals ⇒ this is the rung that names the bug. |
| **★Live memory reader** `memrd.c` | `/data/local/tmp/memrd <pid> <hexaddr> <n>` | `pread` on `/proc/pid/mem`; root, **no ptrace attach, no perturbation**. Read the ArtMethod fields and the dex header that proved the index was out of range. |
| **★Symbolizer** `sym.py` | `python3 sym.py <maps> <pc> <host.so>` | does maps→file_off→PT_LOAD→**vaddr**→`st_size` containment. Written *because* the hand math was wrong four times. |

Cross-compile: `clang --target=aarch64-linux-ohos --sysroot=/home/dspfac/ohos-sdk-6.1/linux/native/sysroot`.

---

## 6. Traps that cost real time (record these)

1. **PC → symbol is TWO steps** (§4.0): owning mapping for the file offset, then the owning PT_LOAD's
   `p_vaddr − p_offset` delta. libart maps 4 segments and its exec segment carries a 0x1000 skew.
   Getting step 1 wrong symbolizes to obvious garbage; getting step 2 wrong symbolizes to a
   **plausible neighbouring function** — that one produced a fully-argued wrong root cause.
   ★Always ask whether the instruction you land on *can* fault.
2. **Device shell truncates large arithmetic**: `$((0x7f1dd94000/4096))` → `122260`, correct is
   `133291412`. A `dd skip=` built that way reads the wrong page and returns zeros — which reads
   exactly like "the lock was released". Compute host-side.
3. **Env vars do NOT reach appspawn-x children** — a `getenv()` feature gate never opens. Use a file
   trigger, or `setenv` inside `child_main`.
4. **`ptrace` perturbs the target**: attach/detach cycles left a wedged child with all threads in a
   traced state. Relaunch before re-measuring.
5. **No `tr`/`awk` on device**; libc++ includes must sit with the top-of-file block or the `std::__h`
   namespace setup breaks.
6. ⚠️**`hdc.exe file send` with a RELATIVE local path silently creates a DIRECTORY on the device**
   (`/data/local/tmp/asx/x.sh/bridge-build-arm64/x.sh`). `sh x.sh` then fails with "Is a directory"
   and the launcher prints **nothing at all**, which reads exactly like a dead board. Always pass a
   Windows UNC path: `'\\\\wsl.localhost\\Ubuntu-24.04\\home\\dspfac\\...\\x.sh'`.
7. **Capture `/proc/<pid>/maps` in the SAME run as the PC** and *before* any ptrace — ASLR makes a
   maps file from a previous launch silently wrong.

---

## 7. Hypotheses tested and RETIRED (do not re-run)

| # | Hypothesis | Killed by |
|---|---|---|
| 1 | §597-style suspend/fault storm | fault records were a different signature |
| 2 | "val=2 isn't musl's mutex protocol ⇒ ART lock" | control set: healthy ART daemons wait identically |
| 3 | NPTH RT signals vs musl sigchain | bisect: removed, livelock unchanged |
| 4 | Multi-process (15 processes declared) | log shows **0** `bindService`/`startService` calls |
| 5 | musl `__synccall` (from `setrlimit`) | `SigPnd`=0, `ShdPnd`=0; and musl tries `prlimit64` first |
| 6 | Stale `__thread_list_lock` from custom fork | **healthy zygote control shows the same dead-TID pattern** |
| 7 | `brk #1` RETGUARD re-trap in `__vsnprintf_chk` | signal-delivery observer: **0 SIGTRAP**, all SIGSEGV |
| 8 | ⛔**`LinkMethodsHelper<k32>` / wrong `image_pointer_size_`** (my own §630 answer) | 0x1000 `p_vaddr−p_offset` symbolization skew; correct symbol is `ArtMethod::GetNameView()`. Also independently impossible: `kRuntimeISA`=kArm64 is set at `runtime.cc:1596` *before* `InitWithoutImage` at 2024, and `kNone` aborts rather than falling back to k32 |

**Subsystems exonerated by controlled bisect:** NPTH, ByteHook, Godzilla, MetaSec — separately *and*
all together. ⚠️Design note: bisect arms #1/#2 each left a `setrlimit` importer present, so they did
not test what I claimed at the time. Check arms against the *mechanism*, not just the subsystem.

---

## 8. PLAN

### Step 1 — fix the sigchain trampoline (next action, ABI boundary, no ART change)
`stubs/sigchain_musl.cc` must install a **trampoline** that calls `sca->sc_sigaction(...)` and honours
the result: `true` = claimed, return; `false` = chain to the handler that was installed before ours
(OHOS libdfx's crash reporter), or `SIG_DFL` if there is none.
- ⚠️The 2 ms re-assert thread re-`sigaction`s constantly — it must **never** record our own trampoline
  as the "previous" handler.
- ⚠️Everything runs in a signal handler: async-signal-safe only.
- ★**Effect**: unclaimed faults become diagnosable crashes instead of silent hangs. That is strictly
  better information, but it *changes behaviour for every app*.
- ⛔**RUN THE CONTROL**: noice and the catalog must still launch and walk after this change.

### Step 2 — Defect 1: the cross-dex `called_method`
Two levels, cheapest first:
(a) guard the §551 validator so it cannot call `GetNameView()` when
    `dex_method_index_ >= dex_file->NumMethodIds()` — restores upstream behaviour (upstream does not
    validate here at all) rather than adding divergence;
(b) find *why* an interface dispatch for `"tag"` resolves to a method carrying another dex's index —
    that is the real defect, and (a) only stops it from being fatal.

### Step 3 — bypass Tinker
Add an **`ASX_APP_CLASS`** override to `AppSchedulerBridge.directLaunchNoBms` (which already sets
`ai.className`) so the delegate app loads directly instead of via `MuteApplication.load/miniLoad`.
Delegate class name is in `MuteArticleApplicationStub`'s ctor in `classes15.dex`.

### Step 4 — finish the Class-1 native sweep
`FileChannelImpl.position0` (lseek) and `FileObserver$ObserverThread.init` (inotify_init) — same
export-from-the-bridge pattern. Keva's `libkeva.so` self-registers once init progresses.

### Step 5 — multi-process
15 declared processes incl. Mira's `:stubp1/2/3`. Not blocking yet (0 cross-process calls observed).
When it bites: stub the cross-process paths, or teach appspawn-x to spawn siblings.

### Step 6 — bionic/musl heavy machinery, only if measured
Re-measure the **LP64** opaque-type table first (see `BIONIC-MUSL-PLAN.md` §2). On aarch64 the sizes
appear to match, which would make the interposer/linker-namespace options unnecessary.
⚠️Note this document previously listed a bionic/musl root cause as likely; the two proven defects are
**not** bionic/musl issues. The only confirmed bionic/musl collision remains the LD_LIBRARY_PATH
ordering in §2.

### Working rule
**Consult codex before every major fix.** It has now *three times* corrected conclusions I was about
to build on — most importantly here, it both flagged that `add` cannot fault (which unravelled the
wrong root cause) and located Defect 2 in our sigchain shim. It has twice corrected conclusions
(§588 ref-args; and here: `setrlimit`→`prlimit64` first, `DumpForSigQuit` uses `RunCheckpoint` +
buffers output so "0 bytes" proves nothing, and forwarding `_chk`→plain would disable FORTIFY).
Invocation: `codex exec --sandbox read-only --skip-git-repo-check` with the prompt on stdin; give it
setup + numbered evidence + constraint and ask it to **flag speculation vs certainty**.
⚠️Still verify its answers on the board.

---

## 9. Honest assessment

"Full function" (feed, video, login, push) is far beyond the current wall and depends on layers not
yet reached — `libsscronet` (Chromium net), the Lynx UI runtime, and the multi-process architecture.
Reaching **first render** is the realistic near-term goal, and it now has a named root cause standing
in front of it rather than an unknown.

---

# PLAN TO FIRST RENDER (rewritten 2026-08-16, after §631-§662)

Goal is **first render**, not a working Toutiao. Everything below is ordered by leverage, not by
where the current exception happens to be.

## Step 1 — Why is a CLASS LITERAL null?  (the current blocker, 1-2 runs)
`FragmentManagerViewModel.getInstance()` does `.get(FragmentManagerViewModel.class)` and gets
**null**. A `const-class` must yield a Class or throw; null is neither.
Decide between two very different causes before fixing anything:
 (a) **class resolution is being "tolerated"** — something swallows a resolution failure and returns
     null (`stubs/tolerant_native_util.h` is included by the compiled `patches/runtime/native/*.cc`);
 (b) the null arrives from a **stub return**, i.e. the §658 problem for `L`-typed returns.
★Cheapest discriminator: with `THROWTRACE` armed, log in the `const-class` / `ResolveType` path when
it returns null, printing the descriptor. If it names `androidx/fragment/app/FragmentManagerViewModel`,
it is (a).

## Step 2 — Stop stubs returning null (systematic; the highest-leverage item)
§658 proved the pattern for arrays (`null` → empty array) and unblocked `Application.onCreate`.
Extend the same principle to the rest of the ~425 `[STUB]` sites: a stub should return a **plausible
empty value**, never null, wherever the type admits one (empty array, empty String, empty
List/Map, 0/false). Null means "broken" to modern Android/Kotlin; empty means "nothing to give you".
⚠️Do this as a *typed* rule at the shim boundary, not by hand-editing 425 sites.

## Step 3 — Make the bind path complete (stop driving it by hand)
`ensureBindApplication` never calls `Instrumentation.callApplicationOnCreate` (§655) — today it only
works because `libwl636.so` invokes `Application.onCreate()` over JNI on a file trigger. Also missing:
`installContentProviders`. Land both in `AppSchedulerBridge`.
⛔BLOCKED: those classes live in `adapter-runtime-bcp.jar`, whose build chain needs an AOSP tree that
is not on this machine. **Unblock via dexlib2 surgery on the deployed jar** (the §464 framework.jar
toolchain does exactly this class of edit) — or restore an AOSP tree.

## Step 4 — First render
Only reachable once MainActivity.onCreate completes. The path is already proven for noice/catalog:
`side-channels started` → SceneBoard window adoption → `ViewRootImpl`/`relayout` → surface.
★Capture a KNOWN-GOOD noice child log first and diff its activity-launch markers against Toutiao's —
I have twice lacked that reference because the harness deleted it (now archived to `appspawnx/prev/`).

## Step 5 — Only after first render
`libsscronet` (Chromium net), the Lynx UI runtime, 15 declared processes, and JIT as a *performance*
question (restore §607 `-O0`, re-validate §605 — see the JIT memory; it is currently reverted).

## Cross-cutting, run in parallel (see PORTING-PLAYBOOK.md)
- **Tier 0 first**: keep un-gating dead diagnostics; two of them named two bugs in one run each.
- **Tier 2 on Toutiao**: `PatchLog.java` at every `MOVE_EXCEPTION` finds *swallowed* exceptions in
  obfuscated code — nothing else can.
- **Tier 1 differential**: instrumented noice on real Android vs the port ⇒ a map of shim gaps that
  is app-agnostic and directly feeds Step 2.
- **Fix the bridge build** (11/97 TUs fail and it links anyway) so bridge-side fixes stop needing the
  `libwl636.so` workaround.
- ⛔**Always run the noice/catalog control** after any runtime change, and **3 runs per verdict** —
  both the launch lottery and this NPE are intermittent (reproduced 2 of 3).
