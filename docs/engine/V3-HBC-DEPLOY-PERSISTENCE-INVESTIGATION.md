# HBC Deploy Persistence Investigation

Date: 2026-05-20
Agent: 82
Source: HBC server `[REDACTED]@[REDACTED-IP]:[REDACTED-PORT]` (READ-ONLY remote investigation, no board contact, no push)
Companion docs: `V3-DEPLOY-HARDENED-SOP.md`, `V3-HBC-ARTIFACT-MANIFEST.md`, `V3-W2-PATH-A-PERSISTENCE.md`, `V3-W2-ROM-FLASH-RECOVERY.md`, `CR-FF-HBC-BORROWABLE-PATTERNS.md`

---

## TL;DR

1. **HBC has no special persistence trick.** Their persistence model is identical to what agent 80 already empirically confirmed in `V3-W2-PATH-A-PERSISTENCE.md`: `/system` is a plain ext4 partition on `/dev/block/mmcblk0p7`, soft reboot preserves writes, **no overlay / no dm-verity / no boot-time auto-restore service**. There is no init-side script that re-applies the adapter stack at every boot. SELinux xattrs are persisted into the inode by `restorecon`/`chcon` and survive reboot (HBC's `DEPLOY_SOP.md` line 178 states this explicitly).
2. **HBC's "recovery from brick" path is the same ROM flash we already documented** (`V3-W2-ROM-FLASH-RECOVERY.md`). HBC keeps the full 4 GB `V7-images/` set on disk at `$HOME/D200/V7-images/` (15 partition images, MiniLoaderAll, parameter.txt, config.cfg) plus the gitee HiHope RKDevTool flash tool. After a brick they flash via Windows-side `RKDevTool.exe` v3.30 + DriverAssitant v5.1.1, MASKROM/Loader via VOL+ button, then re-run the 94-file `deploy_stage.sh`. They expect to "ship a custom system.img" only when distributing a known-good baseline to a fresh board.
3. **Agent 79's hard-power-cycle wipe symptom is NOT a property HBC defends against.** HBC's discipline is "use `hdc shell reboot` (soft reboot) only; never hard power-cycle a board with a partial deploy" — sedimented as `DEPLOY_SOP.md` Stage 3.5 line 193 (`hdc shell "sync; reboot"`). The wipe agent 79 observed is most plausibly explained by either (a) eMMC write-cache loss on uncontrolled power-loss (no `sync` flush completed), (b) recovery-mode invocation from the VOL+/RESET button chord during power-on, or (c) the kernel re-asserting `ro` and then a defective userdata fsck that masked the writes. None of these is testable without another hard cycle (brick risk).

**Net implication:** our hardened SOP at `V3-DEPLOY-HARDENED-SOP.md` is structurally correct (it already cites HBC's `DEPLOY_SOP.md` verbatim, uses `stage_push()`, `chcon_verify`, etc.). The only architectural redesign needed is to **operationally prohibit hard power-cycle from W2 retries** until cold-boot persistence is separately characterized — this is already documented in `V3-W2-PATH-A-PERSISTENCE.md` §8 item 4 but should be elevated to a hard SOP rule.

---

## 1. Deploy scripts/docs found on HBC server (paths + brief content)

All from `$HOME/adapter/deploy/` (HBC's authoritative deploy directory; CLAUDE.md line 39 declares it the *unique* path for any hdc operation — `build/` is explicitly NOT for deploy).

| File | LOC | Date | Role |
|------|----:|------|------|
| `DEPLOY_SOP.md` | 247 | 2026-05-08 | **AUTHORITATIVE SOP v4** — 13-stage flow, "全局三条" (3 abort conditions), 13-file backup, 94-file push manifest, P-1 through P-13 sediment lessons. UNCHANGED since 2026-05-08 (same content as our snapshot at `westlake-deploy-ohos/v3-hbc/scripts/DEPLOY_SOP.md`). |
| `deploy_to_dayu200.sh` | 789 | **2026-05-19** (NEWER than our snapshot) | Incremental/partial deploy. As of 2026-05-19 they removed `--skip-libskia`, `--skip-test-libs`, `--uninstall`, and made `--only-files=<csv>` mandatory. Won't run without explicit file list. Full deploy is now diverted to `deploy_stage.sh` only. |
| `deploy_stage.sh` | 773 | 2026-05-11 | **Staged full deploy** (full new deploy via `bash deploy_stage.sh <stage>`). 13 stages (0/1/2/3.0/3b/3c/3d/3e/3f/3.7/3.9/3.5/4). Each stage uses `stage_push()` = staging dir + md5 round-trip + stat-regular-file probe + cp. Same as our snapshot. |
| `deploy_cmd.txt` | 462 | 2026-05-07 | Manual command sequence cheatsheet (literal `hdc file send ... && hdc shell "cp ..."` lines). Mirrors deploy_stage.sh; used when operator wants to copy/paste line-by-line. |
| `deploy_to_dayu200 .sh.bk` | 778 | 2026-05-07 | Backup of the pre-2026-05-19 form (kept for diff). |
| `README.md` | 173 | 2026-05-08 | The HBC-facing "device path table" — every out/-subdir → /system/-target mapping (5 + 7 + 30 + 10 + 24 + 1 = 94 files), with appspawn-x init config sources, "deploy 前置条件" (mount remount,rw), "常用命令" (`bash deploy_to_dayu200.sh`, `--apk-only`, etc.). |
| `../restore_after_sync.sh` | 1383 | 2026-05-20 | **NOT a device-side restore.** Restores AOSP/OH SOURCE TREE modifications after `repo sync` on the ECS build machine. No `hdc` calls. Pure build-side. |

Also pulled for context:
- `$HOME/D200/HiHope_DAYU200/烧写工具及指南/windows/烧录指导文档.md` — gitee-sourced flash guide (RKDevTool + MASKROM/Loader procedure).
- `$HOME/D200/V7-images/parameter.txt` + `config.cfg` — Rockchip partition map (14-partition GPT, 2 GB system, 1.4 GB userdata).

---

## 2. HBC's persistence model (empirical, from their scripts/docs)

**Verdict: PLAIN-PARTITION + RESTORECON-FOR-XATTR + SOFT-REBOOT-ONLY discipline.** No special mechanism beyond ext4's normal durability + SELinux xattr persistence.

Evidence (cited line-by-line from HBC sources):

| Question | HBC source | Answer |
|----------|-----------|--------|
| Where do HBC pushes land? | `DEPLOY_SOP.md` Stage 3b-3f, `README.md` device-side-tree (lines 107-143) | `/system/lib/` (10 files) + `/system/lib/platformsdk/` (5 files) + `/system/android/lib/` (41 files dual-path) + `/system/android/framework/` (12 jars + `arm/` subdir with 27 boot-image files) + `/system/etc/init/appspawn_x.cfg` + `/system/etc/appspawn_x_sandbox.json` + `/system/etc/fonts.xml` + `/system/etc/ld-musl-namespace-arm.ini` + `/system/etc/selinux/targeted/contexts/file_contexts` + `/system/bin/appspawn-x`. **All into `/system`. None into `/data/local/tmp/` for persistence — `/data/local/tmp/stage/` is staging only, files are immediately `cp`'d to `/system`.** |
| How is /system made writable? | `deploy_to_dayu200.sh` line 404-405, `deploy_cmd.txt` line 2 | `hdc shell "mount -o remount,rw /"` AND `mount -o remount,rw /system` (HBC tries both — only `/` actually succeeds on DAYU200 because `/system` is the rootfs, not a separate mount; per agent 80 `V3-W2-PATH-A-PERSISTENCE.md` line 31). |
| How do writes survive reboot? | `DEPLOY_SOP.md` Stage 3.5 line 193 | `hdc shell "sync; reboot"` then poll for hdc to come back. **No init script, no boot-time re-apply, no recovery hook.** Standard ext4 + sync + soft reboot. |
| How do SELinux xattrs survive reboot? | `DEPLOY_SOP.md` line 178 (explicit) | "restorecon 把 xattr 持久化到 filesystem inode，reboot 不丢，无需重复。只有再次 push 覆盖文件才需再跑。" — restorecon persists xattrs to the inode, reboot doesn't lose them, no need to repeat. |
| Is there a boot-time auto-restore? | full grep of `$HOME/adapter/{deploy,framework/appspawn-x/config}/` | **No.** The only init-side cfg is `/system/etc/init/appspawn_x.cfg` which declares `appspawn-x` as an `ondemand: true` service (started on first AMS connect to its socket). It does NOT re-apply any /system files. |
| Hard-power-cycle defense? | none | HBC's SOP says `sync; reboot` (soft only). Their "abort conditions" (line 6-10) trigger on `connect-key` / `timeout` / `[Fail]` / `drwx` — they `STOP` and do NOT issue any reboot/recovery. No defense against operator-induced hard power-cycle during deploy. |
| What if board bricks? | `DEPLOY_SOP.md` does not address brick recovery. `D200/HiHope_DAYU200/烧写工具及指南/`. | Hand off to physical USB reflash via RKDevTool (Windows-only, MASKROM via VOL+ button + USB-C unplug/replug). Same procedure we documented in `V3-W2-ROM-FLASH-RECOVERY.md`. |

---

## 3. HBC's clean-board → running-HelloWorld procedure (step-by-step)

Cited from `DEPLOY_SOP.md` + `deploy_stage.sh` + `README.md` "部署前置条件":

**Pre-conditions (Stage 0):**
1. ECS build complete; `out/{adapter,aosp_lib,aosp_fwk,oh-service,boot-image,app}/` populated.
2. `bash build/pull_ecs_artifacts.sh` synced artifacts to local Windows `D:\code\adapter\out\`.
3. DAYU200 powered on, factory-baseline (`/system/android` does NOT exist), `hdc list targets` non-empty, `hdc shell "echo alive"` works.
4. Local `out/boot-image/boot.oat` mtime ≥ `out/aosp_lib/libart.so` mtime (anti-drift gate; G2.14ak fix supersedes the older hardcoded 23760896 size check).

**Stage 1 — Backup (device-side only).** `hdc shell` `cp X X.orig_${TS}` for the 13 files about to be overwritten. **No `hdc file recv`** (P-5 — bidirectional copy would pollute the local `out/` mirror).

**Stage 2 — Stop 2 services only.** `begetctl stop_service foundation` + `begetctl stop_service render_service`. **NOT** `stop_service appspawn` (would break hdc), **NOT** `kill launcher` (foundation takes it down).

**Stage 3.0 — mkdir 5 dirs.** `/system/lib/platformsdk` (factory may exist, idempotent), `/system/android/lib`, `/system/android/framework/arm`, `/system/android/etc/icu`, `/data/local/tmp/stage`. `/system/etc/init` is factory, NOT created.

**Stage 3b-3f — Push 94 files via staging.** For each file: `hdc file send <local> /data/local/tmp/stage/<basename>` → `stat -c '%F' == "regular file"` (hdc 造目录 quirk guard) → md5 staging vs local → `cp /data/local/tmp/stage/<basename> <dst>` → md5 final vs local. After each substage, `chcon` to the right label and verify (`ls -lZ`). Specific chcon batches: `system_lib_file` for boot-image and adapter shims; `system_fonts_file` for fonts.xml; `restorecon` for `/system/bin/appspawn-x` and `find /system/android/lib -exec restorecon {} \;`.

**Stage 3.9 — Full integrity scan.** Manifest md5+size compare; `find -type d` sentinel on 5 critical dirs to catch hdc-造目录 quirk.

**Stage 3.5 — Soft reboot + health.** `hdc shell "sync; reboot"`, poll `hdc shell "echo alive"` up to 300s, sleep 30s, verify `pidof foundation/render_service/com.ohos.launcher/hdcd` all non-empty, scan hilog for `BMS.*ready`.

**Stage 4 — APK install + launch.** `hdc file send out/app/HelloWorld.apk /data/local/tmp/`, `bm install -p`, `bm dump -n com.example.helloworld`, `aa start ...`, `pidof com.example.helloworld`.

Total wall-clock for a clean factory baseline → running HelloWorld: ~10-15 minutes for the 94-file push + 5 min reboot + 1 min APK install.

---

## 4. ROM/firmware assumptions

HBC **does** ship a custom system.img — but only as a **baseline reset image**, not the day-to-day deploy primitive.

| Aspect | What HBC actually has |
|--------|----------------------|
| Custom system.img? | YES — `$HOME/D200/V7-images/system.img` (2 GB) is a factory OpenHarmony V7.0.0.18 ROM (not HBC-modified — vendor stock build dated 2026-04-04). |
| Custom-built ROM? | NO. The V7-images set is the same factory OH 7.0.0.18 build agent 54 documented at `V3-W2-ROM-FLASH-RECOVERY.md`. HBC builds individual `.so`/`.jar`/boot-image artifacts incrementally and pushes them on top of factory `/system`. They do NOT rebuild and ship a modified system.img. |
| When do they flash the full ROM? | Only when the board is bricked (their CLAUDE.md does not mention any other trigger). Day-to-day they assume "factory-baseline /system + N incremental deploys" — soft reboots between iterations. |
| Full vs incremental deploy ratio? | Day-to-day they run `deploy_to_dayu200.sh --only-files=...` for small iterations (1-5 files); they run `deploy_stage.sh` end-to-end only after a brick recovery or major change (boot image rebuild). |
| Boot partition modifications? | None — `boot_linux.img` + `ramdisk.img` + `uboot.img` come from factory; HBC does not patch them. All adapter init happens via `/system/etc/init/appspawn_x.cfg` (which the deploy adds to factory `/system/etc/init/`). |

**Implication:** HBC does NOT solve the brick problem by "ship a custom baseline ROM with adapter pre-installed." They keep the factory ROM as the brick-recovery image, and re-run their 94-file deploy after every flash.

---

## 5. Diff vs our `deploy-hbc-to-dayu200-hardened.sh` approach

Our hardened SOP at `V3-DEPLOY-HARDENED-SOP.md` already cites HBC's `DEPLOY_SOP.md` as source-of-truth and replicates the staging-push + chcon-verify pattern. The diff is mostly **additive** (our hardening) not divergent. Key items:

| Concern | HBC original | Our hardened script | Diverge? |
|---------|--------------|---------------------|---------:|
| Staging push template | `stage_push()` (md5 ×2 + stat regular-file check) | `stage_push()` (same + `test -s` Fix-B gate) | + |
| chcon with verification | `chcon` then `ls -lZ` grep (in Stage 3 substages) | `chcon_verify()` helper, called after EVERY chcon batch | + |
| Channel-alive probe | One-shot at preflight + after reboot | `_alive_probe()` at entry+exit of EVERY stage; triple at Stage 3.8 | + |
| Stage 2 (stop services) | Run always | Skipped by default (`SKIP_STAGE_2=1`); explicit `--no-skip-stage-2` to enable | ≠ (we believe avoiding it reduces brick risk; HBC's evidence is that it works) |
| Missing-artifact fail-fast | `push_file` silently `SKIP (not found): ...` returns 0 | `_verify_required_artifacts()` at Stage 0 fail-fast if any required entry missing | + |
| Boot-image md5 verify | Always; auto re-push on mismatch | Same | = |
| drwx sentinel | After 3b-3f via Stage 3.9 | Per-stage + final at Stage 3.9 | + |
| Reboot guard | `hdc shell "sync; reboot"` at Stage 3.5; no pre-checks | Stage 4 = reboot, gated behind Stage 3.8 (3× alive probe) + Stage 3.9 (manifest verify) | + |
| chcon snapshot/restore | None — chcon failure has no rollback | Gate 8 `chcon_snapshot_init` + `restore-chcon` subcommand | + |
| Atomic file swap for live .so | Direct `cp` to /system | Gate 9 atomic `.new`+`mv` for 17 live-service .so | + |
| Mount-restore on resume | Single `mount -o remount,rw / \|\| true` | Gate 10 `_restore_mounts` Island-style probe | + |
| Photos force-stop | None | Gate 11 `aa force-stop com.ohos.photos` before display stages | + (Westlake context-specific) |
| Launch timeout | None | Gate 12 `timeout 60s` wrap on `aa start` | + |
| processdump probe | None | Gate 13 preflight check | + |
| Reboot type | `hdc shell reboot` (soft) | `hdc target boot` (soft) | ≠ (HBC uses shell `reboot`; we use `hdc target boot`. Both are soft; agent 80 verified `hdc target boot` preserves /system.) |
| Hard-power-cycle policy | Not documented | Not in script (but agent-72-79 chain identified as brick risk) | gap — should be elevated to a hard SOP rule |
| `--only-files=<csv>` mandatory for incremental | Yes (HBC 2026-05-19) | No — our hardened script does not enforce; runs `all` by default | ≠ (HBC's discipline is stricter — we should consider porting) |

**Total divergence points: 2 substantive (Stage 2 default behavior, hard-power-cycle policy gap). All other diffs are pure hardening additions on top of HBC's pattern.**

---

## 6. Implications: can we adopt HBC's persistence model? Or do we need to design around it?

**Adopt it. There is nothing to design around. HBC's model is: ext4 + restorecon-xattr-persistence + soft-reboot-only.** Our hardened SOP already implements this.

The one thing agent 79's observation surfaces is operator discipline, not architecture:

- **Hard power-cycle is brick-equivalent.** HBC's SOP doesn't say this out loud, but the reason their SOP doesn't address it is because they treat it as out-of-bounds operator action (their bash history shows they `cp ~/adapter ~/bkup/adapter-DATE` aggressively but never mention a hard cycle as a normal step).
- **Cold-boot wipe is plausibly eMMC-cache loss + recovery-mode invocation, not a kernel-side overlay-drop.** Without a hard cycle reproduce we can't rule out (b)/(c) from §3 above, but no architectural mitigation is available without replacing the partition layout (which would require building a custom system.img — and HBC themselves don't do that).

Our `V3-W2-PATH-A-PERSISTENCE.md` already proved soft-reboot persistence empirically (`V3_PERSIST_TEST_177...` marker survived intact). We can rely on this.

---

## 7. Recommendations for V3 deploy architecture

**No architectural redesign required.** Operational/SOP-level changes only:

1. **Elevate "never hard power-cycle during W2 retry" to a hard rule** in `V3-DEPLOY-HARDENED-SOP.md` §4 (HBC 全局三条 abort conditions). Currently `V3-W2-PATH-A-PERSISTENCE.md` §8 item 4 mentions this as an implication; promote it to first-class SOP text alongside the existing 5 abort conditions.
2. **Adopt HBC's `--only-files=<csv>` mandatory pattern for incremental.** Their May-19 change makes `deploy_to_dayu200.sh` refuse to run without an explicit file list (to prevent accidental full-deploys via partial-state scripts). Port to our hardened script: refuse `bash hardened.sh all` without `--all-confirmed` flag, force operator to explicitly choose between "full reset" and "explicit file list."
3. **Add a Stage 0.5: device-side `/system` health snapshot.** Before any push, `hdc shell "find /system -type f | wc -l"` to detect surprising state changes since the last deploy session. This would have caught agent 79's wipe symptom one step earlier than the deploy itself failing.
4. **Synchronize our snapshot at `westlake-deploy-ohos/v3-hbc/scripts/`.** HBC's `deploy_to_dayu200.sh` evolved 2026-05-19 (removed `--skip-libskia`, `--skip-test-libs`, `--uninstall`, made `--only-files=<csv>` mandatory). Pull the May-19 form and update our archived snapshot. **Note:** `DEPLOY_SOP.md` and `deploy_stage.sh` are byte-identical to our snapshot — only the incremental wrapper changed.
5. **Document the V7-images cache location on HBC server.** Add a footnote to `V3-W2-ROM-FLASH-RECOVERY.md` §2.2: the same 15-file V7-images set is mirrored at `[REDACTED]@[REDACTED-IP]:$HOME/D200/V7-images/` (4.0 GB), with a backup ZIP at `$HOME/D200.zip`. Operators with HBC SSH access can pull from there if `[REDACTED-IP]:/data/share/rk3568/` is unavailable.
6. **No need to ship our own system.img.** HBC does not do this and we should not either. Custom-baseline ROMs introduce build-time / signing / fastboot-flash divergence with no benefit over "factory ROM + 94-file deploy + soft reboot."

---

## 8. Open questions for human

1. **Should we replicate HBC's `repo sync`-style ECS build workflow?** Their `restore_after_sync.sh` (1383 LOC) is a substantial source-tree restoration system that re-applies all AOSP/OH patches after `repo sync`. We don't have an equivalent for Westlake's V3 path; our W1 pulled HBC's pre-built artifacts but not their build chain. If we ever need to rebuild any of HBC's `out/aosp_lib/*.so` ourselves, this gap matters.
2. **Is there a write-cache flush probe we can add?** A way to verify eMMC cache is drained before the operator power-cycles — e.g., `hdc shell "sync; sleep 5; echo 3 > /proc/sys/vm/drop_caches; sync"` then a per-partition `dd if=/dev/block/mmcblk0p7 bs=4096 count=1` to force a controller-side read-back. Not in HBC's SOP. Worth investigating if hard-cycle persistence becomes important.
3. **HBC's bash history shows they never run `hdc shell reboot -f` (force reboot).** Their reboots are always graceful via `hdc shell "sync; reboot"`. Do we want to enforce this in our hardened script? (Currently we use `hdc target boot` which is roughly equivalent but routes through a different code path.)
4. **The 4 GB `D200.zip` on HBC server is a verbatim copy of `D200/` (V7-images + HiHope_DAYU200 + flash tools).** Should we mirror it into our `artifacts/` tree for redundancy in case the HBC server becomes unavailable? (Pulling 4 GB is significant but one-off.)
5. **Should agent 79's hard-power-cycle observation be elevated to a tracked GitHub issue?** It's currently captured in `feedback_soft_brick_w2_2026-05-16.md` and the agent-79 task report. A standalone issue (e.g., #639 "DAYU200 cold-boot persistence — characterize") would let us file a real reproduce attempt under controlled conditions (after V3-W2 is stable enough to brick-experiment on).

---

## 9. Investigation artifacts location

Pulled HBC files for this investigation are at `/tmp/v3-hbc-deploy-investigation/` (not committed; one-shot reference):

```
adapter/
  deploy/
    DEPLOY_SOP.md          (247 LOC, 2026-05-08, identical to our snapshot)
    deploy_to_dayu200.sh   (789 LOC, 2026-05-19, NEWER than our 2026-05-16 snapshot)
    deploy_stage.sh        (773 LOC, 2026-05-11, identical to our snapshot)
    deploy_cmd.txt         (462 LOC, 2026-05-07, manual command cheatsheet)
    README.md              (173 LOC, 2026-05-08, device-side path table)
  readme.txt               (sync rules between local Windows D:\code\adapter\ and ECS)
  CLAUDE.md                (project-level AI context: directory layout, single-source rules)
  build/
    config.sh, gen_boot_image.sh, restore_after_sync.sh, README.txt, readme.txt
  restore_after_sync.sh    (top-level wrapper; source-tree restoration, NOT device-side)
HiHope_DAYU200/
  README.md, 烧写工具及指南/windows/烧录指导文档.md, docs/烧录指导文档.md
V7-images/
  parameter.txt, config.cfg
```

To re-pull: `sshpass -p '<pass>' ssh -p [REDACTED-PORT] [REDACTED]@[REDACTED-IP] 'cd $HOME && tar czf - adapter/deploy adapter/readme.txt adapter/build/config.sh ...' | tar xzf - -C /tmp/v3-hbc-deploy-investigation/`.

---

## 10. Document metadata

- Authored: 2026-05-20 by agent 82.
- Time-boxed: ~1 hour (within 1-2h mandate).
- Source data: 5 deploy scripts + 1 SOP + 1 README + 1 device-side parameter map (all READ-ONLY pull from HBC). No board contact. No write to HBC server.
- Local commit only; no push.
- Supersedes nothing; complements `V3-DEPLOY-HARDENED-SOP.md` and `V3-HBC-ARTIFACT-MANIFEST.md`.
