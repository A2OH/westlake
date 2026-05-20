# Alternate hdc.exe Search

Date: 2026-05-20
Agent: 91 (pure host research, no board contact)
Predecessor context: agent 90 (commit `a8ceb502`) — confirmed `/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe` Ver `3.2.0b` returns empty stdout/stderr for every shell command against freshly-reflashed DAYU200; G6 gate of hardened script flags 3.2.0b as "not in known-good list (1.3.0c/d/e expected)".

## TL;DR (one sentence)

No alternate **Windows** `hdc.exe` exists anywhere on this system; the only fallback locally available is a **Linux** `hdc` Ver `2.0.0a` built from the OHOS 4.1.10.5 source tree (copied to `tools/hdc-alt/hdc-linux-2.0.0a`), so the operator-side decision is either (a) run that Linux hdc from WSL against the board over USB-passthrough, or (b) download `ohos-sdk-windows_linux-public.tar.gz` (2.2 GiB for 4.1-Release or 4.3 GiB for 5.0.3-Release) from Huawei Cloud to extract a known-good `hdc.exe`.

## 1. Locations searched + candidate hdc.exe found (path + version)

### Windows side (`/mnt/c`)

| Path | Result |
|---|---|
| `/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe` | **Ver 3.2.0b** — the suspect, NOT overwritten |
| `/mnt/c/Users` (recursive) | only the suspect above |
| `/mnt/c/Program Files*` | none |
| `/mnt/c/*deveco*` | DevEco Studio NOT installed (only a stale Rust crate doc reference under cargo registry) |
| `/mnt/c/*openharmony*` | only Rust toolchain HTML docs |
| `/mnt/c/*ohos*` | only the `ohos-tools` dir already searched + Cargo registry indexes |
| `/mnt/c/*Huawei*` | none |
| Full case-insensitive `find /mnt/c -iname 'hdc*'` | only suspect + `HdcpHandler.dll` (Windows HDCP, unrelated) + temp logs from prior hdc runs |

### Linux side (`/`, `$HOME`)

| Path | Result |
|---|---|
| `$HOME/openharmony/out/sdk/clang_x64/developtools/hdc/hdc` | Linux ELF x86-64, **Ver 2.0.0a**, 5,041,848 bytes |
| `$HOME/openharmony/out/sdk/ohos-sdk/linux/toolchains/hdc` | same file (identical sha256) packaged into SDK |
| `$HOME/openharmony/out/sdk/clang_x64/exe.unstripped/.../hdc` | unstripped twin |
| Wider `find /` for any `hdc.exe` | empty |
| Wider `find /` for any `hdc` binary | only the three above + WSL distro mount duplicate |
| `$HOME/android-to-openharmony-migration/tools/hdc-candidates/` | empty (agent 81 stub README only) |

### HBC server (historical, NOT re-probed this session)

Per `tools/hdc-candidates/README.md` from agent 81 (2026-05-20): HBC server `[REDACTED]@[REDACTED-IP]:[REDACTED-PORT]` has **zero `hdc.exe`** anywhere; only Linux `hdc` ELF at `$HOME/oh/out/sdk/clang_x64/hdc` (sha256 `5ddf68c67cc6d45dd4d0b426062dc950b754203c459e02f8e1e85dfcd615bbcc`, different from local because HBC's tree is at a different revision). HBC's `mingw_x86_64/` cross-build target does not include hdc.

## 2. Pulled binary (if any) — path + sha256

```
$HOME/android-to-openharmony-migration/tools/hdc-alt/hdc-linux-2.0.0a
  sha256: adf319a2cfec63595e959cff588e3f3ba7577a6fcd4d246bb6e9c6696d50dbe6
  size:   5,041,848 bytes
  type:   ELF 64-bit LSB pie executable, x86-64, dynamically linked, stripped
  source: $HOME/openharmony/out/sdk/ohos-sdk/linux/toolchains/hdc
          (built locally from OHOS 4.1.10.5 source tree, dated 2026-03-22)

$HOME/android-to-openharmony-migration/tools/hdc-alt/libusb_shared.so
  sha256: e0bb5549d6523ba160b8d97aff8f5926a122647ad490aca92cd0c1b0bd1d5c18
  size:   113,608 bytes
```

For comparison, the suspect:
```
/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe
  sha256: f6d6c47551d976f33b0f22b17a74f345c0788e59131873aa5f75d356f5141d9b
  size:   5,448,704 bytes
  ver:    3.2.0b
  (NOT modified — left in place for forensic comparison)
```

## 3. Version verify (output of hdc -v)

```
$ $HOME/android-to-openharmony-migration/tools/hdc-alt/hdc-linux-2.0.0a -v
Ver: 2.0.0a
```

Ldd confirms the Linux hdc 2.0.0a links cleanly to system libstdc++/libc and bundled `libusb_shared.so` (the latter is in the same dir, found via rpath `./`).

Note: hardened script's "known-good" pin is **1.3.0c/d/e**. Ver 2.0.0a is **not** the pinned version, but it is one major version older than 3.2.0b and produced from the **same local source tree** that targets the DAYU200 we're working with — significantly more likely to be protocol-compatible with the on-board `hdcd` than 3.2.0b is.

## 4. Public download attempts (URLs + success/fail)

All downloads ABORTED — every Windows-containing artifact exceeds the 50 MB no-confirm cap. URLs verified reachable via `curl -sI -L`:

| Source | Path | Size | Reachable | Pulled? |
|---|---|---|---|---|
| Huawei Cloud | `https://repo.huaweicloud.com/openharmony/os/3.2-Release/ohos-sdk-windows_linux-public.tar.gz` | 1.6 GiB | not HEADed (older than 4.x; would contain hdc roughly Ver ~1.x) | NO |
| Huawei Cloud | `https://repo.huaweicloud.com/openharmony/os/4.0-Release/ohos-sdk-windows_linux-public.tar.gz` | 2.0 GiB | not HEADed | NO |
| Huawei Cloud | `https://repo.huaweicloud.com/openharmony/os/4.1-Release/ohos-sdk-windows_linux-public.tar.gz` | **2.19 GiB** (2,352,145,619 B) | **HTTP 200 OK** verified | NO (>50 MB) |
| Huawei Cloud | `https://repo.huaweicloud.com/openharmony/os/5.0.3-Release/ohos-sdk-windows_linux-public.tar.gz` | **4.25 GiB** (4,563,988,878 B) | **HTTP 200 OK** verified | NO (>50 MB) |
| Huawei Cloud | `https://repo.huaweicloud.com/openharmony/os/6.1-Release/ohos-sdk-windows_linux-public.tar.gz` | 2.3 GiB | not HEADed | NO |
| Huawei Cloud | `https://repo.huaweicloud.com/harmonyos/ohpm/5.0.5/commandline-tools-linux-x64-5.0.5.310.zip` | 3.0 GiB | not HEADed; LINUX ONLY — no windows variant exists in this mirror | NO |
| Huawei Cloud | `https://repo.huaweicloud.com/harmonyos/develop_tools/` | n/a | reachable | nothing useful (only `hapsigntoolv2.jar`, `bpftool`, `pahole`) |
| Huawei Cloud | `https://repo.huaweicloud.com/harmonyos/sdk/SDK_2.1.1.22.rar` | 34.7 KiB | not pulled | tiny — looks like an obsolete stub, almost certainly does not contain hdc.exe |
| OpenHarmony CI | `https://ci.openharmony.cn/workbench/cicd/dailybuild/dailylist` | n/a | **FAIL** — TLS certificate expired | NO |
| Gitee | `https://gitee.com/openharmony/developtools_hdc/releases` | n/a | reachable but **no binary attachments** — repo uses tag-only releases (OpenHarmony-vX.X.X-Release naming) with no prebuilt binaries | NO |
| DevEco Studio CDN | `developer.harmonyos.com/cn/develop/deveco-studio/` | n/a | redirects to authenticated huawei.com — would need Huawei Developer Alliance login for direct DevEco Studio download | NO |

**No mirror surfaces a standalone hdc.exe**. Every Windows hdc.exe is bundled inside `ohos-sdk-windows_linux-public.tar.gz` somewhere under `windows/toolchains/hdc.exe` (path inferred from local 4.1.10.5 layout — locally we only have `linux/toolchains/hdc` because our build targeted linux only).

## 5. Recommendation

Three options in order of cost / impact:

### Option A (cheapest, recommended for next agent regardless) — try the bundled Linux hdc 2.0.0a from WSL

The Linux hdc Ver 2.0.0a at `$HOME/android-to-openharmony-migration/tools/hdc-alt/hdc-linux-2.0.0a` is **already on disk, no download needed**. Even though WSL2 does not have native USB host access, `usbipd-win` (free, Microsoft-signed) bridges Windows USB devices into WSL2 and is a standard install. The next agent can:

1. On Windows: `usbipd list` → find DAYU200 (Vendor 2207:0011) → `usbipd bind --busid <X-Y>` → `usbipd attach --wsl --busid <X-Y>`.
2. In WSL: `lsusb` confirms device visible.
3. Run `$HOME/android-to-openharmony-migration/tools/hdc-alt/hdc-linux-2.0.0a list targets`.
4. If `hdc shell echo OK` returns "OK" non-empty, **the bug is in 3.2.0b's Windows-side TCP/stdout plumbing, not on the board** — pivot to Option A as the authoritative tool and re-run agent 90's repro shell-command-by-shell-command.

This is the cheapest way to falsify the "board hdcd is broken" hypothesis vs. the "Windows hdc.exe 3.2.0b is broken" hypothesis.

### Option B — operator downloads 4.1-Release SDK from Huawei Cloud (~2.2 GiB)

If Option A confirms the board hdcd is fine and only 3.2.0b is broken, the operator needs a Windows hdc.exe. Download `https://repo.huaweicloud.com/openharmony/os/4.1-Release/ohos-sdk-windows_linux-public.tar.gz` (2.19 GiB, HTTP 200 verified). Extract `windows/toolchains/hdc.exe` (path pattern from local Linux SDK layout). Use 4.1-Release specifically because it matches the OHOS version our DAYU200 image is built from (4.1.10.5) → highest compat probability.

User-confirmation gate: this is a 2.2 GiB pull. **Operator must explicitly approve** before any agent fetches it.

### Option C — operator installs DevEco Studio

Heaviest. DevEco Studio Windows installer is ~3-5 GiB, requires Huawei Developer Alliance account login, and pulls in IDE bloat unnecessary for command-line hdc work. **Not recommended unless Option A and Option B both fail.**

### NOT viable

- Pulling 1.3.0c/d/e specifically: no public mirror or release archive surfaces it. The "1.3.0c/d/e" pin in the hardened script appears to be the version originally tested by an earlier agent against a specific known-good board state; it is not a release-tagged version.
- HBC server: agent 81 already verified no hdc.exe anywhere on HBC.
