# HBC hdc.exe candidates — search results

Date: 2026-05-20 (agent 81)

## Search outcome: EMPTY

Exhaustive search of HBC server (`[REDACTED]@[REDACTED-IP]:[REDACTED-PORT]`) found
**zero hdc.exe Windows binaries**. The HBC server builds OpenHarmony
SDK for **Linux only** — its mingw_x86_64 cross-build target does not
include hdc.

### Searched paths (all negative for hdc.exe)
- `$HOME/D200/HiHope_DAYU200/烧写工具及指南/windows/` —
  contains RKDevTool.exe (Rockchip flasher) + DriverAssistant_v5.1.1.zip, no hdc.
- `$HOME/oh/out/sdk/clang_x64/` — Linux `hdc` only (ELF, 6.16 MB,
  sha256 `5ddf68c67cc6d45dd4d0b426062dc950b754203c459e02f8e1e85dfcd615bbcc`).
- `$HOME/oh/out/sdk/mingw_x86_64/` — present but contains no hdc.
- `$HOME/oh/out/sdk/sdk-native/os-specific/windows/` — only build-tools + llvm.
- `$HOME/oh/prebuilts/ohos-sdk/` — `linux/` only, no `windows/`.
- `$HOME/oh/prebuilts/tool/command-line-tools/bin/` — `hvigorw`, `ohpm` (no hdc).
- Full `find /home -name hdc.exe` across the box: empty.

### Implication for Path B agent-81 mandate
Hard Stop #13 triggered: "HBC server unreachable or hdc.exe not findable —
STOP + report; don't fabricate 'known-good' without proof".

The premise "fetch known-good 1.3.0c/d/e pinned hdc.exe from HBC" cannot
be satisfied from HBC. If a pinned version is needed in the future, it
must be sourced from an OHOS release SDK bundle (Huawei Cloud / OpenHarmony
Releases mirror) — not from this HBC server.

### What WAS executable
Step 3 (long-load probe) is independent of pinning and was executed
against the only Windows hdc available locally (`/mnt/c/Users/dspfa/Dev/
ohos-tools/hdc.exe`, Ver 3.2.0b). See `docs/engine/V3-W2-PATH-B-RESUMED.md`.
