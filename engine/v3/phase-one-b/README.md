# V3 Phase 1b — chroot-spawned McD mini-driver

Quickstart for the next agent picking up Phase 1b.

## Status (2026-05-19, agent 73)

| Phase | Status | Notes |
|-------|--------|-------|
| 1b.1 spawn round-trip | **PASS** | `test_tlv_client` works; spawn() returns result=0 + child pid |
| 1b.2 Java main in child | **PARTIAL** | `J_initChild_entry` Java marker reached; `Log.i` throws UnsatisfiedLinkError → child exits before invokeStaticMain |
| 1b.3 mini-driver port | NOT STARTED | Driver class stub committed; build blocked on substrate |
| 1b.4 McD via driver | NOT STARTED | Blocked on 1b.3 |

**Substrate gap blocking 1b.3/1b.4:** `liboh_android_runtime.so` doesn't load in
spawned child because of missing `liboh_adapter_bridge.so` in the chroot's
`/system/lib/` (shadowed by RO bind-mount of host's `/system/lib`, host
doesn't have it). Pushing `liboh_adapter_bridge.so` to `/system/android/lib/`
unshadowed location makes parent load it, but parent then SEGVs during
`AppSpawnXInit.preInitTypefaceDefault`. See `docs/engine/V3-W2-PHASE-1B-PROGRESS.md`
§4 for full substrate analysis and §4.3 for fix options (A1 = 3-LOC
script change recommended).

## Files

| File | Purpose |
|------|---------|
| `src/com/westlake/engine/PhaseOneBDriver.java` | Main class (stub with full call-flow design) |
| `src/com/westlake/engine/Marker.java` | Child-safe logging bridge (AppSpawnXInit.appLog → HiLogPrint) |
| `combo.sh` | Chroot-internal launch script (operates daemon + TLV client) |
| `test_tlv_client.c` | Symlinked from HBC's source (compiled via build-test-tlv-client.sh) |
| `bin/test_tlv_client` | Compiled ARM32 OHOS PIE binary (43 KB) |
| `out/phase-one-b-driver(-dex).jar` | Built by build-phase-one-b-driver.sh (not yet present) |

## How to re-run Phase 1b.1 (working today)

```bash
cd /home/dspfac/android-to-openharmony-migration

# 1. Chroot should be up from prior agent. Verify:
HDC=/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe
"$HDC" shell 'getenforce; mount | grep v3-hbc-chroot | wc -l'
# expect: Enforcing, 5

# 2. Push the TLV client + combo.sh
bash scripts/v3/launch-mcd-chroot.sh push-tlv

# 3. Run the spawn round-trip
bash scripts/v3/launch-mcd-chroot.sh spawn-helloworld

# 4. Verify markers
bash scripts/v3/launch-mcd-chroot.sh verify

# Expected hilog markers:
#   Phase 4: Ready to accept spawn requests
#   OH binary msg received
#   Spawn request: proc=com.example.helloworld bundle=com.example.helloworld
#   Child process started, pid=...
#   [CHILD_CK] CK_BEFORE_initChild_call
#   J_initChild_entry proc=com.example.helloworld
#
# Child stderr (Java markers) at:
#   /data/local/tmp/v3-hbc-chroot/data/local/tmp/adapter_child_<pid>.stderr
```

## How to attempt Phase 1b.3 (substrate gap blocks)

```bash
# (1) Apply substrate fix A1 first — push liboh_adapter_bridge.so to BOTH
#     /system/lib/ AND /system/android/lib/ in chroot. Three-LOC change in
#     deploy-hbc-to-dayu200-chroot.sh's REQUIRED_MANIFEST or via dual-path
#     iteration. See docs/engine/V3-W2-PHASE-1B-PROGRESS.md §4.3 A1.
#
# (2) Verify child reaches `Log.i` without throwing:
bash scripts/v3/launch-mcd-chroot.sh spawn-helloworld
# Look for `Calling AppSpawnXInit.preload()...` followed by `Phase 4: Ready`,
# then in child stderr: `initChild: proc=...` (Log.i body), then
# `J_after_initCommonRuntime` through `J_before_invokeStaticMain target=android.app.ActivityThread`.

# (3) Build the driver JAR:
bash scripts/v3/build-phase-one-b-driver.sh

# (4) Push driver + McD APK:
bash scripts/v3/launch-mcd-chroot.sh push-driver
bash scripts/v3/launch-mcd-chroot.sh push-mcd-apk

# (5) Send TEXT-PROTOCOL spawn (NOT OH binary TLV; the binary protocol
#     has no targetClass field). The combo.sh + test_tlv_client today
#     uses binary TLV with hard-coded targetClass=android.app.ActivityThread.
#     For driver invocation, write a new C helper that builds:
#       procName=com.mcdonalds.app\n
#       bundleName=com.mcdonalds.app\n
#       uid=20010042\n
#       gid=20010042\n
#       targetClass=com.westlake.engine.PhaseOneBDriver\n
#       dexPaths=/system/android/framework/phase-one-b-driver.jar:/data/app/com.mcdonalds.app/base.apk\n
#       apkPath=/data/app/com.mcdonalds.app/base.apk\n
#     and sends as raw bytes to the AppSpawnX socket. Parser at
#     spawn_server.cpp:632-708 (text-protocol path).

# (6) Verify (expect SplashActivity.onCreate body in McD's own log):
bash scripts/v3/launch-mcd-chroot.sh verify
hdc shell 'hilog -x | grep -E "PhaseOneBDriver|McDMarket|SplashActivity" | tail -30'
```

## Constraints honored (self-audit)

- No Unsafe.allocateInstance, no setAccessible(true), no per-app branches
- No HBC fork (`git diff westlake-deploy-ohos/v3-hbc/` empty)
- No framework shim (no new `android.*` classes)
- Macro-shim contract: every reflective call body is actual AOSP method
- APK transparency: McD APK never repackaged
- Chroot RO bind-mount of /lib + /system/lib preserved
- All artifacts under `engine/v3/phase-one-b/` for clear ownership
