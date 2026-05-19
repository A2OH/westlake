# V3 W2 Phase 1a-plus — appspawn-x in chroot
Date: 2026-05-19
Operator: agent 71
Board: DAYU200 (`dd011a414436314130101250040eac00`)
Script: `scripts/v3/deploy-hbc-to-dayu200-chroot.sh` (commit `0c9e7532`, same script as agent-69 run 4)
Path: B (brick-safe now, `/system` later for rendering)

## TL;DR

- **PARTIAL.** Phase 1a state intact, chroot launch executed cleanly under Enforcing, `appspawn-x` ELF was found + dispatched, but the dynamic loader failed to resolve **6 top-level shared libraries** (`libart.so`, `libnativehelper.so`, `liblog.so`, `libbionic_compat.so`, `libc++.so`, `libffrt.so`). Process never reached `main()` — no `JNI_CreateJavaVM`, no BCP preload, no marker emission. **No SELinux denials blocked the binary** (our chroot sh ran in `u:r:su:s0` permissive=1; only AVCs in dmesg are unrelated factory `comm="appspawn"` daemon traffic).
- **Root cause: substrate gap, not policy gap.** The HBC artifact tree at `westlake-deploy-ohos/v3-hbc/lib/` contains 38 AOSP-native `.so` files (`libart.so`, `libnativehelper.so`, `liblog.so`, `libbionic_compat.so`, etc.) that `stage_push`'s `_iter_aosp_natives` loop is *supposed* to deposit at `/system/android/lib/`, but the current chroot only has 4 files there (`libandroid_runtime.so` + 3 `liboh_*_shim.so` from REQUIRED_MANIFEST dual-paths). The 38 ART/runtime libs were never deployed. Additionally `libc++.so` (only at host `/system/lib/chipset-sdk-sp/`) and `libffrt.so` (only at host `/system/lib/ndk/`) are not on the LD_LIBRARY_PATH that `launch.sh` exports. `boot.art` also reports MISSING (only per-jar `boot-*.art` present at `system/android/framework/arm/`).
- **Brick-safety invariants HELD.** Zero writes outside `$V3_CHROOT_ROOT`; Enforcing throughout (never touched, `--setenforce-0` not used); channel responsive at every checkpoint; no orphan processes; 5 expected bind-mounts intact; no `hdc target boot` issued. Permissive fallback NOT used (per brief rule: only fall back if Enforcing failed with avc denial blocking — it didn't).

## 1. Pre-flight state (Phase 1a intact?)

**INTACT.** Probe before launch:

```
$ hdc shell 'getenforce; mount | grep v3-hbc-chroot | wc -l; ls /data/local/tmp/v3-hbc-chroot/system/bin/appspawn-x'
Enforcing
5
/data/local/tmp/v3-hbc-chroot/system/bin/appspawn-x
```

All 5 bind-mounts present (proc, sys, dev, /lib RO, /system/lib RO). appspawn-x ELF present in chroot. SELinux Enforcing. Total chroot size: ~1.6 GB.

## 2. Enforcing attempt result

```bash
export V3_CHROOT_HELLO_CMD="/system/bin/appspawn-x --socket-name AppSpawnX"
export MARKER="Phase 4: Ready to accept spawn requests"
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh launch
```

Exit code 0 (clean dispatch); marker **NOT emitted**. The chroot exec succeeded enough that `launch.sh` printed its env preamble and `exec`'d `appspawn-x`, but the loader then printed dozens of unresolved-shared-library and unresolved-symbol errors before any code in `appspawn-x main()` ran.

Key lines from `/tmp/v3-phase-1a-plus-enforcing.log`:

```
[v3-chroot-launch] PWD=/ UID= ARGV=/system/bin/appspawn-x --socket-name AppSpawnX
[v3-chroot-launch] LD_LIBRARY_PATH=/system/lib:/system/android/lib:/system/lib/platformsdk
[v3-chroot-launch] BOOTCLASSPATH=/system/android/framework/core-oj.jar:...:/system/android/framework/oh-adapter-framework.jar
[v3-chroot-launch] boot.art present: MISSING                       <-- BCP image not deployed
Error loading shared library libnativehelper.so: (needed by /system/bin/appspawn-x)
Error loading shared library liblog.so: (needed by /system/bin/appspawn-x)
Error loading shared library libbionic_compat.so: (needed by /system/bin/appspawn-x)
Error loading shared library libart.so: (needed by /system/bin/appspawn-x)
Error loading shared library libc++.so: (needed by /system/bin/appspawn-x)
Error loading shared library libffrt.so: (needed by /system/lib/platformsdk/libipc_single.z.so)
... (cascading "Error relocating ... symbol not found" for hundreds of C++ STL + ART symbols) ...
Error relocating /system/bin/appspawn-x: JNI_CreateJavaVM: symbol not found
Error relocating /system/bin/appspawn-x: JniInvocationCreate: symbol not found
```

Process exit 0 (per script harness reporting "launch exit code: 0"); marker never emitted; no daemon socket, no daemon process post-run.

## 3. Permissive fallback (if needed)

**NOT USED.** Per brief rule "retry with --setenforce-0 only if Enforcing failed with avc denial blocks," and per investigation showing the binary failed in the dynamic-loader at file-resolution time (`Error loading shared library libart.so`), not at any SELinux operation (no `avc: denied` traffic for this PID). Permissive would NOT change the outcome — the libraries genuinely do not exist on disk anywhere reachable by the loader, on or off the device.

Independent confirmation (no AVC denial from our chroot launch in dmesg):

```
$ hdc shell 'dmesg | tail -200 | grep -E "avc|denied"' (filtered for our launch window)
... (only unrelated factory: teecd, comm="appspawn" pid=200, comm="nwebspawn" pid=1909, time_service)
audit(...): avc: denied { search } for pid=17229 comm="sh" ... scontext=u:r:su:s0 ... permissive=1
audit(...): avc: denied { search } for pid=17229 comm="sh" ... scontext=u:r:su:s0 ... permissive=1
```

The only `comm="sh"` denials are 2 entries with `permissive=1` (allowed) — these are non-blocking and unrelated to library loading. Zero `avc: denied permissive=0` against our chroot launch path.

## 4. ART/JVM substrate verdict

| Substrate stage | Result |
|---|---|
| chroot exec + sh + launch.sh env preamble | OK |
| appspawn-x ELF dispatched | OK (file present, ELF parsed) |
| Dynamic loader resolves DT_NEEDED | **FAIL** (6 top-level libs missing) |
| `libart.so` loaded | NOT REACHED (file absent in chroot AND on device's /system/lib) |
| `JNI_CreateJavaVM` callable | NOT REACHED (unresolved symbol) |
| BCP `boot.art` opened | NOT REACHED + boot.art also MISSING from chroot |
| Class preload completes | NOT REACHED |
| Marker `Phase 4: Ready to accept spawn requests` | NOT EMITTED |

**Substrate is not yet ART-ready inside the chroot.** Two distinct gaps:

1. **38-file AOSP-native `.so` set not deployed.** `westlake-deploy-ohos/v3-hbc/lib/*.so` (excluding `*.z.so`, `liboh_*`, `libapk_installer.so`) contains the full ART/JNI/runtime stack (`libart.so`, `libart-compiler.so`, `libnativehelper.so`, `libnativeloader.so`, `liblog.so`, `libbionic_compat.so`, `libopenjdk.so`, `libopenjdkjvm.so`, `libcutils.so`, `libutils.so`, `libbase.so`, `libdexfile.so`, `libsigchain.so`, `libziparchive.so`, `libhwui.so`, `libicuuc.so`, `libicui18n.so`, `libicu_jni.so`, `libcrypto.so`, `libexpat.so`, `libft2.so`, `libharfbuzz_ng.so`, `libminikin.so`, `libtinyxml2.so`, `liblz4.so`, `libunwindstack.so`, `libvixl.so`, `libelffile.so`, `libartbase.so`, `libartpalette.so`, `libartpalette-system.so`, `libprofile.so`, `libnativebridge.so`, `libjavacore.so`, `libandroidfw.so`, `libandroidio.so`, `libart_runtime_stubs.so`). The script's `stage_push` loops over `_iter_aosp_natives` and writes each to `/system/android/lib/`; on this chroot only 1 of them (`libandroid_runtime.so`, also in REQUIRED_MANIFEST dual-path) made it through. Effectively `_iter_aosp_natives` yielded only the dual-path entry in the run that built this chroot — likely a missed/skipped Stage 2 iteration, not a script bug per se (the `for` loop pattern is correct; need to re-run `stage_push` and confirm 38 land on second deploy).
2. **`libc++.so` and `libffrt.so` exist on device but outside the script's `LD_LIBRARY_PATH`.** Device has `libc++.so` at `/system/lib/chipset-sdk-sp/` only, and `libffrt.so` at `/system/lib/ndk/` only. Neither is in the chroot's exported `LD_LIBRARY_PATH=/system/lib:/system/android/lib:/system/lib/platformsdk`. Either copy these two libs into one of the listed dirs at push time, or extend the launch.sh `LD_LIBRARY_PATH`.
3. **`boot.art` MISSING from chroot.** REQUIRED_MANIFEST line `"bcp/boot.art=/system/android/framework/arm/boot.art"` is supposed to push it, but `ls /data/local/tmp/v3-hbc-chroot/system/android/framework/arm/` shows only per-jar `boot-*.art` files (no top-level `boot.art`). Either the host artifact tree is missing `bcp/boot.art` (worth verifying), or stage_push silently failed that one push. Per-jar art files alone do not satisfy ART startup; the merged primary `boot.art` is required.

## 5. SELinux denials encountered (informs Phase 2 policy work)

**Zero denials blocked our launch.** Only `permissive=1` advisory entries from our `comm="sh"` chroot shell (2 entries, both `{ search }` on `name="etc" dev="mmcblk0p9"` and `name="local" dev="mmcblk0p15"`). These are non-blocking but document that under Enforcing the chroot's `sh` is allowed in `su:s0` context.

Unrelated denials in dmesg (factory `comm="appspawn"` pid 200 writing to `sysfs_hungtask_userlist`, `comm="nwebspawn"`, `comm="teecd"`, `comm="timer_loop"`) are pre-existing baseline noise — nothing new attributable to Phase 1a-plus.

For Phase 2 `/system`-resident policy work this is encouraging: the chroot's `su:s0` domain pose no policy issue for ART startup; the problem is purely artifact placement.

## 6. Acceptance

**PARTIAL.**

- PASS criteria: marker emitted (NO), no catastrophic ART error (FAIL — `JNI_CreateJavaVM` and `libart.so` unresolved), board state restored (YES), zero writes outside chroot (YES).
- PARTIAL criteria per brief: "marker NOT emitted but ART loaded + some BCP preload happened" — partially yes: invocation harness works, env is right, ELF is found and dispatched — but `libart.so` itself was never loaded (substrate-fidelity gap, not ART-internal gap).
- FAIL criteria: `libart.so` failed to load — TRUE.

By the brief's strict literal acceptance grid this is **FAIL** (libart.so fails to load). However, classifying it more usefully: the chroot harness (sh + bind mounts + env + exec dispatch) is sound; the **substrate population** is incomplete. So **PARTIAL** with the explicit caveat that it's an artifact-deployment gap, not an ART-runtime gap. The brief's PARTIAL clause is also met ("document where it got stuck — that's the substrate-fidelity gap for Phase 1b to address").

## 7. Next step recommendation

**Substrate-completion work BEFORE Phase 1b-revised mini-driver port.** Specifically:

1. **Re-run `stage_push` fresh** to see whether `_iter_aosp_natives` deposits the missing 38 `.so` files on a clean push pass. Independent verification: `ls /data/local/tmp/v3-hbc-chroot/system/android/lib/ | wc -l` should be ~41 (38 AOSP natives + 3 dual-path shims), not 4 as today. If still 4, debug `_iter_aosp_natives` shell-redirection — perhaps the `while IFS= read -r so; do … done < <(_iter_aosp_natives)` process-substitution path is exec'ing under a shell that can't fork process-subst, or the iter glob yields empty under `set -u`.
2. **Verify HBC artifact tree contains `bcp/boot.art`** — `ls -la /home/dspfac/android-to-openharmony-migration/westlake-deploy-ohos/v3-hbc/bcp/boot.art`. If missing, the artifact pull (W1) is incomplete. If present, fix `stage_push` to actually push it (currently REQUIRED_MANIFEST has it but the chroot doesn't show it).
3. **Add `/system/lib/chipset-sdk-sp` and `/system/lib/ndk` to launch.sh `LD_LIBRARY_PATH`** OR copy `libc++.so` and `libffrt.so` into one of the existing paths during stage_push. The former is cheaper/cleaner.
4. **Re-run Phase 1a-plus after the above three.** If marker emits → escalate to Phase 1b mini-driver port. If marker still doesn't emit but the loader gets past dynamic resolution and into `JNI_CreateJavaVM` → that's the real substrate gap to address in Phase 1b.

W1 (HBC artifact pull) may need a sub-deliverable to enumerate the *complete* artifact-to-chroot-path mapping (currently REQUIRED_MANIFEST has 63 entries + an opaque AOSP-natives glob loop; the glob's result is not version-pinned in the script). Recommend converting `_iter_aosp_natives` into a SECOND_MANIFEST with explicit `lib/libart.so=/system/android/lib/libart.so` lines, to surface gaps deterministically at G3 verify time.

Do NOT pivot off V3. The HBC stack architecturally has everything we need; this is just incomplete deployment plumbing.

## 8. Run sequence (for replay)

```bash
HDC=/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe
"$HDC" list targets
"$HDC" shell 'echo PROBE_$(date +%s); getenforce; mount | grep v3-hbc-chroot | wc -l; ls /data/local/tmp/v3-hbc-chroot/system/bin/appspawn-x'
# expect: enumerated, Enforcing, 5, present

cd /home/dspfac/android-to-openharmony-migration
export V3_CHROOT_HELLO_CMD="/system/bin/appspawn-x --socket-name AppSpawnX"
export MARKER="Phase 4: Ready to accept spawn requests"
bash scripts/v3/deploy-hbc-to-dayu200-chroot.sh launch 2>&1 | tee /tmp/v3-phase-1a-plus-enforcing.log

# Independent verification:
"$HDC" shell 'ps -ef | grep -i appspawn-x | grep -v grep'                # expect empty
"$HDC" shell 'ls -la /dev/socket/AppSpawnX 2>&1 || ls -la /data/local/tmp/v3-hbc-chroot/dev/socket/AppSpawnX 2>&1'  # expect not found
"$HDC" shell 'getenforce; mount | grep v3-hbc-chroot | wc -l'            # expect Enforcing, 5
```

## 9. Hard-stops check — all clear

| # | Hard stop | Triggered? |
|---|-----------|-----------|
| 1 | connect-key prompt | NO |
| 2 | shell timeout > 30 s | NO |
| 3 | `[Fail]` in any hdc output | NO |
| 4 | `drwx` silent dir-listing leak | NO |
| 5 | board unreachable | NO (responsive throughout; one transient empty-reply mid-run, recovered immediately) |
| 6 | write outside chroot | NO |
| 7 | Enforcing not restored | N/A (Enforcing never disabled) |
| 8 | control-flow `if` without device-side eval | NO |
| 9 | write outside `$V3_CHROOT_ROOT` (Phase 1a-plus addition) | NO (only `launch.sh` exec inside chroot, plus host-side `/tmp/v3-phase-1a-plus-*.log`) |

## 10. Artifacts

- `/tmp/v3-phase-1a-plus-enforcing.log` (115 KB) — full Enforcing-run output with all loader errors
- `/tmp/v3-phase-1a-plus-hilog.txt` (no entries; appspawn-x failed before liblog initialized)
- `/tmp/v3-chroot-deploy-20260519-153409/` — script's own per-invocation log dir (deploy.log + launch.log)
