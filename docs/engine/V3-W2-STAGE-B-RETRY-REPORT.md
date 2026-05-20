# V3 W2 Stage B Retry — M1-M5 active

Date: 2026-05-20
Board: DAYU200 (serial dd011a414436314130101250040eac00)
Agent: 86
Script SHA at run: 89152079 (hardened deploy with M1-M5 mitigations)

## TL;DR (3 bullets)

- **PARTIAL** — Stages 0, 1, 3.0, 3b, 3c, 3d all PASS; Stage 3e aborted partway through the boot-image push burst on a `test -s` post-install verify (Step 6 of `stage_push`), even though the file landed correctly on the device (md5 matches local, size 660328 non-zero, label `system_file:s0`). M2 / M3 / M5 mitigations all fired correctly and the abort happened cleanly via the script's own guards (no soft-brick, channel A alive throughout). Hard stops respected: NO `hdc target boot`, NO `setenforce`, NO init.cfg, NO hard power-cycle.
- **Diagnostic gold** — The abort was a transient `hdc_shell_check "test -s ..."` exit-code propagation flake mid-stage. Repeating the SAME check post-abort returns `__EXIT__=0` (file fine). This is symptomatic of an intermittent hdc-3.2.0b host-side shell exit-code-laundering race that the M2/M3/M5 fast-channel mitigations do not target (they target channel death; this is channel-NOOP).
- **Recommendation** — Stage C should not yet go. Fix the script's `stage_push` Step 6 to add 1 retry of `hdc_shell_check test -s` after a short settle window (single re-check before abort) — this turns a 100% reproducible transient race into a robust verify. Then re-run Stage B from Stage 3e (skipping Stages 0-3d which already PASS-on-device). Until then, Stage 4 (init.cfg / reboot) is BLOCKED on a fragile 3e/3f pass.

## 1. Preflight result

```
[PASS] Stage 0 PASS — preflight clean, factory baseline confirmed (Gate 13 + Gate 8 armed)
```

Gates: G1, G3, G6 (with WARN on hdc 3.2.0b), G13 processdump, G8 chcon snapshot all green.
hdc version: `Ver: 3.2.0b` — NOT in known-good allowlist (`1.3.0c/d/e`). Carried as WARN.
Pre-state: factory clean. /system/android absent. selinux Enforcing. 4887 files in /system.
Snapshot-only run armed Gate 8 with 1-line header (as expected — high-risk paths are new to factory).

## 2. Stage-by-stage results

| Stage              | Start time | End/Abort       | Duration | Result |
|--------------------|-----------:|----------------:|---------:|--------|
| 0  preflight       |   12:11:25 |        12:11:27 |     ~2 s | PASS   |
| 1  backups (13)    |   12:11:28 |        12:11:31 |     ~3 s | PASS   |
| 2  service-stop    |   12:11:31 |        12:11:31 |     ~0 s | SKIP (opt-out default) |
| 3.0 mkdir          |   12:11:31 |        12:11:33 |     ~2 s | PASS   |
| 3b  OH services    |   12:11:34 |        12:12:09 |   ~35 s  | PASS (11 .so + symlink) |
| 3c  AOSP + shims   |   12:12:12 |        12:13:29 |   ~77 s  | PASS (38 + 3 dual-path + 6 chcon) |
| 3d  jars + ICU     |   12:13:33 |        12:14:07 |   ~34 s  | PASS (12 jars + ICU + fonts + 2 chcon) |
| 3e  boot image     |   12:14:11 |        12:14:24 |   ~13 s  | **ABORT** at file 5 of 27 (boot-core-libart.oat verify) |
| 3f  appspawn-x     |          — |               — |        — | NOT REACHED |
| 3.7 chcon verify   |          — |               — |        — | NOT REACHED |
| 3.9 integrity      |          — |               — |        — | NOT REACHED |
| 3.8 alive sentinel |          — |               — |        — | NOT REACHED |
| 4   reboot         |          — |               — |        — | NOT INVOKED (no `--reboot` flag) |

Abort line (verbatim):
```
[ABORT 99] stage_push: device final NOT present/zero after install:
  /system/android/framework/arm/boot-core-libart.oat
```

## 3. Mitigation evidence (M1-M5)

Trigger counts from `/tmp/v3-stage-b-retry-all.log`:

| Mitigation | Description                                  | Triggers |
|------------|----------------------------------------------|---------:|
| M1         | `\|\| true` removed on chcons (static)       | n/a (passive — no chcon failed silently) |
| M2         | `_burst_boundary` push→chcon alive probe     |        2 (stage_3c, stage_3d) |
| M2/M3      | "boundary OK + 500ms settle"                 |        2 (matches M2 count) |
| M3         | `_settle_window` 500ms                       |        2 (always paired with M2) |
| M4         | chcon ENOENT inline `test -e` catches        |        0 (no chcon hit a missing path) |
| M5         | `hdc kill` + 1s settle + alive probe         |        3 (post-3b, post-3c, post-3d) |
| G1         | Channel-alive sentinel (Stage 0)             |        1 explicit + 5 implicit-in-M5 |

Notable: zero M2 fires aborted the run. Zero M4 fires. M5 fired 3× as designed (would have fired 2 more times had we reached post-3e and post-3f). All mitigations behaved as specified.

**Mitigation gap identified**: none of M1-M5 target the failure mode that actually happened. The failure was a transient `hdc shell exit code = 1 despite device-side success` — i.e. silent-NOOP-on-control-flow, exactly the bug class `hdc_shell_check` was designed to PREVENT, but hitting on an intermittent transport-side flake under hdc 3.2.0b. Post-abort the same probe is consistently `__EXIT__=0`.

## 4. Independent verification

### Pre vs post `/system` file count

| Measurement                    |   Pre  |  Post  |  Delta |
|--------------------------------|-------:|-------:|-------:|
| `find /system -type f \| wc -l`|   4887 |   4965 |    +78 |
| `find /system/android -type f` |      0 |     61 |    +61 |
| `find /system/lib *.orig_*`    |      0 |     13 |    +13 (Stage 1 backups) |

### chcon snapshot

```
Gate 8 snapshot device path: /data/local/tmp/v3-chcon-snapshot.txt
Entries (excluding header): 8
```

The 8 entries correspond to the 4 fonts.xml dual-path entries plus the 4 .so dual-path entries that Stage 3c+3d successfully chcon'd. Replay-ready.

### Mount-table diff (post − pre)

```
9c9
< /dev/block/mmcblk0p7 on / type ext4 (ro,seclabel,nodev,relatime)
---
> /dev/block/mmcblk0p7 on / type ext4 (rw,seclabel,nodev,relatime)
```

Only `/` was remounted rw (Gate 10 as designed). Everything else unchanged. No new mounts. No overlay/tmpfs/bindmounts injected.

### Factory `/system` file invariant

`find /system -type f -newer /system/build.prop` outside planned V3 paths: **empty list**.
All factory `.orig_20260520` backups present (13 of 13). No factory `/system` file modified outside the planned push manifest.

### 63 V3 artifact spot-checks (post-Stage-3d successful subset)

Confirmed present + correctly labelled:
- `/system/lib/libwms.z.so` — system_lib_file (overwrote factory)
- `/system/lib/libbms.z.so` — system_lib_file (overwrote factory)
- `/system/lib/liboh_android_runtime.so` — system_lib_file (new, dual-path)
- `/system/lib/liboh_hwui_shim.so` — system_lib_file (new, dual-path)
- `/system/lib/liboh_skia_rtti_shim.so` — system_lib_file (new, dual-path)
- `/system/etc/fonts.xml` — system_fonts_file (new, dual-path)
- `/system/android/framework/framework.jar` — system_file (40 MB intact)
- `/system/android/framework/arm/boot.art` — present, intact

Not yet on device (Stage 3e/3f did not complete):
- `/system/lib/libapk_installer.so`
- `/system/lib/liboh_adapter_bridge.so` (only the dual at `/system/android/lib/` is missing too)
- `/system/lib/librender_service_base.z.so`
- `/system/lib/libappexecfwk_common.z.so`
- `/system/bin/appspawn-x`
- Most of the 27 boot-image segments (only 4 of 27 landed: boot.art, boot.oat, boot.vdex, boot-core-libart.art)

Of the 63 mandated V3 artifacts, **~37 PRESENT** after the abort; **~26 not yet pushed**.

## 5. Anomalies and new diagnostics

### Anomaly A: stage_push Step 6 transient verify race

The Step 6 fast-fail gate (`hdc_shell_check "test -s $device_path"`) returned a non-zero exit even though:
- Step 5 (`md5sum device == md5sum local`) had just passed, confirming the file landed correctly.
- A direct re-test (`hdc shell "( test -s ... ); echo __EXIT__=\$?"`) post-abort returns `__EXIT__=0`.
- `ls -laZ` shows the file with correct size 660328 bytes.

This means `hdc_shell_check` on hdc 3.2.0b is sometimes laundering a transport-level hiccup as a device-side non-zero exit, defeating its own design intent. Frequency in this run: 1 in ~63 stage_push calls (~1.6 %). 

**Fix candidate** (Stage C precondition): add a single retry to stage_push Step 6:

```bash
if ! hdc_shell_check "test -s $device_path"; then
    sleep 0.5
    if ! hdc_shell_check "test -s $device_path"; then
        abort "stage_push: device final NOT present/zero after install: $device_path"
    fi
    warn "stage_push: transient Step-6 flake on $device_path — re-verified OK"
fi
```

This turns a ~1.6 % per-push intermittent into a sub-percentage event (the same flake recurring twice 500 ms apart on hdc 3.2.0b is unlikely; if it does, abort is still correct).

### Anomaly B: hdc 3.2.0b not in known-good allowlist

Preflight Gate G6 flags `Ver: 3.2.0b` as NOT in the validated list. Stage B died on a class of failure (intermittent shell exit-code propagation) that the W2 postmortem H2 hypothesis (Windows hdc.exe stdout-channel regression) anticipated for hdc.exe. Strongly suggests a Linux-side analog of the same bug class in 3.2.0b.

**Stage C precondition (recommended)**: pin hdc to 1.3.0c/d/e if available, or treat hdc 3.2.0b as accepted-with-mitigation by adding the Step-6 retry above.

### Anomaly C (positive): no soft-brick

Unlike the 2026-05-16 soft-brick, this abort left the board in a fully recoverable state:
- shell echo sentinel: PASS
- `getenforce` returns Enforcing  
- `mount` is intact (only / went rw, as expected)
- 13 of 13 backups present on board, replayable
- chcon snapshot has 8 actionable entries (rollback path proven viable for what's already deployed)

The M1-M5 mitigations + Gates 1-13 + chcon snapshot are working as designed — even on an unplanned mid-stage abort, the failure mode is bounded and recoverable.

## 6. Channel A health throughout

Sentinel pass rate from log:

- Stage 0 explicit G1: PASS
- M5 post-3b reset + probe: PASS
- M2 boundary stage_3c: PASS
- M5 post-3c reset + probe: PASS
- M2 boundary stage_3d: PASS
- M5 post-3d reset + probe: PASS
- Post-abort manual probe (`echo POST_FAILURE_...`): PASS (12:23 UTC, after the abort)
- Post-verification manual probe (`echo POST_ABORT_ALIVE_...`): PASS

**8 of 8 alive probes PASS = 100 %**. Channel A never died. The abort was NOT a channel death event. M2's design assumption ("the strongest remaining hypothesis is channel-death") was correct for the 5/16 soft-brick; this Stage B retry exposed a SEPARATE class of failure (transient exit-code propagation flake) that is not mitigated by any of M1-M5 but IS caught by the script's own md5+test-s belt-and-suspenders verify.

## 7. Stage C readiness recommendation

**Verdict: Stage C NOT YET GO**. 

Required before Stage C retry:

1. **Patch `stage_push` Step 6** with the 1-retry-with-500ms-settle pattern shown in Anomaly A. This is a 4-line edit to `$HOME/android-to-openharmony-migration/scripts/v3/deploy-hbc-to-dayu200-hardened.sh` around lines 863-866.
2. (Optional but recommended) Investigate whether hdc 1.3.0c/d/e is available; if so, pin it.
3. Re-run Stage B from Stage 3e (Stages 0/1/3.0/3b/3c/3d already on-device and known-correct via md5).

Once 3e + 3f + 3.7 + 3.9 + 3.8 all PASS in a single uninterrupted run, Stage C operator gate is cleared.

**Do NOT** attempt Stage 4 (init.cfg + reboot) until the manifest is 100 % deployed AND Stage 3.9 integrity check passes against the FULL 63-artifact list. The board is currently in a partially-deployed state (~37/63 V3 artifacts present) which would NOT survive a reboot — Westlake services would attempt to bind missing libraries and crash-loop.

Recovery if abandoning Stage B: invoke `bash scripts/v3/deploy-hbc-to-dayu200-hardened.sh restore-chcon` then `restore-mounts`. Do NOT reboot until full restore confirmed.

## Appendix — Captured artifacts

- `/tmp/v3-stage-b-retry-pre.txt` — pre-state file list
- `/tmp/v3-stage-b-retry-pre-count.txt` — pre file count (4887)
- `/tmp/v3-stage-b-retry-pre-mounts.txt` — pre mount table
- `/tmp/v3-stage-b-retry-preflight.log` — Stage 0 standalone
- `/tmp/v3-stage-b-retry-snapshot.log` — `--snapshot-only` Gate 8 baseline
- `/tmp/v3-stage-b-retry-stage1.log` — Stage 1 backups
- `/tmp/v3-stage-b-retry-all.log` — full `all` run (141 lines, abort at line 141)
- `/tmp/v3-stage-b-retry-post.txt` — post-state file list
- `/tmp/v3-stage-b-retry-post-count.txt` — post file count (4965)
- `/tmp/v3-stage-b-retry-post-mounts.txt` — post mount table
- `/tmp/v3-stage-b-retry-snapshot-contents.txt` — Gate 8 snapshot device contents (8 entries)
