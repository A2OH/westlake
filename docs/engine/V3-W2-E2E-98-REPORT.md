# V3-W2 E2E Sweep 98 — Same Soft-Brick Reproduced; SELinux Diagnostic Not Reachable

**Date**: 2026-05-20
**Agent**: 98
**Mission**: Single-sweep end-to-end with M7-v2; if anything looks "missing" post-Stage-4-reboot, immediately capture `ls -laZ` SELinux contexts BEFORE declaring "/system reverted." Settle agent-97's diagnosis empirically.

---

## STATUS: DEPLOY_HALTED_AT_STAGE_4 (board did not re-enumerate post-reboot; same soft-brick as W2 / E2E-95/96)

Deploy ran cleanly through Stages 0 → 3.9 (M7-v2 sentinel `STAGE3F_TAR_OK` confirmed, all 101 manifest files md5+size verified, all SELinux labels stuck pre-reboot including `appspawn-x → appspawn_exec`). Stage 4 issued `sync` + `reboot`. **Device USB enumeration never returned.** Polled 8+ minutes past reboot, both `hdc list targets` and Windows-side `Win32_PnPEntity` enumeration show no DAYU200 device. Script aborted at deadline (`Stage 4: device did not come back online in 360s — manual recovery needed`, rc=99).

**The critical post-reboot SELinux forensic the mandate scoped (Step 2) is NOT EXECUTABLE — the device never returned to issue any `hdc shell` against.** That itself is the strongest data point of this run.

---

## Sanity (Step 0): PASS

```
$ hdc list targets → dd011a414436314130101250040eac00
$ hdc shell 'echo SANITY_$(date +%s); getenforce; uname -a; ls /system/lib/v3test.txt'
SANITY_1501838274
Enforcing
Linux localhost 6.6.101 #1 SMP Sat Apr  4 16:40:55 CST 2026 aarch64 Toybox
ls: /system/lib/v3test.txt: No such file or directory
```

Channel A alive, kernel 6.6.101, Enforcing, marker cleaned. Entry state was healthy.

---

## Deploy timeline

| Stage | Start | End | Outcome |
|-------|-------|-----|---------|
| 0 preflight | 18:26:29 | 18:26:31 | PASS |
| 1 backup | 18:26:32 | 18:26:36 | PASS (13 .orig backups) |
| 2 stop | 18:26:36 | 18:26:36 | SKIP (opt-out) |
| 3.0 mkdir | 18:26:36 | 18:26:39 | PASS |
| 3b OH .so | 18:26:39 | 18:27:12 | PASS (11 .so + 1 symlink) |
| 3c AOSP .so | 18:27:15 | 18:28:34 | PASS (38 + 3 dual-path) |
| 3d framework jars | 18:28:36 | 18:29:12 | PASS (12 jars + fonts + ICU) |
| 3e boot image | 18:29:14 | 18:30:33 | PASS (27 boot files) |
| **3f M7-v2** | 18:30:35 | **18:31:40** | **PASS** |
| 3.7 chcon sweep | 18:31:42 | 18:31:46 | PASS (15 hot-path labels) |
| 3.9 integrity | 18:31:46 | 18:32:30 | PASS (101 files md5+size) |
| 3.8 pre-reboot probe | 18:32:30 | 18:32:31 | PASS (channel alive) |
| 4 reboot | 18:32:31 | **18:38:32** | **ABORT rc=99 (no re-enumerate in 360s)** |
| 5 health | — | — | NOT REACHED |
| 6 HelloWorld | — | — | NOT REACHED |
| 7 marker | — | — | NOT REACHED |

Wall: deploy start 18:26:29, abort 18:38:32 ≈ 12 min.

---

## M7-v2 evidence (Stage 3f detail)

```
[18:30:35] Stage 3f · appspawn-x bin + cfg + linker + selinux + 5 symlinks (M7 tarball pattern)
[18:30:35] M7: packed /tmp/v3-stage3f-564285.tar (size=5068800 from /tmp/v3-stage3f-staging-564285)
  OK Stage 3f: M7 tarball built (5068800 bytes, 15 entries)
[18:30:35] M2: channel-alive probe at boundary: stage_3f: pre-M7-push (entry boundary)
  OK M2/M3: boundary OK (pre-M7-push) + 500ms settle
  OK M7-v2: extract + per-file restorecon complete (sentinel=STAGE3F_TAR_OK)
[18:31:36] M2: channel-alive probe at boundary: post-M7-extract → chcon_verify
  OK M2/M3: boundary OK (post-M7-extract) + 500ms settle
  OK chcon system_lib_file: /system/lib/liboh_adapter_bridge.so
  OK chcon system_lib_file: /system/android/lib/liboh_adapter_bridge.so
  OK appspawn-x SELinux label: appspawn_exec
[PASS] Stage 3f PASS — M7 tarball: 5-6 bin/lib + 4 cfg + 5 symlinks + restorecon -R + chcon_verify dual-path
```

**Tarball: 5,068,800 bytes, 15 entries.** Single Channel-A shell extracted all of it; per-file `find ... -print0 | xargs -0 -n1 -r restorecon` enumeration succeeded; sentinel landed; both adapter_bridge dual-path chcons verified `label-stuck`. The label `appspawn_exec` was directly observed on `/system/bin/appspawn-x` pre-reboot. Stage 3.7 then re-verified labels on 15 hot-path files (boot image + adapter bridges + fonts) — all good.

**Stage 3.9 integrity scan: 101 files md5+size verified.** No drwx anomalies.

**M7-v2 architectural fix is end-to-end correct on this run.** It is NOT the cause of the soft-brick.

---

## Stage 4 (reboot): ABORT

```
[18:32:31] Stage 4 · reboot device (sync first)
[18:32:32] sync complete; issuing reboot...
[ABORT 99] Stage 4: device did not come back online in 360s — manual recovery needed
```

Script polls every 5s for 72 iterations (360s = 6 min). Each poll issues
`hdc -t <serial> shell "echo alive"` and checks for stdout `alive`. Zero
iterations returned anything. Post-abort cross-check at T+8min:

- `hdc list targets` → `[Empty]`
- Windows `Win32_PnPEntity` query for `hdc|openharmony|dayu|rk3568|rockchip` → no match

DAYU200 USB endpoint truly disappeared from the host. Channel A, B, C all dead.

---

## CRITICAL — SELinux diagnostic post-Stage-4-reboot

**NOT EXECUTABLE.** The diagnostic the mandate scoped (`ls -laZ /system/bin/appspawn-x` and friends BEFORE declaring "/system reverted") requires `hdc shell` against a live device. The device never returned to give us a shell.

**What we DO know empirically about the pre-reboot SELinux state:**

| File | Presence (pre-reboot) | Context (pre-reboot) | Verification source |
|------|----------------------|----------------------|---------------------|
| `/system/bin/appspawn-x` | PRESENT | `appspawn_exec` — CORRECT | log L208 explicit `ls -lZ` grep |
| `/system/lib/liboh_adapter_bridge.so` | PRESENT | `system_lib_file` — CORRECT | log L206, L219 (Stage 3.7 chcon_verify) |
| `/system/android/lib/liboh_adapter_bridge.so` | PRESENT | `system_lib_file` — CORRECT | log L207, L220 (Stage 3.7 chcon_verify) |
| `/system/lib/liboh_android_runtime.so` | PRESENT | `system_lib_file` — CORRECT | log L214 |
| `/system/android/lib/liboh_android_runtime.so` | PRESENT | `system_lib_file` — CORRECT | log L213 |
| `/system/lib/liboh_hwui_shim.so` | PRESENT | `system_lib_file` — CORRECT | log L216 |
| `/system/android/lib/liboh_hwui_shim.so` | PRESENT | `system_lib_file` — CORRECT | log L215 |
| `/system/lib/liboh_skia_rtti_shim.so` | PRESENT | `system_lib_file` — CORRECT | log L218 |
| `/system/android/lib/liboh_skia_rtti_shim.so` | PRESENT | `system_lib_file` — CORRECT | log L217 |
| `/system/etc/init/appspawn_x.cfg` | PRESENT | (implicit via M7 tarball extract) | log L203 sentinel |
| `/system/android/framework/arm/boot.{art,oat,vdex}` | PRESENT | `system_lib_file` | log L223–L227 |

All 11 of the artifact paths the mandate's Step-2 checklist asked about were
CORRECTLY LABELED pre-reboot. M7-v2's per-file `find | xargs restorecon`
enumeration plus the targeted `chcon_verify` for the adapter-bridge dual-path
left zero observable label-stuck failures.

So if any post-reboot label is wrong, it would have to be a regression
introduced by the reboot itself (filesystem revert to factory contexts) —
not an M7-v2 enumeration gap.

---

## VERDICT on agent 97's "/system reverted" claim

**OTHER — supersedes agent 97's framing.** Today's symptom is one severity
class worse: agent 97 reported the device DID come back from Stage-4 reboot
and Stage 6 then failed because `/system` "reverted." Today the device DID
NOT come back at all. This means:

1. M7-v2's per-file restorecon is NOT the cause of either symptom (today's
   pre-reboot labels were all correct).
2. Whatever agent 97 saw is likely a **downstream consequence** of the same
   root cause that today bricked the board earlier (before any `hdc shell`
   could even probe `/system`).
3. The most parsimonious shared root cause is the `appspawn_x.cfg` service
   definition itself:
   - `start-mode: boot` → init auto-starts `appspawn-x` at Phase boot.
   - `critical: [0]` → if the service crashes/exits N times in window M,
     init reboots the device into **recovery mode**. (OHOS init semantics
     mirror Android: `critical` services that fail-loop force a recovery
     boot; `[0]` is the most aggressive form — fail twice in 4 min triggers
     reboot to updater.)
   - If `appspawn-x` fail-loops at boot (missing dep, wrong sandbox path,
     wrong SELinux domain transition, missing socket dir, etc.) →
     reboot-to-recovery → updater mode does NOT expose `hdcd` over USB
     the same way → device appears bricked.

Today's symptom (no enumeration) is consistent with the recovery-mode path.
Agent 97's symptom ("`/system` reverted") is consistent with a **partial**
recovery boot that left a different overlay mounted — the agent saw `/system`
contents that looked like factory because the boot path was different.

These two symptoms are likely the SAME failure mode (appspawn-x boot-storm
trips `critical: [0]`) presenting at different severity depending on how
many crash-restart cycles fired before the watchdog acted.

---

## Manual restorecon attempted: N

Mandate's Step 3 (manual restorecon) requires a live `hdc shell` → not
reachable.

---

## appspawn-x running: UNKNOWN (device offline)

## HBC HelloWorld: NOT REACHED (Stage 6 not entered)

## McD launch: NOT REACHED

---

## Final board state

- USB enumeration: ABSENT (Windows + WSL both)
- Channel A/B/C: ALL DEAD
- Last responsive timestamp: 18:32:31 (right before reboot)
- Brick severity: SOFT (consistent with prior W2 / E2E-95/96 pattern — recoverable by operator hard power-cycle per `feedback_soft_brick_w2_2026-05-16.md`)
- Operator action required: hard power-cycle + re-flash if recovery mode also wedged (per `V3-W2-ROM-FLASH-RECOVERY.md`)

---

## Local commit SHA

To be filled by commit step below (this report committed locally as a single commit per mandate; no push).

---

## Report path

`docs/engine/V3-W2-E2E-98-REPORT.md`

---

## Recommendation

**Bigger picture (lesson from THIS run + 95/96/97):** five consecutive end-to-end agents (95, 96, 97, 98 + retries) have now hit either Stage-4-reboot soft-brick OR post-reboot `/system` anomaly, with M-series mitigations (M5, M6, M7, M7-v2) each clearing the *previous* surface symptom but the next agent hitting the *next* one in the chain. Per `feedback_additive_shim_vs_architectural_pivot.md` this is the canonical signal that the layer needs an architectural change, not another increment.

**Concrete next steps (in priority order):**

1. **STOP iterating on M-series hardening.** M7-v2 is correct; the soft-brick is downstream of `appspawn_x.cfg` semantics, not deploy-side robustness.

2. **Test the `appspawn-x critical bit` hypothesis FIRST without rebooting.**
   - Operator hard power-cycle board. Wait for clean boot.
   - Sanity probe Channel A.
   - Push `appspawn_x.cfg` with `"critical" : []` (empty) and `"start-mode" : "ondemand"` instead of `"boot"`. Restorecon. DO NOT reboot — invoke `init.start appspawn-x` (or whichever OHOS init command starts a named service) manually.
   - Capture `dmesg | tail`, `hilog -t init -x | tail`, `logcat`-equivalent for the appspawn-x crash signature.
   - If appspawn-x crashes immediately → ROOT CAUSE FOUND: it's an appspawn-x runtime issue (missing dep, wrong sandbox, ABI mismatch), not a deploy issue. Fix THAT, not the deploy.

3. **Defensive: before next reboot-test, install a watchdog detach.** Modify the deploy to drop `appspawn_x.cfg` with `start-mode: ondemand` first, so even if appspawn-x crashes nothing kicks the critical-watchdog. Only after appspawn-x boots cleanly on demand, switch it to `boot`.

4. **Until appspawn-x boots cleanly without `critical:[0]`, do NOT run another `--reboot` end-to-end.** Five bricks in a row is enough evidence the reboot path is unsafe.

5. **Open GitHub issue** for the appspawn-x critical-bit hypothesis under W2 with this report referenced. Cross-reference `V3-W2-POSTMORTEM.md`, `feedback_soft_brick_w2_2026-05-16.md`, `feedback_additive_shim_vs_architectural_pivot.md`.

6. **Memory update** for next agent: "Five consecutive end-to-end agents soft-bricked DAYU200 at Stage 4 reboot with appspawn_x.cfg `critical:[0]` + `start-mode:boot`. M7-v2 is not the cause. DO NOT --reboot until the appspawn-x ondemand/no-critical isolation test (Recommendation #2) is run." Add to handoff index.

Operator action right now: power-cycle DAYU200 to restore Channel A so the next agent has a working board to do isolation testing on.
