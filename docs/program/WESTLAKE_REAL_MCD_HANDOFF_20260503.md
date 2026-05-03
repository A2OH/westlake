## Westlake Real McD Handoff (2026-05-03 PT)

Repo: `$HOME/android-to-openharmony-migration`

Remote: `origin=https://github.com/A2OH/westlake.git`

Branch: `main`

Last pushed commit (baseline for next agent): `71234401` (`origin/main`)

Phone + ADB:

- device serial: `cfb7c9e3`
- Windows ADB path (preferred):
  `/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe`
- ADB server: `localhost:5037`
- Westlake host package: `com.westlake.host`

Notes:

- `rg` is not installed in this environment; use `grep`/`find`.
- The accepted McD proof is *not* a mock app. It is stock `com.mcdonalds.app`
  executing inside Westlake guest `dalvikvm`.

## Current Success Proof (Accepted)

Artifact:

`artifacts/real-mcd/20260502_175722_mcd_48h_network_pf621_bounded_final/`

Result:

- `gate_status=PASS`
- `PASS proof_real_mcd_guest_dalvikvm package=com.mcdonalds.app`
- `PASS westlake_subprocess_purity ... direct_mcd_processes=0` (no phone ART McD process)
- live network 2xx via Westlake HTTP bridge/proxy
- real dashboard section XML + real item-row XML inflation
- dashboard -> PDP -> Add-to-bag -> cart count `1` (app-visible)

Re-check the artifact quickly:

```bash
cd $HOME/android-to-openharmony-migration
./scripts/check-real-mcd-proof.sh artifacts/real-mcd/20260502_175722_mcd_48h_network_pf621_bounded_final
```

## How The Proof Runs (Call Path)

1. Phone launches Westlake host (`com.westlake.host`).
2. Host spawns Westlake guest runtime (`dalvikvm`) as a subprocess.
3. Guest loads stock APK+DEX from `/data/local/tmp/westlake` and executes
   `com.westlake.engine.WestlakeLauncher` as entrypoint.
4. Westlake shims provide Android-shaped framework APIs/resources/input.
5. Network calls from the guest are routed through the host bridge (file-based
   V2 request lines) and (for phone proof) optionally through a localhost proxy
   via `adb reverse`.
6. The proof harness interacts with the UI through marker-driven taps and
   captures:
   - streamed logcat (`logcat-stream.txt`)
   - full logcat dump (`logcat-dump.txt`)
   - screenshot (`screen.png`)
   - process list and runtime hashes.

Key code locations:

- guest launcher: `shim/java/com/westlake/engine/WestlakeLauncher.java`
- host HTTP bridge implementation: `westlake-host-gradle/app/src/main/java/com/westlake/host/WestlakeVM.kt`
- phone runtime sync: `scripts/sync-westlake-phone-runtime.sh`
- phone proof harness: `scripts/run-real-mcd-phone-gate.sh`
- proof checker: `scripts/check-real-mcd-proof.sh`

## Reproduce A Green Phone Gate

This reproduces a current bounded proof run (same shape as the accepted one).

```bash
cd $HOME/android-to-openharmony-migration

env ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
  ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
  ./scripts/sync-westlake-phone-runtime.sh

env ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
  ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
  WESTLAKE_HTTP_PROXY_BASE=http://127.0.0.1:18080 \
  WESTLAKE_START_HTTP_PROXY=1 \
  WESTLAKE_GATE_INTERACT=1 \
  WESTLAKE_GATE_SLEEP=470 WESTLAKE_DASH_WAIT=360 WESTLAKE_PDP_WAIT=240 \
  ./scripts/run-real-mcd-phone-gate.sh mcd_repro_bounded_pf621
```

## Active Workstreams (What Is Still Not “Full McD App”)

Issue tracker (opened via `gh` CLI):

- PF-627 https://github.com/A2OH/westlake/issues/593 (dashboard rows + RecyclerView parity expansion)
- PF-628 https://github.com/A2OH/westlake/issues/594 (remove `CartInfo` propagation bridge)
- PF-629 https://github.com/A2OH/westlake/issues/595 (deeper stock routes)
- PF-630 https://github.com/A2OH/westlake/issues/596 (Realm close/finalizer long-soak stability)
- PF-632 https://github.com/A2OH/westlake/issues/597 (new Android framework/API misses)

Supervisor docs (must stay updated):

- `docs/program/WESTLAKE_REAL_MCD_48H_FULL_APP_RALLY_20260502.md`
- `docs/program/WESTLAKE_PLATFORM_FIRST_ISSUES.md`
- `docs/program/WESTLAKE_SOUTHBOUND_API.md`

### PF-628: Remove CartInfo Bridge (No McD-specific propagation)

Current accepted proof still uses:

- `MCD_PDP_CARTINFO_SET_BRIDGE ... applied=true`

Why it matters:

- It proves the stock cart exists, but it is not the final architecture.
- Success criteria for PF-628 is: `CartViewModel` updates naturally from
  stock lifecycle/LiveData/model propagation with zero bridge markers.

Recent attempt (no-bridge) result:

- artifact: `artifacts/real-mcd/20260502_183431_mcd_48h_pf628_lifecycle_no_bridge_long/`
- advanced past lifecycle/PDP readiness with network green and `bridge=0`,
  then hit `SIGBUS` at `BasketAPIHandler.Q` before positive cart readback.

### PF-630: Long-Soak SIGBUS (Realm close/finalizer)

Observed failure:

- artifact: `artifacts/real-mcd/20260502_174634_mcd_48h_network_pf621_full_app_final/`
- later crash near `OsSharedRealm.nativeCloseSharedRealm` (`SIGBUS`)

Candidate runtime fix exists but is NOT yet integrated into this repo:

- runtime repo: `$HOME/art-latest`
- patched source: `patches/runtime/interpreter/interpreter_common.cc`
- built binary: `$HOME/art-latest/build-ohos-arm64/bin/dalvikvm`
- candidate hash (from worker): `20acd7b1c18493cd9a52c0cf8f67243c32459e34fc0faa2be1295cf0f43d2999`

To test PF-630 candidate runtime on phone:

```bash
cd $HOME/android-to-openharmony-migration

env ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
  ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
  DALVIKVM_SRC=$HOME/art-latest/build-ohos-arm64/bin/dalvikvm \
  ./scripts/sync-westlake-phone-runtime.sh

env ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
  ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
  WESTLAKE_HTTP_PROXY_BASE=http://127.0.0.1:18080 \
  WESTLAKE_START_HTTP_PROXY=1 \
  WESTLAKE_GATE_INTERACT=1 \
  WESTLAKE_GATE_SLEEP=600 WESTLAKE_DASH_WAIT=360 WESTLAKE_PDP_WAIT=240 \
  ./scripts/run-real-mcd-phone-gate.sh mcd_pf630_realm_close_soak
```

Acceptance for PF-630:

- 10-minute post-cart soak, no fatal/SIGBUS, guest VM pid still present.

### PF-632: New Android Framework/API Misses

First concrete miss observed during stock PDP click path:

- `android.app.AlertDialog$Builder(Context,int)`

Status:

- The shim includes `AlertDialog.Builder(Context,int)` in `shim/java/android/app/AlertDialog.java`.
- If future route expansion finds new missing APIs, add them to PF-632 and
  record southbound implications in `WESTLAKE_SOUTHBOUND_API.md`.

### PF-629: Deeper Stock Routes

The accepted artifact stops at PDP/cart-state proof; it does not yet prove a
native cart screen/bottom-nav route.

The proof harness supports PDP tap sequences:

- `WESTLAKE_GATE_PDP_PRE_ADD_TAPS=quantity_plus`
- `WESTLAKE_GATE_PDP_POST_ADD_TAPS=quantity_minus,customize`

Suggested next proof (PF-629): quantity-plus then add:

```bash
cd $HOME/android-to-openharmony-migration
env ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe \
  ADB_HOST=localhost ADB_PORT=5037 ADB_SERIAL=cfb7c9e3 \
  WESTLAKE_HTTP_PROXY_BASE=http://127.0.0.1:18080 \
  WESTLAKE_START_HTTP_PROXY=1 \
  WESTLAKE_GATE_INTERACT=1 \
  WESTLAKE_GATE_PDP_PRE_ADD_TAPS=quantity_plus \
  WESTLAKE_GATE_SLEEP=150 WESTLAKE_DASH_WAIT=260 WESTLAKE_PDP_WAIT=180 \
  ./scripts/run-real-mcd-phone-gate.sh mcd_pf629_quantity_plus_cart2_gate
```

Expected markers:

- `MCD_ORDER_PDP_STOCK_ACTION control=quantity_plus ... invoked=true`
- cart gate/readback shows quantity/count `2` (or a precise stock rejection)

## Guardrails For Next Agent

- Do not weaken `scripts/check-real-mcd-proof.sh` acceptance requirements.
- Keep McD-specific bridges labeled and removable; prefer generic shims.
- Keep `unsafe` flags off for accepted proofs; isolate unsafe probes to
  explicitly named artifacts.
- Keep Android-phone proof and OHOS portability tied together by updating
  `docs/program/WESTLAKE_SOUTHBOUND_API.md` whenever a new host dependency is
  added.
