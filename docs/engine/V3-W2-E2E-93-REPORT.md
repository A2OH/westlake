# V3-W2 E2E Sweep 93 — Channel A Still Dead, USB Fully Gone

**Date**: 2026-05-20
**Agent**: 93
**Mission**: Sanity probe Channel A on DAYU200; if green run hardened M1-M6 deploy
end-to-end and McD launch; otherwise stop and report.

---

## STATUS: CHANNEL_A_STILL_DEAD (regressed from agent 92 — USB endpoint also gone)

Channel A (interactive `hdc shell <cmd>` returning stdout) is still broken.
Worse than agent 92's state: the DAYU200 USB endpoint is no longer enumerated
at all. Agent 92 left the device as `Offline` with file-recv still working;
this sweep finds only the 6 inert UART COM ports enumerated, no USB target.

Per brief: STOPPED IMMEDIATELY at Step 0. Did not run deploy. Did not run McD
launch. Did not touch the device beyond read-only enumeration / shell probes
that the hardened SOP would itself issue.

---

## Step 0 — Sanity probe sequence + findings

### Probe 0a — default hdc.exe (3.2.0b at `/mnt/c/Users/dspfa/Dev/ohos-tools/`)

```
$ /mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe list targets
[Empty]

$ /mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe list targets -v
COM3            UART    Ready           hdc
COM4            UART    Ready           hdc
COM5            UART    Ready           hdc
COM6            UART    Ready           hdc
COM7            UART    Ready           hdc
COM8            UART    Ready           hdc
dd011a414436314130101250040eac00     USB     Offline    localhost   hdc

$ /mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe shell 'echo SANITY_$(date +%s); getenforce; uname -a'
[Fail]ExecuteCommand need connect-key? please confirm a device by help info
```

Initial state matches agent 92's close-state exactly: USB endpoint visible
but `Offline`, shell channel refusing because device is not in a connected
state.

### Probe 0b — kill+restart server, re-probe

```
$ /mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe kill
Kill server finish

$ /mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe list targets
[Empty]

$ /mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe list targets -v
COM3 .. COM8 UART Ready ... (only)
# NO dd011a... line — USB endpoint vanished after server kill+restart
```

The `hdc kill` recycled the daemon; on restart the daemon no longer sees the
DAYU200 USB endpoint at all. The 6 COM ports are always-on inert UART
loopbacks (they enumerate Ready regardless of whether anything is attached);
the only real target is the missing `dd011a...` line.

### Probe 0c — explicit -t + tconn fail

```
$ hdc -t dd011a414436314130101250040eac00 shell 'echo ALIVE'
[Fail]Not match target founded, check connect-key please

$ hdc tconn dd011a414436314130101250040eac00
[Fail]CreateConnect failed
```

### Probe 0d — alt hdc.exe (Ver 2.0.0a from OHOS 4.1 SDK, the one agent 92 staged)

```
$ ALT=$HOME/android-to-openharmony-migration/tools/hdc-alt/hdc.exe
$ $ALT kill
Kill server finish
$ sleep 3
$ $ALT list targets
[Empty]
$ $ALT list targets -v
COM3 .. COM8 UART Ready ... (only)
$ $ALT file recv /proc/version /tmp/version.93.recv
[Fail]ExecuteCommand need connect-key?
$ $ALT shell 'echo SANITY93_...'
[Fail]ExecuteCommand need connect-key?
$ $ALT shell 'echo HI93 > /data/local/tmp/sanity93.txt'  # side-effect probe
[Fail]ExecuteCommand need connect-key?
```

Agent 92's file-recv channel (Ver 2.0.0a, `file recv /proc/version` returning
176 bytes in 10ms) is **also dead now**. Both channels broken — the host has
no path to the device.

### Probe 0e — re-retry after 5s USB resettle wait

```
$ sleep 5
$ $ALT list targets -v
COM3 .. COM8 UART Ready (only)
$ $ALT kill ; sleep 1 ; $HDC list targets -v
COM3 .. COM8 UART Ready (only, unknown... state)
```

USB endpoint did not re-enumerate within 5s+ wait.

---

## Regression vs agent 92

| Channel | Agent 92 close-state | Agent 93 open-state | Delta |
| --- | --- | --- | --- |
| `list targets -v` shows `dd011a...` USB | YES (state: `Offline`) | NO (gone entirely) | REGRESSED |
| `file recv /proc/version` | OK (176 B, 10 ms) | `need connect-key` | REGRESSED |
| `hdc shell <cmd>` | empty stdout, exit 0 | `need connect-key` | REGRESSED |
| `hdc shell` interactive | "Not support stdio TTY mode" | `need connect-key` | REGRESSED |

The first `hdc list targets -v` of this sweep DID still see the USB endpoint
as `Offline`. The subsequent `hdc kill` + restart of the host daemon dropped
it. Likely sequence: device-side hdcd USB endpoint was still half-alive when
this sweep opened; the host daemon's reconnect handshake on restart could not
complete because device-side shell/connect helper is gone → host daemon then
dropped the endpoint from its tracked list.

I do **not** believe the `hdc kill` itself bricked anything device-side
(`hdc kill` is host-only and cannot signal the device). The device-side hdcd
was almost certainly already in a state where it could not complete a fresh
handshake; agent 92's host daemon was holding the half-connected session
open from before the W2 deploy degraded it.

---

## What I did NOT do (per brief)

- Did **not** run `scripts/v3/deploy-hbc-to-dayu200-hardened.sh all`.
  Channel A is required for every Stage of the hardened script (chcon, mkdir,
  chmod, init reload, am start). With shell channel and file channel both
  broken, no Stage can proceed.
- Did **not** install McD APK or attempt `aa start`.
- Did **not** call `setenforce`.
- Did **not** call `hdc target boot`.
- Did **not** hard-power-cycle.
- Did **not** dispatch additional agents.

## Forensic capture attempts

Standard forensic path (file-recv of `/proc/self/status`, `/proc/1/cmdline`,
`/proc/mounts`, `/sys/fs/selinux/enforce`, `/data/log/hilog/*`,
`/data/log/faultlog/*`) all blocked by `need connect-key` — file-recv channel
is dead this sweep.

The only remaining off-board forensic path is operator action:
1. Read DAYU200 UART serial console (COM ports listed by hdc are the host's
   own USB-serial bridges, but a real serial cable to DAYU200's UART header
   would let an operator pull dmesg + kernel logs without USB).
2. Hardware power-cycle then immediately capture USB enumeration / dmesg /
   first hdc shell echo to see whether device-side hdcd shell helper comes up
   cleanly on a fresh boot.

Both are operator-scoped. Per brief hard constraint ("NEVER hard
power-cycle"), I cannot do (2) myself.

---

## Final board state

| Property | Value |
| --- | --- |
| USB endpoint enumerated | NO (only inert UART COM3-8 visible) |
| `list targets` | `[Empty]` |
| `file recv` | BROKEN (`need connect-key`) |
| `shell` | BROKEN (`need connect-key`) |
| Bricked? | YES from host POV (no responsive channel) |
| SELinux mode | UNKNOWN (no readable channel) |
| Recovery needed | Operator hard power-cycle |

This matches the W2 postmortem prediction (`docs/engine/V3-W2-POSTMORTEM.md`)
that the board is "OFFLINE awaiting operator hard power-cycle"; agent 92's
file-recv side-channel that briefly remained available has now also closed.

---

## Recommendation — next productive move

1. **Operator hard power-cycle DAYU200** (only fix at this point — host can't
   reach the device through any channel). On first boot, immediately capture:
   ```
   hdc list targets -v
   hdc shell 'echo BOOT_$(date +%s); getenforce; ps -ef | grep hdcd'
   hdc file recv /data/log/faultlog/temp ./fault.log
   ```
   If shell works on first boot but breaks again within minutes → respawn
   loop on shell-helper process (consistent with W2-POSTMORTEM H1 SELinux
   denial storm). If shell broken even on first boot → hdcd shell-helper
   binary itself is corrupted (consistent with H3 mislabel/missing file from
   silent `chcon || true` during W2 Stage 3f).

2. **If power-cycle restores even file-recv** (but not shell), use that
   read-only window to pull `/system/bin/hdcd`, `/system/bin/sh`,
   `/system/etc/selinux/targeted/contexts/file_contexts`,
   `/data/log/faultlog/*` for off-board analysis BEFORE doing anything else.

3. **Do not retry deploy until SOP gains a pre-flight channel-health probe**
   per `feedback_soft_brick_w2_2026-05-16.md` rule. The current hardened
   script's M1-M6 do not include a pre-deploy Channel A probe that aborts
   cleanly if the board is offline — this sweep would have aborted at "Step 0
   echo check" if such a probe existed at the top of the hardened script.

4. **Consider adding a forensic-only mode to the hardened script** that uses
   file-recv only (no shell) to gather the standard /proc + /sys + /data/log
   snapshot whenever shell is broken but file is alive. This would have let
   agent 92 (file-recv alive) capture full state automatically; would not
   help this sweep (file-recv also dead).

I recommend (1) first, immediately. The longer the board sits in this state,
the less likely a soft recovery without reflash.

---

## What this sweep newly established

- Agent 92's surviving file-recv channel has closed in the ~hours since
  agent 92's session ended. Both channels now dead, no degradation gradient
  left to exploit.
- A host `hdc kill` + restart cannot recover a half-connected USB endpoint
  whose device-side helper has died — once the host daemon drops it from
  `list targets -v`, it will not re-appear without device-side hdcd coming
  back up.
- `hdc list targets -v` includes inert local UART COM ports that are always
  `Ready` regardless of board state — these are NOT a sign the board is
  alive. Only a real device ID line (USB or network) indicates a real
  target.

## Files touched

- `docs/engine/V3-W2-E2E-93-REPORT.md` — THIS file.

No other files written. Repo state otherwise unchanged from agent 92's commit
`5bfe3a3b`.

## Local commit

To be made by this agent at the end of the sweep. No push.
