# V3-W2 E2E Alt-HDC Sweep Report

**Date**: 2026-05-20
**Agent**: 92
**Mission**: Download OHOS 4.1 SDK, extract older hdc.exe (Ver 2.0.0a), retry Channel A,
end-to-end retry deploy if Channel A works.

---

## STATUS: CHANNEL_A_STILL_EMPTY_WITH_NEW_HDC

Channel-A (interactive `hdc shell <cmd>` returning stdout) is **broken on the
device side, not the host-binary side**. A newer/older host hdc.exe does not fix it.

---

## Step 1 — SDK download

| Item | Value |
| --- | --- |
| URL | `https://repo.huaweicloud.com/openharmony/os/4.1-Release/ohos-sdk-windows_linux-public.tar.gz` |
| Tarball size | 2,352,145,619 bytes (2.2 GiB) |
| Download time | 2 min 44 s (avg 13.6 MiB/s) |
| Cleanup status | tarball + intermediate zip + extracted dir REMOVED (see Cleanup section below) |

## Step 2 — hdc.exe extraction

Archive layout: outer `tar.gz` contains 5 inner zips per host OS. Windows hdc.exe
lives inside `ohos-sdk/windows/toolchains-windows-x64-4.1.7.5-Release.zip`,
path `toolchains/hdc.exe` (4,930,560 bytes, mtime 2001-01-01) and
`toolchains/libusb_shared.dll` (143,872 bytes).

Both binaries copied to:
- `$HOME/android-to-openharmony-migration/tools/hdc-alt/hdc.exe`
- `$HOME/android-to-openharmony-migration/tools/hdc-alt/libusb_shared.dll`

**Version check**:
```
$ $HOME/android-to-openharmony-migration/tools/hdc-alt/hdc.exe -v
Ver: 2.0.0a
```

That confirms a host binary older than the 3.2.0b in `C:\Users\dspfa\Dev\ohos-tools\`.

## Step 3 — Channel-A retest with new hdc

Killed running 3.2.0b daemon first (`hdc kill` → `Kill server finish`).
DAYU200 (target id `dd011a414436314130101250040eac00`) is enumerated and
file-transfer channel works; ONLY the shell-stdout channel is silent.

### Test results

```
$ ALT_HDC=$HOME/android-to-openharmony-migration/tools/hdc-alt/hdc.exe

$ $ALT_HDC list targets
dd011a414436314130101250040eac00            # device visible

$ $ALT_HDC shell 'echo TEST_$(date +%s); getenforce; date'
(empty stdout, exit 0)

$ $ALT_HDC -t dd011a414436314130101250040eac00 shell 'echo HI'
(empty stdout, exit 0)

$ $ALT_HDC shell ls /
(empty stdout, exit 0)

$ $ALT_HDC shell                                   # interactive form
[W][2026-05-20 16:34:08] Not support stdio TTY mode
```

### Critical disambiguation — shell command isn't even running server-side

To rule out "shell runs, only stdout pipe is silent" I issued a side-effect
shell command and then file-recv'd the artifact it should have produced:

```
$ $ALT_HDC shell 'echo HI > /data/local/tmp/shelltest.txt'
(empty stdout, exit 0)

$ $ALT_HDC file recv /data/local/tmp/shelltest.txt ./shelltest.recv
[Fail]Error opening file: no such file or directory, path:/data/local/tmp/shelltest.txt
```

The file was never created — the device-side shell command did not execute.
This is materially worse than "stdout silently dropped"; it means hdcd's
shell-channel is fully broken (parses the command then drops the request).

### Confirming the device is otherwise alive

File transfer channel works fine and proves the device is healthy:

```
$ $ALT_HDC file recv /system/etc/init.cfg ./init.cfg.recv
FileTransfer finish, Size:10514, File count = 1, time:11ms rate:955.82kB/s

$ $ALT_HDC file recv /proc/version ./version.recv
FileTransfer finish, Size:176, File count = 1, time:10ms rate:17.60kB/s
$ cat ./version.recv
Linux version 6.6.101 ([REDACTED]@GZ05) (OHOS (dev) clang version 15.0.4 …) #1 SMP Sat Apr  4 16:40:55 CST 2026
```

So:
- USB enumeration ............ OK
- `list targets` ............... OK
- `file recv` .................. OK (read /system, /proc)
- `shell` ..................... BROKEN (no stdout, no side-effect)
- `hilog` ..................... empty (consistent with shell-channel break — hilog uses similar plumbing)
- `fport ls` ................... `[Empty]` (works, just nothing forwarded)

Newer host hdc (3.2.0b) and older host hdc (2.0.0a) **both exhibit identical
device-side symptom**. Confirms operator hypothesis H2 (Windows hdc.exe
stdout-channel regression) is **wrong** — pivot to **H1 (SELinux respawn
storm) or a new H3 (hdcd shell-side helper missing/respawning)**.

## Step 4-5 — End-to-end deploy + McD launch

**SKIPPED** per script directive: "If output is still empty → device-side bug
confirmed. STOP + report."

Cannot run `scripts/v3/deploy-hbc-to-dayu200-hardened.sh all` because every
Stage of the hardened script issues `hdc shell` calls (chcon, mkdir, chmod,
init reload, am start). With shell-channel broken, deploy cannot complete
even with file-recv working.

## Step 6 — Cleanup

```
$HOME/android-to-openharmony-migration/tools/hdc-alt/
├── hdc-linux-2.0.0a        (5.0 MB, was already here)
├── hdc.exe                 (4.9 MB, NEW — Ver 2.0.0a, Windows)
├── libusb_shared.dll       (144 KB, NEW)
├── libusb_shared.so        (114 KB, was already here)
└── sdk-download/           (REMOVED post-extract to reclaim 2.4 GiB)
```

Tarball + intermediate zip + extracted dir deleted at the end of the sweep.

---

## Final board state

| Property | Value |
| --- | --- |
| Enumerated via USB | YES (id `dd011a414436314130101250040eac00`) |
| `list targets` | OK |
| `file recv` | OK |
| `shell` | BROKEN (no stdout, no side-effect) |
| Bricked? | NO — partially responsive, just shell-channel down |
| SELinux mode | unknown (cannot read via shell; would need a side-channel) |

Operator can still file-recv arbitrary files from the device. That's a valid
path for read-only forensics: e.g. `hdc file recv /proc/self/status`,
`/sys/fs/selinux/enforce`, etc.

## Recommendation — next productive move

Three options ranked:

1. **Hard recovery (operator action)** — power-cycle DAYU200, watch USB
   enumeration, retry `hdc shell echo X` immediately on first boot. If
   shell works on first boot but breaks again later → hdcd shell-side
   subprocess is being killed by a respawn loop (most likely SELinux denial
   per W2-POSTMORTEM H1). If shell is broken even on first boot → hdcd's
   shell helper itself is wedged (file/binary issue, possibly from chcon `||
   true` silent-skip in earlier deploy that left /system/bin/sh or shell
   helper mislabeled).

2. **Read-only forensic via file-recv** — without touching the device,
   pull these files via the still-working file-recv channel and inspect
   off-device:
   - `/proc/self/status`, `/proc/1/cmdline`, `/proc/mounts`
   - `/sys/fs/selinux/enforce`
   - `/data/log/hilog/*` (if accessible)
   - `/data/log/faultlog/*`
   - `/system/bin/sh` (to verify checksum vs ROM)
   - `/sys/fs/selinux/policy_capabilities/*`

   That gives state without further perturbation and lets us decide between
   H1 (SELinux) and H3 (shell binary corruption).

3. **ROM reflash** — full DAYU200 ROM reflash, then redo W2 deploy with the
   updated SOP (channel-health probe BETWEEN every Stage; no `chcon || true`;
   no silent SKIP). This is the most expensive option but is the cleanest
   path to a working W2 if (1) and (2) don't pinpoint the cause.

I recommend **(2) first** (zero risk, gives data), then **(1)** based on
findings.

## What this sweep ruled out

- Host hdc.exe version regression (3.2.0b → 2.0.0a tested, same symptom).
- USB cable / enumeration (file-recv proves USB stack OK).
- Device hung (file-recv works, hdcd file-server alive).

## What this sweep newly established

- Device-side shell channel is broken at the **execution** level, not the
  **stdout-piping** level (proven via the side-effect-and-recv probe). This
  is harder to repair than a piping bug.
- File-recv as a forensic side-channel is fully available — useful for the
  next agent.

---

## Files touched

- `tools/hdc-alt/hdc.exe` — NEW (extracted from OHOS 4.1 SDK)
- `tools/hdc-alt/libusb_shared.dll` — NEW
- `docs/engine/V3-W2-E2E-ALTHDC-REPORT.md` — THIS file

## Local commit

To be made by this agent at the end of the sweep. No push.
