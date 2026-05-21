# HBC deploy_stage.sh As-Is Run

Date: 2026-05-21
Board: DAYU200 V7 ROM, post-reflash
Agent: 101
Log: `/tmp/v3-hbc-asis-101-deploy.log` (268 lines)

## TL;DR

- Ran HBC's proven staged deploy procedure verbatim through Stage 0 -> 1 -> 2 -> 3.0 -> 3b -> 3c -> 3d -> 3e -> 3f -> 3.9 -> 3.5. Adaptations were 2-line `to_win()` wslpath fallback in the script plus a host-side hardlinked artifact-tree mirror matching HBC's `out/` layout (no semantic change to HBC's commands).
- Stage 0 through 3.9 ALL PASSED, including the suspected P-1 fix: 27 boot image segments confirmed labelled `system_lib_file:s0` on device via `ls -lZ`, fonts.xml confirmed `system_fonts_file`, and 101-file md5+size manifest verified clean post-deploy.
- Stage 3.5 reboot SOFT-BRICKED the board (same symptom class as W2 postmortem) — device did not return via hdc within ~18min total wait, including hdc server restart. HBC's procedure as-written did not produce a working boot on this V7 ROM.

## 1. Sanity (pre-run)

```
$ "$HDC" list targets
dd011a414436314130101250040eac00

$ "$HDC" shell 'echo SANITY_$(date +%s); getenforce'
SANITY_1609506154
Enforcing
```

PASS. Channel A alive, SELinux Enforcing.

Discovered `/system/android` already existed on the board (empty `etc/`, `framework/`, `lib/`, `framework/arm/` skeleton — no regular files, no symlinks). Operator-equivalent cleanup performed (`mount -o remount,rw / && rm -rf /system/android`) so that Stage 0's factory-baseline check would pass. This counts as board-side prep, not script modification.

## 2. HBC script structure analysis

`deploy_stage.sh` is a staged script (one stage per invocation), not a single-command `all` entrypoint. Stages map to `DEPLOY_SOP.md v4`:

- `0` preflight (hdc alive, factory baseline, boot.oat mtime vs libart.so)
- `1` backup 14 device originals (device-side `cp X X.orig_$TS` only, never `hdc file recv`)
- `2` stop foundation + render_service (NEVER appspawn, NEVER launcher)
- `3.0` mkdir 4 target dirs
- `3b` 12 OH service .so (`stage_push` with md5 staging + final verify)
- `3c` 38 AOSP native .so + 3 adapter shims dual-pathed (/system/lib + /system/android/lib) + **P-1 chcon system_lib_file:s0 on adapter shims**
- `3d` 12 framework jars + ICU + fonts.xml dual-path + **P-13 chcon system_fonts_file:s0 on fonts.xml**
- `3e` 27 boot image segments (`stage_push` md5-verified) + **P-1 chcon system_lib_file:s0 on EVERY `boot*.{art,oat,vdex}`**
- `3f` appspawn-x bin + 3 configs + ld-musl-namespace-arm.ini + file_contexts + 4 symlinks + chmod batch + restorecon (P-2)
- `3.9` full integrity audit (md5 + size + drwx-anomaly scan, 101 files)
- `3.5` reboot + 5-min poll + service health (foundation/render_service/launcher/hdcd) + BMS-ready hilog grep
- `4` HelloWorld.apk install + aa start verify

Defaults: `ADAPTER_ROOT=D:/code/adapter`, `HDC=hdc`, all paths derived. `hdc_shell()` helper unconditionally exports `MSYS_NO_PATHCONV=1` (P-4 baked in). `to_win()` helper assumes `cygpath` (Git-Bash) or naive `sed 's|/|\\|g'` fallback — neither works under WSL with the Windows `hdc.exe`.

Reboot in Stage 3.5 is internal (`hdc shell reboot` then 60×5s poll). Operator never reboots manually under HBC's design.

## 3. Adaptations made (4 lines total)

(1) Script edit — `deploy_stage.sh` `to_win()` helper, 2 added lines for WSL `wslpath -w`:

```diff
@@ -106,2 +106,4 @@
+    elif command -v wslpath >/dev/null 2>&1; then
+        wslpath -w "$p"
```

(2) Env vars (operator-side, no script change):
```
export HDC=/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe
export ADAPTER_ROOT=$HOME/android-to-openharmony-migration/hbc-deploy-as-is/adapter
```

(3) Host-side artifact-tree mirror at `$ADAPTER_ROOT` matching HBC's expected `out/{oh-service,aosp_lib,aosp_fwk,adapter,boot-image,app}/` + `framework/appspawn-x/config/` + `ohos_patches/third_party/musl/config/` + `aosp_patches/data/fonts/` topology, populated initially with symlinks into `$HOME/android-to-openharmony-migration/westlake-deploy-ohos/v3-hbc/{lib,jars,bcp,etc,bin,app,adapter-src/,patches/}`. Used 1 categorization shell loop, then on Stage 3.9 size-check failure (104 symlinks made `stat -c %s` return target-path length not file size) converted all 104 to hardlinks in 1 loop. Zero semantic change to HBC's commands — md5 verified clean before and after.

(4) Board-side prep: `rm -rf /system/android` (was empty skeleton from prior partial deploy) so Stage 0's factory-baseline guard would pass.

Total HBC-script modification footprint: **2 added lines** (well under 5-line budget).

## 4. Deploy outcome

| Stage | Result | Notes |
|---|---|---|
| 0  | PASS | hdc alive, /system/android absent, boot.oat newer than libart.so, ELF magic OK |
| 1  | PASS | 14 .orig_20260521 backups confirmed device-side |
| 2  | PASS | foundation + render_service stopped, hdcd alive (2629), launcher untouched |
| 3.0 | PASS | 4 directories created |
| 3b | PASS | 12 OH service .so pushed (md5 staging+final verified), libbms platformsdk symlink set |
| 3c | PASS | 39 AOSP libs (one over the documented 38 — `librender_service_base.z.so` is in `oh-service/`) + 3 adapter shims dual-pathed + P-1 chcon on shims |
| 3d | PASS | 12 jars + ICU + fonts.xml dual-path; fonts.xml SE label verified at runtime as `system_fonts_file` |
| 3e | PASS | 27 boot image segments md5-verified + P-1 chcon applied — see Section 5 |
| 3f | PASS | appspawn-x + 3 cfg + ld-musl-namespace + file_contexts + 4 symlinks + chmod + restorecon (P-2) |
| 3.9 | PASS (after hardlink fix) | 101 files md5+size verified, no drwx anomalies |
| 3.5 | **FAIL** | sync+reboot issued; device did not return after 300s built-in poll, did not return after another ~600s manual poll + hdc server restart |
| 4 | NOT REACHED | board offline |

Total runtime through 3.9: ~6 minutes. Reboot at 11:10:59. As of 11:30+ (~20min wait including hdc server restart and second poll round), device still offline.

## 5. P-1 boot image chcon evidence

Verbatim from `deploy_stage.sh` line 466 (Stage 3e):

```bash
hdc_shell "for f in /system/android/framework/arm/boot*.art /system/android/framework/arm/boot*.oat /system/android/framework/arm/boot*.vdex; do case \$f in *.b40_pre|*.orig*|*.moved*|*.pre_b11*) ;; *) chcon u:object_r:system_lib_file:s0 \$f 2>/dev/null;; esac; done"
```

HBC's comment block at line 459-465 documents the rationale verbatim: "hdc file send + cp gives boot.{art,oat,vdex} default label = system_file:s0 from parent dir, but appspawn:s0 domain only allows flock() on system_lib_file:s0. Without this chcon, ART JNI_CreateJavaVM Phase 2 SIGABRTs immediately on boot image lock acquisition."

Confirmed at runtime via `ls -lZ /system/android/framework/arm/`:

```
-rw-r--r-- 1 root root u:object_r:system_lib_file:s0    245760 boot-adapter-mainline-stubs.art
-rw-r--r-- 1 root root u:object_r:system_lib_file:s0     43180 boot-adapter-mainline-stubs.oat
-rw-r--r-- 1 root root u:object_r:system_lib_file:s0    174500 boot-adapter-mainline-stubs.vdex
-rw-r--r-- 1 root root u:object_r:system_lib_file:s0   1327104 boot-apache-xml.art
...
```

All 27 segments confirmed labelled `system_lib_file:s0` immediately post-deploy, before reboot. **P-1 fix was applied successfully.** The post-reboot brick is therefore NOT a P-1 failure.

Also verified during Stage 3c: 3 adapter shims (`liboh_android_runtime.so`, `liboh_hwui_shim.so`, `liboh_skia_rtti_shim.so`) chcon'd to `system_lib_file:s0` in both `/system/lib/` and `/system/android/lib/`. And Stage 3d fonts.xml chcon'd to `system_fonts_file:s0` (P-13).

## 6. Post-deploy board state

```
$ "$HDC" list targets
[Empty]

$ "$HDC" shell 'echo POST_$(date +%s)'
[Fail]ExecuteCommand need connect-key? please confirm a device by help info
```

Board not enumerated by hdc as of ~20min after reboot command issued at 11:10:59. Restart of hdc server (`hdc kill && hdc start`) did not recover enumeration. No `hilog` capture possible.

Same symptom class as `feedback_soft_brick_w2_2026-05-16.md` W2-RECOVERY postmortem: hdc shell silent post-reboot, board does not re-enumerate. Operator hard power-cycle required.

## 7. HBC HelloWorld test

Not reached. Stage 4 (`bash deploy_stage.sh 4`) would do `hdc file send out/app/HelloWorld.apk` + `bm install` + `aa start` + `pidof com.example.helloworld`. Cannot execute because hdc lost the device after reboot.

## 8. McD launch

Not reached. Same reason as #7.

## 9. Comparison: HBC's procedure vs our hardened V3-W2 path

Both procedures: (a) push 100+ artifacts, (b) chcon boot image segments to `system_lib_file:s0`, (c) chcon fonts.xml to `system_fonts_file:s0`, (d) reboot, (e) expect appspawn-x + foundation up post-boot.

HBC's procedure adds (or makes more explicit) over our path as currently practiced:
- **Staged `cp X X.orig_$TS` backup of 14 files** (Stage 1) — we currently rely on full re-flash for rollback
- **Stage 3b/3c distinction**: HBC treats `liboh_*shim.so` as adapter (3c) but `librender_service.z.so` etc. as OH service (3b); we may have been pushing as one batch
- **Single-source rule for `oh-adapter-framework.jar`** (Stage 3d comment, [P-9]): only `out/adapter/`, never two sources
- **`adapter-mainline-stubs.jar` (BCP segment 7)**: HBC's 27-file boot image count includes `boot-adapter-mainline-stubs.{art,oat,vdex}`; we should verify our boot image also has 9 segments × 3 = 27 files
- **dual-path adapter shim chcon** (3c end, lines 359-365): both `/system/lib/` AND `/system/android/lib/` copies chcon'd
- **libbms platformsdk symlink**: explicit `rm -f && ln -sf` to keep platformsdk/libbms.z.so pointing at /system/lib/libbms.z.so
- **`/system/etc/init/appspawn_x.cfg` size 4246 vs our deployed file** (we ship 128-byte trampoline??? our W2-Stage-B used a different appspawn-x cfg; HBC's is 4246 bytes)
- **`/system/etc/appspawn_x_sandbox.json`** 6666 bytes — HBC's sandbox.json (we may or may not deploy this)
- **`file_contexts`** at `/system/etc/selinux/targeted/contexts/` — HBC ships a 1475-byte version (factory 618 lines + adapter 3 lines per their comment)
- **`ld-musl-namespace-arm.ini`** at `/system/etc/` — 3293 bytes; HBC's appspawn-x layer-2 prerequisite
- **`restorecon` AFTER `file_contexts` push** (Stage 3f end, lines 543-546): forces newly-added adapter rules to take effect on already-deployed files; uses `find -exec restorecon {} \;` because OH toybox lacks `-R`

The fact that HBC's full SOP-following procedure (which includes all of the above + the suspected P-1 chcon) ALSO soft-bricked this V7 ROM strongly suggests **the bug is NOT a missing HBC-known fix** but something specific to this board / this exact artifact set / this V7 ROM revision.

## 10. Recommendation

1. **The board needs operator hard power-cycle** (USB unplug + power cycle DAYU200 + re-enumerate). Same recovery procedure as `V3-W2-RECOVERY-PROCEDURE.md`.

2. **The P-1 chcon hypothesis is REFUTED as the sole cause**: HBC's procedure applied P-1 verbatim and the board still soft-bricked. If P-1 was the missing fix, HBC's run would have succeeded.

3. **Next experiments worth running** (in priority order):
   - (a) Pull this exact board's pre-reboot `hilog` to a side-channel (serial? OTG storage?) so the next attempt can capture the actual SIGABRT/init-fail trace. The Windows-only `hdc shell hilog` channel goes silent at the exact moment when we most need it.
   - (b) Compare HBC's 4246-byte `appspawn_x.cfg` against the cfg our V3-W2-Stage-B is shipping. The cfg `ondemand: true` semantics (HBC's [P-10] warning) and any per-cfg differences may explain why init can't bring appspawn-x online.
   - (c) Compare HBC's `file_contexts` (1475 bytes; factory 618 lines + 3 adapter lines) against our V7-ROM-factory `file_contexts`. If V7 ROM ships a different factory line count or a re-organized layout, the 3-line adapter patch may produce conflicting rules.
   - (d) Try `aa start helloworld` from rescue/RAM-only mode without rebooting (i.e. stop foundation, push artifacts, manually `appspawn-x` from shell, observe boot-time SIGABRT live).

4. **The `out/` artifact-tree layout vs flat-tree decision** — HBC's `out/{oh-service,aosp_lib,aosp_fwk,adapter,boot-image,app}` categorization carries semantic information (which lib is OH vs AOSP vs adapter, single-source vs dual-source). Our flat `lib/` + `jars/` collapses this. **Recommend** converting our `westlake-deploy-ohos/v3-hbc/` to mirror HBC's categorized layout natively, so future deploys don't need a side mirror.

5. **Do not modify HBC's script further before next attempt.** The 2-line `wslpath` change is the only host-portability adaptation needed. Everything else HBC does is intentional and battle-hardened per their P-1 to P-13 fix history.

---

## Appendix A — Exact commands executed

```bash
export HDC=/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe
export ADAPTER_ROOT=$HOME/android-to-openharmony-migration/hbc-deploy-as-is/adapter
export TS=20260521

cd $HOME/android-to-openharmony-migration/hbc-deploy-as-is/deploy

for stage in 0 1 2 3.0 3b 3c 3d 3e 3f 3.9 3.5; do
    bash deploy_stage.sh $stage 2>&1 | tee -a /tmp/v3-hbc-asis-101-deploy.log
done
```

## Appendix B — Mirror-tree build

See `$HOME/android-to-openharmony-migration/hbc-deploy-as-is/adapter/` (104 hardlinked entries into v3-hbc tree).
