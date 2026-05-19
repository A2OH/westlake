# W4-empty: HelloWorld Invocation Discovery

**Date:** 2026-05-19
**Author:** agent 64 (scoping / discovery, document-only)
**Status:** SCOPING (no board contact yet; no code changes; no script edits)
**Companions:**
  `scripts/v3/deploy-hbc-to-dayu200-chroot.sh` (the harness whose
  `$V3_CHROOT_HELLO_CMD` env this doc scopes),
  `docs/engine/V3-DEPLOY-CHROOT-SOP.md` (operator's view of HELLO_CMD,
  §3.7),
  `docs/engine/V3-LAUNCH-MODEL.md` (the AOSP `aa start → AMS → appspawn-x
  → ActivityThread` chain),
  `docs/engine/V3-WORKSTREAMS.md` (W4-empty acceptance, §"W4-empty"),
  `westlake-deploy-ohos/v3-hbc/scripts/DEPLOY_SOP.md` (HBC's
  source-of-truth: `aa start` against /system layout),
  `westlake-deploy-ohos/v3-hbc/adapter-src/framework/appspawn-x/`
  (appspawn-x source: `main.cpp`, `child_main.cpp`,
  `java/com/android/internal/os/AppSpawnXInit.java`,
  `test/test_tlv_client.c`),
  `artifacts/v3-w3-westlake-stack/16.14-Island/tests/R4-apk-runner/src/apk_runner.c`
  (Island's chroot-launch reference),
  `aosp-libbinder-port/m3-dalvikvm-boot.sh` (V2-Phone dalvikvm reference).

---

## TL;DR (5 bullets)

1. **HBC's HelloWorld launch path requires factory OH AMS callback** (per
   `AppSpawnXInit.java:924-938`): `aa start` → `IAbilityManagerService::
   StartAbility` → `AppMgr::AttachApplication` callback → `AppSchedulerAdapter::
   ScheduleLaunchAbility` (C++) → `AppSchedulerBridge.nativeOnScheduleLaunch
   Ability` → `IApplicationThread.scheduleTransaction` → `ActivityThread.
   handleLaunchActivity` → `MainActivity.onCreate`. Chroot Phase 1 has none
   of that infrastructure — factory `libabilityms.z.so` (which is what
   `aa start` ultimately routes to outside the chroot) has no HBC adapter
   hooks, so the AMS-callback edge into our chroot child cannot fire.
2. **HBC bundle has ZERO chroot-compatible HelloWorld launch path
   shipped as a binary** — `bin/` contains only `appspawn-x` (no
   `dalvikvm`, no `aa`, no `bm`, no `app_process`, no `apk_runner` analog).
   The closest thing in the bundle to a chroot-direct launcher is the
   `test_tlv_client.c` SOURCE under `adapter-src/framework/appspawn-x/
   test/` (a 143-LOC C client that speaks the OH AppSpawn TLV protocol
   directly to the AppSpawnX Unix socket), but it isn't pre-built and
   even when built it would only reach `ActivityThread.main() →
   Looper.loop()`, NOT `MainActivity.onCreate` (no Intent in the spawn
   message).
3. **Recommended for today's W2 retry: Phase 1a (env-probe-only)** — the
   harness's existing default-no-arg behavior is the right choice; it
   validates that the chroot exec works + the env is right + boot.art is
   visible inside chroot, which is the minimum substrate viability gate.
   The `$V3_CHROOT_HELLO_CMD` punt is honest about Phase 1 limits.
4. **Phase 1a-plus (recommended W4-empty stretch goal): build
   `test_tlv_client` from `adapter-src/.../test/test_tlv_client.c`, push
   into chroot, set `$V3_CHROOT_HELLO_CMD` to start `appspawn-x` in
   background + run the client.** Reaches `[CHILD_CK] CK_BEFORE_initChild_call`
   (Java entered) and `AppSpawnXInit.initChild`'s `J_after_initAdapterLayer`
   marker. Does NOT reach `MainActivity.onCreate` (no AMS-edge), but
   measures real `ActivityThread.main()` + framework-class load reach
   inside chroot, which IS what W4-empty is designed to measure.
5. **`MainActivity.onCreate` is NOT reachable in chroot Phase 1 by
   construction.** Reaching it requires Phase 2 (`/system/lib/libabilityms.z.so`
   replacement per `V3-DEPLOY-CHROOT-SOP.md` §6 row 1). Today's W2 retry
   acceptance must explicitly exclude the `MainActivity.onCreate` marker
   for chroot Phase 1; trying to grep it will always FAIL and is not a
   real signal.

---

## 1. The problem

Agent 62's chroot harness (`scripts/v3/deploy-hbc-to-dayu200-chroot.sh`)
runs Stage 5 (launch) with this contract (see lines 750-781):

* Default `$V3_CHROOT_HELLO_CMD` empty → run `launch.sh` with no inner
  arg = env-probe-only mode → prints `[v3-chroot-launch] PWD=...
  LD_LIBRARY_PATH=... BOOTCLASSPATH=...` and exits 0.
* Operator-supplied `$V3_CHROOT_HELLO_CMD` non-empty → forwards
  `$V3_CHROOT_HELLO_CMD` to `exec "$@"` inside `chroot $V3_CHROOT_ROOT
  /system/bin/sh /launch.sh`.

The script's docstring suggests an "operator may set
`$V3_CHROOT_HELLO_CMD=/system/bin/appspawn-x --socket-name AppSpawnX`",
but this is at best a probe of "does the appspawn-x daemon start under
chroot" — it does NOT exercise the path to `MainActivity.onCreate`.

The W4-empty acceptance criterion (per `V3-WORKSTREAMS.md` §W4-empty
L223-256) is **"how far does pure-HBC reach with engine = empty,
including the MainActivity.onCreate marker"**. The current chroot path
cannot answer that question as-architected, because:

* MainActivity.onCreate is reached via `aa start → AMS → AppSchedulerAdapter
  → IApplicationThread.scheduleTransaction → ActivityThread.handle
  LaunchActivity → MainActivity.onCreate`.
* In chroot Phase 1, the AMS side of that chain runs in the FACTORY OH
  ability manager service process (outside the chroot, against factory
  `/system/lib/libabilityms.z.so`), which has NO HBC adapter hooks. The
  spawn-then-callback link from AMS into our chroot child cannot fire
  because (a) factory AMS doesn't know about our chroot's appspawn-x
  socket and (b) factory `libabilityms.z.so` doesn't know about
  `AppSchedulerAdapter`.

Hence the harness's honest punt: ship the env wrapper, default to
env-probe-only mode, and leave the actual ART/AMS-edge invocation to
"future work (W4/W5/W6) per proposal §5 Phase 1" (script L753).

This document spells out the three feasible Phase-1 invocation paths
and recommends one for today.

---

## 2. HBC bundle launch-artifact inventory

Walked `westlake-deploy-ohos/v3-hbc/` top-to-bottom; the launch-relevant
artifacts are below. The bundle has NO launcher binary other than
`appspawn-x`; the AMS-side launcher (`aa`, `bm`, `app_process`,
`dalvikvm`, etc.) is intentionally absent because HBC's design relies on
factory OH `aa`/`bm` issuing standard OH startAbility against factory OH
AMS, which then talks to HBC's appspawn-x over the `AppSpawnX` socket.

### 2.1 `bin/` — 1 executable (112 KB)

| File | Purpose | Dependencies | Env requirements |
|------|---------|--------------|------------------|
| `appspawn-x` | Hybrid OH-appspawn + Android-Zygote daemon. Listens on AF_UNIX socket `AppSpawnX`. On spawn request: `fork()` → child specialization (DAC, sandbox, SELinux, AccessToken) → `ChildMain::run` → `AppSpawnXInit.initChild` → `ActivityThread.main()` → `Looper.loop()`. 32-bit ARM PIE, dynamically linked to `/lib/ld-musl-arm.so.1`. | All 56 `lib/` .so files (transitively via DT_NEEDED: `libart.so`, `liboh_adapter_bridge.so`, `libhwui.so`, `libandroid_runtime.so`, ...). HBC's 7 `oh-service/` patched `lib*.z.so` for AMS/WMS/AppMS hooks. ICU data. Boot image. | `BOOTCLASSPATH`, `DEX2OATBOOTCLASSPATH`, `ANDROID_ROOT=/system/android`, `ANDROID_DATA=/data`, `ANDROID_BOOT_IMAGE=/system/android/framework/boot.art`, `ANDROID_I18N_ROOT`, `ANDROID_TZDATA_ROOT`, `ICU_DATA=/system/android/etc/icu`, `LD_LIBRARY_PATH` covering `/system/android/lib:/system/lib/platformsdk:/system/lib`. (All these are seeded by `appspawn-x main.cpp seedRequiredEnvs()` at L86-111 unconditionally, so a chroot invocation can rely on `appspawn-x` setting its own env even if `launch.sh` didn't.) |

### 2.2 `etc/` — 2 launch-related configs

| File | Purpose | Notes |
|------|---------|-------|
| `appspawn_x.cfg` | OH init service descriptor. Declares `services.appspawn-x` with `path=["/system/bin/appspawn-x","--socket-name","AppSpawnX"]`, `secon=u:r:appspawn:s0`, socket `AppSpawnX` AF_LOCAL SOCK_STREAM uid=root gid=appspawn permissions=0660, env block seeding ANDROID_ROOT etc. Used only when appspawn-x is registered as an OH init service — Phase 1 invokes appspawn-x manually from shell (in `sh:s0`) and `init` does NOT consume this file inside chroot. | This is the canonical OH init service config; Phase 1 chroot replicates the env block via `launch.sh` (already done by agent 62, L662-668) and just runs appspawn-x as a manual shell child. |
| `appspawn_x_sandbox.json` | Sandbox mount-namespace + symlink config the child applies during specialization. Lists `/system`, `/data/app`, `/data/data/<PackageName>`, etc. as bind mounts. Consumed by `SpawnServer::initSecurity()` (called from `appspawn-x main.cpp` L185). | Phase 1: appspawn-x will try to load this even when run from shell; sandbox setup may EPERM in `sh:s0` (proposal §5.2). Either pass `--sandbox-config /dev/null`-style stub, or accept the warning. |

### 2.3 `scripts/` — 13 HBC deploy scripts

None of these is a launcher. They are all deploy / build / restore /
SOP docs. `DEPLOY_SOP.md` Stage 4 (L204-211) says:

```bash
hdc file send out/app/HelloWorld.apk /data/local/tmp/HelloWorld.apk
bm install -p /data/local/tmp/HelloWorld.apk
bm dump -n com.example.helloworld
aa start -a com.example.helloworld.MainActivity -b com.example.helloworld
pidof com.example.helloworld
```

i.e., HBC's launch is `bm install` (factory OH bm CLI; not in bundle)
then `aa start` (factory OH aa CLI; not in bundle) — both rely on
factory OH services being present and routing through HBC's
`libabilityms.z.so` patches on `/system/lib/`. This is the
`/system`-path deploy model, NOT chroot.

### 2.4 `adapter-src/framework/appspawn-x/test/` — 3 source files (NOT in bin/)

| File | Purpose | Status |
|------|---------|--------|
| `test_tlv_client.c` (143 LOC) | Speaks raw OH AppSpawn TLV protocol to AppSpawnX socket. Sends magic `0xEF201234` + 280B header + `TLV_BUNDLE_INFO` ("com.example.helloworld") + `TLV_DAC_INFO`. **Bypasses factory OH AMS entirely.** Receives `AppSpawnResponseMsg` (result, pid, checkPointId). | **Source only — must be cross-compiled for ARM32 + pushed into chroot.** Standalone C client; depends only on `<sys/socket.h>`, `<sys/un.h>`. No external libs. |
| `test_init_minimal.c` (39 LOC) | Zero-dependency smoke probe. Links only libc. Writes diagnostic to stderr (when console:2 redirect is active). Used to isolate "init/SELinux/cap fault vs our libs fault" questions. | **Source only — and one pre-built binary at `adapter-src/framework/appspawn-x/test/out/test_init_minimal`** (verified present in bundle). Not chroot-launch-relevant, but a useful "is exec working at all" canary. |
| `test_init_minimal.cfg` | OH init service descriptor for the above. Not chroot-relevant. | — |

### 2.5 `app/` — HelloWorld APK + source

| File | Purpose |
|------|---------|
| `HelloWorld.apk` | The standard Android HelloWorld APK that HBC's MainActivity.onCreate test targets. Has `MainActivity extends android.app.Activity`. `MainActivity.onCreate()` at L72-171 of `app/src/java/com/example/helloworld/MainActivity.java`; the `Log.i(TAG, "=== MainActivity.onCreate() ===")` marker is at L75 (not L83 as the brief states — L83 is `title.setTextSize(24)`). |
| `helloworld_resources.hap` | OHOS-packaged variant. Not currently in chroot scope. |
| `src/java/com/example/helloworld/{MainActivity,SecondActivity,HelloWorldApplication,HelloService}.java` | Source for the four classes inside the APK. |

### 2.6 `lib/`, `jars/`, `bcp/` — substrate, not launchers

These are the runtime substrate the appspawn-x child loads via
`-Xbootclasspath` and `LD_LIBRARY_PATH`. They are reachable from the
chroot launch.sh env per agent 62's wrapper (BOOTCLASSPATH wired L668,
LD_LIBRARY_PATH L661). Not launchers themselves.

### 2.7 Confirmed absent (would be needed for full HBC launch but bundle omits)

* `aa` (factory OH ability CLI) — not built by HBC, comes from factory OH
* `bm` (factory OH bundle manager CLI) — same
* `app_process` (AOSP app_process binary) — not present
* `dalvikvm` (AOSP / KitKat-era VM launcher) — not present (and HBC's
  ART is not the kitkat dalvikvm; their `libart.so` is AOSP-14)
* `apk_runner` (Island's chroot launcher analog) — not present (Island
  built their own; HBC didn't need one)
* `WindowManagerSystemServiceXxx` test invokers — not present

---

## 3. AOSP launch-sequence map (chroot intercept points)

On real Android (and on HBC's `/system`-deployed model), `am start
com.example.HelloWorld/.MainActivity` does:

```
Step 1.  shell                    → `am` CLI binary (AOSP) or `aa` (OH)
                                    → IActivityTaskManager.startActivity binder
                                    → AMS (system_server / OH foundation)

Step 2.  AMS                      → resolveIntent → LaunchActivityItem
                                    → AppMgr.AttachApplication (if cold)
                                    → spawn-request to Zygote / appspawn / appspawn-x

Step 3.  Zygote / appspawn-x      → fork() → child specialization
                                    → ART VM (preloaded; COW after fork)
                                    → AppSpawnXInit.initChild
                                    → ActivityThread.main()
                                    → Looper.loop()
                                    → [idle, waiting for IApplicationThread call]

Step 4.  AMS                      → IApplicationThread.scheduleLaunchActivity
                                    (binder back to child process)
                                    → ClientTransaction → handleLaunchActivity
                                    → performLaunchActivity → Activity.attach
                                    → Activity.performCreate → MainActivity.onCreate
```

**Chroot Phase 1 reach by step:**

| Step | Reachable in chroot Phase 1? | Why / how |
|------|------------------------------|-----------|
| 1    | NO — `aa` binary not in chroot, factory `aa` won't reach our chroot's `AppSpawnX` socket | The only way to bypass: speak raw TLV directly to the socket (Path A2 below, via `test_tlv_client`) |
| 2    | NO — factory `libabilityms.z.so` (the only AMS on the board) has no HBC adapter hooks | Phase 2 fix: replace `/system/lib/libabilityms.z.so` per chroot SOP §6 row 1 |
| 3    | YES — appspawn-x runs in chroot, forks, runs `AppSpawnXInit.initChild`, gets to `ActivityThread.main() → Looper.loop()` | Reachable by Path A2 (TLV client) |
| 4    | NO — requires AMS-side IApplicationThread.scheduleTransaction call back into our child | Requires Phase 2 + Westlake engine code (W4-engine W6/W7) |

**Key consequence:** the `MainActivity.onCreate` line that the brief and
W4-empty acceptance grep for is **Step 4**, which is structurally
unreachable from chroot Phase 1. It can be reached only after Phase 2
(`/system/lib/libabilityms.z.so` replacement) OR via a Westlake engine
code path that hand-drives `Instrumentation.callActivityOnCreate` itself
(Path C / V2-Phone M4-PRE3 style).

---

## 4. Three Phase-1 invocation paths

Each path lists: what it validates, what it requires, the concrete
`$V3_CHROOT_HELLO_CMD` recipe, the acceptance marker, and limitations.

### 4.1 Phase 1a — env-probe-only (chroot harness default)

**What it validates:**

* `chroot $V3_CHROOT_ROOT /system/bin/sh /launch.sh` exec works
* `/proc /sys /dev` bind-mounts visible from inside chroot
* `LD_LIBRARY_PATH`, `BOOTCLASSPATH`, `ANDROID_ROOT`, `ICU_DATA` set
  correctly
* `/system/android/framework/boot.art` exists and is readable from
  inside chroot
* Channel-alive sentinel survives the launch

**What it requires:**

* Stages 0-4 of `deploy-hbc-to-dayu200-chroot.sh` PASS (artifacts under
  chroot, mounts present, launch.sh installed)
* Nothing else

**$V3_CHROOT_HELLO_CMD recipe:**

```bash
# UNSET (or empty) — harness default
unset V3_CHROOT_HELLO_CMD
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh launch
```

**Acceptance marker:**

```
[v3-chroot-launch] PWD=/
[v3-chroot-launch] LD_LIBRARY_PATH=/system/lib:/system/android/lib:...
[v3-chroot-launch] BOOTCLASSPATH=/system/android/framework/core-oj.jar:...
[v3-chroot-launch] boot.art present: /system/android/framework/arm/boot.art
```

…then clean exit 0. Verified by chroot script's Stage 6 verify
(default `MARKER='\[v3-chroot-launch\]'`).

**Limitations:**

* Does NOT exercise ART. Does NOT exercise appspawn-x. Does NOT load
  any HBC `.so`. Does NOT validate framework jars are loadable.
* Soft-bricks observable only via post-launch channel-alive sentinel
  (which the harness already checks).
* PASS here means "substrate skeleton viable" — not "ART or framework
  works".

### 4.2 Phase 1a-plus — direct appspawn-x daemon-only smoke (recommended W4-empty stretch)

**What it validates:** Phase 1a PLUS

* appspawn-x can `dlopen libart.so` from `/system/android/lib/` inside
  chroot (per LD_LIBRARY_PATH)
* `JNI_CreateJavaVM` succeeds with HBC's 9-segment BCP
* `Runtime::Init` reads BOOTCLASSPATH + boot.art and validates
* `AppSpawnXRuntime::preload()` loads AppSpawnXInit class via
  PathClassLoader from `oh-adapter-runtime.jar` (`adapter-classes.dex`
  inside)
* Phase 4 "Listening on /dev/unix/socket/AppSpawnX" — daemon stays up

**What it requires:**

* All of Phase 1a's prerequisites
* `/dev/unix` directory writable from chroot for the AF_UNIX socket
  (verify with `ls -ld /dev/unix` post-chroot — may need bind-mount or
  Phase 2 socket location override)

**$V3_CHROOT_HELLO_CMD recipe:**

```bash
export V3_CHROOT_HELLO_CMD="/system/bin/appspawn-x --socket-name AppSpawnX"
export MARKER="Phase 4: Ready to accept spawn requests"   # see main.cpp L237
# Optional: also catch ART init success
# export MARKER="ART VM started \\+ framework preloaded"  # main.cpp L234
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh launch --setenforce-0
```

(`--setenforce-0` because shell-domain appspawn-x will hit cgroup /
setuid / setcon EPERMs that Phase 1 explicitly trades fidelity to
sidestep.)

**Acceptance markers (any one):**

* `[AppSpawnX] ========================================` (banner L149)
* `[AppSpawnX] ART VM started + framework preloaded on worker pthread` (L234)
* `[AppSpawnX] Phase 4: Ready to accept spawn requests` (L237)
* `[AppSpawnX] Listening on /dev/unix/socket/AppSpawnX` (L238)

(Note: hilog markers will also appear via the `HiLogPrint` side of the
`LOGI` macro per spawn_msg.h L29-35; but stderr is more reliable in
shell-launched mode.)

**Limitations:**

* Daemon runs in foreground, blocks until LAUNCH_BUDGET_SECS timeout
  (60s default). Either: (a) accept the timeout-124 exit code as PASS
  if the marker grep succeeded BEFORE the timeout, or (b) push a
  separate "send TLV spawn then kill daemon" wrapper.
* Daemon process holds the chroot — teardown via `pkill -9` works (the
  chroot script's Teardown stage already does this).
* Sandbox / AccessToken / SELinux setcon will WARN-not-fail in `sh:s0`;
  spec accepts this per proposal §5.2.

### 4.3 Phase 1b — full spawn (cross-compile test_tlv_client + run)

**What it validates:** Phase 1a-plus PLUS

* AppSpawnX TLV protocol parser works (`magic 0xEF201234`, header,
  TLV_BUNDLE_INFO, TLV_DAC_INFO)
* `SpawnServer::run` → handler closure → `runtime.zygotePreFork()` →
  `fork()` succeeds
* Child process specialization runs through (DAC, sandbox, SELinux
  setcon, AccessToken)
* `ChildMain::run` → `applyAccessToken` → `applyDac` → `applySELinux`
  → `applyAndroidAdapter` → `launchActivityThread`
* `AppSpawnXInit.initChild` Java side fires:
  - `J_initChild_entry` (proves Java entry reached after fork)
  - `J_after_setArgV0`
  - `J_after_initCommonRuntime`
  - `J_after_initAdapterLayer`
  - `J_after_installActivityManagerStub`
  - `J_after_installPackageManagerStub`
  - `J_after_installWindowManagerStub`
  - `J_after_installInputManagerAdapter`
  - `J_after_installServiceManagerAdapter`
  - `J_after_installActivityClientControllerStub`
  - `J_after_initSystemAssetManager`
  - `J_before_invokeStaticMain target=android.app.ActivityThread`
* `ActivityThread.main()` → `Looper.loop()` → child idle (waiting for
  IApplicationThread call that never comes)
* Parent appspawn-x sends `AppSpawnResponseMsg{result=0, pid=<child>}`

**What it requires:**

* All of Phase 1a-plus's prerequisites
* Cross-compile `adapter-src/framework/appspawn-x/test/test_tlv_client.c`
  → ARM32 musl binary (one-shot host build: `${ARM32_OHOS_TOOLCHAIN}/clang
  -o test_tlv_client test_tlv_client.c`)
* Push the binary into chroot at `/system/bin/test_tlv_client`
* Drive a two-step launch: background appspawn-x, then run client
* AccessToken plumbing is fragile in `sh:s0`; child may EPERM on
  `applyAccessToken` (acceptable in Phase 1; document the failure mode)

**$V3_CHROOT_HELLO_CMD recipe (multi-line shell, fits inside launch.sh
$@):**

```bash
# Pre-step (one-time, on host): cross-compile
TOOLCHAIN=/tmp/arm32-ohos-toolchain.cmake
src=westlake-deploy-ohos/v3-hbc/adapter-src/framework/appspawn-x/test/test_tlv_client.c
clang --target=arm-linux-ohos --sysroot=$OHOS_SYSROOT -static -o test_tlv_client "$src"

# Pre-step (one-time): push into chroot
hdc file send test_tlv_client /data/local/tmp/v3-hbc-chroot/system/bin/test_tlv_client
hdc shell "chmod 755 /data/local/tmp/v3-hbc-chroot/system/bin/test_tlv_client"

# Launch (multi-line via sh -c so launch.sh can exec it)
export V3_CHROOT_HELLO_CMD='/system/bin/sh -c "
  /system/bin/appspawn-x --socket-name AppSpawnX &
  APPSPAWN_PID=$!
  sleep 3
  /system/bin/test_tlv_client /dev/unix/socket/AppSpawnX
  RC=$?
  kill $APPSPAWN_PID 2>/dev/null
  exit $RC
"'
export MARKER="J_after_invokeStaticMain|J_before_invokeStaticMain"
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh launch --setenforce-0
```

**Acceptance markers (escalating reach):**

* `[AppSpawnX] Spawn request: proc=com.example.helloworld bundle=com.example.helloworld` (parent received TLV; spawn_server.cpp)
* `[AppSpawnX] Child process started, pid=<N> bundle=com.example.helloworld` (child_main.cpp L41)
* `[AppSpawnX] Adapter layer initialized successfully` (child_main.cpp L460)
* `[AppSpawnX] [CHILD_CK] CK_BEFORE_initChild_call (about to enter Java)` (child_main.cpp L578)
* `J_initChild_entry proc=com.example.helloworld` (appLog from AppSpawnXInit.java L855)
* `J_after_initAdapterLayer` (AppSpawnXInit.java L879)
* `J_after_installActivityManagerStub` (L895)
* `J_after_installServiceManagerAdapter` (L911)
* `J_after_initSystemAssetManager` (L921)
* `J_before_invokeStaticMain target=android.app.ActivityThread` (L942)
* … then child idles in `Looper.loop()` until LAUNCH_BUDGET_SECS

**Limitations:**

* Does NOT reach `MainActivity.onCreate` (Step 4 of §3 above is missing).
* Cross-compile is one-time engineering work (~½ day) but is a code
  change technically forbidden in this scoping pass — left as a Phase 2
  / W4-engine work item to actually exercise.
* Sandbox setup, AccessToken setself, SELinux setcon will likely EPERM
  in `sh:s0` — exact failure point gives us W4-empty data.

### 4.4 Phase 1c — Westlake mini-driver (DEFERRED to W4-engine)

The V2-Phone equivalent (`aosp-libbinder-port/test/NoiceLauncher.java`,
`McdLauncher.java`, `McdProductionLauncher.java`, and the M4-PRE3
mini-ActivityThread harness that hand-drives
`Instrumentation.callActivityOnCreate`) is the existing precedent. Per
V3 V2→V3 replacement table in `V3-LAUNCH-MODEL.md` §3, this entire
launcher direction has been **archived** and the V3 design explicitly
trades it for HBC's real `ActivityThread.main()`. Reviving it for
chroot Phase 1 would re-introduce ~600 LOC of Westlake-owned launcher
code that V3-WORKSTREAMS §W3 hard-removed.

**Recommendation: defer 1c.** If Phase 1a-plus or 1b reveals that ART
+ framework do load but the Step-4 AMS-callback gap blocks all further
reach, then W4-engine (per V3-WORKSTREAMS §W4-engine) can decide
between (a) Phase 2 (port `/system/lib/libabilityms.z.so` to the board)
or (b) a Westlake-owned `LifecycleDriver` (per W4-engine borrow #2,
already scoped). 1c is not a separate Phase 1 path — it's an alias for
W4-engine.

### 4.5 What HBC does (canonical reference)

HBC's own HelloWorld path is **NOT chroot-based.** HBC writes
artifacts to `/system/...` per `DEPLOY_SOP.md` Stage 3 (94 files + 4
symlinks), reboots into a normal-init-managed system where the patched
`libabilityms.z.so` etc. are loaded by factory OH services, then runs
`bm install` + `aa start`. The reach to `MainActivity.onCreate` requires
both ends of the AMS-callback chain (HBC's patched `libabilityms.z.so`
on `/system/lib/` and HBC's appspawn-x in `appspawn:s0` domain) — which
is exactly the Phase 2 path.

### 4.6 What Island does (reference; different stack)

Island's `apk_runner.c` (`artifacts/v3-w3-westlake-stack/16.14-Island/
tests/R4-apk-runner/src/apk_runner.c`) is a Phase-1a-plus + Phase-1c
hybrid for a DIFFERENT stack. It:

1. `dlopen("libart.so")` and `JNI_CreateJavaVM` with explicit BCP
   (AOSP-stock, NOT HBC-9-segment)
2. `dlopen libandroid + libhwui + libandroid_runtime` GLOBAL
3. `dlsym("_ZN7android14AndroidRuntime8startRegEP7_JNIEnv")` and call
   `startReg(env)` — registers AOSP framework natives
4. Optionally `dlopen libhello2_jni.so` and `RegisterNatives` for their
   own `NpBinder` classes
5. `FindClass(main_class)` + `GetStaticMethodID("main",
   "([Ljava/lang/String;)V")` + `CallStaticVoidMethod`

The class invoked is `com.westlake.island.APKRunner` (their own Java
driver) — not `MainActivity` directly. APKRunner then loads the APK's
dex via PathClassLoader, instantiates the activity via reflection,
hand-drives `Activity.attach` + `performCreate` + `performStart` +
`performResume`. This is **Path C in our taxonomy** — Westlake-owned
lifecycle driver — and Island built ~1600 LOC of C + Java to do it.

We don't have a port of Island's APKRunner to HBC's stack. Porting
it would be the W4-engine "Lifecycle Driver" borrow (#2 in
V3-WORKSTREAMS §W4-engine), estimated 1 day per the borrow doc.

### 4.7 What V2-Phone did (reference; related precedent)

V2-Phone's `m3-dalvikvm-boot.sh` runs:

```bash
su 1000 -c "
    BINDER_DEVICE=/dev/vndbinder \
    LD_LIBRARY_PATH=$SM_LIB \
    $DIR/dalvikvm \
        -Xbootclasspath:$DIR/core-oj.jar:$DIR/core-libart.jar:$DIR/core-icu4j.jar:$DIR/bouncycastle.jar:$DIR/aosp-shim.dex \
        -Xverify:none -Xnorelocate \
        -Djava.library.path=$SM_LIB \
        -cp $DIR/dex/HelloBinder.dex \
        HelloBinder
"
```

This is Path A (direct dalvikvm main) for an Android-15-on-OnePlus
chroot-like deploy. `HelloBinder` is a class with `public static void
main(String[] args)` that runs `ServiceManager.listServices()` /
`getService()` smoke tests. It does NOT reach `MainActivity.onCreate`;
it doesn't need to. The whole NoiceLauncher / McdLauncher /
McdProductionLauncher chain (Path C / 1c) is layered on top for that.

HBC's stack has NO `dalvikvm` binary, so Path A is not directly
available. The nearest equivalent is Phase 1a-plus (just run appspawn-x
in standalone mode and see how far ART init gets).

---

## 5. Recommendation for today's W2 retry

**Pick: Phase 1a (env-probe-only, harness default).**

Rationale:

* **Brick-safe.** Phase 1a does zero new work compared to what agent 62
  already shipped. The chroot Stage 5 launch with no `$V3_CHROOT_HELLO_CMD`
  is the exact "exec the wrapper, print env, exit clean" path the harness
  was designed for.
* **Validates the substrate skeleton.** PASS here means chroot exec
  works, mounts visible, env wrapper installed correctly, BCP and
  ANDROID_ROOT set, channel-alive sentinel survived a launch. That's
  the minimum gate for "the deploy script's structure is sound."
* **Doesn't fabricate a MainActivity.onCreate PASS.** The brief asks
  to be honest if HBC's bundle doesn't have a chroot-compatible
  HelloWorld path. It doesn't. Phase 1a being honest about that —
  marker is `[v3-chroot-launch]`, NOT `MainActivity.onCreate` — is the
  right call.
* **Cheapest reach with most information.** ~5-10 min full deploy. If
  it PASSes, we have a clean chroot to layer Phase 1a-plus on top of.
  If it FAILs, we have unambiguous diagnostic data (no ART/framework
  noise muddying the failure signal).
* **Path forward is staged.** After 1a PASS, we can decide whether to
  layer 1a-plus (within today's window) or 1b (multi-hour
  cross-compile) or punt to W4-engine for 1c.

**Stretch goal for today (if Phase 1a PASSes and time permits): Phase
1a-plus.** Adds ~½ hour of investigation (push existing appspawn-x
binary, point env at it, observe stderr/hilog). Doesn't require any
new code (appspawn-x is already in the chroot per Stage 2 push). Gives
us first real data on whether ART + framework load inside chroot.

**Explicitly NOT today: Phase 1b (TLV client), Phase 1c
(mini-driver), Phase 2 (`/system` writes).** Each is a separate work
item with measurable engineering cost; none is in scope for this
discovery pass.

---

## 6. Concrete operator recipe (today's W2 retry)

### 6.1 Phase 1a (env-probe-only — recommended)

```bash
# Defaults: V3_CHROOT_HELLO_CMD unset, MARKER unset → harness uses
# "[v3-chroot-launch]" marker
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh all
```

Expected PASS markers (in stdout of Stage 5 launch + Stage 6 verify):

```
[v3-chroot-launch] PWD=/
[v3-chroot-launch] LD_LIBRARY_PATH=/system/lib:/system/android/lib:/system/lib/platformsdk
[v3-chroot-launch] BOOTCLASSPATH=/system/android/framework/core-oj.jar:...:/system/android/framework/oh-adapter-framework.jar
[v3-chroot-launch] boot.art present: /system/android/framework/arm/boot.art
[v3-chroot-launch] no command given; exiting 0 (env probe only)
[Stage 6] marker '\[v3-chroot-launch\]' observed in launch log
[Stage 6] no avc denials in hilog tail (good sign)
[PASS] Stage 6 PASS — verify complete; logs at /tmp/v3-chroot-deploy-<TS>/
```

If `--setenforce-0` is needed (operator decision; not strictly required
for env-probe-only since no `chcon` happens):

```bash
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh all --setenforce-0
```

### 6.2 Phase 1a-plus (stretch — appspawn-x daemon-only)

After Phase 1a PASS:

```bash
export V3_CHROOT_HELLO_CMD="/system/bin/appspawn-x --socket-name AppSpawnX"
export MARKER="Phase 4: Ready to accept spawn requests"
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh launch --setenforce-0
```

(Re-running just `launch` after `all` PASS is idempotent on the env +
launch.sh — chroot dir + mounts + launch.sh all persist.)

Acceptance: any one of the appspawn-x banner / ART-VM-started / Phase-4
markers in `$LOG_DIR/launch.log`. Daemon will block until 60s
LAUNCH_BUDGET_SECS hits; timeout exit code 124 is expected and is PASS
if the marker grep succeeded.

### 6.3 Verifier

```bash
# After Phase 1a/1a-plus run, inspect log for marker grep
LOG_DIR=$(ls -dt /tmp/v3-chroot-deploy-* | head -1)
grep -E '\[v3-chroot-launch\]|\[AppSpawnX\]' "$LOG_DIR/launch.log"
grep -iE 'avc.*denied|fatal|abort' "$LOG_DIR/launch.log" "$LOG_DIR/deploy.log"
```

### 6.4 Recovery (if anything goes wrong)

```bash
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh teardown
```

`rm -rf` the chroot. Brick-impossible by construction.

---

## 7. W4-empty acceptance proposed update (DIFF — do not apply yet)

Current `V3-WORKSTREAMS.md` §W4-empty acceptance (L225-256) bundles
"`aa start <noice-bundle>/<MainActivity>` issued" and seven stage-by-stage
markers into one criterion (L229-238). This implicitly assumes
factory OH AMS is the launch driver, which is only true for the
`/system`-deploy path. The chroot path needs phase-specific acceptance.

**Proposed restructure** (paste-ready diff for next agent to apply
after operator sign-off):

```diff
 ## W4-empty — Pure-HBC baseline spike (NEW 2026-05-19)

 ### Goal
 ...

 ### Acceptance criteria

-- [ ] Factory-clean DAYU200 (per W2-recovery procedure), stock HBC bundle
-      deployed (`scripts/deploy-hbc-to-dayu200.sh`), **zero Westlake-owned code**
-      in `/system/` (verified: `find /system/ -iname '*westlake*' -o -iname '*aosp-shim-westlake*'`
-      returns empty)
-- [ ] `aa start <noice-bundle>/<MainActivity>` (or HBC equivalent) issued; full
-      launch sequence instrumented at every stage:
-  - process spawn (`appspawn-x` fork) reached?
-  - `Application.onCreate` entered?
-  - Hilt bootstrap (`@HiltAndroidApp` annotation processor's generated
-    `Hilt_NoiceApplication.onCreate`) entered?
-  - `MainActivity.onCreate` entered?
-  - `Activity.performStart` returned?
-  - `Activity.performResume` returned?
-  - first frame submitted to display (`Choreographer.doFrame`)?
+- [ ] Deploy path declared up-front: **chroot Phase 1a / 1a-plus** (brick-
+      impossible per V3-DEPLOY-CHROOT-SOP.md) OR **/system Phase 2**
+      (hardened deploy with HBC adapter `.so` writes per V3-DEPLOY-HARDENED-SOP.md).
+      Decision recorded in findings doc §"Phase choice".
+- [ ] For **chroot Phase 1a**: `[v3-chroot-launch]` marker observed; channel-
+      alive sentinel survives the launch; ≤0 avc denials in hilog tail (warn-not-fail).
+      Acceptance reach: substrate-skeleton-viable. Does NOT include MainActivity.onCreate.
+- [ ] For **chroot Phase 1a-plus** (optional stretch): `[AppSpawnX] Phase 4:
+      Ready to accept spawn requests` marker observed. Acceptance reach: ART + framework
+      loaded inside chroot. Does NOT include MainActivity.onCreate.
+- [ ] For **chroot Phase 1b** (if cross-compile test_tlv_client + Phase 1b run):
+      `J_after_initAdapterLayer` + `J_before_invokeStaticMain target=android.app.ActivityThread`
+      markers observed in child stderr. Acceptance reach: appspawn-x spawn + child specialization +
+      ActivityThread.main → Looper.loop. Does NOT include MainActivity.onCreate.
+- [ ] For **/system Phase 2** (full HBC hardened deploy): factory-clean DAYU200,
+      stock HBC bundle deployed via `scripts/v3/deploy-hbc-to-dayu200-hardened.sh`,
+      `aa start com.example.helloworld -a .MainActivity` issued, full chain reaches:
+  - process spawn (`appspawn-x` fork) reached
+  - `Application.onCreate` entered
+  - Hilt bootstrap (`Hilt_NoiceApplication.onCreate`) entered  *(noice only)*
+  - `MainActivity.onCreate` entered
+  - `Activity.performStart` returned
+  - `Activity.performResume` returned
+  - first frame submitted to display (`Choreographer.doFrame`)
+- [ ] **Zero Westlake-owned code** verified per chosen path:
+  - Chroot: `hdc shell "find /data/local/tmp/v3-hbc-chroot -iname '*westlake*'"` returns empty
+  - /system: `hdc shell "find /system /vendor -iname '*westlake*' -o -iname '*aosp-shim-westlake*'"` returns empty
 - [ ] Stage-by-stage reach captured in
       `docs/engine/V3-W4-EMPTY-FINDINGS.md` with: first failure stacktrace,
       hilog snippet, suspected root cause class.
 - [ ] Same measurement repeated for **McD** APK (different failure mode profile).
+      *(Phase 1a/1a-plus do not exercise per-app code; this criterion is
+      meaningful only on Phase 1b or Phase 2.)*
 - [ ] Recommendation block in the findings doc, one of:
```

(Keep the three Recommendation sub-bullets unchanged; they still
apply, with the gloss that they're per-app measurements meaningful
only on Phase 1b+ or Phase 2.)

**Why this restructure matters:** the current criterion implies
`MainActivity.onCreate` is reachable on any W4-empty run. It is not.
Today's chroot Phase 1 retry will FAIL the current criterion as
written, even though it would PASS the actual goal of "measure
substrate viability without engine code." The diff splits the
criterion by deploy phase so each run can have a meaningful per-phase
PASS/FAIL verdict and we don't burn cycles re-grepping for
`MainActivity.onCreate` on runs that structurally can't reach it.

---

## 8. Open questions for human

1. **Today's deploy: chroot or hardened (`/system`)?** The brief asks
   to scope `$V3_CHROOT_HELLO_CMD` for chroot, so I assume chroot is
   today's path. If the operator preference flipped to `/system` since
   yesterday, the recommendation changes: hardened deploy → `aa start
   com.example.helloworld -a .MainActivity` → grep
   `MainActivity.onCreate` directly. The chroot path's `$V3_CHROOT_HELLO_CMD`
   question becomes irrelevant.
2. **Phase 1b cross-compile budget — is ½ day of new C work in scope
   for W4-empty?** `test_tlv_client.c` is 143 LOC of stock C; building it
   for ARM32 musl is one toolchain invocation, but it IS a code change.
   This doc lists it as "Phase 1b" because it gives the most W4-empty
   data per investigation hour, but technically the scoping pass should
   refrain from creating new binaries. If the answer is "no new
   binaries today", scrap 1b and stop at 1a-plus.
3. **`MainActivity.onCreate` marker reframing — does the human accept
   the §7 diff?** The reframing is load-bearing for honest W4-empty
   acceptance. If the human wants to insist on
   `MainActivity.onCreate` as the universal acceptance marker, the
   chroot path is no longer a Phase-1 vehicle for W4-empty — we'd need
   to commit to Phase 2 (hardened deploy + AVC-mining iteration) and
   the W2 retry becomes a 1-2 day effort, not today's 5-10 min.

---

## 9. Cross-reference index

* HBC bundle root: `westlake-deploy-ohos/v3-hbc/`
  * Launch-relevant: `bin/appspawn-x`, `etc/appspawn_x.cfg`, `etc/appspawn_x_sandbox.json`
  * NOT in bundle: `aa`, `bm`, `dalvikvm`, `app_process`, `apk_runner`
  * Test source: `adapter-src/framework/appspawn-x/test/test_tlv_client.c`,
    `adapter-src/framework/appspawn-x/test/test_init_minimal.c`
  * Test binary: `adapter-src/framework/appspawn-x/test/out/test_init_minimal`
* HBC SOP: `westlake-deploy-ohos/v3-hbc/scripts/DEPLOY_SOP.md` (Stage 4 = aa start)
* HBC launch model: `docs/engine/V3-LAUNCH-MODEL.md` (4-step chain §2)
* HBC architecture analysis: `docs/engine/CR-EE-HANBINGCHEN-ARCHITECTURE-ANALYSIS.md` §8 (MainActivity.onCreate line 83 milestone)
* HBC borrowable patterns: `docs/engine/CR-FF-HBC-BORROWABLE-PATTERNS.md`
* Chroot SOP: `docs/engine/V3-DEPLOY-CHROOT-SOP.md` §3.7 (V3_CHROOT_HELLO_CMD)
* Chroot proposal: `docs/engine/V3-CHROOT-CONTAINMENT-PROPOSAL.md` §9 Q5 (cross-Activity launch through factory AMS)
* Hardened SOP: `docs/engine/V3-DEPLOY-HARDENED-SOP.md` (Phase 2 path; Stage 6 = aa start)
* Workstreams: `docs/engine/V3-WORKSTREAMS.md` §W4-empty L208-275 (acceptance to amend)
* Chroot deploy script: `scripts/v3/deploy-hbc-to-dayu200-chroot.sh` (Stage 5 launch L719-781)
* Island reference launcher: `artifacts/v3-w3-westlake-stack/16.14-Island/tests/R4-apk-runner/src/apk_runner.c`
* Island demo: `artifacts/v3-w3-westlake-stack/16.14-Island/demo/run-live.sh`
* V2-Phone reference: `aosp-libbinder-port/m3-dalvikvm-boot.sh`
* W2 postmortem: `docs/engine/V3-W2-POSTMORTEM.md`
* hdc transport probe: `docs/engine/V3-HDC-3.2.0B-PROBE-REPORT.md`
* AppSpawnXInit IPC chain doc: `adapter-src/framework/appspawn-x/java/com/android/internal/os/AppSpawnXInit.java` L924-938 (verbatim)
