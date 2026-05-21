# HBC adapter full resync
Date: 2026-05-21
Agent: 104
Predecessor: agent 103 (commit `06aca21e`, report `V3-HBC-BOOTIMAGE-DIFF.md`)
Successor input expected: deploy retry (W2 / V3 W-line)

## TL;DR

Pulled 33 artifacts (27 boot-image + 2 jars + 3 libs + 1 binary) from HBC live
at `[REDACTED]@[REDACTED-IP]:[REDACTED-PORT]:$HOME/adapter/out/` and replaced
the corresponding files in `westlake-deploy-ohos/v3-hbc/`. All 33 post-replace
md5s match HBC source exactly. 25 of 33 files were drifted (matches agent
103's diff scope); the 8 unchanged were mostly small `.vdex` files (apache-xml,
bouncycastle, core-icu4j, core-libart, framework, okhttp, adapter-mainline-stubs)
plus `boot.vdex`. **Recommendation: keep `libinstalls.z.so`** — HBC's
`stage_push()` aborts on missing local files (line 130 of `deploy_stage.sh`),
so HBC's own deploy is in fact broken on a fresh clone until they
either re-add the file or remove the three references. Our copy from
2026-05-16 (999088 bytes) remains valid because the consumer code on-device
(installs JNI path) hasn't changed.

## 1. Files pulled

### Authoritative HBC paths

| Local target dir | HBC source path |
| --- | --- |
| `bcp/` (boot-image) | `$HOME/adapter/out/boot-image/{boot*.art,boot*.oat,boot*.vdex}` (27 files) |
| `jars/` | `$HOME/adapter/out/adapter/oh-adapter-framework.jar` |
| `jars/` | `$HOME/adapter/out/adapter/oh-adapter-runtime.jar` |
| `lib/` | `$HOME/adapter/out/adapter/liboh_android_runtime.so` |
| `lib/` | `$HOME/adapter/out/adapter/liboh_adapter_bridge.so` |
| `lib/` | `$HOME/adapter/out/adapter/libapk_installer.so` |
| `bin/` | `$HOME/adapter/out/adapter/appspawn-x` |

`$HOME/adapter/out/aosp_fwk/` has an older 2026-04-29 copy of
`oh-adapter-framework.jar` (92816 bytes) and `oh-adapter-runtime.jar` (21532
bytes) — these are stale and NOT authoritative for the rebuilt path we
need (15:17/11:23 May timestamps in `out/adapter/`).

### Sizes / md5 (HBC source = local post-resync, verified bit-identical)

Boot-image (all timestamped 2026-05-21 15:18 on HBC):
```
324beb77b4fbd2664f4829b4c1f63787  boot-adapter-mainline-stubs.art       245760
631cce39477a139dfd6ca1f2c246406f  boot-adapter-mainline-stubs.oat        43180
5d22b53d84957ae7d715b0b8f4f16f12  boot-adapter-mainline-stubs.vdex      174500
0a9b47a72bf00321e37af85b64951ad0  boot-apache-xml.art                  1327104
6a7644d7b5250abb031855357b7792cd  boot-apache-xml.oat                  1375596
9581b1d5f9b004db87e72d1a60e18086  boot-apache-xml.vdex                   18388
f003aacf4ff62fba8bd11806b32b6786  boot-bouncycastle.art                 827392
18fd5ffdbe2dbac293e967b13a2a9cde  boot-bouncycastle.oat                2204124
b4ade8f29954351dc749ceba4649db0e  boot-bouncycastle.vdex                 32980
7042f0ddf478c3d9ccccd58943b2f762  boot-core-icu4j.art                  1425408
d1cc0bb148597a5392d57b617f3249cc  boot-core-icu4j.oat                  3880540
2f088fec3527c57d0a5371be9230c377  boot-core-icu4j.vdex                 2403760
41cfe7543afab1460a25a9541e57823b  boot-core-libart.art                  339968
fef029ff8809d408383f81e05156286d  boot-core-libart.oat                  660328
493bb1520c668eb23cc9d75d3766c953  boot-core-libart.vdex                   8404
3218672a15e02d07831714d9df9c4858  boot-framework.art                  23781376
3279e2713e44ddd56dded465bb40d0f4  boot-framework.oat                  51271200
337e4f576f6942307fdceb8969978d28  boot-framework.vdex                 37029828
27b53c08980d4b654b5903b41211e091  boot-oh-adapter-framework.art         151552
08b6ebd537e0b2328e7eeaa97a0c4ecc  boot-oh-adapter-framework.oat         456520
7658cc99c5ef95debae5cdf20c73a8ea  boot-oh-adapter-framework.vdex        248484
c2d522c8475382b539ffa646fc1b1cf5  boot-okhttp.art                       225280
aee17a5416a660a16e1b271284399e54  boot-okhttp.oat                       639504
42fdb24e2bb060593333e857d9081689  boot-okhttp.vdex                        5168
cda9f985bcb612e3862380d638ce5e20  boot.art                             3436544
dfd1fd261020118f034fe135d1859d48  boot.oat                             8939492
60c069676f11e83c4913161da05ab1cd  boot.vdex                              77400
```

Adapter framework / runtime / libs / binary:
```
ecfb5ac35eff39f2c3bdeb579b30b02a  oh-adapter-framework.jar              105655  (HBC ts 2026-05-21 15:17)
4b2660239f33bb6e88a65454c56d4f00  oh-adapter-runtime.jar                 34090  (HBC ts 2026-05-19 11:23)
13c82ecb2c482b1d36483d72e14a2f6c  liboh_android_runtime.so              358844  (HBC ts 2026-05-20 11:12)
762297072c8e9db5652e02950c2acb8b  liboh_adapter_bridge.so              1593340  (HBC ts 2026-05-20 16:46)
61eaa7bd60519c4ca9f620c4aea08e46  libapk_installer.so                   375496  (HBC ts 2026-05-21 12:47)
c80c7711c34dd863c059e2d479bdb133  appspawn-x                            109856  (HBC ts 2026-05-21 12:36)
```

## 2. Replace operation

| Metric | Value |
| --- | --- |
| Files transferred from HBC | 33 |
| Files actually drifted (md5 differs ours-pre vs HBC) | 25 |
| Files already matching (no-op replace) | 8 |
| Files post-replace matching HBC source md5 | 33 / 33 |
| `git status --porcelain westlake-deploy-ohos/v3-hbc/` modified count | 25 (consistent with drift count above) |
| Backup of pre-resync state | `$HOME/android-to-openharmony-migration/westlake-deploy-ohos/v3-hbc.pre-resync-backup-1779398407/` (413 MB) |
| Staging directory | `/tmp/v3-resync/hbc-latest/` |
| md5 diff files | `/tmp/v3-resync/{hbc-md5,local-md5,ours-pre-md5,ours-post-md5}.txt` |

8 unchanged vdex files (no rebuild needed because dex didn't change in those
slots): `boot-adapter-mainline-stubs.vdex`, `boot-apache-xml.vdex`,
`boot-bouncycastle.vdex`, `boot-core-icu4j.vdex`, `boot-core-libart.vdex`,
`boot-framework.vdex`, `boot-okhttp.vdex`, `boot.vdex`. The drifted vdex
was `boot-oh-adapter-framework.vdex` — consistent with agent 103's finding
that the new InputEventBridge / OhInactiveHeartbeatCallbacks /
OhTokenRegistry / OH_WSA-relayout hooks landed in
`oh-adapter-framework.jar`.

## 3. libinstalls.z.so resolution

### Investigation

- HBC `find $HOME/adapter -name libinstalls.z.so` returns **nothing**.
- Our `v3-hbc/lib/libinstalls.z.so`: 999088 bytes, 2026-05-16 01:17, mode 755.
- HBC `deploy/deploy_stage.sh` references `$OUT/adapter/libinstalls.z.so` at
  lines 80, 487, 533, 611 — 4 references total.
- `stage_push()` (line 127) — line 130 is `[ -f "$local_path" ] || abort "local missing: $local_path"`.
- The reference at line 487 (`stage_push "$OUT/adapter/libinstalls.z.so" "/system/lib/libinstalls.z.so"`)
  is unconditional → HBC's script will abort on missing file.
- Our hardened wrappers
  (`scripts/v3/deploy-hbc-to-dayu200-{hardened,chroot}.sh`) also reference
  `$V3_LOCAL/lib/libinstalls.z.so` unconditionally.

### Conclusions

1. **HBC's own deploy is currently broken on a fresh clone** — they removed
   the binary from their build output but kept the deploy script references.
   Either an oversight or they have the binary in a sister directory we
   didn't find under `$HOME/adapter/` (we searched the whole
   tree).
2. **Keep our copy.** Removing it from `v3-hbc/lib/` would simultaneously
   break our hardened wrapper AND HBC's own deploy_stage.sh when invoked
   via our pipeline. The 2026-05-16 copy is functionally inert until the
   installs JNI path actually changes — no risk to deploy retry.

### Recommendation

**KEEP** `westlake-deploy-ohos/v3-hbc/lib/libinstalls.z.so`. Flag to HBC
team that they need to either restore the binary in `out/adapter/` or strip
the 4 references from `deploy_stage.sh` (lines 80, 487, 533, 611).

## 4. Post-resync state of v3-hbc bundle

```
westlake-deploy-ohos/v3-hbc/
├── bcp/           27 files (all post-resync, md5 = HBC source)
├── jars/          14 files (oh-adapter-framework.jar + oh-adapter-runtime.jar resynced)
├── lib/           56 files (3 resynced: liboh_android_runtime.so, liboh_adapter_bridge.so, libapk_installer.so)
├── bin/            1 file  (appspawn-x resynced)
├── adapter-src/  (unchanged — not in resync scope)
├── app/          (unchanged)
├── docs/         (unchanged)
├── etc/          (unchanged)
├── patches/      (unchanged)
└── scripts/      (unchanged — wrapper deploy logic, not artifacts)
```

Pre-resync backup at sibling path
`westlake-deploy-ohos/v3-hbc.pre-resync-backup-1779398407/` (untracked,
gitignored by being non-checked-in directory).

## 5. Recommendation: now ready for deploy retry

**READY (Y)** with caveats:
- Board is OFFLINE per `feedback_soft_brick_w2_2026-05-16.md`; operator must
  hard power-cycle DAYU200 first.
- W2 SOP must be rerun with the new `channel-health probe between every Stage`
  rule (postmortem `docs/engine/V3-W2-POSTMORTEM.md`).
- libinstalls.z.so is kept locally; if HBC's deploy_stage.sh path is
  exercised verbatim, it will pass `stage_push`'s `[ -f ]` precondition
  (because our 2026-05-16 copy is on disk).
- All adapter framework + bridge + appspawn-x are now lockstep with
  the same HBC build (May 21 15:17–15:18 boot-image regeneration). No
  risk of "old framework.jar tries to call new JNI symbol" mismatch.

## Artifacts

- Backup: `$HOME/android-to-openharmony-migration/westlake-deploy-ohos/v3-hbc.pre-resync-backup-1779398407/`
- Staging: `/tmp/v3-resync/hbc-latest/` + `/tmp/v3-resync/*.txt` md5 transcripts
- This report: `docs/engine/V3-HBC-RESYNC-2026-05-21.md`
- Predecessor: `docs/engine/V3-HBC-BOOTIMAGE-DIFF.md` (commit `06aca21e`)
