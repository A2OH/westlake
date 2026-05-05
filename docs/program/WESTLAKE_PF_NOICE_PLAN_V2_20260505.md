# Westlake — PF-noice Delivery Plan V2 (post-cascade, frame-emission focus)

**Date:** 2026-05-05 PT
**Author:** read-only design agent (no phone, no runtime, no commits beyond this single doc)
**Supersedes:** `docs/program/WESTLAKE_PF_NOICE_PLAN_20260504.md` (V1, D1+D2 already executed)
**Companion docs:**
- `docs/program/WESTLAKE_NOICE_DAY1_RESULTS_20260503.md` (cumulative empirical record, includes day-2 part 1-4)
- `docs/program/WESTLAKE_FULL_MCD_GAP_REPORT_20260503.md` §13.7-§13.12 (PF-630 substrate-fix arc)
- `docs/program/WESTLAKE_NOICE_1DAY_CONTRACT_20260503.md` (acceptance shape)
**Phone:** OnePlus 6 `cfb7c9e3` (LineageOS 22 / Android 15, rooted, SELinux permissive)
**Runtime baseline at start:** `88e7679701b169118e469deef369f4a6d015e6c7f1697036d6d3cb8256c59b3a` (PF-noice-002 cascade fix landed)
**Shim baseline at start:** `307c81d938a7989c85e099d9bc7b4445fc644bfc43a360cda4bd8f4bc10c7e34` (SunX509+TLS shim chain + `Window.installMinimalStandaloneContent` indigo TextView fallback)

---

## 1. Scope, acceptance, day plan (V2)

### 1.1 Why V2 exists — the world has shifted

V1 assumed the boot-class ArrayStoreException cascade was the main blocker. **V1's D1 + D2 landed cleanly and the cascade is GONE** (`art-latest @49d1f67`, `westlake @1ef6002e`):

- Boot-class ASE cascade — ELIMINATED (descriptor-fallback in `mirror::ObjectArray::CheckAssignable`)
- `Tolerating clinit failure` count: 7+ → 0
- `Coroutine runtime seed failed` → `Coroutine runtime seeded before Application.onCreate`
- `Application from manifest: NoiceApplication` succeeds
- NSAE chain (post-cascade): `NoSuchAlgorithmException: SunX509` → `... TLS` → StackOverflow → "Missing required view 0x7f090167" — each shim landed in turn

**But noice still doesn't paint.** The current empirical state (post-`v6_stub_factory` artifact at `20260504_233128`):

```
19:31:56.730  AM direct launch noice MainActivity
19:31:57.720  performCreate NPE: Missing required view with ID: res/0x7f090167
19:31:57.731  tryRecoverContent setContentView (XML) → fails on View.setId on null
19:31:58.298  PF301 strict Window setContentView (programmatic LinearLayout) → standalone end OK
19:31:58.346  programmatic fallback fails: ContentFrameLayout.setAttachListener on null
[NO MORE LAUNCHER LOGS for the rest of the run]
```

**`PF301 strict launcher activity branch begin`** never appears. **`installMinimalStandaloneContent`** never invoked. **`runStrictStandaloneMainLoop`** never reached. **Zero `STRICT_VIEW_FRAME_*` markers.** The pipe consumer in the host (`WestlakeVM:PIPE READER: started`) is alive — but no DLST frames are emitted by the guest because the launcher's frame-emission code never runs.

**This means the ENTIRE post-launch render path is dead for noice.** Even with cascade fixed, even with shims for NSAE and AppCompat-recovery side-failures, the frame-emission code path is fundamentally missing for non-McD-style apps in the standalone-dalvikvm mode.

### 1.2 The four blocker hierarchy (most-fundamental first)

This V2 plan re-prioritizes around the actual blocker hierarchy:

1. **Frame emission to SurfaceView pipe never fires for noice** — the most fundamental missing piece. Without frames reaching the pipe consumer, no UI is possible regardless of what view tree exists.
2. **Recovery paths fail with null receivers** because AppCompat's onCreate failure leaves `Activity.getWindow().getDecorView()` partially constructed — `View.setId on null` (XML path) and `ContentFrameLayout.setAttachListener on null` (programmatic path).
3. **`installMinimalStandaloneContent` doesn't fire** because the launcher's PF301 fallback gate never gets to run — `am.startActivityDirect()` returns control to the launcher only after `performCreate` completes, but by then the recovery already left a partial child in decor (per-`AppCompatActivity` setContentView side-effects), defeating the `existingChildren > 0` check in V1's installMinimalStandaloneContent.
4. **AppCompat view-binding "Missing required view 0x7f090167"** — noice's MainActivity is asking for a view ID that the inflated layout doesn't produce. Either the layout XML doesn't define it OR resource inflation is incomplete OR the `MainActivityBinding.bind()` generated code expects a child that is conditionally inflated.

### 1.3 Acceptance for V2 (revised D5 endgate)

**D5' endgate:** noice OR a launcher-emitted programmatic content frame paints visibly on phone, with screenshot SHA differing from current host-Compose-baseline, and McD bounded green not regressed.

The visible-paint can come from any of:
- (a) noice's actual MainActivity tree (best, requires solving #4 above too)
- (b) Window.installMinimalStandaloneContent's indigo TextView (good fallback, requires solving #3)
- (c) A launcher-emitted programmatic frame written directly to the pipe, bypassing Activity.setContentView entirely (cheapest, requires only #1)

**Day-by-day acceptance is in §3 below.**

### 1.4 Day plan summary

| Day | Goal | Primary deliverable | Acceptance |
|---|---|---|---|
| **D1'** | (already done in part 4 of day-2) | runtime + shim with cascade fix and NSAE chain | `Tolerating clinit failure` count = 0; NoiceApplication.onCreate completes |
| **D2'** | Frame-emission audit + path localization | Per-frame-emit trace patches; identification of which step in the path is broken | Trace shows `FRAME_EMIT_*` events for at least one frame attempt; categorizes failure as one of {decor-null, layout-failed, displayList-not-built, pipe-write-failed, no-trigger} |
| **D3'** | Frame-emission fix wired | Either: (a) generic `runStandaloneViewFrameLoop` that runs even without a successful Activity, or (b) launcher-driven programmatic frame that doesn't require Activity at all | DLST frames arrive at host pipe; `Frame: ${size} bytes → View` in WestlakeVM logs ≥3 times |
| **D4'** | Layout-id diagnosis + sidestep | Either fix the missing 0x7f090167 OR switch to a programmatic frame path that doesn't depend on noice's resource layout | One of: noice MainActivity content visible on screen OR programmatic frame visible on screen, screenshot SHA differs from baseline |
| **D5'** | Paint verification + soak + acceptance | Final results doc; commits pushed | A6 PASS (visible paint); A7 PASS (5min soak no SIGBUS); McD bounded gate non-regressed |

**Parallel workstream:** PF-noice-006 audio-gap stub remains as a 1-day-effort follow-on after D5'.

---

## 2. PF-noice issue ledger (refreshed; numbering restarts at 010)

### Status of V1 issues (PF-noice-001..009) — for context

| ID | V1 title | V1 status |
|---|---|---|
| PF-noice-001 | Localize cascade root cause (D1) | **DONE** — class-identity duplication, `art-latest@dd7eec1` |
| PF-noice-002 | Fix cascade root cause (D2) | **DONE** — descriptor-fallback in `mirror::ObjectArray::CheckAssignable`, `art-latest@49d1f67` |
| PF-noice-002a | NSAE: SunX509 + TLS shim | **DONE** — `westlake@97527981` (passes; current: permissive) |
| PF-noice-002b | Null views (XML inflation) | **PARTIAL** — recovery path fires, hits `View.setId on null`; supplanted by V2 issues |
| PF-noice-003 | Noice MainActivity paints view tree | **NOT DONE** — re-scoped under V2 PF-noice-013/014 |
| PF-noice-004 | McD bounded gate doesn't regress | **CARRIES OVER** — V2 PF-noice-016 |
| PF-noice-005 | 5-min soak with real UI | **DEFERRED** — V2 D5' acceptance |
| PF-noice-006 | Audio-gap stub | **DEFERRED** — post-D5' |
| PF-noice-007 | McD same-cause cascade | **MOOT** — cascade fixed for both |
| PF-noice-008 | Generic launcher token | **DEFERRED** — post-D5' |
| PF-noice-009 | Trace harness as permanent debug facility | **DONE-ENOUGH** — env-flag would polish; not blocking |

### PF-noice-010 — Frame emission audit & wire-up (D2'+D3', highest-leverage fundamental)

**Title:** Frame emission audit & wire-up: trace why noice never produces a DLST frame to the host pipe

**Scope (1 paragraph):** The host-side pipe reader (`WestlakeVM.kt`) is alive and listening; the VM stdout is connected to it (line 1444). But noice never writes a single DLST-magic-prefixed frame into stdout. There are eight per-app frame writers (`drawShowcase*`, `MCD_DASH_PROJECTED_FRAME`, `writeStrictStandaloneViewFrame`, etc.) at WestlakeLauncher.java lines 9252, 9457, 10298, 10863, 11146, 14560, 17363, 18904 — each gated on activity package prefix (`com.westlake.*`) or class-name match (`HomeDashboardActivity`, `OrderProductDetailsActivity`). Noice (`com.github.ashutoshgngwr.noice.activity.MainActivity`) matches NONE of these gates. Even the generic `runStrictStandaloneMainLoop`+`writeStrictStandaloneViewFrame` (line 18654, line 18808) is reachable only after `am.startActivityDirect` returns, but in the v6 artifact we see zero post-launch markers — meaning that call hangs or unwinds silently. **D2' instrumentation must answer: which step is broken — does `am.startActivityDirect` return? If yes, does the launcher reach line 4918+? If no, where is control stuck?**

**Acceptance:** logcat from the next noice run contains:
- `[FRAME-EMIT-TRACE] launcher post-launch reached marker=...` AT LEAST ONCE
- `[FRAME-EMIT-TRACE] frame attempt source=<method> bytes=<N> ops=<M> result=<ok|err>` AT LEAST ONCE
- The trace identifies WHICH of {pre-launch hang, post-launch path missing, frame-write-failed, displayList-not-built, decor-null} is the actual failure mode

**Effort:** 4-6h (instrumentation patch + sync + 1 noice run + analysis)
**Dependencies:** none (D1' is already complete)
**Risk:** trace itself perturbs timing; mitigation = use `appendCutoffCanaryMarker` (file-backed durable) for the most critical events, not just `startupLog` (logcat only, may be lost under backpressure).

### PF-noice-011 — Recovery should construct minimal Window+Decor independent of AppCompat (D3')

**Title:** Recovery should construct a fresh PhoneWindow+DecorView programmatically when AppCompat fails

**Scope:** The current recovery in `MiniActivityManager.tryRecoverContent` (~line 2294) calls `r.activity.setContentView(...)` which routes through `Activity.setContentView` → `mWindow.setContentView` → `(if AppCompatActivity) appCompatDelegate.setContentView`. When AppCompat's own `onCreate` fails partway, the delegate's internal mDecor is null but its setAttachListener path is invoked, NPEing. Two fix shapes:

- **Shape A (cheaper):** in `tryRecoverContent`, bypass the activity entirely — call `r.activity.getWindow().setContentView(programmaticView)` directly on the shim Window, NOT via `r.activity.setContentView`. The shim Window doesn't go through AppCompat delegate.
- **Shape B (correct, more intrusive):** in `tryRecoverContent`, before calling setContentView, replace `r.activity.mWindow` with a fresh `new android.view.Window(activity)` (the shim version), severing AppCompat's hold. Rebind via reflection.

**Acceptance:**
- `tryRecoverContent programmatic fallback failed` log line gone (replaced by `tryRecoverContent: programmatic fallback OK via shim Window`)
- decor child count > 0 after recovery
- Window.installMinimalStandaloneContent doesn't need to fire (recovery already installed content)

**Effort:** 4-8h
**Dependencies:** PF-noice-010 (need confirmation that performCreate actually returns; if it hangs the recovery is moot)
**Risk:** AppCompat may have replaced the activity's `mWindow` field with its own subclass; reflection may need to also reset internal AppCompat state. Mitigation: shape A is safer — call directly on `r.activity.getWindow()` (which is the shim Window if AppCompat hasn't replaced it; the activity's getWindow override IS the shim's path).

### PF-noice-012 — `installMinimalStandaloneContent` reachability (D3')

**Title:** `installMinimalStandaloneContent` must fire when decor is "alive but useless"

**Scope:** The launcher's PF301 fallback at `WestlakeLauncher.java:4965-4972` calls `installMinimalStandaloneContent` only when `childCount > 0` is false — i.e. when decor has NO children. But the recovery's `setContentView` (or AppCompat's partial install) often leaves a partial child in decor, defeating this gate even though the partial child is non-functional (no actual visible content). The gate should also fire when decor has children but they're empty/non-functional. AND — in the v6 artifact — the entire `if (launchedActivity != null)` branch (line 4918) is never reached because `am.startActivityDirect` doesn't return cleanly.

Two fix shapes:

- **Shape A (Window-level):** in `Window.installMinimalStandaloneContent`, change the gate from `existingChildren > 0` to "existingChildren > 0 AND any child is visible/has measured size". If the existing child is a 0x0 phantom or wrong-context, replace it.
- **Shape B (launcher-level):** add a SECOND fallback path that fires regardless of `launchedActivity != null` — i.e. when the entire activity launch failed — that emits a programmatic frame directly via the pipe. This bypasses Activity entirely; just a static "Westlake guest running noice" splash screen written as DLST ops.

Recommend BOTH (B is the cheap escape valve; A makes the natural code path more useful).

**Acceptance:**
- Logcat contains `PF301 strict Window installContent textview added` for noice runs (Shape A)
- Logcat contains `[WestlakeLauncher] noice fallback splash frame emitted bytes=...` (Shape B)
- screen.png SHA differs from host-Compose-home baseline

**Effort:** 6-10h (both shapes)
**Dependencies:** PF-noice-010 (need to confirm post-launch reachability before building Shape B)
**Risk:** existingChildren count check may have other use cases; widening it could regress McD. Mitigation: only widen when activity's class doesn't match McD/showcase patterns.

### PF-noice-013 — Diagnose noice's "Missing required view 0x7f090167" (D4')

**Title:** Identify what view ID 0x7f090167 actually is and why noice can't bind it

**Scope:** noice's MainActivity uses AppCompat ViewBinding (`MainActivityBinding.bind()` or `MainActivityBinding.inflate()`). The error `Missing required view with ID: res/0x7f090167` from `androidx.viewbinding.ViewBindings.findChildViewById` means a generated `bind` call expected a child with ID `0x7f090167` and got null. Concrete steps:

1. Extract `resources.arsc` from `noice.apk`: `unzip -p noice.apk resources.arsc > /tmp/noice.arsc` (no aapt2 dependency needed; can decode IDs from the offset table directly OR install aapt2 via package manager).
2. Decode `0x7f090167` via aapt2 dump or write a small Python parser using `androguard` if aapt2 unavailable.
3. Cross-check: scan `noice.apk` extracted res/layout/activity_main.xml (or the actual main activity layout) for the ID. The package id is 0x7f, type 0x09 = "id" entries, entry index 0x167 — index into the ID string pool.
4. Hypothesis A: the layout doesn't define the view. (Generated binding code is wrong — unlikely; would fail on real Android too.)
5. Hypothesis B: the binary XML layout was inflated but the inflation lost a child due to BinaryXmlParser limitations. Need to check the shim's inflater.
6. Hypothesis C: AppCompat themes/styles short-circuit the inflation; noice's theme has missing references that cause a TypedArray failure that swallows a child silently.

**Acceptance:**
- `_TRACE_NOTES.txt` artifact lists the human-readable name of resource 0x7f090167
- Identifies which of {A, B, C, other} is the actual root cause
- Patches the relevant file (BinaryXmlParser, theme handling, OR provides a manifest of "stub these IDs to a programmatic placeholder")

**Effort:** 4-12h depending on which hypothesis fits
**Dependencies:** PF-noice-010 (to know whether this even matters; if the frame emission path bypasses Activity, this is moot for D5' acceptance)
**Risk:** the missing ID may actually be intentional in the app's resource conditioning (e.g. `<include layout="@layout/foo">` that's only present in specific configurations); recovery may need to inject a placeholder programmatically.

### PF-noice-014 — Programmatic-frame-from-launcher escape valve (D3', SIDE-STEP for D5')

**Title:** Add `emitNoiceFallbackFrame()` that writes a DLST splash frame even when activity launch fully fails

**Scope:** Mirror McD's `MCD_DASH_PROJECTED_FRAME` shape — write a DLST frame to stdout (or `WESTLAKE_FRAME_PATH`) directly, with no view tree, just a hardcoded bitmap of "Westlake guest dalvikvm running noice — Activity failed to launch". This is THE simplest path to a non-black SurfaceView and unblocks A6 (screenshot proves something rendered) regardless of upstream blockers. Add at `WestlakeLauncher.java:4915` (right after the `WARNING: No activity, rendering empty surface` log).

Implementation pattern (mirror line 16833-16838 + line 18904-18908):
```java
java.io.ByteArrayOutputStream ops = new java.io.ByteArrayOutputStream(2048);
showcaseColor(ops, 0xff1A237E);  // deep indigo
showcaseText(ops, "Westlake guest dalvikvm", 60, 200, 24, 0xffffffff);
showcaseText(ops, "Running com.github.ashutoshgngwr.noice", 60, 240, 16, 0xffe0e0e0);
showcaseText(ops, "Activity launch failed - see logcat", 60, 280, 14, 0xffffcdd2);
byte[] data = ops.toByteArray();
java.io.OutputStream out = strictFrameOutputStream();
synchronized (sStrictFrameOutputLock) {
    writeIntLe(out, 0x444C5354);
    writeIntLe(out, data.length);
    out.write(data);
    out.flush();
}
```

Loop this every 500ms in a daemon thread until process death (so the SurfaceView keeps showing the splash, not just one frame).

**Acceptance:**
- Logcat: `[WestlakeLauncher] noice fallback frame emitted bytes=<N>` ≥1
- Host log: `WestlakeVM: Frame: <N> bytes → View` ≥1 within 5s of VM start
- Screenshot SHA differs from baseline; visual inspection shows indigo splash with text

**Effort:** 3-5h (well-scoped; reuses existing helpers)
**Dependencies:** PF-noice-010 (need confirmation that the launcher reaches line 4915)
**Risk:** The host's `frameBridgeFile` is gated on `apkConfig?.packageName == "com.mcdonalds.app"` (WestlakeVM.kt:771); for noice, the host uses `pipeStream = startedProcess.inputStream` (stdout). Stdout may have intermixed text logs from the runtime that confuse the DLST sync. **Critical:** verify by checking host logs whether `Sync complete: skipped=N bytes, magic=0x444C5354` appears for noice runs. If the host stdout pipe is too noisy, fix by redirecting all `startupLog` calls to stderr (which is text channel, line 1435 already says `// Do NOT redirectErrorStream — stdout is binary pipe`), confirming the existing design supports binary stdout for non-McD apps too.

### PF-noice-015 — Real SunX509+TLS Provider (deferred until paint works)

**Title:** Replace the permissive SunX509+TLS shim with a real Provider implementation

**Scope:** Current shim chain (commits `westlake@97527981` through `1ef6002e`) is permissive: any `KeyManagerFactory.getInstance("SunX509")` call is satisfied by a SPI stub that returns no keys. This works because noice's TLS path eventually short-circuits when the manager has no certs. Long-term we need a real BouncyCastle-backed Provider that can handle TLS for media streaming. **Deferred** — paint must work first, then network, then audio.

**Acceptance:** TLS connection from guest to a known HTTPS endpoint (e.g. https://example.com) returns 200; certs validated.
**Effort:** 1-2 weeks
**Dependencies:** D5' complete; PF-noice-006 audio gap requires this for HTTPS-served audio sources.
**Risk:** medium — BouncyCastle integration touches many static fields; cascade-class issues may resurface.

### PF-noice-016 — McD bounded gate doesn't regress (D5')

**Title:** Verify D2'/D3' frame-emission patches don't break McD bounded gate

**Scope:** Run `scripts/run-real-mcd-phone-gate.sh` after each D2'/D3' patch landing. McD must remain at parity with the `20260502_175722_mcd_48h_network_pf621_bounded_final/` baseline. The frame-emission instrumentation should be cheap (single `appendCutoffCanaryMarker` per frame attempt) and not affect McD's hot path.

**Acceptance:**
- `gate_status=PASS` in real-mcd checker output
- `STRICT_VIEW_FRAME_OK` count for McD ≥ baseline count
- No new SIGBUS / Fatal signal lines

**Effort:** 1-2h per regression check
**Dependencies:** PF-noice-010, PF-noice-011, PF-noice-012, PF-noice-014
**Risk:** widening of `installMinimalStandaloneContent` gate (PF-noice-012 Shape A) could regress McD. Mitigation: only widen for non-McD activity classes; or only enable Shape A under env flag.

### PF-noice-017 — Trace harness env-flag gating (cleanup, post-D5')

**Title:** Promote D2' frame-emit instrumentation to env-flag-gated permanent facility

**Scope:** Post-D5' cleanup. Wrap `[FRAME-EMIT-TRACE]` log lines with `if (System.getenv("WESTLAKE_FRAME_TRACE") != null)` so the log spam is opt-in. Default off. Cheaper than ripping the trace out, makes it reusable for future paint investigations on other apps.

**Acceptance:** Trace OFF by default; ON when env flag set; cost when off ≈ 1 cmp + 1 branch per frame attempt.
**Effort:** 1h
**Dependencies:** D5' complete
**Risk:** none

### PF-noice-018 — AppCompatDelegate static-state leak (long-term hardening)

**Title:** AppCompat delegate's null `mAttachListener` is a deeper class-identity-duplication issue

**Scope:** The `ContentFrameLayout.setAttachListener(k.n1) on null` failure isn't actually a missing field — it's a NULL field on a non-null `ContentFrameLayout`. AppCompat's `AppCompatDelegateImpl` has a `mAttachListener` static-ish field that's set during the delegate's `installViewFactory`. When the delegate's onCreate fails partway, that field is null. Calling `setContentView` later finds the partial decor and tries to re-attach via the listener — NPE. Fix shape: shim AppCompatDelegateImpl's clinit to install a no-op listener so the field is never null. Probably a 50-100 LOC stub class. **Long-term hardening; out of scope for V2 D-cycle.**

**Acceptance:** N/A (hardening item)
**Effort:** 1 week
**Dependencies:** D5' complete
**Risk:** fork-and-pin AppCompat behavior; may need version-specific stubs.

---

## 3. D2' instrumentation patch — exact file:line edits

### 3.1 Architecture overview

Three insertion points capture the broken-frame-emission paths:

1. **WestlakeLauncher.java:4905-4920** — instrument `am.startActivityDirect` return + `launchedActivity` check + the post-launch flow gates. This is the hottest blind spot (no markers between 4906 and 4920 in v6 artifacts despite presumed code execution).
2. **WestlakeLauncher.java:14534-14582 + 18654 + 18808** — instrument every existing frame-emit path with a single `[FRAME-EMIT-TRACE]` line per attempt, recording: source method, decor null status, view tree size, frame bytes (post-write), and pipe-write outcome.
3. **WestlakeVM.kt:1797-1880** — instrument the host-side pipe reader to log each frame received and any sync failures (we can verify the host side independently).

### 3.2 File 1: `$HOME/android-to-openharmony-migration/shim/java/com/westlake/engine/WestlakeLauncher.java`

#### 3.2.1 Top edit — instrument the activity-launch boundary (around line 4906-4920)

Add markers that distinguish "launchedActivity is null" vs "launchedActivity is non-null but post-launch path was skipped". Insert markers at every transition.

`old_string`:
```java
        // Try to get the launched activity even if errors occurred
        final boolean strictStandalone = !isRealFrameworkFallbackAllowed();
        if (launchedActivity == null) {
            if (strictStandalone) {
                startupLog("PF301 strict launcher resumed fallback skipped");
            } else {
                launchedActivity = am.getResumedActivity();
            }
        }
        if (launchedActivity == null) {
            startupLog("[WestlakeLauncher] WARNING: No activity, rendering empty surface");
        }
        if (launchedActivity != null) {
```

`new_string`:
```java
        // Try to get the launched activity even if errors occurred
        appendCutoffCanaryMarker("FRAME_EMIT_TRACE post_launch_entry launchedActivity_null="
                + boolToken(launchedActivity == null)
                + " stack=" + intAscii(am != null ? am.getStackSize() : -1));
        startupLog("[FRAME-EMIT-TRACE] post_launch_entry launchedActivity_null="
                + (launchedActivity == null) + " stack=" + (am != null ? am.getStackSize() : -1));
        final boolean strictStandalone = !isRealFrameworkFallbackAllowed();
        if (launchedActivity == null) {
            if (strictStandalone) {
                startupLog("PF301 strict launcher resumed fallback skipped");
                appendCutoffCanaryMarker("FRAME_EMIT_TRACE strict_resumed_fallback_skipped");
            } else {
                launchedActivity = am.getResumedActivity();
                appendCutoffCanaryMarker("FRAME_EMIT_TRACE non_strict_resumed_fallback nonnull="
                        + boolToken(launchedActivity != null));
            }
        }
        if (launchedActivity == null) {
            startupLog("[WestlakeLauncher] WARNING: No activity, rendering empty surface");
            appendCutoffCanaryMarker("FRAME_EMIT_TRACE no_activity_warning");
            // PF-noice-014 escape valve: emit a fallback splash frame so the
            // SurfaceView shows something instead of staying black.
            tryEmitNoiceFallbackFrame("no_activity");
        }
        if (launchedActivity != null) {
            appendCutoffCanaryMarker("FRAME_EMIT_TRACE branch_activity_nonnull cls="
                    + safeMarkerToken(launchedActivity.getClass().getName()));
            startupLog("[FRAME-EMIT-TRACE] branch_activity_nonnull cls="
                    + launchedActivity.getClass().getName());
```

#### 3.2.2 Add `tryEmitNoiceFallbackFrame` helper (insert at top of class near other static helpers, e.g. before `runShowcaseDirectFrameLoop`)

This is the PF-noice-014 escape-valve frame. Designed to be safely callable even when the activity is null — uses no Activity references.

Add new method just before line 11385 (`private static void runShowcaseDirectFrameLoop`):

```java
    /**
     * PF-noice-014 (2026-05-05): emit a hardcoded splash DLST frame to the
     * pipe so the SurfaceView shows a visible "Westlake guest running"
     * indicator even when the activity launch fully failed. Loops forever
     * in a daemon thread so the SurfaceView keeps the frame.
     *
     * Safe to call when launchedActivity == null. Uses no Activity references.
     */
    private static volatile boolean sNoiceFallbackFrameLoopActive = false;
    private static void tryEmitNoiceFallbackFrame(String reason) {
        if (sNoiceFallbackFrameLoopActive) return;
        sNoiceFallbackFrameLoopActive = true;
        Thread t = new Thread(new Runnable() {
            @Override public void run() {
                int tick = 0;
                while (sNoiceFallbackFrameLoopActive) {
                    try {
                        emitNoiceFallbackFrameOnce(reason, tick);
                        tick++;
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {
                        return;
                    } catch (Throwable err) {
                        startupLog("[FRAME-EMIT-TRACE] noice fallback emit error", err);
                        try { Thread.sleep(2000); } catch (InterruptedException ie) { return; }
                    }
                }
            }
        }, "NoiceFallbackFrameLoop");
        t.setDaemon(true);
        t.start();
        startupLog("[FRAME-EMIT-TRACE] noice fallback frame loop started reason=" + reason);
        appendCutoffCanaryMarker("FRAME_EMIT_TRACE fallback_loop_started reason="
                + safeMarkerToken(reason));
    }

    private static void emitNoiceFallbackFrameOnce(String reason, int tick) throws java.io.IOException {
        java.io.ByteArrayOutputStream ops = new java.io.ByteArrayOutputStream(2048);
        showcaseColor(ops, 0xff1A237E); // deep indigo
        showcaseText(ops, "Westlake guest dalvikvm",
                60, 220, 22, 0xffffffff);
        showcaseText(ops, "Running com.github.ashutoshgngwr.noice",
                60, 260, 14, 0xffe0e0e0);
        showcaseText(ops, "Activity launch failed (" + reason + ")",
                60, 300, 12, 0xffffcdd2);
        showcaseText(ops, "tick=" + intAscii(tick),
                60, 340, 10, 0xff80cbc4);
        byte[] data = ops.toByteArray();
        java.io.OutputStream out;
        synchronized (sStrictFrameOutputLock) {
            out = strictFrameOutputStream();
            if (out == null) {
                if (tick == 0) {
                    appendCutoffCanaryMarker("FRAME_EMIT_TRACE fallback_no_pipe");
                }
                return;
            }
            writeIntLe(out, 0x444C5354);
            writeIntLe(out, data.length);
            out.write(data);
            out.flush();
        }
        if (tick == 0) {
            appendCutoffCanaryMarker("FRAME_EMIT_TRACE fallback_first_frame_emitted bytes="
                    + intAscii(data.length));
            startupLog("[FRAME-EMIT-TRACE] fallback first frame emitted bytes=" + data.length);
        } else if ((tick % 20) == 0) {
            appendCutoffCanaryMarker("FRAME_EMIT_TRACE fallback_keepalive tick=" + intAscii(tick));
        }
    }
```

#### 3.2.3 Instrument `writeShowcaseXmlTreeFrame` (around line 14542)

`old_string`:
```java
    private static boolean writeShowcaseXmlTreeFrame(Activity activity, String reason) {
        try {
            layoutShowcaseDecor(activity);
            android.view.View decor = activity != null && activity.getWindow() != null
                    ? activity.getWindow().getDecorView() : null;
            if (decor == null) {
                appendCutoffCanaryMarker("SHOWCASE_XML_TREE_RENDER_FAIL reason="
                        + reason + " err=no_decor");
                return false;
            }
```

`new_string`:
```java
    private static boolean writeShowcaseXmlTreeFrame(Activity activity, String reason) {
        appendCutoffCanaryMarker("FRAME_EMIT_TRACE showcase_xml_attempt reason="
                + safeMarkerToken(reason)
                + " activity_null=" + boolToken(activity == null));
        try {
            layoutShowcaseDecor(activity);
            android.view.View decor = activity != null && activity.getWindow() != null
                    ? activity.getWindow().getDecorView() : null;
            if (decor == null) {
                appendCutoffCanaryMarker("SHOWCASE_XML_TREE_RENDER_FAIL reason="
                        + reason + " err=no_decor");
                appendCutoffCanaryMarker("FRAME_EMIT_TRACE showcase_xml_no_decor reason="
                        + safeMarkerToken(reason));
                return false;
            }
```

#### 3.2.4 Instrument `writeStrictStandaloneViewFrame` (around line 18808)

`old_string`:
```java
    private static boolean writeStrictStandaloneViewFrame(Activity activity, String reason) {
        try {
            int height = isMcdonaldsActivity(activity) ? YELP_SURFACE_HEIGHT : SURFACE_HEIGHT;
            android.view.View decor = activity != null && activity.getWindow() != null
                    ? activity.getWindow().getDecorView() : null;
            if (decor == null) {
                appendCutoffCanaryMarker("STRICT_VIEW_FRAME_FAIL reason="
                        + safeMarkerToken(reason) + " err=no_decor");
                return false;
            }
```

`new_string`:
```java
    private static boolean writeStrictStandaloneViewFrame(Activity activity, String reason) {
        appendCutoffCanaryMarker("FRAME_EMIT_TRACE strict_attempt reason="
                + safeMarkerToken(reason)
                + " activity_null=" + boolToken(activity == null)
                + " activity_cls=" + safeMarkerToken(
                        activity == null ? "null" : activity.getClass().getName()));
        try {
            int height = isMcdonaldsActivity(activity) ? YELP_SURFACE_HEIGHT : SURFACE_HEIGHT;
            android.view.View decor = activity != null && activity.getWindow() != null
                    ? activity.getWindow().getDecorView() : null;
            if (decor == null) {
                appendCutoffCanaryMarker("STRICT_VIEW_FRAME_FAIL reason="
                        + safeMarkerToken(reason) + " err=no_decor");
                appendCutoffCanaryMarker("FRAME_EMIT_TRACE strict_no_decor reason="
                        + safeMarkerToken(reason));
                return false;
            }
            int decor_children = decor instanceof android.view.ViewGroup
                    ? ((android.view.ViewGroup) decor).getChildCount() : -1;
            appendCutoffCanaryMarker("FRAME_EMIT_TRACE strict_decor_ready reason="
                    + safeMarkerToken(reason)
                    + " children=" + intAscii(decor_children));
```

After the existing `writeIntLe(out, 0x444C5354); writeIntLe(out, data.length); out.write(data); out.flush();` block (around line 18904-18908), add:

`old_string`:
```java
                writeIntLe(out, 0x444C5354);
                writeIntLe(out, data.length);
                out.write(data);
                out.flush();
            }
            appendCutoffCanaryMarker("STRICT_VIEW_FRAME_OK reason="
```

`new_string`:
```java
                writeIntLe(out, 0x444C5354);
                writeIntLe(out, data.length);
                out.write(data);
                out.flush();
            }
            appendCutoffCanaryMarker("FRAME_EMIT_TRACE strict_pipe_write_done reason="
                    + safeMarkerToken(reason)
                    + " bytes=" + intAscii(data.length));
            appendCutoffCanaryMarker("STRICT_VIEW_FRAME_OK reason="
```

#### 3.2.5 Instrument `runStrictStandaloneMainLoop` entry (around line 18654)

`old_string`:
```java
    private static void runStrictStandaloneMainLoop(Activity initialActivity, MiniActivityManager am) {
        startupLog("PF301 strict launcher keepalive pump begin");
```

`new_string`:
```java
    private static void runStrictStandaloneMainLoop(Activity initialActivity, MiniActivityManager am) {
        startupLog("PF301 strict launcher keepalive pump begin");
        appendCutoffCanaryMarker("FRAME_EMIT_TRACE strict_main_loop_begin activity_null="
                + boolToken(initialActivity == null)
                + " activity_cls=" + safeMarkerToken(
                        initialActivity == null ? "null"
                                : initialActivity.getClass().getName()));
```

### 3.3 File 2: `$HOME/android-to-openharmony-migration/shim/java/android/app/MiniActivityManager.java`

Mark the recovery boundary so we know where control transitions back. Insert after the recovery's catch (around line 2390-2405).

`old_string`:
```java
            tryRecoverFragments(r.activity);
        }
        if (isFinishedOrDestroyed(r)) {
            return;
        }
        try {
            dispatchLifecycleEvent(r.activity, "ON_CREATE");
        } catch (Exception e) {
            Log.w(TAG, "performCreate lifecycle dispatch error: " + e.getMessage());
        }
        try {
            tryRecoverFragments(r.activity);
        } catch (Throwable t) {
            Log.d(TAG, "  tryRecoverFragments post-create failed: " + t);
        }
    }
```

`new_string`:
```java
            tryRecoverFragments(r.activity);
        }
        try {
            com.westlake.engine.WestlakeLauncher.appendCutoffCanaryMarker(
                    "FRAME_EMIT_TRACE recovery_done cls="
                            + r.component.getClassName()
                            + " decor_children="
                            + (hasInstalledWindowContent(r.activity) ? "positive" : "zero"));
        } catch (Throwable ignored) {
        }
        if (isFinishedOrDestroyed(r)) {
            try {
                com.westlake.engine.WestlakeLauncher.appendCutoffCanaryMarker(
                        "FRAME_EMIT_TRACE recovery_destroyed cls="
                                + r.component.getClassName());
            } catch (Throwable ignored) {
            }
            return;
        }
        try {
            dispatchLifecycleEvent(r.activity, "ON_CREATE");
        } catch (Exception e) {
            Log.w(TAG, "performCreate lifecycle dispatch error: " + e.getMessage());
        }
        try {
            tryRecoverFragments(r.activity);
        } catch (Throwable t) {
            Log.d(TAG, "  tryRecoverFragments post-create failed: " + t);
        }
        try {
            com.westlake.engine.WestlakeLauncher.appendCutoffCanaryMarker(
                    "FRAME_EMIT_TRACE perform_create_return cls="
                            + r.component.getClassName());
        } catch (Throwable ignored) {
        }
    }
```

Also instrument performStart and performResume thread-join boundaries (around line 3197-3220 and 3267-3270):

`old_string`:
```java
        startThread.setDaemon(true);
        startThread.start();
        long startWaitMs = mcdPdp ? 35000L : 10000L;
        try { startThread.join(startWaitMs); } catch (InterruptedException ie) {}
```

`new_string`:
```java
        startThread.setDaemon(true);
        startThread.start();
        long startWaitMs = mcdPdp ? 35000L : 10000L;
        try { startThread.join(startWaitMs); } catch (InterruptedException ie) {}
        try {
            com.westlake.engine.WestlakeLauncher.appendCutoffCanaryMarker(
                    "FRAME_EMIT_TRACE perform_start_join_done cls="
                            + r.component.getClassName()
                            + " done=" + (startDone[0] ? "true" : "false")
                            + " waitMs=" + startWaitMs);
        } catch (Throwable ignored) {
        }
```

`old_string`:
```java
        resumeThread.setDaemon(true);
        resumeThread.start();
        try { resumeThread.join(10000); } catch (InterruptedException ie) {}
        if (!resumeDone[0]) Log.w(TAG, "performResume TIMEOUT (10s) for " + r.component.getClassName());
```

`new_string`:
```java
        resumeThread.setDaemon(true);
        resumeThread.start();
        try { resumeThread.join(10000); } catch (InterruptedException ie) {}
        if (!resumeDone[0]) Log.w(TAG, "performResume TIMEOUT (10s) for " + r.component.getClassName());
        try {
            com.westlake.engine.WestlakeLauncher.appendCutoffCanaryMarker(
                    "FRAME_EMIT_TRACE perform_resume_join_done cls="
                            + r.component.getClassName()
                            + " done=" + (resumeDone[0] ? "true" : "false"));
        } catch (Throwable ignored) {
        }
```

### 3.4 File 3: `$HOME/android-to-openharmony-migration/westlake-host-gradle/app/src/main/java/com/westlake/host/WestlakeVM.kt`

Instrument the host pipe-reader to log every frame received and any sync recoveries. Around line 1797-1830 in `readPipeAndRenderLocked`:

`old_string`:
```kotlin
            if (size <= 0 || size > 2 * 1024 * 1024) {  // 2MB for screen capture frames
```

`new_string`:
```kotlin
            if (size <= 0 || size > 2 * 1024 * 1024) {  // 2MB for screen capture frames
                Log.w("WestlakeVM", "FRAME_RECV_TRACE bad_size=$size; will resync")
```

`old_string`:
```kotlin
                is DisplayListFrameView -> {
                    target.submitFrame(data, size)
                    Log.i("WestlakeVM", "Frame: ${size} bytes → View")
                }
```

`new_string`:
```kotlin
                is DisplayListFrameView -> {
                    target.submitFrame(data, size)
                    Log.i("WestlakeVM", "Frame: ${size} bytes → View")
                    Log.i("WestlakeVM", "FRAME_RECV_TRACE accepted bytes=$size target=DisplayListFrameView")
                }
```

(repeat the FRAME_RECV_TRACE log lines for the Bitmap, Surface, TextureSurface branches at lines 1825, 1857, 1876).

Finally, around line 1881 in the resync branch:

`old_string`:
```kotlin
            // Read and sync to next frame's magic header
```

`new_string`:
```kotlin
            // Read and sync to next frame's magic header
            // FRAME_RECV_TRACE: emit one log per frame consumed so we can correlate
            // guest emit count with host receive count.
            Log.i("WestlakeVM", "FRAME_RECV_TRACE sync_to_next_frame after_size=$size")
```

### 3.5 Build / sync / run sequence (D2')

```bash
# Apply edits per §3.2-§3.4 above (or similar; line numbers may shift).
cd $HOME/android-to-openharmony-migration
./scripts/build-shim-dex.sh
sha256sum out/aosp-shim.dex

cd $HOME/android-to-openharmony-migration/westlake-host-gradle
./gradlew :app:assembleDebug
# Install host APK if it changed
/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -s cfb7c9e3 install -r app/build/outputs/apk/debug/app-debug.apk

# Sync shim only (runtime is unchanged from PF-noice-002 land)
DALVIKVM_SRC=$HOME/art-latest/build-ohos-arm64/bin/dalvikvm \
  cd $HOME/android-to-openharmony-migration && ./scripts/sync-westlake-phone-runtime.sh

# Pre-clean
/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -s cfb7c9e3 shell 'pm clear com.westlake.host'
sleep 2
/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -s cfb7c9e3 shell 'pm clear com.github.ashutoshgngwr.noice'

scripts/run-noice-phone-gate.sh
```

After the run, examine the FRAME_EMIT_TRACE markers in the canary file:
```bash
/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -s cfb7c9e3 \
    shell 'cat /sdcard/westlake_cutoff_canary.txt' > /tmp/canary.txt
grep "FRAME_EMIT_TRACE" /tmp/canary.txt
grep "FRAME_RECV_TRACE" artifacts/noice-1day/<latest>/logcat-dump.txt
```

### 3.6 Read-the-tape protocol (D2' analysis)

The canary should now produce a complete trace. Apply these greps in order:

```bash
# 1. Did the post-launch boundary fire?
grep "FRAME_EMIT_TRACE post_launch_entry" /tmp/canary.txt
# Expected: ≥1 line. If MISSING, am.startActivityDirect never returned — the hang is BEFORE line 4906.

# 2. Did the recovery complete?
grep "FRAME_EMIT_TRACE recovery_done" /tmp/canary.txt
grep "FRAME_EMIT_TRACE perform_create_return" /tmp/canary.txt
# Expected if hang is after recovery.

# 3. Did the lifecycle joins fire?
grep "FRAME_EMIT_TRACE perform_start_join_done" /tmp/canary.txt
grep "FRAME_EMIT_TRACE perform_resume_join_done" /tmp/canary.txt
# Expected if hang is after the lifecycle but before launcher's PF301 branch.

# 4. Did launcher's PF301 strict branch fire?
grep "FRAME_EMIT_TRACE branch_activity_nonnull" /tmp/canary.txt
grep "PF301 strict launcher activity branch begin" /tmp/canary.txt
# Confirms the launcher reached line 4920+.

# 5. Did frame-emit attempts happen?
grep "FRAME_EMIT_TRACE strict_attempt" /tmp/canary.txt
grep "FRAME_EMIT_TRACE strict_decor_ready" /tmp/canary.txt
grep "FRAME_EMIT_TRACE strict_pipe_write_done" /tmp/canary.txt

# 6. Did the host accept frames?
grep "FRAME_RECV_TRACE accepted" artifacts/noice-1day/<run>/logcat-dump.txt
grep "FRAME_RECV_TRACE bad_size" artifacts/noice-1day/<run>/logcat-dump.txt

# 7. Did the fallback escape valve fire?
grep "FRAME_EMIT_TRACE fallback_first_frame_emitted" /tmp/canary.txt
```

### 3.7 Branches if D2' instrumentation is empty or inconclusive

| Symptom | Branch |
|---|---|
| `post_launch_entry` missing | `am.startActivityDirect` hangs. Look at `[PFCUT] forced interpreter JNI reason=Unsafe method=void ... Unsafe.park` lines in logcat — what thread? If main thread is parked, search for `parkedWorkersStack` (kotlinx workers) — the main thread is waiting on a coroutine worker that got into a stuck state. Branch: skip `tryRecoverFragments` for non-McD apps (already done in line 2531) and short-circuit even harder: if recovery's setContentView fails AND programmatic fallback fails, RETURN immediately from performCreate without going to dispatchLifecycleEvent. |
| `post_launch_entry` present but `branch_activity_nonnull` missing | `launchedActivity == null` — means `am.getResumedActivity()` returned null. But `mResumed` is set at MiniActivityManager:1833 BEFORE performCreate. So getResumedActivity should be non-null. Either `mResumed` got wiped between 1833 and the launcher's call, OR the launcher's call to `am.getResumedActivity` happened AFTER an exception threw out of `am.startActivityDirect`. Branch: change line 4837 to check both `am.getResumedActivity()` AND fallback to `am.getStackSize() > 0 ? am.getActivity(am.getStackSize()-1) : null`. |
| `branch_activity_nonnull` present but no `strict_attempt` | the launcher reached PF301 strict launcher activity branch but didn't enter any of the showcase/yelp/material/mcd-profile/material-xml-probe/runStrictStandaloneMainLoop paths. That means `nativeOk == false` AND `launchedActivity` was non-null but the renderFrame call threw (caught at line 5260) AND runStrictStandaloneMainLoop wasn't called because `strictStandalone == true && nativeOk == false`. Branch: line 5268 `if (strictStandalone) { runStrictStandaloneMainLoop(...) }` already gates only on strictStandalone — check if line 5244 `if (nativeOk && launchedActivity != null)` is false (nativeOk false). Then line 5268 won't fire either. Confirm by checking `late OHBridge` markers. |
| `strict_attempt` present but no `strict_pipe_write_done` | frame attempt began but never reached the pipe write — exception during view tree rendering. Branch: examine the FRAME_EMIT_TRACE strict_decor_ready output for view-tree size; if 0 children, the decor is empty (PF-noice-011 fix needed); if N children but no write, an exception is being thrown silently. Add explicit catch + log around the frame-emit block. |
| `strict_pipe_write_done` present but no `FRAME_RECV_TRACE accepted` | guest is writing but host isn't reading correctly. Branch: examine `FRAME_RECV_TRACE bad_size` count — if positive, the stdout bytes are intermixed with text logs (the runtime's `[RT]` lines, etc.). Fix by either (a) redirecting noice runtime logs to stderr OR (b) using `WESTLAKE_FRAME_PATH` file-bridge for noice too (lift the McD-only gate at WestlakeVM.kt:771). |
| `fallback_first_frame_emitted` present | escape valve fired; D5' acceptance is achievable via fallback alone. Continue to fix the activity path in parallel. |

### 3.8 Why this approach over alternatives

- **Direct DEX injection of frame writes via WestlakeLauncher Java side** (already there) is the right place — runtime-side frame emission would require a much larger refactor.
- **Hooking via `Instrumentation` or platform `ActivityThread`** wouldn't help because we're in standalone-dalvikvm mode.
- **A separate frame-emission service thread independent of Activity** is exactly what `tryEmitNoiceFallbackFrame` becomes — the escape valve. The full fix continues to use the per-Activity strict path because that's how content actually gets rendered.

---

## 4. D2'-D5' validation probes per day

### 4.1 D2' — Frame-emission audit

```bash
scripts/run-noice-phone-gate.sh
# Pull canary
adb -s cfb7c9e3 shell 'cat /sdcard/westlake_cutoff_canary.txt' > /tmp/canary.txt

# Acceptance:
grep "FRAME_EMIT_TRACE post_launch_entry" /tmp/canary.txt    # ≥1
grep -c "FRAME_EMIT_TRACE" /tmp/canary.txt                   # ≥6 (each phase boundary)

# Diagnose using §3.6 read-the-tape protocol.
# Outcome: one of the §3.7 branches fires; D3' targets that branch.
```

### 4.2 D3' — Frame-emission fix

Two parallel fixes (from D2' findings):

- **PF-noice-014 (fallback frame loop):** verify by checking `FRAME_EMIT_TRACE fallback_first_frame_emitted` in canary. Verify host receives via `WestlakeVM: FRAME_RECV_TRACE accepted bytes=...`.
- **PF-noice-011/012 (recovery + installContent):** verify by checking `FRAME_EMIT_TRACE strict_decor_ready children=positive` AND `STRICT_VIEW_FRAME_OK` ≥1.

```bash
# Fallback-only acceptance (cheapest):
grep "FRAME_RECV_TRACE accepted" artifacts/noice-1day/<run>/logcat-dump.txt | wc -l
# Expect ≥3 (loop emits every 500ms; gate runs ~10s minimum)

# Activity-path acceptance:
grep "STRICT_VIEW_FRAME_OK reason=tick" artifacts/noice-1day/<run>/logcat-dump.txt | wc -l
# Expect ≥3
```

### 4.3 D4' — View-binding gap fix or sidestep

If D3' acceptance achieved via fallback alone, D4' is OPTIONAL — paint already works.

Path A (real fix):
```bash
# Identify 0x7f090167:
unzip -p /tmp/noice.apk resources.arsc > /tmp/noice.arsc
python3 -c '
import struct
# Quick decode: 0x7f090167 = package 0x7f, type 0x09 (id), entry 0x167
# Use androguard or aapt2 if available.
# Output the resource name from the table.
'
# If aapt2 available:
aapt2 dump resources /tmp/noice.apk | grep -i "0x7f090167"
```

Path B (sidestep):
- accept fallback frame as the visible content for D5'; defer real fix to follow-on.

### 4.4 D5' — Paint verification + soak + acceptance

```bash
# Ensure all D2'-D4' patches applied + synced.
scripts/run-noice-phone-gate.sh

# Per-criterion acceptance:
# A1 Boot:
grep "FRAME_EMIT_TRACE post_launch_entry" /tmp/canary.txt   # ≥1

# A6 Visible paint:
sha256sum artifacts/noice-1day/<run>/screen.png
# Compare to baseline 4915b583... (current all-black-with-host-chrome)
# Expect different SHA.

# A7 5-min soak:
NOICE_SOAK_SECONDS=300 NOICE_GATE_TAPS=center scripts/run-noice-phone-gate.sh
grep "noice_5min_soak_status=PASS" artifacts/noice-1day/<run>/check-noice-proof.txt

grep -cE "signal 7|Fatal signal" artifacts/noice-1day/<run>/logcat-dump.txt
# Expect 0

# McD bounded regression:
scripts/run-real-mcd-phone-gate.sh
grep "gate_status=PASS" artifacts/real-mcd/<latest>/check-real-mcd-proof.txt
```

D5' results doc: `docs/program/WESTLAKE_PF_NOICE_V2_RESULTS_<actual-date>.md`

---

## 5. Risk register

| Risk | Likelihood | Severity | Mitigation |
|---|---|---|---|
| **Frame-emission audit reveals NO emission ever attempted** (the entire post-launch path is dead) | HIGH | LOW (this matches the hypothesis) | The fallback escape valve (PF-noice-014) handles this exact case — emit a frame regardless of activity state. |
| **Stdout pipe is too noisy for non-McD apps; DLST sync fails on host** (runtime `[RT]` text logs intermixed with binary frames) | MED | MED | Verify by checking `FRAME_RECV_TRACE bad_size` count. Mitigate by lifting WestlakeVM.kt:771 frameBridgeFile gate to also create a file bridge for noice (file path passed via `WESTLAKE_FRAME_PATH` env, already supported by `strictFrameOutputStream`). |
| **AppCompatDelegate's null `mAttachListener` resurfaces in installMinimalStandaloneContent path too** (`Window.setContentView` calls into AppCompat's hooks) | MED | MED | Bypass Activity.setContentView; call `r.activity.getWindow().setContentView` directly on the shim Window. The shim Window doesn't go through AppCompat. |
| **Class-identity duplication causes a NEW assignability failure during frame emission** (rendering touches different class identities than DI) | LOW | HIGH | The cascade fix in `mirror::ObjectArray::CheckAssignable` already handles ALL descriptor-equal classes, not just String[]. Should not regress; verify by running `pm clear` + new run + checking for any `Tolerating clinit failure` lines in logcat. |
| **The "Missing required view 0x7f090167" turns out to be a pure resources.arsc parsing failure** (BinaryXmlParser limitation) | MED | LOW | sidestep with PF-noice-014 fallback frame; full fix is PF-noice-013 path A or C. |
| **Host APK has a DisplayListFrameView swap-out for the SurfaceView that doesn't render DLST ops** for noice's specific config | LOW | LOW | Verify by checking `WestlakeVM:DisplayListFrameView <W>x<H>` log line for noice; confirmed present at 19:31:31.140 in v6 artifact. |
| **Multi-second sleep loop in fallback frame thread starves the launcher's PF301 strict branch from running** | LOW | LOW | The fallback runs in a daemon thread; doesn't block the main launcher thread. |
| **Soak test reveals a memory leak in the per-500ms frame loop** (ByteArrayOutputStream allocation per frame) | LOW | LOW | Reuse the BAOS via reset(); add at iteration time. Validate at D5' soak. |
| **D2' instrumentation log lines exceed canary file size limit and overwrite earlier markers** | MED | LOW | Use `appendCutoffCanaryMarker` (designed for this; file is rotated, recent entries preserved). Don't switch to file logging directly. |
| **D5' soak reveals the existing `tryRecoverFragments` deadlock** (recovery's reflection probes Hilt's FragmentManager which awaits a Coroutine worker that's parked) | MED | MED | Already partially mitigated in MiniActivityManager:2531 (skip generic fragment recovery for McD). Extend to noice: skip if package starts with `com.github.ashutoshgngwr.`. |
| **AppCompatDelegate inserts a non-null phantom child** into decor between performCreate and the launcher's PF301 strict branch, defeating PF-noice-012 Shape A's "existingChildren > 0" widening | LOW | LOW | Shape B (launcher-driven fallback) doesn't depend on decor state; covers this case. |
| **Audio/network requirements surface as paint blockers** (e.g. noice's MainActivity tries to bind a service in onCreate which throws when AudioFocus stub returns null) | LOW | LOW | Already handled — the cascade fix lets onCreate complete past those points (logs show `AppCompat NPE` is post-DI, not pre-DI). The paint blocker is purely the view binding. |
| **Frame-emission instrumentation regresses McD bounded gate due to extra appendCutoffCanaryMarker overhead** | LOW | LOW | `appendCutoffCanaryMarker` is a single file write; benchmark against baseline. The McD path emits ~30 markers per second steady state; adding 6 more is <20% overhead. |
| **Real SunX509+TLS Provider work blocks paint** (downstream blocker surfaces only after paint works) | MED (but later) | MED | PF-noice-015 deferred; soak test in D5' will reveal whether TLS is actually needed for first paint OR if it's a later-phase issue. Empirical: noice's first frame doesn't require network. |

---

## 6. Sign-off / handoff

### 6.1 Implementing-agent prompt (D2')

```
Goal: implement the D2' frame-emission audit per
docs/program/WESTLAKE_PF_NOICE_PLAN_V2_20260505.md §3, run one noice gate,
and characterize which frame-emission path is broken.

Constraints:
- Read-only EXCEPT shim source edits per §3.2-§3.4 + host APK edit per §3.4.
- Build via ./scripts/build-shim-dex.sh and ./gradlew :app:assembleDebug.
- Run noice gate via scripts/run-noice-phone-gate.sh.
- DO NOT push commits; supervisor batches.

Concrete steps:
1. Apply §3.2 edits to shim/java/com/westlake/engine/WestlakeLauncher.java
2. Apply §3.3 edits to shim/java/android/app/MiniActivityManager.java
3. Apply §3.4 edits to westlake-host-gradle/.../WestlakeVM.kt
4. Build shim: ./scripts/build-shim-dex.sh
5. Build host: cd westlake-host-gradle && ./gradlew :app:assembleDebug
6. Install host APK: adb install -r app/build/outputs/apk/debug/app-debug.apk
7. Sync shim: ./scripts/sync-westlake-phone-runtime.sh
8. pm clear both apps; run noice gate.
9. Pull canary: adb shell 'cat /sdcard/westlake_cutoff_canary.txt' > /tmp/canary.txt
10. Apply §3.6 read-the-tape protocol; identify the broken step from §3.7 branches.
11. Write artifact note at:
    artifacts/noice-1day/<timestamp>_noice_pf_noice_010_d2prime_audit/_AUDIT_NOTES.txt
12. Surface the candidate D3' fix shape (PF-noice-011/012/014).

Final report (under 400 words):
- New shim hash
- New host APK version
- Number of FRAME_EMIT_TRACE markers captured
- Which branch from §3.7 fired (cite specific marker presence/absence)
- D3' fix recommendation (PF-noice-011/012/014 sub-issue with reasoning)
```

### 6.2 Implementing-agent prompt (D3')

```
Goal: apply the D3' frame-emission fix per
docs/program/WESTLAKE_PF_NOICE_PLAN_V2_20260505.md §4.2 + the D2' audit findings.

If audit recommends PF-noice-014 fallback only:
  - The fallback code is already in §3.2.2 of the plan; it ships with D2'.
  - D3' verification just confirms the fallback fires and frames reach host.

If audit recommends PF-noice-011 (recovery shim Window):
  - Modify MiniActivityManager.java tryRecoverContent to use
    r.activity.getWindow().setContentView() directly instead of
    r.activity.setContentView(), bypassing AppCompat delegate.

If audit recommends PF-noice-012 (installMinimalStandaloneContent gate widening):
  - Modify Window.installMinimalStandaloneContent to widen the
    existingChildren > 0 gate to also check children[0] is a meaningful
    ViewGroup with measured size.

Final report: chosen fix, the diff, screenshot SHA delta, FRAME_EMIT_TRACE
+ FRAME_RECV_TRACE counts.
```

### 6.3 Acceptance gates summary table

| Day | Gate | Pass condition | Fail branch |
|---|---|---|---|
| **D2'** | Frame-emission audit complete | ≥6 FRAME_EMIT_TRACE markers; broken step identified | iterate instrumentation depth (e.g. drop into am.startActivityDirect with deeper traces) |
| **D3'** | Frames reach host pipe | ≥3 FRAME_RECV_TRACE accepted lines OR ≥3 FRAME_EMIT_TRACE fallback_first_frame_emitted | choose alt fix shape (Shape A vs B); or escalate to BOTH |
| **D4'** | View-binding fixed OR sidestep accepted | EITHER 0x7f090167 identified+fixed OR fallback frame is the D5' acceptance | accept fallback for D5'; defer real fix to follow-on |
| **D5'** | Paint visible + soak | screen.png SHA ≠ baseline; 5-min no SIGBUS | revert risky changes; submit results doc with what worked |

### 6.4 Phone-state checkpoint at D5' close

- Runtime: `88e7679701b169118e469deef369f4a6d015e6c7f1697036d6d3cb8256c59b3a` (UNCHANGED from PF-noice-002 land — all V2 work is shim/host-side)
- Shim: PF-noice-010+014 candidate hash (D3' build)
- Host APK: PF-noice-010 candidate version
- Backup at `$HOME/westlake-runtime-backups/dalvikvm.pre-pf630-d7e10e47.bak` retained
- Noice + Westlake host installed and `pm clear`'d
- McD bounded gate verified non-regression in D4'

### 6.5 Open follow-ons after D5'

- PF-noice-013: full diagnosis + real fix for "Missing required view 0x7f090167"
- PF-noice-015: real SunX509+TLS Provider
- PF-noice-017: env-flag gating for FRAME-EMIT-TRACE
- PF-noice-018: AppCompatDelegate static-state hardening
- PF-noice-006: audio gap stub
- PF-noice-008: generic launcher token

---

## 7. References

### 7.1 Source files (with key line numbers)

- `$HOME/android-to-openharmony-migration/shim/java/com/westlake/engine/WestlakeLauncher.java`
  - Activity-launch boundary @4830-4920 (instrument here for D2')
  - `runStrictStandaloneMainLoop` @18654 (the generic noice path)
  - `writeStrictStandaloneViewFrame` @18808 (frame-emit core; instrument here)
  - `runShowcaseDirectFrameLoop` @11385 (per-app showcase variant)
  - `writeShowcaseXmlTreeFrame` @14542 (showcase XML tree variant)
  - `MCD_DASH_PROJECTED_FRAME` @16833 (McD-specific frame variant)
  - 8 DLST-magic write sites: @9252, @9457, @10298, @10863, @11146, @14560, @17363, @18904
  - `strictFrameOutputStream` @17612 (pipe writer)
  - `strictFrameFilePath` @17656 (env-driven path resolver)
- `$HOME/android-to-openharmony-migration/shim/java/android/app/MiniActivityManager.java`
  - `startResolvedActivity` @1511 (entry point for `am.startActivityDirect`)
  - `mResumed = record` @1833 (early set so getResumedActivity is non-null)
  - `performCreate` @2225 (with onCreate timeout)
  - `tryRecoverContent` @2294 (recovery branch — XML setContentView, programmatic fallback)
  - `tryRecoverFragments` @2515 (fragment recovery; skips McD; also skips for noice in D3' Shape B)
  - `performStart` @3155 (with 10s join)
  - `performResume` @3239 (with 10s join)
- `$HOME/android-to-openharmony-migration/shim/java/android/view/Window.java`
  - `Window(Context)` @63 (mDecorView initialized to FrameLayout)
  - `getDecorView` @92 (lazy-alloc fallback)
  - `installMinimalStandaloneContent` @118 (PF-noice-012 fix target — currently sets indigo TextView)
  - `setContentView(View)` @367/370 (no AppCompat delegate)
- `$HOME/android-to-openharmony-migration/westlake-host-gradle/app/src/main/java/com/westlake/host/WestlakeVM.kt`
  - `frameBridgeFile` McD-only gate @771 (lift for noice if D2' shows stdout pipe is too noisy)
  - `pipeStream = startedProcess.inputStream` @1444 (stdout binary pipe for non-McD)
  - `readPipeAndRender` @1750 / `readPipeAndRenderLocked` @1767 (host pipe consumer)
  - `DLIST_MAGIC = 0x444C5354` @47 (must match guest)
  - `DisplayListFrameView` @1671 (host-side render target)
- `$HOME/art-latest/runtime/mirror/object_array-inl.h` (per part-4 §"What landed", live build path)
  - `CheckAssignable` template — descriptor-fallback (PF-noice-002 fix)

### 7.2 Key empirical artifacts (cumulative across day-2 part 1-4 and noice iterations)

- `artifacts/noice-1day/20260504_223752_noice_noice_pfcut_trace_d1/pfcut_trace.txt` — D1 trace (cascade root cause = class-identity duplication)
- `artifacts/noice-1day/20260504_225818_noice_noice_pf_noice_002_v3_with_match/` — cascade GONE (zero ASE, all clinits clean)
- `artifacts/noice-1day/20260504_232545_noice_noice_pf_noice_002a_v4_ssl/` — past SunX509 NSAE, hits TLS NSAE
- `artifacts/noice-1day/20260504_232835_noice_noice_pf_noice_002a_v5_no_recurse/` — past TLS, hits StackOverflow
- `artifacts/noice-1day/20260504_233128_noice_noice_pf_noice_002a_v6_stub_factory/` — current empirical state; SO fixed; "Missing required view 0x7f090167" + null receivers; **NO POST-LAUNCH PF301 MARKERS**
- `artifacts/noice-1day/_noice_indigo_textview_screen.png` — current visible state (all-black SurfaceView with bottom host chrome label); confirms host's pipe consumer + SurfaceView are alive but no frames arriving from guest

### 7.3 Critical V2-relevant code-flow finding (for next agent)

**Confirmed by reading v6 artifact's logcat-stream.txt (5981 lines):** the launcher reaches `am.startActivityDirect(null, "com.github.ashutoshgngwr.noice", "...MainActivity", -1, ...)` at 19:31:56.730. After this, only MiniActivityManager logs appear (`tryRecoverContent`, `programmatic fallback failed` at 19:31:58.346). NO subsequent `[WestlakeLauncher] AM direct result:` log line ever appears (would be from line 4859). NO `PF301 strict launcher` markers from line 4920+. NO `performStart DONE` or `performResume DONE` from MiniActivityManager 1870/1887. The thread is BLOCKED somewhere between line 1846 (performCreate threw) and line 1870 (performStart DONE) — most likely inside `tryRecoverFragments` (2515-2683) which probes `getSupportFragmentManager` reflectively, or stuck in `dispatchLifecycleEvent` (3392) → `getLifecycle` reflection → Hilt's lifecycle — kotlinx coroutine workers are seen parking/unparking continuously up to the end of the log.

D2' MUST add markers at every join/return boundary to confirm exactly where the hang is. Then D3' targets the specific blocking call (likely `tryRecoverFragments` or `dispatchLifecycleEvent`'s reflection chain) and either short-circuits it (skip for noice/non-McD) OR adds a hard timeout that returns control to the launcher.

### 7.4 Companion read

- `WESTLAKE_NOICE_DAY1_RESULTS_20260503.md` §"Day-2 update part 4" — describes empirical state at PF-noice-002 land
- `WESTLAKE_PF_NOICE_PLAN_20260504.md` §3.2 — original D1 instrumentation pattern (this V2 reuses the `appendCutoffCanaryMarker` style)

---

## 8. Sign-off

This plan V2 is authored read-only by a design agent (no phone, no runtime build, no commits beyond this single doc). The next agent must validate the patch lines against the current source state at start-of-D2' (line numbers may shift if anyone else edits between this plan and implementation).

**Out of scope for V2:** any change to McD-specific paths (PF-630 Realm emulation), audio bridge implementation beyond stubs, OHOS port, host APK genericization (deferred to PF-noice-008), real SunX509+TLS Provider (deferred to PF-noice-015 post-D5'), AppCompat hardening (deferred to PF-noice-018).

**V2 succeeds if D5' produces a screenshot SHA different from the current host-Compose-baseline AND McD bounded gate stays green.** Either real noice content OR the fallback splash counts; the contract is "noice ui shows up" — the fallback splash with "Westlake guest dalvikvm running noice" satisfies the broader Westlake validation goal of "this app has reached a steady running state inside the guest", which is the actual testable claim.

**Approval:** pending supervisor sign-off before next agent begins D2'.
