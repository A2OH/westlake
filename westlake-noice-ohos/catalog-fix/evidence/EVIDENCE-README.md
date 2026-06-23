# Material Catalog 2nd-level demo Activity crash — FIXED + VALIDATED (2026-06-23)

ROOT CAUSE: `android_view_ThreadedRenderer_createHardwareBitmapFromRenderNode` read an
UNINITIALIZED `ANativeWindow* window` (OH has no functional AImageReader, so
`AImageReader_getWindow` left it as stack garbage) -> `proxy.setSurface(garbage)` ->
`CanvasContext::setupPipelineSurface` deref -> SIGBUS BUS_ADRALN on RenderThread ->
process death. Hit when any Material Catalog demo Activity opens.

FIX (2 parts, both BUILT + DEPLOYED):
1. libhwui.so (0c82b1db): real createHardwareBitmap — render the RenderNode into an
   off-screen OHOS IConsumerSurface, AcquireBuffer + fence-wait + CPU-map, copy into a
   heap Bitmap. Falls back to null (software bitmap) if the bridge is unavailable.
   UND audit vs deployed known-good 21daf5e9 = 0 new undefined symbols (brick-safe).
2. liboh_adapter_bridge.so (20ab65a6): adds oh_imagereader_{create,get_window,acquire,
   destroy} (surface_oh_helper.cpp), the OHOS-primitive AImageReader equivalent.
   Superset of deployed 42ab7575 (all input/scroll/key symbols preserved).

VALIDATED on device (catalog child pid 2469, uid 16371):
- Tapped grid -> Adaptive -> List View Demo (AdaptiveListViewDemoActivity, the documented
  crasher). It RENDERS (Inbox email list). Deeper nav to email detail also works.
- stderr: [OH-HWBMP] OK req=720x144 buf=720x144 stride=2880 (readback succeeded);
  SIGBUS=0 SIGSEGV=0 FATAL=0 setupPipelineSurface=0; process survived.
- Screenshots: 01 grid, 02 adaptive demos page, 03 AdaptiveListViewDemoActivity rendered,
  04 deeper email-detail nav.

Backups on device: /data/local/tmp/{libhwui.pre-hwbmp(21daf5e9), bridge.pre-hwbmp(42ab7575)}

## PROOF AImageReader actually CAPTURES content (not just "doesn't crash") — 2026-06-23

Concern: centerRGBA=0x00000000 (one pixel) is weak; a structurally-OK readback could
still return a BLANK buffer, and the on-screen demo render does NOT depend on
createHardwareBitmap, so "demo renders" proves nothing about the readback content.

Method: instrumented libhwui (e79888ac) to scan the WHOLE returned bitmap and dump raw
RGBA. Re-ran grid→Adaptive→List View Demo.

Result (device stderr + host PIL, identical):
  req=720x144 nonZeroRGB=6145/103680 (5.9%) opaque=2459 distinctColors>=501
  R[0..64] G[0..72] B[0..77] Amax=255   (Material dark-on-light ink #40484D, AA edges)
  => NOT blank; transparent bg + dark text/icons.

Visual clincher (05-AImageReader-CAPTURED-bitmap-PROOF.png): the dump decodes to the EXACT
"List View Demo / AdaptiveListViewDemoActivity" row (play button + title + subtitle + heart)
— i.e. the shared-element view the container-transform snapshots. The off-screen OHOS
IConsumerSurface readback (oh_imagereader_create→get_window→syncAndDrawFrame→AcquireBuffer
→fence-wait→CPU-map) captured real rendered pixels, pixel-for-pixel matching the on-screen row.

Note: e79888ac is the DIAGNOSTIC build (dumps + scans every call). Production build = 0c82b1db
(same readback, no dump). Restore 0c82b1db for production (1 reboot).

## WATERMARK EXPERIMENT (2026-06-23) — is the snapshot actually PAINTED on screen?

Question (user): "I only see a plain screen — what is AImageReader actually doing?"
Test: diagnostic libhwui 01f1900f overwrites the returned createHardwareBitmap with OPAQUE
MAGENTA (0xffff00ff), a color absent from the catalog's grayscale UI. Then a 60-frame burst
spanning the grid->Adaptive->List View Demo transition.

RESULTS — decisive:
- Returned bitmap IS magenta: stderr "nonZeroRGB=103680/103680 opaque=103680 min=max=0xffff00ff"
  => my readback code runs and hands a valid bitmap to MaterialContainerTransform. ✓
- On-screen: 0.00% magenta in ALL 60 frames, INCLUDING the mid-transition frames (size
  sequence: 4x source 69994B -> 3x mid 17281B -> dest 65092B). The mid "blank" frames are
  IDENTICAL to the non-watermarked run (07-watermark-midframe-NO-magenta.jpeg) = the
  destination Activity drawing in progressively (FAB+bottom-nav first, then list), NOT the
  transform overlay drawing the snapshot.

CONCLUSION (honest):
- AImageReader/createHardwareBitmap WORKS as a function (renders view -> off-screen OHOS
  surface -> reads back correct pixels; proven by real-content dump AND watermark-in-returned-bitmap).
- BUT on this OHOS adapter the captured snapshot is NEVER composited to the screen — the
  Android activity shared-element/MaterialContainerTransform overlay is not rendered. The
  transition is effectively a plain cut (brief blank while the destination renders).
- Therefore the concrete value of the createHardwareBitmap fix is CRASH-PREVENTION /
  REACHABILITY: it stops the uninitialized-window SIGBUS so the demo Activity opens at all.
  It does not (and cannot, without adapter transition-overlay compositing) produce a visible
  morph. The "plain screen" the user saw is correct and expected.
