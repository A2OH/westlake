# Toutiao native runtime sources

This directory is the source-of-truth for the small arm64 OpenHarmony boundary
libraries used by the Toutiao validation harness. No built `.so`, extracted APK
library, or device-specific binary is committed.

The inventory is intentionally explicit:

| Output | Sources |
| --- | --- |
| `libbionic_abi_shim.so` | `bionic_abi_shim.c` |
| `libbionic_stdio_shim.so` | `bionic_stdio_shim.c` |
| `liblog_shim.so` | `liblog_shim.c` |
| `libandroid_native_network_compat.so` | `android_native_network_compat.c`, `.map` |
| `libwebview_bionic_shim.so` | `webview_bionic_shim.c`, `.map`, `webview_setjmp_arm64.S` |
| `libwl636.so` | `wl_natives636.cpp`, `wl_dump636.cpp` |
| `libwestlake_exit_trace.so` | `westlake_exit_trace.c` |
| `libwestlake_native_bootstrap_anchor.so` | `westlake_native_bootstrap_anchor.c` |
| `libwestlake_player_anchor.so` | `westlake_player_anchor.c`, native-network source and map |
| `fatal_signal_tracer` | `tools/fatal_signal_tracer.c` |

Run:

```bash
scripts/toutiao/native/build.sh
scripts/toutiao/deploy_asx.sh scripts/toutiao/native/out/asx
```

The build defaults match the validated workstation. Override `OH_ROOT`,
`OHOS_NATIVE_SDK`, `BRIDGE_ROOT`, `LLVM`, `SYSROOT`, or `JNI_INCLUDE` when the
trees live elsewhere. The generated stage is:

```text
asx/
├── fatal_signal_tracer
├── libandroid_native_network_compat.so
├── libbionic_abi_shim.so
├── liblog_shim.so
├── libwestlake_exit_trace.so
├── libwl636.so
├── nsprobe/
│   ├── libbionic_stdio_shim.so
│   ├── libopensles_android_compat.so
│   ├── libwestlake_native_bootstrap_anchor.so
│   └── libwestlake_player_anchor.so
└── webview-t-lib/
    └── libwebview_bionic_shim.so
```

`libkeva.so` and `libttmplayer.so` are APK-owned libraries and are not
redistributable source inputs. The build creates link-only DSOs to record their
SONAMEs in the anchors' ordered `DT_NEEDED` lists; those temporary DSOs are not
staged. The real libraries must already be available to the device loader.

`libopensles_android_compat` is included because it is a required build and
runtime dependency of `libwestlake_player_anchor`, even though it was not in
the original missing-file inventory.

`liblog_shim.c` preserves the export surface and inert stderr-forwarding
behavior measured from the deployed library; the original scratch source was
not retained. All other sources are copied from the exact bridge/Toutiao source
files that produced the deployed fixes.

`build.sh` keeps unstripped artifacts under `out/unstripped`, strips the staged
copies, verifies required exports and anchor dependencies, and writes
`asx/MANIFEST.sha256`.

The operational handoff remains at
[`docs/toutiao/TOUTIAO-HANDOFF.md`](../../../docs/toutiao/TOUTIAO-HANDOFF.md).
