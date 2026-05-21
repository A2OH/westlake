# V3 HBC — system-restoration disable patches inventory

**Investigation date**: 2026-05-21 (agent 107)
**Mandate**: Find whether HBC's `ohos_patches/` tree disables the
`module_update_sa` / `sys_installer_sa` / `mksandbox` mechanism that
agent 105 observed wiping `/system` on every reboot of the V7
DAYU200 ROM. If yes, document the diff; if no, redirect the search.

**Verdict**: **NO DISABLE PATCH FOUND.** HBC does *not* patch out
`module_update`, `sys_installer`, or the `mksandbox system` line in
`init.cfg`. They run the same factory init pipeline as we do. Their
deploy must persist for a reason unrelated to source patches —
probably a difference in their physical bench device's ROM, partition
layout, or boot mode, not anything captured in `ohos_patches/`.

## Sources surveyed

| Tree | Path | Files |
| ---- | ---- | ----- |
| Local pinned copy | `westlake-deploy-ohos/v3-hbc/patches/ohos_patches/` | 71 |
| HBC server live | `[REDACTED]@[REDACTED-IP]:$HOME/adapter/ohos_patches/` | 86 (incl. `.patch` and applied copies) |
| HBC server OH src | `$HOME/oh/` (baseline + module_update + sandbox cfgs) | reference only |

Local copy = HBC live (15 extra entries on server are `.patch` sidecars
co-located with the post-patch file, same content). Patches script
manifest: `adapter/build/inner/apply_ohos_patches.sh`.

## Candidate patches scanned

Searched HBC's full patch set for keywords `module_update`,
`sys_installer`, `mksandbox`, `sandbox`, `wipe`, `restor`, `disable`,
`updater`, `/system/etc/init`, `init.cfg`. Findings:

| Patch file | Touches /system persistence? | Note |
| ---------- | ---------------------------- | ---- |
| `init/apply_init_max_env_value.py` | NO | Bumps `MAX_ENV_VALUE 128 -> 1024` in `init_service.h` so init can carry long BOOTCLASSPATH env strings for appspawn-x. Pure env-buffer fix, unrelated to /system restoration. |
| `base/startup/init/services/init/include/init_service.h` | NO | Output of the above script. Mentions of `SANDBOX`/`MODULE_UPDATE` are the upstream `SERVICE_ATTR_*` bit-flag constants, untouched by the patch. |
| `base/startup/appspawn/*` (4 files) | NO | appspawn-x routing/sepolicy/namespace patches. App-spawn side, not init. |
| `base/security/selinux_adapter/.../file_contexts` | NO | Adds SELinux labels for the appspawn-x binary/cfg. Doesn't relabel anything that would block module_update or block writes to /system. |
| `build/foundation/arkui/ui_lite/ext/updater/BUILD.gn.patch` | NO | Removes `graphic_utils_lite:utils_lite` dependency from the lite-UI updater UI module. This is the cosmetic updater-mode UI, not the `sys_installer`/`module_update_sa` services. One-line `# patched` comment-out, totally unrelated to /system restoration. |
| `bundle_framework/**` (~25 files) | NO | BMS APK-install path, not init or sys_installer. |
| `foundation/ability/**` (3 files) | NO | Multi-Ability Mission stack support. |
| `graphic_2d/**`, `graphic_surface/**`, `third_party/skia/**` | NO | Rendering. |
| `third_party/musl/**` (3 files) | NO | `ld-musl-namespace-arm.ini` for AOSP lib path, `ADLTSection.h`, `create_syscall.sh`. Linker/syscall, not init. |
| `third_party/icu/**`, `third_party/skia/.../icu/**` | NO | ICU numeric formatting. |
| `vendor/hihope/rk3568/config.json` | NO | Vendor product config, no `system_restore`/`module_update` keys. |

**No init.cfg patch.** HBC's `$HOME/oh/base/startup/init/services/etc/init.cfg`
on the server still contains lines 36-37:

```
"mksandbox system",
"mksandbox chipset",
```

verbatim — unmodified. No patch in `ohos_patches/` targets `init.cfg`,
no patch disables the `module_update_sa.cfg` or `sys_installer_sa.cfg`
services either. Both .cfgs are present on HBC and marked
`"ondemand": true` (i.e. they don't auto-start in `pre-init`; they
wait for SAMgr load-on-demand), which means they're not the wiper
anyway.

## Init/restoration mechanism: what HBC actually has

HBC's stock OH 7.0.0.18-strict (`weekly_20260302`) baseline has:

1. **`mksandbox system`** in init.cfg — just a mount-namespace bind
   (`/system/{bin,etc,lib,profile,...}` → `/mnt/sandbox/system/system/...`,
   per `services/sandbox/system-sandbox.json`). **Not** a copy or restore;
   the underlying inode is shared. Modifying /system files outside the
   sandbox is visible inside it.
2. **`module_update_sa`** — `ondemand: true`, user `update`, sandbox 0.
   Scans `MODULE_PREINSTALL_DIR = "/system/module_update"` for `.hmp`
   packages and bind-mounts their contents under `/module_update`. Does
   not touch arbitrary /system files. On an empty `/system/module_update`
   (the factory default for DAYU200 standard build), it's a no-op.
3. **`sys_installer_sa`** — `ondemand: true`. Just an IPC server; only
   acts when an installer caller pushes packages. Idle by default.
4. **`check_module_update`** — `start-mode: condition`, `once: 1`,
   started by `late-fs` job. Runs `/system/bin/check_module_update_init`
   which calls `ModuleUpdate::CheckModuleUpdate()` (scans the same
   `/system/module_update` dir as above). Empty dir → no-op.

None of these constitute a "wipe /system to factory baseline" on each
boot in stock 7.0.0.18 source — and HBC has not patched any of them
out, because they don't need to.

## What changed on the V7 ROM agent 105 hit

Agent 105's symptom is `/system/android` gone post-reboot AND
`.orig_<TS>` device-side backups gone — i.e. things HBC's own SOP
relies on as durable. This is inconsistent with stock 7.0.0.18 init
behavior. The V7 ROM on the agent-105 device must therefore have one
of (a) verified-boot/dm-verity rejecting modified `/system` and
falling back to a known-good copy, (b) an OEM `module_update`
preinstall HMP package that actually overlays /system, (c) a different
ROM build (vendor patch) whose pre-init phase formats /system
filesystem on each boot, or (d) eng_system vs system partition
selection mismatch where the running rootfs differs from the one
written to.

HBC's bench appears not to hit this path. Plausibility ordering:

1. **Different physical ROM/build on HBC's bench device.** HBC has not
   re-flashed since the date their procedure first worked; ours has.
   The baseline source string `7.0.0.18 weekly_20260302` matches but
   the on-device boot loader, dm-verity key set, and `/system/module_update`
   preinstall HMP contents may differ.
2. **HBC's device boots from `eng_system` partition** instead of
   `system`. `eng_system` is an engineering rootfs with verification
   disabled and is what `hdc target boot eng_system` (or fastboot
   `--set-active eng`) selects. We haven't verified which one our V7
   board boots from.
3. **HBC's device has DAYU200 factory image where `/system/module_update`
   is empty**, while V7 ROM ships preinstall HMP packages that overlay
   /system contents (including possibly overwriting `/system/bin/appspawn-x`
   on each `CheckModuleUpdate` pass).

## Recommendation

1. **Do not look for a disable patch — there isn't one.** Stop scanning
   the patches/ tree for a magic switch.
2. **Probe the V7 ROM device** for the actual restoration mechanism.
   Highest-value probes (next operator slot, board hard-power-cycled):
   - `ls -la /system/module_update/` — is there a preinstall HMP package
     that bind-mounts on boot? Check its `module.json` for what paths
     it owns.
   - `cat /proc/cmdline | tr ' ' '\n' | grep -iE 'verity|veritymode|system|boot|eng'`
     — is `dm-verity` on? Are we on `system` or `eng_system`?
   - `mount | grep -E 'system|verity|loop'` — what's actually mounted
     read-only with what backing.
   - `find / -name 'module_update_sa.cfg' -o -name 'check_module_update.cfg' 2>/dev/null`
     then `cat` to compare against HBC's source.
   - `hilog -t INIT | grep -i module_update` from a fresh boot — does
     `CheckModuleUpdate` actually install something or no-op?
   - `bootctl get-current-slot` (if exposed) or `getparam | grep -i slot`
     — A/B slot vs eng_system slot.
3. **If `/system/module_update/*.hmp` exists and overlays our files**,
   the fix is to `rm` those HMPs before deploying (or rename) — no
   source patch needed, just a SOP prepend.
4. **If `dm-verity` is enabled on the V7 ROM**, deploy will never
   persist without disabling verity (`avbctl disable-verification` or
   `disable-verity` if exposed) followed by a controlled reboot —
   again, no source patch needed.
5. **If HBC's bench boots from `eng_system`**, our SOP needs an
   `hdc target boot eng_system` equivalent before stage 1.

In all cases the action is at the **device/operator** level, not in
`ohos_patches/`. Reorienting V3-W2 retry around device probing, not
patch hunting.
