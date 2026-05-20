# V3 W2 End-to-End Sweep with M6

- Date: 2026-05-20
- Agent: 90
- Board: DAYU200 (post operator reflash)
- HDC binary: `C:\Users\dspfa\Dev\ohos-tools\hdc.exe` Ver 3.2.0b (Windows)
- Deploy script HEAD: `e11272ad` (M6 chunking landed at `8c0d0755`)

---

## TL;DR (3 bullets)

- **STATUS: DEPLOY_HALTED_AT_STAGE_0** — the deploy script aborted at the very first `_alive_probe` in stage_0 (Gate G1, "hdc shell silent — empty stdout"). M6 chunking never got a chance to exercise — board is in the W2-postmortem H2 state (Channel A silent, Channel B alive) BEFORE any V3 byte was written.
- **Operator reflash got the OS up but did NOT restore Channel A.** OHOS 7.0.0.18 fresh boot, kernel `6.6.101 #1 SMP Sat Apr 4 16:40:55 CST 2026`, full system mounted (`/`, `/vendor`, `/chip_ckm`, etc.), init+foundation+softbus_server+hdcd all running. `hdcd` PID 631 sleeping in `do_epoll_wait` (idle, not crashed). Yet every `hdc shell …` returns empty stdout AND empty stderr — even via `cmd.exe` (bypassing WSL stdio), even after `hdc kill` + `hdc start`, even with explicit `-t <serial>`.
- **Cannot run M6 single-sweep on this board state.** No safe forward move from this agent: per W2-POSTMORTEM R0 ("If shell stdout is still broken but file send/recv works… STOP for human review before attempting restore") and per agent-90 hard-stop rule 1, I halted, captured forensic via Channel B (file recv), and am reporting. Recommendation: this is a `hdc.exe 3.2.0b` regression or a fresh-boot hdcd state — try one of the older Windows hdc.exe versions in the known-good list (`Ver:1.3.0c|d|e`) before declaring a hardware issue.

---

## 1. Pre-state

### Channel A (hdc shell): DEAD

```
$ hdc.exe list targets
dd011a414436314130101250040eac00

$ hdc.exe shell 'id'        → (empty)
$ hdc.exe shell 'getenforce' → (empty)
$ hdc.exe shell 'uptime'     → (empty)
$ hdc.exe shell true; echo $?  → 0  (exits silently and "successfully")
```

Reproduced via:
- WSL invocation of `hdc.exe` (stdin redirected from `/dev/null` to mirror `_alive_probe`)
- `cmd.exe /c "C:\Users\dspfa\Dev\ohos-tools\hdc.exe shell …"` (bypasses WSL stdio bridge entirely)
- 5× consecutive retries with unique markers → all empty
- `hdc kill` → 2s wait → `hdc start` → 3× retries → all still empty
- With explicit `-t dd011a414436314130101250040eac00` → still empty
- Without `-t` (auto-select) → still empty

Stderr is ALSO empty (confirmed by redirecting `2>/tmp/probe.err` and reading the file).

### Channel B (hdc file send/recv): ALIVE

- File send via `cmd.exe` works (UNC path issue when invoked from WSL CWD, but content transfers when source is a Windows path).
- File recv works on any readable path:
  - `/init` (319872B), `/system/etc/init.cfg` (10514B), `/system/bin/sh` (235204B), `/system/bin/toybox` (388360B) — all retrieved cleanly.
  - `/proc/version`, `/proc/cmdline`, `/proc/uptime`, `/proc/meminfo`, `/proc/mounts`, `/proc/1/cmdline`, `/proc/631/status`, `/proc/631/wchan` — all retrieved.

### Board status (via Channel B only)

| Probe | Value | Source |
|------|------|--------|
| OS | OpenHarmony 7.0.0.18 (Device info: OpenHarmony 3.2) | `cppcrash-bootanimation-…log` |
| Kernel | `Linux 6.6.101 ([REDACTED]@GZ05) … #1 SMP Sat Apr 4 16:40:55 CST 2026` | `/proc/version` |
| Hardware | rk3568 | `/proc/cmdline` |
| Uptime | 2461.95s ≈ 41 min | `/proc/uptime` |
| PID 1 | `/bin/init --second-stage 3078974` (State: S sleeping) | `/proc/1/{cmdline,status}` |
| hdcd | PID 631, `/system/bin/hdcd`, State: S sleeping, 12 threads, wchan=`do_epoll_wait`, 268555 minor faults | `/proc/631/{status,stat,wchan}` |
| Other live PIDs found | foundation (1500), softbus_server (500) | `/proc/*/cmdline` survey |
| `/system/etc/init.cfg` | Vanilla OHOS init config, NO V3 modifications, NO `appspawn-x` references | recv |
| `/system/etc/init/hdcd.cfg` | Vanilla OHOS hdcd post-fs-data job + persist.hdc.control.{shell,file,fport} param-gates | recv |
| `/system/android/` | does not exist (factory clean) | (would need shell to test directly; inferred from init.cfg) |

Factory-clean from filesystem perspective. Board is up, services running, hdcd present and not respawning. **But Channel A is silent.**

### Faultlog evidence found

- `/data/log/faultlog/temp/cppcrash-478-1501837359497` (79869B) AND `/data/log/faultlog/faultlogger/cppcrash-bootanimation-1003-20170804170239497.log`
  - **bootanimation pid 478 SEGV** at `/system/lib/ld-musl-arm.so.1!get_meta+92` → `__libc_free+48` → `libav_codec_media_engine_modules.z.so` (in `__funcs_on_exit` / atexit handler)
  - Memory access `@0x9faae991` MAPERR; r0=`9faae989`, r1=`9faae981` — pointer arithmetic on freed heap
  - Process life time 34s, died at exit
- 1× ServiceReap line in `/dev/kmsg`: `[init][INFO] ServiceReap info teecd pid 5744`

These do not directly explain Channel A silence — bootanimation is a one-shot, teecd was reaped cleanly. But they confirm the OS is going through normal service supervision.

---

## 2. M6 chunking observed in 3f

**N/A — deploy aborted at Stage 0 before reaching Stage 3a, let alone 3f.**

```
_chunked_chmod count:            0
_chunked_symlink count:          0
_chunked_restorecon count:       0
_alive_probe fires within 3f:    0
_reset_shell_channel within 3f:  0
Default cadence (5/10) wedge:    N/A (never executed)
Finer cadence (3/6) attempted:   N
```

M6 chunking is correctly integrated in the script (verified by `grep` against the file at `e11272ad`), but cannot be exercised when Channel A is dead at preflight.

---

## 3. Stages completed

| Stage | Status | Notes |
|-------|--------|-------|
| 0 — preflight | **FAIL (G1 abort)** | G6 emitted WARN about hdc 3.2.0b not in known-good list (1.3.0c/d/e expected). Then `_alive_probe` fired → `hdc shell "echo HBC_DEPLOY_…"` returned empty → `abort 99`. |
| 1 → 6 | NOT REACHED | — |

Deploy log captured at `/tmp/v3-e2e-m6-deploy.log` (6 lines, total execution ~0.4s):
```
[15:52:27] Stage 0 · preflight (hdc version, board state, factory baseline)
[15:52:27] G6: hdc reports: Ver: 3.2.0b
  WARN G6: hdc version 'Ver: 3.2.0b' NOT in known-good list (Ver:1.3.0c Ver:1.3.0d Ver:1.3.0e)
  WARN G6: proceeding with caution; if W2-style silent-stdout returns, suspect this
  OK device visible: dd011a414436314130101250040eac00
[ABORT 99] G1: hdc shell silent (empty stdout). HBC全局三条 abort condition. STOP.
```

The G6 WARN is **prescient** — the script told us in plain English what the failure mode would be, then it materialized one line later.

---

## 4. HBC HelloWorld smoke

**N/A** — cannot dispatch `aa start` without `hdc shell`.

---

## 5. McD launch result + marker

**N/A** — cannot install/launch APK without `hdc shell` (`bm install`).

McD APK was not pulled (no point; no install path).

---

## 6. Failure mode + forensic

**Failure mode:** W2 postmortem hypothesis H2 ("Windows hdc.exe stdout-channel regression"). hdcd is healthy on device; the bridge from `hdc.exe` to `hdcd` for command output is broken.

**Distinguishing observations vs prior W2 episode (2026-05-16 agent 49):**

| Prior W2 event (2026-05-16) | This event (2026-05-20) |
|-----------------------------|--------------------------|
| Channel A went silent **mid-deploy after Stage-3 chcon batches** | Channel A silent **at boot, BEFORE any V3 write** |
| Followed silent-chcon `\|\| true` semantics + repeated hdc shell invocations | Followed a fresh operator reflash, no V3 touch since |
| hdcd respawn storm strongly suspected (H1) | hdcd PID 631 stable, sleeping in epoll_wait, NOT respawning — H1 unlikely |
| H2 (hdc.exe regression) listed as 2nd hypothesis | H2 promoted to **prime suspect** for this event |

This shifts evidence: the current state is hard to explain by anything the V3 deploy script did, because we never ran it on this fresh boot. The Channel-A silence appears to be either:
1. **`hdc.exe 3.2.0b` Windows-side regression** that manifests on a class of fresh-boot OHOS devices (most likely given the WARN in G6 was hand-coded by an earlier agent who knew about this).
2. **Default-deny hdc shell param** on this OHOS image: `/system/etc/init/hdcd.cfg` shows `persist.hdc.control.shell` param gating shell at boot; if its default value or persisted state is "false", Channel A is locked off.
3. **hdc auth / one-time-token state** unrecognized by the host server (`hdc list targets` succeeds but commands don't propagate).

### Forensic archive (Channel B captures)

All in `/mnt/c/Users/dspfa/AppData/Local/Temp/v3-m6-forensic/`:

```
cmdline.txt                          (634 B,  /proc/cmdline)
hdcd_cfg.txt                         (7407 B, /system/etc/init/hdcd.cfg)
hdcd_pid631_cmdline.txt              (17 B)
hdcd_pid631_stat.txt                 (254 B)
hdcd_pid631_status.txt               (1140 B, State=S, FDSize=64, 12 threads)
hdcd_pid631_wchan.txt                (13 B,  "do_epoll_wait")
init.txt                             (319872 B, /init ELF)
kmsg.txt                             (100 B, single ServiceReap line)
meminfo.txt                          (1257 B)
mounts.txt                           (3938 B, factory mount layout)
pid1_cmdline.txt                     (33 B, "/bin/init --second-stage 3078974")
pid1_status.txt                      (1113 B, State=S, FDSize=64)
system_init_cfg.txt                  (10514 B, vanilla OHOS init.cfg)
uptime.txt                           (16 B, "2461.95 9590.20")
version.txt                          (176 B, kernel banner)
_data_log_faultlog.bin/              (recursive dir, bootanimation cppcrash)
_data_log_faultlog_temp.bin/         (same crash, 79869 B copy)
```

Plus the deploy log: `/tmp/v3-e2e-m6-deploy.log` (and copy in same forensic dir).

---

## 7. Recommendation

**Next agent: DO NOT attempt M6 deploy on this board state.** It will abort at Stage 0 again — there is no chunking refinement that fixes a dead Channel A. Single-sweep mandate is unsatisfiable until Channel A is restored.

Triage path, in increasing intrusiveness:

1. **Swap hdc.exe to a 1.3.0c/d/e build** (matches the script's G6 known-good list). Likely cached somewhere on the operator's Windows side — search `C:\Users\dspfa\` for older `hdc.exe`, or pull from a corp build artifact. ~30 min experiment; if Channel A comes alive with old hdc → 3.2.0b regression confirmed, and we pin hdc.exe choice via env var in the deploy script.
2. **Try `hdc shell -d` (debug)** or any 3.2.0b-specific flag that might re-enable bytewise stdout proxying. Read 3.2.0b release notes (if available on developtools.openharmony.cn).
3. **Toggle hdc shell control param via file write.** If `persist.hdc.control.shell` is stored under `/data/service/el1/public/hdc/` or `/data/parameters/`, we can craft a file with the truthy value and write it via Channel B, then reboot. Risky — wrong path/format could brick worse, but file-only mutation is reversible by another file write.
4. **Operator hard power-cycle + bootloader-level hdc reset.** If steps 1-3 fail, the W2 postmortem R5 path (USB burn-tool reflash) is the recovery. The current reflash apparently completed (filesystems mount, services run) but didn't restore Channel A — suggests the issue is in `hdc.exe` not the image. Don't repeat the reflash without first proving #1 doesn't work.

**Strategic note:** M6 chunking is fine code and a valid mitigation for the in-deploy degradation pattern, but it does not address the pre-deploy dead-Channel-A pattern. Agent 90's mandate was a deploy attempt, not a recovery; the right move was to halt at the first hard-stop signal and document. That's what happened.

---

## Appendices

- Forensic dir: `/mnt/c/Users/dspfa/AppData/Local/Temp/v3-m6-forensic/`
- Deploy log:   `/tmp/v3-e2e-m6-deploy.log`
- Related docs: `docs/engine/V3-W2-POSTMORTEM.md` (R0 explicitly anticipated this state — see §5 "If R0 shell broken but file send/recv works… STOP for human review")
- Memory pointer: `feedback_soft_brick_w2_2026-05-16.md`
