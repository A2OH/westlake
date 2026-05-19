# V3 chroot-containment proposal — Island anti-brick pattern adoption

**Status:** PROPOSAL (doc-only; awaits human decision per §10).
**Date:** 2026-05-19.
**Authoring agent:** 60 (Doc-only, ~2-3h time-box; sibling to agent 57's
`WESTLAKE-ISLAND-BORROW-MAP.md`).
**Companion docs:** `V3-W2-POSTMORTEM.md` (brick mechanism),
`V3-HBC-ARTIFACT-MANIFEST.md` (the 563-file bundle),
`V3-DEPLOY-HARDENED-SOP.md` (the current /system path), Island
`demo/run-live.sh` + `tests/R2-jni-smoke/setup-7.0.sh` (the containment
reference).

---

## 1. Problem statement

### 1.1 The W2 brick mechanism (recap)

`V3-W2-POSTMORTEM.md` §2 ranked two top causes for the 2026-05-16
soft-brick of DAYU200:

- **H1** — `chcon ... || true` batches at the end of Stage 3c / 3e
  silently no-op'd when `hdc shell` stdout went empty. SELinux labels
  on host `/system/lib/*.so` stayed at the inherited `system_file:s0`
  instead of being upgraded to `system_lib_file:s0`. On the next
  service tick, ART JNI_CreateJavaVM in the appspawn:s0 domain hit a
  dlopen flock denial and respawn-stormed. USB enumeration wedged.
- **H2** — the Windows `hdc.exe` invoked from WSL via wslpath
  translation regressed on the stdout channel mid-Stage-3. Exit codes
  stayed 0, file-send/recv kept working, but shell commands returned
  zero bytes. Compounded H1 because every chcon ran-but-couldn't-be-
  observed.

Recovery required a hard power-cycle + a ROM flash by the operator. The
board was OFFLINE for ~24 hours.

### 1.2 What the hardened script fixed (G1-G7)

`scripts/v3/deploy-hbc-to-dayu200-hardened.sh` (1190 LOC; landed
2026-05-18, commit `2086e1e4`) implemented seven defenses:

- **G1** channel-alive sentinel between every Stage (`_alive_probe`)
- **G2** chcon-verify-and-abort (replaces `chcon … || true`)
- **G3** required-artifact allowlist (no silent SKIP)
- **G4** opt-out flag for Stage 2 service-stop (default SKIP)
- **G5** Stage 3.9 integrity + drwx sentinel
- **G6** `hdc.exe` known-good version pin + warn
- **G7** twelve incrementally invocable stages

These mitigate the symptoms. They do not change the architecture: the
hardened script still writes to host `/system/lib/`,
`/system/android/`, `/system/etc/selinux/targeted/contexts/`,
`/system/bin/appspawn-x`, and `/system/etc/init/appspawn_x.cfg`.
Anything that goes wrong during those writes still requires a
device-side `.orig_<TS>` restore via shell — which assumes the shell
channel survives the failure.

### 1.3 hdc.exe version drift (today's halt)

The W2 retry stalled at the very first stage on a second, distinct
issue: the Windows `hdc.exe` at
`/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe` reports version `3.2.0b`
while the hardened script's `KNOWN_GOOD_HDC_VERSIONS` whitelist
(`scripts/v3/deploy-hbc-to-dayu200-hardened.sh` line 94) contains
only `Ver:1.3.0c Ver:1.3.0d Ver:1.3.0e`. G6 emits a `WARN` rather
than aborting, but the version mismatch is unresolved evidence that
host-side tooling state is **harder to pin than we'd like**. Each
DevEco-Studio update on the operator's Mac/Windows host silently
re-rolls the hdc binary. The reference deploy-author and the deploy-
runner can be on different hdc versions without either noticing until
something breaks.

### 1.4 The architectural gap

G1-G7 reduces the rate at which bricks happen. It does not change the
brick class. **Any** write to host `/system/` on DAYU200 retains the
property that a misordered, mislabeled, or partial state leaves init
unable to come back up on next boot, and rollback depends on a
host-side shell that may have died.

We are still asking a Windows process invoked from a WSL shell to
manage a 94-file, 412 MB write to a flash filesystem on a board with
no out-of-band reset and no second console — and asking it to survive
that operation **without** any of the failure being silent. That is a
hard correctness contract under hostile conditions.

---

## 2. Island's containment model

`artifacts/v3-w3-westlake-stack/16.14-Island/` (snapshot from
AlexYang's `/opt/10.Project/16-WestLake/16.14-Island/`) takes the
opposite stance: never write to host `/system`.

### 2.1 chroot at `/data/local/tmp/bionic-root/`

Every push in `tests/R2-jni-smoke/setup-7.0.sh` targets
`$ROOT/system/...` where `ROOT=/data/local/tmp/bionic-root`. Grep
confirms: 18+ `"$HDC" file send` invocations, all under `$ROOT`.
Zero invocations target host `/system/`.

The chroot's internal tree mirrors AOSP layout exactly:

- `$ROOT/system/bin/` (linker, dalvikvm32, dex2oat32, r2_runner,
  apk_runner)
- `$ROOT/system/lib/` (38 ART libs + 8 ICU/javacore extras +
  libproperty_stub.so + libhello2_jni.so)
- `$ROOT/system/framework/` (core-oj, core-libart, framework.jar,
  okhttp.jar, etc.)
- `$ROOT/system/framework/arm/` (boot.art, boot.oat, boot.vdex)
- `$ROOT/system/etc/` (public.libraries.txt, boot-image.prof)
- `$ROOT/apex/com.android.{art,conscrypt,i18n,os.statsd,runtime,tzdata}/`
- `$ROOT/linkerconfig/ld.config.txt`
- `$ROOT/data/local/oat/arm/` (relocated boot images)
- `$ROOT/proc`, `$ROOT/dev` (bind-mount targets)
- `$ROOT/host_tmp/` (the only path that crosses out — for live.png
  capture)

### 2.2 What stays outside

Host `/system/{bin,lib,etc,framework}` is **never touched**. Host
SELinux file_contexts is **never modified**. Host
`/system/etc/init/` is **never modified**. Host `/system/bin/`
gains **no new binaries**. The factory `appspawn`, factory
`foundation`, factory `render_service`, factory launcher, and OH
`hdcd` all keep running on factory artifacts, factory labels.

If the chroot is corrupted, the recovery is
`rm -rf /data/local/tmp/bionic-root` (a single shell command, against
a path where shell-domain rm is permitted by default OHOS SELinux).

### 2.3 Launch model

`demo/run-live.sh` shows the runtime entry:

```bash
ENV='LD_PRELOAD=/system/lib/libfakelog.so:/system/lib/libproperty_stub.so \
     LD_LIBRARY_PATH=/system/lib \
     ANDROID_DATA=/data ANDROID_ROOT=/system \
     ANDROID_TZDATA_ROOT=/apex/com.android.tzdata \
     ANDROID_I18N_ROOT=/apex/com.android.i18n \
     ANDROID_ART_ROOT=/apex/com.android.art \
     ANDROID_RUNTIME_ROOT=/apex/com.android.runtime'

$HDC -t $DEV1 shell "$ENV chroot $ROOT /system/bin/linker \
    /system/bin/apk_runner -class com.westlake.island.APKRunner \
    -dex /apkrunner.dex /hello-app.apk $CLS /host_tmp/live.png \
    480 800 -loop=10 -loop-budget=60000"
```

Note: the env vars say `/system/...` but they're resolved **inside**
the chroot, so they actually point at `$ROOT/system/...`. The ELF
binary itself has been built with
`-Wl,--dynamic-linker=/data/local/tmp/bionic-root/system/bin/linker`
(per `tests/R2-jni-smoke/build_runner.sh` line 5-7), so it can run
**outside** the chroot too and still find its own linker. Inside the
chroot, the path resolution is natural.

### 2.4 The "Never Die" five patterns (from START-HERE.md §5)

`demo/run-live.sh` operationalizes these as a pre-flight at every
launch:

1. **Restore chroot bind mounts after reboot** —
   `mountpoint -q $ROOT/proc || mount --bind /proc $ROOT/proc`
   (idempotent, runs every launch).
2. **Kill stale `apk_runner` before live runs** —
   `pkill -9 -f apk_runner 2>/dev/null` (idempotent).
3. **Start `np_daemon` if missing** —
   `pgrep np_daemon >/dev/null || (cd /data/local/tmp && \
    setsid ./np_daemon /data/local/tmp/oh-bridge.sock &)`.
4. **Force-stop OH Photos viewer before display reload** —
   `aa force-stop com.ohos.photos` then `aa start -a
   com.ohos.photos.MainAbility -b com.ohos.photos`. Without this, the
   stale Photos viewer holds a file handle on the previous frame PNG
   and the auto-reload daemon stutters.
5. **Loop-budgeted runs (not unbounded)** —
   `apk_runner -loop=10 -loop-budget=60000` (60 seconds max).
   Plus the daemon-side `NEVERDIE_FORK=1` fork-per-client +
   `SIGSEGV/SIGABRT → OH hilog FATAL`
   handler (`neutral-protocol/runtime-musl/np_daemon.c` lines 121-132,
   206-271) and the `NpBinder.transact()` catch-Throwable →
   `writeNoException + 0` fallback (`NpBinder.java` lines 83-105).

### 2.5 Empirical evidence it works on DAYU200

From `START-HERE.md` §6:

- V14 baseline: **smoke 77/77** on DAYU200 OH 7.0.0.18 (build 16.14).
- V15 baseline: **smoke 77/77** on the same board after the V14→V15
  upgrade (`docs/V15-SWITCH-2026-05-18-COMPLETE.md`).
- Hello30 V15 saved.
- Hello67 V15 saved.
- Cross-APK Calculator UI renders (`docs/L15-Hello111-ULTIMATE.md`).
- `demo/run-live.sh` 60-second live demo ready (per `START-HERE.md`
  Path B+C).

The same board (DAYU200, same hardware class) has been running this
containment pattern through multiple deploy iterations without
brick-class failures.

---

## 3. HBC bundle audit — what depends on /system?

The HBC bundle (`westlake-deploy-ohos/v3-hbc/`, 563 files, 412 MB per
`V3-HBC-ARTIFACT-MANIFEST.md` §1) is *not* simply "build for chroot."
Per-component honest audit follows. Categories:

- **TRIVIAL_RELOCATE** — runs from `$ROOT/...` with no rebuild;
  path-discovery is via env var or LD_LIBRARY_PATH.
- **REQUIRES_REBUILD** — has hard-coded `/system/...` strings in the
  ELF/dex that would resolve to host `/system/...` even from inside
  a chroot if the chroot's `/system/...` is the staging path; **but**
  changing the chroot root path to something other than `system/` (or
  bind-mounting to make paths match) sidesteps rebuild.
- **SYSTEM_BOUND** — semantically tied to host `/system` /
  host init / host SELinux labels; cannot run from chroot without
  losing the function.
- **UNKNOWN** — needs runtime experiment to classify.

### 3.1 Native `.so` files (56 files, 87 MB)

| Subset | `/system/` hardcodes | Classification |
|---|---|---|
| 38 AOSP runtime (libart, libhwui, libbase, libcutils, libutils, libicui18n, etc.) | ICU + libsigchain + libnativeloader resolve via env (ICU_DATA, ANDROID_ROOT). Verified by Island: same runtime libs (different vintage) run from `$ROOT/system/lib/` with `LD_LIBRARY_PATH=/system/lib` inside chroot. | TRIVIAL_RELOCATE |
| 5 HBC adapter shims (`liboh_adapter_bridge`, `liboh_android_runtime`, `liboh_hwui_shim`, `liboh_skia_rtti_shim`, `libapk_installer`) | `liboh_android_runtime` strings show `/system/android/lib/libhwui.so`, `/system/lib/liboh_adapter_bridge.so`, `/system/lib/platformsdk/libnative_vsync.so`. These are **dlopen() targets**; resolvable via LD_LIBRARY_PATH if chroot mirrors the layout (`$ROOT/system/android/lib/libhwui.so` exists). | TRIVIAL_RELOCATE (chroot must mirror full layout) |
| 13 OH service replacements (libabilityms, libappms, libbms, libwms, libscene_session*, librender_service*, libsurface, libskia_canvaskit, libappspawn_client, libinstalls, libappexecfwk_common) | `librender_service.z.so` strings show `/system/bin/render_service`, `/system/etc/hiview/...`, `/system/variant/*/base/`. `libbms.z.so` shows `/system/app/SceneBoard`, `/system/etc/ability_runtime/...`, `/system/etc/ark/...`, `/system/lib/appdetailability`. These libs are **the OH service implementations** — they ARE designed to be the running `/system/lib/libbms.z.so` that the factory `foundation` process loads. Inside an isolated chroot they'd be merely dlopened by appspawn-x's children; they would NOT be the OH services that the host foundation/launcher consume. | SYSTEM_BOUND IF GOAL IS REPLACING FACTORY OH SERVICES; TRIVIAL_RELOCATE if goal is "load these into the appspawn-x child for ART's Android Framework call-out" |

**Critical interpretation point:** the 13 OH service replacements are
what the HBC SOP §3b puts on host `/system/lib/` so that **factory OH
foundation** loads HBC's replacements instead of its own. This is the
high-risk write — every other write supports this one. If the chroot
model gives up replacing factory OH services, the entire HBC SOP §3b
becomes optional. **The question is whether the Westlake substrate
acceptance criterion (MainActivity.onCreate L83) requires the
*foundation/launcher path*, or just appspawn-x → ART → Activity.**
Per `V3-ARCHITECTURE.md` §3, appspawn-x is the spawn server and it
calls into AMS via `liboh_adapter_bridge.so` → `libipc_core`. AMS is
provided by factory `foundation`. So we DO need foundation, but
foundation can run the FACTORY OH libs as long as the AMS path we
exercise is functional. The HBC §3b replacements are an enhancement
(richer adapter coverage), not a hard requirement for first-light.

### 3.2 Boot image (27 files, 135 MB)

`bcp/boot-framework.art` has **baked-in** strings from `strings(1)`:

```
/system/android/framework/framework.jar
/system/android/framework/framework.jar!classes2.dex
/system/etc/fonts.xml
/system/etc/preloaded-classes
/system/etc/security/otacerts.zip
/system/fonts/Roboto-Regular.ttf
/system/fonts/RobotoStatic-Regular.ttf
/system/framework/android.hidl.base-V1.0-java.jar
```

`framework.jar` itself has `/system/fonts/Roboto-Regular.ttf` and
`/system/framework/framework-res.apk` baked into resource paths.

These strings come from `dex2oat`'s OAT metadata (DexLocations).
ART validates these at boot via `OatFile::ValidateBootClassPath`.
Boot image was generated for paths `/system/android/framework/*` —
not `/data/local/tmp/v3-hbc-chroot/system/android/framework/*`.

There are two ways to make boot image consumable from chroot:

A. **Bind-mount the chroot's framework path into host
`/system/android/framework/`** — keeps the strings resolvable. But
this bind-mount itself is a write to host `/`'s namespace and will
not survive reboot without an init.cfg hook (which puts us right back
to host-/system writes for the hook).

B. **Symlink** — `mkdir /system/android && ln -s
/data/local/tmp/v3-hbc-chroot/system/android/* /system/android/`.
Still a host-/system write (creating /system/android and the symlink).

C. **Force chroot path to match baked strings** by setting the chroot
root to `/data/local/tmp/v3-hbc-chroot/` and inside it ensuring
`/system/android/framework/framework.jar` exists. Then ART runs
inside the chroot resolve the baked strings via the chroot's
filesystem view → works without any host /system write. **This is the
Island pattern.** Boot image strings starting `/system/...` become
chroot-relative `$ROOT/system/...` and resolve to the same artifact
the strings expect. **TRIVIAL_RELOCATE for boot image, conditional on
chroot.**

D. Re-run `dex2oat` to bake in new locations. Requires HBC's build
machinery + an extra ~1-2 days. Last-resort.

**Verdict:** boot image is **TRIVIAL_RELOCATE conditional on path C**
(chroot, not host-/system symlink). Path C requires the apk_runner to
either `chroot()` into the bundle or to set up a personal mount
namespace before exec.

### 3.3 `appspawn-x` daemon + cfg

`appspawn-x` binary strings show:
- `/system/android` (ANDROID_ROOT)
- `/system/android/framework/boot.art` (ANDROID_BOOT_IMAGE)
- `/system/android/framework/core-oj.jar:...:/system/android/framework/framework.jar` (-Xbootclasspath)
- `/system/etc/appspawn_x_sandbox.json` (sandbox config)

The .cfg file (`etc/appspawn_x.cfg`) declares appspawn-x as an OH
`init` service with `secon: u:r:appspawn:s0` and an `AF_LOCAL`
socket `AppSpawnX`. All env-controllable via the `env:` block.

The cfg lives at `/system/etc/init/appspawn_x.cfg` and is consumed by
factory OH init. **If appspawn-x runs out of chroot via a manual `aa
start` style invocation rather than as an OH init service**, the
init.cfg becomes unnecessary; appspawn-x just becomes a daemon we run
ourselves via `chroot $ROOT /system/bin/appspawn-x --socket-name
AppSpawnX` (the binary's own argv format per the cfg).

Path-wise: appspawn-x ALSO has `/system/etc/appspawn_x_sandbox.json`
baked in as a string. We can override via env (`APPSPAWNX_SANDBOX=...`)
if that env is honored — needs source-check of `framework/appspawn-x/
src/main.cpp`. Worst case: bind-mount or symlink the sandbox JSON.

**Verdict:** appspawn-x binary is **TRIVIAL_RELOCATE conditional on
running it manually rather than as init service**. The `.cfg` is
**SYSTEM_BOUND** but **OPTIONAL** if we run appspawn-x ourselves.

### 3.4 Framework jars (12 jars + dex, 152 MB) + ICU dat

All dex-loaded by ART. Path resolution is via `-Xbootclasspath` and
`-Djava.class.path`. Both fully env-controllable. TRIVIAL_RELOCATE.

### 3.5 etc/ (6 files, 33 MB)

| File | Purpose | Class |
|---|---|---|
| `fonts.xml` | system_fonts_file SELinux label; consumed by Skia/HWUI on graphics_jni init | TRIVIAL_RELOCATE inside chroot (Skia reads it via FS API; label irrelevant inside chroot because there's no SELinux denial within shell_data_file domain) |
| `icudt72l.dat` | ICU runtime data | TRIVIAL_RELOCATE via ICU_DATA env |
| `appspawn_x.cfg` | OH init service descriptor | SYSTEM_BOUND but optional (see §3.3) |
| `appspawn_x_sandbox.json` | sandbox config | TRIVIAL_RELOCATE via env or bind-mount inside chroot |
| `ld-musl-namespace-arm.ini` | OH musl linker namespace config | **SYSTEM_BOUND** — this configures the OH musl linker used by FACTORY processes that link against OH .z.so libs (foundation, render_service). Inside a chroot, the chroot-internal processes use bionic linker, not musl, so the ini is moot for the chroot path. For the HBC §3b "replace factory OH services" goal, this is required and must land on host. |
| `file_contexts` | SELinux | **SYSTEM_BOUND** — only matters if we're labeling files in domains the OH SELinux policy enforces. Inside `/data/local/tmp/`, files inherit `u:object_r:shell_data_file:s0` by default; AppSpawnX socket and appspawn_exec are appspawn-domain. **This is a real risk for chroot** — see §9. |

### 3.6 Adapters (333 files of source) + patches (104 files)

Source/patches. Not deployed at runtime. N/A.

### 3.7 Tally

| Class | Count | % of bundle | Notes |
|---|---:|---:|---|
| TRIVIAL_RELOCATE (chroot-only) | ~85 native .so + 27 boot image + 13 jars + 2 etc | ~91% of binary mass | Driven by env vars + chroot path discipline |
| REQUIRES_REBUILD | 0 | 0% | Boot image's baked strings resolve fine inside chroot |
| SYSTEM_BOUND (functional loss in chroot) | 13 OH service replacements + ld-musl-namespace + file_contexts | ~8% of binary mass | These provide the "factory foundation/render_service uses HBC's libs" enhancement. Optional for first-light. |
| UNKNOWN | appspawn-x SELinux domain interaction | <1% | The big risk — see §9 |

**Honest estimate:** ~91% of bundle mass runs from chroot with
zero changes. The 8% SYSTEM_BOUND set is an *enhancement* HBC's SOP
lists as required; in containment-Phase-1 we'd skip those and accept
running ART against factory OH foundation/render_service. The
remaining ~1% (SELinux domain interactions) is the real unknown.

---

## 4. Three deploy models compared

| Model | Brick risk | Fidelity | Effort | DAYU200 proven |
|---|---|---|---|---|
| **A. Current hardened /system** (`deploy-hbc-to-dayu200-hardened.sh`) | LOW after G1-G7 but **not zero** — host /system writes remain; a silent-G2 chcon-verify failure during the host write still leaves the board in a state where recovery depends on shell. ROM flash is the last-resort recovery, has been needed once already. | **FULL** — runs the exact HBC stack HBC proved works in HBC's hands. MainActivity.onCreate L83 is HBC's own acceptance gate; same gate would apply here. | DONE (script + SOP exist). Phase 1 deploy = 1 retry session. | NO. W2 attempt 1 bricked. Retry blocked on hdc version drift + operator decision. |
| **B. Chroot containment** (Island pattern, new) | **IMPOSSIBLE BY CONSTRUCTION**. The only host-writeable path involved is `/data/local/tmp/v3-hbc-chroot/`. Recovery is `rm -rf $ROOT`. No SELinux contexts on host files change. | **PARTIAL**. The 91% TRIVIAL_RELOCATE works. Factory OH foundation continues to run its factory libs. HBC §3b "replace OH services" is skipped — accepted fidelity loss. We rely on the HBC adapter bridges going through factory OH `libipc_core` + factory OH AMS/WMS rather than HBC's enhanced replacements. | NEW — ~1-2 engineering days for `scripts/v3/deploy-hbc-to-dayu200-chroot.sh` (estimate: 400-600 LOC, modeled on Island's `setup-7.0.sh`). | YES — Island's analogous stack runs 77/77 smoke on the same hardware class with this pattern. |
| **C. Hybrid: chroot foundation + selective /system writes** | LOW — chroot covers the bulk; only the proven-safe `/system` writes remain (e.g. one `init.cfg` for appspawn-x once the chroot path is validated). Hardened script G1-G7 still applies for any /system writes. | **FULL eventually** — same as Model A once the /system writes are added back in a known-safe sequence. | MEDIUM — Phase 1 chroot deploy (Model B) + then Phase 2 layered /system addition only of the deltas that materially raise fidelity. ~2-3 engineering days additive over Model B. | PARTIAL — chroot half is proven (per B); the additive /system writes are unproven (per A). |

### 4.1 Honest fidelity-loss assessment for Model B

What Westlake **loses** by going chroot-only and skipping HBC SOP
§3b/§3c/§3e for first-light:

1. **Factory OH foundation runs factory `libabilityms.z.so` etc.**
   instead of HBC's replacements. HBC's replacements presumably have
   adapter hooks (for AMS callbacks to ART). If those hooks are
   missing on factory `libabilityms.z.so`, callbacks from AMS into
   ART (LifecycleAdapter → onResume etc.) may not fire. **This is the
   single biggest fidelity unknown.**
2. **OH `librender_service.z.so` doesn't get the HBC variant** so the
   HBC HWUI ANativeWindow shim path may not connect to factory
   render_service. Display path may need the PNG-blit-to-fb0 fallback
   that Island uses, not the real `IBufferProducer` integration the
   V3 architecture promises.
3. **`appspawn_x.cfg` not installed as an init service** so
   appspawn-x doesn't start at boot in the `appspawn:s0` SELinux
   domain. Running it manually from shell puts it in
   `shell_data_file:s0` / `untrusted_app:s0` (or similar). Some OH
   IPC operations may EPERM in that domain. **Real risk.**
4. **`file_contexts` SELinux additions** for `/dev/unix/socket/
   AppSpawnX` (appspawn_socket label) don't take effect. Communication
   from other OH processes to AppSpawnX requires that label. **Real
   risk.**
5. **MainActivity.onCreate L83 acceptance gate is HBC-defined.** We
   would need to define a Westlake-equivalent acceptance gate that
   demonstrates the chroot path works (e.g. HelloWorld.apk's
   MainActivity.onCreate reached from inside chroot's apk_runner
   analog). The bar is then lower than HBC's, but the meaning of
   "first light" shifts.

What Westlake **gains** by going chroot-only:

1. **Brick-class impossibility.** No operator power-cycle, no ROM
   flash, no offline-board.
2. **Faster iteration.** Bricks took 24+ hours of operator time to
   recover. Each retry of a chroot deploy costs zero operator time;
   only the agent time.
3. **Per-session isolation.** Operator can have multiple chroot
   bundles simultaneously (`v3-hbc-chroot.20260519/`,
   `v3-hbc-chroot.20260520/`) and switch by env var. Diff-debug
   sessions become trivial.
4. **Borrows the proven Island Never-Die patterns** which give
   bounded run-time + safe-default fallbacks even within the chroot
   stack.

---

## 5. Phased adoption proposal

### Phase 1 — Today's W2 retry (proposed)

**Adopt full chroot containment for the immediate W2 retry.**

**Acceptance criterion (Phase 1, weakened from HBC's):**
HBC HelloWorld.apk's `MainActivity.onCreate` reached from inside
chroot's `apk_runner` analog (loads `framework.jar`, runs ART
JNI_CreateJavaVM with HBC boot image, calls Activity.onCreate via
the HBC adapter framework). Not yet required: full AMS/WMS
integration with factory foundation; full HWUI/Skia render via
factory render_service.

**Deliverables (engineering):**

1. `scripts/v3/deploy-hbc-to-dayu200-chroot.sh` (~400-600 LOC,
   modeled on Island's `tests/R2-jni-smoke/setup-7.0.sh` but using
   the HBC bundle layout under `westlake-deploy-ohos/v3-hbc/`).
2. `scripts/v3/run-hbc-chroot-helloworld.sh` (~80 LOC, modeled on
   Island's `demo/run-live.sh`) — launches HBC HelloWorld inside
   the chroot with the 5 Never-Die preflight steps.
3. `docs/engine/V3-DEPLOY-CHROOT-SOP.md` (~150 LOC) — incremental
   stages 0-7 mirroring the hardened SOP but with chroot paths.

**Engineering cost estimate:** 1-2 days for a single engineer. Most of
the work is mechanical translation of file destinations from
`/system/...` to `$ROOT/system/...` plus the chroot exec wrapping.
The HBC bundle is already pulled (W1 closed); the chroot recipe is
proven by Island; the brick risk is by construction zero.

**Required script discipline:**

- chroot mount setup script (idempotent; bind-mount `/proc`, `/sys`,
  `/dev` into `$ROOT`).
- LD_LIBRARY_PATH / LD_PRELOAD env wrap mirroring the HBC SOP env.
- force-stop OH Photos before any display reload (Island pattern).
- loop-budget on launch (`apk_runner -loop=N -loop-budget=Tms`).
- channel-alive sentinel between every stage (G1, kept from hardened).
- chcon-verify still applies inside chroot for any SELinux work
  (though §9 risks below suggest chroot SELinux may be irrelevant).

### Phase 2 — Selective /system writes (later, if needed)

Conditional on whether Phase 1 chroot has fidelity gaps that block
the V3 W4/W5/W6 acceptance criteria.

**If Phase 1 hits a hard fidelity gap** (e.g., HBC adapter calls into
factory foundation EPERM because factory `libabilityms.z.so` lacks
HBC's adapter hooks), then **selectively** add back the
SYSTEM_BOUND deltas one at a time:

- First, only the HBC `libabilityms.z.so` (replaces factory; risk
  isolated to that single .so; rollback = restore .orig_<TS>).
- Second, only `libwms.z.so` and `libappms.z.so` if window/app
  integration matters.
- Third, the appspawn_x.cfg + file_contexts for proper SELinux
  domain — done with full hardened script discipline.

Each addition is a separate W-stage with its own postmortem-able
rollback. G1-G7 still apply. The full Model A
(`deploy-hbc-to-dayu200-hardened.sh`) becomes the LAST resort, only
if all other layered adds fail.

This is the Model C hybrid path. It buys Westlake the brick-immune
quick-iteration property AND eventual fidelity, at the cost of
discovering which /system writes really matter empirically (instead
of running HBC's whole 94-file SOP blind).

---

## 6. Specific Island patterns to borrow today

Each item: source file in Island, exact pattern to copy, V3 adoption.

### 6.1 chroot under `/data/local/tmp/v3-hbc-chroot/` (NEW BORROW)

**Source:** `tests/R2-jni-smoke/setup-7.0.sh` `ROOT=/data/local/tmp/
bionic-root` (Island uses `bionic-root`; V3 should use a sibling
name to avoid clobbering Island's existing chroot if both ever
co-exist on the same board).

**V3 adoption:** New env `V3_CHROOT_ROOT=/data/local/tmp/v3-hbc-chroot`
(default). Every `$HDC file send` in the new chroot deploy script
prefixes its device-side path with `$V3_CHROOT_ROOT`. Every chroot
launch uses `chroot $V3_CHROOT_ROOT ...`.

### 6.2 Mount restore script (NEW BORROW)

**Source:** `demo/run-live.sh` lines 35-42:

```bash
mountpoint -q $ROOT/proc 2>/dev/null || mount --bind /proc $ROOT/proc
mountpoint -q $ROOT/dev 2>/dev/null  || mount --bind /dev $ROOT/dev
```

Idempotent: `mountpoint -q` first, then conditional bind. Runs every
launch.

**V3 adoption:** `scripts/v3/v3-chroot-mounts.sh` — idempotent mount
restorer that runs at every `aa-launch-chroot.sh` invocation. Add
`/sys` and `/host_tmp` to the list per V3 needs.

### 6.3 Force-stop OH Photos viewer before display reload (NEW BORROW)

**Source:** `demo/run-live.sh` line 60-62:

```bash
aa force-stop com.ohos.photos 2>&1 | tail -1
sleep 1
aa start -a com.ohos.photos.MainAbility -b com.ohos.photos 2>&1 | tail -1
```

The Photos viewer holds file handles on the last frame PNG; without
the force-stop, auto-reload stutters.

**V3 adoption:** When V3 uses the PNG-blit-via-Photos display path
(if at all — V3's architecture promises real render_service
integration, which doesn't need this), include the same force-stop
+ restart in the launcher script.

### 6.4 Loop-budget on launch (NEW BORROW)

**Source:** `demo/run-live.sh` line 71:
`-loop=10 -loop-budget=60000` (60 second hard cap).
`APKRunner.java:161-237` implements the budget.

**V3 adoption:** Any V3 chroot launch wraps the ART process with a
budget. Default 60s for first-light validation. The launcher script
runs the budget on the **shell** side
(`timeout 60s hdc shell "$ENV chroot $ROOT ..."`) to defend against
the chroot process hanging without exiting.

### 6.5 Safe-default stubs for missing services (NEW BORROW)

**Source:** `NpBinder.java:83-105` — `IBinder.transact()` catches any
handler `Throwable` → `reply.writeNoException(); reply.writeInt(0);
return true`. Missing-service degrades to "false / 0 / null" rather
than crashing.

**V3 adoption:** This is **Borrow #1** in
`WESTLAKE-ISLAND-BORROW-MAP.md` §3 — an `InvocationHandler` decorator
around HBC adapter proxies that catches `InvocationTargetException` →
returns AOSP-default for the return type. Cite as the SOPI of "the
chroot stack continues to limp even when an adapter method we
don't yet bridge throws." Lands as
`westlake-host-gradle/app/src/main/java/com/westlake/host/
NeverDieAdapterDecorator.java`.

### 6.6 Never `hdc target boot` after a deploy (REINFORCEMENT)

The hardened script's 全局三条 already encodes this. Containment
makes it nearly impossible to need a `hdc target boot` in the first
place: there's nothing in chroot that requires a reboot to take
effect.

### 6.7 chcon verify, never `|| true` (REINFORCEMENT)

G2 already in hardened. Inside chroot the chcon question becomes
"do we even need chcon?" — files in `/data/local/tmp/` inherit
`shell_data_file:s0` which is permissive for chroot-internal
processes. **Outside chroot** (if Phase 2 layers any /system write),
G2 still applies.

### 6.8 Channel-alive sentinel between stages (REINFORCEMENT)

G1 already in hardened. Mirror into the chroot script verbatim. Even
though chroot writes can't brick, hdc still needs to be alive for
the deploy to complete.

---

## 7. hdc version drift mitigation (Gate 8 proposal)

Today's W2 retry halted because `hdc.exe` reports `3.2.0b` while
the script's `KNOWN_GOOD_HDC_VERSIONS` lists `1.3.0c-e`.
Diagnosis options:

**Option A — Downgrade hdc.exe to a pinned 1.3.0c-e build.** Find a
known-good `hdc.exe` (last successful pull) and check it into
`scripts/v3/tools/hdc.exe.pinned` (binary in repo). Set `HDC` env
default to that path.

**Option B — Update KNOWN_GOOD_HDC_VERSIONS to include 3.2.0b after
running a compatibility sniff test.** Run the W2 hardened script's
Stage 0-1 (read-only stages) against a sacrificial board state, verify
all G1 alive probes return correctly and all stat/md5 round-trips
match Island's checksums. If clean, append `Ver:3.2.0b` to the list.

**Recommendation:** Option A for today's retry (eliminates the
variable). Option B as a follow-up workstream once we have a known-
good board to test against.

**Add to `V3-DEPLOY-HARDENED-SOP.md` as Gate 8:**

> **Gate 8 — hdc.exe pinned binary.** Deploy script refuses to run if
> `$HDC` points at a binary outside `scripts/v3/tools/hdc-pinned/` or
> a path explicitly authorized by `HDC_PIN_OVERRIDE=1` env. Reasoning:
> host-side tool drift is a real failure mode (W2 retry today). Pinned
> binary is checked into repo (Apache-2.0 / OHOS-license-compatible).

---

## 8. Concrete next steps for today's W2 retry

1. **Resolve hdc.exe.** Either:
   - downgrade to a known-good 1.3.0c-e (Option A from §7), OR
   - run a compatibility sniff with 3.2.0b against a SAFE chroot path
     (no /system writes) and append to KNOWN_GOOD list if it passes.
2. **Write `scripts/v3/deploy-hbc-to-dayu200-chroot.sh`** —
   chroot-mode variant of the hardened deploy. ~1-2 engineering days.
3. **Run Phase 1 acceptance:** HBC HelloWorld.apk MainActivity.
   onCreate reached inside `$V3_CHROOT_ROOT` via apk_runner analog.
4. **No /system writes** at all in this retry. Brick-impossible by
   construction.
5. **If Phase 1 PASS:** declare W2 acceptance "first-light reached in
   containment"; queue Phase 2 selective /system writes as a separate
   workstream gated by human decision §10.

---

## 9. Open questions / risks for human review

These are the items the agent flags as REAL UNKNOWNS that the chroot
path cannot resolve from doc-analysis alone.

### Q1. Does HBC's render_service integration require /system writes?

HBC's architecture (per `V3-ARCHITECTURE.md` §3) routes Java
`Surface` → JNI `liboh_hwui_shim.so` →
`OHNativeWindow → IBufferProducer → RS BufferQueue`. The
`librender_service.z.so` HBC replacement adds adapter hooks to the
OH RS. If we DON'T deploy HBC's `librender_service.z.so` and rely on
factory RS, do the HBC adapter hooks get called? Or does HWUI silently
fall back to "no display, headless"? **Most likely outcome:** headless
rendering succeeds (PNG dump path); on-screen rendering requires the
HBC RS replacement. **Phase 1 is OK with headless validation.**
Phase 2 may need the RS replacement.

### Q2. Does appspawn-x require /system/bin location for PID-namespace correctness?

The appspawn-x cfg sets `cgroup: true` and `setuid: true`. These
require the binary live in a path that OH init can exec with the
appspawn SELinux domain transition (`appspawn_exec` label on
`/system/bin/appspawn-x`). If we run appspawn-x from inside chroot
manually (not via OH init), it inherits the launching shell's
domain (likely `shell_data_file:s0` or `untrusted_app:s0`). **PID
namespace setup that requires CAP_SYS_ADMIN may EPERM in shell
domain.** Risk: high. **Mitigation:** Phase 1 runs ART without
appspawn-x in the spawn-server role — apk_runner-style direct
invocation. Phase 2 if AppSpawnX socket semantics required.

### Q3. Does boot-image baked-in `/system/framework/boot.art` path block chroot?

Per §3.2, the boot image strings reference `/system/android/framework/
*.jar`. Inside chroot with `$ROOT=/data/local/tmp/v3-hbc-chroot/`,
`/system/android/framework/...` resolves to
`$ROOT/system/android/framework/...` via the chroot's filesystem
view. ART's `OatFile::ValidateBootClassPath` compares the recorded
location against the runtime location — but both are
chroot-relative strings, so they match. **Most likely outcome:**
boot image loads cleanly. **Caveat:** ART may also check
inode/mtime; if those drift, validation fails. Mitigation: preserve
boot image artifact timestamps via `hdc file send -m` (if supported)
or `touch -r` round-trip.

### Q4. SELinux domain interactions inside chroot — REAL RISK

OH's `selinux` enforces by domain, not by path. Inside chroot, files
inherit `u:object_r:shell_data_file:s0` by default; chroot
inherited from a hdc shell is `u:r:sh:s0` or
`u:r:hdcd:s0`. Operations that work in `system_app:s0` or
`appspawn:s0` may EPERM in `sh:s0`. Examples:
- `mount --bind` requires CAP_SYS_ADMIN (root); hdc shell on DAYU200
  *is* root via su, but the SELinux domain may deny mount in `sh:s0`.
- `setuid()` from sh domain to other UID may EPERM.
- Loading dex via ART may trip `dexopt` SELinux denials in `sh:s0`.

**Island works around this empirically** — their 77/77 smoke implies
their chroot has been tuned for OH 7.0's SELinux. Whether their
exact `setenforce 0` posture (or equivalent) carries to V3 is
**unknown** until we run the chroot deploy and capture denials in
`hilog | grep avc:`. **This is the single biggest risk for chroot
adoption.**

Mitigation paths if SELinux denies operations in chroot:

A. Run `setenforce 0` for the duration of the chroot test. Restores
   on next reboot. Trades enforcement for first-light validation.
B. Add a single, targeted SELinux policy patch to host
   `/system/etc/selinux/.../file_contexts` (which IS a /system write
   and re-introduces brick risk — defeats the purpose).
C. Use `runcon u:r:appspawn:s0` to manually enter the appspawn
   domain for the chroot. Requires shell to have the transition
   right, which depends on factory policy.

**Phase 1 plan:** start with `setenforce 0` per Path A as the
fastest validation. Restore Enforcing for fidelity work in Phase 2.
**Document this explicitly** — running in Permissive is itself a
fidelity caveat, but it isolates the SELinux question from the
chroot question.

### Q5. Is HBC's "MainActivity.onCreate L83" reachable via chroot acceptance?

HBC's acceptance criterion is "ART process started by appspawn-x
runs HelloWorld.apk and emits a hilog line containing
`MainActivity.onCreate` line 83." With Phase 1 chroot deploy, the
flow becomes: `apk_runner-like binary` (custom; not appspawn-x) →
`JNI_CreateJavaVM` with HBC -Xbootclasspath → load HBC
framework.jar boot image → load HelloWorld.dex →
`Class.forName(MainActivity).newInstance()` →
`Activity.attach(…stub Context…)` → `Activity.onCreate()`. This
is a **direct invocation** rather than via appspawn-x's
`spawn_server.cpp` fork-exec path. The Java-side payload (HelloWorld
calling MainActivity.onCreate at line 83) is identical. The hilog
line should appear. **Most likely outcome:** acceptance reachable
in chroot.

**Caveat:** if HelloWorld uses Context.startActivity (cross-Activity
launch), that hits AMS, which is factory `libabilityms.z.so` (per
§3.1) which we're not replacing in Phase 1. May EPERM or no-op.
Acceptance is unchanged if MainActivity.onCreate is the
single-activity entry; broken if HelloWorld depends on cross-activity
within itself.

---

## 10. Decision request

Two yes/no questions for the human:

**Q-D1.** *Adopt Phase 1 containment (full chroot, no host /system
writes) for today's W2 retry?*

- **Yes:** agent proceeds to write
  `scripts/v3/deploy-hbc-to-dayu200-chroot.sh` per §5 Phase 1.
  ~1-2 engineering days. Brick-impossible by construction. Fidelity
  caveats from §9 understood and accepted.
- **No:** agent stays on Model A (hardened script). hdc.exe drift
  resolved per §7. Retry runs the existing hardened script. Brick
  risk reduced by G1-G7 but not zero.

**Q-D2.** *Authorize 1-2 days additional engineering to build the
chroot-mode deploy script?*

- **Yes:** agent (or follow-up agent) gets a W14 budget. Estimated
  deliverable: chroot deploy script + SOP + Phase 1 acceptance log
  + Phase 2 layered-write plan if Phase 1 PASS.
- **No:** the chroot path stays a proposal; today's retry runs
  Model A only. The brick-impossibility property is forgone for
  this iteration.

---

## 11. Cross-references

- `docs/engine/V3-W2-POSTMORTEM.md` — brick mechanism
- `docs/engine/V3-HBC-ARTIFACT-MANIFEST.md` — bundle inventory
- `docs/engine/V3-DEPLOY-HARDENED-SOP.md` — current /system deploy
  procedure (Model A)
- `scripts/v3/deploy-hbc-to-dayu200-hardened.sh` — Model A driver
- `docs/engine/WESTLAKE-ISLAND-BORROW-MAP.md` — sibling-agent's
  borrow analysis (5 top borrows for engine layer; this proposal
  is the deploy-layer counterpart)
- `artifacts/v3-w3-westlake-stack/16.14-Island/demo/run-live.sh` —
  Island chroot launcher (the §6 borrow source for patterns 1-5)
- `artifacts/v3-w3-westlake-stack/16.14-Island/tests/R2-jni-smoke/
  setup-7.0.sh` — Island chroot setup script (the structural
  template for `scripts/v3/deploy-hbc-to-dayu200-chroot.sh`)
- `artifacts/v3-w3-westlake-stack/16.14-Island/tests/R4-apk-runner/
  src/apk_runner.c` — Island bionic-side runner (the C-side launch
  pattern V3 would mirror for HBC's stack)
- `westlake-deploy-ohos/v3-hbc/scripts/DEPLOY_SOP.md` — HBC's
  authoritative SOP (read-only; V3 doesn't modify in place)
- `/home/dspfac/.claude/projects/-home-dspfac-openharmony/memory/
  feedback_soft_brick_w2_2026-05-16.md` — memory lesson from the
  W2 brick
- `/home/dspfac/.claude/projects/-home-dspfac-openharmony/memory/
  feedback_additive_shim_vs_architectural_pivot.md` — anti-pattern
  guard. NOTE: chroot-containment is not a third pivot of the V3
  ARCHITECTURE — it is a deploy-layer alternative. The substrate
  (HBC artifacts, real `framework.jar`, real `libart.so`, 4 L5
  patches, etc.) is unchanged. Only the *deploy location* changes.
- `/home/dspfac/.claude/projects/-home-dspfac-openharmony/memory/
  feedback_two_pivots_in_two_days.md` — same theme; this proposal
  is explicitly deploy-only, not an architectural pivot.
