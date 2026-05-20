# V3 W2 Stage A — Preflight + Snapshot-only

Date: 2026-05-19
Board: DAYU200 (dd011a414436314130101250040eac00)
Script: `scripts/v3/deploy-hbc-to-dayu200-hardened.sh` (commit 73ae3ac1, 1707 LOC)
Agent: 76
Operator gate: Stage A approved; Stage B NOT yet approved.

---

## TL;DR

- **PASS.** All Stage 0 preflight gates clean (G1, G3, G6 widened-warn, Gate 13 processdump probe, Gate 8 snapshot armed).
- **Zero /system writes** during preflight, `--snapshot-only`, `restore-chcon`, and `probe-processdump` — pre/post diffs of `/system/lib/*` and `mount` table are both empty.
- **Stage B unblocked from a safety-gate perspective.** Recommend operator approve Stage B.

---

## 1. Preflight result (per-gate, Stage 0)

| Gate | Result | Notes |
|------|--------|-------|
| G6 hdc version | WARN (proceed) | Reports `Ver: 3.2.0b` — not in known-good list (`1.3.0c/d/e`). Widened to WARN per agent-75 fix; no W2-style silent-stdout symptom seen during Stage A. |
| device visible | OK | `dd011a414436314130101250040eac00` |
| G1 channel sentinel | OK | `echo` round-trip succeeded |
| uname | OK | `Linux localhost 6.6.101 #1 SMP Sat Apr 4 16:40:55 CST 2026 aarch64 Toybox` |
| LONG_BIT | OK | userspace LONG_BIT=32 (matches V3 32-bit-ARM mandate) |
| selinux | OK | `Enforcing` |
| /system/android absent | OK | Factory baseline confirmed |
| hdcd alive | OK | pid=2207 |
| G3 required artifacts | OK | 63/63 host-side artifacts present |
| **Gate 13 processdump** | **OK (rc=0)** | New diagnostic-capability gate — passes. Recovery path armed. |
| Gate 8 snapshot init | OK | `/data/local/tmp/v3-chcon-snapshot.txt` created |
| Stage 0 final | **PASS** | "preflight clean, factory baseline confirmed (Gate 13 + Gate 8 armed)" |

Log: `/tmp/v3-stage-a-preflight.log`

## 2. Snapshot-only result (Gate 8 baseline capture)

- Invocation: `bash deploy-hbc-to-dayu200-hardened.sh --snapshot-only`
- Init: pre-existing snapshot file preserved on board (idempotent re-arm; first-capture-wins).
- 11 high-risk paths probed — **all skipped with WARN "(file may not exist yet)"**. This is semantically correct: every probed path is a NEW V3 artifact (`liboh_android_runtime.so`, `liboh_hwui_shim.so`, `liboh_skia_rtti_shim.so`, `liboh_adapter_bridge.so`, `fonts.xml`, `appspawn-x` — both `/system/lib` and `/system/android/lib` variants). On factory baseline none of these exist, so there is no pre-existing factory label to record; on Stage B push, the script will assign labels and the snapshot will record the assigned label as the rollback target.
- Snapshot on device: `/data/local/tmp/v3-chcon-snapshot.txt` (1 line, header-only)
  - Content: `# v3 chcon snapshot Tue May 19 17:32:24 PDT 2026`
- Exit: PASS — "invoke 'restore-chcon' subcommand to replay"

Log: `/tmp/v3-stage-a-snapshot.log` / snapshot pulled: `/tmp/v3-stage-a-snapshot-contents.txt`

## 3. Independent post-verification (diffs)

Pre-state captured BEFORE preflight; post-state captured AFTER snapshot-only; final-state captured AFTER restore-chcon.

| Diff | Lines | Result |
|------|-------|--------|
| `/system/lib/lib{wms,ams,bms,apk_installer}.z.so` + `/system/etc/init/` ls -laZ pre→post | 0 | empty |
| `mount` pre→post | 0 | empty |
| `/system/...` ls -laZ pre→final (after restore-chcon) | 0 | empty |
| `mount` pre→final | 0 | empty |
| `getenforce` final | `Enforcing` | unchanged |

Pre-state observations (baseline anchors for later verification):
- `/system/lib/libwms.z.so` — `u:object_r:system_lib_file:s0`, 1,072,868 bytes, 2026-04-04 20:36
- `/system/lib/libbms.z.so` — `u:object_r:system_lib_file:s0`, 5,048,116 bytes, 2026-04-04 20:36
- `/system/lib/libams.z.so` — NOT PRESENT (factory)
- `/system/lib/libapk_installer.so` — NOT PRESENT (factory)
- `/system/etc/init/` — `dr-x------ root:root u:object_r:system_etc_file:s0` (factory)

Pre/post artifacts: `/tmp/v3-stage-a-{pre,post,final}-state.txt`, `/tmp/v3-stage-a-{pre,post,final}-mounts.txt`, `/tmp/v3-stage-a-{state,mounts}-diff.txt`.

## 4. restore-chcon subcommand validation

- Invocation: `bash deploy-hbc-to-dayu200-hardened.sh restore-chcon`
- Recv'd snapshot from board, parsed, replayed: **0 restored, 0 failed** (correct — snapshot has only the header line; no data rows to replay).
- No `chcon` system call issued (replay loop has zero iterations).
- Subcommand is invokable, parses cleanly, no aborts.
- Post-restore re-diff of `/system` + mounts: still empty. Invariant intact.

Log: `/tmp/v3-stage-a-restore-dryrun.log`

## 5. Stage A acceptance: **PASS**

All five acceptance criteria met:
- [x] All preflight gates pass
- [x] Gate 13 processdump probe succeeds (standalone probe also re-confirmed)
- [x] Gate 8 chcon snapshot captures (file initialised on device; high-risk paths probed)
- [x] Zero /system file modifications (state diff empty across two probe windows)
- [x] Zero mount changes (mount diff empty across two probe windows)
- [x] restore-chcon subcommand invokable (bonus check passed)

Brick-safety invariant **HELD**.

## 6. Board state captured for forensic

| Artifact | Path |
|----------|------|
| Preflight log | `/tmp/v3-stage-a-preflight.log` |
| Snapshot-only log | `/tmp/v3-stage-a-snapshot.log` |
| Snapshot contents (pulled) | `/tmp/v3-stage-a-snapshot-contents.txt` |
| Restore-chcon log | `/tmp/v3-stage-a-restore-dryrun.log` |
| Pre-state /system + mounts | `/tmp/v3-stage-a-pre-state.txt`, `/tmp/v3-stage-a-pre-mounts.txt` |
| Post-state (after snapshot-only) | `/tmp/v3-stage-a-post-state.txt`, `/tmp/v3-stage-a-post-mounts.txt` |
| Final-state (after restore-chcon) | `/tmp/v3-stage-a-final-state.txt`, `/tmp/v3-stage-a-final-mounts.txt` |
| Diffs (empty) | `/tmp/v3-stage-a-state-diff.txt`, `/tmp/v3-stage-a-mounts-diff.txt` |

## 7. Recommendation for Stage B

**GO** (operator approval still required).

Justification:
- Hardened script's preflight + safety-net plumbing exercises end-to-end without side-effects.
- Gate 13 processdump probe passes — diagnostic-capability gap from Phase 1b chroot path is closed for the /system path.
- Gate 8 snapshot plumbing armed and validated; restore-chcon recovery path proven invokable.
- One soft observation worth flagging to operator before Stage B: G6 widened-warn on `Ver: 3.2.0b` is the right call given W2 silent-stdout precedent. Stage B should re-check channel-sentinel between stages per the W2 postmortem rule; the hardened script already does this via `_alive_probe` / `hdc_shell` sentinels, but operators should keep an eye on the live log for any sentinel gap.

No blocker found. Hard-stop conditions (1-11) all clear.

---

## Hard-stop audit

| # | Condition | Triggered? |
|---|-----------|------------|
| 1 | connect-key | No |
| 2 | shell timeout >30s | No |
| 3 | `[Fail]` line | No |
| 4 | drwx | No |
| 5 | board unreachable | No |
| 6 | write outside `/data/local/tmp/` | No (snapshot file is at `/data/local/tmp/v3-chcon-snapshot.txt`) |
| 7 | SELinux state changed | No (`Enforcing` → `Enforcing`) |
| 8 | control-flow if without visible eval | No |
| 9 | push without target byte-match | No (no pushes) |
| 10 | macro-shim violation | N/A |
| 11 | substrate-level fix needed | No |

None triggered. Stage A clean.
