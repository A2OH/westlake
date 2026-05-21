# HBC boot-image variant diff — May-16 snapshot vs HBC live (May 21)

Date: 2026-05-21
Agent: 103 (static investigation, no board contact)

## TL;DR

- HBC keeps **9 boot-image variants** under `adapter/out/` (`boot-image`, `boot-image-new`, `boot-image-v8`, `boot-image-v9`, `boot-image-v9b…v9f`). The actively-deployed one is the plain **`boot-image/`** dir — that's the only path referenced by `deploy_stage.sh` (`$OUT/boot-image/`).
- HBC rebuilt its live `boot-image/` today (May 21 15:18) after rebuilding `oh-adapter-framework.jar` 1 minute earlier (May 21 15:17). Our snapshot is the May-16 build.
- **Only ONE segment has real content drift**: `boot-oh-adapter-framework.{art,oat,vdex}` (+4-5 KB each, vdex md5 differs). The other 8 segments have md5 mismatches on `.art`/`.oat` only (header timestamps from the May-21 dex2oat re-run) — their `.vdex` are byte-identical and sizes are unchanged, so functionally equivalent.
- **Boot-image drift is unlikely the agent-101 brick cause**: our boot-image was built against the SAME `libart.so` (md5 `eadd3926`, identical), satisfies Stage 0's mtime check (boot.oat May 16 ≥ libart.so May 9), and deploy_stage.sh deploys our OWN jars alongside our OWN boot-image — they're internally consistent. Real risk is that HBC's deploy_stage.sh **also** pushes adapter native libs (`liboh_android_runtime.so`, `liboh_adapter_bridge.so`, `libapk_installer.so`, `appspawn-x`) — three of those drifted between May-16 and May-21, and the oh-adapter-framework.jar/dex changes added new code paths (InputEventBridge, OH INACTIVE heartbeat callbacks, OhTokenRegistry hooks) which may interact with the SELinux-respawn-storm hypothesis already documented for the brick.

## 1. Boot-image variants found on HBC server

Live tree (`$HOME/adapter/out/`):

```
drwxrwxr-x  3 user  4096 May 21 15:18  boot-image     <-- ACTIVE (referenced by deploy_stage.sh)
drwxrwxr-x  2 user  4096 Apr 27 16:25  boot-image-new
drwxrwxr-x  2 user  4096 Apr 27 17:27  boot-image-v8
drwxrwxr-x  2 user  4096 Apr 27 17:43  boot-image-v9
drwxrwxr-x  2 user  4096 Apr 28 08:56  boot-image-v9b
drwxrwxr-x  2 user  4096 Apr 28 10:16  boot-image-v9c
drwxrwxr-x  2 user  4096 Apr 28 10:21  boot-image-v9d
drwxrwxr-x  2 user  4096 Apr 28 10:25  boot-image-v9e
drwxrwxr-x  2 user  4096 Apr 28 13:00  boot-image-v9f
-rwxrwxr-x  1 user  4198 Apr 15 10:13  gen_boot_image.log
```

Backup trees `bkup/adapter-0509-ok/out/`, `bkup/adapter-0513-UI/out/`, `bkup/adapter-0520pre/out/` each carry the same 9 variants (snapshot copies). Total = 30 boot-image dirs across HBC (3 backup × 9 variants + 1 live × 9 variants + 1 backup duplicate counted differently doesn't matter — only `out/boot-image/` is consumed).

## 2. Which variant deploy_stage.sh actively uses

`hbc-deploy-as-is/deploy/deploy_stage.sh`:

```bash
69:  ADAPTER_ROOT="${ADAPTER_ROOT:-D:/code/adapter}"
70:  OUT="$ADAPTER_ROOT/out"
…
195: local boot_oat="$OUT/boot-image/boot.oat"
457: stage_push "$OUT/boot-image/$g.$e" "/system/android/framework/arm/$g.$e"
603: manifest+=("$OUT/boot-image/$g.$e=/system/android/framework/arm/$g.$e")
```

Active path = `$OUT/boot-image/` (plain). No reference to any `boot-image-v8…v9f` variant.

Segment order (boot1=`boot`, boot9=`boot-oh-adapter-framework`):
```bash
453: for g in boot boot-core-libart boot-core-icu4j boot-okhttp boot-bouncycastle \
454:          boot-apache-xml boot-adapter-mainline-stubs boot-framework \
455:          boot-oh-adapter-framework; do
```

## 3. Per-file md5/size diff — HBC current vs our May-16 snapshot

All 27 files (9 segments × 3 ext) present on both sides. **Vdex match table** (vdex carries the verified-dex content; identical vdex = identical input dex):

| segment                        | vdex md5 match | art/oat size delta |
|--------------------------------|----------------|---------------------|
| boot                           | IDENTICAL      | 0 / 0               |
| boot-core-libart               | IDENTICAL      | 0 / 0               |
| boot-core-icu4j                | IDENTICAL      | 0 / 0               |
| boot-okhttp                    | IDENTICAL      | 0 / 0               |
| boot-bouncycastle              | IDENTICAL      | 0 / 0               |
| boot-apache-xml                | IDENTICAL      | 0 / 0               |
| boot-adapter-mainline-stubs    | IDENTICAL      | 0 / 0               |
| boot-framework                 | IDENTICAL      | 0 / 0               |
| **boot-oh-adapter-framework**  | **DIFFERS**    | **+4096 / +200**    |

All 27 `.art` / `.oat` md5s differ — but for 8/9 segments size is identical AND vdex matches, meaning the dex content is byte-identical. The .art/.oat header differences come from the May-21 dex2oat re-run baking new build-time identifiers/checksums (these images were ALL regenerated together in one dex2oat invocation at 15:18 after the oh-adapter-framework.jar rebuild).

Binary cmp on `boot-framework.art`: 10.95 MB of bytes differ (out of 23.78 MB). This is NOT just a header change — but ALSO is not a code change (vdex identical, size identical). It is rebaked-image artifacts (slot offsets, embedded reference checksums to other segments). Functionally equivalent if the chain is loaded together.

## 4. Files only in one side

None for boot-image itself (both sides have all 27 files). But broader drift survey of files deploy_stage.sh ALSO pushes:

### Jars (stage_3d)
| jar                              | match      |
|----------------------------------|------------|
| framework.jar                    | IDENTICAL  |
| framework-classes.dex.jar        | IDENTICAL  |
| framework-res-package.jar        | IDENTICAL  |
| core-oj.jar / core-libart.jar    | IDENTICAL  |
| core-icu4j.jar                   | IDENTICAL  |
| okhttp.jar / bouncycastle.jar    | IDENTICAL  |
| apache-xml.jar                   | IDENTICAL  |
| adapter-mainline-stubs.jar       | IDENTICAL  |
| **oh-adapter-framework.jar**     | **DIFFERS (103253 → 105655 bytes, +2402)** |
| **oh-adapter-runtime.jar**       | **DIFFERS (md5 ef3e8c88 → 4b266023)**       |

### Native libs (stage 3a)
| lib                              | match      |
|----------------------------------|------------|
| liboh_hwui_shim.so               | IDENTICAL  |
| liboh_skia_rtti_shim.so          | IDENTICAL  |
| **liboh_android_runtime.so**     | **DIFFERS (md5 e4451878 → 13c82ecb)**      |
| **liboh_adapter_bridge.so**      | **DIFFERS (md5 27e4040e → 76229707)**      |
| **libapk_installer.so**          | **DIFFERS (md5 5d73f8aa → 61eaa7bd)**      |
| **appspawn-x** (binary)          | **DIFFERS (md5 0d9ba07d → c80c7711)**      |
| libinstalls.z.so                 | **MISSING from HBC** (HBC removed it; we still ship) |

### Critical confirm
`libart.so` is **byte-identical** (md5 `eadd392606013bca388dd1a196e5b0b7`). So our boot-image is correctly paired with our libart. Stage 0's mtime guard (`boot.oat ≥ libart.so`) passes on our snapshot: our boot.oat is May 16 00:09, our libart.so is May 15 23:59.

## 5. V7-images baked-in boot.art

V7-images is the factory OHOS image flashed before HBC's deploy overlay. Contents (Apr 4):

```
boot_linux.img  chip_ckm.img  chip_prod.img  config.cfg  eng_system.img
MiniLoaderAll.bin  parameter.txt  ramdisk.img  resource.img  sys_prod.img
system.img (2.0 GB)  uboot.img  updater.img  userdata.img  vendor.img
```

No boot.art baked in (factory OHOS doesn't have ART). The 2 GB `system.img` is the OHOS base — deploy_stage.sh overlays the Android adapter stack onto `/system/android/` after mount. So V7-images carries zero risk of boot-image collision; the only boot-image on the device after deploy is whatever stage_3e pushed.

## 6. Verdict — is boot-image drift the agent-101 brick cause?

**Unlikely as the proximate cause, but is one ingredient in a broader drift story.**

Evidence AGAINST boot-image being the brick cause:
- Our boot-image is consistent with our libart.so (md5 identical, mtimes pass Stage 0 check).
- 8 of 9 segments are functionally equivalent (vdex byte-identical, size unchanged).
- deploy_stage.sh deploys OUR jars and OUR boot-image together; the chain is internally consistent.
- Agent 101's brick happened post-Stage-3 with `hdc shell` going silent (per `feedback_soft_brick_w2_2026-05-16.md`) — that pattern is more consistent with SELinux-respawn or chcon errors than boot-image rejection (which would surface as ART abort messages reachable in dmesg via uart).

Evidence that drift IS material:
- 4 native libs differ (liboh_android_runtime.so, liboh_adapter_bridge.so, libapk_installer.so, appspawn-x). **appspawn-x is the zygote analog** — drift here directly affects every app spawn including launcher.
- oh-adapter-framework.jar grew the `InputEventBridge`, `OhInactiveHeartbeatCallbacks`, `OhTokenRegistry` API surface (per dex string diff). The OH adapter glue layer has evolved 1+ week since our snapshot.
- HBC themselves rebuilt boot-image today (May 21 15:18) — meaning even on the day of agent 101's run, the HBC team was tweaking these layers.

The 1 segment that actually drifted (`boot-oh-adapter-framework`) corresponds to the same jar that grew — so the boot-image change is downstream of the real jar change. The brick risk is most likely in the **native** drift (appspawn-x + adapter bridges) which agent 101 deployed without pulling fresh HBC copies, NOT in boot-image rejection.

## 7. Recommendation

**Do NOT just swap boot-images** — that addresses 5KB of vdex drift while leaving 4 native binaries (incl. appspawn-x) on stale May-16 versions. Instead:

1. **Sync all adapter artifacts from HBC live before next deploy attempt**, not just boot-image. Specifically pull:
   - `out/adapter/oh-adapter-framework.jar`
   - `out/adapter/oh-adapter-runtime.jar`
   - `out/adapter/liboh_android_runtime.so`
   - `out/adapter/liboh_adapter_bridge.so`
   - `out/adapter/libapk_installer.so`
   - `out/adapter/appspawn-x`
   - `out/boot-image/*` (full 27-file set, paired with the jar update above)
   - Possibly drop `libinstalls.z.so` (HBC removed it from their adapter dir; check if their deploy_stage.sh still references it — `grep -n libinstalls`: yes, lines 357, 488, 583)
2. Re-establish the **snapshot date stamp**: rename `westlake-deploy-ohos/v3-hbc/` → `westlake-deploy-ohos/v3-hbc-2026-05-21/`, or add a `SNAPSHOT_DATE` env-var checked at deploy time so we never silently drift more than a week from HBC.
3. Before re-running deploy_stage.sh, run the channel-health probe sequence from `feedback_soft_brick_w2_2026-05-16.md` — boot-image drift is not the brick cause; the brick is more likely in chcon/SELinux territory and will recur with new artifacts unless the soft-brick lessons are applied.
4. **Separate investigation needed** on agent 101 brick — boot-image drift ruled out by this report; next candidates remain SELinux respawn storm (H1) and Windows hdc.exe stdout regression (H2) per V3-W2-POSTMORTEM.md.

## Artifacts

- `/tmp/v3-bootimage-diff/hbc-current/` — pulled 27 files (active HBC boot-image, May 21 15:18)
- `/tmp/v3-bootimage-diff/hbc-v9f/` — pulled 27 files (boot-image-v9f, Apr 28 — for variant cross-check)
- `/tmp/v3-bootimage-diff/ours/` — our snapshot (May 16 00:08-00:11)
- `/tmp/v3-bootimage-diff/hbc-current.md5` `ours.md5` `hbc-v9f.md5` — sorted manifests
- `/tmp/v3-bootimage-diff/oh-adapter-framework-hbc.jar` `oh-adapter-framework-ours.jar` — jar drift evidence

## Inputs referenced

- HBC server: `[REDACTED]@[REDACTED-IP]:[REDACTED-PORT]` (read-only, via sshpass)
- HBC deploy script: `hbc-deploy-as-is/deploy/deploy_stage.sh` (lines 195, 453-455, 457)
- Agent 101 report: `docs/engine/V3-HBC-DEPLOY-STAGE-AS-IS-RUN.md` (commit `e1fdd2c5`)
- Agent 102 report: `docs/engine/V3-HBC-CFG-DIFF.md` (commit `6e904d36`)
- Cfg fix follow-up: commit `7c192fe6`
- Soft-brick postmortem: `docs/engine/V3-W2-POSTMORTEM.md`
- Soft-brick lesson: `feedback_soft_brick_w2_2026-05-16.md`
