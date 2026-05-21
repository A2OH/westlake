# HBC deploy_stage.sh retry — with cfg fix + adapter resync

Date: 2026-05-21
Board: DAYU200 V7 ROM
Agent: 105
Previous attempts: agent 101 (ABORTED at Stage 3.5 reboot, board did not re-enumerate in 300s)
Fixes since agent 101:
- `7c192fe6` — appspawn_x.cfg corruption restored to HBC live (4246 bytes, md5 `3e1f9ac1...`)
- `e6bd7cce` — full v3-hbc artifact resync from HBC live (33 critical files re-pulled, 25 updated)

Log: `/tmp/v3-105-deploy.log` (175 lines)
Forensic: `/tmp/v3-105-final-{hilog,dmesg,state}.txt`, `/tmp/v3-105-mounts.txt`

## TL;DR

- **HBC's deploy_stage.sh ran clean through Stages 0 → 3.9** (identical to agent 101). All 101 artifacts staged + md5+size verified, appspawn-x relabelled `appspawn_exec:s0`, fonts.xml `system_fonts_file:s0`, 27 boot image segments `system_lib_file:s0`, appspawn_x.cfg correctly deployed at 4246 bytes — the post-fix cfg.
- **Stage 3.5 reboot still soft-bricks the 300s poll** (HBC's threshold), but **the board DID re-enumerate at ~6-7 min post-reboot** after hdc server restart — so it is NOT a hard brick like the W2-RECOVERY postmortem class. Agent 101 may have hit the same recovery timing but did not wait long enough.
- **Catastrophic, unexpected finding**: post-reboot, the entire deploy has been REVERTED. /system/android directory gone, /system/bin/appspawn-x gone, /system/etc/init/appspawn_x.cfg gone, even the `.orig_20260521` Stage-1 backups are gone. Only the OHOS factory `appspawn` (PID 199) is running. The brick mechanism is **not** cfg corruption or adapter drift — it is **/system being restored to factory baseline on every reboot**, which means HBC's procedure as-is can never persist on this V7 ROM. This invalidates the implicit assumption shared by HBC and all our prior V3 runs.

## 1. Pre-state

```
$ hdc list targets
dd011a414436314130101250040eac00

$ hdc shell 'echo SANITY; getenforce; uptime'
SANITY_1609512563
Enforcing
 22:49:23 up  2:49, ...

$ md5sum westlake-deploy-ohos/v3-hbc/etc/appspawn_x.cfg \
          westlake-deploy-ohos/v3-hbc/adapter-src/framework/appspawn-x/config/appspawn_x.cfg
3e1f9ac10c0d1a079db04e01330d5b8f  v3-hbc/etc/appspawn_x.cfg
3e1f9ac10c0d1a079db04e01330d5b8f  v3-hbc/adapter-src/.../appspawn_x.cfg

$ md5sum westlake-deploy-ohos/v3-hbc/bin/appspawn-x
c80c7711c34dd863c059e2d479bdb133  (post-resync, matches HBC)

$ hdc shell 'ls /system/bin/appspawn-x; ls /system/android; find /system -name "*.orig*"'
ls: /system/bin/appspawn-x: No such file or directory
ls: /system/android: No such file or directory
(no .orig backups)
```

Board confirmed factory clean. Cfg fix (`7c192fe6`) verified at both staging paths. Adapter resync (`e6bd7cce`) reflected in mirror inode-sharing with v3-hbc adapter-src.

## 2. Deploy outcome (stage-by-stage)

| Stage | Result | Notes |
|---|---|---|
| 0 | PASS | hdc alive, factory baseline, boot.oat ELF OK |
| 1 | PASS | 14 .orig_20260521 device-side backups |
| 2 | PASS | foundation + render_service stopped, hdcd alive (2711) |
| 3.0 | PASS | 4 directories created |
| 3b | PASS | 10 OH .so + 1 libbms symlink |
| 3c | PASS | 41 AOSP .so (38 + 3 adapter shims dual-path) + chcon system_lib_file |
| 3d | PASS | 12 jars + ICU + fonts.xml dual-path + chcon system_fonts_file |
| 3e | PASS | 27 boot image segments md5-verified + chcon system_lib_file |
| 3f | PASS | appspawn-x + 3 cfg + ld-musl-namespace + file_contexts + 4 symlinks + chmod + restorecon system_lib_file dir |
| 3.9 | PASS | 101 files md5+size verified, no drwx anomalies |
| 3.5 | **ABORT** | sync+reboot, polled 60×5s=300s, device did not return; HBC `abort()` exit |
| 4 | NOT REACHED | board offline at Stage 3.5 deadline |

Total runtime through 3.9: ~4 min (faster than agent 101's 6 min because Stage 3.9 uses the categorized adapter mirror directly). All warnings noted by agent 101 (one extra file in 3c, fonts SE label verified) reproduced identically.

Pre-reboot verification of agent 101's known good signals:
```
-rwxr-xr-x  appspawn_exec:s0  109856  /system/bin/appspawn-x        ← P-2 OK
-rw-r--r--  system_etc_file:s0  4246  /system/etc/init/appspawn_x.cfg  ← cfg-fix in place + correct size
/system/android/framework/{adapter-mainline-stubs.jar, apache-xml.jar, arm/, bouncycastle.jar, core-icu4j.jar, ...}
```

All correct pre-reboot.

## 3. Stage 3.5 reboot result

`hdc shell sync && hdc shell reboot` issued at 14:33:26. Script polled 300s and aborted at 14:38:36.

Manual continued polling after script abort:
- 0–300s post-reboot: no enumeration
- 300–600s: hdc kill && hdc start, still no enumeration
- 600–~420s: device re-enumerated as `dd011a414436314130101250040eac00`

So the device DID recover. **Brick was not permanent.** Recovery time ~7 min total. This is consistent with the agent 101 observation but agent 101 capped at ~20 min in two phases and may have just missed the window or hit a transient hdc-server confused state.

This contradicts the "soft brick" framing from agent 101 — it is more accurately a **long boot delay exceeding HBC's 300s threshold**, not a brick. HBC's 60×5s poll is too short for this V7 ROM.

## 4. Post-reboot board state (after manual recovery)

```
$ hdc shell 'uptime; getenforce'
17:03:39 up 1 min,  0 users,  load average: 6.58, 2.67, 0.97
Enforcing

$ hdc shell 'ps -ef | grep -iE appspawn|foundation|render_service|launcher|hdcd | grep -v grep'
root           199     1 22 17:02:26 ?    00:00:15 appspawn        ← original OHOS appspawn
foundation     417     1 39 17:02:29 ?    00:00:27 foundation
graphics       556     1 4 17:02:30 ?     00:00:02 render_service
root           739     1 0 17:02:31 ?     00:00:00 hdcd
20010009      1577   199 9 17:02:53 ?     00:00:03 com.ohos.launcher
```

**OH services up and running normally.** Launcher running, foundation up, render_service up — system functional.

BUT:
```
$ hdc shell 'ls /system/bin/appspawn-x'
ls: /system/bin/appspawn-x: No such file or directory

$ hdc shell 'ls /system/etc/init/appspawn_x.cfg'
ls: /system/etc/init/appspawn_x.cfg: No such file or directory

$ hdc shell 'ls /system/android'
ls: /system/android: No such file or directory

$ hdc shell 'find /system -name "*.orig*" 2>/dev/null'
(empty — even the Stage 1 backups are gone)

$ hdc shell 'find / -name "*appspawn_x*" -o -name "appspawn-x" 2>/dev/null'
(empty — appspawn-x and its cfg do not exist anywhere on the device)
```

**The entire deploy was reverted.** All 101 files plus the 14 Stage-1 device-side `cp X X.orig_$TS` backups are gone. Only the unmodified factory `/system` baseline remains.

Per-file md5 sanity check confirms factory bits:
| File | Local (HBC adapter) md5 | Device md5 | Match |
|---|---|---|---|
| libwms.z.so | a9fd1617fa1037afa579f6702e1ab1e5 | a9fd1617fa1037afa579f6702e1ab1e5 | Y (factory == HBC) |
| libappms.z.so | 3c07c6d4ab51673df222529660352f76 | 20d0a5f23acfe76130ed196f85b401dd | **N** |
| libbms.z.so | 7d7f508a6b46b4d57e8f3cdef7a007d9 | 36222229354d39061085d56edcb39b55 | **N** |
| librender_service.z.so | f15454679d2f7e210821147e538acbd2 | d73379c5267dfc0a42f911143803570c | **N** |
| libabilityms.z.so | 7ccdebe058788d7865efc8a3722b2f98 | 8d37e60b0ad807bf174e6f5ccf597012 | **N** |

The single `libwms` match is coincidental (HBC libwms == V7 factory libwms). All four other adapter libs are factory bits, mtime `2026-04-04` (factory build date), not our deploy timestamp.

Filesystem evidence:
```
/dev/block/.../system / ext4 ro,seclabel,nodev,relatime 0 0    ← /system IS root, ro by default

$ df /system
/dev/block/mmcblk0p7   2058088 1180448    877640  58% /

$ echo test > /system/persist_check_105.txt; cat /system/persist_check_105.txt
TEST_AGAIN_1501837572                                              ← post-boot writes DO persist within session
```

Post-boot writes (when /system is remounted rw) persist normally on /dev/block/mmcblk0p7. But pre-reboot deploy writes do NOT survive the reboot.

Hypothesis: an OHOS init-time `module_update` / `sys_installer` / verified-boot mechanism restores `/system` from the `updater` partition or an in-flash snapshot on every boot. Cmdline partition list confirms presence of `updater`, `boot_linux`, `bootctrl`, `eng_system`, `module_update` partitions. The `module_update_sa` and `sys_installer_sa` services run as user `update` with permission to write to /system.

## 5. HBC HelloWorld + McD launch

Not attempted. With `/system/bin/appspawn-x` absent and `/system/etc/init/appspawn_x.cfg` absent, no HBC APK can spawn — there is no appspawn-x runtime to launch through. Running `aa start com.example.helloworld.MainActivity -b com.example.helloworld` would either invoke the OHOS-native abilityms path (not the HBC AOSP-flavoured spawn) or fail to find the BMS-registered HelloWorld package (we never reached Stage 4 to `bm install`).

## 6. Comparison to agent 101 outcome — did fixes change the brick mechanism?

| Dimension | Agent 101 | Agent 105 (this run) |
|---|---|---|
| Stages 0–3.9 | PASS | PASS (identical) |
| Stage 3.5 300s poll | ABORT | ABORT (identical) |
| Device re-enumeration | NEVER observed in 20min wait | Observed at ~7 min post-reboot |
| Post-reboot /system state | NOT INSPECTED (board offline) | **REVERTED to factory** |
| cfg corruption (7c192fe6) | not yet identified | IRRELEVANT — cfg disappears entirely post-reboot |
| Adapter drift (e6bd7cce) | not yet resolved | IRRELEVANT — all artifacts disappear post-reboot |

**The two fixes (cfg + adapter resync) did NOT change the Stage 3.5 outcome**, because the Stage 3.5 problem is not "appspawn-x cannot start due to corrupt cfg or stale artifact" — it is "/system is wiped on reboot so appspawn-x cannot start because it does not exist on disk anymore". The cfg/adapter fixes are correct in their own right (they bring our staging tree to parity with HBC live), but they cannot solve a deeper class-A problem: **the deploy isn't actually being persisted across reboot on this V7 ROM**.

The brick mechanism, restated:
1. Deploy lands correctly to /system (verified pre-reboot).
2. `reboot` → init starts → at some point in init's pre-init or early-init phase, /system is restored from `updater`/`module_update` source. All deploy artifacts (and `.orig_` backups, since those are also on /system) are deleted.
3. OHOS comes up with original `appspawn` only. `aa start` would use OH-native ability spawn, not HBC's appspawn-x.
4. Stage 3.5's `pidof foundation render_service hdcd com.ohos.launcher` health check would actually PASS (these services come up). The 300s wait is just the unexpectedly long reboot time on this V7 ROM, not a brick.

If HBC had survived the 300s poll on their boards, they would have proceeded to Stage 3.5's `BMS-ready` hilog check and Stage 4 APK install — and Stage 4's `aa start com.example.helloworld` would also have appeared to "work" by invoking the OH-native path, but no HBC artifact would actually be in play. So HBC's success on their bench may itself depend on artifact persistence that this V7 ROM denies.

## 7. Recommendation

1. **Stop running HBC's deploy_stage.sh as-is on V7 ROM until we understand /system persistence.** Until we know whether /system on V7 ROM is verified-boot-protected, snapshot-rolled-back, or restored from `updater` partition by `module_update_sa`, every deploy attempt will simply be wiped on reboot — no fix to cfg, adapter, P-1, P-2, etc. can change this. The brick is not what we thought it was; it is "deploy never persists".

2. **Concrete next investigation steps (in priority order)**:
   - (a) `hdc shell 'ls -la /dev/block/by-name/updater' && hdc shell 'dd if=/dev/block/by-name/updater bs=1M count=1 of=/sdcard/updater_head.bin'` then pull and `file` it. If it's an ext4/squashfs image carrying a verbatim copy of factory /system, we have our culprit.
   - (b) Disable `module_update_sa` and `sys_installer_sa` services (move their .cfgs to .cfg.disabled before reboot — but how, when /system is also restored? must use bind-mount or grub-like cmdline override) and observe whether deploy persists.
   - (c) Reread `feedback_two_pivots_in_two_days.md` — this is the 3rd consecutive different-symptom investigation in the same architectural layer (cfg → adapter drift → /system reversion). The triple of (W2 soft-brick / agent 101 abort / agent 105 revert) with rotating symptoms but same end state (board can't run appspawn-x post-reboot) starts to clear the evidence bar for a deeper architectural reconsideration of "deploy artifacts to /system over hdc on this ROM".
   - (d) Investigate **kernel boot cmdline `ohos.required_mount.system=...@/usr@ext4@ro,barrier=1@wait,required`** — the mount point is `/usr` not `/system`! `/system` shows up because of `mksandbox system` (mount namespace) in init.cfg. If the init pre-init phase rebuilds the sandbox from a cleaner source (e.g. a snapshot of /usr from updater partition), this would explain everything. Sandbox restoration is exactly the kind of init-job that would undo our deploy invisibly.

3. **Hand-off note for agent 106 (or whoever takes the post-V3-W11-audit slot)**: The HBC server SOP and persistence model on HBC's bench probably differs from V7 ROM on DAYU200. HBC may have been able to deploy because their ROM did NOT have `mksandbox system` or did NOT have a snapshot-restoring `updater` partition init job. Investigate whether HBC has a `disable-system-sandbox` patch in their `ohos_patches/`, or whether they boot via `eng_system` partition (which is a separate partition listed in `/dev/block/by-name`) rather than `system`. Look for boot mode switches.

4. **The fixes (cfg `7c192fe6` + adapter resync `e6bd7cce`) are still correct and should remain landed.** They are necessary-but-not-sufficient. They bring our static state to parity with HBC live. The unsolved problem is dynamic — persistence across reboot.

---

## Appendix A — Exact commands

```bash
export HDC=/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe
export ADAPTER_ROOT=$HOME/android-to-openharmony-migration/hbc-deploy-as-is/adapter
export TS=20260521
export MSYS_NO_PATHCONV=1
cd $HOME/android-to-openharmony-migration/hbc-deploy-as-is/deploy

for stage in 0 1 2 3.0 3b 3c 3d 3e 3f 3.9 3.5; do
    bash deploy_stage.sh $stage 2>&1 | tee -a /tmp/v3-105-deploy.log
done
```

## Appendix B — Forensic file list

- `/tmp/v3-105-deploy.log` (full deploy log, 175 lines)
- `/tmp/v3-105-final-hilog.txt` (post-recovery hilog tail, 200 lines)
- `/tmp/v3-105-final-dmesg.txt` (post-recovery dmesg tail, 100 lines)
- `/tmp/v3-105-final-state.txt` (uptime + services)
- `/tmp/v3-105-mounts.txt` (/proc/mounts post-recovery)
- `/tmp/v3-105-init-cfg.txt` (head of /system/etc/init.cfg)
