# V3 W2 Phase 1b.2 — Retry after 3 side-fixes — REPORT

**Date:** 2026-05-19
**Operator:** agent 74
**Board:** DAYU200 (`dd011a414436314130101250040eac00`)
**Base commit:** `1f789703` (3 side-fixes by operator)
**Predecessor:** `36717af8` agent 73 + `docs/engine/V3-W2-PHASE-1B-PROGRESS.md`
**Status:** **BLOCKED** — substrate-level investigation required

---

## TL;DR (5 bullets)

1. **All 3 side-fixes confirmed deployed on board.** `liboh_adapter_bridge.so`
   present at both `/system/lib/...` (underneath RO bind-mount) and
   `/system/android/lib/...`; `fonts.xml` present at both `/system/etc/fonts.xml`
   and `/system/android/etc/fonts.xml`; `combo.sh` has the extended
   `LD_LIBRARY_PATH` with `chipset-sdk` segment.

2. **liboh_android_runtime.so NOW LOADS successfully** (was previously
   chain-failing per agent 73 baseline). Hilog confirms `Framework JNI methods
   registered via AndroidRuntime::startReg`, then full enumeration of real
   `android.view.Surface::*`, `android.graphics.BLASTBufferQueue::*`,
   `android.graphics.FontFamily::nInitBuilder/nAddFont/...`,
   `android.graphics.fonts.Font$Builder::nBuild` etc. via the OH_RegHook
   trace. Side-fix #1 is doing what it should.

3. **Daemon now SEGVs (SIGSEGV signo 11, si_code 1 = SEGV_MAPERR) during
   `AppSpawnXInit.preload()`** — exactly the failure mode agent 73 warned
   about. Crash happens ~12ms after `Pre-init AssetManager.sSystem` log
   completes; daemon dies before `Phase 4: Listening on socket` is reached;
   `connect: Connection refused` from test_tlv_client (the stale
   `/dev/unix/socket/AppSpawnX` from before makes the combo's "socket present"
   probe falsely positive). No spawn round-trip possible. **Phase 1b.1
   REGRESSED** in 1b.2-retry configuration (was PASS on baseline; broken by
   side-fix #1 enabling fuller liboh_android_runtime load).

4. **Root cause precisely localized: AOSP Typeface.<clinit> setSystemFontMap
   path is hit and tries to load `Roboto-Regular.ttf` via libhwui native font
   loader.** Smoking-gun hilog line at 15:32:07.746:
   `I C00f00/Typeface: Preloading /system/fonts/Roboto-Regular.ttf` —
   followed immediately by `MUSL-SIGCHAIN signal_chain_handler` and
   `DfxSignalHandler signo(11)` at 15:32:07.757. Bind-mounting host's
   `/system/fonts/` into chroot (so Roboto-Regular.ttf IS present) does NOT
   prevent the SEGV — same crash pattern, same timing. The SEGV is INSIDE
   libhwui's font parsing/loading code (likely libft2 / libminikin path
   triggered by `nAddFont` or `nInitBuilder`), not at the `fopen` step.

5. **DFX could not produce a backtrace** — `start processdump fail dummy
   exit with error(2)` because the chroot doesn't have processdump binary
   reachable (factory OH `processdump` lives at `/system/bin/` on the host's
   mmcblk0p7, which IS bind-mounted but processdump itself needs additional
   .so chain unavailable in chroot). Without backtrace, we cannot confirm
   which libhwui sub-function crashes, only that the trigger is the
   Typeface.preload chain after liboh_android_runtime.so binds the real
   AOSP-native font JNI methods. **STOPPED per hard-stop #11 (substrate
   investigation required); not hacking around.**

---

## 1. Side-fix verification on board (Step 2 of brief)

| Fix | Expected | Actual | Result |
|-----|----------|--------|--------|
| 1. liboh_adapter_bridge.so dual-path | `/system/lib/liboh_adapter_bridge.so` AND `/system/android/lib/liboh_adapter_bridge.so` both present, identical 1,569,240 bytes | `/system/lib/...` already underneath RO bind-mount (pre-existing from Phase 1a). `/system/android/lib/...` pushed this run (size 1,569,240 = match). | **PASS** |
| 2. fonts.xml dual-path | `/system/etc/fonts.xml` AND `/system/android/etc/fonts.xml` both present | Both present, 4235 bytes each | **PASS** |
| 3. LD_LIBRARY_PATH adds `/system/lib/chipset-sdk` | `combo.sh` exports the new path | Verified in `combo.sh` line 3: `export LD_LIBRARY_PATH=/system/lib:/system/android/lib:/system/lib/platformsdk:/system/lib/chipset-sdk:/system/lib/chipset-sdk-sp:/system/lib/ndk` | **PASS** |

**Note:** Full deploy script re-push (Step 1 of brief) was NOT run because
the script would attempt `_push_one /system/lib/liboh_adapter_bridge.so`
which is RO bind-mounted from host (push would fail with EROFS). Instead,
the only file that was actually missing — `/system/android/lib/liboh_adapter_bridge.so`
— was pushed directly via `hdc file send`. All other deploy artifacts from
Phase 1a (107 files, ~9k bytes of libs) remain intact from the prior run.

---

## 2. Daemon launch + crash (Step 3 of brief)

### combo.sh run sequence

```bash
hdc shell 'cd /data/local/tmp/v3-hbc-chroot && chroot . /system/bin/sh /combo.sh > combo-1b2-retry-2.log 2>&1'
```

### combo log (truncated)

```
[combo] starting appspawn-x daemon
[combo] daemon pid=20332
[combo] socket present after 0*500ms        ← stale socket from prior run
[combo] running test_tlv_client
connect: Connection refused                  ← daemon already dead
[combo] test_tlv_client rc=1
...
/combo.sh[45]: kill: 20332: No such process  ← confirmed dead
[combo] DONE
```

### Hilog progress (AppSpawnX tag only, run pid 20332 / 20423)

```
... appspawn-x hybrid spawner starting ...
Phase 1: Initializing OH security modules...      ✓
Listen socket ready (manual): fd=4 path=/dev/unix/socket/AppSpawnX  ✓
Phase 2: Initializing Android Runtime (ART VM)... ✓
Registering framework JNI native methods           ✓
Framework JNI methods registered via AndroidRuntime::startReg  ✓ ← NEW (was failing)
JNI registration complete                          ✓
AppSpawnXInit loaded via PathClassLoader           ✓
Cached PathClassLoader + loadClass for child reuse ✓
ART VM initialization complete                     ✓
Calling AppSpawnXInit.preload()...                 ✓
=== Preloading Android framework ===                ✓
JCA providers overridden for OH (dropped 4 original, installed BC + CertPath)  ✓
JCA active providers: [BC=1.68] [CertPathProvider=1.0]                          ✓
BC: registered MessageDigest MD5/SHA-1/-224/-256/-384/-512                       ✓
X.509 CertificateFactory self-test OK provider=BC                              ✓
SHA-1 MessageDigest OK provider=BC                                              ✓
Loaded real framework-res ApkAssets from /system/android/framework/framework-res.apk ✓
Pre-init AssetManager.sSystem (sentinel=...) — real framework-res loaded         ✓ ←LAST AppSpawnXInit log
                                                                                  ↓ 12ms gap
[Typeface tag]: Preloading /system/fonts/Roboto-Regular.ttf                     ← AOSP Typeface.<clinit>!
[MUSL-SIGCHAIN tag]: signal_chain_handler call 3 rd sigchain ... FREEZE_signo_11 ← about to crash
DfxSignalHandler :: signo(11), si_code(1), pid(20332), tid(20332).               ← SEGV_MAPERR
dummy exit with error(2)  start processdump fail                                 ← no backtrace
Finish handle signal(11) in 20332:20332.
DfxSignalHandler :: signo(11), si_code(0), ...                                   ← rethrown
```

### Liboh_adapter_bridge.so chain-load: SUCCESS

Sample of newly visible JNI registrations (from `OH_RegHook` log) proving
the side-fix unblocked the full library chain:

| Class | Native methods registered | From .so |
|-------|---------------------------|----------|
| `android.view.Surface` | nativeDestroy, nativeLockCanvas, nativeUnlockCanvasAndPost, nativeGetWidth, nativeGetHeight, nativeSetFrameRate, nativeAttachAndQueueBufferWithColorSpace, ... (~17 methods) | `/system/android/lib/liboh_android_runtime.so` |
| `android.graphics.BLASTBufferQueue` | nativeCreate, nativeDestroy, nativeGetSurface, nativeSetNextTransaction, nativeUpdate, nativeSetSyncTransaction, nativeMergeWithNextTransaction, ... (~10 methods) | `/system/android/lib/liboh_android_runtime.so` |
| `android.graphics.FontFamily` | nInitBuilder, nAddFont, nAddFontWeightStyle | `/system/android/lib/libhwui.so` |
| `android.graphics.fonts.Font$Builder` | nBuild | `/system/android/lib/libhwui.so` |
| `android.graphics.MeshSpecification` | nativeMake, nativeMakeWithCS, nativeMakeWithAlpha | `/system/android/lib/libhwui.so` |
| `android.graphics.ImageDecoder` | nGetMimeType | `/system/android/lib/liboh_android_runtime.so` |

Pre-fix: `liboh_android_runtime.so` chain-failed → only "basic VM"; many
of the above classes had stub Java fallbacks. Post-fix: real C++ native
code bound to all of these → AOSP's Typeface.<clinit> can now make real
font calls instead of being deflected by NoSuchMethodError. **The fix is
working as intended — it's just exposing a deeper issue.**

---

## 3. Root cause localization (Step 4a diagnostics)

### Smoking-gun hilog line

```
08-05 15:32:07.746 20423 20423 I C00f00/Typeface: Preloading /system/fonts/Roboto-Regular.ttf
08-05 15:32:07.757 20423 20423 W C03f07/MUSL-SIGCHAIN: signal_chain_handler call 3 rd sigchain action for signal: 11 sca_sigaction=f6e45f09
08-05 15:32:07.758 20423 20423 I C02d11/DfxSignalHandler: DFX_SignalHandler :: signo(11), si_code(1) ...
```

Tag `C00f00/Typeface` = AOSP framework's `android.graphics.Typeface` class.
Line "Preloading /system/fonts/Roboto-Regular.ttf" is AOSP's standard
`Typeface.preloadOne(String path)` log. This is **NOT** in HBC's `AppSpawnXInit`
— it's the AOSP `Typeface.<clinit>` chain calling `Typeface.create(File, int)`
which then calls native `nGetWeightStyle(JlongPointer)` etc. on returned
handle.

### Diagnostic checks performed

| Check | Method | Result |
|-------|--------|--------|
| Does the crash happen at `fopen("Roboto-Regular.ttf")`? | Bind-mounted host's `/system/fonts/` into chroot (which has Roboto-Regular.ttf) and re-ran | **NO change** — same SEGV at same timing |
| Could fonts.xml itself be malformed? | `xmllint`-compatible parse of fonts.xml | Clean (10 family entries, all referenced TTF files exist in host `/system/fonts/`) |
| Are libft2/libharfbuzz_ng/libminikin in chroot? | `ls /data/local/tmp/v3-hbc-chroot/system/android/lib/` | YES (all 41 AOSP libs deployed by agent 71/72) |
| Backtrace from processdump | `find /data/log/faultlog -newer combo.sh -name "cppcrash*"` | **NONE** — `dummy exit with error(2)`, processdump not reachable from chroot |
| Crash in main thread? | `tid(20332) == pid(20332)` in DfxSignalHandler log | YES — single-threaded crash in the daemon main thread, not a worker |
| Stale socket faking "ready"? | `ls -la /dev/unix/socket/AppSpawnX` | YES — leftover from prior runs; pkill doesn't unlink. combo.sh treats it as ready and proceeds with `connect`, which then sees daemon gone. Cosmetic — not root cause. |

### Why moving `/system/fonts` into chroot didn't help

Two non-exclusive theories (next agent to disambiguate):

1. **The native font parser SEGVs even with file present** — likely libft2
   or libminikin path. Possibilities: TTF binary parse buffer overrun, OOM
   on first font cache allocation, NULL deref on a NULL FT_Library handle.
   The native code may assume Android-style font loading where some
   bookkeeping is pre-initialized by `app_main` / Zygote startup that
   AppSpawnX doesn't replicate.

2. **`Typeface.setSystemFontMap()` invokes native `nativeCreateFromTypeface_redirect`
   or similar that traverses `sFontMap` which is null in OH** — AOSP
   framework.jar's `Typeface` static fields are pre-baked at AOT compile
   time and may have `null` values that don't get re-populated when running
   inside HBC.

Without a backtrace, can't distinguish (1) from (2). Both require either:
- Working `processdump` inside chroot (substrate work)
- LiveDebugd / GDB attach to live daemon (manual operator step)
- Recompile HBC's `appspawn-x` with `Typeface.setSystemFontMap` reflectively
  short-circuited (substrate fork — forbidden)

### Hard-stop check

Per brief hard-stop #11: "Substrate-level fix needed (e.g., a missing .so
we need to source from elsewhere, or a typeface init bug deep in libart)
→ STOP + report, do not invent fixes". This is exactly that situation:
the crash is in libhwui native code at libft2/libminikin layer; fixing it
requires either a substrate-level patch or substrate-level diagnostic
infrastructure (processdump in chroot). Neither is in this brief's scope.

**STOPPED. Did not attempt:**
- Patching `AppSpawnXInit.preload` to swallow native crashes (impossible —
  SIGSEGV not catchable as Java exception)
- Patching AOSP `Typeface.<clinit>` to skip setSystemFontMap (substrate fork)
- Recompiling libhwui with a Typeface stub (substrate fork)
- Stubbing `nInitBuilder` / `nAddFont` JNI methods at runtime (substrate
  fork; also impossible without JVMTI)

---

## 4. Acceptance vs. brief

| Brief acceptance criterion | Met? |
|----------------------------|------|
| All 3 side-fixes verified on board | YES |
| liboh_android_runtime.so loads (no chain failure) | YES |
| No preInitTypefaceDefault SEGV during preload | **NO — SEGV in Typeface preload chain (substrate-level)** |
| Child process reaches `Log.i` successfully (no UnsatisfiedLinkError) | N/A — no child ever spawned because daemon died before Phase 4 |
| ActivityThread.main + Looper.loop reached | N/A — no child ever spawned |

**Verdict: BLOCKED.** The 3 side-fixes are correct and necessary; they
unblocked Phase 2 (liboh_android_runtime.so loads). But they also enable
the Typeface.<clinit> code path which triggers a native-side SEGV inside
libhwui/libft2/libminikin. The crash is below the Java level (uncatchable
by `catch (Throwable)` in `AppSpawnXInit.preload()`), and processdump
cannot run inside the chroot to give a backtrace. Substrate-level
investigation needed.

---

## 5. Comparison: agent 73 baseline vs. this run

| State | Phase 1b.1 (spawn round-trip) | Phase 1b.2 (Java in child) |
|-------|-------------------------------|------------------------------|
| Agent 73 baseline (no side-fixes) | PASS (`result=0 pid=<child>`) | PARTIAL — child Java entry reached, Log.i UnsatisfiedLinkError |
| Agent 74 (side-fixes applied) | **REGRESSED — daemon dies before Phase 4; "connect: Connection refused"** | N/A (no child ever spawned) |

Phase 1b.1 regression is because the side-fix that enables full
`liboh_android_runtime.so` loading also exposes the Typeface.<clinit>
SEGV — a deeper substrate gap that agent 73's baseline avoided by
keeping the runtime in "basic VM only" mode. **The side-fix is the right
direction but reveals work needed in the layer it unblocks.**

To recover Phase 1b.1 PASS:
- Either remove `liboh_adapter_bridge.so` from `/system/android/lib/`
  (reverts to agent 73 baseline)
- OR fix the underlying Typeface/libhwui crash (this report's
  recommendation; substrate investigation)

The remediation must happen before Phase 1b.3 (mini-driver port) can be
attempted, because PhaseOneBDriver.onCreate would also touch Typeface
(any `setContentView` / `TextView.setText` does).

---

## 6. Hard-stops check

| # | Hard stop | Triggered? |
|---|-----------|-----------|
| 1 | connect-key | NO |
| 2 | shell timeout > 30s | NO |
| 3 | `[Fail]` | NO |
| 4 | `drwx` | NO |
| 5 | unreachable | NO |
| 6 | write outside chroot | NO |
| 7 | Enforcing not restored | NO (never disabled) |
| 8 | control-flow `if` without device-side eval | NO |
| 9 | push success without target byte-match | NO (verified 1,569,240 byte size match) |
| 10 | macro-shim violation | NO (no Java changes this run) |
| 11 | substrate-level fix needed → STOP + report | **YES — triggered. Stopped here.** |

**Board state on exit:**
```
getenforce → Enforcing
mount | grep v3-hbc-chroot | wc -l → 5 (proc, sys, dev, /lib RO, /system/lib RO)
pgrep -af appspawn-x → (empty)
chroot artifacts preserved at /data/local/tmp/v3-hbc-chroot/
NEW: /data/local/tmp/v3-hbc-chroot/system/android/lib/liboh_adapter_bridge.so (1,569,240 bytes)
NEW: /data/local/tmp/v3-hbc-chroot/combo-1b2-retry-2.log (~50 lines, daemon crash trace)
NEW: /data/local/tmp/v3-hbc-chroot/combo-1b2-retry-fonts.log (~50 lines, with-fonts crash trace)
TEMP (cleaned): /data/local/tmp/v3-hbc-chroot/system/fonts/ (bind-mounted then unmounted/rmdir'd)
```

---

## 7. Artifacts captured

| Artifact | Local path | Lines / size |
|----------|------------|--------------|
| combo runs (3 invocations) | `/tmp/v3-phase-1b2-retry-combo.out`, `/tmp/v3-phase-1b2-retry-combo-2.out` | combined ~50 lines |
| Hilog: AppSpawnX/AppSpawnXInit narrative | `/tmp/v3-phase-1b2-retry-hilog-3.txt`, `/tmp/v3-phase-1b2-retry-hilog-fonts.txt` | 36 + 42 lines (the smoking-gun Typeface line is here) |
| Hilog: OH_RegHook trace | `/tmp/v3-phase-1b2-retry-jni.txt` | 30 lines (proves liboh_android_runtime.so loaded) |
| Hilog: DFX trace | `/tmp/v3-phase-1b2-retry-dfx.txt` | 20 lines (signo 11 si_code 1 SEGV_MAPERR) |
| Hilog: full per-pid 20423 | `/tmp/v3-phase-1b2-retry-full-hilog.txt` | 80 lines (the signal chain context) |

---

## 8. Recommended next steps (for human / next agent)

### Option A — Substrate investigation (recommended, RISK MEDIUM)

1. **Get processdump working inside chroot.** Verify processdump's runtime
   deps (likely libunwindstack + libdfx + libfaultlogger_client) are all
   in chroot's `/system/lib` or `/system/android/lib`. If missing, push.
   Then re-run combo and read the resulting `cppcrash-20423-*.log` for a
   real stack trace.

2. **Alternative: gdb-style attach.** Push `gdbserver` into chroot, run
   `gdbserver :1234 appspawn-x` in foreground from chroot, then attach
   from WSL `gdb-multiarch` and break on SIGSEGV. Get backtrace at frame
   of crash.

3. **Read libhwui Typeface code** specifically `Typeface_preloadOne` or
   `nInitBuilder` / `nAddFont` (in `frameworks/base/libs/hwui/jni/`)
   to identify what global pointer is dereferenced. The 12ms timing
   from "Preloading Roboto-Regular.ttf" to SEGV is consistent with a
   single mmap + parse + addFont call sequence.

### Option B — Patch chroot prep (avoid the crash) (RISK LOW)

1. Revert the `liboh_adapter_bridge.so` `/system/android/lib/` placement
   to restore Phase 1b.1 PASS baseline (agent 73 state). Accept that
   Phase 1b.2 is blocked on substrate work.

2. Document that V3 chroot path can deliver spawn round-trip + child Java
   entry, but CANNOT yet deliver real ActivityThread.main reach without
   the Typeface substrate work.

3. Re-plan W2 to either:
   - Skip the Typeface chain entirely by NOT calling Typeface in
     AppSpawnXInit.preload (controlled in the parent only — but child
     still needs Log.i, which goes through Log JNI, which is in
     liboh_android_runtime.so… so we're back to the same problem)
   - Add chroot setup steps to bind-mount /system/fonts AND patch
     fonts.xml on-device to NOT reference Roboto (only HarmonyOS_Sans
     entries that exist locally)

### Option C — Defer V3 W2 (RISK HIGH for V3 timeline)

If Options A and B are blocked, re-evaluate whether V3 path is the right
direction for the OHOS substrate. Per the discipline note
(`feedback_two_pivots_in_two_days.md`), a third pivot in 2 weeks requires
hard evidence. The current evidence is ONE substrate gap (Typeface), which
is solidly within "W-level retries" territory — NOT pivot evidence.

### Risk note

This is a CR23-grade substrate diagnosis — likely 1-2 days of focused
investigation to (a) get processdump working OR (b) port a font-init
shim. Per supervisor's hard-stop, the next agent should be tooled for
substrate work, not for chroot deploy iteration.

---

## 9. Files added/modified this commit

| Path | Purpose |
|------|---------|
| `docs/engine/V3-W2-PHASE-1B2-RETRY.md` | this report |

NOT modified (per brief constraint):
- `scripts/v3/deploy-hbc-to-dayu200-chroot.sh` (already has the 3 side-fixes from `1f789703`)
- `engine/v3/phase-one-b/*` (agent 73's combo.sh + test_tlv_client intact)
- HBC adapter sources (`westlake-deploy-ohos/v3-hbc/*`)
- V2-Phone (`westlake-host-gradle/*`)

---

## 10. Cross-references

- `docs/engine/V3-W2-PHASE-1B-PROGRESS.md` — agent 73 baseline, §4.2 SEGV warning
- `westlake-deploy-ohos/v3-hbc/adapter-src/framework/appspawn-x/java/com/android/internal/os/AppSpawnXInit.java`
  L165-251 (`preload()`), L632-653 (`preInitTypefaceDefault()` no-op), L673-735 (`preInitAssetManagerSystem`)
- `westlake-deploy-ohos/v3-hbc/etc/fonts.xml` — minimal HarmonyOS_Sans-only config (G2.14q)
- `feedback_macro_shim_contract.md` — forbidden patterns (verified clean; no
  Java/Kotlin source changes this run)
- `feedback_two_pivots_in_two_days.md` — discipline check (this report is
  W-level retry evidence, NOT pivot evidence)
